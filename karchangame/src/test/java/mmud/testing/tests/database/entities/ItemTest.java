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
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
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
      Item item = new NormalItem();
      item.setItemDefinition(itemDefinition);
      assertThat(item.isWearable(Wearing.ON_HEAD)).isFalse(); // 1
      assertThat(item.isWearable(Wearing.ON_NECK)).isFalse(); // 2
      assertThat(item.isWearable(Wearing.ON_TORSO)).isFalse(); // 4
      assertThat(item.isWearable(Wearing.ON_ARMS)).isFalse(); // 8
      assertThat(item.isWearable(Wearing.ON_LEFT_WRIST)).isFalse(); // 16
      assertThat(item.isWearable(Wearing.ON_RIGHT_WRIST)).isFalse(); // 32
      assertThat(item.isWearable(Wearing.ON_LEFT_FINGER)).isFalse(); // 64
      assertThat(item.isWearable(Wearing.ON_RIGHT_FINGER)).isFalse(); // 128
      assertThat(item.isWearable(Wearing.ON_FEET)).isFalse(); // 256
      assertThat(item.isWearable(Wearing.ON_HANDS)).isFalse(); // 512
      assertThat(item.isWearable(Wearing.FLOATING_NEARBY)).isFalse(); // 1024
      assertThat(item.isWearable(Wearing.ON_WAIST)).isFalse();  // 2048
      assertThat(item.isWearable(Wearing.ON_LEGS)).isFalse(); // 4096
      assertThat(item.isWearable(Wearing.ON_EYES)).isFalse();  // 8192
      assertThat(item.isWearable(Wearing.ON_EARS)).isFalse();  // 16384
      assertThat(item.isWearable(Wearing.ABOUT_BODY)).isFalse(); // 32768
      assertThat(item.isWieldable(Wielding.WIELD_LEFT)).isFalse(); // 65536
      assertThat(item.isWieldable(Wielding.WIELD_RIGHT)).isFalse(); // 131072
      assertThat(item.isWieldable(Wielding.WIELD_BOTH)).isFalse(); // 262144
    }

    @Test
    public void checkWieldable()
    {
      ItemDefinition itemDefinition = new ItemDefinition();
      itemDefinition.setId(3234L);
      itemDefinition.setWearable("ON_LEFT_FINGER,ON_RIGHT_FINGER");
      itemDefinition.setWieldable("WIELD_LEFT,WIELD_RIGHT");
      Item item = new NormalItem();
      item.setItemDefinition(itemDefinition);
      assertThat(item.isWearable(Wearing.ON_HEAD)).isFalse(); // 1
      assertThat(item.isWearable(Wearing.ON_NECK)).isFalse(); // 2
      assertThat(item.isWearable(Wearing.ON_TORSO)).isFalse(); // 4
      assertThat(item.isWearable(Wearing.ON_ARMS)).isFalse(); // 8
      assertThat(item.isWearable(Wearing.ON_LEFT_WRIST)).isFalse(); // 16
      assertThat(item.isWearable(Wearing.ON_RIGHT_WRIST)).isFalse(); // 32
      assertThat(item.isWearable(Wearing.ON_LEFT_FINGER)).isTrue(); // 64
      assertThat(item.isWearable(Wearing.ON_RIGHT_FINGER)).isTrue(); // 128
      assertThat(item.isWearable(Wearing.ON_FEET)).isFalse(); // 256
      assertThat(item.isWearable(Wearing.ON_HANDS)).isFalse(); // 512
      assertThat(item.isWearable(Wearing.FLOATING_NEARBY)).isFalse(); // 1024
      assertThat(item.isWearable(Wearing.ON_WAIST)).isFalse();  // 2048
      assertThat(item.isWearable(Wearing.ON_LEGS)).isFalse(); // 4096
      assertThat(item.isWearable(Wearing.ON_EYES)).isFalse();  // 8192
      assertThat(item.isWearable(Wearing.ON_EARS)).isFalse();  // 16384
      assertThat(item.isWearable(Wearing.ABOUT_BODY)).isFalse(); // 32768
      assertThat(item.isWieldable(Wielding.WIELD_LEFT)).isTrue(); // 1
      assertThat(item.isWieldable(Wielding.WIELD_RIGHT)).isTrue(); // 2
      assertThat(item.isWieldable(Wielding.WIELD_BOTH)).isFalse(); // 4
    }
}
