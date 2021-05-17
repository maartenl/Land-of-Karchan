/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmud.database;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.html.HtmlEscapers;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;

/**
 * @author maartenl
 */
public class OutputFormatter
{

  private static final Logger LOGGER = Logger.getLogger(OutputFormatter.class.getName());

  private OutputFormatter()
  {
  }

  /**
   * Changes a sentence to always start with a capital.
   *
   * @param string the string to change, may be empty or null.
   * @return a new string, containing a capital for the first letter. If
   * original is null, returns null. If original is empty string, returns empty
   * string.
   */
  public static String startWithCapital(String string)
  {
    if (string == null)
    {
      return null;
    }
    if (string.equals(""))
    {
      return "";
    }
    return string.substring(0, 1).toUpperCase() + string.substring(1);
  }

  /**
   * get a description based on the adjectives and the noun.
   *
   * @param adjectives   String containing the adjectives.
   * @param noun         String containing the noun.
   * @param with_a_or_an indicates if the word should include an a or an an.
   * @return String containing the description in the format: "an/a [adject1],
   * [adject2], [adject3] [noun]".
   */
  public static String getDescriptionOfItem(String adjectives, String noun, boolean with_a_or_an)
  {
    StringBuilder buf = new StringBuilder();
    if ((adjectives != null) && !adjectives.trim().equals(""))
    {
      buf.append(adjectives);
    }
    buf.append(" ").append(noun);
    String total = buf.toString();
    final String result = (with_a_or_an ? (isVowel(total.charAt(0)) ? "an " : "a ") : "") + total;
    return result;
  }

  /**
   * Returns whether or not a character is a vowel.
   *
   * @param aChar the character to check
   * @return boolean which is true if the character is a vowel.
   */
  public static boolean isVowel(char aChar)
  {
    return aChar == 'a' || aChar == 'e' || aChar == 'u' || aChar == 'o'
      || aChar == 'i' || aChar == 'A' || aChar == 'E' || aChar == 'U'
      || aChar == 'I' || aChar == 'O';
  }

  /**
   * Returns the amount of money in gold coins, silver coins and copper coins.
   * <UL>
   * <LI>1 silver = 10 copper
   * <LI>1 gold = 10 silver
   * </UL>
   *
   * @param aValue the amount of money in copper coins.
   * @return String description of the amount of money, for example 320 will be
   * translated as "<I>3 gold coins, 2 silver coins</I>". Returns "no money" if
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
   * For example "dusty grey-trimmed fur white cloak".
   *
   * @param parsed     A list of all the words entered by the player, see the example.
   * @param adjectives adjectives, in this case "dusty, grey-trimmed, fur, white".
   * @param name       the noun, in this case "cloak".
   * @return true if the item description matches.
   */
  public static boolean compareItemDescription(List<String> parsed, String adjectives, String name)
  {
    if (parsed == null)
    {
      LOGGER.warning("compareItemDescription: parsed == null");
      return false;
    }
    if (parsed.isEmpty())
    {
      // weird, empty list, nothing matches
      LOGGER.warning("compareItemDescription: parsed.size() == 0");
      return false;
    }
    if (!name.equalsIgnoreCase(parsed.get(parsed.size() - 1)))
    {
      // name should be equals
      return false;
    }
    List<String> possibleAdjectives = Arrays.stream(adjectives.replace(",", " ").split(" ")).map(String::trim).collect(Collectors.toList());
    List<String> playerAdjectives = parsed.subList(0, parsed.size() - 1);
    return playerAdjectives.stream()
      .filter(x -> possibleAdjectives.stream().noneMatch(y -> y.equalsIgnoreCase(x)))
      .findFirst()
      .isEmpty();
  }

  /**
   * Adds a descriptive set of the items and their buy/sell price to the string
   * builder.
   *
   * @param set     a set of items to be described.
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
      String desc = definition.getShortDescription();
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
      builder.append(", ").append(OutputFormatter.getDescriptionOfMoney(definition.getCopper()));
      builder.append("</li>\r\n");
    }
    builder.append("</ul>");
  }

  /**
   * Provides a map containing description of items and their amounts.
   *
   * @param set
   * @return
   */
  public static Map<ItemDefinition, Integer> inventory(Set<Item> set)
  {
    Map<ItemDefinition, Integer> inventory = new HashMap<>();

    for (Item item : set)
    {
      if (item.getItemDefinition().getId() < 0L)
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

  public static String htmlEscaper(String source)
  {
    return HtmlEscapers.htmlEscaper().escape(source);
  }

}
