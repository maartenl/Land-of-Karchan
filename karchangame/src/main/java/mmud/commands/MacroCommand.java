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
package mmud.commands;

import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Macro;
import mmud.database.entities.game.MacroPK;
import mmud.exceptions.MudException;

/**
 * Macros :
 * <dl>
 * <dt>"macro"</dt>
 * <dd>shows a list of all macros</dt>
 * <dt>"macro dostuff say "Hello, everyone"; bow"</dt>
 * <dd>creates a macro with the command behind it. Multiple commands are separated by ";".</dd>
 * <dt>"macro dostuff"</dt>
 * <dd>clears a created macro, effectively deleting it.</dd></dl>
 *
 * @author maartenl
 */
public class MacroCommand extends NormalCommand
{

    public MacroCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] myParsed = parseCommand(command, 3);
        if (myParsed.length == 1)
        {
            return aUser.writeMacros();
        } else if (myParsed.length == 2)
        {
            if (!aUser.removeMacro(myParsed[1]))
            {
                aUser.writeMessage("Cannot remove macro [" + myParsed[1] + "]. Macro unknown.<br/>\r\n");
            return aUser.getRoom();
            }
            aUser.writeMessage("Removed macro [" + myParsed[1] + "].<br/>\r\n");
            return aUser.getRoom();
        }
        Macro macro = new Macro();
        macro.setPerson(aUser);
        macro.setMacroPK(new MacroPK(aUser.getName(), myParsed[1]));
        macro.setContents(myParsed[2]);
        aUser.addMacro(macro);
        aUser.writeMessage("Macro [" + myParsed[1] + "] added.<br/>\r\n");

        return aUser.getRoom();
    }
}
