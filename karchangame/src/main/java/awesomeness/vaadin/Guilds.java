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

import awesomeness.vaadin.editor.Buttons;
import awesomeness.vaadin.editor.Editor;
import awesomeness.vaadin.editor.SearchPanel;
import awesomeness.vaadin.utils.Utilities;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import java.time.LocalDateTime;

import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.Constants;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Guild;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class Guilds extends Editor
{

  private static final Logger LOGGER = Logger.getLogger(Guilds.class.getName());

  private Guild newInstance;

  private final Table table;

  private Item item;

  Guilds(final Admin currentUser, final LogBean logBean, UserInterface userInterface)
  {
    super(currentUser, logBean);
    final JPAContainer<Guild> container = Utilities.getJPAContainer(Guild.class);
    final SearchPanel searchPanel = new SearchPanel(container, currentUser)
    {
      @Override
      protected void addSpecifics()
      {
        // do nothing special.
      }
    };
    searchPanel.init();
    addComponent(searchPanel);

    Panel tablePanel = new Panel();
    addComponent(tablePanel);

    table = new Table("Guilds", container);
    table.setVisibleColumns("name", "title", "guildurl", "imageurl", "colour", "owner");
    Utilities.setTableSize(table);
    table.setSelectable(true);
    table.setImmediate(true);
    table.setSortAscending(false);
    table.setSortContainerPropertyId("name");
    table.setSortEnabled(true);
    tablePanel.setContent(table);

    // BeanItem<BanTable> item = new BeanItem<>());
    final FieldGroup group = new FieldGroup(table.getItem(container.firstItemId()));

    Panel formPanel = new Panel();
    addComponent(formPanel);
    final FormLayout layout = new FormLayout();
    formPanel.setContent(layout);

    final Field<?> name = group.buildAndBind("name", "name");
    name.setRequired(true);
    Field<?> imageurl = group.buildAndBind("imageurl", "imageurl");
    final Field<?> title = group.buildAndBind("title", "title");

    Field<?> colour = group.buildAndBind("colour", "colour");
    TextArea logonmessage = group.buildAndBind("logonmessage", "logonmessage", TextArea.class);
    TextArea guilddescription = group.buildAndBind("guilddescription", "guilddescription", TextArea.class);

    // math
    Field<?> daysguilddeath = group.buildAndBind("daysguilddeath", "daysguilddeath");
    Field<?> maxguilddeath = group.buildAndBind("maxguilddeath", "maxguilddeath");
    Field<?> minguildmembers = group.buildAndBind("minguildmembers", "minguildmembers");
    Field<?> minguildlevel = group.buildAndBind("minguildlevel", "minguildlevel");

    name.addValidator(new BeanValidator(Guild.class, "name"));
    title.addValidator(new BeanValidator(Guild.class, "title"));
    imageurl.addValidator(new BeanValidator(Guild.class, "image"));
    colour.addValidator(new BeanValidator(Guild.class, "colour"));
    logonmessage.addValidator(new BeanValidator(Guild.class, "logonmessage"));
    logonmessage.setWidth(80, Unit.PERCENTAGE);
    guilddescription.addValidator(new BeanValidator(Guild.class, "guilddescription"));
    guilddescription.setWidth(80, Unit.PERCENTAGE);

    daysguilddeath.addValidator(new BeanValidator(Guild.class, "daysguilddeath"));
    maxguilddeath.addValidator(new BeanValidator(Guild.class, "maxguilddeath"));
    minguildmembers.addValidator(new BeanValidator(Guild.class, "minguildmembers"));
    minguildlevel.addValidator(new BeanValidator(Guild.class, "minguildlevel"));

    layout.addComponent(name);
    layout.addComponent(imageurl);
    layout.addComponent(title);
    layout.addComponent(colour);
    layout.addComponent(logonmessage);
    layout.addComponent(guilddescription);

    layout.addComponent(daysguilddeath);
    layout.addComponent(maxguilddeath);
    layout.addComponent(minguildmembers);
    layout.addComponent(minguildlevel);

    final Label owner = new Label();
    owner.setCaption("Owner");
    layout.addComponent(owner);

    final Buttons buttons = new Buttons(currentUser, logBean, "Guild", true, userInterface)
    {

      @Override
      protected Object save()
      {
        item.getItemProperty("owner").setValue(currentUser);
        try
        {
          group.commit();
        } catch (FieldGroup.CommitException ex)
        {
          Logger.getLogger(Guilds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table.getValue();
      }

      @Override
      protected Object create()
      {
        newInstance.setOwner(currentUser);
        String string = Constants.checkValidation(newInstance);
        if (string != null)
        {
          Logger.getLogger(Guilds.class.getName()).log(Level.SEVERE, string);
        }
        Object itemId = container.addEntity(newInstance);
        table.setValue(itemId);
        return itemId;
      }

      @Override
      protected void instantiate()
      {
        newInstance = new Guild();
        newInstance.setCreation(LocalDateTime.now());
        newInstance.setOwner(currentUser);
        newInstance.setDescription("Mandatory description goes here.");
        BeanItem beanItem = new BeanItem(newInstance);
        group.setItemDataSource((beanItem));
        layout.setEnabled(true);
        //deputy.setEnabled(false);
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
          LOGGER.log(Level.SEVERE, null, ex);
        }
        return item.toString();
      }
    };
    addComponent(buttons);

    table.addValueChangeListener(
            new Property.ValueChangeListener()
    {

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
          buttons.setButtonsEnabled(enabled);
          layout.setEnabled(enabled);
        }
      }
    }
    );

  }

}
