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

#include "cgic.h"

#define MMPORT 3339 // the port users will be connecting to
#define MMVERSION "4.01b" // the mmud version in general
#define MMPROTVERSION "1.0" // the protocol version used in this mud
#define IDENTITY "Maartens Mud (MMud) Version " MMVERSION " " __DATE__ __TIME__ "\n"

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

void CookieNotFound(char *name, char *address)
{
	cgiHeaderContentType("text/html");
//getCookie(cgiOut, "Karchan","");
	fprintf(cgiOut, "<HTML><HEAD><TITLE>Unable to logon</TITLE></HEAD>\n\n");
	fprintf(cgiOut, "<BODY>\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Unable to logon</H1><HR>\n");
	fprintf(cgiOut, "When you logon, a cookie is automatically generated. ");
	fprintf(cgiOut, "However, I have been unable to find my cookie.<P>\n");
	fprintf(cgiOut, "Please attempt to relogon.<P>\n");
	fprintf(cgiOut, "</body>\n");
	fprintf(cgiOut, "</HTML>\n");
}

/* attempts to send data over a socket, if not all information is sent.
will automatically attempt to send the rest.
@param int socket descriptor
@param char* message
@param int* length of message, should be equal to strlen(message) both at the beginning as well as after
*/
int
send_socket(int s, char *buf, int *len)
{
	int total = 0;	// how many btytes we've sent
	int bytesleft = *len;	// how many we have left to send
	int n;
#ifdef DEBUG
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

int theFrames = 0;

int getFrames()
{
	return theFrames;
}

void setFrames(int i)
{
	theFrames = i;
}

int cgiMain()
{
	char name[20];
	char password[40];
	char cookie[80];
	char frames[10];
	char *temp;
	
	int sockfd, numbytes;
	int first;
	char receivebuf[1024], *sendbuf;
	struct hostent *he;
	struct sockaddr_in their_addr; // connector's address information
          
	umask(0000);
	
#ifdef DEBUG
	strcpy(name, "Karn");
	strcpy(password, "tryout");
	setFrames(0);
#else
	cgiFormString("name", name, 20);
	cgiFormString("password", password, 40);
	if (!getCookie("Karchan", cookie))
	{
		strcpy(cookie, " ");
	}
	if (cgiFormString("frames", frames, 10)!=cgiFormSuccess) 
	{
		strcpy(frames, "none");
		setFrames(0);
	}
	if (!strcmp(frames,"1")) {setFrames(0);}
	if (!strcmp(frames,"2")) {setFrames(1);}
	if (!strcmp(frames,"3")) {setFrames(2);}
	*ftitle=*frealname=*femail=*frace=*fsex=*fage=*flength=0;
	*fwidth=*fcomplexion=*feyes=*fface=*fhair=*fbeard=*farm=*fleg=0;
	cgiFormString("title", ftitle, 80);
	cgiFormString("RealName", frealname, 80);
	cgiFormString("EMAIL", femail, 40);
	cgiFormString("race", frace, 10);
	cgiFormString("sex", fsex, 10);
	cgiFormString("age", fage, 15);
	cgiFormString("length", flength, 16);
	cgiFormString("width", fwidth, 16);
	cgiFormString("complexion", fcomplexion, 16);
	cgiFormString("eyes", feyes, 16);
	cgiFormString("face", fface, 19);
	cgiFormString("hair", fhair, 19);
	cgiFormString("beard", fbeard, 19);
	cgiFormString("arms", farm, 16);
	cgiFormString("legs", fleg, 16);
#endif
	
	/* setup socket stuff*/
	if ((he = gethostbyname("localhost")) == NULL)
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
	their_addr.sin_port = htons(MMPORT);	// short, network byte order
	their_addr.sin_addr = *((struct in_addr *)he->h_addr);
	memset(&(their_addr.sin_zero), '\0', 8);	//  zero the rest of the struct
	
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
	
	receivebuf[numbytes] = '\0';
//	printf("<FONT Size=1>%s</FONT><HR>",receivebuf);
	sendbuf = createXmlString(name, password, cookie, getFrames());
//	printf("[%s]", sendbuf);
	numbytes=strlen(sendbuf);
	send_socket(sockfd, sendbuf, &numbytes);
	free(sendbuf);
//	send_socket(sockfd, "\n", &numbytes);

	first = 1;
	while ((numbytes = recv(sockfd, receivebuf, 1024-1, 0)) != 0)
	{
		receivebuf[numbytes]=0;
		if (first)
		{
			if (strstr(receivebuf, "Content") != receivebuf)
			{
//				cgiHeaderContentType("text/html");
				fprintf(cgiOut, "Content-type: text/html\r\n\r\n");
			}
			first = 0;
		}
		fprintf(cgiOut, "%s", receivebuf);
	}
	close(sockfd);

	return 0;
}

