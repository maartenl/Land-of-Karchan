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
import mmud.commands.WhoCommand;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.rest.services.LogBean;
import mmud.rest.services.PersonBean;
import mmud.testing.TestingConstants;
import mmud.testing.tests.MudTest;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author maartenl
 */
public class WhoCommandTest extends MudTest
{

    private Administrator karn;
    private User marvin;

    private LogBeanImpl logBean;

    @Mocked
    private CommandRunner commandRunner;

    private PersonBean personBean;

    public WhoCommandTest()
    {
    }

    @BeforeMethod
    public void setup()
    {
        logBean = new LogBeanImpl();
    }

    @Test
    public void runWhoCommand()
    {
        WhoCommand whoCommand = new WhoCommand("who");
        whoCommand.setCallback(commandRunner);
        assertThat(whoCommand.getRegExpr()).isEqualTo("who");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getPersonBean();
                result = personBean;

            }
        };
        DisplayInterface display = whoCommand.run("who", marvin);
        assertThat(display).isNotNull();
        assertThat(marvin.getLog(0)).isEqualTo("");
        assertThat(display.getMainTitle()).isEqualTo("Who");
        assertThat(display.getImage()).isNull();
        assertThat(display.getBody()).contains("There are 2 players.");
        assertThat(display.getBody()).contains("Karn, Ruler of Karchan, Keeper of the Key to the Room of Lost Souls");
        assertThat(display.getBody()).contains("Marvin, The Paranoid Android");
    }

    @Test
    public void runWhoCommandSleeping()
    {
        WhoCommand whoCommand = new WhoCommand("who");
        marvin.setSleep(true);
        whoCommand.setCallback(commandRunner);
        assertThat(whoCommand.getRegExpr()).isEqualTo("who");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getPersonBean();
                result = personBean;

            }
        };
        DisplayInterface display = whoCommand.run("who", marvin);
        assertThat(display).isNotNull();
        assertThat(marvin.getLog(0)).isEqualTo("");
        assertThat(display.getMainTitle()).isEqualTo("Who");
        assertThat(display.getImage()).isNull();
        assertThat(display.getBody()).contains("There are 2 players.");
        assertThat(display.getBody()).contains("Karn, Ruler of Karchan, Keeper of the Key to the Room of Lost Souls");
        assertThat(display.getBody()).contains("Marvin, The Paranoid Android, sleeping");
    }

    @Test
    public void runWhoCommandInvisible()
    {
        WhoCommand whoCommand = new WhoCommand("who");
        karn.setVisible(false);
        whoCommand.setCallback(commandRunner);
        assertThat(whoCommand.getRegExpr()).isEqualTo("who");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getPersonBean();
                result = personBean;

            }
        };
        DisplayInterface display = whoCommand.run("who", marvin);
        assertThat(display).isNotNull();
        assertThat(marvin.getLog(0)).isEqualTo("");
        assertThat(display.getMainTitle()).isEqualTo("Who");
        assertThat(display.getImage()).isNull();
        assertThat(display.getBody()).contains("There are 1 players.");
        assertThat(display.getBody()).doesNotContain("Karn, Ruler of Karchan, Keeper of the Key to the Room of Lost Souls");
        assertThat(display.getBody()).contains("Marvin, The Paranoid Android");
    }

    @Test
    public void runWhoCommandFrog()
    {
        WhoCommand whoCommand = new WhoCommand("who");
        karn.setFrogging(5);
        whoCommand.setCallback(commandRunner);
        assertThat(whoCommand.getRegExpr()).isEqualTo("who");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getPersonBean();
                result = personBean;

            }
        };
        DisplayInterface display = whoCommand.run("who", marvin);
        assertThat(display).isNotNull();
        assertThat(marvin.getLog(0)).isEqualTo("");
        assertThat(display.getMainTitle()).isEqualTo("Who");
        assertThat(display.getImage()).isNull();
        assertThat(display.getBody()).contains("There are 2 players.");
        assertThat(display.getBody()).contains("a frog called Karn, Ruler of Karchan, Keeper of the Key to the Room of Lost Souls");
        assertThat(display.getBody()).contains("Marvin, The Paranoid Android");
    }

    @Test
    public void runWhoCommandJackass()
    {
        WhoCommand whoCommand = new WhoCommand("who");
        karn.setJackassing(5);
        whoCommand.setCallback(commandRunner);
        assertThat(whoCommand.getRegExpr()).isEqualTo("who");
        new Expectations() // an "expectation block"
        {


            {
                commandRunner.getPersonBean();
                result = personBean;

            }
        };
        DisplayInterface display = whoCommand.run("who", marvin);
        assertThat(display).isNotNull();
        assertThat(marvin.getLog(0)).isEqualTo("");
        assertThat(display.getMainTitle()).isEqualTo("Who");
        assertThat(display.getImage()).isNull();
        assertThat(display.getBody()).contains("There are 2 players.");
        assertThat(display.getBody()).contains("a jackass called Karn, Ruler of Karchan, Keeper of the Key to the Room of Lost Souls");
        assertThat(display.getBody()).contains("Marvin, The Paranoid Android");
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
            public List<User> getActivePlayers()
            {
                return Arrays.asList(karn, marvin);
            }

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

    private class LogBeanImpl extends LogBean
    {

        public LogBeanImpl()
        {
        }
        private final StringBuffer buffer = new StringBuffer();

        @Override
        public void writeLog(Person person, String message)
        {
            buffer.append(person.getName()).append(":").append(message).append("\n");
        }

        public String getLog()
        {
            return buffer.toString();
        }
    }
}
