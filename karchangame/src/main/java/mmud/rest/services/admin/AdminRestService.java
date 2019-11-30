/*
 * Copyright (C) 2019 maartenl
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

import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author maartenl
 * @param <T> the primary key of the entity.
 */
public interface AdminRestService<T>
{

  void create(String json, SecurityContext sc);

  void edit(T id, String json, SecurityContext sc);

  void remove(T id, SecurityContext sc);

  String find(T id, SecurityContext sc);
  
  String findAll();
  
  String findRange(Integer offset, Integer pageSize);
  
  String count();

}
