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

import mmud.database.enums.Alignment;
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
public class AlignmentTest
{

    public AlignmentTest()
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

    @DataProvider(name = "alignmentRanges")
    public Object[][] createData1()
    {
        return new Object[][]
                {
                    {
                        Alignment.EVIL, -4, Integer.valueOf(-3999), Integer.valueOf(-3000)
                    },
                    {
                        Alignment.BAD, -3, Integer.valueOf(-2999), Integer.valueOf(-2000)
                    },
                    {
                        Alignment.MEAN, -2, Integer.valueOf(-1999), Integer.valueOf(-1000)
                    },
                    {
                        Alignment.UNTRUSTWORTHY, -1, Integer.valueOf(-999), Integer.valueOf(-1)
                    },
                    {
                        Alignment.NEUTRAL, 0, Integer.valueOf(0), Integer.valueOf(999)
                    },
                    {
                        Alignment.TRUSTWORTHY, 1, Integer.valueOf(1000), Integer.valueOf(1999)
                    },
                    {
                        Alignment.KIND, 2, Integer.valueOf(2000), Integer.valueOf(2999)
                    },
                    {
                        Alignment.AWFULLY_GOOD, 3, Integer.valueOf(3000), Integer.valueOf(3999)
                    },
                    {
                        Alignment.GOOD, 4, Integer.valueOf(4000), Integer.valueOf(4999)
                    }
                };
    }

    @Test(dataProvider = "alignmentRanges")
    public void rangesTest(Alignment sobr, Integer ordinal, Integer n1, Integer n2)
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
            assertEquals(Alignment.getAlignment(i), sobr, "getAlignment(" + i + ") didn't return proper Alignment");
        }
    }

    private void test(Alignment sobr, int i, String s)
    {
        assertNotNull(sobr);
        assertEquals(sobr.getOrdinalValue(), i);
        assertEquals(sobr.getDescription(), s);
    }

    @Test
    public void defaultTest()
    {
        test(Alignment.EVIL, -4, "evil");
    }

    @Test
    public void exceptionMinTest()
    {
        assertEquals(Alignment.min(), Integer.valueOf(-4999));
        assertEquals(Alignment.getAlignment(Alignment.min()), Alignment.EVIL);
        try
        {
            Alignment.getAlignment(-5000);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Alignment -5000 not allowed!");
        }
    }

    @Test
    public void exceptionMaxTest()
    {
        assertEquals(Alignment.max(), Integer.valueOf(4999));
        assertEquals(Alignment.getAlignment(Alignment.max()), Alignment.GOOD);
        try
        {
            Alignment.getAlignment(5000);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Alignment 5000 not allowed!");
        }
    }
}
