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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import mmud.database.entities.web.Users;
import mmud.encryption.Hash;
import mmud.encryption.HexEncoder;
import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static jakarta.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

/**
 * Can locate a user and verify his/her password.
 *
 * @author maartenl
 */
@ApplicationScoped
public class KarchanAuthenticationStore implements IdentityStore
{

  private static final Logger LOGGER = Logger.getLogger(KarchanAuthenticationStore.class.getName());

  @PersistenceUnit
  private EntityManagerFactory entityManagerFactory;

  @VisibleForTesting
  static String encryptThisString(String input)
  {
    return new HexEncoder(128).encrypt(input, Hash.SHA_512);
  }

  @Override
  public CredentialValidationResult validate(Credential credential)
  {
    LOGGER.entering(this.getClass().getName(), "validate");

    if (credential instanceof UsernamePasswordCredential)
    {
      UsernamePasswordCredential userCredential = (UsernamePasswordCredential) credential;

      EntityManager entityManager = entityManagerFactory.createEntityManager();
      TypedQuery<Users> query = entityManager.createNamedQuery("Users.findByNameAndPassword", Users.class);
      query.setParameter("name", userCredential.getCaller());
      query.setParameter("password", encryptThisString(userCredential.getPasswordAsString()));
      List<Users> users = query.getResultList();

      if (users.size() == 1)
      {
        LOGGER.finest("VALID_RESULT");
        return new CredentialValidationResult(userCredential.getCaller());
      } else
      {
        LOGGER.log(Level.FINEST, "INVALID_RESULT");
        return INVALID_RESULT;
      }
    }
    LOGGER.finest("NOT_VALIDATED_RESULT");
    return NOT_VALIDATED_RESULT;
  }

  @Override
  public Set<ValidationType> validationTypes()
  {
    return Collections.singleton(ValidationType.VALIDATE);
  }

}
