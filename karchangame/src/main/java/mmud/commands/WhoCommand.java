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
package mmud.commands;

import java.util.ArrayList;
import java.util.List;

import mmud.database.entities.characters.User;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Display;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.services.PersonBean;

/**
 * Provide a list of who is currently logged onto the game: "who".
 *
 * @author maartenl
 */
public class WhoCommand extends NormalCommand
{

    public WhoCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        PersonBean personBean = getPersonBean();
        List<User> persons = new ArrayList<>();
        for (User user : personBean.getActivePlayers())
        {
            if (user.getVisible())
            {
                persons.add(user);
            }
        }
        StringBuilder whoList = new StringBuilder();
        if (persons.isEmpty())
        {
            // this is weird, you yourself are online, obviously!
            whoList.append("There are no people online at the moment.");
            return new Display("Who", null, whoList.toString());
        }
        whoList.append("There are ").append(persons.size()).append(" players.<br/><br/><hr/>");
        whoList.append("<ul>");
        int count = 0;
        for (User player : persons)
        {
          Area myArea = player.getRoom().getArea();
          String name = player.getName();
          if (player.getFrogging() > 0)
          {
            name = "a frog called " + name;
          }
          if (player.getJackassing() > 0)
          {
            name = "a jackass called " + name;
          }
          whoList.append("<li>").append(name).append(", ").append(player.getTitle()).append(!myArea.getArea().equals("Main")
            ? " in " + myArea.getShortdescription() : "").append(player.getSleep() ? ", sleeping " : " ").append("</li>\r\n");
        }
        whoList.append("</ul>");
        return new Display("Who", null, whoList.toString());
    }

}
