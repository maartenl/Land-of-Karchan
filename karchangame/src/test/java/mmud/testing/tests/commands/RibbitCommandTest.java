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
import java.time.LocalDateTime;

import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.RibbitCommand;
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
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author maartenl
 */
public class RibbitCommandTest extends MudTest
{

  private Administrator karn;
  private User marvin;

  private LogServiceStub logService;

  private CommandRunner commandRunner = new CommandRunner();

  public RibbitCommandTest()
  {
  }

  /**
   * Runs the ribbit command, but one is not a frog, so no ribbitting is
   * required or even possible.
   */
  @Test
  public void runRibbitWithoutBeingFrogged()
  {
    RibbitCommand ribbitCommand = new RibbitCommand("ribbit");
    ribbitCommand.setCallback(commandRunner);
    assertThat(ribbitCommand.getRegExpr()).isEqualTo("ribbit");
    DisplayInterface display = ribbitCommand.run("ribbit", marvin);
    assertThat(display).isNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).isEmpty();
  }

  /**
   * Runs the Ribbit command, bringing the punishment down from 5 to 4.
   */
  @Test
  public void runRibbit()
  {
    marvin.setFrogging(5);
    LocalDateTime seventeenSecondsAgo = LocalDateTime.now().plusSeconds(-17L);
    RibbitCommand ribbitCommand = new RibbitCommand("ribbit");
    ribbitCommand.setRibbitTime("Marvin", seventeenSecondsAgo);
    ribbitCommand.setCallback(commandRunner);
    assertThat(ribbitCommand.getRegExpr()).isEqualTo("ribbit");
    DisplayInterface display = ribbitCommand.run("ribbit", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).isEqualTo("A frog called Marvin says &#34;Rrribbit!&#34;.<br />\nYou feel the need to say &#39;Ribbit&#39; just 4 times.<br />\r\n");
    assertThat(logService.getLog()).isEmpty();
  }

  /**
   * Warning that you are issuing the ribbit command too fast. No points are
   * deducted, and you must wait for more time to pass. (at least 10 seconds)
   */
  @Test
  public void runRibbitFast()
  {
    marvin.setFrogging(5);
    RibbitCommand ribbitCommand = new RibbitCommand("ribbit");
    ribbitCommand.setCallback(commandRunner);
    ribbitCommand.setRibbitTime("Marvin", LocalDateTime.now());
    assertThat(ribbitCommand.getRegExpr()).isEqualTo("ribbit");
    DisplayInterface display = ribbitCommand.run("ribbit", marvin);
    assertThat(display).isNotNull();
    String log = CommunicationService.getCommunicationService(marvin).getLog(0L).log;
    assertThat(log).isEqualTo("You cannot say &#39;Ribbit&#39; that fast! You will get tongue tied!<br />\r\n");
    assertThat(logService.getLog()).isEmpty();
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
