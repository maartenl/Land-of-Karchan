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
package mmud.database.entities.game;

import mmud.database.entities.items.Item;
import mmud.database.enums.Wielding;
import mmud.exceptions.MudException;

/**
 * Display interface, indicates the things that needs to be shown.
 * @author maartenl
 */
public interface DisplayInterface
{
    public String getMainTitle() throws MudException;

    public String getImage() throws MudException;

    public String getBody() throws MudException;

}
