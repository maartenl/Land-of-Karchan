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
package org.karchan.menus;

import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import mmud.database.entities.web.Blog;
import mmud.database.entities.web.Faq;

/**
 *
 * @author maartenl
 */
public class MenuFactory
{
  
  private final static Logger LOGGER = Logger.getLogger(MenuFactory.class.getName());

  /**
   * Contains a mapping between the url (for example "blogs/index.html") and the
   * menu (for example "Blogs" with template name "blogs/index").
   */
  private static final Map<String, Menu> MENUS = new HashMap<>();

  @VisibleForTesting
  private static final Menu rootMenu;

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

  public static Menu getRootMenu()
  {
    return rootMenu;
  }

  public static Menu getNotFoundMenu()
  {
    return notFoundMenu;
  }

  /**
   * Returns the visible menu, based on the URL. Can return Null, in case the
   * menu option is not visible in the navigation bar.
   *
   * @param url the url to check.
   * @return a Menu or no menu.
   */
  public static Optional<Menu> findVisibleMenu(String url)
  {
    return getRootMenu().findVisibleMenu(url);
  }

  /**
   * Returns the top level menus (and the appropriate sub menus) for displaying 
   * purposes.
   * @return list of top level menus.
   */
  public static List<Menu> getNavigationBarMenus() {
    return getRootMenu().getSubMenu();
  }
  
  public static Menu createBlogMenu(String url) {
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
    return specificBlogMenu;
  }
}
