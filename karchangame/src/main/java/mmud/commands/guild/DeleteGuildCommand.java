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

import mmud.commands.*;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.exceptions.MudException;
import mmud.rest.services.LogBean;

/**
 * Deletes a guild.
 * Command syntax something like : <TT>deleteguild</TT>
 * Requirements:
 * <ul><li>you are the guildmaster of this guild</li></ul>
 *
 * @author maartenl
 */
public class DeleteGuildCommand extends GuildMasterCommand
{

  public DeleteGuildCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    Guild guild = aUser.getGuild();
    if (guild == null)
    {
      aUser.writeMessage("You are not a member of a guild.<br/>\n");
      return aUser.getRoom();
    }
    LogBean logBean = getLogBean();
    String guildName = guild.getName();
    getGuildBean().deleteGuild(aUser.getName());
    aUser.setGuild(null);
    aUser.writeMessage("Guild " + guildName + " deleted.<br/ >\r\n");
    logBean.writeLog(aUser, " guild " + guildName + " deleted");
    return aUser.getRoom();
  }

}