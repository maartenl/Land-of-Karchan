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
public enum Alignment
{


    EVIL(-4, "evil"),
    BAD(-3, "bad"),
    MEAN(-2, "mean"),
    UNTRUSTWORTHY(-1, "untrustworthy"),
    NEUTRAL(0, "neutral"),
    TRUSTWORTHY(1, "trustworthy"),
    KIND(2, "kind"),
    AWFULLY_GOOD(3, "awfully good"),
    GOOD(4, "good");
    private String value;
    private int ordinalValue;

    private Alignment(int ordinalValue, String value)
    {
        this.value = value;
        this.ordinalValue = ordinalValue;
    }

    public static Integer min()
    {
        return EVIL.getOrdinalValue() * 1000 - 999;
    }

    public static Integer max()
    {
        return GOOD.getOrdinalValue() * 1000  + 999;
    }

    /**
     * Returns the thirstiness of the character. Must be a number between -5999 and 5999, where
     * -5999..-5000 is out of your skull on alcohol and 5000..5999 is no longer able to drink.
     *
     * @return Alignment enum.
     * @throws RuntimeException if the movement is not allowed
     * @throws NullPointerException if movement not provided
     */
    public static Alignment getAlignment(Integer alignment)
    {
        if (alignment == null)
        {
            throw new NullPointerException("Null found!");
        }
        if (alignment < min() || alignment > max())
        {
            throw new RuntimeException("Alignment " + alignment + " not allowed!");
        }
        for (Alignment sobrEnum : values())
        {
            if (alignment < 0)
            {
                if (alignment / 1000 - 1 == sobrEnum.ordinalValue)
                {
                    return sobrEnum;
                }
            }
            if (alignment / 1000 == sobrEnum.ordinalValue)
            {
                return sobrEnum;
            }
        }
        // we shouldn't really get here.
        throw new RuntimeException("Alignment " + alignment + " not found!");
    }

    /**
     * Returns the description of this enum.
     * @return for example "mean"
     */
    public String getDescription()
    {
        return value;
    }

    /**
     * Returns the integer indicating the alignment.
     * @return integer
     */
    public int getOrdinalValue()
    {
        return ordinalValue;
    }
}
