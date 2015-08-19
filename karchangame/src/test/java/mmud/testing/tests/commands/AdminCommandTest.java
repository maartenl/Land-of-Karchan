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
import mmud.commands.AdminCommand;
import mmud.commands.CommandRunner;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.rest.services.LogBean;
import mmud.testing.TestingConstants;
import mmud.testing.tests.MudTest;
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
public class AdminCommandTest extends MudTest
{

    private Administrator karn;
    private User marvin;

    @Mocked
    private LogBean logBean;

    @Mocked
    private CommandRunner commandRunner;

    public AdminCommandTest()
    {
    }

    @Test
    public void runAdminCommandAsUser()
    {
        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr(), equalTo("admin .+"));
        DisplayInterface display = adminCommand.run("admin visible false", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("You are not an administrator.<br />\n"));
    }

    @Test
    public void visibleOnTest()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr(), equalTo("admin .+"));
        DisplayInterface display = adminCommand.run("admin visible on", karn);
        assertThat(display, not(nullValue()));
        assertThat(karn.getVisible(), equalTo(true));
        String log = karn.getLog(0);
        assertThat(log, equalTo("Setting visibility to true.<br />\n"));
    }

    @Test
    public void visibleOffTest()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr(), equalTo("admin .+"));
        DisplayInterface display = adminCommand.run("admin visible off", karn);
        assertThat(display, not(nullValue()));
        assertThat(karn.getVisible(), equalTo(false));
        String log = karn.getLog(0);
        assertThat(log, equalTo("Setting visibility to false.<br />\n"));
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
        karn = TestingConstants.getKarn();
        karn.setRoom(TestingConstants.getRoom(TestingConstants.getArea()));
        marvin = TestingConstants.getMarvin(TestingConstants.getRoom(TestingConstants.getArea()));
        karn.clearLog();
        marvin.clearLog();
        File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
        PrintWriter writer = new PrintWriter(file);
        writer.close();
        file = new File(Constants.getMudfilepath() + File.separator + "Marvin.log");
        writer = new PrintWriter(file);
        writer.close();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception
    {
    }
}
