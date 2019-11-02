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
 * Indicates the appetite of a person.
 * @author maartenl
 */
public enum Appetite
{

    HUNGRY(0, "You are hungry."),
    EAT_WHOLE_LOT_MORE(1, "You can eat a whole lot more."),
    EAT_LOT_MORE(2, "You can eat a lot more."),
    EAT_SOME(3, "You can eat some."),
    EAT_LITTLE(4, "You can only eat a little more."),
    FULL(5, "You are full.");
    private String value;
    private int ordinalValue;

    private Appetite(int ordinalValue, String value)
    {
        this.value = value;
        this.ordinalValue = ordinalValue;
    }

    public static Integer min()
    {
        return HUNGRY.getOrdinalValue() * 1000;
    }

    public static Integer max()
    {
        return FULL.getOrdinalValue() * 1000 + 999;
    }

    /**
     * Returns the appetite of the character. Must be a number between 0 and 5999, where
     * 0..999 is HUNGRY and 5000..5999 is FULL.
     *
     * @return Appetite enum.
     * @throws RuntimeException if the appetite is not allowed
     * @throws NullPointerException if appetite not provided
     */
    public static Appetite getAppetite(Integer appetite)
    {
        if (appetite == null)
        {
            throw new NullPointerException("Null found!");
        }
        if (appetite < min() || appetite > max())
        {
            throw new RuntimeException("Appetite " + appetite + " not allowed!");
        }
        for (Appetite appetiteEnum : values())
        {
            if (appetite / 1000 == appetiteEnum.ordinalValue)
            {
                return appetiteEnum;
            }
        }
        // we shouldn't really get here.
        throw new RuntimeException("Appetite " + appetite + " not found!");
    }

    /**
     * Returns the appetite based on provided ordinal value.
     *
     * @return Appetite enum or null if not found.
     */
    public static Appetite get(Integer appetite)
    {
        if (appetite == null)
        {
            return null;
        }
        for (Appetite appetiteEnum : values())
        {
            if (appetite == appetiteEnum.ordinalValue)
            {
                return appetiteEnum;
            }
        }
        return null;
    }

    /**
     * Returns the appetite based on a description.
     *
     * @return Appetite enum or null if not found.
     */
    public static Appetite get(String appetite)
    {
        if (appetite == null)
        {
            return null;
        }
        for (Appetite appetiteEnum : values())
        {
            if (appetite.equalsIgnoreCase(appetiteEnum.value))
            {
                return appetiteEnum;
            }
        }
        return null;
    }

    /**
     * Returns the description of this enum.
     * @return for example "at death's door"
     */
    public String getDescription()
    {
        return value;
    }

    /**
     * Returns the integer indicating the appetite.
     * @return integer
     */
    public int getOrdinalValue()
    {
        return ordinalValue;
    }
}
