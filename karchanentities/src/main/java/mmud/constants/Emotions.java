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

import java.util.*;

public class Emotions
{

  public record Emotion(String i, String heshe)
  {
  }

  private static final TreeMap<String, Emotion> emotionStructure = new TreeMap<>();
  private static final TreeMap<String, Emotion> targetEmotionStructure = new TreeMap<>();
  private static final List<Emotion> emotions = List.of(
      new Emotion("agree", "agrees"),
      new Emotion("apologize", "apologizes"),
      new Emotion("blink", "blinks"),
      new Emotion("cheer", "cheers"),
      new Emotion("chuckle", "chuckles"),
      new Emotion("cough", "coughs"),
      new Emotion("dance", "dances"),
      new Emotion("disagree", "disagrees"),
      new Emotion("flinch", "flinches"),
      new Emotion("flirt", "flirts"),
      new Emotion("frown", "frowns"),
      new Emotion("giggle", "giggles"),
      new Emotion("glare", "glares"),
      new Emotion("grimace", "grimaces"),
      new Emotion("grin", "grins"),
      new Emotion("groan", "groans"),
      new Emotion("growl", "growls"),
      new Emotion("grumble", "grumbles"),
      new Emotion("grunt", "grunts"),
      new Emotion("hmm", "hmms"),
      new Emotion("howl", "howls"),
      new Emotion("hum", "hums"),
      new Emotion("kneel", "kneels"),
      new Emotion("kneel", "kneels"),
      new Emotion("listen", "listens"),
      new Emotion("melt", "melts"),
      new Emotion("mumble", "mumbles"),
      new Emotion("mutter", "mutters"),
      new Emotion("nod", "nods"),
      new Emotion("purr", "purrs"),
      new Emotion("shrug", "shrugs"),
      new Emotion("sigh", "sighs"),
      new Emotion("smile", "smiles"),
      new Emotion("smirk", "smirks"),
      new Emotion("snarl", "snarls"),
      new Emotion("sneeze", "sneezes"),
      new Emotion("stare", "stares"),
      new Emotion("think", "thinks"),
      new Emotion("wave", "waves"),
      new Emotion("whistle", "whistles"),
      new Emotion("wink", "winks"),
      new Emotion("laugh", "laughs out loud"),
      new Emotion("wonder", "wonders"),
      new Emotion("wince", "winces")
  );
  private static final List<Emotion> targetedEmotions =
      List.of(
          new Emotion("caress", "caresses"),
          new Emotion("comfort", "comforts"),
          new Emotion("confuse", "confuses"),
          new Emotion("congratulate", "congratulates"),
          new Emotion("cuddle", "cuddles"),
          new Emotion("fondle", "fondles"),
          new Emotion("greet", "greets"),
          new Emotion("hug", "hugs"),
          new Emotion("ignore", "ignores"),
          new Emotion("kick", "kicks"),
          new Emotion("kiss", "kisses"),
          new Emotion("knee", "knees"),
          new Emotion("lick", "licks"),
          new Emotion("like", "likes"),
          new Emotion("love", "loves"),
          new Emotion("nudge", "nudges"),
          new Emotion("pat", "pats"),
          new Emotion("pinch", "pinches"),
          new Emotion("poke", "pokes"),
          new Emotion("slap", "slaps"),
          new Emotion("smooch", "smooches"),
          new Emotion("sniff", "sniffes"),
          new Emotion("squeeze", "squeezes"),
          new Emotion("tackle", "tackles"),
          new Emotion("thank", "thanks"),
          new Emotion("tickle", "tickles"),
          new Emotion("worship", "worships")
      );

  static
  {
    for (Emotion emotion : Emotions.emotions)
    {
      Emotions.emotionStructure.put(emotion.i, emotion);
    }
    for (Emotion emotion : Emotions.targetedEmotions)
    {
      Emotions.targetEmotionStructure.put(emotion.i, emotion);
    }
  }

  /**
   * Returns a sorted collection of emotions, like cheer. Can have a target,
   * but isn't necessary.
   *
   * @return returns a sorted collection containing emotions. For example ['agree','apologize','blink',..].
   */
  public static Set<String> getEmotions()
  {
    return emotionStructure.keySet();
  }

  /**
   * Returns an sorted collection of emotions, like greet. Must have a target.
   *
   * @return returns a sorted collection containing emotions. For example ['thank','tickle','worship',..].
   */
  public static Set<String> getTargetEmotions()
  {
    return targetEmotionStructure.keySet();
  }

  /**
   * returns the appropriate emotion for a third person view.
   *
   * @param anEmotion the emotion, for example "whistle".
   * @return the third person grammar, for example "whistles".
   * @see #returnEmotionTo
   */
  public static Optional<Emotion> returnEmotion(String anEmotion)
  {
    if (anEmotion == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(emotionStructure.get(anEmotion.toLowerCase()));
  }

  /**
   * returns the appropriate emotion for a third person view. The difference
   * with returnEmotion is that this has a target.
   *
   * @param anEmotion the emotion, for example "caress".
   * @return the third person grammar, for example "caresses".
   * @see #returnEmotion
   */
  public static Optional<Emotion> returnEmotionTo(String anEmotion)
  {
    if (anEmotion == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(targetEmotionStructure.get(anEmotion.toLowerCase()));
  }

}
