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
 * Buys an item from a shopkeeper. Syntax : buy [&lt;amount&gt;] &lt;item&gt; from &lt;character&gt;.
 * For example "buy bucket to Karcas".
 *
 *
 * @see SellCommand
 * @author maartenl
 */
public class BuyCommand extends NormalCommand
{

    public BuyCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    /**
     * Tries out the buy command. There are a couple of requirements that need
     * to be met, before a successful sale takes place.
     * <ol>
     * <li>command struct. should be "<I>buy [&lt;amount&gt;] &lt;item&gt; from
     * &lt;character&gt;</I>", for example: "<I>sell gold ring to Karcas</I>".
     * <li>shopkeeper selling the item should
     * <ol>
     * <li>exist,
     * <li>be in the same room and
     * <li>
     * have a god==4 to indicate a "shopkeeper" and
     * <li>has the item for sale
     * </ol>
     * <li>the customer should have the money
     * <li>the item itself should <I>NOT</I> have a attribute called
     * "notbuyable".
     * <li>the item should not contain any items</li>
     * </ol>
     * A best effort is tried, this means the following sequence of events:
     * <ol>
     * <li>the item is transferred into the inventory of the user
     * <li>money is transferred into the inventory of the shopkeeper
     * <li>continue with next item
     * </ol>
     *
     * @param command the command entered.
     * @param aUser the character doing the buying.
     * @return a simple displayinterface, in our case the room.
     */
    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "buy"
        String shopkeeperName = parsed.get(parsed.size() - 1);
        parsed.remove(parsed.size() - 1); // remove shopkeeper
        parsed.remove(parsed.size() - 1); // remove "from"
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
        // find the item on the shopkeeper
        List<Item> itemsFound = shopkeeper.findItems(parsed);
        if (itemsFound.isEmpty())
        {
            aUser.writeMessage("The shopkeeper doesn't have that.<br/>\n");
            return aUser.getRoom();
        }
        if (itemsFound.size() < amount)
        {
            aUser.writeMessage("The shopkeeper doesn't have that many items in stock.<br/>\r\n");
            return aUser.getRoom();
        }
        boolean bought = false;
        ItemBean itemBean = getItemBean();
        for (Item item : itemsFound)
        {
            // item is not used.
            if (item.getCopper() == 0)
            {
                String message = "That item is not worth anything.";
                aUser.getRoom().sendMessage(shopkeeper, aUser,
                        "%SNAME say%VERB2 [to %TNAME] : " + message
                        + "<br/>\r\n");
                continue;
            }
            if (!item.isBuyable())
            {
                aUser.writeMessage("You cannot buy that item.<BR>\r\n");
                continue;
            }
            if (aUser.getCopper() < item.getCopper())
            {
                aUser.writeMessage("You do not have enough money.<br/>\r\n");
                break;
            }
            if (!itemBean.buy(item, aUser, shopkeeper))
            {
                continue;
            }
            aUser.getRoom().sendMessage(aUser, "%SNAME buy%VERB2 " + item.getDescription() + " from " + shopkeeper.getName() + ".<br/>\r\n");
            bought = true;
            amount--;
            if (amount == 0)
            {
                return aUser.getRoom();
            }
        }
        if (!bought)
        {
            aUser.writeMessage("You did not buy anything.<br/>\r\n");
        } else
        {
            aUser.writeMessage("You bought some of the items.<br/>\r\n");
        }
        return aUser.getRoom();
    }

}
