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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;

import mmud.database.entities.items.Item;
import mmud.scripting.RunScript;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

/**
 * Tests the new Items global javascript object that is used
 * to create new inventory - scriptwise.
 *
 * @author maartenl
 */
public class ItemRunScriptTest extends RunScriptTest
{

    public ItemRunScriptTest()
    {
    }

    @Test
    public void runAddItemToPerson() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        String command = "create signature";
      RunScript runScript = new RunScript(persons, rooms, items, world, null);
      StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function command(person, command) {");
        sourceCode.append("person.sendMessage('%SNAME writes his name in big flowing letters on a piece of paper.');");
        sourceCode.append("var newitem = items.createItem(4);");
        sourceCode.append("person.addItem(newitem);");
        sourceCode.append("return;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(hotblack, command, sourceCode.toString());
            Set<Item> inventory = hotblack.getItems();
            assertThat(inventory).hasSize(1);
            Item item = inventory.iterator().next();
            assertThat(item.getItemDefinition().getId()).isEqualTo(4);
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(ItemRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected.");
        }
    }

    @Test
    public void runAddItemToRoom() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
      String command = "create signature";
      RunScript runScript = new RunScript(persons, rooms, items, world, null);
      StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function command(person, command) {");
        sourceCode.append("person.sendMessage('%SNAME writes his name in big flowing letters on a piece of paper.');");
      sourceCode.append("var newitem = items.createItem(4);");
      sourceCode.append("person.getRoom().addItem(newitem);");
      sourceCode.append("return;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(hotblack, command, sourceCode.toString());
            Set<Item> inventory = hotblack.getRoom().getItems();
            assertThat(inventory).hasSize(1);
            Item item = inventory.iterator().next();
            assertThat(item.getItemDefinition().getId()).isEqualTo(4);
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(ItemRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected.");
        }
    }

    @Test
    public void runAddItemToBag() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
      String command = "create signature";
      RunScript runScript = new RunScript(persons, rooms, items, world, null);
      StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("function command(person, command) {");
        sourceCode.append("person.sendMessage('%SNAME writes his name in big flowing letters on a piece of paper.');");
        sourceCode.append("var bag = items.createItem(1);");
        sourceCode.append("var newitem = items.createItem(4);");
        sourceCode.append("person.addItem(bag);");
        sourceCode.append("bag.addItem(newitem);");
        sourceCode.append("return;");
        sourceCode.append("}");
        try
        {
            Object result = runScript.run(hotblack, command, sourceCode.toString());
            Set<Item> inventory = hotblack.getItems();
            assertThat(inventory).hasSize(1);
            Item bag = inventory.iterator().next();
            assertThat(bag.getItemDefinition().getId()).isEqualTo(1);
            assertThat(bag.isContainer()).isEqualTo(true);
            Set<Item> inbag = bag.getItems();
            assertThat(inbag).hasSize(1);
            Item item = inbag.iterator().next();
            assertThat(item.getItemDefinition().getId()).isEqualTo(4);
            assertThat(item.isContainer()).isEqualTo(false);
        } catch (ScriptException | NoSuchMethodException ex)
        {
            Logger.getLogger(ItemRunScriptTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("No error message was expected.");
        }
    }
}
