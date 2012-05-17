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
package mmud.commands.communication;

/**
 *
 * @author maartenl
 */
public enum CommType
{

    SAY("say", "says"),
    SING("sing", "sings"),
    SCREAM("scream", "screams"),
    CRY("cry", "cries"),
    WHISPER("whisper", "whispers"),
    SHOUT("shout", "shouts"),
    TELL("tell", "tells"),
    ASK("ask", "asks");
    private final String theType;
    private final String thePlural;

    private CommType(String aType, String aPlural)
    {
        theType = aType;
        thePlural = aPlural;
    }

    /**
     * returns the communication type.
     *
     * @return returns "ask", "say", "whisper", "shout", "tell".
     */
    @Override
    public String toString()
    {
        return theType;
    }

    /**
     * Returns the conjugation of the verb or some such stuff.
     *
     * @return for example, say becomes "says", but cry becomes "cries".
     */
    public String getPlural()
    {
        return thePlural;
    }
}
