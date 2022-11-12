package mmud.services.websocket;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import mmud.rest.webentities.Message;

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
