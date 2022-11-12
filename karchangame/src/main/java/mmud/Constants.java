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

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import mmud.database.OutputFormatter;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.rest.webentities.PrivateItem;

/**
 * @author maartenl
 */
public class Constants
{

  private static final String BASEPATH = System.getProperty("user.home");

  public static final String DEPUTIES_EMAILADDRESS = "deputiesofkarchan@outlook.com";

  private static final String PLAYERLOG_DIR = "temp";

  public static final String MMUD_BASE_PATH_PROPERTY = "mmud.base.path";

  private Constants()
  {
    // create private constructor to hide the public one
  }

  /**
   * For example 2020-05-17 13:23:34.
   */
  public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /**
   * Returns the base file path. This property can be set as a startup parameter called "mmud.base.path".
   * So for instance upon startup "-Dmmud.base.path=/home/payara".
   *
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
   * Adds a descriptive set of the items to the string builder.
   *
   * @param set     a set of items to be described.
   * @param builder the builder to append to. Should not be null.
   */
  public static void addInventory(Set<Item> set, StringBuilder builder)
  {
    Escaper htmlEscaper = HtmlEscapers.htmlEscaper();
    if ((set == null || set.isEmpty()))
    {
      builder.append(" absolutely nothing.</p>");
      return;
    }

    Map<ItemDefinition, Integer> inventory = OutputFormatter.inventory(set);

    builder.append("<ul>");
    for (Map.Entry<ItemDefinition, Integer> definition : inventory.entrySet())
    {
      String desc = definition.getKey().getShortDescription();
      builder.append("<li>");
      int amount = definition.getValue();
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
        privateItem.adjectives = item.getItemDefinition().getAdjectives();
        privateItem.name = item.getItemDefinition().getName();
        privateItem.amount = 1;
        inventory.put(key, privateItem);
      }
    }
    return new ArrayList<>(inventory.values());
  }

  /**
   * @param <T>    generic
   * @param entity the JPA entity to check
   * @return description of problems with the bean, or null if all is well.
   */
  public static <T> String checkValidation(T entity)
  {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory())
    {
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
      StringBuilder buffer = null;
      if (!constraintViolations.isEmpty())
      {
        for (ConstraintViolation<T> cv : constraintViolations)
        {
          if (buffer == null)
          {
            buffer = new StringBuilder();
          }
          buffer.append(cv.getRootBeanClass().getSimpleName()).append(".").append(cv.getPropertyPath()).append(" ").append(cv.getMessage());
        }
      }
      return buffer == null ? null : buffer.toString();
    }
  }

}
