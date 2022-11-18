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
package mmud.commands;

import java.util.List;
import java.util.stream.Collectors;

import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Shows the current active deputies (may not be online in the game).
 *
 * @author maartenl
 */
public class DeputiesCommand extends NormalCommand
{

    /**
     * @param aRegExpr See {@link NormalCommand#NormalCommand(java.lang.String)}
     */
    public DeputiesCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
      List<Admin> administrators = getAdminService().getAdministrators();
      String deps = administrators.stream().map(Admin::getName)
        .collect(Collectors.joining(", "));
      CommunicationService.getCommunicationService(aUser).writeMessage("Current deputies are " + deps + ".<br/>");
      return aUser.getRoom();
    }

}
