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
 * Fight another person. Syntax is :<P>
 * <TT>fight &lt;name&gt;</TT><P>
 * Persons available currently are as follows:
 * <UL>
 * <LI>Person
 * <LI>Bot -> Person
 * <LI>User -> Person
 * <LI>StdShopKeeper -> Person
 * </UL>
 * The requirements are as follows:
 * <UL>
 * <LI>the person should exist
 * <LI>the person should be playing the game
 * <LI>the person should be in the same room as the player issuing the command
 * <LI>if person is User -> only fighting if playerkill is active for both parties
 * <LI>if person is Bot -> only fighting if bot if a MOB instead of an NPC.
 * </UL>
 * TODO: make fighting available in <I>Arena</I>(s), check bot is fightable.
 */
public class FightCommand extends NormalCommand
{

	public FightCommand(String aRegExpr)
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
		String[] myParsed = getParsedCommand();
		// determine if appropriate fighter is found.
		Person toChar = Persons.retrievePerson(myParsed[myParsed.length-1]);
		if ((toChar == null) || (!toChar.getRoom().equals(aUser.getRoom())) )
		{
			aUser.writeMessage("Cannot find that person.<BR>\r\n");
			return true;
		}
		if (toChar instanceof User)
		{
			// check for playerkill flag (on both sides!)
			if (!aUser.isPkill())
			{
				aUser.writeMessage("You do not have <I>pkill</I> active.<BR>\r\n");
				return true;
			}
			if (!((User) toChar).isPkill())
			{
				aUser.writeMessage("Your opponent does not have <I>pkill</I> active.<BR>\r\n");
				return true;
			}
		}
		aUser.setFightingPerson(toChar);
		toChar.setFightingPerson(aUser);
		Persons.sendMessage(aUser, toChar, "%SNAME start%VERB2 to fight against %TNAME.<BR>\r\n");
		Database.writeLog(aUser.getName(), "starts fighting " +toChar.getName() + ".");
		Logger.getLogger("mmud").finer("waking up fighting thread...");
		Constants.wakeupFightingThread();
		return true;
	}

	public Command createCommand()
	{
		return new FightCommand(getRegExpr());
	}
	
}
