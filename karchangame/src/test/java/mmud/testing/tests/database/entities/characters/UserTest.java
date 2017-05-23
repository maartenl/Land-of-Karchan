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
    person.setNewpassword("secret");
    assertThat(person.getNewpassword(), equalTo("bd2b1aaf7ef4f09be9f52ce2d8d599674d81aa9d6a4421696dc4d93dd0619d682ce56b4d64a9ef097761ced99e0f67265b5f76085e5b0ee7ca4696b2ad6fe2b2"));
  }

  @Test
  public void testSetNewPasswordTwice()
  {
    User person = new User();
    person.setNewpassword("secret");
    person.setNewpassword("differentsecret");
    assertThat(person.getNewpassword(), equalTo("dc6dac17e09f0cc84bfc54b09edcb2329bb7ea43927eb96fe2c279730537d56e91267bae81cbe28ae5df0ec589dd2d5fcfdbd0db4ff4248a27ec294f2074f063"));
  }

  /**
   * Unable to clear password. Can only set it to a different value.
   */
  @Test
  public void testSetNewPasswordEmpty()
  {
    User person = new User();
    person.setNewpassword("secret");
    person.setNewpassword(null);
    assertThat(person.getNewpassword(), equalTo("bd2b1aaf7ef4f09be9f52ce2d8d599674d81aa9d6a4421696dc4d93dd0619d682ce56b4d64a9ef097761ced99e0f67265b5f76085e5b0ee7ca4696b2ad6fe2b2"));
  }

  @Test(expectedExceptions = MudException.class, expectedExceptionsMessageRegExp = "value itsa should match regexp .{5,}")
  public void testSetNewPasswordBadly() throws IOException
  {
    User person = new User();
    person.setNewpassword("itsa");
  }

  /**
   * If you try to set a 128 character value in the new password, the method
   * will interpret this as a hash, and not re-hash it.
   * This to prevent a setter from being called many times (because of, for example,
   * value change listeners or ORMs).
   */
  // @Test
  public void testSetNewPassword128characters()
  {
    User person = new User();
    person.setNewpassword("woahnelly");
    person.setNewpassword("bd2b1aaf7ef4f09be9f52ce2d8d599674d81aa9d6a4421696dc4d93dd0619d682ce56b4d64a9ef097761ced99e0f67265b5f76085e5b0ee7ca4696b2ad6fe2b2");
    assertThat(person.getNewpassword(), equalTo("bd2b1aaf7ef4f09be9f52ce2d8d599674d81aa9d6a4421696dc4d93dd0619d682ce56b4d64a9ef097761ced99e0f67265b5f76085e5b0ee7ca4696b2ad6fe2b2"));
  }
}
