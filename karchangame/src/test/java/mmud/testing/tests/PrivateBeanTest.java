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

import java.util.logging.Logger;
import javax.persistence.EntityManager;

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Room;
import mmud.database.entities.web.CharacterInfo;
import mmud.database.entities.web.Family;
import mmud.database.entities.web.FamilyPK;
import mmud.database.entities.web.FamilyValue;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.PrivateBean;
import mmud.rest.webentities.PrivatePerson;
import mmud.testing.TestingConstants;
import org.mockito.invocation.InvocationOnMock;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
public class PrivateBeanTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(PrivateBeanTest.class.getName());

  private User hotblack;
  private User marvin;

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
    hotblack = TestingConstants.getHotblack(aRoom);
    marvin = TestingConstants.getMarvin(aRoom);
  }

  @AfterMethod
  public void tearDown()
  {
  }

  @Test
  public void updateCharacterSheet() throws MudException
  {
    LOGGER.fine("updateCharacterSheet");

    final CharacterInfo cinfo = new CharacterInfo();
    cinfo.setName("Marvin");
    cinfo.setImageurl("http://www.images.com/image.jpg");
    cinfo.setHomepageurl("http://www.homepages.com");
    cinfo.setDateofbirth("none");
    cinfo.setCityofbirth("none");
    cinfo.setStoryline("none");

    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(CharacterInfo.class, "Marvin")).thenReturn(cinfo);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);

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
    // Unit under test is exercised.
    final PrivatePerson person = new PrivatePerson();
    person.name = "Marvin";
    person.imageurl = "http://www.images.com/newimage.jpg";
    person.homepageurl = "http://www.homepages.com/homepage.html";
    person.dateofbirth = "Beginning of time";
    person.cityofbirth = "Sirius";
    person.storyline = "Life, don&#39;t talk to me about life.";
    privateBean.updateCharacterSheet("Marvin", person);
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
    LOGGER.fine("newCharacterSheet");
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(CharacterInfo.class, "Marvin")).thenReturn(null);

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
    // Unit under test is exercised.
    final PrivatePerson person = new PrivatePerson();
    person.name = "Marvin";
    person.imageurl = "http://www.images.com/newimage.jpg";
    person.homepageurl = "http://www.homepages.com/homepage.html";
    person.dateofbirth = "Beginning of time";
    person.cityofbirth = "Sirius";
    person.storyline = "Life, don't talk to me about life.";
    doAnswer((InvocationOnMock invocation) ->
    {
      CharacterInfo cinfo = (CharacterInfo) invocation.getArguments()[0];
      assertNotNull(cinfo);
      assertEquals(cinfo.getName(), "Marvin");
      assertEquals(cinfo.getImageurl(), "http://www.images.com/newimage.jpg");
      assertEquals(cinfo.getHomepageurl(), "http://www.homepages.com/homepage.html");
      assertEquals(cinfo.getDateofbirth(), "Beginning of time");
      assertEquals(cinfo.getCityofbirth(), "Sirius");
      assertEquals(cinfo.getStoryline(), "Life, don&#39;t talk to me about life.");
      return null;
    }).when(entityManager).persist(any(CharacterInfo.class));
    privateBean.updateCharacterSheet("Marvin", person);
    // Verification code (JUnit/TestNG asserts), if any.
  }

  @Test
  public void updateCharacterSheetScriptInjection() throws MudException
  {
    LOGGER.fine("updateCharacterSheetScriptInjection");
    final CharacterInfo cinfo = new CharacterInfo();
    cinfo.setName("Marvin");
    cinfo.setImageurl("http://www.images.com/image.jpg");
    cinfo.setHomepageurl("http://www.homepages.com");
    cinfo.setDateofbirth("none");
    cinfo.setCityofbirth("none");
    cinfo.setStoryline("none");

    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(CharacterInfo.class, "Marvin")).thenReturn(cinfo);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);

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
    final PrivatePerson person = new PrivatePerson();
    person.name = "Marvin";
    person.imageurl = "http://www.images.com/newimage.jpg";
    person.homepageurl = "http://www.homepages.com/homepage.html";
    person.dateofbirth = "Beginning of time";
    person.cityofbirth = "Sirius";
    person.storyline = "Life, don't talk to me about <script>alert('woaj');</script>life.";
    privateBean.updateCharacterSheet("Marvin", person);
    // Verification code (JUnit/TestNG asserts), if any.

    assertEquals(cinfo.getName(), person.name);
    assertEquals(cinfo.getImageurl(), person.imageurl);
    assertEquals(cinfo.getHomepageurl(), person.homepageurl);
    assertEquals(cinfo.getDateofbirth(), person.dateofbirth);
    assertEquals(cinfo.getCityofbirth(), person.cityofbirth);
    assertEquals(cinfo.getStoryline(), "Life, don&#39;t talk to me about life.");

  }

  @Test(expectedExceptions = MudWebException.class)
  public void updateCharacterSheetOfSomebodyElse() throws MudException
  {
    LOGGER.fine("updateCharacterSheetOfSomebodyElse");
    final CharacterInfo cinfo = new CharacterInfo();
    cinfo.setName("Marvin");
    cinfo.setImageurl("http://www.images.com/image.jpg");
    cinfo.setHomepageurl("http://www.homepages.com");
    cinfo.setDateofbirth("none");
    cinfo.setCityofbirth("none");
    cinfo.setStoryline("none");

    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);

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
    // Unit under test is exercised.
    final PrivatePerson person = new PrivatePerson();
    person.name = "Hotblack";
    person.imageurl = "http://www.images.com/newimage.jpg";
    person.homepageurl = "http://www.homepages.com/homepage.html";
    person.dateofbirth = "Beginning of time";
    person.cityofbirth = "Sirius";
    person.storyline = "Life, don't talk to me about <script>alert('woaj');</script>life.";
    privateBean.updateCharacterSheet("Marvin", person);
  }

  @Test
  public void updateFamilyvalues() throws MudException
  {
    LOGGER.fine("updateFamilyvalues");
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
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(Person.class, "Hotblack")).thenReturn(hotblack);
    when(entityManager.find(FamilyValue.class, 2)).thenReturn(value2);
    when(entityManager.find(eq(Family.class), any(FamilyPK.class))).thenReturn(family);
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
    // Unit under test is exercised.
    privateBean.updateFamilyvalues("Marvin", "Hotblack", 2);
    // Verification code (JUnit/TestNG asserts), if any.
    assertEquals(family.getDescription(), value2);
  }

  @Test
  public void updateFamilyvaluesNotFound() throws MudException
  {
    LOGGER.fine("updateFamilyvaluesNotFound");
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(Person.class, "Hotblack")).thenReturn(hotblack);
    when(entityManager.find(FamilyValue.class, 12)).thenReturn(null);
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
    assertThatThrownBy(() -> privateBean.updateFamilyvalues("Marvin", "Hotblack", 12))
      .isInstanceOf(MudWebException.class)
      .hasMessage("Family value 12 was not found.");
  }

  @Test
  public void newFamilyvalues() throws MudException
  {
    LOGGER.fine("newFamilyvalues");
    final FamilyValue value = new FamilyValue();
    value.setDescription("friend");
    value.setId(1);
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(Person.class, "Hotblack")).thenReturn(hotblack);
    when(entityManager.find(FamilyValue.class, 1)).thenReturn(value);
    when(entityManager.find(eq(Family.class), any(FamilyPK.class))).thenReturn(null);
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

    doAnswer((InvocationOnMock invocation) ->
    {
      Family fam = (Family) invocation.getArguments()[0];
      assertThat(fam).isNotNull();
      assertThat(fam.getDescription()).isEqualTo(value);
      assertThat(fam.getFamilyPK().getName()).isEqualTo( "Marvin");
      assertThat(fam.getFamilyPK().getToname()).isEqualTo( "Hotblack");
      return null;
    }).when(entityManager).persist(any(Family.class));
    // Unit under test is exercised.
    privateBean.updateFamilyvalues("Marvin", "Hotblack", 1);
    // Verification code (JUnit/TestNG asserts), if any.
  }

  @Test
  public void deleteFamilyvalues() throws MudException
  {
    LOGGER.fine("deleteFamilyvalues");
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
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManager.find(User.class, "Marvin")).thenReturn(marvin);
    when(entityManager.find(eq(Family.class), any(FamilyPK.class))).thenReturn(family);
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
    // Unit under test is exercised.
    privateBean.deleteFamilyvalues("Marvin", "Hotblack");
    // Verification code (JUnit/TestNG asserts), if any.
    verify(entityManager, times(1)).remove(any(Family.class));
  }

}
