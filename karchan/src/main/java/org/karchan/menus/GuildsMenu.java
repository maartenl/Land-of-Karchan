package org.karchan.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;

import mmud.database.entities.game.Guild;
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
    List<PublicGuild> res = new ArrayList<>();
    TypedQuery<Guild> query = entityManager.createNamedQuery("Guild.findAll", Guild.class);
    List<Guild> list = query.getResultList();

    for (Guild guild : list)
    {
      PublicGuild newGuild = new PublicGuild();
      newGuild.guildurl = guild.getHomepage();
      newGuild.title = guild.getTitle();
      if (guild.getBoss() == null)
      {
        LOGGER.log(Level.INFO, "guilds: no boss found for guild {0}", guild.getName());
      } else
      {
        newGuild.bossname = guild.getBoss().getName();
      }
      newGuild.guilddescription = guild.getDescription();
      newGuild.creation = guild.getCreation();
      res.add(newGuild);
    }
    root.put("guilds", list);
  }
}
