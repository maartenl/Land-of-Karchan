/*-------------------------------------------------------------------------
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
#include "typedefs.h"

void Error(int i, char *troep);
/* Pre: i contains valid error values
   Post:ErrorFile(new) = ErrorFile(old) + Error(i)
*/

char *lowercase(char dest[512], char *buf);
/* Pre: strlen(buf)<512
   Post:dest=buffer without capitals
*/   

void Wait(int sec, int usec);
/* Pre: sec = seconds to wait ^
        usec = useconds to wait
   Post: Operation suspended for sec seconds and usec microseconds
*/

int ActivateUser(char *name);
/* Pre: x = valid character ^ x = not active
   Post:ActiveUserFile(new) = ActiveUserFile(old)+x ^
        UserFile(new) = UserFile(old) + x made active ^
        (Ei : i elem of Collection(files with extentoin ".log") : i=x.username+".log") 
*/        

int RemoveUser(char *name);
/*Pre: x exists ^ x elem of ActiveUserFile
  Post:ActiveUserFile(new) = ActiveUserFile(old) + x.room=0 + x.name="" ^
       UserFile(new) = UserFile(old) + x.activeuserpos = -1 ^
       !exists x.name+".log" 
/*      

int ExistUser(char *name);
/*Pre: x = valid ^ x exists
  Post:x active => return pos x in ActiveUserFile ^ x=x in ActiveUserFile
       x !active=> return 0 
*/  

int ExistUserRoom(int roomnr, char *name);
/*Pre: x = geldig ^ x exists
  Post:x active => return pos x in ActiveUserFile ^ x=x in ActiveUserFile ^ x in room roomnr
       x !active=> return 0 
*/  

int SearchUser(char *name);
/*Pre: x = geldig
  Post:x exists => return pos x in UserFile ^
       x !exists=> return 0
*/
       
void ReadEvents();

void CleanEvents();

void WriteEvents();

void ReadGuildLists();
/*Pre: -
  Post: reads three lists (MIF, Knights, Rangers) in three arrays
*/
  
int SearchGuildMember(int item);
/*Pre: item = valid
  Post: return Knights = 1 + MIF = 2 + Rangers = 4
*/

void WriteGuildLists();
/*Pre: -
  Post: writes three lists (MIF, Knights, Rangers) from three arrays in file
*/

int
SearchBanList(char *item, char *username);
/*Pre: -
  Post: returns true is person is banned from list
*/

char *ItemDescription(char *name);

void ClearLogFile(char *filenaam);

void WriteSentenceIntoOwnLogFile(char *string, char *filenaam);

void WriteSentenceIntoOwnLogFile2(char *filenaam, char *fmt, ...);

void WriteMessage(char *to, char *name, int roomnr);

void WriteMessage2(char *name, int roomnr, char *fmt, ...);

int WriteMessageTo(char *to, char *second, char *toname);

int WriteMessageTo2(char *toname, char *name, int roomnr, char *fmt, ...);

int WriteSayTo(char *toname, char *name, int roomnr, char *fmt, ...);

int WriteLinkTo(char *toname, char *name, char *fmt, ...);

void SayToAll(char *to);

char *HeShe(char *kill);
/* returns "He" or "She" */

char *HeSheSmall(char *kill);
/* returns "he" or "she" */

char *HeShe2(char *kill);
/* returns "him" or "her" */

char *HeShe3(char *kill);
/* returns "his" or "her" */

char *ShowString(int i, int maxi);

char *ShowMovement(int i, int maxi);

char *ShowHittingStuff(int i, char *race);

char *ShowDrink(int i);

char *ShowEat(int i);

char *ShowBurden(int i);
/* weight 0..3000 */

int computeEncumberance(int fweight, int fstrength);
/* weight 0..3000, encumberance 0..50
   -1 = too much to carry, you are totally bogged down*/

char *ShowAlignment(int i);

char *HitMe(int i);

char *ErgHard(int i);
