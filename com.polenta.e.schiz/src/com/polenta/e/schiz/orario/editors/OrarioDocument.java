package com.polenta.e.schiz.orario.editors;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.DefaultLineTracker;

public class OrarioDocument extends AbstractDocument
{

	/**
	 * Create a new OrarioDocument.
	 * 
	 * This document uses an OrarioTextStore to manage the
	 * document text and a OrarioLineTracker.
	 */
	public OrarioDocument()
	{
		super();
		//set the necessary plugins for the document
		setTextStore(new OrarioTextStore());
		//setLineTracker(new OrarioLineTracker());
		setLineTracker(new DefaultLineTracker());
	}

}
