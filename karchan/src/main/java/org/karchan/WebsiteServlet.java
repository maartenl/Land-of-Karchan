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

  private final static Logger LOGGER = Logger.getLogger(WebsiteServlet.class.getName());

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
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
      {
        TypedQuery<Faq> faqQuery = entityManager.createNamedQuery("Faq.findAll", Faq.class);
        List<Faq> faq = faqQuery.getResultList();
        root.put("faq", faq);
      }
    };

    Menu welcome = new Menu("Welcome", "/index.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
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
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
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
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
      {
        Integer page = 1;
        TypedQuery<Long> numberOfBlogsQuery = entityManager.createNamedQuery("Blog.count", Long.class);
        Long numberOfPages = numberOfBlogsQuery.getSingleResult() / PAGE_SIZE + 1;

        TypedQuery<Blog> blogsQuery = entityManager.createNamedQuery("Blog.findAll", Blog.class);
        if (parameters != null)
        {
          String[] get = parameters.get("page");
          if (get != null && get.length == 1)
          {
            try
            {
              page = Integer.valueOf(get[0]);
              if (page > 0)
              {
                blogsQuery.setFirstResult((page - 1) * PAGE_SIZE);
              }
            } catch (NumberFormatException e)
            {
              // do nothing, default to an offset of 0.
            }
          }
        }
        blogsQuery.setMaxResults(PAGE_SIZE);
        List<Blog> blogs = blogsQuery.getResultList();
        root.put("blogs", blogs);
        root.put("page", page);
        root.put("size", numberOfPages);
      }
      private static final int PAGE_SIZE = 10;
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

    String filename = "index.html";

    final String url = getUrl(request);
    PrintWriter out = response.getWriter();

    // Actual logic goes here.

    /* Create a data-model */
    Map<String, Object> root = new HashMap<>();
    root.put("user", "Big Joe");

    root.put("menus", rootMenu.getSubMenu());
    root.put("url", url);

    Optional<Menu> visibleMenu = rootMenu.findVisibleMenu(url);
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
        Menu specificBlogMenu = new Menu("Blog", "blogs/specific.html")
        {
          @Override
          public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
          {
            LOGGER.finest("setDatamodel called for BlogSpecific menu");
            TypedQuery<Blog> blogsQuery = entityManager.createNamedQuery("Blog.findByUrlTitle", Blog.class);
            String searchBlog = url.substring("/blogs/".length()).replace(".html", "");
            blogsQuery.setParameter("title", searchBlog);
            List<Blog> blogs = blogsQuery.getResultList();
            if (blogs.size() == 1)
            {
              root.put("blog", blogs.get(0));
              setName(blogs.get(0).getTitle());
              Menu.findMenu("/blogs/index.html").ifPresent(menu -> this.setParent(menu));
            }
            if (blogs.size() > 1)
            {
              LOGGER.log(Level.SEVERE, "{0} blogs found... expected only one with name {1}.", new Object[]
              {
                blogs.size(), searchBlog
              });
            }
            if (blogs.isEmpty())
            {
              LOGGER.log(Level.SEVERE, "No blogs with name {0} found.", searchBlog);
            }
          }
        };
        foundMenu = Optional.of(specificBlogMenu);
      }
    }
    foundMenu.ifPresent(menu ->
    {
      LOGGER.log(Level.FINEST, "Menu {0} found with url {1}.", new Object[]
      {
        menu, url
      });
      menu.setDatamodel(entityManager, root, request.getParameterMap());
      root.put("lastBreadcrumb", menu);
      List<Menu> breadcrumbs = menu.getParent() == rootMenu || menu.getParent() == null ? Collections.emptyList() : Arrays.asList(menu.getParent());
      if (menu.getParent() != null)
      {
        LOGGER.log(Level.FINEST, "Menu {0} has parent with name {1}.", new Object[]
        {
          menu, menu.getParent()
        });
      } else
      {
        LOGGER.log(Level.FINEST, "Menu {0} has no parent.", menu);
      }
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
      LOGGER.log(Level.FINEST, "Template {0} not found.", templateName);
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
      LOGGER.log(Level.SEVERE, null, ex);
      throw new ServletException(ex);
    }
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
