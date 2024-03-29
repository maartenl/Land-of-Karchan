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
package mmud.database.entities.characters;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import mmud.database.enums.God;

/**
 * An administrator, which is a simple user with extra rights, in the game.
 * Also called a "deputy".
 *
 * @author maartenl
 */
@Entity
@DiscriminatorValue("1")
public class Administrator extends User
{

    @Column(name = "visible")
    private Boolean visible;

    @Override
    public God getGod()
    {
        return God.GOD;
    }

    /**
     * An administrator, contrary to a user, can indicate if he/she should
     * be visible or invisible.
     *
     * @return
     */
    @Override
    public boolean getVisible()
    {
        if (visible == null)
        {
            visible = true;
            return true;
        }
        return visible;
    }

    /**
     * Sets an administrator visible or invisible.
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
}
