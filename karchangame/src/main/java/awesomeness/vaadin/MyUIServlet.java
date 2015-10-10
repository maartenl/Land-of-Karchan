/*
 * Copyright (C) 2015 maartenl
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
package awesomeness.vaadin;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author maartenl
 */
@WebServlet(urlPatterns =
{
    "/administration/*", "/VAADIN/*"
}, name = "MyUIServlet", asyncSupported = true)
@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
public class MyUIServlet extends VaadinServlet
{

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    /**
     * Returns the entity manager of JPA. This is defined in
     * build/web/WEB-INF/classes/META-INF/persistence.xml.
     *
     * @return EntityManager
     */
    EntityManager getEntityManager()
    {
        return em;
    }
}
