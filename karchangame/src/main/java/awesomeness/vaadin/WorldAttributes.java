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
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Worldattribute;

/**
 *
 * @author maartenl
 */
class WorldAttributes extends VerticalLayout implements
        Property.ValueChangeListener, Button.ClickListener
{

    private final Table worldattribTable;
    private final TextField name;
    private final TextArea contents;
    private final TextField typename;
    private final Label owner;
    private final Button commit;
    private FieldGroup binder;
    private final String currentUser;

    WorldAttributes(final EntityProvider entityProvider, final String currentUser)
    {
        this.currentUser = currentUser;
        // And there we have it
        JPAContainer<Worldattribute> attributes
                = new JPAContainer<>(Worldattribute.class);
        attributes.setEntityProvider(entityProvider);

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

        FormLayout layout = new FormLayout();
        formPanel.setContent(layout);

        name = new TextField("Name");
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
        commit = new Button("Save", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                try
                {
                    binder.commit();
                } catch (FieldGroup.CommitException ex)
                {
                    Logger.getLogger(WorldAttributes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        layout.addComponent(commit);

        Button discard = new Button("Cancel", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                binder.discard();
            }
        });
        layout.addComponent(discard);
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event)
    {
        Object itemId = event.getProperty().getValue();
        Item item = worldattribTable.getItem(itemId);
        boolean entitySelected = item != null;
        if (entitySelected)
        {
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
                if (admin.getName().equals(currentUser))
                {
                    enabled = true;
                }
            }
            binder.setEnabled(enabled);
            commit.setEnabled(enabled);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event
    )
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
