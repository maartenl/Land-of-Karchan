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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mmud.Constants;
import mmud.Utils;
import mmud.commands.CommandFactory;
import mmud.commands.CommandFactory.UserCommandInfo;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Board;
import mmud.database.entities.game.BoardMessage;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Macro;
import mmud.database.entities.game.MacroPK;
import mmud.database.entities.game.Room;
import mmud.database.entities.game.UserCommand;
import mmud.database.entities.game.Worldattribute;
import mmud.database.enums.God;
import mmud.database.enums.Sex;
import mmud.exceptions.MudException;
import mmud.rest.webentities.PrivateDisplay;
import mmud.rest.webentities.PrivateLog;
import mmud.rest.webentities.PrivatePerson;
import mmud.scripting.Persons;
import mmud.scripting.Rooms;
import mmud.scripting.RoomsInterface;
import mmud.scripting.RunScript;
import mmud.scripting.World;
import mmud.scripting.WorldInterface;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * Takes care of all the game-related functions.
 * <img
 * src="../../../images/Gamebean.png">
 *
 * @startuml Gamebean.png
 * (*) --> "Create character"
 * --> "Login"
 * --> "Play"
 * --> "Retrieve log"
 * --> "Play"
 * --> "Quit"
 * -->(*)
 * @enduml
 * @author maartenl
 */
@Stateless
@LocalBean
@Path("/game")
public class GameBean implements RoomsInterface, WorldInterface
{

    @EJB
    private BoardBean boardBean;
    @EJB
    private PersonBean personBean;
    @EJB
    private MailBean mailBean;
    @EJB
    private LogBean logBean;
    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    /**
     * Returns the entity manager of Hibernate/JPA. This is defined in
     * build/web/WEB-INF/classes/META-INF/persistence.xml.
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager()
    {
        return em;
    }
    private static final Logger itsLog = Logger.getLogger(GameBean.class.getName());

    /**
     * <p>
     * This method should be called to verify that the target of a certain
     * action is indeed a proper authenticated user.</p>
     * <p>
     * <img
     * src="../../../images/Gamebean_authenticate.png"></p>
     *
     * @param lok session password
     * @param name the name to identify the person
     * @throws WebApplicationException NOT_FOUND, if the user is either not
     * found or is not a proper user. BAD_REQUEST if an unexpected exception
     * crops up or provided info is really not proper. UNAUTHORIZED if session
     * passwords do not match.
     * @startuml Gamebean_authenticate.png
     * (*) --> "find character"
     * --> "character found"
     * --> "character == user"
     * --> "session password is good"
     * -->(*)
     * @enduml
     */
    private User authenticate(String name, String lok)
    {
        User person = getEntityManager().find(User.class, name);
        if (person == null)
        {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (!person.isUser())
        {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (!person.verifySessionPassword(lok))
        {
            throw new WebApplicationException(new MudException("session password " + lok + " does not match session password of " + name), Response.Status.UNAUTHORIZED);
        }
        return person;
    }

    /**
     * <p>
     * This method should be called to verify that the target of a certain
     * action is a user with the appropriate password.</p>
     * <p>
     * <img
     * src="../../../images/Gamebean_authenticateWithPassword.png"></p>
     *
     * @param password real password
     * @param name the name to identify the person
     * @return the authenticated User
     * @throws WebApplicationException BAD_REQUEST if an unexpected exception
     * crops up or provided info is really not proper. UNAUTHORIZED if session
     * passwords do not match or user not found.
     * @startuml Gamebean_authenticateWithPassword.png
     * (*) --> "find character"
     * --> "character found"
     * --> "character == user"
     * --> "password is good"
     * -->(*)
     * @enduml
     */
    protected User authenticateWithPassword(String name, String password)
    {
        User person = getEntityManager().find(User.class, name);
        if (person == null)
        {
            throw new WebApplicationException("User was not found (" + name + ", " + password + ")", Response.Status.NOT_FOUND);
        }
        if (!person.isUser())
        {
            throw new WebApplicationException("User was not a user (" + name + ", " + password + ")", Response.Status.BAD_REQUEST);
        }

        Query query = getEntityManager().createNamedQuery("User.authorise");
        query.setParameter("name", name);
        query.setParameter("password", password);
        List<User> persons = query.getResultList();
        if (persons.isEmpty())
        {
            throw new WebApplicationException("User provided wrong password (" + name + ", " + password + ")", Response.Status.UNAUTHORIZED);
        }
        if (persons.size() > 1)
        {
            throw new WebApplicationException("User/password combo search returned more than one solution, impossible! (" + name + ", " + password + ")", Response.Status.BAD_REQUEST);
        }
        person = persons.get(0);
        if (!person.isUser())
        {
            throw new WebApplicationException("User was not a user (" + name + ", " + password + ")", Response.Status.BAD_REQUEST);
        }
        return person;
    }

    /**
     * <p>
     * Checks to see if a person is banned from playing.</p>
     * <p>
     * <img
     * src="../../../images/Gamebean_isBanned.png"></p>
     *
     * @param name the name of the person
     * @param address the ip address the person is playing from
     * @return true if banned, false otherwise.
     * @startuml Gamebean_isBanned.png
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
        Query query = getEntityManager().createNamedQuery("SillyName.findByName");
        query.setParameter("name", name);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty())
        {
            // silly name found!
            return true;
        }

        // check unbanned names
        query = getEntityManager().createNamedQuery("UnbanTable.findByName");
        query.setParameter("name", name);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty())
        {
            // unbanned name found!
            return false;
        }

        // check address banned
        String address2 = "bogushostman!";
        try
        {
            InetAddress inetAddress = InetAddress.getByName(address2);
            address2 = inetAddress.getHostName();
        } catch (UnknownHostException e)
        {
            // ignore this.
        }
        query = getEntityManager().createNamedQuery("BanTable.find");
        query.setParameter("address", address);
        query.setParameter("address2", address2);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty())
        {
            // banned address found!
            return true;
        }

        // check name banned
        query = getEntityManager().createNamedQuery("BannedName.find");
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
     * src="../../../images/Gamebean_create.png"></p>
     * @param requestContext for headers, like remote address.
     * @param name the name of the user
     * @param pperson the data of the new character
     * @return NO_CONTENT if the game is offline for maintenance.
     * @throws WebApplicationException BAD_REQUEST if an unexpected exception
     * crops up or something could not be validated.
     * @startuml Gamebean_create.png
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
    @POST
    @Path("{name}")
    @Produces(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response create(@Context HttpServletRequest requestContext, @PathParam("name") String name, PrivatePerson pperson)
    {
        itsLog.finer("entering create");
        getEntityManager().setProperty("activePersonFilter", 0); // turns filter off
        String address = requestContext.getRemoteAddr().toString();
        try
        {

            if (Utils.isOffline())
            {
                // game offline
                throw new WebApplicationException("Game is offline", Response.Status.NO_CONTENT);
            }
            if (pperson == null)
            {
                // no data provided
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            if (name == null || !pperson.name.equals(name))
            {
                // wrong data provided
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            if (pperson.password == null || !pperson.password.equals(pperson.password2))
            {
                // passwords do not match
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            User person = new User();
            person.setName(name);
            person.setPassword(pperson.password);
            if (isBanned(name, address))
            {
                // is banned
                throw new WebApplicationException("User was banned (" + name + ", " + address + ")", Response.Status.FORBIDDEN);
            }
            Person foundPerson = getEntityManager().find(Person.class, name);
            if (foundPerson != null)
            {
                // already a person
                throw new WebApplicationException("User already exists (" + name + ", " + address + ")", Response.Status.FOUND);
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
            person.setBirth(new Date());
            person.setCreation(new Date());
            person.setRoom(getEntityManager().find(Room.class, Room.STARTERS_ROOM));
            person.setGod(God.DEFAULT_USER);

            getEntityManager().persist(person);
            // TODO automatically add a welcome mail.
            logBean.writeLog(person, "character created.");
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (ConstraintViolationException e)
        {
            StringBuilder buffer = new StringBuilder("ConstraintViolationException:");
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                buffer.append(violation);
            }
            throw new RuntimeException(buffer.toString(), e);

        } catch (javax.persistence.PersistenceException f)
        {
            if (f.getCause().getClass().getName().equals("ConstraintViolationException"))
            {
                ConstraintViolationException e = (ConstraintViolationException) f.getCause();
                StringBuilder buffer = new StringBuilder("PersistenceException:");
                for (ConstraintViolation<?> violation : e.getConstraintViolations())
                {
                    buffer.append(violation);
                }
                throw new RuntimeException(buffer.toString(), e);
            }
            throw f;
        } catch (MudException e)
        {
            itsLog.throwing("create: throws ", e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }

    /**
     * Deletes a character, permanently. Use with extreme caution.
     *
     * @param name the name of the user
     * @param password the password of the character to be deleted
     * @param password2 verification of the password, a second time.
     * @return Response.ok
     * @throws WebApplicationException BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @DELETE
    @Path("{name}")
    @Produces(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response delete(@PathParam("name") String name, @QueryParam("password") String password, @QueryParam("password2") String password2)
    {
        itsLog.finer("entering delete");
        if (password == null || !password.equals(password2))
        {
            // passwords do not match
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        Person person = authenticateWithPassword(name, password);
        try
        {
            getEntityManager().remove(person);
            logBean.writeLog(person, "character deleted.");
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (Exception e)
        {
            itsLog.throwing("delete: throws ", e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }

    /**
     * Logs a character in, to start playing.
     *
     * @param requestContext
     * @param password password for verification of the user.
     * @param name the name of the user
     * @return the session password upon success
     * @throws WebApplicationException BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @POST
    @Path("{name}/logon")
    @Produces(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public String logon(@Context HttpServletRequest requestContext, @PathParam("name") String name, @QueryParam("password") String password)
    {
        itsLog.finer("entering logon");
        String address = requestContext.getRemoteAddr().toString();

        if ((address == null) || ("".equals(address.trim())))
        {
            throw new WebApplicationException("User has no address (" + name + ", " + address + ")", Response.Status.BAD_REQUEST);
        }
        if (Utils.isOffline())
        {
            // game offline
            throw new WebApplicationException("Game is offline", Response.Status.NO_CONTENT);
        }
        if (isBanned(name, address))
        {
            // is banned
            throw new WebApplicationException("User was banned (" + name + ", " + address + ")", Response.Status.FORBIDDEN);
        }
        User person = authenticateWithPassword(name, password);
        try
        {
            person.activate(address);
            person.generateSessionPassword();
            // write logon message
            Board newsBoard = boardBean.getNewsBoard();
            StringBuilder buffer = new StringBuilder(newsBoard.getDescription());
            List<BoardMessage> news = boardBean.getNews();
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
            person.writeMessage(buffer.toString());
            // check mail
            if (mailBean.hasNewMail(person))
            {
                person.writeMessage("<p>You have new Mudmail!</p>\r\n");
            } else
            {
                person.writeMessage("<p>You have no new Mudmail...</p>\r\n");
            }
            // has guild
            if (person.getGuild() != null)
            {

                // guild logonmessage
                if (person.getGuild().getLogonmessage() != null)
                {
                    person.writeMessage(person.getGuild().getLogonmessage()
                            + "<hr/>");
                }
                // guild alarm message
                person.writeMessage(person.getGuild().getAlarmDescription()
                        + "<hr/>");
            }
            // write log "entered game."
            logBean.writeLog(person, "entered game.");
            // TODO : execute command "me has entered the game..." -> can be moved to the darned next ajax calls

        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (MudException e)
        {
            itsLog.throwing("logon: throws ", e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return person.getLok();
    }

    /**
     * Main function for executing a command in the game.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param command the command issued
     * @param offset the offset used for the log
     * @param log indicates with true or false, whether or not we are
     * interested in the log.
     * @return NO_CONTENT if the game is offline for maintenance.
     * @throws WebApplicationException BAD_REQUEST if an unexpected exception
     * crops up or something could not be validated.
     */
    @POST
    @Path("{name}/play")
    @Produces(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public PrivateDisplay play(@PathParam("name") String name, @QueryParam("lok") String lok, @QueryParam("offset") Integer offset, String command, @QueryParam("log") boolean log) throws MudException
    {
        itsLog.entering(this.getClass().getName(), "play");
        if (Utils.isOffline())
        {
            // game offline
            throw new WebApplicationException("Game is offline", Response.Status.NO_CONTENT);
        }
        try
        {
            command = Utils.security(command);
        } catch (PolicyException | ScanException ex)
        {
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        }
        PrivateDisplay display = null;
        getEntityManager().setProperty("activePersonFilter", 1);
        User person = authenticate(name, lok);
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
                    macro = getEntityManager().find(Macro.class, pk);
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
                        command = command.replaceAll("%t", parsedCommand[1]);
                    }
                    display = runMultipleCommands(person, command);
                }
            }
        } catch (MudException e)
        {
            try
            {
                person.writeMessage(e.getMessage());
            } catch (MudException ex)
            {
                itsLog.throwing("play: throws ", ex.getMessage(), ex);
            }
            display = createPrivateDisplayFromRoom(person);
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        }
        // add log to the return value
        if (log)
        {
            try
            {
                display.log = retrieveLog(person, offset);
            } catch (MudException ex)
            {
                itsLog.throwing("play: throws ", ex.getMessage(), ex);
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
     * It parses and executes the command of the
     * user. The main batch of the server.
     *
     * @param person User who wishes to execute a command.
     * @param command String containing the command entered
     * @return PrivateDisplay containing the response.
     * @throws MudException when something goes wrong.
     */
    private PrivateDisplay gameMain(User person, String command) throws MudException
    {
        itsLog.log(Level.FINER, "{0}.gameMain", this.getClass().getName());
        logBean.writeCommandLog(person, command);
        if (CommandFactory.noUserCommands())
        {
            // get all user commands
            itsLog.log(Level.FINER, "{0}.gameMain - get all user commands", this.getClass().getName());
            Query query = getEntityManager().createNamedQuery("UserCommand.findActive");
            List<UserCommand> list = query.getResultList();
            for (UserCommand userCommand : list)
            {
                CommandFactory.addUserCommand(userCommand.getId(), userCommand.getCommand(), userCommand.getMethodName().getName(), (userCommand.getRoom() == null ? null : userCommand.getRoom().getId()));
            }
        }
        // TODO: add user commands using Rhino and scripting.
        List<UserCommandInfo> userCommands = CommandFactory.getUserCommands(person, command);
        List<UserCommand> userCommands2 = new ArrayList<>();
        if (!userCommands.isEmpty())
        {
            for (UserCommandInfo info : userCommands)
            {
                UserCommand com = getEntityManager().find(UserCommand.class, info.getCommandId());
                if (com != null)
                {
                    userCommands2.add(com);
                }
            }
        }
        Persons persons = new Persons(personBean);
        Rooms rooms = new Rooms(this);
        World world = new World(this);
        RunScript runScript = new RunScript(persons, rooms, world);
        DisplayInterface display = CommandFactory.runCommand(person, command, userCommands2, runScript);
        if (display == null || display == person.getRoom())
        {
            return createPrivateDisplayFromRoom(person);
        }
        return createPrivateDisplay(display, person);
    }

    private PrivateLog retrieveLog(Person person, Integer offset) throws MudException
    {
        if (offset == null)
        {
            offset = 0;
        }
        String log = person.getLog(offset);
        PrivateLog plog = new PrivateLog();
        plog.offset = offset;
        plog.log = log;
        plog.size = plog.log.length();
        return plog;
    }

    /**
     * Retrieves the log of a player.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param offset the offset from whence to read the log
     * @return returns the log
     * @see PrivateLog
     * @throws WebApplicationException BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @GET
    @Path("{name}/log")
    @Produces(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public PrivateLog retrieveLog(@PathParam("name") String name, @QueryParam("lok") String lok, @QueryParam("offset") Integer offset)
    {
        itsLog.finer("entering retrieveLog");
        Person person = authenticate(name, lok);
        if (offset == null)
        {
            offset = Integer.valueOf(0);
        }
        try
        {
            return retrieveLog(person, offset);
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (MudException e)
        {
            itsLog.throwing("retrieveLog: throws ", e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Removes the log of a player, i.e. creates a new empty log.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @return Response.ok if everything's okay
     * @throws WebApplicationException BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @DELETE
    @Path("{name}/log")
    @Produces(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response deleteLog(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.finer("entering deleteLog");
        Person person = authenticate(name, lok);

        try
        {
            person.clearLog();
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (MudException e)
        {
            itsLog.throwing("deleteLog: throws ", e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }

    /**
     * Stops a playing character from playing.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @return An Ok response.
     * @throws WebApplicationException BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @GET
    @Path("{name}/quit")
    @Produces(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public Response quit(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.finer("entering quit");
        getEntityManager().setProperty("activePersonFilter", 1);

        User person = authenticate(name, lok);
        try
        {
            person.getRoom().sendMessage(person, "%SNAME left the game.<BR>\r\n");
            person.deactivate();
            logBean.writeLog(person, "left the game.");
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (MudException e)
        {
            itsLog.throwing("quit: throws ", e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
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
        for (Person person : player.getRoom().getUsers(player))
        {
            PrivatePerson pp = new PrivatePerson();
            pp.race = person.getRace();
            pp.name = person.getName();
            persons.add(pp);
        }
        result.persons = persons;
        result.items = Constants.addInventoryForRoom(player.getRoom().getItems());
        return result;
    }

    @Override
    public Room find(Integer id)
    {
        return getEntityManager().find(Room.class, id);
    }

    @Override
    public String getAttribute(String name)
    {
        Worldattribute attribute = getEntityManager().find(Worldattribute.class, name);
        if (attribute == null)
        {
            return null;
        }
        return attribute.getContents();
    }

}
