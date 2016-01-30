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

import awesomeness.vaadin.utils.Utilities;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Worldattribute;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
class WorldAttributes extends VerticalLayout implements
        Property.ValueChangeListener
{

    private static final Logger logger = Logger.getLogger(WorldAttributes.class.getName());

    private final CheckBox filterOnOwner;

    private final Table worldattribTable;
    private final TextField name;
    private final TextArea contents;
    private final TextField typename;
    private final Label owner;
    private final Button commit;
    private FieldGroup binder;
    private final Admin currentUser;
    private final Button discard;
    private final Button disown;
    private final FormLayout layout;
    private Item item;
    private boolean busyCreatingNewItem = false;
    private Worldattribute newInstance;
    private final LogBean logBean;

    WorldAttributes(final Admin currentUser, final LogBean logBean)
    {
        this.currentUser = currentUser;
        this.logBean = logBean;
        // And there we have it
        final JPAContainer<Worldattribute> attributes = Utilities.getJPAContainer(Worldattribute.class);

        final Container.Filter filter = new Compare.Equal("owner",
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

        worldattribTable = new Table("Worldattributes", attributes);
        worldattribTable.setVisibleColumns("name", "type", "owner", "creation");
        worldattribTable.setSizeFull();
        worldattribTable.setSelectable(true);
        worldattribTable.addValueChangeListener(this);
        worldattribTable.setImmediate(true);
        tablePanel.setContent(worldattribTable);

        Panel formPanel = new Panel();
        addComponent(formPanel);

        layout = new FormLayout();
        formPanel.setContent(layout);

        name = new TextField("Name");
        name.setWidth(80, Unit.EM);
        layout.addComponent(name);

        contents = new TextArea("Value");
        contents.setRows(15);
        contents.setWidth(80, Sizeable.Unit.EM);
        layout.addComponent(contents);

        typename = new TextField("Type");
        layout.addComponent(typename);

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
                        worldattribTable.setValue(itemId);
                        logBean.writeDeputyLog(currentUser, "New world attribute '" + itemId + "' created.");
                    } else
                    {
                        logBean.writeDeputyLog(currentUser, "World attribute '" + worldattribTable.getValue() + "' updated.");
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
                newInstance = new Worldattribute();
                newInstance.setOwner(currentUser);
                newInstance.setCreation(new Date());
                item = new BeanItem(newInstance);
                binder = new FieldGroup(item);
                binder.setBuffered(true);
                binder.setEnabled(true);
                binder.setReadOnly(false);
                name.setReadOnly(true);
                name.setEnabled(false);
                binder.bind(name, "name");
                binder.bind(contents, "contents");
                binder.bind(typename, "type");
                // Object itemId = attributes.addEntity(newInstance);
                // worldattribTable.setValue(itemId);
            }
        });
        buttonsLayout.addComponent(create);

        Button delete = new Button("Delete", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                attributes.removeItem(worldattribTable.getValue());
                logBean.writeDeputyLog(currentUser, "World attribute '" + worldattribTable.getValue() + "' deleted.");
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
        item = worldattribTable.getItem(itemId);
        boolean entitySelected = item != null;
        if (entitySelected)
        {
            busyCreatingNewItem = false;
            binder = new FieldGroup(item);
            binder.setBuffered(true);
            binder.setEnabled(true);
            binder.setReadOnly(false);
            name.setReadOnly(true);
            name.setEnabled(false);
            binder.bind(name, "name");
            binder.bind(contents, "contents");
            binder.bind(typename, "type");
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
