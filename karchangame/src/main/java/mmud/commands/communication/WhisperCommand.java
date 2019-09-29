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

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Whisper something : "whisper Anybody here?"
 * @author maartenl
 */
public class WhisperCommand extends CommunicationCommand
{

    public WhisperCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    /**
     * Special case again, because this whisper has a very different message
     * when whispering to people. Other people will not understand you.
     *
     * @param aUser
     * @param aTarget
     * @param message
     * @return the room
     * @throws MudException
     */
    @Override
    protected DisplayInterface actionTo(User aUser, Person aTarget, String verb, String message) throws MudException
    {
        if (message == null)
        {
            // "whisper to Marvin", whisper what?
            return null;
        }
//        if (aUser.isIgnored(toChar))
//        {
//            aUser.writeMessage(toChar.getName()
//                    + " is ignoring you fully.<BR>\r\n");
//            return true;
//        }

        CommunicationService.getCommunicationService(aUser).writeMessage("<B>You whisper [to " + aTarget.getName()
                + "]</B> : " + message + "<BR>\r\n");
        CommunicationService.getCommunicationService(aTarget).writeMessage("<B>" + aUser.getName()
                + " whispers [to you]</B> : " + message + "<BR>\r\n");
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessageExcl(
                aUser,
                aTarget,
                "%SNAME %SISARE whispering something to %TNAME, but you cannot hear what.<BR>\r\n");
        return aUser.getRoom();
    }

    @Override
    public CommType getCommType()
    {
        return CommType.WHISPER;
    }
}
