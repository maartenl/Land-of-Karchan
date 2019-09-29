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
 * Indicates how drunk you are, or how thirsty.
 * @author maartenl
 */
public enum Sobriety
{

    TOTALLY_DRUNK(-7, "You are out of your skull on alcohol."),
    VERY_DRUNK(-6, "You are very drunk."),
    DRUNK(-5, "You are drunk."),
    PISSED(-4, "You are pissed."),
    LITTLE_DRUNK(-3, "You are a little drunk."),
    INEBRIATED(-2, "You are inebriated."),
    HEADACHE(-1, "You have a headache."),
    THIRSTY(0, "You are thirsty."),
    DRINK_WHOLE_LOT_MORE(1, "You can drink a whole lot more."),
    DRINK_MORE(2, "You can drink a lot more."),
    DRINK_COME(3, "You can drink some."),
    DRINK_LITTLE(4, "You can drink a little more."),
    NO_DRINK(5, "You cannot drink anymore.");

    private String value;
    private int ordinalValue;

    private Sobriety(int ordinalValue, String value)
    {
        this.value = value;
        this.ordinalValue = ordinalValue;
    }

    public static Integer min()
    {
        return TOTALLY_DRUNK.getOrdinalValue() * 1000 - 999;
    }

    public static Integer max()
    {
        return NO_DRINK.getOrdinalValue() * 1000  + 999;
    }

    /**
     * Returns the thirstiness of the character. Must be a number between -5999 and 5999, where
     * -5999..-5000 is out of your skull on alcohol and 5000..5999 is no longer able to drink.
     *
     * @return Sobriety enum.
     * @throws RuntimeException if the movement is not allowed
     * @throws NullPointerException if movement not provided
     */
    public static Sobriety getSobriety(Integer sobriety)
    {
        if (sobriety == null)
        {
            throw new NullPointerException("Null found!");
        }
        if (sobriety < min() || sobriety > max())
        {
            throw new RuntimeException("Drink " + sobriety + " not allowed!");
        }
        for (Sobriety sobrEnum : values())
        {
            if (sobriety < 0)
            {
                if (sobriety / 1000 - 1 == sobrEnum.ordinalValue)
                {
                    return sobrEnum;
                }
            }
            if (sobriety / 1000 == sobrEnum.ordinalValue)
            {
                return sobrEnum;
            }
        }
        // we shouldn't really get here.
        throw new RuntimeException("Sobriety " + sobriety + " not found!");
    }

    /**
     * Returns the description of this enum.
     * @return for example "You can drink some more."
     */
    public String getDescription()
    {
        return value;
    }

    /**
     * Returns the integer indicating the sobriety.
     * @return integer
     */
    public int getOrdinalValue()
    {
        return ordinalValue;
    }
}
