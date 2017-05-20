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

import java.io.FileWriter;
import java.io.IOException;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.exceptions.MudException;
import mockit.Delegate;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
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
public class PersonTest
{

    @Mocked
    FileWriter fileWriter;

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
    public void writeMessageTest() throws IOException
    {
        Person person = new User();
        new NonStrictExpectations() // an "expectation block"
        {


            {
                fileWriter.write((String) any, 0, 13);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    public void write(String s, int off, int len)
                    {
                        assertNotNull(s);
                        assertEquals(off, 0);
                        assertEquals(s.length(), len);
                        assertEquals(s, "Hello, world!");
                    }
                };
                times = 1;
                fileWriter.close();
                times = 1;
            }
        };
        // Unit under test is exercised.
        try
        {
            person.writeMessage("Hello, world!");
        } catch (MudException ex)
        {
            fail("unexpected exception", ex);
        }
    }

    @Test
    public void writeStrangeMessageTest() throws IOException
    {
        Person person = new User();
        new NonStrictExpectations() // an "expectation block"
        {


            {
                fileWriter.write((String) any, 0, 13);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    public void write(String s, int off, int len)
                    {
                        assertNotNull(s);
                        assertEquals(off, 0);
                        assertEquals(s.length(), len);
                        assertEquals(s, "Hello, world!");
                    }
                };
                times = 1;
                fileWriter.close();
                times = 1;
            }
        };
        // Unit under test is exercised.
        try
        {
            person.writeMessage("Hello,<try me> world!");
        } catch (MudException ex)
        {
            fail("unexpected exception", ex);
        }

    }

    @Test
    public void writeStrangeMessageManyTagsTest() throws IOException
    {
        Person person = new User();
        new NonStrictExpectations() // an "expectation block"
        {


            {
                fileWriter.write((String) any, 0, 13);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    public void write(String s, int off, int len)
                    {
                        assertNotNull(s);
                        assertEquals(off, 0);
                        assertEquals(s.length(), len);
                        assertEquals(s, "Hello, world!");
                    }
                };
                times = 1;
                fileWriter.close();
                times = 1;
            }
        };
        // Unit under test is exercised.
        try
        {
            person.writeMessage("Hello,<try me> wor<or>ld<this too>!");
        } catch (MudException ex)
        {
            fail("unexpected exception", ex);
        }

    }

    @Test
    public void writeStrangeOpenTagMessageTest() throws IOException
    {
        Person person = new User();
        new NonStrictExpectations() // an "expectation block"
        {


            {
                fileWriter.write((String) any, 0, 6);
                result = new Delegate()
                {
                    // The name of this method can actually be anything.
                    public void write(String s, int off, int len)
                    {
                        assertNotNull(s);
                        assertEquals(off, 0);
                        assertEquals(s.length(), len);
                        assertEquals(s, "Hello,");
                    }
                };
                times = 1;
                fileWriter.close();
                times = 1;
            }
        };
        // Unit under test is exercised.
        try
        {
            person.writeMessage("hello,<try me world!");
        } catch (MudException ex)
        {
            fail("unexpected exception", ex);
        }

    }
}
