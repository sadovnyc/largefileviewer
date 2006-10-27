package speedyviewer.views;

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
import speedyviewer.core.LargeFileContent;


/**
 * This view shows a large file.
 */
public class SpeedyView extends ViewPart
{
	private StyledText viewer;
	private IndexerThread indexer;
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
				if(indexer != null)
					indexer.setListener(null);
				File file = new File(fileName);
				indexer = new IndexerThread(64*1024, file);
				indexer.setListener(listener);
				indexer.start();
				viewer.setContent(new LargeFileContent(file, indexer));
				
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
		//create and start indexer thread here
//		IndexerThread indexer = new IndexerThread(64*1024,new File("/home/fab/ciccione.txt"));
//		indexer.setListener(listener);
//		indexer.start();
		
		//viewer.setContent(new LargeFileContent(fileName, indexer));
		
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