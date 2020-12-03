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
import mmud.services.CommunicationService;
import org.testng.annotations.*;

import java.io.IOException;
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
}
