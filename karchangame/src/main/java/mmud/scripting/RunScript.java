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
package mmud.scripting;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.scripting.entities.Room;

/**
 * Can run javascript source code. The methods call specific javascript
 * functions in the source code.
 *
 * @author maartenl
 */
public class RunScript
{

    private static final Logger LOGGER = Logger.getLogger(RunScript.class.getName());

    private Persons persons;

    private Rooms rooms;

    private Items items;

    private World world;

    public RunScript(Persons persons, Rooms rooms, Items items, World world)
    {
        if (persons == null)
        {
            throw new NullPointerException("persons was null.");
        }
        if (rooms == null)
        {
            throw new NullPointerException("rooms was null.");
        }
        if (world == null)
        {
            throw new NullPointerException("world was null.");
        }
        this.persons = persons;
        this.rooms = rooms;
        this.world = world;
        this.items = items;
    }

    /**
     * Runs a specific function called "function command(person, command)".
     *
     * Basically it calls a specific deputy defined "command".
     *
     * @param person the person issuing forth the command.
     * @param command the command, a string.
     * @param sourceCode the source code, javascript, a string.
     * @return false if failed, true if successful
     * @throws ScriptException if an error occurred in the javascript
     * @throws NoSuchMethodException if the function cannot be found,
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InstantiationException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public DisplayInterface run(mmud.database.entities.characters.Person person, String command, String sourceCode) throws ScriptException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException
    {
        LOGGER.entering(this.getClass().getName(), "run(" + person.getName() + ", " + command + ")");

        final Invocable inv = initialiseScriptEngine(sourceCode);

        final Object result = inv.invokeFunction("command", new mmud.scripting.entities.Person(person), command);
        if (result != null)
        {
            return new DisplayInterface()
            {
                @Override
                public String getMainTitle() throws MudException
                {
                    try
                    {
                        return (String) inv.invokeMethod(result, "getTitle");
                    } catch (ScriptException | NoSuchMethodException ex)
                    {
                        throw new MudException(ex);
                    }
                }

                @Override
                public String getImage() throws MudException
                {
                    try
                    {
                        return (String) inv.invokeMethod(result, "getImage");
                    } catch (ScriptException | NoSuchMethodException ex)
                    {
                        throw new MudException(ex);
                    }
                }

                @Override
                public String getBody() throws MudException
                {
                    try
                    {
                        return (String) inv.invokeMethod(result, "getBody");
                    } catch (ScriptException | NoSuchMethodException ex)
                    {
                        throw new MudException(ex);
                    }
                }
            };
        }
        return null;
    }

    /**
     * Runs a specific function called "function event(person)".
     *
     * Basically it calls a specific deputy defined "event".
     *
     * @param person the event needs to be executed with this person as the focus.
     * @param sourceCode the source code, javascript, a string.
     * @return false if failed, true if successful
     * @throws ScriptException if an error occurred in the javascript
     * @throws NoSuchMethodException if the function cannot be found,
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InstantiationException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public boolean run(mmud.database.entities.characters.Person person, String sourceCode) throws ScriptException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException
    {
        LOGGER.entering(this.getClass().getName(), "run(" + person.getName() + ")");

        Invocable inv = initialiseScriptEngine(sourceCode);

        // invoke the global function named "hello"
        Boolean result = (Boolean) inv.invokeFunction("event", new mmud.scripting.entities.Person(person));
        return result == null ? false : result;
    }

    /**
     * Runs a specific function called "function event(room)".
     *
     * Basically it calls a specific deputy defined "event".
     *
     * @param room the event needs to be executed with this room as the focus.
     * @param sourceCode the source code, javascript, a string.
     * @return false if failed, true if successful
     * @throws ScriptException if an error occurred in the javascript
     * @throws NoSuchMethodException if the function cannot be found,
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InstantiationException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public boolean run(mmud.database.entities.game.Room room, String sourceCode) throws ScriptException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException
    {
        LOGGER.entering(this.getClass().getName(), "run(" + room.getId() + ")");

        Invocable inv = initialiseScriptEngine(sourceCode);

        // invoke the global function named "hello"
        Boolean result = (Boolean) inv.invokeFunction("event", new Room(room));
        return result == null ? false : result;
    }

    /**
     * Runs a specific function called "function event()". This is a generic
     * event without a focus.
     *
     * @param sourceCode the source code, javascript, a string.
     * @return false if failed, true if successful
     * @throws ScriptException if an error occurred in the javascript
     * @throws NoSuchMethodException if the function cannot be found,
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InstantiationException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public boolean run(String sourceCode) throws ScriptException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException
    {
        LOGGER.entering(this.getClass().getName(), "run()");

        Invocable inv = initialiseScriptEngine(sourceCode);

        // invoke the global function named "hello"
        Boolean result = (Boolean) inv.invokeFunction("event");
        return result == null ? false : result;
    }

    private Invocable initialiseScriptEngine(String sourceCode) throws ScriptException, IllegalAccessException, InstantiationException, InvocationTargetException
    {
        LOGGER.entering(this.getClass().getName(), "initialiseScriptEngine");
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByExtension("js");
        if (engine == null)
        {
            LOGGER.warning("Javascript engine missing!");
            List<ScriptEngineFactory> engineFactories = factory.getEngineFactories();
            LOGGER.warning("Available scripting engines:");
            for (ScriptEngineFactory engines : engineFactories)
            {
                LOGGER.warning(engines.getEngineName());
            }
            throw new MudException("Javascript engine missing!");
        }
        // expose persons and rooms object as variable to script
        engine.put("persons", persons);
        engine.put("rooms", rooms);
        engine.put("items", items);
        engine.put("world", world);

        //        engine.eval("print('Hello, World')");
        engine.eval(sourceCode);
        Invocable inv = (Invocable) engine;
        return inv;
    }
}
