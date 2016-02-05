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
package mmud.testing.tests.commands.communication;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.communication.OocCommand;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.rest.services.PersonBean;
import mmud.testing.TestingConstants;
import mmud.testing.tests.LogBeanStub;
import mmud.testing.tests.MudTest;
import mockit.Expectations;
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
public class OocCommandTest extends MudTest
{

    private Administrator karn;
    private User marvin;

    private LogBeanStub logBean;

    @Mocked
    private CommandRunner commandRunner;

    private PersonBean personBean;

    private String wallMessage;

    public OocCommandTest()
    {
    }

    /**
     * Runs the ooc command, in order to turn it on.
     */
    @Test
    public void runTurnOn()
    {
        assertThat(marvin.getOoc(), equalTo(false));
        OocCommand heehawCommand = new OocCommand("ooc .+");
        heehawCommand.setCallback(commandRunner);
        assertThat(heehawCommand.getRegExpr(), equalTo("ooc .+"));
        DisplayInterface display = heehawCommand.run("ooc on", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("Your OOC channel is now turned on.<br />\n"));
        assertThat(marvin.getOoc(), equalTo(true));
    }

    /**
     * Runs the ooc command, in order to turn it off.
     */
    @Test
    public void runTurnOff()
    {
        marvin.setOoc(true);
        assertThat(marvin.getOoc(), equalTo(true));
        OocCommand heehawCommand = new OocCommand("ooc .+");
        heehawCommand.setCallback(commandRunner);
        assertThat(heehawCommand.getRegExpr(), equalTo("ooc .+"));
        DisplayInterface display = heehawCommand.run("ooc off", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("Your OOC channel is now turned off.<br />\n"));
        assertThat(marvin.getOoc(), equalTo(false));
    }

    /**
     * Runs the ooc command, to tell everyone, but it's turned off.
     */
    @Test
    public void runMessageTurnedOff()
    {
        assertThat(marvin.getOoc(), equalTo(false));
        OocCommand heehawCommand = new OocCommand("ooc .+");
        heehawCommand.setCallback(commandRunner);
        assertThat(heehawCommand.getRegExpr(), equalTo("ooc .+"));
        DisplayInterface display = heehawCommand.run("ooc Hey! This doesn't work!", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("Sorry, you have your OOC channel turned off.<br />\n"));
        assertThat(marvin.getOoc(), equalTo(false));
    }

    /**
     * Runs the ooc command, to tell everyone.
     */
    @Test
    public void runMessageTurnedOn()
    {
        marvin.setOoc(true);
        assertThat(marvin.getOoc(), equalTo(true));
        OocCommand heehawCommand = new OocCommand("ooc .+");
        heehawCommand.setCallback(commandRunner);
        assertThat(heehawCommand.getRegExpr(), equalTo("ooc .+"));
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getPersonBean();
                result = personBean;
            }
        };
        DisplayInterface display = heehawCommand.run("ooc Hey! This works!", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("<span style=\"color: rgb(76,118,162);\">&gt;[OOC: <b>Marvin</b>] Hey! This works!</span>\n<br />\n"));
        assertThat(marvin.getOoc(), equalTo(true));
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
        logBean = new LogBeanStub();
        wallMessage = null;
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
                return Arrays.asList(karn, marvin);
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
