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
@Table(name = "mm_bantable")
@NamedQueries(
{
    @NamedQuery(name = "Bantable.findAll", query = "SELECT b FROM Bantable b"),
    @NamedQuery(name = "Bantable.findByAddress", query = "SELECT b FROM Bantable b WHERE b.address = :address"),
    @NamedQuery(name = "Bantable.findByDays", query = "SELECT b FROM Bantable b WHERE b.days = :days"),
    @NamedQuery(name = "Bantable.findByIp", query = "SELECT b FROM Bantable b WHERE b.ip = :ip"),
    @NamedQuery(name = "Bantable.findByName", query = "SELECT b FROM Bantable b WHERE b.name = :name"),
    @NamedQuery(name = "Bantable.findByDeputy", query = "SELECT b FROM Bantable b WHERE b.deputy = :deputy"),
    @NamedQuery(name = "Bantable.findByDate", query = "SELECT b FROM Bantable b WHERE b.date = :date"),
    @NamedQuery(name = "Bantable.findByReason", query = "SELECT b FROM Bantable b WHERE b.reason = :reason")
})
public class Bantable implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "address")
    private String address;
    @Column(name = "days")
    private Integer days;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "IP")
    private String ip;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "deputy")
    private String deputy;
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "reason")
    private String reason;

    public Bantable()
    {
    }

    public Bantable(String address)
    {
        this.address = address;
    }

    public Bantable(String address, String ip, String name, String deputy, String reason)
    {
        this.address = address;
        this.ip = ip;
        this.name = name;
        this.deputy = deputy;
        this.reason = reason;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Integer getDays()
    {
        return days;
    }

    public void setDays(Integer days)
    {
        this.days = days;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
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

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
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
        hash += (address != null ? address.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bantable))
        {
            return false;
        }
        Bantable other = (Bantable) object;
        if ((this.address == null && other.address != null) || (this.address != null && !this.address.equals(other.address)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.Bantable[ address=" + address + " ]";
    }

}
