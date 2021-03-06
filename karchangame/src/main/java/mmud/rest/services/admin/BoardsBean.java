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
import java.util.stream.Collectors;
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

import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Board;
import mmud.database.entities.game.BoardMessage;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminBoard;
import mmud.rest.webentities.admin.AdminBoardMessage;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/boards")
public class BoardsBean
{

  private static final Logger LOGGER = Logger.getLogger(BoardsBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  @POST
  @Consumes(
    {
      "application/json"
    })
  public Long create(String json, @Context SecurityContext sc)
  {
    AdminBoard adminBoard = AdminBoard.fromJson(json);

    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    Board board = getEntityManager().find(Board.class, adminBoard.id);
    if (board != null)
    {
      throw new MudWebException(name, "Board " + adminBoard.id + " already exists.", Response.Status.PRECONDITION_FAILED);
    }    board = new Board();
    board.setId(adminBoard.id);
    board.setName(adminBoard.name);
    board.setDescription(adminBoard.description);
    if (adminBoard.room != null)
    {
      Room room = getEntityManager().find(Room.class, adminBoard.room);
      if (room == null)
      {
        throw new MudWebException(name, "Room " + adminBoard.room + " not found.", Response.Status.NOT_FOUND);
      }
      board.setRoom(room);
    }
    board.setCreation(LocalDateTime.now());
    board.setOwner(admin);
    ValidationUtils.checkValidation(name, board);
    logBean.writeDeputyLog(admin, "New board '" + board.getName() + "' created.");
    getEntityManager().persist(board);
    return board.getId();
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      "application/xml", "application/json"
    })
  public void edit(@PathParam("id") Long id, String json, @Context SecurityContext sc)
  {
    AdminBoard adminBoard = AdminBoard.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminBoard.id))
    {
      throw new MudWebException(name, "Board ids do not match.", Response.Status.BAD_REQUEST);
    }
    Board board = getEntityManager().find(Board.class, adminBoard.id);
    if (board == null)
    {
      throw new MudWebException(name, id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, board);
    board.setName(adminBoard.name);
    board.setDescription(adminBoard.description);
    if (adminBoard.room != null)
    {
      Room room = getEntityManager().find(Room.class, adminBoard.room);
      if (room == null)
      {
        throw new MudWebException(name, "Room " + adminBoard.room + " not found.", Response.Status.NOT_FOUND);
      }
      board.setRoom(room);
    } else
    {
      board.setRoom(null);
    }
    board.setOwner(OwnerHelper.getNewOwner(adminBoard.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, board);
    logBean.writeDeputyLog(admin, "Board '" + board.getName() + "' updated.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Long id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Board board = getEntityManager().find(Board.class, id);
    if (board == null)
    {
      throw new MudWebException(name, "Board " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, board);
    getEntityManager().remove(board);
    logBean.writeDeputyLog(admin, "Board '" + board.getName() + "' deleted.");
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
    final Board board = getEntityManager().find(Board.class, id);
    if (board == null)
    {
      throw new MudWebException(name, "Board " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminBoard(board).toJson();
  }


  @GET
  @Path("{id}/messages")
  @Produces(
    {
      "application/json"
    })
  public String findRecentMessages(@PathParam("id") Long id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Board board = getEntityManager().find(Board.class, id);
    if (board == null)
    {
      throw new MudWebException(name, "Board " + id + " not found.", Response.Status.NOT_FOUND);
    }
    List<BoardMessage> messages = getEntityManager().createNamedQuery("BoardMessage.recent", BoardMessage.class)
      .setParameter("board", board)
      .setMaxResults(50)
      .getResultList();
    return "[" + messages.stream().map(message -> new AdminBoardMessage(message).toJson()).collect(Collectors.joining(",")) + "]";
  }

  @PUT
  @Path("{id}/messages/{messageid}")
  @Produces(
    {
      "application/json"
    })
  public void editMessage(@PathParam("id") Long id, @PathParam("messageid") Long messageid, String json, @Context SecurityContext sc)
  {
    AdminBoardMessage adminBoardMessage = AdminBoardMessage.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    final Admin admin = getEntityManager().find(Admin.class, name);
    final Board board = getEntityManager().find(Board.class, id);
    if (board == null)
    {
      throw new MudWebException(name, "Board " + id + " not found.", Response.Status.NOT_FOUND);
    }
    final BoardMessage boardmessage = getEntityManager().find(BoardMessage.class, messageid);
    if (boardmessage == null)
    {
      throw new MudWebException(name, "Boardmessage " + messageid + " not found.", Response.Status.NOT_FOUND);
    }
    if (!boardmessage.getBoard().equals(board)) {
      throw new MudWebException(name, "Boardmessage " + messageid + " does not belong to board " + id + ".", Response.Status.NOT_FOUND);
    }
    boardmessage.setRemoved(adminBoardMessage.removed);
    logBean.writeDeputyLog(admin, "Boardmessage '" + messageid + "' updated.");
  }

  @GET
  @Produces(
    {
      "application/json"
    })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminBoard.GET_QUERY)).build();
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
    List<String> items = getEntityManager().createNativeQuery(AdminBoard.GET_QUERY)
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
    return String.valueOf(getEntityManager().createNamedQuery("Board.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
