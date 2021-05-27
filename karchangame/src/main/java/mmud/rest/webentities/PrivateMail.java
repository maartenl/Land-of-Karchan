/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmud.rest.webentities;


import java.time.LocalDateTime;

import mmud.JsonUtils;
import mmud.database.entities.game.Mail;
import mmud.database.entities.game.MailReceiver;

/**
 * @author maartenl
 */
public class PrivateMail
{

  public String toname;
  public String name;
  public String subject;
  public String body;
  public Long id;
  public Boolean haveread;
  public Boolean newmail;
  public LocalDateTime whensent;
  public Boolean deleted;
  public Long item_id;

  public PrivateMail(MailReceiver receiver)
  {
    name = receiver.getMail().getName().getName();
    toname = receiver.getMail().getToname();
    subject = receiver.getMail().getSubject();
    body = receiver.getMail().getBody();
    id = receiver.getId();
    haveread = receiver.getHaveread();
    newmail = receiver.getNewmail();
    whensent = receiver.getMail().getWhensent();
    deleted = receiver.getDeleted();
  }

  public PrivateMail(Mail mail)
  {
    name = mail.getName().getName();
    toname = mail.getToname();
    subject = mail.getSubject();
    body = mail.getBody();
    id = mail.getId();
    whensent = mail.getWhensent();
    deleted = mail.getDeleted();
  }

  public PrivateMail()
  {
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static PrivateMail fromJson(String json)
  {
    return JsonUtils.fromJson(json, PrivateMail.class);

  }
}
