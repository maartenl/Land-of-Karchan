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
 * A person could not be found.
 * @author maartenl
 */
public class PersonNotFoundException extends MudException
{

    public PersonNotFoundException()
    {
        super("Person not found.");
    }

    public PersonNotFoundException(String string)
    {
        super(string);
    }

    public PersonNotFoundException(Exception ex)
    {
        super(ex);
    }

    public PersonNotFoundException(String aString, Exception ex)
    {
        super(aString, ex);
    }
}
