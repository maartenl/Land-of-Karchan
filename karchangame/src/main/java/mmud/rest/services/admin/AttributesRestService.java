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
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Attribute;
import mmud.database.entities.game.Charattribute;
import mmud.database.entities.game.Room;
import mmud.database.entities.game.Roomattribute;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.Itemattribute;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminAttribute;
import mmud.rest.webentities.admin.AdminMudType;
import mmud.services.LogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Transactional
@Path("/administration/attributes")
public class AttributesRestService
{

  private static final Logger LOGGER = Logger.getLogger(AttributesRestService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogService logService;

  @Context
  private SecurityContext sc;

  @PUT
  @Path("byType/{type}/{objectid}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public void edit(@PathParam("type") String type, @PathParam("objectid") String objectid, String json)
  {
    AdminAttribute adminAttribute = AdminAttribute.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    AdminMudType adminMudType = getAdminMudType(type);
    switch (adminMudType)
    {
      case PERSON:
        editCharattribute(objectid, adminAttribute, name, admin);
        break;
      case ITEM:
        editItemattribute(objectid, adminAttribute, name, admin);
        break;
      case ROOM:
        editRoomattribute(objectid, adminAttribute, name, admin);
        break;
      default:
        throw new MudWebException(name, "Type of attribute not supported.", Response.Status.BAD_REQUEST);
    }
  }

  private void editItemattribute(String objectid, AdminAttribute adminAttribute, String name, Admin admin)
  {
    Attribute attribute;
    TypedQuery<Itemattribute> query = getEntityManager().createNamedQuery("Itemattribute.findByNameAndItemid",
      Itemattribute.class);
    query.setParameter("name", adminAttribute.name);
    query.setParameter("itemid", Long.valueOf(objectid));
    Optional<Itemattribute> first = query.getResultList().stream().findFirst();
    if (first.isEmpty())
    {
      Item item = getEntityManager().find(Item.class, Long.valueOf(objectid));
      if (item == null)
      {
        throw new MudWebException(name, "Item " + objectid + " not found", Response.Status.NOT_FOUND);
      }
      Itemattribute itemattribute = new Itemattribute(adminAttribute.name, item);
      itemattribute.setValue(adminAttribute.value);
      itemattribute.setValueType(adminAttribute.valueType);
      getEntityManager().persist(itemattribute);
      attribute = itemattribute;
    } else
    {
      attribute = first.get();
      attribute.setValue(adminAttribute.value);
      attribute.setValueType(adminAttribute.valueType);
    }
    ValidationUtils.checkValidation(name, attribute);
    if (attribute.getAttributeId() == null)
    {
      logService.writeDeputyLog(admin, "Itemattribute '" + attribute.getName() + "' created for item " + objectid +
        ".");
    } else
    {
      logService.writeDeputyLog(admin, "Itemattribute '" + attribute.getName() + "' updated for item " + objectid +
        ".");
    }
  }

  private void editRoomattribute(String objectid, AdminAttribute adminAttribute, String name, Admin admin)
  {
    Attribute attribute;
    TypedQuery<Roomattribute> query = getEntityManager().createNamedQuery("Roomattribute.findByNameAndRoomid",
      Roomattribute.class);
    query.setParameter("name", adminAttribute.name);
    query.setParameter("roomid", Long.valueOf(objectid));
    Optional<Roomattribute> first = query.getResultList().stream().findFirst();
    if (first.isEmpty())
    {
      Room room = getEntityManager().find(Room.class, Long.valueOf(objectid));
      if (room == null)
      {
        throw new MudWebException(name, "Room " + objectid + " not found", Response.Status.NOT_FOUND);
      }
      Roomattribute roomattribute = new Roomattribute(adminAttribute.name, room);
      roomattribute.setValue(adminAttribute.value);
      roomattribute.setValueType(adminAttribute.valueType);
      getEntityManager().persist(roomattribute);
      attribute = roomattribute;
    } else
    {
      attribute = first.get();
      attribute.setValue(adminAttribute.value);
      attribute.setValueType(adminAttribute.valueType);
    }
    ValidationUtils.checkValidation(name, attribute);
    if (attribute.getAttributeId() == null)
    {
      logService.writeDeputyLog(admin, "Roomattribute '" + attribute.getName() + "' created for room " + objectid +
        ".");
    } else
    {
      logService.writeDeputyLog(admin, "Roomattribute '" + attribute.getName() + "' updated for room " + objectid +
        ".");
    }
  }

  private void editCharattribute(String objectid, AdminAttribute adminAttribute, String name, Admin admin)
  {
    Attribute attribute;
    TypedQuery<Charattribute> query = getEntityManager().createNamedQuery("Charattribute.findByNameAndPerson",
      Charattribute.class);
    query.setParameter("name", adminAttribute.name);
    query.setParameter("person", objectid);
    Optional<Charattribute> first = query.getResultList().stream().findFirst();
    if (first.isEmpty())
    {
      Person person = getEntityManager().find(Person.class, objectid);
      if (person == null)
      {
        throw new MudWebException(name, "Person " + objectid + " not found", Response.Status.NOT_FOUND);
      }
      Charattribute charattribute = new Charattribute(adminAttribute.name, person);
      charattribute.setValue(adminAttribute.value);
      charattribute.setValueType(adminAttribute.valueType);
      getEntityManager().persist(charattribute);
      attribute = charattribute;
    } else
    {
      attribute = first.get();
      attribute.setValue(adminAttribute.value);
      attribute.setValueType(adminAttribute.valueType);
    }
    ValidationUtils.checkValidation(name, attribute);
    if (attribute.getAttributeId() == null)
    {
      logService.writeDeputyLog(admin,
        "Charattribute '" + attribute.getName() + "' created for person " + objectid + ".");
    } else
    {
      logService.writeDeputyLog(admin,
        "Charattribute '" + attribute.getName() + "' updated for person " + objectid + ".");
    }
  }

  private AdminMudType getAdminMudType(String type)
  {
    try
    {
      return AdminMudType.valueOf(type);
    } catch (IllegalArgumentException e)
    {
      throw new MudWebException(e, Response.Status.BAD_REQUEST);
    }
  }

  @DELETE
  @Path("byId/{type}/{id}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public void remove(@PathParam("type") String type, @PathParam("id") Long id)
  {
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    AdminMudType adminMudType = getAdminMudType(type);
    switch (adminMudType)
    {
      case PERSON:
        Charattribute charattribute = getEntityManager().find(Charattribute.class, id);
        if (charattribute == null)
        {
          throw new MudWebException(name, "Charattribute " + id + "' not found.", Response.Status.NOT_FOUND);
        }
        getEntityManager().remove(charattribute);
        logService.writeDeputyLog(admin,
          "Charattribute '" + charattribute.getName() + "' deleted for person " + charattribute.getPerson().getName() + ".");
        break;
      case ITEM:
        Itemattribute itemattribute = getEntityManager().find(Itemattribute.class, id);
        if (itemattribute == null)
        {
          throw new MudWebException(name, "Itemattribute " + id + "' not found.", Response.Status.NOT_FOUND);
        }
        getEntityManager().remove(itemattribute);
        logService.writeDeputyLog(admin,
          "Itemattribute '" + itemattribute.getName() + "' deleted for item " + itemattribute.getItem().getId() + ".");
        break;
      case ROOM:
        Roomattribute roomattribute = getEntityManager().find(Roomattribute.class, id);
        if (roomattribute == null)
        {
          throw new MudWebException(name, "Roomattribute " + id + "' not found.", Response.Status.NOT_FOUND);
        }
        getEntityManager().remove(roomattribute);
        logService.writeDeputyLog(admin,
          "Roomattribute '" + roomattribute.getName() + "' deleted for person " + roomattribute.getRoom().getId() +
            ".");
        break;
      default:
        throw new MudWebException(name, "Type of attribute not supported.", Response.Status.BAD_REQUEST);
    }
  }

  @DELETE
  @Path("byName/{name}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public void remove(@PathParam("name") String name)
  {
    final String adminName = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, adminName);
    int results =
      getEntityManager().createNamedQuery("Charattribute.deleteByName", Charattribute.class).setParameter("name",
        adminName).executeUpdate();
    logService.writeDeputyLog(admin, results + " charattributes with name '" + adminName + "' deleted for persons.");
  }

  @GET
  @Path("byType/{type}/{objectid}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public String find(@PathParam("type") String type, @PathParam("objectid") String objectid)
  {
    final String name = sc.getUserPrincipal().getName();
    AdminMudType adminMudType = getAdminMudType(type);
    List<Attribute> list;
    switch (adminMudType)
    {
      case PERSON:
        TypedQuery<Charattribute> charquery = getEntityManager().createNamedQuery("Charattribute.findByPerson",
          Charattribute.class);
        charquery.setParameter("person", objectid);
        list = new ArrayList<>(charquery.getResultList());
        break;
      case ITEM:
        TypedQuery<Itemattribute> itemquery = getEntityManager().createNamedQuery("Itemattribute.findByItemid",
          Itemattribute.class);
        itemquery.setParameter("itemid", Long.valueOf(objectid));
        list = new ArrayList<>(itemquery.getResultList());
        break;
      case ROOM:
        TypedQuery<Roomattribute> roomquery = getEntityManager().createNamedQuery("Roomattribute.findByRoomid",
          Roomattribute.class);
        roomquery.setParameter("roomid", Long.valueOf(objectid));
        list = new ArrayList<>(roomquery.getResultList());
        break;
      default:
        throw new MudWebException(name, "Type of attribute not supported.", Response.Status.BAD_REQUEST);
    }
    return "[" + list.stream()
      .map(x -> new AdminAttribute(x, adminMudType))
      .map(AdminAttribute::toJson)
      .collect(Collectors.joining(",")) + "]";
  }

  @GET
  @Path("byName/{name}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public String find(@PathParam("name") String name)
  {
    List<AdminAttribute> attribs = new ArrayList<>();
    attribs.addAll(getEntityManager().createNamedQuery("Charattribute.findByName", Charattribute.class)
      .setParameter("name", name)
      .getResultList()
      .stream()
      .map(x -> new AdminAttribute(x, AdminMudType.PERSON, x.getPerson().getName()))
      .toList());
    attribs.addAll(getEntityManager().createNamedQuery("Itemattribute.findByName", Itemattribute.class)
      .setParameter("name", name)
      .getResultList()
      .stream()
      .map(x -> new AdminAttribute(x, AdminMudType.ITEM, x.getItem().getId().toString()))
      .toList());
    attribs.addAll(getEntityManager().createNamedQuery("Roomattribute.findByName", Roomattribute.class)
      .setParameter("name", name)
      .getResultList()
      .stream()
      .map(x -> new AdminAttribute(x, AdminMudType.ROOM, x.getRoom().getId().toString()))
      .toList());

    return "[" + attribs.stream()
      .map(AdminAttribute::toJson)
      .collect(Collectors.joining(",")) + "]";
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}

