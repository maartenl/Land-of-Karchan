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
import mmud.commands.CommandRunner;
import mmud.commands.OwnerCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.rest.services.ItemBean;
import mmud.rest.services.LogBean;
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
public class OwnerCommandTest extends MudTest
{

    private User karn;
    private Room room1;
    private NormalItem ring;
    private ItemDefinition itemDef;
    private ItemBean itemBean;

    @Mocked
    private LogBean logBean;

    @Mocked
    private CommandRunner commandRunner;

    public OwnerCommandTest()
    {
    }

    @Test
    public void showEmptyOwner()
    {
        OwnerCommand ownerCommand = new OwnerCommand("owner( (\\w)+)?");
        ownerCommand.setCallback(commandRunner);
        assertThat(ownerCommand.getRegExpr(), equalTo("owner( (\\w)+)?"));
        DisplayInterface display = ownerCommand.run("owner", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("You are not owned.<br />"));
    }

    @Test
    public void showKarnOwner()
    {
        Admin owner = new Admin();
        owner.setName("Midevia");
        karn.setOwner(owner);
        OwnerCommand ownerCommand = new OwnerCommand("owner( (\\w)+)?");
        ownerCommand.setCallback(commandRunner);
        assertThat(ownerCommand.getRegExpr(), equalTo("owner( (\\w)+)?"));
        DisplayInterface display = ownerCommand.run("owner", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("Your current owner is Midevia.<br />"));
    }

    @Test
    public void removeKarnOwner()
    {
        Admin owner = new Admin();
        owner.setName("Midevia");
        karn.setOwner(owner);
        OwnerCommand ownerCommand = new OwnerCommand("owner( (\\w)+)?");
        ownerCommand.setCallback(commandRunner);
        assertThat(ownerCommand.getRegExpr(), equalTo("owner( (\\w)+)?"));
        DisplayInterface display = ownerCommand.run("owner remove", karn);
        assertThat(display, not(nullValue()));
        assertThat(display.getBody(), equalTo("You are in a small room."));
        String log = karn.getLog(0);
        assertThat(log, equalTo("Owner removed.<br />"));
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
        room1 = new Room();
        room1.setId(1);
        room1.setContents("You are in a small room.");

        karn = new User();
        karn.setName("Karn");
        karn.setRoom(room1);

        File file = new File(Constants.getMudfilepath() + File.separator + "Karn.log");
        PrintWriter writer = new PrintWriter(file);
        writer.close();
    }

    @AfterMethod
    public void tearDownMethod() throws Exception
    {
    }
}
