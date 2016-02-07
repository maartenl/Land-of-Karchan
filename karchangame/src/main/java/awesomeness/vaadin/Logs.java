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

import awesomeness.vaadin.utils.Utilities;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.logging.Logger;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Commandlog;
import mmud.database.entities.game.Log;
import mmud.database.entities.game.Systemlog;
import mmud.rest.services.LogBean;

/**
 *
 * @author maartenl
 */
public class Logs extends VerticalLayout
{

    private static final Logger logger = Logger.getLogger(Logs.class.getName());

    private final CheckBox filterOnDeputyMessages;

    private final Table logsTable;
    private final Table chatlogsTable;
    private final Table systemlogsTable;

    private Date initialLogDate;

    private final Admin currentUser;
    private final LogBean logBean;

    Logs(final Admin currentUser, final LogBean logBean)
    {
        initialLogDate = Date.from(LocalDate.now().plusYears(10).atStartOfDay().toInstant(ZoneOffset.UTC));

        this.currentUser = currentUser;
        this.logBean = logBean;

        final JPAContainer<Log> logattributes = Utilities.getJPAContainer(Log.class);
        final Container.Filter filter = new Compare.Equal("deputy",
                true);
        final JPAContainer<Commandlog> chatlogattributes = Utilities.getJPAContainer(Commandlog.class);
        final JPAContainer<Systemlog> systemlogattributes = Utilities.getJPAContainer(Systemlog.class);

        Panel logsearchPanel = new Panel("Game Logs");
        addComponent(logsearchPanel);

        HorizontalLayout logsearchLayout = new HorizontalLayout();
        logsearchPanel.setContent(logsearchLayout);

        filterOnDeputyMessages = new CheckBox("Show only dep messages");
        filterOnDeputyMessages.setValue(true);
        filterOnDeputyMessages.addValueChangeListener(new Property.ValueChangeListener()
        {
            {
                logattributes.addContainerFilter(filter);
            }

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                if (event.getProperty().getValue().equals(Boolean.TRUE))
                {
                    logattributes.addContainerFilter(filter);
                } else
                {
                    logattributes.removeContainerFilter(filter);
                }
            }
        });
        logsearchLayout.addComponent(filterOnDeputyMessages);

        // Create a DateField with the default style
        DateField logdateField = new DateField();
        logsearchLayout.addComponent(logdateField);
        logdateField.addValueChangeListener(new Property.ValueChangeListener()
        {

            private Container.Filter logFilter = createFilter("creation", initialLogDate);


            {
                logattributes.addContainerFilter(logFilter);
            }

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                Date thedate = (Date) event.getProperty().getValue();
                logBean.writeDeputyLog(currentUser, "Consulted game log of " + thedate + ".");
                logattributes.removeContainerFilter(logFilter);
                logFilter = createFilter("creation", thedate);
                logattributes.addContainerFilter(logFilter);
            }
        });

        Panel logtablePanel = new Panel();
        addComponent(logtablePanel);

        logsTable = new Table("Game Logs", logattributes);
        logsTable.setVisibleColumns("id", "name", "message", "deputy", "creation");
        Utilities.setTableSize(logsTable);
        logsTable.setSelectable(true);
        logsTable.setImmediate(true);
        logsTable.setSortAscending(false);
        logsTable.setSortContainerPropertyId("creation");
        logsTable.setSortEnabled(false);
        logtablePanel.setContent(logsTable);

        Panel addendumPanel = new Panel();
        addComponent(addendumPanel);

        FormLayout addendumLayout = new FormLayout();
        addendumPanel.setContent(addendumLayout);

        final TextArea contents = new TextArea("Addendum");
        contents.setRows(15);
        contents.setWidth(80, Sizeable.Unit.PERCENTAGE);
        addendumLayout.addComponent(contents);
        logsTable.addValueChangeListener(new Property.ValueChangeListener()
        {

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                Object itemId = event.getProperty().getValue();
                Item item = logsTable.getItem(itemId);
                boolean entitySelected = item != null;
                if (entitySelected && item.getItemProperty("addendum") != null)
                {
                    contents.setValue(item.getItemProperty("addendum").toString());
                } else
                {
                    contents.setValue(null);
                }
            }
        });

        Panel chatlogsearchPanel = new Panel("Chat logs");
        addComponent(chatlogsearchPanel);

        HorizontalLayout chatlogsearchLayout = new HorizontalLayout();
        chatlogsearchPanel.setContent(chatlogsearchLayout);

        // Create a DateField with the default style
        final TextField reason = new TextField("Reason");
        DateField chatlogdateField = new DateField();
        chatlogsearchLayout.addComponent(chatlogdateField);
        chatlogdateField.addValueChangeListener(new Property.ValueChangeListener()
        {

            private Container.Filter logFilter = createFilter("stamp", initialLogDate);


            {
                chatlogattributes.addContainerFilter(logFilter);
            }

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                Date thedate = (Date) event.getProperty().getValue();
                if (reason.getValue() == null || reason.getValue().trim().equals(""))
                {
                    thedate = initialLogDate;
                    logBean.writeDeputyLog(currentUser, "Tried consulting chat log of " + thedate + ", without reason given.");
                } else
                {
                    logBean.writeDeputyLog(currentUser, "Consulted chat log of " + thedate + ".", "Reason given was : " + reason.getValue());
                }
                chatlogattributes.removeContainerFilter(logFilter);
                logFilter = createFilter("stamp", thedate);
                chatlogattributes.addContainerFilter(logFilter);
            }
        });
        reason.setWidth(80, Sizeable.Unit.PERCENTAGE);
        chatlogsearchLayout.addComponent(reason);

        Panel chatlogtablePanel = new Panel();
        addComponent(chatlogtablePanel);

        chatlogsTable = new Table("Chat Logs", chatlogattributes);
        chatlogsTable.setVisibleColumns("id", "name", "command", "stamp");
        Utilities.setTableSize(chatlogsTable);
        chatlogsTable.setSelectable(true);
        chatlogsTable.setImmediate(true);
        chatlogsTable.setSortAscending(false);
        chatlogsTable.setSortContainerPropertyId("stamp");
        chatlogsTable.setSortEnabled(false);
        chatlogtablePanel.setContent(chatlogsTable);

        Panel syslogsearchPanel = new Panel("System logs");
        addComponent(syslogsearchPanel);

        HorizontalLayout syslogsearchLayout = new HorizontalLayout();
        syslogsearchPanel.setContent(syslogsearchLayout);

        // Create a DateField with the default style
        DateField syslogdateField = new DateField();
        syslogsearchLayout.addComponent(syslogdateField);
        syslogdateField.addValueChangeListener(new Property.ValueChangeListener()
        {

            private Container.Filter logFilter = createFilter("millis", initialLogDate);


            {
                systemlogattributes.addContainerFilter(logFilter);
            }

            @Override
            public void valueChange(Property.ValueChangeEvent event)
            {
                Date thedate = (Date) event.getProperty().getValue();
                logBean.writeDeputyLog(currentUser, "Consulted system log of " + thedate + ".");
                systemlogattributes.removeContainerFilter(logFilter);
                logFilter = createFilter("millis", thedate);
                systemlogattributes.addContainerFilter(logFilter);
            }
        });

        Panel systemlogtablePanel = new Panel();
        addComponent(systemlogtablePanel);

        systemlogsTable = new Table("System Logs", systemlogattributes);
        systemlogsTable.setVisibleColumns("id", "message", "millis", "creationdate", "sequence", "logger", "level", "class1", "method", "thread");
        Utilities.setTableSize(systemlogsTable);
        systemlogsTable.setSelectable(true);
        systemlogsTable.setImmediate(true);
        systemlogsTable.setSortAscending(false);
        systemlogsTable.setSortContainerPropertyId("millis");
        systemlogsTable.setSortEnabled(false);
        systemlogtablePanel.setContent(systemlogsTable);

    }

    private Container.Filter createFilter(String dateFieldName, Date logDate2)
    {
        LocalDate logDate = logDate2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Date fromDate = Date.from(logDate.atStartOfDay().toInstant(ZoneOffset.UTC));
        Date toDate = Date.from(logDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));

        return new And(new Compare.GreaterOrEqual(dateFieldName,
                fromDate), new Compare.LessOrEqual(dateFieldName, toDate));
    }

    private Container.Filter createFilter(String dateFieldName, LocalDate logDate)
    {
        Date fromDate = Date.from(logDate.atStartOfDay().toInstant(ZoneOffset.UTC));
        Date toDate = Date.from(logDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));

        return new And(new Compare.GreaterOrEqual(dateFieldName,
                fromDate), new Compare.LessOrEqual(dateFieldName, toDate));
    }

    private Container.Filter createFilter(Date logDate)
    {
        long fromDate = logDate.getTime();
        long toDate = fromDate + (1000 * 60 * 60 * 24);

        return new And(new Compare.GreaterOrEqual("millis",
                fromDate), new Compare.LessOrEqual("millis", toDate));
    }

}