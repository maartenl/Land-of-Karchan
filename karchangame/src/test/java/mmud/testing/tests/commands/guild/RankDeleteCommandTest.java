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
import mmud.commands.guild.RankDeleteCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Guildrank;
import mmud.database.entities.game.GuildrankPK;
import mmud.database.entities.game.Room;
import mmud.services.CommunicationService;
import mmud.services.LogService;
import mmud.testing.tests.LogServiceStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author maartenl
 */
public class RankDeleteCommandTest extends MudTest
{

  private User karn;
  private User marvin;
  private Room room1;

  private LogService logService = new LogServiceStub();

  private CommandRunner commandRunner = new CommandRunner();
  private Guild deputy;
  private Guildrank boss;
  private Guildrank minion;
  private User hotblack;

  public RankDeleteCommandTest()
  {
  }

  /**
   * Remove a rank from the guild.
   */
  @Test
  public void removeRankFromGuild()
  {
    RankDeleteCommand rankCommand = new RankDeleteCommand("guilddelrank (\\d){1,3} (\\w)+");
    rankCommand.setCallback(commandRunner);
    assertThat(rankCommand.getRegExpr()).isEqualTo("guilddelrank (\\d){1,3} (\\w)+");
    commandRunner.setServices(null, logService, null, null, null, null, null, null);
    DisplayInterface display = rankCommand.run("guilddelrank 100", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(karnLog).isEqualTo("Rank removed.<br />\r\n");
    // the important bit
    assertThat(karn.getGuild()).isEqualTo(deputy);
    assertThat(karn.getGuild().getGuildrankCollection()).hasSize(1);
    Guildrank rank = karn.getGuild().getRank(100);
    assertThat(rank).isNull();
  }

  /**
   * Remove a non-existing rank from the guild.
   */
  @Test
  public void removeNonExistingRankFromGuild()
  {
    RankDeleteCommand rankCommand = new RankDeleteCommand("guilddelrank (\\d){1,3} (\\w)+");
    rankCommand.setCallback(commandRunner);
    assertThat(rankCommand.getRegExpr()).isEqualTo("guilddelrank (\\d){1,3} (\\w)+");
    commandRunner.setServices(null, logService, null, null, null, null, null, null);
    DisplayInterface display = rankCommand.run("guilddelrank 50", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String karnLog = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(karnLog).isEqualTo("Rank not found.<br />\r\n");
    // the important bit
    assertThat(karn.getGuild()).isEqualTo(deputy);
    assertThat(karn.getGuild().getGuildrankCollection()).hasSize(2);
    Guildrank rank = karn.getGuild().getRank(50);
    assertThat(rank).isNull();
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

    boss = new Guildrank();
    boss.setGuild(deputy);
    boss.setTitle("Boss");
    final GuildrankPK bossRankPK = new GuildrankPK();
    bossRankPK.setGuildlevel(100);
    bossRankPK.setGuildname("deputy");
    boss.setGuildrankPK(bossRankPK);
    guildranks.add(boss);

    minion = new Guildrank();
    minion.setGuild(deputy);
    minion.setTitle("Minion");
    final GuildrankPK minionRankPK = new GuildrankPK();
    minionRankPK.setGuildlevel(0);
    minionRankPK.setGuildname("deputy");
    minion.setGuildrankPK(minionRankPK);
    guildranks.add(minion);
    deputy.setGuildrankCollection(guildranks);

    final SortedSet<User> members = new TreeSet<>((arg0, arg1) -> arg0.getName().compareTo(arg1.getName()));
    members.add(karn);
    members.add(marvin);
    deputy.setMembers(members);

    karn.setGuild(deputy);
    karn.setGuildrank(boss);

    marvin.setGuild(deputy);
    marvin.setGuildrank(minion);

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

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }
}
