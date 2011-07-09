/*-------------------------------------------------------------------------
svninfo: $Id: Mob.java 1320 2011-07-03 11:59:49Z maartenl $
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/


package mmud.characters;

/**
 * User defined macros.
 * @author maartenl
 */
public class Macro {

    public Macro(String macroname, String contents) {
        this.macroname = macroname;
        this.contents = contents;
    }

    private String macroname;

    /**
     * Get the value of macroname
     *
     * @return the value of macroname
     */
    public String getMacroname() {
        return macroname;
    }

    /**
     * Set the value of macroname
     *
     * @param macroname new value of macroname
     */
    public void setMacroname(String macroname) {
        this.macroname = macroname;
    }

    private String contents;

    /**
     * Get the value of contents, indicating what to do when this macro
     * should execute.
     *
     * @return the value of contents
     */
    public String getContents() {
        return contents;
    }

    /**
     * Set the value of contents.
     *
     * @param contents new value of contents
     */
    public void setContents(String contents) {
        this.contents = contents;
    }

}
