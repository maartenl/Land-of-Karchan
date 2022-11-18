package mmud.services;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Guildrank;

public class GuildService
{
  private static final Logger LOGGER = Logger.getLogger(GuildService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  /**
   * Returns the entity manager of JPA. This is defined in
   * build/web/WEB-INF/classes/META-INF/persistence.xml.
   *
   * @return EntityManager
   */
  protected EntityManager getEntityManager()
  {
    return em;
  }

  /**
   * Retrieves the guild by name. Returns null if not found.
   *
   * @param name the name of the guild.
   * @return a guild or null.
   */
  public Guild getGuild(String name)
  {
    return getEntityManager().find(Guild.class, name);
  }

  public void deleteGuild(User person)
  {
    LOGGER.finer("entering deleteGuild");
    Guild guild = person.getGuild();
    for (User guildmember : guild.getMembers())
    {
      guildmember.setGuild(null);
    }
    SortedSet<User> emptySet = new TreeSet<>();
    guild.setMembers(emptySet);
    for (Guildrank rank : guild.getGuildrankCollection())
    {
      getEntityManager().remove(rank);
    }
    SortedSet<Guildrank> emptyRanks = new TreeSet<>();
    guild.setGuildrankCollection(emptyRanks);
    getEntityManager().remove(guild);
  }


}
