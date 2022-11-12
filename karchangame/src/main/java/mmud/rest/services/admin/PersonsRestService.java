/*
 * Copyright (C) 2014 maartenl
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
package mmud.rest.services.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import mmud.database.entities.characters.Bot;
import mmud.database.entities.characters.Mob;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.Shopkeeper;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Room;
import mmud.database.enums.God;
import mmud.database.enums.Sex;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminCharacter;
import mmud.services.LogBean;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")


@Path("/administration/characters")
public class PersonsRestService
{
  private static final Logger LOGGER = Logger.getLogger(PersonsRestService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  @POST
  @Consumes(
    {
      "application/json"
    })
  public void create(String json, @Context SecurityContext sc)
  {
    LOGGER.finer("entering create");
    AdminCharacter adminCharacter = AdminCharacter.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    Person alreadyExistingPerson = getEntityManager().find(Person.class, adminCharacter.name);
    if (alreadyExistingPerson != null)
    {
      throw new MudWebException(name, "Character '" + adminCharacter.name + "' already exists.", Response.Status.FOUND);
    }
    Person character;
    if (adminCharacter.god == null)
    {
      throw new MudWebException(name, "Character has no god value.", Response.Status.BAD_REQUEST);
    }
    switch (adminCharacter.god.trim())
    {
      case "BOT":
        character = new Bot();
        break;
      case "MOB":
        character = new Mob();
        break;
      case "SHOPKEEPER":
        character = new Shopkeeper();
        break;
      case "DEFAULT_USER":
        User user = new User();
        user.setAddress(adminCharacter.address);
        user.setRealname(adminCharacter.realname);
        user.setEmail(adminCharacter.email);
        user.setOoc(adminCharacter.ooc);
        character = user;
        break;
      default:
        throw new MudWebException(name, "Character has invalid god value (" + adminCharacter.god + ").", Response.Status.BAD_REQUEST);
    }
    character.setName(adminCharacter.name);
//    character.setImage(adminCharacter.image);
    character.setTitle(adminCharacter.title);
    character.setRace(adminCharacter.race);
    try
    {
      character.setSex(Sex.createFromString(adminCharacter.sex));
    } catch (MudException e)
    {
      throw new MudWebException(name, e, Response.Status.BAD_REQUEST);
    }
    character.setAge(adminCharacter.age);
    character.setHeight(adminCharacter.height);
    character.setWidth(adminCharacter.width);
    character.setComplexion(adminCharacter.complexion);
    character.setEyes(adminCharacter.eyes);
    character.setFace(adminCharacter.face);
    character.setHair(adminCharacter.hair);
    character.setBeard(adminCharacter.beard);
    character.setArm(adminCharacter.arm);
    character.setLeg(adminCharacter.leg);
//    character.setCopper(0); -- the default
    if (adminCharacter.room != null)
    {
      Room room = getEntityManager().find(Room.class, adminCharacter.room);
      if (room == null)
      {
        throw new MudWebException(name, "Room " + adminCharacter.room + " not found.", Response.Status.NOT_FOUND);
      }
      character.setRoom(room);
    }
    character.setVisible(adminCharacter.visible);
    character.setBirth(LocalDateTime.now());
    character.setNotes(adminCharacter.notes);
//    character.setCurrentstate(adminCharacter.currentstate);
    character.setCreation(LocalDateTime.now());
    character.setOwner(admin);
    ValidationUtils.checkValidation(name, character);
    getEntityManager().persist(character);
    logBean.writeDeputyLog(admin, "New character '" + character.getName() + "' created.");
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      "application/json"
    })
  public void edit(@PathParam("id") String id, String json, @Context SecurityContext sc)
  {
    LOGGER.finer("entering edit");
    AdminCharacter adminCharacter = AdminCharacter.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminCharacter.name))
    {
      throw new MudWebException(name, "Character names do not match.", Response.Status.BAD_REQUEST);
    }
    Person character = getEntityManager().find(Person.class, adminCharacter.name);

    if (character == null)
    {
      throw new MudWebException(name, "User " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, character);
    character.setTitle(adminCharacter.title);
//    character.setImage(adminCharacter.image);
    character.setRace(adminCharacter.race);
    character.setSex(Sex.createFromString(adminCharacter.sex));
    character.setAge(adminCharacter.age);
    character.setHeight(adminCharacter.height);
    character.setWidth(adminCharacter.width);
    character.setComplexion(adminCharacter.complexion);
    character.setEyes(adminCharacter.eyes);
    character.setFace(adminCharacter.face);
    character.setHair(adminCharacter.hair);
    character.setBeard(adminCharacter.beard);
    character.setArm(adminCharacter.arm);
    character.setLeg(adminCharacter.leg);
//    character.setCopper(0); -- the default
    if (adminCharacter.room != null)
    {
      Room room = getEntityManager().find(Room.class, adminCharacter.room);
      if (room == null)
      {
        throw new MudWebException(name, "Room " + adminCharacter.room + " not found.", Response.Status.NOT_FOUND);
      }
      character.setRoom(room);
    }
    character.setVisible(adminCharacter.visible);
    character.setNotes(adminCharacter.notes);
//    character.setCurrentstate(adminCharacter.currentstate);
    if (character.getGod() == God.DEFAULT_USER)
    {
      User user = (User) character;
      user.setRealname(adminCharacter.realname);
      user.setEmail(adminCharacter.email);
      user.setOoc(adminCharacter.ooc);
      if (adminCharacter.newpassword != null && !adminCharacter.newpassword.trim().equals(""))
      {
        user.setNewpassword(adminCharacter.newpassword);
      }
    }
    character.setOwner(OwnerHelper.getNewOwner(adminCharacter.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, character);
    logBean.writeDeputyLog(admin, "Character '" + character.getName() + "' updated.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") String id, @Context SecurityContext sc)
  {
    LOGGER.finer("entering remove");
    final String name = sc.getUserPrincipal().getName();
    final User character = getEntityManager().find(User.class, id);
    if (character == null)
    {
      throw new MudWebException(name, "Character " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, character);
    getEntityManager().remove(character);
    logBean.writeDeputyLog(admin, "Character '" + character.getName() + "' deleted.");
  }

  @GET
  @Path("{id}")
  @Produces(
    {
      "application/json"
    })
  public String find(@PathParam("id") String id, @Context SecurityContext sc)
  {
    LOGGER.finer("entering find");
    final String name = sc.getUserPrincipal().getName();
    Person item = getEntityManager().find(Person.class, id);
    if (item == null)
    {
      throw new MudWebException(name, "Character " + id + " not found.", Response.Status.NOT_FOUND);
    }
    if (item.getGod() == God.DEFAULT_USER || item.getGod() == God.GOD)
    {
      return new AdminCharacter((User) item).toJson();
    }
    return new AdminCharacter(item).toJson();
  }

  @GET
  @Produces(
    {
      "application/json"
    })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminCharacter.GET_QUERY)).build();
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
    {
      "application/json"
    })

  public String findRange(@Context UriInfo info, @PathParam("offset") Integer offset,
                          @PathParam("pageSize") Integer pageSize
  )
  {
    @SuppressWarnings("unchecked")
    List<String> items = getEntityManager().createNativeQuery(AdminCharacter.GET_QUERY)
      .setMaxResults(pageSize)
      .setFirstResult(offset)
      .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @GET
  @Path("count")
  @Produces("text/plain")
  public String count(@Context UriInfo info)
  {
    return String.valueOf(getEntityManager().createNamedQuery("Person.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
