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
import mmud.database.entities.game.Admin;
import mmud.database.entities.items.ItemDefinition;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminItemDefinition;
import mmud.services.LogService;
import org.apache.commons.lang3.StringUtils;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Transactional
@Path("/administration/items")
public class ItemsRestService
{

  private static final Logger LOGGER = Logger.getLogger(ItemsRestService.class.getName());

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
  public void create(String json)
  {
    AdminItemDefinition adminItemDefinition = AdminItemDefinition.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    ItemDefinition item = new ItemDefinition();
    item.setId(adminItemDefinition.id);
    item.setName(adminItemDefinition.name);
    item.setAdjectives(adminItemDefinition.adjectives);
//    item.setManaincrease(adminItem.manaincrease);
//    item.setHitincrease(adminItem.hitincrease);
//    item.setVitalincrease(adminItem.vitalincrease);
//    item.setMovementincrease(adminItem.movementincrease);
//    item.setPasdefense(adminItem.pasdefense);
//    item.setDamageresistance(adminItem.damageresistance);
    item.setEatable(StringUtils.stripToNull(adminItemDefinition.eatable));
    item.setDrinkable(StringUtils.stripToNull(adminItemDefinition.drinkable));
    item.setLightable(adminItemDefinition.lightable);
    item.setGetable(adminItemDefinition.getable);
    item.setDropable(adminItemDefinition.dropable);
    item.setVisible(adminItemDefinition.visible);
    item.setDescription(adminItemDefinition.description);
    item.setReaddescription(StringUtils.stripToNull(adminItemDefinition.readdescr));
    item.setWieldable(StringUtils.stripToNull(adminItemDefinition.wieldable));
    item.setWearable(StringUtils.stripToNull(adminItemDefinition.wearable));
    item.setCopper(adminItemDefinition.copper);
    item.setWeight(adminItemDefinition.weight);
    item.setContainer(adminItemDefinition.container);
    item.setCapacity(adminItemDefinition.capacity);
    item.setOpenable(adminItemDefinition.isopenable);
    if (adminItemDefinition.keyid != null)
    {
      ItemDefinition key = getEntityManager().find(ItemDefinition.class, adminItemDefinition.keyid);
      if (key == null)
      {
        throw new MudWebException(name, "Item definition for key of item " + item.getId() + " not found.", Response.Status.NOT_FOUND);
      }
      item.setKey(key);
    }
    item.setContaintype(adminItemDefinition.containtype);
    item.setNotes(StringUtils.stripToNull(adminItemDefinition.notes));
    item.setImage(StringUtils.stripToNull(adminItemDefinition.image));
    item.setTitle(StringUtils.stripToNull(adminItemDefinition.title));
    item.setDiscriminator(adminItemDefinition.discriminator);
    item.setBound(adminItemDefinition.bound);
    item.setCreation(LocalDateTime.now());
    item.setOwner(admin);
    ValidationUtils.checkValidation(name, item);
    getEntityManager().persist(item);
    logService.writeDeputyLog(admin, "New item definition '" + item.getId() + "' created.");
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public void edit(@PathParam("id") Long id, String json)
  {
    AdminItemDefinition adminItemDefinition = AdminItemDefinition.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminItemDefinition.id))
    {
      throw new MudWebException(name, "Item definition ids do not match.", Response.Status.BAD_REQUEST);
    }
    ItemDefinition item = getEntityManager().find(ItemDefinition.class, adminItemDefinition.id);

    if (item == null)
    {
      throw new MudWebException(name, "Item definition " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, item);
    item.setName(adminItemDefinition.name);
    item.setAdjectives(adminItemDefinition.adjectives);
//    item.setManaincrease(adminItem.manaincrease);
//    item.setHitincrease(adminItem.hitincrease);
//    item.setVitalincrease(adminItem.vitalincrease);
//    item.setMovementincrease(adminItem.movementincrease);
//    item.setPasdefense(adminItem.pasdefense);
//    item.setDamageresistance(adminItem.damageresistance);
    item.setEatable(StringUtils.stripToNull(adminItemDefinition.eatable));
    item.setDrinkable(StringUtils.stripToNull(adminItemDefinition.drinkable));
    item.setLightable(adminItemDefinition.lightable);
    item.setGetable(adminItemDefinition.getable);
    item.setDropable(adminItemDefinition.dropable);
    item.setVisible(adminItemDefinition.visible);
    try
    {
      item.setWearable(StringUtils.stripToNull(adminItemDefinition.wearable));
    } catch (IllegalArgumentException e)
    {
      throw new MudWebException(name, "Wearable " + adminItemDefinition.wearable + " not proper.", Response.Status.BAD_REQUEST);
    }
    try
    {
      item.setWieldable(StringUtils.stripToNull(adminItemDefinition.wieldable));
    } catch (IllegalArgumentException e)
    {
      throw new MudWebException(name, "Wieldable " + adminItemDefinition.wieldable + " not proper.", Response.Status.BAD_REQUEST);
    }
    item.setDescription(adminItemDefinition.description);
    item.setReaddescription(StringUtils.stripToNull(adminItemDefinition.readdescr));
    item.setCopper(adminItemDefinition.copper);
    item.setWeight(adminItemDefinition.weight);
    item.setContainer(adminItemDefinition.container);
    item.setCapacity(adminItemDefinition.capacity);
    item.setOpenable(adminItemDefinition.isopenable);
    if (adminItemDefinition.keyid != null)
    {
      ItemDefinition key = getEntityManager().find(ItemDefinition.class, adminItemDefinition.keyid);
      if (key == null)
      {
        throw new MudWebException(name, "Item definition for key of item " + item.getId() + " not found.", Response.Status.NOT_FOUND);
      }
      item.setKey(key);
    }
    item.setContaintype(adminItemDefinition.containtype);
    item.setNotes(StringUtils.stripToNull(adminItemDefinition.notes));
    item.setImage(StringUtils.stripToNull(adminItemDefinition.image));
    item.setTitle(StringUtils.stripToNull(adminItemDefinition.title));
    if (adminItemDefinition.bound != null)
    {
      item.setBound(adminItemDefinition.bound);
    }
    item.setOwner(OwnerHelper.getNewOwner(adminItemDefinition.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, item);
    logService.writeDeputyLog(admin, "Item definition '" + item.getId() + "' updated.");
  }

  @DELETE
  @Path("{id}")

  public void remove(@PathParam("id") String id)
  {
    final String name = sc.getUserPrincipal().getName();
    final ItemDefinition item = getEntityManager().find(ItemDefinition.class, id);
    if (item == null)
    {
      throw new MudWebException(name, "Item definition " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, item);
    getEntityManager().remove(item);
    logService.writeDeputyLog(admin, "Item definition '" + item.getId() + "' deleted.");
  }

  @GET
  @Path("{id}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })

  public String find(@PathParam("id") Long id)
  {
    final String name = sc.getUserPrincipal().getName();
    ItemDefinition item = getEntityManager().find(ItemDefinition.class, id);
    if (item == null)
    {
      throw new MudWebException(name, "Item definition " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminItemDefinition(item).toJson();
  }

  @GET
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminItemDefinition.GET_QUERY)).build();
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })

  public String findRange(@Context UriInfo info, @PathParam("offset") Integer offset,
                          @PathParam("pageSize") Integer pageSize
  )
  {
    List<String> items = getEntityManager().createNativeQuery(AdminItemDefinition.GET_QUERY)
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
    return String.valueOf(getEntityManager().createNamedQuery("ItemDefinition.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
