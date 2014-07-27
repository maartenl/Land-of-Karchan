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
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mmud.database.entities.game.Board;
import mmud.database.entities.game.BoardMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of the public boards and the private boards.
 *
 * @author maartenl
 */
@Stateless
@LocalBean
public class BoardBean
{

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    /**
     * Returns the entity manager of Hibernate/JPA. This is defined in
     * build/web/WEB-INF/classes/META-INF/persistence.xml.
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager()
    {
        return em;
    }
    private static final Logger itsLog = LoggerFactory.getLogger(BoardBean.class);

    public Board getNewsBoard()
    {
        return getBoard("logonmessage");
    }

    public Board getBoard(String name)
    {
        Query query = getEntityManager().createNamedQuery("Board.findByName");
        query.setParameter("name", name);
        return (Board) query.getSingleResult();

    }

    public List<BoardMessage> getNews()
    {
        getEntityManager().setProperty("activePersonFilter", 0); // turns filter off
        Query query = getEntityManager().createNamedQuery("BoardMessage.news");
        query.setMaxResults(10);
        List<BoardMessage> list = query.getResultList();
        return list;
    }
}
