package mmud.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Singleton;

/**
 * Keeps a registration on when which user has last issued a command.
 */
@Singleton
public class IdleUsersService
{
  /**
   * Indicates the number of minutes that a person can be idle (i.e. not submit any commands in game), currently set to 60 minutes.
   */
  private static final long MAX_IDLE_TIME = 60;

  private Map<String, LocalDateTime> idleusers = new ConcurrentHashMap<>();

  public void resetUser(String name)
  {
    idleusers.put(name, LocalDateTime.now());
  }

  public LocalDateTime removeUser(String name)
  {
    return idleusers.remove(name);
  }


  /**
   * Retrieves the idle time in minutes, i,e. between the last entered command
   * and the current time. Returns null if the last command date/time is not
   * available.
   *
   * @return Long containing minutes, or null.
   */
  public Long getIdleTime(String name)
  {
    LocalDateTime date = idleusers.get(name);
    if (date == null)
    {
      return null;
    }
    LocalDateTime now = LocalDateTime.now();
    return Duration.between(date, now).toMinutes();
  }

  /**
   * Check to see if the User was inactive for more than an hour.
   *
   * @return true if the User was inactive (i.e. has not entered a command) for
   * more than an hour. Wil return false if user is not playing.
   */
  public boolean isIdleTooLong(String name)
  {
    LocalDateTime localDateTime = idleusers.get(name);
    if (localDateTime == null)
    {
      return false;
    }
    return this.getIdleTime(name) != null && this.getIdleTime(name) > MAX_IDLE_TIME;
  }

  /**
   * Returns the time that a user was inactive.
   *
   * @param name the name of the user to check
   * @return String the number of minutes and seconds that the player has been
   * idle in the following format: "(&lt;min&gt; min, &lt;sec&gt; sec idle)". Or empty
   * string if user is not playing.
   */
  public String getIdleTimeInMinAndSeconds(String name)
  {
    LocalDateTime localDateTime = idleusers.get(name);
    if (localDateTime == null)
    {
      return "";
    }
    LocalDateTime now = LocalDateTime.now();
    Long timeDiff = Duration.between(localDateTime, now).toMillis() / 1000;
    return "(" + timeDiff / 60 + " min, " + timeDiff % 60 + " sec idle)";
  }

  /**
   * Returns a list of users that have been idle for more than one hour.
   *
   * @return list of names of users
   */
  public List<String> getIdleUsers()
  {
    if (idleusers.isEmpty())
    {
      return Collections.emptyList();
    }
    LocalDateTime allowedIdleTime = LocalDateTime.now().minusMinutes(MAX_IDLE_TIME);
    List<String> result = new ArrayList<>();
    idleusers.forEach((name, time) ->
    {
      if (time.isBefore(allowedIdleTime))
      {
        result.add(name);
      }
    });
    return result;
  }
}
