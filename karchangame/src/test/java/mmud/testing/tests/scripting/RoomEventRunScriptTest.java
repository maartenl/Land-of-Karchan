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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class RoomEventRunScriptTest extends RunScriptTest
{

    public RoomEventRunScriptTest()
    {
    }

    @Test
    public void runRoomEventEmptySource() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        RunScript runScript = new RunScript(persons, rooms, items, world);
        StringBuilder sourceCode = new StringBuilder();
        try
        {
            Object result = runScript.run(room, sourceCode.toString());
            fail("No event defined, so why no exception then?");
        } catch (ScriptException ex)
        {
            Logger.getLogger(RoomEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected.");
        } catch (NoSuchMethodException ex)
        {
            assertEquals(ex.getMessage(), "no such method: event");
        }
    }

    @Test
    public void runRoomEvent() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        RunScript runScript = new RunScript(persons, rooms, items, world);
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event(room) {");
        sourceCode.append("return false;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(room, sourceCode.toString());
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(RoomEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected.");
        }
    }

    @Test
    public void runRoomEventCommunication() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        RunScript runScript = new RunScript(persons, rooms, items, world);
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event(room) {");
        sourceCode.append("room.sendMessage('Birds chirp in the trees.');");
        sourceCode.append("room.sendMessage('Hotblack','%SNAME say%VERB: Hello, everyone.');");
        sourceCode.append("room.sendMessageExcl('Hotblack','%SNAME is whispering something.');");
        sourceCode.append("return false;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(room, sourceCode.toString());
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(RoomEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected.");
        }
    }

    @Test
    public void runRoomEventAttributes() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        RunScript runScript = new RunScript(persons, rooms, items, world);
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event(room) {");
        sourceCode.append("room.setAttribute('scripting','true');");
        sourceCode.append("return false;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(room, sourceCode.toString());
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(RoomEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected.");
        }
        assertTrue(room.verifyAttribute("scripting", "true"), "scripting atttribute of room should be true");
    }
}
