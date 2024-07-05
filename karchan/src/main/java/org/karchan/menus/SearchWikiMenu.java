package org.karchan.menus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mmud.database.entities.web.Wikipage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SearchWikiMenu extends Menu
{
  private static final Logger LOGGER = Logger.getLogger(SearchWikiMenu.class.getName());

  private static final int MAX_SEARCH_RESULTS = 50;
  public static final String SEARCHPARAMETERS = "searchparams";

  public SearchWikiMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
  {
    LOGGER.entering("SearchWikiMenu", "setDatamodel");
    if (!parameters.containsKey(SEARCHPARAMETERS))
    {
      root.put("foundWikipages", Collections.emptyList());
      return;
    }
    String searchparams = String.join("", parameters.get(SEARCHPARAMETERS));
    LOGGER.info(searchparams);

    boolean isDeputy = root.get("isDeputy").equals(Boolean.TRUE);

    String sql = isDeputy ?
        "Wikipage.searchWikipagesAsDeputy" :
        "Wikipage.searchWikipages";
    TypedQuery<Wikipage> searchResults = entityManager.createNamedQuery(sql, Wikipage.class);
    searchResults.setMaxResults(MAX_SEARCH_RESULTS);
    searchResults.setParameter(1, searchparams);
    List<Wikipage> foundWikipages = searchResults.getResultList();
    root.put("foundWikipages", foundWikipages);
    root.put(SEARCHPARAMETERS, searchparams);
  }
}
