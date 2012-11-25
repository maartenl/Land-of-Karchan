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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import mmud.Utils;
import mmud.commands.communication.AskCommand;
import mmud.commands.communication.CryCommand;
import mmud.commands.communication.SayCommand;
import mmud.commands.communication.ScreamCommand;
import mmud.commands.communication.ShoutCommand;
import mmud.commands.communication.SingCommand;
import mmud.commands.communication.WhisperCommand;
import mmud.commands.guild.AcceptCommand;
import mmud.commands.guild.ApplyCommand;
import mmud.commands.guild.ChangeMasterCommand;
import mmud.commands.guild.DetailsCommand;
import mmud.commands.guild.LeaveCommand;
import mmud.commands.guild.MessageCommand;
import mmud.commands.guild.RejectCommand;
import mmud.commands.guild.RemoveCommand;
import mmud.commands.items.InventoryCommand;
import mmud.commands.items.LookAtCommand;
import mmud.commands.items.LookInCommand;
import mmud.commands.movement.DownCommand;
import mmud.commands.movement.EastCommand;
import mmud.commands.movement.GoCommand;
import mmud.commands.movement.NorthCommand;
import mmud.commands.movement.SouthCommand;
import mmud.commands.movement.UpCommand;
import mmud.commands.movement.WestCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author maartenl
 */
public class CommandFactory
{

    private static final Logger itsLog = LoggerFactory.getLogger(CommandFactory.class);
    private static final BogusCommand BOGUS = new BogusCommand(".+");
    private static final AwakenCommand AWAKEN = new AwakenCommand("awaken");
    private static final AlreadyAsleepCommand ASLEEP = new AlreadyAsleepCommand(".+");


    /**
     * Contains mappings from what command people have entered, to what command
     * should be executed. Supports two and one string, for example
     * "look at" or "look", in order to distinguish different commands with the same
     * verb.
     */
    private static final TreeMap<String, NormalCommand> theCommandStructure = new TreeMap<>();
    //private static List<UserCommandInfo> theUserCommandStructure = new ArrayList<UserCommandInfo>();

    static
    {
        theCommandStructure.put("bow", new BowCommand(
                "bow( to (\\w)+)?( (\\w)+)?"));
        theCommandStructure.put("me", new MeCommand("me .+"));
//        theCommandStructure.put("quit", new QuitCommand("quit"));
        theCommandStructure.put("sleep", new SleepCommand("sleep"));
        theCommandStructure.put("condition", new ConditionCommand("condition( .+)?"));
        theCommandStructure.put("awaken", new AwakenCommand("awaken"));
        theCommandStructure.put("ask", new AskCommand("ask (to (\\w)+ )?.+"));
//        theCommandStructure.put("tell", new TellCommand("tell to (\\w)+ .+"));
        theCommandStructure.put("say", new SayCommand("say (to (\\w)+ )?.+"));
//        theCommandStructure.put("macro", new MacroCommand("macro( .+)?"));
        theCommandStructure.put("sing", new SingCommand("sing (to (\\w)+ )?.+"));
        theCommandStructure.put("cry", new CryCommand("cry (to (\\w)+ )?.+"));
        theCommandStructure.put("shout", new ShoutCommand(
                "shout (to (\\w )+)?.+"));
        theCommandStructure.put("scream", new ScreamCommand(
                "scream (to (\\w )+)?.+"));
        theCommandStructure.put("whisper", new WhisperCommand(
                "whisper (to (\\w)+ )?.+"));
//        theCommandStructure.put("clear", new ClearCommand("clear"));
        theCommandStructure.put("time", new TimeCommand("time"));
        theCommandStructure.put("date", new DateCommand("date"));
        theCommandStructure.put("south", new SouthCommand("south"));
        theCommandStructure.put("north", new NorthCommand("north"));
        theCommandStructure.put("east", new EastCommand("east"));
        theCommandStructure.put("west", new WestCommand("west"));
        theCommandStructure.put("s", new SouthCommand("s"));
        theCommandStructure.put("n", new NorthCommand("n"));
        theCommandStructure.put("e", new EastCommand("e"));
        theCommandStructure.put("w", new WestCommand("w"));
        theCommandStructure.put("up", new UpCommand("up"));
        theCommandStructure.put("down", new DownCommand("down"));
        theCommandStructure.put("go", new GoCommand(
                "go (up|down|north|south|east|west)?"));
        theCommandStructure.put("help", new HelpCommand("help( (\\w)+)?"));
//        theCommandStructure.put("fully", new IgnoreCommand(
//                "fully ignore (\\w)+"));
//        theCommandStructure.put("acknowledge", new AcknowledgeCommand(
//                "acknowledge (\\w)+"));
        theCommandStructure.put("curtsey", new CurtseyCommand(
                "curtsey( to (\\w)+)?"));
        theCommandStructure.put("eyebrow", new EyebrowCommand("eyebrow"));
        theCommandStructure.put("wimpy",
                new WimpyCommand("wimpy( .+|help)?"));
//        theCommandStructure.put("who", new WhoCommand("who"));
//        theCommandStructure.put("pkill", new PkillCommand("pkill( (\\w)+)?"));
//        theCommandStructure.put("fight", new FightCommand("fight (\\w)+"));
//        theCommandStructure.put("stop", new FightCommand("stop fighting"));
        theCommandStructure.put("stats", new StatsCommand("stats"));
        theCommandStructure.put("inventory", new InventoryCommand("inventory"));
        theCommandStructure.put("i", new InventoryCommand("i"));
//        theCommandStructure.put("drink", new DrinkCommand(
//                "drink( (\\w|-)+){1,4}"));
//        theCommandStructure.put("eat", new EatCommand("eat( (\\w|-)+){1,4}"));
//        theCommandStructure.put("destroy", new DestroyCommand("destroy( (\\w|-)+){1,4}"));
//        theCommandStructure.put("wear", new WearCommand(
//                "wear( (\\w|-)+){1,4} on (\\w)+"));
//        theCommandStructure.put("remove", new UnwearCommand(
//                "remove( (\\w|-)+){1,4} from (\\w)+"));
//        theCommandStructure.put("undress", new UndressCommand(
//                "undress"));
//        theCommandStructure.put("disarm", new DisarmCommand(
//                "disarm"));
//        theCommandStructure.put("wield", new WieldCommand(
//                "wield( (\\w|-)+){1,4} with (\\w)+"));
//        theCommandStructure.put("unwield", new UnwieldCommand(
//                "unwield( (\\w|-)+){1,4} from (\\w)+"));
//        theCommandStructure.put("drop", new DropCommand("drop( (\\w|-)+){1,4}"));
//        theCommandStructure.put("get", new GetCommand("get( (\\w|-)+){1,4}"));
//        theCommandStructure.put("search", new SearchCommand(
//                "search( (\\w|-)+){1,4}"));
//        theCommandStructure.put("put", new PutCommand(
//                "put( (\\w|-)+){1,4} in( (\\w|-)+){1,4}"));
//        theCommandStructure.put("retrieve", new RetrieveCommand(
//                "retrieve( (\\w|-)+){1,4} from( (\\w|-)+){1,4}"));
//        theCommandStructure.put("lock", new LockCommand(
//                "lock( (\\w|-)+){1,4} with( (\\w|-)+){1,4}"));
//        theCommandStructure.put("unlock", new UnlockCommand(
//                "unlock( (\\w|-)+){1,4} with( (\\w|-)+){1,4}"));
//        theCommandStructure.put("give", new GiveCommand(
//                "give( (\\w|-)+){1,4} to (\\w)+"));
//        theCommandStructure.put("open", new OpenCommand("open( (\\w|-)+){1,4}"));
//        theCommandStructure.put("close", new CloseCommand(
//                "close( (\\w|-)+){1,4}"));
//        theCommandStructure.put("readroleplay", new ReadRpgBoardCommand(
//                "readroleplay"));
//        theCommandStructure.put("read", new ReadCommand("read( (\\w|-)+){1,4}"));
//        theCommandStructure.put("readpublic", new ReadPublicCommand(
//                "readpublic"));
//        theCommandStructure.put("public", new PostPublicCommand("public .+"));
        theCommandStructure.put("l", new LookCommand("l"));
        theCommandStructure.put("look", new LookCommand(
                "look"));
        theCommandStructure.put("look at", new LookAtCommand(
                "look at ((\\w|-)+){1,4}"));
        theCommandStructure.put("look in", new LookInCommand(
                "look in ((\\w|-)+){1,4}"));
//        theCommandStructure.put("buy", new BuyCommand(
//                "buy( (\\w|-)+){1,4} from (\\w)+"));
//        theCommandStructure.put("sell", new SellCommand(
//                "sell( (\\w|-)+){1,4} to (\\w)+"));
//        theCommandStructure.put("show", new ShowCommand(
//                "show( (\\w|-)+){1,4} to (\\w)+"));
//        theCommandStructure.put("title", new TitleCommand("title .+"));
//        theCommandStructure.put("admin", new AdminCommand("admin .+"));
//        theCommandStructure.put("roleplay", new PostRpgBoardCommand(
//                "roleplay .+"));
//
        // guild commands
        theCommandStructure.put("guildapply", new ApplyCommand(
                "guildapply( (\\w)+)?"));
        theCommandStructure.put("guildleave", new LeaveCommand("guildleave"));
        theCommandStructure.put("guilddetails", new DetailsCommand(
                "guilddetails"));
        theCommandStructure.put("guildaccept", new AcceptCommand(
                "guildaccept (\\w)+"));
        theCommandStructure.put("guildreject", new RejectCommand(
                "guildreject (\\w)+"));
//        theCommandStructure.put("guildaddrank", new AddRankCommand(
//                "guildaddrank (\\d)+ .+"));
//        theCommandStructure.put("guildassign", new AssignRankCommand(
//                "guildassign (\\d)+ (\\w)+"));
//        theCommandStructure.put("guilddelrank", new DelRankCommand(
//                "guilddelrank (\\d)+"));
//        theCommandStructure.put("guilddescription", new SetDescriptionCommand(
//                "guilddescription .+"));
//        theCommandStructure.put("guildtitle", new SetTitleCommand(
//                "guildtitle .+"));
//        theCommandStructure.put("guildurl", new SetUrlCommand("guildurl .+"));
//        theCommandStructure.put("guildmessage", new SetLogonMessageCommand(
//                "guildmessage .+"));
        theCommandStructure.put("guildremove", new RemoveCommand(
                "guildremove (\\w)+"));
        theCommandStructure.put("guildmasterchange", new ChangeMasterCommand(
                "guildmasterchange (\\w)+"));
        theCommandStructure.put("guild", new MessageCommand("guild .+"));

        for (Map.Entry<String, String> entry : Utils.getEmotions().entrySet())
        {
            theCommandStructure.put(entry.getKey(), new EmotionCommand(".+"));
        }
        for (Map.Entry<String, String> entry : Utils.getTargetEmotions().entrySet())
        {
            theCommandStructure.put(entry.getKey(), new EmotionToCommand(".+"));
        }
    }

    public static NormalCommand getBogusCommand()
    {
        return BOGUS;
    }

    /**
     * Runs a specific command. If this person appears to be asleep, the only
     * possible commands are "quit" and "awaken". If the command found returns a
     * false, i.e. nothing is executed, the method will call the standard
     * BogusCommand.
     *
     * @param aUser the person executing the command
     * @param aCommand
     *            the command to be run
     * @return DisplayInterface object containing the result of the command executed.
     */
    public static DisplayInterface runCommand(User aUser, String aCommand) throws MudException
    {
        itsLog.debug(aUser.getName() + " : " + aCommand);
        if (aUser.getSleep())
        {
            NormalCommand command;

            if (aCommand.trim().equalsIgnoreCase("awaken"))
            {
                command = AWAKEN;
            } else
            {
                command = ASLEEP;
            }
            return command.start(aCommand, aUser);
        }

        List<NormalCommand> myCol = getCommand(aCommand);
        for (NormalCommand command : myCol)
        {
            DisplayInterface result = command.start(aCommand, aUser);
            if (result != null)
            {
                return result;
            }
        }
        return getBogusCommand().start(aCommand, aUser);
    }

    /**
     * Returns the commands to be used, based on the first word in the command
     * entered by the user.
     *
     * @param aCommand
     *            String containing the command entered by the user.
     * @return Collection (Vector) containing the commands that fit the
     *         description. The commands that are contained are in the following
     *         order:
     *         <ol>
     *         <li>special commands retrieved from the database
     *         <li>normal commands
     *         <li>bogus command (the ultimate failover, "I don't understand
     *         that.".)
     *         </ol>
     *         It also means that this collection will always carry at least one
     *         command, the bogus command.
     *         <P>
     *         All commands are newly created.
     */
    private static List<NormalCommand> getCommand(String aCommand)
    {
        List<NormalCommand> result = new ArrayList<>(5);
        // TODO: add user commands using Rhino and scripting.
//        Iterator myI = theUserCommandStructure.iterator();
//        while (myI.hasNext())
//        {
//            UserCommandInfo myCom = (UserCommandInfo) myI.next();
//            if (aCommand.matches(myCom.getCommand()))
//            {
//                ScriptCommand scriptCommand;
//                scriptCommand = new ScriptCommand(myCom);
//                scriptCommand.setCommand(aCommand);
//                result.add(scriptCommand);
//            }
//        }
        String[] strings = aCommand.split(" ");
        NormalCommand myCommand = null;
        if (strings.length >= 2)
        {
            myCommand = theCommandStructure.get(strings[0] + " " + strings[1]);
        }
        if (myCommand == null && strings.length >= 1)
        {
            myCommand = theCommandStructure.get(strings[0]);
        }
        if (myCommand == null)
        {
            myCommand = BOGUS;
        }
        result.add(myCommand);
        return result;
    }
}
