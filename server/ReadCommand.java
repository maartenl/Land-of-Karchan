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

public class ReadCommand implements Command
{

	String theResult = null;

	public boolean run(User aUser, String command)
		throws ItemException
	{
		if (Constants.logging)
		{
			System.err.println("ReadCommand.run " + aUser + "," + command);
		}
		// initialise string, important otherwise previous instances will return this
		theResult = null;
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length > 1)
		{
			Item myItem = null;// = aUser.getItem(myParsed, 1, myParsed.length);
			if (myItem == null)
			{
//				myItem = aUser.getRoom().getItem(myParsed, 1, myParsed.length);
			}
			if (myItem == null)
			{	
				aUser.writeMessage("You cannot read that.<BR>\r\n");
				return true;
			}
			if (!myItem.isAttribute("readable"))
			{
				aUser.writeMessage("You cannot read that.<BR>\r\n");
				return true;
			}
			aUser.sendMessage(aUser.getName() + " reads " + myItem.getDescription() + ".<BR>\r\n");
			aUser.writeMessage("You read " + myItem.getDescription() + ".<BR>\r\n");
			theResult = myItem.getAttribute("readable").getValue();
			theResult += aUser.printForm();
			return true;
		}
		return false;
	}

	public String getResult()
	{
		if (Constants.logging)
		{
			System.err.println("ReadCommand.getResult  returns " + theResult);
		}
		return theResult;
	}

}