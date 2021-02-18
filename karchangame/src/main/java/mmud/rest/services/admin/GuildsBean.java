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
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Guild;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminGuild;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/guilds")
public class GuildsBean
{

  private static final Logger LOGGER = Logger.getLogger(GuildsBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  @POST
  @Consumes(
    {
      "application/json"
    })
  public String create(String json, @Context SecurityContext sc)
  {
    AdminGuild adminGuild = AdminGuild.fromJson(json);

    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    Guild guild = getEntityManager().find(Guild.class, adminGuild.name);
    if (guild != null)
    {
      throw new MudWebException(name, "Guild " + adminGuild.name + " already exists.", Response.Status.PRECONDITION_FAILED);
    }
    guild = new Guild();
    guild.setName(adminGuild.name);
    guild.setTitle(adminGuild.title);
    guild.setDescription(adminGuild.guilddescription);
    guild.setHomepage(adminGuild.guildurl);
    User guildmaster = getEntityManager().find(User.class, adminGuild.bossname);
    if (guildmaster == null)
    {
      throw new MudWebException(name, "Guild " + adminGuild.name + " needs a guildmaster, guildmaster not found.", Response.Status.NOT_FOUND);
    }
    if (guildmaster.getGuild() != null)
    {
      throw new MudWebException(name, adminGuild.bossname + " is already a guildmaster.", Response.Status.PRECONDITION_FAILED);
    }
    guild.setBoss(guildmaster);
    guild.setActive(true);
    guild.setLogonmessage(adminGuild.logonmessage);
    guild.setColour(adminGuild.colour);
    guild.setCreation(LocalDateTime.now());
    guild.setOwner(admin);
    ValidationUtils.checkValidation(name, guild);
    logBean.writeDeputyLog(admin, "New guild '" + guild.getName() + "' created.");
    getEntityManager().persist(guild);
    return guild.getName();
  }

  @PUT
  @Path("{name}")
  @Consumes(
    {
      "application/xml", "application/json"
    })
  public void edit(@PathParam("name") String guildname, String json, @Context SecurityContext sc)
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
      throw new MudWebException(name, adminGuild.bossname + " is already a guildmaster of guild " + guildmaster.getGuild().getName() + ".", Response.Status.PRECONDITION_FAILED);
    }
    guild.setBoss(guildmaster);
    guild.setColour(adminGuild.colour);
    guild.setLogonmessage(adminGuild.logonmessage);
    guild.setImage(adminGuild.imageurl);
    guild.setOwner(OwnerHelper.getNewOwner(adminGuild.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, guild);
    logBean.writeDeputyLog(admin, "Guild '" + guild.getName() + "' updated.");
  }

  @DELETE
  @Path("{name}")
  public void remove(@PathParam("name") String guildname, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Guild guild = getEntityManager().find(Guild.class, guildname);
    if (guild == null)
    {
      throw new MudWebException(name, "Guild " + guildname + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, guild);
    getEntityManager().remove(guild);
    logBean.writeDeputyLog(admin, "Guild '" + guild.getName() + "' deleted.");
  }

  @GET
  @Path("{name}")
  @Produces(
    {
      "application/json"
    })
  public String find(@PathParam("name") String guildname, @Context SecurityContext sc)
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
      "application/json"
    })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminGuild.GET_QUERY)).build();
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
    {
      "application/json"
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
