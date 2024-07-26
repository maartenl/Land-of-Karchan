package mmud.rest.webentities.admin;

import jakarta.json.bind.annotation.JsonbCreator;
import mmud.JsonUtils;
import mmud.database.entities.items.Item;

import java.time.LocalDateTime;

public record AdminItem(Integer id,
                        Long itemid,
                        Integer containerid,
                        Long containerdefid,
                        String belongsto,
                        Long room,
                        Integer discriminator,
                        String shopkeeper,
                        String owner,
                        LocalDateTime creation)
{
  public static final String GET_QUERY = """
      select json_object("id", mm_itemtable.id, "itemid", mm_itemtable.itemid, "containerid", mm_itemtable.containerid, "containerdefid", container.itemid, "belongsto", mm_itemtable.belongsto, "room",
                         mm_itemtable.room, "discriminator", mm_itemtable.discriminator, "shopkeeper", mm_itemtable.shopkeeper, "owner", mm_itemtable.owner, "creation", mm_itemtable.creation)
      from mm_itemtable
               left join mm_itemtable container
                         on (container.id = mm_itemtable.containerid)
      where mm_itemtable.itemid = ?
      order by mm_itemtable.id
      """;

  @JsonbCreator
  public AdminItem
  {
    // empty constructor, because I need to put the annotation @JsonbCreator somewhere.
  }

  public AdminItem(Item item)
  {
    this(item.getId(), item.getItemDefinition().getId(),
        item.getContainer() == null ? null : item.getContainer().getId(),
        item.getContainer() == null ? null : item.getContainer().getItemDefinition().getId(),
        item.getBelongsTo() == null ? null : item.getBelongsTo().getName(),
        item.getRoom() == null ? null : item.getRoom().getId(),
        item.getDiscriminator(),
        // TODO:
        // this.shopkeeper = item.getShoplee[er]
        null,
        item.getOwner() == null ? null : item.getOwner().getName(),
        item.getCreation());
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminItem fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminItem.class);
  }

}
