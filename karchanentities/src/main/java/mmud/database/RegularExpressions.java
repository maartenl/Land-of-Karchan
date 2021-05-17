/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmud.database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mmud.exceptions.RegularExpressionException;

/**
 * A class to verify that input conforms to certain regular expressions. Also
 * contains constants for Regular Expressions used in the validation of the
 * entities.
 *
 * @author maartenl
 */
public class RegularExpressions
{

  public static final String NAME_REGEXP = "[a-zA-Z]{3,}";
  public static final String NAME_MESSAGE = "Invalid name. Only letters are allowed and at least three letters are required, but not more than 20.";

  public static final String URL_REGEXP = "^\\/[a-zA-Z0-9\\-]+(\\/[a-zA-Z0-9\\-]+)*\\.[a-zA-Z]{3,3}$";
  public static final String URL_MESSAGE = "Invalid url. Only letters, digits and a - or / are allowed, must start with a / and end with a file extention containing three letters.";

  public static final String ONLY_LETTERS_ONE_OR_MORE_REGEXP = "[a-zA-Z-]{1,}";
  public static final String ONLY_LETTERS_ONE_OR_MORE_MESSAGE = "Only letters and dash (-) are allowed and at least one letter is required.";

  public static final String ONLY_LETTERS_REGEXP = "[a-zA-Z-', ]*";
  public static final String ONLY_LETTERS_MESSAGE = "Only letters, spaces, apostrophes, comma's and dashes (-) are allowed.";

  public static final String COMMENTS_REGEXP = "^[a-zA-Z0-9- ]*$";
  public static final String COMMENTS_MESSAGE = "For comments only letters, digits, dash and spaces are allowed.";

  public static final String WIKIPAGE_TITLE_REGEXP = "^[a-zA-Z0-9- ]*$";
  public static final String WIKIPAGE_TITLE_MESSAGE = "For the title of a wikipage only letters, digits, dash and spaces are allowed.";

  private RegularExpressions()
  {
  }

  /**
   * Checks if the value matches the regular expression
   *
   * @param regexp  the regular expression
   * @param value   the value to verify
   * @param message a message to display if the regular expression does not
   *                check out. May be null, in which case the message becomes something generic
   *                like "value x should match regexp y.
   * @throws RegularExpressionException a checked exception indicating failure.
   */
  public static void checkRegexp(String regexp, String value, String message) throws RegularExpressionException
  {
    Pattern p = Pattern.compile(regexp);
    Matcher m = p.matcher(value);
    if (!m.matches())
    {
      if (message == null)
      {
        message = "value " + value + " should match regexp " + regexp;
      }
      throw new RegularExpressionException(message);
    }
  }

  public static boolean regExpTest(String regexp, String matches)
  {
    Pattern p = Pattern.compile(regexp);
    Matcher m = p.matcher(matches);
    return m.matches();
  }
}
