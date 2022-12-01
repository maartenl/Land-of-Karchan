package mmud.services;

import java.util.List;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.core.Response;
import mmud.database.entities.characters.User;
import mmud.database.entities.web.CharacterInfo;
import mmud.database.entities.web.Family;
import mmud.exceptions.MudWebException;
import mmud.rest.services.RestUtilities;
import mmud.rest.webentities.PrivatePerson;

public class PrivateService
{
  private static final Logger LOGGER = Logger.getLogger(PrivateService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  /**
   * Returns the entity manager of JPA. This is defined in
   * build/web/WEB-INF/classes/META-INF/persistence.xml.
   *
   * @return EntityManager
   */
  protected EntityManager getEntityManager()
  {
    return em;
  }

  public PrivatePerson charactersheet(String name)
  {
    LOGGER.finer("entering charactersheet");

    User person = getEntityManager().find(User.class, name);
    if (person == null)
    {
      throw new MudWebException(name, "Charactersheet not found.", Response.Status.NOT_FOUND);
    }

    TypedQuery<Family> query = getEntityManager().createNamedQuery("Family.findByName", Family.class);
    query.setParameter("name", person.getName());
    List<Family> family = query.getResultList();
    CharacterInfo characterInfo = getEntityManager().find(CharacterInfo.class, person.getName());
    PrivatePerson res = RestUtilities.getPrivatePerson(person, family, characterInfo);

    LOGGER.finer("exiting charactersheet");
    return res;
  }

}
