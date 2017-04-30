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

/**
 * <p>
 * Makes you, as guildmaster, assign a rank to a member of the guild.
 * Command syntax something like : <TT>guildassignrank &lt;position&gt; &lt;guildmember&gt;</TT>
 * Where position is the index of the rank.</p>
 * <p>
 * A special version of this command is the command to remove a rank. For example:
 * <TT>guildassignrank none &lt;guildmember&gt;</TT>. It removed the rank of the guildmember
 * leaving him or her guildrankless.</p>
 * <p>
 * I think, but I'm not sure, that the guildmember is required to be online in the game.</p>
 *
 * @author maartenl
 */
public class RankAssignCommand extends GuildMasterCommand
{

  public RankAssignCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    LogBean logBean = getLogBean();
    String[] myParsed = parseCommand(command, 3);
    String rankString = myParsed[1];
    String memberName = myParsed[2];
    User member = aUser.getGuild().getMember(memberName);
    if (member == null)
    {
      aUser.writeMessage("Guild member not found.<BR>\r\n");
      return aUser.getRoom();
    }
    if (rankString.equalsIgnoreCase("none"))
    {
      member.setGuildrank(null);
      aUser.writeMessage("Rank removed from guild member " + member.getName() + ".<BR>\r\n");
      logBean.writeLog(aUser, " rankremoved from " + memberName
              + " for guild " + aUser.getGuild().getName());
      return aUser.getRoom();
    }
    int rankIndex = 0;
    try
    {
      rankIndex = Integer.valueOf(rankString);
    } catch (NumberFormatException e)
    {
      aUser.writeMessage("Ranknumber not a number.<BR>\r\n");
      return aUser.getRoom();
    }
    Guildrank rank = aUser.getGuild().getRank(rankIndex);
    if (rank == null)
    {
      aUser.writeMessage("Rank does not exist.<BR>\r\n");
      return aUser.getRoom();
    }
    member.setGuildrank(rank);
    aUser.writeMessage("Rank " + rank.getTitle() + " assigned to guild member " + member.getName() + ".<BR>\r\n");
    logBean.writeLog(aUser, " rank " + rankIndex + " assigned to " + memberName
            + " for guild " + aUser.getGuild().getName());
    return aUser.getRoom();
  }

}
