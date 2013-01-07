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
 *
 * @author maartenl
 */
public class RunScript
{

    private Lookup lookup;

    public RunScript(Lookup lookup)
    {
        this.lookup = lookup;
    }

    public boolean run(mmud.database.entities.characters.Person person, String sourceCode) throws ScriptException, NoSuchMethodException
    {

        Invocable inv = initialiseScriptEngine(sourceCode);

        // invoke the global function named "hello"
        Boolean result = (Boolean) inv.invokeFunction("event", new mmud.scripting.entities.Person(person));
        return result == null ? false : result;
    }

    public boolean run(mmud.database.entities.game.Room room, String sourceCode) throws ScriptException, NoSuchMethodException
    {

        Invocable inv = initialiseScriptEngine(sourceCode);

        // invoke the global function named "hello"
        Boolean result = (Boolean) inv.invokeFunction("event", new Room(room));
        return result == null ? false : result;
    }

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
        // expose File object as variable to script
        engine.put("lookup", lookup);
        // evaluate JavaScript code from String
        //        engine.eval("print('Hello, World')");
        engine.eval(sourceCode);
        Invocable inv = (Invocable) engine;
        return inv;
    }
}
