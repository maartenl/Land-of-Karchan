package mmud.database.entities;

import mmud.exceptions.MudException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class LevellingUtilities
{

  private static final int levels = 20;
  private static final int experienceForFirstLevel = 50;
  private static final int experienceForLastLevel = 200_000;

  private static Map<Integer, Long> map = new HashMap<>();
  /**
   * Shows how much experience points you need to have AT LEAST, to get this level.
   */
  private static final long[] levelRange = new long[levels + 1];

  static
  {
    double b = Math.log((double) experienceForLastLevel / experienceForFirstLevel) / (levels - 1);
    double a = (double) experienceForFirstLevel / (Math.exp(b) - 1.0);
    long total = 0;
    levelRange[0] = 0;
    for (int i = 1; i <= levels; i++)
    {
      long oldExp = Math.round(a * Math.exp(b * (i - 1)));
      long newExp = Math.round(a * Math.exp(b * i));
      map.put(i, newExp - oldExp);
      total += (newExp - oldExp);
      levelRange[i] = total;
    }
  }

  private LevellingUtilities()
  {
  }

  /**
   *
   * @param level the level of which to return how much experience points are needed.
   * @return how many experience points are neeeded?
   */
  public static Long getLevelExperienceRequirement(Integer level)
  {
    if (level == null)
    {
      throw new MudException("Level is null.");
    }
    if (level < 1)
    {
      throw new MudException("Level cannot be smaller than 0.");
    }
    if (level > 20)
    {
      throw new MudException("Level too high.");
    }
    return map.get(level);
  }

  /**
   * Returns the level based on total experience.
   * @param totalExperience the total amount of experience gained.
   * @return the level appropriate to this.
   */
  public static Integer getLevel(Long totalExperience)
  {
    return IntStream.range(0, levels)
      .filter(level -> levelRange[level] <= totalExperience)
      .max()
      .orElseThrow(() -> new MudException("cannot compute level from experience " + totalExperience + ".")) + 1;
  }

  /**
   * Returns the amount of experience gained for the current level.
   * @param totalExperience the total amount of experience gained.
   * @return the amount of experience gained for the current level.
   */
  public static Long getExperience(Long totalExperience)
  {
    return totalExperience - Arrays.stream(levelRange)
      .filter(experience -> experience <= totalExperience)
      .max()
      .orElseThrow(() -> new MudException("cannot compute experience for this level from total experience " + totalExperience + "."));
  }

  public static Long getExperienceLeftUntilFollowingLevel(Long totalExperience) {
    return Arrays.stream(levelRange)
      .filter(experience -> experience > totalExperience)
      .min()
      .orElseThrow(() -> new MudException("cannot compute experience for this level from total experience " + totalExperience + "."))
      - totalExperience;
  }
}
