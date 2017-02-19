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
import mmud.commands.guild.RankChangeCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Guildrank;
import mmud.database.entities.game.GuildrankPK;
import mmud.database.entities.game.Room;
import mmud.rest.services.LogBean;
import mmud.testing.tests.MudTest;
import mockit.Expectations;
import mockit.Mocked;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
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
public class RankChangeCommandTest extends MudTest
{

  private User karn;
  private User marvin;
  private Room room1;

  @Mocked
  private LogBean logBean;

  @Mocked
  private CommandRunner commandRunner;
  private Guild deputy;
  private Guildrank boss;
  private Guildrank minion;
  private User hotblack;

  public RankChangeCommandTest()
  {
  }

  /**
   * Add a rank to the guild.
   */
  @Test
  public void addRankToGuild()
  {
    RankChangeCommand rankCommand = new RankChangeCommand("guildrank (\\d){1,3} (\\w)+");
    rankCommand.setCallback(commandRunner);
    assertThat(rankCommand.getRegExpr(), equalTo("guildrank (\\d){1,3} (\\w)+"));
    new Expectations() // an "expectation block"
    {

      {
        commandRunner.getLogBean();
        result = logBean;
      }
    };
    DisplayInterface display = rankCommand.run("guildrank 1 Senior Deputy", karn);
    assertThat(display, not(nullValue()));
    assertThat(display.getBody(), equalTo("You are in a small room."));
    String karnLog = karn.getLog(0);
    assertThat(karnLog, equalTo("New rank created.<br />\n"));
    // the important bit
    assertThat(karn.getGuild(), equalTo(deputy));
    assertThat(karn.getGuild().getGuildrankCollection(), Matchers.hasSize(3));
    Guildrank rank = karn.getGuild().getRank(1);
    assertThat(rank.getTitle(), equalTo("Senior Deputy"));
    assertThat(rank.getGuild(), equalTo(karn.getGuild()));
    assertThat(rank.getGuildrankPK().getGuildlevel(), equalTo(1));
    assertThat(rank.getGuildrankPK().getGuildname(), equalTo(karn.getGuild().getName()));
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
