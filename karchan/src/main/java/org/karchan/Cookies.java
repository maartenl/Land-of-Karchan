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
package org.karchan;

import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author maartenl
 */
class Cookies
{

  private final Cookie[] cookies;

  private HttpServletResponse response;

  Cookies(HttpServletRequest request, HttpServletResponse response)
  {
    if (request == null || request.getCookies() == null)
    {
      cookies = new Cookie[0];
    } else
    {
      cookies = request.getCookies();
    }
    this.response = response;
  }

  boolean isEmpty()
  {
    return cookies.length == 0;
  }

  boolean contains(String cookieName)
  {
    return Stream.of(cookies)
            .anyMatch(cookie -> cookie.getName().equals(cookieName));
  }

  boolean remove(String cookieName)
  {
    Optional<Cookie> foundCookie = Stream.of(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findAny();
    foundCookie.ifPresent(cookie ->
    {
      cookie.setMaxAge(0);
      response.addCookie(cookie);
    });

    return foundCookie.isPresent();
  }

  Optional<Cookie> get(String cookieName)
  {
    return Stream.of(cookies)
            .filter(cookie -> cookie.getName().equals(cookieName))
            .findFirst();
  }

}
