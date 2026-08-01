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

import mmud.scripting.RunScript;
import org.graalvm.polyglot.PolyglotException;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author maartenl
 */
public class GlobalEventRunScriptTest extends RunScriptTest
{

  public GlobalEventRunScriptTest()
  {
  }

  @Test(expectedExceptions = PolyglotException.class, expectedExceptionsMessageRegExp = "ReferenceError: event is not defined")
  public void runGlobalEventEmptySource()
  {
    RunScript runScript = new RunScript(persons, rooms, items, world, null);
    Object result = runScript.run("");
  }

  @Test
  public void runGlobalEvent()
  {
    RunScript runScript = new RunScript(persons, rooms, items, world, null);
    StringBuilder sourceCode = new StringBuilder();
    sourceCode.append("function event() {");
    sourceCode.append("return false;");
    sourceCode.append("}");
    Object result = runScript.run(sourceCode.toString());
  }

  @Test
  public void runPersonEvent()
  {
    RunScript runScript = new RunScript(persons, rooms, items, world, null);
    StringBuilder sourceCode = new StringBuilder();
    sourceCode.append("function event() {\n");
    sourceCode.append("var person = persons.find('hotblack');\n");
    sourceCode.append("return false;\n");
    sourceCode.append("}");
    Object result = runScript.run(sourceCode.toString());
  }

  @Test
  public void runRoomEvent()
  {
    RunScript runScript = new RunScript(persons, rooms, items, world, null);
    StringBuilder sourceCode = new StringBuilder();
    sourceCode.append("function event() {");
    sourceCode.append("var room = rooms.find(1);");
    sourceCode.append("return false;");
    sourceCode.append("}");
    Object result = runScript.run(room, sourceCode.toString());
  }
}
