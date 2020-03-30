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

import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.database.enums.Wearing;
import mmud.database.enums.Wielding;
import mmud.testing.tests.MudTest;
import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void checkWearable()
    {
        ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinition.setId(905L);
        setField(ItemDefinition.class, "wearable", itemDefinition, 458752);
        setField(ItemDefinition.class, "wieldable", itemDefinition, 0);
        Item item = new NormalItem();
        item.setItemDefinition(itemDefinition);
        assertThat(item.isWearable(Wearing.ON_HEAD)).isEqualTo(false); // 1
        assertThat(item.isWearable(Wearing.ON_NECK)).isEqualTo(false); // 2
        assertThat(item.isWearable(Wearing.ON_TORSO)).isEqualTo(false); // 4
        assertThat(item.isWearable(Wearing.ON_ARMS)).isEqualTo(false); // 8
        assertThat(item.isWearable(Wearing.ON_LEFT_WRIST)).isEqualTo(false); // 16
        assertThat(item.isWearable(Wearing.ON_RIGHT_WRIST)).isEqualTo(false); // 32
        assertThat(item.isWearable(Wearing.ON_LEFT_FINGER)).isEqualTo(false); // 64
        assertThat(item.isWearable(Wearing.ON_RIGHT_FINGER)).isEqualTo(false); // 128
        assertThat(item.isWearable(Wearing.ON_FEET)).isEqualTo(false); // 256
        assertThat(item.isWearable(Wearing.ON_HANDS)).isEqualTo(false); // 512
        assertThat(item.isWearable(Wearing.FLOATING_NEARBY)).isEqualTo(false); // 1024
        assertThat(item.isWearable(Wearing.ON_WAIST)).isEqualTo(false);  // 2048
        assertThat(item.isWearable(Wearing.ON_LEGS)).isEqualTo(false); // 4096
        assertThat(item.isWearable(Wearing.ON_EYES)).isEqualTo(false);  // 8192
        assertThat(item.isWearable(Wearing.ON_EARS)).isEqualTo(false);  // 16384
        assertThat(item.isWearable(Wearing.ABOUT_BODY)).isEqualTo(false); // 32768
        assertThat(item.isWieldable(Wielding.WIELD_LEFT)).isEqualTo(false); // 65536
        assertThat(item.isWieldable(Wielding.WIELD_RIGHT)).isEqualTo(false); // 131072
        assertThat(item.isWieldable(Wielding.WIELD_BOTH)).isEqualTo(false); // 262144
    }

    @Test
    public void checkWieldable()
    {
        ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinition.setId(3234L);
        setField(ItemDefinition.class, "wearable", itemDefinition, 192);// 64 + 128
        setField(ItemDefinition.class, "wieldable", itemDefinition, 3);
        Item item = new NormalItem();
        item.setItemDefinition(itemDefinition);
        assertThat(item.isWearable(Wearing.ON_HEAD)).isEqualTo(false); // 1
        assertThat(item.isWearable(Wearing.ON_NECK)).isEqualTo(false); // 2
        assertThat(item.isWearable(Wearing.ON_TORSO)).isEqualTo(false); // 4
        assertThat(item.isWearable(Wearing.ON_ARMS)).isEqualTo(false); // 8
        assertThat(item.isWearable(Wearing.ON_LEFT_WRIST)).isEqualTo(false); // 16
        assertThat(item.isWearable(Wearing.ON_RIGHT_WRIST)).isEqualTo(false); // 32
        assertThat(item.isWearable(Wearing.ON_LEFT_FINGER)).isEqualTo(true); // 64
        assertThat(item.isWearable(Wearing.ON_RIGHT_FINGER)).isEqualTo(true); // 128
        assertThat(item.isWearable(Wearing.ON_FEET)).isEqualTo(false); // 256
        assertThat(item.isWearable(Wearing.ON_HANDS)).isEqualTo(false); // 512
        assertThat(item.isWearable(Wearing.FLOATING_NEARBY)).isEqualTo(false); // 1024
        assertThat(item.isWearable(Wearing.ON_WAIST)).isEqualTo(false);  // 2048
        assertThat(item.isWearable(Wearing.ON_LEGS)).isEqualTo(false); // 4096
        assertThat(item.isWearable(Wearing.ON_EYES)).isEqualTo(false);  // 8192
        assertThat(item.isWearable(Wearing.ON_EARS)).isEqualTo(false);  // 16384
        assertThat(item.isWearable(Wearing.ABOUT_BODY)).isEqualTo(false); // 32768
        assertThat(item.isWieldable(Wielding.WIELD_LEFT)).isEqualTo(true); // 1
        assertThat(item.isWieldable(Wielding.WIELD_RIGHT)).isEqualTo(true); // 2
        assertThat(item.isWieldable(Wielding.WIELD_BOTH)).isEqualTo(false); // 4
    }
}
