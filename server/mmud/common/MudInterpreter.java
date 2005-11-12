/*-------------------------------------------------------------------------
svninfo: $Id: MudException.java 987 2005-10-20 19:26:50Z maartenl $
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

package mmud.common;

import mmud.characters.Persons;
import mmud.rooms.Rooms;
import simkin.Interpreter;

/**
 * The interpreter used for executing user defined scripts in the mud.
 * This is derived from the simkin interpreter so we can
 * implement some standard global variables.<P>
 * The variables in question are:
 * <UL><LI>rooms : contains all rooms
 * <LI>persons : contains all <I>active</I> persons
 * </UL>
 */ 
public class MudInterpreter extends Interpreter
{

	/**
	 * constructor for creating a interpreter.
	 */
	public MudInterpreter()
	{
		super();
		addGlobalVariable("rooms", Rooms.create());
		addGlobalVariable("persons", Persons.create());
	}

}
