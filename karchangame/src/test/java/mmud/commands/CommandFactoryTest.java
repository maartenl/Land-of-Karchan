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
package mmud.commands;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import mmud.Constants;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Room;
import mmud.rest.services.PersonBean;
import mmud.testing.TestingConstants;
import mmud.testing.tests.LogBeanStub;
import mmud.testing.tests.MudTest;
import mockit.Mocked;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.hasSize;

/**
 *
 * @author maartenl
 */
public class CommandFactoryTest extends MudTest
{

    private Administrator karn;
    private User marvin;

    private LogBeanStub logBean;

    @Mocked
    private CommandRunner commandRunner;

    private PersonBean personBean;

    public CommandFactoryTest()
    {
    }

    @BeforeMethod
    public void setup()
    {
        logBean = new LogBeanStub();
    }

    @Test
    public void checkForBogusCommand()
    {
        List<NormalCommand> commands = CommandFactory.getCommand("woahnelly");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0).getClass().getSimpleName(), equalTo("BogusCommand"));

    }
    
    @Test
    public void checkForTitleCommandSimple()
    {
        List<NormalCommand> commands = CommandFactory.getCommand("title");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0).getClass().getSimpleName(), equalTo("TitleCommand"));       
    }

    @Test
    public void checkForTitleCommandRemove()
    {
        List<NormalCommand> commands = CommandFactory.getCommand("title remove");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0).getClass().getSimpleName(), equalTo("TitleCommand"));
    }
    @Test
    public void checkForTitleCommandSetter()
    {
        List<NormalCommand> commands = CommandFactory.getCommand("title Ruler of the Land");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0).getClass().getSimpleName(), equalTo("TitleCommand"));
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