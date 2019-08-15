/*
 * Copyright =C) 2019 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * =at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;public static final String  without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.karchan.security;

/**
 * The different authorization roles.
 *
 * @author maartenl
 */
public class Roles
{

  /**
   * Everyone basically has this role, if they've registered a character.
   */
  public static final String PLAYER = "player";

  /**
   * Administrators.
   */
  public static final String DEPUTY = "deputy";

  public static final String GUILDMEMBER = "guildmember";

  public static final String GUILDMASTER = "guildmaster";

  /**
   * Currently this is Karn.
   */
  public static final String GOD = "god";

}
