/*-------------------------------------------------------------------------
svninfo: $Id: SleepCommand.java 1192 2009-10-20 05:42:27Z maartenl $
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

/**
 * Gets or sets the condition/state of your character.
 */
public class ConditionCommand extends NormalCommand
{

	public ConditionCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(Player aPlayer) throws MmudException
	{
		Logger.getLogger("mmud").finer("");
                String[] myParsed = getParsedCommand();
		if (myParsed.length == 1)
		{
                    if (aPlayer.getState() == null)
                    {
			aPlayer.writeMessage("You do not have a current condition.<br/>\r\n");
                        return true;
                    }
			aPlayer.writeMessage("Your current condition is \"" + aPlayer.getState() + "\".<br/>\r\n");
                        return true;
		}
                if (myParsed.length == 2 && myParsed[1].equals("empty"))
                {
                    aPlayer.setState(null);
                    aPlayer.writeMessage("Removed your current condition.<br/>\r\n");
                    return true;
                }

                aPlayer.setState(getCommand().substring("condition ".length()));
                Persons.sendMessage(aPlayer, "Condition set.<BR>\r\n");
		return true;
	}

	public Command createCommand()
	{
		return new ConditionCommand(getRegExpr());
	}

}
