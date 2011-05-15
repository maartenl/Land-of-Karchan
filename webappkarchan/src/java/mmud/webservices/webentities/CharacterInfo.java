/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmud.webservices.webentities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author maartenl
 */
@XmlRootElement
public class CharacterInfo extends MmudObject {

    public CharacterInfo() {
    }

    private String lok;
    private String name;
    private String imageurl;
    private String homepageurl;
    private String dateofbirth;
    private String cityofbirth;
    private String storyline;

    @Override
    public String toString()
    {
        return "CharacterInfo[" + lok + "," +
                name + "," +
                imageurl + "," +
                homepageurl + "," +
                dateofbirth + "," +
                cityofbirth + "," +
                storyline + "]";
    }

    /**
     * @return the lok
     */
    public String getLok() {
        return lok;
    }

    /**
     * @param lok the lok to set
     */
    public void setLok(String lok) {
        this.lok = lok;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the imageurl
     */
    public String getImageurl() {
        return imageurl;
    }

    /**
     * @param imageurl the imageurl to set
     */
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    /**
     * @return the dateofbirth
     */
    public String getDateofbirth() {
        return dateofbirth;
    }

    /**
     * @param dateofbirth the dateofbirth to set
     */
    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    /**
     * @return the cityofbirth
     */
    public String getCityofbirth() {
        return cityofbirth;
    }

    /**
     * @param cityofbirth the cityofbirth to set
     */
    public void setCityofbirth(String cityofbirth) {
        this.cityofbirth = cityofbirth;
    }

    /**
     * @return the storyline
     */
    public String getStoryline() {
        return storyline;
    }

    /**
     * @param storyline the storyline to set
     */
    public void setStoryline(String storyline) {
        this.storyline = storyline;
    }

    /**
     * @return the homepageurl
     */
    public String getHomepageurl() {
        return homepageurl;
    }

    /**
     * @param homepageurl the homepageurl to set
     */
    public void setHomepageurl(String homepageurl) {
        this.homepageurl = homepageurl;
    }


}
