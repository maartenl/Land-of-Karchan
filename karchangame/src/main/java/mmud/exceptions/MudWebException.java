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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author maartenl
 */
public class MudWebException extends WebApplicationException
{

    /**
     * Create a new MudWebException.
     *
     * @param name the name of the player.
     * @param message the message to send to the player.
     * @param status the HTTP status code.
     */
    public MudWebException(String name, String message, Response.Status status)
    {
        super(message, (new ErrorDetails(name, message)).getResponse(status));
    }

    /**
     * Create a new MudWebException.
     *
     * @param name the name of the player.
     * @param message the message to send to the player.
     * @param status the HTTP status code.
     * @param errormessage the error message to log in GlassFish (which can provide additional information too sensitive for the user).
     */
    public MudWebException(String name, String message, String errormessage, Response.Status status)
    {
        super(errormessage, (new ErrorDetails(name, message)).getResponse(status));
    }

    /**
     * Create a new MudWebException caused by a different exception.
     *
     * @param name the name of the player.
     * @param message the message to send to the player.
     * @param status the HTTP status code.
     * @param e the underlying exception that was thrown.
     */
    public MudWebException(String name, String message, Throwable e, Response.Status status)
    {
        super(e, (new ErrorDetails(name, message, e)).getResponse(status));
    }

    /**
     * Create a new MudWebException caused by a different exception.
     *
     * @param name the name of the player.
     * @param status the HTTP status code.
     * @param e the underlying exception that was thrown.
     */
    public MudWebException(String name, Throwable e, Response.Status status)
    {
        super(e, (new ErrorDetails(name, e)).getResponse(status));
    }

    /**
     * Create a new MudWebException caused by a different exception.
     *
     * @param status the HTTP status code.
     * @param e the underlying exception that was thrown.
     */
    public MudWebException(Throwable e, Response.Status status)
    {
        super(e, (new ErrorDetails(e)).getResponse(status));
    }
}