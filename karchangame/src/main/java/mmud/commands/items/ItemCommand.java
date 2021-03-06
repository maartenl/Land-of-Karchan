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
package mmud.commands.items;

import java.util.logging.Level;
import java.util.logging.Logger;

import mmud.commands.Command;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;

/**
 * An abstract class for the most normal commands.
 *
 * @author maartenl
 */
public abstract class ItemCommand implements Command
{

    private static final Logger LOGGER = Logger.getLogger(ItemCommand.class.getName());
    /**
     * the regular expression the command structure must follow
     */
    private final String theRegExpr;

    @Override
    public String getRegExpr()
    {
        return theRegExpr;
    }

  /**
   * Constructor.
   *
   * @param aRegExpr a regular expression to which the command should follow. For
   *                 example "give [A..Za..z]*1-4 to [A..Za..z]*". %me is a
   *                 parameter that can be used when the name of the character
   *                 playing is requested.
   */
  protected ItemCommand(String aRegExpr)
  {
    theRegExpr = aRegExpr;
  }

  /**
   * Starts the command, this method is used for default behaviour that needs
   * to take place before the run command is issued. This method starts the
   * run command.
   *
   * @param command the command entered by the user
   * @param aUser   the user that executed the command
   * @return DisplayInterface for providing to the user, or null if the
   * statement was not executed for some different reason.
   */
  DisplayInterface start(String command, User aUser)
  {
    LOGGER.log(Level.FINER, "{0}({1}) : {2}", new Object[]
      {
        aUser.getName(), this.getClass().getName(), command
      });
    String myregexpr = theRegExpr.replace("%s", aUser.getName());
    boolean result;
    if (myregexpr.endsWith(".+") && command.indexOf('\n') != -1)
    {
      String stuff = command.substring(0, command.indexOf('\n'));
      result = stuff.matches(myregexpr);
    } else
    {
      result = (command.matches(myregexpr));
    }
    if (!result)
    {
      /** didn't match the case, exiting */
      return null;
    }
    /** continue with the actual command */
    return run(command, aUser);
  }

  @Override
  public abstract DisplayInterface run(String command, User aUser);

  /**
   * split up the command into different words.
   *
   * @param aCommand String containing the command
   * @return String array where each String contains a word from the command.
   */
  protected static String[] parseCommand(String aCommand)
  {
        return aCommand.split("( )+", 50);
    }

    /**
     * split up the command into different words.<p/>
     * For example:
     * <p/>
     * <tt>parseCommand("bow to Marvin evilly", 4)</tt> returns array
     * <tt>["bow","to","Marvin", "evilly"]</tt><p/>
     * <tt>parseCommand("say to Marvin Greetings this morning.", 4)</tt>
     * returns array <tt>["say","to","Marvin", "Greetings this morning."]</tt>
     *
     * @param limit the result threshold, example 3 means the array holds
     * three, the first two words and the rest in the third array element.
     * @param aCommand
     * String containing the command
     * @return String array where each String contains a word from the command.
     * @see String#split(java.lang.String, int)
     */
    protected static String[] parseCommand(String aCommand, int limit)
    {
        return aCommand.split("( )+", limit);
    }
}
