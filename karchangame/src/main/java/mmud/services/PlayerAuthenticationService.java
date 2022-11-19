package mmud.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.characters.User;
import mmud.exceptions.MudWebException;
import mmud.rest.services.GameRestService;

public class PlayerAuthenticationService
{

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


  /**
   * This method should be called to verify that the target of a certain
   * action is indeed a proper authenticated user.
   *
   * @param name the name to identify the person
   * @return the User identified by the name.
   * @throws WebApplicationException NOT_FOUND, if the user is either not
   *                                 found or is not a proper user. BAD_REQUEST if an unexpected exception
   *                                 crops up or provided info is really not proper. UNAUTHORIZED if session
   *                                 passwords do not match.
   */
  public User authenticate(String name, SecurityContext context)
  {
    if (name == null || name.equals("null"))
    {
      throw new MudWebException(name, "You are not logged in.", Response.Status.UNAUTHORIZED);
    }
    if (!getPlayerName(context).equals(name))
    {
      throw new MudWebException(name, "You are not logged in as " + name + ".", Response.Status.UNAUTHORIZED);
    }
    User person = getEntityManager().find(User.class, name);
    if (person == null)
    {
      throw new MudWebException(name, "User was not found.", "User was not found  (" + name + ")", Response.Status.NOT_FOUND);
    }
    if (!person.isUser())
    {
      throw new MudWebException(name, "User was not a user.", "User was not a user (" + name + ")", Response.Status.BAD_REQUEST);
    }
    return person;
  }

  /**
   * Authenticates a guildmaster.
   *
   * @param name the name to identify the person
   * @return WebApplicationException NOT_FOUND if you are not the member of a guild
   * UNAUTHORIZED if you are not the guild master of your guild.
   */
  public User authenticateGuildMaster(String name, SecurityContext context)
  {
    if (!getPlayerName(context).equals(name))
    {
      throw new MudWebException(name, "You are not logged in as " + name, Response.Status.UNAUTHORIZED);
    }
    User person = authenticate(name, context);
    if (person.getGuild() == null)
    {
      throw new MudWebException(name, name + " is not a member of a guild.", "Person (" + name + ") is not a member of a guild", Response.Status.NOT_FOUND);
    }
    if (!person.getGuild().getBoss().getName().equals(person.getName()))
    {
      throw new MudWebException(name, name + " is not the guild master of " + person.getGuild().getTitle() + ".", "Person (" + name + ") is not the guild master of " + person.getGuild().getName(), Response.Status.UNAUTHORIZED);
    }
    return person;
  }

  /**
   * Provides the player who is logged in during this session.
   *
   * @return name of the player
   */
  public String getPlayerName(SecurityContext context)
  {
    final String name = context.getUserPrincipal().getName();
    if (name.equals("ANONYMOUS"))
    {
      throw new MudWebException(null, "Not logged in.", Response.Status.UNAUTHORIZED);
    }
    return name;
  }

  public void logoff(HttpServletRequest requestContext)
  {
    // nasty work-around for Catalina AuthenticatorBase to be able to
    // change/create the session cookie
    requestContext.getSession();
    try
    {
      requestContext.logout();
    } catch (ServletException ex)
    {
      if (ex.getCause() != null)
      {
        Logger.getLogger(GameRestService.class.getName()).log(Level.SEVERE, null, ex.getCause());
      }
      if (ex.getMessage().equals("Logout failed"))
      {
        throw new WebApplicationException(ex, Response.Status.UNAUTHORIZED);
      }
      Logger.getLogger(GameRestService.class.getName()).log(Level.SEVERE, null, ex);
      throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @VisibleForTesting
  public void setEntityManager(EntityManager em)
  {
    this.em = em;
  }
}
