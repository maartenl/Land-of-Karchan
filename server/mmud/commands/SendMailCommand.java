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

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * expected command something like :<P>
 * <B>sendmail [to] [header length] [header] [body]
 * </B>
 */
public class SendMailCommand extends NormalCommand
{

	public boolean run(User aUser, String command)
		throws MailException
	{
		if (Constants.logging)
		{
			System.err.println("SendMailCommand.run " + aUser + "," + command);
		}
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length > 4)
		{
            Person toChar = Persons.retrievePerson(myParsed[1]);
            if ((toChar == null) || (!(toChar instanceof User)))
            {
                aUser.writeMessage("Cannot find that person.<BR>\r\n");
            }
            else
			{
				User toUser = (User) toChar;
				int size = 0, start = 0;
				String header;
				String message;
				try
				{
					size = Integer.parseInt(myParsed[2]);
				}
				catch (NumberFormatException e)
				{
					if (Constants.logging)
					{
						System.err.println("thrown: " + Constants.INVALIDMAILERROR);
					}
					throw new MailException(Constants.INVALIDMAILERROR);
				}
				if (Constants.logging)
				{
					System.err.println("SendMailCommand.run " + size);
				}
				start = 8 + 1 + toUser.getName().length() + 1 + myParsed[2].length() + 1;
				if (Constants.logging)
				{
					System.err.println("SendMailCommand.run " + start);
				}
				header = command.substring(start, size + start);
				if (Constants.logging)
				{
					System.err.println("SendMailCommand.run " + header);
				}
				message = command.substring(start + size + 1 - 1);
				if (Constants.logging)
				{
					System.err.println("SendMailCommand.run " + message);
				}
				Database.sendMail(aUser, toUser, header, message);
				aUser.writeMessage("Mail sent.<BR>\r\n");
			}
		}
		return false;
	}

}
