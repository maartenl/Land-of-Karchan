package mmud.rest.webentities.admin.bans;

import mmud.database.entities.game.SillyName;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class AdminUnbannedName
{
  public static final String GET_QUERY = "select json_object(\"name\", name) from mm_unbantable order by name";

  public String name;

  public AdminUnbannedName()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminUnbannedName(SillyName item)
  {
    this.name = item.getName();
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminUnbannedName fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminUnbannedName.class);
  }


}
