/*
 * Copyright (C) 2015 maartenl
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
package mmud.services.admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.game.Admin;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;

/**
 * @author maartenl
 */

public class IdentificationService
{

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  /**
   * Provides the player who is logged in during this session.
   *
   * @return the current user, for example "Karn".
   */
  public String getCurrentUser(SecurityContext context)
  {
    String currentUser = context.getUserPrincipal().getName();
    if (currentUser.equals("ANONYMOUS"))
    {
      throw new MudWebException(null, "Not logged in.", Response.Status.UNAUTHORIZED);
    }
    return currentUser;
  }

  /**
   * TODO: check validUntil.
   *
   * @return the admin identified by the current user, or null if no
   * proper admin can be found.
   */
  public Admin getCurrentAdmin(SecurityContext context)
  {
    String currentUser = context.getUserPrincipal().getName();
    Admin admin = em.find(Admin.class, currentUser);
    if (admin == null)
    {
      throw new MudException("admin " + currentUser + " not found.");
    }
    return admin;
  }
}
