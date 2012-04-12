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
import mmud.testing.TestingConstants;
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
        Area aArea = TestingConstants.getArea();
        Room aRoom = TestingConstants.getRoom(aArea);
        hotblack = TestingConstants.getHotblack(aRoom);
        marvin = TestingConstants.getMarvin(aRoom);
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

}
