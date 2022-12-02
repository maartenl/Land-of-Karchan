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

import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.guild.CreateGuildCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Room;
import mmud.services.CommunicationService;
import mmud.services.LogService;
import mmud.testing.tests.LogServiceStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author maartenl
 */
public class CreateGuildCommandTest extends MudTest
{

  private User karn;
  private Room room1;

  private LogService logService = new LogServiceStub();

  private CommandRunner commandRunner = new CommandRunner();

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
    assertThat(rankCommand.getRegExpr()).isEqualTo("createguild (\\w)+ .+");
    commandRunner.setServices(null, logService, null, null, null, null, null);
    DisplayInterface display = rankCommand.run("createguild deputies The Royal Club Of Deputies", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(karnLog).isEqualTo("Guild deputies created.<br />\r\n");
    // the important bit
    assertThat(karn.getGuild()).isNotNull();
    Guild guild = karn.getGuild();
    assertThat(guild.getTitle()).isEqualTo("The Royal Club Of Deputies");
    assertThat(guild.getName()).isEqualTo("deputies");
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
    assertThat(rankCommand.getRegExpr()).isEqualTo("createguild (\\w)+ .+");
    DisplayInterface display = rankCommand.run("createguild deputies The Royal Club Of Deputies", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0L).log;

    assertThat(karnLog).isEqualTo("You are a member of a guild, and can therefore not start a new guild.<br />\n");
    // the important bit
    assertThat(karn.getGuild()).isNotNull();
    Guild guild = karn.getGuild();
    assertThat(guild.getTitle()).isEqualTo("oldtitle");
    assertThat(guild.getName()).isEqualTo("oldguild");
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

    room1.addPerson(karn);

  }
}
