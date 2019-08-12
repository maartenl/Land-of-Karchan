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
package awesomeness.vaadin.utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Help;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class HelpEditor extends SimpleEditor
{

    private static final Logger LOGGER = Logger.getLogger(HelpEditor.class.getName());

    private Table table;
    private boolean busyCreatingNewItem;
    private Help newInstance;
    private final Layout mainLayout;
    private final Admin currentUser;
    private final LogBean logBean;

    public HelpEditor(Layout layout, Admin currentUser, LogBean logBean)
    {
        this.mainLayout = layout;
        this.currentUser = currentUser;
        this.logBean = logBean;
    }

    public void buildView()
    {
        final JPAContainer<Help> container = Utilities.getJPAContainer(Help.class);

        table = new Table(null, container);
        Utilities.setTableSize(table);
        table.setVisibleColumns("command", "synopsis", "seealso");
        table.setSelectable(true);
        table.setImmediate(true);
        table.setHeight(215, Sizeable.Unit.PIXELS);
        mainLayout.addComponent(table);

        // BeanItem<Help> item = new BeanItem<>());
        final FieldGroup group = new FieldGroup(table.getItem(container.firstItemId()));

        Panel formPanel = new Panel();
        mainLayout.addComponent(formPanel);
        FormLayout layout = new FormLayout();
        formPanel.setContent(layout);

        Field<?> command = group.buildAndBind("command", "command");
        TextArea contents = group.buildAndBind("contents", "contents", TextArea.class);
        Field<?> synopsis = group.buildAndBind("synopsis", "synopsis");
        Field<?> seealso = group.buildAndBind("seealso", "seealso");
        Field<?> example1 = group.buildAndBind("example1", "example1");
        Field<?> example1a = group.buildAndBind("example1a", "example1a");
        Field<?> example1b = group.buildAndBind("example1b", "example1b");
        Field<?> example2 = group.buildAndBind("example2", "example2");
        Field<?> example2a = group.buildAndBind("example2a", "example2a");
        Field<?> example2b = group.buildAndBind("example2b", "example2b");
        Field<?> example2c = group.buildAndBind("example2c", "example2c");

        contents.setWidth(80, Sizeable.Unit.PERCENTAGE);
        // reason.setWidth(80, Sizeable.Unit.PERCENTAGE);
        command.addValidator(new BeanValidator(Help.class, "command"));
        contents.addValidator(new BeanValidator(Help.class, "contents"));
        synopsis.addValidator(new BeanValidator(Help.class, "synopsis"));
        seealso.addValidator(new BeanValidator(Help.class, "seealso"));
        example1.addValidator(new BeanValidator(Help.class, "example1"));
        example1a.addValidator(new BeanValidator(Help.class, "example1a"));
        example1b.addValidator(new BeanValidator(Help.class, "example1b"));
        example2.addValidator(new BeanValidator(Help.class, "example2"));
        example2a.addValidator(new BeanValidator(Help.class, "example2a"));
        example2b.addValidator(new BeanValidator(Help.class, "example2b"));
        example2c.addValidator(new BeanValidator(Help.class, "example2c"));
        layout.addComponent(command);
        layout.addComponent(contents);
        layout.addComponent(synopsis);
        layout.addComponent(seealso);
        layout.addComponent(example1);
        layout.addComponent(example1a);
        layout.addComponent(example1b);
        layout.addComponent(example2);
        layout.addComponent(example2a);
        layout.addComponent(example2b);
        layout.addComponent(example2c);

        table.addValueChangeListener(new Property.ValueChangeListener()
        {

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                Object itemId = event.getProperty().getValue();
                Item item = table.getItem(itemId);
                boolean entitySelected = item != null;
                if (entitySelected)
                {
                    group.setItemDataSource((item));
                }
            }
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        mainLayout.addComponent(buttonsLayout);
        Button commit = new Button("Save", new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                try
                {
                    group.commit();
                    if (busyCreatingNewItem == true)
                    {
                        Object itemId = container.addEntity(newInstance);
                        table.setValue(itemId);
                        logBean.writeDeputyLog(currentUser, "New help '" + itemId + "' created.");
                    } else
                    {
                        logBean.writeDeputyLog(currentUser, "Help '" + table.getValue() + "' updated.");
                    }
                } catch (FieldGroup.CommitException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
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
                LOGGER.log(Level.FINEST, "discard clicked.");
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
                newInstance = new Help();
                BeanItem beanItem = new BeanItem(newInstance);
                group.setItemDataSource((beanItem));
            }
        });
        buttonsLayout.addComponent(create);

        Button delete = new Button("Delete", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                container.removeItem(table.getValue());
                logBean.writeDeputyLog(currentUser, "Help '" + table.getValue() + "' deleted.");
            }
        });
        buttonsLayout.addComponent(delete);
    }
}
