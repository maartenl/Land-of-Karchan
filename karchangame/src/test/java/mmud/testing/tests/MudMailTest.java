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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Mail;
import mmud.database.entities.game.MailReceiver;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.PrivateRestService;
import mmud.rest.webentities.PrivateMail;
import mmud.services.PlayerAuthenticationService;
import mmud.services.PublicService;
import mmud.testing.TestingConstants;
import mmud.testing.TestingUtils;
import org.mockito.invocation.InvocationOnMock;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author maartenl
 */
public class MudMailTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(MudMailTest.class.getName());

  private User hotblack;
  private User marvin;

  private final PlayerAuthenticationService playerAuthenticationService = new PlayerAuthenticationService()
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
  @Test
  public void listMailAuthenticate2()
  {
    LOGGER.fine("listMailAuthenticate2");
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(null);
    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(new PlayerAuthenticationService()
    {
      {
        setEntityManager(entityManager);
      }

      @Override
      public String getPlayerName(SecurityContext context) throws IllegalStateException
      {
        return "Marvin";
      }

    });
    // Unit under test is exercised.
    assertThatThrownBy(() -> privateRestService.listMail("Marvin", null)).isInstanceOf(MudWebException.class)
      .hasMessage("User was not found  (Marvin)");
  }

  @Test
  public void listMailEmpty()
  {
    LOGGER.fine("listMailEmpty");
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);

    TypedQuery<MailReceiver> listMailQuery = mock(TypedQuery.class);
    when(entityManager.createNamedQuery("Mail.listmail", MailReceiver.class)).thenReturn(listMailQuery);
    when(listMailQuery.getResultList()).thenReturn(Collections.emptyList());

    TypedQuery nonewmailQuery = mock(TypedQuery.class);
    when(entityManager.createNamedQuery("Mail.nonewmail")).thenReturn(nonewmailQuery);
    when(nonewmailQuery.setParameter("name", marvin)).thenReturn(nonewmailQuery);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    List<PrivateMail> result = privateRestService.listMail("Marvin", null);
    verify(nonewmailQuery, times(1)).executeUpdate();
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
    mail.setId(1L);
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
    mail.setId(2L);
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

    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);

    TypedQuery listMailQuery = mock(TypedQuery.class);
    when(entityManager.createNamedQuery("Mail.listmail", MailReceiver.class)).thenReturn(listMailQuery);
    when(listMailQuery.getResultList()).thenReturn(list);

    TypedQuery nonewmailQuery = mock(TypedQuery.class);
    when(entityManager.createNamedQuery("Mail.nonewmail")).thenReturn(nonewmailQuery);
    when(nonewmailQuery.setParameter("name", marvin)).thenReturn(nonewmailQuery);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);

    // Unit under test is exercised.
    List<PrivateMail> result = privateRestService.listMail("Marvin", null);
    // Verification code (JUnit/TestNG asserts), if any.
    verify(nonewmailQuery, times(1)).executeUpdate();
    assertEquals(result.size(), 2);
    PrivateMail expected = new PrivateMail();
    expected.body = "First mail";
    expected.subject = "Subject";
    expected.haveread = false;
    expected.id = 5L;
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
    expected.id = 6L;
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

    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(User.class, "Hotblack")).thenReturn(hotblack);
    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);

    // Unit under test is exercised.
    doAnswer((InvocationOnMock invocation) ->
    {
      Mail mail = (Mail) invocation.getArguments()[0];
      assertThat(mail).isNotNull();
      assertThat(mail.getBody()).isEqualTo("First mail");
      assertThat(mail.getSubject()).isEqualTo("Subject");
      assertThat(mail.getId()).isNull();
      assertThat(mail.getName()).isEqualTo(marvin);
      assertThat(mail.getToname()).isEqualTo("Hotblack");
      // unable to assertEquals getWhensent. As it is using the current date/time.
      assertThat(mail.getDeleted()).isFalse();
      return null;
    }).when(entityManager).persist(any(Mail.class));
    doAnswer((InvocationOnMock invocation) ->
    {
      MailReceiver mail = (MailReceiver) invocation.getArguments()[0];
      assertThat(mail).isNotNull();
      assertThat(mail.getHaveread()).isFalse();
      assertThat(mail.getId()).isNull();

      assertThat(mail.getNewmail()).isTrue();
      assertThat(mail.getToname()).isEqualTo(hotblack);
      // unable to assertEquals getWhensent. As it is using the current date/time.
      assertThat(mail.getDeleted()).isFalse();
      return null;
    }).when(entityManager).persist(any(MailReceiver.class));
    privateRestService.newMail(privateMail, "Marvin");
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

    PublicService publicService = new PublicService()
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
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);

    // all other props are ignored by the method under test
    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

      @Override
      public PublicService getPublicService()
      {
        return publicService;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.

    List<Object> persisted = new ArrayList<>();
    doAnswer((InvocationOnMock invocation) ->
    {
      Object mail = invocation.getArguments()[0];
      persisted.add(mail);
      return null;
    }).when(entityManager).persist(any(Object.class));

    privateRestService.newMail(privateMail, "Marvin");
    // Verification code (JUnit/TestNG asserts), if any.
    assertThat(persisted).hasSize(4);
    assertThat(persisted.get(0)).isInstanceOf(Mail.class);
    Mail mail = (Mail) persisted.get(0);
    assertThat(mail).isNotNull();
    assertThat(mail.getBody()).isEqualTo("First mail");
    assertThat(mail.getSubject()).isEqualTo("Subject");
    assertThat(mail.getId()).isNull();

    assertThat(mail.getName()).isEqualTo(marvin);
    // unable to assertEquals getWhensent. As it is using the current date/time.
    assertThat(mail.getDeleted()).isFalse();
    assertMailReceiver(persisted.get(1), "Karn");
    assertMailReceiver(persisted.get(2), "Mya");
    assertMailReceiver(persisted.get(3), "Ephinie");

  }

  private void assertMailReceiver(Object o, String name)
  {
    assertThat(o)
      .isInstanceOf(MailReceiver.class);
    MailReceiver mailreceiver = (MailReceiver) o;
    assertThat(mailreceiver.getHaveread()).isFalse();
    assertThat(mailreceiver.getId()).isNull();
    assertThat(mailreceiver.getNewmail()).isTrue();
    assertThat(mailreceiver.getToname().getName()).isEqualTo(name);
    // unable to assertEquals getWhensent. As it is using the current date/time.
    assertThat(mailreceiver.getDeleted()).isFalse();
  }

  @Test
  public void newMailToUnknownUser()
  {
    LOGGER.fine("newMailToUnknownUser");
    PrivateMail privateMail = new PrivateMail();
    privateMail.body = "First mail";
    privateMail.subject = "Subject";
    privateMail.toname = "Unknown";
    // all other props are ignored by the method under test
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    assertThatThrownBy(() -> privateRestService.newMail(privateMail, "Marvin"))
      .isInstanceOf(MudWebException.class)
      .hasMessage("User was not found (Unknown)");
  }

  @Test
  public void getMailDoesnotExist()
  {
    LOGGER.fine("getMailDoesnotExist");
    // all other props are ignored by the method under test
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    assertThatThrownBy(() -> privateRestService.getMailInfo("Marvin", 1L))
      .isInstanceOf(MudWebException.class)
      .hasMessage("Mail 1 not found.");
  }

  @Test
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
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(mailreceiver);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    assertThatThrownBy(() -> privateRestService.getMailInfo("Marvin", 1L))
      .isInstanceOf(MudWebException.class)
      .hasMessage("Mail with id 1 was not for Marvin.");
  }

  @Test
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
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(mailreceiver);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    assertThatThrownBy(() -> privateRestService.getMailInfo("Marvin", 1L))
      .isInstanceOf(MudWebException.class)
      .hasMessage("Mail with id 1 was deleted.");
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
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(mailreceiver);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    PrivateMail actual = privateRestService.getMailInfo("Marvin", 1L);
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

  @Test
  public void createMailItem()
  {
    LOGGER.fine("createMailItem");
    final Admin admin = TestingConstants.getAdmin();
    final ItemDefinition itemDef = new ItemDefinition();
    itemDef.setId(8009L);
    itemDef.setReaddescription("this is letterhead.</p><p>letterbody</p><p>letterfoot</p>");
    itemDef.setName("paper");
    itemDef.setAdjectives("small piece of");
    itemDef.setGetable(true);
    itemDef.setDropable(true);
    itemDef.setVisible(true);

    itemDef.setCopper(1);
    itemDef.setOwner(admin);
    itemDef.setNotes("Some notes");

    final Mail mail = new Mail();
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

    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(mailreceiver);
    when(entityManager.find(ItemDefinition.class, 8009L)).thenReturn(itemDef);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

      @Override
      protected Response createResponse()
      {
        return null;
      }
    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    LogServiceStub logBean = new LogServiceStub();
    privateRestService.setLogService(logBean);
    // Unit under test is exercised.
    privateRestService.createMailItem("Marvin", 1L, 1);
    // Verification code (JUnit/TestNG asserts), if any.
    assertThat(marvin.getItems()).hasSize(1);
    Item item = marvin.getItems().stream().findFirst().orElseThrow(() -> new AssertionError("Expected at least one"));
    assertThat(item).isNotNull();
    assertThat(item.getId()).isNull();
    assertThat(item.getBelongsTo()).isEqualTo(marvin);
    assertThat(item.getRoom()).isNull();
    assertThat(item.getContainer()).isNull();
    assertThat(item.getItems()).as("It should not contain items.").isEmpty();
    assertThat(item.getItemDefinition()).isEqualTo(itemDef);
    assertThat(logBean.getLog()).isEqualTo("Marvin:item small piece of paper from mail 1 created.\n");
  }

  @Test
  public void createSecondMailItem() throws MudException
  {
    LOGGER.fine("createSecondMailItem");
    final Admin admin = TestingConstants.getAdmin();
    final ItemDefinition itemDef = new ItemDefinition();
    itemDef.setId(12L);
    itemDef.setReaddescription("Dear people,</p><p>Blahblah</p><p>Regards, Karn.</p>");
    itemDef.setName("paper");
    itemDef.setAdjectives("small piece of");
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

    MailReceiver mailreceiver = new MailReceiver();
    mailreceiver.setDeleted(false);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(false);
    mailreceiver.setToname(marvin);
    mailreceiver.setNewmail(true);

    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(mailreceiver);
    when(entityManager.find(ItemDefinition.class, 8009L)).thenReturn(itemDef);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

      @Override
      protected Response createResponse()
      {
        return null;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    LogServiceStub logBean = new LogServiceStub();
    privateRestService.setLogService(logBean);
    // Unit under test is exercised.
    privateRestService.createMailItem("Marvin", 1L, 1);
    // Verification code (JUnit/TestNG asserts), if any.
    assertThat(logBean.getLog()).isEqualTo("Marvin:item small piece of paper from mail 1 created.\n");
  }

  /**
   * An illegal itemdefinition number (-1) is requested by the user.
   */
  @Test
  public void createMailItemError1()
  {
    LOGGER.fine("createMailItemError1");
    final Mail mail = new Mail();
//    mail.setToname(marvin);
    mail.setName(hotblack);
    mail.setBody("First mail");
    mail.setSubject("Subject");
    mail.setDeleted(Boolean.FALSE);
//    mail.setHaveread(Boolean.FALSE);
    MailReceiver mailreceiver = new MailReceiver();
    mailreceiver.setDeleted(false);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(false);
    mailreceiver.setToname(marvin);
    mailreceiver.setNewmail(true);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(mailreceiver);
    when(entityManager.find(ItemDefinition.class, 8009L)).thenReturn(null);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    assertThatThrownBy(() -> privateRestService.createMailItem("Marvin", 1L, -1))
      .isInstanceOf(MudWebException.class)
      .hasMessage("mmud.exceptions.MudWebException: Could not create item from mail.");
    // Verification code (JUnit/TestNG asserts), if any.
  }

  @Test
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
    MailReceiver mailreceiver = new MailReceiver();
    mailreceiver.setDeleted(false);
    mailreceiver.setMail(mail);
    mailreceiver.setHaveread(false);
    mailreceiver.setToname(marvin);
    mailreceiver.setNewmail(true);

    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(mailreceiver);

    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

      @Override
      protected Response createResponse()
      {
        return null;
      }
    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    assertThatThrownBy(() -> privateRestService.createMailItem("Marvin", 1L, 8))
      .isInstanceOf(MudWebException.class)
      .hasMessage("mmud.exceptions.MudWebException: Could not create item from mail.");
    // Verification code (JUnit/TestNG asserts), if any.
  }

  @Test
  public void deleteMail() throws MudException
  {
    LOGGER.fine("deleteMail");
    final Mail mail = new Mail();
    mail.setId(1L);
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
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(mailreceiver);
    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

      @Override
      protected Response createResponse()
      {
        return null;
      }
    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    LogServiceStub logBean = new LogServiceStub();
    privateRestService.setLogService(logBean);
    // Unit under test is exercised.
    privateRestService.deleteMail("Marvin", 1L);
    // Verification code (JUnit/TestNG asserts), if any.
    assertEquals(mailreceiver.getDeleted(), Boolean.TRUE);
    assertThat(logBean.getLog()).isEqualTo("Marvin:mail 1 deleted.\n");
  }

  @Test
  public void deleteMailNotYours() throws MudException
  {
    LOGGER.fine("deleteMail");
    final Mail mail = new Mail();
    mail.setId(1L);
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
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(mailreceiver);
    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }


    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    assertThatThrownBy(() -> privateRestService.deleteMail("Marvin", 1L))
      .isInstanceOf(MudWebException.class)
      .hasMessage("Mail with id 1 was not for Marvin.");
    // Verification code (JUnit/TestNG asserts), if any.
    assertEquals(mailreceiver.getDeleted(), Boolean.FALSE);
  }

  @Test
  public void deleteMailNotFound() throws MudException
  {
    LOGGER.fine("deleteMail");
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(MailReceiver.class, 1L)).thenReturn(null);
    PrivateRestService privateRestService = new PrivateRestService()
    {
      @Override
      protected EntityManager getEntityManager()
      {
        return entityManager;
      }

    };
    privateRestService.setPlayerAuthenticationService(playerAuthenticationService);
    // Unit under test is exercised.
    assertThatThrownBy(() -> privateRestService.deleteMail("Marvin", 1L))
      .isInstanceOf(MudWebException.class)
      .hasMessage("Mail 1 not found.");
  }

}
