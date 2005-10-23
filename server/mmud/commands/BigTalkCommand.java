/*-------------------------------------------------------------------------
svninfo: $Id$
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
package mmud.commands;  

import java.util.logging.Logger;

import mmud.Constants;
import mmud.MudException;
import mmud.characters.User;

/**
 * Sometimes the space in the fillout form is not enough, than this command
 * will show a bigger entry form : "bigtalk".
 */
public class BigTalkCommand extends NormalCommand
{

	User theUser;

	public BigTalkCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	public boolean run(User aUser)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aUser))
		{
			return false;
		}
		theUser = aUser;
		return true;
	}

	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		try
		{
			String aString = "Due to the size of the area, this is where the really big messages are " +
				"typed. This is ideally suited for exampe mudmail.<P>\r\n" +
				"<SCRIPT language=\"JavaScript\">\r\n" +
				"<!-- In hiding!\r\n" +
				"function setfocus() {\r\n" +
				"	   document.CommandForm.command.focus();\r\n" +
				"   return;\r\n" +
				"   }\r\n" +
				"//-->\r\n" +
				"</SCRIPT>\r\n" +
				"<FORM METHOD=\"POST\" ACTION=\"" + Constants.mudcgi + "\" NAME=\"CommandForm\">\n" +
				"<TEXTAREA NAME=\"command\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n" +
				"<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"" + theUser.getName() + "\">\n" +
				"<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"" + (theUser.getFrames()+1) + "\">\n" +
				"<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n" +
				"</FORM><P>\n";

			aString += theUser.readLog();
			return aString;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Command createCommand()
	{
		return new BigTalkCommand(getRegExpr());
	}
	
}
