/*-------------------------------------------------------------------------
cvsinfo: $Header$
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
Driek van Erpstraat 9
5341 AK Oss
Nederland
Europe
maartenl@il.fontys.nl
-------------------------------------------------------------------------*/

int ParseSentence(char *name, int *room, char *parserstring);
/* Pre: 	name = name current user
			room = room current user currently occupies
			parserstring = single line string with one command in it
			the reason for making room a pointer, is because it needs to be able
			to be changed in the parser by the command "set room="
 Post:   returns 0 -> continue operation as normal
			1 -> end found
			2 -> else found
			3 -> return found
			4 -> if found, if true
			5 -> if found, if false (so skip first sequence of actions)
			6 -> showstandard found, skip everything and dump output
			7 -> show found, dump description and skip everything else
*/			

int Parse(char *name, int *room, char *parserstring);
/* Pre:	name = name current user
			room = room current user currently occupies
			parserstring = string belonging to special command downloaded from database
			the reason for making room a pointer, is because it needs to be able
			to be changed in the parser by the command "set room=", because
			room is not refreshed after an update on the database.
   Post: returns 0 if nothing happened
			returns 1 if needs to display standard bull shit and exit.
*/			

int SearchForSpecialCommand(char *name, char *password, int room);

