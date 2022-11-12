package mmud.services.websocket;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;
import mmud.rest.webentities.Message;

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
