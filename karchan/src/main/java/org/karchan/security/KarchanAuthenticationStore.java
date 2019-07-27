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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;
import javax.security.enterprise.identitystore.IdentityStore;

/**
 * Can locate a user and verify his/her password.
 * @author maartenl
 */
@ApplicationScoped
public class KarchanAuthenticationStore implements IdentityStore
{

  private static final Logger LOGGER = Logger.getLogger(KarchanAuthenticationStore.class.getName());

  private final Map<String, String> callerToPassword;

  public KarchanAuthenticationStore()
  {
    callerToPassword
            = new HashMap<>();
    callerToPassword.put("Karn", "secret");
    callerToPassword.put("Marvin", "secret");
  }

  @Override
  public CredentialValidationResult validate(Credential credential)
  {
    LOGGER.info("validate");

    if (credential instanceof UsernamePasswordCredential)
    {
      UsernamePasswordCredential userCredential = (UsernamePasswordCredential) credential;
      String expectedPW = callerToPassword.get(userCredential.getCaller());
      LOGGER.info("instanceof " + userCredential.getCaller() + ":" + userCredential.getPasswordAsString());
      if (expectedPW != null && expectedPW.equals(userCredential.getPasswordAsString()))
      {
        LOGGER.info("VALID!");
        return new CredentialValidationResult(userCredential.getCaller());
      } else
      {
        LOGGER.info("INVALID_RESULT");
        return INVALID_RESULT;
      }
    }
    LOGGER.info("NOT_VALIDATED_RESULT");
    return NOT_VALIDATED_RESULT;
  }

  @Override
  public Set<ValidationType> validationTypes()
  {
    return Collections.singleton(ValidationType.VALIDATE);
  }

}
