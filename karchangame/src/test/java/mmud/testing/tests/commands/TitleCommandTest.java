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
import mmud.commands.TitleCommand;
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
public class TitleCommandTest extends MudTest
{

    private Administrator karn;
    private User marvin;

    private LogBeanStub logBean;

    @Mocked
    private CommandRunner commandRunner;

    private PersonBean personBean;

    public TitleCommandTest()
    {
    }

    @Test
    public void setTitle()
    {
        assertThat(marvin.getTitle(), equalTo("The Paranoid Android"));
        TitleCommand titleCommand = new TitleCommand("(title|title ?.+)");
        titleCommand.setCallback(commandRunner);
        assertThat(titleCommand.getRegExpr(), equalTo("(title|title ?.+)"));
        DisplayInterface display = titleCommand.run("title Ruler of the Land", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("Changed your title to : 'Ruler of the Land'.<br />\n"));
        assertThat(marvin.getTitle(), equalTo("Ruler of the Land"));
    }

    @Test
    public void removeTitle()
    {
        assertThat(marvin.getTitle(), equalTo("The Paranoid Android"));
        TitleCommand titleCommand = new TitleCommand("(title|title ?.+)");
        titleCommand.setCallback(commandRunner);
        assertThat(titleCommand.getRegExpr(), equalTo("(title|title ?.+)"));
        DisplayInterface display = titleCommand.run("title remove", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("You have removed your current title.<br />\n"));
        assertThat(marvin.getTitle(), nullValue());
    }

    @Test
    public void getTitle()
    {
        assertThat(marvin.getTitle(), equalTo("The Paranoid Android"));
        TitleCommand titleCommand = new TitleCommand("(title|title ?.+)");
        titleCommand.setCallback(commandRunner);
        assertThat(titleCommand.getRegExpr(), equalTo("(title|title ?.+)"));
        DisplayInterface display = titleCommand.run("title", marvin);
        assertThat(display, not(nullValue()));
        String log = marvin.getLog(0);
        assertThat(log, equalTo("Your current title is 'The Paranoid Android'.<br />\n"));
        assertThat(marvin.getTitle(), equalTo("The Paranoid Android"));
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
