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
package mmud.database.entities.web;

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
@Table(name = "family", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "Family.findAll", query = "SELECT f FROM Family f"),
    @NamedQuery(name = "Family.findByName", query = "SELECT f FROM Family f WHERE f.familyPK.name = :name"),
    @NamedQuery(name = "Family.findByToname", query = "SELECT f FROM Family f WHERE f.familyPK.toname = :toname"),
    @NamedQuery(name = "Family.findByDescription", query = "SELECT f FROM Family f WHERE f.description = :description")
})
public class Family implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FamilyPK familyPK;
    @Column(name = "description")
    private Integer description;

    public Family()
    {
    }

    public Family(FamilyPK familyPK)
    {
        this.familyPK = familyPK;
    }

    public Family(String name, String toname)
    {
        this.familyPK = new FamilyPK(name, toname);
    }

    public FamilyPK getFamilyPK()
    {
        return familyPK;
    }

    public void setFamilyPK(FamilyPK familyPK)
    {
        this.familyPK = familyPK;
    }

    public Integer getDescription()
    {
        return description;
    }

    public void setDescription(Integer description)
    {
        this.description = description;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (familyPK != null ? familyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Family))
        {
            return false;
        }
        Family other = (Family) object;
        if ((this.familyPK == null && other.familyPK != null) || (this.familyPK != null && !this.familyPK.equals(other.familyPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.web.Family[ familyPK=" + familyPK + " ]";
    }

}
