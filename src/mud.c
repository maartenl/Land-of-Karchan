/*-------------------------------------------------------------------------
cvsinfo: $Header$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This nifty program is distributed in the hope that it will be useful,
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
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/resource.h>
#include <sys/time.h>

// include files for socket communication
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <netdb.h>
#include <arpa/inet.h>

// include file for using the syslogd system calls
#include <syslog.h>

// include files for the xml library calls
#include <libxml/xmlmemory.h>
#include <libxml/parser.h>
#include <libxml/tree.h>

#include "typedefs.h"
#include "cgi-util.h"

/*! \file mud.c
	\brief  simple cgi-binary used for entering a command as a character in the game
*/

#define MMVERSION "4.01b" // the mmud version in general
#define MMPROTVERSION "1.0" // the protocol version used in this mud 
#define IDENTITY "Maartens Mud (MMud) Version " MMVERSION " " __DATE__ __TIME__ "\n"

//! retrieve cookie value
/*! 
\param name a string describing the name of the cookie
\param value a string that shall contain the value if the cookie exists
\returns int true upon success, otherwise false
			if returnvalue true then 'value' contains the value 
			of the cookie with name 'name'
*/
int getCookie(char *name, char *value)
{
	char *environmentvar;
	char *found;
	char *ending;
	environmentvar = getenv("HTTP_COOKIE");
	if (environmentvar == NULL)
	{
		/* Problems: Environment var containing cookies not found */
		return 0;
	}
//	fprintf(fp, "[%s]", environmentvar);
	if ((found = strstr(environmentvar, name)) == NULL)
	{
		/* Problems: Cookie not found! */
		return 0;
	}
	if ((ending = strstr(found, ";")) == NULL)
	{
		/* Hmmm, probably last cookie in the string of cookies */
		found += strlen(name)+1;
		strcpy(value, found);
	}
	else
	{
		/* Hmmm, everything seems to be in order, copying until ; */
		found += strlen(name)+1;
		strncpy(value, found+strlen(name)+1, ending-found);
		value[ending-found]='\0';
	}
	return 1;
}

//! display standard error page
void displayError(char *message, int i)
{
	printf("Content-type: text/html\n\n");
	printf("<HTML><HEAD><TITLE>Error - %s</TITLE></HEAD>\n\n", strerror(i));
	printf("<BODY>\n");
	printf("<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>%s - %s</H1><HR>\n", message, strerror(i));
	printf("Please contact me at <A HREF=\"mailto:karn@karchan.org\">karn@karchan.org</A>");
	printf(" to report the error.<P>\r\n");
	
	printf("<A HREF=\"/karchan/enter.html\">Click here to retry</A></body>\n");
	printf("</body>\n");
	printf("</HTML>\n");
	fflush(stdout);
	cgi_quit();
	exit(1);
}

int frames;

//! returns the interface id
int getFrames()
{
	return frames;
}

//! sets the interface id
void setFrames(int aFrames)
{
	frames = aFrames;
}

//! create a valid xml document
/*! created a valid xml document with all the necessary fields to generate a new character on the mud. */
char *
createXmlString(char *fcommand, char *fname, char *fpassword, char *fcookie, int fframes)
{
	xmlDocPtr doc;
	xmlNodePtr tree, subtree;
	char frames[10], *myBuffer;
	int mySize;
	
	sprintf(frames, "%i", fframes);
	doc = xmlNewDoc("1.0");
	doc->children = xmlNewDocNode(doc, NULL, "root", NULL);
	//xmlSetProp(doc->children, "prop1", "gnome is great");
	tree = xmlNewChild(doc->children, NULL, "action", "mud");
	tree = xmlNewChild(doc->children, NULL, "user", NULL);
	subtree = xmlNewChild(tree, NULL, "name", fname);
	//tree = xmlNewChild(doc->children, NULL, "chapter", NULL);
	subtree = xmlNewChild(tree, NULL, "password", fpassword);
	if (getenv("REMOTE_ADDR") != NULL)
	{   
		subtree = xmlNewChild(tree, NULL, "address", getenv("REMOTE_ADDR"));
	}   
	if ( (fcookie != NULL) && (strcmp(fcookie, "")) && (strcmp(fcookie, " ")) )
	{
		subtree = xmlNewChild(tree, NULL, "cookie", fcookie);
	}
	subtree = xmlNewChild(tree, NULL, "frames", frames);
	tree = xmlNewChild(doc->children, NULL, "command", fcommand);
	//xmlSaveFile("myxmlfile.xml", doc);

	//void xmlDocDumpMemory(xmlDocPtr cur, xmlChar**mem, int *size)
	xmlDocDumpMemory(doc, (xmlChar **) &myBuffer, &mySize);

	return myBuffer;
}

//! show not-active page to the user, means the player has not logged on yet
void 
NotActive(char *fname, char *fpassword, int errornr)
{
	printf("<HTML><HEAD><TITLE>Error</TITLE></HEAD>\n\n");
	printf("<BODY>\n");
	printf("<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>You are no longer active</H1><HR>\n");
	printf("You cannot MUD because you are not logged in (no more)."
	" This might have happened to the following circumstances:<P>");
	printf("<UL><LI>you were kicked out of the game for bad conduct");
	printf("<LI>the game went down for dayly cleanup, killing of all"
		"active users");
	printf("<LI>you were deactivated for not responding for over 1 hour");
	printf("<LI>an error occurred</UL>");
	printf("You should be able to relogin by using the usual link below:<P>");
	printf("<A HREF=\"/karchan/enter.html\">Click here to\n");
	printf("relogin</A><P>\n");
	printf("</body>\n");
	printf("</HTML>\n");
}

//! main function 
int 
main(int argc, char * argv[])
{
	char *command;
	char name[22];
	char password[40];
	char cookiepassword[40];
	char *myhostname, *myport;
	char *mudtitle = NULL;
		
	int sockfd, numbytes, totalnumbytes;
	char receivebuf[1024], *sendbuf, *checkbuf;
	struct hostent *he;
	struct sockaddr_in their_addr; // connector's address information
	
	initParam();
	readConfigFiles("/karchan/config.xml");

#ifdef DEBUG
	command = (char *) malloc(1024);
	printf("Command:");
	fgets(command, 1023, stdin);command[strlen(command)-1]=0;
	printf("Name:");
	fgets(name, 21, stdin);name[strlen(name)-1]=0;
	printf("Password:");
	fgets(password, 39, stdin);password[strlen(password)-1]=0;
	printf("Cookie:");
	fgets(cookiepassword, 39, stdin);cookiepassword[strlen(cookiepassword)-1]=0;
	setFrames(0);
	myhostname = strdup(getParam(MM_HOST));
	myport = strdup(getParam(MM_PORT));
#else
	res = cgi_init();
	if (res != CGIERR_NONE)
	{
		printf("Content-type: text/html\n\n");
		printf("Error # %d: %s<P>\n", res, cgi_strerror(res));
		exit(1);
	}
	if (cgi_getentrystr("command") == NULL)
	{
		displayError("Error #: command field required but not found<P>\n", 0);
		//printf("Content-type: text/html\n\n");
		printf("Error #: command field required but not found<P>\n");
		exit(1);
	}
	command = (char *) malloc(strlen(cgi_getentrystr("command"))+1);
	strcpy(command, cgi_getentrystr("command"));
	if (command[0]==0) 
	{
		free(command);
		command = strdup("l");
	}
	if (cgi_getentrystr("name") == NULL)
	{
		printf("Content-type: text/html\n\n");
		printf("Error #: name field required but not found<P>\n");
		exit(1);
	}
	strncpy(name, cgi_getentrystr("name"), 19);
	name[19]=0;
	if (cgi_getentrystr("password") == NULL)
	{
		printf("Content-type: text/html\n\n");
		printf("Error #: password field required but not found<P>\n");
		exit(1);
	}
	strncpy(password, cgi_getentrystr("password"), 39);
	password[39]='\0';
	if (cgi_getentrystr("frames") == NULL)
	{
		strcpy(frames, "none");
		setFrames(0);
	}
	else
	{
		strncpy(frames, cgi_getentrystr("frames"), 9);
		frames[9]='\0';
	}
	if (cgi_getentrystr("hostname") == NULL)
	{
		myhostname = strdup(getParam(MM_HOST));
	}
	else
	{
		myhostname = strdup(cgi_getentrystr("hostname"));
	}
	if (cgi_getentrystr("port") == NULL)
	{
		myport = strdup(getParam(MM_PORT));
	}
	else
	{
		if (atoi(cgi_getentrystr("port")) == 0)
		{
			myport = strdup(getParam(MM_PORT));
		}
		else
		{
			myport = strdup(cgi_getentrystr("port"));
		}
	}
	if (!strcasecmp("sendmail", command))
	{
		int i;
		if ( (cgi_getentrystr("mailheader") == NULL) ||
			(cgi_getentrystr("mailbody") == NULL) ||
			(cgi_getentrystr("mailto") == NULL) )
		{
			printf("Content-type: text/html\n\n");
			printf("Error #: mail form fields required but not found<P>\n");
			exit(1);
		}
		i = strlen(cgi_getentrystr("mailto")) + strlen(cgi_getentrystr("mailheader")) 
			+ strlen(cgi_getentrystr("mailbody")) + 40;
		free(command);
		command = (char *) malloc(i);
		sprintf(command, "sendmail %s %i %s %s", 
			cgi_getentrystr("mailto"), strlen(cgi_getentrystr("mailheader")), cgi_getentrystr("mailheader"), cgi_getentrystr("mailbody"));
	}
	if (getCookie("Karchan", cookiepassword) == 0)
	{
			strcpy(cookiepassword, " ");
	}
	if (strcmp(cookiepassword, password))
	{
		printf("Content-type: text/html\n\n");
			NotActive(name, password,3);
			return 0;
	}
	if (!strcmp(frames,"1")) {setFrames(0);}
	if (!strcmp(frames,"2")) {setFrames(1);}
	if (!strcmp(frames,"3")) {setFrames(2);}
	//WriteSentenceIntoOwnLogFile(BigFile, "%s (%s): |%s|\n", name, password, command);
#endif	

	if (strcasecmp("quit", command))
	{
		printf("Content-type: text/html\n\n");
	}
	else
	{
		printf("Content-type: text/html\r\n");
			printf("Set-cookie: Karchan=; expires= Monday, 01-January-01 00:05:00 GMT\r\n\r\n");
	}
	
	/* setup socket stuff*/
	if ((he = gethostbyname(myhostname)) == NULL)
	{
		displayError("gethostbyname", errno);
	}
	
	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
	{
		displayError("socket", errno);
	}
	
	their_addr.sin_family = AF_INET;	// host byte order
	their_addr.sin_port = htons(atoi(myport));	// short, network byte order
	their_addr.sin_addr = *((struct in_addr *)he->h_addr);
	memset(&(their_addr.sin_zero), '\0', 8);	//zero the rest of the struct
	
	if (connect(sockfd, (struct sockaddr *)&their_addr, sizeof(struct sockaddr)) == -1)
	{
		displayError("connect", errno);
	}
	
	if ((numbytes=recv(sockfd, receivebuf, 1024-1, 0)) == -1)
	{
		displayError("recv", errno);
	}
	
	receivebuf[numbytes] = '\0';checkbuf = NULL;totalnumbytes=1;
	mudtitle = (char *) malloc(strlen(receivebuf)+1);
	strcpy(mudtitle, receivebuf);
//	printf("<FONT Size=1>%s</FONT><HR>",receivebuf);
	sendbuf = createXmlString(command, name, password, cookiepassword, getFrames());
//	printf("[%s]", sendbuf);
	numbytes=strlen(sendbuf);
	send_socket(sockfd, sendbuf, &numbytes);
	free(sendbuf);
//	send_socket(sockfd, "\n", &numbytes);

	while ((numbytes = recv(sockfd, receivebuf, 1024-1, 0)) != 0)
	{
		if (numbytes==-1)
		{
			int i = errno;
			printf("[An error occurred receiving information: %s]", strerror(i));
		}
		else
		{
			receivebuf[numbytes]='\0';
			totalnumbytes+=numbytes;
			if (checkbuf == NULL)
			{
				checkbuf = (char *) malloc(numbytes+1);
				checkbuf[0]=0;
			}
			else
			{
				checkbuf = (char *) realloc(checkbuf, totalnumbytes);
			}
			strcat(checkbuf, receivebuf);
			if (strstr(checkbuf, "</HTML>") != NULL)
			{
				break;
			}
		}
	}
	numbytes=strlen("OK");
	send_socket(sockfd, "OK", &numbytes);
      
	close(sockfd);
	if (strstr(checkbuf, "Content") != checkbuf)
	{
	//		printf(Content-type: text/html\r\n\r\n");
		if (mudtitle != NULL)
		{
			printf("<FONT Size=1>%s</FONT><HR>",mudtitle);
			free(mudtitle);
			mudtitle = NULL;
		 }
	}
	if (mudtitle != NULL)
	{
		free(mudtitle);
		mudtitle = NULL;
	}
 	printf("%s", checkbuf);
	free(checkbuf);

	cgi_quit();

	fflush(stdout);
	free(command); // clear the entered command
	free(myhostname);
	free(myport);
	freeParam();
	return 0;
}
