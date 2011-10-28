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

import mmud.MethodDoesNotExistException;
import mmud.exceptions.MmudException;
import mmud.PlayerCommandInfo;
import mmud.database.entities.Player;
import mmud.database.Database;

/**
 * The Script Command. Runs a script.
 * 
 * @see mmud.Constants#getCommand
 */
public class ScriptCommand extends NormalCommand
{
	private final PlayerCommandInfo theCommandInfo;

	private String theResult;

	/**
	 * Constructor for the script command.
	 * 
	 * @param aCommandInfo
	 *            data class containing information about the command.
	 */
	public ScriptCommand(PlayerCommandInfo aCommandInfo)
	{
		super(aCommandInfo.getCommand());
		theCommandInfo = aCommandInfo;
	}

	@Override
	public boolean run(Player aPlayer) throws MmudException
	{
		Logger.getLogger("mmud").finer("");
		if ((theCommandInfo.getRoom() != null)
				&& (aPlayer.getRoom().getId() != theCommandInfo.getRoom()
						.intValue()))
		{
			return false;
		}
		String mySource = Database.getMethodSource(theCommandInfo
				.getMethodName());
		Object stuff = null;
		try
		{
			if (mySource == null)
			{
				throw new MethodDoesNotExistException(" ("
						+ theCommandInfo.getMethodName() + ")");
			}
			stuff = aPlayer.runScript("command", mySource, getParsedCommand());
		} catch (MmudException e)
		{
			theCommandInfo.deactivateCommand();
			throw e;
		}
		if (!(stuff instanceof String))
		{
			theResult = null;
			return true;
		}
		theResult = (String) stuff;
		if ("false".equalsIgnoreCase(theResult))
		{
			theResult = null;
			return false;
		}
		theResult += aPlayer.printForm();
		return true;
	}

	@Override
	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return theResult;
	}

	/**
	 * There are too many parameters required for this one to be automatically
	 * created using the standard. Instead, this method will return the object
	 * on which it was called. This should not be a problem, as script commands
	 * are always created anew.
	 * 
	 * @return <I>this</I>
	 */
	public Command createCommand()
	{
		return this;
	}

}
