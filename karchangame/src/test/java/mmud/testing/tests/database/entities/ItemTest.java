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
package mmud.testing.tests.database.entities;

import mmud.testing.tests.MudTest;
import java.io.IOException;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.database.enums.Wearing;
import mmud.database.enums.Wielding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class ItemTest extends MudTest
{

    public ItemTest()
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
    public void checkWearable() throws IOException
    {
        ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinition.setId(905);
        setField(ItemDefinition.class, "wearable", itemDefinition, 458752);
        setField(ItemDefinition.class, "wieldable", itemDefinition, 0);
        Item item = new NormalItem();
        item.setItemDefinition(itemDefinition);
        assertThat(item.isWearable(Wearing.ON_HEAD), equalTo(false)); // 1
        assertThat(item.isWearable(Wearing.ON_NECK), equalTo(false)); // 2
        assertThat(item.isWearable(Wearing.ON_TORSO), equalTo(false)); // 4
        assertThat(item.isWearable(Wearing.ON_ARMS), equalTo(false)); // 8
        assertThat(item.isWearable(Wearing.ON_LEFT_WRIST), equalTo(false)); // 16
        assertThat(item.isWearable(Wearing.ON_RIGHT_WRIST), equalTo(false)); // 32
        assertThat(item.isWearable(Wearing.ON_LEFT_FINGER), equalTo(false)); // 64
        assertThat(item.isWearable(Wearing.ON_RIGHT_FINGER), equalTo(false)); // 128
        assertThat(item.isWearable(Wearing.ON_FEET), equalTo(false)); // 256
        assertThat(item.isWearable(Wearing.ON_HANDS), equalTo(false)); // 512
        assertThat(item.isWearable(Wearing.FLOATING_NEARBY), equalTo(false)); // 1024
        assertThat(item.isWearable(Wearing.ON_WAIST), equalTo(false));  // 2048
        assertThat(item.isWearable(Wearing.ON_LEGS), equalTo(false)); // 4096
        assertThat(item.isWearable(Wearing.ON_EYES), equalTo(false));  // 8192
        assertThat(item.isWearable(Wearing.ON_EARS), equalTo(false));  // 16384
        assertThat(item.isWearable(Wearing.ABOUT_BODY), equalTo(false)); // 32768
        assertThat(item.isWieldable(Wielding.WIELD_LEFT), equalTo(false)); // 65536
        assertThat(item.isWieldable(Wielding.WIELD_RIGHT), equalTo(false)); // 131072
        assertThat(item.isWieldable(Wielding.WIELD_BOTH), equalTo(false)); // 262144
    }

    @Test
    public void checkWieldable() throws IOException
    {
        ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinition.setId(3234);
        setField(ItemDefinition.class, "wearable", itemDefinition, 192);// 64 + 128
        setField(ItemDefinition.class, "wieldable", itemDefinition, 3);
        Item item = new NormalItem();
        item.setItemDefinition(itemDefinition);
        assertThat(item.isWearable(Wearing.ON_HEAD), equalTo(false)); // 1
        assertThat(item.isWearable(Wearing.ON_NECK), equalTo(false)); // 2
        assertThat(item.isWearable(Wearing.ON_TORSO), equalTo(false)); // 4
        assertThat(item.isWearable(Wearing.ON_ARMS), equalTo(false)); // 8
        assertThat(item.isWearable(Wearing.ON_LEFT_WRIST), equalTo(false)); // 16
        assertThat(item.isWearable(Wearing.ON_RIGHT_WRIST), equalTo(false)); // 32
        assertThat(item.isWearable(Wearing.ON_LEFT_FINGER), equalTo(true)); // 64
        assertThat(item.isWearable(Wearing.ON_RIGHT_FINGER), equalTo(true)); // 128
        assertThat(item.isWearable(Wearing.ON_FEET), equalTo(false)); // 256
        assertThat(item.isWearable(Wearing.ON_HANDS), equalTo(false)); // 512
        assertThat(item.isWearable(Wearing.FLOATING_NEARBY), equalTo(false)); // 1024
        assertThat(item.isWearable(Wearing.ON_WAIST), equalTo(false));  // 2048
        assertThat(item.isWearable(Wearing.ON_LEGS), equalTo(false)); // 4096
        assertThat(item.isWearable(Wearing.ON_EYES), equalTo(false));  // 8192
        assertThat(item.isWearable(Wearing.ON_EARS), equalTo(false));  // 16384
        assertThat(item.isWearable(Wearing.ABOUT_BODY), equalTo(false)); // 32768
        assertThat(item.isWieldable(Wielding.WIELD_LEFT), equalTo(true)); // 1
        assertThat(item.isWieldable(Wielding.WIELD_RIGHT), equalTo(true)); // 2
        assertThat(item.isWieldable(Wielding.WIELD_BOTH), equalTo(false)); // 4
    }
}
