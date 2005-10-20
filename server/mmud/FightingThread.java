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

import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.File;
import java.util.logging.Logger;
import java.util.Iterator;
import mmud.characters.*;
import mmud.database.*;
import mmud.commands.*;

/**
 * The class that takes care of the fighting. Every two seconds, this thread
 * iterates across all users to check if anyone is fighting anyone else.
 * <P>
 * If this is the case, a hit is calculated.
 */
public class FightingThread extends Thread
{
	private static boolean theStarted = false;

	public FightingThread()
	{
		super("fighting");
		Logger.getLogger("mmud").finer("");
	}

	/**
	 * The method that takes care of the fighting of one specific
	 * person.
	 * @param aPerson the person that is currently fighting.
	 */
	public void processFighting(Person aPerson)
	{
		Person to = aPerson.getFightingPerson();
		Logger.getLogger("mmud").finer(aPerson + " is fighting " + to);
	}

	/**
	 * The method that checks to see if the person fighting is
	 * fighting a valid other person. (for example: the opponent has
	 * not logged out yet)<P>Requirements:<UL>
	 * <LI>opponent must be logged in
	 * </UL>
	 * @param aPerson the person that is currently fighting.
	 */
	public void validateFighting(Person aPerson)
	{
		Logger.getLogger("mmud").finer("aPerson=" + aPerson);
		Person to = Persons.retrievePerson(
			aPerson.getFightingPerson().getName());
		if (to == null)
		{
			aPerson.setFightingPerson(null);
		}
	}

	/**
	 * Run method of the thread. This is started when the start method
	 * is called. It iterates over all persons in the game
	 * and determines if they are fighting against someone, and if
	 * they <I>should</I> be fighting against someone.
	 */
	public void run()
	{
		Logger.getLogger("mmud").finer("");
		if (theStarted)
		{
			throw new RuntimeException("Fighting thread already started!");
		}
		theStarted = true;
		try
		{
			sleep(3000);
		}
		catch (InterruptedException e)
		{
		}
		boolean nobody_is_fighting = false;
		while ( (!Constants.shutdown) && (!nobody_is_fighting) )
		{
			try
			{
				sleep(2000);
			}
			catch (InterruptedException e)
			{
			}
			Iterator i = Persons.getIterator();
			nobody_is_fighting = true;
			while (i.hasNext())
			{
				Person o = (Person) i.next();
				if (o.isFighting())
				{
					validateFighting(o);
				}
				if (o.isFighting())
				{
					nobody_is_fighting = false;
					processFighting(o);
				}
			}
		} // neverending loop. Well,... not actually neverending really.
		Logger.getLogger("mmud").finer("nobody fighting, thread ending...");
		Constants.emptyFightingThread();
	}

}
