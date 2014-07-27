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
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Guild;
import mmud.exceptions.MudException;

/**
 * Takes care of the guilds.
 *
 * @author maartenl
 */
@Stateless
@LocalBean
public class GuildBean
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
    private static final Logger itsLog = Logger.getLogger(GuildBean.class.getName());

    /**
     * Retrieves the guild by name. Returns null if not found.
     *
     * @param name the name of the guild.
     * @return a guild or null.
     */
    public Guild getGuild(String name)
    {
        return getEntityManager().find(Guild.class, name);

    }

    /**
     * returns a list of persons that wish to
     * become a member of a guild.
     *
     * @return list of guild hopefuls.
     * @param aGuild
     * the guild
     * @throws MudException
     * if something goes wrong.
     */
    public List<Person> getGuildHopefuls(Guild aGuild)
    {
        Query query = getEntityManager().createNamedQuery("Guild.findGuildHopefuls");
        query.setParameter("guildname", aGuild.getName());
        query.setParameter("attributename", "guildwish");
        query.setParameter("valuetype", "string");

        return query.getResultList();
    }
}
