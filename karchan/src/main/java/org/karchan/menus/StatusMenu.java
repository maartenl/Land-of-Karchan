package org.karchan.menus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import mmud.database.entities.characters.User;
import org.karchan.webentities.PublicAdmin;

public class StatusMenu extends Menu
{
  public StatusMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]>
    parameters)
  {
    List<PublicAdmin> list = entityManager.createNamedQuery("User.status", User.class).getResultList().stream()
      .map(PublicAdmin::new)
      .collect(Collectors.toList());
    root.put("status", list);
  }
}
