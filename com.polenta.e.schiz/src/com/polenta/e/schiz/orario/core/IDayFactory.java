package com.polenta.e.schiz.orario.core;

public interface IDayFactory
{
	public IDay createChildFor( IDayComposite parent );
	public void setModel( IOrario model );
}
