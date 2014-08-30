/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmud.rest.webentities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import mmud.database.entities.game.Mail;

/**
 *
 * @author maartenl
 */
@XmlRootElement
public class PrivateMail
{

    public PrivateMail(Mail mail)
    {
        name = mail.getName().getName();
        toname = mail.getToname().getName();
        subject = mail.getSubject();
        body = mail.getBody();
        id = mail.getId();
        haveread = mail.getHaveread();
        newmail = mail.getNewmail();
        whensent = mail.getWhensent();
        deleted = false;
        item_id = mail.getItemDefinition() != null ? mail.getItemDefinition().getId() : null;
    }

    public PrivateMail()
    {
    }

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
