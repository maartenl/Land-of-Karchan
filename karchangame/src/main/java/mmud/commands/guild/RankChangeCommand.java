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

import mmud.commands.GuildMasterCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guildrank;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;
import mmud.services.LogService;
import mmud.services.PersonCommunicationService;

/**
 * Makes you, as guildmaster, create or change a rank of the guild.
 * Command syntax something like : <TT>guildaddrank &lt;position&gt; &lt;ranktitle&gt;</TT>
 * Where position between 0 and 100, where 0 is Initiate and 100 is ultimate bossman.
 *
 * @author maartenl
 */
public class RankChangeCommand extends GuildMasterCommand
{

  public RankChangeCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    LogService logService = getLogService();
    String[] myParsed = parseCommand(command, 3);
    String rankString = myParsed[1];
    int rankIndex = 0;
    try
    {
      rankIndex = Integer.valueOf(rankString);
    } catch (NumberFormatException e)
    {
      communicationService.writeMessage("Ranknumber not a number.<BR>\r\n");
      return aUser.getRoom();
    }
    Guildrank rank = aUser.getGuild().getRank(rankIndex);
    if (rank == null)
    {
      communicationService.writeMessage("New rank created.<BR>\r\n");
      rank = new Guildrank(rankIndex, aUser.getGuild().getName());
      rank.setGuild(aUser.getGuild());
      aUser.getGuild().addGuildrank(rank);
    } else
    {
      communicationService.writeMessage("Existing rank updated.<BR>\r\n");
    }
    rank.setTitle(myParsed[2]);
    logService.writeLog(aUser, " rank created/updated " + rankIndex + ":" + myParsed[2]
      + " for guild " + aUser.getGuild().getName());
    return aUser.getRoom();
  }

}
