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

import java.util.logging.Logger;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @author maartenl
 */
@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception>
{
  private static final Logger LOGGER = Logger.getLogger(CustomExceptionMapper.class.getName());

  @Override
  public Response toResponse(Exception arg0)
  {
    LOGGER.throwing("CustomExceptionMapper", "toResponse", arg0);
    String message = arg0.getMessage();
    if (arg0 instanceof ConstraintViolationException constraintViolationException)
    {
      message = ExceptionUtils.createMessage(constraintViolationException);
    }
    return Response.status(500).entity(
      new ErrorDetails(null, message)
    ).type(MediaType.APPLICATION_JSON).build();
  }

}
