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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import mmud.Attributes;
import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.exceptions.MudException;
import mmud.rest.services.GuildBean;

/**
 * Makes you apply to a guild. There are some requirements to follow:
 * <UL>
 * <LI>the guild must exist
 * <LI>you must not already belong to a guild
 * </UL>
 * Command syntax something like : <TT>guildapply &lt;guildname&gt;</TT>
 * @author maartenl
 */
public class ApplyCommand extends NormalCommand
{

    public ApplyCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] myParsed = parseCommand(command);
        if (myParsed.length == 1)
        {
            aUser.removeAttribute("guildwish");
            aUser.writeMessage("You have no longer applied to any guild.<BR>\r\n");
            return aUser.getRoom();
        }
        Guild guild = null;
        GuildBean guildBean;
        try
        {
            guildBean = (GuildBean) new InitialContext().lookup("java:module/GuildBean");
        } catch (NamingException e)
        {
            throw new MudException("Unable to retrieve guild.", e);
        }

        guild = guildBean.getGuild(myParsed[1]);
        if (guild == null)
        {
            aUser.writeMessage("Unable to find guild <I>" + myParsed[1]
                    + "</I>.<BR>\r\n");
            return aUser.getRoom();
        }
        if (aUser.getGuild() != null)
        {
            aUser.writeMessage("You already belong to guild <I>"
                    + aUser.getGuild().getTitle() + "</I>.<BR>\r\n");
            return aUser.getRoom();
        }
        aUser.setAttribute(Attributes.GUILDWISH, guild.getName());
        aUser.writeMessage("You have applied to guild <I>" + guild.getTitle()
                + "</I>.<BR>\r\n");
        return aUser.getRoom();
    }
}
