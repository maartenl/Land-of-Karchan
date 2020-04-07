package mmud.rest.webentities.admin;

import mmud.database.entities.game.Area;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.LocalDateTime;

public class AdminArea
{
  public static final String GET_QUERY = "select json_object(\"area\", area, \"owner\", owner, \"creation\", creation) from mm_area order by area";

  public String area;
  public String description;
  public String shortdesc;
  public LocalDateTime creation;
  public String owner;

  public AdminArea()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminArea(Area item)
  {
    this.area = item.getArea();
    this.description = item.getDescription();
    this.shortdesc = item.getShortdescription();
    this.creation = item.getCreation();
    this.owner = item.getOwner() == null ? null : item.getOwner().getName();
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminArea fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminArea.class);
  }


}
