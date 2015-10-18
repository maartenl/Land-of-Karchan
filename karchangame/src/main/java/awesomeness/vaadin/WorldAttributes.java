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
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Table;
import mmud.database.entities.game.Worldattribute;

/**
 *
 * @author maartenl
 */
class WorldAttributes extends FormLayout implements
        Property.ValueChangeListener, Button.ClickListener
{

    private final Table worldattribTable;
    private final FieldGroup form;

    WorldAttributes(EntityProvider entityProvider)
    {

        // And there we have it
        JPAContainer<Worldattribute> attributes
                = new JPAContainer<>(Worldattribute.class);
        attributes.setEntityProvider(entityProvider);

        worldattribTable = new Table("Worldattributes", attributes);
        worldattribTable.setVisibleColumns("name", "type", "owner", "creation");
        worldattribTable.setSizeFull();
        worldattribTable.setSelectable(true);
        worldattribTable.addValueChangeListener(this);
        worldattribTable.setImmediate(true);
        addComponent(worldattribTable);

        form = new FieldGroup();
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event)
    {
        Object itemId = event.getProperty().getValue();
        Item item = worldattribTable.getItem(itemId);
        boolean entitySelected = item != null;
        if (entitySelected)
        {
            System.out.println(item);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
