package mmud.commands.items;

import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.database.entities.characters.Shopkeeper;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.services.CommunicationService;
import mmud.services.ItemService;
import mmud.services.LogService;
import mmud.testing.tests.LogServiceStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class ShowCommandTest extends MudTest
{
  private User karn;
  private Room room1;
  private NormalItem ring;
  private ItemDefinition itemDef;
  private ItemService itemService;

  private LogService logService = new LogServiceStub();

  private CommandRunner commandRunner = new CommandRunner();

  @Test
  public void showItemYouDonotHave()
  {
    ShowCommand ShowCommand = new ShowCommand("show( (\\w|-)+){1,4} to (\\w)+");
    assertThat(ShowCommand.getRegExpr()).isEqualTo("show( (\\w|-)+){1,4} to (\\w)+");
    DisplayInterface display = ShowCommand.run("show brush to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(log).isEqualTo("You don&#39;t have that.<br />\n");
  }

  @Test
  public void showToUnknownShopkeeper()
  {
    karn.addItem(ring);
    ShowCommand showCommand = new ShowCommand("show( (\\w|-)+){1,4} to (\\w)+");
    assertThat(showCommand.getRegExpr()).isEqualTo("show( (\\w|-)+){1,4} to (\\w)+");
    DisplayInterface display = showCommand.run("show ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(log).isEqualTo("Unable to locate shopkeeper.<br />\r\n");
  }


  @Test
  public void showToNonShopkeeper()
  {
    User karcas = new User();
    karcas.setName("Karcas");
    karcas.setRoom(room1);

    room1.addPerson(karn);
    room1.addPerson(karcas);

    karn.addItem(ring);
    ShowCommand showCommand = new ShowCommand("show( (\\w|-)+){1,4} to (\\w)+");
    assertThat(showCommand.getRegExpr()).isEqualTo("show( (\\w|-)+){1,4} to (\\w)+");
    DisplayInterface display = showCommand.run("show ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(log).isEqualTo("That&#39;s not a shopkeeper!<br />\r\n");
  }

  @Test
  public void showItemNotWorthAnything()
  {
    ring.getItemDefinition().setCopper(1);
    Shopkeeper karcas = new Shopkeeper();
    karcas.setName("Karcas");
    karcas.setRoom(room1);

    room1.addPerson(karn);
    room1.addPerson(karcas);

    karn.addItem(ring);
    ShowCommand showCommand = new ShowCommand("show( (\\w|-)+){1,4} to (\\w)+");
    showCommand.setCallback(commandRunner);
    assertThat(showCommand.getRegExpr()).isEqualTo("show( (\\w|-)+){1,4} to (\\w)+");
    commandRunner.setServices(null, null, null, itemService, null, null, null, null);
    DisplayInterface display = showCommand.run("show ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(log).isEqualTo("You show a nice, golden, friendship ring to Karcas.<br />\r\n" +
      "Karcas says [to you] : A nice, golden, friendship ring is not worth anything to me.<br />\r\n");
  }

  @Test
  public void showOk()
  {
    Shopkeeper karcas = new Shopkeeper();
    karcas.setName("Karcas");
    karcas.setRoom(room1);

    room1.addPerson(karn);
    room1.addPerson(karcas);
    karcas.setCopper(1111);

    karn.addItem(ring);
    ShowCommand showCommand = new ShowCommand("show( (\\w|-)+){1,4} to (\\w)+");
    showCommand.setCallback(commandRunner);
    assertThat(showCommand.getRegExpr()).isEqualTo("show( (\\w|-)+){1,4} to (\\w)+");
    commandRunner.setServices(null, logService, null, itemService, null, null, null, null);
    DisplayInterface display = showCommand.run("show ring to karcas", karn);
    assertThat(display).isNotNull();
    assertThat(display.getBody()).isEqualTo("You are in a small room.");
    String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
    assertThat(log).isEqualTo("You show a nice, golden, friendship ring to Karcas.<br />\r\n" +
      "Karcas says [to you] : It&#39;s worth 5 copper coins. I&#39;ll give you 4 copper coins for it.<br />\r\n");
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
    itemService = new ItemService(logService);

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
