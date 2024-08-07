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
package mmud.rest.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import mmud.JsonUtils;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Mail;
import mmud.database.entities.game.MailReceiver;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.database.entities.web.CharacterInfo;
import mmud.database.entities.web.Family;
import mmud.database.entities.web.FamilyPK;
import mmud.database.entities.web.FamilyValue;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.PrivateMail;
import mmud.rest.webentities.PrivatePassword;
import mmud.rest.webentities.PrivatePerson;
import mmud.services.LogService;
import mmud.services.MailService;
import mmud.services.PersonService;
import mmud.services.PlayerAuthenticationService;
import mmud.services.PrivateService;
import mmud.services.PublicService;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains all rest calls that are available to a specific character, with
 * authentication and authorization. You can find them at
 * /karchangame/resources/private.
 *
 * @author maartenl
 */
@DeclareRoles("player")
@RolesAllowed("player")
@Path("/private")
@Transactional
public class PrivateRestService
{

  public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match.";
  @Inject

  private MailService mailService;

  @Inject
  private PublicService publicService;

  @Inject
  private PrivateService privateService;

  @Context
  private SecurityContext context;

  @Inject
  private LogService logService;

  /**
   * Indicates that only 20 mails may be retrieved per time (basically a
   * page).
   */
  public static final int MAX_MAILS = 20;

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private PlayerAuthenticationService playerAuthenticationService;

  @Inject
  private PersonService personService;

  public PrivateService getPrivateService()
  {
    return privateService;
  }

  public static class PrivateHasMail
  {
    public boolean hasMail;
  }

  public PrivateRestService()
  {
    // empty constructor, I don't know why, but it's necessary.
  }

  /**
   * Returns the entity manager of JPA. This is defined in
   * build/web/WEB-INF/classes/META-INF/persistence.xml.
   *
   * @return EntityManager
   */
  protected EntityManager getEntityManager()
  {
    return em;
  }

  public PublicService getPublicService()
  {
    return publicService;
  }

  private static final Logger LOGGER = Logger.getLogger(PrivateRestService.class.getName());

  /**
   * Returns a List of your mail, with a maximum of 20 mails and an offset for
   * paging.
   *
   * @param name   the name of the user
   * @param offset the offset, default is 0 if not provided.
   * @return a List of (max 20 by default) mails.
   * @throws WebApplicationException <ul><li>UNAUTHORIZED, if the
   *                                 authorisation failed. </li><li>BAD_REQUEST if an unexpected exception
   *                                 crops up.</li></ul>
   * @see #MAX_MAILS
   */
  @GET
  @Path("{name}/mail")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public List<PrivateMail> listMail(@PathParam("name") String name, @QueryParam("offset") Integer offset)
  {
    LOGGER.finer("entering listMail");
    User person = authenticate(name);
    List<PrivateMail> res = new ArrayList<>();
    try
    {
      TypedQuery<MailReceiver> query = getEntityManager().createNamedQuery("Mail.listmail", MailReceiver.class);
      query.setParameter("name", person);
      query.setMaxResults(MAX_MAILS);
      if (offset != null)
      {
        query.setFirstResult(offset);
      }

      List<MailReceiver> list = query.getResultList();
      for (MailReceiver mail : list)
      {
        res.add(new PrivateMail(mail));
      }

      // turn off the "newmail" sign.
      getEntityManager().createNamedQuery("Mail.nonewmail")
        .setParameter("name", person)
        .executeUpdate();
    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(name, e, Status.BAD_REQUEST);
    }
    return res;
  }

  private User authenticate(String name)
  {
    return playerAuthenticationService.authenticate(name, context);
  }

  /**
   * Returns a List of mails you sent, with a maximum of 20 mails and an offset for
   * paging.
   *
   * @param name   the name of the user
   * @param offset the offset, default is 0 if not provided.
   * @return a List of (max 20 by default) mails.
   * @throws WebApplicationException <ul><li>UNAUTHORIZED, if the
   *                                 authorisation failed. </li><li>BAD_REQUEST if an unexpected exception
   *                                 crops up.</li></ul>
   * @see #MAX_MAILS
   */
  @GET
  @Path("{name}/sentmail")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public List<PrivateMail> listSentMail(@PathParam("name") String name, @QueryParam("offset") Integer offset)
  {
    User person = authenticate(name);
    List<PrivateMail> res = new ArrayList<>();
    try
    {
      TypedQuery<Mail> query = getEntityManager().createNamedQuery("Mail.listsentmail", Mail.class);
      query.setParameter("name", person);
      query.setMaxResults(MAX_MAILS);
      if (offset != null)
      {
        query.setFirstResult(offset);
      }

      List<Mail> list = query.getResultList();
      for (Mail mail : list)
      {
        res.add(new PrivateMail(mail));
      }
    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(name, e, Status.BAD_REQUEST);
    }
    return res;
  }

  /**
   * Returns a boolean, indicating if you have new mail. (i.e. mail since you
   * last checked)
   *
   * @param name the name of the user
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @GET
  @Path("{name}/newmail")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public String hasNewMail(@PathParam("name") String name)
  {
    LOGGER.finer("entering hasNewMail");
    Person person = authenticate(name);
    boolean result = mailService.hasNewMail(person);
    var privateHasMail = new PrivateHasMail();
    privateHasMail.hasMail = result;
    return JsonUtils.toJson(privateHasMail);
  }

  private Person getPerson(String name)
  {
    var toperson = getEntityManager().find(Person.class, name);
    if (toperson == null)
    {
      throw new MudWebException(name, name + " was not found.", "User was not found (" + name + ")", Status.NOT_FOUND);
    }
    return toperson;
  }

  private User getUser(String name)
  {
    User toperson = getEntityManager().find(User.class, name);
    if (toperson == null)
    {
      throw new MudWebException(name, name + " was not found.", "User was not found (" + name + ")", Status.NOT_FOUND);
    }
    if (!toperson.isUser())
    {
      LOGGER.log(Level.INFO, "user not proper user, {0}", name);
      throw new MudWebException(name, name + " was not a proper user.", "User was not a proper user (" + name + ")", Status.BAD_REQUEST);
    }
    return toperson;
  }

  /**
   * Send a new mail.
   *
   * @param json json representation of a new mail object received from the client. Sending to
   *             user "deputies" allows for a mail to be sent to all current active deputies.
   * @param name the name of the person logged in/sending mail
   * @return Response.ok if everything's okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @POST
  @Path("{name}/mail")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response newMailRest(String json, @PathParam("name") String name)
  {
    LOGGER.finer("entering newMailRest");
    var newMail = PrivateMail.fromJson(json);
    newMail(newMail, name);
    return createResponse();
  }

  public void newMail(PrivateMail newMail, String name)
  {
    LOGGER.finer("entering newMail");
    User person = authenticate(name);
    if (newMail.toname == null || "".equals(newMail.toname.trim()))
    {
      throw new MudWebException(person.getName(), "No recipient found.", Status.NOT_FOUND);
    }
    Set<User> users = Stream.of(newMail.toname.split(","))
      .map(String::trim)
      .flatMap(receiver -> getReceivers(person.getName(), receiver, person.getGuild()))
      .collect(Collectors.toSet());
    try
    {
      createNewMail(newMail.subject, newMail.body, person, newMail.toname, users);
    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(name, e, Status.BAD_REQUEST);
    }
    LOGGER.finer("exiting newMail");
  }

  private Stream<User> getReceivers(String fromname, String toname, Guild guild)
  {
    if ("deputies".equalsIgnoreCase(toname))
    {
      return getPublicService().getDeputies().stream();
    }

    if ("guild".equalsIgnoreCase(toname))
    {
      if (guild == null)
      {
        throw new MudWebException(fromname, "You are not a member of a guild.", Status.NOT_FOUND);
      }
      return guild.getMembers().stream();
    }
    if (toname.toLowerCase().startsWith("guild:"))
    {
      String guildname = toname.substring(6).trim();
      guild = getEntityManager().find(Guild.class, guildname);
      if (guild == null)
      {
        throw new MudWebException(fromname, "Guild " + guildname + " not found.", Status.NOT_FOUND);
      }
      return guild.getMembers().stream();
    }
    if ("everybody".equalsIgnoreCase(toname))
    {
      var admin = getEntityManager().find(Admin.class, fromname);
      if (admin == null || !admin.isValid())
      {
        throw new MudWebException(fromname, "Only administrators are allowed to use 'everybody' in mail.", Status.UNAUTHORIZED);
      }
      TypedQuery<User> query = getEntityManager().createNamedQuery("User.everybodymail", User.class);
      query.setParameter("olddate", LocalDateTime.now().minusMonths(3));
      return query.getResultList().stream();
    }
    return Stream.of(getUser(toname));
  }

  private void createNewMail(String subject, String body, User person, String toperson, Set<User> receivers)
  {
    var mail = new Mail();
    mail.setBody(body);
    mail.setDeleted(Boolean.FALSE);
    mail.setId(null);
    mail.setName(person);
    mail.setSubject(subject);
    mail.setToname(toperson);
    mail.setWhensent(LocalDateTime.now());
    getEntityManager().persist(mail);

    receivers.forEach(receiver ->
    {
      var mailReceiver = new MailReceiver();
      mailReceiver.setId(null);
      mailReceiver.setToname(receiver);
      mailReceiver.setHaveread(false);
      mailReceiver.setNewmail(true);
      mailReceiver.setMail(mail);
      mailReceiver.setDeleted(false);

      getEntityManager().persist(mailReceiver);
    });

  }

  private MailReceiver getMail(String toname, Long id)
  {
    MailReceiver mail = getEntityManager().find(MailReceiver.class, id);

    if (mail == null)
    {
      throw new MudWebException(toname, "Mail " + id + " not found.", Status.NOT_FOUND);
    }
    if (Boolean.TRUE.equals(mail.getDeleted()))
    {
      throw new MudWebException(toname, "Mail with id " + id + " was deleted.", Status.NOT_FOUND);
    }
    if (!mail.getToname().getName().equals(toname))
    {
      LOGGER.log(Level.INFO, "mail {0} not for {1}", new Object[]
        {
          id, toname
        });

      throw new MudWebException(toname, "Mail with id " + id + " was not for " + toname + ".", Status.UNAUTHORIZED);
    }
    return mail;
  }

  /**
   * Returns a single mail based by id. Can only retrieve mail destined for
   * the user requesting the mail.
   *
   * @param name the name of the user
   * @param id   the id of the mail to get
   * @return A specific mail.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @GET
  @Path("{name}/mail/{id}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public PrivateMail getMailInfo(@PathParam("name") String name, @PathParam("id") long id)
  {
    LOGGER.finer("entering getMail");
    Person person = authenticate(name);
    try
    {
      MailReceiver mail = getMail(person.getName(), id);
      // turn off the "have not read" sign.
      mail.setHaveread(Boolean.TRUE);
      LOGGER.finer("exiting getMail");
      return new PrivateMail(mail);
    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(name, e, Status.BAD_REQUEST);
    }
  }

  /**
   * Creates an itemDefinitionId instance (and, if required, an
   * itemDefinitionId definition) representing an in-game version of a single
   * mail based by id.
   * @param name             the name of the user
   * @param id               the id of the mail to get
   * @param itemDefinitionId the kind of itemDefinitionId that is to be made.
   *                         It may be null, if there already is attached a item definition to the
   *                         mail.
   * @return Response.ok if everything is fine.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   * @plantuml
   * <!--
   * (*) --> "check params"
   * --> "getMail"
   * if "has Item Definition" then
   * ->[true] "create instance object"
   * else
   * ->[false] "get maxid"
   * --> "create itemDefinitionId definition"
   * --> "set itemdefinition into the mail"
   * --> "create instance object"
   * endif
   * --> "create inventory object"
   * -->(*)
   * -->
   * @see Mail#ITEMS
   */
  @GET
  @Path("{name}/mail/{id}/createMailItem/{item}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response createMailItem(@PathParam("name") String name, @PathParam("id") long id, @PathParam("item") int itemDefinitionId)
  {
    LOGGER.finer("entering createMailItem");
    Person person = authenticate(name);

    // get the specific mail with id {id}
    MailReceiver mail = getMail(person.getName(), id);

    LOGGER.finer("createMailItem: retrieve template item definition");

    if (itemDefinitionId >= Mail.ITEMS.size() || itemDefinitionId < 0)
    {
      LOGGER.finer("createMailItem: wrong item def");
      throw new MudWebException(name, "Could not create item from mail.", Status.NOT_FOUND);
    }

    ItemDefinition definition = getEntityManager().find(ItemDefinition.class, Mail.ITEMS.get(itemDefinitionId));
    if (definition == null)
    {
      LOGGER.finer("createMailItem: could not find itemdefinition from itemdefinitionnr " + Mail.ITEMS.get(itemDefinitionId));
      throw new MudWebException(name, "Could not create item from mail.", Status.NOT_FOUND);
    }
    // create itemDefinitionId instance
    LOGGER.finer(() -> "createMailItem: create instance object " + definition.getId() + " " + name);

    var item = new NormalItem(definition);
    item.setOwner(getEntityManager().find(Admin.class, Admin.DEFAULT_OWNER));
    getEntityManager().persist(item);
    var description = definition.getReaddescription()
//        <div class="card">
//                <div class="card-text">
//                    <h4 class="card-title">{{ mail?.subject }}</h4>
//                    <p class="card-text"><strong>From:</strong> {{ mail?.name }}</p>
//                    <p class="card-text"><strong>To:</strong> {{ mail?.toname }}</p>
//                    <p class="card-text"><strong>On:</strong> {{ mail?.whensent | date:'medium' }}</p>
//                </div>
//                <div class="card-text" [innerHTML]="mail?.body">
//                </div>
//            </div>
      .replace("letterhead", "<div class=\"card\">\n" +
        "  <div class=\"card-body\">\n" +
        "<h4 class=\"card-title\">" + mail.getMail().getSubject() + "</h4></div>")
      .replace("letterbody",
        "                <p class=\"card-text\">\n" + mail.getMail().getBody() +
          "                </p>")
      .replace("letterfooter", "<h6 class=\"card-subtitle mb-2 text-muted\">From " + mail.getMail().getName().getName() + "</h6>\n" +
        "</div></div>");
    item.setAttribute("readable", description);
    person.addItem(item);
    logService.writeLog(person, "item " + definition.getAdjectives() + " " + definition.getName() + " from mail " + id + " created.");

    LOGGER.finer("exiting createMailItem");
    return createResponse();
  }

  protected Response createResponse()
  {
    return Response.ok().build();
  }

  /**
   * Deletes a single mail based by id.
   *
   * @param name the name of the user
   * @param id   the id of the mail to delete
   * @return Response.ok() if all is well.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @DELETE
  @Path("{name}/mail/{id}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response deleteMail(@PathParam("name") String name, @PathParam("id") long id)
  {
    LOGGER.finer("entering deleteMail");
    Person person = authenticate(name);
    // get the specific mail with id {id}
    MailReceiver mail = getMail(person.getName(), id);
    mail.setDeleted(Boolean.TRUE);
    LOGGER.finer("exiting deleteMail");
    logService.writeLog(person, "mail " + id + " deleted.");
    return createResponse();
  }

  /**
   * Deletes an entire character.
   *
   * @param requestContext the context to log the player off who just deleted
   *                       his entire character.
   * @param name           the name of the user
   * @return Response.ok() if all is well.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
   *                                 BAD_REQUEST if an unexpected exception crops up.
   */
  @DELETE
  @Path("{name}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response deletePerson(@Context HttpServletRequest requestContext, @PathParam("name") String name)
  {
    LOGGER.log(Level.FINER, "entering deletePerson {0}", name);
    User person = authenticate(name);
    personService.deletePerson(person);
    playerAuthenticationService.logoff(requestContext);
    LOGGER.finer("exiting deletePerson");
    return createResponse();
  }

  /**
   * Adds or updates your current character info.
   *
   * @param name  the name of the user
   * @param cinfo the object containing the new stuff to update.
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @PUT
  @Path("{name}/charactersheet")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response updateCharacterSheet(@PathParam("name") String name, PrivatePerson cinfo)
  {
    LOGGER.finer("entering updateCharacterSheet");
    User person = authenticate(name);
    if (!person.getName().equals(cinfo.name))
    {
      throw new MudWebException(name, "User trying to update somebody elses charactersheet?",
        person.getName() + " trying to update charactersheet of " + cinfo.name, Status.UNAUTHORIZED);
    }

    var characterInfo = getEntityManager().find(CharacterInfo.class, person.getName());
    var isNew = false;
    if (characterInfo == null)
    {
      isNew = true;
      characterInfo = new CharacterInfo();
      characterInfo.setName(person.getName());
    }
    characterInfo.setImageurl(cinfo.imageurl);
    person.setTitle(StringUtils.trimToNull(cinfo.title));
    person.setFamilyname(StringUtils.trimToNull(cinfo.familyname));
    if (cinfo.websockets != null)
    {
      person.setWebsocketSupport(cinfo.websockets);
    }
    characterInfo.setHomepageurl(cinfo.homepageurl);
    characterInfo.setDateofbirth(cinfo.dateofbirth);
    characterInfo.setCityofbirth(cinfo.cityofbirth);
    characterInfo.setStoryline(cinfo.storyline);
    if (isNew)
    {
      getEntityManager().persist(characterInfo);
    }
    logService.writeLog(person, "settings changed.");

    return createResponse();
  }

  /**
   * Resets your current password
   *
   * @param name the name of the user
   * @param json the object containing the new stuff to update.
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @PUT
  @Path("{name}/resetPassword")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response resetPassword(@PathParam("name") String name, String json)
  {
    LOGGER.finer("entering resetPassword");
    User person = authenticate(name);
    PrivatePassword privatePassword = PrivatePassword.fromJson(json);

    if (!person.getName().equals(privatePassword.name))
    {
      throw new MudWebException(name, "User trying to update somebody elses password?",
        person.getName() + " trying to update password of " + privatePassword.name, Status.UNAUTHORIZED);
    }
    if (StringUtils.isBlank(privatePassword.oldpassword) ||
      StringUtils.isBlank(privatePassword.password) ||
      StringUtils.isBlank(privatePassword.password2)
    )
    {
      throw new MudWebException(name, "Not enough information to reset password.",
        person.getName() + " provides not enough information to reset password.", Status.UNAUTHORIZED);
    }
    if (!privatePassword.password.equals(privatePassword.password2))
    {
      throw new MudWebException(name,
        PASSWORDS_DO_NOT_MATCH,
        PASSWORDS_DO_NOT_MATCH,
        Status.BAD_REQUEST);
    }
    if (!person.verifyPassword(privatePassword.oldpassword))
    {
      throw new MudWebException(name,
        PASSWORDS_DO_NOT_MATCH,
        PASSWORDS_DO_NOT_MATCH,
        Status.BAD_REQUEST);
    }
    logService.writeLog(person, "password changed.");
    person.setNewpassword(privatePassword.password);
    return createResponse();
  }

  /**
   * Deletes a character, permanently. Use with extreme caution.
   *
   * @param name the name of the user
   * @return Response.ok
   * @throws WebApplicationException BAD_REQUEST if an unexpected exception
   *                                 crops up.
   */
  @DELETE
  @Path("{name}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public Response delete(@PathParam("name") String name)
  {
    LOGGER.finer("entering delete");
    Person person = authenticate(name);
    try
    {
      logService.writeLog(person, "character deleted.");
      getEntityManager().remove(person);
    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(name, e, Status.BAD_REQUEST);
    }
    return createResponse();
  }

  /**
   * Gets your current character info.
   *
   * @param name the name of the user
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @GET
  @Path("{name}/charactersheet")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public PrivatePerson getCharacterSheet(@PathParam("name") String name)
  {
    authenticate(name);
    return getPrivateService().charactersheet(name);
  }

  /**
   * Add or updates some family values from your family tree.
   *
   * @param name        the name of the user
   * @param toname      the name of the user you are related to
   * @param description the description of the family relation, for example
   *                    "mother".
   * @return Response.ok if everything's okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @PUT
  @Path("{name}/charactersheet/familyvalues/{toname}/{description}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response updateFamilyvalues(@PathParam("name") String name, @PathParam("toname") String toname, @PathParam("description") Integer description)
  {
    LOGGER.finer("entering updateFamilyvalues");
    if (description == null || description == 0)
    {
      throw new MudWebException(name, "An invalid relationship was provided.", Status.BAD_REQUEST);
    }
    if (toname == null || "".equals(toname.trim()))
    {
      throw new MudWebException(name, "No person provided.", Status.BAD_REQUEST);
    }
    User person = authenticate(name);
    var toperson = getPerson(toname);
    var familyValue = getEntityManager().find(FamilyValue.class, description);
    if (familyValue == null)
    {
      throw new MudWebException(name, "Family value was not found.",
        "Family value " + description + " was not found.",
        Status.BAD_REQUEST);
    }
    var pk = new FamilyPK();
    pk.setName(person.getName());
    pk.setToname(toperson.getName());
    var family = getEntityManager().find(Family.class, pk);

    boolean isNew = family == null;
    if (family == null)
    {
      family = new Family();
      family.setFamilyPK(pk);
    }
    family.setDescription(familyValue);
    if (isNew)
    {
      getEntityManager().persist(family);
    }

    LOGGER.finer("exiting updateFamilyvalues");
    return createResponse();
  }

  /**
   * Deletes some family values from your family tree.
   *
   * @param name   the name of the user
   * @param toname the name of the user you are related to
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @DELETE
  @Path("{name}/charactersheet/familyvalues/{toname}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response deleteFamilyvalues(@PathParam("name") String name, @PathParam("toname") String toname)
  {
    LOGGER.finer("entering deleteFamilyValues");
    Person person = authenticate(name);
    var pk = new FamilyPK();
    pk.setName(person.getName());
    pk.setToname(toname);
    var family = getEntityManager().find(Family.class, pk);
    if (family == null)
    {
      throw new MudWebException(name, "Unable to delete family value. Family value not found.", Status.NOT_FOUND);
    }
    getEntityManager().remove(family);
    LOGGER.finer("exiting deleteFamilyValues");
    return createResponse();
  }

  @VisibleForTesting
  public void setLogService(LogService logService)
  {
    this.logService = logService;
  }

  @VisibleForTesting
  public void setPlayerAuthenticationService(PlayerAuthenticationService playerAuthenticationService)
  {
    this.playerAuthenticationService = playerAuthenticationService;
  }
}
