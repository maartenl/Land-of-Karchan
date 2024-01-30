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
package mmud.services;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Chatline;
import mmud.scripting.PersonsInterface;

/**
 * @author maartenl
 */
public class PersonService implements PersonsInterface
{
  private static final Logger LOGGER = Logger.getLogger(PersonService.class.getName());

  LogService logService;

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  public PersonService(LogService logService)
  {
    this.logService = logService;
  }

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
    TypedQuery<Person> query = em.createNamedQuery("Person.findByName", Person.class);
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
    Person result = em.find(Person.class, name);
    if (result == null)
    {
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
    TypedQuery<User> query = em.createNamedQuery("User.findByName", User.class);
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
    TypedQuery<User> query = em.createNamedQuery("User.findActiveByName", User.class);
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
    TypedQuery<User> query = em.createNamedQuery("User.who", User.class);
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

  public void sendChatBubble(Person from, String message)
  {
    getActivePlayers().forEach(to -> CommunicationService.getCommunicationService(to).sendChatBubble(to, from, message));
  }

  public void sendWall(String message, Predicate<User> predicate)
  {
    getActivePlayers().stream().filter(predicate).forEach(p -> CommunicationService.getCommunicationService(p).writeMessage(message));
  }

  @VisibleForTesting
  public void setEntityManager(EntityManager em)
  {
    this.em = em;
  }
}
