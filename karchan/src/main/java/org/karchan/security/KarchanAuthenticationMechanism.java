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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RememberMe(
        cookieMaxAgeSeconds = KarchanAuthenticationMechanism.TWENTYFOUR_HOURS,
        // this should really be set to true, which is the default.
        // this will be removed sometime.
        cookieSecureOnly = false
)
@ApplicationScoped
public class KarchanAuthenticationMechanism implements HttpAuthenticationMechanism
{

  private static final Logger LOGGER = Logger.getLogger(KarchanAuthenticationMechanism.class.getName());
  
  public static final int TWENTYFOUR_HOURS = 60 * 60 * 24;

  @Inject
  private IdentityStoreHandler identityStoreHandler;

  @Override
  public AuthenticationStatus validateRequest(HttpServletRequest request,
          HttpServletResponse response,
          HttpMessageContext context)
  {
    LOGGER.entering(this.getClass().getName(), "validateRequest", request.getRequestURI());
    final String authorization = request.getHeader("Authorization");
    String name = null;
    String password = null;
    if (authorization != null && authorization.toLowerCase().startsWith("basic"))
    {
      // Authorization: Basic base64credentials
      String base64Credentials = authorization.substring("Basic".length()).trim();
      byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
      String credentials = new String(credDecoded, StandardCharsets.UTF_8);
      // credentials = username:password
      final String[] values = credentials.split(":", 2);
      if (values != null && values.length == 2)
      {
        name = values[0];
        password = values[1];
      }
    }
    if (name != null && password != null)
    {
      LOGGER.log(Level.FINEST, "credentials : {0}, {1}", new String[]
      {
        name, password
      });
      CredentialValidationResult result = identityStoreHandler.validate(new UsernamePasswordCredential(name, password));
      if (result.getStatus() == CredentialValidationResult.Status.VALID)
      {
        String karchanname = result.getCallerPrincipal().getName();
        String karchanroles = result.getCallerGroups().stream().collect(Collectors.joining(","));
        Cookie nameCookie = new Cookie("karchanname", karchanname);
        nameCookie.setMaxAge(TWENTYFOUR_HOURS);
        nameCookie.setHttpOnly(false);
        nameCookie.setPath("/");
        response.addCookie(nameCookie);
        Cookie rolesCookie = new Cookie("karchanroles", karchanroles);
        rolesCookie.setMaxAge(TWENTYFOUR_HOURS);
        rolesCookie.setHttpOnly(false);
        rolesCookie.setPath("/");
        response.addCookie(rolesCookie);
        Cookie xsrfCookie = new Cookie("XSRF-TOKEN", (karchanname+":"+karchanroles).hashCode()+"");
        xsrfCookie.setMaxAge(TWENTYFOUR_HOURS);
        xsrfCookie.setHttpOnly(false);
        xsrfCookie.setPath("/");
        response.addCookie(xsrfCookie);
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
