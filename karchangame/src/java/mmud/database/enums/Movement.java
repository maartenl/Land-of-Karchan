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
 * Indicates how tired you are. Indicates the manner of movement possible.
 * @author maartenl
 */
public enum Movement
{
    FULLY_EXHAUSTED(0, "fully exhausted"), ALMOST_EXHAUSTED(1,
    "almost exhausted"), VERY_TIRED(2, "very tired"), SLIGHTLY_TIRED(3, "slightly tired"),
    SLIGHTLY_FATIGUED(4, "slightly fatigued"), NOT_TIRED_AT_ALL(5, "not tired at all");
    private String value;
    private int ordinalValue;

    private Movement(int ordinalValue, String value)
    {
        this.value = value;
        this.ordinalValue = ordinalValue;
    }

    public static Integer min()
    {
        return FULLY_EXHAUSTED.getOrdinalValue() * 1000;
    }

    public static Integer max()
    {
        return NOT_TIRED_AT_ALL.getOrdinalValue() * 1000  + 999;
    }
    /**
     * Returns the movement of the character. Must be a number between 0 and 5999, where
     * 0..999 is FULLY_EXHAUSTED and 5999 is NOT_TIRED_AT_ALL.
     *
     * @return Movement enum.
     * @throws RuntimwException if the movement is not allowed
     * @throws NullPointerException if movement not provided
     */
    public static Movement getMovement(Integer movement)
    {
        if (movement == null)
        {
            throw new NullPointerException("Null found!");
        }
        if (movement < min() || movement > max())
        {
            throw new RuntimeException("Movement " + movement + " not allowed!");
        }
        for (Movement movementEnum : values())
        {
            if (movement / 1000 == movementEnum.ordinalValue)
            {
                return movementEnum;
            }
        }
        // we shouldn't really get here.
        throw new RuntimeException("Movement " + movement + " not found!");
    }

    /**
     * Returns the description of this enum.
     * @return for example "not tired at all"
     */
    public String getDescription()
    {
        return value;
    }

    /**
     * Returns the integer indicating the movement.
     * @return integer
     */
    public int getOrdinalValue()
    {
        return ordinalValue;
    }
}
