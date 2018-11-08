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
import mmud.commands.HelpCommand;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Help;
import mmud.database.entities.game.Room;
import mmud.rest.services.HelpBean;
import mmud.testing.TestingConstants;
import mmud.testing.tests.LogBeanStub;
import mmud.testing.tests.MudTest;
import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author maartenl
 */
public class HelpCommandTest extends MudTest
{

    private Administrator karn;
    private User marvin;

    private LogBeanStub logBean;

    @Mocked
    private CommandRunner commandRunner;

    private HelpBean helpBean;

    public HelpCommandTest()
    {
    }

    /**
     * Runs the help command.
     */
    @Test
    public void runHelpDrop()
    {
        HelpCommand helpCommand = new HelpCommand("help( (\\w)+)?");
        helpCommand.setCallback(commandRunner);
        assertThat(helpCommand.getRegExpr()).isEqualTo("help( (\\w)+)?");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getHelpBean();
                result = helpBean;
            }
        };
        DisplayInterface display = helpCommand.run("help drop", marvin);
        assertThat(display).isNotNull();
        assertThat(display.getImage()).isNull();
        assertThat(display.getMainTitle()).isEqualTo("Drop");
        assertThat(display.getBody()).isEqualTo(" <dl><dt><b>NAME</b></dt><dd><b>Drop</b> - formatted output</dd><p/><dt><b>SYNOPSIS</b></dt><dd><b>drop</b> &lt;item&gt;</dd><p/><dt><b>DESCRIPTION</b></dt><dd><b>Drop</b> makes your character <b>drop</b> an item out of your inventory and onto the ground. Once it is lying on the ground, it can be picked up again by anyone coming by. An added effect is that you have to carry around less stuff. </dd><p/><dt><b>EXAMPLES</b></dt><dd>\"drop leather jerkin\"<p/>You: <tt>You drop a black, leather jerkin.</tt><br/>Anybody: <tt>Hotblack drops a black, leather jerkin.</tt><p/></dd><dt><b>SEE ALSO</b></dt><dd>get, remove, wield, unwield<p/></dd></dl>");
        String log = marvin.getLog(0);
        assertThat(log).isEqualTo("");
    }

    /**
     * Runs the help command, without a recognized command.
     */
    @Test
    public void runUnknownHelp()
    {
        HelpCommand helpCommand = new HelpCommand("help( (\\w)+)?");
        helpCommand.setCallback(commandRunner);
        assertThat(helpCommand.getRegExpr()).isEqualTo("help( (\\w)+)?");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getHelpBean();
                result = helpBean;
            }
        };
        DisplayInterface display = helpCommand.run("help awesomeness", marvin);
        assertThat(display).isNotNull();
        assertThat(display.getImage()).isNull();
        assertThat(display.getMainTitle()).isEqualTo("Sorry");
        assertThat(display.getBody()).isEqualTo(" <dl><dt><b>NAME</b></dt><dd><b>Sorry</b> - formatted output</dd><p/><dt><b>SYNOPSIS</b></dt><dd>Error messages.</dd><p/><dt><b>DESCRIPTION</b></dt><dd><H1><b>Sorry</b></H1><li><b>Sorry</b>, I don't recognise that command.<li>I am afraid I do not understand.Readouts for mistyped, absent, or broken commands.  If this is a command you are certain should work, or one that worked previously, please make use of the bugs screen to inform Admin.</dd><p/><dt><b>EXAMPLES</b></dt><dd></dd><dt><b>SEE ALSO</b></dt><dd>null<p/></dd></dl>");
        String log = marvin.getLog(0);
        assertThat(log).isEqualTo("");
    }

    /**
     * Runs the help command, giving general help.
     */
    @Test
    public void runGeneralHelp()
    {
        HelpCommand helpCommand = new HelpCommand("help( (\\w)+)?");
        helpCommand.setCallback(commandRunner);
        assertThat(helpCommand.getRegExpr()).isEqualTo("help( (\\w)+)?");
        new Expectations() // an "expectation block"
        {

            {
                commandRunner.getHelpBean();
                result = helpBean;
            }
        };
        DisplayInterface display = helpCommand.run("help", marvin);
        assertThat(display).isNotNull();
        assertThat(display.getImage()).isNull();
        assertThat(display.getMainTitle()).isEqualTo("General help");
        assertThat(display.getBody()).isEqualTo(" <dl><dt><b>NAME</b></dt><dd><b>General help</b> - formatted output</dd><p/><dt><b>SYNOPSIS</b></dt><dd></dd><p/><dt><b>DESCRIPTION</b></dt><dd>This is <b>general help</b>.</dd><p/><dt><b>EXAMPLES</b></dt><dd></dd><dt><b>SEE ALSO</b></dt><dd>null<p/></dd></dl>");
        String log = marvin.getLog(0);
        assertThat(log).isEqualTo("");
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

        helpBean = new HelpBean()
        {

            private final Help generalHelp;
            private final Help sorry;
            private final Help drop;


            {
                generalHelp = new Help();
                generalHelp.setCommand("general help");
                generalHelp.setContents("This is general help.");
                generalHelp.setSynopsis(null);
                sorry = new Help();
                sorry.setCommand("sorry");
                sorry.setContents("<H1>Sorry</H1><li>Sorry, I don't recognise that command.<li>I am afraid I do not understand.Readouts for mistyped, absent, or broken commands.  If this is a command you are certain should work, or one that worked previously, please make use of the bugs screen to inform Admin.");
                sorry.setSynopsis("Error messages.");
                drop = new Help();
                drop.setCommand("drop");
                drop.setContents("Drop makes your character drop an item out of your inventory and onto the ground. Once it is lying on the ground, it can be picked up again by anyone coming by. An added effect is that you have to carry around less stuff. ");
                drop.setSynopsis("drop <item>");
                drop.setSeealso("get, remove, wield, unwield");
                drop.setExample1("drop leather jerkin");
                drop.setExample1a("You drop a black, leather jerkin.");
                drop.setExample1b("Hotblack drops a black, leather jerkin.");
            }

            @Override
            public Help getHelp(String name)
            {
                if (name == null)
                {
                    return generalHelp;

                }
                if (name.equalsIgnoreCase("drop"))
                {
                    return drop;
                }
                return sorry;
            }

        };

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
