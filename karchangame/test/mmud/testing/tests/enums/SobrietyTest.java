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

import mmud.database.enums.Sobriety;
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
public class SobrietyTest
{

    public SobrietyTest()
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

    @DataProvider(name = "sobrietyRanges")
    public Object[][] createData1()
    {
        return new Object[][]
                {
                    {
                        Sobriety.TOTALLY_DRUNK, -7, Integer.valueOf(-6999), Integer.valueOf(-6000)
                    },
                    {
                        Sobriety.VERY_DRUNK, -6, Integer.valueOf(-5999), Integer.valueOf(-5000)
                    },
                    {
                        Sobriety.DRUNK, -5, Integer.valueOf(-4999), Integer.valueOf(-4000)
                    },
                    {
                        Sobriety.PISSED, -4, Integer.valueOf(-3999), Integer.valueOf(-3000)
                    },
                    {
                        Sobriety.LITTLE_DRUNK, -3, Integer.valueOf(-2999), Integer.valueOf(-2000)
                    },
                    {
                        Sobriety.INEBRIATED, -2, Integer.valueOf(-1999), Integer.valueOf(-1000)
                    },
                    {
                        Sobriety.HEADACHE, -1, Integer.valueOf(-999), Integer.valueOf(-1)
                    },
                    {
                        Sobriety.THIRSTY, 0, Integer.valueOf(0), Integer.valueOf(999)
                    },
                    {
                        Sobriety.DRINK_WHOLE_LOT_MORE, 1, Integer.valueOf(1000), Integer.valueOf(1999)
                    },
                    {
                        Sobriety.DRINK_MORE, 2, Integer.valueOf(2000), Integer.valueOf(2999)
                    },
                    {
                        Sobriety.DRINK_COME, 3, Integer.valueOf(3000), Integer.valueOf(3999)
                    },
                    {
                        Sobriety.DRINK_LITTLE, 4, Integer.valueOf(4000), Integer.valueOf(4999)
                    },
                    {
                        Sobriety.NO_DRINK, 5, Integer.valueOf(5000), Integer.valueOf(5999)
                    }
                };
    }

    @Test(dataProvider = "sobrietyRanges")
    public void rangesTest(Sobriety sobr, Integer ordinal, Integer n1, Integer n2)
    {
        for (int i = n1; i <= n2; i++)
        {
            assertEquals(ordinal, Integer.valueOf(sobr.getOrdinalValue()), "ordinalValue didn't match");
            if (i < 0)
            {
                assertEquals(Integer.valueOf(i / 1000 - 1), ordinal, "negative number(" + i + ") did not translate to ordinal properly");
            } else
            {
                assertEquals(Integer.valueOf(i / 1000), ordinal, "positive number(" + i + ") did not translate to ordinal properly");
            }
            assertEquals(Sobriety.getSobriety(i), sobr, "getSobriety(" + i + ") didn't return proper Sobriety");
        }
    }

    private void test(Sobriety sobr, int i, String s)
    {
        assertNotNull(sobr);
        assertEquals(sobr.getOrdinalValue(), i);
        assertEquals(sobr.getDescription(), s);
    }

    @Test
    public void defaultTest()
    {
        test(Sobriety.TOTALLY_DRUNK, -7, "You are out of your skull on alcohol.");
    }

    @Test
    public void exceptionMinTest()
    {
        assertEquals(Sobriety.min(), Integer.valueOf(-7999));
        assertEquals(Sobriety.getSobriety(Sobriety.min()), Sobriety.TOTALLY_DRUNK);
        try
        {
            Sobriety.getSobriety(-8000);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Drink -8000 not allowed!");
        }
    }

    @Test
    public void exceptionMaxTest()
    {
        assertEquals(Sobriety.max(), Integer.valueOf(5999));
        assertEquals(Sobriety.getSobriety(Sobriety.max()), Sobriety.NO_DRINK);
        try
        {
            Sobriety.getSobriety(6000);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Drink 6000 not allowed!");
        }
    }
}
