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
package mmud.testing.tests.commands.guild;

import java.io.File;
import java.io.PrintWriter;
import java.util.SortedSet;
import java.util.TreeSet;

import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.guild.RemoveCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Guildrank;
import mmud.database.entities.game.Room;
import mmud.services.CommunicationService;
import mmud.services.LogService;
import mmud.testing.tests.LogServiceStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 *
 * @author maartenl
 */
public class RemoveCommandTest extends MudTest
{

  private User karn;
  private User marvin;
  private Room room1;


  private LogService logService = new LogServiceStub();

  private CommandRunner commandRunner = new CommandRunner();
  private Guild deputy;
  private User hotblack;

  public RemoveCommandTest()
  {
  }

  /**
   * Hotblack wasn't in the guild.
   */
  @Test
  public void removeHotblackFromGuild()
  {
    RemoveCommand removeCommand = new RemoveCommand("guildremove (\\w)+");
    removeCommand.setCallback(commandRunner);
    assertThat(removeCommand.getRegExpr()).isEqualTo("guildremove (\\w)+");
    commandRunner.setServices(null, logService, null, null, null, null, null, null);
    DisplayInterface display = removeCommand.run("guildremove hotblack", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(karnLog).isEqualTo("Cannot find that person.<br />\r\n");
    String hotblackLog = CommunicationService.getCommunicationService(hotblack).getLog(0L).log;
    assertThat(hotblackLog).isEmpty();
    // the important bit
    assertThat(karn.getGuild()).isEqualTo(deputy);
    assertThat(hotblack.getGuild()).isNull();
    assertThat(hotblack.getGuildrank()).isNull();
  }

  /**
   * Karcas doesn't exist.
   */
  @Test
  public void removeKarcasFromGuild()
  {
    RemoveCommand removeCommand = new RemoveCommand("guildremove (\\w)+");
    removeCommand.setCallback(commandRunner);
    assertThat(removeCommand.getRegExpr()).isEqualTo("guildremove (\\w)+");
    commandRunner.setServices(null, logService, null, null, null, null, null, null);
    DisplayInterface display = removeCommand.run("guildremove karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(karnLog).isEqualTo("Cannot find that person.<br />\r\n");
    // the important bit
    assertThat(karn.getGuild()).isEqualTo(deputy);
  }

  /**
   * Cannot remove leader of the guild.
   */
  @Test
  public void removeKarnFromGuild()
  {
    RemoveCommand removeCommand = new RemoveCommand("guildremove (\\w)+");
    removeCommand.setCallback(commandRunner);
    assertThat(removeCommand.getRegExpr()).isEqualTo("guildremove (\\w)+");
    commandRunner.setServices(null, logService, null, null, null, null, null, null);
    DisplayInterface display = removeCommand.run("guildremove karn", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(karnLog).isEqualTo("A guildmaster cannot remove him/herself from the guild.<br />\r\n");
    // the important bit
    assertThat(karn.getGuild()).isEqualTo(deputy);
  }

  /**
   * Remove Marvin, who was a member of the guild, from the guild.
   */
  @Test
  public void removeMarvinFromGuild()
  {
    RemoveCommand removeCommand = new RemoveCommand("guildremove (\\w)+");
    removeCommand.setCallback(commandRunner);
    assertThat(removeCommand.getRegExpr()).isEqualTo("guildremove (\\w)+");
    commandRunner.setServices(null, logService, null, null, null, null, null, null);
    DisplayInterface display = removeCommand.run("guildremove marvin", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(karnLog).isEqualTo("You have removed Marvin from your guild.<br />\r\n<b>Marvin</b> has been removed from the guild.<br />\r\n");
    String marvinLog = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(marvinLog).isEqualTo("<b>Marvin</b> has been removed from the guild.<br />\r\n");
    // the important bit
    assertThat(karn.getGuild()).isEqualTo(deputy);
    assertThat(marvin.getGuild()).isNull();
    assertThat(marvin.getGuildrank()).isNull();
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
    room1 = new Room();
    room1.setId(1L);
    room1.setContents("You are in a small room.");

    karn = new User();
    karn.setName("Karn");
    karn.setRoom(room1);

    marvin = new User();
    marvin.setName("Marvin");
    marvin.setRoom(room1);

    hotblack = new User();
    hotblack.setName("Marvin");
    hotblack.setRoom(room1);

    deputy = new Guild();
    deputy.setName("deputy");
    deputy.setBoss(karn);
    final SortedSet<Guildrank> guildranks = new TreeSet<>((arg0, arg1) ->
    {
      if (arg0.getGuildrankPK() == null)
      {
        if (arg1.getGuildrankPK() == null)
        {
          return 0;
        }
        return 1;
      }
      int arg0level = arg0.getGuildrankPK().getGuildlevel();
      int arg1level = arg1.getGuildrankPK().getGuildlevel();
      return arg0level - arg1level;
    });

    final SortedSet<User> members = new TreeSet<>((arg0, arg1) -> arg0.getName().compareTo(arg1.getName()));
    members.add(karn);
    members.add(marvin);
    deputy.setMembers(members);
    final SortedSet<User> activeMembers = new TreeSet<>((arg0, arg1) -> arg0.getName().compareTo(arg1.getName()));
    activeMembers.add(karn);
    activeMembers.add(marvin);
    deputy.setActiveMembers(activeMembers);

    karn.setGuild(deputy);

    marvin.setGuild(deputy);

    File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
    PrintWriter writer = new PrintWriter(file);
    writer.close();
    file = new File(Constants.getMudfilepath() + File.separator + "Marvin.log");
    writer = new PrintWriter(file);
    writer.close();

    room1.addPerson(karn);
    room1.addPerson(marvin);
    room1.addPerson(hotblack);

  }

}
