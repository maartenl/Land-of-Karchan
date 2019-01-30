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

import java.util.Arrays;
import java.util.Optional;

/**
 * Enumerated type for the male/female thing.
 *
 * @author maartenl
 */
public enum Sex
{

  MALE("male", "his", "him", "he"), FEMALE("female", "her", "her", "she"),
  /**
   * Also known as gender-neutral or third gender or non-binary gender. Just
   * pick one.
   */
  OTHER("other", "their", "them", "they");
  private final String theSex;
  private final String thePosession;
  private final String theIndirect;
  private final String theDirect;

  private Sex(String aSex, String aPosession, String aIndirect, String aDirect)
  {
    theSex = aSex;
    thePosession = aPosession;
    theIndirect = aIndirect;
    theDirect = aDirect;

  }

  /**
   * Little factory method for creating a Sex object.
   *
   * @param aString string describing the sex object to be created: "female",
   * "male" or "other".
   * @return Sex object, either male of female or other.
   * @throws RuntimeException if the sex is neither male nor female nor other.
   * In this case we do not know what to do.
   */
  public static Sex createFromString(String aString)
  {
    Optional<Sex> result = Arrays.stream(values()).filter(sex -> sex.theSex.equals(aString)).findFirst();
    return result.orElseThrow(() -> new RuntimeException("Illegal sex '" + aString + "'."));
  }

  /**
   * returns either "male" or "female"
   *
   * @return returns either "male" or "female"
   */
  @Override
  public String toString()
  {
    return theSex;
  }

  /**
   * returns either "his" or "her"
   *
   * @return returns either "his" or "her"
   */
  public String posession()
  {
    return thePosession;
  }

  /**
   * returns either "him" or "her"
   *
   * @return returns either "him" or "her"
   */
  public String indirect()
  {
    return theIndirect;
  }

  /**
   * returns either "he" or "she"
   *
   * @return returns either "he" or "she"
   */
  public String direct()
  {
    return theDirect;
  }

  /**
   * returns either "He" or "She"
   *
   * @return returns either "He" or "She"
   * @see #direct()
   */
  public String Direct()
  {
    return theDirect.substring(0, 1).toUpperCase() + theDirect.substring(1);
  }
}
