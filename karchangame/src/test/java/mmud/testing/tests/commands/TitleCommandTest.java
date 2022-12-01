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

import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.TitleCommand;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.services.CommunicationService;
import mmud.testing.TestingConstants;
import mmud.testing.tests.LogServiceStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author maartenl
 */
public class TitleCommandTest extends MudTest
{

  private Administrator karn;
  private User marvin;

  private LogServiceStub logService;

  private CommandRunner commandRunner = new CommandRunner();

  public TitleCommandTest()
  {
  }

  @Test
  public void setTitle()
  {
    assertThat(marvin.getTitle()).isEqualTo("The Paranoid Android");
    TitleCommand titleCommand = new TitleCommand("(title|title ?.+)");
    titleCommand.setCallback(commandRunner);
    assertThat(titleCommand.getRegExpr()).isEqualTo("(title|title ?.+)");
    DisplayInterface display = titleCommand.run("title Ruler of the Land", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L);
    assertThat(log).isEqualTo("Changed your title to : &#39;Ruler of the Land&#39;.<br />\r\n");
    assertThat(marvin.getTitle()).isEqualTo("Ruler of the Land");
  }

  @Test
  public void removeTitle()
  {
    assertThat(marvin.getTitle()).isEqualTo("The Paranoid Android");
    TitleCommand titleCommand = new TitleCommand("(title|title ?.+)");
    titleCommand.setCallback(commandRunner);
    assertThat(titleCommand.getRegExpr()).isEqualTo("(title|title ?.+)");
    DisplayInterface display = titleCommand.run("title remove", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L);
    assertThat(log).isEqualTo("You have removed your current title.<br />\r\n");
    assertThat(marvin.getTitle()).isNull();
  }

  @Test
  public void getTitle()
  {
    assertThat(marvin.getTitle()).isEqualTo("The Paranoid Android");
    TitleCommand titleCommand = new TitleCommand("(title|title ?.+)");
    titleCommand.setCallback(commandRunner);
    assertThat(titleCommand.getRegExpr()).isEqualTo("(title|title ?.+)");
    DisplayInterface display = titleCommand.run("title", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L);
    assertThat(log).isEqualTo("Your current title is &#39;The Paranoid Android&#39;.<br />\r\n");
    assertThat(marvin.getTitle()).isEqualTo("The Paranoid Android");
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
    logService = new LogServiceStub();

    karn = TestingConstants.getKarn();
    final Room room = TestingConstants.getRoom(TestingConstants.getArea());
    karn.setRoom(room);
    marvin = TestingConstants.getMarvin(room);
    CommunicationService.getCommunicationService(karn).clearLog();
    CommunicationService.getCommunicationService(marvin).clearLog();
    room.addPerson(karn);
    room.addPerson(marvin);
    File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
    PrintWriter writer = new PrintWriter(file);
    writer.close();
    file = new File(Constants.getMudfilepath() + File.separator + "Marvin.log");
    writer = new PrintWriter(file);
    writer.close();
  }

}
