package mmud.rest.webentities;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class Message
{
  public String from;
  public String to;
  public String content;

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static Message fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, Message.class);
  }
}
