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

import mmud.database.enums.Health;
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
public class HealthTest
{

    public HealthTest()
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

    @DataProvider(name = "healthRanges")
    public Object[][] createData1()
    {
        return new Object[][]
                {
                    {
                        Health.AT_DEATH, 0, Integer.valueOf(0), Integer.valueOf(999)
                    },
                    {
                        Health.VERY_BAD, 1, Integer.valueOf(1000), Integer.valueOf(1999)
                    },
                    {
                        Health.BAD, 2, Integer.valueOf(2000), Integer.valueOf(2999)
                    },
                    {
                        Health.TERRIBLY_HURT, 3, Integer.valueOf(3000), Integer.valueOf(3999)
                    },
                    {
                        Health.EXTREMELY_HURT, 4, Integer.valueOf(4000), Integer.valueOf(4999)
                    },
                    {
                        Health.QUITE_HURT, 5, Integer.valueOf(5000), Integer.valueOf(5999)
                    },
                    {
                        Health.HURT, 6, Integer.valueOf(6000), Integer.valueOf(6999)
                    },
                    {
                        Health.SLIGHTLY_HURT, 7, Integer.valueOf(7000), Integer.valueOf(7999)
                    },
                    {
                        Health.QUITE_NICE, 8, Integer.valueOf(8000), Integer.valueOf(8999)
                    },
                    {
                        Health.NICE, 9, Integer.valueOf(9000), Integer.valueOf(9999)
                    },
                    {
                        Health.WELL, 10, Integer.valueOf(10000), Integer.valueOf(10999)
                    },
                    {
                        Health.VERY_WELL, 11, Integer.valueOf(11000), Integer.valueOf(11999)
                    }
                };
    }

    @Test(dataProvider = "healthRanges")
    public void rangesTest(Health health, Integer ordinal, Integer n1, Integer n2)
    {
        for (int i = n1; i <= n2; i++)
        {
            assertEquals(ordinal, Integer.valueOf(health.getOrdinalValue()), "ordinalValue didn't match");
            assertEquals(Integer.valueOf(i / 1000), ordinal, "positive number(" + i + ") did not translate to ordinal properly");
            assertEquals(Health.getHealth(i), health, "getHealth(" + i + ") didn't return proper Health");
        }
    }

    private void test(Health health, int i, String s)
    {
        assertNotNull(health);
        assertEquals(health.getOrdinalValue(), i);
        assertEquals(health.getDescription(), s);
    }

    @Test
    public void defaultTest()
    {
        test(Health.AT_DEATH, 0, "at death's door");
    }

    @Test
    public void exceptionMinTest()
    {
        assertEquals(Health.min(), Integer.valueOf(0));
        assertEquals(Health.getHealth(Health.min()), Health.AT_DEATH);
        try
        {
            Health.getHealth(-1);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Health -1 not allowed!");
        }
    }

    @Test
    public void exceptionMaxTest()
    {
        assertEquals(Health.max(), Integer.valueOf(11999));
        assertEquals(Health.getHealth(Health.max()), Health.VERY_WELL);
        try
        {
            Health.getHealth(12000);
            fail("RuntimeException expected.");
        } catch (RuntimeException e)
        {
            assertEquals(e.getMessage(), "Health 12000 not allowed!");
        }
    }
}
