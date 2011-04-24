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
public class MmudMail extends MmudObject {

    public MmudMail() {
    }

    public MmudMail(String lok, String toname, String name, String subject, String body) {
        this.lok = lok;
        this.toname = toname;
        this.name = name;
        this.subject = subject;
        this.body = body;
    }

    public MmudMail(String lok, String toname, String name, String subject, String body, Long id, Boolean haveread, Boolean newmail, Date whensent) {
        this.lok = lok;
        this.toname = toname;
        this.name = name;
        this.subject = subject;
        this.body = body;
        this.id = id;
        this.haveread = haveread;
        this.newmail = newmail;
        this.whensent = whensent;
    }

    private String lok;
    private String toname;
    private String name;
    private String subject;
    private String body;
    private Long id;
    private Boolean haveread;
    private Boolean newmail;
    private Date whensent;

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
     * @return the toname
     */
    public String getToname() {
        return toname;
    }

    /**
     * @param toname the toname to set
     */
    public void setToname(String toname) {
        this.toname = toname;
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
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString()
    {
        return name + ":" + toname + ":" + subject + ":" + body;
    }

    /**
     * @return the haveread
     */
    public Boolean getHaveread() {
        return haveread;
    }

    /**
     * @param haveread the haveread to set
     */
    public void setHaveread(Boolean haveread) {
        this.haveread = haveread;
    }

    /**
     * @return the newmail
     */
    public Boolean getNewmail() {
        return newmail;
    }

    /**
     * @param newmail the newmail to set
     */
    public void setNewmail(Boolean newmail) {
        this.newmail = newmail;
    }

    /**
     * @return the whensent
     */
    public Date getWhensent() {
        return whensent;
    }

    /**
     * @param whensent the whensent to set
     */
    public void setWhensent(Date whensent) {
        this.whensent = whensent;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

}
