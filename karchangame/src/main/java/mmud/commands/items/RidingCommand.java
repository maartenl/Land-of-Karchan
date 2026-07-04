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
package mmud.commands.items;

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.database.enums.Wielding;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Starts you riding an item, for example a horse. Syntax: ride &lt;item&gt;
 *
 * @author maartenl
 * @see UnwieldCommand
 */
public class RidingCommand extends WieldCommand
{

  public RidingCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    return super.run(command + " with riding", aUser);
  }
}
