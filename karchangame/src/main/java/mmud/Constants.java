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

import com.google.gwt.thirdparty.guava.common.escape.Escaper;
import com.google.gwt.thirdparty.guava.common.html.HtmlEscapers;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.rest.webentities.PrivateItem;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.util.*;

/**
 * @author maartenl
 */
public class Constants
{

  public static final String NAME_REGEXP = "[a-zA-Z]{3,}";
  public static final String NAME_MESSAGE = "Invalid name. Only letters are allowed and at least three letters are required.";

  public static final String ONLY_LETTERS_ONE_OR_MORE_REGEXP = "[a-zA-Z-]{1,}";
  public static final String ONLY_LETTERS_ONE_OR_MORE_MESSAGE = "Only letters and dash (-) are allowed and at least one letter is required.";

  public static final String ONLY_LETTERS_REGEXP = "[a-zA-Z-]*";
  public static final String ONLY_LETTERS_MESSAGE = "Only letters and dash (-) are allowed.";

  private static final String BASEPATH = "/home/payara";

  public static final String DEPUTIES_EMAILADDRESS = "deputiesofkarchan@outlook.com";

  private static final String PLAYERLOG_DIR = "temp";

  private static final String POLICY_FILE_LOCATION = "antisamy-myspace.xml";

  public static final String MMUD_BASE_PATH_PROPERTY = "mmud.base.path";

  /**
   * Returns the base file path. This property can be set as a startup parameter called "mmud.base.path".
   * So for instance upon startup "-Dmmud.base.path=/home/payara".
   * @return the base file path to use.
   */
  public static String getBasepath()
  {
    return System.getProperty(MMUD_BASE_PATH_PROPERTY, BASEPATH);
  }

  public static String getMudfilepath()
  {
    return getBasepath() + File.separator + PLAYERLOG_DIR;
  }

  /**
   * <p>Returns the policy file for antisamy. Currently set to antisamy-myspace.xml.</p>
   * <p>Possible values:<ul>
   * <li>antisamy-anythinggoes.xml</li>
   * <li>antisamy-ebay.xml</li>
   * <li>antisamy-myspace.xml</li>
   * <li>antisamy-slashdot.xml</li>
   * <li>antisamy-tinymce.xml</li></ul>
   * </p>
   *
   * @return a string indicating the used policy file for antisamy.
   */
  public static String getPolicyFile()
  {
    return POLICY_FILE_LOCATION;
  }

  /**
   * Returns the amount of money in gold coins, silver coins and copper
   * coins.
   * <UL>
   * <LI>1 silver = 10 copper
   * <LI>1 gold = 10 silver
   * </UL>
   *
   * @param aValue the amount of money in copper coins.
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
   * @param adject1      String containing the first adjective.
   * @param adject2      String containing the second adjective.
   * @param adject3      String containing the third adjective.
   * @param noun         String containing the noun.
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
   * @param set     a set of items to be described.
   * @param builder the builder to append to. Should not be null.
   */
  @HtmlEscaped
  public static void addInventory(Set<Item> set, StringBuilder builder)
  {
    Escaper htmlEscaper = HtmlEscapers.htmlEscaper();
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
          htmlEscaper.escape(desc));
        if (!desc.endsWith("s"))
        {
          builder.append("s");
        }
      } else
      {
        // a gold, hard cup
        builder.append(htmlEscaper.escape(desc));
      }
      builder.append("</li>\r\n");
    }
    builder.append("</ul>");
  }

  /**
   * Adds a descriptive set of the items and their buy/sell price to the string builder.
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

  /**
   * Returns null if all is well, otherwise a description of the problems with the bean.
   *
   * @param <T>
   * @param entity
   * @return
   */
  public static <T> String checkValidation(T entity)
  {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
    StringBuffer buffer = null;
    if (!constraintViolations.isEmpty())
    {
      for (ConstraintViolation<T> cv : constraintViolations)
      {
        if (buffer == null)
        {
          buffer = new StringBuffer();
        }
        buffer.append(cv.getRootBeanClass().getSimpleName()).append(".").append(cv.getPropertyPath()).append(" ").append(cv.getMessage());
      }
    }
    return buffer == null ? null : buffer.toString();
  }
}
