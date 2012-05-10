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

import mmud.Utils;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * Bow : "bow".
 * @author maartenl
 */
public class BowCommand extends NormalCommand
{

    public BowCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    private void bowTo(User aUser, Person aTarget, String[] parsed) throws MudException
    {
        if (parsed.length == 4)
        {
            if (Utils.existsAdverb(parsed[3]))
            {
                // bow to Marvin evilly
                aUser.getRoom().sendMessage(aUser, aTarget,
                        "%SNAME bow%VERB2 to %TNAME "
                        + parsed[3].toLowerCase()
                        + ".<BR>\r\n");
                return;
            }
            // bow to Marvin unknownadverb
            aUser.writeMessage("Unknown adverb found.<BR>\r\n");
            return;
        }
        // bow to Marvin
        aUser.getRoom().sendMessage(aUser, aTarget, "%SNAME bow%VERB2 to %TNAME.<BR>\r\n");
    }

    private void bow(User aUser, String[] parsed) throws MudException
    {
        if (parsed.length == 2)
        {
            if (!Utils.existsAdverb(parsed[1]))
            {            // bow unknownadverb

                aUser.writeMessage("Unknown adverb found.<BR>\r\n");
                return;
            }           // bow evilly
            aUser.getRoom().sendMessage(aUser, "%SNAME bow%VERB2 "
                    + parsed[1].toLowerCase() + ".<BR>\r\n");
            return;
        }
        // bow
        aUser.getRoom().sendMessage(aUser, "%SNAME bow%VERB2.<BR>\r\n");

    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] myParsed = parseCommand(command);
        if (myParsed.length > 2 && myParsed[1].equalsIgnoreCase("to"))
        {
            Person toChar = aUser.getRoom().retrievePerson(myParsed[2]);
            if (toChar == null)
            {
                // bow to unknown
                aUser.writeMessage("Cannot find that person.<BR>\r\n");
                return aUser.getRoom();
            }
            bowTo(aUser, toChar, myParsed);
        } else
        {
            bow(aUser, myParsed);
        }
        return aUser.getRoom();
    }
}
