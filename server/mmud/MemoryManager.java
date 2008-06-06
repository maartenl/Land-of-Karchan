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

package mmud;

/**
 * The class that can be used to provide
 * information regarding memory use of the
 * java virtual machine.
 */ 
public class MemoryManager
{
	/**
	 * The memory manager that can be used to get a good
	 * grip on how much memory we have left, etc.
	 */
	public static final MemoryManager MEMORY_MANAGER = new MemoryManager();
	
	private Runtime itsRuntime;
	
	/**
	 * constructor for creating the MemoryManager.
	 * Only one memory manager is allowed to exist
	 * during the lifetime of the program.
	 */
	private MemoryManager()
	{
		itsRuntime = Runtime.getRuntime();
	}

	/**
	 * toString returns a html-formatted string with the
	 * current memory status.
	 */
	public String toString()
	{
		StringBuffer myBuffer = new StringBuffer();
		long used = itsRuntime.totalMemory() - itsRuntime.freeMemory();
		myBuffer.append("Memory Management<HR>\nAvailable Processors: ");
		myBuffer.append(itsRuntime.availableProcessors() + "");
		myBuffer.append("<BR>\nused Memory: ");
		myBuffer.append(used + " (");
		myBuffer.append((used * 100 / itsRuntime.totalMemory()) + "%)");
		myBuffer.append("<BR>\nfree Memory: ");
		myBuffer.append(itsRuntime.freeMemory() + "");
		myBuffer.append("<BR>\ntotal Memory: ");
		myBuffer.append(itsRuntime.totalMemory() + "");
		myBuffer.append("<BR>\nmax Memory: ");
		myBuffer.append(itsRuntime.maxMemory() + "<P>");
		
		return myBuffer.toString();
	}

}
