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
package mmud.database;

/**
 *
 * @author maartenl
 */
public class Attributes
{

    /**
     * Attribute indicating that someone wishes to join a certain guild.
     */
    public final static String GUILDWISH = "guildwish";

    /**
     * Attribute indicating that someone can receive money or items from you.
     */
    public final static String CANRECEIVE = "canreceive";

    public final static String VALUETYPE_STRING = "string";

    public final static String VALUETYPE_BOOLEAN = "boolean";

    /**
     * Attribute indicating that an item is locked.
     */
    public static final String LOCKED = "islocked";

    public static final String NOTSELLABLE = "notsellable";

    public static String NOTBUYABLE = "notbuyable";

    /**
     * Attribute indicating a description that takes precedence
     * over an existing description. For example, the long description
     * used in items.
     */
    public static String DESCRIPTION = "description";
}
