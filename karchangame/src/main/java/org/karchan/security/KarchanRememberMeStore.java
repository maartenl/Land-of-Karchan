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

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.credential.RememberMeCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.RememberMeIdentityStore;
import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;

/**
 * A Store that can deal with a REMEMBERME cookie and spits out a validation result, if the cookie
 * contains a valid JWT.
 *
 * @author maartenl
 */
@ApplicationScoped
public class KarchanRememberMeStore implements RememberMeIdentityStore
{

  private static final Logger LOGGER = Logger.getLogger(KarchanRememberMeStore.class.getName());

  @Inject
  private JsonWebTokenProvider tokenProvider;

  @Override
  public CredentialValidationResult validate(RememberMeCredential rememberMeCredential)
  {
    LOGGER.entering(this.getClass().getName(), "validate", rememberMeCredential.getToken());
    try
    {
      if (tokenProvider.validateToken(rememberMeCredential.getToken()))
      {
        JsonWebTokenCredential credential = tokenProvider.getCredential(rememberMeCredential.getToken());
        LOGGER.log(Level.FINEST, "validated: {0}", credential.getPrincipal());
        return new CredentialValidationResult(credential.getPrincipal(), credential.getAuthorities());
      }
      // if token invalid, response with invalid result status
      LOGGER.log(Level.FINEST, "invalid");
      return INVALID_RESULT;
    } catch (ExpiredJwtException eje)
    {
      LOGGER.log(Level.FINEST, "Security exception for user {0} - {1}", new Object[]
      {
        eje.getClaims().getSubject(), eje.getMessage()
      });
      return INVALID_RESULT;
    }
  }

  @Override
  public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups)
  {
    String token = tokenProvider.createToken(callerPrincipal.getName(), groups);
    LOGGER.finest("created token");
    return token;
  }

  @Override
  public void removeLoginToken(String token)
  {
    // Stateless authentication means at server side we don't maintain the state
  }

}
