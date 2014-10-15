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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.enums.Filter;
import mmud.rest.webentities.PrivateItem;

/**
 *
 * @author maartenl
 */
public class Constants
{

    // TODO : create .properties file for languages, default is English. Add properties throughout the code.
    private static final String mudfilepath = "/home/glassfish/temp";
    // TODO : fix this to be less static, and has to make use of either
    // web-context param or env-context param/.
    private static final String POLICY_FILE_LOCATION = "/home/glassfish/antisamy-myspace-1.4.4.xml";

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
     * @return String description of the amount of money, for example 320 will
     * be translated as 
     * "<I>3 gold coins, 2 silver coins</I>". Returns "no money" if
     * no money is present.
     */
    public static String getDescriptionOfMoney(int aValue)
    {
        if (aValue == 0)
        {
            return "no money";
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
        }
        return total;
    }

    /**
     * get a description based on the adjectives and the noun.
     *
     * @param adject1
     * String containing the first adjective.
     * @param adject2
     * String containing the second adjective.
     * @param adject3
     * String containing the third adjective.
     * @param noun
     * String containing the noun.
     * @param with_a_or_an indicates if the word should include an a or an an.
     * @return String containing the description in the format: "an/a [adject1],
     * [adject2], [adject3] [noun]".
     */
    public static String getDescriptionOfItem(String adject1, String adject2,
            String adject3, String noun, boolean with_a_or_an)
    {
        int i = 0;
        StringBuilder buf = new StringBuilder();
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
        buf.append(" ").append(noun);
        String total = buf.toString();
        return (with_a_or_an ? (isVowel(total.charAt(0)) ? "an " : "a ") : "") + total;
    }

    /**
     * Returns whether or not a character is a vowel.
     *
     * @param aChar
     * the character to check
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

    public static boolean compareItemDescription(List<String> parsed, String adject1, String adject2, String adject3, String name)
    {
        if (parsed == null)
        {
            return false;
        }
        List<String> possibleAdjectives = Arrays.asList(adject1, adject2, adject3);
        switch (parsed.size())
        {
            case 0:
                // weird, empty list, nothing matches
                return false;
            case 1:
                // name should be equals
                return name.equalsIgnoreCase(parsed.get(0));
            case 2:
                // name and an adjective should be equals
                return possibleAdjectives.contains(parsed.get(0))
                        && name.equalsIgnoreCase(parsed.get(1));
            case 3:
                // name and two adjectives should be equals
                return possibleAdjectives.contains(parsed.get(0))
                        && possibleAdjectives.contains(parsed.get(1))
                        && name.equalsIgnoreCase(parsed.get(2));
            default:
                // name and all three adjective should be equals
                return possibleAdjectives.contains(parsed.get(0))
                        && possibleAdjectives.contains(parsed.get(1))
                        && possibleAdjectives.contains(parsed.get(2))
                        && name.equalsIgnoreCase(parsed.get(3));
        }
    }

    /**
     * Provides a map containing description of items and their amounts.
     *
     * @param set
     * @return
     */
    private static Map<ItemDefinition, Integer> inventory(Set<Item> set)
    {
        Map<ItemDefinition, Integer> inventory = new HashMap<>();

        for (Item item : set)
        {
            if (item.getItemDefinition().getId() < 0)
            {
                continue;
            }
            ItemDefinition key = item.getItemDefinition();
            if (inventory.containsKey(key))
            {
                inventory.put(key, inventory.get(key) + 1);
            } else
            {
                inventory.put(key, 1);
            }
        }
        return inventory;
    }

    /**
     * Adds a descriptive set of the items to the string builder.
     *
     * @param set a set of items to be described.
     * @param builder the builder to append to. Should not be null.
     */
    public static void addInventory(Set<Item> set, StringBuilder builder)
    {
        if ((set == null || set.isEmpty()))
        {
            builder.append(" absolutely nothing.</p>");
            return;
        }

        Map<ItemDefinition, Integer> inventory = inventory(set);

        builder.append("<ul>");
        for (ItemDefinition definition : inventory.keySet())
        {
            String desc = definition.getDescription();
            builder.append("<li>");
            int amount = inventory.get(definition);
            if (amount != 1)
            {
                // 5 gold, hard cups
                if (desc.startsWith("an"))
                {
                    // remove 'an '
                    desc = desc.substring(3);
                } else
                {
                    // remove 'a '
                    desc = desc.substring(2);
                }
                builder.append(amount).append(" ").append(
                        desc);
                if (!desc.endsWith("s"))
                {
                    builder.append("s");
                }
            } else
            {
                // a gold, hard cup
                builder.append(desc);
            }
            builder.append("</li>\r\n");
        }
        builder.append("</ul>");
    }

    /**
     * Adds a descriptive set of the items and their buy/sell price to the string builder.
     *
     * @param set a set of items to be described.
     * @param builder the builder to append to. Should not be null.
     */
    public static void addShopkeeperList(Set<Item> set, StringBuilder builder)
    {
        if ((set == null || set.isEmpty()))
        {
            builder.append(" absolutely nothing.</p>");
            return;
        }

        Map<ItemDefinition, Integer> inventory = inventory(set);

        builder.append("<ul>");
        for (ItemDefinition definition : inventory.keySet())
        {
            String desc = definition.getDescription();
            builder.append("<li>");
            int amount = inventory.get(definition);
            if (amount != 1)
            {
                // 5 gold, hard cups
                if (desc.startsWith("an"))
                {
                    // remove 'an '
                    desc = desc.substring(3);
                } else
                {
                    // remove 'a '
                    desc = desc.substring(2);
                }
                builder.append(amount).append(" ").append(
                        desc);
                if (!desc.endsWith("s"))
                {
                    builder.append("s");
                }
            } else
            {
                // a gold, hard cup
                builder.append(desc);
            }
            builder.append(", ").append(getDescriptionOfMoney(definition.getCopper()));
            builder.append("</li>\r\n");
        }
        builder.append("</ul>");
    }

    /**
     * Adds a descriptive set of the items.
     *
     * @param set a set of items to be described.
     * @return a list of items
     */
    public static List<PrivateItem> addInventoryForRoom(Set<Item> set)
    {
        if ((set == null || set.isEmpty()))
        {
            return Collections.emptyList();
        }
        Map<String, PrivateItem> inventory = new HashMap<>();
        for (Item item : set)
        {
            if (!item.isVisible())
            {
                continue;
            }
            String key = item.getDescription();
            if (inventory.containsKey(key))
            {
                inventory.get(key).amount += 1;
            } else
            {
                PrivateItem privateItem = new PrivateItem();
                privateItem.adject1 = item.getItemDefinition().getAdject1();
                privateItem.adject2 = item.getItemDefinition().getAdject2();
                privateItem.adject3 = item.getItemDefinition().getAdject3();
                privateItem.name = item.getItemDefinition().getName();
                privateItem.amount = 1;
                inventory.put(key, privateItem);
            }
        }
        return new ArrayList<>(inventory.values());
    }

    public static void setFilters(final EntityManager entityManager, Filter activePersonFilter)
    {
        entityManager.setProperty("activePersonFilter", activePersonFilter == Filter.ON ? 1 : 0);
        entityManager.setProperty("sundaydateFilter", getSundays());
    }

    /**
     * <p>
     * Retrieves the date of the last Sunday, compared to <i>now</i>.</p>
     * <p>
     * So, if today is Wednesday, 12-12-2010, 13:00:00 hours, this would return
     * Saturday, 08-12-2010, 00:00:00 hours (midnight).</p>
     *
     * @return the Date of last Sunday morning.
     */
    public static Date getSundays()
    {
        Calendar cal = Calendar.getInstance();
        int daysBackToSunday = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, -daysBackToSunday);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date sundays = cal.getTime();
        return sundays;
    }
}
