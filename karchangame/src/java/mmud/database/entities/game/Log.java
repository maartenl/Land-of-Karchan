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
@Table(name = "mm_log", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
    @NamedQuery(name = "Log.findByCreation", query = "SELECT l FROM Log l WHERE l.logPK.creation = :creation"),
    @NamedQuery(name = "Log.findByName", query = "SELECT l FROM Log l WHERE l.logPK.name = :name"),
    @NamedQuery(name = "Log.findByMessage", query = "SELECT l FROM Log l WHERE l.logPK.message = :message")
})
public class Log implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected LogPK logPK;
    @Lob
    @Size(max = 65535)
    @Column(name = "addendum")
    private String addendum;

    public Log()
    {
    }

    public Log(LogPK logPK)
    {
        this.logPK = logPK;
    }

    public Log(Date creation, String name, String message)
    {
        this.logPK = new LogPK(creation, name, message);
    }

    public LogPK getLogPK()
    {
        return logPK;
    }

    public void setLogPK(LogPK logPK)
    {
        this.logPK = logPK;
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
        hash += (logPK != null ? logPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Log))
        {
            return false;
        }
        Log other = (Log) object;
        if ((this.logPK == null && other.logPK != null) || (this.logPK != null && !this.logPK.equals(other.logPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Log[ logPK=" + logPK + " ]";
    }

}
