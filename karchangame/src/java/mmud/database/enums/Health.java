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
 * Indicates the health of a person.
 * @author maartenl
 */
public enum Health
{

    AT_DEATH(0, "at death's door"), VERY_BAD(1,
    "feeling very bad"), BAD(2, "feeling bad"), TERRIBLY_HURT(3, "terribly hurt"),
    EXTREMELY_HURT(4, "extremely hurt"), QUITE_HURT(5, "quite hurt"), HURT(6, "hurt"), SLIGHTLY_HURT(7, "slightly hurt"),
    QUITE_NICE(8, "feeling quite nice"), NICE(9, "feeling fine"), WELL(10, "feeling well"),
    VERY_WELL(11, "feeling very well");
    private String value;
    private int ordinalValue;

    private Health(int ordinalValue, String value)
    {
        this.value = value;
        this.ordinalValue = ordinalValue;
    }

    public static Integer min()
    {
        return AT_DEATH.getOrdinalValue() * 1000;
    }

    public static Integer max()
    {
        return VERY_WELL.getOrdinalValue() * 1000  + 999;
    }

    /**
     * Returns the health of the character. Must be a number between 0 and 11999, where
     * 0..999 is AT_DEATH and 11000..11999 is VERY_WELL.
     *
     * @return Health enum.
     * @throws RuntimwException if the health is not allowed
     * @throws NullPointerException if health not provided
     */
    public static Health getHealth(Integer health)
    {
        if (health == null)
        {
            throw new NullPointerException("Null found!");
        }
        if (health < min() || health > max())
        {
            throw new RuntimeException("Health " + health + " not allowed!");
        }
        for (Health healthEnum : values())
        {
            if (health / 1000 == healthEnum.ordinalValue)
            {
                return healthEnum;
            }
        }
        // we shouldn't really get here.
        throw new RuntimeException("Health " + health + " not found!");
    }

    /**
     * Returns the health based on provided ordinal value.
     *
     * @return Health enum or null if not found.
     */
    public static Health get(Integer health)
    {
        if (health == null)
        {
            return null;
        }
        for (Health healthEnum : values())
        {
            if (health == healthEnum.ordinalValue)
            {
                return healthEnum;
            }
        }
        return null;
    }

    /**
     * Returns the health based on a description.
     *
     * @return Health enum or null if not found.
     */
    public static Health get(String health)
    {
        if (health == null)
        {
            return null;
        }
        for (Health healthEnum : values())
        {
            if (health.equalsIgnoreCase(healthEnum.value))
            {
                return healthEnum;
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
     * Returns the integer indicating the health.
     * @return integer
     */
    public int getOrdinalValue()
    {
        return ordinalValue;
    }
}
