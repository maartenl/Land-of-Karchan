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
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_log")
@NamedQueries(
{
    @NamedQuery(name = "MmudErrorLog.findAll", query = "SELECT m FROM MmudErrorLog m"),
    @NamedQuery(name = "MmudErrorLog.findByCreation", query = "SELECT m FROM MmudErrorLog m WHERE m.mmudErrorLogPK.creation = :creation"),
    @NamedQuery(name = "MmudErrorLog.findByName", query = "SELECT m FROM MmudErrorLog m WHERE m.mmudErrorLogPK.name = :name"),
    @NamedQuery(name = "MmudErrorLog.findByMessage", query = "SELECT m FROM MmudErrorLog m WHERE m.mmudErrorLogPK.message = :message")
})
public class MmudErrorLog implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MmudErrorLogPK mmudErrorLogPK;
    @Lob
    @Size(max = 65535)
    @Column(name = "addendum")
    private String addendum;

    public MmudErrorLog()
    {
    }

    public MmudErrorLog(MmudErrorLogPK mmudErrorLogPK)
    {
        this.mmudErrorLogPK = mmudErrorLogPK;
    }

    public MmudErrorLog(Date creation, String name, String message)
    {
        this.mmudErrorLogPK = new MmudErrorLogPK(creation, name, message);
    }

    public MmudErrorLogPK getMmudErrorLogPK()
    {
        return mmudErrorLogPK;
    }

    public void setMmudErrorLogPK(MmudErrorLogPK mmudErrorLogPK)
    {
        this.mmudErrorLogPK = mmudErrorLogPK;
    }

    public String getAddendum()
    {
        return addendum;
    }

    public void setAddendum(String addendum)
    {
        this.addendum = addendum;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (mmudErrorLogPK != null ? mmudErrorLogPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MmudErrorLog))
        {
            return false;
        }
        MmudErrorLog other = (MmudErrorLog) object;
        if ((this.mmudErrorLogPK == null && other.mmudErrorLogPK != null) || (this.mmudErrorLogPK != null && !this.mmudErrorLogPK.equals(other.mmudErrorLogPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.MmudErrorLog[ mmudErrorLogPK=" + mmudErrorLogPK + " ]";
    }

}
