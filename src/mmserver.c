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
#include <signal.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/types.h>
#include <errno.h>
       
// include files for socket communication
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <unistd.h>
#include <string.h>

// include file for using the syslogd system calls
#include <syslog.h>

// include files for the xml library calls
#include <libxml/xmlmemory.h>
#include <libxml/parser.h>
#include <libxml/tree.h>

// include file for multi threading
#include <pthread.h>

#include "typedefs.h"
#include "mudmain.h"
#include "mudnewchar.h"

/*! \file mmserver.c
	\brief  the main server executable file, takes care of socket communication and calling the appropriate other methods
like gameMain, gameLogon and gameNewchar */

/*! version number of mmserver */
#define MMVERSION "4.01b" // the mmud version in general
/*! protocol version of mmserver, should be kept backwards compatible,
    if backwards compatibility should be broken, update the major
    version number */
#define MMPROTVERSION "1.0" // the protocol version used in this mud 
/*! identity string of mmserver, sent immediately to client when
    the client connects */
#define IDENTITY "Maartens Mud (MMud) Version " MMVERSION " " __DATE__ __TIME__ "\n"

int signal_caught = 0;

/*! this function returns true if a SIGHUP/SIGUSR1/SIGUSR2 signal was caught 
    \return int the identification number of the signal caught*/
int isSignalCaught()
{
	int i = signal_caught;
	signal_caught = 0; // empty signal caught, i.e. is reset upon calling this method
	return i;
}

/*!
 this function will be run when the process receives a SIGHUP, SIGUSR1 or SIGUSR2 signal
 and will attempt to close and reopen the different files as well as reread it's config files.
 /param int the signal received, standard parameter required for catching signals
 */
void signalhandler(int signum)
{
	syslog(LOG_INFO, "signal %i caught.", signum);
	signal_caught = signum;
}

/*!
 this function will be run when the process receives a SIGTERM signal
 and will attempt to write the current command,username,frames to the syslog
 for debugging purposes. (i.e. we now know which command SegFaults the mmserver)
 This function also immediately terminates this process by means of abort()
 \param signum int the signal received, standard parameter required for catching signals
 */
void emergency_signalhandler(int signum)
{
	syslog(LOG_INFO, "SIGSEGV signal caught.");
	abort();
}

/*! initialises statistics with startup information of the mmserver */
void
init_mudinfo()
{
	struct hostent *myhostent;
	mudinfostruct mymudinfo;
	
	mymudinfo = getMudInfo();	
	
	if (gethostname(mymudinfo.hostname, 255) == -1)
	{
		perror("attempting to retrieve current host...");
		exit(1);
	}
	if ((myhostent = gethostbyname(mymudinfo.hostname)) == NULL)
	{
		herror("attempting to retrieve current ip...");
		exit(1);
	}
	strcpy(mymudinfo.hostip, inet_ntoa(*((struct in_addr *)myhostent->h_addr)));
	if (getdomainname(mymudinfo.domainname, 255) == -1)
	{
		perror("attempting to retrieve current ip...");
		exit(1);
	}
	strcpy(mymudinfo.mmudtime, __TIME__);
	strcpy(mymudinfo.mmuddate, __DATE__);
	strcpy(mymudinfo.protversion, MMPROTVERSION);
	strcpy(mymudinfo.mmudversion, MMVERSION);
	time(&(mymudinfo.mmudstartuptime));
	mymudinfo.number_of_connections = 0;
	mymudinfo.number_of_timeouts = 0;
	mymudinfo.number_of_current_connections = 0;
	mymudinfo.maxnumber_of_current_connections = 0;
	setMudInfo(mymudinfo);	
}

/*! adds a connection attempt to the statistics */
void
addconnection_mudinfo()
{
	mudinfostruct mymudinfo;
	mymudinfo = getMudInfo();
	mymudinfo.number_of_connections++;
	mymudinfo.number_of_current_connections++;
	if (mymudinfo.number_of_current_connections > mymudinfo.maxnumber_of_current_connections)
	{
			mymudinfo.maxnumber_of_current_connections = mymudinfo.number_of_current_connections;
	}
	setMudInfo(mymudinfo);
}

/*! removes a closed connection from the statistics */
void
removeconnection_mudinfo()
{
	mudinfostruct mymudinfo;
	mymudinfo = getMudInfo();
	mymudinfo.number_of_current_connections--;
	setMudInfo(mymudinfo);
}

/*! display statistics, deprecated, has been handled in mudmain.c*/
void
display_mudinfo()
{
	mudinfostruct mymudinfo;
	
	mymudinfo = getMudInfo();
	//time_t mmudstartuptime;
	printf("Host: %s\nIp address: %s\nDomainname: %s\nProtocol version: %s\nMmud version: %s %s %s\n", 
	mymudinfo.hostname, mymudinfo.hostip, mymudinfo.domainname, mymudinfo.protversion, mymudinfo.mmudversion,
	mymudinfo.mmudtime, mymudinfo.mmuddate);
	printf("Number of connections: %i\nNumber of timeouts: %i\nNumber of current connections: %i\nNumber of max cur connections: %i\n",
	mymudinfo.number_of_connections, mymudinfo.number_of_timeouts, mymudinfo.number_of_current_connections,
	mymudinfo.maxnumber_of_current_connections);
}

/*! rereads the config files, closes all logfiles and reopens them with the new names
 also closes all sockets if socket port number has been changed.
 /return int always returns 1
 */
int
rereadConfigFiles()
{
	syslog(LOG_INFO,"rereading config files.");
	display_mudinfo();
	return 1;
}

/*! the socket descriptor used for connections */
int sockfd = -1;

/*! initialise socket descriptor and bind it to the necessary port 
    /return int always returns 1, if error occurs exits program */
int
init_socket()
{
	struct sockaddr_in my_addr; // my address information
	struct hostent *he;
	int yes = 1;
	
	if ((he = gethostbyname(getParam(MM_HOST))) == NULL)
	{
		perror("gethostbyname");
		exit(1);
	}
	
	// get socket descriptor
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd == -1) // do some error checking
	{
		perror("attempting to get socket descriptor...");
		exit(1);
	}
	
	// lose the pesky "Address already in use" error messages 
	if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1)
	{
		perror("attempting to reuse address...");
		exit(2);
	}
	
	my_addr.sin_family = AF_INET; // host byte order
	my_addr.sin_port = htons(atoi(getParam(MM_PORT))); // short, network byte order
	//	my_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	//	my_addr.sin_addr.s_addr = inet_addr(getParam(MM_HOST));
	my_addr.sin_addr = *((struct in_addr *)he->h_addr);
	memset(&(my_addr.sin_zero), '\0', 8); // zero the rest of the struct
	
	// bind port/ip to socket descriptor
	if (bind(sockfd, (struct sockaddr *) &my_addr, sizeof(struct sockaddr)) == -1) 
	{
		// do some error checking
		perror("attempting to bind socket...");
		exit(2);
	}
	return 1;
}

/*! send messages and receive messages from socket, deprecated, used
    for testing */
void
do_socket()
{
	int sin_size;
	int new_fd;
	char *msg;
	char received_msg[1024];
	int bytes_sent;
	int bytes_received;
	struct sockaddr_in their_addr; // connector's address information
	if (listen(sockfd, 20) == -1)
	{
		// do some error checking (again)
		perror("attempting to listen..");
		exit(3);
	}
	
	sin_size = sizeof(struct sockaddr);
	new_fd = accept(sockfd, (struct sockaddr *) &their_addr, &sin_size);
	if (new_fd == -1)
	{
		// do some error checking
		perror("attempting to accept connection...");
		exit(4);
	}
	
	msg = IDENTITY;
	bytes_sent = send(new_fd, msg, strlen(msg), 0);
	if (bytes_sent == -1)
	{
		// do some error checking
		perror("attempting to send...");
		exit(5);
	}
	while (bytes_sent != strlen(msg))
	{
		msg += bytes_sent;
		bytes_sent = send(new_fd, msg, strlen(msg), 0);
		if (bytes_sent == -1)
		{
			// do some error checking
			perror("attempting to send...");
			exit(5);
		}
	}
	bytes_received = recv(new_fd, received_msg, 1024, 0);
	if (bytes_received == -1)
	{
		// do some error checking
		perror("attempting to receive...");
		exit(6);
	}
	while (bytes_received != 0)
	{
		bytes_received = recv(new_fd, received_msg, 1023, 0);
		if (bytes_received == -1)
		{
			// do some error checking
			perror("attempting to receive...");
			exit(6);
		}
		received_msg[bytes_received] = 0;
	}
	
	if (close(new_fd) == -1)
	{
		// do some error checking
		perror("attempting to close...");
		exit(7);
	}
}

/*! close the main socket, usually called at the end of the main while loop */
void
close_socket()
{
	if (close(sockfd) == -1)
	{
		// do some error checking
		perror("attempting to close...");
		exit(7);
	}
}

/*! parses the Xml document received from the socket and contained in
    mudpersonstruct.readbuf, parsed information is stored in the other
    fields of mudpersonstruct 
    \param fmine mudpersonstruct* mud information used for parsing and storing information
    \return int 1 on success, 0 on failure*/
int
parseXml(mudpersonstruct *fmine)
{
	xmlDocPtr doc;
	xmlNsPtr ns;
	xmlNodePtr cur, cur2;
	xmlDtdPtr myDtd = NULL;
	char *temp;
	mudnewcharstruct *newcharstruct;
	
	if (fmine == NULL)
	{
		return 0;
	}

	// build an XML tree from a the file;
	doc = xmlParseMemory(fmine->readbuf, strlen(fmine->readbuf));
	if (doc == NULL)
	{
		return 0;
	}

	myDtd = doc->intSubset;
	if (myDtd != NULL)
	{
		printf("[%s]\n", myDtd->name);
	}
	myDtd = doc->extSubset;
	if (myDtd != NULL)
	{
		printf("[%s]\n", myDtd->name);
	}
	cur = xmlDocGetRootElement(doc);
	if (cur == NULL) 
	{
		fprintf(stderr,"empty document\n");
		xmlFreeDoc(doc);
		return(0);
	}
	cur = cur->xmlChildrenNode;
	cur2 = NULL;
	while (cur != NULL) 
	{
		temp = NULL;
		if (!xmlStrcmp(cur->name, (const xmlChar *)"action"))
		{
			temp = xmlNodeListGetString(doc, cur->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				fmine->action = strdup(temp);
				xmlFree(temp);
			}
#ifdef DEBUG
			printf("action: %s\n", fmine->action);
#endif
		}
		if (!xmlStrcmp(cur->name, (const xmlChar *)"command"))
		{
			temp = xmlNodeListGetString(doc, cur->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				fmine->command = strdup(temp);
				xmlFree(temp);
			}
#ifdef DEBUG	
			printf("%s,%s\n", cur->name, fmine->command);
#endif
		}
		if (!xmlStrcmp(cur->name, (const xmlChar *)"user"))
		{
			cur2 = cur->xmlChildrenNode;
		}
		cur = cur->next;
	}
	// retrieve all information specific for the user (name,  passwd, frames)
	while (cur2 != NULL) 
	{
		temp = NULL;
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"name"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>19)
				{
					xmlFree(temp);
					temp = NULL;
					xmlFreeDoc(doc);
					return 0;
				}
				strcpy(fmine->name, temp);
#ifdef DEBUG	
				printf("%s,%s\n", cur2->name, temp);
#endif
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"password"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>39)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
				strcpy(fmine->password, temp);
#ifdef DEBUG	
				printf("%s,%s\n", cur2->name, fmine->password); //temp);
#endif
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"address"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>39)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
				strcpy(fmine->address, temp);
#ifdef DEBUG	
				printf("%s,%s\n", cur2->name, fmine->address); //temp);
#endif
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"cookie"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>79)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
				strcpy(fmine->cookie, temp);
#ifdef DEBUG	
				printf("%s,%s\n", cur2->name, temp);
#endif
				xmlFree(temp);
			}
			else
			{
				fmine->cookie[0]=0;
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"frames"))
		{
			int i;
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				i = atoi(temp);
				fmine->frames = i;
#ifdef DEBUG	
				printf("%s,%s\n", cur2->name, temp);
#endif
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"frace"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>9)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->frace, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"fsex"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>9)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->fsex, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"fage"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>14)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->fage, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"flength"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>14)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->flength, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"fwidth"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>15)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->fwidth, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"fcomplexion"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>15)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->fcomplexion, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"feyes"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>15)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->feyes, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"fface"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>18)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->fface, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"fhair"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>18)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->fhair, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"fbeard"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>18)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->fbeard, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"farm"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>18)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->farm, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"fleg"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
			if (temp != NULL)
			{
				if (strlen(temp)>18)
				{
					xmlFree(temp);
					temp=NULL;
					xmlFreeDoc(doc);
					return 0;
				}
#ifdef DEBUG	
				printf("newchar: %s, %s\n", cur2->name, temp);
#endif
				if (fmine->newchar == NULL) {fmine->newchar = create_mudnewcharstruct();}
				newcharstruct = (mudnewcharstruct *) fmine->newchar;
				strcpy(newcharstruct->fleg, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"ftitle"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
#ifdef DEBUG	
			printf("newchar: %s, %s\n", cur2->name, temp);
#endif
			if (temp != NULL)
			{
				if (fmine->newchar == NULL) 
				{
					fmine->newchar = create_mudnewcharstruct();
					newcharstruct = (mudnewcharstruct *) fmine->newchar;
				}
				else
				{
					if (newcharstruct->ftitle != NULL) {free(newcharstruct->ftitle);}
				}
				newcharstruct->ftitle = (char *) malloc(strlen(temp)+1);
				strcpy(newcharstruct->ftitle, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"frealname"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
#ifdef DEBUG	
			printf("newchar: %s, %s\n", cur2->name, temp);
#endif
			if (temp != NULL)
			{
				if (fmine->newchar == NULL) 
				{
					fmine->newchar = create_mudnewcharstruct();
					newcharstruct = (mudnewcharstruct *) fmine->newchar;
				}
				else
				{
					if (newcharstruct->frealname != NULL) {free(newcharstruct->frealname);}
				}
				newcharstruct->frealname = (char *) malloc(strlen(temp)+1);
				strcpy(newcharstruct->frealname, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		if (!xmlStrcmp(cur2->name, (const xmlChar *)"femail"))
		{
			temp = xmlNodeListGetString(doc, cur2->xmlChildrenNode, 1);
#ifdef DEBUG	
			printf("newchar: %s, %s\n", cur2->name, temp);
#endif
			if (temp != NULL)
			{
				if (fmine->newchar == NULL) 
				{
					fmine->newchar = create_mudnewcharstruct();
					newcharstruct = (mudnewcharstruct *) fmine->newchar;
				}
				else
				{
					if (newcharstruct->femail != NULL) {free(newcharstruct->femail);}
				}
				newcharstruct->femail = (char *) malloc(strlen(temp)+1);
				strcpy(newcharstruct->femail, temp);
				xmlFree(temp);
			}
			temp = NULL;
		}
		cur2 = cur2->next;	
	}
#ifdef DEBUG	
	xmlDocDump(stderr, doc);
#endif
	xmlFreeDoc(doc);
	return 1;
}

/*! add contents of read buffer to the socketstruct
    \param socketfd int socket descriptor
    \param buf char* buffer read from socket to be added to connection struct
    \return 1 upon success, 0 upon failure to interpret Xml document
 */
int
store_in_list(int socketfd, char *buf)
{
	mudpersonstruct *mine;
	mine = find_in_list(socketfd);
	if (mine != NULL)	{
		char *temp;
		temp = realloc(mine->readbuf, mine->bufsize + strlen(buf));
		if (temp == NULL)
		{
			syslog(LOG_ERR, "attempting to realloc storage...");
			exit(7);
		}
		mine->bufsize += strlen(buf);
		mine->readbuf = temp;
		strcat(mine->readbuf, buf);
		temp = strstr(mine->readbuf, "</root>");
		if (temp != NULL)
		{
			char t;
			t = temp[7];
			temp[7] = 0;
#ifdef DEBUG
			printf(mine->readbuf);
#endif
			if (parseXml(mine))
			{
				char *temp2, string[1024];
				FILE *filep;
				int j;
				temp[7] = t;
				temp2 = (char *) malloc(strlen(temp+7)+1);
				if (temp2 == NULL)
				{
					syslog(LOG_ERR, "attempting to malloc storage...");
					exit(7);
				}
				strcpy(temp2, temp+7);
				free(mine->readbuf);
				mine->readbuf = temp2;
				mine->bufsize = strlen(temp2)+1;
#ifdef DEBUG
//				printf("%s/%s/%s/%i/%s\n", mine->action, mine->name, mine->password, mine->frames, mine->command);
				fflush(stdout);
#endif
				
				WriteSentenceIntoOwnLogFile(getParam(MM_BIGFILE), "mmserver: %s (%s): |%s|\n", mine->name, mine->password, mine->command);
				// decide what action to take
				if (!strcasecmp(mine->action, "logon"))
				{
					gameLogon(socketfd);
				}
				else
				if (!strcasecmp(mine->action, "newchar"))
				{
					gameNewchar(socketfd);
				}
				else
				{
					gameMain(socketfd); 
				}
				j = strlen("</HTML>");
				if (send_socket(socketfd, "</HTML>", &j) == -1)
				{
					syslog(LOG_INFO, "error during send to the socket...");
				}
				if (j != strlen("</HTML>"))
				{
					syslog(LOG_INFO, "unable to send all information to the socket...");
				}
				return 1;
			}
#ifdef DEBUG
			else
			{
				printf("Error parsing XML file...");
			}
#endif				
		}
	}
	return 0;
}

typedef struct thread_control
{
	int socketfd;
	int active;
	pthread_t mythread;
	struct thread_control *next;
} thread_control;

thread_control *head = NULL;
thread_control *cleanup = NULL;
pthread_mutex_t threadlistmutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t threadcond;

void *cleanupthread_function(void *arg)
{
	thread_control *stuff;

	while (!isShuttingdown())
	{
		pthread_mutex_lock(&threadlistmutex);
		pthread_cond_wait(&threadcond, &threadlistmutex);
		while (cleanup != NULL)
		{
#ifdef DEBUG
			printf("cleanupthread: awoken\n");
#endif
			stuff = cleanup;
			cleanup = cleanup->next;
			pthread_join(stuff->mythread, NULL);
			stuff->next = NULL;
			free(stuff);
#ifdef DEBUG
			printf("cleanupthread: back to sleep\n");
#endif
		}
		pthread_mutex_unlock(&threadlistmutex);
	}
	pthread_exit(NULL);
	return NULL;
}

void *thread_function(void *arg)
{
	int socketfd;
	int msglength;
	thread_control *mycontrol, *checklist;
	char buf[1024];
	
	// necessary for mysql to initialize thread specific variables
//	my_thread_init();

	mycontrol = (thread_control *) arg;
	socketfd = mycontrol->socketfd;
	addconnection_mudinfo();
	add_to_list(socketfd);
	printf("Starting thread with socket %i...\n", socketfd);
	msglength = strlen(IDENTITY);
	send_socket(socketfd, IDENTITY, &msglength);
	if (msglength != strlen(IDENTITY))
	{
		perror("Unable to send entire message...");
	}
	// handle data from a client
	int nbytes;
	while ((nbytes = recv(socketfd, buf, sizeof(buf), 0)) > 0)
	{
		// we got some data from a client
		buf[nbytes]=0;
		if (store_in_list(socketfd, buf) == 1)
		{
			// do nothing
		}
	}
	// got error or connection closed by client
	if (nbytes == 0)
	{
		// connection closed
		printf("Connection %i closed\n", socketfd);
	}
	else
	{
		syslog(LOG_WARNING, "attempting to receive data from socket");
	}
	// close socket from our side as well
	if (close(socketfd) == -1)
	{
		syslog(LOG_WARNING, "attempting to close user socket");
	}
	else
	{
		remove_from_list(socketfd);
		removeconnection_mudinfo();
	}
	
	pthread_mutex_lock(&threadlistmutex);
	checklist = head;
	if (checklist != mycontrol)
	{
		while (checklist->next != mycontrol)
		{
			checklist = checklist->next;
		}
		checklist->next = mycontrol->next;
	}
	else
	{
		head = mycontrol->next;
	}

	mycontrol->active = 0;
	mycontrol->next = cleanup;
	cleanup = mycontrol;
	pthread_mutex_unlock(&threadlistmutex);
	pthread_cond_broadcast(&threadcond);
	// free(mycontrol)

	// deinitialize the mysql thread-specific variables
//	my_thread_end();
	pthread_exit(NULL);
	return NULL;
}

/*! main function */
int 
main(int argc, char **argv)
{
//	struct rusage usage;
	int i;
	pthread_t cleanupthread;
        
	// socket variables
	fd_set master_fds;	// master file descriptor list
	fd_set read_fds;	// temp file descriptorlist for select()
	int fdmax; // biggest filedescriptor
	struct sockaddr_in their_addr; // connector's address information
	char buf[256]; // buffer for receiving info from socket
		
	// signal catching variables
	struct sigaction mySig, myEmergencySig;
	mySig.sa_handler = &signalhandler;
	myEmergencySig.sa_handler = &emergency_signalhandler;

	sigemptyset(&(mySig.sa_mask));
	mySig.sa_flags = 0;
	sigaction(SIGHUP, &mySig, NULL);
	sigaction(SIGUSR1, &mySig, NULL);
	sigaction(SIGUSR2, &mySig, NULL);
	
	sigemptyset(&(myEmergencySig.sa_mask));
	myEmergencySig.sa_flags = 0;
	sigaction(SIGSEGV, &myEmergencySig, NULL);

	/* do socket stuff */

	FD_ZERO(&master_fds); // clear master file descriptor set
	FD_ZERO(&read_fds); // clear the read file descriptor set
	
	openlog("mmserver", LOG_CONS || LOG_PERROR || LOG_PID, LOG_USER);

	syslog(LOG_INFO, "%s: Started.", IDENTITY);
	syslog(LOG_INFO, "reading config file");
	initParam();
	readConfigFiles("config.xml");
	
	init_mudinfo();
	display_mudinfo();
	init_socket();
	if (listen(sockfd, 20) == -1)
	{
		// do some error checking (again)
		perror("attempting to listen..");
		exit(3);
	}
	// add main socket to the master set
	FD_SET(sockfd, &master_fds);
	// keep track of the biggest file descriptor
	fdmax = sockfd;
	
	/* below starts basically the entire call to the mudEngine */
	syslog(LOG_INFO, "opening database connection....");
	
	if (!opendbconnection())
	{
		syslog(LOG_INFO, "Unable to open database connection...");
		syslog(LOG_INFO, getdberror());
		return 1;
	}
	if (mysql_thread_safe() != 1)
	{
		syslog(LOG_WARNING, "Mysql Client not Thread Safe...");
	}
	initGameFunctionIndex(); // initialise command index 
	syslog(LOG_INFO, "accepting incoming connections...");
	syslog(LOG_INFO, "starting cleanup thread...");
	if (pthread_create( &cleanupthread, NULL, cleanupthread_function, NULL) )	
	{
		printf("error creating cleanup thread...\n");
		abort();
	}
	while (!isShuttingdown())
	{
#ifdef DEBUG
		printf("[Listening on socket...]\n");
#endif
		/* more socket stuff */
		int newconnectionsocket;
		thread_control *newthread;
		if (isSignalCaught())
		{
			rereadConfigFiles();
		}
		if ((newconnectionsocket = accept(sockfd, NULL, NULL)) == -1)
		{
			int i = errno;
			if (i != EINTR)
			{
				syslog(LOG_ERR, "accept : %s\n", strerror(i));
				exit(8);
			}
		}
		/* we now have a new connection socket!!! */
		newthread = (thread_control *) malloc(sizeof(thread_control));
		newthread->socketfd = newconnectionsocket;
		newthread->active = 1;
		pthread_mutex_lock(&threadlistmutex);
		newthread->next = head;
		head = newthread;
		pthread_mutex_unlock(&threadlistmutex);
		if (pthread_create( &(newthread->mythread), NULL, thread_function, newthread) )
		{
			printf("error creating thread...\n");
			abort();
		}
	}
	syslog(LOG_INFO, "shutdown initiated...");
	syslog(LOG_INFO, "waiting for threads...");
	while (head !=NULL)
	{
		pthread_join(head->mythread, NULL);
	}
	syslog(LOG_INFO, "waiting for ending cleanup thread...");
	pthread_join(cleanupthread, NULL);
	clearGameFunctionIndex(); // clear command index
	if (close(sockfd) == -1)
	{
		// do some error checking
		syslog(LOG_ERR, "attempting to close main socket...");
		exit(7);
	}
	
	syslog(LOG_INFO, "closing database connection...");
	closedbconnection();
	
	freeParam();

	syslog(LOG_INFO, "%s: Stopped.", IDENTITY);
	
	closelog();
	
	return 0;
}
