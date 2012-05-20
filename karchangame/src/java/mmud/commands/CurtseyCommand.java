/*
 *  Copyright (C) 2012 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmud.commands;

import java.util.logging.Logger;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 *
 * @author maartenl

 /**
 * Curtsey to someone: "curtsey to Karn".
 */
public class CurtseyCommand extends TargetCommand
{

    public CurtseyCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    public Command createCommand()
    {
        return new CurtseyCommand(getRegExpr());
    }

    @Override
    protected DisplayInterface actionTo(User aUser, Person aTarget, String verb, String command) throws MudException
    {
        if (aUser == aTarget)
        {
            aUser.writeMessage("Drop a curtsey to myself? What are you trying to do?<BR>\r\n");
            return aUser.getRoom();
        }
        aUser.getRoom().sendMessage(aUser, aTarget,
                "%SNAME drop%VERB2 a curtsey to %TNAME.<BR>\r\n");
        return aUser.getRoom();

    }

    @Override
    protected DisplayInterface action(User aUser, String verb, String command) throws MudException
    {
        aUser.getRoom().sendMessage(aUser, "%SNAME drop%VERB2 a curtsey.<BR>\r\n");
        return aUser.getRoom();
    }
}
