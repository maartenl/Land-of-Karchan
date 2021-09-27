package mmud.rest.webentities.admin.bans;

import java.time.LocalDateTime;

import mmud.JsonUtils;
import mmud.database.entities.game.BanTable;

public class AdminBannedIP
{
  public static final String GET_QUERY = "select json_object(\"address\", address, \"days\", days, \"IP\", IP, \"name\", name, \"deputy\", deputy, \"date\", date, \"reason\", reason) from mm_bantable order by address";

  public String address;
  public Integer days;
  public String IP;
  public String name;
  public String deputy;
  public LocalDateTime date;
  public String reason;

  public AdminBannedIP()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminBannedIP(BanTable item)
  {
    this.address = item.getAddress();
    this.days = item.getDays();
    this.IP = item.getIp();
    this.name = item.getName();
    this.deputy = item.getDeputy();
    this.date = item.getDate();
    this.reason = item.getReason();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminBannedIP fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminBannedIP.class);
  }


}
