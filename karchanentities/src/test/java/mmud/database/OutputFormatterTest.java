package mmud.database;

import java.util.Arrays;

import org.testng.annotations.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OutputFormatterTest
{

  public static final String ADJECTIVES = "dusty, grey-trimmed, fur, white";
  public static final String NOUN = "cloak";

  @Test
  public void testCompareItemDescription()
  {
    assertThat(OutputFormatter.compareItemDescription(Arrays.asList("dusty grey-trimmed fur white cloak".split(" ")),
      ADJECTIVES, NOUN)).isTrue();
    assertThat(OutputFormatter.compareItemDescription(Arrays.asList(NOUN.split(" ")),
      ADJECTIVES, NOUN)).isTrue();
    assertThat(OutputFormatter.compareItemDescription(Arrays.asList("fur cloak".split(" ")),
      ADJECTIVES, NOUN)).isTrue();
    assertThat(OutputFormatter.compareItemDescription(Arrays.asList("grey cloak".split(" ")),
      ADJECTIVES, NOUN)).isFalse();
  }

  @Test
  public void testCompareItemDescriptionWithCommas()
  {
    assertThat(OutputFormatter.compareItemDescription(Arrays.asList("gold gown".split(" ")), "gold and black-accented, velvet", "gown")).isTrue();
    assertThat(OutputFormatter.compareItemDescription(Arrays.asList("gold and black-accented velvet gown".split(" ")), "gold and black-accented, velvet", "gown")).isTrue();
    assertThat(OutputFormatter.compareItemDescription(Arrays.asList("gold and black velvet gown".split(" ")), "gold and black-accented, velvet", "gown")).isFalse();
  }

  @Test
  public void testCompareItemDescriptionWithChristmas()
  {
    assertThat(OutputFormatter.compareItemDescription(Arrays.asList("ornamented brightly lit up christmas tree".split(" ")), "ornamented, brightly lit up, christmas", "tree")).isTrue();
  }
}
