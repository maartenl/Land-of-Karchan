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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.text.SimpleDateFormat;
import javax.persistence.EntityManager;
import mmud.database.entities.game.Admin;

/**
 *
 */
@Theme("reindeer")
@Widgetset("awesomeness.vaadin.MyAppWidgetset")
public class MyUI extends UI
{

    @Override
    protected void init(VaadinRequest vaadinRequest)
    {
        MyUIServlet servlet = ((MyUIServlet) MyUIServlet.getCurrent());
        EntityManager em = servlet.getEntityManager();
        Admin admin = servlet.getCurrentUser();

        Panel mainPanel = new Panel("Administration pages");
        setContent(mainPanel);

        VerticalLayout layout = new VerticalLayout();

        mainPanel.setContent(layout);

        // Create the tabsheet
        MenuBar barmenu = new MenuBar();
        layout.addComponent(barmenu);

        final Panel currentUserPanel = new Panel();
        Layout currentUserLayout = new HorizontalLayout();
        currentUserPanel.setContent(currentUserLayout);
        SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("EEEE, dd MMMM yyyy");// yyyy/MM/dd
        String dateString
                = simpleDateFormat.format(admin.getValiduntil().getTime());
        Label nameLabel = new Label("Name: " + admin.getName());
        Label emailLabel = new Label("Email: " + admin.getEmail());
        Label validUntilLabel = new Label("Valid until: " + dateString);
        currentUserLayout.setWidth(100, Unit.PERCENTAGE);
        currentUserLayout.addComponent(nameLabel);
        currentUserLayout.addComponent(emailLabel);
        currentUserLayout.addComponent(validUntilLabel);
        layout.addComponent(currentUserPanel);

        final Panel worldAttributesPanel = new Panel("Worldattributes");
        worldAttributesPanel.setVisible(false);
        Layout worldattributes = new WorldAttributes(admin, servlet.getLogBean());
        worldAttributesPanel.setContent(worldattributes);
        layout.addComponent(worldAttributesPanel);

        final Panel scriptsPanel = new Panel("Scripts");
        scriptsPanel.setVisible(false);
        Layout scripts = new Scripts(admin, servlet.getLogBean());
        scriptsPanel.setContent(scripts);
        layout.addComponent(scriptsPanel);

        final Panel areasPanel = new Panel("Areas");
        areasPanel.setVisible(false);
        Layout areas = new Areas(admin, servlet.getLogBean());
        areasPanel.setContent(areas);
        layout.addComponent(areasPanel);

        final Panel eventsPanel = new Panel("Events");
        eventsPanel.setVisible(false);
        Layout events = new Events(admin, servlet.getLogBean());
        eventsPanel.setContent(events);
        layout.addComponent(eventsPanel);

        final Panel roomsPanel = new Panel("Rooms");
        roomsPanel.setVisible(false);
        Layout rooms = new Rooms(admin, servlet.getLogBean());
        roomsPanel.setContent(rooms);
        layout.addComponent(roomsPanel);

        final Panel commandsPanel = new Panel("Commands");
        commandsPanel.setVisible(false);
        Layout commands = new Commands(admin, servlet.getLogBean());
        commandsPanel.setContent(commands);
        layout.addComponent(commandsPanel);

        final Panel logsPanel = new Panel("Logs");
        logsPanel.setVisible(false);
        Layout logs = new Logs(admin, servlet.getLogBean());
        logsPanel.setContent(logs);
        layout.addComponent(logsPanel);

        final Panel helpPanel = new Panel("Help");
        helpPanel.setVisible(false);
        Explanations explanations = new Explanations(admin, servlet.getLogBean());
        explanations.init();
        helpPanel.setContent(explanations);
        layout.addComponent(helpPanel);

        final Panel banishmentPanel = new Panel("Banishment");
        banishmentPanel.setVisible(false);
        Banishment banishment = new Banishment(admin, servlet.getLogBean());
        banishment.init();
        banishmentPanel.setContent(banishment);
        layout.addComponent(banishmentPanel);

        final Panel itemDefinitionsPanel = new Panel("Item definitions");
        itemDefinitionsPanel.setVisible(false);
        ItemDefinitions itemdefinitions = new ItemDefinitions(admin, servlet.getLogBean(), this);
        itemDefinitionsPanel.setContent(itemdefinitions);
        layout.addComponent(itemDefinitionsPanel);

        final Panel charactersPanel = new Panel("Characters");
        charactersPanel.setVisible(false);
        Characters characters = new Characters(servlet.getPersonProvider(), admin, servlet.getLogBean(), this);
        charactersPanel.setContent(characters);
        layout.addComponent(charactersPanel);

        final Panel guildsPanel = new Panel("Guilds");
        guildsPanel.setVisible(false);
        Guilds guilds = new Guilds(admin, servlet.getLogBean(), this);
        guildsPanel.setContent(guilds);
        layout.addComponent(guildsPanel);

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
                banishmentPanel.setVisible(false);
                itemDefinitionsPanel.setVisible(false);
                helpPanel.setVisible(false);
                charactersPanel.setVisible(false);
                guildsPanel.setVisible(false);

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
                    case "Banishment":
                        banishmentPanel.setVisible(true);
                        break;
                    case "Item definitions":
                        itemDefinitionsPanel.setVisible(true);
                        break;
                    case "Help":
                        helpPanel.setVisible(true);
                        break;
                    case "Characters":
                        charactersPanel.setVisible(true);
                        break;
                    case "Guilds":
                        guildsPanel.setVisible(true);
                        break;
                }
            }
        };

        MenuItem players = barmenu.addItem("Players", null, null);
        players.addItem("Characters", null, command);
        players.addItem("Guilds", null, command);
        players.addItem("Banishment", null, command);

        barmenu.addItem("Rooms", null, command);

        MenuItem items = barmenu.addItem("Items", null, null);
        items.addItem("Item definitions", null, command);

        barmenu.addItem("Areas", null, command);

        barmenu.addItem("Logs", null, command);

        // toplevel
        MenuItem scripting = barmenu.addItem("Scripting", null, null);

        scripting.addItem("Worldattributes", null, command);
        scripting.addItem("Commands", null, command);
        scripting.addItem("Events", null, command);
        scripting.addItem("Scripts", null, command);

        barmenu.addItem("Help", null, command);
    }

}
