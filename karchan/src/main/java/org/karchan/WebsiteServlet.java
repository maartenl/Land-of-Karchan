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

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mmud.database.entities.web.Blog;
import mmud.database.entities.web.Faq;
import org.assertj.core.util.VisibleForTesting;

@WebServlet("*.html")
public class WebsiteServlet extends HttpServlet
{

  @Inject
  private Freemarker freemarker;

  @PersistenceContext
  private EntityManager entityManager;

  @VisibleForTesting
  static final Menu rootMenu;

  private static final Menu notFoundMenu;

  static
  {
    notFoundMenu = new Menu("Not found", "/notFound.html");

    Menu dash = new Menu("--", "--");

    Menu map = new Menu("Map", "/chronicles/map.html");
    Menu history = new Menu("History", "/chronicles/history.html");
    Menu people = new Menu("People", "/chronicles/people.html");
    Menu fortunes = new Menu("Fortunes", "/chronicles/fortunes.html");
    Menu guilds = new Menu("Guilds", "/chronicles/guilds.html");

    Menu status = new Menu("Status", "/help/status.html");
    Menu guide = new Menu("The Guide", "/help/guide.html");
    Menu techSpecs = new Menu("Tech Specs", "/help/tech_specs.html");
    Menu source = new Menu("Source", "/help/source.html");
    Menu security = new Menu("Security", "/help/security.html");
    Menu faq = new Menu("FAQ", "/help/faq.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root)
      {
        TypedQuery<Faq> faqQuery = entityManager.createNamedQuery("Faq.findAll", Faq.class);
        List<Faq> faq = faqQuery.getResultList();
        root.put("faq", faq);
      }
    };

    Menu welcome = new Menu("Welcome", "/index.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root)
      {
        TypedQuery<Blog> blogsQuery = entityManager.createNamedQuery("Blog.findAll", Blog.class);
        blogsQuery.setMaxResults(5);
        List<Blog> blogs = blogsQuery.getResultList();
        root.put("blogs", blogs);
      }

    };
    Menu logon = new Menu("Logon", "/logon.html");
    Menu introduction = new Menu("Introduction", "/introduction.html");
    Menu newCharacter = new Menu("New character", "/new_character.html");

    Menu chronicles = new Menu("Chronicles", "/chronicles/index.html",
            Arrays.asList(map, history, dash, people, fortunes, guilds));

    Menu who = new Menu("Who", "/who.html");
    Menu theLaw = new Menu("The Law", "/the_law.html");

    Menu help = new Menu("Help", "/help/index.html",
            Arrays.asList(status, guide, techSpecs, source, security, faq));

    Menu links = new Menu("Links", "/links.html");
    Menu wiki = new Menu("Wiki", "/wiki/index.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root)
      {
        root.put("wikicontent", "<p>Woah nelly!</p>");
      }
    };

    rootMenu = new Menu("root", " root ",
            Arrays.asList(welcome, logon, introduction, newCharacter,
                    chronicles, who, theLaw, help, links, wiki));

    Menu blogs = new Menu("Blogs", "/blogs/index.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root)
      {
        TypedQuery<Blog> blogsQuery = entityManager.createNamedQuery("Blog.findAll", Blog.class);
        if (root.get("parameters") != null)
        {
          String get = ((Map<String, String>) root.get("parameters")).get("offset");
          if (get != null)
          {
            try
            {
              Integer offset = Integer.valueOf(get);
              if (offset > 0)
              {
                blogsQuery.setFirstResult(offset);
              }
            } catch (NumberFormatException e)
            {
            }
          }
        }
        blogsQuery.setMaxResults(5);
        List<Blog> blogs = blogsQuery.getResultList();
        root.put("blogs", blogs);
      }
    };

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

    List<String> breadcrumbs = Arrays.asList(request.getRequestURI().split("/"));

    String filename = "index.html";

    String url = request.getRequestURI();
    PrintWriter out = response.getWriter();
    if (url.equals("/"))
    {
      url = "/index.html";
    }
    final String templateName = url.replace(".html", "");

    // Actual logic goes here.

    /* Create a data-model */
    Map<String, Object> root = new HashMap<>();
    root.put("user", "Big Joe");
    root.put("parameters", request.getParameterMap());
    root.put("menus", rootMenu.getSubMenu());
    root.put("url", url);
    root.put("template", templateName);

    Optional<Menu> visibleMenu = rootMenu.findVisibleMenu(url);
    if (visibleMenu.isPresent())
    {
      Menu activeMenu = visibleMenu.get();
      root.put("activeMenu", activeMenu.getName());
      root.put("breadcrumbs", activeMenu.getParent() == rootMenu ? Collections.emptyList() : Arrays.asList(activeMenu.getParent()));
    } else
    {
      root.put("activeMenu", "none");
      root.put("breadcrumbs", Collections.emptyList());
    }
    Menu.findMenu(url).ifPresent(menu ->
    {
      root.put("lastBreadcrumb", menu);
      menu.setDatamodel(entityManager, root);
    });

    /* Get the template (uses cache internally) */
    Template header = freemarker.getConfiguration().getTemplate("header");
    Template main;
    try
    {
      main = freemarker.getConfiguration().getTemplate(templateName);
    } catch (TemplateNotFoundException notFound)
    {
      root.put("activeMenu", "none");
      root.put("breadcrumbs", Collections.emptyList());
      root.put("lastBreadcrumb", notFoundMenu);
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
      Logger.getLogger(WebsiteServlet.class.getName()).log(Level.SEVERE, null, ex);
      throw new ServletException(ex);
    }
  }

}
