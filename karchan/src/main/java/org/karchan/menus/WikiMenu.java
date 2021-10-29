package org.karchan.menus;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import mmud.database.entities.web.Wikipage;
import org.karchan.wiki.WikiRenderer;

public class WikiMenu extends Menu
{
  private static final Logger LOGGER = Logger.getLogger(WikiMenu.class.getName());

  private static final int MAX_RECENT_WIKIPAGES_EDITED = 8;

  public WikiMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
  {
    TypedQuery<Wikipage> recentQuery = entityManager.createNamedQuery("Wikipage.findRecentEdits", Wikipage.class);
    recentQuery.setMaxResults(MAX_RECENT_WIKIPAGES_EDITED);
    List<Wikipage> recentEdits = recentQuery.getResultList();
    root.put("recentEdits", recentEdits);

    TypedQuery<Wikipage> wikipageQuery = entityManager.createNamedQuery("Wikipage.findFrontpage", Wikipage.class);
    List<Wikipage> wikipages = wikipageQuery.getResultList();
    if (wikipages.size() == 1)
    {
      final Wikipage wikipage = wikipages.get(0);
      wikipage.setHtmlContent(new WikiRenderer().render(wikipage.getContent()));
      root.put("wikipage", wikipage);
      root.put("children", wikipage.getChildren());
    } else
    {
      LOGGER.log(Level.SEVERE, "{0} main wikipages ('FrontPage') found.", wikipages.size());
    }
  }
}
