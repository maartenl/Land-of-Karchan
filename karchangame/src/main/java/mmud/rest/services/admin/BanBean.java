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

import mmud.database.entities.game.Admin;
import mmud.database.entities.game.BanTable;
import mmud.database.entities.game.BannedName;
import mmud.database.entities.game.SillyName;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.bans.AdminBannedIP;
import mmud.rest.webentities.admin.bans.AdminBannedName;
import mmud.rest.webentities.admin.bans.AdminSillyName;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@LocalBean
@Path("/administration/ban")
public class BanBean
{
  private static final Logger LOGGER = Logger.getLogger(BanBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  @GET
  @Path("bannedips")
  @Produces(
    {
      "application/json"
    })
  public String findAllBannedIPS(@Context UriInfo info)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminBannedIP.GET_QUERY)
      .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @GET
  @Path("bannednames")
  @Produces(
    {
      "application/json"
    })
  public String findAllBannedNames(@Context UriInfo info)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminBannedName.GET_QUERY)
      .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @GET
  @Path("sillynames")
  @Produces(
    {
      "application/json"
    })
  public String findAllSillyNames(@Context UriInfo info)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminSillyName.GET_QUERY)
      .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @POST
  @Consumes(
    {
      "application/json"
    })
  @Path("bannedips")
  public void createBannedIP(String json, @Context SecurityContext sc)
  {
    AdminBannedIP adminItem = AdminBannedIP.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    final BanTable existingItem = getEntityManager().find(BanTable.class, adminItem.address);
    if (existingItem != null)
    {
      throw new MudWebException(name, "Banned IP " + adminItem.address + " already exists.", Response.Status.PRECONDITION_FAILED);
    }

    BanTable item = new BanTable();
    item.setAddress(adminItem.address);
    item.setDate(LocalDateTime.now());
    item.setDays(adminItem.days);
    item.setDeputy(admin.getName());
    item.setIp(adminItem.IP);
    item.setName(adminItem.name);
    item.setReason(adminItem.reason);
    ValidationUtils.checkValidation(name, item);
    getEntityManager().persist(item);
    logBean.writeDeputyLog(admin, "New banned IP '" + item.getAddress() + "' created.");
  }


  @POST
  @Consumes(
    {
      "application/json"
    })
  @Path("bannednames")
  public void createBannedName(String json, @Context SecurityContext sc)
  {
    AdminBannedName adminItem = AdminBannedName.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    final BannedName existingItem = getEntityManager().find(BannedName.class, adminItem.name);
    if (existingItem != null)
    {
      throw new MudWebException(name, "Banned name " + adminItem.name + " already exists.", Response.Status.PRECONDITION_FAILED);
    }

    BannedName item = new BannedName();
    item.setCreation(LocalDateTime.now());
    item.setDays(adminItem.days);
    item.setDeputy(admin.getName());
    item.setName(adminItem.name);
    item.setReason(adminItem.reason);
    ValidationUtils.checkValidation(name, item);
    getEntityManager().persist(item);
    logBean.writeDeputyLog(admin, "New banned name '" + item.getName() + "' created.");
  }

  @POST
  @Consumes(
    {
      "application/json"
    })
  @Path("sillynames")
  public void createSillyName(String json, @Context SecurityContext sc)
  {
    AdminSillyName adminItem = AdminSillyName.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    final SillyName existingItem = getEntityManager().find(SillyName.class, adminItem.name);
    if (existingItem != null)
    {
      throw new MudWebException(name, "Silly name " + adminItem.name + " already exists.", Response.Status.PRECONDITION_FAILED);
    }

    SillyName item = new SillyName();
    item.setName(adminItem.name);
    ValidationUtils.checkValidation(name, item);
    getEntityManager().persist(item);
    logBean.writeDeputyLog(admin, "New silly name '" + item.getName() + "' created.");
  }

  @DELETE
  @Path("bannedips/{address}")
  public void removeBannedIP(@PathParam("address") String address, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final BanTable item = getEntityManager().find(BanTable.class, address);
    if (item == null)
    {
      throw new MudWebException(name, "Banned IP " + address + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = getEntityManager().find(Admin.class, name);
    if (admin == null)
    {
      throw new MudWebException(name, "Administrator " + name + " not found.", Response.Status.UNAUTHORIZED);
    }
    getEntityManager().remove(item);
    logBean.writeDeputyLog(admin, "Banned IP '" + item.getAddress() + "' deleted.");
  }

  @DELETE
  @Path("bannednames/{name}")
  public void removeBannedName(@PathParam("name") String bannedName, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final BannedName item = getEntityManager().find(BannedName.class, bannedName);
    if (item == null)
    {
      throw new MudWebException(name, "Banned name " + bannedName + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = getEntityManager().find(Admin.class, name);
    if (admin == null)
    {
      throw new MudWebException(name, "Administrator " + name + " not found.", Response.Status.UNAUTHORIZED);
    }
    getEntityManager().remove(item);
    logBean.writeDeputyLog(admin, "Banned name '" + item.getName() + "' deleted.");
  }

  @DELETE
  @Path("sillynames/{name}")
  public void removeSillyName(@PathParam("name") String sillyName, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final SillyName item = getEntityManager().find(SillyName.class, sillyName);
    if (item == null)
    {
      throw new MudWebException(name, "Silly name " + sillyName + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = getEntityManager().find(Admin.class, name);
    if (admin == null)
    {
      throw new MudWebException(name, "Administrator " + name + " not found.", Response.Status.UNAUTHORIZED);
    }
    getEntityManager().remove(item);
    logBean.writeDeputyLog(admin, "Silly name '" + item.getName() + "' deleted.");
  }

  private EntityManager getEntityManager()
  {
    return em;
  }
}
