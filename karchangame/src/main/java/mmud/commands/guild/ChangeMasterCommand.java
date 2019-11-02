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
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.rest.services.LogBean;
import mmud.services.CommunicationService;
import mmud.services.PersonCommunicationService;

/**
 * Makes you, as guildmaster, promote somebody else as guildmaster. There are
 * some requirements to follow:
 * <UL>
 * <LI>the user must exist and be a normal player
 * <LI>the user must be online
 * <LI>the user must be a member of the guild
 * </UL>
 * Command syntax something like : <TT>guildmasterchange &lt;username&gt;</TT>
 *
 * @author maartenl
 */
public class ChangeMasterCommand extends GuildMasterCommand
{

    public ChangeMasterCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
        LogBean logBean = getLogBean();
        String[] myParsed = parseCommand(command);
        // TODO : casting is nasty, there must be a better way!
        Person toChar2 = aUser.getRoom().retrievePerson(myParsed[1]);
        if ((toChar2 == null) || (!(toChar2 instanceof User)))
        {
            communicationService.writeMessage("Cannot find that person.<BR>\r\n");
            return aUser.getRoom();
        }

        User toChar = (User) toChar2;
        if (toChar.getGuild() == null
                || (!toChar.getGuild().getName().equals(
                        aUser.getGuild().getName())))
        {
            throw new MudException(
                    "error occurred, the person to promote to guildmaster is either not in a guild or not in the correct guild.");
        }
        logBean.writeLog(aUser, " stepped down as guildmaster of "
                + aUser.getGuild().getName() + " in favor of "
                + toChar.getName());
        aUser.getGuild().setBoss(toChar);
        CommunicationService.getCommunicationService(aUser.getGuild()).sendMessage(toChar.getName()
                + " is now the guildmaster.<BR>\r\n");
        CommunicationService.getCommunicationService(toChar).writeMessage("You are now the guildmaster of <I>"
                + aUser.getGuild().getTitle() + "</I>.<BR>\r\n");
        return aUser.getRoom();
    }
}
