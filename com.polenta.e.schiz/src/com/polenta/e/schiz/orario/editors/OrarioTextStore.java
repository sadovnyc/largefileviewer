package com.polenta.e.schiz.orario.editors;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.eclipse.jface.text.ITextStore;

import com.polenta.e.schiz.orario.core.IOrario;
import com.polenta.e.schiz.orario.core.OrarioParser;

public class OrarioTextStore implements ITextStore
{
	private IOrario model;
	private SortedSet<OrarioTextElement> elements;
	
	public enum ElementType
	{
		Day, /** getElement() returns an IDay, or null if the description is corrupted */
		OrarioBegin, /** getElement() returns a String */
		OrarioEnd, /** getElement() returns a String */
		Header, /** getElement() returns a String */
		Separation /** getElement() returns a String */
	};

	public class OrarioTextElement implements Comparable<OrarioTextElement>
	{
		private int offset;
		private int length;
		private StringBuilder buffer;
		private String originalDocument;
		private ElementType type;
		private Object element;
		
		/**
		 * @param text element text
		 * @param offset offset of this text
		 */
		public OrarioTextElement(String text, int offset)
		{
			originalDocument = text;
			this.offset = offset;
			this.length = text.length();
			this.element = getElementForText();
		}

		@Override
		public boolean equals(Object obj)
		{
			//this implementation makes 'equals' consistent with compareTo
			if(obj instanceof OrarioTextElement)
				return this.offset == ((OrarioTextElement)obj).offset;
			else
				return false;
		}

		public int compareTo(OrarioTextElement arg0)
		{
			return this.offset - arg0.offset;
		}
		
		public Object getElement()
		{
			return element;
		}

	}

	public class OrarioHeaderElement implements IOrarioElement
	{
		private String regExp = "(<\\?.*\\?>)\\s+(<!DOCTYPE.*]>)";
		public int parseText(String text, int offset)
		{
			Matcher m = Pattern.compile(regExp,Pattern.DOTALL).matcher(text);
			if(m.find())
			{
				return m.end();
			}
			return 0;
		}
		
	}

	public class OrarioBeginElement implements IOrarioElement
	{
		private String regExp = "<orario.*>";
		public int parseText(String text, int offset)
		{
			Matcher m = Pattern.compile(regExp,Pattern.DOTALL).matcher(text);
			if(m.find())
			{
				return m.end();
			}
			return 0;
		}
		
	}

	public class OrarioEndElement implements IOrarioElement
	{
		private String regExp = "</orario>";
		public int parseText(String text, int offset)
		{
			Matcher m = Pattern.compile(regExp,Pattern.DOTALL).matcher(text);
			if(m.find())
			{
				return m.end();
			}
			return 0;
		}
		
	}

	public class OrarioDayElement implements IOrarioElement
	{
		private String regExp = "<day.*</day>";
		public int parseText(String text, int offset)
		{
			Matcher m = Pattern.compile(regExp,Pattern.DOTALL).matcher(text);

			if(text.startsWith("<day"))
			{
				int index;
				
				index = text.indexOf("</day>");
				if(index >= 0)
				{
					//check if another day was beginning in between
					if(text.indexOf("<day", 4) < 0)
					{
						//no: this is a well-formed day definition
						return index + 6;
					}
					//complete (maybe incorrect) day
					index += 6;
				}
				else
				{
					//missing closing bracket
				}
			}
				
			if(m.find())
			{
				return m.end();
			}
			return 0;
		}
		
	}

	public char get(int offset) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String get(int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void replace(int offset, int length, String text) {
		// TODO Auto-generated method stub

	}

	public void set(String text)
	{
		//rebuild the model
		StringReader reader = new StringReader(text);
		OrarioParser.parseXMLFile( reader, model, null, null);
	}

	private Object getElementForText()
	{
		return null;
	}
	
	private void parse()
	{
		CharSequence textToParse;
	}
}
