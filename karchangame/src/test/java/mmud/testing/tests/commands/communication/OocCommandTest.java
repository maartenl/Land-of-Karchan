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
package mmud.testing.tests.commands.communication;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.communication.OocCommand;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.services.CommunicationService;
import mmud.services.PersonService;
import mmud.testing.TestingConstants;
import mmud.testing.tests.LogServiceStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author maartenl
 */
public class OocCommandTest extends MudTest
{

  private Administrator karn;
  private User marvin;

  private LogServiceStub logService;

  private final CommandRunner commandRunner = new CommandRunner();

  private PersonService personService;

  public OocCommandTest()
  {
  }

  /**
   * Runs the ooc command, in order to turn it on.
   */
  @Test
  public void runTurnOn()
  {
    assertThat(marvin.getOoc()).isFalse();
    OocCommand heehawCommand = new OocCommand("ooc .+");
    heehawCommand.setCallback(commandRunner);
    assertThat(heehawCommand.getRegExpr()).isEqualTo("ooc .+");
    DisplayInterface display = heehawCommand.run("ooc on", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L);
    assertThat(log).isEqualTo("Your OOC channel is now turned on.<br />\r\n");
    assertThat(marvin.getOoc()).isTrue();
  }

  /**
   * Runs the ooc command, in order to turn it off.
   */
  @Test
  public void runTurnOff()
  {
    marvin.setOoc(true);
    assertThat(marvin.getOoc()).isTrue();
    OocCommand heehawCommand = new OocCommand("ooc .+");
    heehawCommand.setCallback(commandRunner);
    assertThat(heehawCommand.getRegExpr()).isEqualTo("ooc .+");
    DisplayInterface display = heehawCommand.run("ooc off", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L);
    assertThat(log).isEqualTo("Your OOC channel is now turned off.<br />\r\n");
    assertThat(marvin.getOoc()).isFalse();
  }

  /**
   * Runs the ooc command, to tell everyone, but it's turned off.
   */
  @Test
  public void runMessageTurnedOff()
  {
    assertThat(marvin.getOoc()).isFalse();
    OocCommand heehawCommand = new OocCommand("ooc .+");
    heehawCommand.setCallback(commandRunner);
    assertThat(heehawCommand.getRegExpr()).isEqualTo("ooc .+");
    DisplayInterface display = heehawCommand.run("ooc Hey! This doesn't work!", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L);
    assertThat(log).isEqualTo("Sorry, you have your OOC channel turned off.<br />\r\n");
    assertThat(marvin.getOoc()).isFalse();
  }

  /**
   * Runs the ooc command, to tell everyone.
   */
  @Test
  public void runMessageTurnedOn()
  {
    marvin.setOoc(true);
    karn.setOoc(true);
    assertThat(marvin.getOoc()).isTrue();
    OocCommand heehawCommand = new OocCommand("ooc .+");
    heehawCommand.setCallback(commandRunner);
    assertThat(heehawCommand.getRegExpr()).isEqualTo("ooc .+");
    commandRunner.setServices(personService, null, null, null, null, null, null);
    DisplayInterface display = heehawCommand.run("ooc Hey! This works!", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L);
    assertThat(log).isEqualTo("<span class=\"chat-cyanblue\">[OOC: <b>Marvin</b>] Hey! This works!</span><br />\r\n");
    log = CommunicationService.getCommunicationService(karn).getLog(0L);
    assertThat(log).isEqualTo("<span class=\"chat-cyanblue\">[OOC: <b>Marvin</b>] Hey! This works!</span><br />\r\n");
    assertThat(marvin.getOoc()).isTrue();
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
    logService = new LogServiceStub();
    personService = new PersonService(logService)
    {
      @Override
      public User getActiveUser(String name)
      {
        if (name.equalsIgnoreCase("Marvin"))
        {
          return marvin;
        }
        if (name.equalsIgnoreCase("Karn"))
        {
          return karn;
        }
        return null;
      }

      @Override
      public List<User> getActivePlayers()
      {
        return Arrays.asList(karn, marvin);
      }

    };

    karn = TestingConstants.getKarn();
    final Room room = TestingConstants.getRoom(TestingConstants.getArea());
    karn.setRoom(room);
    marvin = TestingConstants.getMarvin(room);
    CommunicationService.getCommunicationService(karn).clearLog();
    CommunicationService.getCommunicationService(marvin).clearLog();
    room.addPerson(karn);
    room.addPerson(marvin);
    File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
    PrintWriter writer = new PrintWriter(file);
    writer.close();
    file = new File(Constants.getMudfilepath() + File.separator + "Marvin.log");
    writer = new PrintWriter(file);
    writer.close();
  }

}
