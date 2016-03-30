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
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import mmud.database.entities.game.Admin;
import mmud.rest.services.LogBean;
import mmud.rest.services.admin.IdentificationBean;

/**
 *
 * @author maartenl
 */
@WebServlet(urlPatterns =
{
    "/administration/*", "/VAADIN/*"
}, name = "MyUIServlet", asyncSupported = true)
@ServletSecurity(
        @HttpConstraint(rolesAllowed =
        {
            "deputy"
}))
@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
public class MyUIServlet extends VaadinServlet
{

    @PersistenceContext(name = "persistence/em", unitName = "karchangamePU")
    private EntityManager em;

    @EJB
    private IdentificationBean identificationBean;

    @EJB
    private PersonProvider personProvider;

    @EJB
    private LogBean logBean;

    public Admin getCurrentUser()
    {
        return identificationBean.getCurrentAdmin();
    }

    public MyUIServlet()
    {
    }

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

    public LogBean getLogBean()
    {
        return logBean;
    }

    public PersonProvider getPersonProvider()
    {
        return personProvider;
    }
}
