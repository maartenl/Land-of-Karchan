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

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.GuildRestService;
import mmud.rest.webentities.PrivatePerson;
import mmud.services.PlayerAuthenticationService;
import mmud.testing.TestingConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * @author maartenl
 */
public class GuildRestServiceTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(GuildRestServiceTest.class.getName());

  private User hotblack;
  private User marvin;

  private PlayerAuthenticationService playerAuthenticationService = new PlayerAuthenticationService()
  {
    @Override
    public String getPlayerName(SecurityContext context) throws IllegalStateException
    {
      return "Marvin";
    }

    @Override
    public User authenticate(String name, SecurityContext context)
    {
      if (name.equals("Marvin"))
      {
        return marvin;
      }
      return null;
    }
  };

  public GuildRestServiceTest()
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
    GuildRestService guildRestService = new GuildRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    guildRestService.setPlayerAuthenticationService(playerAuthenticationService);
//    Field field = GuildRestService.class.getDeclaredField("privateRestService");
//    field.setAccessible(true);
//    field.set(guildRestService, privateRestService);
    // Unit under test is exercised.
    assertThatThrownBy(() -> guildRestService.getMembers("Marvin"))
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
    GuildRestService guildRestService = new GuildRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    guildRestService.setPlayerAuthenticationService(playerAuthenticationService);
//    Field field = GuildRestService.class.getDeclaredField("privateRestService");
//    field.setAccessible(true);
//    field.set(guildRestService, privateRestService);
    // Unit under test is exercised.
    List<PrivatePerson> result = guildRestService.getMembers("Marvin");
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
    GuildRestService guildRestService = new GuildRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    guildRestService.setPlayerAuthenticationService(playerAuthenticationService);
//    Field field = GuildRestService.class.getDeclaredField("privateRestService");
//    field.setAccessible(true);
//    field.set(guildRestService, privateRestService);
    // Unit under test is exercised.
    List<PrivatePerson> result = guildRestService.getMembers("Marvin");
    // Verification code (JUnit/TestNG asserts), if any.
    assertThat(result).isEmpty();
  }
}
