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
import mmud.commands.AdminCommand;
import mmud.commands.CommandRunner;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudException;
import mmud.exceptions.PersonNotFoundException;
import mmud.rest.services.PersonBean;
import mmud.testing.TestingConstants;
import mmud.testing.tests.LogBeanStub;
import mmud.testing.tests.MudTest;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testng.Assert.fail;

/**
 *
 * @author maartenl
 */
public class AdminCommandTest extends MudTest
{

    private Administrator karn;
    private User marvin;

    private LogBeanStub logBean;

    @Mocked
    private CommandRunner commandRunner;

    private PersonBean personBean;

    public AdminCommandTest()
    {
    }

    @BeforeMethod
    public void setup()
    {
        logBean = new LogBeanStub();
    }

    @Test
    public void runAdminCommandAsUser()
    {
        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        DisplayInterface display = adminCommand.run("admin visible false", marvin);
        assertThat(display).isNotNull();
        String log = marvin.getLog(0);
        assertThat(log).isEqualTo("You are not an administrator.<br />\n");
    }

    @Test
    public void visibleOnTest()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getLogBean();
                result = logBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin visible on", karn);
        assertThat(display).isNotNull();
        assertThat(karn.getVisible()).isEqualTo(true);
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("Setting visibility to true.<br />\n");
        assertThat(logBean.getLog()).isEqualTo("Karn: turned visible.\n");
    }

    @Test
    public void visibleOffTest()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getLogBean();
                result = logBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin visible off", karn);
        assertThat(display).isNotNull();
        assertThat(karn.getVisible()).isEqualTo(false);
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("Setting visibility to false.<br />\n");
        assertThat(logBean.getLog()).isEqualTo("Karn: turned invisible.\n");
    }

    @Test
    public void froggingOn()
    {
        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        assertThat(marvin.getFrogging()).isEqualTo(0);
        assertThat(marvin.getJackassing()).isEqualTo(0);
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;
                commandRunner.getLogBean();
                result = logBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin frog marvin 5", karn);
        assertThat(display).isNotNull();
        assertThat(marvin.getFrogging()).isEqualTo(5);
        assertThat(marvin.getJackassing()).isEqualTo(0);
        assertThat(karn.getLog(0)).isEqualTo("Changed Marvin into frog (5).<br />\nMarvin is suddenly changed into a frog!<br />\n");
        assertThat(marvin.getLog(0)).isEqualTo("You are suddenly changed into a frog!<br />\n");
        assertThat(logBean.getLog()).isEqualTo("Marvin: was changed into a frog by Karn for 5.\n");
    }

    @Test
    public void jackassingOn()
    {
        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        assertThat(marvin.getFrogging()).isEqualTo(0);
        assertThat(marvin.getJackassing()).isEqualTo(0);
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;
                commandRunner.getLogBean();
                result = logBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin jackass marvin 5", karn);
        assertThat(display).isNotNull();
        assertThat(marvin.getFrogging()).isEqualTo(0);
        assertThat(marvin.getJackassing()).isEqualTo(5);
        assertThat(karn.getLog(0)).isEqualTo("Changed Marvin into jackass (5).<br />\nMarvin is suddenly changed into a jackass!<br />\n");
        assertThat(marvin.getLog(0)).isEqualTo("You are suddenly changed into a jackass!<br />\n");
        assertThat(logBean.getLog()).isEqualTo("Marvin: was changed into a jackass by Karn for 5.\n");
    }

    @Test
    public void froggingOnUserNotFound()
    {
        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        assertThat(marvin.getFrogging()).isEqualTo(0);
        assertThat(marvin.getJackassing()).isEqualTo(0);
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;

            }
        };
        try
        {
            DisplayInterface display = adminCommand.run("admin frog slartibartfast 5", karn);
        } catch (PersonNotFoundException exception)
        {
            assertThat(exception.getMessage()).isEqualTo("slartibartfast not found.");
        }
    }

    @Test
    public void jackassingOnUserNotFound()
    {
        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        assertThat(marvin.getFrogging()).isEqualTo(0);
        assertThat(marvin.getJackassing()).isEqualTo(0);
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;

            }
        };
        try
        {
            DisplayInterface display = adminCommand.run("admin jackass slartibartfast 5", karn);
            fail("Exception expected.");
        } catch (PersonNotFoundException exception)
        {
            assertThat(exception.getMessage()).isEqualTo("slartibartfast not found.");
        }

    }

    @Test
    public void froggingOnNumberNotParsed()
    {
        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        assertThat(marvin.getFrogging()).isEqualTo(0);
        assertThat(marvin.getJackassing()).isEqualTo(0);
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;

            }
        };
        try
        {
            DisplayInterface display = adminCommand.run("admin frog marvin jimminy", karn);
        } catch (MudException exception)
        {
            assertThat(exception.getMessage()).isEqualTo("Number for amount could not be parsed.");
        }
    }

    @Test
    public void jackassingOnNumberNotParsed()
    {
        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        assertThat(marvin.getFrogging()).isEqualTo(0);
        assertThat(marvin.getJackassing()).isEqualTo(0);
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;

            }
        };
        try
        {
            DisplayInterface display = adminCommand.run("admin jackass marvin jimminy", karn);
            fail("Exception expected.");
        } catch (MudException exception)
        {
            assertThat(exception.getMessage()).isEqualTo("Number for amount could not be parsed.");
        }

    }

    @Test(expectedExceptions = MudException.class, expectedExceptionsMessageRegExp = "Expected admin kick command in the shape of 'admin kick personname minutes'.")
    public void testKickWrongNrOfArguments()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin kick", karn);

    }

    @Test
    public void testKick()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;

                commandRunner.getLogBean();
                result = logBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin kick marvin", karn);

        // check for deactivation
        assertThat(marvin.isActive()).isEqualTo(false);
        assertThat(marvin.getTimeout()).isEqualTo(0);

        assertThat(display).isNotNull();
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("Karn causes Marvin to cease to exist for 0 minutes.<br />\n");
        log = marvin.getLog(0);
        assertThat(log).isEqualTo("Karn causes Marvin to cease to exist for 0 minutes.<br />\n");
        assertThat(logBean.getLog()).isEqualTo("Marvin: has been kicked out of the game by Karn for 0 minutes.\n");
    }

    @Test
    public void testKickWithMinutes()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;

                commandRunner.getLogBean();
                result = logBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin kick marvin 60", karn);

        // check for deactivation
        assertThat(marvin.isActive()).isEqualTo(false);
        assertThat(marvin.getTimeout()).isEqualTo(59);

        assertThat(display).isNotNull();
        String log = karn.getLog(0);
        assertThat(log).isEqualTo("Karn causes Marvin to cease to exist for 60 minutes.<br />\n");
        log = marvin.getLog(0);
        assertThat(log).isEqualTo("Karn causes Marvin to cease to exist for 60 minutes.<br />\n");
        assertThat(logBean.getLog()).isEqualTo("Marvin: has been kicked out of the game by Karn for 60 minutes.\n");
    }

    @Test(expectedExceptions = MudException.class, expectedExceptionsMessageRegExp = "Number for minutes could not be parsed.")
    public void testKickWithNegativeMinutes()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin kick marvin -5", karn);

    }

    @Test(expectedExceptions = MudException.class, expectedExceptionsMessageRegExp = "Number for minutes could not be parsed.")
    public void testKickWithBogusMinutes()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin kick marvin boogey", karn);

    }

    @Test(expectedExceptions = MudException.class, expectedExceptionsMessageRegExp = "janedoe not found.")
    public void testKickWithUnknownPerson()
    {

        AdminCommand adminCommand = new AdminCommand("admin .+");
        adminCommand.setCallback(commandRunner);
        assertThat(adminCommand.getRegExpr()).isEqualTo("admin .+");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;
            }
        };
        DisplayInterface display = adminCommand.run("admin kick janedoe 60", karn);

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
        personBean = new PersonBean()
        {
            @Override
            public User getActiveUser(String name)
            {
                if (name.equalsIgnoreCase("Marvin"))
                {
                    return marvin;
                }
                if (name.equalsIgnoreCase("Karn"))
                {
                    return karn;
                }
                return null;
            }

            @Override
            public List<User> getActivePlayers()
            {
                return Arrays.asList(marvin, karn);
            }

        };
        setField(PersonBean.class, "logBean", personBean, logBean);

        karn = TestingConstants.getKarn();
        final Room room = TestingConstants.getRoom(TestingConstants.getArea());
        karn.setRoom(room);
        marvin = TestingConstants.getMarvin(room);
        karn.clearLog();
        marvin.clearLog();
        HashSet<Person> persons = new HashSet<>();
        persons.add(karn);
        persons.add(marvin);
        setField(Room.class, "persons", room, persons);
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
