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
public class CommandRunScriptTest extends RunScriptTest
{

  public CommandRunScriptTest()
  {
  }

  @Test(expectedExceptions = PolyglotException.class, expectedExceptionsMessageRegExp = "ReferenceError: command is not defined")
  public void runCommandEmptySource()
  {
    RunScript runScript = new RunScript(persons, rooms, items, world, null);
    Object result = runScript.run(hotblack, "bow", "");
  }

  @Test
  public void runCommand()
  {
    String command = "bow";
    RunScript runScript = new RunScript(persons, rooms, items, world, null);
    StringBuilder sourceCode = new StringBuilder();
    sourceCode.append("function command(person, command) {");
    sourceCode.append("person.sendMessage('%SNAME bows, and almost falls down.');");
    sourceCode.append("return;");
    sourceCode.append("}");
    Object result = runScript.run(hotblack, command, sourceCode.toString());
  }
}
