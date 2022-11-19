package mmud.services;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
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
   * Retrieves the guild by name. Returns null if not found.
   *
   * @param name the name of the guild.
   * @return a guild or null.
   */
  public Guild getGuild(String name)
  {
    return em.find(Guild.class, name);
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
      em.remove(rank);
    }
    SortedSet<Guildrank> emptyRanks = new TreeSet<>();
    guild.setGuildrankCollection(emptyRanks);
    em.remove(guild);
  }

  /**
   * Should only be used for TESTING! You hear that!
   */
  @VisibleForTesting
  public void setEntityManager(EntityManager entityManager)
  {
    this.em = entityManager;
  }
}
