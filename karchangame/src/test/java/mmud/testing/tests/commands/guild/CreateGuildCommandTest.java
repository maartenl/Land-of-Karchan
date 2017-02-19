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
import java.util.HashSet;
import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.guild.CreateGuildCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
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
public class CreateGuildCommandTest extends MudTest
{

  private User karn;
  private Room room1;

  @Mocked
  private LogBean logBean;

  @Mocked
  private CommandRunner commandRunner;

  public CreateGuildCommandTest()
  {
  }

  /**
   * Create a guild.
   */
  @Test
  public void createGuild()
  {
    CreateGuildCommand rankCommand = new CreateGuildCommand("createguild (\\w)+ .+");
    rankCommand.setCallback(commandRunner);
    assertThat(rankCommand.getRegExpr(), equalTo("createguild (\\w)+ .+"));
    new Expectations() // an "expectation block"
    {

      {
        commandRunner.getLogBean();
        result = logBean;
      }
    };
    DisplayInterface display = rankCommand.run("createguild deputies The Royal Club Of Deputies", karn);
    assertThat(display, not(nullValue()));
    assertThat(display.getBody(), equalTo("You are in a small room."));
    String karnLog = karn.getLog(0);
    assertThat(karnLog, equalTo("Guild deputies created.<br />\n"));
    // the important bit
    assertThat(karn.getGuild(), not(nullValue()));
    Guild guild = karn.getGuild();
    assertThat(guild.getTitle(), equalTo("The Royal Club Of Deputies"));
    assertThat(guild.getName(), equalTo("deputies"));
  }

  /**
   * Create a guild, while you are already a member of a guild.
   */
  @Test
  public void createGuildWhenAlreadyInAGuild()
  {
    karn.setGuild(new Guild());
    karn.getGuild().setName("oldguild");
    karn.getGuild().setTitle("oldtitle");
    CreateGuildCommand rankCommand = new CreateGuildCommand("createguild (\\w)+ .+");
    rankCommand.setCallback(commandRunner);
    assertThat(rankCommand.getRegExpr(), equalTo("createguild (\\w)+ .+"));
    DisplayInterface display = rankCommand.run("createguild deputies The Royal Club Of Deputies", karn);
    assertThat(display, not(nullValue()));
    assertThat(display.getBody(), equalTo("You are in a small room."));
    String karnLog = karn.getLog(0);

    assertThat(karnLog, equalTo("You are a member of a guild, and can therefore not start a new guild.<br />\n"));
    // the important bit
    assertThat(karn.getGuild(), not(nullValue()));
    Guild guild = karn.getGuild();
    assertThat(guild.getTitle(), equalTo("oldtitle"));
    assertThat(guild.getName(), equalTo("oldguild"));
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
