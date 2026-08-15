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

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.NormalItem;
import mmud.database.enums.Wielding;
import mmud.exceptions.ItemException;
import mmud.services.CommunicationService;
import mmud.testing.TestingConstants;
import org.testng.annotations.*;

import java.util.function.Consumer;

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

  @Test
  public void testGetLevel()
  {
    Person person = new User();
    person.setName("Marvin");
    assertThat(person.getExperience()).isEqualTo(0L);
    assertThat(person.getLevel()).isEqualTo(1);

    person.addExperience(49);
    assertThat(person.getExperience()).isEqualTo(49L);
    assertThat(person.getLevel()).isEqualTo(1);

    person.addExperience(1);
    assertThat(person.getExperience()).isEqualTo(0L);
    assertThat(person.getLevel()).isEqualTo(2);

    person.addExperience(500);
    assertThat(person.getExperience()).isEqualTo(117);
    assertThat(person.getLevel()).isEqualTo(5);
  }

  @Test
  public void testGetStatistics()
  {
    Person person = new User();
    person.setName("Marvin");
    person.setRace("human");
    assertThat(person.getStatistics().split("\n")).contains(
      "    A male human who calls himself Marvin.<br/>",
      "    You seem to be feeling very well.<br/>",
      "    You are fully exhausted.<br/>",
      "    You are thirsty.<br/>",
      "    You are hungry.<br/>",
      "    You are neutral.<br/>",
      "    You have accumulated 0 experience points.",
      "    You are level 1 and 50 experience points away from levelling.<br/>",
      "    You will flee when you are at death's door.<br/>");
  }
}
