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
package mmud.items;

/**
 * Contains the adjectives, and the amount and the name itself. Just for parsing.
 * @author maartenl
 */
public class ParsedItemDef
{

    private Integer amount;
    private String adject1;
    private String adject2;
    private String adject3;
    private String name;

    public ParsedItemDef(Integer amount, String adject1, String adject2, String adject3, String name)
    {
        this.amount = amount;
        this.adject1 = adject1;
        this.adject2 = adject2;
        this.adject3 = adject3;
        this.name = name;
    }

    /**
     * @return the amount
     */
    public Integer getAmount()
    {
        return amount;
    }

    /**
     * @return the adject1
     */
    public String getAdject1()
    {
        return adject1;
    }

    /**
     * @return the adject2
     */
    public String getAdject2()
    {
        return adject2;
    }

    /**
     * @return the adject3
     */
    public String getAdject3()
    {
        return adject3;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
}
