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
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;

/**
 *
 * @author maartenl
 */
public class Events extends VerticalLayout implements
        Property.ValueChangeListener
{

    private static final Logger logger = Logger.getLogger(Events.class.getName());

    private final CheckBox filterOnOwner;

    private final Table eventsTable;
    private final TextField month;
    private final TextField dayofmonth;
    private final Label owner;
    private final Button commit;
    private FieldGroup binder;
    private final Admin currentUser;
    private final Button discard;
    private final Button disown;
    private final FormLayout layout;
    private Item item;
    private boolean busyCreatingNewItem = false;
    private mmud.database.entities.game.Event newInstance;
    private final TextField hour;
    private final TextField minute;
    private final TextField dayofweek;
    private final TextField eventid;

    Events(final EntityProvider entityProvider, final Admin currentUser)
    {
        this.currentUser = currentUser;
        // And there we have it
        final JPAContainer<mmud.database.entities.game.Event> attributes
                = new JPAContainer<>(mmud.database.entities.game.Event.class);
        final Container.Filter filter = new Compare.Equal("owner",
                currentUser);
        attributes.setEntityProvider(entityProvider);

        filterOnOwner = new CheckBox("Filter on owner");
        filterOnOwner.addValueChangeListener(new Property.ValueChangeListener()
        {

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                if (event.getProperty().getValue().equals(Boolean.TRUE))
                {
                    attributes.addContainerFilter(filter);
                } else
                {
                    attributes.removeContainerFilter(filter);
                }
            }
        });
        addComponent(filterOnOwner);
        Panel tablePanel = new Panel();
        addComponent(tablePanel);

        eventsTable = new Table("Events", attributes);
        eventsTable.setVisibleColumns("eventid", "person", "room", "month", "dayofmonth", "hour", "minute", "dayofweek", "method", "owner", "creation");
//        eventsTable.setVisibleColumns("eventid", "person", "room", "month", "dayofmonth", "hour", "minute", "dayofweek", "callable", "method", "owner", "creation");
        eventsTable.setSizeFull();
        eventsTable.setSelectable(true);
        eventsTable.addValueChangeListener(this);
        eventsTable.setImmediate(true);
        tablePanel.setContent(eventsTable);

        Panel formPanel = new Panel();
        addComponent(formPanel);

        layout = new FormLayout();
        formPanel.setContent(layout);

        eventid = new TextField("Eventid");
        layout.addComponent(eventid);

        month = new TextField("Month");
        layout.addComponent(month);

        dayofmonth = new TextField("Day of month");
        layout.addComponent(dayofmonth);

        hour = new TextField("Hour");
        layout.addComponent(hour);

        minute = new TextField("Minute");
        layout.addComponent(minute);

        dayofweek = new TextField("Day of week");
        layout.addComponent(dayofweek);

        owner = new Label();
        owner.setCaption("Owner");
        layout.addComponent(owner);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        layout.addComponent(buttonsLayout);
        commit = new Button("Save", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                logger.log(Level.FINEST, "commit clicked.");
                item.getItemProperty("owner").setValue(currentUser);
                try
                {
                    binder.commit();
                    if (busyCreatingNewItem == true)
                    {

                        Object itemId = attributes.addEntity(newInstance);
                        eventsTable.setValue(itemId);
                    }
                } catch (FieldGroup.CommitException ex)
                {
                    logger.log(Level.SEVERE, null, ex);
                }
                busyCreatingNewItem = false;
            }
        });
        buttonsLayout.addComponent(commit);

        discard = new Button("Cancel", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                logger.log(Level.FINEST, "discard clicked.");
                binder.discard();
            }
        });
        buttonsLayout.addComponent(discard);

        Button create = new Button("Create", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                busyCreatingNewItem = true;
                newInstance = new mmud.database.entities.game.Event();
                newInstance.setOwner(currentUser);
                newInstance.setCreation(new Date());
                item = new BeanItem(newInstance);
                binder = new FieldGroup(item);
                binder.setBuffered(true);
                binder.setEnabled(true);
                binder.setReadOnly(false);
                month.setReadOnly(true);
                month.setEnabled(false);
                binder.bind(eventid, "eventid");
                binder.bind(month, "month");
                binder.bind(dayofmonth, "dayofmonth");
                binder.bind(hour, "hour");
                binder.bind(minute, "minute");
                binder.bind(dayofweek, "dayofweek");
                // Object itemId = attributes.addEntity(newInstance);
                // eventsTable.setValue(itemId);
            }
        });
        buttonsLayout.addComponent(create);

        Button delete = new Button("Delete", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                attributes.removeItem(eventsTable.getValue());
            }
        });
        buttonsLayout.addComponent(delete);

        disown = new Button("Disown", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                logger.log(Level.FINEST, "disown clicked.");
                item.getItemProperty("owner").setValue(null);
//                try
//                {
//                    binder.commit();
//                } catch (FieldGroup.CommitException ex)
//                {
//                    logger.log(Level.SEVERE, null, ex);
//                }
            }
        });
        buttonsLayout.addComponent(disown);
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event)
    {
        Object itemId = event.getProperty().getValue();
        item = eventsTable.getItem(itemId);
        boolean entitySelected = item != null;
        if (entitySelected)
        {
            busyCreatingNewItem = false;
            binder = new FieldGroup(item);
            binder.setBuffered(true);
            binder.setEnabled(true);
            binder.setReadOnly(false);
            month.setReadOnly(true);
            month.setEnabled(false);
            binder.bind(eventid, "eventid");
            binder.bind(month, "month");
            binder.bind(dayofmonth, "dayofmonth");
            binder.bind(hour, "hour");
            binder.bind(minute, "minute");
            binder.bind(dayofweek, "dayofweek");
            Property itemProperty = item.getItemProperty("owner");
            boolean enabled = false;
            if (itemProperty == null || itemProperty.getValue() == null)
            {
                owner.setValue("");
                enabled = true;
            } else
            {
                Admin admin = (Admin) itemProperty.getValue();
                owner.setValue(admin.getName());
                if (admin.getName().equals(currentUser.getName()))
                {
                    enabled = true;
                }
            }
            layout.setEnabled(enabled);
        }
    }

}
