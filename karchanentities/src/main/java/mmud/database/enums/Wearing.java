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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Different position on the body that can wear clothes of some kind.
 */
public enum Wearing
{

    ON_HEAD(1, "on %SHISHER head", "head"),
    ON_NECK(2, "around %SHISHER neck", "neck"),
    ON_TORSO(4, "around %SHISHER torso", "torso"),
    ON_ARMS(8, "on %SHISHER arms", "arms"),
    ON_LEFT_WRIST(16, "on %SHISHER left wrist", "leftwrist"),
    ON_RIGHT_WRIST(32, "on %SHISHER right wrist", "rightwrist"),
    ON_LEFT_FINGER(64, "on %SHISHER left finger", "leftfinger"),
    ON_RIGHT_FINGER(128, "on %SHISHER right finger", "rightfinger"),
    ON_FEET(256, "on %SHISHER feet", "feet"),
    ON_HANDS(512, "on %SHISHER hands", "hands"),
    FLOATING_NEARBY(1024, "floating nearby", "nothing"),
    ON_WAIST(2048, "on %SHISHER waist", "waist"),
    ON_LEGS(4096, "on %SHISHER legs", "legs"),
    ON_EYES(8192, "over %SHISHER eyes", "eyes"),
    ON_EARS(16384, "on %SHISHER ears", "ears"),
    ABOUT_BODY(32768, "about %SHISHER body", "body");

    private int enumVal;
    private String name;
    private String parse;

    /**
     * Recreates the constants from an integer. An integer value of 0 causes a
     * null pointer to be returned.
     *
     * @param aVal the integer corresponding to the constant.
     * @return the constant object
     * @throws RuntimeException in case the integer provided does not correspond
     * to any of the available objects.
     */
    public static Wearing get(int aVal)
    {
        if (aVal == 0)
        {
            return null;
        }
        for (Wearing position : Wearing.values())
        {
            if (position.toInt() == aVal)
            {
                return position;
            }
        }
        throw new RuntimeException("value " + aVal + " does not "
                + "correspond to a Wearing");
    }

    /**
     * Provides some parsing, a string is translated to the enum in question.
     * For example "neck" will return Wearing.NECK;
     *
     * @param aVal the string to be parsed
     * @return the constant object. Will return null, if a faulty aval is provided
     * that does not map to any of the enums.
     */
    public static Wearing parse(String aVal)
    {
        if (aVal == null)
        {
            return null;
        }
        for (Wearing position : Wearing.values())
        {
            if (position.parse.equalsIgnoreCase(aVal))
            {
                return position;
            }
        }
        throw new RuntimeException("value " + aVal + " does not "
                + "correspond to a Wearing");
    }

    /**
     * Receives an integer and checks to see that the Wearing is a
     * part of it.
     *
     * @param aVal the integer corresponding to a number of Wearings
     * @param aPos the Wearing for which to check.
     * @return boolean, true if the integer contains the Wearing.
     */
    public static boolean isIn(Integer aVal, Wearing aPos)
    {
        if (aVal == null)
        {
            // an empty value, means cannot be worn.
            return false;
        }
        if (aPos == null)
        {
            // the empty position is always a good position.
            return true;
        }
        return (aVal & aPos.toInt()) == aPos.toInt();
    }

    /**
     * Constructor for the enum.
     *
     * @param aVal the integer value (database)
     * @param str the description of use in communication
     * @param parse for parsing commands, contains one word indicating the
     * position on the body.
     */
    private Wearing(int aVal, String str, String parse)
    {
        name = str;
        enumVal = aVal;
        this.parse = parse;
    }

    /**
     * Returns the name of the format.
     *
     * @return format name.
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Returns the numerical representation of the format.
     *
     * @return identification integer.
     */
    public int toInt()
    {
        return enumVal;
    }

    /**
     * Receives an integer and returns a list of Wieldings to correspond.
     *
     * @param aVal the integer corresponding to a number of Wieldings
     * @return a List of Wieldings.
     */
    public static Set<Wearing> returnWearings(Integer aVal)
    {
        if (aVal == null)
        {
            // an empty value means, cannot be worn.
            return Collections.emptySet();
        }
        Set<Wearing> result = new HashSet<>();
        for (Wearing position : Wearing.values())
        {
            if (isIn(aVal, position))
            {
                result.add(position);
            }
        }
        return result;
    }
}
