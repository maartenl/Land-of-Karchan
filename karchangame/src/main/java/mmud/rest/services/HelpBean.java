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

import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mmud.database.entities.game.Help;

/**
 * Takes care of the help topics.
 *
 * @author maartenl
 */
@Stateless
@LocalBean
public class HelpBean
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
    private static final Logger itsLog = Logger.getLogger(HelpBean.class.getName());

    /**
     * returns a help message on the current command, or general help
     *
     * @param name
     * the command to enlist help for. If this is a null pointer,
     * general help will be requested.
     * @return Help entity.
     */
    public Help getHelp(String name)
    {
        if (name == null)
        {
            name = "general help";
        }
        Help help = getEntityManager().find(Help.class, name);
        if (help == null)
        {
            help = getEntityManager().find(Help.class, "sorry");

        }
        return help;
    }
}
