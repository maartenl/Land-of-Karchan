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
public class GlobalEventRunScriptTest extends RunScriptTest
{

    public GlobalEventRunScriptTest()
    {
    }

    @Test
    public void runGlobalEventEmptySource()
    {
        Lookup lookup = new Lookup();
        RunScript runScript = new RunScript(lookup);
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
            assertEquals(ex.getMessage(), "no such method: event");
        }
    }

    @Test
    public void runGlobalEvent()
    {
        Lookup lookup = new Lookup();
        RunScript runScript = new RunScript(lookup);
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event() {");
        sourceCode.append("println('Hello, world.');");
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
}
