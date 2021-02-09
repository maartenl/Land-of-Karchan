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
import mmud.database.entities.items.ItemDefinition;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminItem;

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
@Path("/administration/items")
public class ItemsBean //implements AdminRestService<String>
{

  private static final Logger LOGGER = Logger.getLogger(ItemsBean.class.getName());

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
    AdminItem adminItem = AdminItem.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    ItemDefinition item = new ItemDefinition();
    item.setName(adminItem.name);
    item.setAdject1(adminItem.adject1);
    item.setAdject2(adminItem.adject2);
    item.setAdject3(adminItem.adject3);
//    item.setManaincrease(adminItem.manaincrease);
//    item.setHitincrease(adminItem.hitincrease);
//    item.setVitalincrease(adminItem.vitalincrease);
//    item.setMovementincrease(adminItem.movementincrease);
//    item.setPasdefense(adminItem.pasdefense);
//    item.setDamageresistance(adminItem.damageresistance);
    item.setEatable(adminItem.eatable);
    item.setDrinkable(adminItem.drinkable);
    item.setLightable(adminItem.lightable);
    item.setGetable(adminItem.getable);
    item.setDropable(adminItem.dropable);
    item.setVisible(adminItem.visible);
//    item.getWieldable()
    item.setDescription(adminItem.description);
    item.setReaddescription(adminItem.readdescr);
//    item.setWearable()
    item.setCopper(adminItem.copper);
    item.setWeight(adminItem.weight);
    item.setContainer(adminItem.container);
    item.setCapacity(adminItem.capacity);
    item.setOpenable(adminItem.isopenable);
    if (adminItem.keyid != null)
    {
      ItemDefinition key = getEntityManager().find(ItemDefinition.class, adminItem.keyid);
      if (key == null)
      {
        throw new MudWebException(name, "Item definition for key of item " + item.getId() + " not found.", Response.Status.NOT_FOUND);
      }
      item.setKey(key);
    }
    item.setContaintype(adminItem.containtype);
    item.setNotes(adminItem.notes);
    item.setImage(adminItem.image);
    item.setTitle(adminItem.title);
    item.setDiscriminator(adminItem.discriminator);
    item.setBound(adminItem.bound);
    item.setCreation(LocalDateTime.now());
    item.setOwner(admin);
    ValidationUtils.checkValidation(name, item);
    getEntityManager().persist(item);
    logBean.writeDeputyLog(admin, "New item definition '" + item.getId() + "' created.");
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      "application/json"
    })
  public void edit(@PathParam("id") Long id, String json, @Context SecurityContext sc)
  {
    AdminItem adminItem = AdminItem.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminItem.id))
    {
      throw new MudWebException(name, "Item definition ids do not match.", Response.Status.BAD_REQUEST);
    }
    ItemDefinition item = getEntityManager().find(ItemDefinition.class, adminItem.id);

    if (item == null)
    {
      throw new MudWebException(name, "Item definition " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, item);
    item.setName(adminItem.name);
    item.setAdject1(adminItem.adject1);
    item.setAdject2(adminItem.adject2);
    item.setAdject3(adminItem.adject3);
//    item.setManaincrease(adminItem.manaincrease);
//    item.setHitincrease(adminItem.hitincrease);
//    item.setVitalincrease(adminItem.vitalincrease);
//    item.setMovementincrease(adminItem.movementincrease);
//    item.setPasdefense(adminItem.pasdefense);
//    item.setDamageresistance(adminItem.damageresistance);
    item.setEatable(adminItem.eatable);
    item.setDrinkable(adminItem.drinkable);
    item.setLightable(adminItem.lightable);
    item.setGetable(adminItem.getable);
    item.setDropable(adminItem.dropable);
    item.setVisible(adminItem.visible);
//    item.getWieldable()
    item.setDescription(adminItem.description);
    item.setReaddescription(adminItem.readdescr);
//    item.setWearable()
    item.setCopper(adminItem.copper);
    item.setWeight(adminItem.weight);
    item.setContainer(adminItem.container);
    item.setCapacity(adminItem.capacity);
    item.setOpenable(adminItem.isopenable);
    if (adminItem.keyid != null)
    {
      ItemDefinition key = getEntityManager().find(ItemDefinition.class, adminItem.keyid);
      if (key == null)
      {
        throw new MudWebException(name, "Item definition for key of item " + item.getId() + " not found.", Response.Status.NOT_FOUND);
      }
      item.setKey(key);
    }
    item.setContaintype(adminItem.containtype);
    item.setNotes(adminItem.notes);
    item.setImage(adminItem.image);
    item.setTitle(adminItem.title);
    if (adminItem.bound != null)
    {
      item.setBound(adminItem.bound);
    }
    item.setOwner(OwnerHelper.getNewOwner(adminItem.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, item);
    logBean.writeDeputyLog(admin, "Item definition '" + item.getId() + "' updated.");
  }

  @DELETE
  @Path("{id}")

  public void remove(@PathParam("id") String id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final ItemDefinition item = getEntityManager().find(ItemDefinition.class, id);
    if (item == null)
    {
      throw new MudWebException(name, "Item definition " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, item);
    getEntityManager().remove(item);
    logBean.writeDeputyLog(admin, "Item definition '" + item.getId() + "' deleted.");
  }

  @GET
  @Path("{id}")
  @Produces(
    {
      "application/json"
    })

  public String find(@PathParam("id") Long id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    ItemDefinition item = getEntityManager().find(ItemDefinition.class, id);
    if (item == null)
    {
      throw new MudWebException(name, "Item definition " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminItem(item).toJson();
  }

  @GET
  @Produces(
    {
      "application/json"
    })
  public String findAll(@Context UriInfo info)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminItem.GET_QUERY)
      .getResultList();
    return "[" + String.join(",", items) + "]";
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
    List<String> items = getEntityManager().createNativeQuery(AdminItem.GET_QUERY)
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
