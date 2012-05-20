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
package mmud.testing.tests.enums;

import mmud.database.enums.Movement;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class MovementTest
{

    public MovementTest()
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

    @DataProvider(name = "movementRanges")
    public Object[][] createData1()
    {
        return new Object[][]
                {
                    {
                        Movement.FULLY_EXHAUSTED, 0, Integer.valueOf(0), Integer.valueOf(999)
                    },
                    {
                        Movement.ALMOST_EXHAUSTED, 1, Integer.valueOf(1000), Integer.valueOf(1999)
                    },
                    {
                        Movement.VERY_TIRED, 2, Integer.valueOf(2000), Integer.valueOf(2999)
                    },
                    {
                        Movement.SLIGHTLY_TIRED, 3, Integer.valueOf(3000), Integer.valueOf(3999)
                    },
                    {
                        Movement.SLIGHTLY_FATIGUED, 4, Integer.valueOf(4000), Integer.valueOf(4999)
                    },
                    {
                        Movement.NOT_TIRED_AT_ALL, 5, Integer.valueOf(5000), Integer.valueOf(5999)
                    }
                };
    }

    @Test(dataProvider = "movementRanges")
    public void rangesTest(Movement movement, Integer ordinal, Integer n1, Integer n2)
    {
        for (int i = n1; i <= n2; i++)
        {
            assertEquals(ordinal, Integer.valueOf(movement.getOrdinalValue()), "ordinalValue didn't match");
            assertEquals(Integer.valueOf(i / 1000), ordinal, "positive number(" + i + ") did not translate to ordinal properly");
            assertEquals(Movement.getMovement(i), movement, "getMovement(" + i + ") didn't return proper Movement");
        }
    }

    private void test(Movement movement, int i, String s)
    {
        assertNotNull(movement);
        assertEquals(movement.getOrdinalValue(), i);
        assertEquals(movement.getDescription(), s);
    }

    @Test
    public void defaultTest()
    {
        test(Movement.FULLY_EXHAUSTED, 0, "fully exhausted");
    }

    @Test
    public void exceptionMinTest()
    {
        assertEquals(Movement.min(), Integer.valueOf(0));
        assertEquals(Movement.getMovement(Movement.min()), Movement.FULLY_EXHAUSTED);
        try
        {
            Movement.getMovement(-1);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Movement -1 not allowed!");
        }
    }

    @Test
    public void exceptionMaxTest()
    {
        assertEquals(Movement.max(), Integer.valueOf(5999));
        assertEquals(Movement.getMovement(Movement.max()), Movement.NOT_TIRED_AT_ALL);
        try
        {
            Movement.getMovement(6000);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Movement 6000 not allowed!");
        }
    }
}
