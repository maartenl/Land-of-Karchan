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

//#define ServerName "www.karchan.org"
#define CGIName    "/cgi-bin/"

#define ServerName "zeus"
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

#define MudExe              "http://"ServerName CGIName"mud2.cgi"
#define EnterExe            "http://"ServerName CGIName"enter2.cgi"
#define NewcharExe          "http://"ServerName CGIName"newchar2.cgi"
#define MudreloginExe       "http://"ServerName CGIName"mudrelogin2.cgi"
#define LogonframeExe       "http://"ServerName CGIName"logonframe2.cgi"
#define LeftframeExe       "http://"ServerName CGIName"leftframe2.cgi"

typedef struct 
{
	int roomindex;
	int west,east,north,south,up,down;
	int light_source;
} roomstruct;

/* this structure is used to provide valuable information about the current status of both the socket as well as the daemon
*/
typedef struct
{
	char hostname[256];
	char hostip[256];
	char domainname[256];
	char protversion[10];
	char mmudversion[10];
	char mmudtime[20];
	char mmuddate[20];
	time_t mmudstartuptime;
	int number_of_connections;
	int number_of_timeouts;
	int number_of_current_connections;
	int maxnumber_of_current_connections;
} mudinfostruct;

/* sets the FileDescriptor used to output any information to the user 
   Function usually called at the start of the mudMain call.
*/
void setMMudOut(FILE *aFileDescriptor);

/* gets the FileDescriptor used to output any information to the user
   Function usually called ALL the time for any possible output
*/
FILE *getMMudOut();

/* returns 0, if not to be shut down
   returns 1, if to be shut down
   returns >1, countdown
*/
int isShuttingdown();

/* uses an offset, offset is added to shutdown count
   use setShuttingDown(-1) for countdown
*/
void setShutdown(int aOffset);

/* set the Frame variable to a certain value
   0 = normal operation
   1 = operation with frames
   2 = operation with frames and server push */
void setFrames(int i);
   
/* get the Frame currently in use
   0 = normal operation
   1 = operation with frames
   2 = operation with frames and server push */
int getFrames();

/* return the currently in use database connection handle */
MYSQL getdbconnection();

/* open a connection to the database DatabaseName 
   using DatabaseLogin and DatabasePassword */
void opendbconnection();
   
/* close the connection to the database */
void closedbconnection();

/* return the structure that contains all the mud information (either for mutating or for displaying) */
mudinfostruct getMudInfo();

/* set the structure to something else. The way these methods work is usually, you get the structure, you change
it and you put it back. */
void setMudInfo(mudinfostruct amudinfostruct);

void 
FatalError(FILE *output, int i, char *description, char *busywith);
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

int getCookie(char *name, char *value);
/* Post: name = a string describing the name of the cookie
			value = a string that shall contain the value if the cookie exists
	Pre:  returnvalue true upon success, otherwise false
			if returnvalue true then 'value' contains the value 
			of the cookie with name 'name'
*/
