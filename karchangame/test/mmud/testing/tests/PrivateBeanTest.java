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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.BoardMessage;
import mmud.database.entities.game.Person;
import mmud.database.entities.game.Room;
import mmud.rest.services.PublicBean;
import mmud.rest.webentities.Fortune;
import mmud.rest.webentities.News;
import mmud.rest.webentities.PublicPerson;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author maartenl
 */
public class PrivateBeanTest
{

    // Obtain a suitable logger.
    private static final Logger logger = Logger.getLogger(PrivateBeanTest.class.getName());
    @Mocked
    EntityManager entityManager;
    @Mocked
    WebApplicationException stuff;
    @Mocked
    Query query;
    private Person hotblack;
    private Person marvin;

    public PrivateBeanTest()
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
    public void setUp()
    {
        Area aArea = new Area();
        aArea.setShortdescription("On board the Starship Heart of Gold");
        Room aRoom = new Room();
        aRoom.setTitle("The bridge");
        aRoom.setArea(aArea);

        Person person = new Person();
        person.setName("Hotblack");
        // JDK7: number formats, for clarification.
        // 1_000_000 ms = 1_000 sec = 16 min, 40 sec
        person.setLastlogin(new Date((new Date()).getTime() - 1_000_000));
        person.setSleep(Boolean.FALSE);
        person.setRoom(aRoom);
        hotblack = person;
        person = new Person();
        person.setName("Marvin");
        // JDK7: number formats, for clarification.
        // 2_000_000 ms = 2_000 sec = 33 min, 20 sec
        person.setLastlogin(new Date((new Date()).getTime() - 2_000_000));
        person.setRoom(aRoom);
        person.setSleep(Boolean.TRUE);
        marvin = person;
    }

    @AfterMethod
    public void tearDown()
    {
    }

    @Test
    public void hello()
    {
        assertEquals(2, 2);
    }

    private boolean compareBase(Object actual, Object expected)
    {
        if (actual == null && expected == null)
        {
            return true;
        }
        assertNotNull(actual, "actual should not be null");
        assertNotNull(expected, "expected should not be null");
        return false;
    }

    private void compare(Fortune actual, Fortune expected)
    {
        if (compareBase(actual, expected))
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
        if (compareBase(actual, expected))
        {
            return;
        }
        assertEquals(actual.name, expected.name, "name");
        assertEquals(actual.title, expected.title, "title");
        assertEquals(actual.sleep, expected.sleep, "sleep");
        assertEquals(actual.area, expected.area, "area");
        assertEquals(actual.min, expected.min, "min");
        assertEquals(actual.sec, expected.sec, "sec");
    }

    private void compare(News actual, News expected)
    {
        if (compareBase(actual, expected))
        {
            return;
        }
        assertEquals(actual.name, expected.name, "name");
        assertEquals(actual.message, expected.message, "message");
        if (compareBase(actual.posttime, expected.posttime))
        {
            return;
        }
        assertEquals(actual.posttime.getTime(), expected.posttime.getTime(), "posttime");
    }

}
