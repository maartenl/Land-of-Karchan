package org.karchan.menus;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class PeopleMenu extends Menu
{

  PeopleMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]>
    parameters)
  {
    TypedQuery<String> query = entityManager.createNamedQuery("CharacterInfo.charactersheets", String.class);
    List<String> list = query.getResultList();
    root.put("people", list);
  }
}
