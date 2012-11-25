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
package mmud.commands.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mmud.commands.*;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * Look at stuff: "look at well". There are three different possibilities:
 * <ul>
 * <li>look at something in your inventory.
 * <li>look at something in the room that you occupy
 * <li>look at a person in the same room as you
 * </ul>
 * @author maartenl
 */
public class LookAtCommand extends NormalCommand
{

    public LookAtCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        DisplayInterface resultaat = null;
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "look"
        parsed.remove(0); // remove "at"
        // find the item on ourselves
        resultaat = aUser.findItem(parsed);
        if (resultaat == null)
        {
            // find the item in the room
            resultaat = aUser.getRoom().findItem(parsed);
        }
        if (resultaat == null)
        {
            if (parsed.size() == 1)
            {
                // look at the person, who hopefully is identified by the command
                Person toChar = aUser.getRoom().retrievePerson(parsed.get(0));
                if (toChar != null)
                {
                    aUser.getRoom().sendMessage(aUser, toChar, "%SNAME look%VERB2 at %TNAME.<br/>\r\n");
                    resultaat = toChar;
                }
            }
        }
        if (resultaat == null)
        {
            // we didn't find what we wanted to look at.
            resultaat = aUser.getRoom();
            aUser.writeMessage("You don't know what to look at.<br/>\n");
        }
        return resultaat;
    }
}
