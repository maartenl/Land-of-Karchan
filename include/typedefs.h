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
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <stdarg.h>
#include <sys/file.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#include <errno.h>
#include "mysql.h"

// HTMLHeader, the place where to find things like html-files, audit trail,
// error log, etc.
// USERHeader, the place where the log files of every individual player is
// situated, contains the entire transcription of the conversation and actions
// displayed on the main browser frame.
// DATABASEHeader, the place where the original old binary files were kept.
// Besides some changes this is largely unused because of the database that is
// currently being used.
#define HTMLHeader "/karchan/mud/data/"
#define USERHeader "/karchan/mud/tmp/"
#define DATABASEHeader "/karchan/mud/data/"

// Copyright header. Will be displayed on virtually every webpage on the mud.
//
#define CopyrightHeader "&copy; Copyright Maarten van Leunen"

// Databasename, Login and password that are to be used for the correct
// access to the database in order for the mud to mutate the data in the
// correct way. Make certain that it has the appropriate rights to access
// the database.

#define DatabaseName "mud"
#define DatabaseLogin "mud"
#define DatabasePassword "42rakah"

// So, syntax is : http://lok.il.fontys.nl/~karchan/cgi-bin/enter.cgi
// become: "http://"ServerName CGIName "enter.cgi"

#define ServerName "www.karchan.org"
#define CGIName    "/cgi-bin/"

//#define ServerName "localhost.localdomain"
//#define CGIName    "/cgi-bin/"

#define ActiveUserFile      USERHeader"users.active.txt"
#define UserFile            USERHeader"users.txt"
#define UserSecFile         USERHeader"users.bak"
#define MudOffLineFile		DATABASEHeader"offline.txt"
#define ActiveItemFile      DATABASEHeader"items.active.bin"
#define ItemFile            DATABASEHeader"items.bin"
#define EventFile           DATABASEHeader"events.txt"
#define GuildListFile       DATABASEHeader"guildlists.txt"
#define BanListFile         DATABASEHeader"banlist.txt"
#define MIFLogFile          DATABASEHeader"mifguild.log"
#define RangerLogFile       DATABASEHeader"mifguild.log"
#define MailMessageFile     DATABASEHeader"mail.txt"
#define MailStructFile      DATABASEHeader"mail.bin"
#define LookMessageFile     DATABASEHeader"lookat.txt"
#define LookStructFile      DATABASEHeader"lookat.bin"
#define RoomTextFile        DATABASEHeader"room.txt"
#define RoomStructFile      DATABASEHeader"room.bin"
#define MessageBoardFile    DATABASEHeader"board.txt"
#define StdHelpFile         DATABASEHeader"help.txt"
#define DeputyBoardFile     DATABASEHeader"deputyboard.txt"
#define AuditTrailFile      DATABASEHeader"audit.trail"
#define BigFile             DATABASEHeader"bigfile"
#define ErrorFile           DATABASEHeader"error.txt"
#define JunkFile            USERHeader"junk0"

/*#define MudExe              "http://www.il.fontys.nl/usr-bin/mud"
#define EnterExe            "http://www.il.fontys.nl/usr-bin/enter"
#define NewcharExe          "http://www.il.fontys.nl/usr-bin/newchar"
#define MudreloginExe       "http://www.il.fontys.nl/usr-bin/mudrelogin"*/

#define MudExe              "http://"ServerName CGIName"mud.cgi"
#define EnterExe            "http://"ServerName CGIName"enter.cgi"
#define NewcharExe          "http://"ServerName CGIName"newchar.cgi"
#define MudreloginExe       "http://"ServerName CGIName"mudrelogin.cgi"
#define LogonframeExe       "http://"ServerName CGIName"logonframe.cgi"
#define LeftframeExe       "http://"ServerName CGIName"leftframe.cgi"

#define FirstNormalChar     25648
#define FirstNormalItem     91584
/*#define FirstNormalItem     0*/
#define DebugOptionsOn	    0

#define PerlHeader

typedef struct
{long		fileposition;
 int		size;
} indexrec;
typedef struct
{char   ip[20];
 int	days;
} banstruct;
typedef struct
{char name[20];
 char toname[20];
 char header[100];
 time_t datetime;
 indexrec positie;
} mailstruct;
typedef struct {
       int roomindex;
       int west,east,north,south,up,down;
       int light_source;
       } roomstruct;


void setFrames(int i);
/* set the Frame variable to a certain value
   0 = normal operation
   1 = operation with frames
   2 = operation with frames and server push */
   
int getFrames();
/* get the Frame currently in use
   0 = normal operation
   1 = operation with frames
   2 = operation with frames and server push */

MYSQL getdbconnection();
/* return the currently in use database connection handle */

void opendbconnection();
/* open a connection to the database DatabaseName 
   using DatabaseLogin and DatabasePassword */
   
void closedbconnection();
/* close the connection to the database */

void 
FatalError(FILE *output, int i, char *troep, char *busywith);
/* prints an error on "output" and into the error file */

void InitializeRooms();

int transfertime(time_t datetime, char *stringrepresentation);

int transfertimeback(time_t *datetime, char *stringrepresentation);

int SendSQL(char *file, char *name, char *password, char *sqlstring);
   
MYSQL_RES *SendSQL2(char *sqlstring, int *affected_rows);

roomstruct *GetRoomInfo(int room);

char *generate_password(char *fpassword);
/* Post: an empty string that may be modified, len>26
   Pre:  a string containing 24 random digits/capitals/small caps
   Returns: the pointer to fpassword
*/
