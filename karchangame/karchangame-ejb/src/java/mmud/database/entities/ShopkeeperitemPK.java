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
package mmud.database.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Embeddable
public class ShopkeeperitemPK implements Serializable
{
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "charname")
    private String charname;

    public ShopkeeperitemPK()
    {
    }

    public ShopkeeperitemPK(int id, String charname)
    {
        this.id = id;
        this.charname = charname;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCharname()
    {
        return charname;
    }

    public void setCharname(String charname)
    {
        this.charname = charname;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (int) id;
        hash += (charname != null ? charname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ShopkeeperitemPK))
        {
            return false;
        }
        ShopkeeperitemPK other = (ShopkeeperitemPK) object;
        if (this.id != other.id)
        {
            return false;
        }
        if ((this.charname == null && other.charname != null) || (this.charname != null && !this.charname.equals(other.charname)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.ShopkeeperitemPK[ id=" + id + ", charname=" + charname + " ]";
    }

}
