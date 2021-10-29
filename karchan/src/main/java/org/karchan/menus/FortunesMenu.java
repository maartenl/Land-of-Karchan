package org.karchan.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;

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
    List<Fortune> res = new ArrayList<>();
    Query query = entityManager.createNamedQuery("User.fortunes");
    query.setMaxResults(100);
    List<Object[]> list = query.getResultList();

    for (Object[] objectarray : list)
    {
      res.add(new Fortune((String) objectarray[0], (Integer) objectarray[1]));
    }
    root.put("fortunes", list);
  }
}
