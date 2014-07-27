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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import mmud.database.entities.characters.Person;

/**
 * An event, defined to execute at a certain time.
 * The time can be defined in the following fields:
 * <ul><li>month</li>
 * <li>dayofmonth</li>
 * <li>dayofweek</li>
 * <li>hour</li>
 * <li>minute</li>
 * </ul>
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_events")
@NamedQueries(
        {
            @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
            @NamedQuery(name = "Event.findByEventid", query = "SELECT e FROM Event e WHERE e.eventid = :eventid"),
            @NamedQuery(name = "Event.list", query = "SELECT e "
                    + "FROM Event e "
                    + "WHERE e.callable = 1 "
                    + "AND (e.month is null OR e.month = :month) "
                    + "AND (e.dayofmonth is null OR e.dayofmonth = :dayofmonth) "
                    + "AND (e.hour is null OR e.hour = :hour) "
                    + "AND (e.dayofweek is null OR e.dayofweek = :dayofweek) "
                    + "AND (e.minute is null OR e.minute = :minute)")
        })
public class Event implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "eventid")
    private Integer eventid;
    @JoinColumn(name = "name", nullable = true, referencedColumnName = "name")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Person person;
    @Basic(optional = false)
    @Column(name = "month")
    private Integer month;
    @Basic(optional = false)
    @Column(name = "dayofmonth")
    private Integer dayofmonth;
    @Basic(optional = false)
    @Column(name = "hour")
    private Integer hour;
    @Basic(optional = false)
    @Column(name = "minute")
    private Integer minute;
    @Basic(optional = false)
    @Column(name = "dayofweek")
    private Integer dayofweek;
    @Basic(optional = false)
    @NotNull
    @Column(name = "callable")
    private int callable;
    @JoinColumn(name = "room", nullable = true, referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Room room;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;
    @JoinColumn(name = "method_name", referencedColumnName = "name")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Method method;

    public Event()
    {
    }

    public Event(Integer eventid)
    {
        this.eventid = eventid;
    }

    public Event(Integer eventid, Integer month, Integer dayofmonth, Integer hour, Integer minute, Integer dayofweek, boolean callable, Date creation)
    {
        this.eventid = eventid;
        this.month = month;
        this.dayofmonth = dayofmonth;
        this.hour = hour;
        this.minute = minute;
        this.dayofweek = dayofweek;
        setCallable(callable);
        this.creation = creation;
    }

    public Integer getEventid()
    {
        return eventid;
    }

    public void setEventid(Integer eventid)
    {
        this.eventid = eventid;
    }

    public Person getPerson()
    {
        return person;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }

    /**
     * Returns the month in which this event should be run. Be warned
     * JANUARY is month 0, the first month. December is month 11, the last month.
     * If it returns null, it means every month is matched.
     *
     * @return integer
     */
    public Integer getMonth()
    {
        return month;
    }

    public void setMonth(Integer month)
    {
        this.month = month;
    }

    /**
     * Returns the day of the month, starting with 1.
     * If it returns null, it means every day of the month is matched.
     *
     * @return integer
     */
    public Integer getDayofmonth()
    {
        return dayofmonth;
    }

    public void setDayofmonth(Integer dayofmonth)
    {
        this.dayofmonth = dayofmonth;
    }

    /**
     * Returns the hour in which this event should be run. It's a 24 hour
     * clock used here. First hour right after midnight is 0.
     * If it returns null, it means every hour is matched.
     *
     * @return integer
     */
    public Integer getHour()
    {
        return hour;
    }

    public void setHour(Integer hour)
    {
        this.hour = hour;
    }

    /**
     * Returns the minute in which this event should be run. Any value between
     * 0 and 59 would be okay.
     * If it returns null, it means every minute is matched.
     *
     * @return integer
     */
    public Integer getMinute()
    {
        return minute;
    }

    public void setMinute(Integer minute)
    {
        this.minute = minute;
    }

    /**
     * Returns the day of the week at which this event should be run. SUNDAY
     * being 1 and SATURDAY being 7.
     * If it returns null, it means every day of the week is matched.
     *
     * @return integer
     */
    public Integer getDayofweek()
    {
        return dayofweek;
    }

    public void setDayofweek(Integer dayofweek)
    {
        this.dayofweek = dayofweek;
    }

    /**
     * Returns if this event is 'active'.
     *
     * @return boolean, true if active, false otherwise.
     */
    public boolean getCallable()
    {
        return callable == 1;
    }

    public void setCallable(Boolean callable)
    {
        this.callable = Boolean.TRUE.equals(callable) ? 1 : 0;
    }

    public Room getRoom()
    {
        return room;
    }

    public void setRoom(Room room)
    {
        this.room = room;
    }

    public Date getCreation()
    {
        return creation;
    }

    public void setCreation(Date creation)
    {
        this.creation = creation;
    }

    public Admin getOwner()
    {
        return owner;
    }

    public void setOwner(Admin owner)
    {
        this.owner = owner;
    }

    public Method getMethod()
    {
        return method;
    }

    public void setMethod(Method method)
    {
        this.method = method;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (eventid != null ? eventid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event))
        {
            return false;
        }
        Event other = (Event) object;
        if ((this.eventid == null && other.eventid != null) || (this.eventid != null && !this.eventid.equals(other.eventid)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Event[ eventid=" + eventid + " ]";
    }
}
