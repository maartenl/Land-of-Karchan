/*
 *  Copyright (C) 2012 maartenl
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
package mmud.rest.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.Constants;
import mmud.Utils;
import mmud.commands.CommandFactory;
import mmud.commands.CommandFactory.UserCommandInfo;
import mmud.commands.CommandRunner;
import mmud.database.InputSanitizer;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Board;
import mmud.database.entities.game.BoardMessage;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Macro;
import mmud.database.entities.game.MacroPK;
import mmud.database.entities.game.Room;
import mmud.database.entities.game.UserCommand;
import mmud.database.enums.Sex;
import mmud.exceptions.ExceptionUtils;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.PrivateDisplay;
import mmud.rest.webentities.PrivateLog;
import mmud.rest.webentities.PrivatePerson;
import mmud.services.BoardService;
import mmud.services.CommunicationService;
import mmud.services.IdleUsersService;
import mmud.services.LogService;
import mmud.services.MailService;
import mmud.services.PersonCommunicationService;
import mmud.services.PlayerAuthenticationService;
import static mmud.Constants.DATETIME_FORMAT;

/**
 * Takes care of all the game-related functions.
 * <img
 * src="doc-files/Gamebean.png">
 *
 * @author maartenl
 * @startuml doc-files/Gamebean.png
 * (*) --> "Create character"
 * --> "Login"
 * --> "Play"
 * --> "Retrieve log"
 * --> "Play"
 * --> "Quit"
 * -->(*)
 * @enduml
 */
@DeclareRoles("player")
@RolesAllowed("player")
@Path("/game")
@Transactional
public class GameRestService
{

  private final BoardService boardService;

  private final IdleUsersService idleUsersService;

  private final MailService mailService;

  private final CommandRunner commandRunner;

  private final LogService logService;

  private final PlayerAuthenticationService playerAuthenticationService;

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Context
  private SecurityContext context;

  @Inject
  public GameRestService(BoardService boardService, IdleUsersService idleUsersService, MailService mailService, CommandRunner commandRunner, LogService logService, PlayerAuthenticationService playerAuthenticationService)
  {
    this.boardService = boardService;
    this.idleUsersService = idleUsersService;
    this.mailService = mailService;
    this.commandRunner = commandRunner;
    this.logService = logService;
    this.playerAuthenticationService = playerAuthenticationService;
  }

  /**
   * The ip address of the karchan server (vps386.directvps.nl). This address
   * means that the call to the game came from the server itself, and therefore
   * we require access to the {@link #X_FORWARDED_FOR} header.
   */
  public static final String VPS386 = "194.145.201.161";

  /**
   * If the call was forwarded from an Internet proxy, then the original ip is
   * stored in this http header.
   */
  public static final String X_FORWARDED_FOR = "X-Forwarded-For";

  private static final Logger LOGGER = Logger.getLogger(GameRestService.class.getName());

  /**
   * <p>
   * This method should be called to verify that the target of a certain action
   * is indeed a proper authenticated user.</p>
   * <p>
   * <img
   * src="doc-files/Gamebean_authenticate.png"></p>
   *
   * @param name the name to identify the person
   * @throws WebApplicationException NOT_FOUND, if the user is either not found
   *                                 or is not a proper user. BAD_REQUEST if an unexpected exception crops up or
   *                                 provided info is really not proper. UNAUTHORIZED if session passwords do
   *                                 not match.
   * @startuml doc-files/Gamebean_authenticate.png
   * (*) --> "find character"
   * --> "character found"
   * --> "character == user"
   * --> "session password is good"
   * -->(*)
   * @enduml
   */
  private User authenticate(String name)
  {
    playerAuthenticationService.authenticate(name, context);
    User person = getPlayer(name);
    if (!person.isActive())
    {
      throw new MudWebException(name, name + " is not playing the game. Please log in.", Response.Status.UNAUTHORIZED);
    }
    return person;
  }

  /**
   * Will also work if the person is not playing the game, yet. When playing the game, use {@link #authenticate(String)}.
   *
   * @param name the name of the player
   * @return the user
   */
  private User authenticateToEnterGame(String name)
  {
    if (!playerAuthenticationService.getPlayerName(context).equals(name))
    {
      throw new MudWebException(name, "You are not logged in as " + name, Response.Status.UNAUTHORIZED);
    }
    User person = getPlayer(name);
    return person;
  }

  /**
   * <p>
   * This method should be called to verify that the target of a certain action
   * is a user with the appropriate password.</p>
   * <p>
   * <img
   * src="doc-files/Gamebean_authenticateWithPassword.png"></p>
   *
   * @param name the name to identify the person
   * @return the authenticated User
   * @throws WebApplicationException BAD_REQUEST if an unexpected exception
   *                                 crops up or provided info is really not proper. UNAUTHORIZED if session
   *                                 passwords do not match or user not found.
   * @startuml doc-files/Gamebean_authenticateWithPassword.png
   * (*) --> "find character"
   * --> "character found"
   * --> "character == user"
   * --> "password is good"
   * -->(*)
   * @enduml
   */
  protected User getPlayer(String name)
  {
    if (Utils.isOffline())
    {
      throw new MudWebException(name, "Game is offline", Response.Status.NO_CONTENT);
    }
    User person = em.find(User.class, name);
    if (person == null)
    {
      throw new MudWebException(name,
        name + " was not found.",
        "User was not found (" + name + ")",
        Response.Status.NOT_FOUND);
    }
    if (!person.isUser())
    {
      throw new MudWebException(name,
        name + " is not a user.",
        "User was not a user (" + name + ")",
        Response.Status.BAD_REQUEST);
    }
    if (person.getTimeout() > 0)
    {
      throw new MudWebException(name, name + " has been kicked out of the game and "
        + "is not allowed to logon for " + person.getTimeout() + " minutes.",
        Response.Status.FORBIDDEN);
    }
    return person;
  }

  /**
   * <p>
   * Checks to see if a person is banned from playing.</p>
   * <p>
   * <img
   * src="doc-files/Gamebean_isBanned.png"></p>
   *
   * @param name    the name of the person
   * @param address the ip address the person is playing from
   * @return true if banned, false otherwise.
   * @startuml doc-files/Gamebean_isBanned.png
   * (*) --> "check silly names"
   * --> "check unbanned"
   * --> "check address banned"
   * --> "check name banned"
   * -->(*)
   * @enduml
   */
  public boolean isBanned(String name, String address)
  {
    // check silly names
    Query query = em.createNamedQuery("SillyName.findByName");
    query.setParameter("name", name);
    query.setMaxResults(1);
    if (!query.getResultList().isEmpty())
    {
      // silly name found!
      return true;
    }

    // check unbanned names
    query = em.createNamedQuery("UnbanTable.findByName");
    query.setParameter("name", name);
    query.setMaxResults(1);
    if (!query.getResultList().isEmpty())
    {
      // unbanned name found!
      return false;
    }

    // check address banned
    String address2 = "bogushostman";
    try
    {
      InetAddress inetAddress = InetAddress.getByName(address);
      address2 = inetAddress.getHostName();
    } catch (UnknownHostException e)
    {
      // ignore this.
    }
    query = em.createNamedQuery("BanTable.find");
    query.setParameter("address", address);
    query.setParameter("address2", address2);
    query.setMaxResults(1);
    if (!query.getResultList().isEmpty())
    {
      // banned address found!
      return true;
    }

    // check name banned
    query = em.createNamedQuery("BannedName.find");
    query.setParameter("name", name);
    query.setMaxResults(1);
    if (!query.getResultList().isEmpty())
    {
      // banned name found!
      return true;
    }

    return false;
  }

  /**
   * <p>
   * Creates a new character, suitable for playing.</p>
   * <p>
   * <img
   * src="doc-files/Gamebean_create.png"></p> @param requestContext for headers,
   * like remote address.
   *
   * @param requestContext for determining the remote address.
   * @param name           the name of the user
   * @param pperson        the data of the new character
   * @return NO_CONTENT if the game is offline for maintenance.
   * @throws WebApplicationException BAD_REQUEST if an unexpected exception
   *                                 crops up or something could not be validated.
   * @startuml doc-files/Gamebean_create.png
   * (*) --> "check for offline"
   * --> "check presence of data"
   * --> "check name == pperson.name"
   * --> "check password == password2"
   * --> "check isBanned"
   * --> "check already person"
   * --> "create person"
   * -->(*)
   * @enduml
   */
  @PermitAll
  @POST
  @Path("{name}")
  public Response create(@Context HttpServletRequest requestContext, @PathParam("name") String name, PrivatePerson pperson)
  {
    LOGGER.log(Level.FINER, "entering create {0}", name);
    String address = requestContext.getRemoteAddr();
    try
    {

      if (Utils.isOffline())
      {
        throw new MudWebException(name, "Game is offline", Response.Status.NO_CONTENT);
      }
      if (pperson == null)
      {
        throw new MudWebException(name, "No data received.", Response.Status.BAD_REQUEST);
      }
      if (pperson.name == null || !pperson.name.equals(name))
      {
        throw new MudWebException(name, "Wrong data received.", Response.Status.BAD_REQUEST);
      }
      if (pperson.password == null || !pperson.password.equals(pperson.password2))
      {
        throw new MudWebException(name,
          "Passwords do not match.",
          "Passwords do not match.",
          Response.Status.BAD_REQUEST);
      }
      User person = new User();
      person.setName(name);
      person.setNewpassword(pperson.password);
      if (isBanned(name, address))
      {
        // is banned
        throw new MudWebException(name,
          name + " was banned.",
          "User was banned (" + name + ", " + address + ")",
          Response.Status.FORBIDDEN);
      }
      Person foundPerson = em.find(Person.class, name);
      if (foundPerson != null)
      {
        // already a person
        throw new MudWebException(name,
          name + " already exists.",
          "User already exists (" + name + ", " + address + ")",
          Response.Status.BAD_REQUEST);
      }
      // everything's cool! Let's do this!
      person.setActive(false);
      person.setTitle(pperson.title);
      person.setRealname(pperson.realname);
      person.setEmail(pperson.email);
      person.setAddress(address);

      person.setSex(Sex.createFromString(pperson.sex));
      person.setRace(pperson.race);
      person.setAge(pperson.age);
      person.setHeight(pperson.height);
      person.setWidth(pperson.width);
      person.setComplexion(pperson.complexion);
      person.setEyes(pperson.eyes);
      person.setFace(pperson.face);
      person.setHair(pperson.hair);
      person.setBeard(pperson.beard);
      person.setArm(pperson.arm);
      person.setLeg(pperson.leg);
      person.setBirth(LocalDateTime.now());
      person.setOoc(true);
      person.setCreation(LocalDateTime.now());
      person.setRoom(em.find(Room.class, Room.STARTERS_ROOM));

      em.persist(person);
      // TODO automatically add a welcome mail.
      logService.writeLog(person, "character created.");
    } catch (MudWebException e)
    {
      //ignore
      throw e;
    } catch (ConstraintViolationException e)
    {
      throw new MudWebException(name, ExceptionUtils.createMessage(e), e, Response.Status.BAD_REQUEST);

    } catch (jakarta.persistence.PersistenceException f)
    {
      ExceptionUtils.createMessage(f).ifPresent(message ->
      {
        throw new MudWebException(name, message, f, Response.Status.BAD_REQUEST);
      });
      throw new MudWebException(name, f.getMessage(), f, Response.Status.BAD_REQUEST);
    } catch (MudException e)
    {
      throw new MudWebException(name, e, Response.Status.BAD_REQUEST);
    }
    LOGGER.log(Level.FINER, "exiting create {0}", name);
    return Response.ok().build();
  }

  /**
   * Logs a player into the server. Once logged in, he/she can start playing the
   * game. Url is for example
   * http://localhost:8080/karchangame/resources/game/Karn/logon?password=itsasecret..
   *
   * @param requestContext the context of the request, useful for example
   *                       querying for the IP address.
   * @param password       password for verification of the user.
   * @param name           the name of the user
   */
  @PermitAll
  @PUT
  @Path("{name}/logon")
  public void logon(@Context HttpServletRequest requestContext, @PathParam("name") String name, @QueryParam("password") String password)
  {
    LOGGER.log(Level.FINER, "entering logon {0}", name);

    String address = requestContext.getHeader(X_FORWARDED_FOR);
    if ((address == null) || ("".equals(address.trim())))
    {
      address = requestContext.getRemoteAddr();
      if (VPS386.equals(address))
      {
        throw new MudWebException(name,
          "User has server address.",
          "User has server address (" + name + ", " + address + ")",
          Response.Status.BAD_REQUEST);
      }
    }
    if ((address == null) || ("".equals(address.trim())))
    {
      throw new MudWebException(name,
        "User has no address.",
        "User has no address (" + name + ", " + address + ")",
        Response.Status.BAD_REQUEST);
    }
    if (Utils.isOffline())
    {
      throw new MudWebException(name, "Game is offline", Response.Status.NO_CONTENT);
    }
    if (isBanned(name, address))
    {
      throw new MudWebException(name, "User was banned",
        "User was banned (" + name + ", " + address + ")",
        Response.Status.FORBIDDEN);
    }
    User person = getPlayer(name);

    // nasty work-around for Catalina AuthenticatorBase to be able to
    // change/create the session cookie
    requestContext.getSession();
    try
    {
      requestContext.login(name, password);
    } catch (ServletException ex)
    {
      if (ex.getCause() != null)
      {
        Logger.getLogger(GameRestService.class.getName()).log(Level.SEVERE, null, ex.getCause());
      }
      if (ex.getMessage().equals("Login failed"))
      {
        throw new WebApplicationException(ex, Response.Status.UNAUTHORIZED);
      }
      if (ex.getMessage().equals("This is request has already been authenticated"))
      {
        return;
      }
      Logger.getLogger(GameRestService.class.getName()).log(Level.SEVERE, null, ex);
      throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
    }
    LOGGER.log(Level.FINER, "exiting logon successful {0}", name);
    person.setAddress(address);
    person.setNewpassword(password);

  }

  /**
   * Logs a player off. TODO MLE: should be moved elsewhere, is not specifically
   * part of the game.
   *
   * @param requestContext the context of the request, useful for example
   *                       querying for the IP address.
   */
  @PUT
  @Path("{name}/logoff")
  public void logoff(@Context HttpServletRequest requestContext)
  {
    LOGGER.finer("entering logoff");
    playerAuthenticationService.logoff(requestContext);
  }

  /**
   * Retrieves the logon message.
   *
   * @param name the name of the character/player
   * @throws WebApplicationException <ul><li>BAD_REQUEST if an unexpected
   *                                 exception crops up.</li>
   *                                 <li>NO_CONTENT if the game is offline</li>
   *                                 <li>FORBIDDEN, if the player is banned.</li>
   *                                 </ul>
   */
  @GET
  @Path("{name}/logonmessage")
  @Produces(
    {
      MediaType.TEXT_HTML
    })
  public String getLogonMessage(@PathParam("name") String name)
  {
    LOGGER.finer("entering getLogonMessage");
    User person = authenticateToEnterGame(name);
    // write logon message
    Board newsBoard = boardService.getNewsBoard();
    StringBuilder buffer = new StringBuilder(newsBoard.getDescription());
    List<BoardMessage> news = boardService.getNews();
    for (BoardMessage newMessage : news)
    {
      buffer.append("<hr/>");
      buffer.append(newMessage.getPosttime());
      buffer.append("<p/>\r\n");
      buffer.append(newMessage.getMessage());
      buffer.append("<p><i>");
      buffer.append(newMessage.getPerson().getName());
      buffer.append("</i>");
    }
    // has guild
    if (person.getGuild() != null)
    {
      // guild logonmessage
      if (person.getGuild().getLogonmessage() != null)
      {
        buffer.append(person.getGuild().getLogonmessage()).append("<hr/>");
      }
      // guild alarm message
      buffer.append(person.getGuild().getAlarmDescription()).append("<hr/>");
    }
    return buffer.toString();
  }

  /**
   * Starts playing the game.
   *
   * @param requestContext used for retrieving the ip address/hostname of the
   *                       player for determining ban-rules and the like.
   * @param name           the name of the character/player
   * @throws WebApplicationException <ul><li>BAD_REQUEST if an unexpected
   *                                 exception crops up.</li>
   *                                 <li>NO_CONTENT if the game is offline</li>
   *                                 <li>FORBIDDEN, if the player is banned.</li>
   *                                 </ul>
   */
  @POST
  @Path("{name}/enter")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public void enterGame(@Context HttpServletRequest requestContext, @PathParam("name") String name)
  {
    LOGGER.finer("entering enterGame");
    User person = authenticateToEnterGame(name);
    try
    {
      person.activate();
      idleUsersService.resetUser(person.getName());
      final PersonCommunicationService communicationService = CommunicationService.getCommunicationService(person);
      String message = "You have entered the game. (" + LocalDateTime.now().format(DATETIME_FORMAT) + ")<br>";
      communicationService.writeMessage(message);
      // check mail
      if (mailService.hasNewMail(person))
      {
        communicationService.writeMessage("You have new Mudmail!<br>");
      } else
      {
        communicationService.writeMessage("You have no new Mudmail...<br>");
      }
      // write log "entered game."
      logService.writeLog(person, "entered game.");
    } catch (PersistenceException e)
    {
      ExceptionUtils.createMessage(e).ifPresent(message ->
      {
        throw new MudWebException(name, message, e, Response.Status.BAD_REQUEST);
      });
      throw new MudWebException(name, e.getMessage(), e, Response.Status.BAD_REQUEST);
    } catch (ConstraintViolationException e)
    {
      throw new MudWebException(name, ExceptionUtils.createMessage(e), e, Response.Status.BAD_REQUEST);
    }
  }


  /**
   * Main function for executing a command in the game.
   *
   * @param name    the name of the player. Should match the authorized user.
   * @param command the command issued
   * @param offset  the offset used for the log
   * @param log     indicates with true or false, whether or not we are interested
   *                in the log.
   * @return NO_CONTENT if the game is offline for maintenance.
   * @throws WebApplicationException BAD_REQUEST if an unexpected exception
   *                                 crops up or something could not be validated.
   */
  @POST
  @Path("{name}/play")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public PrivateDisplay playGame(@PathParam(value = "name") String name, String command, @QueryParam(value = "offset") Long offset, @QueryParam(value = "log") Boolean log) throws MudException
  {
    LOGGER.entering(this.getClass().getName(), "playGame");
    if (log == null)
    {
      log = false;
    }
    command = InputSanitizer.security(command);
    PrivateDisplay display = null;
    User person = authenticate(name);
    idleUsersService.resetUser(person.getName());
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(person);
    if (log && offset > communicationService.getLogSize())
    {
      offset = communicationService.getLogSize();
    }
    try
    {
      if (command.contains(" ; ") && (!command.toLowerCase().startsWith("macro ")))
      {
        // it's not a macro , but does contain multiple commands
        display = runMultipleCommands(person, command);
      } else
      {
        String[] parsedCommand = command.split(" ");
        Macro macro = null;
        if (parsedCommand.length <= 2 && (!command.toLowerCase().startsWith("macro ")))
        {
          // find macro (if it exists)
          MacroPK pk = new MacroPK();
          pk.setMacroname(parsedCommand[0]);
          pk.setName(person.getName());
          macro = em.find(Macro.class, pk);
        }
        if (macro == null || macro.getContents() == null || macro.getContents().trim().equals(""))
        {
          // no macro found, execute single command
          display = gameMain(person, command);
        } else
        {
          // macro found, execute macro.
          command = macro.getContents();
          // macro
          if (parsedCommand.length == 2)
          {
            command = command.replace("%t", parsedCommand[1]);
          }
          display = runMultipleCommands(person, command);
        }
      }
    } catch (WebApplicationException e)
    {
      LOGGER.log(Level.INFO, "caught and rethrowing WebApplicationException {0}  {1}", new Object[]
        {
          e.getClass().getName(), e.getMessage()
        });
      //ignore
      throw e;
    } catch (Throwable e)
    {
      Throwable f = e;
      while (f.getCause() != null)
      {
        if (f.getCause() instanceof WebApplicationException)
        {
          throw (WebApplicationException) f.getCause();
        }
        f = f.getCause();
      }
      String message = (e.getMessage() == null || e.getMessage().trim().equals(""))
        ? "An error occurred. Please notify Karn or one of the deps."
        : e.getMessage();
      //ignore
      throw new MudWebException(name, message, e, Response.Status.BAD_REQUEST);
    }
    // add log to the return value
    if (log)
    {
      try
      {
        display.log = communicationService.getLog(offset);
      } catch (MudException ex)
      {
        LOGGER.throwing("play: throws ", ex.getMessage(), ex);
      }
    }
    return display;
  }

  private PrivateDisplay runMultipleCommands(User person, String command)
    throws MudException
  {
    PrivateDisplay result = null;
    // multiple commands
    String[] splitted = command.split(" ; ");
    for (String str : splitted)
    {
      result = gameMain(person, str.trim());
    }
    return result;
  }

  /**
   * It parses and executes the command of the user. The main batch of the
   * server.
   *
   * @param person  User who wishes to execute a command.
   * @param command String containing the command entered
   * @return PrivateDisplay containing the response.
   * @throws MudException when something goes wrong.
   */
  private PrivateDisplay gameMain(User person, String command) throws MudException
  {
    LOGGER.log(Level.FINER, "gameMain");
    command = command.replace("&#39;", "'");
    logService.writeCommandLog(person, command);
    if (CommandFactory.noUserCommands())
    {
      // get all user commands
      LOGGER.log(Level.FINER, "{0}.gameMain - get all user commands", this.getClass().getName());
      TypedQuery<UserCommand> query = em.createNamedQuery("UserCommand.findActive", UserCommand.class);
      List<UserCommand> list = query.getResultList();
      for (UserCommand userCommand : list)
      {
        CommandFactory.addUserCommand(userCommand.getId(), userCommand.getCommand(), userCommand.getMethodName().getName(), (userCommand.getRoom() == null ? null : userCommand.getRoom().getId()));
      }
    }
    List<UserCommandInfo> userCommands = CommandFactory.getUserCommands(person, command);
    List<UserCommand> userCommands2 = new ArrayList<>();
    if (!userCommands.isEmpty())
    {
      for (UserCommandInfo info : userCommands)
      {
        UserCommand com = em.find(UserCommand.class, info.getCommandId());
        if (com != null)
        {
          userCommands2.add(com);
        }
      }
    }
    DisplayInterface display = commandRunner.runCommand(person, command, userCommands2);
    if (display == null || display == person.getRoom())
    {
      return createPrivateDisplayFromRoom(person);
    }
    return createPrivateDisplay(display, person);
  }

  /**
   * Retrieves the log of a player.
   *
   * @param name   the name of the user
   * @param offset the offset from whence to read the log
   * @return returns the log
   * @throws WebApplicationException BAD_REQUEST if an unexpected exception
   *                                 crops up.
   * @see PrivateLog
   */
  @GET
  @Path("{name}/log")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public String getLog(@PathParam("name") String name, @QueryParam("offset") Long offset)
  {
    LOGGER.finer("entering retrieveLog");
    Person person = authenticateToEnterGame(name);
    if (offset == null)
    {
      offset = 0L;
    }
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(person);
    PrivateLog plog = communicationService.getLog(offset);
    return plog.toJson();
  }

  /**
   * Removes the log of a player, i.e. creates a new empty log.
   *
   * @param name the name of the user
   * @return Response.ok if everything's okay
   * @throws WebApplicationException BAD_REQUEST if an unexpected exception
   *                                 crops up.
   */
  @DELETE
  @Path("{name}/log")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public Response deleteLog(@PathParam("name") String name)
  {
    LOGGER.finer("entering deleteLog");
    Person person = authenticate(name);
    CommunicationService.getCommunicationService(person).clearLog();
    return Response.ok().build();
  }

  /**
   * Stops a playing character from playing.
   *
   * @param name the name of the user
   * @return An Ok response.
   * @throws WebApplicationException BAD_REQUEST if an unexpected exception
   *                                 crops up.
   */
  @GET
  @Path("{name}/quit")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public Response quitGame(@PathParam("name") String name)
  {
    LOGGER.finer("entering quit");

    User person = authenticate(name);
    String message = "You have left the game. (" + LocalDateTime.now().format(DATETIME_FORMAT) + ")<br>";
    CommunicationService.getCommunicationService(person).writeMessage(person, message);
    person.deactivate();
    idleUsersService.removeUser(person.getName());
    logService.writeLog(person, "left the game.");
    return Response.ok().build();
  }

  private PrivateDisplay createPrivateDisplay(DisplayInterface display, User player) throws MudException
  {
    PrivateDisplay result = new PrivateDisplay();
    result.body = display.getBody();
    result.image = display.getImage();
    result.title = display.getMainTitle();
    return result;
  }

  private PrivateDisplay createPrivateDisplayFromRoom(User player) throws MudException
  {
    PrivateDisplay result = createPrivateDisplay(player.getRoom(), player);
    if (player.getRoom().getWest() != null)
    {
      result.west = true;
    }
    if (player.getRoom().getEast() != null)
    {
      result.east = true;
    }
    if (player.getRoom().getNorth() != null)
    {
      result.north = true;
    }
    if (player.getRoom().getSouth() != null)
    {
      result.south = true;
    }
    if (player.getRoom().getUp() != null)
    {
      result.up = true;
    }
    if (player.getRoom().getDown() != null)
    {
      result.down = true;
    }
    List<PrivatePerson> persons = new ArrayList<>();
    for (Person person : player.getRoom().getPersons(player))
    {
      if (person.getVisible())
      {
        PrivatePerson pp = new PrivatePerson();
        pp.race = person.getRace();
        pp.name = person.getName();
        persons.add(pp);
      }
    }
    result.persons = persons;
    result.items = Constants.addInventoryForRoom(player.getRoom().getItems());
    return result;
  }

  @VisibleForTesting
  public void setEntityManager(EntityManager em)
  {
    this.em = em;
  }
}
