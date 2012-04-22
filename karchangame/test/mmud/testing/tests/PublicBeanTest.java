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
import javax.ws.rs.core.Response;
import mmud.database.entities.game.*;
import mmud.database.entities.web.CharacterInfo;
import mmud.exceptions.MudException;
import mmud.rest.services.PublicBean;
import mmud.rest.webentities.Fortune;
import mmud.rest.webentities.News;
import mmud.rest.webentities.PublicGuild;
import mmud.rest.webentities.PublicPerson;
import mmud.testing.TestingConstants;
import mmud.testing.TestingUtils;
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
    Query query;
    private Person hotblack;
    private Person marvin;

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
        assertEquals(actual.posttime.getTime(), expected.posttime.getTime(), "posttime");
    }

    @Test
    public void fortunesEmptyTest()
    {
        logger.fine("fortunesEmptyTest");
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
        logger.fine("fortunesTest");
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
        logger.fine("whoEmptyTest");

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
                entityManager.createNamedQuery("Person.who");
                result = query;
            }
        };
        // Unit under test is exercised.
        List<PublicPerson> result = publicBean.who();
        // Verification code (JUnit/TestNG asserts), if any.
        assertEquals(result.size(), 0);
    }

    @Test
    public void whoTest()
    {
        logger.fine("whoTest");

        final List<Person> list = new ArrayList<>();
        list.add(hotblack);
        list.add(marvin);
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
                entityManager.createNamedQuery("Person.who");
                result = query;
                query.getResultList();
                result = list;
            }
        };
        // Unit under test is exercised.
        List<PublicPerson> result = publicBean.who();
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(result, "list expected");
        assertEquals(result.size(), 2);
        PublicPerson expected = new PublicPerson();
        expected.name = "Hotblack";
        expected.sleep = "";
        expected.area = "On board the Starship Heart of Gold";
        expected.min = 16l;
        expected.sec = 40l;
        expected.title = "Guitar keyboard player of the rock group Disaster Area";
        compare(result.get(0), expected);
        expected = new PublicPerson();
        expected.name = "Marvin";
        expected.sleep = "sleeping";
        expected.area = "On board the Starship Heart of Gold";
        expected.min = 33l;
        expected.sec = 20l;
        expected.title = "The Paranoid Android";
        compare(result.get(1), expected);
    }

    @Test
    public void newsEmptyTest()
    {
        logger.fine("newsEmptyTest");
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
                entityManager.createNamedQuery("BoardMessage.news");
                result = query;
                query.setMaxResults(10);
                query.getResultList();
            }
        };
        // Unit under test is exercised.
        List<News> result = publicBean.news();
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(result, "list expected");
        assertEquals(result.size(), 0);
    }

    @Test
    public void newsTest()
    {
        logger.fine("newsTest");
        Date secondDate = new Date();
        Date firstDate = new Date(secondDate.getTime() - 1_000_000);
        final List<BoardMessage> list = new ArrayList<>();
        BoardMessage message = new BoardMessage();
        message.setId(1);
        message.setPerson(hotblack);
        message.setMessage("First post!");
        message.setPosttime(firstDate);
        message.setRemoved(Boolean.FALSE);
        list.add(message);
        message = new BoardMessage();
        message.setId(2);
        message.setPerson(marvin);
        message.setMessage("Damn!");
        message.setPosttime(secondDate);
        message.setRemoved(Boolean.FALSE);
        list.add(message);
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
                entityManager.createNamedQuery("BoardMessage.news");
                result = query;
                query.setMaxResults(10);
                query.getResultList();
                result = list;
            }
        };
        // Unit under test is exercised.
        List<News> result = publicBean.news();
        // Verification code (JUnit/TestNG asserts), if any.
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
        logger.fine("statusEmptyTest");
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
                entityManager.createNamedQuery("Person.status");
                result = query;
                query.getResultList();
            }
        };
        // Unit under test is exercised.
        List<PublicPerson> result = publicBean.status();
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(result, "list expected");
        assertEquals(result.size(), 0);
    }

    @Test
    public void statusTest()
    {
        logger.fine("statusTest");
        final List<Person> list = new ArrayList<>();
        list.add(hotblack);
        list.add(marvin);
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
                entityManager.createNamedQuery("Person.status");
                result = query;
                query.getResultList();
                result = list;
            }
        };
        // Unit under test is exercised.
        List<PublicPerson> result = publicBean.status();
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(result, "list expected");
        assertEquals(result.size(), 2);
        PublicPerson expected = new PublicPerson();
        expected.name = "Hotblack";
        expected.title = "Guitar keyboard player of the rock group Disaster Area";
        compare(result.get(0), expected);
        expected = new PublicPerson();
        expected.name = "Marvin";
        expected.title = "The Paranoid Android";
        compare(result.get(1), expected);
    }

    @Test
    public void guildsEmptyTest()
    {
        logger.fine("guildsEmptyTest");
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
                entityManager.createNamedQuery("Guild.findAll");
                result = query;
                query.getResultList();
            }
        };
        // Unit under test is exercised.
        List<PublicGuild> result = publicBean.guilds();
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(result, "list expected");
        assertEquals(result.size(), 0);
    }

    @Test
    public void guildsTest()
    {
        logger.fine("guildsTest");
        Date secondDate = new Date();
        Date firstDate = new Date(secondDate.getTime() - 1_000_000);
        final List<Guild> list = new ArrayList<>();
        Guild guild = new Guild();
        guild.setTitle("Disaster Area");
        guild.setBossname(hotblack);
        guild.setCreation(firstDate);
        guild.setGuildurl("http://www.disasterarea.com");
        guild.setGuilddescription("This is just a description");
        list.add(guild);
        guild = new Guild();
        guild.setTitle("Sirius Cybernetics Corporation");
        guild.setBossname(marvin);
        guild.setCreation(secondDate);
        guild.setGuildurl("http://www.scc.com");
        guild.setGuilddescription("This is just a description");
        list.add(guild);
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
                entityManager.createNamedQuery("Guild.findAll");
                result = query;
                query.getResultList();
                result = list;
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
        logger.fine("charactersheetNotFoundTest");
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
                entityManager.find(Person.class, "Marvin");
                result = null;
            }
        };
        // Unit under test is exercised.
        try
        {
            PublicPerson person = publicBean.charactersheet("Marvin");
            fail("We expected a not found exception");
        } catch (WebApplicationException e)
        {
            assertEquals(e.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());
        }
        // Verification code (JUnit/TestNG asserts), if any.
    }

    @Test
    public void charactersheetTest() throws MudException
    {
        logger.fine("charactersheetTest");
        Date secondDate = new Date();
        Date firstDate = new Date(secondDate.getTime() - 1_000_000);
        final CharacterInfo charinfo = new CharacterInfo();
        charinfo.setImageurl("http://www.images.com/imageurl.jpg");
        charinfo.setHomepageurl("http://www.homepage.com/");
        charinfo.setDateofbirth("0000");
        charinfo.setCityofbirth("Sirius");
        charinfo.setStoryline("An android");
        charinfo.setName("Marvin");
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
                entityManager.find(Person.class, "Marvin");
                result = marvin;
                entityManager.find(CharacterInfo.class, marvin.getName());
                result = charinfo;
                entityManager.createNamedQuery("Family.findByName");
                result = query;
                query.setParameter("name", marvin.getName());
                query.getResultList();
            }
        };
        // Unit under test is exercised.
        PublicPerson person = publicBean.charactersheet("Marvin");
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(person, "person expected");
        PublicPerson expected = new PublicPerson();
        expected.name = "Marvin";
        expected.title = "The Paranoid Android";
        expected.sex = "male";
        expected.description = "male android";
        expected.imageurl = "http://www.images.com/imageurl.jpg";
        expected.homepageurl = "http://www.homepage.com/";
        expected.dateofbirth = "0000";
        expected.cityofbirth = "Sirius";
        expected.storyline = "An android";
        compare(person, expected);
    }

    @Test
    public void charactersheetsEmptyTest()
    {
        logger.fine("charactersheetsEmptyTest");
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
                entityManager.createNamedQuery("CharacterInfo.charactersheets");
                result = query;
                query.getResultList();
            }
        };
        // Unit under test is exercised.
        List<PublicPerson> result = publicBean.charactersheets();
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(result, "list expected");
        assertEquals(result.size(), 0);
    }

    @Test
    public void charactersheetsTest()
    {
        logger.fine("charactersheetsTest");
        final List<String> list = new ArrayList<>();
        list.add("Marvin");
        list.add("Hotblack");
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
                entityManager.createNamedQuery("CharacterInfo.charactersheets");
                result = query;
                query.getResultList();
                result = list;
            }
        };
        // Unit under test is exercised.
        List<PublicPerson> result = publicBean.charactersheets();
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(result, "list expected");
        assertEquals(result.size(), 2);

        PublicPerson expected = new PublicPerson();
        expected.name = "Marvin";
        expected.url = "/karchangame/resources/public/charactersheets/Marvin";
        compare(result.get(0), expected);
        expected = new PublicPerson();
        expected.name = "Hotblack";
        expected.url = "/karchangame/resources/public/charactersheets/Hotblack";
        compare(result.get(1), expected);
    }
}
