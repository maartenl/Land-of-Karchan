/*-------------------------------------------------------------------------
svninfo: $Id: PkillCommand.java 994 2005-10-23 10:19:20Z maartenl $
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
package mmud.commands.guilds;

import java.util.logging.Logger;

import mmud.exceptions.MmudException;
import mmud.database.entities.Person;
import mmud.database.entities.Persons;
import mmud.database.entities.Player;
import mmud.commands.Command;
import mmud.database.Database;

/**
 * Makes you, as guildmaster, remove a member of the guild forcibly. Command
 * syntax something like : <TT>guildremove &lt;person&gt;</TT>
 */
public class RemoveCommand extends GuildMasterCommand {

	public RemoveCommand(String aRegExpr) {
		super(aRegExpr);
	}

	@Override
	public boolean run(Player aPlayer) throws MmudException {
		Logger.getLogger("mmud").finer("");
		if (!super.run(aPlayer)) {
			return false;
		}
		aPlayer.getGuild();
		String[] myParsed = getParsedCommand();
		Person toChar2 = Persons.getPerson(myParsed[1]);

		if ((toChar2 == null) || (!(toChar2 instanceof Player))) {
			aPlayer.writeMessage("Cannot find that person.<BR>\r\n");
			return true;
		}
		Player toChar = (Player) toChar2;
		if (!aPlayer.getGuild().equals(toChar.getGuild())) {
			aPlayer
					.writeMessage("That person is not a member of your guild.<BR>\r\n");
			return true;
		}
		toChar.setGuild(null);
		aPlayer.getGuild().decreaseAmountOfMembers();
		Database.writeLog(aPlayer.getName(), "removed " + toChar.getName()
				+ " from guild " + aPlayer.getGuild().getName());
		aPlayer.writeMessage("You have removed " + toChar.getName()
				+ " from your guild.<BR>\r\n");
		Persons.sendGuildMessage(aPlayer, aPlayer.getGuild(), "<B>"
				+ toChar.getName()
				+ "</B> has been removed from the guild.<BR>\r\n");
		if (toChar.isActive()) {
			toChar.writeMessage("You have been removed from the guild <I>"
					+ aPlayer.getGuild().getTitle() + "</I>.<BR>\r\n");
		}
		return true;
	}

	public Command createCommand() {
		return new RemoveCommand(getRegExpr());
	}

}
