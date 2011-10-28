/*
 * Copyright (C) 2011 maartenl
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
package mmud.beans;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mmud.Constants;
import mmud.database.entities.BannedAddress;
import mmud.database.entities.Bannedname;
import mmud.database.entities.Eventlog;
import mmud.database.entities.Person;
import mmud.database.entities.Player;
import mmud.database.entities.Sillyname;
import mmud.database.entities.Unban;
import mmud.exceptions.AuthenticationException;
import mmud.exceptions.NotFoundException;

/**
 * The bean that can be used to enter, exit, play the game, register, etc.
 * @author maartenl
 */
@Stateless
public class GameBean implements GameBeanLocal
{

    private static final boolean karchanOffline = true;
    private static final Logger itsLog = Logger.getLogger("mmudbeans");
    @PersistenceContext(unitName = "karchangame-ejbPU")
    private EntityManager em;

    @Override
    public String helloWorld()
    {
        itsLog.entering(this.getClass().getName(), "helloWorld");

        return "Hello, world.";
    }

    /**
     * Creates a nice new event log.
     * @param name character/player name
     * @param message the message
     */
    public void log(String name, String message)
    {
        log(name, message, null);
    }

    /**
     * Creates a nice new event log.
     * @param name character/player name
     * @param message the message
     * @param addendum addendum, for example a stack trace.
     */
    public void log(String name, String message, String addendum)
    {
        Eventlog aNewLog = new Eventlog(name, message, addendum);
        em.persist(aNewLog);
    }

    @Override
    public String getSessionPassword(final String name, final String password) throws AuthenticationException, NotFoundException
    {
        itsLog.entering(this.getClass().getName(), "getSessionPassword");

        if (name == null || password == null || name.trim().equals("") || password.trim().equals(""))
        {
            throw new NullPointerException();
        }
        Person player = em.find(Person.class, name);
        if (player == null)
        {
            throw new NotFoundException("player " + name + " not found.");
        }
        itsLog.log(Level.SEVERE, player.toString());
        String result = player.getBeard();
        itsLog.exiting(this.getClass().getName(), "getSessionPassword");

        return result;
    }

    @Override
    public void register()
    {
    }

    @Override
    public void remove(final String name, final String password)
    {
    }

    @Override
    public CommandOutput enter(final String name, final String password, final String address, final String cookie)
            throws NotFoundException, AuthenticationException
    {
        /*
         * The steps:
         * - validate params
         * - karchan online?
         * - user banned?
         * - user exists?
         * Tasks:
         * - activate user
         * - generate session pwd
         * - set frames
         * - write logon message
         * - check new mudmail
         * - check guild log message
         */
        itsLog.entering(this.getClass().getName(), "enter");

        if (name == null || password == null || name.trim().equals("") || password.trim().equals(""))
        {
            throw new NullPointerException();
        }
        if (password.length() < 5)
        {
            throw new AuthenticationException("password too short.");
        }
        if ((address == null) || ("".equals(address.trim())))
        {
            throw new AuthenticationException("address unknown");
        }
        if (karchanOffline)
        {
            CommandOutput commandOutput = new CommandOutput();
            commandOutput.setTitle("Land of Karchan is Temporarily Offline");
            commandOutput.setImage("some image");
            commandOutput.setDescription("the description");
            return commandOutput;
        }
        if (isPlayerBanned(name, address))
        {
            itsLog.info(
                    "thrown " + Constants.USERBANNEDERROR);
            throw new AuthenticationException(Constants.USERBANNEDERROR);
        }
        Player player = em.find(Player.class, name);
        if (player == null)
        {
            throw new NotFoundException("player " + name + " not found.");
        }

        CommandOutput commandOutput = new CommandOutput();
        commandOutput.setTitle("Entering");
        commandOutput.setImage("some image");
        commandOutput.setDescription("the description");

        MmudLog log = new MmudLog();
        log.setLog("contents of log");
        log.setOffset(0);
        commandOutput.setLog(log);
        itsLog.exiting(this.getClass().getName(), "enter");
        return commandOutput;
    }

    @Override
    public CommandOutput play(final String name, final String sessionpwd, final String command)
    {

        return null;
    }

    @Override
    public MmudLog getLog(final String name, final String sessionpwd, final Integer offset)
    {
        return null;
    }

    public void persist(Object object)
    {
        em.persist(object);
    }

    /**
     * search for the username amongst the banned users list in the database.
     * First checks the mm_sillynamestable in the database, if found returns
     * true. Then checks the mm_unbantable, if found returns false. Then checks
     * the mm_bannednamestable, if found returns true. And as last check checks
     * the mm_bantable, if found returns true, otherwise returns false.
     *
     * @return boolean, true if found, false if not found
     * @param username
     *            String, name of the playercharacter
     * @param address
     *            String, the address of the player
     */
    private boolean isPlayerBanned(String name, String address)
    {
        // check if user has silly name
        Query query = em.createNamedQuery(Sillyname.CHECKFORSILLYNAMES);
        query.setParameter("name", name);
        Integer i = (Integer) query.getSingleResult();
        if (i != null && i > 0)
        {
            // you've used a silly name! Banned!
            log(name, "silly name, banned!");
            return true;
        }
        // check if users name has specifically been unbanned
        query = em.createNamedQuery(Unban.CHECKFORUNBAN);
        query.setParameter("name", name);
        i = (Integer) query.getSingleResult();
        if (i != null && i > 0)
        {
            // you've been specially selected to continue! Unbanned!
            log(name, "especially selected not to be banned!");
            return false;
        }
        // check if users name has been banned
        query = em.createNamedQuery(Bannedname.CHECKFORBANNEDNAME);
        query.setParameter("name", name);
        i = (Integer) query.getSingleResult();
        if (i != null && i > 0)
        {
            // the char using this name has been banned
            log(name, "banned!");
            return true;
        }
        // check if users address has been banned
        query = em.createNamedQuery(BannedAddress.CHECKFORADDRESS);
        query.setParameter("address", address);
        i = (Integer) query.getSingleResult();
        if (i != null && i > 0)
        {
            // the address has been banned
            log(name, "address banned!");
            return true;
        }
        return false;
    }
}
