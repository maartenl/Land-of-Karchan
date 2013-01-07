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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import mmud.scripting.Lookup;
import mmud.scripting.RunScript;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

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
    public void runRoomEventEmptySource()
    {
        Lookup lookup = new Lookup();
        RunScript runScript = new RunScript(lookup);
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
    public void runRoomEvent()
    {
        Lookup lookup = new Lookup();
        RunScript runScript = new RunScript(lookup);
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event(room) {");
        sourceCode.append("println('Currently in room  ' + room.id + '.');");
        sourceCode.append("println('Room has description ' + room.description + '.');");
        sourceCode.append("println('Room has title ' + room.title + '.');");
        sourceCode.append("println('Room has image ' + room.image + '.');");
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
    public void runRoomEventCommunication()
    {
        Lookup lookup = new Lookup();
        RunScript runScript = new RunScript(lookup);
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
    public void runRoomEventAttributes()
    {
        Lookup lookup = new Lookup();
        RunScript runScript = new RunScript(lookup);
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event(room) {");
        sourceCode.append("room.setAttribute('scripting','true');");
        sourceCode.append("println('Attribute scripting = ' + room.getAttribute('scripting'));");
        sourceCode.append("println('Attribute scripting is true = ' + room.verifyAttribute('scripting','true'));");
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
