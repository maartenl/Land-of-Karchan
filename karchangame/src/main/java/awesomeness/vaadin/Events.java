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

import awesomeness.vaadin.utils.IntegerProperty;
import awesomeness.vaadin.utils.Utilities;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Sizeable;
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
import javax.persistence.Query;
import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Method;
import mmud.database.entities.game.Room;
import mmud.rest.services.LogBean;

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

    private final TextField person;
    private final TextField room;
    private final TextField method;
    private final LogBean logBean;

    Events(final Admin currentUser, final LogBean logBean)
    {
        this.currentUser = currentUser;
        this.logBean = logBean;
        // And there we have it
        final JPAContainer<mmud.database.entities.game.Event> attributes = Utilities.getJPAContainer(mmud.database.entities.game.Event.class);

        final Container.Filter filter = new Compare.Equal("owner",
                currentUser);

        Panel searchPanel = new Panel();
        addComponent(searchPanel);

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchPanel.setContent(searchLayout);

        final TextField filterOnId = new TextField("Id", new IntegerProperty());
        filterOnId.setConverter(new StringToIntegerConverter());
        filterOnId.setDescription("Does not allows wildcards.");
        filterOnId.addValueChangeListener(new Property.ValueChangeListener()
        {
            private Container.Filter filter;

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                if (filter != null)
                {
                    attributes.removeContainerFilter(filter);
                    filter = null;
                }
                // Integer eventId = (Integer) event.getProperty().getValue();
                Integer eventId = (Integer) filterOnId.getPropertyDataSource().getValue();
                if (eventId == null)
                {
                    return;
                }
                filter = new Compare.Equal("eventid", eventId);
                attributes.addContainerFilter(filter);
            }
        });
        searchLayout.addComponent(filterOnId);

        TextField filterOnMethodName = new TextField("Filter on methodname");
        filterOnMethodName.setDescription("Does not allows wildcards.");
        filterOnMethodName.addValueChangeListener(new Property.ValueChangeListener()
        {
            private Container.Filter filter;

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                if (filter != null)
                {
                    attributes.removeContainerFilter(filter);
                    filter = null;
                }
                String methodName = (String) event.getProperty().getValue();
                if (methodName == null || methodName.trim().equals(""))
                {
                    return;
                }
                Query methodQuery = attributes.getEntityProvider().getEntityManager().createNamedQuery("Method.findByName");
                methodQuery.setParameter("name", methodName);
                Method foundMethod = (Method) methodQuery.getSingleResult();
                if (foundMethod != null)
                {
                    filter = new Compare.Equal("method", foundMethod);
                    attributes.addContainerFilter(filter);
                }
            }
        });
        searchLayout.addComponent(filterOnMethodName);

        TextField filterOnPerson = new TextField("Filter on person");
        filterOnPerson.setDescription("Does not allows wildcards.");
        filterOnPerson.addValueChangeListener(new Property.ValueChangeListener()
        {
            private Container.Filter filter;

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                if (filter != null)
                {
                    attributes.removeContainerFilter(filter);
                    filter = null;
                }
                String personName = (String) event.getProperty().getValue();
                if (personName == null || personName.trim().equals(""))
                {
                    return;
                }
                Query personQuery = attributes.getEntityProvider().getEntityManager().createNamedQuery("Person.findByName");
                personQuery.setParameter("name", personName);
                Person foundPerson = (Person) personQuery.getSingleResult();
                if (foundPerson != null)
                {
                    filter = new Compare.Equal("person", foundPerson);
                    attributes.addContainerFilter(filter);
                }
            }
        });
        searchLayout.addComponent(filterOnPerson);

        final TextField filterOnRoom = new TextField("Filter on room", new IntegerProperty());
        filterOnRoom.setConverter(new StringToIntegerConverter());
        filterOnRoom.setDescription("Does not allows wildcards.");
        filterOnRoom.addValueChangeListener(new Property.ValueChangeListener()
        {
            private Container.Filter filter;

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                if (filter != null)
                {
                    attributes.removeContainerFilter(filter);
                    filter = null;
                }
                // Integer eventId = (Integer) event.getProperty().getValue();
                Integer roomId = (Integer) filterOnRoom.getPropertyDataSource().getValue();
                if (roomId == null)
                {
                    return;
                }
                Query roomQuery = attributes.getEntityProvider().getEntityManager().createNamedQuery("Room.findById");
                roomQuery.setParameter("id", roomId);
                Room foundRoom = (Room) roomQuery.getSingleResult();
                if (foundRoom != null)
                {
                    filter = new Compare.Equal("room", foundRoom);
                    attributes.addContainerFilter(filter);
                }
            }
        });
        searchLayout.addComponent(filterOnRoom);

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
        searchLayout.addComponent(filterOnOwner);

        Panel tablePanel = new Panel();
        addComponent(tablePanel);

        eventsTable = new Table("Events", attributes);
        eventsTable.setVisibleColumns("eventid", "person", "room", "month", "dayofmonth", "hour", "minute", "dayofweek", "method", "owner", "creation");
//        eventsTable.setVisibleColumns("eventid", "person", "room", "month", "dayofmonth", "hour", "minute", "dayofweek", "callable", "method", "owner", "creation");
        Utilities.setTableSize(eventsTable);
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

        person = new TextField("Person");
        layout.addComponent(person);

        room = new TextField("Room", new IntegerProperty());
        room.setConverter(new StringToIntegerConverter());
        layout.addComponent(room);

        method = new TextField("Methodname");
        method.setWidth(50, Sizeable.Unit.EM);
        layout.addComponent(method);

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
                String methodName = (String) method.getValue();
                Query methodQuery = attributes.getEntityProvider().getEntityManager().createNamedQuery("Method.findByName");
                methodQuery.setParameter("name", methodName);
                Method foundMethod = (Method) methodQuery.getSingleResult();
                item.getItemProperty("method").setValue(foundMethod);
                setPerson("person", person, item);
                setRoom("room", room, item);
                try
                {
                    binder.commit();
                    if (busyCreatingNewItem == true)
                    {

                        Object itemId = attributes.addEntity(newInstance);
                        eventsTable.setValue(itemId);
                        logBean.writeDeputyLog(currentUser, "New event '" + itemId + "' created.");
                    } else
                    {
                        logBean.writeDeputyLog(currentUser, "Event '" + eventsTable.getValue() + "' updated.");
                    }
                } catch (FieldGroup.CommitException ex)
                {
                    logger.log(Level.SEVERE, null, ex);
                }
                busyCreatingNewItem = false;
            }

            private void setRoom(String direction, TextField roomTextfield, Item item) throws Property.ReadOnlyException
            {
                if (roomTextfield.getPropertyDataSource() == null)
                {
                    item.getItemProperty(direction).setValue(null);
                }
                Integer roomId = (Integer) roomTextfield.getPropertyDataSource().getValue();
                if (roomId != null)
                {
                    Query roomQuery = attributes.getEntityProvider().getEntityManager().createNamedQuery("Room.findById");
                    roomQuery.setParameter("id", roomId);
                    item.getItemProperty(direction).setValue((Room) roomQuery.getSingleResult());
                } else
                {
                    item.getItemProperty(direction).setValue(null);
                }
            }

            private void setPerson(String person, TextField personTextfield, Item item)
            {
                if (personTextfield.getValue() == null)
                {
                    item.getItemProperty(person).setValue(null);
                }
                String personName = (String) personTextfield.getValue();
                if (personName != null && !personName.trim().equals(""))
                {
                    Query personQuery = attributes.getEntityProvider().getEntityManager().createNamedQuery("Person.findByName");
                    personQuery.setParameter("name", personName);
                    item.getItemProperty(person).setValue((Person) personQuery.getSingleResult());
                } else
                {
                    item.getItemProperty(person).setValue(null);
                }
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
                logBean.writeDeputyLog(currentUser, "Event '" + eventsTable.getValue() + "' deleted.");
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
            Property personProperty = item.getItemProperty("person");
            if (personProperty != null && personProperty.getValue() != null)
            {
                Person personName = (Person) personProperty.getValue();
                person.setValue(personName.getName());
            } else
            {
                person.setValue(null);
            }
            Property roomProperty = item.getItemProperty("room");
            if (roomProperty != null && roomProperty.getValue() != null)
            {
                Room roomroom = (Room) roomProperty.getValue();
                room.setPropertyDataSource(new IntegerProperty(roomroom.getId()));
            } else
            {
                room.setPropertyDataSource(new IntegerProperty());
            }
            Property methodProperty = item.getItemProperty("method");
            if (methodProperty != null && methodProperty.getValue() != null)
            {
                method.setValue(((Method) methodProperty.getValue()).getName());
            } else
            {
                method.setValue(null);
            }
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
