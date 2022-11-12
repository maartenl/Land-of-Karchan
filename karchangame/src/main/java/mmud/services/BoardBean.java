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

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import mmud.database.entities.DateTimeUtilities;
import mmud.database.entities.game.Board;
import mmud.database.entities.game.BoardMessage;

/**
 * Takes care of the public boards and the private boards.
 *
 * @author maartenl
 */
public class BoardBean
{

  private static final Logger LOGGER = Logger.getLogger(BoardBean.class.getName());

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

  public Board getNewsBoard()
  {
    return getBoard("logonmessage");
  }

  public Board getBoard(String name)
  {
    LOGGER.log(Level.FINER, "getBoard {0}", name);
    Query query = getEntityManager().createNamedQuery("Board.findByName");
    query.setParameter("name", name);
    Board result = (Board) query.getSingleResult();
    return result;

  }

  /**
   * Retrieves the most recent board messages.
   *
   * @return
   */
  public List<BoardMessage> getNews()
  {
    Query query = getEntityManager().createNamedQuery("BoardMessage.news");
    query.setParameter("lastSunday", DateTimeUtilities.getLastSunday(LocalDateTime.now()));
    List<BoardMessage> list = query.getResultList();
    return list;
  }
}
