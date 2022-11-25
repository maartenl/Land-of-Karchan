package mmud.services.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import mmud.rest.webentities.Message;

/**
 * The websocket defined for real time chat communication among players that are playing the game.
 * Every instance of this class, corresponds to an active user chatting.
 * We are communicating via text, the {@link Message} class is encoded and decoded to JSON format.
 */
@ServerEndpoint(value = "/chat",
  decoders = MessageDecoder.class,
  encoders = MessageEncoder.class)
public class ChatLogEndPoint
{

  private static final Logger LOGGER = Logger.getLogger(ChatLogEndPoint.class.getName());

  private Session session;
  private static final Set<ChatLogEndPoint> chatEndpoints = new CopyOnWriteArraySet<>();
  private static final Map<String, String> users = new ConcurrentHashMap<>();
  public static boolean internalpong = true;

  @OnOpen
  public void onOpen(Session session) throws IOException, EncodeException
  {
    LOGGER.finest("onOpen " + session);
    String username = session.getUserPrincipal().getName();
    // Get session and WebSocket connection
    this.session = session;
    chatEndpoints.add(this);
    users.put(session.getId(), username);

    Message message = new Message();
    message.from = username;
    message.content = username + " has entered the game.";
    message.type = "info";
    broadcast(message);
  }

  @OnMessage
  public void onMessage(Session session, Message message) throws IOException, EncodeException
  {
    // Handle new messages
    message.from = users.get(session.getId());
    if ("ping".equals(message.type))
    {
      send(message.from, new Message(message.from, null, "pong", "pong"));
      return;
    }
    if ("internalping".equals(message.type))
    {
      if (internalpong)
      {
        send(message.from, new Message(message.from, null, "internalpong", "internalpong"));
      }
      return;
    }
    broadcast(message);
  }

  @OnClose
  public void onClose(Session session) throws IOException, EncodeException
  {
    LOGGER.finest("onClose " + session);
    String username = session.getUserPrincipal().getName();
    // WebSocket connection closes
    chatEndpoints.remove(this);
    users.remove(session.getId());
    Message message = new Message();
    message.from = users.get(session.getId());
    message.content = username + " has left the game.";
    broadcast(message);
  }

  @OnError
  public void onError(Session session, Throwable throwable)
  {
    LOGGER.throwing("ChatLogEndPoint", "onError", throwable);
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
