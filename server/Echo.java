/*-------------------------------------------------------------------------
cvsinfo: $Header$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/


import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;


public class Echo extends DefaultHandler
{
	private StringBuffer theOutput = new StringBuffer();
	
	public void parse(String anXmlString)
	{
		// Use an instance of ourselves as the SAX event handler
		DefaultHandler handler = this;

		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse( new File("config.xml"), handler);
//anXmlString, handler );
	
		} 
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void nl()
	throws SAXException
	{
		String lineEnd =  System.getProperty("line.separator");
		theOutput.append(lineEnd);
	}

   	public void startDocument()
		throws SAXException
	{
		theOutput.append("<?xml version='1.0' encoding='UTF-8'?>");
		nl();
	}

	public void endDocument()
	throws SAXException
	{
		nl();
	}

	public void startElement(String namespaceURI,
		 String sName, // simple name (localName)
		 String qName, // qualified name
		 Attributes attrs)
	throws SAXException
	{
		String eName = sName; // element name
		if ("".equals(eName)) eName = qName; // namespaceAware = false
		theOutput.append("<"+eName);
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				String aName = attrs.getLocalName(i); // Attr name
				if ("".equals(aName)) aName = attrs.getQName(i);
				theOutput.append(" ");
				theOutput.append(aName+"=\""+attrs.getValue(i)+"\"");
			}
		}
		theOutput.append(">");
	}

	public void endElement(String namespaceURI,
				   String sName, // simple name
				   String qName  // qualified name
				  )
	throws SAXException
	{
		theOutput.append("</"+sName+">");
	}

	public String getOutput()
	{
		return theOutput.toString();
	}
}
