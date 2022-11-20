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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Commandlog;
import mmud.database.entities.game.Log;
import mmud.exceptions.ExceptionUtils;
import mmud.exceptions.MudWebException;

/**
 * @author maartenl
 */
public class LogService
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

  private static final Logger LOGGER = Logger.getLogger(LogService.class.getName());

  /**
   * <p>
   * Create and persist a new log entity.</p>
   * <p>
   * In case the message is too long, the message will be truncated to 255 characters in length.</p>
   * <p>
   * Also in this case a log message
   * is created+persisted containing the error that the message was too long and the message
   * is appended in the addendum.</p>
   * <p>
   * </p>
   *
   * @param person   the person, may be null.
   * @param message  the message, may not be null.
   * @param addendum the addendum that does not fit in the message, may be null.
   * @return the new log
   */
  private Log createLogWithString(String person, String message, String addendum)
  {
    if (message.length() > 255)
    {
      createLogWithString(person, "The log message was too long!", message);
    }
    Log log = new Log();
    log.setName(person);
    log.setMessage(message.length() < 255 ? message : message.substring(0, 255));
    log.setAddendum(addendum);
    getEntityManager().persist(log);
    return log;
  }

  /**
   * @param name     the name of the player
   * @param creation all 1000 messages after this creation datetime.
   * @return list of logs
   */
  @Nonnull
  public List<Log> getLogs(@Nullable String name, @Nullable LocalDate creation)
  {
    LOGGER.finest("getLogs");
    LocalDateTime localDateTime = creation == null ? LocalDate.now().atTime(23, 59, 0) : creation.atStartOfDay();
    TypedQuery<Log> namedQuery = getEntityManager().createNamedQuery("Log.find", Log.class)
      .setParameter("name", name)
      .setParameter("creation", localDateTime)
      .setMaxResults(1000);
    return namedQuery.getResultList();
  }

  /**
   * @param person
   * @param message
   * @param addendum
   * @return
   * @see #createLogWithString(java.lang.String, java.lang.String, java.lang.String)
   */
  private Log createLogWithPerson(Person person, String message, String addendum)
  {
    return createLogWithString(person == null ? null : person.getName(), message, addendum);
  }

  /**
   * @param person  the person, may be null.
   * @param message the message, may not be null.
   * @return the new log
   * @see #createLog(mmud.database.entities.characters.Person, java.lang.String)
   */
  private Log createLog(Person person, String message)
  {
    Log log = createLogWithPerson(person, message, null);
    return log;
  }

  /**
   * write a deputy log message to the database.
   *
   * @param admin    the deputy which has caused this log message.
   * @param message  the message to be written in the log, may not be larger
   *                 than 255 characters or it will be truncated, and a second message will
   *                 be logged mentioning this.
   * @param addendum the rest of the message, can be large. Used for storing
   *                 specifics regarding the logmessage.
   */
  public void writeDeputyLog(Admin admin, String message, String addendum)
  {
    Log log = createLogWithString(admin.getName(), message, addendum);
    log.setDeputy(true);
  }

  /**
   * write a log message to the database. A simple general message.Ã¸
   *
   * @param admin   the deputy which has caused this log message.
   * @param message the message to be written in the log, may not be larger
   *                than 255 characters or it will be truncated, and a second message will
   *                be logged mentioning this.
   */
  public void writeDeputyLog(Admin admin, String message)
  {
    Log log = createLogWithString(admin.getName(), message, null);
    log.setDeputy(true);
  }

  /**
   * write a log message to the database. A simple general message.Ã¸
   *
   * @param message  the message to be written in the log, may not be larger
   *                 than 255 characters or it will be truncated, and a second message will
   *                 be logged mentioning this.
   * @param addendum the rest of the message, can be large. Used for storing
   *                 specifics regarding the logmessage.
   */
  public void writeLog(String message, String addendum)
  {
    writeLog(null, message, addendum);
  }

  /**
   * write a log message to the database. A simple general message.Ã¸
   *
   * @param message the message to be written in the log, may not be larger
   *                than 255 characters or it will be truncated, and a second message will
   *                be logged mentioning this.
   */
  public void writeLog(String message)
  {
    writeLog(null, message, null);
  }

  /**
   * write a log message to the database. This log facility is primarily used
   * to keep a record of what kind of important mutations are done or
   * attempted by both characters as well as administrators. Some examples:
   * <ul>
   * <li>an item is picked up off the floor by a character
   * <li>an item is eaten
   * <li>an administrator creates a new item/room/character
   * </ul>
   *
   * @param person  the person to be inscribed in the log table. May be null.
   * @param message the message to be written in the log, may not be larger
   *                than 255 characters or it will be truncated, and a second message will
   *                be logged mentioning this.
   */
  public void writeLog(Person person, String message)
  {
    LOGGER.finer("writeLog");
    createLog(person, message);
  }

  /**
   * write a log message to the database. This log facility is primarily used
   * to keep a record of what kind of important mutations are done or
   * attempted by both characters as well as administrators. Some examples:
   * <ul>
   * <li>an item is picked up off the floor by a character
   * <li>an item is eaten
   * <li>an administrator creates a new item/room/character
   * </ul>
   *
   * @param person   the person to be inscribed in the log table. May be null.
   * @param message  the message to be written in the log, may not be larger
   *                 than 255 characters or it will be truncated, and a second message will
   *                 be logged mentioning this.
   * @param addendum the rest of the message, can be large. Used for storing
   *                 specifics regarding the logmessage.
   */
  public void writeLog(Person person, String message, String addendum)
  {
    LOGGER.finer("writeLog");
    createLogWithPerson(person, message, addendum);
  }

  /**
   * write a log message of an exception to the database.
   *
   * @param person    the person to be inscribed in the log table. May be null.
   * @param throwable the exception or error to be written to the log table.
   */
  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void writeLogException(Person person, Throwable throwable)
  {
    LOGGER.finer("writeLogException");
    createLogWithPerson(person, throwable.toString(), org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(throwable));
  }

  /**
   * write a log message of an exception to the database.
   *
   * @param person    the person to be inscribed in the log table. May be null.
   * @param throwable the exception or error to be written to the log table.
   */
  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void writeLogException(Person person, String message, Throwable throwable)
  {
    LOGGER.finer("writeLogException");
    createLogWithPerson(person, message, throwable.toString() + ":" + org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(throwable));
  }

  /**
   * write a log message of an exception to the database.
   *
   * @param throwable the exception or error to be written to the log table.
   */
  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void writeLogException(String message, Throwable throwable)
  {
    LOGGER.finer("writeLogException");
    createLogWithString(null, message, throwable.toString() + ":" + org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(throwable));
  }

  /**
   * write a log message of an exception to the database.
   *
   * @param throwable the exception or error to be written to the log table.
   */
  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void writeLogException(Throwable throwable)
  {
    LOGGER.finer("writeLogException");
    createLogWithString(null, throwable.toString(), org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(throwable));
  }

  /**
   * write a command to the database. This log facility is primarily used to
   * keep a chatrecord.
   *
   * @param person  the person to be inscribed in the log table. May be null.
   * @param command the command that is to be executed written in the log, may
   *                not be larger than 255 characters.
   */
  public void writeCommandLog(Person person, String command)
  {
    LOGGER.finer("writeCommandLog");

    Commandlog commandlog = new Commandlog();
    commandlog.setName(person == null ? null : person.getName());
    commandlog.setStamp(LocalDateTime.now());
    commandlog.setCommand(command);
    try
    {
      getEntityManager().persist(commandlog);
    } catch (ConstraintViolationException e)
    {
      throw new MudWebException(null, ExceptionUtils.createMessage(e), e, Response.Status.BAD_REQUEST);
    }
  }
}
