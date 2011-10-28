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

import java.util.Calendar;
import java.util.logging.Logger;

import mmud.MudException;
import mmud.characters.User;

/**
 * Displays the current time: "time".
 */
public class TimeCommand extends NormalCommand
{

	public TimeCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(User aUser) throws MudException
	{
		Logger.getLogger("mmud").finer("");
		Calendar myCalendar = Calendar.getInstance();
		aUser.writeMessage("Current time is "
				+ myCalendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ +myCalendar.get(Calendar.MINUTE) + ":"
				+ +myCalendar.get(Calendar.SECOND) + ".<BR>\r\n");
		return true;
	}

	public Command createCommand()
	{
		return new TimeCommand(getRegExpr());
	}

}
