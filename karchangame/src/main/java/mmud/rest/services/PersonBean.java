/*
 *  Copyright (C) 2012 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmud.rest.services;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.scripting.PersonsInterface;
import mmud.services.CommunicationService;

/**
 * @author maartenl
 */
@Stateless
@LocalBean
public class PersonBean implements PersonsInterface
{

  @EJB
  LogBean logBean;

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
  private static final Logger LOGGER = Logger.getLogger(PersonBean.class.getName());

  /**
   * Retrieves a person from the pool of all persons. Bear in mind that
   * no automatic filters need apply, nor does the person need to
   * actually be playing the game, and the name lookup is case-insensitive.
   *
   * @param name the name of the person.
   * @return a person or null if not found.
   */
  public Person getPerson(String name)
  {
    TypedQuery<Person> query = getEntityManager().createNamedQuery("Person.findByName", Person.class);
    query.setParameter("name", name);
    List<Person> result = query.getResultList();
    if (result.isEmpty())
    {
      return null;
    }
    if (result.size() > 1)
    {
      throw new RuntimeException("bogus!");
    }
    return result.get(0);

  }

  /**
   * Retrieves someone in the game. A bot or someone who is actually active
   * and playing.
   *
   * @param name the name of the person to lookup.
   * @return a Person or null if not found.
   */
  @Override
  public Person find(String name)
  {
    Person result = getEntityManager().find(Person.class, name);
    if (result == null) {
      return null;
    }
    if (!result.isActive())
    {
      return null;
    }
    return result;
  }

  /**
   * Retrieves a player, may or may not be playing the game. The name
   * lookup is case-insensitive.
   *
   * @param name the name of the person.
   * @return a person or null if not found.
   */
  public User getUser(String name)
  {
    TypedQuery<User> query = getEntityManager().createNamedQuery("User.findByName", User.class);
    query.setParameter("name", name);
    List<User> result = query.getResultList();
    if (result.isEmpty())
    {
      return null;
    }
    if (result.size() > 1)
    {
      throw new RuntimeException("bogus!");
    }
    return result.get(0);
  }

  /**
   * Retrieves a player, playing the game. The name
   * lookup is case-insensitive. Is often used for room-overreaching
   * commands like the Who or Tell during play.
   *
   * @param name the name of the person.
   * @return a person or null if not found.
   */
  public User getActiveUser(String name)
  {
    TypedQuery<User> query = getEntityManager().createNamedQuery("User.findActiveByName", User.class);
    query.setParameter("name", name);
    List<User> result = query.getResultList();
    if (result.isEmpty())
    {
      return null;
    }
    if (result.size() > 1)
    {
      throw new RuntimeException("bogus!");
    }
    if (!result.get(0).isActive())
    {
      return null;
    }
    return result.get(0);
  }

  public List<User> getActivePlayers()
  {
    TypedQuery<User> query = getEntityManager().createNamedQuery("User.who", User.class);
    return query.getResultList();
  }

  /**
   * Sends a message from an administrator to all players currently playing the game,
   * irrespective of room they are in.
   *
   * @param message the message in question.
   */
  public void sendWall(String message)
  {
    getActivePlayers().forEach(p -> CommunicationService.getCommunicationService(p).writeMessage(message));
  }

  public void sendWall(String message, Predicate<User> predicate)
  {
    getActivePlayers().stream().filter(predicate).forEach(p -> CommunicationService.getCommunicationService(p).writeMessage(message));
  }
}
