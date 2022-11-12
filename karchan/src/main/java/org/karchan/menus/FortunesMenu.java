package org.karchan.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.karchan.webentities.Fortune;

public class FortunesMenu extends Menu
{

  FortunesMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]>
    parameters)
  {
    List<Fortune> fortunes = new ArrayList<>();
    Query query = entityManager.createNamedQuery("User.fortunes");
    query.setMaxResults(100);
    List<Object[]> list = query.getResultList();

    for (Object[] objectarray : list)
    {
      fortunes.add(new Fortune((String) objectarray[0], (Integer) objectarray[1]));
    }
    root.put("fortunes", fortunes);
  }
}
