/*-------------------------------------------------------------------------
svninfo: $Id$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/

package mmud.characters;

import mmud.MudException;

/**
 * Event that is triggered when either a say/tell/ask/whisper/shout command is
 * executed. It is triggered usually on the person spoken to.
 */
public interface CommunicationListener
{

	public static final CommType SAY = CommType.SAY;
	public static final CommType WHISPER = CommType.WHISPER;
	public static final CommType SHOUT = CommType.SHOUT;
	public static final CommType TELL = CommType.TELL;
	public static final CommType ASK = CommType.ASK;
	public static final CommType SING = CommType.SING;
	public static final CommType SCREAM = CommType.SCREAM;
	public static final CommType CRY = CommType.CRY;

	public final class CommType
	{
		private final String theType;
		private final String thePlural;

		public static final CommType SAY = new CommType("say", "says");
		public static final CommType SING = new CommType("sing", "sings");
		public static final CommType SCREAM = new CommType("scream", "screams");
		public static final CommType CRY = new CommType("cry", "cries");
		public static final CommType WHISPER = new CommType("whisper",
				"whispers");
		public static final CommType SHOUT = new CommType("shout", "shouts");
		public static final CommType TELL = new CommType("tell", "tells");
		public static final CommType ASK = new CommType("ask", "asks");

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

	/**
	 * An event method, triggered when someone issues a communication command
	 * towards this person. Enables this person to react appropriately to the
	 * command. This is usually reserved for bots.
	 * 
	 * @param aSentence
	 *            the string that is being communicated.
	 * @param aType
	 *            the type of communication. Cannot be null.
	 * @param aPerson
	 *            the person that issued the communication command.
	 * @throws MudException
	 * @see CommType
	 */
	public void commEvent(Person aPerson, CommType aType, String aSentence)
			throws MudException;

}
