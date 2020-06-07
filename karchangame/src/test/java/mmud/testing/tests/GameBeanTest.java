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

import mmud.commands.CommandRunner;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Macro;
import mmud.database.entities.game.Room;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.ErrorDetails;
import mmud.exceptions.MudException;
import mmud.rest.services.GameBean;
import mmud.rest.webentities.PrivateDisplay;
import mmud.testing.TestingConstants;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.testng.annotations.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author maartenl
 */
public class GameBeanTest extends MudTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(GameBeanTest.class.getName());

  @Mocked
  private EntityManager entityManager;

  private LogBeanStub logBean;

  @Mocked(
    {
      "ok", "status"
    })
  private javax.ws.rs.core.Response response;

  @Mocked
  private Query query;

  @Mocked
  private TypedQuery typedquery;

  @Mocked
  private CommandRunner commandRunner;

  private User hotblack;
  private Administrator karn;
  private User marvin;
  private Room room;

  public GameBeanTest()
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
    logBean = new LogBeanStub();

    Area aArea = TestingConstants.getArea();
    room = TestingConstants.getRoom(aArea);
    hotblack = TestingConstants.getHotblack(room);
    karn = TestingConstants.getKarn();
    karn.setRoom(room);
    marvin = TestingConstants.getMarvin(room);
    HashSet<Person> persons = new HashSet<>();
    persons.add(hotblack);
    persons.add(marvin);
    persons.add(karn);
    setField(Room.class, "persons", room, persons);
    new MockUp<ErrorDetails>()
    {
      @Mock
      public Response getResponse(Response.Status status)
      {
        return response;
      }

    };

  }

  @AfterMethod
  public void tearDown()
  {
  }

  /**
   * Look around.
   */
  @Test
  public void lookAround()
  {
    LOGGER.fine("authenticate1");
    marvin.setActive(true);
    GameBean gameBean = new GameBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

      @Override
      protected String getPlayerName() throws IllegalStateException
      {
        return "Marvin";
      }

    };
    setField(GameBean.class, "logBean", gameBean, logBean);
    setField(GameBean.class, "commandRunner", gameBean, commandRunner);
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(Macro.class, (Object) any);
        result = null;
        entityManager.createNamedQuery("UserCommand.findActive", UserCommand.class);
        result = typedquery;
        typedquery.getResultList();
        result = Collections.emptyList();

      }
    };
    // Unit under test is exercised.
    PrivateDisplay result = gameBean.playGame("Marvin", "l");
    assertThat(result.body).isEqualTo("You are standing on a small bridge.");
    assertThat(result.image).isNull();
    assertThat(result.title).isEqualTo("The bridge");
    assertThat(result.down).isNull();
    assertThat(result.up).isNull();
    assertThat(result.east).isNull();
    assertThat(result.west).isNull();
    assertThat(result.north).isNull();
    assertThat(result.south).isNull();
    assertThat(result.items).hasSize(0);
    assertThat(result.persons).hasSize(2); // marvin doesn't show up, because he's the one playing
    assertThat(result.persons.get(0).name).isEqualTo("Hotblack");
    assertThat(result.persons.get(1).name).isEqualTo("Karn");
    assertThat(getLog(marvin)).isEqualTo("");
  }

  /**
   * Look around.
   */
  @Test
  public void lookAroundWithFrog()
  {
    LOGGER.fine("authenticate1");
    marvin.setActive(true);
    hotblack.setFrogging(5);
    GameBean gameBean = new GameBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

      @Override
      protected String getPlayerName() throws IllegalStateException
      {
        return "Marvin";
      }
    };
    setField(GameBean.class, "logBean", gameBean, logBean);
    setField(GameBean.class, "commandRunner", gameBean, commandRunner);
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(Macro.class, (Object) any);
        result = null;
        entityManager.createNamedQuery("UserCommand.findActive", UserCommand.class);
        result = typedquery;
        typedquery.getResultList();
        result = Collections.emptyList();

      }
    };
    // Unit under test is exercised.
    PrivateDisplay result = gameBean.playGame("Marvin", "l");
    assertThat(result.body).isEqualTo("You are standing on a small bridge.");
    assertThat(result.image).isNull();
    assertThat(result.title).isEqualTo("The bridge");
    assertThat(result.down).isNull();
    assertThat(result.up).isNull();
    assertThat(result.east).isNull();
    assertThat(result.west).isNull();
    assertThat(result.north).isNull();
    assertThat(result.south).isNull();
    assertThat(result.items).hasSize(0);
    assertThat(result.persons).hasSize(2); // marvin doesn't show up, because he's the one playing
    assertThat(result.persons.get(0).name).isEqualTo("Hotblack");
    assertThat(result.persons.get(0).race).isEqualTo("frog");
    assertThat(result.persons.get(1).name).isEqualTo("Karn");
    assertThat(result.persons.get(1).race).isEqualTo("human");
    assertThat(getLog(marvin)).isEqualTo("");
  }

  /**
   * Look around.
   */
  @Test
  public void lookAroundWithJackass()
  {
    LOGGER.fine("authenticate1");
    marvin.setActive(true);
    hotblack.setJackassing(5);
    GameBean gameBean = new GameBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

      @Override
      protected String getPlayerName() throws IllegalStateException
      {
        return "Marvin";
      }
    };
    setField(GameBean.class, "logBean", gameBean, logBean);
    setField(GameBean.class, "commandRunner", gameBean, commandRunner);
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(Macro.class, (Object) any);
        result = null;
        entityManager.createNamedQuery("UserCommand.findActive", UserCommand.class);
        result = typedquery;
        typedquery.getResultList();
        result = Collections.emptyList();

      }
    };
    // Unit under test is exercised.
    PrivateDisplay result = gameBean.playGame("Marvin", "l");
    assertThat(result.body).isEqualTo("You are standing on a small bridge.");
    assertThat(result.image).isNull();
    assertThat(result.title).isEqualTo("The bridge");
    assertThat(result.down).isNull();
    assertThat(result.up).isNull();
    assertThat(result.east).isNull();
    assertThat(result.west).isNull();
    assertThat(result.north).isNull();
    assertThat(result.south).isNull();
    assertThat(result.items).hasSize(0);
    assertThat(result.persons).hasSize(2); // marvin doesn't show up, because he's the one playing
    assertThat(result.persons.get(0).name).isEqualTo("Hotblack");
    assertThat(result.persons.get(0).race).isEqualTo("jackass");
    assertThat(result.persons.get(1).name).isEqualTo("Karn");
    assertThat(result.persons.get(1).race).isEqualTo("human");
    assertThat(getLog(marvin)).isEqualTo("");

  }

  /**
   * Look around.
   */
  @Test
  public void lookAroundWithInvisible()
  {
    LOGGER.fine("authenticate1");
    marvin.setActive(true);
    karn.setVisible(false);
    GameBean gameBean = new GameBean()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

      @Override
      protected String getPlayerName() throws IllegalStateException
      {
        return "Marvin";
      }
    };
    setField(GameBean.class, "logBean", gameBean, logBean);
    setField(GameBean.class, "commandRunner", gameBean, commandRunner);
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(Macro.class, (Object) any);
        result = null;
        entityManager.createNamedQuery("UserCommand.findActive", UserCommand.class);
        result = typedquery;
        typedquery.getResultList();
        result = Collections.emptyList();

      }
    };
    // Unit under test is exercised.
    PrivateDisplay result = gameBean.playGame("Marvin", "l");
    assertThat(result.body).isEqualTo("You are standing on a small bridge.");
    assertThat(result.image).isNull();
    assertThat(result.title).isEqualTo("The bridge");
    assertThat(result.down).isNull();
    assertThat(result.up).isNull();
    assertThat(result.east).isNull();
    assertThat(result.west).isNull();
    assertThat(result.north).isNull();
    assertThat(result.south).isNull();
    assertThat(result.items).hasSize(0);
    assertThat(result.persons).hasSize(1); // marvin doesn't show up, because he's the one playing
    assertThat(result.persons.get(0).name).isEqualTo("Hotblack");
    // karn doesn't show up! Which is awesome!
    assertThat(getLog(marvin)).isEqualTo("");
  }

}
