package mmud.services.websocket;

import mmud.rest.webentities.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The websocket defined for real time chat communication among players that are playing the game.
 * Every instance of this class, corresponds to an active user chatting.
 * We are communicating via text, the {@link Message} class is encoded and decoded to JSON format.
 */
@ServerEndpoint(value = "/chat/{username}",
  decoders = MessageDecoder.class,
  encoders = MessageEncoder.class)
public class ChatLogEndPoint
{
  private Session session;
  private static final Set<ChatLogEndPoint> chatEndpoints = new CopyOnWriteArraySet<>();
  private static Map<String, String> users = new HashMap<>();

  @OnOpen
  public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException
  {
    // Get session and WebSocket connection
    this.session = session;
    chatEndpoints.add(this);
    users.put(session.getId(), username);

    Message message = new Message();
    message.from = username;
    message.content = "Connected!";
    broadcast(message);
  }

  @OnMessage
  public void onMessage(Session session, Message message) throws IOException, EncodeException
  {
    // Handle new messages
    message.from = users.get(session.getId());
    broadcast(message);
  }

  @OnClose
  public void onClose(Session session) throws IOException, EncodeException
  {
    // WebSocket connection closes
    chatEndpoints.remove(this);
    Message message = new Message();
    message.from = users.get(session.getId());
    message.content = "Disconnected!";
    broadcast(message);
  }

  @OnError
  public void onError(Session session, Throwable throwable)
  {
    // Do error handling here
  }

  public static void broadcast(Message message) throws IOException, EncodeException
  {
    for (ChatLogEndPoint endpoint : chatEndpoints)
    {
      synchronized (endpoint)
      {
        endpoint.session.getBasicRemote().sendObject(message);
      }
    }
  }

  public static void send(String user, Message message) throws IOException, EncodeException
  {
    for (ChatLogEndPoint endpoint : chatEndpoints)
    {
      String s = users.get(endpoint.session.getId());
      if (s.equals(user))
      {
        synchronized (endpoint)
        {
          endpoint.session.getBasicRemote().sendObject(message);
        }
      }
    }
  }
}
