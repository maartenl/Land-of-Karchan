package mmud.services;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import mmud.database.entities.game.Admin;

public class AdminService
{
  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;


  protected EntityManager getEntityManager()
  {
    return em;
  }

  /**
   * Returns a list of currently valid administrators.
   *
   * @return list of administrators
   */
  @RolesAllowed("player")
  public List<Admin> getAdministrators()
  {
    TypedQuery<Admin> query = getEntityManager().createNamedQuery("Admin.findValid", Admin.class);
    List<Admin> list = query.getResultList();
    return list;
  }

  /**
   * Returns the found valid administrator with that name.
   *
   * @param name the name of the administrator to look for, case insensitive.
   * @return An optional which is either empty (not found) or contains
   * the administrator.
   */
  @RolesAllowed("player")
  public Optional<Admin> getAdministrator(String name)
  {
    return getAdministrators().stream().filter(x -> x.getName().equalsIgnoreCase(name)).findFirst();
  }
}
