package mmud.database.entities;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LevellingUtilitiesTest
{

  @DataProvider(name = "MaxExperiencePointsPerLevelDataProvider")
  public Object[][] maxExperiencePointsPerLevel()
  {
    return new Object[][]{
      {1, 50L},
      {2, 78L},
      {3, 119L},
      {4, 186L},
      {5, 286L},
      {6, 444L},
      {7, 686L},
      {8, 1062L},
      {9, 1643L},
      {10, 2542L},
      {11, 3934L}
    };
  }

  @DataProvider(name = "TotalExpToLevelDataProvider")
  public Object[][] totalExpToLevel()
  {
    return new Object[][]{
      {1, 49L},
      {2, 50L},
      {2, 50L + 77L},
      {3, 50L + 78L},
      {3, 50L + 78L + 119L - 1},
      {4, 50L + 78L + 119L},
    };
  }

  @DataProvider(name = "ExperienceFromTotalExpDataProvider")
  public Object[][] experienceFromTotalExp()
  {
    return new Object[][]{
      {49L, 49L, 1L},
      {0L, 50L, 78L},
      {77L, 50L + 78L - 1L, 1L},
      {0L, 50L + 78L, 119L},
      {118L, 50L + 78L + 119L - 1, 1L},
      {0L, 50L + 78L + 119L, 186L},
    };
  }

  @Test(dataProvider = "MaxExperiencePointsPerLevelDataProvider")
  public void testGetLevelExperienceRequirement(Integer level, Long expectedExp)
  {
    assertThat(LevellingUtilities.getLevelExperienceRequirement(level))
      .as("Level " + level + " was supposed to be " + expectedExp + " experience points, but was " + LevellingUtilities.getLevelExperienceRequirement(level) + " experience points.")
      .isEqualTo(expectedExp);
  }


  @Test(dataProvider = "TotalExpToLevelDataProvider")
  public void testGetLevel(Integer expectedLevel, Long totalExperience)
  {
    assertThat(LevellingUtilities.getLevel(totalExperience))
      .as("Total experience " + totalExperience + " was supposed to be level " + expectedLevel + ", but was level " + LevellingUtilities.getLevel(totalExperience) + ".")
      .isEqualTo(expectedLevel);
  }


  @Test(dataProvider = "ExperienceFromTotalExpDataProvider")
  public void testGetExperienceAndLeft(Long expectedExperience, Long totalExperience, Long expectedexperienceLeftUntilFollowingLevel)
  {
    assertThat(LevellingUtilities.getExperience(totalExperience))
      .as("Total experience " + totalExperience + " was supposed to be experience " + expectedExperience + ", but was experience " + LevellingUtilities.getExperience(totalExperience) + ".")
      .isEqualTo(expectedExperience);
    assertThat(LevellingUtilities.getExperienceLeftUntilFollowingLevel(totalExperience))
      .as("Total experience " + totalExperience + " was supposed to be experience left until next level " + expectedexperienceLeftUntilFollowingLevel + ", but was experience left until next level " + LevellingUtilities.getExperienceLeftUntilFollowingLevel(totalExperience) + ".")
      .isEqualTo(expectedexperienceLeftUntilFollowingLevel);
  }

}
