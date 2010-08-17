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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * One character. Has XmlRootElement property, for proper formatting
 * into JSON.
 * @author maartenl
 */
@XmlRootElement
public class Character extends MmudObject {

    public Character(String name, String password, String address, String title, String realname, String email, String race, String sex, String age, String length, String width, String complexion, String eyes, String face, String hair, String beard, String arm, String leg, String notes, String owner) 
    throws Exception
    {
        this.name = name;
        this.password = password;
        this.address = address;
        this.title = title;
        this.realname = realname;
        this.email = email;
        this.race = race;
        setSex(sex);
        this.age = age;
        this.length = length;
        this.width = width;
        this.complexion = complexion;
        this.eyes = eyes;
        this.face = face;
        this.hair = hair;
        this.beard = beard;
        this.arm = arm;
        this.leg = leg;
        this.notes = notes;
        this.owner = owner;
    }

    public Character() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Character other = (Character) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    private String name;
    private String password;
    private String address;
    private String title;
    private String realname;
    private String email;
    private String race;
    private String sex;
    private String age;
    private String length;
    private String width;
    private String complexion;
    private String eyes;
    private String face;
    private String hair;
    private String beard;
    private String arm;
    private String leg;
    private String notes;
    private String owner;

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
     * Get the value of notes
     *
     * @return the value of notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Set the value of notes
     *
     * @param notes new value of notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Get the value of leg
     *
     * @return the value of leg
     */
    public String getLeg() {
        return leg;
    }

    /**
     * Set the value of leg
     *
     * @param leg new value of leg
     */
    public void setLeg(String leg) {
        this.leg = leg;
    }

    /**
     * Get the value of arm
     *
     * @return the value of arm
     */
    public String getArm() {
        return arm;
    }

    /**
     * Set the value of arm
     *
     * @param arm new value of arm
     */
    public void setArm(String arm) {
        this.arm = arm;
    }

    /**
     * Get the value of beard
     *
     * @return the value of beard
     */
    public String getBeard() {
        return beard;
    }

    /**
     * Set the value of beard
     *
     * @param beard new value of beard
     */
    public void setBeard(String beard) {
        this.beard = beard;
    }

    /**
     * Get the value of hair
     *
     * @return the value of hair
     */
    public String getHair() {
        return hair;
    }

    /**
     * Set the value of hair
     *
     * @param hair new value of hair
     */
    public void setHair(String hair) {
        this.hair = hair;
    }

    /**
     * Get the value of face
     *
     * @return the value of face
     */
    public String getFace() {
        return face;
    }

    /**
     * Set the value of face
     *
     * @param face new value of face
     */
    public void setFace(String face) {
        this.face = face;
    }

    /**
     * Get the value of eyes
     *
     * @return the value of eyes
     */
    public String getEyes() {
        return eyes;
    }

    /**
     * Set the value of eyes
     *
     * @param eyes new value of eyes
     */
    public void setEyes(String eyes) {
        this.eyes = eyes;
    }

    /**
     * Get the value of complexion
     *
     * @return the value of complexion
     */
    public String getComplexion() {
        return complexion;
    }

    /**
     * Set the value of complexion
     *
     * @param complexion new value of complexion
     */
    public void setComplexion(String complexion) {
        this.complexion = complexion;
    }

    /**
     * Get the value of width
     *
     * @return the value of width
     */
    public String getWidth() {
        return width;
    }

    /**
     * Set the value of width
     *
     * @param width new value of width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Get the value of length
     *
     * @return the value of length
     */
    public String getLength() {
        return length;
    }

    /**
     * Set the value of length
     *
     * @param length new value of length
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * Get the value of age
     *
     * @return the value of age
     */
    public String getAge() {
        return age;
    }

    /**
     * Set the value of age
     *
     * @param age new value of age
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * Get the value of sex
     *
     * @return the value of sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * Set the value of sex
     *
     * @param sex new value of sex
     */
    public void setSex(String sex)
    throws Exception
    {
        if ("male".equalsIgnoreCase(sex))
        {
            this.sex = "male";
            return;
        }
        if ("female".equalsIgnoreCase(sex))
        {
            this.sex = "female";
            return;
        }
        throw new Exception("sex is undefined.");
    }

    /**
     * Get the value of race
     *
     * @return the value of race
     */
    public String getRace() {
        return race;
    }

    /**
     * Set the value of race
     *
     * @param race new value of race
     */
    public void setRace(String race) {
        this.race = race;
    }

    /**
     * Get the value of email
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the value of email
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the value of realname
     *
     * @return the value of realname
     */
    public String getRealname() {
        return realname;
    }

    /**
     * Set the value of realname
     *
     * @param realname new value of realname
     */
    public void setRealname(String realname) {
        this.realname = realname;
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
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the value of address
     *
     * @return the value of address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the value of address
     *
     * @param address new value of address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    @XmlElement
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
