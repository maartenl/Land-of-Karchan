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
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/

#define SINGULARIS 0
#define PLURALIS   1

int ReadFile(const char *filenaam, int socketfd);
      
int PrintForm(char * name, char * password, int frames, int socketfd);

int Inventory_Command(mudpersonstruct *fmudstruct);

int PayUp(int z0, int z1, int z2, int *a, int *b, int *c);

char *get_pluralis(char *s);

char *get_pluralis2(char *s);

int exist_adverb(char *s);

void WriteRoom(mudpersonstruct *fmudstruct);

int CheckWeight(char * name);
        
