
package com.polenta.e.schiz.orario.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.polenta.e.schiz.orario.core.IOrario;
import com.polenta.e.schiz.orario.core.OrarioDay;
import com.polenta.e.schiz.orario.core.OrarioFormat;
import com.polenta.e.schiz.orario.core.OrarioModel;
import com.polenta.e.schiz.orario.core.OrarioParser;

public class OrarioEditor extends MultiPageEditorPart
{
    private TreeViewer viewer;
    private OrarioActivityFilter viewFilter;
    private TableViewer todayView;
    private TextEditor textEditor;
    
    private Combo activityNameSelector;
    private Combo activityTypeSelector;
    private Button todayButton;
    private ArrayList<String> activityNames = new ArrayList<String>(); 
    private ArrayList<String> activityTypes = new ArrayList<String>(); 
    private IOrario model;
    private IOrario orarioview;
    private Font font = null;
    
    private OrarioOutlinePage outlinePage = null;
    
    private ContentProvider todayContentProvider;

    private ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if(selection instanceof IStructuredSelection &&
					!((IStructuredSelection)selection).isEmpty() &&
					((IStructuredSelection)selection).getFirstElement() instanceof OrarioDay)
				viewer.setSelection(selection);
		}
    };

    private static final String[] columnProperties = {"name", "type", "start", "end", "comment"}; 

    private class ContentProvider implements IStructuredContentProvider
    {
    	private OrarioDay source;
    	private final Object[] kEmpty = new Object[0]; 
    	
		public Object[] getElements(Object inputElement)
		{
			if(source != null)
				return source.getWorkedIntervals();
			else
				return kEmpty;
		}

		public void dispose()
		{
			source = null;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			if(newInput instanceof OrarioDay)
			{
				source = (OrarioDay) newInput;
			}
		}
    	
    }
    private SelectionListener NameComboListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e)
		{
		}
		public void widgetSelected(SelectionEvent e)
		{
			int index = activityNameSelector.getSelectionIndex();
			String item = index < 0 ? "" : activityNameSelector.getItem(index);
			
			if(item != null)
			{
				System.out.println(item);
				
				if(viewFilter != null)
				{
					viewFilter.resetFilter();
					if(e.data != null && e.data instanceof String)
						viewFilter.addNameToFilter(item);
					viewer.refresh();
				}
			}
			else
				System.out.println("null selection");
		}
	};
    
    public OrarioEditor()
    {
        super();
    }
    
    public void doSave(IProgressMonitor monitor)
    {
    	textEditor.doSave(monitor);
    }

    public void doSaveAs()
    {
    	textEditor.doSaveAs();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException
    {
    	super.init(site, input);
        setPartName(input.getName());
        if (input instanceof IPathEditorInput)
        {
            IPathEditorInput pinput = (IPathEditorInput) input;
            IPath p = pinput.getPath();
            File f = p.toFile();
            //read the file here
            try
            {
                Reader in = new FileReader(f);
               	readXMLFile(in);
            }
            catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

    public boolean isSaveAsAllowed()
    {
        return textEditor.isSaveAsAllowed();
    }

    public void createTreePage(Composite parent)
    {
        GridLayout layout;
        GridData gd;
        
        //this font will be released in the dispose method
        font = new Font (this.getSite().getShell().getDisplay(), "Courier", 8, SWT.NORMAL);
        
        layout = new GridLayout(5, false);
        parent.setLayout(layout);
        
        gd = new GridData(GridData.BEGINNING);
        
        viewFilter = new OrarioActivityFilter();
        
        Label activityName = new Label(parent, SWT.RIGHT);
        activityName.setText("Activity:");
        
        activityNameSelector = new Combo(parent,SWT.DROP_DOWN | SWT.READ_ONLY);
        activityNameSelector.setLayoutData(gd);
        activityNameSelector.setVisibleItemCount(20);       
        activityNameSelector.addSelectionListener(NameComboListener);
        activityNameSelector.add("<no filter>");
        for (String name : activityNames)
			activityNameSelector.add(name);
        
        Label activityType = new Label(parent, SWT.RIGHT);
        activityType.setText("Type:");
        
        activityTypeSelector = new Combo(parent,SWT.DROP_DOWN | SWT.READ_ONLY);
        activityTypeSelector.setVisibleItemCount(20);
        activityTypeSelector.add("<no filter>");
        for (String name : activityTypes)
        	activityTypeSelector.add(name);

        todayButton = new Button(parent, SWT.NONE);
        todayButton.setText("Today");
        todayButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if(viewer != null)
				{
					Date todayDate = new Date();
					OrarioDay today = null;
					for (OrarioDay day : model.getDays())
					{
						if(day.getDayDate().after(todayDate))
							break;
						today = day;
					}
					if(today != null)
						viewer.setSelection(new StructuredSelection(today), true);
				}
			}
        	
        });

        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 5;
        
        SashForm sashform = new SashForm(parent,SWT.VERTICAL);
        sashform.setLayoutData(gd);
        
        viewer = new TreeViewer(sashform, SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.getTree().setHeaderVisible(false);
        //viewer.getTree().setFont(font);

        viewer.addFilter(viewFilter);
        viewer.setContentProvider(new OrarioContentProvider());
        viewer.setLabelProvider(new OrarioLabelProvider(this.getSite().getShell().getDisplay()));
        
//        getSite().setSelectionProvider(viewer);
        
        viewer.setInput(orarioview);

        todayContentProvider = new ContentProvider();
        todayView = new TableViewer(sashform, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
        todayView.getTable().setHeaderVisible(true);

        //add name column
        TableColumn col = new TableColumn(todayView.getTable(), SWT.LEFT, 0);
        col.setText("Activity");
        col.setWidth(200);
        col.setResizable(true);

        //add type column
        col = new TableColumn(todayView.getTable(), SWT.LEFT, 1);
        col.setText("Type");
        col.setWidth(120);
        col.setResizable(true);

        //add start time column
        col = new TableColumn(todayView.getTable(), SWT.RIGHT, 2);
        col.setText("Start");
        col.setWidth(50);
        col.setResizable(true);

        //add end time column
        col = new TableColumn(todayView.getTable(), SWT.RIGHT, 3);
        col.setText("End");
        col.setWidth(50);
        col.setResizable(true);

        //add comment column
        col = new TableColumn(todayView.getTable(), SWT.LEFT, 4);
        col.setText("Comment");
        col.setWidth(50);
        col.setResizable(true);

        todayView.setContentProvider(todayContentProvider);
        todayView.setLabelProvider(new OrarioTableLabelProvider());

        todayView.setColumnProperties(columnProperties);
        
        todayView.setColumnProperties(columnProperties);
        todayView.setCellModifier(new OrarioCellModifier());
        TextCellEditor[] editors = new TextCellEditor[todayView.getTable().getColumnCount()];
        editors[0] = new TextCellEditor(todayView.getTable());
        editors[1] = new TextCellEditor(todayView.getTable());
        editors[2] = new TextCellEditor(todayView.getTable());
        editors[3] = new TextCellEditor(todayView.getTable());
        editors[4] = new TextCellEditor(todayView.getTable());
        todayView.setCellEditors(editors);

        //link the tree to the current day table (editable)
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel = event.getSelection();
				if(!sel.isEmpty() && sel instanceof IStructuredSelection)
					todayView.setInput(((IStructuredSelection)sel).getFirstElement());
			}
        	
        });
        
        updateDate();
        
        //listen to selection events
        this.getEditorSite().getPage().addSelectionListener(selectionListener);
    }
    
    public IEditorPart createTextPage()
    {
    	textEditor = new TextEditor();
    	return textEditor;
    }

    @Override
    public void setFocus()
    {
        viewer.getTree().setFocus();
    }

    private void readXMLFile(Reader in)
    {
    	if(model == null)
    		model = new OrarioModel(null);
    	OrarioParser.parseXMLFile(in, model, activityNames, activityTypes);
    	Collections.sort(activityNames);
    	orarioview = new OrarioFormat(model);
    }

    @Override
	public void dispose()
	{
		super.dispose();
		if(font != null)
			font.dispose();
	}
	
	private void updateDate()
	{
		SimpleDateFormat timeformat = new SimpleDateFormat("dd.MMMM.yyyy ' (w'ww') -' HH:mm ");
        setContentDescription(timeformat.format(Calendar.getInstance(Locale.ITALY).getTime()));        
	}

	@Override
	protected void createPages()
	{
		//create the tree view
		Composite composite = new Composite(getContainer(), SWT.FLAT);
		createTreePage(composite);
	    int index = addPage(composite);
	    setPageText(index, "Tree");

	    //create the text view
	    try {
			index = addPage(createTextPage(), getEditorInput());
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    setPageText(index, "Text");
	}

	@Override
	public Object getAdapter(Class adapter)
	{
		if(IContentOutlinePage.class.equals(adapter))
		{
			if(outlinePage == null)
				outlinePage = new OrarioOutlinePage(orarioview);
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}
}
