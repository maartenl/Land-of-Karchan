/*
 * Copyright (C) 2016 maartenl
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_systemlog")
@NamedQueries(
        {
            @NamedQuery(name = "Systemlog.findAll", query = "SELECT s FROM Systemlog s")
        })
public class Systemlog implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "millis")
    private long millis;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sequence")
    private long sequence;
    @Size(max = 255)
    @Column(name = "logger")
    private String logger;
    @Size(max = 25)
    @Column(name = "level")
    private String level;
    @Size(max = 255)
    @Column(name = "class")
    private String class1;
    @Size(max = 255)
    @Column(name = "method")
    private String method;
    @Column(name = "thread")
    private Integer thread;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "message")
    private String message;
    @Transient
    private LocalDateTime creationdate;

    public Systemlog()
    {
    }

    public Systemlog(Long id)
    {
        this.id = id;
    }

    public Systemlog(Long id, long millis, long sequence, String message)
    {
        this.id = id;
        this.millis = millis;
        this.sequence = sequence;
        this.message = message;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public long getMillis()
    {
        return millis;
    }

    public void setMillis(long millis)
    {
        this.millis = millis;
        creationdate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public long getSequence()
    {
        return sequence;
    }

    public void setSequence(long sequence)
    {
        this.sequence = sequence;
    }

    public String getLogger()
    {
        return logger;
    }

    public void setLogger(String logger)
    {
        this.logger = logger;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getClass1()
    {
        return class1;
    }

    public void setClass1(String class1)
    {
        this.class1 = class1;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public Integer getThread()
    {
        return thread;
    }

    public void setThread(Integer thread)
    {
        this.thread = thread;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public LocalDateTime getCreationdate()
    {
        creationdate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();        
        return creationdate;
    }

    public void setCreationdate(LocalDateTime creationdate)
    {
        millis = creationdate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.creationdate = creationdate;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Systemlog))
        {
            return false;
        }
        Systemlog other = (Systemlog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Systemlog[ id=" + id + " ]";
    }

}
