package mmud.rest.webentities;

import mmud.JsonUtils;

public class Message
{
  public Long fileLength;
  public String from;
  public String to;
  public String content;
  public String type;

  /**
   * A default chat message.
   *
   * @param to         to someone, playername
   * @param content    content of the message
   * @param fileLength length of the logfile (including this message).
   */
  public Message(String to, String content, long fileLength)
  {
    this(null, to, content, "chat", fileLength);
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static Message fromJson(String json)
  {
    return JsonUtils.fromJson(json, Message.class);
  }

  /**
   * Public default constructor for the json construction of an instance.
   */
  public Message()
  {
  }

  private Message(String from, String to, String content, String type, Long fileLength)
  {
    this.from = from;
    this.to = to;
    this.content = content;
    this.type = type;
    this.fileLength = fileLength;
  }


  /**
   * Anything but a chat message, usually broadcasted to everyone playing or used for administrative network
   * purposes (pong or internalpong).
   *
   * @param from    from someone, playername
   * @param content content of the message
   * @param type    message type, info or pong or internalpong
   */
  public Message(String from, String content, String type)
  {
    this(from, null, content, type, null);
  }
}
