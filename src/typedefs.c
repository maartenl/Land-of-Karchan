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

#include <sys/types.h>
#include <sys/socket.h>

// include files for the xml library calls
#include <libxml/xmlmemory.h>
#include <libxml/parser.h>
#include <libxml/tree.h>

#include "typedefs.h"
#include "mudnewchar.h"

/*! \file definition file with constants and essential operations
like database operations, server statistics, configuration and tokenization
*/

/*roomindex, west, east, north, south, up, down, light_source*/
roomstruct room;

int frames;
char secretpassword[40];
/*! property providing where ANY user output of the program should be redirected to. */
int mmout;

/*! set/redirect output from a mud command into a filedescriptor */
void setMMudOut(int aFileDescriptor)
{
	mmout = aFileDescriptor;
}

/*! get the redirected output/filedescriptor. This is usually used in conjunction with an fprintf statement
like fprintf(getMMudOut(), "Message from Karn\n");
*/
int getMMudOut()
{
	return mmout;
}

/*! attempts to send data over a socket, if not all information is sent.
will automatically attempt to send the rest.
\param s int socket descriptor
\param buf char* message
\param len int* length of message, should be equal to strlen(message) both at the beginning as well as after
\return return -1 on failure, 0 on success
*/
int
send_socket(int s, char *buf, int *len)
{
	int total = 0;	// how many btytes we've sent
	int bytesleft = *len;	// how many we have left to send
	int n;
#if DEBUG==2
	printf("[message]: %s\n", buf);
#endif
	while (total < *len)
	{
		n = send(s, buf+total, bytesleft, 0);
		if (n == -1)
		{
			break;
		}
		total += n;
		bytesleft -= n;
	}
	*len = total;	// return number actually sent here
	
	return (n == -1 ? -1 : 0);	// return -1 on failure, 0 on success
}

MYSQL dbconnection;

//! set the mud interface
/*! this sets the mud interface used.
\param i int, can have the following values:
<UL><LI>0 -> normal interface, one page
<LI>1 -> frames
<LI>2 -> frames with server push
<LI>3 -> future use
</UL>
*/
void setFrames(int i)
{
	frames=i;
}

//! gets the current mud interface
/*! this gets the current interface used.
\see setFrames
*/
int getFrames()
{
	return frames;
}

//! returns dbconnection variable
/*! this returns the dbconnection as setup by opendbconnection and used by
	closedbconnection
	\return dbconnection variable of type MYSQL
	\sa opendbconnection, closedbconnection
*/
MYSQL getdbconnection()
{
	return dbconnection;
}

mudinfostruct mudinfo;

//! retrieve information about the mud (statistics)
mudinfostruct 
getMudInfo()
{
	return mudinfo;
}

//! set information about mud (statistics), called from mmserver.c
void 
setMudInfo(mudinfostruct amudinfostruct)
{
	mudinfo = amudinfostruct;
}

int game_shutdown = 0;

//! game should be shut down, shut down initiated
int isShuttingdown()
{
	return game_shutdown;
}

//! indicate that the game should be shutting down as soon as possible
void setShutdown(int aOffset)
{
	game_shutdown += aOffset;
	if (game_shutdown < 0) 
	{			
		game_shutdown = 0;
	}
}

char *parameters[24];

/*! clear out the entire parameterlist
	This should be called at the beginning of each program
	if you do not want nasty core dumps and segmentation faults. 
*/
void initParam()
{
	int i;
	for (i = 0; i < 24; i++)
	{
		parameters[i] = NULL;
	}
}

/*! clear out the entire parameterlist
	This should be called at the end of each program
	It cleanly frees all memory requested by previous calls by setParam().
*/
void freeParam()
{
	int i;
	for (i = 0; i < 24; i++)
	{
		free(parameters[i]);
		parameters[i] = NULL;
	}
}

/*! retrieve a parameter from the parameter list
	\param i int constant describing which parameter to retrieve, valid range 1..23
	\return char* parameter contents
*/
const char *getParam(int i)
{
	if ((i<0) || (i>23))
	{
		return NULL;
	}
	if (parameters[i] == NULL)
	{
		printf("Problem! Parameter %i is NULL.\n", i);
		abort();
	}
	return parameters[i];
}

/*! set a parameter
	\param i int constant describing which parameter to set, valid range 1..23
	\param parameter char* string to set the constant to, the string is copied */
void setParam(int i, char *parameter)
{
	if (parameters[i] != NULL) 
	{
		free(parameters[i]);
	}
	if (parameter != NULL)
	{
		parameters[i] = strdup(parameter);
	}
}

char **tokens;
int  tokenamount;

/*! set number of available tokens. Usually only called once in gameMain
*/
void
setTokenAmount(int amount)
{
	tokenamount = amount;
}

/*! 
	retrieve number of available tokens
*/
int
getTokenAmount()
{
	return tokenamount;
}

/*! set the token array, usually only called once in gameMaine
*/
void
setTokens(char **ftokens)
{
	tokens = ftokens;
}

/*! get the index of the token matching the description
	returns -1 if not found
*/
int
getTokenIndex(char *ftoken)
{
	int i;
	for (i=0; i<tokenamount; i++)
	{
		if (!strcasecmp(ftoken, tokens[i]))
		{
			return i;
		}
	}
	return -1;
}

/*! returns the i-th token, if i is beyond the number of available tokens, returns empty constant string
*/
char *
getToken(int i)
{
	if ((i >= tokenamount) && (i <  0))
	{
		return "";
	}
	return tokens[i];
}

/*! linked list of mudpersonstruct, keeps a list of established
    connections/personal mud information */
mudpersonstruct *list = NULL;

//! check to see if list is empty
/*! check to see if the list of mud connections is empty or not
 \return int 0 upon non-empty list, 1 if list is empty
*/
int
is_list_empty()
{
	return (list == NULL);
}

//! retrieve the first mud connection of the list. 
/*! retrieve the first mud connection of the list. This is primarily used to clear the entire list without 
knowing which socketfds are still open.
\return mudpersonstruct* the first in the list, returns NULL if the list is empty
*/
mudpersonstruct *
get_first_from_list()
{
	return list;
}

/*! add a new mudpersonstruct with default values to the beginning of the list
 (i.e. the first member of the list is the newly added socketfd) 
 \param socketfd int socket descriptor to be added to list of established connections and corresponding mud info
 \return int always returns 1
 */
int
add_to_list(int socketfd)
{
	mudpersonstruct *mine;
	mine = (mudpersonstruct *) malloc(sizeof(mudpersonstruct));
	mine->name[0] = 0;
	mine->password[0] = 0;
	mine->address[0] = 0;
	mine->cookie[0] = 0;
	mine->frames = 0;
	mine->action = NULL;
	mine->command = NULL;
	mine->bufsize = 1;
	mine->readbuf = (char *) malloc(mine->bufsize);
	mine->readbuf[0] = 0; // initialized to empty string
	mine->socketfd = socketfd;
	mine->newchar = NULL;
	mine->next = list;
	list = mine;
	return 1;
}

/*!
 return the mudpersonstruct attached to the socketfd. If not found, return NULL 
 \param socketfd int socket descriptor used in search
 \return mudpersonstruct* mudpersonstruct found in linked list, returns NULL if not found
 */
mudpersonstruct
*find_in_list(int socketfd)
{
	mudpersonstruct *mine;
	mine = list;
	while (mine != NULL)
	{
		if (mine->socketfd = socketfd)
		{
			return mine;
		}
		mine = (mudpersonstruct *) mine->next;
	}
	return NULL;
}

/*! remove the mudpersonstruct from the list based on socketfd. The 'command' char pointer member
 is freed, then the mudpersonstruct is freed and the list is updated. 
 \param socketfd int socket descriptor
 \return 1 upon success, 0 if descriptor not found
 */
int
remove_from_list(int socketfd)
{
	mudpersonstruct *mine, *mine2;
	mine = list;
	mine2 = NULL;
	while (mine != NULL)
	{
		if (mine->socketfd == socketfd)
		{
			if (mine2 == NULL)
			{
				list = (mudpersonstruct *) mine->next;
			}
			else
			{
				mine2->next = mine->next;
			}
			if (mine->command != NULL) 
			{
				free(mine->command);
				mine->command = NULL;
			}
			if (mine->action != NULL) 
			{
				free(mine->action);
				mine->action = NULL;
			}
			if (mine->newchar != NULL) 
			{
				mudnewcharstruct *temp = (mudnewcharstruct *) mine->newchar;
				if (temp->ftitle != NULL)
				{
					free(temp->ftitle);
				}
				if (temp->frealname != NULL)
				{
					free(temp->frealname);
				}
				if (temp->femail != NULL)
				{
					free(temp->femail);
				}
				free(temp);
				mine->newchar = NULL;
			}
			free(mine->readbuf);
			mine->readbuf = NULL;
			free(mine);
			mine = NULL;
			return 1;
		}
		mine2 = mine;
		mine = (mudpersonstruct *) mine->next;
	}
	return 0;
}

//! print error to screen and exit
void 
FatalError(FILE *output, int i, char *description, char *busywith)
{         
  FILE           *fp;

	fprintf(output, "<HTML>\n");
	fprintf(output, "<HEAD>\n");
	fprintf(output, "<TITLE>\n");
	fprintf(output, "Land of Karchan - Fatal Error\n");
	fprintf(output, "</TITLE>\n");
	fprintf(output, "</HEAD>\n");
	fprintf(output, "<BODY BGCOLOR=#FFFFFF>\n");
	fprintf(output, "<H1>Fatal Error %i : %s</H1>\r\n", i, description);
	fprintf(output, "An fatal error was received while %s. Please mail the "
	"error you received to <A HREF=\"mailto:karn@karchan.org\">karn@karchan.org</A>.<P>\r\n", busywith);
	fprintf(output, "</BODY></HTML>\r\n");

  fp = fopen(getParam(MM_ERRORFILE), "a");
  fprintf(fp, "fatal error %i: %s, %s\n", i, description, busywith);
  fclose(fp);
  exit(0);
}         

/*! initialize rooms, basically retrieves information about one current room */
void InitializeRooms(int roomint)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;

	temp = composeSqlStatement("select west, east, north, south, up, down from rooms where id=%i", roomint);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;

	row = mysql_fetch_row(res);

	room.west=atoi(row[0]);
	room.east=atoi(row[1]);
	room.north=atoi(row[2]);
	room.south=atoi(row[3]);
	room.up=atoi(row[4]);
	room.down=atoi(row[5]);

	mysql_free_result(res);
}

//! write error to file
void exiterr(int exitcode, char *sqlstring, MYSQL *mysql)
{
FILE *fp;
fp = fopen(getParam(MM_ERRORFILE), "a");
fprintf( fp, "Error %i: %s\n {%s}\n", exitcode, mysql_error(mysql), sqlstring );
fclose(fp);
}

//!write error to file
void exiterr2(int exitcode, char *sqlstring, MYSQL *mysql, char *file)
{
FILE *fp;

fp = fopen(file, "a");
fprintf( fp, "<FONT COLOR=red><I>Error</I> %i: %s\n {%s}</FONT><BR>\r\n", exitcode, mysql_error(mysql), sqlstring );
fclose(fp);
}


//! send a message to a socket
/*! can contain a formatted string and extra parameters
\see printf
*/
int
send_printf(const int socketfd, char *fmt,...)
{
	/* Guess we need no more than 255 bytes. */
	int n, size = 255, sentbytes, resulting;
	char *p;
	va_list ap;
	if ((p = malloc (size)) == NULL)
	{
		return 0;
	}
	while (1) 
	{
		/* Try to print in the allocated space. */
		va_start(ap, fmt);
		n = vsnprintf (p, size, fmt, ap);
		va_end(ap);
		/* If that worked, return the string. */
		if (n > -1 && n < size)
		{
			break;
		}
		/* Else try again with more space. */
		if (n > 1) /* glibc 2.1 */
			size = n+1; /* precisely what is needed */
		else /* glibc 2.0 */
			size *= 2;/* twice the old size */
		if ((p = realloc (p, size)) == NULL)
		{
			return 0;
		}
	}
	sentbytes = strlen(p);
	resulting = send_socket(socketfd, p, &sentbytes);
	resulting = resulting & (sentbytes == strlen(p));
	free(p);
	return resulting;
}

//! send sql query to database, deprecated!
int SendSQL(char *file, char *name, char *password, char *sqlstring)
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
	FILE *fp;
	uint i = 0;
 
	if (!(mysql_connect(&mysql,"localhost",name,password))) 
	{
		exiterr(1, sqlstring, &mysql);
		exiterr2(1, sqlstring, &mysql, file);
	}
 
	if (mysql_select_db(&mysql,getParam(MM_DATABASENAME)))
	{
		exiterr(2, sqlstring, &mysql);
		exiterr2(2, sqlstring, &mysql, file);
	}
 
	if (mysql_query(&mysql,sqlstring))
	{
		exiterr(3, sqlstring, &mysql);
		exiterr2(3, sqlstring, &mysql, file);
	}
 
	if (!(res = mysql_store_result(&mysql)))
		{
		exiterr(4, sqlstring, &mysql);
		exiterr2(4, "Unable to retrieve rows, probably insert of update"
		" query...", &mysql, file);
		exiterr2(4, sqlstring, &mysql, file);
		} else {
 
		fp=fopen(file, "a");
		fprintf(fp, "<HR><CENTER><TABLE BORDER=1>\r\n");
		while((row = mysql_fetch_row(res))) {
			fprintf(fp, "<TR>");
			 for (i=0 ; i < mysql_num_fields(res); i++) 
				fprintf(fp, "<TD>%s</TD>\r\n",row[i]);
			fprintf(fp, "</TR>\r\n");
		}
		fprintf(fp, "</TABLE></CENTER><HR><BR>\r\n");
		fclose(fp);
	 
	if (!mysql_eof(res))
	{
		exiterr(5, sqlstring, &mysql);
		exiterr2(5, sqlstring, &mysql, file);
	}
 
	mysql_free_result(res);}
	mysql_close(&mysql);
}

//! open the connection to the database server
/*! uses the constants as defined in typedefs.h to connect, needs to be changed.
	\sa closedbconnection
*/
void
opendbconnection()
{
	mysql_init(&dbconnection);
	
	if (!(mysql_real_connect(&dbconnection, 
		getParam(MM_DATABASEHOST), 
		getParam(MM_DATABASELOGIN),
		getParam(MM_DATABASEPASSWORD), 
		getParam(MM_DATABASENAME), atoi(getParam(MM_DATABASEPORT)), NULL, 0)))
	{
		exiterr(1, "error establishing connection with mysql", &dbconnection);
	} 
}

//! close the connection to the database server
/*! 
	\sa opendbconnection
*/
void
closedbconnection()
{
	mysql_close(&dbconnection);
}

//! send query to database, if necessary retrieve rows
/*! might be a little deprecated, use executeQuery instead
\param sqlstring the string containing the query
	\param affected_rows the pointer to integer returning the number of rows affected/retrieved
	\return the resultset, please do not forget to call mysql_free_result!
	\sa composeSqlStatement, getdbconnection, closedbconnection
*/
MYSQL_RES *SendSQL2(char *sqlstring, int *affected_rows)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	uint i = 0;
 
	if (mysql_query(&dbconnection,sqlstring))
		exiterr(3, sqlstring, &dbconnection);
 
	if (!(res = mysql_store_result(&dbconnection)) && mysql_field_count(&dbconnection))
		exiterr(4, sqlstring, &dbconnection);
 	
	if (affected_rows!=NULL)
	{
		*affected_rows = mysql_affected_rows(&dbconnection);
	}

	return res;
}

//! create an sql statement
/*! create an sql statement in the printf-way, but with the following options:
	<UL><LI>\%s - normal string
	<LI>\%x - special string, interpreted for use as string literal in the query
	(so special characters are escaped properly)
	<LI>\%i - integer
	</UL>
	If you need a % sign, literally, try '%\i' for instance.
	Example: composeSqlStatement("select * from tmp_usertable where name='\%x', name);
	\param sqlstring the format string (with options as displayed above)
	\param ... the unknown list of parameters to be used
	\return a char pointer to the composed string (memory has been allocated, do not forget to use free())
	\sa SendSQL2
*/
char *
composeSqlStatement(char *sqlstring, ...)
{
	char *my_sqlstring, *s, *special_s;
	int my_size, my_length, d, i;
	va_list ap;
	char temp[20];

	my_size = strlen(sqlstring);
	my_sqlstring = (char *) malloc(my_size+1);
	*my_sqlstring = 0;
	my_length = 0;
	va_start(ap, sqlstring);
	while (*sqlstring)
	{
		if (*sqlstring == '%')
		{
			switch(sqlstring[1]) 
			{
				case 's': /* common_string */
					s = va_arg(ap, char *);
					if (s == NULL) {s = "";}
					my_size += strlen(s);
					my_length += strlen(s);
					my_sqlstring = realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, s);
					sqlstring++;
					break;
				case 'x': /* mysql_string */
					s = va_arg(ap, char *);
					if (s == NULL) {s = "";}
					special_s = (char *) malloc(strlen(s)*2+1);
					i = mysql_real_escape_string(&dbconnection, special_s, s, strlen(s));
					my_size += i;
					my_length += i;
					my_sqlstring = realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, special_s);
					free(special_s);special_s=NULL;
					sqlstring++;
					break;
				case 'i': /* int */
					d = va_arg(ap, int);
					sprintf(temp, "%i", d);
					s = temp;
					my_size += strlen(s);
					my_length += strlen(s);
					my_sqlstring = realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, s);
					sqlstring++;
					break;
				default :
					my_sqlstring[my_length] = *sqlstring;
					my_length++;
					my_sqlstring[my_length] = 0;
				break;
			}
		}
		else
		{
			my_sqlstring[my_length] = *sqlstring;
			my_length++;
			my_sqlstring[my_length] = 0;
		}
		sqlstring++;
	}
	va_end(ap);

	return my_sqlstring;
}

//! execute a composed sql statement
/*! create an sql statement in the printf-way, with the following options and execute it.
	<UL><LI>\%s - normal string
	<LI>\%x - special string, interpreted for use as string literal in the query
	(so special characters are escaped properly)
	<LI>\%i - integer
	</UL>
	If you need a % sign, literally, try '%\i' for instance.
	Example: composeSqlStatement("select * from tmp_usertable where name='\%x', name);
	\param affected_rows int* the integer returning the number of rows affected by the query
	(if NULL pointer provided, the function does not return the number of
	affected rows (obviously) )
	\param sqlstring the format string (with options as displayed above)
	\param ... the unknown list of parameters to be used
	\return the resultset, please do not forget to call mysql_free_result
	\sa SendSQL2
*/
MYSQL_RES *
executeQuery(int *affected_rows, char *sqlstring, ...)
{
	char *my_sqlstring, *s, *special_s;
	int my_size, my_length, d, i;
	va_list ap;
	char temp[20];
	MYSQL_RES *myResultSet;

	my_size = strlen(sqlstring);
	my_sqlstring = (char *) malloc(my_size+1);
	*my_sqlstring = 0;
	my_length = 0;

//	va_start(ap, sqlstring);
//	for (i=1;i++;i<15)
//	{
//		s = va_arg(ap, char *);
//		printf("%s\n", s);
//	}
//	va_end(ap);

	va_start(ap, sqlstring);
	while (*sqlstring)
	{
		if (*sqlstring == '%')
		{
			switch(sqlstring[1]) 
			{
				case 's': /* common_string */
					s = va_arg(ap, char *);
					if (s == NULL) {s = "";}
					my_size += strlen(s);
					my_length += strlen(s);
					my_sqlstring = realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, s);
					sqlstring++;
					break;
				case 'x': /* mysql_string */
					s = va_arg(ap, char *);
					if (s == NULL) {s = "";}
					special_s = (char *) malloc(strlen(s)*2+1);
					i = mysql_real_escape_string(&dbconnection, special_s, s, strlen(s));
					my_size += i;
					my_length += i;
					my_sqlstring = realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, special_s);
					free(special_s);special_s=NULL;
					sqlstring++;
					break;
				case 'i': /* int */
					d = va_arg(ap, int);
					sprintf(temp, "%i", d);
					s = temp;
					my_size += strlen(s);
					my_length += strlen(s);
					my_sqlstring = realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, s);
					sqlstring++;
					break;
				default :
					my_sqlstring[my_length] = *sqlstring;
					my_length++;
					my_sqlstring[my_length] = 0;
				break;
			}
		}
		else
		{
			my_sqlstring[my_length] = *sqlstring;
			my_length++;
			my_sqlstring[my_length] = 0;
		}
		sqlstring++;
	}
	va_end(ap);
	
	myResultSet = SendSQL2(my_sqlstring, affected_rows);
	free(my_sqlstring);

	return myResultSet;
}

//! get room infor about a certain room
roomstruct *GetRoomInfo(int room)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	roomstruct *roomstr;
	int i;
	char *temp;
	
	roomstr = (roomstruct *)malloc(sizeof(*roomstr));
	
	roomstr->west=0;
	roomstr->east=0;
	roomstr->north=0;
	roomstr->south=0;
	roomstr->up=0;
	roomstr->down=0;
	
	temp = composeSqlStatement("select west, east, north, south, up, down from rooms where id=%i", room);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
	
		if (row != NULL)
		{
			roomstr->west=atoi(row[0]);
			roomstr->east=atoi(row[1]);
			roomstr->north=atoi(row[2]);
			roomstr->south=atoi(row[3]);
			roomstr->up=atoi(row[4]);
			roomstr->down=atoi(row[5]);
		}
	
		mysql_free_result(res);
	}

	return roomstr;
}

//! generate a session password to be used by player during game session
char *generate_password(char *fpassword)
{
int i;
srand(time(NULL));
for (i=0;i<25;i++) 
{
	switch (rand()*3/RAND_MAX)
	{
		case 0 :
			/* numbers 48-57*/
			fpassword[i]=48+(int) (10.0*rand()/(RAND_MAX+1.0));
			break;
		case 1 :
			/* capital letters 65-90 */
			fpassword[i]=65+(int) (26.0*rand()/(RAND_MAX+1.0));
			break;
		case 2 :
			/* small caps 97-122 */
			fpassword[i]=97+(int) (26.0*rand()/(RAND_MAX+1.0));
			break;
	}
}
fpassword[25]=0;
return fpassword;
}

//#define MM_ARRAYDEF
char *parameterdefs[24] =
{"HTMLHeader",
"USERHeader", 
"DATABASEHeader",
"CopyrightHeader",
"DatabaseName",
"DatabaseLogin", 
"DatabasePassword",
"DatabaseHost",
"DatabasePort",
"ServerName",
"CGIName",
"MudOffLineFile",
"StdHelpFile",
"AuditTrailFile", 
"BigFile",
"ErrorFile",
"MudCgi",
"EnterCgi",
"NewcharCgi",
"LogonframeCgi",
"LeftframeCgi",
"MMHOST",
"MMPORT",
NULL};

/*! dumps the contents of the configuration to the screen. THe
	databasepassword is skipped for security reasons. 
*/
void writeConfig()
{
	int i;
	i=0;
	send_printf(getMMudOut(), "<TABLE>\r\n");
	while (parameterdefs[i] != NULL)
	{
		if ((i+1) != MM_DATABASEPASSWORD)
		{
			send_printf(getMMudOut(), "<TR><TD>%i</TD><TD>%s</TD><TD>%s</TD></TR>\r\n",
			i+1, parameterdefs[i], getParam(i+1));
		}
		i++;
	}
	send_printf(getMMudOut(), "</TABLE>\r\n");
}
	

/*! read config file (xml file) and parse it properly 
	\param filename char * filename which contents is an appropriate xml format
*/
int readConfigFiles(char *filename)
{
	xmlDocPtr doc;
	xmlNsPtr ns;
	xmlNodePtr cur;
	xmlDtdPtr myDtd = NULL;
	char *temp;
	
	// build an XML tree from the file;
	doc = xmlParseFile(filename);
	if (doc == NULL)
	{
		return 0;
	}
	cur = xmlDocGetRootElement(doc);
	if (cur == NULL) 
	{
		fprintf(stderr,"empty document\n");
		xmlFreeDoc(doc);
		return(0);
	}
	cur = cur->xmlChildrenNode;
	while (cur != NULL) 
	{
		int counter;
		temp = NULL;
		counter = 0;
		while (parameterdefs[counter] != NULL)
		{
			if (!xmlStrcmp(cur->name, (const xmlChar *) parameterdefs[counter]))
			{
				if ((temp = xmlNodeListGetString(doc, cur->xmlChildrenNode, 1)) != NULL)
				{
					setParam(counter+1, temp);
#ifdef DEBUG
					printf("config: %s,%s(%i),%s\n", temp, parameterdefs[counter], counter, getParam(counter+1));
#endif					
					xmlFree(temp);
				}
			}
			counter++;
		}
		cur = cur->next;
	}
#ifdef DEBUG	
	xmlDocDump(stderr, doc);
#endif
	xmlFreeDoc(doc);
	return 1;
}


