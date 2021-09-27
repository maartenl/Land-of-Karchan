package mmud.rest.webentities;

import mmud.JsonUtils;

public class Message
{
  public String from;
  public String to;
  public String content;
  public String type;

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static Message fromJson(String json)
  {
    return JsonUtils.fromJson(json, Message.class);
  }

  public Message()
  {
  }

  public Message(String from, String to, String content, String type)
  {
    this.from = from;
    this.to = to;
    this.content = content;
    this.type = type;
  }
}
