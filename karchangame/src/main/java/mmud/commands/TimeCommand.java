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
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * Displays the current time: "time".
 * @author maarten
 */
public class TimeCommand extends NormalCommand
{

    public TimeCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        Calendar myCalendar = Calendar.getInstance();
        aUser.writeMessage("Current time is "
                + myCalendar.get(Calendar.HOUR_OF_DAY) + ":"
                + +myCalendar.get(Calendar.MINUTE) + ":"
                + +myCalendar.get(Calendar.SECOND) + ".<BR>\r\n");
        return aUser.getRoom();
    }
}
