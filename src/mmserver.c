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
Driek van Erpstraat 9
5341 AK Oss
Nederland
Europe
maartenl@il.fontys.nl
-------------------------------------------------------------------------*/
#include <time.h>
#include <signal.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/types.h>

// include files for socket communication
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <unistd.h>
// include file for using the syslogd system calls
#include <syslog.h>

// include files for the xml library calls
#include <libxml/xmlmemory.h>
#include <libxml/parser.h>
#include <libxml/tree.h>

#include "mudnewchar.h"

/*! version number of mmserver */
#define MMVERSION "4.01b" // the mmud version in general
/*! protocol version of mmserver, should be kept backwards compatible,
    if backwards compatibility should be broken, update the major
    version number */
#define MMPROTVERSION "1.0" // the protocol version used in this mud 
/*! identity string of mmserver, sent immediately to client when
    the client connects */
#define IDENTITY "Maartens Mud (MMud) Version " MMVERSION " " __DATE__ __TIME__ "\n"

char *current_name;
char *current_password;
int current_frames;
char *current_command; 

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
 \param int the signal received, standard parameter required for catching signals
 */
void emergency_signalhandler(int signum)
{
	syslog(LOG_INFO, "SIGSEGV signal caught. (%s, %s, %i)", current_name, current_command, current_frames);
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

/*! attempts to send data over a socket, if not all information is sent.
will automatically attempt to send the rest.
\param int socket descriptor
\param char* message
\param int* length of message, should be equal to strlen(message) both at the beginning as well as after
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
	mudnewcharstruct *newchar; // usually NULL, except when action=newchar
	void *next; // next item in the list
} mudpersonstruct;

/*! linked list of mudpersonstruct, keeps a list of established 
    connections/personal mud information */
mudpersonstruct *list = NULL;

/*! parses the Xml document received from the socket and contained in
    mudpersonstruct.readbuf, parsed information is stored in the other
    fields of mudpersonstruct 
    \param mudpersonstruct* mud information used for parsing and storing information
    \return int 1 on success, 0 on failure*/
int
parseXml(mudpersonstruct *fmine)
{
	xmlDocPtr doc;
	xmlNsPtr ns;
	xmlNodePtr cur, cur2;
	xmlDtdPtr myDtd = NULL;
	char *temp;

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
				strcpy(fmine->newchar->frace, temp);
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
				strcpy(fmine->newchar->fsex, temp);
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
				strcpy(fmine->newchar->fage, temp);
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
				strcpy(fmine->newchar->flength, temp);
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
				strcpy(fmine->newchar->fwidth, temp);
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
				strcpy(fmine->newchar->fcomplexion, temp);
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
				strcpy(fmine->newchar->feyes, temp);
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
				strcpy(fmine->newchar->fface, temp);
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
				strcpy(fmine->newchar->fhair, temp);
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
				strcpy(fmine->newchar->fbeard, temp);
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
				strcpy(fmine->newchar->farm, temp);
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
				strcpy(fmine->newchar->fleg, temp);
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
				}
				else
				{
					if (fmine->newchar->ftitle != NULL) {free(fmine->newchar->ftitle);}
				}
				fmine->newchar->ftitle = (char *) malloc(strlen(temp)+1);
				strcpy(fmine->newchar->ftitle, temp);
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
				}
				else
				{
					if (fmine->newchar->frealname != NULL) {free(fmine->newchar->frealname);}
				}
				fmine->newchar->frealname = (char *) malloc(strlen(temp)+1);
				strcpy(fmine->newchar->frealname, temp);
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
				}
				else
				{
					if (fmine->newchar->femail != NULL) {free(fmine->newchar->femail);}
				}
				fmine->newchar->femail = (char *) malloc(strlen(temp)+1);
				strcpy(fmine->newchar->femail, temp);
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

/*! add a new mudpersonstruct with default values to the beginning of the list
 (i.e. the first member of the list is the newly added socketfd) 
 \param int socket descriptor to be added to list of established connections and corresponding mud info
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
 \param int socket descriptor used in search
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
 \param int socket descriptor
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
				if (mine->newchar->ftitle != NULL)
				{
					free(mine->newchar->ftitle);
				}
				if (mine->newchar->frealname != NULL)
				{
					free(mine->newchar->frealname);
				}
				if (mine->newchar->femail != NULL)
				{
					free(mine->newchar->femail);
				}
				free(mine->newchar);
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

/*! add contents of read buffer to the socketstruct
    \param int socket descriptor
    \param char* buffer read from socket to be added to connection struct
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
				current_name = mine->name;
				current_password = mine->password;
				current_frames = mine->frames;
				if (!strcmp(mine->action, "mud")) {current_command = mine->command;}
				
				WriteSentenceIntoOwnLogFile(getParam(MM_BIGFILE), "mmserver: %s (%s): |%s|\n", mine->name, mine->password, mine->command);
				setFrames(mine->frames);
				filep = fopen("temp.txt", "w");
				if (filep == NULL)
				{
					syslog(LOG_ERR, "attempting to open file.(%s)..", strerror(errno));
					exit(7);
				}
				setMMudOut(filep);
				// decide what action to take
				if (!strcasecmp(mine->action, "logon"))
				{
					gameLogon(mine->name, mine->password, mine->cookie, mine->address);
				}
				else
				if (!strcasecmp(mine->action, "newchar"))
				{
					gameNewchar(mine->name, mine->password, mine->cookie, mine->address, mine->newchar);
				}
				else
				{
					gameMain(mine->command, mine->name, mine->password, mine->cookie, mine->address); 
				}
				fclose(filep);
				filep = fopen("temp.txt", "r");
				while (fgets(string, 1023, filep) != 0) 
				{
					int i = strlen(string);
					if (send_socket(socketfd, string, &i) == -1)
					{
						syslog(LOG_INFO, "error during send to the socket...");
					}
					if (i != strlen(string))
					{
						syslog(LOG_INFO, "unable to send all information to the socket...");
					}
				}
				fclose(filep);
				setMMudOut(stdout);
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

/*! main function */
int 
main(int argc, char **argv)
{
//	struct rusage usage;
	int i;

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
	
	opendbconnection();
	initGameFunctionIndex(); // initialise command index 
	setMMudOut(stdout); // sets the standard output stream of the mud to the filedescriptor 
	syslog(LOG_INFO, "accepting incoming connections...");
	while (!isShuttingdown())
	{
#ifdef DEBUG
		printf("[Listening on socket...]\n");
#endif
		/* more socket stuff */
		read_fds = master_fds; // copy it
		if (select(fdmax+1, &read_fds, NULL, NULL, NULL) == -1)
		{
			int i = errno;
			if (i != EINTR)
			{
				syslog(LOG_ERR, "select : %s\n", strerror(i));
				exit(8);
			}
		}
		if (isSignalCaught())
		{
			rereadConfigFiles();
		}
		// run through the existing connections looking for data to read
		for (i = 0; i <= fdmax; i++)
		{
			if (FD_ISSET(i, &read_fds))
			{
				// found one
				if (i == sockfd)
				{
					// handle new connections
					int newfd;
					int addrlen;
					int msglength;
					addrlen = sizeof(their_addr);
					if ((newfd = accept(sockfd, (struct sockaddr *)&their_addr, &addrlen)) == -1)
					{
						perror("accept");
					}
					else
					{
						FD_SET(newfd, &master_fds);	 // add to master set
						addconnection_mudinfo();
						if (newfd > fdmax) 
						{
							// keep track of the maximum file descriptor value
							fdmax = newfd;
						}
						// print stuff
						printf("New connection setup %i\n", newfd);
						msglength = strlen(IDENTITY);
						send_socket(newfd, IDENTITY, &msglength);
						if (msglength != strlen(IDENTITY))
						{
							perror("Unable to send entire message...");
						}
						add_to_list(newfd);
					}
				}
				else
				{
					// handle data from a client
					int nbytes;
					if ((nbytes = recv(i, buf, sizeof(buf), 0)) <= 0)
					{
						// got error or connection closed by client
						if (nbytes == 0)
						{
							// connection closed
							printf("Connection %i closed\n", i);
						}
						else
						{
							syslog(LOG_WARNING, "attempting to receive data from socket");
						}
						// close socket from our side as well
						if (close(i) == -1)
						{
							syslog(LOG_WARNING, "attempting to close user socket");
						}
						else
						{
							FD_CLR(i, &master_fds); // remove closed socket from master set
							remove_from_list(i);
							removeconnection_mudinfo();
						}
					}
					else
					{
						// we got some data from a client
						buf[nbytes]=0;
						if (store_in_list(i, buf) == 1)
						{
							// do nothing
						}
					}
				}
			}
		}
	}
	syslog(LOG_INFO, "shutdown initiated...");
	clearGameFunctionIndex(); // clear command index
	if (close(sockfd) == -1)
	{
		// do some error checking
		syslog(LOG_ERR, "attempting to close main socket...");
		exit(7);
	}
	while (list != NULL)
	{
		FD_CLR(list->socketfd, &master_fds); // remove closed socket from master set
		if (close(list->socketfd) == -1)
		{
			// do some error checking
			syslog(LOG_ERR, "attempting to close user socket...");
			exit(7);
		}
		remove_from_list(list->socketfd);
	}
	
	syslog(LOG_INFO, "closing database connection...");
	closedbconnection();
	
	freeParam();

	syslog(LOG_INFO, "%s: Stopped.", IDENTITY);
	
	closelog();
	
	return 0;
}
