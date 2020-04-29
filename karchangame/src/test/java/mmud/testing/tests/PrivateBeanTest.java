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
import mmud.database.entities.web.CharacterInfo;
import mmud.database.entities.web.Family;
import mmud.database.entities.web.FamilyPK;
import mmud.database.entities.web.FamilyValue;
import mmud.exceptions.ErrorDetails;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.services.PrivateBean;
import mmud.rest.services.PublicBean;
import mmud.rest.webentities.PrivateMail;
import mmud.rest.webentities.PrivatePerson;
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
public class PrivateBeanTest
{

  // Obtain a suitable LOGGER.
  private static final Logger LOGGER = Logger.getLogger(PrivateBeanTest.class.getName());

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
    person.storyline = "Life, don&#39;t talk to me about life.";
    Response response = privateBean.updateCharacterSheet("Marvin", person);
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
            assertEquals(cinfo.getStoryline(), "Life, don&#39;t talk to me about life.");
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
    Response response = privateBean.updateCharacterSheet("Marvin", person);
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
    Response response = privateBean.updateCharacterSheet("Marvin", person);
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
        entityManager.find(Person.class, "Hotblack");
        result = hotblack;
        entityManager.find(FamilyValue.class, 2);
        result = value2;
        entityManager.find(Family.class, (FamilyPK) any);
        result = family;
      }
    };
    responseOkExpectations();        // Unit under test is exercised.
    Response response = privateBean.updateFamilyvalues("Marvin", "Hotblack", 2);
    // Verification code (JUnit/TestNG asserts), if any.
    assertEquals(family.getDescription(), value2);

  }

  @Test(expectedExceptions = MudWebException.class)
  public void updateFamilyvaluesNotFound() throws MudException
  {
    LOGGER.fine("updateFamilyvaluesNotFound");
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
        entityManager.find(Person.class, "Hotblack");
        result = hotblack;
        entityManager.find(FamilyValue.class, 12);
        result = null;
      }
    };
    Response response = privateBean.updateFamilyvalues("Marvin", "Hotblack", 12);
  }

  @Test
  public void newFamilyvalues() throws MudException
  {
    LOGGER.fine("newFamilyvalues");
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
    Response response = privateBean.updateFamilyvalues("Marvin", "Hotblack", 1);
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
        entityManager.find(Family.class, (FamilyPK) any);
        result = family;
        entityManager.remove(family);
      }
    };
    responseOkExpectations();
    // Unit under test is exercised.
    privateBean.deleteFamilyvalues("Marvin", "Hotblack");
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
