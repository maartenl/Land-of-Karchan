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

// include file for threading, database calls need a mutex
#include <pthread.h>

#include <syslog.h>

#include "typedefs.h"
#include "mudnewchar.h"

/*! \file typedefs.c
	\brief  definition file with constants and essential operations
like database operations, server statistics, configuration and tokenization
*/

#ifndef MEMMAN
#define mud_malloc(A,B,C)	malloc(A)
#define mud_free(A)		free(A)
#define mud_strdup(A,B,C)	strdup(A)
#define mud_realloc(A,B)	realloc(A,B)
#endif

char secretpassword[40];

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
	int n = 0;
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
pthread_mutex_t mysqlmutex=PTHREAD_MUTEX_INITIALIZER;

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

//! returns the error of the last mysql call
/*!
	\return char* containing the error message, empty string ("") in case no error occurred.
*/
const char *getdberror()
{
	return mysql_error(&dbconnection);
}

mudinfostruct mudinfo;

//! retrieve information about the mud (statistics)
mudinfostruct 
getMudInfo()
{
	return mudinfo;
}

//! set information about mud (statistics), called from mmserver.c
/*! The way these methods work is usually, you get the structure, you change
it and you put it back.
*/
void 
setMudInfo(mudinfostruct amudinfostruct)
{
	mudinfo = amudinfostruct;
}

int game_shutdown = 0;

//! game should be shut down, shut down initiated
/*! \return int, <UL><LI> 0, if not to be shut down
	<LI>1, if to be shut down
	<LI>>1, countdown</UL>
*/
int isShuttingdown()
{
	return game_shutdown;
}

//! indicate that the game should be shutting down as soon as possible
/*! \param aOffset int, used as offset, offset is added to shutdown count
	use setShuttingDown(-1) for countdown
*/
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
		mud_free(parameters[i]);
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
		mud_free(parameters[i]);
	}
	if (parameter != NULL)
	{
		parameters[i] = mud_strdup(parameter, __LINE__, __FILE__);
	}
}

//! returns the number of tokens of a command to be executed 
int
getTokenAmount(mudpersonstruct *fmudstruct)
{
	if (fmudstruct != NULL)
	{
		return fmudstruct->tokenamount;
	}
	return 0;
}


//! get the index of the token matching the description
/*! \param ftoken char* containing the token to look for in the tokens, 
	is not allowed to have any spaces as the tokens contain none.
	\return int, index number of token, -1 if not found
*/
int
getTokenIndex(mudpersonstruct *fmudstruct, char *ftoken)
{
	int i;
	for (i=0; i<fmudstruct->tokenamount; i++)
	{
		if (!strcasecmp(ftoken, fmudstruct->tokens[i]))
		{
			return i;
		}
	}
	return -1;
}

//! returns the i-th token, if i is beyond the number of available tokens, returns empty constant string
char *
getToken(mudpersonstruct *fmudstruct, int i)
{
	if ((i >= fmudstruct->tokenamount) || (i <  0))
	{
		return "";
	}
	return fmudstruct->tokens[i];
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
 This requires that this method be updated whenever a change in the mudpersonstruct is contemplated.
 \param socketfd int socket descriptor to be added to list of established connections and corresponding mud info, primary key
 \return int always returns 1
 */
int
add_to_list(int socketfd)
{
	mudpersonstruct *mine;
	int i;
	mine = (mudpersonstruct *) mud_malloc(sizeof(mudpersonstruct), __LINE__, __FILE__);
	if (mine == NULL)
	{
		return 0;
	}
	mine->name[0] = 0;
	mine->password[0] = 0;
	mine->address[0] = 0;
	mine->cookie[0] = 0;
	mine->frames = 0;
	mine->action = NULL;
	mine->command = NULL;
	mine->bufsize = 1;
	mine->room = -1;
	mine->readbuf = (char *) mud_malloc(mine->bufsize, __LINE__, __FILE__);
	mine->readbuf[0] = 0; // initialized to empty string
	mine->socketfd = socketfd;
	mine->newchar = NULL;
	mine->tokenamount = 0;
	mine->memblock = NULL; // once again, should contain a memory block used for strtok
	for (i = 0; i < 50; i++) 
	{
		mine->tokens[i] = NULL;
	}
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
		if (mine->socketfd == socketfd)
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
				mud_free(mine->command);
				mine->command = NULL;
			}
			if (mine->memblock != NULL) 
			{
				mud_free(mine->memblock);
				mine->memblock = NULL;
			}
			if (mine->action != NULL) 
			{
				mud_free(mine->action);
				mine->action = NULL;
			}
			if (mine->newchar != NULL) 
			{
				mudnewcharstruct *temp = (mudnewcharstruct *) mine->newchar;
				if (temp->ftitle != NULL)
				{
					mud_free(temp->ftitle);
				}
				if (temp->frealname != NULL)
				{
					mud_free(temp->frealname);
				}
				if (temp->femail != NULL)
				{
					mud_free(temp->femail);
				}
				mud_free(temp);
				mine->newchar = NULL;
			}
			mud_free(mine->readbuf);
			mine->readbuf = NULL;
			mud_free(mine);
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
	if ((p = mud_malloc (size, __LINE__, __FILE__)) == NULL)
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
		if ((p = mud_realloc (p, size)) == NULL)
		{
			return 0;
		}
	}
	sentbytes = strlen(p);
	resulting = send_socket(socketfd, p, &sentbytes);
	resulting = resulting & (sentbytes == strlen(p));
	mud_free(p);
	return resulting;
}

//! open the connection to the database server
/*! uses the parameters from the configuration file (config.xml) to connect.
	\sa closedbconnection
	\return int, 0 upon error, 1 upon success
*/
int 
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
		return 0;
	} 
	
	return 1;
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
MYSQL_RES *sendQuery(char *sqlstring, int *affected_rows)
{
	MYSQL_RES *res;
 
	pthread_mutex_lock(&mysqlmutex);
	if (mysql_query(&dbconnection,sqlstring))
		exiterr(3, sqlstring, &dbconnection);
 
	if (!(res = mysql_store_result(&dbconnection)) && mysql_field_count(&dbconnection))
		exiterr(4, sqlstring, &dbconnection);
	pthread_mutex_unlock(&mysqlmutex);
 	
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
	\return a char pointer to the composed string (memory has been allocated, do not forget to use mud_free())
	\sa sendQuery
*/
char *
composeSqlStatement(char *sqlstring, ...)
{
	char *my_sqlstring, *s, *special_s;
	int my_size, my_length, d, i;
	va_list ap;
	char temp[20];

	my_size = strlen(sqlstring);
	my_sqlstring = (char *) mud_malloc(my_size+1, __LINE__, __FILE__);
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
					my_sqlstring = mud_realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, s);
					sqlstring++;
					break;
				case 'x': /* mysql_string */
					s = va_arg(ap, char *);
					if (s == NULL) {s = "";}
					special_s = (char *) mud_malloc(strlen(s)*2+1, __LINE__, __FILE__);
					i = mysql_real_escape_string(&dbconnection, special_s, s, strlen(s));
					my_size += i;
					my_length += i;
					my_sqlstring = mud_realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, special_s);
					mud_free(special_s);special_s=NULL;
					sqlstring++;
					break;
				case 'i': /* int */
					d = va_arg(ap, int);
					sprintf(temp, "%i", d);
					s = temp;
					my_size += strlen(s);
					my_length += strlen(s);
					my_sqlstring = mud_realloc(my_sqlstring, my_size+1);
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
	\sa sendQuery
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
	my_sqlstring = (char *) mud_malloc(my_size+1, __LINE__, __FILE__);
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
					my_sqlstring = mud_realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, s);
					sqlstring++;
					break;
				case 'x': /* mysql_string */
					s = va_arg(ap, char *);
					if (s == NULL) {s = "";}
					special_s = (char *) mud_malloc(strlen(s)*2+1, __LINE__, __FILE__);
					i = mysql_real_escape_string(&dbconnection, special_s, s, strlen(s));
					my_size += i;
					my_length += i;
					my_sqlstring = mud_realloc(my_sqlstring, my_size+1);
					strcat(my_sqlstring, special_s);
					mud_free(special_s);special_s=NULL;
					sqlstring++;
					break;
				case 'i': /* int */
					d = va_arg(ap, int);
					sprintf(temp, "%i", d);
					s = temp;
					my_size += strlen(s);
					my_length += strlen(s);
					my_sqlstring = mud_realloc(my_sqlstring, my_size+1);
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
	
	myResultSet = sendQuery(my_sqlstring, affected_rows);
	mud_free(my_sqlstring);

	return myResultSet;
}

//! generate a session password to be used by player during game session
/*! \param fpassword a char* that may be modified and has as length at least 26 characters available
	\return char* the same pointer as fpassword, fpassword has been changed and contains 25 random 
	digits, capitals and smallcaps
*/
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
void writeConfig(int socketfd)
{
	int i;
	i=0;
	send_printf(socketfd, "<TABLE>\r\n");
	while (parameterdefs[i] != NULL)
	{
		if ((i+1) != MM_DATABASEPASSWORD)
		{
			send_printf(socketfd, "<TR><TD>%i</TD><TD>%s</TD><TD>%s</TD></TR>\r\n",
			i+1, parameterdefs[i], getParam(i+1));
		}
		i++;
	}
	send_printf(socketfd, "</TABLE>\r\n");
}
	

/*! read config file (xml file) and parse it properly 
	\param filename char * filename which contents is an appropriate xml format
*/
int readConfigFiles(char *filename)
{
	xmlDocPtr doc;
	xmlNodePtr cur;
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

// only code below is activated when memory needs checking
#ifdef MEMMAN

typedef struct memoryinfostruct
{
	void *pointer;
	size_t size;
	char *filename;
	int line;
} memoryinfostruct;

int memoryused = 0;
memoryinfostruct pointerarray[100];
int pointerarrayindex=0;

void *mud_malloc(size_t size, int line, char *filename)
{
	memoryinfostruct *mystruct;
	int i = 0;
	memoryused+=size;
	mystruct = &pointerarray[0];
	while ((mystruct->pointer != NULL) && (i<pointerarrayindex))
	{
		i++;
		mystruct = &pointerarray[i];
	}
	if (i==pointerarrayindex) 
	{
		pointerarrayindex++;
	}
	mystruct = &pointerarray[i];
	mystruct->pointer = malloc(size);
	mystruct->size = size;
	mystruct->line = line;
	mystruct->filename = filename;
	return mystruct->pointer;
}

char *mud_strdup(const char *s, int line, char *filename)
{
	size_t size;
	char *stuff;
	size = strlen(s)+1;
	stuff = (char *) mud_malloc(size, line, filename);
	strcpy(stuff, s);
	return stuff;
}

void mud_free(void *ptr)
{
	memoryinfostruct *mystruct;
	int result = 0;
	int i;
	i=0;
	while (i<pointerarrayindex)
	{
		mystruct = &pointerarray[i];
		if ((mystruct->pointer) == ptr)
		{
			mystruct->pointer = NULL;
			memoryused-=mystruct->size;
			mystruct->size = 0;
			result = 1;
		} else
		if ((mystruct->pointer < ptr) && (mystruct->pointer + mystruct->size >= ptr))
		{
			char *stuff;
			int j;
			stuff = (char *) malloc (mystruct->size+1);
			memcpy(stuff, mystruct->pointer, mystruct->size);
			stuff[mystruct->size]=0;
			//syslog(LOG_INFO, "   Memory pointer %i found: size= %i\n", i, mystruct->size);
			//syslog(LOG_INFO, "                       contents=\" ");
			for (j=0;j<mystruct->size;j++)
			{
				//syslog(LOG_INFO, "%.2i ",(unsigned int)stuff[j]);
			}
			//syslog(LOG_INFO,"   Found in: line=%i, file=%s\n", mystruct->line, mystruct->filename);
			free(stuff);
		}
		i++;
	}
	if (result == 0)
	{
		        //syslog(LOG_INFO, "unable to find allocated memory (%s).", (char *) ptr);
	}
	free((void *)ptr);
}

void *mud_realloc(void *ptr, size_t size)
{
	memoryinfostruct *mystruct;
	int i;
	i=0;
	while (i<pointerarrayindex)
	{
		mystruct = &pointerarray[i];
		if ((mystruct->pointer) == ptr)
		{
			mystruct->pointer = realloc(ptr, size);
			memoryused-=mystruct->size;
			memoryused+=size;
			mystruct->size = size;
			return mystruct->pointer;
		}
		i++;
	}
	return NULL;
}

void InitMem()
{
	memoryinfostruct *mystruct;
	int i;
	memoryused=0;
	printf("   Memory manager initialized...\n");
	for (i=0;i<100;i++)
	{
		mystruct = &pointerarray[i];
		mystruct->pointer = NULL;
		mystruct->size = 0;
		mystruct->line = 0;
		mystruct->filename = "unknown";
	}
}

void DeinitMem()
{
	memoryinfostruct *mystruct;
	int i;
	printf("   Memory still in use: %i\n", memoryused);
	for (i=0;i<100;i++)
	{
		mystruct = &pointerarray[i];
		if (mystruct->pointer != NULL)
		{
			char *stuff;
			int j;
			stuff = (char *) malloc (mystruct->size+1);
			memcpy(stuff, mystruct->pointer, mystruct->size);
			stuff[mystruct->size]=0;
			printf("   Memory pointer %i found: size= %i\n", i, mystruct->size);
			printf("                       contents=\" ");
			for (j=0;j<mystruct->size;j++)
			{
				printf("%.2i ",(unsigned int)stuff[j]);
			}
			printf("\"\n");
			printf("   Found in: line=%i, file=%s\n", mystruct->line, mystruct->filename);
			free(stuff);
		}
		free(mystruct->pointer);
		mystruct->pointer = NULL;
		mystruct->size = 0;
	}
}

#endif
// end of memory checking
