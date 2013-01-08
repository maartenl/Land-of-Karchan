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

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import mmud.scripting.entities.Room;

/**
 * Can run javascript source code. The methods call specific javascript
 * functions in the source code.
 * @author maartenl
 */
public class RunScript
{

    private Persons persons;
    private Rooms rooms;

    public RunScript(Persons persons, Rooms rooms)
    {
        if (persons == null)
        {
            throw new NullPointerException("persons was null.");
        }
        if (rooms == null)
        {
            throw new NullPointerException("rooms was null.");
        }
        this.persons = persons;
        this.rooms = rooms;
    }

    /**
     * Runs a specific function called "function command(person, command)".
     *
     * Basically it calls a specific deputy defined "command".
     * @param person the person issuing forth the command.
     * @param command the command, a string.
     * @param sourceCode the source code, javascript, a string.
     * @return false if failed, true if successful
     * @throws ScriptException if an error occurred in the javascript
     * @throws NoSuchMethodException  if the function cannot be found,
     */
    public boolean run(mmud.database.entities.characters.Person person, String command, String sourceCode) throws ScriptException, NoSuchMethodException
    {

        Invocable inv = initialiseScriptEngine(sourceCode);

        // invoke the global function named "hello"
        Boolean result = (Boolean) inv.invokeFunction("command", new mmud.scripting.entities.Person(person), command);
        return result == null ? false : result;
    }

    /**
     * Runs a specific function called "function event(person)".
     *
     * Basically it calls a specific deputy defined "event".
     * @param person the event needs to be executed with this person as the focus.
     * @param sourceCode the source code, javascript, a string.
     * @return false if failed, true if successful
     * @throws ScriptException if an error occurred in the javascript
     * @throws NoSuchMethodException  if the function cannot be found,
     */
    public boolean run(mmud.database.entities.characters.Person person, String sourceCode) throws ScriptException, NoSuchMethodException
    {

        Invocable inv = initialiseScriptEngine(sourceCode);

        // invoke the global function named "hello"
        Boolean result = (Boolean) inv.invokeFunction("event", new mmud.scripting.entities.Person(person));
        return result == null ? false : result;
    }

    /**
     * Runs a specific function called "function event(room)".
     *
     * Basically it calls a specific deputy defined "event".
     * @param room the event needs to be executed with this room as the focus.
     * @param sourceCode the source code, javascript, a string.
     * @return false if failed, true if successful
     * @throws ScriptException if an error occurred in the javascript
     * @throws NoSuchMethodException  if the function cannot be found,
     */
    public boolean run(mmud.database.entities.game.Room room, String sourceCode) throws ScriptException, NoSuchMethodException
    {

        Invocable inv = initialiseScriptEngine(sourceCode);

        // invoke the global function named "hello"
        Boolean result = (Boolean) inv.invokeFunction("event", new Room(room));
        return result == null ? false : result;
    }

    /**
     * Runs a specific function called "function event()". This is a generic
     * event without a focus.
     * @param sourceCode the source code, javascript, a string.
     * @return false if failed, true if successful
     * @throws ScriptException if an error occurred in the javascript
     * @throws NoSuchMethodException  if the function cannot be found,
     */
    public boolean run(String sourceCode) throws ScriptException, NoSuchMethodException
    {

        Invocable inv = initialiseScriptEngine(sourceCode);

        // invoke the global function named "hello"
        Boolean result = (Boolean) inv.invokeFunction("event");
        return result == null ? false : result;
    }

    private Invocable initialiseScriptEngine(String sourceCode) throws ScriptException
    {
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // expose persons and rooms object as variable to script
        engine.put("persons", persons);
        engine.put("rooms", rooms);
        // evaluate JavaScript code from String
        //        engine.eval("print('Hello, World')");
        engine.eval(sourceCode);
        Invocable inv = (Invocable) engine;
        return inv;
    }
}
