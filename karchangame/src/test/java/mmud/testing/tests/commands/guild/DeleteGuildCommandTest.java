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

import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.guild.DeleteGuildCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Guildrank;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudWebException;
import mmud.rest.services.GameBean;
import mmud.rest.services.GuildBean;
import mmud.rest.services.LogBean;
import mmud.testing.tests.LogBeanStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import mmud.services.CommunicationService;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

/**
 *
 * @author maartenl
 */
public class DeleteGuildCommandTest extends MudTest
{

  private User karn;
  private Room room1;

  private GuildBean guildBean = new GuildBean(){
    @Override
    protected User authenticateGuildMaster(String name)
    {
      if (!karn.getName().equals(name))
      {
        throw new MudWebException(name, "You are not logged in as " + name, Response.Status.UNAUTHORIZED);
      }
      User person = karn;
      if (person.getGuild() == null)
      {
        throw new MudWebException(name, name + " is not a member of a guild.", "Person (" + name + ") is not a member of a guild", Response.Status.NOT_FOUND);
      }
      if (!person.getGuild().getBoss().getName().equals(person.getName()))
      {
        throw new MudWebException(name, name + " is not the guild master of " + person.getGuild().getTitle() + ".", "Person (" + name + ") is not the guild master of " + person.getGuild().getName(), Response.Status.UNAUTHORIZED);
      }
      return person;
    }

  };

  private LogBean logBean = new LogBeanStub();

  private CommandRunner commandRunner = new CommandRunner();

  public DeleteGuildCommandTest()
  {
  }

  /**
   * Delete a guild, while you are not a member of a guild.
   */
  @Test
  public void deleteGuildWhenNotInGuild()
  {
    DeleteGuildCommand deleteguildCommand = new DeleteGuildCommand("deleteguild");
    deleteguildCommand.setCallback(commandRunner);
    assertThat(deleteguildCommand.getRegExpr()).isEqualTo("deleteguild");
    DisplayInterface display = deleteguildCommand.run("deleteguild", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0);
    assertThat(karnLog).isEqualTo("You are not a member of a guild.<br />\n");
    // the important bit
    assertThat(karn.getGuild()).isNull();
  }

  /**
   * Delete a guild.
   */
  @Test
  public void deleteGuild()
  {
    EntityManager entityManager = mock(EntityManager.class);
    setField(GuildBean.class, "em", guildBean, entityManager);

    karn.setGuild(new Guild());
    karn.getGuild().setName("oldguild");
    karn.getGuild().setTitle("oldtitle");
    karn.getGuild().setBoss(karn);
    SortedSet<User> users = new TreeSet<>(Comparator.comparing(User::getName));
    users.add(karn);
    karn.getGuild().setMembers(users);
    karn.getGuild().setActiveMembers(users);
    SortedSet<Guildrank> ranks = new TreeSet<>();
    karn.getGuild().setGuildrankCollection(ranks);

    DeleteGuildCommand deleteguildCommand = new DeleteGuildCommand("deleteguild");
    deleteguildCommand.setCallback(commandRunner);
    assertThat(deleteguildCommand.getRegExpr()).isEqualTo("deleteguild");
    commandRunner.setBeans(null, logBean, guildBean, null, null, null, null);
    DisplayInterface display = deleteguildCommand.run("deleteguild", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0);

    assertThat(karnLog).isEqualTo("Guild oldguild deleted.<br />\r\n");
    // the important bit
    assertThat(karn.getGuild()).isNull();
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

    File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
    PrintWriter writer = new PrintWriter(file);
    writer.close();

    HashSet<Person> persons = new HashSet<>();
    persons.add(karn);
    setField(Room.class, "persons", room1, persons);

  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }
}
