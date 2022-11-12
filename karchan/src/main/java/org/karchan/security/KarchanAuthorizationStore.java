/*
 * Copyright (C) 2019 maartenl
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
package org.karchan.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

/**
 * Can retrieve the groups a user has access to.
 *
 * @author maartenl
 */
@ApplicationScoped
public class KarchanAuthorizationStore implements IdentityStore
{

  private static final Logger LOGGER = Logger.getLogger(KarchanAuthorizationStore.class.getName());

  @PersistenceUnit
  private EntityManagerFactory entityManagerFactory;

  @Override
  public Set<String> getCallerGroups(CredentialValidationResult validationResult)
  {
    LOGGER.entering(this.getClass().getName(), "getCallerGroups");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    Query query = entityManager.createNativeQuery("select groupid from mmv_groups where name = ?");
    // named parameters are not defined in the JPA spec for Native queries.
    query.setParameter(1, validationResult.getCallerPrincipal().getName());
    Set<String> roles = new HashSet<>();
    query.getResultList().forEach((object) ->
    {
      roles.add(object.toString());
    });

    return roles;
  }

  @Override
  public Set<ValidationType> validationTypes()
  {
    return Collections.singleton(ValidationType.PROVIDE_GROUPS);
  }

}
