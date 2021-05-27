package mmud.rest.webentities.admin.bans;

import mmud.JsonUtils;
import mmud.database.entities.game.SillyName;

public class AdminSillyName
{
  public static final String GET_QUERY = "select json_object(\"name\", name) from mm_sillynamestable order by name";

  public String name;

  public AdminSillyName()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminSillyName(SillyName item)
  {
    this.name = item.getName();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminSillyName fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminSillyName.class);
  }


}
