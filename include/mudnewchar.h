/*-------------------------------------------------------------------------
cvsinfo: $Header$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/

/* this structure is used only to provide information regarding a <I>new</I>
character
*/
typedef struct
{
	char frace[10];
	char fsex[10];
	char fage[15];
	char flength[16];
	char fwidth[16];
	char fcomplexion[16];
	char feyes[16];
	char fface[19];
	char fhair[19];
	char fbeard[19];
	char farm[16];
	char fleg[16];
	char *ftitle;
	char *frealname;
	char *femail;
} mudnewcharstruct;

mudnewcharstruct *create_mudnewcharstruct();

int gameNewchar(char *name, char *password, char *cookie, char *address, mudnewcharstruct *infostruct);

