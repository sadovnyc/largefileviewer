package com.polenta.e.schiz.orario.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.polenta.e.schiz.orario.activity.ActivityFragment;
import com.polenta.e.schiz.orario.core.OrarioDay;
import com.polenta.e.schiz.orario.core.OrarioInterval;
import com.polenta.e.schiz.orario.core.OrarioWeek;
import com.polenta.e.schiz.orario.core.OrarioYear;

public class ActivityView extends ViewPart implements ISelectionListener
{
	private TableViewer tviewer;
	private ActivityViewLabelProvider labelProvider;
	private HashMap<String,ActivityFragment> activityMap = new HashMap<String, ActivityFragment>();
	private Action timeFormat;

	//formatter used to parse and display date
    private SimpleDateFormat dayformat = new SimpleDateFormat("'day:' yyyy.MM.dd");
    private SimpleDateFormat weekformat = new SimpleDateFormat("'week' yyyy.ww");
    private SimpleDateFormat yearformat = new SimpleDateFormat("'year:' yyyy");

	public void createPartControl(Composite parent)
	{

        //tableview for activity duration + name
        tviewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.BEGINNING | GridData.FILL_BOTH);
        gd.horizontalSpan = 1;
        gd.verticalSpan = 1;
        tviewer.getTable().setLayoutData(gd);
        tviewer.getTable().setHeaderVisible(true);

        //add duration column
        TableColumn col = new TableColumn(tviewer.getTable(), SWT.RIGHT, 0);
        col.setText("Duration");
        col.setWidth(50);
        col.setResizable(true);

        //add name column
        col = new TableColumn(tviewer.getTable(), SWT.RIGHT, 1);
        col.setText("Activity");
        col.setWidth(100);
        col.setAlignment(SWT.LEFT);
        col.setResizable(true);

        tviewer.getTable().setLinesVisible(true);
        
        //table content & label provider
        tviewer.setContentProvider(new ArrayContentProvider());
        labelProvider = new ActivityViewLabelProvider();
        tviewer.setLabelProvider(labelProvider);

        // register to selection events
        getViewSite().getPage().addSelectionListener(this);

        tviewer.setInput(null);
        
        makeActions();
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(timeFormat);
	}

	@Override
	public void setFocus()
	{
		tviewer.getTable().setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
        if (selection instanceof IStructuredSelection)
        {
            IStructuredSelection strsel = (IStructuredSelection) selection;
            Object element = strsel.getFirstElement();
            if(element instanceof OrarioDay)
                showActivities((OrarioDay) element); 
            else if(element instanceof OrarioWeek)
                showActivities((OrarioWeek) element); 
            else if(element instanceof OrarioYear)
                showActivities((OrarioYear) element); 
        }
	}

	/**
	 * Show in the viewer the activities from the specified day.
	 * 
	 * @param day day whose activities should be shown.
	 */
	private void showActivities(OrarioDay day)
	{
		clearActivityNames();
		
		addActivityNames(day);

		tviewer.setInput(activityMap.values());
		
		setDescription(day.getDayDate(), dayformat);
	}
	
	/**
	 * Show in the viewer the activities from the specified week.
	 * 
	 * @param week week whose activities should be shown.
	 */
	private void showActivities(OrarioWeek week)
	{
		OrarioDay[] days = week.getDaysArray();

		clearActivityNames();
		
		for (OrarioDay day : days)
			addActivityNames(day);
		
		tviewer.setInput(activityMap.values());
		
		Calendar cal = Calendar.getInstance(Locale.ITALY);
		
		cal.setTime(week.getDaysArray()[0].getDayDate());
		//for some reason the week number displayed using the formatter
		//is different from the one returned by Calendar...
		setContentDescription("week: " + cal.get(Calendar.YEAR) + "." + cal.get(Calendar.WEEK_OF_YEAR));
//		setDescription(week.getDaysArray()[0].getDayDate(), weekformat);
	}
	
	/**
	 * Show in the viewer the activities from the specified year.
	 * 
	 * @param year year whose activities should be shown.
	 */
	private void showActivities(OrarioYear year)
	{
		OrarioWeek[] weeks = year.getWeeks();

		clearActivityNames();
		
		for (OrarioWeek week : weeks)
		{
			if(week != null && week.getDaysArray() != null)
				for (OrarioDay day : week.getDaysArray())
					addActivityNames(day);
		}
		
		tviewer.setInput(activityMap.values());

		setDescription(year.getWeeks()[0].getDaysArray()[0].getDayDate(), yearformat);
	}

	/**
	 * Clear the internal activity list.
	 */
	private void clearActivityNames()
	{
		activityMap.clear();
	}

	/**
	 * Add activities from the specified day to the internal list.
	 * If such an activity is not in the list then it is added as a fragment
	 * along with the duration.
	 * If the activity is already in the list the duration is added to the
	 * corresponding activity fragment.
	 * 
	 * @param day Day whose activities should be added.
	 */
	private void addActivityNames(OrarioDay day)
	{
		OrarioInterval[] intervals = null;
		
		if(day != null)
			 intervals = day.getWorkedIntervals();
		
		if(intervals != null)
			for (OrarioInterval interval : intervals)
			{
				//add Activity Fragment if this is the first interval with this name
				if(!activityMap.containsKey(interval.getActivityName()))
					activityMap.put(interval.getActivityName(), new ActivityFragment());
				
				activityMap.get(interval.getActivityName()).addInterval(interval);
			}
	}
	
	private void showTimesInDays(boolean activate)
	{
		labelProvider.setShowDurationInDays(!labelProvider.isShowDurationInDays());
		tviewer.refresh(true);
	}
	
	private void makeActions()
	{
		timeFormat = new Action() {
			public void run() {
				showTimesInDays(true);
			}
		};
		timeFormat.setText("Time Format");
		timeFormat.setToolTipText("Change time format for duration of activities");
		timeFormat.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		};
		
	private void setDescription(Date dayDate, DateFormat dateformat)
	{
        Calendar cal = Calendar.getInstance(Locale.ITALY);
            	
    	cal.setTime(dayDate);
    	
    	setContentDescription(dateformat.format(dayDate));
	}
}

