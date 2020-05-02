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

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.*;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.exceptions.ErrorDetails;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.PrivateBean;
import mmud.rest.services.PublicBean;
import mmud.rest.webentities.PrivateMail;
import mmud.testing.TestingConstants;
import mmud.testing.TestingUtils;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.testng.Assert.*;

/**
 * @author maartenl
 */
public class MudMailTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(MudMailTest.class.getName());

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

  @Mocked
  TypedQuery typedquery;

  private Person hotblack;
  private Person marvin;

  public MudMailTest()
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
  @Test(expectedExceptions = MudWebException.class)
  public void listMailAuthenticate2()
  {
    LOGGER.fine("listMailAuthenticate2");
    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = null;
      }
    };
    // Unit under test is exercised.
    List<PrivateMail> result = privateBean.listMail("Marvin", null);
  }

  @Test
  public void listMailEmpty()
  {
    LOGGER.fine("listMailEmpty");
    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.createNamedQuery("Mail.listmail", MailReceiver.class);
        result = typedquery;
        typedquery.setParameter("name", marvin);
        typedquery.setMaxResults(20);
        typedquery.getResultList();

        entityManager.createNamedQuery("Mail.nonewmail");
        result = query;
        query.setParameter("name", marvin);
        result = query;
        query.executeUpdate();
      }
    };
    // Unit under test is exercised.
    List<PrivateMail> result = privateBean.listMail("Marvin", null);
    // Verification code (JUnit/TestNG asserts), if any.
    assertEquals(result.size(), 0);
  }

  @Test
  public void listMail()
  {
    LOGGER.fine("listMail");
    LocalDateTime secondDate = LocalDateTime.now();
    LocalDateTime firstDate = secondDate.plusSeconds(-1_000L);
    final List<MailReceiver> list = new ArrayList<>();
    Mail mail = new Mail();
    mail.setId(1l);
    mail.setSubject("Subject");
    mail.setBody("First mail");
    mail.setToname("hotblack");
    mail.setName(marvin);
    mail.setDeleted(false);
    mail.setWhensent(firstDate);
    MailReceiver mailreceiver = new MailReceiver();
    mailreceiver.setId(5L);
    mailreceiver.setDeleted(false);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(false);
    mailreceiver.setToname(hotblack);
    mailreceiver.setNewmail(true);
    list.add(mailreceiver);
    mail = new Mail();
    mail.setId(2l);
    mail.setSubject("Subject2");
    mail.setBody("Second mail");
    mail.setToname("hotblack");
    mail.setName(marvin);
    mail.setDeleted(false);
    mail.setWhensent(secondDate);
    mailreceiver = new MailReceiver();
    mailreceiver.setId(6L);
    mailreceiver.setDeleted(false);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(true);
    mailreceiver.setToname(hotblack);
    mailreceiver.setNewmail(false);
    list.add(mailreceiver);
    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.createNamedQuery("Mail.listmail", MailReceiver.class);
        result = typedquery;
        typedquery.setParameter("name", marvin);
        typedquery.setMaxResults(20);
        typedquery.getResultList();
        result = list;

        entityManager.createNamedQuery("Mail.nonewmail");
        result = query;
        query.setParameter("name", marvin);
        result = query;
        query.executeUpdate();
        result = 1;
      }
    };
    // Unit under test is exercised.
    List<PrivateMail> result = privateBean.listMail("Marvin", null);
    // Verification code (JUnit/TestNG asserts), if any.
    assertEquals(result.size(), 2);
    PrivateMail expected = new PrivateMail();
    expected.body = "First mail";
    expected.subject = "Subject";
    expected.haveread = false;
    expected.id = 5l;
    expected.item_id = null;
    expected.name = "Marvin";
    expected.newmail = true;
    expected.toname = "hotblack";
    expected.whensent = firstDate;
    expected.deleted = false;
    compare(result.get(0), expected);
    expected = new PrivateMail();
    expected.body = "Second mail";
    expected.subject = "Subject2";
    expected.haveread = true;
    expected.id = 6l;
    expected.item_id = null;
    expected.name = "Marvin";
    expected.newmail = false;
    expected.toname = "hotblack";
    expected.whensent = secondDate;
    expected.deleted = false;
    compare(result.get(1), expected);

  }

  @Test
  public void newMail()
  {
    LOGGER.fine("newMail");
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

      @Override
      protected String getPlayerName() throws IllegalStateException
      {
        return "Marvin";
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
            assertEquals(mail.getId(), null);

            assertEquals(mail.getName(), marvin);
            assertEquals(mail.getToname(), "Hotblack");
            // unable to assertEquals getWhensent. As it is using the current date/time.
            assertEquals(mail.getDeleted(), Boolean.FALSE);

          }
        };
        entityManager.persist((MailReceiver) any);
        result = new Delegate()
        {
          // The name of this method can actually be anything.
          void persist(MailReceiver mail)
          {
            assertNotNull(mail);
            assertEquals(mail.getHaveread(), Boolean.FALSE);
            assertEquals(mail.getId(), null);

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
    Response result = privateBean.newMail(privateMail, "Marvin");
    // Verification code (JUnit/TestNG asserts), if any.
  }

  @Test
  public void newMailToDeputies()
  {
    LOGGER.fine("newMail");
    PrivateMail privateMail = new PrivateMail();
    privateMail.body = "First mail";
    privateMail.subject = "Subject";
    privateMail.toname = "deputies";

    PublicBean publicBean = new PublicBean()
    {
      @Override
      public List<User> getDeputies()
      {
        User karn = new User();
        karn.setName("Karn");
        User ephinie = new User();
        ephinie.setName("Ephinie");
        User mya = new User();
        mya.setName("Mya");
        return Arrays.asList(karn, ephinie, mya);
      }
    };

    // all other props are ignored by the method under test
    PrivateBean privateBean = new PrivateBean()
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

      @Override
      public PublicBean getPublicBean()
      {
        return publicBean;
      }

    };
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.persist((Mail) any);
        result = new Delegate()
        {
          // The name of this method can actually be anything.
          void persist(Mail mail)
          {
            assertNotNull(mail);
            assertEquals(mail.getBody(), "First mail");
            assertEquals(mail.getSubject(), "Subject");
            assertEquals(mail.getId(), null);
            assertEquals(mail.getName(), marvin);
            // unable to assertEquals getWhensent. As it is using the current date/time.
            assertEquals(mail.getDeleted(), Boolean.FALSE);

          }
        };
        entityManager.persist((MailReceiver) any);
        result = new Delegate()
        {
          // The name of this method can actually be anything.
          void persist(MailReceiver mail)
          {
            assertNotNull(mail);
            assertEquals(mail.getHaveread(), Boolean.FALSE);
            assertEquals(mail.getId(), null);

            assertEquals(mail.getNewmail(), Boolean.TRUE);
            assertEquals(mail.getToname().getName(), "Karn");
            // unable to assertEquals getWhensent. As it is using the current date/time.
            assertEquals(mail.getDeleted(), Boolean.FALSE);

          }
        };
        entityManager.persist((MailReceiver) any);
        result = new Delegate()
        {
          // The name of this method can actually be anything.
          void persist(MailReceiver mail)
          {
            assertNotNull(mail);
            assertEquals(mail.getHaveread(), Boolean.FALSE);
            assertEquals(mail.getId(), null);

            assertEquals(mail.getNewmail(), Boolean.TRUE);
            assertEquals(mail.getToname().getName(), "Mya");
//             unable to assertEquals getWhensent. As it is using the current date/time.
            assertEquals(mail.getDeleted(), Boolean.FALSE);

          }
        };
        entityManager.persist((MailReceiver) any);
        result = new Delegate()
        {
          // The name of this method can actually be anything.
          void persist(MailReceiver mail)
          {
            assertNotNull(mail);
            assertEquals(mail.getHaveread(), Boolean.FALSE);
            assertEquals(mail.getId(), null);

            assertEquals(mail.getNewmail(), Boolean.TRUE);
            assertEquals(mail.getToname().getName(), "Ephinie");
            // unable to assertEquals getWhensent. As it is using the current date/time.
            assertEquals(mail.getDeleted(), Boolean.FALSE);

          }
        };

      }
    };
    responseOkExpectations();
// Unit under test is exercised.
    Response result = privateBean.newMail(privateMail, "Marvin");
    // Verification code (JUnit/TestNG asserts), if any.
  }

  @Test(expectedExceptions = MudWebException.class)
  public void newMailToUnknownUser()
  {
    LOGGER.fine("newMailToUnknownUser");
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

      @Override
      protected String getPlayerName() throws IllegalStateException
      {
        return "Marvin";
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
    Response result = privateBean.newMail(privateMail, "Marvin");
  }

  @Test(expectedExceptions = MudWebException.class)
  public void getMailDoesnotExist()
  {
    LOGGER.fine("getMailDoesnotExist");
    // all other props are ignored by the method under test

    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(MailReceiver.class, 1l);
        result = null;
      }
    };
    // Unit under test is exercised.
    PrivateMail actual = privateBean.getMailInfo("Marvin", 1l);
  }

  @Test(expectedExceptions = MudWebException.class)
  public void getMailSomeoneElse()
  {
    LOGGER.fine("getMailSomeoneElse");
    final Mail mail = new Mail();
    mail.setToname("hotblack");
    mail.setName(hotblack);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.TRUE);
    MailReceiver mailreceiver = new MailReceiver();
    mailreceiver.setDeleted(false);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(true);
    mailreceiver.setToname(hotblack);
    mailreceiver.setNewmail(true);
    // all other props are ignored by the method under test

    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(MailReceiver.class, 1l);
        result = mailreceiver;
      }
    };
    // Unit under test is exercised.
    PrivateMail actual = privateBean.getMailInfo("Marvin", 1l);
  }

  @Test(expectedExceptions = MudWebException.class)
  public void getMailDeleted()
  {
    LOGGER.fine("getMailDeleted");
    final Mail mail = new Mail();
    mail.setToname("marvin");
    mail.setName(hotblack);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.FALSE);
    MailReceiver mailreceiver = new MailReceiver();
    mailreceiver.setDeleted(true);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(true);
    mailreceiver.setToname(marvin);
    mailreceiver.setNewmail(true);
    // all other props are ignored by the method under test

    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(MailReceiver.class, 1l);
        result = mailreceiver;
      }
    };
    // Unit under test is exercised.
    PrivateMail actual = privateBean.getMailInfo("Marvin", 1l);
  }

  @Test
  public void getMail()
  {
    LOGGER.fine("getMail");
    final Mail mail = new Mail();
    mail.setToname("marvin");
    mail.setName(hotblack);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.FALSE);
    MailReceiver mailreceiver = new MailReceiver();
    mailreceiver.setDeleted(false);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(false);
    mailreceiver.setToname(marvin);
    mailreceiver.setNewmail(true);
    // all other props are ignored by the method under test

    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(MailReceiver.class, 1l);
        result = mailreceiver;
      }
    };
    // Unit under test is exercised.
    PrivateMail actual = privateBean.getMailInfo("Marvin", 1l);
    // Verification code (JUnit/TestNG asserts), if any.
    assertNotNull(actual);
    PrivateMail expected = new PrivateMail();
    expected.body = "First mail";
    expected.subject = "Subject";
    expected.toname = "marvin";
    expected.name = "Hotblack";
    expected.deleted = false;
    expected.haveread = true;
    expected.newmail = true;
    compare(actual, expected);
  }

  // TODO : Fix this!
  //@Test
  public void createMailItem()
  {
    LOGGER.fine("createMailItem");
    final Admin admin = TestingConstants.getAdmin();
    final ItemDefinition itemDef = new ItemDefinition();
    itemDef.setId(8009L);
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
//    mail.setToname(marvin);
    mail.setName(hotblack);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.FALSE);
//    mail.setHaveread(Boolean.FALSE);
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
            assertEquals(newItemDef.getShortDescription(), itemDef.getShortDescription());
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
            // assertEquals(item.getCreation(), LocalDateTime.now());
            assertNull(item.getId());
            assertEquals(item.getItemDefinition().getId(), Integer.valueOf(6));
            assertEquals(item.getOwner(), admin);
          }
        };

      }
    };
    // Unit under test is exercised.
    Response response = privateBean.createMailItem("Marvin", 1l, 1);
    // Verification code (JUnit/TestNG asserts), if any.
    assertNotNull(response);
    assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
  }

  // TODO : Fix this!
  //@Test
  public void createSecondMailItem() throws MudException
  {
    LOGGER.fine("createSecondMailItem");
    final Admin admin = TestingConstants.getAdmin();
    final ItemDefinition itemDef = new ItemDefinition();
    itemDef.setId(12L);
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
//    mail.setToname(marvin);
    mail.setName(hotblack);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.FALSE);
//    mail.setHaveread(Boolean.FALSE);
//    mail.setItemDefinition(itemDef);
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
            // assertEquals(item.getCreation(), LocalDateTime.now());
            assertNull(item.getId());
            assertEquals(item.getItemDefinition().getId(), Integer.valueOf(12));
            assertEquals(item.getOwner(), admin);
          }
        };

      }
    };
    // Unit under test is exercised.
    Response response = privateBean.createMailItem("Marvin", 1l, 1);
    // Verification code (JUnit/TestNG asserts), if any.
    assertNotNull(response);
    assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
  }

  // TODO : Fix this!
  //@Test
  public void createMailItemError1() throws MudException
  {
    LOGGER.fine("createMailItemError1");
    final Mail mail = new Mail();
//    mail.setToname(marvin);
    mail.setName(hotblack);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.FALSE);
//    mail.setHaveread(Boolean.FALSE);
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
      Response response = privateBean.createMailItem("Marvin", 1l, -1);
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
    LOGGER.fine("createMailItemError1");
    final Mail mail = new Mail();
//    mail.setToname(marvin);
    mail.setName(hotblack);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.FALSE);
//    mail.setHaveread(Boolean.FALSE);
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
      Response response = privateBean.createMailItem("Marvin", 1l, 8);
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
    LOGGER.fine("deleteMail");
    final Mail mail = new Mail();
    mail.setId(1l);
    mail.setToname("marvin");
    mail.setName(hotblack);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.FALSE);
    MailReceiver mailreceiver = new MailReceiver();
    mailreceiver.setDeleted(false);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(false);
    mailreceiver.setToname(marvin);
    mailreceiver.setNewmail(true);
    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(MailReceiver.class, 1l);
        result = mailreceiver;
      }
    };
    responseOkExpectations();
    // Unit under test is exercised.
    Response response = privateBean.deleteMail("Marvin", 1l);
    // Verification code (JUnit/TestNG asserts), if any.
    assertEquals(mailreceiver.getDeleted(), Boolean.TRUE);
  }

  @Test
  public void deleteMailNotYours() throws MudException
  {
    LOGGER.fine("deleteMail");
    final Mail mail = new Mail();
    mail.setId(1l);
    mail.setToname("hotblack");
    mail.setName(marvin);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.FALSE);
    MailReceiver mailreceiver = new MailReceiver();
    mailreceiver.setDeleted(false);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(false);
    mailreceiver.setToname(hotblack);
    mailreceiver.setNewmail(true);
    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(MailReceiver.class, 1l);
        result = mailreceiver;
      }
    };

    // Unit under test is exercised.
    try
    {
      privateBean.deleteMail("Marvin", 1l);
      fail("Exception expected");
    } catch (MudWebException result)
    {
      // Yay! We get an exception!
    }// Verification code (JUnit/TestNG asserts), if any.
    assertEquals(mailreceiver.getDeleted(), Boolean.FALSE);
  }

  @Test(expectedExceptions = MudWebException.class)
  public void deleteMailNotFound() throws MudException
  {
    LOGGER.fine("deleteMail");
    PrivateBean privateBean = new PrivateBean()
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
    new Expectations() // an "expectation block"
    {

      {
        entityManager.find(User.class, "Marvin");
        result = marvin;
        entityManager.find(MailReceiver.class, 1L);
        result = null;
      }
    };
    // Unit under test is exercised.
    Response response = privateBean.deleteMail("Marvin", 1L);
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

}
