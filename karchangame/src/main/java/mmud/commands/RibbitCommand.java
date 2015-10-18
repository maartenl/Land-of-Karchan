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

import java.util.Calendar;
import java.util.GregorianCalendar;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * The Ribbit Command: "Rrribbit". Makes you go "Rrribbit". It's a punishment
 * method. It is not allowed to execute this command faster than once every 10
 * seconds.
 *
 * @author maartenl
 * @see HeehawCommand
 */
public class RibbitCommand extends NormalCommand
{

    public RibbitCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        if (aUser.getFrogging() == 0)
        {
            return null;
        }
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.SECOND, -10);
        if (calendar.getTime().before(aUser.getLastcommand()))
        {
            aUser.writeMessage("You cannot say 'Ribbit' that fast! You will get tongue tied!<br/>\r\n");
            return aUser.getRoom();
        }
        aUser.getRoom().sendMessage(aUser, "A frog called " + aUser.getName() + " says \"Rrribbit!\""
                + ".<br/>\n");
        aUser.lessFrogging();
        if (aUser.getFrogging() == 0)
        {
            aUser.getRoom().sendMessage(aUser, "A frog called " + aUser.getName() + " magically changes back to a " + aUser.getRace() + ".<br/>\n");
        } else
        {
            aUser.writeMessage("You feel the need to say 'Ribbit' just " + aUser.getFrogging() + " times.<br/>\r\n");
        }
        return aUser.getRoom();
    }

}