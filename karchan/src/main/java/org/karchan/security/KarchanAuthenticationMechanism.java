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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RememberMe(
        cookieMaxAgeSeconds = 60 * 60 * 24,
        cookieSecureOnly = false        
)
@ApplicationScoped
public class KarchanAuthenticationMechanism implements HttpAuthenticationMechanism
{

  private static final Logger LOGGER = Logger.getLogger(KarchanAuthenticationMechanism.class.getName());

  @Inject
  private IdentityStoreHandler identityStoreHandler;

  @Override
  public AuthenticationStatus validateRequest(HttpServletRequest request,
          HttpServletResponse res,
          HttpMessageContext context)
  {
    LOGGER.log(Level.INFO, "validateRequest: {0}", request.getRequestURI());
    String name = request.getParameter("name");
    String password = request.getParameter("password");
    if (name != null && password != null)
    {
      LOGGER.log(Level.INFO, "credentials : {0}, {1}", new String[]
      {
        name, password
      });
      CredentialValidationResult result = identityStoreHandler.validate(new UsernamePasswordCredential(name, password));
      if (result.getStatus() == CredentialValidationResult.Status.VALID)
      {
        return context.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
      }
      return context.responseUnauthorized();
    } else if (context.isProtected())
    {
      return context.responseUnauthorized();
    }
    return context.doNothing();
  }

}
