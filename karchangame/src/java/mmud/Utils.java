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

    // TODO : fix this to be less static, and has to make use of either
    // web-context param or env-context param/.
    private static final String POLICY_FILE_LOCATION = "/home/maartenl/Land-of-Karchan/karchangame/antisamy-myspace-1.4.4.xml";

    /**
     * Returns a safe string, containing no javascript at all.
     *
     * @param dirtyInput the original string.
     * @return the new string, sanse javascript.
     */
    public static String security(String dirtyInput) throws PolicyException, ScanException
    {
        Policy policy = Policy.getInstance(POLICY_FILE_LOCATION);

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
     * @return
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
}
