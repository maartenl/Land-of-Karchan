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

import java.util.stream.Collectors;

import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.enums.God;
import mmud.exceptions.MudException;
import mmud.exceptions.PersonNotFoundException;
import mmud.rest.services.EventsRestService;
import mmud.services.CommunicationService;
import mmud.services.PersonBean;
import mmud.services.PersonCommunicationService;

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
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);

    if (aUser.getGod() != God.GOD)
    {
      communicationService.writeMessage("You are not an administrator.<br/>\r\n");
      return aUser.getRoom();

    }
    Administrator administrator = (Administrator) aUser;
    if (command.equalsIgnoreCase("admin reset commands"))
    {
      CommandFactory.clearUserCommandStructure();
      communicationService.writeMessage("User commands have been reloaded from database.<br/>\r\n");
    }
    if (command.toLowerCase().startsWith("admin wall"))
    {
      PersonBean personBean = getPersonBean();
      final String message = command.substring(11);
      personBean.sendWall(message);
      getLogBean().writeLog(administrator, "admin command 'wall' executed.", message);
      communicationService.writeMessage("Wall message sent.<br/>\r\n");
    }
    if (command.toLowerCase().startsWith("admin runevent"))
    {
      EventsRestService eventsRestService = getEventsBean();
      eventsRestService.runSingleEvent(administrator, Integer.valueOf(command.substring(15)));
      communicationService.writeMessage("Event run attempted.<br/>\r\n");
    }
    if (command.toLowerCase().startsWith("admin visible"))
    {
      if (command.equalsIgnoreCase("admin visible off"))
      {
        aUser.setVisible(false);
        communicationService.writeMessage("Setting visibility to false.<br/>\r\n");
        getLogBean().writeLog(aUser, " turned invisible.");
      }
      if (command.equalsIgnoreCase("admin visible on"))
      {
        aUser.setVisible(true);
        communicationService.writeMessage("Setting visibility to true.<br/>\r\n");
        getLogBean().writeLog(aUser, " turned visible.");
      }
    }
    if (command.toLowerCase().startsWith("admin chatlines"))
    {
      String table =
        "<table><tr><th>id</th><th>name</th><th>attribute</th><th>colour</th><th>last used</th></tr>" +
          getPersonBean().getChatlines().stream()
            .map(chat -> "<tr><td>" + chat.getId() + "</td><td>" + chat.getChatname() + "</td><td>" + chat.getAttributename() + "</td><td>" + chat.getColour() + "</td><td>" + chat.getLastchattime() +
              "</td></tr>")
            .collect(Collectors.joining()) +
          "</table><br/>\r\n";
      communicationService.writeMessage(table);
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
      communicationService.writeMessage("Changed " + person.getName() + " into frog (" + amount + ").<br/>\r\n");
      CommunicationService.getCommunicationService(person.getRoom()).sendMessage(person, "%SNAME %SISARE suddenly changed into a frog!<br/>\r\n");
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
      int amount = 0;
      User person = personBean.getActiveUser(commands[2]);
      if (person == null)
      {
        throw new PersonNotFoundException(commands[2] + " not found.");
      }
      if (commands.length == 4)
      {
        try
        {
          amount = Integer.parseInt(commands[3]);
          person.setJackassing(amount);
        } catch (NumberFormatException f)
        {
          throw new MudException("Number for amount could not be parsed.");
        }
      }
      communicationService.writeMessage("Changed " + person.getName() + " into jackass (" + amount + ").<br/>\r\n");
      CommunicationService.getCommunicationService(person.getRoom()).sendMessage(person, "%SNAME %SISARE suddenly changed into a jackass!<br/>\r\n");
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
          if (minutes <= 0)
          {
            throw new NumberFormatException("Expected number bigger than zero.");
          }
          person.setTimeout(minutes);
        } catch (NumberFormatException f)
        {
          throw new MudException("Number for minutes could not be parsed.", f);
        }
      }
      person.deactivate();
      personBean.sendWall(administrator.getName() + " causes " + person.getName() + " to cease to exist for " + minutes + " minutes.<br/>\n");
      getLogBean().writeLog(person, " has been kicked out of the game by " + aUser.getName() + " for " + minutes + " minutes.");
    }
    if (command.equalsIgnoreCase("admin help"))
    {
      communicationService.writeMessage("Possible commands are:<ul>"
        + "<li>admin help - this help text</li>"
        + "<li>admin wall &lt;message&gt; - pages all users with the message entered</li>"
        + "<li>admin chatlines - shows all available chatlines, with info</li>"
        + "<li>admin kick &lt;playername&gt; [&lt;minutes&gt;] - kicks a user off the game. When the minutes are provided,"
        + "said user will not be able to logon for that many minutes.</li>"
        + "<li>admin frog &lt;playername&gt; [&lt;amount&gt;] - turns the user into a frog, automatically turns back after [amount] ribbits."
        + "<li>admin jackass &lt;playername&gt; [&lt;amount&gt;] - turns the user into a jackass, automatically turns back after [amount] heehaws."
        + "<li>admin visible [on | off] - makes an administrator not show up in the wholist or in a room.</li>"
        + "<li>admin runevent &lt;eventid&gt; - runs an event, in order to see if it works.</li>"
        + "<li>admin reset commands - reset the cached <i>special</i> commands."
        + "Necessary if a command has been deleted, added or changed.</li>"
        + "</ul>\r\n");
    }
    return aUser.getRoom();
  }

}
