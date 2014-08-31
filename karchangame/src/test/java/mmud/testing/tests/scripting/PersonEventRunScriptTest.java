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
public class PersonEventRunScriptTest extends RunScriptTest
{

    public PersonEventRunScriptTest()
    {
    }

    @Test
    public void runPersonEventEmptySource() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        RunScript runScript = new RunScript(persons, rooms, world);
        StringBuilder sourceCode = new StringBuilder();
        try
        {
            Object result = runScript.run(hotblack, sourceCode.toString());
            fail("No event defined, so why no exception then?");
        } catch (ScriptException ex)
        {
            Logger.getLogger(PersonEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected. But we got " + ex.getMessage());
        } catch (NoSuchMethodException ex)
        {
            assertEquals(ex.getMessage(), "no such method: event");
        }
    }

    @Test
    public void runPersonEvent() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        RunScript runScript = new RunScript(persons, rooms, world);
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event(person) {");
        sourceCode.append("println('Hello, ' + person.name + '.');");
        sourceCode.append("println(person.name + ' has sex ' + person.sex + '.');");
        sourceCode.append("println(person.name + ' has guild ' + person.guild + '.');");
        sourceCode.append("return false;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(hotblack, sourceCode.toString());
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(PersonEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected. But we got " + ex.getMessage());
        }
    }

    @Test
    public void runPersonEventCommunication() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        RunScript runScript = new RunScript(persons, rooms, world);
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event(person) {");
        sourceCode.append("person.personal('My name is ' + person.name + '.');");
        sourceCode.append("person.sendMessage('%SNAME say%VERB: Hello, everyone.');");
        sourceCode.append("return false;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(hotblack, sourceCode.toString());
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(PersonEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected. But we got " + ex.getMessage());
        }
    }

    @Test
    public void runPersonEventAttributes() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        RunScript runScript = new RunScript(persons, rooms, world);
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function event(person) {");
        sourceCode.append("person.setAttribute('scripting','true');");
        sourceCode.append("println('Attribute = ' + person.getAttribute('scripting'));");
        sourceCode.append("println('Attribute is true = ' + person.getAttribute('scripting') == true);");
        sourceCode.append("return false;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(hotblack, sourceCode.toString());
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(PersonEventRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected. But we got " + ex.getMessage());
        }
        assertTrue(hotblack.verifyAttribute("scripting", "true"), "scripting atttribute of hotblack should be true");
    }
}
