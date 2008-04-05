package com.polenta.e.schiz.orario.core;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class OrarioParser
{
    /**
     * Parse an input stream and correspondingly initialise the target OrarioModel.
     * 
     * 
     * @param inReader the input data.
     * @param target the model to initialise
     * @param activityNames collection to populate with activity name
     * @param activityTypes collection to populate with activity types
     * @return
     */
    static public IOrario parseXMLFile( Reader	inReader, IOrario	 target, Collection<String> activityNames, Collection<String> activityTypes )
    {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		OrarioXMLHandler handler = new OrarioXMLHandler(activityNames, activityTypes);
		IOrario model = null;
		
		try
		{
			XMLReader reader = factory.newSAXParser().getXMLReader();
			
			reader.setContentHandler(handler);
			
			InputSource inputSource = new InputSource(inReader);
			
			reader.parse(inputSource);
			
			target.set(handler.getDays());
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return model;
    }
}
