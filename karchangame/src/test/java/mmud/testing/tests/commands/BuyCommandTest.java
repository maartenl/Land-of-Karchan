/*
 * Copyright (C) 2014 maartenl
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
package mmud.testing.tests.commands;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import mmud.commands.CommandRunner;
import mmud.commands.items.BuyCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.Shopkeeper;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.rest.services.ItemBean;
import mmud.rest.services.LogBean;
import mockit.Expectations;
import mockit.Mocked;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class BuyCommandTest
{

    private User karn;
    private Room room1;
    private NormalItem ring;
    private ItemDefinition itemDef;
    private ItemBean itemBean;

    @Mocked
    private LogBean logBean;

    @Mocked
    private CommandRunner commandRunner;
    

    public <T> void setField(Class<T> targetClass, String fieldName, Object object, Object value)
    {
        try
        {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex)
        {
            Logger.getLogger(BuyCommandTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    public BuyCommandTest()
    {
    }

    @Test
    public void buyItemShopkeeperDoesNotHave()
    {

        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);

        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        DisplayInterface display = buyCommand.run("buy brush from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("The shopkeeper doesn't have that.<br />\n"));
    }

    @Test
    public void buyIllegalAmount()
    {
        User karcas = new User();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karcas, items);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        DisplayInterface display = buyCommand.run("buy -2 ring from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("That is an illegal amount.<br />\n"));
    }

    @Test
    public void buyNotEnoughItems()
    {
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karcas, items);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        DisplayInterface display = buyCommand.run("buy 3 ring from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("The shopkeeper doesn't have that many items in stock.<br />\n"));
    }

    @Test
    public void buyToUnknownShopkeeper()
    {
        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("Unable to locate shopkeeper.<br />\n"));
    }

    @Test
    public void buyFromNonShopkeeper()
    {
        User karcas = new User();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karcas, items);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("That's not a shopkeeper!<br />\n"));
    }

    @Test
    public void buyItemNotWorthAnything() throws NamingException
    {
        ring.getItemDefinition().setCopper(0);
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karcas, items);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("Karcas says [to you] : That item is not worth anything.<br />\nYou did not buy anything.<br />\n"));
    }

    @Test
    public void buyCustomerHasNoMoney() throws NamingException
    {
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karcas, items);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("You do not have enough money.<br />\nYou did not buy anything.<br />\n"));
    }

    @Test
    public void buyItemdefIdNegative() throws NamingException
    {
        ring.getItemDefinition().setId(-3);
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);
        setField(Person.class, "copper", karcas, 1111);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karcas, items);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("You cannot buy that item.<br />\nYou did not buy anything.<br />\n"));
    }

    @Test
    public void buyBound() throws NamingException
    {
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);
        setField(Person.class, "copper", karn, 1111);
        ring.getItemDefinition().setBound(true);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karcas, items);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("You cannot buy that item.<br />\nYou did not buy anything.<br />\n"));
    }

    @Test
    public void buyOk() throws NamingException
    {
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);
        setField(Person.class, "copper", karn, 1111);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karcas, items);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr(), equalTo("buy( (\\w|-)+){1,4} from (\\w)+"));
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("You buy a nice, golden, friendship ring from Karcas.<br />\n"));
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception
    {
        itemBean = new ItemBean();
        setField(ItemBean.class, "logBean", itemBean, logBean);

        itemDef = new ItemDefinition();
        itemDef.setId(1);
        itemDef.setName("ring");
        itemDef.setAdject1("nice");
        itemDef.setAdject2("golden");
        itemDef.setAdject3("friendship");
        itemDef.setCopper(5);

        ring = new NormalItem();
        ring.setItemDefinition(itemDef);

        room1 = new Room();
        room1.setId(1);
        room1.setContents("You are in a small room.");

        karn = new User();
        karn.setName("Karn");
        karn.setRoom(room1);

        File file = new File("/home/glassfish/temp/Karn.log");
        PrintWriter writer = new PrintWriter(file);
        writer.close();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception
    {
    }
}
