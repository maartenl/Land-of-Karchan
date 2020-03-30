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
import mmud.testing.tests.MudTest;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import mmud.services.CommunicationService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 *
 * @author maartenl
 */
public class BuyCommandTest extends MudTest
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
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        DisplayInterface display = buyCommand.run("buy brush from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("The shopkeeper doesn&#39;t have that.<br />\n");
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
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        DisplayInterface display = buyCommand.run("buy -2 ring from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("That is an illegal amount.<br />\n");
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
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        DisplayInterface display = buyCommand.run("buy 3 ring from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("The shopkeeper doesn&#39;t have that many items in stock.<br />\r\n");
    }

    @Test
    public void buyToUnknownShopkeeper()
    {
        HashSet<Item> items = new HashSet<>();
        items.add(ring);
        BuyCommand buyCommand = new BuyCommand("buy( (\\w|-)+){1,4} from (\\w)+");
        buyCommand.setCallback(commandRunner);
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("Unable to locate shopkeeper.<br />\r\n");
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
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("That&#39;s not a shopkeeper!<br />\r\n");
    }

    @Test
    public void buyItemNotWorthAnything()
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
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("Karcas says [to you] : That item is not worth anything.<br />\r\nYou did not buy anything.<br />\r\n");
    }

    @Test
    public void buyCustomerHasNoMoney()
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
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("You do not have enough money.<br />\r\nYou did not buy anything.<br />\r\n");
    }

    @Test
    public void buyItemdefIdNegative()
    {
        ring.getItemDefinition().setId(-3L);
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
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("You cannot buy that item.<br />\r\nYou did not buy anything.<br />\r\n");
    }

    @Test
    public void buyBound()
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
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("You cannot buy that item.<br />\r\nYou did not buy anything.<br />\r\n");
    }

    @Test
    public void buyOk()
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
        assertThat(buyCommand.getRegExpr()).isEqualTo("buy( (\\w|-)+){1,4} from (\\w)+");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getItemBean();
                result = itemBean;
            }
        };
        DisplayInterface display = buyCommand.run("buy ring from karcas", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("You buy a nice, golden, friendship ring from Karcas for 5 copper coins.<br />\r\n");
    }

    @BeforeMethod
    public void setUpMethod() throws Exception
    {
        itemBean = new ItemBean();
        setField(ItemBean.class, "logBean", itemBean, logBean);

        itemDef = new ItemDefinition();
        itemDef.setId(1L);
        itemDef.setName("ring");
        itemDef.setAdject1("nice");
        itemDef.setAdject2("golden");
        itemDef.setAdject3("friendship");
        itemDef.setCopper(5);

        ring = new NormalItem();
        ring.setItemDefinition(itemDef);

        room1 = new Room();
        room1.setId(1L);
        room1.setContents("You are in a small room.");

        karn = new User();
        karn.setName("Karn");
        karn.setRoom(room1);

        File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
        PrintWriter writer = new PrintWriter(file);
        writer.close();
    }
}
