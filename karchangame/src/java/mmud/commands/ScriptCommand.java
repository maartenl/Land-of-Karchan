/*
 * Copyright (C) 2014 maartenl
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.MudException;
import mmud.scripting.RunScript;

/**
 * The Script Command. Runs a script.
 *
 * @see mmud.Constants#getCommand
 */
public class ScriptCommand extends NormalCommand
{

    private static final Logger itsLog = Logger.getLogger(ScriptCommand.class.getName());

    private final UserCommand theUserCommand;

    private final RunScript runScript;

    /**
     * Constructor for the script command.
     *
     * @param myCom
     * data class containing information about the command.
     */
    ScriptCommand(UserCommand myCom, RunScript runScript)
    {
        super(myCom.getCommand());
        theUserCommand = myCom;
        this.runScript = runScript;
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        itsLog.entering(this.getClass().getName(), "run");
        String mySource = theUserCommand.getMethodName().getSrc();
        try
        {
            runScript.run(aUser, command, mySource);
        } catch (ScriptException | NoSuchMethodException ex)
        {
            final String logMessage = "Error executing method " + theUserCommand.getId() + " because of command " + command + " by user " + aUser.getName();
            Logger.getLogger(ScriptCommand.class.getName()).log(Level.SEVERE, logMessage, ex);
            // TODO: this should be removed, but only after testing is completed.
            // because it is a pain to have to re-activate deactivated functions that bombed.
            // theUserCommand.setCallable(false);
            throw new MudException("I'm afraid, I do not understand that.<br/>", ex);
        }
        itsLog.exiting(this.getClass().getName(), "run");
        return aUser.getRoom();
    }

}
