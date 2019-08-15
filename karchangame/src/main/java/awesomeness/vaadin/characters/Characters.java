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

import awesomeness.vaadin.PersonProvider;
import awesomeness.vaadin.UserInterface;
import awesomeness.vaadin.editor.Buttons;
import awesomeness.vaadin.editor.Editor;
import awesomeness.vaadin.utils.Utilities;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.ComboBox;
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
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class Characters extends Editor
{

  private static final Logger LOGGER = Logger.getLogger(Characters.class.getName());

  private Person newInstance;

  private final Table table;

  private Item item;

  public Characters(PersonProvider entityProvider, final Admin currentUser, final LogBean logBean, UserInterface userInterface)
  {
    super(currentUser, logBean);
    final JPAContainer<Person> container = new JPAContainer<>(Person.class);
    container.setEntityProvider(entityProvider);
    final CharSearchPanel searchPanel = new CharSearchPanel(container, currentUser);
    searchPanel.init();
    addComponent(searchPanel);

    Panel tablePanel = new Panel();
    addComponent(tablePanel);

    table = new Table("Characters", container);
    table.setVisibleColumns("name", "title", "owner");
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

    Field<?> name = group.buildAndBind("name", "name");
    name.setRequired(true);
    Field<?> image = group.buildAndBind("image", "image");
    final Field<?> title = group.buildAndBind("title", "title");
    final Field<?> race = group.buildAndBind("race", "race");
    final ComboBox sex = group.buildAndBind("sex", "sex", ComboBox.class);
    final Field<?> age = group.buildAndBind("age", "age");
    final Field<?> height = group.buildAndBind("height", "height");
    final Field<?> width = group.buildAndBind("width", "width");
    final Field<?> complexion = group.buildAndBind("complexion", "complexion");
    final Field<?> eyes = group.buildAndBind("eyes", "eyes");
    final Field<?> face = group.buildAndBind("face", "face");
    final Field<?> hair = group.buildAndBind("hair", "hair");
    final Field<?> beard = group.buildAndBind("beard", "beard");
    final Field<?> arm = group.buildAndBind("arm", "arm");
    final Field<?> leg = group.buildAndBind("leg", "leg");
    final Field<?> copper = group.buildAndBind("copper", "copper");
    TextArea notes = group.buildAndBind("notes", "notes", TextArea.class);

    // math
    name.addValidator(new BeanValidator(Person.class, "name"));
    image.addValidator(new BeanValidator(Person.class, "image"));
    title.addValidator(new BeanValidator(Person.class, "title"));
    title.setWidth(80, Unit.PERCENTAGE);
    race.addValidator(new BeanValidator(Person.class, "race"));
    // sex.addValidator(new BeanValidator(Person.class, "sex"));
    age.addValidator(new BeanValidator(Person.class, "age"));
    height.addValidator(new BeanValidator(Person.class, "height"));
    width.addValidator(new BeanValidator(Person.class, "width"));
    complexion.addValidator(new BeanValidator(Person.class, "complexion"));
    eyes.addValidator(new BeanValidator(Person.class, "eyes"));
    face.addValidator(new BeanValidator(Person.class, "face"));
    hair.addValidator(new BeanValidator(Person.class, "hair"));
    beard.addValidator(new BeanValidator(Person.class, "beard"));
    arm.addValidator(new BeanValidator(Person.class, "arm"));
    leg.addValidator(new BeanValidator(Person.class, "leg"));
    copper.addValidator(new BeanValidator(Person.class, "copper"));
    notes.addValidator(new BeanValidator(Person.class, "notes"));
    notes.setWidth(80, Unit.PERCENTAGE);

    layout.addComponent(name);
    layout.addComponent(image);
    layout.addComponent(title);
    layout.addComponent(race);
    layout.addComponent(sex);
    layout.addComponent(age);
    layout.addComponent(height);
    layout.addComponent(width);
    layout.addComponent(complexion);
    layout.addComponent(eyes);
    layout.addComponent(face);
    layout.addComponent(hair);
    layout.addComponent(beard);
    layout.addComponent(arm);
    layout.addComponent(leg);
    layout.addComponent(copper);

    layout.addComponent(notes);

    final Label owner = new Label();
    owner.setCaption("Owner");
    layout.addComponent(owner);

    final Buttons buttons = new Buttons(currentUser, logBean, "Character", currentUser.getName().equalsIgnoreCase("Karn"), userInterface)
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
          Logger.getLogger(Characters.class.getName()).log(Level.SEVERE, null, ex);
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
          Logger.getLogger(Characters.class.getName()).log(Level.SEVERE, string);
        }
        // newInstance.setId(Integer.valueOf(id.getValue()));
        Object itemId = container.addEntity(newInstance);
        table.setValue(itemId);
        return itemId;
      }

      @Override
      protected void instantiate()
      {
        newInstance = new User();
        newInstance.setCreation(LocalDateTime.now());
        newInstance.setOwner(currentUser);
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
          buttons.setButtonsEnabled(enabled);
          layout.setEnabled(enabled);
        }
      }
    }
    );

  }

}
