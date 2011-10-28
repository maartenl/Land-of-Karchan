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

import mmud.MudException;
import mmud.characters.Person;
import mmud.characters.Persons;
import mmud.characters.User;
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
	public boolean run(User aUser) throws MudException {
		Logger.getLogger("mmud").finer("");
		if (!super.run(aUser)) {
			return false;
		}
		aUser.getGuild();
		String[] myParsed = getParsedCommand();
		Person toChar2 = Persons.getPerson(myParsed[1]);

		if ((toChar2 == null) || (!(toChar2 instanceof User))) {
			aUser.writeMessage("Cannot find that person.<BR>\r\n");
			return true;
		}
		User toChar = (User) toChar2;
		if (!aUser.getGuild().equals(toChar.getGuild())) {
			aUser
					.writeMessage("That person is not a member of your guild.<BR>\r\n");
			return true;
		}
		toChar.setGuild(null);
		aUser.getGuild().decreaseAmountOfMembers();
		Database.writeLog(aUser.getName(), "removed " + toChar.getName()
				+ " from guild " + aUser.getGuild().getName());
		aUser.writeMessage("You have removed " + toChar.getName()
				+ " from your guild.<BR>\r\n");
		Persons.sendGuildMessage(aUser, aUser.getGuild(), "<B>"
				+ toChar.getName()
				+ "</B> has been removed from the guild.<BR>\r\n");
		if (toChar.isActive()) {
			toChar.writeMessage("You have been removed from the guild <I>"
					+ aUser.getGuild().getTitle() + "</I>.<BR>\r\n");
		}
		return true;
	}

	public Command createCommand() {
		return new RemoveCommand(getRegExpr());
	}

}
