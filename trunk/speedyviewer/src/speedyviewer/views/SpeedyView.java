package speedyviewer.views;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;

import speedyviewer.core.LargeFileIndexer;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SpeedyView extends ViewPart {
	private StyledText viewer;


	/**
	 * The constructor.
	 */
	public SpeedyView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent)
	{
		viewer = new StyledText(parent, SWT.NONE);
		try {
			System.out.println("Starting");
			long start = System.currentTimeMillis();
			LargeFileIndexer indexer = new LargeFileIndexer(64*1024);
			indexer.parallelIndex(new File("C:\\ccviews\\fia060925_avm.prv\\result\\vc7\\Lgreg_high_optiverse_tri_band_all_gcc.txt"));
			System.out.println("Elapsed Time (ms): " + (System.currentTimeMillis() - start));
			System.out.println("Lines counted: " + indexer.getLineCount());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.setFocus();
	}
}