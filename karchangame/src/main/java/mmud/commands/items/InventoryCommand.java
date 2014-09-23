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
package mmud.commands.items;

import mmud.Constants;
import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * Show the inventory: "inventory".
 */
public class InventoryCommand extends NormalCommand
{

    public InventoryCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        StringBuilder builder = new StringBuilder("<p>You have");
        Constants.addInventory(aUser.getItems(), builder);
        builder.append("You have ");
        builder.append(aUser.getDescriptionOfMoney());
        builder.append(".<br/>\r\n");

        final String body = builder.toString();

        return new DisplayInterface()
        {
            @Override
            public String getMainTitle()
            {
                return "Inventory";
            }

            @Override
            public String getImage()
            {
                return "/images/gif/money.gif";
            }

            @Override
            public String getBody()
            {
                return body;
            }
        };
    }
}
