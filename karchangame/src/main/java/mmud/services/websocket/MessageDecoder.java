package mmud.services.websocket;

import mmud.rest.webentities.Message;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message>
{
  @Override
  public Message decode(String s) throws DecodeException
  {
    return Message.fromJson(s);
  }

  @Override
  public boolean willDecode(String s)
  {
    return (s != null);
  }

  @Override
  public void init(EndpointConfig endpointConfig)
  {
    // Custom initialization logic
  }

  @Override
  public void destroy()
  {
    // Close resources
  }
}
