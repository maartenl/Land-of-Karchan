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

public class LookCommand implements Command
{

	String theResult = null;

	public boolean run(User aUser, String command)
		throws ItemException
	{
		if (Constants.logging)
		{
			System.err.println("LookCommand.run " + aUser + "," + command);
		}
		// initialise string, important otherwise previous instances will return this
		theResult = null;
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length > 2)
		{
			if (myParsed[1].equalsIgnoreCase("at"))
			{
				Item myItem = null;// = aUser.getItem(myParsed, 2, myParsed.length);
				if (myItem == null)
				{
//					myItem = aUser.getRoom().getItem(myParsed, 2, myParsed.length);
				}
				if (myItem == null)
				{	
					Character toChar = Characters.retrieveCharacter(myParsed[2]);
					if (toChar == null)
					{
						aUser.writeMessage("You cannot look at that.<BR>\r\n");
						return true;
					}
					aUser.writeMessage("You look at the " + toChar.getLongDescription() + ".<BR>\r\n");
					toChar.writeMessage(aUser.getName() + " looks at you.<BR>\r\n");
					aUser.sendMessage(toChar, aUser.getName() + " looks at " + toChar.getName() + ".<BR>\r\n");
					return true;
				}
				aUser.sendMessage(aUser.getName() + " looks at " + myItem.getDescription() + ".<BR>\r\n");
				aUser.writeMessage("You look at " + myItem.getDescription() + ".<BR>\r\n");
				theResult = myItem.getLongDescription();
				theResult += aUser.printForm();
				return true;
			}
			return false;
		}
		return true;
	}

	public String getResult()
	{
		if (Constants.logging)
		{
			System.err.println("LookCommand.getResult  returns " + theResult);
		}
		return theResult;
	}

}
