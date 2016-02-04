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
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Table;
import javax.persistence.EntityManager;
import mmud.Constants;
import mmud.database.enums.Filter;

/**
 *
 * @author maartenl
 */
public class Utilities
{

    private Utilities()
    {
    }

    /**
     * Creates a {@link JPAContainer} via JNDI.
     *
     * @param <T> the entity to retrieve the JPAContainer for.
     * @param clazz the Class of the entity.
     * @return a JPAContainer if all is well.
     */
    public static <T> JPAContainer<T> getJPAContainer(Class<T> clazz)
    {
        final JPAContainer<T> container = JPAContainerFactory.makeJndi(clazz);
        EntityManager entityManager = container.getEntityProvider().getEntityManager();
        Constants.setFilters(entityManager, Filter.OFF);
        return container;
    }

    public static void setTableSize(Table table)
    {
        table.setWidth(95, Sizeable.Unit.PERCENTAGE);
        table.setHeight(100, Sizeable.Unit.PERCENTAGE);
    }

}
