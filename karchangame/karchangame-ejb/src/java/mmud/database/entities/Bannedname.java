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
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_bannednamestable")
@NamedQueries(
{
    @NamedQuery(name = Bannedname.CHECKFORBANNEDNAME, query = "SELECT b FROM Bannedname b WHERE b.name = :name")
})
public class Bannedname implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String CHECKFORBANNEDNAME = "Bannedname.findByName";

    @Id 
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @Size(max = 20)
    @Column(name = "deputy")
    private String deputy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @Column(name = "days")
    private Integer days;
    @Size(max = 255)
    @Column(name = "reason")
    private String reason;

    public Bannedname()
    {
    }

    public Bannedname(String name)
    {
        this.name = name;
    }

    public Bannedname(String name, Date creation)
    {
        this.name = name;
        this.creation = creation;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDeputy()
    {
        return deputy;
    }

    public void setDeputy(String deputy)
    {
        this.deputy = deputy;
    }

    public Date getCreation()
    {
        return creation;
    }

    public void setCreation(Date creation)
    {
        this.creation = creation;
    }

    public Integer getDays()
    {
        return days;
    }

    public void setDays(Integer days)
    {
        this.days = days;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bannedname))
        {
            return false;
        }
        Bannedname other = (Bannedname) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.Bannedname[ name=" + name + " ]";
    }

}
