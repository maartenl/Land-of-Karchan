/*
 * Copyright (C) 2017 maartenl
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
package awesomeness.vaadin.characters;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.TextField;
import mmud.database.entities.game.Admin;

/**
 *
 * @author maartenl
 */
public class PlayerSearchPanel extends CharSearchPanel
{

  public PlayerSearchPanel(Container.Filterable container, Admin currentUser)
  {
    super(container, currentUser);
  }

  @Override
  protected void addSpecifics()
  {
    super.addSpecifics();
    TextField filterOnAddress = new TextField();
    filterOnAddress.setDescription("Address");
    filterOnAddress.addBlurListener(new FieldEvents.BlurListener()
    {
      private Container.Filter filterByAddress;

      @Override
      public void blur(FieldEvents.BlurEvent event)
      {
        String value = filterOnAddress.getValue();
        getContainer().removeContainerFilter(filterByAddress);
        if (value != null && !value.trim().equals(""))
        {
          filterByAddress = new Like("address", value);
          getContainer().addContainerFilter(filterByAddress);
        }
      }
    });
    getSearchLayout().addComponent(filterOnAddress);
  }
}
