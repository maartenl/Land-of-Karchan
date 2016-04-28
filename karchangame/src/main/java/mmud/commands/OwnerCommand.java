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

import java.util.Optional;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * Owner : "owner", "owner remove" and "owner Karn".
 *
 * @author maartenl
 */
public class OwnerCommand extends NormalCommand
{

    /**
     * @param aRegExpr See {@link NormalCommand#NormalCommand(java.lang.String)}
     */
    public OwnerCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        if (command.equalsIgnoreCase("owner"))
        {
            if (aUser.getOwner() == null)
            {
                aUser.writeMessage("You are not owned.<br/>");
            } else
            {
                aUser.writeMessage("Your current owner is " + aUser.getOwner().getName() + ".<br/>");
            }
            return aUser.getRoom();
        }
        if (command.equalsIgnoreCase("owner remove"))
        {
            getLogBean().writeLog(aUser, "has removed owner " + (aUser.getOwner() == null ? "null" : aUser.getOwner().getName()));
            aUser.setOwner(null);
            aUser.writeMessage("Owner removed.<br/>");
            return aUser.getRoom();
        }
        String adminName = command.substring("owner ".length());
        Optional<Admin> administrator = getAdminBean().getAdministrator(adminName);
        if (administrator.isPresent())
        {
            Admin admin = administrator.get();
            aUser.setOwner(admin);
            aUser.writeMessage("You are now owned by " + admin.getName() + ".<br/>");
            getLogBean().writeLog(aUser, "has set owner to " + aUser.getOwner().getName());
            return aUser.getRoom();
        }
        aUser.writeMessage("Owner " + adminName + " is unknown.<br/>");
        return aUser.getRoom();
    }

}
