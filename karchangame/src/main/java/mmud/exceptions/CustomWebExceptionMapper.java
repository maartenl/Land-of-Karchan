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
package mmud.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author maartenl
 */
@Provider
public class CustomWebExceptionMapper implements ExceptionMapper<MudWebException>
{

  @Override
  public Response toResponse(MudWebException arg0)
  {
    return Response.status(arg0.getStatus()).entity(
            new ErrorDetails(arg0.getName(), arg0.getFriendlyMessage())
            ).type("application/json").build();
    // text/plain
  }

}
