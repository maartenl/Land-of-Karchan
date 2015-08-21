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

import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.enums.God;
import mmud.exceptions.MudException;
import mmud.exceptions.PersonNotFoundException;
import mmud.rest.services.EventsBean;
import mmud.rest.services.PersonBean;

/**
 * Shows the current date in the game: "date".
 *
 * @author maartenl
 */
public class AdminCommand extends NormalCommand
{

    public AdminCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        if (aUser.getGod() != God.GOD)
        {
            aUser.writeMessage("You are not an administrator.<br/>\r\n");
            return aUser.getRoom();

        }
        Administrator administrator = (Administrator) aUser;
        if (command.equalsIgnoreCase("admin reset commands"))
        {
            CommandFactory.clearUserCommandStructure();
            aUser.writeMessage("User commands have been reloaded from database.<br/>\r\n");
        }
        if (command.toLowerCase().startsWith("admin wall"))
        {
            PersonBean personBean = getPersonBean();
            personBean.sendWall(administrator, command.substring(11));
            aUser.writeMessage("Wall message sent.<br/>\r\n");
        }
        if (command.toLowerCase().startsWith("admin runevent"))
        {
            EventsBean eventsBean = getEventsBean();
            eventsBean.runSingleEvent(administrator, Integer.valueOf(command.substring(15)));
            aUser.writeMessage("Event run attempted.<br/>\r\n");
        }
        if (command.toLowerCase().startsWith("admin visible"))
        {
            if (command.equalsIgnoreCase("admin visible off"))
            {
                aUser.setVisible(false);
                aUser.writeMessage("Setting visibility to false.<br/>\r\n");
                getLogBean().writeLog(aUser, " turned invisible.");
            }
            if (command.equalsIgnoreCase("admin visible on"))
            {
                aUser.setVisible(true);
                aUser.writeMessage("Setting visibility to true.<br/>\r\n");
                getLogBean().writeLog(aUser, " turned visible.");
            }
        }
        if (command.toLowerCase().startsWith("admin frog"))
        {
            PersonBean personBean = getPersonBean();
            String[] commands = command.split(" ");
            if (commands.length < 3)
            {
                throw new MudException("Expected admin frog command in the shape of 'admin frog personname amount'.");
            }
            Integer amount = 0;
            User person = personBean.getActiveUser(commands[2]);
            if (person == null)
            {
                throw new PersonNotFoundException(commands[2] + " not found.");
            }
            if (commands.length == 4)
            {
                try
                {
                    amount = Integer.valueOf(commands[3]);
                    person.setFrogging(amount);
                } catch (NumberFormatException f)
                {
                    throw new MudException("Number for amount could not be parsed.");
                }
            }
            aUser.writeMessage("Changed " + person.getName() + " into frog (" + amount + ").<br/>\r\n");
            person.getRoom().sendMessage(person, "%SNAME %SISARE suddenly changed into a frog!<br/>\r\n");
            getLogBean().writeLog(person, " was changed into a frog by " + aUser.getName() + " for " + amount + ".");
        }
        if (command.toLowerCase().startsWith("admin jackass"))
        {
            PersonBean personBean = getPersonBean();
            String[] commands = command.split(" ");
            if (commands.length < 3)
            {
                throw new MudException("Expected admin jackass command in the shape of 'admin jackass personname amount'.");
            }
            Integer amount = 0;
            User person = personBean.getActiveUser(commands[2]);
            if (person == null)
            {
                throw new PersonNotFoundException(commands[2] + " not found.");
            }
            if (commands.length == 4)
            {
                try
                {
                    amount = Integer.valueOf(commands[3]);
                    person.setJackassing(amount);
                } catch (NumberFormatException f)
                {
                    throw new MudException("Number for amount could not be parsed.");
                }
            }
            aUser.writeMessage("Changed " + person.getName() + " into jackass (" + amount + ").<br/>\r\n");
            person.getRoom().sendMessage(person, "%SNAME %SISARE suddenly changed into a jackass!<br/>\r\n");
            getLogBean().writeLog(person, " was changed into a jackass by " + aUser.getName() + " for " + amount + ".");
        }
        if (command.toLowerCase().startsWith("admin kick"))
        {
            PersonBean personBean = getPersonBean();
            String[] commands = command.split(" ");
            if (commands.length < 3)
            {
                throw new MudException("Expected admin kick command in the shape of 'admin kick personname minutes'.");
            }
            Integer minutes = 0;
            User person = personBean.getActiveUser(commands[2]);
            if (person == null)
            {
                throw new PersonNotFoundException(commands[2] + " not found.");
            }
            if (commands.length == 4)
            {
                try
                {
                    minutes = Integer.valueOf(commands[3]);
                    person.setTimeout(minutes);
                } catch (NumberFormatException f)
                {
                    throw new MudException("Number for minutes could not be parsed.");
                }
            }
            person.deactivate();
            getLogBean().writeLog(person, " has been kicked out of the game by " + aUser.getName() + " for " + minutes + ".");
        }
        if (command.equalsIgnoreCase("admin help"))
        {
            aUser.writeMessage("Possible commands are:<ul>"
                    + "<li>admin help - this help text</li>"
                    + "<li>admin wall &lt;message&gt; - pages all users with the message entered</li>"
                    + "<li>admin kick &lt;playername&gt; [&lt;minutes&gt;] - kicks a user off the game. When the minutes are provided,"
                    + "<li>admin frog &lt;playername&gt; [&lt;amount&gt;] - turns the user into a frog, automatically turns back after [amount] ribbits."
                    + "<li>admin jackass &lt;playername&gt; [&lt;amount&gt;] - turns the user into a jackass, automatically turns back after [amount] heehaws."
                    + "said user will not be able to logon for that many minutes.</li>"
                    + "<li>admin visible [on | off] - makes an administrator not show up in the wholist or in a room.</li>"
                    + "<li>admin runevent &lt;eventid&gt; - runs an event, in order to see if it works.</li>"
                    + "<li>admin reset commands - reset the cached <i>special</i> commands."
                    + "Necessary if a command has been deleted, added or changed.</li>"
                    + "</ul>\r\n");
        }
        return aUser.getRoom();
    }

}
