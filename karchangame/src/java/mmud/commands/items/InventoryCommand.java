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

import java.util.HashMap;
import java.util.Map;
import mmud.exceptions.MudException;
import mmud.database.entities.characters.User;
import mmud.commands.NormalCommand;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;

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
        if ((aUser.getItems() == null || aUser.getItems().isEmpty()) && aUser.getCopper() == 0)
        {
            builder.append(" absolutely nothing.</p>");
        } else
        {
            Map<String, Integer> inventory = new HashMap<>();

            for (Item item : aUser.getItems())
            {
                String key = item.getDescription();
                if (inventory.containsKey(key))
                {
                    inventory.put(key, inventory.get(key) + 1);
                } else
                {
                    inventory.put(key, 1);
                }
            }
            builder.append("<ul>");
            for (String desc : inventory.keySet())
            {
                builder.append("<li>");
                int amount = inventory.get(desc);
                if (amount != 1)
                {
                    // 5 gold, hard cups
                    if (desc.startsWith("an"))
                    {
                        // remove 'an '
                        desc = desc.substring(3);
                    } else
                    {
                        // remove 'a '
                        desc = desc.substring(2);
                    }
                    builder.append(
                             amount + " " + desc);
                    if (!desc.endsWith("s"))
                    {
                        builder.append("s");
                    }
                } else
                {
                    // a gold, hard cup
                    builder.append(desc);
                }
                builder.append("</li>");
            }
            builder.append("</ul><ul><li>");
            builder.append(aUser.getDescriptionOfMoney());
            builder.append("</li></ul>");
        }
        final String body = builder.toString();
        return new DisplayInterface()
        {
            @Override
            public String getTitle()
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
