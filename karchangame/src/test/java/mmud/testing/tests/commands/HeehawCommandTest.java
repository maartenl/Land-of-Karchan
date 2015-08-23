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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import mmud.Constants;
import mmud.commands.CommandRunner;
import mmud.commands.HeehawCommand;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.rest.services.PersonBean;
import mmud.testing.TestingConstants;
import mmud.testing.tests.LogBeanStub;
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
public class HeehawCommandTest extends MudTest
{

    private Administrator karn;
    private User marvin;

    private LogBeanStub logBean;

    @Mocked
    private CommandRunner commandRunner;

    private PersonBean personBean;

    public HeehawCommandTest()
    {
    }

    @Test
    public void runHeehawWithoutBeingFrogged()
    {
        HeehawCommand heehawCommand = new HeehawCommand("heehaw");
        heehawCommand.setCallback(commandRunner);
        assertThat(heehawCommand.getRegExpr(), equalTo("heehaw"));
        DisplayInterface display = heehawCommand.run("heehaw", marvin);
        assertThat(display, nullValue());
        String log = marvin.getLog(0);
        assertThat(log, equalTo(""));
    }

    @Test
    public void runHeehaw()
    {
        marvin.setJackassing(5);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.roll(Calendar.SECOND, -17);
        setField(User.class, "lastcommand", marvin, calendar.getTime());
        HeehawCommand heehawCommand = new HeehawCommand("heehaw");
        heehawCommand.setCallback(commandRunner);
        assertThat(heehawCommand.getRegExpr(), equalTo("heehaw"));
        DisplayInterface display = heehawCommand.run("heehaw", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("A jackass called Marvin says &quot;Heeehaw!&quot;.<br />\nYou feel the need to say 'Heehaw' just 4 times.<br />\n"));
        assertThat(logBean.getLog(), equalTo(""));
    }

    @Test
    public void runHeehawFast()
    {
        marvin.setJackassing(5);
        marvin.setNow();
        HeehawCommand heehawCommand = new HeehawCommand("heehaw");
        heehawCommand.setCallback(commandRunner);
        assertThat(heehawCommand.getRegExpr(), equalTo("heehaw"));
        DisplayInterface display = heehawCommand.run("heehaw", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("You cannot say 'Heehaw' that fast! You will get tongue tied!<br />\n"));
        assertThat(logBean.getLog(), equalTo(""));
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
