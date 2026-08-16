package mmud.commands.items;

import mmud.commands.NormalCommand;
import mmud.database.OutputFormatter;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.Shopkeeper;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.database.enums.God;
import mmud.exceptions.ItemException;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;
import mmud.services.ItemService;
import mmud.services.PersonCommunicationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Showing an item to a bot. Syntax : show &lt;item&gt; to &lt;character&gt;.
 * The one reason why this is useful is when you wish to know the value of a
 * certain item.
 *
 * @see BuyCommand
 * @see SellCommand
 */
public class ShowCommand extends NormalCommand implements ItemCommand
{
  public ShowCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  /**
   * Provides the item to a shopkeeper for appraisal. There are a couple of
   * requirements that need to be met, before a successful appraisal takes
   * place.
   * <ol>
   * <li>command struct. should be "<I>show &lt;item&gt; to
   * &lt;character&gt;</I>", for example: "<I>show gold ring to Karcas</I>".
   * <li>shopkeeper buying the item should
   * <ol>
   * <li>exist,
   * <li>be in the same room and
   * <li>
   * have a god==4 to indicate "shopkeeper"
   * </ol>
   * <li>the customer should have the item
   * </ol>
   * The following sequence of events:
   * <ol>
   * <li>the shopkeeper mentions how much he/she would give for the item.
   * <li>continue with next item
   * </ol>
   *
   * @param aUser the character doing the showing.
   * @throws ItemException                  in case the appropriate items could not be properly
   *                                        processed.
   * @throws mmud.exceptions.ParseException if the item description or the number of items requested is
   *                                        illegal.
   */
  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
    parsed.removeFirst(); // remove "show"
    String shopkeeperName = parsed.getLast();
    parsed.removeLast(); // remove shopkeeper
    parsed.removeLast(); // remove "to"
    // find the item on ourselves
    List<Item> itemsFound = aUser.findItems(parsed);
    if (itemsFound.isEmpty())
    {
      communicationService.writeMessage("You don't have that.<br/>\n");
      return aUser.getRoom();
    }
    Person keeper = aUser.getRoom().getPerson(shopkeeperName);
    if (keeper == null)
    {
      communicationService.writeMessage("Unable to locate shopkeeper.<br/>\r\n");
      return aUser.getRoom();
    }
    if (keeper.getGod() != God.SHOPKEEPER)
    {
      communicationService.writeMessage("That's not a shopkeeper!<br/>\r\n");
      return aUser.getRoom();
    }
    Shopkeeper shopkeeper = (Shopkeeper) keeper;
    for (Item item : itemsFound)
    {
      String message;
      if (!item.isSellable())
      {
        communicationService.writeMessage("You cannot sell that item.<BR>\r\n");
        continue;
      }
      CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, shopkeeper,
        "%SNAME show%VERB2 " + item.getDescription() + " to %TNAME.<br/>\r\n");
      if (ItemService.getMarkdown(item.getCopper()) <= 1)
      {
        message = "A" + item.getDescription().substring(1)
          + " is not worth anything to me.";
      } else
      {
        message = "It's worth " + OutputFormatter.getDescriptionOfMoney(item.getCopper()) + ". I'll give you "
          + OutputFormatter.getDescriptionOfMoney(ItemService.getMarkdown(item.getCopper())) + " for it.";
      }
      CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(shopkeeper, aUser,
        "%SNAME say%VERB2 [to %TNAME] : " + message
          + "<br/>\r\n");
    }
    return aUser.getRoom();
  }

}
