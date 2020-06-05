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
import mmud.database.entities.game.Guild;
import mmud.exceptions.MudException;

/**
 *
 * @author maartenl
 */
public class GuildCommunicationService implements CommunicationService
{

  private Guild guild;

  GuildCommunicationService(Guild guild)
  {
    if (guild == null)
    {
      throw new NullPointerException();
    }
    this.guild = guild;
  }

  /**
   * communication method to everyone in the guild, that is active. The message
   * is not parsed. Bear in mind that this method should only be used for
   * communication about environmental issues. If the communication originates
   * from a User/Person, you should use sendMessage(aPerson, aMessage).
   * Otherwise the Ignore functionality will be omitted.
   *
   * @param aMessage the message
   * @throws MudException if the room is not correct
   * @see Person#writeMessage(java.lang.String)
   */
  public void sendMessage(String aMessage)
          throws MudException
  {
    for (Person myChar : guild.getActiveMembers())
    {
      CommunicationService.getCommunicationService(myChar).writeMessage(aMessage);
    }
  }

  /**
   * character communication method to everyone in the guild. The message is
   * parsed, based on who is sending the message.
   * <p>
   * Example output: &lt;span style="color:red;"&gt;[guild] Karn: Hello,
   * everyone!&lt;/br&gt;&lt;/span&gt;</p>
   *
   * @param aPerson the person who is the source of the message.
   * @param aMessage the message
   *
   * @see Person#writeMessage(mmud.database.entities.characters.Person,
   * java.lang.String)
   */
  public void sendMessage(Person aPerson, String aMessage) throws MudException
  {
    String message = "<span style=\"color:" + guild.getColour() + ";\">[guild]<b>" + aPerson.getName() + "</b>: " + aMessage + "</span><br/>\r\n";
    for (Person myChar : guild.getActiveMembers())
    {
      CommunicationService.getCommunicationService(myChar).writeMessage(aPerson, message);
    }
  }

}
