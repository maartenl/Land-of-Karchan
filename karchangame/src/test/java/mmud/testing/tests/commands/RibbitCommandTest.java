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
import mmud.commands.RibbitCommand;
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
public class RibbitCommandTest extends MudTest
{

    private Administrator karn;
    private User marvin;

    private LogBeanStub logBean;

    @Mocked
    private CommandRunner commandRunner;

    private PersonBean personBean;

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
        assertThat(ribbitCommand.getRegExpr(), equalTo("ribbit"));
        DisplayInterface display = ribbitCommand.run("ribbit", marvin);
        assertThat(display, nullValue());
        String log = marvin.getLog(0);
        assertThat(log, equalTo(""));
    }

    /**
     * Runs the Ribbit command, bringing the punishment down from 5 to 4.
     */
    @Test
    public void runRibbit()
    {
        marvin.setFrogging(5);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.SECOND, -17);
        setField(User.class, "lastcommand", marvin, calendar.getTime());
        RibbitCommand ribbitCommand = new RibbitCommand("ribbit");
        ribbitCommand.setCallback(commandRunner);
        assertThat(ribbitCommand.getRegExpr(), equalTo("ribbit"));
        DisplayInterface display = ribbitCommand.run("ribbit", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("A frog called Marvin says &quot;Rrribbit!&quot;.<br />\nYou feel the need to say 'Ribbit' just 4 times.<br />\n"));
        assertThat(logBean.getLog(), equalTo(""));
    }

    /**
     * Warning that you are issuing the ribbit command too fast. No points are
     * deducted, and you must wait for more time to pass. (at least 10 seconds)
     */
    @Test
    public void runRibbitFast()
    {
        marvin.setFrogging(5);
        marvin.setNow();
        RibbitCommand ribbitCommand = new RibbitCommand("ribbit");
        ribbitCommand.setCallback(commandRunner);
        assertThat(ribbitCommand.getRegExpr(), equalTo("ribbit"));
        DisplayInterface display = ribbitCommand.run("ribbit", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("You cannot say 'Ribbit' that fast! You will get tongue tied!<br />\n"));
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