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
package awesomeness.vaadin.characters;

import awesomeness.vaadin.editor.Buttons;
import awesomeness.vaadin.editor.Editor;
import awesomeness.vaadin.editor.SearchPanel;
import awesomeness.vaadin.utils.Utilities;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.rest.services.LogBean;

/**
 * Specific for players. Contains fields not generic, like password and address. You cannot create new players this way.
 *
 * @author maartenl
 */
public class Players extends Editor
{

  private static final Logger logger = Logger.getLogger(Players.class.getName());

  private User newInstance;

  private final Table table;

  private Item item;

  public Players(final Admin currentUser, final LogBean logBean, UI mainWindow)
  {
    super(currentUser, logBean);
    final JPAContainer<mmud.database.entities.characters.User> container = Utilities.getJPAContainer(mmud.database.entities.characters.User.class);

    final SearchPanel searchPanel = new PlayerSearchPanel(container, currentUser);
    searchPanel.init();
    addComponent(searchPanel);

    Panel tablePanel = new Panel();
    addComponent(tablePanel);

    table = new Table("Players", container);
    table.setVisibleColumns("name", "title", "address", "owner");
    table.setColumnWidth("title", 200);
    Utilities.setTableSize(table);
    table.setSelectable(true);
    table.setImmediate(true);
    table.setSortAscending(true);
    table.setSortContainerPropertyId("name");
    table.setSortEnabled(true);
    tablePanel.setContent(table);

    // BeanItem<BanTable> item = new BeanItem<>());
    final FieldGroup group = new FieldGroup(table.getItem(container.firstItemId()));

    Panel formPanel = new Panel();
    addComponent(formPanel);
    final FormLayout layout = new FormLayout();
    formPanel.setContent(layout);

    Field<?> newpassword = group.buildAndBind("Password", "newpassword");

    // math
    newpassword.addValidator(new BeanValidator(User.class, "newpassword"));

    final Label name = new Label();
    name.setCaption("Name");
    layout.addComponent(name);
    final Label address = new Label();
    address.setCaption("Address");
    layout.addComponent(address);
    layout.addComponent(newpassword);
    final Label realname = new Label();
    realname.setCaption("Real name");
    layout.addComponent(realname);
    final Label email = new Label();
    email.setCaption("Email");
    layout.addComponent(email);

    final Label owner = new Label();
    owner.setCaption("Owner");
    layout.addComponent(owner);

    final Buttons buttons = new Buttons(currentUser, logBean, "Player", currentUser.getName().equalsIgnoreCase("Karn"), mainWindow)
    {

      @Override
      protected boolean enableCreate()
      {
        return false;
      }

      @Override
      protected Object save()
      {
        item.getItemProperty("owner").setValue(currentUser);
        try
        {
          group.commit();
        } catch (FieldGroup.CommitException ex)
        {
          Logger.getLogger(Characters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table.getValue();
      }

      @Override
      protected Object create()
      {
        throw new RuntimeException("Not possible to create.");
      }

      @Override
      protected void instantiate()
      {
        throw new RuntimeException("Not possible to instantiate.");
      }

      @Override
      protected void discard()
      {
        group.discard();
      }

      @Override
      protected Object delete()
      {
        final Object value = table.getValue();
        container.removeItem(value);
        return value;
      }

      @Override
      protected String disown()
      {
        item.getItemProperty("owner").setValue(null);
        try
        {
          group.commit();
        } catch (FieldGroup.CommitException ex)
        {
          logger.log(Level.SEVERE, null, ex);
        }
        return item.toString();
      }
    };
    addComponent(buttons);

    table.addValueChangeListener(new Property.ValueChangeListener()
    {

      private void setValue(Label label, Property nameProperty)
      {
        if (nameProperty != null && nameProperty.getValue() != null)
        {
          label.setValue((String) nameProperty.getValue());
        } else
        {
          label.setValue("");
        }
      }

      @Override
      public void valueChange(Property.ValueChangeEvent event
      )
      {
        Object itemId = event.getProperty().getValue();
        item = table.getItem(itemId);
        boolean entitySelected = item != null;
        if (entitySelected)
        {
          group.setItemDataSource((item));
          //deputy.setEnabled(false);
          // id.setValue(item.getItemProperty("id").getValue().toString());
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
          setValue(name, item.getItemProperty("name"));
          setValue(address, item.getItemProperty("address"));
          setValue(realname, item.getItemProperty("realname"));
          setValue(email, item.getItemProperty("email"));

          buttons.setButtonsEnabled(enabled);
          layout.setEnabled(enabled);
        }
      }
    }
    );

  }

}
