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
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Chatline;
import mmud.database.entities.game.Chatlineusers;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.enums.God;
import mmud.exceptions.MudException;
import mmud.exceptions.PersonNotFoundException;
import mmud.services.CommunicationService;
import mmud.services.EventsService;
import mmud.services.PersonCommunicationService;
import mmud.services.PersonService;

import java.util.stream.Collectors;

/**
 * Admin commands are commands executed by users that have god set to 1.
 *
 * @author maartenl
 * @see God#GOD
 */
public class AdminCommand extends NormalCommand
{

  public static final String ADMIN_RESET_COMMANDS = "admin reset commands";
  public static final String ADMIN_WALL = "admin wall";
  public static final String ADMIN_RUNEVENT = "admin runevent";
  public static final String ADMIN_VISIBLE = "admin visible";
  public static final String ADMIN_CHATLINE = "admin chatline ";
  public static final String ADMIN_CHATLINES = "admin chatlines";
  public static final String ADMIN_FROG = "admin frog";
  public static final String ADMIN_JACKASS = "admin jackass";
  public static final String ADMIN_KICK = "admin kick";
  public static final String ADMIN_HELP = "admin help";

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
      getLogService().writeLog(aUser, " tried admin commands, but is not an administrator.");
      return aUser.getRoom();

    }
    Administrator administrator = (Administrator) aUser;
    if (command.equalsIgnoreCase(ADMIN_RESET_COMMANDS))
    {
      CommandFactory.clearUserCommandStructure();
      communicationService.writeMessage("User commands have been reloaded from database.<br/>\r\n");
    }
    if (command.toLowerCase().startsWith(ADMIN_WALL))
    {
      PersonService personService = getPersonService();
      final String message = command.substring(11);
      personService.sendWall(message);
      getLogService().writeLog(administrator, "admin command 'wall' executed.", message);
      communicationService.writeMessage("Wall message sent.<br/>\r\n");
    }
    if (command.toLowerCase().startsWith(ADMIN_RUNEVENT))
    {
      EventsService eventsService = getEventsService();
      eventsService.runSingleEvent(administrator, Integer.valueOf(command.substring(15)));
      communicationService.writeMessage("Event run attempted.<br/>\r\n");
    }
    if (command.toLowerCase().startsWith(ADMIN_VISIBLE))
    {
      if (command.equalsIgnoreCase("admin visible off"))
      {
        aUser.setVisible(false);
        communicationService.writeMessage("Setting visibility to false.<br/>\r\n");
        getLogService().writeLog(aUser, " turned invisible.");
      }
      if (command.equalsIgnoreCase("admin visible on"))
      {
        aUser.setVisible(true);
        communicationService.writeMessage("Setting visibility to true.<br/>\r\n");
        getLogService().writeLog(aUser, " turned visible.");
      }
    }
    if (command.toLowerCase().startsWith(ADMIN_CHATLINE))
    {
      final String chatline = command.substring(ADMIN_CHATLINE.length());
      getChatService().getChatline(chatline).ifPresent(chatline1 -> writeUsersOfChatline(chatline1,
          communicationService));
    }
    if (command.toLowerCase().startsWith(ADMIN_CHATLINES))
    {
      String table =
          "<table><tr><th>id</th><th>name</th><th>attribute</th><th>colour</th><th>last used</th><th>owner</th></tr>" +
              getChatService().getChatlines().stream()
                  .map(chat ->
                      "<tr><td>" + chat.getId() +
                          "</td><td>" + chat.getChatname() +
                          "</td><td>" + chat.getAttributename() +
                          "</td><td>" + chat.getColour() +
                          "</td><td>" + chat.getLastchattime() +
                          "</td><td>" + (chat.getOwner() != null ? chat.getOwner().getName() : null) +
                          "</td></tr>")
                  .collect(Collectors.joining()) +
              "</table><br/>\r\n";
      communicationService.writeMessage(table);
    }
    if (command.toLowerCase().startsWith(ADMIN_FROG))
    {
      PersonService personService = getPersonService();
      String[] commands = command.split(" ");
      if (commands.length < 3)
      {
        throw new MudException("Expected admin frog command in the shape of 'admin frog personname amount'.");
      }
      Integer amount = 0;
      User person = personService.getActiveUser(commands[2]);
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
      CommunicationService.getCommunicationService(person.getRoom()).sendMessage(person, "%SNAME %SISARE suddenly " +
          "changed into a frog!<br/>\r\n");
      getLogService().writeLog(person, " was changed into a frog by " + aUser.getName() + " for " + amount + ".");
    }
    if (command.toLowerCase().startsWith(ADMIN_JACKASS))
    {
      PersonService personService = getPersonService();
      String[] commands = command.split(" ");
      if (commands.length < 3)
      {
        throw new MudException("Expected admin jackass command in the shape of 'admin jackass personname amount'.");
      }
      int amount = 0;
      User person = personService.getActiveUser(commands[2]);
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
      CommunicationService.getCommunicationService(person.getRoom()).sendMessage(person, "%SNAME %SISARE suddenly " +
          "changed into a jackass!<br/>\r\n");
      getLogService().writeLog(person, " was changed into a jackass by " + aUser.getName() + " for " + amount + ".");
    }
    if (command.toLowerCase().startsWith(ADMIN_KICK))
    {
      PersonService personService = getPersonService();
      String[] commands = command.split(" ");
      if (commands.length < 3)
      {
        throw new MudException("Expected admin kick command in the shape of 'admin kick personname minutes'.");
      }
      Integer minutes = 0;
      User person = personService.getActiveUser(commands[2]);
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
      personService.sendWall(administrator.getName() + " causes " + person.getName() + " to cease to exist for " + minutes + " minutes.<br/>\n");
      getLogService().writeLog(person,
          " has been kicked out of the game by " + aUser.getName() + " for " + minutes + " minutes.");
    }
    if (command.equalsIgnoreCase(ADMIN_HELP))
    {
      communicationService.writeMessage("Possible commands are:<ul>"
          + "<li>admin help - this help text</li>"
          + "<li>admin wall &lt;message&gt; - pages all users with the message entered</li>"
          + "<li>admin chatline &lt;chatline&gt; - shows all users on a chatline</li>"
          + "<li>admin chatlines - shows all available chatlines, with info</li>"
          + "<li>admin kick &lt;playername&gt; [&lt;minutes&gt;] - kicks a user off the game. When the minutes are " +
          "provided,"
          + "said user will not be able to logon for that many minutes.</li>"
          + "<li>admin frog &lt;playername&gt; [&lt;amount&gt;] - turns the user into a frog, automatically turns " +
          "back after [amount] ribbits."
          + "<li>admin jackass &lt;playername&gt; [&lt;amount&gt;] - turns the user into a jackass, automatically " +
          "turns back after [amount] heehaws."
          + "<li>admin visible [on | off] - makes an administrator not show up in the wholist or in a room.</li>"
          + "<li>admin runevent &lt;eventid&gt; - runs an event, in order to see if it works.</li>"
          + "<li>admin reset commands - reset the cached <i>special</i> commands."
          + "Necessary if a command has been deleted, added or changed.</li>"
          + "</ul>\r\n");
    }
    return aUser.getRoom();
  }

  private void writeUsersOfChatline(Chatline chatline, PersonCommunicationService communicationService)
  {
    String table =
        "<table><tr><th>Names</th></tr>" +
            chatline.getUsers().stream()
                .map(Chatlineusers::getUser)
                .map(Person::getName)
                .sorted()
                .map(username ->
                    "<tr><td>" + username +
                        "</td></tr>")
                .collect(Collectors.joining()) +
            "</table><br/>\r\n";
    communicationService.writeMessage(table);
  }

}
