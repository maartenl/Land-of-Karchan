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
package mmud.beans;

import javax.ejb.Local;
import mmud.exceptions.AuthenticationException;
import mmud.exceptions.NotFoundException;

/**
 *
 * @author maartenl
 */
@Local
public interface GameBeanLocal
{

    /**
     * For testing purposes.
     * @return the String "Hello, World."
     */
    public String helloWorld();

    /**
     * Returns the session password of a player in the game.
     * @param name the name of the character
     * @param password the password of the character
     * @return the session password
     * @throws AuthenticationException if not able to authenticate the person
     * @throws NotFoundException  if the player does not exist or is invalid in some way.
     */
    public String getSessionPassword(final String name, final String password) throws AuthenticationException, NotFoundException;

    /**
     *
     */
    public void register();

    /**
     * Removes a player permanently from the database and hence from the game.
     * @param name the name of the character
     * @param password the password of the character
     */
    public void remove(final String name, final String password);

    /**
     * Enter the game.
     * @param name the name of the character
     * @param password the password of the character
     * @return CommandOutput, an object to return to the user providing a view of the world.
     */
    public CommandOutput enter(final String name, final String password);

    /**
     * Play the game, issue a command.
     * @param name the name of the character
     * @param sessionpwd the session password
     * @param command the command that the player wishes to issue
     * @return CommandOutput, an object to return to the user providing a view of the world.
     */
    public CommandOutput play(final String name, final String sessionpwd, final String command);

    /**
     * Returns the log of a player, from the offset provided.
     * @param name the name of the character
     * @param sessionpwd the session password
     * @param offset the offset from which the log must be returned. If null, return entire log.
     * @return Object containing the log and the new offset.
     */
    public MmudLog getLog(final String name, final String sessionpwd, final Integer offset);

}
