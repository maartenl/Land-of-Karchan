/*-------------------------------------------------------------------------
svninfo: $Id: SayCommand.java 1192 2009-10-20 05:42:27Z maartenl $
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
package mmud.commands;

import java.util.List;
import java.util.logging.Logger;
import mmud.MudException;
import mmud.characters.Macro;
import mmud.characters.User;
import mmud.database.Database;
import mmud.database.MudDatabaseException;

/**
 * Macros :
 * <UL>
 * <LI>"macro" or
 * <LI>"macro dostuff say "Hello, everyone"; bow"
 * <LI>"macro dostuff"
 * </UL>
 */
public class MacroCommand extends NormalCommand
{

    	private String theResult = null;

	public MacroCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(User aUser) throws MudException
	{
		Logger.getLogger("mmud").finer("");
                String[] myParsed = getParsedCommand();
		if (myParsed.length == 1)
		{
                    return writeMacros(aUser);
		}
                if (myParsed.length == 2)
                {
                    Database.removeMacro(aUser, myParsed[1]);
                    aUser.writeMessage("Removed macro.<br/>\r\n");
                    return true;
                }

		String command = getCommand();

                Macro macro = new Macro(myParsed[1], command.substring(command.indexOf(myParsed[1]) + myParsed[1].length() + 1));
                Database.setMacro(aUser, macro);
                aUser.writeMessage("Created/changed macro.<BR>\r\n");
		return true;
	}

        @Override
	public Command createCommand()
	{
		return new MacroCommand(getRegExpr());
	}

        private boolean writeMacros(User aUser) throws MudDatabaseException
        {
            List<Macro> list = Database.getMacros(aUser);
            Logger.getLogger("mmud").finer("writeMacros");
            StringBuffer sb = new StringBuffer("<H1>Macros</H1><table><tr><td>macro</td><td>contents</td></tr>");
            for (Macro m : list)
            {
                sb.append("<tr><td>" + m.getMacroname() + "</td><td>" + m.getContents() + "</td></tr>");
            }
            sb.append("</table>");
            sb.append(aUser.printForm());
            theResult = sb.toString();
            return true;
        }

	@Override
	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return theResult;
	}
}
