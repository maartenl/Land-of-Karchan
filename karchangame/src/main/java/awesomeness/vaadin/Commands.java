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
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
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
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Method;
import mmud.database.entities.game.Room;
import mmud.database.entities.game.UserCommand;

/**
 *
 * @author maartenl
 */
public class Commands extends VerticalLayout implements
        Property.ValueChangeListener
{

    private static final Logger logger = Logger.getLogger(Commands.class.getName());

    private final CheckBox filterOnOwner;

    private final Table commandsTable;
    private final Label owner;
    private final Button commit;
    private FieldGroup binder;
    private final Admin currentUser;
    private final Button discard;
    private final Button disown;
    private final FormLayout layout;
    private Item item;
    private boolean busyCreatingNewItem = false;
    private mmud.database.entities.game.UserCommand newInstance;
    private final TextField id;
    private final TextField command;
    private final TextField room;
    private final TextField methodName;
    private final CheckBox callable;

    Commands(final EntityProvider entityProvider, final Admin currentUser)
    {
        this.currentUser = currentUser;
        // And there we have it
        final JPAContainer<UserCommand> attributes
                = new JPAContainer<>(UserCommand.class);
        final Filter filter = new Compare.Equal("owner",
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

        commandsTable = new Table("Commands", attributes);
        commandsTable.setVisibleColumns("id", "command", "methodName", "callable", "room", "owner", "creation");
        commandsTable.setSizeFull();
        commandsTable.setSelectable(true);
        commandsTable.addValueChangeListener(this);
        commandsTable.setImmediate(true);
        tablePanel.setContent(commandsTable);

        Panel formPanel = new Panel();
        addComponent(formPanel);

        layout = new FormLayout();
        formPanel.setContent(layout);

        id = new TextField("Id");
        id.setReadOnly(true);
        id.setEnabled(false);
        layout.addComponent(id);

        command = new TextField("Command");
        command.setWidth(50, Sizeable.Unit.EM);
        layout.addComponent(command);

        room = new TextField("Room");
        layout.addComponent(room);

        methodName = new TextField("methodName");
        methodName.setWidth(50, Sizeable.Unit.EM);
        layout.addComponent(methodName);

        callable = new CheckBox("Callable");
        callable.setDescription("checked if the method can be called, unchecked if the method has been deactivated.");
        layout.addComponent(callable);

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
                String method = (String) methodName.getValue();
                Query methodQuery = entityProvider.getEntityManager().createNamedQuery("Method.findByName");
                methodQuery.setParameter("name", method);
                Method foundMethod = (Method) methodQuery.getSingleResult();
                item.getItemProperty("methodName").setValue(foundMethod);
                setRoom("room", room, item);

                try
                {
                    binder.commit();
                    if (busyCreatingNewItem == true)
                    {

                        Object itemId = attributes.addEntity(newInstance);
                        commandsTable.setValue(itemId);
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
                    Query roomQuery = entityProvider.getEntityManager().createNamedQuery("Room.findById");
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
                newInstance = new UserCommand();
                newInstance.setOwner(currentUser);
                newInstance.setCreation(new Date());
                item = new BeanItem(newInstance);
                binder = new FieldGroup(item);
                binder.setBuffered(true);
                binder.setEnabled(true);
                binder.setReadOnly(false);
                binder.bind(id, "id");
                binder.bind(command, "command");
                binder.bind(callable, "callable");
                // Object itemId = attributes.addEntity(newInstance);
                // commandsTable.setValue(itemId);
            }
        });
        buttonsLayout.addComponent(create);

        Button delete = new Button("Delete", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                attributes.removeItem(commandsTable.getValue());
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
        item = commandsTable.getItem(itemId);
        boolean entitySelected = item != null;
        if (entitySelected)
        {
            busyCreatingNewItem = false;
            binder = new FieldGroup(item);
            binder.setBuffered(true);
            binder.setEnabled(true);
            binder.setReadOnly(false);
            binder.bind(id, "id");
            binder.bind(command, "command");
            binder.bind(callable, "callable");
            Property roomProperty = item.getItemProperty("room");
            if (roomProperty != null && roomProperty.getValue() != null)
            {
                Room roomroom = (Room) roomProperty.getValue();
                room.setPropertyDataSource(new IntegerProperty(roomroom.getId()));
            } else
            {
                room.setPropertyDataSource(new IntegerProperty());
            }
            Property methodProperty = item.getItemProperty("methodName");
            if (methodProperty != null && methodProperty.getValue() != null)
            {
                methodName.setValue(((Method) methodProperty.getValue()).getName());
            } else
            {
                methodName.setValue(null);
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
