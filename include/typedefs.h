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
#include <stdio.h>
#include "mysql.h"

// HTMLHeader, the place where to find things like html-files, audit trail,
// error log, etc.
// USERHeader, the place where the log files of every individual player is
// situated, contains the entire transcription of the conversation and actions
// displayed on the main browser frame.
// DATABASEHeader, the place where the original old binary files were kept.
// Besides some changes this is largely unused because of the database that is
// currently being used.
#define MM_HTMLHEADER 1
#define MM_USERHEADER 2
#define MM_DATABASEHEADER 3

// Copyright header. Will be displayed on virtually every webpage on the mud.
//
#define MM_COPYRIGHTHEADER 4

// Databasename, Login and password that are to be used for the correct
// access to the database in order for the mud to mutate the data in the
// correct way. Make certain that it has the appropriate rights to access
// the database.

#define MM_DATABASENAME 5
#define MM_DATABASELOGIN 6
#define MM_DATABASEPASSWORD 7
#define MM_DATABASEHOST 8
#define MM_DATABASEPORT 9

// So, syntax is : http://lok.il.fontys.nl/~karchan/cgi-bin/enter.cgi
// become: "http://"ServerName CGIName "enter.cgi"

#define MM_SERVERNAME 10
#define MM_CGINAME    11

#define MM_MUDOFFLINEFILE		12
#define MM_STDHELPFILE        13
#define MM_AUDITTRAILFILE     14
#define MM_BIGFILE            15
#define MM_ERRORFILE          16

#define MM_MUDCGI             17
#define MM_ENTERCGI           18
#define MM_NEWCHARCGI         19
#define MM_LOGONFRAMECGI      20
#define MM_LEFTFRAMECGI       21
#define MM_HOST 22
#define MM_PORT 23

void initParam();
void freeParam();
const char *getParam(int i);
void setParam(int i, char *parameter);

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

/*! struct used for internal purposes, it keeps track of the information
	regarding the mud (username, password, command) sent during a connection */
typedef struct
{
	char *readbuf; // buffer to read from socket, standard=1024 bytes
	int bufsize; // buffer size, initialised to 1024
	char name[22];
	char password[42];
	char address[42];
	char cookie[80];
	int frames;
	char *action; // {mud, logon, new}
	char *command;
	int socketfd; // socket descriptor
	void *newchar; // usually NULL, except when action=newchar
	void *next; // next item in the list
} mudpersonstruct;

int is_list_empty();

mudpersonstruct *get_first_from_list();

int add_to_list(int socketfd);

mudpersonstruct *find_in_list(int socketfd);

int remove_from_list(int socketfd);

/* sets the FileDescriptor used to output any information to the user 
 Function usually called at the start of the mudMain call.
*/
void setMMudOut(int aFileDescriptor);

/* gets the FileDescriptor used to output any information to the user
 Function usually called ALL the time for any possible output
*/
int getMMudOut();

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

int send_socket(int s, char *buf, int *len);

int send_printf(const int socketfd, char *fmt,...);

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

char *getCommandString();

void setCommandString(char *fstring);

/* set number of available tokens. Usually only called once in gameMain
*/
void setTokenAmount(int amount);

/* retrieve number of available tokens
*/
int getTokenAmount();

/* set the token array, usually only called once in gameMaine
*/
void setTokens(char **ftokens);

/* get the index of the token matching the description
	returns -1 if not found
*/
int getTokenIndex(char *ftoken);
   
/* returns the i-th token, if i is beyond the number of available tokens, returns empty constant string
*/
char *getToken(int i);

void 
FatalError(FILE *output, int i, char *description, char *busywith);
/* prints an error on "output" and into the error file */

void InitializeRooms();

int transfertime(time_t datetime, char *stringrepresentation);

int transfertimeback(time_t *datetime, char *stringrepresentation);

int SendSQL(char *file, char *name, char *password, char *sqlstring);
   
MYSQL_RES *SendSQL2(char *sqlstring, int *affected_rows);

char *composeSqlStatement(char *sqlstring, ...);

MYSQL_RES *executeQuery(int *affected_rows, char *sqlstring, ...);

roomstruct *GetRoomInfo(int room);

char *generate_password(char *fpassword);
/* Post: an empty string that may be modified, len>26
   Pre:  a string containing 24 random digits/capitals/small caps
   Returns: the pointer to fpassword
*/

void writeConfig();
int readConfigFiles(char *filename);
