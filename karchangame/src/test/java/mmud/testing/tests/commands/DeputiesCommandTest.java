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
import mmud.commands.DeputiesCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.rest.services.ItemBean;
import mmud.rest.services.admin.AdminBean;
import mmud.testing.tests.LogBeanStub;
import mmud.testing.tests.MudTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import mmud.services.CommunicationService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 *
 * @author maartenl
 */
public class DeputiesCommandTest extends MudTest
{

    private User karn;
    private Room room1;
    private NormalItem ring;
    private ItemDefinition itemDef;
    private ItemBean itemBean;

    private LogBeanStub logBean;

    private CommandRunner commandRunner = new CommandRunner();
    private AdminBean adminBean;
    private Admin karnAdmin;
    private Admin mideviaAdmin;

    public DeputiesCommandTest()
    {
    }

    @Test
    public void showDeputys()
    {
        DeputiesCommand deputiesCommand = new DeputiesCommand("deputies")
        {
            @Override
            protected AdminBean getAdminBean()
            {
                return adminBean;
            }
        };
        deputiesCommand.setCallback(commandRunner);
        assertThat(deputiesCommand.getRegExpr()).isEqualTo("deputies");
        DisplayInterface display = deputiesCommand.run("deputies", karn);
        assertThat(display).isNotNull();
        assertThat(display.getBody()).isEqualTo("You are in a small room.");
        String log = CommunicationService.getCommunicationService(karn).getLog(0);
        assertThat(log).isEqualTo("Current deputies are Karn, Midevia.<br />");
    }

    @BeforeMethod
    public void setUpMethod() throws Exception
    {
        logBean = new LogBeanStub();

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

        adminBean = new AdminBean()
        {
            @Override
            public List<Admin> getAdministrators()
            {
                return Arrays.asList(karnAdmin, mideviaAdmin);
            }

        };

        File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
        PrintWriter writer = new PrintWriter(file);
        writer.close();
    }

}
