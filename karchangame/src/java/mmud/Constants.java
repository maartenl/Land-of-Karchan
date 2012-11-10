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
package mmud;

/**
 *
 * @author maartenl
 */
public class Constants
{

    // TODO : create .properties file for languages, default is English. Add properties throughout the code.
    private static String mudfilepath = "/home/maartenl/temp";
    // TODO : fix this to be less static, and has to make use of either
    // web-context param or env-context param/.
    private static String POLICY_FILE_LOCATION = "/home/maartenl/Land-of-Karchan/karchangame/antisamy-myspace-1.4.4.xml";

    public static String getMudfilepath()
    {
        return mudfilepath;
    }

    public static String getPolicyFile()
    {
        return POLICY_FILE_LOCATION;
    }

    /**
     * Returns the amount of money in gold coins, silver coins and theCopper
     * coins.
     * <UL>
     * <LI>1 silver = 10 copper
     * <LI>1 gold = 10 silver
     * </UL>
     *
     * @return String description of the amount of money, for example
     *         "<I>3 gold coins, 2 silver coins</I>". Returns an empty string if
     *         no money is present.
     */
    public static String getDescriptionOfMoney(int aValue)
    {
        if (aValue == 0)
        {
            return "";
        }
        int gold = aValue / 100;
        int silver = (aValue % 100) / 10;
        int copper = aValue % 10;
        boolean foundsome = false;
        String total = "";
        if (copper != 0)
        {
            total = copper + " copper coin" + (copper == 1 ? "" : "s");
            foundsome = true;
        }
        if (silver != 0)
        {
            if (foundsome)
            {
                total = ", " + total;
            }
            total = silver + " silver coin" + (silver == 1 ? "" : "s") + total;
            foundsome = true;
        }
        if (gold != 0)
        {
            if (foundsome)
            {
                total = ", " + total;
            }
            total = gold + " gold coin" + (gold == 1 ? "" : "s") + total;
            foundsome = true;
        }
        return total;
    }

    /**
     * get a description based on the adjectives and the verb.
     *
     * @param adject1
     *            String containing the first adjective.
     * @param adject2
     *            String containing the second adjective.
     * @param adject3
     *            String containing the third adjective.
     * @param verb
     *            String containing the verb.
     * @param with_a_or_an indicates if the word should include an a or an an.
     * @return String containing the description in the format: "an/a [adject1],
     *         [adject2], [adject3] [verb]".
     */
    public static String getDescriptionOfItem(String adject1, String adject2,
            String adject3, String verb, boolean with_a_or_an)
    {
        int i = 0;
        StringBuffer buf = new StringBuffer();
        if ((adject1 != null) && !adject1.trim().equals(""))
        {
            i++;
            buf.append(adject1);
        }
        if ((adject2 != null) && !adject2.trim().equals(""))
        {
            if (i == 1)
            {
                buf.append(", ");
            }
            buf.append(adject2);
            i++;
        }
        if ((adject3 != null) && !adject3.trim().equals(""))
        {
            if (i > 0)
            {
                buf.append(", ");
            }
            buf.append(adject3);
            i++;
        }
        buf.append(" " + verb);
        String total = buf.toString();
        return (with_a_or_an ? (isVowel(total.charAt(0)) ? "an " : "a ") : "") + total;
    }

    /**
     * Returns wether or not a character is a vowel.
     *
     * @param aChar
     *            the character to check
     * @return boolean which is true if the character is a vowel.
     */
    public static boolean isVowel(char aChar)
    {
        return aChar == 'a' || aChar == 'e' || aChar == 'u' || aChar == 'o'
                || aChar == 'i' || aChar == 'A' || aChar == 'E' || aChar == 'U'
                || aChar == 'I' || aChar == 'O';
    }

    @Deprecated
    public static boolean isQwerty(char aChar)
    {
        return isVowel(aChar);
    }
}
