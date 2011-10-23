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
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_shopkeeperitems")
@NamedQueries(
{
    @NamedQuery(name = "Shopkeeperitem.findAll", query = "SELECT s FROM Shopkeeperitem s"),
    @NamedQuery(name = "Shopkeeperitem.findById", query = "SELECT s FROM Shopkeeperitem s WHERE s.shopkeeperitemPK.id = :id"),
    @NamedQuery(name = "Shopkeeperitem.findByMax", query = "SELECT s FROM Shopkeeperitem s WHERE s.max = :max"),
    @NamedQuery(name = "Shopkeeperitem.findByCharname", query = "SELECT s FROM Shopkeeperitem s WHERE s.shopkeeperitemPK.charname = :charname")
})
public class Shopkeeperitem implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ShopkeeperitemPK shopkeeperitemPK;
    @Column(name = "max")
    private Integer max;

    public Shopkeeperitem()
    {
    }

    public Shopkeeperitem(ShopkeeperitemPK shopkeeperitemPK)
    {
        this.shopkeeperitemPK = shopkeeperitemPK;
    }

    public Shopkeeperitem(int id, String charname)
    {
        this.shopkeeperitemPK = new ShopkeeperitemPK(id, charname);
    }

    public ShopkeeperitemPK getShopkeeperitemPK()
    {
        return shopkeeperitemPK;
    }

    public void setShopkeeperitemPK(ShopkeeperitemPK shopkeeperitemPK)
    {
        this.shopkeeperitemPK = shopkeeperitemPK;
    }

    public Integer getMax()
    {
        return max;
    }

    public void setMax(Integer max)
    {
        this.max = max;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (shopkeeperitemPK != null ? shopkeeperitemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Shopkeeperitem))
        {
            return false;
        }
        Shopkeeperitem other = (Shopkeeperitem) object;
        if ((this.shopkeeperitemPK == null && other.shopkeeperitemPK != null) || (this.shopkeeperitemPK != null && !this.shopkeeperitemPK.equals(other.shopkeeperitemPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.Shopkeeperitem[ shopkeeperitemPK=" + shopkeeperitemPK + " ]";
    }

}
