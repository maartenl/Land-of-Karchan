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

import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.core.SecurityContext;
import mmud.commands.CommandRunner;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Macro;
import mmud.database.entities.game.MacroPK;
import mmud.database.entities.game.Room;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.MudException;
import mmud.rest.services.GameRestService;
import mmud.rest.webentities.PrivateDisplay;
import mmud.services.IdleUsersService;
import mmud.services.PlayerAuthenticationService;
import mmud.testing.TestingConstants;
import org.mockito.invocation.InvocationOnMock;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author maartenl
 */
public class GameRestServiceTest extends MudTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(GameRestServiceTest.class.getName());

  private LogServiceStub logBean;

  private CommandRunner commandRunner = new CommandRunner();

  private User hotblack;
  private Administrator karn;
  private User marvin;
  private Room room;

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

  private final GameRestService gameRestService = new GameRestService()
  {
    {
      setPlayerAuthenticationService(playerAuthenticationService);
    }
  };

  public GameRestServiceTest()
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
    logBean = new LogServiceStub();

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

    setField(GameRestService.class, "logService", gameRestService, logBean);
    setField(GameRestService.class, "commandRunner", gameRestService, commandRunner);
  }

  @AfterMethod
  public void tearDown()
  {
  }

  /**
   * Look around. (Marvin: 'l')
   */
  @Test
  public void lookAround()
  {
    LOGGER.fine("lookAround");
    marvin.setActive(true);

    TypedQuery typedQuery = mock(TypedQuery.class);
    EntityManager entityManager = mock(EntityManager.class);
    setField(GameRestService.class, "em", gameRestService, entityManager);
    setField(GameRestService.class, "idleUsersService", gameRestService, new IdleUsersService());
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(User.class, "Karn")).thenReturn(karn);
    when(entityManager.createNamedQuery("UserCommand.findActive", UserCommand.class)).thenReturn(typedQuery);

    // we do not test macros here.
    when(entityManager.find(eq(Macro.class), any(MacroPK.class)))
      .thenAnswer((InvocationOnMock invocationOnMock) ->
      {
        MacroPK macropk = invocationOnMock.getArgument(1);
        //inspect the actualBazo here and thrw exception if it does not meet your testing requirements.
        assertThat(macropk.getMacroname()).isEqualTo("l");
        assertThat(macropk.getName()).isEqualTo("Marvin");
        return null;
      });

    when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

    // Unit under test is exercised.
    PrivateDisplay result = gameRestService.playGame("Marvin", "l");
    assertThat(result.body).isEqualTo("You are standing on a small bridge.");
    assertThat(result.image).isNull();
    assertThat(result.title).isEqualTo("The bridge");
    assertThat(result.down).isNull();
    assertThat(result.up).isNull();
    assertThat(result.east).isNull();
    assertThat(result.west).isNull();
    assertThat(result.north).isNull();
    assertThat(result.south).isNull();
    assertThat(result.items).isEmpty();
    assertThat(result.persons).hasSize(2); // marvin doesn't show up, because he's the one playing
    assertThat(result.persons.get(0).name).isEqualTo("Hotblack");
    assertThat(result.persons.get(1).name).isEqualTo("Karn");
    assertThat(getLog(marvin)).isEmpty();
  }

  /**
   * Look around.
   */
  @Test
  public void lookAroundWithFrog()
  {
    LOGGER.fine("lookAroundWithFrog");
    marvin.setActive(true);
    hotblack.setFrogging(5);
    TypedQuery typedQuery = mock(TypedQuery.class);
    EntityManager entityManager = mock(EntityManager.class);
    setField(GameRestService.class, "em", gameRestService, entityManager);
    setField(GameRestService.class, "idleUsersService", gameRestService, new IdleUsersService());
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(User.class, "Karn")).thenReturn(karn);
    when(entityManager.createNamedQuery("UserCommand.findActive", UserCommand.class)).thenReturn(typedQuery);

    // we do not test macros here.
    when(entityManager.find(eq(Macro.class), any(MacroPK.class)))
      .thenAnswer((InvocationOnMock invocationOnMock) ->
      {
        MacroPK macropk = invocationOnMock.getArgument(1);
        //inspect the actualBazo here and thrw exception if it does not meet your testing requirements.
        assertThat(macropk.getMacroname()).isEqualTo("l");
        assertThat(macropk.getName()).isEqualTo("Marvin");
        return null;
      });

    when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

    // Unit under test is exercised.
    PrivateDisplay result = gameRestService.playGame("Marvin", "l");
    assertThat(result.body).isEqualTo("You are standing on a small bridge.");
    assertThat(result.image).isNull();
    assertThat(result.title).isEqualTo("The bridge");
    assertThat(result.down).isNull();
    assertThat(result.up).isNull();
    assertThat(result.east).isNull();
    assertThat(result.west).isNull();
    assertThat(result.north).isNull();
    assertThat(result.south).isNull();
    assertThat(result.items).isEmpty();
    assertThat(result.persons).hasSize(2); // marvin doesn't show up, because he's the one playing
    assertThat(result.persons.get(0).name).isEqualTo("Hotblack");
    assertThat(result.persons.get(0).race).isEqualTo("frog");
    assertThat(result.persons.get(1).name).isEqualTo("Karn");
    assertThat(result.persons.get(1).race).isEqualTo("human");
    assertThat(getLog(marvin)).isEmpty();
  }

  /**
   * Look around.
   */
  @Test
  public void lookAroundWithJackass()
  {
    LOGGER.fine("lookAroundWithJackass");
    marvin.setActive(true);
    hotblack.setJackassing(5);
    TypedQuery typedQuery = mock(TypedQuery.class);
    EntityManager entityManager = mock(EntityManager.class);
    setField(GameRestService.class, "em", gameRestService, entityManager);
    setField(GameRestService.class, "idleUsersService", gameRestService, new IdleUsersService());
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(User.class, "Karn")).thenReturn(karn);
    when(entityManager.createNamedQuery("UserCommand.findActive", UserCommand.class)).thenReturn(typedQuery);

    // we do not test macros here.
    when(entityManager.find(eq(Macro.class), any(MacroPK.class)))
      .thenAnswer((InvocationOnMock invocationOnMock) ->
      {
        MacroPK macropk = invocationOnMock.getArgument(1);
        //inspect the actualBazo here and thrw exception if it does not meet your testing requirements.
        assertThat(macropk.getMacroname()).isEqualTo("l");
        assertThat(macropk.getName()).isEqualTo("Marvin");
        return null;
      });

    when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

    // Unit under test is exercised.
    PrivateDisplay result = gameRestService.playGame("Marvin", "l");
    assertThat(result.body).isEqualTo("You are standing on a small bridge.");
    assertThat(result.image).isNull();
    assertThat(result.title).isEqualTo("The bridge");
    assertThat(result.down).isNull();
    assertThat(result.up).isNull();
    assertThat(result.east).isNull();
    assertThat(result.west).isNull();
    assertThat(result.north).isNull();
    assertThat(result.south).isNull();
    assertThat(result.items).isEmpty();
    assertThat(result.persons).hasSize(2); // marvin doesn't show up, because he's the one playing
    assertThat(result.persons.get(0).name).isEqualTo("Hotblack");
    assertThat(result.persons.get(0).race).isEqualTo("jackass");
    assertThat(result.persons.get(1).name).isEqualTo("Karn");
    assertThat(result.persons.get(1).race).isEqualTo("human");
    assertThat(getLog(marvin)).isEmpty();

  }

  /**
   * Look around.
   */
  @Test
  public void lookAroundWithInvisible()
  {
    LOGGER.fine("lookAroundWithInvisible");
    marvin.setActive(true);
    karn.setVisible(false);
    TypedQuery typedQuery = mock(TypedQuery.class);
    EntityManager entityManager = mock(EntityManager.class);
    setField(GameRestService.class, "em", gameRestService, entityManager);
    setField(GameRestService.class, "idleUsersService", gameRestService, new IdleUsersService());
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(User.class, "Karn")).thenReturn(karn);
    when(entityManager.createNamedQuery("UserCommand.findActive", UserCommand.class)).thenReturn(typedQuery);

    // we do not test macros here.
    when(entityManager.find(eq(Macro.class), any(MacroPK.class)))
      .thenAnswer((InvocationOnMock invocationOnMock) ->
      {
        MacroPK macropk = invocationOnMock.getArgument(1);
        //inspect the actualBazo here and thrw exception if it does not meet your testing requirements.
        assertThat(macropk.getMacroname()).isEqualTo("l");
        assertThat(macropk.getName()).isEqualTo("Marvin");
        return null;
      });

    when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

    // Unit under test is exercised.
    PrivateDisplay result = gameRestService.playGame("Marvin", "l");
    assertThat(result.body).isEqualTo("You are standing on a small bridge.");
    assertThat(result.image).isNull();
    assertThat(result.title).isEqualTo("The bridge");
    assertThat(result.down).isNull();
    assertThat(result.up).isNull();
    assertThat(result.east).isNull();
    assertThat(result.west).isNull();
    assertThat(result.north).isNull();
    assertThat(result.south).isNull();
    assertThat(result.items).isEmpty();
    assertThat(result.persons).hasSize(1); // marvin doesn't show up, because he's the one playing
    assertThat(result.persons.get(0).name).isEqualTo("Hotblack");
    // karn doesn't show up! Which is awesome!
    assertThat(getLog(marvin)).isEmpty();
  }

}
