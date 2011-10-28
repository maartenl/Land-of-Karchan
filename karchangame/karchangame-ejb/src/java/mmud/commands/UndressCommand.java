/*-------------------------------------------------------------------------
svninfo: $Id: UnwearCommand.java 1317 2011-06-12 10:23:06Z maartenl $
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
import mmud.exceptions.ParseException;
import mmud.database.entities.Player;
import mmud.database.ItemsDb;
import mmud.items.ItemException;
import mmud.items.PersonPositionEnum;
import mmud.Constants;

/**
 * You start stripping.
 * @see mmud.command.WearCommand
 */
public class UndressCommand extends NormalCommand
{

	public UndressCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(Player aPlayer) throws ItemException, ParseException,
			MmudException
	{
	        if (Constants.debugOn(aPlayer.getName()))
		{   
		        Logger.getLogger("mmud_debug").finest("run");
                }
		Logger.getLogger("mmud").finer("");
                return unwear(aPlayer, PersonPositionEnum.ON_HEAD) |
                unwear(aPlayer, PersonPositionEnum.ON_NECK) |
                unwear(aPlayer, PersonPositionEnum.ON_TORSO) |
                unwear(aPlayer, PersonPositionEnum.ON_ARMS) |
                unwear(aPlayer, PersonPositionEnum.ON_LEFT_WRIST) |
                unwear(aPlayer, PersonPositionEnum.ON_RIGHT_WRIST) |
                unwear(aPlayer, PersonPositionEnum.ON_LEFT_FINGER) |
                unwear(aPlayer, PersonPositionEnum.ON_RIGHT_FINGER) |
                unwear(aPlayer, PersonPositionEnum.ON_FEET) |
                unwear(aPlayer, PersonPositionEnum.ON_HANDS) |
                unwear(aPlayer, PersonPositionEnum.ON_WAIST) |
                unwear(aPlayer, PersonPositionEnum.ON_LEGS) |
                unwear(aPlayer, PersonPositionEnum.ON_EYES) |
                unwear(aPlayer, PersonPositionEnum.ON_EARS) |
                unwear(aPlayer, PersonPositionEnum.ABOUT_BODY);
	}

        private boolean unwear(Player aPlayer, PersonPositionEnum position)
                throws ItemException, ParseException,
			MmudException
        {
            return UnwearCommand.stopWearing(ItemsDb.getWornItemFromChar(aPlayer, position), aPlayer, position);
        }

	public Command createCommand()
	{
		return new UndressCommand(getRegExpr());
	}

}
