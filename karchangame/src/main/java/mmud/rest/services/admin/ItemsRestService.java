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
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.Shopkeeper;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.database.entities.items.ShopkeeperList;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminItem;
import mmud.services.LogService;

import java.time.LocalDateTime;
import java.util.logging.Logger;

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
    AdminItem adminItem = AdminItem.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    Room room = adminItem.room() == null ? null : getEntityManager().find(Room.class, adminItem.room());
    Item container = adminItem.containerid() == null ? null : getEntityManager().find(Item.class,
        adminItem.containerid());
    Person belongsto = adminItem.belongsto() == null ? null : getEntityManager().find(Person.class,
        adminItem.belongsto());
    Shopkeeper shopkeeper = adminItem.shopkeeper() == null ? null : getEntityManager().find(Shopkeeper.class,
        adminItem.shopkeeper());
    ItemDefinition itemDefinition = getEntityManager().find(ItemDefinition.class, adminItem.itemid());
    if (itemDefinition == null)
    {
      throw new MudWebException(name, "Item definition " + adminItem.itemid() + " not found.",
          Response.Status.NOT_FOUND);
    }
    Item item;
    if (adminItem.discriminator().equals(0))
    {
      item = new NormalItem(itemDefinition);
    } else
    {
      var shopkeeperItem = new ShopkeeperList(itemDefinition);
      shopkeeperItem.setShopkeeper(shopkeeper);
      if (shopkeeper == null)
      {
        throw new MudWebException(name, "Shopkeeper " + adminItem.shopkeeper() + " not found.",
            Response.Status.NOT_FOUND);
      }
      item = shopkeeperItem;
    }
    item.assignTo(room, belongsto, container);
    item.setCreation(LocalDateTime.now());
    item.setOwner(admin);
    ValidationUtils.checkValidation(name, item);
    getEntityManager().persist(item);
    logService.writeDeputyLog(admin, "New item '" + item.getId() + "' created.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Integer id)
  {
    final String name = sc.getUserPrincipal().getName();
    final Item item = getEntityManager().find(Item.class, id);
    if (item == null)
    {
      throw new MudWebException(name, "Item " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, item);
    getEntityManager().remove(item);
    logService.writeDeputyLog(admin, "Item '" + item.getId() + "' deleted.");
  }

  @GET
  @Path("{id}")
  @Produces(
      {
          MediaType.APPLICATION_JSON
      })
  public String find(@PathParam("id") Integer id)
  {
    final String name = sc.getUserPrincipal().getName();
    Item item = getEntityManager().find(Item.class, id);
    if (item == null)
    {
      throw new MudWebException(name, "Item " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminItem(item).toJson();
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
