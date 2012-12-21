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
package mmud.commands.communication;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import mmud.commands.TargetCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.exceptions.PersonNotFoundException;
import mmud.rest.services.PersonBean;

/**
 *
 * Tell to someone something. The difference with normal communication commands,
 * is that this one is mandatory to a person and the person does not need to be
 * in the same room: "tell to Karn Help!".
 * @author maartenl
 */
public class TellCommand extends TargetCommand
{

    public TellCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    public CommType getCommType()
    {
        return CommType.TELL;
    }

    @Override
    protected DisplayInterface actionTo(User aUser, Person aTarget, String verb, String message) throws MudException
    {
        if (message == null)
        {
            // "ask to Marvin", ask what?
            return null;
        }
//        if (aUser.isIgnored(toChar))
//        {
//            aUser.writeMessage(toChar.getName()
//                    + " is ignoring you fully.<BR>\r\n");
//            return true;
//        }
        aUser.writeMessage(aUser, aTarget, "%SNAME "
                + getCommType().getPlural() + " [to %TNAME] : "
                + message + "<BR>\r\n");
        aTarget.writeMessage(aUser, aTarget, "<B>%SNAME "
                + getCommType().getPlural() + " [to %TNAME]</B> : "
                + message + "<BR>\r\n");
        return aUser.getRoom();
    }

    /**
     * Does not compute, TellCommand requires a target name.
     * @param aUser
     * @param verb
     * @param command
     * @return DisplayInterface of the action.
     * @throws MudException
     */
    @Override
    protected DisplayInterface action(User aUser, String verb, String command) throws MudException
    {
        return null;
    }

    protected Person getTarget(String[] myParsed, User aUser) throws PersonNotFoundException
    {
        if (myParsed.length > 2 && myParsed[1].equalsIgnoreCase("to"))
        {
            PersonBean personBean;
            try
            {
                personBean = (PersonBean) new InitialContext().lookup("java:module/PersonBean");
            } catch (NamingException e)
            {
                throw new MudException("Unable to tell people things.", e);
            }
            Person toChar = personBean.getActiveUser(myParsed[2]);
            if (toChar == null)
            {
                // action to unknown
                throw new PersonNotFoundException("Cannot find " + myParsed[2] + ".<br/>\r\n");
            }
            return toChar;
        }
        return null;
    }
}
