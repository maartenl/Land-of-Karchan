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
#include"userlib.h"

#define SINGULARIS 0
#define PLURALIS   1

int ReadFile(const char *filenaam);
/* Read file and dumps it onto standard output
	Pre: filename must exist
	Post: return true 
*/
      
int PrintForm(char * name, char * password);
/* Prints the standard form output */

int
Inventory_Command(char * name, char * password, int room, char *fcommand);
/* Write inventory list to cgiOut */

int PayUp(int z0, int z1, int z2, int *a, int *b, int *c);
/* Pre :
   Post : returns true if a*100+b*10+c>z0*100+z1*10+z2
   Function: withdraws z0 gold, z1 silver, z2 copper from 
   		a gold, b silver, c copper
*/

char *get_pluralis(char *s);

char *get_pluralis2(char *s);

int exit_adverb(char *s);

void WriteRoom(char * name, char * password, int room, int sleepstatus);

int CheckWeight(char * name);
/* weight = copper*1 + silver*2 + gold*3 +
            (item1.weight*item1.amount + item2.weight*item2.amount + ...)
*/
        
