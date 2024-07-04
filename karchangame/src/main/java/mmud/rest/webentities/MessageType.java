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
   * this message. This enumtype will never be generated/used by the server, but will only be sent by the client.
   */
  INTERNALPING,
  /**
   * The internal ping-pong for checking of the connection. This enumtype will never be sent by the client,
   * but only by the server as an answer to a {@link #INTERNALPING}.
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
