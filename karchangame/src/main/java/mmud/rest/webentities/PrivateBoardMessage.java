package mmud.rest.webentities;

import mmud.database.entities.game.BoardMessage;

import java.time.LocalDateTime;

public record PrivateBoardMessage(LocalDateTime posttime, String message, String name)
{
  public PrivateBoardMessage(BoardMessage newMessage)
  {
    this(newMessage.getPosttime(), newMessage.getMessage(), newMessage.getPerson().getName());
  }
}
