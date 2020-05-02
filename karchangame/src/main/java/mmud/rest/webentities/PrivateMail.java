/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mmud.rest.webentities;


import mmud.database.entities.game.Mail;
import mmud.database.entities.game.MailReceiver;
import mmud.rest.webentities.admin.AdminEvent;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.LocalDateTime;

/**
 * @author maartenl
 */
public class PrivateMail {

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

  public PrivateMail(MailReceiver receiver) {
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

  public PrivateMail(Mail mail) {
    name = mail.getName().getName();
    toname = mail.getToname();
    subject = mail.getSubject();
    body = mail.getBody();
    id = mail.getId();
    whensent = mail.getWhensent();
    deleted = mail.getDeleted();
  }

  public PrivateMail() {
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static PrivateMail fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, PrivateMail.class);
  }
}
