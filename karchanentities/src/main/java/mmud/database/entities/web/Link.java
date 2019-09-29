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
import java.time.LocalDateTime;

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
@Table(name = "links")
@NamedQueries(
{
    @NamedQuery(name = "Link.findAll", query = "SELECT l FROM Link l"),
    @NamedQuery(name = "Link.findByLinkname", query = "SELECT l FROM Link l WHERE l.linkname = :linkname"),
    @NamedQuery(name = "Link.findByUrl", query = "SELECT l FROM Link l WHERE l.url = :url"),
    @NamedQuery(name = "Link.findByType", query = "SELECT l FROM Link l WHERE l.type = :type"),
    @NamedQuery(name = "Link.findByCreation", query = "SELECT l FROM Link l WHERE l.creation = :creation"),
    @NamedQuery(name = "Link.findByName", query = "SELECT l FROM Link l WHERE l.name = :name")
})
public class Link implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "linkname")
    private String linkname;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "url")
    private String url;

    @Basic(optional = false)
    @NotNull
    @Column(name = "type")
    private int type;

    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")    
    private LocalDateTime creation;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;

    public Link()
    {
    }

    public Link(String linkname)
    {
        this.linkname = linkname;
    }

    public Link(String linkname, String url, int type, LocalDateTime creation, String name)
    {
        this.linkname = linkname;
        this.url = url;
        this.type = type;
        this.creation = creation;
        this.name = name;
    }

    public String getLinkname()
    {
        return linkname;
    }

    public void setLinkname(String linkname)
    {
        this.linkname = linkname;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public LocalDateTime getCreation()
    {
        return creation;
    }

    public void setCreation(LocalDateTime creation)
    {
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

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (linkname != null ? linkname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Link))
        {
            return false;
        }
        Link other = (Link) object;
        if ((this.linkname == null && other.linkname != null) || (this.linkname != null && !this.linkname.equals(other.linkname)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.web.Link[ linkname=" + linkname + " ]";
    }

}
