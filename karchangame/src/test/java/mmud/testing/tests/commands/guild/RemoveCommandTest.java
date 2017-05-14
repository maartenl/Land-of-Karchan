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
import java.util.Comparator;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.guild.RemoveCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Guildrank;
import mmud.database.entities.game.Room;
import mmud.rest.services.LogBean;
import mmud.testing.tests.MudTest;
import mockit.Expectations;
import mockit.Mocked;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class RemoveCommandTest extends MudTest
{

  private User karn;
  private User marvin;
  private Room room1;

  @Mocked
  private LogBean logBean;

  @Mocked
  private CommandRunner commandRunner;
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
    assertThat(removeCommand.getRegExpr(), equalTo("guildremove (\\w)+"));
    new Expectations() // an "expectation block"
    {

      {
        commandRunner.getLogBean();
        result = logBean;
      }
    };
    DisplayInterface display = removeCommand.run("guildremove hotblack", karn);
    assertThat(display, not(nullValue()));
    assertThat(display.getBody(), equalTo("You are in a small room."));
    String karnLog = karn.getLog(0);
    assertThat(karnLog, equalTo("Cannot find that person.<br />\n"));
    String hotblackLog = hotblack.getLog(0);
    assertThat(hotblackLog, equalTo(""));
    // the important bit
    assertThat(karn.getGuild(), equalTo(deputy));
    assertThat(hotblack.getGuild(), nullValue());
    assertThat(hotblack.getGuildrank(), nullValue());
  }

  /**
   * Karcas doesn't exist.
   */
  @Test
  public void removeKarcasFromGuild()
  {
    RemoveCommand removeCommand = new RemoveCommand("guildremove (\\w)+");
    removeCommand.setCallback(commandRunner);
    assertThat(removeCommand.getRegExpr(), equalTo("guildremove (\\w)+"));
    new Expectations() // an "expectation block"
    {

      {
        commandRunner.getLogBean();
        result = logBean;
      }
    };
    DisplayInterface display = removeCommand.run("guildremove karcas", karn);
    assertThat(display, not(nullValue()));
    assertThat(display.getBody(), equalTo("You are in a small room."));
    String karnLog = karn.getLog(0);
    assertThat(karnLog, equalTo("Cannot find that person.<br />\n"));
    // the important bit
    assertThat(karn.getGuild(), equalTo(deputy));
  }

  /**
   * Cannot remove leader of the guild.
   */
  @Test
  public void removeKarnFromGuild()
  {
    RemoveCommand removeCommand = new RemoveCommand("guildremove (\\w)+");
    removeCommand.setCallback(commandRunner);
    assertThat(removeCommand.getRegExpr(), equalTo("guildremove (\\w)+"));
    new Expectations() // an "expectation block"
    {

      {
        commandRunner.getLogBean();
        result = logBean;
      }
    };
    DisplayInterface display = removeCommand.run("guildremove karn", karn);
    assertThat(display, not(nullValue()));
    assertThat(display.getBody(), equalTo("You are in a small room."));
    String karnLog = karn.getLog(0);
    assertThat(karnLog, equalTo("A guildmaster cannot remove him/herself from the guild.<br />\n"));
    // the important bit
    assertThat(karn.getGuild(), equalTo(deputy));
  }

  /**
   * Remove Marvin, who was a member of the guild, from the guild.
   */
  @Test
  public void removeMarvinFromGuild()
  {
    RemoveCommand removeCommand = new RemoveCommand("guildremove (\\w)+");
    removeCommand.setCallback(commandRunner);
    assertThat(removeCommand.getRegExpr(), equalTo("guildremove (\\w)+"));
    new Expectations() // an "expectation block"
    {

      {
        commandRunner.getLogBean();
        result = logBean;
      }
    };
    DisplayInterface display = removeCommand.run("guildremove marvin", karn);
    assertThat(display, not(nullValue()));
    assertThat(display.getBody(), equalTo("You are in a small room."));
    String karnLog = karn.getLog(0);
    assertThat(karnLog, equalTo("You have removed Marvin from your guild.<br />\n<b>Marvin</b>\n has been removed from the guild.<br />\n"));
    String marvinLog = marvin.getLog(0);
    assertThat(marvinLog, equalTo("<b>Marvin</b>\n has been removed from the guild.<br />\n"));
    // the important bit
    assertThat(karn.getGuild(), equalTo(deputy));
    assertThat(marvin.getGuild(), nullValue());
    assertThat(marvin.getGuildrank(), nullValue());
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
    room1 = new Room();
    room1.setId(1);
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
    final SortedSet<Guildrank> guildranks = new TreeSet<>(new Comparator<Guildrank>()
    {

      @Override
      public int compare(Guildrank arg0, Guildrank arg1)
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
      }
    });

    final SortedSet<User> members = new TreeSet<>(new Comparator<User>()
    {

      @Override
      public int compare(User arg0, User arg1)
      {
        return arg0.getName().compareTo(arg1.getName());
      }

    });
    members.add(karn);
    members.add(marvin);
    deputy.setMembers(members);
    final SortedSet<User> activeMembers = new TreeSet<>(new Comparator<User>()
    {

      @Override
      public int compare(User arg0, User arg1)
      {
        return arg0.getName().compareTo(arg1.getName());
      }

    });
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

    HashSet<Person> persons = new HashSet<>();
    persons.add(karn);
    persons.add(marvin);
    persons.add(hotblack);
    setField(Room.class, "persons", room1, persons);

  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }
}
