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
package mmud.commands.guild;

import java.time.LocalDateTime;

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;
import mmud.services.LogService;

/**
 * Creates a guild. Command syntax something like : <TT>createguild &lt;name&gt;
 * &lt;title&gt;</TT>
 * Requirements:
 * <ul><li>you are not in a guild right now</li>
 * <li>you are also not already the guildboss of a guild.</li></ul>
 *
 * @author maartenl
 */
public class CreateGuildCommand extends NormalCommand
{

  public CreateGuildCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    if (aUser.getGuild() != null)
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("You are a member of a guild, and can therefore not start a new guild.<br/>\n");
      return aUser.getRoom();
    }
    LogService logService = getLogService();
    String[] myParsed = parseCommand(command, 3);
    Guild guild = new Guild();
    guild.setBoss(aUser);
    guild.setActive(Boolean.TRUE);
    guild.setCreation(LocalDateTime.now());
    guild.setName(myParsed[1]);
    guild.setTitle(myParsed[2]);
    aUser.setGuild(guild);
    CommunicationService.getCommunicationService(aUser).writeMessage("Guild " + myParsed[1] + " created.<br/ >\r\n");
    logService.writeLog(aUser, " guild " + myParsed[1] + " created/updated");
    return aUser.getRoom();
  }

}
