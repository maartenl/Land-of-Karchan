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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;

import jakarta.ws.rs.core.Response;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.scripting.RunScript;

/**
 * The Script Command. Runs a script.
 *
 * @see mmud.Constants#getCommand
 */
public class ScriptCommand extends NormalCommand
{

    private static final Logger LOGGER = Logger.getLogger(ScriptCommand.class.getName());

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
        /*
         Very hard to get right, but here's some info:
         - throwing an exception, means something's wrong (we can safely tell the user.)
         - returning null means, show the room
         - returning a Display, which implements DisplayInterface, means everything's fine.
         */
        LOGGER.entering(this.getClass().getName(), "run");
        String mySource = theUserCommand.getMethodName().getSrc();
        try
        {
            DisplayInterface result = runScript.run(aUser, command, mySource);
            return result == null ? aUser.getRoom() : result;
        } catch (ScriptException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex)
        {
            final String logMessage = "Error executing method " + theUserCommand.getId() + " because of command " + command + " by user " + aUser.getName();
            Logger.getLogger(ScriptCommand.class.getName()).log(Level.SEVERE, logMessage, ex);
            // TODO: this should be removed, but only after testing is completed.
            // because it is a pain to have to re-activate deactivated functions that bombed.
            // (better yet, set a method to "testing")
            // theUserCommand.setCallable(false);
            throw new MudWebException(aUser.getName(), command, ex, Response.Status.BAD_REQUEST);
        }
    }

}
