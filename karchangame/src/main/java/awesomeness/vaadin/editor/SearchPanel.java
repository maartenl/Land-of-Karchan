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
 * A standard search panel. Contains only the "filter on owner" bit.
 *
 * @author maartenl
 */
public abstract class SearchPanel extends Panel
{

  private CheckBox filterOnDeputy;
  private final Admin currentUser;
  private final Container.Filterable container;
  private HorizontalLayout searchLayout;

  protected Container.Filterable getContainer()
  {
    return container;
  }

  protected HorizontalLayout getSearchLayout()
  {
    return searchLayout;
  }

  /**
   *
   * @param container The table that can be filtered, based on search settings.
   * @param currentUser the current user (duh)
   */
  public SearchPanel(Container.Filterable container, Admin currentUser)
  {
    super("Search panel");
    this.currentUser = currentUser;
    this.container = container;
  }

  public void init()
  {
    searchLayout = new HorizontalLayout();
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
    addSpecifics();
  }

  protected abstract void addSpecifics();

}
