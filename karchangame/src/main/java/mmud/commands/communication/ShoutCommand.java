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
import mmud.database.entities.game.Room;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Shout to someone "shout Help!". Or just shout in general. The interesting
 * part about this command is, because shouting is louder in general than simple
 * talking, that the shout can be heard in neighbouring rooms.
 * @author maartenl
 */
public class ShoutCommand extends CommunicationCommand
{

    public ShoutCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    protected DisplayInterface actionTo(User aUser, Person aTarget, String verb, String message) throws MudException
    {
        DisplayInterface result = super.actionTo(aUser, aTarget, verb, message);
        if (result == null)
        {
            return null;
        }
        shoutToNeighboringRooms(aUser, message);
        return result;
    }

    @Override
    protected DisplayInterface action(User aUser, String verb, String message) throws MudException
    {
        DisplayInterface result = super.action(aUser, verb, message);
        if (result == null)
        {
            return null;
        }
        shoutToNeighboringRooms(aUser, message);
        return result;

    }

    private void shoutToNeighboringRooms(User aUser, String message) throws MudException
    {

        Room room = aUser.getRoom();
        if (room.getSouth() != null)
        {
            CommunicationService.getCommunicationService(room.getSouth()).sendMessage(aUser, "Someone shouts : " + message + ".<BR>\r\n");
        }
        if (room.getNorth() != null)
        {
          CommunicationService.getCommunicationService(            room.getNorth()).sendMessage(aUser, "Someone shouts : " + message + ".<BR>\r\n");
        }
        if (room.getWest() != null)
        {
            CommunicationService.getCommunicationService(room.getWest()).sendMessage(aUser, "Someone shouts : " + message + ".<BR>\r\n");
        }
        if (room.getEast() != null)
        {
            CommunicationService.getCommunicationService(room.getEast()).sendMessage(aUser, "Someone shouts : " + message + ".<BR>\r\n");
        }
        if (room.getUp() != null)
        {
            CommunicationService.getCommunicationService(room.getUp()).sendMessage(aUser, "Someone shouts : " + message + ".<BR>\r\n");
        }
        if (room.getDown() != null)
        {
            CommunicationService.getCommunicationService(room.getDown()).sendMessage(aUser, "Someone shouts : " + message + ".<BR>\r\n");
        }
    }

    @Override
    public CommType getCommType()
    {
        return CommType.SHOUT;
    }
}
