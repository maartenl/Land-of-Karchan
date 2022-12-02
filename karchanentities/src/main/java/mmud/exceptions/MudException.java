/*
 *  Copyright (C) 2012 maartenl
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

/**
 * The main exception of the mud.
 * This exception is automatically mapped to a nice WebApplicationException, using a CustomExceptionMapper (in karchangame artifact).
 *
 * @author maartenl
 */
public class MudException extends RuntimeException
{

    /**
     * constructor for creating a exception with a message.
     * @param string the string containing the message
     */
    public MudException(String string)
    {
        super(string);
    }

    /**
     * constructor for creating a exception with a message.
     * @param ex the original exception.
     */
    public MudException(Throwable ex)
    {
        super(ex);
    }

    /**
     * constructor for creating a exception with a message.
     * @param aString the string containing the message
     * @param ex the original exception.
     */
    public MudException(String aString, Exception ex)
    {
        super(aString, ex);
    }
}
