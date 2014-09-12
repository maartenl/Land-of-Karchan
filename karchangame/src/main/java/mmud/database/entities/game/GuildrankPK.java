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

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The composite primary key for a Guildrank.
 *
 * @see Guildrank
 * @author maartenl
 */
@Embeddable
public class GuildrankPK implements Serializable
{

    @Basic(optional = false)
    @NotNull
    @Column(name = "guildlevel")
    private int guildlevel;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "guildname")
    private String guildname;

    public GuildrankPK()
    {
    }

    public GuildrankPK(int guildlevel, String guildname)
    {
        this.guildlevel = guildlevel;
        this.guildname = guildname;
    }

    public int getGuildlevel()
    {
        return guildlevel;
    }

    public void setGuildlevel(int guildlevel)
    {
        this.guildlevel = guildlevel;
    }

    public String getGuildname()
    {
        return guildname;
    }

    public void setGuildname(String guildname)
    {
        this.guildname = guildname;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (int) guildlevel;
        hash += (guildname != null ? guildname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GuildrankPK))
        {
            return false;
        }
        GuildrankPK other = (GuildrankPK) object;
        if (this.guildlevel != other.guildlevel)
        {
            return false;
        }
        if ((this.guildname == null && other.guildname != null) || (this.guildname != null && !this.guildname.equals(other.guildname)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.GuildrankPK[ guildlevel=" + guildlevel + ", guildname=" + guildname + " ]";
    }

}
