/*-----------------------------------------------------------------------
svninfo: $Id: charactersheets.php 1078 2006-01-15 09:25:36Z maartenl $
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-----------------------------------------------------------------------*/


package mmud.webservices.webentities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * One room. Has XmlRootElement property, for proper formatting
 * into JSON.
 * @author maartenl
 */
@XmlRootElement
public class Room extends MmudObject {

    public Room(int id, String title, String picture, String area, Date creation, String contents, String owner, int west, int east, int north, int south, int up, int down) {
        this.id = id;
        this.title = title;
        this.picture = picture;
        this.area = area;
        this.creation = creation;
        this.contents = contents;
        this.owner = owner;
        this.west = west;
        this.east = east;
        this.north = north;
        this.south = south;
        this.up = up;
        this.down = down;
    }

    public Room() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Room other = (Room) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
        hash = 59 * hash + (this.title != null ? this.title.hashCode() : 0);
        return hash;
    }

    private int id;
    private String title;
    private String picture;
    private String area;
    private Date creation;
    private String contents;
    private String owner;
    private int west;
    private int east;
    private int north;
    private int south;
    private int up;
    private int down;

    /**
     * Get the value of down
     *
     * @return the value of down
     */
    public int getDown() {
        return down;
    }

    /**
     * Set the value of down
     *
     * @param down new value of down
     */
    public void setDown(int down) {
        this.down = down;
    }

    /**
     * Get the value of up
     *
     * @return the value of up
     */
    public int getUp() {
        return up;
    }

    /**
     * Set the value of up
     *
     * @param up new value of up
     */
    public void setUp(int up) {
        this.up = up;
    }

    /**
     * Get the value of south
     *
     * @return the value of south
     */
    public int getSouth() {
        return south;
    }

    /**
     * Set the value of south
     *
     * @param south new value of south
     */
    public void setSouth(int south) {
        this.south = south;
    }

    /**
     * Get the value of north
     *
     * @return the value of north
     */
    public int getNorth() {
        return north;
    }

    /**
     * Set the value of north
     *
     * @param north new value of north
     */
    public void setNorth(int north) {
        this.north = north;
    }

    /**
     * Get the value of east
     *
     * @return the value of east
     */
    public int getEast() {
        return east;
    }

    /**
     * Set the value of east
     *
     * @param east new value of east
     */
    public void setEast(int east) {
        this.east = east;
    }

    /**
     * Get the value of west
     *
     * @return the value of west
     */
    public int getWest() {
        return west;
    }

    /**
     * Set the value of west
     *
     * @param west new value of west
     */
    public void setWest(int west) {
        this.west = west;
    }

    /**
     * Get the value of owner
     *
     * @return the value of owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Set the value of owner
     *
     * @param owner new value of owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get the value of contents
     *
     * @return the value of contents
     */
    public String getContents() {
        return contents;
    }

    /**
     * Set the value of contents
     *
     * @param contents new value of contents
     */
    public void setContents(String contents) {
        this.contents = contents;
    }

    /**
     * Get the value of creation
     *
     * @return the value of creation
     */
    public Date getCreation() {
        return creation;
    }

    /**
     * Set the value of creation
     *
     * @param creation new value of creation
     */
    public void setCreation(Date creation) {
        this.creation = creation;
    }

    /**
     * Get the value of area
     *
     * @return the value of area
     */
    public String getArea() {
        return area;
    }

    /**
     * Set the value of area
     *
     * @param area new value of area
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Get the value of picture
     *
     * @return the value of picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Set the value of picture
     *
     * @param picture new value of picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param title new value of title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "id: " + id;
    }
}
