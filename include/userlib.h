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

void Error(int i, char *description);

char *lowercase(char dest[512], char *buf);

void Wait(int sec, int usec);

int ActivateUser(char *name);

int RemoveUser(char *name);

int ExistUser(char *name);

int ExistUserRoom(int roomnr, char *name);

char *
ExistUserByDescription(int beginning, int amount, int room, char **returndesc);

int SearchUser(char *name);
       
int
SearchBanList(char *item, char *username);

char *ItemDescription(char *name);

void ClearLogFile(char *filenaam);

void WriteSentenceIntoOwnLogFile(const char *filenaam, char *fmt, ...);

void WriteMessage(char *name, int roomnr, char *fmt, ...);

int WriteMessageTo(char *toname, char *name, int roomnr, char *fmt, ...);

int WriteSayTo(char *toname, char *name, int roomnr, char *fmt, ...);

int WriteLinkTo(char *toname, char *name, char *fmt, ...);

void SayToAll(char *to);

char *HeShe(char *kill);

char *HeSheSmall(char *kill);

char *HeShe2(char *kill);

char *HeShe3(char *kill);

char *ShowString(int i, int maxi);

char *ShowMovement(int i, int maxi);

char *ShowHittingStuff(int i, char *race);

char *ShowDrink(int i);

char *ShowEat(int i);

char *ShowBurden(int i);

int computeEncumberance(int fweight, int fstrength);

char *ShowAlignment(int i);

char *HitMe(int i);

char *ErgHard(int i);
