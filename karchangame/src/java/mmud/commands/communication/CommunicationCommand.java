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
package mmud.commands.communication;

import mmud.commands.TargetCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 *
 * @author maartenl
 */
public abstract class CommunicationCommand extends TargetCommand
{

    public abstract CommType getCommType();

    public CommunicationCommand(String regExpr)
    {
        super(regExpr);
    }

    @Override
    protected DisplayInterface actionTo(User aUser, Person aTarget, String verb, String message) throws MudException
    {
        if (message == null)
        {
            // "ask to Marvin", ask what?
            return null;
        }
//        if (aUser.isIgnored(toChar))
//        {
//            aUser.writeMessage(toChar.getName()
//                    + " is ignoring you fully.<BR>\r\n");
//            return true;
//        }
        aUser.getRoom().sendMessageExcl(aUser, aTarget, "%SNAME "
                + getCommType().getPlural() + " [to %TNAME] : "
                + message+ "<BR>\r\n");
        aUser.writeMessage(aUser, aTarget, "<B>%SNAME "
                + getCommType().toString() + " [to %TNAME]</B> : "
                + message+ "<BR>\r\n");
        aTarget.writeMessage(aUser, aTarget, "<B>%SNAME "
                + getCommType().getPlural() + " [to %TNAME]</B> : "
                + message+ "<BR>\r\n");
        return aUser.getRoom();
    }

    @Override
    protected DisplayInterface action(User aUser, String verb, String command) throws MudException
    {
        if (command == null)
        {
            // "ask", ask what?
            return null;
        }
        aUser.getRoom().sendMessageExcl(aUser, "%SNAME "
                + getCommType().getPlural() + " : " + command
                + "<BR>\r\n");
        aUser.writeMessage(aUser, "<B>%SNAME " + getCommType().toString()
                + "</B> : " + command + "<BR>\r\n");
        return aUser.getRoom();
    }
}
