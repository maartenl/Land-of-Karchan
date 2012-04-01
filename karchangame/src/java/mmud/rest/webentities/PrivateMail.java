/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmud.rest.webentities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author maartenl
 */
@XmlRootElement
public class PrivateMail {

    public String lok;
    public String toname;
    public String name;
    public String subject;
    public String body;
    public Long id;
    public Boolean haveread;
    public Boolean newmail;
    public Date whensent;
    public Boolean deleted;
    public Integer item_id;

}
