package com.polenta.e.schiz.orario.core;

public interface IOrario
{
	/**
	 * Replace the day array for the model, all internal data structures
	 * are rebuilt.
	 * 
	 * A model changed event is fired.
	 * 
	 * @param dayArray the array of days to use for the model (can be NULL).
	 */
	public abstract void set(OrarioDay[] dayArray);

	public abstract void setDay(IDay day);

	public abstract OrarioDay[] getDays();

	public abstract int getMaxYear();

	public abstract int getMinYear();

	public abstract void addListener(IOrarioModelListener listener);

	public abstract void removeListener(IOrarioModelListener listener);

}
