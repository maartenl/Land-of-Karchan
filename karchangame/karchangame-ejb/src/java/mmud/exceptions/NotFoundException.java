/*
 * Copyright (C) 2011 maartenl
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
 *
 * @author maartenl
 */
public class NotFoundException extends MmudException
{

    /**
     * Creates a new instance of <code>NotFoundException</code> without detail message.
     */
    public NotFoundException()
    {
    }

    /**
     * Constructs an instance of <code>NotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NotFoundException(String msg)
    {
        super(msg);
    }
}
