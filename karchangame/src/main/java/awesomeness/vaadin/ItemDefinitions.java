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
import com.vaadin.ui.TextField;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.Constants;
import mmud.database.entities.game.Admin;
import mmud.database.entities.items.ItemDefinition;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class ItemDefinitions extends Editor
{

    private static final Logger logger = Logger.getLogger(ItemDefinitions.class.getName());

    private ItemDefinition newInstance;

    private final Table table;

    private Item item;

    ItemDefinitions(final Admin currentUser, final LogBean logBean)
    {
        super(currentUser, logBean);
        final JPAContainer<ItemDefinition> container = Utilities.getJPAContainer(ItemDefinition.class);

        addComponent(new SearchPanel(container, currentUser));

        Panel tablePanel = new Panel();
        addComponent(tablePanel);

        table = new Table("Item definitions", container);
        table.setVisibleColumns("id", "name", "adject1", "adject2", "adject3", "image", "discriminator", "owner");
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

        final TextField id = new TextField("id"); // group.buildAndBind("id", "id");
        id.setRequired(true);
        Field<?> image = group.buildAndBind("image", "image");
        final Field<?> title = group.buildAndBind("title", "title");
        final Field<?> name = group.buildAndBind("name", "name");
        name.setRequired(true);
        Field<?> adject1 = group.buildAndBind("adject1", "adject1");
        Field<?> adject2 = group.buildAndBind("adject2", "adject2");
        Field<?> adject3 = group.buildAndBind("adject3", "adject3");
        TextArea description = group.buildAndBind("description", "description", TextArea.class);
        description.setRequired(true);
        TextArea eatable = group.buildAndBind("eatable", "eatable", TextArea.class);
        TextArea drinkable = group.buildAndBind("drinkable", "drinkable", TextArea.class);
        TextArea readdescription = group.buildAndBind("readdescription", "readdescription", TextArea.class);
        TextArea notes = group.buildAndBind("notes", "notes", TextArea.class);

        // math
        Field<?> manaincrease = group.buildAndBind("manaincrease", "manaincrease");
        Field<?> hitincrease = group.buildAndBind("hitincrease", "hitincrease");
        Field<?> vitalincrease = group.buildAndBind("vitalincrease", "vitalincrease");
        Field<?> movementincrease = group.buildAndBind("movementincrease", "movementincrease");
        Field<?> copper = group.buildAndBind("copper", "copper");
        Field<?> weight = group.buildAndBind("weight", "weight");
        Field<?> pasdefense = group.buildAndBind("pasdefense", "pasdefense");
        Field<?> damageresistance = group.buildAndBind("damageresistance", "damageresistance");
        Field<?> capacity = group.buildAndBind("capacity", "capacity");
        Field<?> lightable = group.buildAndBind("lightable", "lightable");

        id.addValidator(new BeanValidator(ItemDefinition.class, "id"));
        title.addValidator(new BeanValidator(ItemDefinition.class, "title"));
        name.addValidator(new BeanValidator(ItemDefinition.class, "name"));
        image.addValidator(new BeanValidator(ItemDefinition.class, "image"));
        adject1.addValidator(new BeanValidator(ItemDefinition.class, "adject1"));
        adject2.addValidator(new BeanValidator(ItemDefinition.class, "adject2"));
        adject3.addValidator(new BeanValidator(ItemDefinition.class, "adject3"));
        description.addValidator(new BeanValidator(ItemDefinition.class, "description"));
        description.setWidth(80, Unit.PERCENTAGE);
        eatable.addValidator(new BeanValidator(ItemDefinition.class, "eatable"));
        eatable.setWidth(80, Unit.PERCENTAGE);
        drinkable.addValidator(new BeanValidator(ItemDefinition.class, "drinkable"));
        drinkable.setWidth(80, Unit.PERCENTAGE);
        readdescription.addValidator(new BeanValidator(ItemDefinition.class, "readdescription"));
        readdescription.setWidth(80, Unit.PERCENTAGE);
        notes.addValidator(new BeanValidator(ItemDefinition.class, "notes"));
        notes.setWidth(80, Unit.PERCENTAGE);

        manaincrease.addValidator(new BeanValidator(ItemDefinition.class, "manaincrease"));
        hitincrease.addValidator(new BeanValidator(ItemDefinition.class, "hitincrease"));
        vitalincrease.addValidator(new BeanValidator(ItemDefinition.class, "vitalincrease"));
        movementincrease.addValidator(new BeanValidator(ItemDefinition.class, "movementincrease"));
        copper.addValidator(new BeanValidator(ItemDefinition.class, "copper"));
        weight.addValidator(new BeanValidator(ItemDefinition.class, "weight"));
        pasdefense.addValidator(new BeanValidator(ItemDefinition.class, "pasdefense"));
        damageresistance.addValidator(new BeanValidator(ItemDefinition.class, "damageresistance"));
        capacity.addValidator(new BeanValidator(ItemDefinition.class, "capacity"));
        lightable.addValidator(new BeanValidator(ItemDefinition.class, "lightable"));

        layout.addComponent(id);
        layout.addComponent(image);
        layout.addComponent(title);
        layout.addComponent(name);
        layout.addComponent(adject1);
        layout.addComponent(adject2);
        layout.addComponent(adject3);
        layout.addComponent(description);
        layout.addComponent(eatable);
        layout.addComponent(drinkable);
        layout.addComponent(readdescription);

        layout.addComponent(manaincrease);
        layout.addComponent(hitincrease);
        layout.addComponent(vitalincrease);
        layout.addComponent(movementincrease);
        layout.addComponent(copper);
        layout.addComponent(weight);
        layout.addComponent(pasdefense);
        layout.addComponent(damageresistance);
        layout.addComponent(capacity);
        layout.addComponent(lightable);

        layout.addComponent(notes);

        final Label owner = new Label();
        owner.setCaption("Owner");
        layout.addComponent(owner);

        final Buttons buttons = new Buttons(currentUser, logBean, "Item definition")
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
                    Logger.getLogger(ItemDefinitions.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(ItemDefinitions.class.getName()).log(Level.SEVERE, string);
                }
                newInstance.setId(Integer.valueOf(id.getValue()));
                Object itemId = container.addEntity(newInstance);
                table.setValue(itemId);
                return itemId;
            }

            @Override
            protected void instantiate()
            {
                newInstance = new ItemDefinition();
                id.setValue("");
                newInstance.setCreation(new Date());
                newInstance.setDiscriminator(0);
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
                    logger.log(Level.SEVERE, null, ex);
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
                            id.setValue(item.getItemProperty("id").getValue().toString());
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
