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

public class EastCommand extends NormalCommand
{

	public boolean run(User aUser, String command)
	{
		if (Constants.logging)
		{
			System.err.println("EastCommand.run " + aUser + "," + command);
		}
		Room myRoom = aUser.getRoom();
		if (myRoom.getEast() != null)
		{
			aUser.writeMessage("You leave east.<BR>\r\n");
			aUser.sendMessage(aUser.getName() + " leaves east.<BR>\r\n");
			aUser.setRoom(myRoom.getEast());
			aUser.sendMessage(aUser.getName() + " appears.<BR>\r\n");
		}
		else
		{
			aUser.writeMessage("You cannot go east.<BR>\r\n");
		}
		return true;
	}

}