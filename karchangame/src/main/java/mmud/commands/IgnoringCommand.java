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

import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * Awaken from sleep: "awaken".
 * @author maartenl
 */
class IgnoringCommand extends NormalCommand
{

    public IgnoringCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        final StringBuilder builder = new StringBuilder("<p>You are ignoring:<ul>\r\n");
        for (User person : aUser.getIgnoringSet())
        {
            builder.append("<li>").append(person.getName()).append("</li>");
        }
        builder.append("</ul></p>\r\n");
        builder.append("<p>People ignoring you:<ul>\r\n");
        for (User person : aUser.getIgnoredSet())
        {
            builder.append("<li>").append(person.getName()).append("</li>");
        }
        builder.append("</ul></p>\r\n");
        return new DisplayInterface(){

            @Override
            public String getMainTitle() throws MudException
            {
                return "Ignore list";
            }

            @Override
            public String getImage() throws MudException
            {
                // TODO : add ignore icon
                return null;
            }

            @Override
            public String getBody() throws MudException
            {
                return builder.toString();
            }
        };
    }

}
