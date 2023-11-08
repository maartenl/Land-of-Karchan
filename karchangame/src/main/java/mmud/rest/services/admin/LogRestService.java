package mmud.rest.services.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.game.Log;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminLog;
import mmud.services.LogService;
import org.karchan.security.Roles;

/**
 * <p>
 * Allows getting the log. You can find it at
 * /karchangame/resources/administration/systemlog.</p>
 *
 * @author maartenl
 */
@DeclareRoles(Roles.DEPUTY)
@RolesAllowed(Roles.DEPUTY)
@Transactional
@Path("/administration/systemlog")
public class LogRestService
{
  private static final Logger LOGGER = Logger.getLogger(LogRestService.class.getName());

  @Context
  private SecurityContext securityContext;

  @Inject
  private LogService logService;

  /**
   * @return most recent 100 log messages from the system log.
   */
  @GET
  public String getLog(@QueryParam("name") String name, @QueryParam("creation") String creation)
  {
    if (creation != null && !creation.trim().equals("") && !creation.matches("[1-9]\\d\\d\\d-\\d\\d-\\d\\d"))
    {
      throw new MudWebException(securityContext.getUserPrincipal().getName(), "Expected the search date to have the format yyyy-mm-dd, for example 2021-02-03", Response.Status.BAD_REQUEST);
    }
    if (name != null && name.trim().equals(""))
    {
      name = null;
    }
    if (creation != null && creation.trim().equals(""))
    {
      creation = null;
    }
    String localname = name;
    LocalDate datetime = creation == null ? null : LocalDate.parse(creation, DateTimeFormatter.ISO_LOCAL_DATE);
    LOGGER.finest(() -> "getLog %s %s".formatted(localname, datetime));
    List<Log> logs = logService.getLogs(localname, datetime);
    return "[" + logs.stream().map(log -> new AdminLog(log).toJson()).collect(Collectors.joining(",")) + "]";
  }
}
