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
package mmud.testing;

import static org.testng.Assert.assertNotNull;

/**
 * Some general purpose methods for easy verifying of test results.
 *
 * @author maartenl
 */
public class TestingUtils
{

    /**
     * @param actual
     * @param expected
     * @return returns true if no compare required, returns false if compare should
     * continue.
     */
    public static boolean compareBase(Object actual, Object expected)
    {
        if (actual == null && expected == null)
        {
            return true;
        }
        assertNotNull(actual, "actual should not be null");
        assertNotNull(expected, "expected should not be null");
        return false;
    }
}
