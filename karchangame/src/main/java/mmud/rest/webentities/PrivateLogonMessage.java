package mmud.rest.webentities;

import mmud.JsonUtils;

public record PrivateLogonMessage(String guildmessage, String guildAlarmDescription, String colour, PrivateBoard newsBoard)
{
  public String toJson()
  {
    return JsonUtils.toJson(this);
  }
}
