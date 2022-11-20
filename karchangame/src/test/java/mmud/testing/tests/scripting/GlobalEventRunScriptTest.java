/*
 *  Copyright (C) 2013 maartenl
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
package mmud.testing.tests.scripting;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;

import mmud.scripting.RunScript;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 *
 * @author maartenl
 */
public class GlobalEventRunScriptTest extends RunScriptTest
{

    public GlobalEventRunScriptTest()
    {
    }

    @Test
    public void runGlobalEventEmptySource() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        RunScript runScript = new RunScript(persons, rooms, items, world, null);
      StringBuilder sourceCode = new StringBuilder();
        try
        {
            Object result = runScript.run(sourceCode.toString());
            fail("No event defined, so why no exception then?");
        } catch (ScriptException ex)
        {
            Logger.getLogger(GlobalEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected.");
        } catch (NoSuchMethodException ex)
        {
          assertEquals(ex.getMessage(), "event");
        }
    }

    @Test
    public void runGlobalEvent() throws IllegalAccessException, InstantiationException, InstantiationException, InvocationTargetException
    {
      RunScript runScript = new RunScript(persons, rooms, items, world, null);
      StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event() {");
        sourceCode.append("return false;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(sourceCode.toString());
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(GlobalEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected.");
        }
    }

    @Test
    public void runPersonEvent() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
      RunScript runScript = new RunScript(persons, rooms, items, world, null);
      StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event() {\n");
        sourceCode.append("var person = persons.find('hotblack');\n");
        sourceCode.append("return false;\n");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(sourceCode.toString());
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(PersonEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected. But we got " + ex.getMessage());
        }
    }

    @Test
    public void runRoomEvent() throws IllegalAccessException, InstantiationException, InstantiationException, InvocationTargetException
    {
      RunScript runScript = new RunScript(persons, rooms, items, world, null);
      StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event() {");
        sourceCode.append("var room = rooms.find(1);");
        sourceCode.append("return false;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(room, sourceCode.toString());
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(RoomEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected. But we got " + ex.getMessage());
        }
    }
}
