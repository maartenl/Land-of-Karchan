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
package awesomeness.vaadin;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.BanTable;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class Banishment extends VerticalLayout
{

    private static final Logger logger = Logger.getLogger(Banishment.class.getName());

    private Table table;
    private final LogBean logBean;
    private final Admin currentUser;
    private boolean busyCreatingNewItem;
    private BanTable newInstance;

    protected Table getTable()
    {
        return table;
    }

    public void setVisibleTableProperties(Object... tablePropertyIds)
    {
        table.setVisibleColumns(tablePropertyIds);
    }

    public Banishment(final Admin currentUser, final LogBean logBean)
    {
        this.logBean = logBean;
        this.currentUser = currentUser;
    }

    public void init()
    {
        buildView();
    }

    protected void buildView()
    {
        final JPAContainer<BanTable> container = JPAContainerFactory.makeJndi(BanTable.class);

        // final JPAContainer<BanTable> container = JPAContainerFactory.make(BanTable.class, entityManager);
        table = new Table(null, container);
        table.setSizeFull();
        table.setSelectable(true);
        //table.addListener(this);
        table.setImmediate(true);

        //table.addActionHandler(this);
        addComponent(table);

        // BeanItem<BanTable> item = new BeanItem<>());
        final FieldGroup group = new FieldGroup(table.getItem(container.firstItemId()));

        Panel formPanel = new Panel();
        addComponent(formPanel);
        FormLayout layout = new FormLayout();
        formPanel.setContent(layout);

        Field<?> address = group.buildAndBind("Address", "address");
        Field<?> days = group.buildAndBind("days", "days");
        Field<?> ip = group.buildAndBind("ip", "ip");
        Field<?> name = group.buildAndBind("name", "name");
        final Field<?> deputy = group.buildAndBind("deputy", "deputy");
        final Field<?> date = group.buildAndBind("date", "date");
        date.setEnabled(false);
        deputy.setEnabled(false);
        Field<?> reason = group.buildAndBind("reason", "reason");
        address.addValidator(new BeanValidator(BanTable.class, "address"));
        days.addValidator(new BeanValidator(BanTable.class, "days"));
        ip.addValidator(new BeanValidator(BanTable.class, "ip"));
        name.addValidator(new BeanValidator(BanTable.class, "name"));
        deputy.addValidator(new BeanValidator(BanTable.class, "deputy"));
        date.addValidator(new BeanValidator(BanTable.class, "date"));
        reason.addValidator(new BeanValidator(BanTable.class, "reason"));
        layout.addComponent(address);
        layout.addComponent(days);
        layout.addComponent(ip);
        layout.addComponent(name);
        layout.addComponent(deputy);
        layout.addComponent(date);
        layout.addComponent(reason);

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
                    date.setEnabled(false);
                    deputy.setEnabled(false);
                }
            }
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        addComponent(buttonsLayout);
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
                        logBean.writeDeputyLog(currentUser, "New ban '" + itemId + "' created.");
                    } else
                    {
                        logBean.writeDeputyLog(currentUser, "Ban '" + table.getValue() + "' updated.");
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
                newInstance = new BanTable();
                newInstance.setDeputy(currentUser.getName());
                newInstance.setDate(new Date());
                BeanItem beanItem = new BeanItem(newInstance);
                group.setItemDataSource((beanItem));
                date.setEnabled(false);
                deputy.setEnabled(false);
            }
        });
        buttonsLayout.addComponent(create);

        Button delete = new Button("Delete", new Button.ClickListener()
        {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                container.removeItem(table.getValue());
                logBean.writeDeputyLog(currentUser, "Ban '" + table.getValue() + "' deleted.");
            }
        });
        buttonsLayout.addComponent(delete);

    }
//
//    banishment.setVisibleFormProperties (
//"address", "days", "ip", "name", "reason");

}
