/*
 * Copyright (C) 2017 maartenl
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
package mmud.exceptions;

import javax.ejb.EJBAccessException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * <p>
 * This little class is automatically picked up by JAX-RS and provides a mapping
 * between Exceptions occurring in the application during a REST call, and
 * a HTTP (REST) Response.</p><p>
 * In this case, it maps the EJBAccessException which
 * is thrown when you do not have proper authorization to call methods on an
 * Enterprise Java Bean (EJB) to a 401 HTTP Response (Unauthorized) with an
 * appropriate message.</p>
 *
 * @author maartenl
 */
@Provider
public class EJBAccessExceptionMapper implements
        ExceptionMapper<EJBAccessException>
{

  @Override
  public Response toResponse(EJBAccessException exception)
  {
    return (new ErrorDetails("You are not logged in.")).getResponse(Response.Status.UNAUTHORIZED);
  }

}
