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

import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;
import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceProperty;

/**
 *
 * @author maartenl
 */
@TransactionManagement
public abstract class EjbEntityProvider<T> extends MutableLocalEntityProvider<T>
{

  @PersistenceContext(properties =
  {
    @PersistenceProperty(name = "activePersonFilter", value = "0")
  })
  private EntityManager em;

  protected EjbEntityProvider(Class entityClass)
  {
    super(entityClass);
  }

  @PostConstruct
  public void init()
  {
    setTransactionsHandledByProvider(false);
    setEntityManager(em);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  protected void runInTransaction(Runnable operation)
  {
    super.runInTransaction(operation);
  }

}
