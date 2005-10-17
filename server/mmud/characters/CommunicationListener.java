/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/characters/CommunicationListener.java,v 1.1 2004/10/02 23:45:02 karn Exp $
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

import java.util.logging.Logger;

/**
 * Event that is triggered when either a say/tell/ask/whisper/shout
 * command is executed. It is triggered usually on the person spoken
 * to.
 */
public interface CommunicationListener
{

	public static final CommType SAY = CommType.SAY;
	public static final CommType WHISPER = CommType.WHISPER;
	public static final CommType SHOUT = CommType.SHOUT;
	public static final CommType TELL = CommType.TELL;
	public static final CommType ASK = CommType.ASK;

	public final class CommType
	{
		private String theType;

		public static final CommType SAY = new CommType("say");
		public static final CommType WHISPER = new CommType("whisper");
		public static final CommType SHOUT = new CommType("shout");
		public static final CommType TELL = new CommType("tell");
		public static final CommType ASK = new CommType("ask");

		private CommType(String aType)
		{
			theType = aType;
		}

		/**
		 * returns the communication type.
		 * @return returns "ask", "say", "whisper", "shout", "tell".
		 */
		public String toString()
		{
			return theType;
		}


	}


    /**
     * An event method, triggered when someone issues a communication
     * command towards this person. Enables this person to react
	 * appropriately
     * to the command. This is usually reserved for bots.  
     * @param aSentence the string that is being communicated.
	 * @param aType the type of communication. Cannot be null.
	 * @param aPerson the person that issued the communication command.
	 * @see CommType
     */
	public void commEvent(Person aPerson, CommType aType, String aSentence);

}
