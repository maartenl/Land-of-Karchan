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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_roomattributes", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "Roomattribute.findAll", query = "SELECT r FROM Roomattribute r"),
    @NamedQuery(name = "Roomattribute.findByName", query = "SELECT r FROM Roomattribute r WHERE r.roomattributePK.name = :name"),
    @NamedQuery(name = "Roomattribute.findByValueType", query = "SELECT r FROM Roomattribute r WHERE r.valueType = :valueType"),
    @NamedQuery(name = "Roomattribute.findById", query = "SELECT r FROM Roomattribute r WHERE r.roomattributePK.id = :id")
})
public class Roomattribute implements Serializable, Attribute
{

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RoomattributePK roomattributePK;
    @Lob
    @Size(max = 65535)
    @Column(name = "value")
    private String value;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "value_type")
    private String valueType;
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Room room;

    public Roomattribute()
    {
    }

    public Roomattribute(RoomattributePK roomattributePK)
    {
        this.roomattributePK = roomattributePK;
    }

    public Roomattribute(RoomattributePK roomattributePK, String valueType)
    {
        this.roomattributePK = roomattributePK;
        this.valueType = valueType;
    }

    /**
     * Create a new room attribute, based on the name
     * of the attribute, and the room.
     * @param name the name of the attribute
     * @param id the id of the room.
     */
    public Roomattribute(String name, int id)
    {
        this.roomattributePK = new RoomattributePK(name, id);
    }

    public RoomattributePK getRoomattributePK()
    {
        return roomattributePK;
    }

    public void setRoomattributePK(RoomattributePK roomattributePK)
    {
        this.roomattributePK = roomattributePK;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValueType()
    {
        return valueType;
    }

    public void setValueType(String valueType)
    {
        this.valueType = valueType;
    }

    public Room getRoom()
    {
        return room;
    }

    public void setRoom(Room room)
    {
        this.room = room;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (roomattributePK != null ? roomattributePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Roomattribute))
        {
            return false;
        }
        Roomattribute other = (Roomattribute) object;
        if ((this.roomattributePK == null && other.roomattributePK != null) || (this.roomattributePK != null && !this.roomattributePK.equals(other.roomattributePK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Roomattribute[ roomattributePK=" + roomattributePK + " ]";
    }

    @Override
    public String getName()
    {
        return roomattributePK.getName();
    }
}
