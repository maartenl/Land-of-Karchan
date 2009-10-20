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

import mmud.MudException;
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.characters.CommunicationListener.CommType;

/**
 * Shout to someone "shout Help!". Or just shout in general. The interesting
 * part about this command is, because shouting is louder in general than simple
 * talking, that the shout can be heard in neighbouring rooms.
 */
public class ShoutCommand extends CommunicationCommand
{

	public ShoutCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	/*
	 * * Different behaviour than the standard, shouting must be heard in
	 * neighbouring rooms.
	 */
	public boolean run(User aUser) throws MudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aUser))
		{
			return false;
		}

		Persons.sendMessage(aUser, aUser.getRoom().getSouth(),
				"Someone shouts : " + getMessage() + "<BR>\r\n");
		Persons.sendMessage(aUser, aUser.getRoom().getNorth(),
				"Someone shouts : " + getMessage() + "<BR>\r\n");
		Persons.sendMessage(aUser, aUser.getRoom().getWest(),
				"Someone shouts : " + getMessage() + "<BR>\r\n");
		Persons.sendMessage(aUser, aUser.getRoom().getEast(),
				"Someone shouts : " + getMessage() + "<BR>\r\n");
		Persons.sendMessage(aUser, aUser.getRoom().getUp(), "Someone shouts : "
				+ getMessage() + "<BR>\r\n");
		Persons.sendMessage(aUser, aUser.getRoom().getDown(),
				"Someone shouts : " + getMessage() + "<BR>\r\n");

		return true;
	}

	@Override
	public Command createCommand()
	{
		return new ShoutCommand(getRegExpr());
	}

	@Override
	public CommType getCommType()
	{
		return CommType.SHOUT;
	}

}
