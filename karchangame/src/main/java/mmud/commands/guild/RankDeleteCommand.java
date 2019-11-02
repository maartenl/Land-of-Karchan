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
import mmud.rest.services.LogBean;
import mmud.services.CommunicationService;
import mmud.services.PersonCommunicationService;

/**
 * Makes you, as guildmaster, delete a rank of the guild.
 * Bear in mind this will not work if there are still people who have that
 * specific rank.
 * Command syntax something like : <TT>guilddelrank &lt;position&gt;</TT>
 * Where position between 0 and 100, where 0 is Initiate and 100 is ultimate bossman.
 *
 * @author maartenl
 */
public class RankDeleteCommand extends GuildMasterCommand
{

  public RankDeleteCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    LogBean logBean = getLogBean();
    String[] myParsed = parseCommand(command);
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
      communicationService.writeMessage("Rank not found.<BR>\r\n");
      return aUser.getRoom();
    }
    // TODO: verify that nobody is using the rank.
    aUser.getGuild().deleteGuildrank(rank);
    communicationService.writeMessage("Rank removed.<BR>\r\n");
    logBean.writeLog(aUser, " rank removed " + rankIndex
            + " from guild " + aUser.getGuild().getName());
    return aUser.getRoom();
  }

}
