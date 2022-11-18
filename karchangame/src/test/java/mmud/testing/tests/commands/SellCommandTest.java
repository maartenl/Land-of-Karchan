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
import java.util.HashSet;

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
import mmud.services.CommunicationService;
import mmud.services.ItemService;
import mmud.services.LogService;
import mmud.testing.tests.LogServiceStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author maartenl
 */
public class SellCommandTest extends MudTest
{

  private User karn;
  private Room room1;
  private NormalItem ring;
  private ItemDefinition itemDef;
  private ItemService itemService;

  private LogService logService = new LogServiceStub();

  private CommandRunner commandRunner = new CommandRunner();

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
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
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
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
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
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
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
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
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
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
    assertThat(log).isEqualTo("That&#39;s not a shopkeeper!<br />\r\n");
  }

  @Test
  public void sellItemNotWorthAnything()
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
    commandRunner.setServices(null, null, null, itemService, null, null, null);
    DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
    assertThat(log).isEqualTo("Karcas says [to you] : That item is not worth anything.<br />\r\nYou did not sell anything.<br />\r\n");
  }

  @Test
  public void sellShopkeeperHasNoMoney()
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
    commandRunner.setServices(null, null, null, itemService, null, null, null);
    DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
    assertThat(log).isEqualTo("Karcas mutters something about not having enough money.<br />\r\nYou did not sell anything.<br />\r\n");
  }

  @Test
  public void sellWearingItem()
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
    commandRunner.setServices(null, null, null, itemService, null, null, null);
    DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
    assertThat(log).isEqualTo("You are wearing or wielding this item.<br />\r\nYou did not sell anything.<br />\r\n");
  }

  @Test
  public void sellWieldingItem()
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
    commandRunner.setServices(null, null, null, itemService, null, null, null);
    DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
    assertThat(log).isEqualTo("You are wearing or wielding this item.<br />\r\nYou did not sell anything.<br />\r\n");
  }

  @Test
  public void sellItemdefIdNegative()
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
    setField(Person.class, "items", karn, items);
    SellCommand sellCommand = new SellCommand("sell( (\\w|-)+){1,4} to (\\w)+");
    sellCommand.setCallback(commandRunner);
    assertThat(sellCommand.getRegExpr()).isEqualTo("sell( (\\w|-)+){1,4} to (\\w)+");
    commandRunner.setServices(null, null, null, itemService, null, null, null);
    DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
    assertThat(log).isEqualTo("You cannot sell that item.<br />\r\nYou did not sell anything.<br />\r\n");
  }

  @Test
  public void sellBound()
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
    commandRunner.setServices(null, null, null, itemService, null, null, null);
    DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
    assertThat(log).isEqualTo("You cannot sell that item.<br />\r\nYou did not sell anything.<br />\r\n");
  }

  @Test
  public void sellOk()
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
    commandRunner.setServices(null, logService, null, itemService, null, null, null);
    DisplayInterface display = sellCommand.run("sell ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0);
    assertThat(log).isEqualTo("You sold a nice, golden, friendship ring to Karcas for 4 copper coins.<br />\r\n");
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
    itemService = new ItemService();
    setField(ItemService.class, "logService", itemService, logService);

    itemDef = new ItemDefinition();
    itemDef.setId(1L);
    itemDef.setName("ring");
    itemDef.setAdjectives("nice, golden, friendship");
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
