/*
 *  Copyright (C) 2012 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmud.constants;

import java.util.List;

/**
 * @author maartenl
 */
public class Adverbs
{

  private Adverbs()
  {
  }

  private static final List<String> adverbs = List.of(

      "absentmindedly", "aimlessly", "amazedly", "amusedly", "angrily",
      "anxiously", "appreciatively", "appropriately", "archly",
      "astonishingly", "attentively", "badly", "barely", "belatedly",
      "bitterly", "boringly", "breathlessly", "briefly", "brightly",
      "brotherly", "busily", "carefully", "cautiously", "charmingly",
      "cheerfully", "childishly", "clumsily", "coaxingly", "coldly",
      "completely", "confidently", "confusedly", "contentedly",
      "coquetishly", "courageously", "coyly", "crazily", "cunningly",
      "curiously", "cutely", "cynically", "dangerously", "deeply",
      "defiantly", "dejectedly", "delightedly", "delightfully",
      "deliriously", "demonically", "depressively", "derisively",
      "desperately", "devilishly", "dirtily", "disappointedly",
      "discretely", "disgustedly", "doubtfully", "dreamily", "dubiously",
      "earnestly", "egocentrically", "egoistically", "encouragingly",
      "endearingly", "enthusiastically", "enviously", "erotically",
      "evilly", "exhaustedly", "exuberantly", "faintly", "fanatically",
      "fatherly", "fiercefully", "firmly", "foolishly", "formally",
      "frantically", "friendly", "frostily", "funnily", "furiously",
      "generously", "gleefully", "gracefully", "graciously",
      "gratefully", "greedily", "grimly", "happily", "harmonically",
      "headlessly", "heartbrokenly", "heavily", "helpfully",
      "helplessly", "honestly", "hopefully", "humbly", "hungrily",
      "hysterically", "ignorantly", "impatiently", "inanely",
      "indecently", "indifferently", "innocently", "inquiringly",
      "inquisitively", "insanely", "instantly", "intensely",
      "interestedly", "ironically", "jauntily", "jealously", "joyfully",
      "joyously", "kindly", "knowingly", "lazily", "loudly", "lovingly",
      "lustfully", "madly", "maniacally", "melancholically",
      "menacingly", "mercilessly", "merrily", "mischieviously",
      "motherly", "musically", "mysteriously", "nastily", "naughtily",
      "nervously", "nicely", "noisily", "nonchalantly", "outrageously",
      "overwhelmingly", "painfully", "passionately", "patiently",
      "patronizingly", "perfectly", "personally", "physically",
      "pitifully", "playfully", "politely", "professionally",
      "profoundly", "profusely", "proudly", "questioningly", "quickly",
      "quietly", "quizzically", "randomly", "rapidly", "really",
      "rebelliously", "relieved", "reluctantly", "remorsefully",
      "repeatedly", "resignedly", "respectfully", "romantically",
      "rudely", "sadistically", "sadly", "sarcastically", "sardonically",
      "satanically", "scornfully", "searchingly", "secretively",
      "seductively", "sensually", "seriously", "sexily", "shamelessly",
      "sheepishly", "shyly", "sickly", "significantly", "silently",
      "sisterly", "skilfully", "sleepily", "slightly", "slowly", "slyly",
      "smilingly", "smugly", "socially", "softly", "solemnly",
      "strangely", "stupidly", "sweetly", "tearfully", "tenderly",
      "terribly", "thankfully", "theoretically", "thoughtfully",
      "tightly", "tiredly", "totally", "tragically", "truly",
      "trustfully", "uncontrollably", "understandingly", "unexpectedly",
      "unhappily", "unintentionally", "unknowingly", "vaguely",
      "viciously", "vigorously", "violently", "virtually", "warmly",
      "wearily", "wholeheartedly", "wickedly", "wildly", "wisely",
      "wistfully"
  );

  /**
   * Checks to see that an adverb is valid.
   *
   * @param anAdverb String containing the adverb to check, for example
   *                 "aimlessly".
   * @return boolean which is true if the adverb is real.
   */
  public static boolean existsAdverb(String anAdverb)
  {
    return adverbs.contains(anAdverb.toLowerCase());
  }

  public static List<String> getAdverbs()
  {
    return adverbs;
  }
}
