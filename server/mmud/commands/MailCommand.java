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
package mmud.commands;  

import java.util.logging.Logger;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Mail something to someone.
 */
public class MailCommand extends NormalCommand
{

	String theString;

	public boolean run(User aUser)
	{
		String command = getCommand();
		Logger.getLogger("mmud").finer("");
		if (command.trim().equalsIgnoreCase("mail"))
		{
			theString = "<H1>Mail To</H1><HR noshade>\r\n";

			theString += "<SCRIPT language=\"JavaScript\">\r\n" +
				"<!-- In hiding!\r\n" +
				"function setfocus() {\r\n" +
				" document.CommandForm.mailto.focus();\r\n" +
				" return;\r\n" +
				" }\r\n" +
				"//-->\r\n" +
				"</SCRIPT>\r\n";
		
			theString += "<TABLE BORDER=0>";
			theString += "<TR><TD>From:</TD><TD><B>" + aUser.getName() + "</B></TD></TR>\r\n";
			theString += "<FORM METHOD=\"POST\" ACTION=\"" + Constants.mudcgi + "\" NAME=\"CommandForm\">\n";
			theString += "<TR><TD>To: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailto\" VALUE=\"\"></TD></TR>\r\n";
			theString += "<TR><TD>Header: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailheader\" SIZE=80 VALUE=\"\"></TD></TR>\r\n";
			theString += "<TR><TD>Body:</TD><TD>\r\n";
			theString += "<TEXTAREA NAME=\"mailbody\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n";
			theString += "</TD></TR></TABLE><INPUT TYPE=\"hidden\" NAME=\"command\" VALUE=\"sendmail\">\n";
			theString += "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"" + aUser.getName() + "\">\n";
			theString += "<HR noshade><INPUT TYPE=\"submit\" VALUE=\"Sendmail\">\n";
			theString += "<INPUT TYPE=\"reset\" VALUE=\"Resetform\">\n";
			theString += "</FORM><P>\r\n";
			theString += aUser.readLog();
			return true;
		}
		return false;
	}

	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return theString;
	}

}
