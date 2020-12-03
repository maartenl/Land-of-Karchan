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
package mmud.testing.tests;

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.GuildBean;
import mmud.rest.services.PrivateBean;
import mmud.rest.webentities.PrivatePerson;
import mmud.testing.TestingConstants;
import org.testng.annotations.*;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * @author maartenl
 */
public class GuildBeanTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(GuildBeanTest.class.getName());

  private User hotblack;
  private User marvin;

  private final PrivateBean privateBean = new PrivateBean()
  {
    @Override
    public User authenticate(String name)
    {
      if (name.equals("Hotblack"))
      {
        return hotblack;
      }
      if (name.equals("Marvin"))
      {
        return marvin;
      }
      return null;
    }
  };

  public GuildBeanTest()
  {
  }

  @BeforeClass
  public void setUpClass()
  {
  }

  @AfterClass
  public void tearDownClass()
  {
  }

  @BeforeMethod
  public void setUp() throws MudException
  {
    Area aArea = TestingConstants.getArea();
    Room aRoom = TestingConstants.getRoom(aArea);
    hotblack = TestingConstants.getHotblack(aRoom);
    marvin = TestingConstants.getMarvin(aRoom);
  }

  @AfterMethod
  public void tearDown()
  {
  }

  /**
   * Marvin isn't member of a guild.
   */
  @Test
  public void getMembersButNotInAGuild() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
    LOGGER.fine("getMembersButNotInAGuild");
    EntityManager entityManager = mock(EntityManager.class);
    GuildBean guildBean = new GuildBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    Field field = GuildBean.class.getDeclaredField("privateBean");
    field.setAccessible(true);
    field.set(guildBean, privateBean);
    // Unit under test is exercised.
    assertThatThrownBy(() -> guildBean.getMembers("Marvin"))
      .isInstanceOf(MudWebException.class)
      .hasMessage("User is not a member of a guild (Marvin)");
  }

  /**
   * Marvin is a member of a guild, so we are expecting a nice list of members.
   */
  @Test
  public void getMembers() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
    LOGGER.fine("getMembers");
    EntityManager entityManager = mock(EntityManager.class);
    final Guild guild = TestingConstants.getGuild();
    marvin.setGuild(guild);
    SortedSet<User> members = new TreeSet<>(Comparator.comparing(Person::getName));
    members.add(marvin);
    members.add(hotblack);
    guild.setMembers(members);
    GuildBean guildBean = new GuildBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    Field field = GuildBean.class.getDeclaredField("privateBean");
    field.setAccessible(true);
    field.set(guildBean, privateBean);
    // Unit under test is exercised.
    List<PrivatePerson> result = guildBean.getMembers("Marvin");
    assertThat(result).hasSize(2);
    assertThat(result.get(0).name).isEqualTo("Hotblack");
    assertThat(result.get(1).name).isEqualTo("Marvin");
  }

  @Test
  public void getNoMembers() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
    LOGGER.fine("getNoMembers");
    EntityManager entityManager = mock(EntityManager.class);
    final Guild guild = TestingConstants.getGuild();
    marvin.setGuild(guild);
    SortedSet<User> members = new TreeSet<>(Comparator.comparing(Person::getName));
    guild.setMembers(members);
    GuildBean guildBean = new GuildBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    Field field = GuildBean.class.getDeclaredField("privateBean");
    field.setAccessible(true);
    field.set(guildBean, privateBean);
    // Unit under test is exercised.
    List<PrivatePerson> result = guildBean.getMembers("Marvin");
    // Verification code (JUnit/TestNG asserts), if any.
    assertThat(result).isEmpty();
  }
}
