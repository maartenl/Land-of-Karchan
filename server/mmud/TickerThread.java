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

package mmud;

import java.util.logging.Logger;

import mmud.characters.PersonException;
import mmud.characters.Persons;
import mmud.database.Database;

/**
 * the class that takes care of the passing of time and the events that can
 * occur with the passing of time. Examples of this are:
 * <ul>
 * <li>setting sun
 * <li>deteriorating items
 * <li>cleaning up of idle users.
 * </ul>
 */
public class TickerThread implements Runnable
{
	private static boolean theStarted = false;

	public TickerThread()
	{
		Logger.getLogger("mmud").finer("");
	}

	/**
	 * Run method of the thread. This is started when the start method is
	 * called.
	 */
	public void run()
	{
		Logger.getLogger("mmud").finer("");
		if (theStarted)
		{
			throw new RuntimeException("Ticker thread already started!");
		}
		theStarted = true;
		try
		{
			Thread.sleep(3000);
		} catch (InterruptedException e)
		{
		}
		while (!Constants.shutdown)
		{
			try
			{
				Thread.sleep(60000);
			} catch (InterruptedException e)
			{
			}
			try
			{
				Persons.removeIdleUsers();
			} catch (PersonException e)
			{
				Database.writeLog("root", e);
				e.printStackTrace();
			} catch (MudException e)
			{
				Database.writeLog("root", e);
				e.printStackTrace();
			}
			try
			{
				if (Constants.events_active)
				{
					Database.runEvents();
				}
			} catch (MudException e)
			{
				Database.writeLog("root", e);
				e.printStackTrace();
			}
		} // neverending loop.
	}

}
