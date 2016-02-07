/*
 * Copyright (C) 2016 maartenl
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
package awesomeness.vaadin.editor;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import mmud.database.entities.game.Admin;

/**
 *
 * @author maartenl
 */
public class SearchPanel extends Panel
{

    private final CheckBox filterOnDeputy;

    public SearchPanel(Container.Filterable container, Admin currentUser)
    {
        super("Search panel");
        HorizontalLayout searchLayout = new HorizontalLayout();
        setContent(searchLayout);

        filterOnDeputy = new CheckBox("Filter on owner");
        filterOnDeputy.setValue(false);
        filterOnDeputy.addValueChangeListener(new Property.ValueChangeListener()
        {
            final Container.Filter filter = new Compare.Equal("owner",
                    currentUser);

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                if (event.getProperty().getValue().equals(Boolean.TRUE))
                {
                    container.addContainerFilter(filter);
                } else
                {
                    container.removeContainerFilter(filter);
                }
            }
        });
        searchLayout.addComponent(filterOnDeputy);

    }

}
