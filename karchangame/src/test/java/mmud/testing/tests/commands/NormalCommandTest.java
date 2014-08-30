/*
 *  Copyright (C) 2012 maartenl
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

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.web.CharacterInfo;
import mmud.exceptions.MudException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author maartenl
 */
public class NormalCommandTest
{

    public NormalCommandTest()
    {
    }

    @BeforeClass
    public void setUpClass()
    {
    }

    @AfterClass
    public void tearDownClass()
    {
    }

    @BeforeMethod
    public void setUp()
    {
    }

    @AfterMethod
    public void tearDown()
    {
    }

    @Test
    public void checkParsedCommandNoLimit() throws MudException
    {
        final NormalCommand normalCommand = new NormalCommand("regexp")
        {
            @Override
            public DisplayInterface run(String command, User aUser) throws MudException
            {
                String[] parsed = parseCommand(command);
                assertEquals(parsed.length, 1);
                assertEquals(parsed[0],"ask");
                return null;
            }
        };

        normalCommand.run("ask", null);
    }


    @Test
    public void checkParsedCommandNoLimit2() throws MudException
    {
        final NormalCommand normalCommand = new NormalCommand("regexp")
        {
            @Override
            public DisplayInterface run(String command, User aUser) throws MudException
            {
                String[] parsed = parseCommand(command);
                assertEquals(parsed.length, 6);
                assertEquals(parsed[0],"ask");
                assertEquals(parsed[1],"to");
                assertEquals(parsed[2],"Marvin");
                assertEquals(parsed[3],"Yo!");
                assertEquals(parsed[4],"What's");
                assertEquals(parsed[5],"up?");
                return null;
            }
        };

        normalCommand.run("ask to Marvin Yo! What's up?", null);
    }


    @Test
    public void checkParsedCommandLimit() throws MudException
    {
        final NormalCommand normalCommand = new NormalCommand("regexp")
        {
            @Override
            public DisplayInterface run(String command, User aUser) throws MudException
            {
                String[] parsed = parseCommand(command,4 );
                assertEquals(parsed.length, 1);
                assertEquals(parsed[0],"ask");
                return null;
            }
        };

        normalCommand.run("ask", null);
    }


    @Test
    public void checkParsedCommandLimit2() throws MudException
    {
        final NormalCommand normalCommand = new NormalCommand("regexp")
        {
            @Override
            public DisplayInterface run(String command, User aUser) throws MudException
            {
                String[] parsed = parseCommand(command, 4);
                assertEquals(parsed.length, 3);
                assertEquals(parsed[0],"ask");
                assertEquals(parsed[1],"to");
                assertEquals(parsed[2],"Marvin");
                return null;
            }
        };

        normalCommand.run("ask to Marvin", null);
    }

    @Test
    public void checkParsedCommandLimit4() throws MudException
    {
        final NormalCommand normalCommand = new NormalCommand("regexp")
        {
            @Override
            public DisplayInterface run(String command, User aUser) throws MudException
            {
                String[] parsed = parseCommand(command, 4);
                assertEquals(parsed.length, 4);
                assertEquals(parsed[0],"ask");
                assertEquals(parsed[1],"to");
                assertEquals(parsed[2],"Marvin");
                assertEquals(parsed[3],"Yo! What's up?");
                return null;
            }
        };

        normalCommand.run("ask to Marvin Yo! What's up?", null);
    }
}
