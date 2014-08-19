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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mmud.database.entities.game.Board;
import mmud.database.entities.game.BoardMessage;

/**
 * Takes care of the public boards and the private boards.
 *
 * @author maartenl
 */
@Stateless
@LocalBean
public class BoardBean
{

    private static final Logger itsLog = Logger.getLogger(BoardBean.class.getName());

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

    /**
     * Retrieves the most recent board messages.
     *
     * @return
     */
    public List<BoardMessage> getNews()
    {
        getEntityManager().setProperty("activePersonFilter", 0); // turns filter off
        Query query = getEntityManager().createNamedQuery("BoardMessage.news");
        query.setParameter("sundays", getSundays());
        List<BoardMessage> list = query.getResultList();
        return list;
    }

    /**
     * <p>
     * Retrieves the date of the last Sunday, compared to <i>now</i>.</p>
     * <p>
     * So, if today is Wednesday, 12-12-2010, 13:00:00 hours, this would return
     * Saturday, 08-12-2010, 00:00:00 hours (midnight).</p>
     *
     * @return the Date of last Sunday morning.
     */
    private Date getSundays()
    {
        itsLog.entering(this.getClass().getName(), "getSundays");
        Calendar cal = Calendar.getInstance();
        int daysBackToSunday = cal.get(Calendar.DAY_OF_WEEK); // 1 for sunday ,7 for saturday,
        cal.add(Calendar.DATE, -daysBackToSunday);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date sundays = cal.getTime();
        itsLog.log(Level.INFO, "getSundays: {0}", sundays);
        return sundays;
    }
}
