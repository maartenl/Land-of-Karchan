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
import java.util.Arrays;
import java.util.List;

import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.OwnerCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.services.AdminService;
import mmud.services.CommunicationService;
import mmud.services.ItemService;
import mmud.services.LogService;
import mmud.testing.tests.LogServiceStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 *
 * @author maartenl
 */
public class OwnerCommandTest extends MudTest
{

  private User karn;
  private Room room1;
  private NormalItem ring;
  private ItemDefinition itemDef;
  private ItemService itemService;

  private LogServiceStub logBean;

  private CommandRunner commandRunner = new CommandRunner();
  private AdminService adminService;
  private Admin karnAdmin;
  private Admin mideviaAdmin;

  public OwnerCommandTest()
  {
  }

  @Test
  public void showEmptyOwner()
  {
    OwnerCommand ownerCommand = new OwnerCommand("owner( (\\w)+)?");
        ownerCommand.setCallback(commandRunner);
        assertThat(ownerCommand.getRegExpr()).isEqualTo("owner( (\\w)+)?");
        DisplayInterface display = ownerCommand.run("owner", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
        assertThat(log).isEqualTo("You are not owned.<br />");
    }

    @Test
    public void showKarnOwner()
    {
        Admin owner = new Admin();
        owner.setName("Midevia");
        karn.setOwner(owner);
        OwnerCommand ownerCommand = new OwnerCommand("owner( (\\w)+)?");
        ownerCommand.setCallback(commandRunner);
        assertThat(ownerCommand.getRegExpr()).isEqualTo("owner( (\\w)+)?");
        DisplayInterface display = ownerCommand.run("owner", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
      String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
        assertThat(log).isEqualTo("Your current owner is Midevia.<br />");
    }

    @Test
    public void removeKarnOwner()
    {
        Admin owner = new Admin();
        owner.setName("Midevia");
        karn.setOwner(owner);
        OwnerCommand ownerCommand = new OwnerCommand("owner( (\\w)+)?")
        {

          @Override
          protected LogService getLogService()
          {
            return logBean;
          }
        };
        ownerCommand.setCallback(commandRunner);
        assertThat(ownerCommand.getRegExpr()).isEqualTo("owner( (\\w)+)?");
        DisplayInterface display = ownerCommand.run("owner remove", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
      String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
        assertThat(log).isEqualTo("Owner removed.<br />");
        assertThat(logBean.getLog()).isEqualTo("Karn:has removed owner Midevia\n");
    }

    @Test
    public void setKarnOwner()
    {
        Admin owner = new Admin();
        owner.setName("Karn");
        karn.setOwner(null);
        OwnerCommand ownerCommand = new OwnerCommand("owner( (\\w)+)?")
        {
          @Override
          protected AdminService getAdminService()
          {
            return adminService;
          }

          @Override
          protected LogService getLogService()
          {
            return logBean;
          }
        };
        ownerCommand.setCallback(commandRunner);
        assertThat(ownerCommand.getRegExpr()).isEqualTo("owner( (\\w)+)?");
        DisplayInterface display = ownerCommand.run("owner Karn", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
      String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
        assertThat(log).isEqualTo("You are now owned by Karn.<br />");
        assertThat(karn.getOwner()).isEqualTo(karnAdmin);
        assertThat(logBean.getLog()).isEqualTo("Karn:has set owner to Karn\n");
    }

    @Test
    public void setKarnOwnerErasePreviousOwner()
    {
        Admin owner = new Admin();
        owner.setName("Karn");
        karn.setOwner(mideviaAdmin);
        OwnerCommand ownerCommand = new OwnerCommand("owner( (\\w)+)?")
        {
          @Override
          protected AdminService getAdminService()
          {
            return adminService;
          }

          @Override
          protected LogService getLogService()
          {
            return logBean;
          }

        };
        ownerCommand.setCallback(commandRunner);
        assertThat(ownerCommand.getRegExpr()).isEqualTo("owner( (\\w)+)?");
        DisplayInterface display = ownerCommand.run("owner Karn", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
      String log = CommunicationService.getCommunicationService(karn).getLog(0L).log;
        assertThat(log).isEqualTo("You are now owned by Karn.<br />");
        assertThat(karn.getOwner()).isEqualTo(karnAdmin);
        assertThat(logBean.getLog()).isEqualTo("Karn:has set owner to Karn\n");
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
        logBean = new LogServiceStub();

        room1 = new Room();
        room1.setId(1L);
        room1.setContents("You are in a small room.");

      karnAdmin = new Admin();
      karnAdmin.setName("Karn");

      mideviaAdmin = new Admin();
      mideviaAdmin.setName("Midevia");

      karn = new User();
      karn.setName("Karn");
      karn.setRoom(room1);

      adminService = new AdminService()
      {
        @Override
        public List<Admin> getAdministrators()
        {
          return Arrays.asList(karnAdmin, mideviaAdmin);
        }

      };
//        setField(PersonBean.class, "logService", personBean, logBean);

      File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
      PrintWriter writer = new PrintWriter(file);
      writer.close();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception
    {
    }
}
