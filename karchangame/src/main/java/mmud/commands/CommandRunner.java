/*
 * Copyright (C) 2014 maartenl
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Inject;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.MudException;
import mmud.rest.services.EventsRestService;
import mmud.rest.services.GuildRestService;
import mmud.rest.services.admin.AdminRestService;
import mmud.rest.services.admin.RoomsRestService;
import mmud.scripting.Items;
import mmud.scripting.Persons;
import mmud.scripting.Rooms;
import mmud.scripting.RunScript;
import mmud.scripting.World;
import mmud.services.AttributeService;
import mmud.services.CommunicationService;
import mmud.services.HelpBean;
import mmud.services.ItemBean;
import mmud.services.LogBean;
import mmud.services.PersonBean;

/**
 * Will run the command provided.
 *
 * @see CommandFactory
 * @author maartenl
 */
public class CommandRunner
{

  private static final Logger LOGGER = Logger.getLogger(CommandRunner.class.getName());

  @Inject
  private PersonBean personBean;

  @Inject
  private AttributeService attributeService;

  @Inject
  private RoomsRestService roomBean;

  @Inject
  private HelpBean helpBean;

  @Inject
  private LogBean logBean;

  @Inject
  private GuildRestService guildRestService;

  @Inject
  private ItemBean itemBean;

  @Inject
  private EventsRestService eventsRestService;

  @Inject
  private AdminRestService adminRestService;

  /**
   * Runs a specific command. If this person appears to be asleep, the only
   * possible commands are "quit" and "awaken". If the command found returns a
   * false, i.e. nothing is executed, the method will call the standard
   * BogusCommand.
   *
   * @param aUser the person executing the command
   * @param aCommand the command to be run. Must not be null.
   * @param userCommands the commands as defined by the user, instead of the
   * standard commands. Must never be null.
   * @return DisplayInterface object containing the result of the command
   * executed.
   */
  public DisplayInterface runCommand(User aUser, String aCommand, List<UserCommand> userCommands)
  {
    LOGGER.log(Level.FINER, " entering {0}.runCommand {1}:{2}", new Object[]
    {
      this.getClass().getName(), aUser.getName(), aCommand
    });
    try
    {
      if (aUser.getSleep())
      {
        return determineSleep(aCommand, aUser);
      }
      if (aUser.getFrogging() > 0)
      {
        return determineFrogging(aCommand, aUser);
      }
      if (aUser.getJackassing() > 0)
      {
        return determineJackassing(aCommand, aUser);
      }
      List<NormalCommand> myCol = new ArrayList<>();
      Persons persons = new Persons(personBean);
      Rooms rooms = new Rooms(id -> roomBean.find(id));
      Items items = new Items(itemdefnr -> itemBean.createItem(itemdefnr));
      World world = new World(name -> attributeService.getAttribute(name));
      RunScript runScript = new RunScript(persons, rooms, items, world);
      for (UserCommand myCom : userCommands)
      {
        ScriptCommand scriptCommand = new ScriptCommand(myCom, runScript);
        myCol.add(scriptCommand);
        LOGGER.log(Level.FINE, "added script command {0}", myCom.getMethodName().getName());
      }
      myCol.addAll(CommandFactory.getCommand(aCommand));
      for (NormalCommand command : myCol)
      {
        command.setCallback(this);
        DisplayInterface result = command.start(aCommand, aUser);
        if (result != null)
        {
          return result;
        }
      }
      final NormalCommand bogusCommand = CommandFactory.getBogusCommand();
      bogusCommand.setCallback(this);
      return bogusCommand.start(aCommand, aUser);
    } catch (MudException e)
    {
      try
      {
        CommunicationService.getCommunicationService(aUser).writeMessage(e.getMessage());
      } catch (MudException ex)
      {
        LOGGER.throwing("play: throws ", ex.getMessage(), ex);
      }
      return null;
    }
  }

  private DisplayInterface determineSleep(String aCommand, User aUser)
  {
    NormalCommand command;
    if (aCommand.trim().equalsIgnoreCase("awaken"))
    {
      command = CommandFactory.AWAKEN.createCommand();
    } else
    {
      command = CommandFactory.ALREADY_ASLEEP.createCommand();
    }
    command.setCallback(this);
    return command.start(aCommand, aUser);
  }

  private DisplayInterface determineFrogging(String aCommand, User aUser)
  {
    NormalCommand command = CommandFactory.getBogusCommand();
    if (aCommand.trim().equalsIgnoreCase("ribbit"))
    {
      command = CommandFactory.RIBBIT.createCommand();
    }
    command.setCallback(this);
    return command.start(aCommand, aUser);
  }

  private DisplayInterface determineJackassing(String aCommand, User aUser)
  {
    NormalCommand command = CommandFactory.getBogusCommand();
    if (aCommand.trim().equalsIgnoreCase("heehaw"))
    {
      command = CommandFactory.HEEHAW.createCommand();
    }
    command.setCallback(this);
    return command.start(aCommand, aUser);
  }

  public PersonBean getPersonBean()
  {
    return personBean;
  }

  public HelpBean getHelpBean()
  {
    return helpBean;
  }

  public LogBean getLogBean()
  {
    return logBean;
  }

  public GuildRestService getGuildBean()
  {
    return guildRestService;
  }

  public ItemBean getItemBean()
  {
    return itemBean;
  }

  EventsRestService getEventsBean()
  {
    return eventsRestService;
  }

  AdminRestService getAdminBean()
  {
    return adminRestService;
  }

  @VisibleForTesting
  public void setBeans(PersonBean personBean, LogBean logBean, GuildRestService guildRestService, ItemBean itemBean, EventsRestService eventsRestService, AdminRestService adminRestService, HelpBean helpBean)
  {
    this.personBean = personBean;
    this.logBean = logBean;
    this.guildRestService = guildRestService;
    this.itemBean = itemBean;
    this.eventsRestService = eventsRestService;
    this.adminRestService = adminRestService;
    this.helpBean = helpBean;
  }

}
