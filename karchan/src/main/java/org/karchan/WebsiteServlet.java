/*
 * Copyright (C) 2018 maartenl
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
package org.karchan;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import jakarta.annotation.security.DeclareRoles;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Context;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.karchan.menus.Menu;
import org.karchan.menus.MenuFactory;
import org.karchan.security.Roles;

@DeclareRoles(
  {
    Roles.PLAYER, Roles.DEPUTY, Roles.GUILDMEMBER, Roles.GUILDMASTER, Roles.GOD
  }
)
@WebServlet(urlPatterns = {"*.html", "/scripts/*"})
public class WebsiteServlet extends HttpServlet
{

  private static final Logger LOGGER = Logger.getLogger(WebsiteServlet.class.getName());

  private static final String VERSION_COOKIENAME = "karchanversion";

  private static final String CURRENT_VERSION = "2.0.15-SNAPSHOT";

  /**
   * For example: https://www.karchan.org. If it isn't configured, then no redirect takes place.
   */
  @Inject
  @ConfigProperty(name = "karchan.redirect.url")
  private Optional<String> redirectHttps;

  @Inject
  @Context
  private SecurityContext securityContext;

  @Inject
  private Freemarker freemarker;

  @Inject
  private MenuFactory menuFactory;

  @Inject
  private MailChecker mailChecker;

  @VisibleForTesting
  void setFreemarker(Freemarker freemarker)
  {
    this.freemarker = freemarker;
  }

  @VisibleForTesting
  void setMenuFactory(MenuFactory menuFactory)
  {
    this.menuFactory = menuFactory;
  }

  @Override
  public void init() throws ServletException
  {
    // do nothing for now.
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    final String url = getUrl(request);

    if (!request.isSecure() && redirectHttps.isPresent())
    {
      response.setStatus(301);
      response.setHeader("Location", redirectHttps.get());
      return;
    }
    PrintWriter out = response.getWriter();

    // Set response content type
    if (url.endsWith(".js"))
    {
      // just dump the javascript, don't do any freemarker data stuff.
      response.setContentType("application/javascript;charset=UTF-8");
      Template main;
      String templateName = url;
      try
      {
        main = freemarker.getConfiguration().getTemplate(templateName);
      } catch (TemplateNotFoundException notFound)
      {
        LOGGER.log(Level.SEVERE, "Template {0} not found.", templateName);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      try
      {
        /* Merge data-model with template */
        Map<String, Object> root = new HashMap<>();
        main.process(root, out);
      } catch (TemplateException ex)
      {
        LOGGER.log(Level.SEVERE, ex, () -> "Error processing template " + templateName);
        throw new ServletException(ex);
      }
      return;
    }

    response.setContentType("text/html;charset=UTF-8");

    Cookies cookies = new Cookies(request, response);

    // Actual logic goes here.

    /* Create a data-model */
    Map<String, Object> root = new HashMap<>();
    root.put("menus", MenuFactory.getNavigationBarMenus());
    root.put("url", url);
    root.put("darkmode", cookies.contains("karchandarkmode"));
    root.put("hideToc", cookies.contains("karchanhidetoc"));
    root.put("version", cookies.get(VERSION_COOKIENAME).map(Cookie::getValue).orElse(CURRENT_VERSION));
    if (request.getParameter("logout") != null && !cookies.isEmpty())
    {
      cookies.remove("JREMEMBERMEID");
      cookies.remove("karchanname");
      cookies.remove("karchanroles");
      cookies.remove("XSRF-TOKEN");
      root.put("user", null);
      root.put("isDeputy", false);
      root.put("isPlayer", false);
      root.put("isGuildmember", false);
      root.put("isGuildmaster", false);
      root.put("isGod", false);
      root.put("hasNewMail", false);
    } else
    {
      root.put("user", securityContext.getCallerPrincipal() != null ? securityContext.getCallerPrincipal().getName() : null);
      root.put("isDeputy", securityContext.isCallerInRole(Roles.DEPUTY));
      root.put("isPlayer", securityContext.isCallerInRole(Roles.PLAYER));
      root.put("isGuildmember", securityContext.isCallerInRole(Roles.GUILDMEMBER));
      root.put("isGuildmaster", securityContext.isCallerInRole(Roles.GUILDMASTER));
      root.put("isGod", securityContext.isCallerInRole(Roles.GOD));
      root.put("hasNewMail", mailChecker.checkMail(securityContext.getCallerPrincipal() != null ? securityContext.getCallerPrincipal().getName() : null));
    }
    root.put("isChristmas", isChristmas(request));
    Optional<Menu> visibleMenu = MenuFactory.findVisibleMenu(url);
    if (visibleMenu.isPresent())
    {
      Menu activeMenu = visibleMenu.get();
      root.put("activeMenu", activeMenu.getName());
    } else
    {
      root.put("activeMenu", "none");
    }
    Optional<Menu> foundMenu = MenuFactory.findMenu(url);
    if (foundMenu.isEmpty())
    {
      LOGGER.log(Level.FINEST, "Menu with url {0} not found.", url);
      if (url.startsWith("/blogs/"))
      {
        LOGGER.log(Level.FINEST, "Url {0} starts with /blogs/.", url);
        foundMenu = Optional.of(menuFactory.createBlogMenu(url));
      }
      if (url.startsWith("/wiki/"))
      {
        LOGGER.log(Level.FINEST, "Url {0} starts with /wiki/.", url);
        foundMenu = Optional.of(menuFactory.createWikiMenu(url, securityContext.isCallerInRole(Roles.DEPUTY)));
      }
      if (url.startsWith("/chronicles/person"))
      {
        LOGGER.log(Level.FINEST, "Url {0} starts with /chronicles/person.", url);
        foundMenu = Optional.of(menuFactory.createPersonMenu());
      }
      if (url.startsWith("/help/command") && !url.startsWith("/help/commandreference"))
      {
        LOGGER.log(Level.FINEST, "Url {0} starts with /help/command.", url);
        foundMenu = Optional.of(menuFactory.createCommandMenu());
      }
    }
    List<Menu> breadcrumbs = new ArrayList<>();
    foundMenu.ifPresent(menu ->
    {
      LOGGER.log(Level.FINEST, "Menu {0} found with url {1}.", new Object[]
        {
          menu, url
        });
      menuFactory.setDatamodel(menu, root, request.getParameterMap());
      root.put("lastBreadcrumb", menu);
      breadcrumbs.addAll(createBreadcrumbs(menu));
      LOGGER.log(Level.FINEST, "Breadcrumbs set to {0}.", breadcrumbs);
    });
    root.put("breadcrumbs", breadcrumbs);

    String templateName = foundMenu.map(Menu::getTemplate).orElse(url.replace(".html", "").replace(".js", ""));
    root.put("template", templateName);

    /* Get the template (uses cache internally) */
    Template header = freemarker.getConfiguration().getTemplate("header");
    Template main;
    try
    {
      main = freemarker.getConfiguration().getTemplate(templateName);
    } catch (TemplateNotFoundException notFound)
    {
      LOGGER.log(Level.SEVERE, "Template {0} not found.", templateName);
      root.put("activeMenu", "none");
      root.put("breadcrumbs", Collections.emptyList());
      root.put("lastBreadcrumb", MenuFactory.getNotFoundMenu());
      main = freemarker.getConfiguration().getTemplate("notFound");
    }
    Template footer = freemarker.getConfiguration().getTemplate("footer");

    try
    {
      /* Merge data-model with template */
      header.process(root, out);
      main.process(root, out);
      footer.process(root, out);
    } catch (TemplateException ex)
    {
      LOGGER.log(Level.SEVERE, ex, () -> "Error processing template " + templateName);
      throw new ServletException(ex);
    }
  }

  private List<Menu> createBreadcrumbs(Menu menu)
  {
    if (menu.getParent() == null)
    {
      LOGGER.log(Level.FINEST, "Menu {0} has no parent.", menu);
      return Collections.emptyList();
    }
    List<Menu> breadcrumbs = new ArrayList<>();
    while (menu.getParent() != null && menu.getParent() != MenuFactory.getRootMenu())
    {
      breadcrumbs.add(0, menu.getParent());
      menu = menu.getParent();
    }
    return breadcrumbs;
  }

  private String getUrl(HttpServletRequest url)
  {
    if (url.getRequestURI().equals("/"))
    {
      return "/index.html";
    }
    return url.getRequestURI();
  }

  private boolean isChristmas(HttpServletRequest request)
  {
    if (request.getParameter("christmas") != null)
    {
      return true;
    }
    var now = LocalDateTime.now();
    var startOfChristmas = LocalDateTime.of(now.getYear(), Month.DECEMBER, 7, 0, 0, 0);
    var endOfChristmas = LocalDateTime.of(now.getYear() + 1, Month.JANUARY, 6, 0, 0, 0);
    return now.isAfter(startOfChristmas) && now.isBefore(endOfChristmas);
  }

}
