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
package mmud.testing.tests.database.entities.characters;

import java.util.function.Consumer;

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.NormalItem;
import mmud.database.enums.Wielding;
import mmud.exceptions.ItemException;
import mmud.services.CommunicationService;
import mmud.testing.TestingConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author maartenl
 */
public class PersonTest
{

  public PersonTest()
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
  public void writeMessageTest()
  {
    Person person = new User();
    person.setName("Marvin");
    // Unit under test is exercised.
    Consumer<String> consumer = s -> assertThat(s).isEqualTo("Marvin:Hello, world!");
    CommunicationService.getCommunicationService(person, consumer).writeMessage("Hello, world!");
  }

  @Test
  public void writeStrangeMessageTest()
  {
    Person person = new User();
    person.setName("Marvin");
    // Unit under test is exercised.
    Consumer<String> consumer = s -> assertThat(s).isEqualTo("Marvin:Hello, world!");
    CommunicationService.getCommunicationService(person, consumer).writeMessage("Hello,<try me> world!");
  }

  @Test
  public void writeStrangeMessageManyTagsTest()
  {
    Person person = new User();
    person.setName("Marvin");
    // Unit under test is exercised.
    Consumer<String> consumer = s -> assertThat(s).isEqualTo("Marvin:Hello, world!");
    CommunicationService.getCommunicationService(person, consumer).writeMessage("Hello,<try me> wor<or>ld<this too>!");
  }

  @Test
  public void writeStrangeOpenTagMessageTest()
  {
    Person person = new User();
    person.setName("Marvin");
    // Unit under test is exercised.
    Consumer<String> consumer = s -> assertThat(s).isEqualTo("Marvin:Hello,");
    CommunicationService.getCommunicationService(person, consumer).writeMessage("hello,<try me world!");
  }

  @Test
  public void testMinimalGetLongDescription()
  {
    Person person = new User();
    person.setName("Marvin");
    person.setRace("human");
    // Unit under test is exercised.
    assertThat(person.getLongDescription()).isEqualTo("male human who calls himself Marvin.");
  }

  @Test
  public void testGetLongDescription()
  {
    Person person = new User();
    person.setName("Marvin");
    person.setRace("human");
    person.setAge("old");
    person.setHeight("tall");
    person.setWidth("skinny");
    person.setComplexion("dark-skinned");
    person.setEyes("yellow-eyed");
    person.setFace("handsome");
    person.setHair("white-haired");
    person.setBeard("with a pony tail");
    person.setArm("nice-armed");
    person.setLeg("nice-legged");
    person.setTitle("The awesome one");
    // Unit under test is exercised.
    assertThat(person.getLongDescription()).isEqualTo("old, tall, skinny, dark-skinned, yellow-eyed, handsome, white-haired, with a pony tail, nice-armed, nice-legged, male human who calls himself Marvin (The awesome one).");
  }

  @Test(expectedExceptions = ItemException.class, expectedExceptionsMessageRegExp = "You cannot wield that item there.")
  public void testCannotWieldIsWielding()
  {
    Person person = new User();
    person.setName("Marvin");
    var itemDefinition = TestingConstants.getPick();

    Item item = new NormalItem();
    item.setItemDefinition(itemDefinition);

    person.wield(item, Wielding.WIELD_RIGHT);
  }

  @Test(expectedExceptions = ItemException.class, expectedExceptionsMessageRegExp = "You do not have that item.")
  public void testNoHaveItemIsWielding()
  {
    Person person = new User();
    person.setName("Marvin");
    var itemDefinition = TestingConstants.getPick();

    Item item = new NormalItem();
    item.setItemDefinition(itemDefinition);

    person.wield(item, Wielding.WIELD_BOTH);
    assertThat(person.isWielding(item)).isTrue();
  }

  @Test
  public void testIsWielding()
  {
    Person person = new User();
    person.setName("Marvin");
    var itemDefinition = TestingConstants.getPick();

    Item item = new NormalItem();
    item.setItemDefinition(itemDefinition);
    person.addItem(item);

    person.wield(item, Wielding.WIELD_BOTH);
    assertThat(person.isWielding(item)).isTrue();
    assertThat(person.isWielding(null)).isFalse();
  }

  @Test
  public void testIsRiding()
  {
    Person person = new User();
    person.setName("Marvin");
    var itemDefinition = TestingConstants.getHorse();

    Item item = new NormalItem();
    item.setItemDefinition(itemDefinition);
    person.addItem(item);

    person.wield(item, Wielding.RIDING);
    assertThat(person.isWielding(item)).isTrue();
    assertThat(person.isWielding(null)).isFalse();
  }

  @Test
  public void testIsLeading()
  {
    Person person = new User();
    person.setName("Marvin");
    var itemDefinition = TestingConstants.getDog();

    Item item = new NormalItem();
    item.setItemDefinition(itemDefinition);
    person.addItem(item);

    person.wield(item, Wielding.LEADING);
    assertThat(person.isWielding(item)).isTrue();
    assertThat(person.isWielding(null)).isFalse();
  }
}
