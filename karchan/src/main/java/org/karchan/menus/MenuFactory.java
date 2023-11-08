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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Help;
import mmud.database.entities.web.Blog;
import mmud.database.entities.web.CharacterInfo;
import mmud.database.entities.web.Family;
import mmud.database.entities.web.Wikipage;
import org.karchan.wiki.WikiRenderer;

/**
 * @author maartenl
 */
public class MenuFactory
{

  private static final Logger LOGGER = Logger.getLogger(MenuFactory.class.getName());

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
    Menu people = new PeopleMenu("People", "/chronicles/people.html");
    Menu fortunes = new FortunesMenu("Fortunes", "/chronicles/fortunes.html");
    Menu guilds = new GuildsMenu("Guilds", "/chronicles/guilds.html");

    add(map, history, people, fortunes, guilds);

    Menu status = new StatusMenu("Status", "/help/status.html");
    Menu guide = new SimpleMenu("The Guide", "/help/guide.html");
    Menu commandReference = new CommandReferenceMenu("Command reference", "/help/commandreference.html");
    Menu techSpecs = new SimpleMenu("Tech Specs", "/help/tech_specs.html");
    Menu source = new SimpleMenu("Source", "/help/source.html");
    Menu serverMetrics = new SimpleMenu("Server metrics", "/help/metrics.html");
    Menu security = new SimpleMenu("Security", "/help/security.html");
    Menu darkmode = new SimpleMenu("Darkmode", "/help/darkmode.html");

    add(status, guide, commandReference, techSpecs, serverMetrics, source, security, darkmode);

    Menu faq = new FaqMenu("FAQ", "/help/faq.html");
    add(faq);

    Menu welcome = new WelcomeMenu("Welcome", "/index.html");
    Menu logon = new SimpleMenu("Logon", "/logon.html");
    Menu introduction = new SimpleMenu("Introduction", "/introduction.html");
    Menu newCharacter = new SimpleMenu("New character", "/new_character.html");

    add(welcome, logon, introduction, newCharacter);

    Menu chronicles = new SimpleMenu("Chronicles", "/chronicles/index.html",
      List.of(map, history, dash, people, fortunes, guilds));

    add(chronicles);

    Menu who = new SimpleMenu("Who", "/who.html");
    Menu theLaw = new SimpleMenu("The Law", "/the_law.html");

    add(who, theLaw);

    Menu help = new SimpleMenu("Help", "/help/index.html",
      List.of(status, guide, commandReference, techSpecs, serverMetrics, source, security, darkmode, faq));

    add(help);

    Menu links = new SimpleMenu("Links", "/links.html");
    Menu wiki = new WikiMenu("Wiki", "/wiki/index.html");
    add(links, wiki);

    rootMenu = new SimpleMenu("root", " root ",
      List.of(welcome, logon, introduction, newCharacter, chronicles, who, theLaw, help, links, wiki));
    add(rootMenu);

    Menu blogs = new Menu("Blogs", "/blogs/index.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
      {
        int page = 1;
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
              page = Integer.parseInt(get[0]);
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

    Menu settings = new SimpleMenu("Settings", "/game/settings.html");

    add(settings);

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
    return new Menu("Blog", "blogs/specific.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager1, Map<String, Object> root, Map<String, String[]> parameters)
      {
        LOGGER.finest("setDatamodel called for BlogSpecific menu");
        TypedQuery<Blog> blogsQuery = entityManager1.createNamedQuery("Blog.findByUrlTitle", Blog.class);
        String searchBlog = url.substring("/blogs/".length()).replace(".html", "");
        blogsQuery.setParameter("title", searchBlog);
        List<Blog> blogs = blogsQuery.getResultList();
        if (blogs.size() == 1)
        {
          root.put("blog", blogs.get(0));
          setName(blogs.get(0).getTitle());
          MenuFactory.findMenu("/blogs/index.html").ifPresent(this::setParent);
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
  }

  public Menu createWikiMenu(String url, boolean isDeputy)
  {
    return new Menu("Wiki", "wiki/specific.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager1, Map<String, Object> root, Map<String, String[]> parameters)
      {
        LOGGER.finest("setDatamodel called for WikiSpecific menu");
        String searchWiki;
        searchWiki = URLDecoder.decode(url.substring("/wiki/".length()).replace(".html", ""), StandardCharsets.UTF_8);

        String namedQuery = isDeputy
          ? "Wikipage.findByTitleAuthorized"
          : "Wikipage.findByTitle";
        TypedQuery<Wikipage> wikipageQuery = entityManager1.createNamedQuery(namedQuery, Wikipage.class);
        wikipageQuery.setParameter("title", searchWiki);
        List<Wikipage> wikipages = wikipageQuery.getResultList();
        if (wikipages.size() == 1)
        {
          final Wikipage wikipage = wikipages.get(0);
          wikipage.setHtmlContent(new WikiRenderer().render(wikipage.getContent()));
          root.put("wikipage", wikipage);
          setName(wikipage.getTitle());
          createBreadcrumbsFromWikipages(wikipage, this);
          root.put("children", wikipage.getChildren());
        } else
        {
          LOGGER.log(Level.SEVERE, "{0} wikipages with name {1} found. (deputy={2})", new Object[]
            {
              wikipages.size(), searchWiki, isDeputy
            });
        }
      }
    };
  }

  /**
   * Creates the datamodel for the character sheet in the freemarker root.
   *
   * @return the appropriate menu
   */
  public Menu createPersonMenu()
  {
    return new Menu("Person", "chronicles/person.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager1, Map<String, Object> root, Map<String, String[]> parameters)
      {
        LOGGER.finest("setDatamodel called for Person menu");
        parameters.forEach((key, value) -> LOGGER.finest(key + ":" + Arrays.toString(value)));
        String[] names = parameters.get("name");
        if (names == null) {
          return;
        }
        Optional<String> name = Arrays.stream(names).findFirst();
        if (name.isEmpty())
        {
          return;
        }

        User person = entityManager1.find(User.class, name.get());
        if (person == null)
        {
          return;
        }

        TypedQuery<Family> query = entityManager1.createNamedQuery("Family.findByName", Family.class);
        query.setParameter("name", person.getName());
        List<Family> family = query.getResultList();
        CharacterInfo characterInfo = entityManager1.find(CharacterInfo.class, person.getName());

        root.put("person", person);
        root.put("family", family);
        root.put("characterInfo", characterInfo);
      }
    };
  }

  /**
   * Creates the datamodel for the command explanation in the freemarker root.
   *
   * @return the appropriate menu
   */
  public Menu createCommandMenu()
  {
    return new Menu("Command", "help/command.html")
    {
      @Override
      public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
      {
        LOGGER.finest("setDatamodel called for Command menu");
        parameters.forEach((key, value) -> LOGGER.finest(key + ":" + Arrays.toString(value)));
        String[] commands = parameters.get("command");
        if (commands == null) {
          return;
        }
        Optional<String> command = Arrays.stream(commands).findFirst().map(cmd -> cmd.toLowerCase(Locale.ROOT));
        if (command.isEmpty())
        {
          return;
        }

        Help help = entityManager.find(Help.class, command.get());
        if (help == null)
        {
          return;
        }

        root.put("command", help);
      }
    };
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
    if (result.isEmpty())
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
