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
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.UnbanTable;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class UnbanEditor extends SimpleEditor
{

    private static final Logger logger = Logger.getLogger(UnbanEditor.class.getName());

    private Table table;
    private boolean busyCreatingNewItem;
    private UnbanTable newInstance;
    private final Layout mainLayout;
    private final Admin currentUser;
    private final LogBean logBean;

    public UnbanEditor(Layout layout, Admin currentUser, LogBean logBean)
    {
        this.mainLayout = layout;
        this.currentUser = currentUser;
        this.logBean = logBean;
    }

    public void buildView()
    {
        final JPAContainer<UnbanTable> container = Utilities.getJPAContainer(UnbanTable.class);

        table = new Table(null, container);
        table.setSizeFull();
        table.setSelectable(true);
        table.setImmediate(true);
        table.setHeight(215, Sizeable.Unit.PIXELS);
        mainLayout.addComponent(table);

        final FieldGroup group = new FieldGroup(table.getItem(container.firstItemId()));

        Panel formPanel = new Panel();
        mainLayout.addComponent(formPanel);
        FormLayout layout = new FormLayout();
        formPanel.setContent(layout);

        Field<?> name = group.buildAndBind("Name", "name");
        name.addValidator(new BeanValidator(UnbanTable.class, "name"));
        layout.addComponent(name);

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
                        logBean.writeDeputyLog(currentUser, "New silly  name '" + itemId + "' created.");
                    } else
                    {
                        logBean.writeDeputyLog(currentUser, "Silly name '" + table.getValue() + "' updated.");
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
                newInstance = new UnbanTable();
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
                logBean.writeDeputyLog(currentUser, "Silly name '" + table.getValue() + "' deleted.");
            }
        });
        buttonsLayout.addComponent(delete);
    }
}
