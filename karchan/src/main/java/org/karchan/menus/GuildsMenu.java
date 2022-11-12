package org.karchan.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mmud.database.entities.game.Guild;
import org.apache.commons.lang3.StringUtils;
import org.karchan.webentities.PublicGuild;

public class GuildsMenu extends Menu
{

  private final static Logger LOGGER = Logger.getLogger(GuildsMenu.class.getName());

  public GuildsMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]>
    parameters)
  {
    List<PublicGuild> guilds = new ArrayList<>();
    TypedQuery<Guild> query = entityManager.createNamedQuery("Guild.findAll", Guild.class);
    List<Guild> list = query.getResultList().stream()
      .filter(guild -> StringUtils.isNotBlank(guild.getTitle()))
      .collect(Collectors.toList());

    for (Guild guild : list)
    {
      PublicGuild newGuild = new PublicGuild(guild);
      guilds.add(newGuild);
    }
    root.put("guilds", guilds);
  }
}
