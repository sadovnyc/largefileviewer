package speedyviewer.ui;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.ui.part.ViewPart;

import speedyviewer.core.AbstractFileIndexer;
import speedyviewer.core.FileIndexer;
import speedyviewer.core.IIndexerMonitor;
import speedyviewer.core.IndexPartition;
import speedyviewer.core.IndexerThread;
import speedyviewer.core.BeginEndPartitioner;


/**
 * This view shows a large file.
 */
public class SpeedyView extends ViewPart
{
	private static final int CHUNK_SIZE = 64*1024;

	private StyledText viewer;
	private IndexerThread indexerTh;
	private long fileSize;
	private File file;
	boolean cancel;
	private static final String begin = "!SESSION";
	private static final String end   = "!SESSION";
	private BeginEndPartitioner processor;
	
	private Action loadFileAction = new Action()
	{


		@Override
		public void run()
		{
			FileDialog dialog = new FileDialog(SpeedyView.this.getSite().getShell());
			dialog.setText("select a file");
			String fileName = dialog.open();
			if(fileName != null)
			{
				cancel = false;
				
				setEnabled(false);
				cancelAction.setEnabled(true);

				if(indexerTh != null)
					indexerTh.addListener(null);
				file = new File(fileName);
				fileSize = file.length();
				FileIndexer indexer = new FileIndexer(CHUNK_SIZE);
				processor = new BeginEndPartitioner(begin, end);
				indexer.addLineProcessor(processor);

				indexerTh = new IndexerThread(indexer, file);
				indexerTh.addListener(listener);
				// if set here, the text in the viewer will grow dynamically, the styled text
				// does not do that nicely for very large files (many millions of lines)
				// set it when indexing is complete (below)
//				viewer.setContent(new LargeFileContent(file, (FileIndexer)indexerTh.getIndexer()));
				indexerTh.start();
			}
		}
		
	};

	private Action cancelAction = new Action()
	{
		@Override
		public void run()
		{
			cancel = true;
		}
	};

	/**
	 * Update the view
	 *
	 */
	private class ViewUpdate implements Runnable
	{
		//new line count
		private int count;
		private long percent;

		public ViewUpdate(int count, long percent)
		{
			this.count = count;
			this.percent = percent;
		}

		public void run()
		{
			//just show the new length
			setContentDescription("lines:" + count + " (" + percent + "%)");
		}
	};

	private IIndexerMonitor listener = new IIndexerMonitor() {
		public void newIndexChunk(AbstractFileIndexer indexer)
		{
			long percent = 100;
			
			if( fileSize > 0 )
				percent = (long)indexer.getCharCount() * 100 / fileSize;
			
			//run update in the UI thread (this listener is called
			//from the indexer thread and can not call UI methods)
			SpeedyView.this.getViewSite().getShell().getDisplay().asyncExec(new ViewUpdate(indexer.getLineCount(), percent));
		}

		public void addingIndexChunk(AbstractFileIndexer indexer, int[] chunk, int len, int charCount) {}
		public void indexingComplete(AbstractFileIndexer indexer)
		{
			SpeedyView.this.getViewSite().getShell().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run()
				{
					partitionsViewer.setInput( processor.getPartitions() );
					viewer.setContent(new LargeFileContent(file, (FileIndexer)indexerTh.getIndexer()));
					loadFileAction.setEnabled(true);
					cancelAction.setEnabled(false);
				}
			});
		}

		@Override
		public boolean isCanceled()
		{
			return cancel;
		}
	};

	private ListViewer partitionsViewer;

	/**
	 * The constructor.
	 */
	public SpeedyView()
	{
		super();
	}

	/**
	 * Create the view widgetry.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		SashForm sashForm = new SashForm(parent, SWT.NONE);
		
		partitionsViewer = new ListViewer( sashForm, SWT.V_SCROLL | SWT.H_SCROLL );
		partitionsViewer.setContentProvider(new ArrayContentProvider());
		partitionsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IndexPartition) {
					IndexPartition partition = (IndexPartition) element;
					return "[" + partition.getFirst() + "," + partition.getLast() + "]";
				}
				return super.getText(element);
			}
		});
		
		partitionsViewer.addOpenListener(new IOpenListener() {
			@Override
			public void open(OpenEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				IndexPartition p = (IndexPartition) selection.getFirstElement();
				long first = p.getFirst();
				int offset = indexerTh.getIndexer().getOffsetForLine((int) first);
				viewer.setCaretOffset(offset);
				viewer.showSelection();
				viewer.setFocus();
			}
		});

		viewer = new StyledText(sashForm, SWT.V_SCROLL);

		//add open file / cancel opening in drop down menu
		loadFileAction.setText("Open File");
		cancelAction.setText("Cancel");
		cancelAction.setEnabled(false); // disabled as no file is being loaded yet

		getViewSite().getActionBars().getMenuManager().add(loadFileAction);
		getViewSite().getActionBars().getMenuManager().add(cancelAction);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.setFocus();
	}
}