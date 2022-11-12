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

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudException;
import mmud.exceptions.PersonNotFoundException;
import mmud.services.CommunicationService;
import mmud.services.ItemService;
import mmud.services.PersonCommunicationService;

/**
 * Give an item in your inventory to another character. Syntax: "give
 * &lt;amount&gt; &lt;item&gt; to &lt;character&gt;". It is also possible to
 * provide money to the other person, by means of "give &lt;amount&gt;
 * &lt;copper, silver, gold&gt; &lt;coin|coins&gt; to &lt;character&gt;".
 *
 * @see GetCommand
 * @author maartenl
 */
public class GiveCommand extends NormalCommand
{

  public GiveCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
    parsed.remove(0); // remove "give"
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
      communicationService.writeMessage("That is an illegal amount.<br/>\n");
      return aUser.getRoom();
    }
    Person target = aUser.getRoom().retrievePerson(parsed.get(parsed.size() - 1));
    if (target == null)
    {
      // action to unknown
      throw new PersonNotFoundException("Cannot find " + parsed.get(parsed.size() - 1) + ".<BR>\r\n");
    }
    if (!target.canReceive())
    {
      communicationService.writeMessage(target.getName() + " doesn't accept your stuff.<br/>\n");
      return aUser.getRoom();
    }
    parsed.remove(parsed.size() - 1);
    parsed.remove(parsed.size() - 1);
    if (giveMoney(parsed, amount, aUser, target))
    {
      return aUser.getRoom();
    }
    giveItems(aUser, parsed, amount, target);
    return aUser.getRoom();
  }

  private boolean giveMoney(List<String> parsed, int amount, User aUser, Person target) throws MudException
  {
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    // check for money
    // "give [x] [gold,silver,copper] coin[s] to <person>"
    if (parsed.size() != 2 || (!"coin".equalsIgnoreCase(
            parsed.get(1)) && !"coins".equalsIgnoreCase(parsed.get(1))))
    {
      return false;
    }
    int newamount = amount;
    String currency = parsed.get(0).toLowerCase();
    switch (currency)
    {
      case "gold":
        newamount = newamount * 10;
      case "silver":
        newamount = newamount * 10;
      case "copper":
        // do nothing.
        break;
      default:
        // if currency not in [gold,silver,copper]
        return false;
    }
    // if amount screwy
    if (newamount < 0)
    {
      return false;
    }
    aUser.transferMoney(newamount, target);
    CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, target, "%SNAME give%VERB2 "
            + amount + " " + currency + " coin"
            + (amount == 1 ? "" : "s")
            + " to %TNAME.<br/>\r\n");
    return true;
  }

  private void giveItems(User aUser, List<String> parsed, int amount, Person target) throws MudException
  {
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    // find the item on ourselves
    List<Item> itemsFound = aUser.findItems(parsed);
    if (itemsFound.isEmpty())
    {
      communicationService.writeMessage("You don't have that.<br/>\n");
      return;
    }
    if (itemsFound.size() < amount)
    {
      communicationService.writeMessage("You do not have that many items in your inventory.<br/>\r\n");
      return;
    }
    boolean given = false;
    ItemService itemService = getItemBean();
    for (Item item : itemsFound)
    {
      if (aUser.unused(item) && !item.isBound())
      {
        // item is not used.
        if (!itemService.give(item, aUser, target))
        {
          continue;
        }
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, target, "%SNAME give%VERB2 " + item.getDescription() + " to %TNAME.<br/>\r\n");
        given = true;
        amount--;
        if (amount == 0)
        {
          return;
        }
      }
    }
    if (!given)
    {
      communicationService.writeMessage("You did not give anything.<br/>");
    } else
    {
      communicationService.writeMessage("You gave some of the items.<br/>\r\n");
    }
  }
}
