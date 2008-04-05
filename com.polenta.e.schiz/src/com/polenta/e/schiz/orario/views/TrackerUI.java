package com.polenta.e.schiz.orario.views;


import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import com.polenta.e.schiz.orario.core.Tracker;

public class TrackerUI
{
	private Button startButton;
	private Button stopButton;
	private TableViewer intervalViewer;
	private Tracker tracker;
	
	public TrackerUI(Composite parent, Tracker tracker)
	{
		this.tracker = tracker;
		
		GridData gd;
		parent.setLayout(new GridLayout(3,false));

		startButton = new Button(parent,SWT.NONE);
		startButton.setText("start new");
        gd = new GridData(GridData.BEGINNING );
        gd.horizontalSpan = 1;
        gd.verticalSpan = 1;
        startButton.setLayoutData(gd);
		
		stopButton = new Button(parent,SWT.NONE);
		stopButton.setText("stop");
        gd = new GridData(GridData.BEGINNING );
        gd.horizontalSpan = 1;
        gd.verticalSpan = 1;
        stopButton.setLayoutData(gd);
		
		intervalViewer = new TableViewer(parent, SWT.FLAT);
        gd = new GridData(GridData.BEGINNING | GridData.FILL_BOTH);
        gd.horizontalSpan = 3;
        gd.verticalSpan = 1;
        intervalViewer.getTable().setLayoutData(gd);
        intervalViewer.getTable().setHeaderVisible(true);
		
        //add name column
        TableColumn col = new TableColumn(intervalViewer.getTable(), SWT.LEFT, 0);
        col.setText("Activity");
        col.setWidth(200);
        col.setResizable(true);

        //add type column
        col = new TableColumn(intervalViewer.getTable(), SWT.LEFT, 1);
        col.setText("Type");
        col.setWidth(120);
        col.setResizable(true);

        //add start time column
        col = new TableColumn(intervalViewer.getTable(), SWT.RIGHT, 2);
        col.setText("Start");
        col.setWidth(50);
        col.setResizable(true);

        //add end time column
        col = new TableColumn(intervalViewer.getTable(), SWT.RIGHT, 3);
        col.setText("End");
        col.setWidth(50);
        col.setResizable(true);

        //add comment column
        col = new TableColumn(intervalViewer.getTable(), SWT.LEFT, 4);
        col.setText("Comment");
        col.setWidth(50);
        col.setResizable(true);
	}
	
}
