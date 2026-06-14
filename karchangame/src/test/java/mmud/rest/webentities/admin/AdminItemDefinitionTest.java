package mmud.rest.webentities.admin;

import mmud.database.entities.items.ItemDefinition;
import mmud.database.enums.Wearing;
import mmud.database.enums.Wielding;
import org.testng.annotations.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AdminItemDefinitionTest {

  @Test
  public void testAdminItemDefinitionConstructor() {
    ItemDefinition itemDef = new ItemDefinition();
    itemDef.setId(1L);
    itemDef.setName("ring");
    itemDef.setAdjectives("nice, golden, friendship");
    itemDef.setCopper(5);
    itemDef.setWieldable("WIELD_LEFT,WIELD_RIGHT");
    itemDef.setWearable("ON_LEFT_FINGER");
    assertThat(itemDef.getWearable()).isEqualTo(Set.of(Wearing.ON_LEFT_FINGER));
    assertThat(itemDef.getWieldable()).isEqualTo(Set.of(Wielding.WIELD_LEFT, Wielding.WIELD_RIGHT));
    AdminItemDefinition item = new AdminItemDefinition(itemDef);
    assertThat(item).isNotNull();
    assertThat(item.id).isEqualTo(1L);
    assertThat(item.name).isEqualTo("ring");
    assertThat(item.adjectives).isEqualTo("nice, golden, friendship");
    assertThat(item.copper).isEqualTo(5);
    assertThat(item.wieldable).isIn("WIELD_LEFT,WIELD_RIGHT","WIELD_RIGHT,WIELD_LEFT");
    assertThat(item.wearable).isEqualTo("ON_LEFT_FINGER");
  }

}
