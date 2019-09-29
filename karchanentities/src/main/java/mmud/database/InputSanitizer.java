/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmud.database;

/**
 * <p>Receives unclean input, gives clean output. Anything unclean is scrubbed.</p>
 * <p>For a more powerful version of it, see method Utils.security</p>
 * 
 * @author maartenl
 */
public class InputSanitizer
{
  
  /**
   * Returns a safe string, containing only alphabetical characters and space.
   *
   * @param value the original string.
   * @return the new string
   */
  public static String alphabeticalandspace(String value)
  {
    return value.replaceAll("[^A-Za-z ]", "");
  }

  /**
   * Returns a safe string, containing only alphabetical characters.
   *
   * @param value the original string.
   * @return the new string
   */
  public static String alphabetical(String value)
  {
    return value.replaceAll("[^A-Za-z]", "");
  }

  /**
   * Returns a safe string, containing only alphanumerical characters and space.
   *
   * @param value the original string.
   * @return the new string
   */
  public static String alphanumericalandspace(String value)
  {
    return value.replaceAll("[^A-Za-z0-9 ]", "");
  }

  /**
   * Returns a safe string, containing only alphanumerical characters and
   * punctuation.
   *
   * @param value the original string.
   * @return the new string
   */
  public static String alphanumericalandpuntuation(String value)
  {
    return value.replaceAll("[^A-Za-z0-9!&()_=+;:.,?'\"\\- ]", "");
  }

  /**
   * Returns a safe string, containing only alphanumerical characters.
   *
   * @param value the original string.
   * @return the new string
   */
  public static String alphanumerical(String value)
  {
    return value.replaceAll("[^A-Za-z0-9]", "");
  }

  /**
   * Returns a safe string, containing no javascript at all.
   *
   * @param dirtyInput the original string.
   * @return the new string, without javascript.
   */
  public static String security(String dirtyInput)
  {
    return EbayPolicy.sanitize(dirtyInput);
  }
}
