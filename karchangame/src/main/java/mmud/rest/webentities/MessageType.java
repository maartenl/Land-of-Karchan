package mmud.rest.webentities;

public enum MessageType
{
  /**
   * State changes of the game, for example if somebody logs on.
   */
  INFO,
  /**
   * User generated ping response. For fun.
   */
  PONG,
  /**
   * The internal ping-pong for checking of the connection. We're expecting a {@link #INTERNALPONG}, if we sent
   * this message.
   */
  INTERNALPING,
  /**
   * The internal ping-pong for checking of the connection.
   */
  INTERNALPONG,
  /**
   * General communication, the default.
   */
  CHAT,
  /**
   * The idea here is that this indicates that this text should be displayed above the characters head that says it
   * in a speech bubble in the graphical client.
   */
  CHATBUBBLE
}
