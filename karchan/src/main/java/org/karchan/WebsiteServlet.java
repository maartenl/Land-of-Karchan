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

import com.google.common.annotations.VisibleForTesting;
import org.karchan.menus.Menu;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.karchan.menus.MenuFactory;

@WebServlet("*.html")
public class WebsiteServlet extends HttpServlet
{

  private final static Logger LOGGER = Logger.getLogger(WebsiteServlet.class.getName());

  @Inject
  private Freemarker freemarker;

  @Inject
  private MenuFactory menuFactory;

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
    // Set response content type
    response.setContentType("text/html");

    final String url = getUrl(request);
    PrintWriter out = response.getWriter();

    // Actual logic goes here.

    /* Create a data-model */
    Map<String, Object> root = new HashMap<>();
    root.put("user", "Big Joe");
    root.put("menus", MenuFactory.getNavigationBarMenus());
    root.put("url", url);

    Optional<Menu> visibleMenu = MenuFactory.findVisibleMenu(url);
    if (visibleMenu.isPresent())
    {
      Menu activeMenu = visibleMenu.get();
      root.put("activeMenu", activeMenu.getName());
    } else
    {
      root.put("activeMenu", "none");
    }
    Optional<Menu> foundMenu = Menu.findMenu(url);
    if (!foundMenu.isPresent())
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
        foundMenu = Optional.of(menuFactory.createWikiMenu(url));
      }
    }
    foundMenu.ifPresent(menu ->
    {
      LOGGER.log(Level.FINEST, "Menu {0} found with url {1}.", new Object[]
      {
        menu, url
      });
      menuFactory.setDatamodel(menu, root, request.getParameterMap());
      root.put("lastBreadcrumb", menu);
      List<Menu> breadcrumbs = createBreadcrumbs(menu);
      LOGGER.log(Level.FINEST, "Breadcrumbs set to {0}.", breadcrumbs);
      root.put("breadcrumbs", breadcrumbs);
    });

    String templateName = foundMenu.map(menu -> menu.getTemplate()).orElse(url.replace(".html", ""));
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
    while (menu.getParent() != null && menu.getParent() != menuFactory.getRootMenu())
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

}
