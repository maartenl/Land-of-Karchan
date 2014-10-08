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
import mmud.Attributes;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.rest.services.LogBean;
import mmud.rest.services.PersonBean;

/**
 * Makes you, as guildmaster, accept a new member to the guild. There are some
 * requirements to follow:
 * <UL>
 * <LI>the user must exist and be a normal player
 * <LI>the user must have a <I>guildwish</I>
 * <LI>the user must not already be a member of a guild
 * </UL>
 * Command syntax something like : <TT>guildaccept &lt;username&gt;</TT>
 * @author maartenl
 */
public class AcceptCommand extends GuildMasterCommand
{

    public AcceptCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        // TODO : similar to rejectcommand, refactor?
        PersonBean personBean = getPersonBean();
        LogBean logBean = getLogBean();
        String[] myParsed = parseCommand(command);
        User potentialGuildmember = personBean.getUser(myParsed[1]);
        if (potentialGuildmember == null)
        {
            aUser.writeMessage("Cannot find that person.<BR>\r\n");
            return aUser.getRoom();
        }
        if (!potentialGuildmember.verifyAttribute(Attributes.GUILDWISH, aUser.getGuild().getName()))
        {
            aUser.writeMessage(potentialGuildmember.getName()
                    + " does not wish to join your guild.<BR>\r\n");
            return aUser.getRoom();
        }
        if (potentialGuildmember.getGuild() != null)
        {
            throw new MudException(
                    "error occurred, a person is a member of a guild, yet has a guildwish parameter!");
        }
        potentialGuildmember.removeAttribute(Attributes.GUILDWISH);
        potentialGuildmember.setGuild(aUser.getGuild());
        // TODO : does this need work?
        // aUser.getGuild().increaseAmountOfMembers();
        logBean.writeLog(aUser, " accepted " + potentialGuildmember.getName()
                + " into guild " + aUser.getGuild().getName());
        aUser.writeMessage(potentialGuildmember.getName()
                + " has joined your guild.<BR>\r\n");
        if (potentialGuildmember.isActive())
        {
            potentialGuildmember.writeMessage("You have joined guild <I>"
                    + aUser.getGuild().getTitle() + "</I>.<BR>\r\n");
        }
        // aUser.getGuild().increaseAmountOfMembers();
        return aUser.getRoom();
    }

}
