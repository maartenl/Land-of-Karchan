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

import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is here to make certain the Rememberme cookie can be used to verify the user logged in.
 * The actual logging in is done by another war (karchan.war).
 *
 * @author maartenl
 */
@RememberMe(
        cookieMaxAgeSeconds = 60 * 60 * 24,
        // this should really be set to true, which is the default.
        // this will be removed sometime.
        cookieSecureOnly = false
)
@ApplicationScoped
public class KarchanDummyAuthenticationMechanism implements HttpAuthenticationMechanism
{
  private static final Logger LOGGER = Logger.getLogger(KarchanDummyAuthenticationMechanism.class.getName());

  @Override
  public AuthenticationStatus validateRequest(HttpServletRequest request,
          HttpServletResponse res,
          HttpMessageContext context)
  {
    // we only come here if there's no credentials. In which case we do nothing.
    return context.doNothing();
  }

}
