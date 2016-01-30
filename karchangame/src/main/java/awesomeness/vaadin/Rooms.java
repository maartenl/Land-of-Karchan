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
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Room;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class Rooms extends VerticalLayout implements
        Property.ValueChangeListener
{

    private static final Logger logger = Logger.getLogger(Rooms.class.getName());

    private final CheckBox filterOnOwner;

    private final Table roomsTable;
    private final Label owner;
    private final Button commit;
    private FieldGroup binder;
    private final Admin currentUser;
    private final Button discard;
    private final Button disown;
    private final FormLayout layout;
    private Item item;
    private boolean busyCreatingNewItem = false;
    private mmud.database.entities.game.Room newInstance;
    private final TextField id;
    private final TextField picture;
    private final TextField title;
    private final TextArea contents;
    private final ComboBox area;

    private final TextField north;
    private final TextField south;
    private final TextField east;
    private final TextField west;
    private final TextField up;
    private final TextField down;
    private final LogBean logBean;

    Rooms(final Admin currentUser, final LogBean logBean)
    {
        this.currentUser = currentUser;
        this.logBean = logBean;
        final JPAContainer<mmud.database.entities.game.Room> attributes = Utilities.getJPAContainer(mmud.database.entities.game.Room.class);

        final Filter filter = new Compare.Equal("owner",
                currentUser);

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

        roomsTable = new Table("Rooms", attributes);
        roomsTable.setVisibleColumns("id", "title", "area", "owner", "creation");
        roomsTable.setSizeFull();
        roomsTable.setSelectable(true);
        roomsTable.addValueChangeListener(this);
        roomsTable.setImmediate(true);
        tablePanel.setContent(roomsTable);

        Panel formPanel = new Panel();
        addComponent(formPanel);

        layout = new FormLayout();
        formPanel.setContent(layout);

        id = new TextField("Roomid");
        id.setReadOnly(true);
        id.setEnabled(false);
        layout.addComponent(id);

        picture = new TextField("Picture");
        picture.setWidth(50, Sizeable.Unit.EM);
        layout.addComponent(picture);

        title = new TextField("Title");
        title.setWidth(50, Sizeable.Unit.EM);
        layout.addComponent(title);

        // Create a selection component
        area = new ComboBox("Area");
        // Add items with given item IDs
        Query areas = attributes.getEntityProvider().getEntityManager().createNamedQuery("Area.findAll");
        List<Area> foundareas = areas.getResultList();
        for (Area found : foundareas)
        {
            area.addItem(found.getArea());
        }
        layout.addComponent(area);

        contents = new TextArea("Description");
        contents.setRows(15);
        contents.setWidth(80, Sizeable.Unit.EM);
        layout.addComponent(contents);

        north = new TextField("North", new IntegerProperty());
        north.setConverter(new StringToIntegerConverter());
        layout.addComponent(north);

        south = new TextField("South", new IntegerProperty());
        south.setConverter(new StringToIntegerConverter());
        layout.addComponent(south);

        east = new TextField("East", new IntegerProperty());
        east.setConverter(new StringToIntegerConverter());
        layout.addComponent(east);

        west = new TextField("West", new IntegerProperty());
        west.setConverter(new StringToIntegerConverter());
        layout.addComponent(west);

        up = new TextField("Up", new IntegerProperty());
        up.setConverter(new StringToIntegerConverter());
        layout.addComponent(up);

        down = new TextField("Down", new IntegerProperty());
        down.setConverter(new StringToIntegerConverter());
        layout.addComponent(down);

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
                String areaname = (String) area.getValue();
                Query areaQuery = attributes.getEntityProvider().getEntityManager().createNamedQuery("Area.findByArea");
                areaQuery.setParameter("area", areaname);
                try
                {
                    Area foundArea = (Area) areaQuery.getSingleResult();
                    item.getItemProperty("area").setValue(foundArea);
                } catch (NoResultException e)
                {
                    // so we have no area, no problem.
                }

                setRoom("north", north, item);
                setRoom("south", south, item);
                setRoom("west", west, item);
                setRoom("east", east, item);
                setRoom("up", up, item);
                setRoom("down", down, item);

                try
                {
                    binder.commit();
                    if (busyCreatingNewItem == true)
                    {

                        Object itemId = attributes.addEntity(newInstance);
                        roomsTable.setValue(itemId);
                        logBean.writeDeputyLog(currentUser, "New room '" + itemId + "' created.");
                    } else
                    {
                        logBean.writeDeputyLog(currentUser, "Room '" + roomsTable.getValue() + "' updated.");
                    }
                } catch (FieldGroup.CommitException ex)
                {
                    logger.log(Level.SEVERE, null, ex);
                }
                busyCreatingNewItem = false;
            }

            private void setRoom(String direction, TextField roomTextfield, Item item) throws Property.ReadOnlyException
            {
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
                newInstance = new Room();
                newInstance.setOwner(currentUser);
                newInstance.setCreation(new Date());
                item = new BeanItem(newInstance);
                binder = new FieldGroup(item);
                binder.setBuffered(true);
                binder.setEnabled(true);
                binder.setReadOnly(false);
                binder.bind(id, "id");
                binder.bind(title, "title");
                binder.bind(picture, "picture");
                //binder.bind(area, "area");
                binder.bind(contents, "contents");
                // Object itemId = attributes.addEntity(newInstance);
                // roomsTable.setValue(itemId);
            }
        });
        buttonsLayout.addComponent(create);

        Button delete = new Button("Delete", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                attributes.removeItem(roomsTable.getValue());
                logBean.writeDeputyLog(currentUser, "Room '" + roomsTable.getValue() + "' deleted.");
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
        item = roomsTable.getItem(itemId);
        boolean entitySelected = item != null;
        if (entitySelected)
        {
            busyCreatingNewItem = false;
            binder = new FieldGroup(item);
            binder.setBuffered(true);
            binder.setEnabled(true);
            binder.setReadOnly(false);
            binder.bind(id, "id");
            binder.bind(title, "title");
            binder.bind(picture, "picture");
            //binder.bind(area, "area");
            Property northProperty = item.getItemProperty("north");
            if (northProperty != null && northProperty.getValue() != null)
            {
                Room room = (Room) northProperty.getValue();
                north.setPropertyDataSource(new IntegerProperty(room.getId()));
            } else
            {
                north.setPropertyDataSource(new IntegerProperty());
            }
            Property southProperty = item.getItemProperty("south");
            if (southProperty != null && southProperty.getValue() != null)
            {
                Room room = (Room) southProperty.getValue();
                south.setPropertyDataSource(new IntegerProperty(room.getId()));
            } else
            {
                south.setPropertyDataSource(new IntegerProperty());
            }
            Property westProperty = item.getItemProperty("west");
            if (westProperty != null && westProperty.getValue() != null)
            {
                Room room = (Room) westProperty.getValue();
                west.setPropertyDataSource(new IntegerProperty(room.getId()));
            } else
            {
                west.setPropertyDataSource(new IntegerProperty());
            }
            Property eastProperty = item.getItemProperty("east");
            if (eastProperty != null && eastProperty.getValue() != null)
            {
                Room room = (Room) eastProperty.getValue();
                east.setPropertyDataSource(new IntegerProperty(room.getId()));
            } else
            {
                east.setPropertyDataSource(new IntegerProperty());
            }
            Property upProperty = item.getItemProperty("up");
            if (upProperty != null && upProperty.getValue() != null)
            {
                Room room = (Room) upProperty.getValue();
                up.setPropertyDataSource(new IntegerProperty(room.getId()));
            } else
            {
                up.setPropertyDataSource(new IntegerProperty());
            }
            Property downProperty = item.getItemProperty("down");
            if (downProperty != null && downProperty.getValue() != null)
            {
                Room room = (Room) downProperty.getValue();
                down.setPropertyDataSource(new IntegerProperty(room.getId()));
            } else
            {
                down.setPropertyDataSource(new IntegerProperty());
            }
            Property areaProperty = item.getItemProperty("area");
            if (areaProperty != null && areaProperty.getValue() != null)
            {
                area.setValue(((Area) areaProperty.getValue()).getArea());
            } else
            {
                area.setValue(null);
            }
            binder.bind(contents, "contents");
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
