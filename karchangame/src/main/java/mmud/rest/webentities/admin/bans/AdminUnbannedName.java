package mmud.rest.webentities.admin.bans;

import mmud.JsonUtils;
import mmud.database.entities.game.SillyName;

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
    return JsonUtils.toJson(this);
  }

  public static AdminUnbannedName fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminUnbannedName.class);
  }


}
