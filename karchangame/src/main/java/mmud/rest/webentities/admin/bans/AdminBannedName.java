package mmud.rest.webentities.admin.bans;

import java.time.LocalDateTime;

import mmud.JsonUtils;
import mmud.database.entities.game.BannedName;

public class AdminBannedName
{
  public static final String GET_QUERY = "select json_object(\"name\", name, \"days\", days, \"deputy\", deputy, \"creation\", creation, \"reason\", reason) from mm_bannednamestable order by name";

  public String name;
  public String deputy;
  public LocalDateTime creation;
  public Integer days;
  public String reason;

  public AdminBannedName()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminBannedName(BannedName item)
  {
    this.days = item.getDays();
    this.name = item.getName();
    this.deputy = item.getDeputy();
    this.reason = item.getReason();
    this.creation = item.getCreation();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminBannedName fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminBannedName.class);
  }


}
