package org.karchan.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import mmud.database.entities.characters.User;
import mmud.database.entities.game.Guild;
import org.karchan.webentities.PublicAdmin;
import org.karchan.webentities.PublicGuild;

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
