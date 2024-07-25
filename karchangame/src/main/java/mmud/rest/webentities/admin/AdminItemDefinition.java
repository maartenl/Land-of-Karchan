package mmud.rest.webentities.admin;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import mmud.JsonUtils;
import mmud.database.entities.items.ItemDefinition;

public class AdminItemDefinition
{
  public static final String GET_QUERY = "select json_object(\"id\", id, \"name\", name, \"adjectives\", adjectives, \"owner\", owner, \"creation\", creation) from mm_items order by id";

  public Long id;
  public String name;
  public String adjectives;
  public Integer manaincrease;
  public Integer hitincrease;
  public Integer vitalincrease;
  public Integer movementincrease;
  public String eatable;
  public String drinkable;
  public boolean lightable;
  public Boolean getable;
  public Boolean dropable;
  public Boolean visible;
  public String wieldable;
  public String description;
  public String readdescr;
  public String wearable;
  public Integer copper;
  public int weight;
  public Integer pasdefense;
  public Integer damageresistance;
  public Boolean container;
  public String owner;
  public LocalDateTime creation;
  public Integer capacity;
  public Boolean isopenable;
  public Long keyid;
  public Integer containtype;
  public String notes;
  public String image;
  public String title;
  public Integer discriminator;
  public Boolean bound;

  public AdminItemDefinition()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminItemDefinition(ItemDefinition item)
  {
    this.id = item.getId();
    this.name = item.getName();
    this.adjectives = item.getAdjectives();
    this.manaincrease = item.getManaincrease();
    this.hitincrease = item.getHitincrease();
    this.vitalincrease = item.getVitalincrease();
    this.movementincrease = item.getMovementincrease();
    this.eatable = item.getEatable();
    this.drinkable = item.getDrinkable();
    this.lightable = item.getLightable();
    this.getable = item.getGetable();
    this.dropable = item.getDropable();
    this.visible = item.getVisible();
    this.wieldable = item.getWieldable().stream()
      .map(Enum::name)
      .collect(Collectors.joining(","));
    this.description = item.getDescription();
    this.readdescr = item.getReaddescription();
    this.wearable = item.getWearable().stream()
      .map(Enum::name)
      .collect(Collectors.joining(","));
    this.copper = item.getCopper();
    this.weight = item.getWeight();
    this.pasdefense = item.getPasdefense();
    this.damageresistance = item.getDamageresistance();
    this.container = item.isContainer();
    this.owner = item.getOwner() == null ? null : item.getOwner().getName();
    this.creation = item.getCreation();
    this.capacity = item.getCapacity();
    this.isopenable = item.isOpenable();
    this.keyid = item.getKey() == null ? null : item.getKey().getId();
    this.containtype = item.getContaintype();
    this.notes = item.getNotes();
    this.image = item.getImage();
    this.title = item.getTitle();
    this.discriminator = item.getDiscriminator();
    this.bound = item.isBound();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  @Override
  public String toString()
  {
    return "AdminItem{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", adjectives='" + adjectives + '\'' +
      ", manaincrease=" + manaincrease +
      ", hitincrease=" + hitincrease +
      ", vitalincrease=" + vitalincrease +
      ", movementincrease=" + movementincrease +
      ", eatable='" + eatable + '\'' +
      ", drinkable='" + drinkable + '\'' +
      ", lightable=" + lightable +
      ", getable=" + getable +
      ", dropable=" + dropable +
      ", visible=" + visible +
      ", wieldable=" + wieldable +
      ", description='" + description + '\'' +
      ", readdescr='" + readdescr + '\'' +
      ", wearable=" + wearable +
      ", copper=" + copper +
      ", weight=" + weight +
      ", pasdefense=" + pasdefense +
      ", damageresistance=" + damageresistance +
      ", container=" + container +
      ", owner='" + owner + '\'' +
      ", creation=" + creation +
      ", capacity=" + capacity +
      ", isopenable=" + isopenable +
      ", keyid=" + keyid +
      ", containtype=" + containtype +
      ", notes='" + notes + '\'' +
      ", image='" + image + '\'' +
      ", title='" + title + '\'' +
      ", discriminator=" + discriminator +
      ", bound=" + bound +
      '}';
  }

  public static AdminItemDefinition fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminItemDefinition.class);
  }


}
