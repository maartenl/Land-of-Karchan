/*
 * Copyright (C) 2017 maartenl
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
package mmud.database.entities.game;

/**
 * This class is customizing the collection of {@link mmud.database.entities.characters.Person}s
 * in a {@link Room} to only contain
 * active persons.
 *
 * @author maartenl
 */
public class PersonsFilterForRoom extends PersonsFilter
{

  @Override
  protected String getCollectionName()
  {
    return "persons";
  }

  @Override
  protected String getFieldName()
  {
    return "room";
  }

  @Override
  protected String getIdentifier()
  {
    return "id";
  }

}
