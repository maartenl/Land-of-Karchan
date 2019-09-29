/*
 * Copyright (C) 2019 maartenl
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
package mmud.services;

import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudException;

/**
 *
 * @author maartenl
 */
public class RoomCommunicationService
{

  private final Room room;

  RoomCommunicationService(Room room)
  {
    if (room == null)
    {
      throw new NullPointerException();
    }
    this.room = room;
  }
  
  /**
   * character communication method to everyone in the room. The message is
   * parsed, based on who is sending the message.
   *
   * @param aPerson the person who is the source of the message.
   * @param aMessage the message
   *
   * @see Person#writeMessage(mmud.database.entities.characters.Person,
   * java.lang.String)
   */
  public void sendMessage(Person aPerson, String aMessage) throws MudException
  {
    for (Person myChar : room.getPersons(null))
    {
      CommunicationService.getCommunicationService(myChar).writeMessage(aPerson, aMessage);
    }
  }

  /**
   * character communication method to everyone in the room. The first person is
   * the source of the message. The second person is the target of the message.
   * The message is parsed based on the source and target. Will also be sent to
   * the person doing the communicatin' and the target.
   *
   * @param aPerson the person doing the communicatin'.
   * @param aSecondPerson the person communicated to.
   * @param aMessage the message to be sent
   * @throws MudException if the room is not correct
   * @see Person#writeMessage(mmud.database.entities.characters.Person,
   * mmud.database.entities.characters.Person, java.lang.String)
   */
  public void sendMessage(Person aPerson, Person aSecondPerson,
          String aMessage) throws MudException
  {
    for (Person myChar : room.getPersons(null))
    {
      CommunicationService.getCommunicationService(myChar).writeMessage(aPerson, aSecondPerson, aMessage);
    }
  }

  /**
   * room communication method to everyone in the room. The message is not
   * parsed. Bear in mind that this method should only be used for communication
   * about environmental issues. If the communication originates from a
   * User/Person, you should use {@link #sendMessage(mmud.database.entities.characters.Person, java.lang.String)
   * }. Otherwise the Ignore functionality will be omitted.
   *
   * @param aMessage the message
   * @throws MudException if the room is not correct
   * @see Person#writeMessage(java.lang.String)
   */
  public void sendMessage(String aMessage)
          throws MudException
  {
    for (Person myChar : room.getPersons(null))
    {
      CommunicationService.getCommunicationService(myChar).writeMessage(aMessage);
    }
  }

  /**
   * character communication method to everyone in the room except to the two
   * persons mentioned in the header. The message is parsed based on the source
   * and target.
   *
   * @param aPerson the person doing the communicatin'.
   * @param aSecondPerson the person communicated to.
   * @param aMessage the message to be sent
   * @throws MudException if the room is not correct
   * @see Person#writeMessage(mmud.database.entities.characters.Person,
   * mmud.database.entities.characters.Person, java.lang.String)
   */
  public void sendMessageExcl(Person aPerson, Person aSecondPerson,
          String aMessage) throws MudException
  {
    for (Person myChar : room.getPersons(null))
    {
      if (myChar != aPerson
              && myChar != aSecondPerson)
      {
        CommunicationService.getCommunicationService(myChar).writeMessage(aPerson, aSecondPerson, aMessage);
      }
    }
  }

  /**
   * character communication method to everyone in the room excluded the person
   * mentioned in the parameters. The message is parsed, based on who is sending
   * the message.
   *
   * @param aPerson the person who is the source of the message.
   * @param aMessage the message
   * @throws MudException if the room is not correct
   * @see Person#writeMessage(mmud.database.entities.characters.Person,
   * java.lang.String)
   */
  public void sendMessageExcl(Person aPerson, String aMessage)
          throws MudException
  {
    for (Person myChar : room.getPersons(null))
    {
      if (myChar != aPerson)
      {
        CommunicationService.getCommunicationService(myChar).writeMessage(aPerson, aMessage);
      }
    }
  }

}
