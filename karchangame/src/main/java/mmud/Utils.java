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
package mmud;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mmud.exceptions.MudException;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 *
 * @author maartenl
 */
public class Utils
{

    private static final Map<String, String> theAdverbStructure = new HashMap<>();
    private static final TreeMap<String, String> theEmotionStructure = new TreeMap<>();
    private static final TreeMap<String, String> theEmotion2Structure = new TreeMap<>();
    private final static String[] adverb =
    {
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
    };
    private static final String[][] emotions =
    {
        {
            "agree", "agrees"
        },
        {
            "apologize", "apologizes"
        },
        {
            "blink", "blinks"
        },
        {
            "cheer", "cheers"
        },
        {
            "chuckle", "chuckles"
        },
        {
            "cough", "coughs"
        },
        {
            "dance", "dances"
        },
        {
            "disagree", "disagrees"
        },
        {
            "flinch", "flinches"
        },
        {
            "flirt", "flirts"
        },
        {
            "frown", "frowns"
        },
        {
            "giggle", "giggles"
        },
        {
            "glare", "glares"
        },
        {
            "grimace", "grimaces"
        },
        {
            "grin", "grins"
        },
        {
            "groan", "groans"
        },
        {
            "growl", "growls"
        },
        {
            "grumble", "grumbles"
        },
        {
            "grunt", "grunts"
        },
        {
            "hmm", "hmms"
        },
        {
            "howl", "howls"
        },
        {
            "hum", "hums"
        },
        {
            "kneel", "kneels"
        },
        {
            "kneel", "kneels"
        },
        {
            "listen", "listens"
        },
        {
            "melt", "melts"
        },
        {
            "mumble", "mumbles"
        },
        {
            "mutter", "mutters"
        },
        {
            "nod", "nods"
        },
        {
            "purr", "purrs"
        },
        {
            "shrug", "shrugs"
        },
        {
            "sigh", "sighs"
        },
        {
            "smile", "smiles"
        },
        {
            "smirk", "smirks"
        },
        {
            "snarl", "snarls"
        },
        {
            "sneeze", "sneezes"
        },
        {
            "stare", "stares"
        },
        {
            "think", "thinks"
        },
        {
            "wave", "waves"
        },
        {
            "whistle", "whistles"
        },
        {
            "wink", "winks"
        },
        {
            "laugh", "laughs out loud"
        },
        {
            "wonder", "wonders"
        },
        {
            "wince", "winces"
        }
    };
    private static final String[][] emotions2 =
    {
        {
            "caress", "caresses"
        },
        {
            "comfort", "comforts"
        },
        {
            "confuse", "confuses"
        },
        {
            "congratulate", "congratulates"
        },
        {
            "cuddle", "cuddles"
        },
        {
            "fondle", "fondles"
        },
        {
            "greet", "greets"
        },
        {
            "hug", "hugs"
        },
        {
            "ignore", "ignores"
        },
        {
            "kick", "kicks"
        },
        {
            "kiss", "kisses"
        },
        {
            "knee", "knees"
        },
        {
            "lick", "licks"
        },
        {
            "like", "likes"
        },
        {
            "love", "loves"
        },
        {
            "nudge", "nudges"
        },
        {
            "pat", "pats"
        },
        {
            "pinch", "pinches"
        },
        {
            "poke", "pokes"
        },
        {
            "slap", "slaps"
        },
        {
            "smooch", "smooches"
        },
        {
            "sniff", "sniffes"
        },
        {
            "squeeze", "squeezes"
        },
        {
            "tackle", "tackles"
        },
        {
            "thank", "thanks"
        },
        {
            "tickle", "tickles"
        },
        {
            "worship", "worships"
        }
    };

    static
    {
        for (int i = 0; i < adverb.length; i++)
        {
            theAdverbStructure.put(adverb[i], adverb[i]);
        }
        for (int i = 0; i < emotions.length; i++)
        {
            theEmotionStructure.put(emotions[i][0], emotions[i][1]);
        }
        for (int i = 0; i < emotions2.length; i++)
        {
            theEmotion2Structure.put(emotions2[i][0], emotions2[i][1]);
        }
    }

    /**
     * Returns an unmodifiable map with emotions, like cheer. Can have a target, but isn't
     * necessary.
     *
     * @return returns an unmodifiable map containing emotions. They form pairs for example
     * ['agree','agrees'].
     */
    public static Map<String, String> getEmotions()
    {
        return Collections.unmodifiableMap(theEmotionStructure);
    }

    /**
     * Returns an unmodifiable map with emotions, like greet. Must have a target.
     *
     * @return returns an unmodifiable map containing emotions. They form pairs for example
     * ['congratulate','congratulates'].
     */
    public static Map<String, String> getTargetEmotions()
    {
        return Collections.unmodifiableMap(theEmotion2Structure);
    }

    /**
     * Returns a safe string, containing no javascript at all.
     *
     * @param dirtyInput the original string.
     * @return the new string, sanse javascript.
     * @throws org.owasp.validator.html.PolicyException if the dirty input
     * fails the policy
     * @throws org.owasp.validator.html.ScanException if the dirty input
     * borked parsing.
     */
    public static String security(String dirtyInput) throws PolicyException, ScanException
    {
        Policy policy = Policy.getInstance(Constants.getPolicyFile());

        AntiSamy as = new AntiSamy();

        CleanResults cr = as.scan(dirtyInput, policy);
        return cr.getCleanHTML(); // some custom function
    }

    /**
     * Returns a safe string, containing only alphabetical characters and space.
     *
     * @param value the original string.
     * @return the new string
     */
    public static String alphabeticalandspace(String value)
    {
        return value.replaceAll("[^A-Za-z ]", "");
    }

    /**
     * Returns a safe string, containing only alphabetical characters.
     *
     * @param value the original string.
     * @return the new string
     */
    public static String alphabetical(String value)
    {
        return value.replaceAll("[^A-Za-z]", "");
    }

    /**
     * Returns a safe string, containing only alphanumerical characters and
     * space.
     *
     * @param value the original string.
     * @return the new string
     */
    public static String alphanumericalandspace(String value)
    {
        return value.replaceAll("[^A-Za-z0-9 ]", "");
    }

    /**
     * Returns a safe string, containing only alphanumerical characters and
     * punctuation.
     *
     * @param value the original string.
     * @return the new string
     */
    public static String alphanumericalandpuntuation(String value)
    {
        return value.replaceAll("[^A-Za-z0-9!&()_=+;:.,?'\"\\- ]", "");
    }

    /**
     * Returns a safe string, containing only alphanumerical characters.
     *
     * @param value the original string.
     * @return the new string
     */
    public static String alphanumerical(String value)
    {
        return value.replaceAll("[^A-Za-z0-9]", "");
    }

    /**
     * Indicates the game is offline for maintenance.
     *
     * @return usually false, indicating the game is live.
     */
    public static boolean isOffline()
    {
        // TODO: put some env var here.
        return false;
    }

    public static void checkRegexp(String regexp, String value) throws MudException
    {
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(value);
        if (!m.matches())
        {
            throw new MudException("value " + value + " should match regexp " + regexp);
        }
    }

    /**
     * split up the command into different words.
     *
     * @param aCommand
     * String containing the command
     * @return String array where each String contains a word from the command.
     */
    public static String[] parseCommand(String aCommand)
    {
        return aCommand.split("( )+", 50);
    }

    /**
     * Checks to see that an adverb is valid.
     *
     * @param anAdverb
     * String containing the adverb to check, for example
     * "aimlessly".
     * @return boolean which is true if the adverb is real.
     */
    public static boolean existsAdverb(String anAdverb)
    {
        return theAdverbStructure.containsKey(anAdverb.toLowerCase());
    }

    /**
     * returns the appropriate emotion for a third person view.
     *
     * @param anEmotion
     * the emotion, for example "whistle".
     * @return the third person grammar, for example "whistles".
     * @see #returnEmotionTo
     */
    public static String returnEmotion(String anEmotion)
    {
        return theEmotionStructure.get(anEmotion);
    }

    /**
     * returns the appropriate emotion for a third person view. The difference
     * with returnEmotion is that this has a target.
     *
     * @param anEmotion
     * the emotion, for example "caress".
     * @return the third person grammar, for example "caresses".
     * @see #returnEmotion
     */
    public static String returnEmotionTo(String anEmotion)
    {
        return theEmotion2Structure.get(anEmotion);
    }

    /**
     * Changes a sentence to always start with a capital.
     *
     * @param string the string to change, may be empty or null.
     * @return a new string, containing a capital for the first
     * letter. If original is null, returns null. If original is empty string,
     * returns empty string.
     */
    public static String startWithCapital(String string)
    {
        if (string == null)
        {
            return null;
        }
        if (string.equals(""))
        {
            return "";
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
