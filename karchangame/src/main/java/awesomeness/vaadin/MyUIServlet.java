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

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
public class MyUIServlet extends VaadinServlet
{

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    @EJB
    private WorldattributesProvider worldattributesProvider;

    @EJB
    private ScriptsProvider scriptsProvider;

    @EJB
    private AreasProvider areasProvider;

    @EJB
    private EventsProvider eventsProvider;

    @EJB
    private RoomsProvider roomsProvider;

    @EJB
    private CommandsProvider commandsProvider;

    @EJB
    private LogsProvider logsProvider;

    @EJB
    private CommandlogsProvider commandLogsProvider;

    @EJB
    private IdentificationBean identificationBean;

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

    EntityProvider getWorldattributesProvider()
    {
        return worldattributesProvider;
    }

    EntityProvider getScriptsProvider()
    {
        return scriptsProvider;
    }

    EntityProvider getAreasProvider()
    {
        return areasProvider;
    }

    EntityProvider getEventsProvider()
    {
        return eventsProvider;
    }

    EntityProvider getRoomsProvider()
    {
        return roomsProvider;
    }

    public CommandsProvider getCommandsProvider()
    {
        return commandsProvider;
    }

    public LogBean getLogBean()
    {
        return logBean;
    }

    public CommandlogsProvider getCommandlogsProvider()
    {
        return commandLogsProvider;
    }

    public LogsProvider getLogsProvider()
    {
        return logsProvider;
    }

}
