package com.polenta.e.schiz.orario.editors;

public interface IOrarioElement
{
	/**
	 * @param text the text to parse 
	 * @param offset
	 * @return the characters used during parsing
	 */
	public int parseText(String text, int offset);
}
