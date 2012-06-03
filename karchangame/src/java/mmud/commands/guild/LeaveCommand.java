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

import mmud.commands.GuildCommand;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.exceptions.MudException;
import mmud.rest.services.LogBean;

/**
 * Makes you leave a guild. There are some requirements to follow:
 * <UL>
 * <LI>you must already belong to a guild
 * </UL>
 * Command syntax something like : <TT>guildleave</TT>
 * @author maartenl
 */
public class LeaveCommand extends GuildCommand
{

    public LeaveCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        LogBean logBean;
        try
        {
            logBean = (LogBean) new InitialContext().lookup("java:module/LogBean");

        } catch (NamingException e)
        {
            throw new MudException("Unable to retrieve person.", e);
        }
        Guild guild = aUser.getGuild();
        aUser.removeAttribute("guildrank");
        // aUser.getGuild().decreaseAmountOfMembers();
        aUser.setGuild(null);
        aUser.writeMessage("You leave guild <I>" + guild.getTitle() + "</I>.<BR>\r\n");
        logBean.writeLog(aUser, "left guild "
                + guild.getName());
        // guild.decreaseAmountOfMembers();
        return aUser.getRoom();
    }

}
