package mmud.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mmud.database.entities.game.Room;

public class RoomsService
{
  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  private EntityManager getEntityManager()
  {
    return em;
  }

  public Room find(Long id)
  {
    return getEntityManager().find(Room.class, id);
  }

}
