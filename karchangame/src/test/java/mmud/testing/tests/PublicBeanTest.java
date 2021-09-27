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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.BoardMessage;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Room;
import mmud.database.entities.web.CharacterInfo;
import mmud.database.entities.web.Family;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.BoardBean;
import mmud.rest.services.PersonBean;
import mmud.rest.services.PublicBean;
import mmud.rest.webentities.Fortune;
import mmud.rest.webentities.News;
import mmud.rest.webentities.PublicGuild;
import mmud.rest.webentities.PublicPerson;
import mmud.services.IdleUsersService;
import mmud.testing.TestingConstants;
import mmud.testing.TestingUtils;
import org.mockito.ArgumentMatchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author maartenl
 */
public class PublicBeanTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(PublicBeanTest.class.getName());

  private User hotblack;
  private User marvin;

  public PublicBeanTest()
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
    Area aArea = TestingConstants.getSpecialArea();
    Room aRoom = TestingConstants.getRoom(aArea);

    hotblack = TestingConstants.getHotblack(aRoom);
    marvin = TestingConstants.getMarvin(aRoom);
    marvin.setSleep(true);
  }

  @AfterMethod
  public void tearDown()
  {
  }

  private void compare(Fortune actual, Fortune expected)
  {
    if (TestingUtils.compareBase(actual, expected))
    {
      return;
    }
    assertEquals(actual.gold, expected.gold, "gold");
    assertEquals(actual.silver, expected.silver, "silver");
    assertEquals(actual.copper, expected.copper, "copper");
    assertEquals(actual.name, expected.name, "name");
  }

  private void compare(PublicPerson actual, PublicPerson expected)
  {
    if (TestingUtils.compareBase(actual, expected))
    {
      return;
    }
    assertEquals(actual.name, expected.name, "name");
    assertEquals(actual.title, expected.title, "title");
    assertEquals(actual.sleep, expected.sleep, "sleep");
    assertEquals(actual.area, expected.area, "area");
    assertEquals(actual.min, expected.min, "min");
    assertEquals(actual.sec, expected.sec, "sec");

    assertEquals(actual.sex, expected.sex);
    assertEquals(actual.imageurl, expected.imageurl);
    assertEquals(actual.homepageurl, expected.homepageurl);
    assertEquals(actual.dateofbirth, expected.dateofbirth);
    assertEquals(actual.cityofbirth, expected.cityofbirth);
    assertEquals(actual.storyline, expected.storyline);
    assertEquals(actual.url, expected.url, "sec");
    assertEquals(actual.description, expected.description, "#" + actual.description + "##" + expected.description + "#");
  }

  private void compare(PublicGuild actual, PublicGuild expected)
  {
    if (TestingUtils.compareBase(actual, expected))
    {
      return;
    }
    assertEquals(actual.title, expected.title, "title");
    assertEquals(actual.guildurl, expected.guildurl, "guildurl");
    assertEquals(actual.guilddescription, expected.guilddescription, "guilddescription");
    assertEquals(actual.creation, expected.creation, "creation");
    assertEquals(actual.bossname, expected.bossname, "bossname");
  }

  private void compare(News actual, News expected)
  {
    if (TestingUtils.compareBase(actual, expected))
    {
      return;
    }
    assertEquals(actual.name, expected.name, "name");
    assertEquals(actual.message, expected.message, "message");
    if (TestingUtils.compareBase(actual.posttime, expected.posttime))
    {
      return;
    }
    assertEquals(actual.posttime, expected.posttime, "posttime");
  }

  @Test
  public void fortunesEmptyTest()
  {
    LOGGER.fine("fortunesEmptyTest");
    Query query = mock(Query.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("User.fortunes")).thenReturn(query);
    when(query.getResultList()).thenReturn(Collections.emptyList());

    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    // Unit under test is exercised.
    List<Fortune> result = publicBean.fortunes();
    // Verification code (JUnit/TestNG asserts), if any.
    assertEquals(result.size(), 0);
  }

  @Test
  public void fortunesTest()
  {
    LOGGER.fine("fortunesTest");
    final Object[] one =
      {
        "Hotblack", 34567
      };
    final Object[] two =
      {
        "Marvin",
        345674
      };
    final List<Object[]> list = new ArrayList<>();
    list.add(one);
    list.add(two);

    Query query = mock(Query.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("User.fortunes")).thenReturn(query);
    when(query.getResultList()).thenReturn(list);
    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    // Unit under test is exercised.
    List<Fortune> result = publicBean.fortunes();
    // Verification code (JUnit/TestNG asserts), if any.
    verify(query, times(1)).setMaxResults(100);
    assertEquals(result.size(), 2);
    Fortune expected = new Fortune();
    expected.name = "Hotblack";
    expected.gold = 345;
    expected.silver = 6;
    expected.copper = 7;
    compare(result.get(0), expected);
    expected = new Fortune();
    expected.name = "Marvin";
    expected.gold = 3456;
    expected.silver = 7;
    expected.copper = 4;
    compare(result.get(1), expected);
  }

  @Test
  public void whoEmptyTest()
  {
    LOGGER.fine("whoEmptyTest");
    Query query = mock(Query.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("User.who")).thenReturn(query);
    when(query.getResultList()).thenReturn(Collections.emptyList());

    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    publicBean.setPersonBean(new PersonBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    });
    // Unit under test is exercised.
    List<PublicPerson> result = publicBean.who();
    // Verification code (JUnit/TestNG asserts), if any.
    assertEquals(result.size(), 0);
  }

  @Test
  public void whoTest()
  {
    LOGGER.fine("whoTest");

    final List<Person> list = new ArrayList<>();
    list.add(hotblack);
    list.add(marvin);
    Query query = mock(Query.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("User.who")).thenReturn(query);
    when(query.getResultList()).thenReturn(list);

    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    publicBean.setPersonBean(new PersonBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    });
    IdleUsersService idleUsersService = new IdleUsersService();
    idleUsersService.resetUser("Hotblack");
    publicBean.setIdleUsersService(idleUsersService);
    // Unit under test is exercised.
    List<PublicPerson> result = publicBean.who();
    // Verification code (JUnit/TestNG asserts), if any.
    assertNotNull(result, "list expected");
    assertEquals(result.size(), 2);
    PublicPerson expected = new PublicPerson();
    expected.name = "Hotblack";
    expected.sleep = "";
    expected.area = "On board the Starship Heart of Gold";
    expected.min = 16L;
    expected.sec = 40L;
    expected.idleTime = "";
    expected.title = "Guitar keyboard player of the rock group Disaster Area";
    compare(result.get(0), expected);
    expected = new PublicPerson();
    expected.name = "Marvin";
    expected.sleep = "sleeping";
    expected.area = "On board the Starship Heart of Gold";
    expected.min = 33L;
    expected.sec = 20L;
    expected.title = "The Paranoid Android";
    expected.idleTime = null;
    compare(result.get(1), expected);
  }

  @Test
  public void newsEmptyTest()
  {
    LOGGER.fine("newsEmptyTest");
    Query query = mock(Query.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("BoardMessage.news")).thenReturn(query);
    when(query.getResultList()).thenReturn(Collections.emptyList());
    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    publicBean.setBoardBean(new BoardBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    });
    // Unit under test is exercised.
    List<News> result = publicBean.news();
    // Verification code (JUnit/TestNG asserts), if any.
    verify(query, times(1)).setParameter(ArgumentMatchers.eq("lastSunday"), any(LocalDateTime.class));
    assertNotNull(result, "list expected");
    assertEquals(result.size(), 0);
  }

  @Test
  public void newsTest()
  {
    LOGGER.fine("newsTest");
    LocalDateTime secondDate = LocalDateTime.now();
    LocalDateTime firstDate = secondDate.plusSeconds(-1_000L);
    final List<BoardMessage> list = new ArrayList<>();
    BoardMessage message = new BoardMessage();
    message.setId(1L);
    message.setPerson(hotblack);
    message.setMessage("First post!");
    message.setPosttime(firstDate);
    message.setRemoved(Boolean.FALSE);
    list.add(message);
    message = new BoardMessage();
    message.setId(2L);
    message.setPerson(marvin);
    message.setMessage("Damn!");
    message.setPosttime(secondDate);
    message.setRemoved(Boolean.FALSE);
    list.add(message);
    Query query = mock(Query.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("BoardMessage.news")).thenReturn(query);
    when(query.getResultList()).thenReturn(list);
    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    publicBean.setBoardBean(new BoardBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    });
    // Unit under test is exercised.
    List<News> result = publicBean.news();
    // Verification code (JUnit/TestNG asserts), if any.
    verify(query, times(1)).setParameter(ArgumentMatchers.eq("lastSunday"), any(LocalDateTime.class));
    assertNotNull(result, "list expected");
    assertEquals(result.size(), 2);
    News expected = new News();
    expected.name = "Hotblack";
    expected.message = "First post!";
    expected.posttime = firstDate;
    compare(result.get(0), expected);
    expected = new News();
    expected.name = "Marvin";
    expected.message = "Damn!";
    expected.posttime = secondDate;
    compare(result.get(1), expected);
  }

  @Test
  public void statusEmptyTest()
  {
    LOGGER.fine("statusEmptyTest");
    TypedQuery<User> query = mock(TypedQuery.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("User.status", User.class)).thenReturn(query);
    when(query.getResultList()).thenReturn(Collections.emptyList());
    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    // Unit under test is exercised.
    String result = publicBean.status();
    // Verification code (JUnit/TestNG asserts), if any.
    assertNotNull(result, "list expected");
    assertThat(result).isEqualTo("[]");
  }

  /**
   * Disabled,... cannot use a json builder properly in unit tests.
   */
  @Test(enabled = false)
  public void statusTest()
  {
    LOGGER.fine("statusTest");
    final List<User> list = new ArrayList<>();
    list.add(hotblack);
    list.add(marvin);
    TypedQuery<User> query = mock(TypedQuery.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("User.status", User.class)).thenReturn(query);
    when(query.getResultList()).thenReturn(list);
    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    // Unit under test is exercised.
    String result = publicBean.status();
    // Verification code (JUnit/TestNG asserts), if any.
    assertNotNull(result, "list expected");
    assertThat(result).isEqualTo("Woah");
  }

  @Test
  public void guildsEmptyTest()
  {
    LOGGER.fine("guildsEmptyTest");
    TypedQuery<Guild> query = mock(TypedQuery.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("Guild.findAll", Guild.class)).thenReturn(query);
    when(query.getResultList()).thenReturn(Collections.emptyList());
    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    // Unit under test is exercised.
    List<PublicGuild> result = publicBean.guilds();
    // Verification code (JUnit/TestNG asserts), if any.
    assertNotNull(result, "list expected");
    assertEquals(result.size(), 0);
  }

  @Test
  public void guildsTest() throws MudException
  {
    LOGGER.fine("guildsTest");
    LocalDateTime secondDate = LocalDateTime.now();
    LocalDateTime firstDate = secondDate.plusSeconds(-1_000L);
    final List<Guild> list = new ArrayList<>();
    Guild guild = new Guild();
    guild.setName("disasterarea");
    guild.setTitle("Disaster Area");
    guild.setBoss(hotblack);
    guild.setCreation(firstDate);
    guild.setHomepage("http://www.disasterarea.com");
    guild.setDescription("This is just a description");
    list.add(guild);
    guild = new Guild();
    guild.setName("sirius");
    guild.setTitle("Sirius Cybernetics Corporation");
    guild.setBoss(marvin);
    guild.setCreation(secondDate);
    guild.setHomepage("http://www.scc.com");
    guild.setDescription("This is just a description");
    list.add(guild);
    TypedQuery<Guild> query = mock(TypedQuery.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.createNamedQuery("Guild.findAll", Guild.class)).thenReturn(query);
    when(query.getResultList()).thenReturn(list);
    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    // Unit under test is exercised.
    List<PublicGuild> result = publicBean.guilds();
    // Verification code (JUnit/TestNG asserts), if any.
    assertNotNull(result, "list expected");
    assertEquals(result.size(), 2);
    PublicGuild expected = new PublicGuild();
    expected.title = "Disaster Area";
    expected.guildurl = "http://www.disasterarea.com";
    expected.guilddescription = "This is just a description";
    expected.creation = firstDate;
    expected.bossname = "Hotblack";
    compare(result.get(0), expected);
    expected = new PublicGuild();
    expected.title = "Sirius Cybernetics Corporation";
    expected.guildurl = "http://www.scc.com";
    expected.guilddescription = "This is just a description";
    expected.creation = secondDate;
    expected.bossname = "Marvin";
    compare(result.get(1), expected);
  }

  @Test
  public void charactersheetNotFoundTest()
  {
    LOGGER.fine("charactersheetNotFoundTest");

    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(null);
    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    // Unit under test is exercised.
    assertThatThrownBy(() -> publicBean.charactersheet("Marvin"))
      .isInstanceOf(MudWebException.class)
      .hasMessage("Charactersheet not found.");
    // Verification code (JUnit/TestNG asserts), if any.
  }

  @Test
  public void charactersheetTest() throws MudException
  {
    LOGGER.fine("charactersheetTest");
    LocalDateTime secondDate = LocalDateTime.now();
    LocalDateTime firstDate = secondDate.plusSeconds(-1_000L);
    final CharacterInfo charinfo = new CharacterInfo();
    charinfo.setImageurl("http://www.images.com/imageurl.jpg");
    charinfo.setHomepageurl("http://www.homepage.com/");
    charinfo.setDateofbirth("0000");
    charinfo.setCityofbirth("Sirius");
    charinfo.setStoryline("An android");
    charinfo.setName("Marvin");

    TypedQuery<Family> query = mock(TypedQuery.class);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(CharacterInfo.class, marvin.getName())).thenReturn(charinfo);
    when(entityManager.createNamedQuery("Family.findByName", Family.class)).thenReturn(query);
    when(query.getResultList()).thenReturn(Collections.emptyList());

    PublicBean publicBean = new PublicBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }
    };
    // Unit under test is exercised.
    PublicPerson person = publicBean.charactersheet("Marvin");
    // Verification code (JUnit/TestNG asserts), if any.
    verify(query, times(1)).setParameter("name", marvin.getName());
    assertNotNull(person, "person expected");
    PublicPerson expected = new PublicPerson();
    expected.name = "Marvin";
    expected.title = "The Paranoid Android";
    expected.sex = "male";
    expected.description = "young, tall, slender, swarthy, black-eyed, long-faced, black-haired, long-faced male android";
    expected.imageurl = "http://www.images.com/imageurl.jpg";
    expected.homepageurl = "http://www.homepage.com/";
    expected.dateofbirth = "0000";
    expected.cityofbirth = "Sirius";
    expected.storyline = "An android";
    compare(person, expected);
  }

}
