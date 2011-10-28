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

import mmud.exceptions.MmudException;
import mmud.database.entities.Persons;
import mmud.database.entities.Player;
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
	public boolean run(Player aPlayer) throws MmudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aPlayer))
		{
			return false;
		}

		Persons
				.sendMessage(aPlayer, aPlayer.getRoom().getSouth(), "Someone "
						+ getCommType().getPlural() + " : " + getMessage()
						+ "<BR>\r\n");
		Persons
				.sendMessage(aPlayer, aPlayer.getRoom().getNorth(), "Someone "
						+ getCommType().getPlural() + " : " + getMessage()
						+ "<BR>\r\n");
		Persons
				.sendMessage(aPlayer, aPlayer.getRoom().getWest(), "Someone "
						+ getCommType().getPlural() + " : " + getMessage()
						+ "<BR>\r\n");
		Persons
				.sendMessage(aPlayer, aPlayer.getRoom().getEast(), "Someone "
						+ getCommType().getPlural() + " : " + getMessage()
						+ "<BR>\r\n");
		Persons
				.sendMessage(aPlayer, aPlayer.getRoom().getUp(), "Someone "
						+ getCommType().getPlural() + " : " + getMessage()
						+ "<BR>\r\n");
		Persons
				.sendMessage(aPlayer, aPlayer.getRoom().getDown(), "Someone "
						+ getCommType().getPlural() + " : " + getMessage()
						+ "<BR>\r\n");

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
