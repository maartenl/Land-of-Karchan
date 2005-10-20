/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/MailException.java,v 1.3 2004/03/21 16:23:20 karn Exp $
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

package mmud;

/**
 * Exception thrown regarding the mail system in the mud.
 */
public class MailException extends MudException
{

	/**
	 * constructor for creating a exception with a message.
	 * @param aString the string containing the message
	 */
	public MailException(String aString)
	{
		super(aString);
	}

	/**
	 * constructor for creating a exception with a message.
	 * @param aString the string containing the message
	 * @param aThrowable the original exception.
	 */
	public MailException(String aString, Throwable aThrowable)
	{
		super(aString, aThrowable);
	}

}
