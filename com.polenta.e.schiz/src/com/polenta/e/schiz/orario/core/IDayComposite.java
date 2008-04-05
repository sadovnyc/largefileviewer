package com.polenta.e.schiz.orario.core;

import java.util.Collection;

public interface IDayComposite extends IDay
{
	public Collection<IDay> getDays();
}
