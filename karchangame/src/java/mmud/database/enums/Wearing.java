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
 * Different position on the body that can wear clothes of some kind.
 */
public enum Wearing {

    ON_HEAD(1, "on %SHISHER head"),
    ON_NECK(2, "around %SHISHER neck"),
    ON_TORSO(4, "around %SHISHER torso"),
    ON_ARMS(8, "on %SHISHER arms"),
    ON_LEFT_WRIST(16, "on %SHISHER left wrist"),
    ON_RIGHT_WRIST(32, "on %SHISHER right wrist"),
    ON_LEFT_FINGER(64, "on %SHISHER left finger"),
    ON_RIGHT_FINGER(128, "on %SHISHER right finger"),
    ON_FEET(256, "on %SHISHER feet"),
    ON_HANDS(512, "on %SHISHER hands"),
    FLOATING_NEARBY(1024, "floating nearby"),
    ON_WAIST(2048, "on %SHISHER waist"),
    ON_LEGS(4096, "on %SHISHER legs"),
    ON_EYES(8192, "over %SHISHER eyes"),
    ON_EARS(16384, "on %SHISHER ears"),
    ABOUT_BODY(32768, "about %SHISHER body");
    private int enumVal;
    private String name;

    /**
     * Recreates the constants from an integer. An integer value of 0 causes a
     * null pointer to be returned.
     *
     * @param aVal the integer corresponding to the constant.
     * @return the constant object
     * @throws RuntimeException in case the integer provided does not correspond
     * to any of the available objects.
     */
    public static Wearing get(int aVal) {
        if (aVal == 0) {
            return null;
        }
        for (Wearing position : Wearing.values()) {
            if (position.toInt() == aVal) {
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
    public static boolean isIn(int aVal, Wearing aPos) {
        if (aPos == null) {
            // the empty position is always a good position.
            return true;
        }
        return (aVal & aPos.toInt()) == aPos.toInt();
    }

    private Wearing(int aVal, String str) {
        name = str;
        enumVal = aVal;
    }

    /**
     * Returns the name of the format.
     *
     * @return format name.
     */
    public String toString() {
        return name;
    }

    /**
     * Returns the numerical representation of the format.
     *
     * @return identification integer.
     */
    public int toInt() {
        return enumVal;
    }
}
