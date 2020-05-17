package mmud.services.websocket;

import mmud.rest.webentities.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message>
{
  @Override
  public String encode(Message message) throws EncodeException
  {
    if (message == null)
    {
      return null;
    }
    return message.toJson();
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
