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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mmud.database.entities.Person;
import mmud.exceptions.AuthenticationException;
import mmud.exceptions.NotFoundException;

/**
 * The bean that can be used to enter, exit, play the game, register, etc.
 * @author maartenl
 */
@Stateless
public class GameBean implements GameBeanLocal
{

    private static final Logger itsLog = Logger.getLogger("mmudbeans");
    @PersistenceContext(unitName = "karchangame-ejbPU")
    private EntityManager em;

    @Override
    public String helloWorld()
    {
        itsLog.entering(this.getClass().getName(), "helloWorld");

        return "Hello, world.";
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
    public CommandOutput enter(final String name, final String password)
    {
        return null;
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
}
