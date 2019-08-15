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
import java.time.LocalDateTime;

import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Method;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class Scripts extends VerticalLayout implements
        Property.ValueChangeListener
{

    private static final Logger LOGGER = Logger.getLogger(Scripts.class.getName());

    private final CheckBox filterOnOwner;

    private final Table scriptsTable;
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
    private final LogBean logBean;

    Scripts(final Admin currentUser, final LogBean logBean)
    {
        this.currentUser = currentUser;
        this.logBean = logBean;
        final JPAContainer<Method> attributes = Utilities.getJPAContainer(Method.class);

        final Container.Filter filter = new Compare.Equal("owner",
                currentUser);

        Panel searchPanel = new Panel();
        addComponent(searchPanel);

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchPanel.setContent(searchLayout);

        TextField filterOnMethodName = new TextField("Filter on methodname");
        filterOnMethodName.setDescription("Allows wildcards like %.");
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

        scriptsTable = new Table("Methods", attributes);
        scriptsTable.setVisibleColumns("name", "owner", "creation");
        Utilities.setTableSize(scriptsTable);
        scriptsTable.setSelectable(true);
        scriptsTable.addValueChangeListener(this);
        scriptsTable.setImmediate(true);
        tablePanel.setContent(scriptsTable);

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
                LOGGER.log(Level.FINEST, "commit clicked.");
                item.getItemProperty("owner").setValue(currentUser);
                try
                {
                    binder.commit();
                    if (busyCreatingNewItem == true)
                    {

                        Object itemId = attributes.addEntity(newInstance);
                        scriptsTable.setValue(itemId);
                        logBean.writeDeputyLog(currentUser, "New script '" + itemId + "' created.");
                    } else
                    {
                        logBean.writeDeputyLog(currentUser, "Script '" + scriptsTable.getValue() + "' updated.");
                    }
                } catch (FieldGroup.CommitException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
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
                LOGGER.log(Level.FINEST, "discard clicked.");
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
                newInstance.setCreation(LocalDateTime.now());
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
                // scriptsTable.setValue(itemId);
            }
        });
        buttonsLayout.addComponent(create);

        Button delete = new Button("Delete", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                attributes.removeItem(scriptsTable.getValue());
                logBean.writeDeputyLog(currentUser, "Script '" + scriptsTable.getValue() + "' deleted.");
            }
        });
        buttonsLayout.addComponent(delete);

        disown = new Button("Disown", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                LOGGER.log(Level.FINEST, "disown clicked.");
                item.getItemProperty("owner").setValue(null);
//                try
//                {
//                    binder.commit();
//                } catch (FieldGroup.CommitException ex)
//                {
//                    LOGGER.log(Level.SEVERE, null, ex);
//                }
            }
        });
        buttonsLayout.addComponent(disown);
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event)
    {
        Object itemId = event.getProperty().getValue();
        item = scriptsTable.getItem(itemId);
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
