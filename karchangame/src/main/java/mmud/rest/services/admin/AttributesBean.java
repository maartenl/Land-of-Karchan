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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Attribute;
import mmud.database.entities.game.Charattribute;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminAttribute;
import mmud.rest.webentities.admin.AdminMudType;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/attributes")
public class AttributesBean
{

  private static final Logger LOGGER = Logger.getLogger(AttributesBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  @PUT
  @Path("byType/{type}/{objectid}")
  @Consumes(
    {
      "application/json"
    })
  public void edit(@PathParam("type") String type, @PathParam("objectid") String objectid, String json, @Context SecurityContext sc)
  {
    AdminAttribute adminAttribute = AdminAttribute.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    AdminMudType adminMudType = getAdminMudType(type);
    Attribute attribute;
    switch (adminMudType)
    {
      case PERSON:
        TypedQuery<Charattribute> query = getEntityManager().createNamedQuery("Charattribute.findByNameAndPerson", Charattribute.class);
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
          logBean.writeDeputyLog(admin, "Charattribute '" + attribute.getName() + "' created for person " + objectid + ".");
        } else
        {
          logBean.writeDeputyLog(admin, "Charattribute '" + attribute.getName() + "' updated for person " + objectid + ".");
        }
        break;
      default:
        throw new MudWebException(name, "Type of attribute not supported.", Response.Status.BAD_REQUEST);
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
      "application/json"
    })
  public void remove(@PathParam("type") String type, @PathParam("id") Long id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    AdminMudType adminMudType = getAdminMudType(type);
    switch (adminMudType)
    {
      case PERSON:
        Charattribute attribute = getEntityManager().find(Charattribute.class, id);
        if (attribute == null)
        {
          throw new MudWebException(name, "Charattribute " + id + "' not found.", Response.Status.NOT_FOUND);
        }
        getEntityManager().remove(attribute);
        logBean.writeDeputyLog(admin, "Charattribute '" + attribute.getName() + "' deleted for person " + attribute.getPerson().getName() + ".");
        break;
      default:
        throw new MudWebException(name, "Type of attribute not supported.", Response.Status.BAD_REQUEST);
    }
  }

  @DELETE
  @Path("byName/{name}")
  @Consumes(
    {
      "application/json"
    })
  public void remove(@PathParam("name") String name, @Context SecurityContext sc)
  {
    final String adminName = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, adminName);
    int results =
      getEntityManager().createNamedQuery("Charattribute.deleteByName", Charattribute.class).setParameter("name", adminName).executeUpdate();
    logBean.writeDeputyLog(admin, results + " charattributes with name '" + adminName + "' deleted for persons.");
  }

  @GET
  @Path("byType/{type}/{objectid}")
  @Produces(
    {
      "application/json"
    })
  public String find(@PathParam("type") String type, @PathParam("objectid") String objectid, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    AdminMudType adminMudType = getAdminMudType(type);
    List<Attribute> list;
    switch (adminMudType)
    {
      case PERSON:
        TypedQuery<Charattribute> query = getEntityManager().createNamedQuery("Charattribute.findByPerson", Charattribute.class);
        query.setParameter("person", objectid);
        list = new ArrayList<>(query.getResultList());
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
      "application/json"
    })
  public String find(@PathParam("name") String name, @Context SecurityContext sc)
  {
    final String adminname = sc.getUserPrincipal().getName();
    List<AdminAttribute> attribs = new ArrayList<>();
    attribs.addAll(getEntityManager().createNamedQuery("Charattribute.findByName", Charattribute.class)
      .setParameter("name", name)
      .getResultList()
      .stream()
      .map(x -> new AdminAttribute(x, AdminMudType.PERSON, x.getPerson().getName()))
      .collect(Collectors.toList()));

    return "[" + attribs.stream()
      .map(AdminAttribute::toJson)
      .collect(Collectors.joining(",")) + "]";
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}

