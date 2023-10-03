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
 * <p>
 * Provides the JPA Database Entities for information regarding website scripts.
 * These
 * are specifically meant for the webpages, and do not in any way
 * effect gameplay. These tables are easily
 * recognized because they do not start with the prefix "mm_".</p>
 * <p>
 * <TABLE>
 *
 * <TR valign=top><TD>Tablename</TD><TD>Description</TD></TR>
 *
 * <TR valign=top><TD>characterinfo</TD><TD>contains story information regarding certain
 * characters.</TD></TR>
 *
 * <TR valign=top><TD>family</TD><TD>shows the family relations between characters.</TD></TR>
 *
 * <TR valign=top><TD>familyvalues</TD><TD>provides a number of possible family relations.
 * </TD></TR>
 *
 * <TR valign=top><TD></TD><TD></TD></TR>
 * </TABLE>
 *
 * @plantuml
 * <!--
 * family "description" *-- "id" familyvalues
 * mm_usertable "name" *-- "name" characterinfo
 * mm_usertable "name" *-- "name" family
 * mm_usertable "name" *-- "toname" family
 * class characterinfo {
 * -name
 * -imageurl
 * -homepageurl
 * -dateofbirth
 * -cityofbirth
 * -storyline
 * }
 * class family {
 * -name
 * -toname
 * -description
 * }
 * class familyvalues {
 * -id
 * -description
 * }
 * -->
 */
package mmud.database.entities.web;
