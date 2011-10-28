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
import mmud.InvalidMailException;
import mmud.MailException;
import mmud.exceptions.MmudException;
import mmud.database.entities.Person;
import mmud.database.entities.Persons;
import mmud.database.entities.Player;
import mmud.database.Database;
import mmud.database.MailDb;

/**
 * Send a mudmail to someone. expected command something like :
 * <P>
 * <B>sendmail [to] [header length] [header] [body] </B>
 */
public class SendMailCommand extends NormalCommand
{

	public SendMailCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(Player aPlayer) throws MailException, MmudException
	{
		Logger.getLogger("mmud").finer("");
		String command = getCommand();
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length > 4)
		{
			Person toChar = Persons.retrievePerson(myParsed[1]);
			if (toChar == null)
			{
				toChar = Database.getPlayer(myParsed[1], "");
			}
			if ((toChar == null) || (!(toChar instanceof Player)))
			{
				aPlayer.writeMessage("Cannot find that person.<BR>\r\n");
				return true;
			}
			Player toPlayer = (Player) toChar;
			int size = 0, start = 0;
			String header;
			String message;
			try
			{
				size = Integer.parseInt(myParsed[2]);
			} catch (NumberFormatException e)
			{
				Logger.getLogger("mmud").info(
						"thrown: " + Constants.INVALIDMAILERROR);
				throw new InvalidMailException();
			}
			Logger.getLogger("mmud").finer("");
			start = 8 + 1 + toPlayer.getName().length() + 1
					+ myParsed[2].length() + 1;
			header = command.substring(start, size + start);
			message = command.substring(start + size + 1 - 1);
			MailDb.sendMail(aPlayer, toPlayer, header, message);
			aPlayer.writeMessage("Mail sent.<BR>\r\n");
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new SendMailCommand(getRegExpr());
	}

}
