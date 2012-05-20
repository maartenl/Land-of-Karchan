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

import mmud.database.enums.Appetite;
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
public class AppetiteTest
{

    public AppetiteTest()
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

    @DataProvider(name = "appetiteRanges")
    public Object[][] createData1()
    {
        return new Object[][]
                {
                    {
                        Appetite.HUNGRY, 0, Integer.valueOf(0), Integer.valueOf(999)
                    },
                    {
                        Appetite.EAT_WHOLE_LOT_MORE, 1, Integer.valueOf(1000), Integer.valueOf(1999)
                    },
                    {
                        Appetite.EAT_LOT_MORE, 2, Integer.valueOf(2000), Integer.valueOf(2999)
                    },
                    {
                        Appetite.EAT_SOME, 3, Integer.valueOf(3000), Integer.valueOf(3999)
                    },
                    {
                        Appetite.EAT_LITTLE, 4, Integer.valueOf(4000), Integer.valueOf(4999)
                    },
                    {
                        Appetite.FULL, 5, Integer.valueOf(5000), Integer.valueOf(5999)
                    }
                };
    }

    @Test(dataProvider = "appetiteRanges")
    public void rangesTest(Appetite appetite, Integer ordinal, Integer n1, Integer n2)
    {
        for (int i = n1; i <= n2; i++)
        {
            assertEquals(ordinal, Integer.valueOf(appetite.getOrdinalValue()), "ordinalValue didn't match");
            assertEquals(Integer.valueOf(i / 1000), ordinal, "positive number(" + i + ") did not translate to ordinal properly");
            assertEquals(Appetite.getAppetite(i), appetite, "getAppetite(" + i + ") didn't return proper Appetite");
        }
    }

    private void test(Appetite appetite, int i, String s)
    {
        assertNotNull(appetite);
        assertEquals(appetite.getOrdinalValue(), i);
        assertEquals(appetite.getDescription(), s);
    }

    @Test
    public void defaultTest()
    {
        test(Appetite.HUNGRY, 0, "You are hungry.");
    }

    @Test
    public void exceptionMinTest()
    {
        assertEquals(Appetite.min(), Integer.valueOf(0));
        assertEquals(Appetite.getAppetite(Appetite.min()), Appetite.HUNGRY);
        try
        {
            Appetite.getAppetite(-1);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Appetite -1 not allowed!");
        }
    }

    @Test
    public void exceptionMaxTest()
    {
        assertEquals(Appetite.max(), Integer.valueOf(5999));
        assertEquals(Appetite.getAppetite(Appetite.max()), Appetite.FULL);
        try
        {
            Appetite.getAppetite(6000);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Appetite 6000 not allowed!");
        }
    }
}
