/*
 * Copyright (C) 2014 maartenl
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import mmud.Constants;

/**
 * Object that is sent to the client as a json string, upon throwing
 * a MudWebException.
 *
 * @see MudWebException
 * @author maartenl
 */
@XmlRootElement
public class ErrorDetails
{

  public LocalDateTime timestamp;
  public String errormessage;
  public String user;

  public ErrorDetails()
  {
    // required for REST
  }

  ErrorDetails(String errormessage)
  {
    this.timestamp = LocalDateTime.now();
    this.errormessage = errormessage;
  }

  ErrorDetails(String user, String errormessage)
  {
    this(errormessage);
    this.user = user;
  }

  ErrorDetails(Throwable t)
  {
    this.timestamp = LocalDateTime.now();
    this.errormessage = t.getMessage();
  }

  ErrorDetails(String user, Throwable t)
  {
    this(t);
    this.user = user;
  }

  ErrorDetails(String user, String message, Throwable t)
  {
    this(t);
    this.user = user;
    this.errormessage = message;
  }

  public Response getResponse(Response.Status status)
  {
    this.errormessage += " Report the problem to us at " + Constants.DEPUTIES_EMAILADDRESS + ".";
    return Response.status(status).entity(this).build();
  }
}
