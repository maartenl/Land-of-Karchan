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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import mmud.commands.NormalCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.Shopkeeper;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.database.enums.God;
import mmud.exceptions.MudException;
import mmud.rest.services.ItemBean;

/**
 * Sells an item to a shopkeeper. Syntax : sell [&lt;amount&gt;] &lt;item&gt; to &lt;character&gt;.
 * For example "sell bucket to Karcas".
 *
 *
 * @see BuyCommand
 * @author maartenl
 */
public class SellCommand extends NormalCommand
{

    public SellCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    /**
     * Tries out the sell command. There are a couple of requirements that need
     * to be met, before a successful sale takes place.
     * <ol>
     * <li>command struct. should be "<I>sell [&lt;amount&gt;] &lt;item&gt; to
     * &lt;character&gt;</I>", for example: "<I>sell gold ring to Karcas</I>".</li>
     * <li>keeper buying the item should</li>
     * <ol>
     * <li>exist,</li>
     * <li>be in the same room and</li>
     * <li>
     * have a god==4 to indicate a "keeper" and</li>
     * <li>has enough money</li>
     * </ol>
     * <li>the customer should have the item</li>
     * <li>the item is not being wielded</li>
     * <li>the item is not being worn</li>
     * <li>the item itself should <I>NOT</I> have a attribute called
     * "notsellable".</li>
     * <li>the item should not contain any items</li>
     * </ol>
     * A best effort is tried, this means the following sequence of events:
     * <ol>
     * <li>the item is transferred into the inventory of the keeper</li>
     * <li>money is transferred into the inventory of the customer</li>
     * <li>continue with next item</li>
     * </ol>
     *
     * @param command the command entered.
     * @param aUser the character doing the selling.
     * @return a simple displayinterface, in our case the room.
     */
    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "sell"
        String shopkeeperName = parsed.get(parsed.size() - 1);
        parsed.remove(parsed.size() - 1); // remove shopkeeper
        parsed.remove(parsed.size() - 1); // remove "to"
        int amount = 1;
        try
        {
            amount = Integer.parseInt(parsed.get(0));
            parsed.remove(0);
        } catch (NumberFormatException e)
        {// do nothing here, we assume we need to drop only one item.
        }
        if (amount <= 0)
        {
            aUser.writeMessage("That is an illegal amount.<br/>\n");
            return aUser.getRoom();
        }
        // find the item on ourselves
        List<Item> itemsFound = aUser.findItems(parsed);
        if (itemsFound.isEmpty())
        {
            aUser.writeMessage("You don't have that.<br/>\n");
            return aUser.getRoom();
        }
        if (itemsFound.size() < amount)
        {
            aUser.writeMessage("You do not have that many items in your inventory.<br/>\r\n");
            return aUser.getRoom();
        }
        Person keeper = aUser.getRoom().getPerson(shopkeeperName);
        if (keeper == null)
        {
            aUser.writeMessage("Unable to locate shopkeeper.<br/>\r\n");
            return aUser.getRoom();
        }
        if (keeper.getGod() != God.SHOPKEEPER)
        {
            aUser.writeMessage("That's not a shopkeeper!<br/>\r\n");
            return aUser.getRoom();
        }
        Shopkeeper shopkeeper = (Shopkeeper) keeper;
        boolean sold = false;
        ItemBean itemBean;
        try
        {
            itemBean = (ItemBean) new InitialContext().lookup("java:module/ItemBean");

        } catch (NamingException e)
        {
            throw new MudException("Unable to sell item.", e);
        }
        for (Item item : itemsFound)
        {
            // item is not used.
            if (item.getCopper() <= 1)
            {
                String message = "That item is not worth anything.";
                aUser.getRoom().sendMessage(shopkeeper, aUser,
                        "%SNAME say%VERB2 [to %TNAME] : " + message
                        + "<br/>\r\n");
                continue;
            }
            if (shopkeeper.getCopper() < item.getCopper())
            {
                aUser.writeMessage(shopkeeper.getName()
                        + " mutters something about not having enough money.<br/>\r\n");
                break;
            }
            if (!aUser.unused(item))
            {
                aUser.writeMessage("You are wearing or wielding this item.<BR>\r\n");
                continue;
            }
            if (!item.isSellable())
            {
                aUser.writeMessage("You cannot sell that item.<BR>\r\n");
                continue;
            }
            if (!itemBean.sell(item, aUser, shopkeeper))
            {
                continue;
            }
            aUser.getRoom().sendMessage(aUser, "%SNAME sold%VERB2 " + item.getDescription() + " to " + shopkeeper.getName() + ".<br/>\r\n");
            sold = true;
            amount--;
            if (amount == 0)
            {
                return aUser.getRoom();
            }

        }
        if (!sold)
        {
            aUser.writeMessage("You did not sell anything.<br/>\r\n");
        } else
        {
            aUser.writeMessage("You sold some of the items.<br/>\r\n");
        }
        return aUser.getRoom();
    }
}
