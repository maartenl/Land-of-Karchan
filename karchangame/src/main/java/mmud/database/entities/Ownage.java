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
package mmud.database.entities;

import mmud.database.entities.game.Admin;

/**
 * Indicates that an object in the game has an owner. An owner is, in this
 * context, an administrator allowed to make changed to the object.
 *
 * @author maartenl
 */
public interface Ownage
{

    /**
     * Retrieves the owner, the administrator, of this object.
     * In case this is null, anyone can edit the object.
     *
     * @return
     */
    public Admin getOwner();

    /**
     * Sets the owner, the administrator, of this object.
     *
     * @param owner the owner/administrator of the object. In case this is null,
     * which is allowed, any administrator can edit this object. This is what happens
     * when an object is "disowned".
     */
    public void setOwner(Admin owner);

}
