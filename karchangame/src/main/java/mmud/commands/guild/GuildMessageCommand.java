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
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;
import mmud.services.LogService;

/**
 * Makes you, as guildmaster, change the logonmessage of your guild.
 * Command syntax something like : <TT>guildmessage &lt;the message&gt;</TT>
 *
 * @author maartenl
 */
public class GuildMessageCommand extends GuildMasterCommand
{

    public GuildMessageCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        LogService logService = getLogService();

        String message = command.substring("guildmessage".length() + 1).trim();
        aUser.getGuild().setLogonmessage(message);

        CommunicationService.getCommunicationService(aUser).writeMessage("You have changed your guild logonmessage to <i>" + message + "</i>.<br/>\r\n");
        logService.writeLog(aUser, " changed guildmessage of  " + aUser.getGuild().getName() + ".", message);
        return aUser.getRoom();
    }

}
