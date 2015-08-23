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
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import mmud.commands.CommandRunner;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Macro;
import mmud.database.entities.game.Room;
import mmud.exceptions.ErrorDetails;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.GameBean;
import mmud.rest.webentities.PrivateDisplay;
import mmud.testing.TestingConstants;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.testng.Assert.fail;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class GameBeanTest extends MudTest
{

    // Obtain a suitable logger.
    private static final Logger logger = Logger.getLogger(GameBeanTest.class.getName());

    @Mocked
    private EntityManager entityManager;

    private LogBeanImpl logBean;

    @Mocked(
            {
                "ok", "status"
            })
    private javax.ws.rs.core.Response response;

    @Mocked
    private Query query;

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
        logBean = new LogBeanImpl();

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
     * Person not found, cannot authenticate. MudWebException expected.
     */
    @Test
    public void authenticate2()
    {
        logger.fine("authenticate2");
        GameBean gameBean = new GameBean()
        {
            @Override
            protected EntityManager getEntityManager()
            {
                return entityManager;
            }
        };
        new Expectations() // an "expectation block"
        {


            {
                entityManager.setProperty("activePersonFilter", 1);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = null;
            }
        };
        try
        {
            // Unit under test is exercised.
            PrivateDisplay result = gameBean.play("Marvin", "woahNelly", 0, "l", true);
        } catch (MudWebException exception)
        {
            // Yay! We get an exception!
            assertThat(exception.getMessage(), equalTo("Marvin was not found."));
        }
    }

    /**
     * Person found but lok session password does not match.
     * MudWebException expected.
     */
    @Test
    public void authenticate1()
    {
        logger.fine("authenticate1");
        marvin.setActive(true);
        GameBean gameBean = new GameBean()
        {
            @Override
            protected EntityManager getEntityManager()
            {
                return entityManager;
            }
        };
        new Expectations() // an "expectation block"
        {


            {
                entityManager.setProperty("activePersonFilter", 1);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
            }
        };
        // Unit under test is exercised.
        try
        {
            PrivateDisplay result = gameBean.play("Marvin", "woahNelly", 0, "l", true);
            fail("We are supposed to get an exception here.");
        } catch (WebApplicationException exception)
        {
            // Yay! We get an exception!
            assertThat(exception.getMessage(), equalTo("session password woahNelly does not match session password of Marvin"));
        }
    }

    /**
     * Look around.
     */
    @Test
    public void lookAround()
    {
        logger.fine("authenticate1");
        marvin.setActive(true);
        marvin.setLok("woahNelly");
        GameBean gameBean = new GameBean()
        {
            @Override
            protected EntityManager getEntityManager()
            {
                return entityManager;
            }
        };
        setField(GameBean.class, "logBean", gameBean, logBean);
        setField(GameBean.class, "commandRunner", gameBean, commandRunner);
        new Expectations() // an "expectation block"
        {


            {
                entityManager.setProperty("activePersonFilter", 1);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Macro.class, (Object) any);
                result = null;
                entityManager.createNamedQuery("UserCommand.findActive");
                result = query;
                query.getResultList();
                result = Collections.emptyList();

            }
        };
        // Unit under test is exercised.
        PrivateDisplay result = gameBean.play("Marvin", "woahNelly", 0, "l", true);
        assertThat(result.body, equalTo("You are standing on a small bridge."));
        assertThat(result.image, nullValue());
        assertThat(result.title, equalTo("The bridge"));
        assertThat(result.down, nullValue());
        assertThat(result.up, nullValue());
        assertThat(result.east, nullValue());
        assertThat(result.west, nullValue());
        assertThat(result.north, nullValue());
        assertThat(result.south, nullValue());
        assertThat(result.items, hasSize(0));
        assertThat(result.persons, hasSize(2)); // marvin doesn't show up, because he's the one playing
        assertThat(result.persons.get(0).name, equalTo("Hotblack"));
        assertThat(result.persons.get(1).name, equalTo("Karn"));
        assertThat(result.log.log, equalTo(""));
        assertThat(result.log.offset, equalTo(0));
        assertThat(result.log.size, equalTo(0));
    }

    /**
     * Look around.
     */
    @Test
    public void lookAroundWithFrog()
    {
        logger.fine("authenticate1");
        marvin.setActive(true);
        marvin.setLok("woahNelly");
        hotblack.setFrogging(5);
        GameBean gameBean = new GameBean()
        {
            @Override
            protected EntityManager getEntityManager()
            {
                return entityManager;
            }
        };
        setField(GameBean.class, "logBean", gameBean, logBean);
        setField(GameBean.class, "commandRunner", gameBean, commandRunner);
        new Expectations() // an "expectation block"
        {


            {
                entityManager.setProperty("activePersonFilter", 1);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Macro.class, (Object) any);
                result = null;
                entityManager.createNamedQuery("UserCommand.findActive");
                result = query;
                query.getResultList();
                result = Collections.emptyList();

            }
        };
        // Unit under test is exercised.
        PrivateDisplay result = gameBean.play("Marvin", "woahNelly", 0, "l", true);
        assertThat(result.body, equalTo("You are standing on a small bridge."));
        assertThat(result.image, nullValue());
        assertThat(result.title, equalTo("The bridge"));
        assertThat(result.down, nullValue());
        assertThat(result.up, nullValue());
        assertThat(result.east, nullValue());
        assertThat(result.west, nullValue());
        assertThat(result.north, nullValue());
        assertThat(result.south, nullValue());
        assertThat(result.items, hasSize(0));
        assertThat(result.persons, hasSize(2)); // marvin doesn't show up, because he's the one playing
        assertThat(result.persons.get(0).name, equalTo("Hotblack"));
        assertThat(result.persons.get(0).race, equalTo("frog"));
        assertThat(result.persons.get(1).name, equalTo("Karn"));
        assertThat(result.persons.get(1).race, equalTo("human"));
        assertThat(result.log.log, equalTo(""));
        assertThat(result.log.offset, equalTo(0));
        assertThat(result.log.size, equalTo(0));
    }

    /**
     * Look around.
     */
    @Test
    public void lookAroundWithJackass()
    {
        logger.fine("authenticate1");
        marvin.setActive(true);
        marvin.setLok("woahNelly");
        hotblack.setJackassing(5);
        GameBean gameBean = new GameBean()
        {
            @Override
            protected EntityManager getEntityManager()
            {
                return entityManager;
            }
        };
        setField(GameBean.class, "logBean", gameBean, logBean);
        setField(GameBean.class, "commandRunner", gameBean, commandRunner);
        new Expectations() // an "expectation block"
        {


            {
                entityManager.setProperty("activePersonFilter", 1);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Macro.class, (Object) any);
                result = null;
                entityManager.createNamedQuery("UserCommand.findActive");
                result = query;
                query.getResultList();
                result = Collections.emptyList();

            }
        };
        // Unit under test is exercised.
        PrivateDisplay result = gameBean.play("Marvin", "woahNelly", 0, "l", true);
        assertThat(result.body, equalTo("You are standing on a small bridge."));
        assertThat(result.image, nullValue());
        assertThat(result.title, equalTo("The bridge"));
        assertThat(result.down, nullValue());
        assertThat(result.up, nullValue());
        assertThat(result.east, nullValue());
        assertThat(result.west, nullValue());
        assertThat(result.north, nullValue());
        assertThat(result.south, nullValue());
        assertThat(result.items, hasSize(0));
        assertThat(result.persons, hasSize(2)); // marvin doesn't show up, because he's the one playing
        assertThat(result.persons.get(0).name, equalTo("Hotblack"));
        assertThat(result.persons.get(0).race, equalTo("jackass"));
        assertThat(result.persons.get(1).name, equalTo("Karn"));
        assertThat(result.persons.get(1).race, equalTo("human"));
        assertThat(result.log.log, equalTo(""));
        assertThat(result.log.offset, equalTo(0));
        assertThat(result.log.size, equalTo(0));
    }

    /**
     * Look around.
     */
    @Test
    public void lookAroundWithInvisible()
    {
        logger.fine("authenticate1");
        marvin.setActive(true);
        karn.setVisible(false);
        marvin.setLok("woahNelly");
        GameBean gameBean = new GameBean()
        {
            @Override
            protected EntityManager getEntityManager()
            {
                return entityManager;
            }
        };
        setField(GameBean.class, "logBean", gameBean, logBean);
        setField(GameBean.class, "commandRunner", gameBean, commandRunner);
        new Expectations() // an "expectation block"
        {


            {
                entityManager.setProperty("activePersonFilter", 1);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Macro.class, (Object) any);
                result = null;
                entityManager.createNamedQuery("UserCommand.findActive");
                result = query;
                query.getResultList();
                result = Collections.emptyList();

            }
        };
        // Unit under test is exercised.
        PrivateDisplay result = gameBean.play("Marvin", "woahNelly", 0, "l", true);
        assertThat(result.body, equalTo("You are standing on a small bridge."));
        assertThat(result.image, nullValue());
        assertThat(result.title, equalTo("The bridge"));
        assertThat(result.down, nullValue());
        assertThat(result.up, nullValue());
        assertThat(result.east, nullValue());
        assertThat(result.west, nullValue());
        assertThat(result.north, nullValue());
        assertThat(result.south, nullValue());
        assertThat(result.items, hasSize(0));
        assertThat(result.persons, hasSize(1)); // marvin doesn't show up, because he's the one playing
        assertThat(result.persons.get(0).name, equalTo("Hotblack"));
        // karn doesn't show up! Which is awesome!
        assertThat(result.log.log, equalTo(""));
        assertThat(result.log.offset, equalTo(0));
        assertThat(result.log.size, equalTo(0));
    }

}
