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

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import mmud.database.entities.game.Guild;
import mmud.database.enums.God;

/**
 * A mob in the game.
 *
 * @author maartenl
 */
@Entity
@DiscriminatorValue("3")
public class Mob extends Person
{

    @JoinColumn(name = "guild", referencedColumnName = "name")
    @ManyToOne(fetch = FetchType.LAZY)
    private Guild guild;

    @Override
    public Boolean getFightable()
    {
        return true;
    }

    /**
     * Mobs can fight. Any entry is ignored.
     *
     * @param fightable
     */
    @Override
    public void setFightable(Boolean fightable)
    {
        super.setFightable(true);
    }

    @Override
    public boolean canReceive()
    {
        return false;
    }

    @Override
    public God getGod()
    {
        return God.MOB;
    }

}
