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
package mmud.database.enums;

/**
 * Indicates whether or not a character is a normal player, a god,
 * a bot or a mob or a shopkeeper.
 * @author maartenl
 */
public enum God
{
    DEFAULT_USER(0), GOD(1), BOT(2), MOB(3), SHOPKEEPER(4);
    private int value;

    private God(int value)
    {
        this.value = value;
    }

    /**
     * Returns the enum based on a numerical value.
     * @param value
     * @return God enum, null if not found.
     */
    public static God get(int value)
    {
        for (God god : values())
        {
            if (value == god.getValue())
            {
                return god;
            }
        }
        return null;
    }

    /**
     * Returns the numerical value of this enum.
     * @return
     */
    public int getValue()
    {
        return value;
    }

}
