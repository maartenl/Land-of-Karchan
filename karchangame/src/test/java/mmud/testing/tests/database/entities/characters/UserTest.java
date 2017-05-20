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

import java.io.IOException;
import mmud.database.entities.characters.User;
import mmud.exceptions.MudException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class UserTest
{

  public UserTest()
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
  public void testSetNewPassword()
  {
    User person = new User();
    person.setNewpassword("itsasecret");
    assertThat(person.getNewpassword(), equalTo("d020e4cc6510709814379ba05d72c5a02659c9b2f64c13bab9459f767e1302ca021093a763cea539da0d4b5f0816a82ce017665b7ce6238d086311fc2f1974b2"));
  }

  @Test
  public void testSetNewPasswordTwice()
  {
    User person = new User();
    person.setNewpassword("itsasecret");
    person.setNewpassword("itsadifferentsecret");
    assertThat(person.getNewpassword(), equalTo("07e8193aec7c8c6f20e6482a103b7a66f932feb2845c03ba313e40bc10423cfb50c86ab8b05205fcbb077c454750131b6d8285fce59de468eb9779057bb4b45d"));
  }

  /**
   * Unable to clear password. Can only set it to a different value.
   */
  @Test
  public void testSetNewPasswordEmpty()
  {
    User person = new User();
    person.setNewpassword("itsasecret");
    person.setNewpassword(null);
    assertThat(person.getNewpassword(), equalTo("d020e4cc6510709814379ba05d72c5a02659c9b2f64c13bab9459f767e1302ca021093a763cea539da0d4b5f0816a82ce017665b7ce6238d086311fc2f1974b2"));
  }

  @Test(expectedExceptions = MudException.class, expectedExceptionsMessageRegExp = "value itsa should match regexp .{5,}")
  public void testSetNewPasswordBadly() throws IOException
  {
    User person = new User();
    person.setNewpassword("itsa");
  }
}
