package mmud.rest.webentities.admin.bans;

import mmud.database.entities.game.BannedName;
import mmud.database.entities.game.SillyName;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.LocalDateTime;

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
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminSillyName fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminSillyName.class);
  }


}
