/*
 *  Copyright (C) 2019 maartenl
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
package mmud.rest.services.admin.systemlog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import mmud.exceptions.MudWebException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.karchan.security.Roles;

/**
 * <p>
 * Allows getting the systemlog of the application server. You can find it at
 * /karchangame/resources/administration/systemlog.</p>
 *
 * @author maartenl
 */
@DeclareRoles(Roles.DEPUTY)
@RolesAllowed(Roles.DEPUTY)


@Path("/administration/systemlog")
public class SytemlogBean
{

  private static final Logger LOGGER = Logger.getLogger(SytemlogBean.class.getName());

  private static final int FIRST_ENTRY = 0;

  private static final int MAXIMUM_ENTRIES = 100;

  @Inject
  private SecurityContext securityContext;

  /**
   * For example :
   * "/home/payara/payara5/glassfish/domains/domain1/logs/server.log".
   */
  @Inject
  @ConfigProperty(name = "karchan.system.log")
  private String logFilename;

  /**
   *
   * @return most recent 100 log messages from the system log.
   * @throws WebApplicationException <ul>
   * <li>UNAUTHORIZED, if the authorization failed.</li>
   * <li>NOT_FOUND if this wikipage does not exist.</li>
   * <li>BAD_REQUEST if an unexpected exception crops up.</li></ul>
   */
  @GET
  public String getSystemLog()
  {
    String userName = securityContext.getCallerPrincipal().getName();
    List<String> result = Collections.emptyList();

    try
    {
      File file = new File(logFilename);
      InputStream stream = new FileInputStream(file);
      result = new LogReader().readFile(stream);
    } catch (IOException e)
    {
      throw new MudWebException(userName, e, Response.Status.BAD_REQUEST);
    }
    return "[" + result.subList(FIRST_ENTRY, MAXIMUM_ENTRIES).stream().collect(Collectors.joining(",")) + "]";
  }

}
