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

#include "cgi-util.h"
#include "typedefs.h"

#define MMVERSION "4.01b" // the mmud version in general
#define MMPROTVERSION "1.0" // the protocol version used in this mud
#define IDENTITY "Maartens Mud (MMud) Version " MMVERSION " " __DATE__ __TIME__ "\n"

/*! \file simple cgi-binary used for creating a new character. */

char frace[10];
char fsex[10];
char fage[15];
char flength[16];
char fwidth[16];
char fcomplexion[16];
char feyes[16];
char fface[19];
char fhair[19];
char fbeard[19];
char farm[16];
char fleg[16];
char ftitle[80];
char frealname[80];
char femail[40];
 
//! retrieve cookie 'Karchan' from browser, should contain the session password
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

//! dump standard error page on user, cookie not found. Cookie should have been set by enter.c
void CookieNotFound(char *name, char *address)
{
	printf("Content-type: text/html\n\n");
//getCookie(cgiOut, "Karchan","");
	printf("<HTML><HEAD><TITLE>Unable to logon</TITLE></HEAD>\n\n");
	printf("<BODY>\n");
	printf("<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Unable to logon</H1><HR>\n");
	printf("When you logon, a cookie is automatically generated. ");
	printf("However, I have been unable to find my cookie.<P>\n");
	printf("Please attempt to relogon.<P>\n");
	printf("</body>\n");
	printf("</HTML>\n");
}

//! create a valid xml document
/*! created a valid xml document with all the necessary fields to generate a new character on the mud. */
char *
createXmlString(char *fname, char *fpassword, char *fcookie, int fframes)
{
	xmlDocPtr doc;
	xmlNodePtr tree, subtree;
	char frames[10], *myBuffer;
	int mySize;
	
	sprintf(frames, "%i", fframes);
	doc = xmlNewDoc("1.0");
	doc->children = xmlNewDocNode(doc, NULL, "root", NULL);
	//xmlSetProp(doc->children, "prop1", "gnome is great");
	tree = xmlNewChild(doc->children, NULL, "action", "newchar");
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
	subtree = xmlNewChild(tree, NULL, "frace", frace);
	subtree = xmlNewChild(tree, NULL, "fsex", fsex);
	subtree = xmlNewChild(tree, NULL, "fage", fage);
	subtree = xmlNewChild(tree, NULL, "flength", flength);
	subtree = xmlNewChild(tree, NULL, "fwidth", fwidth);
	subtree = xmlNewChild(tree, NULL, "fcomplexion", fcomplexion);
	subtree = xmlNewChild(tree, NULL, "feyes", feyes);
	subtree = xmlNewChild(tree, NULL, "fface", fface);
	subtree = xmlNewChild(tree, NULL, "fhair", fhair);
	subtree = xmlNewChild(tree, NULL, "fbeard", fbeard);
	subtree = xmlNewChild(tree, NULL, "farm", farm);
	subtree = xmlNewChild(tree, NULL, "fleg", fleg);
	subtree = xmlNewChild(tree, NULL, "ftitle", ftitle);
	subtree = xmlNewChild(tree, NULL, "frealname", frealname);
	subtree = xmlNewChild(tree, NULL, "femail", femail);
	//xmlSaveFile("myxmlfile.xml", doc);

	//void xmlDocDumpMemory(xmlDocPtr cur, xmlChar**mem, int *size)
	xmlDocDumpMemory(doc, (xmlChar **) &myBuffer, &mySize);

	return myBuffer;
}

//! main method
int main(int argc, char *argv[])
{
	int res, totalnumbytes;
	char name[20];
	char password[40];
	char cookie[80];
	char frames[10];
	char *temp;
	char *myhostname, *myport;
	char *mudtitle = NULL;
	
	int sockfd, numbytes;
	int first;
	char receivebuf[1024], *sendbuf, *checkbuf;
	struct hostent *he;
	struct sockaddr_in their_addr; // connector's address information
         
	umask(0000);
	initParam();
	readConfigFiles("/karchan/config.xml");

#ifdef DEBUG
	strcpy(name, "Karn");
	strcpy(password, "tryout");
	setFrames(0);
#else
	res = cgi_init();
	if (res != CGIERR_NONE)
	{
		printf("Content-type: text/html\n\n");
		printf("Error # %d: %s<P>\n", res, cgi_strerror(res));
		exit(1);
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
	password[39]=0;
	if (!getCookie("Karchan", cookie))
	{
		strcpy(cookie," ");
	}
	if (cgi_getentrystr("frames") == NULL)
	{
		strcpy(frames, "none");
		setFrames(0);
	}
	else
	{
		strncpy(frames, cgi_getentrystr("frames"), 9);
		frames[9]=0;
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
	if (!strcmp(frames,"1")) {setFrames(0);}
	if (!strcmp(frames,"2")) {setFrames(1);}
	if (!strcmp(frames,"3")) {setFrames(2);}
	*ftitle=*frealname=*femail=*frace=*fsex=*fage=*flength=0;
	*fwidth=*fcomplexion=*feyes=*fface=*fhair=*fbeard=*farm=*fleg=0;
	if (cgi_getentrystr("title") != NULL) 
	{
		strncpy(ftitle, cgi_getentrystr("title"), 79);
		ftitle[79]=0;
	}
	if (cgi_getentrystr("RealName") != NULL)
	{
		strncpy(frealname, cgi_getentrystr("RealName"), 79);
		frealname[79]=0;
	}
	if (cgi_getentrystr("EMAIL") != NULL)
	{
		strncpy(femail, cgi_getentrystr("EMAIL"), 39);
		femail[39]=0;
	}
	if (cgi_getentrystr("race") != NULL)
	{
		strncpy(frace, cgi_getentrystr("race"), 9);
		frace[9]=0;
	}
	if (cgi_getentrystr("sex") != NULL)
	{
		strncpy(fsex, cgi_getentrystr("sex"), 9);
		fsex[9]=0;
	}
	if (cgi_getentrystr("age") != NULL)
	{
		strncpy(fage, cgi_getentrystr("age"), 14);
		fage[14]=0;
	}
	if (cgi_getentrystr("length") != NULL)
	{
		strncpy(flength, cgi_getentrystr("length"), 15);
		flength[15]=0;
	}
	if (cgi_getentrystr("width") != NULL)
	{
		strncpy(fwidth, cgi_getentrystr("width"), 15);
		fwidth[15]=0;
	}
	if (cgi_getentrystr("complexion") != NULL)
	{
		strncpy(fcomplexion, cgi_getentrystr("complexion"), 15);
		fcomplexion[15]=0;
	}
	if (cgi_getentrystr("eyes") != NULL)
	{
		strncpy(feyes, cgi_getentrystr("eyes"), 15);
		feyes[15];
	}
	if (cgi_getentrystr("face") != NULL)
	{
		strncpy(fface, cgi_getentrystr("face"), 18);
		fface[18]=0;
	}
	if (cgi_getentrystr("hair") != NULL)
	{
		strncpy(fhair, cgi_getentrystr("hair"), 18);
		fhair[18]=0;
	}
	if (cgi_getentrystr("beard") != NULL)
	{
		strncpy(fbeard, cgi_getentrystr("beard"), 18);
		fbeard[18]=0;
	}
	if (cgi_getentrystr("arms") != NULL)
	{
		strncpy(farm, cgi_getentrystr("arms"), 15);
		farm[15]=0;
	}
	if (cgi_getentrystr("legs") != NULL)
	{
		strncpy(fleg, cgi_getentrystr("legs"), 15);
		fleg[15]=0;
	}
#endif

	/* setup socket stuff*/
	if ((he = gethostbyname(myhostname)) == NULL)
	{
		perror("gethostbyname");
		exit(1);
	}
	
	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
	{
		perror("socket");
		exit(1);
	}
	
	their_addr.sin_family = AF_INET;	// host byte order
	their_addr.sin_port = htons(atoi(myport));	// short, network byte order
	their_addr.sin_addr = *((struct in_addr *)he->h_addr);
	memset(&(their_addr.sin_zero), '\0', 8);	//zero the rest of the struct
	
	if (connect(sockfd, (struct sockaddr *)&their_addr, sizeof(struct sockaddr)) == -1)
	{
		perror("connect");
		exit(1);
	}
	
	if ((numbytes=recv(sockfd, receivebuf, 1024-1, 0)) == -1)
	{
		perror("recv");
		exit(1);
	}
	
	receivebuf[numbytes] = '\0';checkbuf = NULL;totalnumbytes=1;
	mudtitle = (char *) malloc(strlen(receivebuf)+1);
	strcpy(mudtitle, receivebuf);
	//	printf("<FONT Size=1>%s</FONT><HR>",receivebuf);
	sendbuf = createXmlString(name, password, cookie, getFrames());
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
			receivebuf[numbytes]=0;
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
		printf("Content-type: text/html\r\n\r\n");
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
	freeParam();
	
	return 0;
}

