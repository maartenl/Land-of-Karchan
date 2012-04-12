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
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Mail;
import mmud.database.entities.game.Person;
import mmud.database.entities.game.Room;
import mmud.database.enums.God;
import mmud.rest.services.PrivateBean;
import mmud.rest.webentities.PrivateMail;
import mmud.testing.TestingConstants;
import mmud.testing.TestingUtils;
import mockit.Delegate;
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
     * See to it that a bot cannot be authenticated. WebApplicationException
     * expected.
     */
    @Test
    public void listMailAuthenticate3()
    {
        logger.fine("listMailAuthenticate3");
        marvin.setGod(God.BOT);
        PrivateBean privateBean = new PrivateBean()
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
            }
        };
        // Unit under test is exercised.
        try
        {
            List<PrivateMail> result = privateBean.listMail("Marvin", null, "lok");
            fail("We are supposed to get an exception here.");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

        }
    }

    /**
     * Person not found, cannot authenticate. WebApplicationException expected.
     */
    @Test
    public void listMailAuthenticate2()
    {
        logger.fine("listMailAuthenticate2");
        PrivateBean privateBean = new PrivateBean()
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
            List<PrivateMail> result = privateBean.listMail("Marvin", null, "woahNelly");
            fail("We are supposed to get an exception here.");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());

        }
    }

    /**
     * Person found but lok session password does not match.
     * WebapplicationException expected.
     */
    @Test
    public void listMailAuthenticate1()
    {
        logger.fine("listMailAuthenticate1");
        PrivateBean privateBean = new PrivateBean()
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
            }
        };
        // Unit under test is exercised.
        try
        {
            List<PrivateMail> result = privateBean.listMail("Marvin", null, "woahNelly");
            fail("We are supposed to get an exception here.");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());

        }
    }

    @Test
    public void listMailEmpty()
    {
        logger.fine("listMailEmpty");
        PrivateBean privateBean = new PrivateBean()
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
                entityManager.createNamedQuery("Mail.listmail");
                result = query;
                query.setParameter("name", marvin);
                query.setMaxResults(20);
                query.getResultList();

                entityManager.createNamedQuery("Mail.nonewmail");
                result = query;
                query.setParameter("name", marvin);
                query.executeUpdate();
            }
        };
        // Unit under test is exercised.
        List<PrivateMail> result = privateBean.listMail("Marvin", null, "lok");
        // Verification code (JUnit/TestNG asserts), if any.
        assertEquals(result.size(), 0);
    }

    @Test
    public void listMail()
    {
        logger.fine("listMail");
        Date secondDate = new Date();
        Date firstDate = new Date(secondDate.getTime() - 1_000_000);
        final List<Mail> list = new ArrayList<>();
        Mail mail = new Mail();
        mail.setId(1l);
        mail.setSubject("Subject");
        mail.setBody("First mail");
        mail.setToname(hotblack);
        mail.setName(marvin);
        mail.setDeleted(false);
        mail.setHaveread(false);
        mail.setNewmail(true);
        mail.setWhensent(firstDate);
        list.add(mail);
        mail = new Mail();
        mail.setId(2l);
        mail.setSubject("Subject2");
        mail.setBody("Second mail");
        mail.setToname(hotblack);
        mail.setName(marvin);
        mail.setDeleted(false);
        mail.setHaveread(true);
        mail.setNewmail(false);
        mail.setWhensent(secondDate);
        list.add(mail);
        PrivateBean privateBean = new PrivateBean()
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
                entityManager.createNamedQuery("Mail.listmail");
                result = query;
                query.setParameter("name", marvin);
                query.setMaxResults(20);
                query.getResultList();
                result = list;

                entityManager.createNamedQuery("Mail.nonewmail");
                result = query;
                query.setParameter("name", marvin);
                query.executeUpdate();
                result = 1;
            }
        };
        // Unit under test is exercised.
        List<PrivateMail> result = privateBean.listMail("Marvin", null, "lok");
        // Verification code (JUnit/TestNG asserts), if any.
        assertEquals(result.size(), 2);
        PrivateMail expected = new PrivateMail();
        expected.body = "First mail";
        expected.subject = "Subject";
        expected.haveread = false;
        expected.id = 1l;
        expected.item_id = null;
        expected.name = "Marvin";
        expected.newmail = true;
        expected.toname = "Hotblack";
        expected.whensent = firstDate;
        expected.deleted = false;
        compare(result.get(0), expected);
        expected = new PrivateMail();
        expected.body = "Second mail";
        expected.subject = "Subject2";
        expected.haveread = true;
        expected.id = 2l;
        expected.item_id = null;
        expected.name = "Marvin";
        expected.newmail = false;
        expected.toname = "Hotblack";
        expected.whensent = secondDate;
        expected.deleted = false;
        compare(result.get(1), expected);

    }

    @Test
    public void hasNewMail()
    {
        logger.fine("hasNewMail");
        PrivateBean privateBean = new PrivateBean()
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

                entityManager.createNamedQuery("Mail.hasnewmail");
                result = query;
                query.setParameter("name", marvin);
                query.getSingleResult();
                result = 1l;

            }
        };
        // Unit under test is exercised.
        Response result = privateBean.hasNewMail("Marvin", "lok");
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(result);
        assertEquals(result.getStatus(), Response.Status.OK.getStatusCode());

    }

    @Test
    public void hasNoNewMail()
    {
        logger.fine("hasNoNewMail");
        PrivateBean privateBean = new PrivateBean()
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

                entityManager.createNamedQuery("Mail.hasnewmail");
                result = query;
                query.setParameter("name", marvin);
                query.getSingleResult();
                result = 0l;

            }
        };
        // Unit under test is exercised.
        Response result = privateBean.hasNewMail("Marvin", "lok");
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(result);
        assertEquals(result.getStatus(), Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void newMail()
    {
        logger.fine("newMail");
        PrivateMail privateMail = new PrivateMail();
        privateMail.body = "First mail";
        privateMail.subject = "Subject";
        privateMail.toname = "Hotblack";
        // all other props are ignored by the method under test

        PrivateBean privateBean = new PrivateBean()
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
                entityManager.find(Person.class, "Hotblack");
                result = hotblack;
                entityManager.persist((Mail) any);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    void persist(Mail mail)
                    {
                        assertNotNull(mail);
                        assertEquals(mail.getBody(), "First mail");
                        assertEquals(mail.getSubject(), "Subject");
                        assertEquals(mail.getHaveread(), Boolean.FALSE);
                        assertEquals(mail.getId(), null);
                        assertEquals(mail.getItemId(), null);

                        assertEquals(mail.getName(), marvin);
                        assertEquals(mail.getNewmail(), Boolean.TRUE);
                        assertEquals(mail.getToname(), hotblack);
                        // unable to assertEquals getWhensent. As it is using the current date/time.
                        assertEquals(mail.getDeleted(), Boolean.FALSE);

                    }
                };

            }
        };
        // Unit under test is exercised.
        Response result = privateBean.newMail(privateMail, "Marvin", "lok");
        // Verification code (JUnit/TestNG asserts), if any.
        assertEquals(result.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test
    public void newMailToUnknownUser()
    {
        logger.fine("newMailToUnknownUser");
        PrivateMail privateMail = new PrivateMail();
        privateMail.body = "First mail";
        privateMail.subject = "Subject";
        privateMail.toname = "Unknown";
        // all other props are ignored by the method under test
        PrivateBean privateBean = new PrivateBean()
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
                entityManager.find(Person.class, "Unknown");
                result = null;

            }
        };
        // Unit under test is exercised.
        try
        {
            Response result = privateBean.newMail(privateMail, "Marvin", "lok");
            fail("Exception expected");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());

        }
        // Verification code (JUnit/TestNG asserts), if any.
    }

    @Test
    public void newMailToBot()
    {
        logger.fine("newMailToBot");
        PrivateMail privateMail = new PrivateMail();
        privateMail.body = "First mail";
        privateMail.subject = "Subject";
        privateMail.toname = "Hotblack";
        hotblack.setGod(God.BOT);
        // all other props are ignored by the method under test
        PrivateBean privateBean = new PrivateBean()
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
                entityManager.find(Person.class, "Hotblack");
                result = hotblack;

            }
        };
        // Unit under test is exercised.
        try
        {
            Response result = privateBean.newMail(privateMail, "Marvin", "lok");
            fail("Exception expected");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        }
        // Verification code (JUnit/TestNG asserts), if any.
    }

    @Test
    public void getMailDoesnotExist()
    {
        logger.fine("getMailDoesnotExist");
        // all other props are ignored by the method under test

        PrivateBean privateBean = new PrivateBean()
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
                entityManager.find(Mail.class, 1l);
                result = null;
            }
        };
        // Unit under test is exercised.
        try
        {
            PrivateMail actual = privateBean.getMail("Marvin", "lok", 1l);
            fail("Exception expected");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());

        }
        // Verification code (JUnit/TestNG asserts), if any.
    }

    @Test
    public void getMailSomeoneElse()
    {
        logger.fine("getMailSomeoneElse");
        final Mail mail = new Mail();
        mail.setToname(hotblack);
        mail.setName(hotblack);
        mail.setBody("First mail");
        mail.setSubject("Subject");
        mail.setDeleted(Boolean.TRUE);
        mail.setHaveread(Boolean.TRUE);
        // all other props are ignored by the method under test

        PrivateBean privateBean = new PrivateBean()
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
                entityManager.find(Mail.class, 1l);
                result = mail;
            }
        };
        // Unit under test is exercised.
        try
        {
            PrivateMail actual = privateBean.getMail("Marvin", "lok", 1l);
            fail("Exception expected");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());

        }
        // Verification code (JUnit/TestNG asserts), if any.
    }

    @Test
    public void getMailDeleted()
    {
        logger.fine("getMailDeleted");
        final Mail mail = new Mail();
        mail.setToname(marvin);
        mail.setName(hotblack);
        mail.setBody("First mail");
        mail.setSubject("Subject");
        mail.setDeleted(Boolean.TRUE);
        mail.setHaveread(Boolean.TRUE);
        // all other props are ignored by the method under test

        PrivateBean privateBean = new PrivateBean()
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
                entityManager.find(Mail.class, 1l);
                result = mail;
            }
        };
        // Unit under test is exercised.
        try
        {
            PrivateMail actual = privateBean.getMail("Marvin", "lok", 1l);
            fail("Exception expected");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.NOT_FOUND.getStatusCode());

        }
        // Verification code (JUnit/TestNG asserts), if any.
    }

    @Test
    public void getMail()
    {
        logger.fine("getMail");
        final Mail mail = new Mail();
        mail.setToname(marvin);
        mail.setName(hotblack);
        mail.setBody("First mail");
        mail.setSubject("Subject");
        mail.setDeleted(Boolean.FALSE);
        mail.setHaveread(Boolean.TRUE);
        // all other props are ignored by the method under test

        PrivateBean privateBean = new PrivateBean()
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
                entityManager.find(Mail.class, 1l);
                result = mail;
            }
        };
        // Unit under test is exercised.
        PrivateMail actual = privateBean.getMail("Marvin", "lok", 1l);
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(actual);
        PrivateMail expected = new PrivateMail();
        expected.body = "First mail";
        expected.subject = "Subject";
        expected.toname = "Marvin";
        expected.name = "Hotblack";
        expected.deleted = false;
        expected.haveread = true;
        compare(actual, expected);
    }
}
