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
@Table(name = "mm_shopkeeperitems", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "ShopkeeperItem.findAll", query = "SELECT s FROM ShopkeeperItem s"),
    @NamedQuery(name = "ShopkeeperItem.findById", query = "SELECT s FROM ShopkeeperItem s WHERE s.shopkeeperItemPK.id = :id"),
    @NamedQuery(name = "ShopkeeperItem.findByMax", query = "SELECT s FROM ShopkeeperItem s WHERE s.max = :max"),
    @NamedQuery(name = "ShopkeeperItem.findByCharname", query = "SELECT s FROM ShopkeeperItem s WHERE s.shopkeeperItemPK.charname = :charname")
})
public class ShopkeeperItem implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ShopkeeperItemPK shopkeeperItemPK;
    @Column(name = "max")
    private Integer max;

    public ShopkeeperItem()
    {
    }

    public ShopkeeperItem(ShopkeeperItemPK shopkeeperItemPK)
    {
        this.shopkeeperItemPK = shopkeeperItemPK;
    }

    public ShopkeeperItem(int id, String charname)
    {
        this.shopkeeperItemPK = new ShopkeeperItemPK(id, charname);
    }

    public ShopkeeperItemPK getShopkeeperItemPK()
    {
        return shopkeeperItemPK;
    }

    public void setShopkeeperItemPK(ShopkeeperItemPK shopkeeperItemPK)
    {
        this.shopkeeperItemPK = shopkeeperItemPK;
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
        hash += (shopkeeperItemPK != null ? shopkeeperItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ShopkeeperItem))
        {
            return false;
        }
        ShopkeeperItem other = (ShopkeeperItem) object;
        if ((this.shopkeeperItemPK == null && other.shopkeeperItemPK != null) || (this.shopkeeperItemPK != null && !this.shopkeeperItemPK.equals(other.shopkeeperItemPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.ShopkeeperItem[ shopkeeperItemPK=" + shopkeeperItemPK + " ]";
    }

}
