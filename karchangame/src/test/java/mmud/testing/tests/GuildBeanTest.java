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

import mmud.database.entities.characters.User;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Room;
import mmud.exceptions.ErrorDetails;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.GuildBean;
import mmud.rest.services.PrivateBean;
import mmud.rest.webentities.PrivateMail;
import mmud.rest.webentities.PrivatePerson;
import mmud.testing.TestingConstants;
import mmud.testing.TestingUtils;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 *
 * @author maartenl
 */
public class GuildBeanTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(GuildBeanTest.class.getName());
  @Mocked
  EntityManager entityManager;

  @Mocked(
          {
            "ok", "status"
          })
  javax.ws.rs.core.Response response;

  @Mocked
  MudWebException webApplicationException;

  @Mocked
  ErrorDetails errorDetails;

  @Mocked
  ResponseBuilder responseBuilder;

  @Mocked
  Query query;

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

  private void compare(PrivateMail actual, PrivateMail expected)
  {
    if (TestingUtils.compareBase(actual, expected))
    {
      return;
    }
    assertEquals(actual.body, expected.body, "body:");
    assertEquals(actual.deleted, expected.deleted, "deleted");
    assertEquals(actual.haveread, expected.haveread, "haveread");
    assertEquals(actual.id, expected.id, "id");
    assertEquals(actual.item_id, expected.item_id, "item_id");
    assertEquals(actual.name, expected.name, "name");
    assertEquals(actual.newmail, expected.newmail, "newmail");
    assertEquals(actual.subject, expected.subject, "subject");
    assertEquals(actual.toname, expected.toname, "toname");
    assertEquals(actual.whensent, expected.whensent, "whensent");
  }

  /**
   * Marvin isn't member of a guild.
   */
  @Test(expectedExceptions = MudWebException.class)
  public void getMembersButNotInAGuild() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
    LOGGER.fine("getMembersButNotInAGuild");
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
  }

  /**
   * Marvin is a member of a guild, so we are expecting a nice list of members.
   */
  @Test
  public void getMembers() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
    LOGGER.fine("getMembers");
    final Guild guild = TestingConstants.getGuild();
    marvin.setGuild(guild);
    SortedSet<User> members = new TreeSet<>(new Comparator<User>()
    {

      @Override
      public int compare(User arg0, User arg1)
      {
        return arg0.getName().compareTo(arg1.getName());
      }
    });
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
    final Guild guild = TestingConstants.getGuild();
    marvin.setGuild(guild);
    SortedSet<User> members = new TreeSet<>(new Comparator<User>()
    {

      @Override
      public int compare(User arg0, User arg1)
      {
        return arg0.getName().compareTo(arg1.getName());
      }
    });
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
    assertThat(result).hasSize(0);
  }

  private void responseOkExpectations()
  {
    new Expectations() // an "expectation block"
    {

      {
        Response.ok();
        result = responseBuilder;
        responseBuilder.build();
      }
    };
  }

  private void responseNoContentExpectations()
  {
    new Expectations() // an "expectation block"
    {

      {
        Response.noContent();
        result = responseBuilder;
        responseBuilder.build();
      }
    };
  }
}
