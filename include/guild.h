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
#include <time.h>
#include "mud-lib.h"

void MIFList(char *name, char *password, int room);
void MIFEntryIn(char *name, char *password, int room);
void MIFEntryOut(char *name, char *password, int room);
void MIFTalk(char *name, char *password, int room);

void RangerList(char *name, char *password, int room);
void RangerEntryIn(char *name, char *password, int room);
void RangerEntryOut(char *name, char *password, int room);
void RangerTalk(char *name, char *password, int room);

/* add SW chat line */
void SWTalk(char *name, char *password, int room);

/* add deputy chat line */
void DepTalk(char *name, char *password, int room);
