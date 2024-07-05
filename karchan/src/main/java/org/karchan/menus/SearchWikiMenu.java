package org.karchan.menus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import mmud.database.entities.web.Wikipage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SearchWikiMenu extends Menu
{
  private static final Logger LOGGER = Logger.getLogger(SearchWikiMenu.class.getName());

  private static final int MAX_SEARCH_RESULTS = 50;

  public SearchWikiMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
  {
    LOGGER.entering("SearchWikiMenu", "setDatamodel");
    if (!parameters.containsKey("searchparams"))
    {
      root.put("foundWikipages", Collections.emptyList());
      return;
    }
    String searchparams = String.join("", parameters.get("searchparams"));
    LOGGER.info(searchparams);

    boolean isDeputy = root.get("isDeputy").equals(Boolean.TRUE);

    String sql = isDeputy ?
        "SELECT * FROM wikipages WHERE MATCH(content) AGAINST (?1 IN BOOLEAN MODE)" :
        "SELECT * FROM wikipages WHERE MATCH(content) AGAINST (?1 IN BOOLEAN MODE) and administration = false";
    Query searchResults = entityManager.createNativeQuery(sql, Wikipage.class);
    searchResults.setMaxResults(MAX_SEARCH_RESULTS);
    searchResults.setParameter(1, searchparams);
    @SuppressWarnings("unchecked")
    List<Wikipage> foundWikipages = (List<Wikipage>) searchResults.getResultList();
    root.put("foundWikipages", foundWikipages);
    root.put("searchparams", searchparams);
  }
}
