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
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Log;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class Logs extends VerticalLayout
{

    private static final Logger logger = Logger.getLogger(Logs.class.getName());

    private final Table logsTable;
    private final Admin currentUser;
    private final LogBean logBean;

    Logs(final EntityProvider logEntityProvider, final EntityProvider commandlogEntityProvider, final Admin currentUser, final LogBean logBean)
    {
        this.currentUser = currentUser;
        this.logBean = logBean;
        // And there we have it
        final JPAContainer<Log> attributes
                = new JPAContainer<>(Log.class);
        attributes.setEntityProvider(logEntityProvider);

        Panel tablePanel = new Panel();
        addComponent(tablePanel);

        logsTable = new Table("Game Logs", attributes);
        logsTable.setVisibleColumns("id", "name", "message", "deputy", "creation");
        logsTable.setSizeFull();
        logsTable.setSelectable(true);
        logsTable.setImmediate(true);
        tablePanel.setContent(logsTable);

    }

}
