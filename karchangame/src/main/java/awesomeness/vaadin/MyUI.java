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

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import javax.persistence.EntityManager;

/**
 *
 */
@Theme("mytheme")
@Widgetset("awesomeness.vaadin.MyAppWidgetset")
public class MyUI extends UI
{

    @Override
    protected void init(VaadinRequest vaadinRequest)
    {
        MyUIServlet servlet = ((MyUIServlet) MyUIServlet.getCurrent());
        EntityManager em = servlet.getEntityManager();

        Panel mainPanel = new Panel("Administration pages");
        setContent(mainPanel);

        VerticalLayout layout = new VerticalLayout();

        mainPanel.setContent(layout);

        // Create the tabsheet
        MenuBar barmenu = new MenuBar();
        layout.addComponent(barmenu);

        final Panel worldAttributesPanel = new Panel("Worldattributes");
        worldAttributesPanel.setVisible(false);
        Layout worldattributes = new WorldAttributes(servlet.getWorldattributesProvider(), servlet.getCurrentUser(), servlet.getLogBean());
        worldAttributesPanel.setContent(worldattributes);
        layout.addComponent(worldAttributesPanel);

        final Panel scriptsPanel = new Panel("Scripts");
        scriptsPanel.setVisible(false);
        Layout scripts = new Scripts(servlet.getScriptsProvider(), servlet.getCurrentUser(), servlet.getLogBean());
        scriptsPanel.setContent(scripts);
        layout.addComponent(scriptsPanel);

        final Panel areasPanel = new Panel("Areas");
        areasPanel.setVisible(false);
        Layout areas = new Areas(servlet.getAreasProvider(), servlet.getCurrentUser(), servlet.getLogBean());
        areasPanel.setContent(areas);
        layout.addComponent(areasPanel);

        final Panel eventsPanel = new Panel("Events");
        eventsPanel.setVisible(false);
        Layout events = new Events(servlet.getEventsProvider(), servlet.getCurrentUser(), servlet.getLogBean());
        eventsPanel.setContent(events);
        layout.addComponent(eventsPanel);

        final Panel roomsPanel = new Panel("Rooms");
        roomsPanel.setVisible(false);
        Layout rooms = new Rooms(servlet.getRoomsProvider(), servlet.getCurrentUser(), servlet.getLogBean());
        roomsPanel.setContent(rooms);
        layout.addComponent(roomsPanel);

        final Panel commandsPanel = new Panel("Commands");
        commandsPanel.setVisible(false);
        Layout commands = new Commands(servlet.getCommandsProvider(), servlet.getCurrentUser(), servlet.getLogBean());
        commandsPanel.setContent(commands);
        layout.addComponent(commandsPanel);

        final Panel logsPanel = new Panel("Logs");
        logsPanel.setVisible(false);
        Layout logs = new Logs(servlet.getLogsProvider(), servlet.getCommandlogsProvider(), servlet.getSystemlogsProvider(), servlet.getCurrentUser(), servlet.getLogBean());
        logsPanel.setContent(logs);
        layout.addComponent(logsPanel);

        final MenuBar.Command command;
        command = new MenuBar.Command()
        {

            @Override
            public void menuSelected(MenuItem selectedItem)
            {
                worldAttributesPanel.setVisible(false);
                scriptsPanel.setVisible(false);
                areasPanel.setVisible(false);
                eventsPanel.setVisible(false);
                roomsPanel.setVisible(false);
                commandsPanel.setVisible(false);
                logsPanel.setVisible(false);

                switch (selectedItem.getText())
                {
                    case "Worldattributes":
                        worldAttributesPanel.setVisible(true);
                        break;
                    case "Scripts":
                        scriptsPanel.setVisible(true);
                        break;
                    case "Areas":
                        areasPanel.setVisible(true);
                        break;
                    case "Events":
                        eventsPanel.setVisible(true);
                        break;
                    case "Rooms":
                        roomsPanel.setVisible(true);
                        break;
                    case "Commands":
                        commandsPanel.setVisible(true);
                        break;
                    case "Logs":
                        logsPanel.setVisible(true);
                        break;
                }
            }
        };

        barmenu.addItem("Rooms", null, command);

        barmenu.addItem("Areas", null, command);

        barmenu.addItem("Logs", null, command);

        // toplevel
        MenuItem scripting = barmenu.addItem("Scripting", null, null);

        scripting.addItem("Worldattributes", null, command);
        scripting.addItem("Commands", null, command);
        scripting.addItem("Events", null, command);
        scripting.addItem("Scripts", null, command);
    }

}
