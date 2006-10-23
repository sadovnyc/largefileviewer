package speedyviewer.views;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.ui.part.ViewPart;

import speedyviewer.core.LargeFileIndexer;
import speedyviewer.core.IIndexerListener;
import speedyviewer.core.IndexerThread;


/**
 * This view shows a large file.
 */
public class SpeedyView extends ViewPart
{
	private StyledText viewer;

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
		public void newIndexChunk(LargeFileIndexer indexer)
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
		viewer = new StyledText(parent, SWT.NONE);
		//create and start indexer thread here
		IndexerThread indexer = new IndexerThread(64*1024,new File("/home/fab/ciccione.txt"));
		indexer.setListener(listener);
		indexer.start();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.setFocus();
	}
}