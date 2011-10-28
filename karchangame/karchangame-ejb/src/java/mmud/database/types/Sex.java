/*
 * Copyright (C) 2011 maartenl
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
package mmud.database.types;

/**
 * Enumerated type for the male/female thing:
 * @author maartenl
 */
public enum Sex
{

    MALE("male", "his", "him", "he"),
    FEMALE("female", "her", "her", "she");

    private Sex(String description, String posession, String indirect, String direct)
    {
        this.description = description;
        this.posession = posession;
        this.indirect = indirect;
        this.direct = direct;
        this.directCapital = direct.substring(0, 1).toUpperCase() + direct.substring(1);

    }
    private String description;
    private String posession;
    private String indirect;
    private String direct;
    private String directCapital;

    /**
     * Little factory method for creating a Sex object.
     * @param aString string describing the sex object
     * to be created: "female" or "male".
     * @return Sex object, either male of female.
     * @throws RuntimeException if the sex is neither
     * male nor female. In this case we do not know what to do.
     * @deprecated
     */
    public static Sex createFromString(String aString)
    {
        if (aString == null)
        {
            return null;
        }
        if (aString.equals("female"))
        {
            return FEMALE;
        }
        if (aString.equals("male"))
        {
            return MALE;
        }
        throw new RuntimeException("Illegal sex for character!!!");
    }

    /**
     * returns either "male" or "female"
     * @return returns either "male" or "female"
     */
    public String description()
    {
        return description;
    }

    /**
     * returns either "his" or "her"
     * @return returns either "his" or "her"
     */
    public String posession()
    {
        return posession;
    }

    /**
     * returns either "him" or "her"
     * @return returns either "him" or "her"
     */
    public String indirect()
    {
        return indirect;
    }

    /**
     * returns either "he" or "she"
     * @return returns either "he" or "she"
     */
    public String direct()
    {
        return direct;
    }

    /**
     * returns either "He" or "She"
     * @return returns either "He" or "She"
     */
    public String Direct()
    {
        return directCapital;
    }

    @Override
    public String toString()
    {
        return "Sex{" + "description=" + description + '}';
    }
}
