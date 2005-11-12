/*-------------------------------------------------------------------------
svninfo: $Id: MudException.java 987 2005-10-20 19:26:50Z maartenl $
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

package mmud.common;

import java.io.StringReader;
import java.util.Random;
import java.util.logging.Logger;

import mmud.Constants;

import org.xml.sax.SAXException;

import simkin.ExecutableContext;
import simkin.MethodNotSupportedException;
import simkin.ParseException;
import simkin.XMLExecutable;

/**
 * The executable that needs to be executed by the interpreter.
 * This is derived from the simkin XMLExecutable so we can
 * implement some global methods.<P>
 * The methods in question are:
 * <UL><LI>int random(int) : returns a random integer
 * <LI>boolean isAdverb(string) : returns true if the string is an adverb
 * defined in the game, for example "evilly".
 * </UL>
 */ 
public class MudXMLExecutable extends XMLExecutable
{

	/**
	 * constructor for creating a interpreter.
	 */
	public MudXMLExecutable(String aName, StringReader aStringReader)
	throws SAXException, java.io.IOException
	{
		super(aName, aStringReader);
	}

	/**
	 * Implements standard methods specific for the mud.
	 */
	public Object method(String method_name, Object[]
		arguments, ExecutableContext ctxt)
	throws MethodNotSupportedException, ParseException
	{
		Logger.getLogger("mmud").finer("method_name=" + method_name +
			", arguments=" + arguments);
		if (method_name.equals("random"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof Integer))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain an Integer as argument.");
				}
				Random random = new Random();
				return new Integer(random.nextInt(((Integer) arguments[0]).intValue()));
			}
		}
		if (method_name.equals("isAdverb"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as argument.");
				}
				return new Boolean(Constants.existsAdverb((String) arguments[0]));
			}
		}
		return super.method(method_name, arguments, ctxt);
	}

}
