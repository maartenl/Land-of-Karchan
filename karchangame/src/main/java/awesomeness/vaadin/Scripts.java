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
import com.vaadin.data.util.filter.Like;
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
import mmud.database.entities.game.Method;

/**
 *
 * @author maartenl
 */
public class Scripts extends VerticalLayout implements
        Property.ValueChangeListener
{

    private static final Logger logger = Logger.getLogger(Scripts.class.getName());

    private final CheckBox filterOnOwner;

    private final Table worldattribTable;
    private final TextField name;
    private final TextArea contents;
    private final Label owner;
    private final Button commit;
    private FieldGroup binder;
    private final Admin currentUser;
    private final Button discard;
    private final Button disown;
    private final FormLayout layout;
    private Item item;
    private boolean busyCreatingNewItem = false;
    private Method newInstance;

    Scripts(final EntityProvider entityProvider, final Admin currentUser)
    {
        this.currentUser = currentUser;
        // And there we have it
        final JPAContainer<Method> attributes
                = new JPAContainer<>(Method.class);
        final Container.Filter filter = new Compare.Equal("owner",
                currentUser);
        attributes.setEntityProvider(entityProvider);

        Panel searchPanel = new Panel();
        addComponent(searchPanel);

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchPanel.setContent(searchLayout);

        TextField filterOnMethodName = new TextField("Filter on methodname");
        filterOnMethodName.setDescription("Allows wildcards.");
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
                String wildcards = (String) event.getProperty().getValue();
                if (wildcards != null && !wildcards.trim().equals(""))
                {
                    filter = new Like("name", wildcards);
                    attributes.addContainerFilter(filter);
                }
            }
        });
        searchLayout.addComponent(filterOnMethodName);

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

        worldattribTable = new Table("Methods", attributes);
        worldattribTable.setVisibleColumns("name", "owner", "creation");
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

        contents = new TextArea("Source");
        contents.setRows(15);
        contents.setWidth(80, Sizeable.Unit.EM);
        layout.addComponent(contents);

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
                newInstance = new Method();
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
                binder.bind(contents, "src");
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
            binder.bind(contents, "src");
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
