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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import mmud.database.entities.web.Blog;
import mmud.database.entities.web.Faq;
import mmud.database.entities.web.Wikipage;

/**
 *
 * @author maartenl
 */
@Stateless
@LocalBean
public class MenuFactory
{

  private final static Logger LOGGER = Logger.getLogger(MenuFactory.class.getName());

  /**
   * Contains a mapping between the url (for example "blogs/index.html") and the
   * menu (for example "Blogs" with template name "blogs/index").
   */
  private static final Map<String, Menu> MENUS = new HashMap<>();

  @PersistenceContext
  private EntityManager entityManager;

  @VisibleForTesting
  private static final Menu rootMenu;

  private static final Menu notFoundMenu;

  private static void add(Menu... menus)
  {
    Arrays.stream(menus).forEach(menu -> MENUS.put(menu.getUrl(), menu));
  }

  static
  {
    notFoundMenu = new SimpleMenu("Not found", "/notFound.html");
    add(notFoundMenu);
    
    Menu dash = new Dash();

    Menu map = new SimpleMenu("Map", "/chronicles/map.html");
    Menu history = new SimpleMenu("History", "/chronicles/history.html");
    Menu people = new SimpleMenu("People", "/chronicles/people.html");
    Menu fortunes = new SimpleMenu("Fortunes", "/chronicles/fortunes.html");
    Menu guilds = new SimpleMenu("Guilds", "/chronicles/guilds.html");
    add(map, history, people, fortunes, guilds);

    Menu status = new SimpleMenu("Status", "/help/status.html");
    Menu guide = new SimpleMenu("The Guide", "/help/guide.html");
    Menu techSpecs = new SimpleMenu("Tech Specs", "/help/tech_specs.html");
    Menu source = new SimpleMenu("Source", "/help/source.html");
    Menu security = new SimpleMenu("Security", "/help/security.html");
    add(status, guide, techSpecs, source, security);
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
    add(faq);
    
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
    Menu logon = new SimpleMenu("Logon", "/logon.html");
    Menu introduction = new SimpleMenu("Introduction", "/introduction.html");
    Menu newCharacter = new SimpleMenu("New character", "/new_character.html");
    add(welcome, logon, introduction, newCharacter);
    
    Menu chronicles = new SimpleMenu("Chronicles", "/chronicles/index.html",
            Arrays.asList(map, history, dash, people, fortunes, guilds));
    add(chronicles);

    Menu who = new SimpleMenu("Who", "/who.html");
    Menu theLaw = new SimpleMenu("The Law", "/the_law.html");
    add(who, theLaw);

    Menu help = new SimpleMenu("Help", "/help/index.html",
            Arrays.asList(status, guide, techSpecs, source, security, faq));
    add(help);

    Menu links = new SimpleMenu("Links", "/links.html");
    Menu wiki = new Menu("Wiki", "/wiki/index.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
      {
        TypedQuery<Wikipage> blogsQuery = entityManager.createNamedQuery("Wikipage.findByTitle", Wikipage.class);
        blogsQuery.setParameter("title", "FrontPage");
        List<Wikipage> wikipages = blogsQuery.getResultList();
        if (wikipages.size() == 1)
        {
          root.put("wikipage", wikipages.get(0));
        } else
        {
          LOGGER.log(Level.SEVERE, "{0} main wikipages ('FrontPage') found.", wikipages.size());
        }
      }
    };
    add(links, wiki);

    rootMenu = new SimpleMenu("root", " root ",
            Arrays.asList(welcome, logon, introduction, newCharacter,
                    chronicles, who, theLaw, help, links, wiki));
    add(rootMenu);

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
    add(blogs);

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
   *
   * @return list of top level menus.
   */
  public static List<Menu> getNavigationBarMenus()
  {
    return getRootMenu().getSubMenu();
  }

  public Menu createBlogMenu(String url)
  {
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
          MenuFactory.findMenu("/blogs/index.html").ifPresent(menu -> this.setParent(menu));
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

  public Menu createWikiMenu(String url)
  {
    Menu specificWikipageMenu = new Menu("Wiki", "wiki/specific.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
      {
        LOGGER.finest("setDatamodel called for WikiSpecific menu");
        TypedQuery<Wikipage> blogsQuery = entityManager.createNamedQuery("Wikipage.findByTitle", Wikipage.class);
        String searchWiki;
        try
        {
          searchWiki = URLDecoder.decode(url.substring("/wiki/".length()).replace(".html", ""), "UTF-8");
        } catch (UnsupportedEncodingException ex)
        {
          Logger.getLogger(MenuFactory.class.getName()).log(Level.SEVERE, null, ex);
          throw new RuntimeException(ex);
        }
        blogsQuery.setParameter("title", searchWiki);
        List<Wikipage> wikipages = blogsQuery.getResultList();
        if (wikipages.size() == 1)
        {
          root.put("wikipage", wikipages.get(0));
          setName(wikipages.get(0).getTitle());
          createBreadcrumbsFromWikipages(wikipages.get(0), this);
        } else
        {
          LOGGER.log(Level.SEVERE, "{0} wikipages with name {1} found.", new Object[]
          {
            wikipages.size(), searchWiki
          });
        }
      }
    };
    return specificWikipageMenu;
  }

  private static void createBreadcrumbsFromWikipages(Wikipage childPage, Menu childMenu)
  {
    LOGGER.log(Level.FINEST, "createBreadcrumbsFromWikipages: {0}", childPage);
    Wikipage parent = childPage.getParent();
    if (parent == null)
    {
      Optional<Menu> rootMenu = MenuFactory.findMenu("/wiki/index.html");
      if (rootMenu.isPresent())
      {
        childMenu.setParent(rootMenu.get());
      }
      return;
    }
    while (parent != null)
    {
      LOGGER.log(Level.FINEST, "createBreadcrumbsFromWikipages: has parent {0}", parent);
      Menu menu = new SimpleMenu(parent.getTitle(), "/wiki/" + parent.getTitle() + ".html");
      childMenu.setParent(menu);
      childMenu = menu;
      if (parent.getParent() == null)
      {
        Optional<Menu> rootMenu = MenuFactory.findMenu("/wiki/index.html");
        if (rootMenu.isPresent())
        {
          childMenu.setParent(rootMenu.get());
        }
      }
      parent = parent.getParent();
    }
  }

  public void setDatamodel(Menu menu, Map<String, Object> root, Map<String, String[]> parameterMap)
  {
    menu.setDatamodel(entityManager, root, parameterMap);
  }

  /**
   * Looks in all menus that have been created.
   *
   * @param url the url to find, for example "/blogs/index.html".
   * @return an optional indicating a found menu or nothing.
   */
  public static Optional<Menu> findMenu(String url)
  {
    Optional<Menu> result = Optional.ofNullable(MENUS.get(url));
    if (!result.isPresent())
    {
      LOGGER.log(Level.FINEST, "Menu with url {0} not found.", url);
      LOGGER.log(Level.FINEST, "Available menus are {0}.", MENUS.keySet());
    } else
    {
      LOGGER.log(Level.FINEST, "Menu with url {0} found.", url);
    }
    return result;
  }

}
