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
package awesomeness.vaadin.rooms;

import awesomeness.vaadin.editor.SearchPanel;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.TextField;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Room;

/**
 *
 * @author maartenl
 */
class RoomSearchPanel extends SearchPanel
{

  public RoomSearchPanel(Container.Filterable container, Admin currentUser)
  {
    super(container, currentUser);
  }

  @Override
  protected void addSpecifics()
  {
    TextField filterOnRoomNr = new TextField();
    filterOnRoomNr.addBlurListener(new FieldEvents.BlurListener()
    {
      private Container.Filter filterByRoomnr;

      @Override
      public void blur(FieldEvents.BlurEvent event)
      {
        String value = filterOnRoomNr.getValue();
        Long roomNumber = null;
        if (value != null && !value.trim().equals(""))
        {
          try
          {
            roomNumber = Long.decode(value);
          } catch (NumberFormatException e)
          {
            // do nothing here, roomnumber stays at null
          }
        }
        getContainer().removeContainerFilter(filterByRoomnr);
        if (roomNumber != null)
        {
          filterByRoomnr = new Compare.Equal("id", roomNumber);
          getContainer().addContainerFilter(filterByRoomnr);
        }
      }
    });
    getSearchLayout().addComponent(filterOnRoomNr);
  }

}
