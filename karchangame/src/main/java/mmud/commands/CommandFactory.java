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
 * MERCHANTABILITY olr FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmud.commands;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import mmud.constants.Emotions;
import mmud.commands.communication.*;
import mmud.commands.guild.AcceptCommand;
import mmud.commands.guild.ApplyCommand;
import mmud.commands.guild.ChangeMasterCommand;
import mmud.commands.guild.CreateGuildCommand;
import mmud.commands.guild.DeleteGuildCommand;
import mmud.commands.guild.DetailsCommand;
import mmud.commands.guild.GuildMessageCommand;
import mmud.commands.guild.LeaveCommand;
import mmud.commands.guild.MessageCommand;
import mmud.commands.guild.RankAssignCommand;
import mmud.commands.guild.RankChangeCommand;
import mmud.commands.guild.RankDeleteCommand;
import mmud.commands.guild.RejectCommand;
import mmud.commands.guild.RemoveCommand;
import mmud.commands.items.BuyCommand;
import mmud.commands.items.CloseCommand;
import mmud.commands.items.DestroyCommand;
import mmud.commands.items.DisarmCommand;
import mmud.commands.items.DrinkCommand;
import mmud.commands.items.DropCommand;
import mmud.commands.items.EatCommand;
import mmud.commands.items.GetCommand;
import mmud.commands.items.GiveCommand;
import mmud.commands.items.InventoryCommand;
import mmud.commands.items.LockCommand;
import mmud.commands.items.LookAtCommand;
import mmud.commands.items.LookInCommand;
import mmud.commands.items.OpenCommand;
import mmud.commands.items.PutCommand;
import mmud.commands.items.ReadCommand;
import mmud.commands.items.RetrieveCommand;
import mmud.commands.items.SellCommand;
import mmud.commands.items.UndressCommand;
import mmud.commands.items.UnlockCommand;
import mmud.commands.items.UnwearCommand;
import mmud.commands.items.UnwieldCommand;
import mmud.commands.items.WearCommand;
import mmud.commands.items.WieldCommand;
import mmud.commands.movement.DownCommand;
import mmud.commands.movement.EastCommand;
import mmud.commands.movement.GoCommand;
import mmud.commands.movement.NorthCommand;
import mmud.commands.movement.SouthCommand;
import mmud.commands.movement.UpCommand;
import mmud.commands.movement.WestCommand;
import mmud.database.entities.characters.User;
import mmud.exceptions.MudException;

/**
 * The factory that creates commands based on the string entered by the user.
 *
 * @author maartenl
 */
public class CommandFactory
{

  private static final Logger LOGGER = Logger.getLogger(CommandFactory.class.getName());
  private static final CommandCreator BOGUS = () -> new BogusCommand(".+");

  static final CommandCreator AWAKEN = () -> new AwakenCommand("awaken");

  static final CommandCreator ALREADY_ASLEEP = () -> new AlreadyAsleepCommand(".+");

  static final CommandCreator RIBBIT = () -> new RibbitCommand("ribbit");

  static final CommandCreator HEEHAW = () -> new HeehawCommand("heehaw");

  /**
   * Contains mappings from what command people have entered, to what command
   * should be executed. Supports two and one string, for example "look at" or
   * "look", in order to distinguish different commands with the same verb.
   */
  private static final TreeMap<String, CommandCreator> theCommandStructure = new TreeMap<>();
  private static final List<UserCommandInfo> theUserCommandStructure = new CopyOnWriteArrayList<>();

  public interface CommandCreator
  {

    NormalCommand createCommand();
  }

  static
  {
    theCommandStructure.put("bow", () -> new BowCommand("bow( to (\\w)+)?( (\\w)+)?"));
    theCommandStructure.put("me", () -> new MeCommand("me .+"));
    theCommandStructure.put("me's", () -> new MeApostropheCommand("me's .+"));
    // quit command has been replaced with a specific rest service.
    // theCommandStructure.put("quit", new QuitCommand("quit"));
    theCommandStructure.put("sleep", () -> new SleepCommand("sleep"));
    theCommandStructure.put("afk", () -> new AfkCommand("afk|afk .+"));
    theCommandStructure.put("condition", () -> new ConditionCommand("(condition)|(condition .+)"));
    theCommandStructure.put("awaken", () -> new AwakenCommand("awaken"));
    theCommandStructure.put("ask", () -> new AskCommand("ask (to (\\w)+ )?.+"));
    theCommandStructure.put("ooc", () -> new OocCommand("ooc .+"));
    theCommandStructure.put("title", () -> new TitleCommand("(title|title ?.+)"));
    theCommandStructure.put("tell", () -> new TellCommand("tell to (\\w)+ .+"));
    theCommandStructure.put("say", () -> new SayCommand("say (to (\\w)+ )?.+"));
    theCommandStructure.put("macro", () -> new MacroCommand("macro( .+)?"));
    theCommandStructure.put("sing", () -> new SingCommand("sing (to (\\w)+ )?.+"));

    theCommandStructure.put("joinchat", () -> new JoinChatCommand("joinchat (\\w)+"));
    theCommandStructure.put("chat", () -> new ChatCommand("chat (\\w)+ .+"));
    theCommandStructure.put("createchat", () -> new CreateChatCommand("createchat (\\w)+ (\\w)+ (\\w)+"));
    theCommandStructure.put("editchat", () -> new EditChatCommand("editchat (\\w)+ (\\w)+ (\\w)+( (\\w)+)?"));
    theCommandStructure.put("deletechat", () -> new DeleteChatCommand("deletechat (\\w)+"));
    theCommandStructure.put("leavechat", () -> new LeaveChatCommand("leavechat (\\w)+"));

    theCommandStructure.put("cry", () -> new CryCommand("cry (to (\\w)+ )?.+"));
    theCommandStructure.put("shout", () -> new ShoutCommand(
      "shout (to (\\w )+)?.+"));
    theCommandStructure.put("scream", () -> new ScreamCommand(
      "scream (to (\\w )+)?.+"));
    theCommandStructure.put("whisper", () -> new WhisperCommand(
      "whisper (to (\\w)+ )?.+"));
    theCommandStructure.put("clear", () -> new ClearCommand("clear"));
    theCommandStructure.put("time", () -> new TimeCommand("time"));
    theCommandStructure.put("date", () -> new DateCommand("date"));
    theCommandStructure.put("south", () -> new SouthCommand("south"));
    theCommandStructure.put("north", () -> new NorthCommand("north"));
    theCommandStructure.put("east", () -> new EastCommand("east"));
    theCommandStructure.put("west", () -> new WestCommand("west"));
    theCommandStructure.put("s", () -> new SouthCommand("s"));
    theCommandStructure.put("n", () -> new NorthCommand("n"));
    theCommandStructure.put("e", () -> new EastCommand("e"));
    theCommandStructure.put("w", () -> new WestCommand("w"));
    theCommandStructure.put("up", () -> new UpCommand("up"));
    theCommandStructure.put("down", () -> new DownCommand("down"));
    theCommandStructure.put("go", () -> new GoCommand(
            "go (up|down|north|south|east|west)?"));
    theCommandStructure.put("help", () -> new HelpCommand("help( (\\w)+)?"));
    theCommandStructure.put("show ignoring", () -> new IgnoringCommand(
            "show ignoring"));
    theCommandStructure.put("fully", () -> new IgnoreCommand(
            "fully ignore (\\w)+"));
    theCommandStructure.put("acknowledge", () -> new AcknowledgeCommand(
            "acknowledge (\\w)+"));
    theCommandStructure.put("curtsey", () -> new CurtseyCommand(
            "curtsey( to (\\w)+)?"));
    theCommandStructure.put("eyebrow", () -> new EyebrowCommand("eyebrow"));
    theCommandStructure.put("wimpy", () -> new WimpyCommand("wimpy( .+|help)?"));
//        theCommandStructure.put("pkill", new PkillCommand("pkill( (\\w)+)?"));
//        theCommandStructure.put("fight", new FightCommand("fight (\\w)+"));
//        theCommandStructure.put("stop", new FightCommand("stop fighting"));
    theCommandStructure.put("stats", () -> new StatsCommand("stats"));
    theCommandStructure.put("inventory", () -> new InventoryCommand("inventory"));
    theCommandStructure.put("i", () -> new InventoryCommand("i"));
    theCommandStructure.put("drink", () -> new DrinkCommand(
      "drink( (\\w|-)+){1,}"));
    theCommandStructure.put("eat", () -> new EatCommand("eat( (\\w|-)+){1,}"));
    theCommandStructure.put("destroy", () -> new DestroyCommand("destroy( (\\w|-)+){1,}"));
    theCommandStructure.put("wear", () -> new WearCommand(
      "wear( (\\w|-)+){1,} on (\\w)+"));
    theCommandStructure.put("remove", () -> new UnwearCommand(
      "remove from (\\w)+"));
    theCommandStructure.put("undress", () -> new UndressCommand(
      "undress"));
    theCommandStructure.put("disarm", () -> new DisarmCommand(
      "disarm"));
    theCommandStructure.put("wield", () -> new WieldCommand(
      "wield( (\\w|-)+){1,} with (\\w)+"));
    theCommandStructure.put("unwield", () -> new UnwieldCommand(
      "unwield from (\\w)+"));
    theCommandStructure.put("drop", () -> new DropCommand("drop( (\\w|-)+){1,}"));
    theCommandStructure.put("get", () -> new GetCommand("get( (\\w|-)+){1,}"));
    theCommandStructure.put("put", () -> new PutCommand(
      "put( (\\w|-)+){1,} in( (\\w|-)+){1,}"));
    theCommandStructure.put("retrieve", () -> new RetrieveCommand(
      "retrieve( (\\w|-)+){1,} from( (\\w|-)+){1,}"));
    theCommandStructure.put("lock", () -> new LockCommand(
      "lock( (\\w|-)+){1,} with( (\\w|-)+){1,}"));
    theCommandStructure.put("unlock", () -> new UnlockCommand(
      "unlock( (\\w|-)+){1,} with( (\\w|-)+){1,}"));
    theCommandStructure.put("give", () -> new GiveCommand(
      "give( (\\w|-)+){1,} to (\\w)+"));
    theCommandStructure.put("open", () -> new OpenCommand("open( (\\w|-)+){1,}"));
    theCommandStructure.put("close", () -> new CloseCommand(
      "close( (\\w|-)+){1,}"));
    theCommandStructure.put("read", () -> new ReadCommand("read( (\\w|-)+){1,}"));
    theCommandStructure.put("readboard", () -> new ReadBoardCommand(
      "readboard (\\w)+"));
    theCommandStructure.put("post", () -> new PostBoardCommand("post (\\w)+ .+"));
    theCommandStructure.put("l", () -> new LookCommand("l"));
    theCommandStructure.put("look", () -> new LookCommand(
      "look"));
    theCommandStructure.put("look at", () -> new LookAtCommand(
      "look at( (\\w|-)+){1,}"));
    theCommandStructure.put("look in", () -> new LookInCommand(
      "look in( (\\w|-)+){1,}"));
//        theCommandStructure.put("search", new SearchCommand(
//                "search( (\\w|-)+){1,4}"));
    theCommandStructure.put("buy", () -> new BuyCommand(
      "buy( (\\w|-)+){1,} from (\\w)+"));
    theCommandStructure.put("sell", () -> new SellCommand(
      "sell( (\\w|-)+){1,} to (\\w)+"));
//        theCommandStructure.put("show", new ShowCommand(
//                "show( (\\w|-)+){1,4} to (\\w)+"));
//        theCommandStructure.put("title", new TitleCommand("title .+"));
    theCommandStructure.put("admin", () -> new AdminCommand("admin .+"));
    theCommandStructure.put("owner", () -> new OwnerCommand("owner( (\\w)+)?"));
    theCommandStructure.put("deputies", () -> new OwnerCommand("deputies"));
    // guild commands
    theCommandStructure.put("guildapply", () -> new ApplyCommand(
            "guildapply( (\\w)+)?"));
    theCommandStructure.put("guildmessage", () -> new GuildMessageCommand(
            "guildmessage .+"));
    theCommandStructure.put("guildleave", () -> new LeaveCommand("guildleave"));
    theCommandStructure.put("guilddetails", () -> new DetailsCommand(
            "guilddetails"));
    theCommandStructure.put("guildaccept", () -> new AcceptCommand(
            "guildaccept (\\w)+"));
    theCommandStructure.put("guildreject", () -> new RejectCommand(
            "guildreject (\\w)+"));
    theCommandStructure.put("guildrank", () -> new RankChangeCommand(
            "guildrank (\\d){1,3} (\\w)+"));
    theCommandStructure.put("guildaddrank", () -> new RankChangeCommand(
            "guildaddrank (\\d){1,3} (\\w)+"));
    theCommandStructure.put("guildassignrank", () -> new RankAssignCommand(
            "guildassignrank (\\w)+ (\\w)+"));
    theCommandStructure.put("guilddelrank", () -> new RankDeleteCommand(
            "guilddelrank (\\d){1,3}"));
    theCommandStructure.put("createguild", () -> new CreateGuildCommand(
            "createguild (\\w)+ .+"));
    theCommandStructure.put("deleteguild", () -> new DeleteGuildCommand(
            "deleteguild"));
    theCommandStructure.put("guildremove", () -> new RemoveCommand(
            "guildremove (\\w)+"));
    theCommandStructure.put("guildmasterchange", () -> new ChangeMasterCommand(
            "guildmasterchange (\\w)+"));
    theCommandStructure.put("guild", () -> new MessageCommand("guild .+"));

    for (String entry : Emotions.getEmotions())
    {
      theCommandStructure.put(entry, () -> new EmotionCommand(".+"));
    }
    for (String entry : Emotions.getTargetEmotions())
    {
      theCommandStructure.put(entry, () -> new EmotionToCommand(".+"));
    }
  }

  public static NormalCommand getBogusCommand()
  {
    return BOGUS.createCommand();
  }

  public static NormalCommand getAdminCommand() {
    return theCommandStructure.get("admin").createCommand();
  }

  /**
   * Returns the commands to be used, based on the first word in the command
   * entered by the user.
   *
   * @param aCommand String containing the command entered by the user.
   * @return List containing the commands that fit the description. The
   * commands that are contained are in the following order:
   * <ol>
   * <li>special commands retrieved from the database
   * <li>normal commands
   * <li>bogus command (the ultimate failover, "I don't understand that.".)
   * </ol>
   * It also means that this collection will always carry at least one
   * command, the bogus command.
   * <P>
   * All commands are newly created.
   */
  public static List<NormalCommand> getCommand(String aCommand)
  {
    List<NormalCommand> result = new ArrayList<>(5);
    String[] strings = aCommand.split(" ");
    NormalCommand myCommand = null;
    if (strings.length >= 2)
    {
      final CommandCreator commandCreator = theCommandStructure.get(strings[0] + " " + strings[1]);
      myCommand = commandCreator == null ? null : commandCreator.createCommand();
    }
    if (myCommand == null && strings.length >= 1)
    {
      final CommandCreator commandCreator = theCommandStructure.get(strings[0]);
      myCommand = commandCreator == null ? null : commandCreator.createCommand();
    }
    if (myCommand == null)
    {
      myCommand = getBogusCommand();
    }
    result.add(myCommand);
    return result;
  }

  /**
   * <p>
   * Returns all matching user commands.</p>
   * <p>
   * There are a few requirements<ul><li>commands must match</li>
   * <li>either the user defined command is valid for all rooms OR</li>
   * <li>room defined in the user defined commands euqals the room the user is
   * in</li></ul></p>
   *
   * @param user the user, used to get the room.
   * @param aCommand the string containing the command issued by the user
   * @return a list of possibly valid user defined commands.
   */
  public static List<UserCommandInfo> getUserCommands(User user, String aCommand, BiConsumer<String, Throwable> errorConsumer)
  {
    LOGGER.entering("CommandFactory", "getUserCommands " + user + " " + aCommand);
    List<UserCommandInfo> result = new ArrayList<>();
    for (UserCommandInfo myCom : theUserCommandStructure)
    {
      try
      {
        if (aCommand.matches(myCom.getCommand()))
        {
          if (myCom.getRoom() == null || myCom.getRoom().equals(user.getRoom().getId()))
          {
            result.add(myCom);
          }
        }
      } catch (PatternSyntaxException e) {
        String msg = "PatternSyntaxException executing user command " + aCommand + " for user " + user.getName();
        LOGGER.severe(msg);
        LOGGER.throwing("CommandFactory", "getUserCommands", e);
        errorConsumer.accept(msg, e);
        theUserCommandStructure.remove(myCom);
      }
    }
    return Collections.unmodifiableList(result);

  }

  /**
   * The definition of a user command. Bear in mind that commands that are
   * executed often do not make very good global user commands, as the load on
   * the database might be a little too much.
   */
  public static class UserCommandInfo
  {

    private final int theCommandId;

    private final String theCommand;

    private final String theMethodName;

    private final Long theRoom;

    /**
     * constructor for creating a user command for a specific room.
     *
     * @param aCommand the regular expression for determining the command is
     * equal to the command entered.
     * @param aMethodName the name of the method to call.
     * @param aRoom the room in which this command is active. Can be a null
     * pointer if the command is active in all rooms.
     * @param aCommandId the identification number for the command
     */
    public UserCommandInfo(int aCommandId, String aCommand,
            String aMethodName, Long aRoom)
    {
      theCommandId = aCommandId;
      theCommand = aCommand;
      theMethodName = aMethodName;
      theRoom = aRoom;
    }

    /**
     * constructor for creating a user command for all rooms.
     *
     * @param aCommand the regular expression for determining the command is
     * equal to the command entered.
     * @param aMethodName the name of the method to call.
     * @param aCommandId the identification number for the command
     */
    public UserCommandInfo(int aCommandId, String aCommand,
            String aMethodName)
    {
      theCommandId = aCommandId;
      theCommand = aCommand;
      theMethodName = aMethodName;
      theRoom = null;
    }

    public String getCommand()
    {
      return theCommand;
    }

    public int getCommandId()
    {
      return theCommandId;
    }

    public String getMethodName()
    {
      return theMethodName;
    }

    public Long getRoom()
    {
      return theRoom;
    }

    @Override
    public boolean equals(Object o)
    {
      if (!(o instanceof UserCommandInfo myUC))
      {
        return false;
      }
      return myUC.getCommand().equals(getCommand());
    }

    public int getTheCommandId()
    {
      return theCommandId;
    }

    @Override
    public String toString()
    {
      return theCommandId + ":" + getCommand() + ":" + getMethodName() + ":" + getRoom();
    }

    /**
     * Use this method when the command that had to be run, throws an
     * exception.
     */
    public void deactivateCommand()
            throws MudException
    {
      theUserCommandStructure.remove(this);
    }

  }

  /**
   * Returns if the map of user commands is empty.
   *
   * @return true if usercommands is empty.
   */
  public static boolean noUserCommands()
  {
    return theUserCommandStructure.isEmpty();
  }

  /**
   * Clears the map containing all user-defined commands. It will be reloaded
   * automatically from the database, upon executing the first command.
   */
  public static void clearUserCommandStructure()
  {
    CommandFactory.theUserCommandStructure.clear();
  }

  /**
   * Add a user command to the structure.
   *
   * @param aCommandId the id of the command
   * @param aCommand the command
   * @param aMethodName the method name
   * @param aRoom the room, optional, may be null in which case the command is
   * valid for all rooms.
   */
  public static void addUserCommand(int aCommandId, String aCommand,
          String aMethodName, Long aRoom)
  {
    UserCommandInfo info = new UserCommandInfo(aCommandId, aCommand, aMethodName, aRoom);
    CommandFactory.theUserCommandStructure.add(info);
  }

}
