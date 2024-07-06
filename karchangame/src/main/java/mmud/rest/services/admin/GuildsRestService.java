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


import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Guild;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminGuild;
import mmud.rest.webentities.admin.AdminGuildmember;
import mmud.services.LogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Transactional
@Path("/administration/guilds")
public class GuildsRestService
{

  private static final Logger LOGGER = Logger.getLogger(GuildsRestService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogService logService;

  @Context
  private SecurityContext sc;

  @POST
  @Consumes(
      {
          MediaType.APPLICATION_JSON
      })
  public String create(String json)
  {
    AdminGuild adminGuild = AdminGuild.fromJson(json);

    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    Guild guild = getEntityManager().find(Guild.class, adminGuild.name);
    if (guild != null)
    {
      throw new MudWebException(name, "Guild " + adminGuild.name + " already exists.",
          Response.Status.PRECONDITION_FAILED);
    }
    guild = new Guild();
    guild.setName(adminGuild.name);
    guild.setTitle(adminGuild.title);
    guild.setDescription(adminGuild.guilddescription);
    guild.setHomepage(adminGuild.guildurl);
    User guildmaster = getEntityManager().find(User.class, adminGuild.bossname);
    if (guildmaster == null)
    {
      throw new MudWebException(name, "Guild " + adminGuild.name + " needs a guildmaster, guildmaster not found.",
          Response.Status.NOT_FOUND);
    }
    if (guildmaster.getGuild() != null)
    {
      throw new MudWebException(name, adminGuild.bossname + " is already a guildmaster.",
          Response.Status.PRECONDITION_FAILED);
    }
    guild.setBoss(guildmaster);
    guild.setActive(true);
    guild.setLogonmessage(adminGuild.logonmessage);
    guild.setColour(adminGuild.colour);
    guild.setCreation(LocalDateTime.now());
    guild.setOwner(admin);
    ValidationUtils.checkValidation(name, guild);
    logService.writeDeputyLog(admin, "New guild '" + guild.getName() + "' created.");
    getEntityManager().persist(guild);
    return guild.getName();
  }

  @PUT
  @Path("{name}")
  @Consumes(
      {
          MediaType.APPLICATION_JSON
      })
  public void edit(@PathParam("name") String guildname, String json)
  {
    AdminGuild adminGuild = AdminGuild.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!guildname.equals(adminGuild.name))
    {
      throw new MudWebException(name, "Guild names do not match.", Response.Status.BAD_REQUEST);
    }
    Guild guild = getEntityManager().find(Guild.class, adminGuild.name);
    if (guild == null)
    {
      throw new MudWebException(name, "Guild " + guildname + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, guild);
    guild.setTitle(adminGuild.title);
    guild.setDescription(adminGuild.guilddescription);
    guild.setHomepage(adminGuild.guildurl);
    User guildmaster = getEntityManager().find(User.class, adminGuild.bossname);
    if (guildmaster == null)
    {
      throw new MudWebException(name, "Guildmaster " + adminGuild.bossname + " not found.", Response.Status.NOT_FOUND);
    }
    if (guildmaster.getGuild().getBoss() == guildmaster && guild != guildmaster.getGuild())
    {
      throw new MudWebException(name,
          adminGuild.bossname + " is already a guildmaster of guild " + guildmaster.getGuild().getName() + ".",
          Response.Status.PRECONDITION_FAILED);
    }
    guild.setBoss(guildmaster);
    guild.setColour(adminGuild.colour);
    guild.setLogonmessage(adminGuild.logonmessage);
    guild.setImage(adminGuild.imageurl);
    guild.setOwner(OwnerHelper.getNewOwner(adminGuild.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, guild);
    logService.writeDeputyLog(admin, "Guild '" + guild.getName() + "' updated.");
  }

  @DELETE
  @Path("{name}")
  public void remove(@PathParam("name") String guildname)
  {
    final String name = sc.getUserPrincipal().getName();
    final Guild guild = getEntityManager().find(Guild.class, guildname);
    if (guild == null)
    {
      throw new MudWebException(name, "Guild " + guildname + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, guild);
    getEntityManager().remove(guild);
    logService.writeDeputyLog(admin, "Guild '" + guild.getName() + "' deleted.");
  }

  @GET
  @Path("{name}")
  @Produces(
      {
          MediaType.APPLICATION_JSON
      })
  public String find(@PathParam("name") String guildname)
  {
    final String name = sc.getUserPrincipal().getName();
    final Guild guild = getEntityManager().find(Guild.class, guildname);
    if (guild == null)
    {
      throw new MudWebException(name, "Guild " + guildname + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminGuild(guild).toJson();
  }

  @GET
  @Produces(
      {
          MediaType.APPLICATION_JSON
      })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminGuild.GET_QUERY)).build();
  }

  @GET
  @Path("{name}/guildmembers")
  @Produces(
      {
          MediaType.APPLICATION_JSON
      })
  public String getGuildmembers(@PathParam("name") String guildname)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminGuildmember.GET_QUERY)
        .setParameter(1, guildname)
        .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
      {
          MediaType.APPLICATION_JSON
      })
  public String findRange(@Context UriInfo info, @PathParam("offset") Integer offset,
                          @PathParam("pageSize") Integer pageSize)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminGuild.GET_QUERY)
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
    return String.valueOf(getEntityManager().createNamedQuery("Guild.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
