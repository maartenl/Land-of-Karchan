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

import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.items.SellCommand;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.Shopkeeper;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.database.enums.Wearing;
import mmud.database.enums.Wielding;
import mmud.rest.services.ItemBean;
import mmud.rest.services.LogBean;
import mmud.testing.tests.MudTest;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.*;

import javax.naming.NamingException;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author maartenl
 */
public class SellCommandTest extends MudTest
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

    public SellCommandTest()
    {
    }

    @Test
    public void sellItemYouDonotHave()
    {
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        DisplayInterface display = sellCommand.run("sell brush to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("You don&#39;t have that.<br />\n");
    }

    @Test
    public void sellIllegalAmount()
    {
        User karcas = new User();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karn, items);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);

        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        DisplayInterface display = sellCommand.run("sell -2 ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("That is an illegal amount.<br />\n");
    }

    @Test
    public void sellNotEnoughItems()
    {
        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karn, items);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        DisplayInterface display = sellCommand.run("sell 3 ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("You do not have that many items in your inventory.<br />\r\n");
    }

    @Test
    public void sellToUnknownShopkeeper()
    {
        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karn, items);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("Unable to locate shopkeeper.<br />\r\n");
    }

    @Test
    public void sellToNonShopkeeper()
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
        setField(Person.class, "items", karn, items);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("That&#39;s not a shopkeeper!<br />\r\n");
    }

    @Test
    public void sellItemNotWorthAnything() throws NamingException
    {
        ring.getItemDefinition().setCopper(1);
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karn, items);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        sellCommand.setCallback(commandRunner);
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("Karcas says [to you] : That item is not worth anything.<br />\r\nYou did not sell anything.<br />\r\n");
    }

    @Test
    public void sellShopkeeperHasNoMoney() throws NamingException
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
        setField(Person.class, "items", karn, items);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        sellCommand.setCallback(commandRunner);
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("Karcas mutters something about not having enough money.<br />\r\nYou did not sell anything.<br />\r\n");
    }

    @Test
    public void sellWearingItem() throws NamingException
    {
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);
        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);
        setField(Person.class, "copper", karcas, 1111);
        setField(ItemDefinition.class, "wearable", ring.getItemDefinition(), Wearing.ON_LEFT_FINGER.toInt());
        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karn, items);
        karn.wear(ring, Wearing.ON_LEFT_FINGER);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        sellCommand.setCallback(commandRunner);
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("You are wearing or wielding this item.<br />\r\nYou did not sell anything.<br />\r\n");
    }

    @Test
    public void sellWieldingItem() throws NamingException
    {
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);
        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        setField(Room.class, "persons", room1, persons);
        setField(Person.class, "copper", karcas, 1111);

        HashSet<Item> items = new HashSet<>();
        setField(ItemDefinition.class, "wieldable", ring.getItemDefinition(), Wielding.WIELD_LEFT.toInt());
        items.add(ring);
        setField(Person.class, "items", karn, items);
        karn.wield(ring, Wielding.WIELD_LEFT);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        sellCommand.setCallback(commandRunner);
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("You are wearing or wielding this item.<br />\r\nYou did not sell anything.<br />\r\n");
    }

    @Test
    public void sellItemdefIdNegative() throws NamingException
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
        setField(Person.class, "items", karn, items);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        sellCommand.setCallback(commandRunner);
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("You cannot sell that item.<br />\r\nYou did not sell anything.<br />\r\n");
    }

    @Test
    public void sellBound() throws NamingException
    {
        Shopkeeper karcas = new Shopkeeper();
        karcas.setName("Karcas");
        karcas.setRoom(room1);

        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(karcas);
        ring.getItemDefinition().setBound(true);
        setField(Room.class, "persons", room1, persons);
        setField(Person.class, "copper", karcas, 1111);

        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        setField(Person.class, "items", karn, items);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        sellCommand.setCallback(commandRunner);
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("You cannot sell that item.<br />\r\nYou did not sell anything.<br />\r\n");
    }

    @Test
    public void sellOk() throws NamingException
    {
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
        setField(Person.class, "items", karn, items);
        SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
        sellCommand.setCallback(commandRunner);
        assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("You sold a nice, golden, friendship ring to Karcas for 4 copper coins.<br />\r\n");
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

        File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
        PrintWriter writer = new PrintWriter(file);
        writer.close();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception
    {
    }
}
