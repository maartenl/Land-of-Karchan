package mmud.rest.webentities;

import mmud.database.entities.game.Board;

import java.util.List;

public record PrivateBoard(String description, List<PrivateBoardMessage> messages)
{
  public PrivateBoard(Board board, List<PrivateBoardMessage> messages)
  {
    this(board.getDescription(), messages);
  }
}
