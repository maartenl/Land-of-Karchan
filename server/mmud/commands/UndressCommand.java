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

import mmud.MudException;
import mmud.ParseException;
import mmud.characters.User;
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
	public boolean run(User aUser) throws ItemException, ParseException,
			MudException
	{
	        if (Constants.debugOn(aUser.getName()))
		{   
		        Logger.getLogger("mmud_debug").finest("run");
                }
		Logger.getLogger("mmud").finer("");
                return unwear(aUser, PersonPositionEnum.ON_HEAD) |
                unwear(aUser, PersonPositionEnum.ON_NECK) |
                unwear(aUser, PersonPositionEnum.ON_TORSO) |
                unwear(aUser, PersonPositionEnum.ON_ARMS) |
                unwear(aUser, PersonPositionEnum.ON_LEFT_WRIST) |
                unwear(aUser, PersonPositionEnum.ON_RIGHT_WRIST) |
                unwear(aUser, PersonPositionEnum.ON_LEFT_FINGER) |
                unwear(aUser, PersonPositionEnum.ON_RIGHT_FINGER) |
                unwear(aUser, PersonPositionEnum.ON_FEET) |
                unwear(aUser, PersonPositionEnum.ON_HANDS) |
                unwear(aUser, PersonPositionEnum.ON_WAIST) |
                unwear(aUser, PersonPositionEnum.ON_LEGS) |
                unwear(aUser, PersonPositionEnum.ON_EYES) |
                unwear(aUser, PersonPositionEnum.ON_EARS) |
                unwear(aUser, PersonPositionEnum.ABOUT_BODY);
	}

        private boolean unwear(User aUser, PersonPositionEnum position)
                throws ItemException, ParseException,
			MudException
        {
            return UnwearCommand.stopWearing(ItemsDb.getWornItemFromChar(aUser, position), aUser, position);
        }

	public Command createCommand()
	{
		return new UndressCommand(getRegExpr());
	}

}
