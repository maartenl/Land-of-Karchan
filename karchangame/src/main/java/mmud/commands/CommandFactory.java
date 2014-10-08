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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import mmud.Utils;
import mmud.commands.communication.AskCommand;
import mmud.commands.communication.CryCommand;
import mmud.commands.communication.SayCommand;
import mmud.commands.communication.ScreamCommand;
import mmud.commands.communication.ShoutCommand;
import mmud.commands.communication.SingCommand;
import mmud.commands.communication.TellCommand;
import mmud.commands.communication.WhisperCommand;
import mmud.commands.guild.AcceptCommand;
import mmud.commands.guild.ApplyCommand;
import mmud.commands.guild.ChangeMasterCommand;
import mmud.commands.guild.DetailsCommand;
import mmud.commands.guild.LeaveCommand;
import mmud.commands.guild.MessageCommand;
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

    private static final Logger itsLog = Logger.getLogger(CommandFactory.class.getName());
    private static final CommandCreator BOGUS = new CommandCreator()
    {
        @Override
        public NormalCommand createCommand()
        {
            return new BogusCommand(".+");
        }
    };
    static final CommandCreator AWAKEN = new CommandCreator()
    {

        @Override
        public NormalCommand createCommand()
        {
            return new AwakenCommand("awaken");
        }
    };
    static final CommandCreator ASLEEP = new CommandCreator()
    {

        @Override
        public NormalCommand createCommand()
        {
            return new AlreadyAsleepCommand(".+");
        }
    };
    /**
     * Contains mappings from what command people have entered, to what command
     * should be executed. Supports two and one string, for example
     * "look at" or "look", in order to distinguish different commands with the same
     * verb.
     */
    private static final TreeMap<String, CommandCreator> theCommandStructure = new TreeMap<>();
    private static List<UserCommandInfo> theUserCommandStructure = new CopyOnWriteArrayList<>();

    public static interface CommandCreator
    {

        public NormalCommand createCommand();
    }

    static
    {
        theCommandStructure.put("bow", new CommandCreator()
        {

            @Override
            public NormalCommand createCommand()
            {
                return new BowCommand("bow( to (\\w)+)?( (\\w)+)?");
            }
        });
        theCommandStructure.put("me", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new MeCommand("me .+");
            }
        });
        // quit command has been replaced with a specific rest service.
        // theCommandStructure.put("quit", new QuitCommand("quit"));
        theCommandStructure.put("sleep", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new SleepCommand("sleep");
            }
        });
        theCommandStructure.put("condition", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new ConditionCommand("condition( .+)?");
            }
        });
        theCommandStructure.put("awaken", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new AwakenCommand("awaken");
            }
        });
        theCommandStructure.put("ask", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new AskCommand("ask (to (\\w)+ )?.+");
            }
        });
        theCommandStructure.put("tell", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new TellCommand("tell to (\\w)+ .+");
            }
        });
        theCommandStructure.put("say", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new SayCommand("say (to (\\w)+ )?.+");
            }
        });
        theCommandStructure.put("macro", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new MacroCommand("macro( .+)?");
            }
        });
        theCommandStructure.put("sing", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new SingCommand("sing (to (\\w)+ )?.+");
            }
        });
        theCommandStructure.put("cry", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new CryCommand("cry (to (\\w)+ )?.+");
            }
        });
        theCommandStructure.put("shout", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new ShoutCommand(
                        "shout (to (\\w )+)?.+");
            }
        });
        theCommandStructure.put("scream", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new ScreamCommand(
                        "scream (to (\\w )+)?.+");
            }
        });
        theCommandStructure.put("whisper", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new WhisperCommand(
                        "whisper (to (\\w)+ )?.+");
            }
        });
        // clear command has been replaced with a log-deleteing rest service
        // theCommandStructure.put("clear", new ClearCommand("clear");            }        });
        theCommandStructure.put("time", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new TimeCommand("time");
            }
        });
        theCommandStructure.put("date", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new DateCommand("date");
            }
        });
        theCommandStructure.put("south", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new SouthCommand("south");
            }
        });
        theCommandStructure.put("north", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new NorthCommand("north");
            }
        });
        theCommandStructure.put("east", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new EastCommand("east");
            }
        });
        theCommandStructure.put("west", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new WestCommand("west");
            }
        });
        theCommandStructure.put("s", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new SouthCommand("s");
            }
        });
        theCommandStructure.put("n", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new NorthCommand("n");
            }
        });
        theCommandStructure.put("e", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new EastCommand("e");
            }
        });
        theCommandStructure.put("w", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new WestCommand("w");
            }
        });
        theCommandStructure.put("up", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new UpCommand("up");
            }
        });
        theCommandStructure.put("down", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new DownCommand("down");
            }
        });
        theCommandStructure.put("go", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new GoCommand(
                        "go (up|down|north|south|east|west)?");
            }
        });
        theCommandStructure.put("help", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new HelpCommand("help( (\\w)+)?");
            }
        });
        theCommandStructure.put("show ignoring", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new IgnoringCommand(
                        "show ignoring");
            }
        });
        theCommandStructure.put("fully", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new IgnoreCommand(
                        "fully ignore (\\w)+");
            }
        });
        theCommandStructure.put("acknowledge", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new AcknowledgeCommand(
                        "acknowledge (\\w)+");
            }
        });
        theCommandStructure.put("curtsey", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new CurtseyCommand(
                        "curtsey( to (\\w)+)?");
            }
        });
        theCommandStructure.put("eyebrow", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new EyebrowCommand("eyebrow");
            }
        });
        theCommandStructure.put("wimpy",
                new CommandCreator()
                {
                    @Override
                    public NormalCommand createCommand()
                    {
                        return new WimpyCommand("wimpy( .+|help)?");
                    }
                }
        );
        theCommandStructure.put("who", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new WhoCommand("who");
            }
        });
//        theCommandStructure.put("pkill", new PkillCommand("pkill( (\\w)+)?"));
//        theCommandStructure.put("fight", new FightCommand("fight (\\w)+"));
//        theCommandStructure.put("stop", new FightCommand("stop fighting"));
        theCommandStructure.put("stats", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new StatsCommand("stats");
            }
        });
        theCommandStructure.put("inventory", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new InventoryCommand("inventory");
            }
        });
        theCommandStructure.put("i", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new InventoryCommand("i");
            }
        });
        theCommandStructure.put("drink", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new DrinkCommand(
                        "drink( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("eat", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new EatCommand("eat( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("destroy", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new DestroyCommand("destroy( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("wear", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new WearCommand(
                        "wear( (\\w|-)+){1,4} on (\\w)+");
            }
        });
        theCommandStructure.put("remove", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new UnwearCommand(
                        "remove from (\\w)+");
            }
        });
        theCommandStructure.put("undress", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new UndressCommand(
                        "undress");
            }
        });
        theCommandStructure.put("disarm", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new DisarmCommand(
                        "disarm");
            }
        });
        theCommandStructure.put("wield", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new WieldCommand(
                        "wield( (\\w|-)+){1,4} with (\\w)+");
            }
        });
        theCommandStructure.put("unwield", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new UnwieldCommand(
                        "unwield from (\\w)+");
            }
        });
        theCommandStructure.put("drop", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new DropCommand("drop( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("get", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new GetCommand("get( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("put", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new PutCommand(
                        "put( (\\w|-)+){1,4} in( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("retrieve", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new RetrieveCommand(
                        "retrieve( (\\w|-)+){1,4} from( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("lock", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new LockCommand(
                        "lock( (\\w|-)+){1,4} with( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("unlock", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new UnlockCommand(
                        "unlock( (\\w|-)+){1,4} with( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("give", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new GiveCommand(
                        "give( (\\w|-)+){1,4} to (\\w)+");
            }
        });
        theCommandStructure.put("open", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new OpenCommand("open( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("close", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new CloseCommand(
                        "close( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("read", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new ReadCommand("read( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("readboard", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new ReadBoardCommand(
                        "readboard (\\w)+");
            }
        });
        theCommandStructure.put("post", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new PostBoardCommand("post (\\w)+ .+");
            }
        });
        theCommandStructure.put("l", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new LookCommand("l");
            }
        });
        theCommandStructure.put("look", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new LookCommand(
                        "look");
            }
        });
        theCommandStructure.put("look at", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new LookAtCommand(
                        "look at( (\\w|-)+){1,4}");
            }
        });
        theCommandStructure.put("look in", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new LookInCommand(
                        "look in( (\\w|-)+){1,4}");
            }
        });
//        theCommandStructure.put("search", new SearchCommand(
//                "search( (\\w|-)+){1,4}"));
        theCommandStructure.put("buy", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new BuyCommand(
                        "buy( (\\w|-)+){1,4} from (\\w)+");
            }
        });
        theCommandStructure.put("sell", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new SellCommand(
                        "sell( (\\w|-)+){1,4} to (\\w)+");
            }
        });
//        theCommandStructure.put("show", new ShowCommand(
//                "show( (\\w|-)+){1,4} to (\\w)+"));
//        theCommandStructure.put("title", new TitleCommand("title .+"));
        theCommandStructure.put("admin", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new AdminCommand("admin .+");
            }
        });
        // guild commands
        theCommandStructure.put("guildapply", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new ApplyCommand(
                        "guildapply( (\\w)+)?");
            }
        });
        theCommandStructure.put("guildleave", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new LeaveCommand("guildleave");
            }
        });
        theCommandStructure.put("guilddetails", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new DetailsCommand(
                        "guilddetails");
            }
        });
        theCommandStructure.put("guildaccept", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new AcceptCommand(
                        "guildaccept (\\w)+");
            }
        });
        theCommandStructure.put("guildreject", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new RejectCommand(
                        "guildreject (\\w)+");
            }
        });
        theCommandStructure.put("guildremove", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new RemoveCommand(
                        "guildremove (\\w)+");
            }
        });
        theCommandStructure.put("guildmasterchange", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new ChangeMasterCommand(
                        "guildmasterchange (\\w)+");
            }
        });
        theCommandStructure.put("guild", new CommandCreator()
        {
            @Override
            public NormalCommand createCommand()
            {
                return new MessageCommand("guild .+");
            }
        });

        for (Map.Entry<String, String> entry : Utils.getEmotions().entrySet())
        {
            theCommandStructure.put(entry.getKey(), new CommandCreator()
            {
                @Override
                public NormalCommand createCommand()
                {
                    return new EmotionCommand(".+");
                }
            });
        }
        for (Map.Entry<String, String> entry : Utils.getTargetEmotions().entrySet())
        {
            theCommandStructure.put(entry.getKey(), new CommandCreator()
            {
                @Override
                public NormalCommand createCommand()
                {
                    return new EmotionToCommand(".+");
                }
            });
        }
    }

    public static NormalCommand getBogusCommand()
    {
        return BOGUS.createCommand();
    }

    /**
     * Returns the commands to be used, based on the first word in the command
     * entered by the user.
     *
     * @param aCommand
     * String containing the command entered by the user.
     * @return List containing the commands that fit the
     * description. The commands that are contained are in the following
     * order:
     * <ol>
     * <li>special commands retrieved from the database
     * <li>normal commands
     * <li>bogus command (the ultimate failover, "I don't understand
     * that.".)
     * </ol>
     * It also means that this collection will always carry at least one
     * command, the bogus command.
     * <P>
     * All commands are newly created.
     */
    static List<NormalCommand> getCommand(String aCommand)
    {
        List<NormalCommand> result = new ArrayList<>(5);
        String[] strings = aCommand.split(" ");
        NormalCommand myCommand = null;
        if (myCommand == null && strings.length >= 2)
        {
            myCommand = theCommandStructure.get(strings[0] + " " + strings[1]).createCommand();
        }
        if (myCommand == null && strings.length >= 1)
        {
            myCommand = theCommandStructure.get(strings[0]).createCommand();
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
     * <li>room defined in the user defined commands euqals the room the
     * user is in</li></ul></p>
     *
     * @param user the user, used to get the room.
     * @param aCommand the string containing the command issued by the user
     * @return a list of possibly valid user defined commands.
     */
    public static List<UserCommandInfo> getUserCommands(User user, String aCommand)
    {
        itsLog.entering("CommandFactory", "getUserCommands " + user + " " + aCommand);
        List<UserCommandInfo> result = new ArrayList<>();
        for (UserCommandInfo myCom : theUserCommandStructure)
        {
            if (aCommand.matches(myCom.getCommand()))
            {
                if (myCom.getRoom() == null || myCom.getRoom().equals(user.getRoom().getId()))
                {
                    result.add(myCom);
                }
            }
        }
        return Collections.unmodifiableList(result);

    }

    /**
     * The definition of a user command. Bear in mind that commands that
     * are executed often do not make very good global user commands,
     * as the load on the database might be a little too much.
     */
    public static class UserCommandInfo
    {

        private final int theCommandId;

        private final String theCommand;

        private final String theMethodName;

        private final Integer theRoom;

        /**
         * constructor for creating a user command for a specific room.
         *
         * @param aCommand the regular expression for determining
         * the command is equal to the command entered.
         * @param aMethodName the name of the method to call.
         * @param aRoom the room in which this command is active. Can be
         * a null pointer if the command is active in all rooms.
         * @param aCommandId the identification number for the command
         */
        public UserCommandInfo(int aCommandId, String aCommand,
                String aMethodName, Integer aRoom)
        {
            theCommandId = aCommandId;
            theCommand = aCommand;
            theMethodName = aMethodName;
            theRoom = aRoom;
        }

        /**
         * constructor for creating a user command for all rooms.
         *
         * @param aCommand the regular expression for determining
         * the command is equal to the command entered.
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

        public Integer getRoom()
        {
            return theRoom;
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof UserCommandInfo))
            {
                return false;
            }
            UserCommandInfo myUC = (UserCommandInfo) o;
            boolean p = myUC.getCommand().equals(getCommand());
            return p;
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
         * Use this method when the command that had to be run, throws
         * an exception.
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
     * @return
     */
    public static boolean noUserCommands()
    {
        return theUserCommandStructure.isEmpty();
    }

    /**
     * Clears the map containing all user-defined commands.
     * It will be reloaded automatically from the database, upon executing the
     * first command.
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
     * @param aRoom the room, optional, may be null in which case the command is valid for all
     * rooms.
     */
    public static void addUserCommand(int aCommandId, String aCommand,
            String aMethodName, Integer aRoom)
    {
        UserCommandInfo info = new UserCommandInfo(aCommandId, aCommand, aMethodName, aRoom);
        CommandFactory.theUserCommandStructure.add(info);
    }

}
