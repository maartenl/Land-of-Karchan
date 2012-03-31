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

/**
 * Provides snapshots of database entities, specifically for the web.
 * This has the following advantages:
 * <ul><li>security, not all the fields are automatically exported to the web</li>
 * <li>security, you can have 'layers', in our case the private info on the web
 * and the public info on the web.</li>
 * <li>the REST library will not try and resolve the entire database entity tree
 * for consumption (leading to stack overflow due to recursion)
 * </li></ul>
 */
package mmud.rest.webentities;
