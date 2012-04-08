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
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;
import mmud.rest.services.PublicBean;
import mmud.rest.webentities.Fortune;
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
public class PublicBeanTest
{

    // Obtain a suitable logger.
    private static final Logger logger = Logger.getLogger(PublicBeanTest.class.getName());
    @Mocked
    EntityManager entityManager;
    @Mocked
    WebApplicationException stuff;
    @Mocked
    Query query;

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
    public void setUp()
    {
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

    @Test
    public void fortunesEmptyTest()
    {
        PublicBean publicBean = new PublicBean()
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
                entityManager.createNamedQuery("Person.fortunes");
                result = query;
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
        PublicBean publicBean = new PublicBean()
        {
            @Override
            protected EntityManager getEntityManager()
            {
                return entityManager;
            }
        };

        final Object[] one =
        {
            "Hotblack", Integer.valueOf(34567)
        };
        final Object[] two =
        {
            "Marvin",
            Integer.valueOf(345674)
        };
        final List<Object[]> list = new ArrayList<>();
        list.add(one);
        list.add(two);
        new Expectations() // an "expectation block"
        {

            {
                entityManager.createNamedQuery("Person.fortunes");
                result = query;
                query.setMaxResults(100);
                query.getResultList();
                result = list;
            }
        };
        // Unit under test is exercised.
        List<Fortune> result = publicBean.fortunes();
        // Verification code (JUnit/TestNG asserts), if any.

        assertEquals(result.size(), 2);
    }

    @Test
    public void whoTest()
    {
        System.out.println("whoTest");
        PublicBean publicBean = new PublicBean()
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
                entityManager.createNamedQuery((String) any);
                result = query;
            }
        };
        // Unit under test is exercised.
        List<PublicPerson> result = publicBean.who();
        // Verification code (JUnit/TestNG asserts), if any.
        assertEquals(result.size(), 0);
    }
}
