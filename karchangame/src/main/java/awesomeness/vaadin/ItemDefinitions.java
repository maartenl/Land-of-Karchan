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

import awesomeness.vaadin.utils.Editor;
import awesomeness.vaadin.utils.Utilities;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private final CheckBox filterOnDeputy;

    private ItemDefinition newInstance;

    private boolean busyCreatingNewItem;

    private final Table table;

    private Item item;

    ItemDefinitions(final Admin currentUser, final LogBean logBean)
    {
        super(currentUser, logBean);
        final JPAContainer<ItemDefinition> container = Utilities.getJPAContainer(ItemDefinition.class);

        // final JPAContainer<Person> container = Utilities.getJPAContainerSpecial(Person.class);
        final Container.Filter filter = new Compare.Equal("owner",
                currentUser);

        Panel searchPanel = new Panel("Search panel");
        addComponent(searchPanel);
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchPanel.setContent(searchLayout);

        filterOnDeputy = new CheckBox("Filter on owner");
        filterOnDeputy.setValue(false);
        filterOnDeputy.addValueChangeListener(new Property.ValueChangeListener()
        {

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

        Panel tablePanel = new Panel();
        addComponent(tablePanel);

        table = new Table("Item definitions", container);
        table.setVisibleColumns("id", "name", "adject1", "adject2", "adject3", "image", "discriminator", "owner");
        table.setSizeFull();
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

        final Field<?> id = group.buildAndBind("Id", "id");
        Field<?> name = group.buildAndBind("name", "name");
        Field<?> adject1 = group.buildAndBind("adject1", "adject1");
        Field<?> adject2 = group.buildAndBind("adject2", "adject2");
        Field<?> adject3 = group.buildAndBind("adject3", "adject3");
        id.setEnabled(false);
        name.addValidator(new BeanValidator(ItemDefinition.class, "name"));
        adject1.addValidator(new BeanValidator(ItemDefinition.class, "adject1"));
        adject2.addValidator(new BeanValidator(ItemDefinition.class, "adject2"));
        adject3.addValidator(new BeanValidator(ItemDefinition.class, "adject3"));
        layout.addComponent(id);
        layout.addComponent(name);
        layout.addComponent(adject1);
        layout.addComponent(adject2);
        layout.addComponent(adject3);

        final Label owner = new Label();
        owner.setCaption("Owner");
        layout.addComponent(owner);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        addComponent(buttonsLayout);
        final Button commit = new Button("Save", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                try
                {
                    item.getItemProperty("owner").setValue(currentUser);
                    group.commit();
                    if (busyCreatingNewItem == true)
                    {
                        Object itemId = container.addEntity(newInstance);
                        table.setValue(itemId);
                        logBean.writeDeputyLog(currentUser, "New item definition '" + itemId + "' created.");
                    } else
                    {
                        logBean.writeDeputyLog(currentUser, "Item definition '" + table.getValue() + "' updated.");
                    }
                } catch (FieldGroup.CommitException ex)
                {
                    logger.log(Level.SEVERE, null, ex);
                }
                busyCreatingNewItem = false;
            }

        });
        buttonsLayout.addComponent(commit);

        Button discard = new Button("Cancel", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                logger.log(Level.FINEST, "discard clicked.");
                group.discard();
            }
        });
        buttonsLayout.addComponent(discard);

        Button create = new Button("Create", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                busyCreatingNewItem = true;
                newInstance = new ItemDefinition();
                newInstance.setOwner(currentUser);
                BeanItem beanItem = new BeanItem(newInstance);
                group.setItemDataSource((beanItem));
                id.setEnabled(false);
                //deputy.setEnabled(false);
            }
        });
        buttonsLayout.addComponent(create);

        final Button delete = new Button("Delete", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                container.removeItem(table.getValue());
                logBean.writeDeputyLog(currentUser, "Item definition '" + table.getValue() + "' deleted.");
            }
        });
        buttonsLayout.addComponent(delete);

        final Button disown = new Button("Disown", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                logger.log(Level.FINEST, "disown clicked.");
                item.getItemProperty("owner").setValue(null);
//                try
//                {
//                    binder.commit();
//                } catch (FieldGroup.CommitException ex)
//                {
//                    logger.log(Level.SEVERE, null, ex);
//                }
            }
        });
        buttonsLayout.addComponent(disown);
        table.addValueChangeListener(new Property.ValueChangeListener()
        {

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                Object itemId = event.getProperty().getValue();
                item = table.getItem(itemId);
                boolean entitySelected = item != null;
                if (entitySelected)
                {
                    group.setItemDataSource((item));
                    id.setEnabled(false);
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
                    disown.setEnabled(enabled);
                    delete.setEnabled(enabled);
                    commit.setEnabled(enabled);
                    layout.setEnabled(enabled);
                }
            }
        });

    }

}
