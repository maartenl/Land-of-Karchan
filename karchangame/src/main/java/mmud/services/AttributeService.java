package mmud.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mmud.database.entities.game.Worldattribute;

public class AttributeService
{

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  /**
   * Returns the entity manager of JPA. This is defined in
   * build/web/WEB-INF/classes/META-INF/persistence.xml.
   *
   * @return EntityManager
   */
  private EntityManager getEntityManager()
  {
    return em;
  }

  public String getAttribute(String name)
  {
    Worldattribute attribute = getEntityManager().find(Worldattribute.class, name);
    if (attribute == null)
    {
      return null;
    }
    return attribute.getContents();
  }
}
