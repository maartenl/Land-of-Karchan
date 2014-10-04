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
import javax.ws.rs.core.Response.ResponseBuilder;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Mail;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.web.CharacterInfo;
import mmud.database.entities.web.Family;
import mmud.database.entities.web.FamilyPK;
import mmud.database.entities.web.FamilyValue;
import mmud.database.enums.God;
import mmud.exceptions.ErrorDetails;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.MailBean;
import mmud.rest.services.PrivateBean;
import mmud.rest.webentities.PrivateMail;
import mmud.rest.webentities.PrivatePerson;
import mmud.testing.TestingConstants;
import mmud.testing.TestingUtils;
import mockit.Deencapsulation;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
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
public class PrivateBeanTest
{

    // Obtain a suitable logger.
    private static final Logger logger = Logger.getLogger(PrivateBeanTest.class.getName());
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
    public void setUp() throws MudException
    {
        Area aArea = TestingConstants.getArea();
        Room aRoom = TestingConstants.getRoom(aArea);
        hotblack = TestingConstants.getHotblack(God.DEFAULT_USER, aRoom);
        marvin = TestingConstants.getMarvin(God.DEFAULT_USER, aRoom);
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
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
            // Yay! We get an exception!
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
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
            // Yay! We get an exception!
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
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
        Deencapsulation.setField(privateBean, "mailBean", new MailBean()
        {
            @Override
            protected EntityManager getEntityManager()
            {
                return entityManager;
            }
        });
        new Expectations() // an "expectation block"
        {


            {
                entityManager.find(User.class, "Marvin");
                result = marvin;

                entityManager.createNamedQuery("Mail.hasnewmail");
                result = query;
                query.setParameter("name", marvin);
                query.getSingleResult();
                result = 1l;

            }
        };
        responseOkExpectations();
        // Unit under test is exercised.
        privateBean.hasNewMail("Marvin", "lok");
        // Verification code (JUnit/TestNG asserts), if any.
    }

    //TODO : fix this
    // @Test
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
        Deencapsulation.setField(privateBean, "mailBean", new MailBean()
        {
            @Override
            protected EntityManager getEntityManager()
            {
                return entityManager;
            }
        });
        new Expectations() // an "expectation block"
        {


            {
                entityManager.find(User.class, "Marvin");
                result = marvin;

                entityManager.createNamedQuery("Mail.hasnewmail");
                result = query;
                query.setParameter("name", marvin);
                query.getSingleResult();
                result = 0l;

            }
        };
        // Unit under test is exercised.
        privateBean.hasNewMail("Marvin", "lok");
        // Verification code (JUnit/TestNG asserts), if any.
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
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(User.class, "Hotblack");
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
                        assertEquals(mail.getItemDefinition(), null);

                        assertEquals(mail.getName(), marvin);
                        assertEquals(mail.getNewmail(), Boolean.TRUE);
                        assertEquals(mail.getToname(), hotblack);
                        // unable to assertEquals getWhensent. As it is using the current date/time.
                        assertEquals(mail.getDeleted(), Boolean.FALSE);

                    }
                };

            }
        };
        responseOkExpectations();
// Unit under test is exercised.
        Response result = privateBean.newMail(privateMail, "Marvin", "lok");
        // Verification code (JUnit/TestNG asserts), if any.
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
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(User.class, "Unknown");
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
            // Yay! We get an exception!
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
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
            // Yay! We get an exception!
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
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
            // Yay! We get an exception!
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
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
            // Yay! We get an exception!
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
        mail.setHaveread(Boolean.FALSE);
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
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

    // TODO : Fix this!
    //@Test
    public void createMailItem()
    {
        logger.fine("createMailItem");
        final Admin admin = TestingConstants.getAdmin();
        final ItemDefinition itemDef = new ItemDefinition();
        itemDef.setId(8009);
        itemDef.setReaddescription("this is letterhead.</p><p>letterbody</p><p>letterfoot</p>");
        itemDef.setName("paper");
        itemDef.setAdject1("small");
        itemDef.setAdject2("piece");
        itemDef.setAdject3("of");
        itemDef.setGetable(true);
        itemDef.setDropable(true);
        itemDef.setVisible(true);

        itemDef.setCopper(1);
        itemDef.setOwner(admin);
        itemDef.setNotes("Some notes");

        final Mail mail = new Mail();
        mail.setToname(marvin);
        mail.setName(hotblack);
        mail.setBody("First mail");
        mail.setSubject("Subject");
        mail.setDeleted(Boolean.FALSE);
        mail.setHaveread(Boolean.FALSE);
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
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Mail.class, 1l);
                result = mail;
                entityManager.find(ItemDefinition.class, 8009);
                result = itemDef;
                entityManager.createNamedQuery("ItemDefinition.maxid");
                result = query;
                query.getSingleResult();
                result = Integer.valueOf(5);
                entityManager.persist((ItemDefinition) any);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    void persist(ItemDefinition newItemDef)
                    {
                        assertNotNull(newItemDef);
                        assertEquals(newItemDef.getId(), Integer.valueOf(6));
                        assertEquals(newItemDef.getDescription(), itemDef.getDescription());
                        assertEquals(newItemDef.getReaddescription(), "this is <div id=\"karchan_letterhead\">Subject</div>.</p><p><div id=\"karchan_letterbody\">First mail</div></p><p>letterfoot</p>");
                        assertEquals(newItemDef.getName(), itemDef.getName());
                        assertEquals(newItemDef.getAdject1(), itemDef.getAdject1());
                        assertEquals(newItemDef.getAdject2(), itemDef.getAdject2());
                        assertEquals(newItemDef.getAdject3(), itemDef.getAdject3());
                        assertEquals(newItemDef.getGetable(), itemDef.getGetable());
                        assertEquals(newItemDef.getDropable(), itemDef.getDropable());
                        assertEquals(newItemDef.getVisible(), itemDef.getVisible());

                        assertEquals(newItemDef.getCopper(), itemDef.getCopper());
                        assertEquals(newItemDef.getOwner(), itemDef.getOwner());
                        assertEquals(newItemDef.getNotes(), itemDef.getNotes());
                    }
                };

                entityManager.find(Admin.class, Admin.DEFAULT_OWNER);
                result = TestingConstants.getAdmin();

                entityManager.persist((Item) any);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    void persist(Item item)
                    {
                        assertNotNull(item);
                        // assertEquals(item.getCreation(), new Date());
                        assertNull(item.getId());
                        assertEquals(item.getItemDefinition().getId(), Integer.valueOf(6));
                        assertEquals(item.getOwner(), admin);
                    }
                };

            }
        };
        // Unit under test is exercised.
        Response response = privateBean.createMailItem("Marvin", "lok", 1l, 1);
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    // TODO : Fix this!
    //@Test
    public void createSecondMailItem() throws MudException
    {
        logger.fine("createSecondMailItem");
        final Admin admin = TestingConstants.getAdmin();
        final ItemDefinition itemDef = new ItemDefinition();
        itemDef.setId(12);
        itemDef.setReaddescription("Dear people,</p><p>Blahblah</p><p>Regards, Karn.</p>");
        itemDef.setName("paper");
        itemDef.setAdject1("small");
        itemDef.setAdject2("piece");
        itemDef.setAdject3("of");
        itemDef.setGetable(true);
        itemDef.setDropable(true);
        itemDef.setVisible(true);

        itemDef.setCopper(1);
        itemDef.setOwner(admin);
        itemDef.setNotes("Some notes");

        final Mail mail = new Mail();
        mail.setToname(marvin);
        mail.setName(hotblack);
        mail.setBody("First mail");
        mail.setSubject("Subject");
        mail.setDeleted(Boolean.FALSE);
        mail.setHaveread(Boolean.FALSE);
        mail.setItemDefinition(itemDef);
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
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Mail.class, 1l);
                result = mail;
                entityManager.find(Admin.class, Admin.DEFAULT_OWNER);
                result = TestingConstants.getAdmin();
                entityManager.persist((Item) any);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    void persist(Item item)
                    {
                        assertNotNull(item);
                        // assertEquals(item.getCreation(), new Date());
                        assertNull(item.getId());
                        assertEquals(item.getItemDefinition().getId(), Integer.valueOf(12));
                        assertEquals(item.getOwner(), admin);
                    }
                };

            }
        };
        // Unit under test is exercised.
        Response response = privateBean.createMailItem("Marvin", "lok", 1l, 1);
        // Verification code (JUnit/TestNG asserts), if any.
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    // TODO : Fix this!
    //@Test
    public void createMailItemError1() throws MudException
    {
        logger.fine("createMailItemError1");
        final Mail mail = new Mail();
        mail.setToname(marvin);
        mail.setName(hotblack);
        mail.setBody("First mail");
        mail.setSubject("Subject");
        mail.setDeleted(Boolean.FALSE);
        mail.setHaveread(Boolean.FALSE);
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
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Mail.class, 1l);
                result = mail;
            }
        };
        try
        {
            // Unit under test is exercised.
            Response response = privateBean.createMailItem("Marvin", "lok", 1l, -1);
            fail("Exception expected");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

        }
        // Verification code (JUnit/TestNG asserts), if any.
    }

    // TODO : Fix this!
    //@Test
    public void createMailItemError2() throws MudException
    {
        logger.fine("createMailItemError1");
        final Mail mail = new Mail();
        mail.setToname(marvin);
        mail.setName(hotblack);
        mail.setBody("First mail");
        mail.setSubject("Subject");
        mail.setDeleted(Boolean.FALSE);
        mail.setHaveread(Boolean.FALSE);
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
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Mail.class, 1l);
                result = mail;
            }
        };
        try
        {
            // Unit under test is exercised.
            Response response = privateBean.createMailItem("Marvin", "lok", 1l, 8);
            fail("Exception expected");
        } catch (WebApplicationException result)
        {
            assertEquals(result.getResponse().getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

        }
        // Verification code (JUnit/TestNG asserts), if any.
    }

    @Test
    public void deleteMail() throws MudException
    {
        logger.fine("deleteMail");
        final Mail mail = new Mail();
        mail.setId(1l);
        mail.setToname(marvin);
        mail.setName(hotblack);
        mail.setBody("First mail");
        mail.setSubject("Subject");
        mail.setDeleted(Boolean.FALSE);
        mail.setHaveread(Boolean.FALSE);
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Mail.class, 1l);
                result = mail;
            }
        };
        responseOkExpectations();
        // Unit under test is exercised.
        Response response = privateBean.deleteMail("Marvin", "lok", 1l);
        // Verification code (JUnit/TestNG asserts), if any.
        assertEquals(mail.getDeleted(), Boolean.TRUE);
    }

    @Test
    public void deleteMailNotYours() throws MudException
    {
        logger.fine("deleteMail");
        final Mail mail = new Mail();
        mail.setId(1l);
        mail.setToname(hotblack);
        mail.setName(marvin);
        mail.setBody("First mail");
        mail.setSubject("Subject");
        mail.setDeleted(Boolean.FALSE);
        mail.setHaveread(Boolean.FALSE);
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Mail.class, 1l);
                result = mail;
            }
        };

        // Unit under test is exercised.
        try
        {
            privateBean.deleteMail("Marvin", "lok", 1l);
            fail("Exception expected");
        } catch (WebApplicationException result)
        {
            // Yay! We get an exception!
        }// Verification code (JUnit/TestNG asserts), if any.
        assertEquals(mail.getDeleted(), Boolean.FALSE);
    }

    @Test
    public void deleteMailNotFound() throws MudException
    {
        logger.fine("deleteMail");
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Mail.class, 1l);
                result = null;
            }
        };
        try
        {
            // Unit under test is exercised.
            Response response = privateBean.deleteMail("Marvin", "lok", 1l);
            // Verification code (JUnit/TestNG asserts), if any.
            fail("Exception expected");
        } catch (WebApplicationException result)
        {
            // Yay! We get an exception!
        }
    }

    @Test
    public void updateCharacterSheet() throws MudException
    {
        logger.fine("updateCharacterSheet");
        final CharacterInfo cinfo = new CharacterInfo();
        cinfo.setName("Marvin");
        cinfo.setImageurl("http://www.images.com/image.jpg");
        cinfo.setHomepageurl("http://www.homepages.com");
        cinfo.setDateofbirth("none");
        cinfo.setCityofbirth("none");
        cinfo.setStoryline("none");

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
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(CharacterInfo.class, "Marvin");
                result = cinfo;
            }
        };
        responseOkExpectations();
// Unit under test is exercised.
        final PrivatePerson person = new PrivatePerson();
        person.name = "Marvin";
        person.imageurl = "http://www.images.com/newimage.jpg";
        person.homepageurl = "http://www.homepages.com/homepage.html";
        person.dateofbirth = "Beginning of time";
        person.cityofbirth = "Sirius";
        person.storyline = "Life, don't talk to me about life.";
        Response response = privateBean.updateCharacterSheet("Marvin", "lok", person);
        // Verification code (JUnit/TestNG asserts), if any.
        assertEquals(cinfo.getName(), person.name);
        assertEquals(cinfo.getImageurl(), person.imageurl);
        assertEquals(cinfo.getHomepageurl(), person.homepageurl);
        assertEquals(cinfo.getDateofbirth(), person.dateofbirth);
        assertEquals(cinfo.getCityofbirth(), person.cityofbirth);
        assertEquals(cinfo.getStoryline(), person.storyline);

    }

    @Test
    public void newCharacterSheet() throws MudException
    {
        logger.fine("newCharacterSheet");
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
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(CharacterInfo.class, "Marvin");
                result = null;

                entityManager.persist((CharacterInfo) any);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    void persist(CharacterInfo cinfo)
                    {
                        assertNotNull(cinfo);
                        assertEquals(cinfo.getName(), "Marvin");
                        assertEquals(cinfo.getImageurl(), "http://www.images.com/newimage.jpg");
                        assertEquals(cinfo.getHomepageurl(), "http://www.homepages.com/homepage.html");
                        assertEquals(cinfo.getDateofbirth(), "Beginning of time");
                        assertEquals(cinfo.getCityofbirth(), "Sirius");
                        assertEquals(cinfo.getStoryline(), "Life, don't talk to me about life.");
                    }
                };
            }
        };
        responseOkExpectations();
        // Unit under test is exercised.
        final PrivatePerson person = new PrivatePerson();
        person.name = "Marvin";
        person.imageurl = "http://www.images.com/newimage.jpg";
        person.homepageurl = "http://www.homepages.com/homepage.html";
        person.dateofbirth = "Beginning of time";
        person.cityofbirth = "Sirius";
        person.storyline = "Life, don't talk to me about life.";
        privateBean.updateCharacterSheet("Marvin", "lok", person);
        // Verification code (JUnit/TestNG asserts), if any.
    }

    @Test
    public void updateCharacterSheetScriptInjection() throws MudException
    {
        logger.fine("updateCharacterSheetScriptInjection");
        final CharacterInfo cinfo = new CharacterInfo();
        cinfo.setName("Marvin");
        cinfo.setImageurl("http://www.images.com/image.jpg");
        cinfo.setHomepageurl("http://www.homepages.com");
        cinfo.setDateofbirth("none");
        cinfo.setCityofbirth("none");
        cinfo.setStoryline("none");

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
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(CharacterInfo.class, "Marvin");
                result = cinfo;
            }
        };
        responseOkExpectations();        // Unit under test is exercised.
        final PrivatePerson person = new PrivatePerson();
        person.name = "Marvin";
        person.imageurl = "http://www.images.com/newimage.jpg";
        person.homepageurl = "http://www.homepages.com/homepage.html";
        person.dateofbirth = "Beginning of time";
        person.cityofbirth = "Sirius";
        person.storyline = "Life, don't talk to me about <script>alert('woaj');</script>life.";
        Response response = privateBean.updateCharacterSheet("Marvin", "lok", person);
        // Verification code (JUnit/TestNG asserts), if any.

        assertEquals(cinfo.getName(), person.name);
        assertEquals(cinfo.getImageurl(), person.imageurl);
        assertEquals(cinfo.getHomepageurl(), person.homepageurl);
        assertEquals(cinfo.getDateofbirth(), person.dateofbirth);
        assertEquals(cinfo.getCityofbirth(), person.cityofbirth);
        assertEquals(cinfo.getStoryline(), "Life, don't talk to me about life.");

    }

    @Test
    public void updateCharacterSheetOfSomebodyElse() throws MudException
    {
        logger.fine("updateCharacterSheetOfSomebodyElse");
        final CharacterInfo cinfo = new CharacterInfo();
        cinfo.setName("Marvin");
        cinfo.setImageurl("http://www.images.com/image.jpg");
        cinfo.setHomepageurl("http://www.homepages.com");
        cinfo.setDateofbirth("none");
        cinfo.setCityofbirth("none");
        cinfo.setStoryline("none");

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
                entityManager.find(User.class, "Marvin");
                result = marvin;
            }
        };
        // Unit under test is exercised.
        final PrivatePerson person = new PrivatePerson();
        person.name = "Hotblack";
        person.imageurl = "http://www.images.com/newimage.jpg";
        person.homepageurl = "http://www.homepages.com/homepage.html";
        person.dateofbirth = "Beginning of time";
        person.cityofbirth = "Sirius";
        person.storyline = "Life, don't talk to me about <script>alert('woaj');</script>life.";
        try
        {
            Response response = privateBean.updateCharacterSheet("Marvin", "lok", person);
            fail("We are supposed to get an exception here.");
        } catch (WebApplicationException result)
        {
            // Yay! We get an exception!
        }
    }

    @Test
    public void updateFamilyvalues() throws MudException
    {
        logger.fine("updateFamilyvalues");
        final FamilyValue value = new FamilyValue();
        value.setDescription("friend");
        value.setId(1);
        final FamilyValue value2 = new FamilyValue();
        value2.setDescription("bff");
        value2.setId(2);
        final Family family = new Family();
        family.setDescription(value);
        FamilyPK pk = new FamilyPK();
        pk.setName("Marvin");
        pk.setToname("Hotblack");
        family.setFamilyPK(pk);
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Person.class, "Hotblack");
                result = hotblack;
                entityManager.find(FamilyValue.class, 2);
                result = value2;
                entityManager.find(Family.class, (FamilyPK) any);
                result = family;
            }
        };
        responseOkExpectations();        // Unit under test is exercised.
        Response response = privateBean.updateFamilyvalues("Marvin", "lok", "Hotblack", 2);
        // Verification code (JUnit/TestNG asserts), if any.
        assertEquals(family.getDescription(), value2);

    }

    @Test
    public void updateFamilyvaluesNotFound() throws MudException
    {
        logger.fine("updateFamilyvaluesNotFound");
        final FamilyValue value = new FamilyValue();
        value.setDescription("friend");
        value.setId(1);
        final FamilyValue value2 = new FamilyValue();
        value2.setDescription("bff");
        value2.setId(2);
        final Family family = new Family();
        family.setDescription(value);
        FamilyPK pk = new FamilyPK();
        pk.setName("Marvin");
        pk.setToname("Hotblack");
        family.setFamilyPK(pk);
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);

                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Person.class, "Hotblack");
                result = hotblack;
                entityManager.find(FamilyValue.class, 12);
                result = null;
            }
        };
        try
        {
            // Unit under test is exercised.
            Response response = privateBean.updateFamilyvalues("Marvin", "lok", "Hotblack", 12);
            fail("We are supposed to get an exception here.");
        } catch (WebApplicationException result)
        {
            // Yay! We get an exception!
        }

    }

    @Test
    public void newFamilyvalues() throws MudException
    {
        logger.fine("newFamilyvalues");
        final FamilyValue value = new FamilyValue();
        value.setDescription("friend");
        value.setId(1);
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Person.class, "Hotblack");
                result = hotblack;
                entityManager.find(FamilyValue.class, 1);
                result = value;
                entityManager.find(Family.class, (FamilyPK) any);
                result = null;
                entityManager.persist((Family) any);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    void persist(Family fam)
                    {
                        assertNotNull(fam);
                        assertEquals(fam.getDescription(), value);
                        assertEquals(fam.getFamilyPK().getName(), "Marvin");
                        assertEquals(fam.getFamilyPK().getToname(), "Hotblack");
                    }
                };
            }
        };
        responseOkExpectations();
        // Unit under test is exercised.
        Response response = privateBean.updateFamilyvalues("Marvin", "lok", "Hotblack", 1);
        // Verification code (JUnit/TestNG asserts), if any.
    }

    @Test
    public void deleteFamilyvalues() throws MudException
    {
        logger.fine("deleteFamilyvalues");
        final FamilyValue value = new FamilyValue();
        value.setDescription("friend");
        value.setId(1);
        final FamilyValue value2 = new FamilyValue();
        value2.setDescription("bff");
        value2.setId(2);
        final Family family = new Family();
        family.setDescription(value);
        FamilyPK pk = new FamilyPK();
        pk.setName("Marvin");
        pk.setToname("Hotblack");
        family.setFamilyPK(pk);
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
                entityManager.setProperty("activePersonFilter", 0);
                entityManager.setProperty("sundaydateFilter", (Date) any);
                entityManager.find(User.class, "Marvin");
                result = marvin;
                entityManager.find(Family.class, (FamilyPK) any);
                result = family;
                entityManager.remove(family);
            }
        };
        responseOkExpectations();
        // Unit under test is exercised.
        privateBean.deleteFamilyvalues("Marvin", "lok", "Hotblack");
        // Verification code (JUnit/TestNG asserts), if any.
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
