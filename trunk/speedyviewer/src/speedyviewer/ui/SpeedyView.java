package speedyviewer.ui;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.ui.part.ViewPart;

import speedyviewer.core.FileIndexer;
import speedyviewer.core.IIndexerListener;
import speedyviewer.core.IndexerThread;


/**
 * This view shows a large file.
 */
public class SpeedyView extends ViewPart
{
	private static final int CHUNK_SIZE = 64*1024;

	private StyledText viewer;
	private IndexerThread indexerTh;
	private String fileName;
	
	private Action loadFileAction = new Action()
	{
		@Override
		public void run()
		{
			FileDialog dialog = new FileDialog(SpeedyView.this.getSite().getShell());
			dialog.setText("select a file");
			fileName = dialog.open();
			if(fileName != null)
			{
				if(indexerTh != null)
					indexerTh.setListener(null);
				File file = new File(fileName);
				indexerTh = new IndexerThread(new FileIndexer(CHUNK_SIZE), file);
				indexerTh.setListener(listener);
				viewer.setContent(new LargeFileContent(file, indexerTh.getIndexer()));
				indexerTh.start();
			}
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

		public ViewUpdate(int count)
		{
			this.count = count;
		}

		public void run()
		{
			//just show the new length
			setContentDescription("lines:" + count);
		}
	};

	private IIndexerListener listener = new IIndexerListener() {
		public void newIndexChunk(FileIndexer indexer)
		{
			//run update in the UI thread (this listener is called
			//from the indexer thread and can not call UI methods)
			SpeedyView.this.getViewSite().getShell().getDisplay().asyncExec(new ViewUpdate(indexer.getLineCount()));
		}

		public void addingIndexChunk(FileIndexer indexer, int[] chunk, int len, int charCount) {}
		public void indexingComplete(FileIndexer indexer) {}
	};

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
		viewer = new StyledText(parent, SWT.V_SCROLL);

		//add load item in drop down menu
		loadFileAction.setText("Open File");
		getViewSite().getActionBars().getMenuManager().add(loadFileAction);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.setFocus();
	}
}