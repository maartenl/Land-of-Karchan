/*
** client.c -- a stream socket client demo
*/

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <time.h>
#include "cgic.h"

#define PORT 3490    /* the port client will be connecting to */

#define HOST "lok.il.fontys.nl"	/* the host client will be connecting to */

#define MAXDATASIZE 100 /* max number of bytes we can get at once */

void
addLog(char *s)
{
	FILE *fp;
	time_t tijd;
	struct tm datum;
	time(&tijd);
	datum=*(gmtime(&tijd));
	fp = fopen("/home/karchan/mud/data/error.log","a");
	fprintf(fp, "[%02i:%02i:%02i %02i-%02i-%i] client: %s\n",
	datum.tm_hour,datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,s);
	fclose(fp);
}

void
stuff(char *name, char *password, char *command)
{
    int sockfd, numbytes;  
    char buf[MAXDATASIZE];
    struct hostent *he;
    struct sockaddr_in their_addr; /* connector's address information */

	addLog("Connection opened...");

    if ((he=gethostbyname(HOST)) == NULL) {  /* get the host info */
        addLog( "Unable to 'gethostbyname'");
//        exit(1);
    }

    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        addLog( "Unable to get 'socket'");
//        exit(1);
    }

    their_addr.sin_family = AF_INET;         /* host byte order */
    their_addr.sin_port = htons(PORT);     /* short, network byte order */
    their_addr.sin_addr = *((struct in_addr *)he->h_addr);
    bzero(&(their_addr.sin_zero), 8);        /* zero the rest of the struct */

    if (connect(sockfd, (struct sockaddr *)&their_addr, sizeof(struct sockaddr)) == -1) {
        addLog( "Unable to 'connect'");
//        exit(1);
    }

//	[name\npassword\nmessage\n\1]
	if (send(sockfd, name, strlen(name), 0) == -1)
		addLog( "Unable to send name");
	if (send(sockfd, "\n", 1, 0) == -1)
		addLog( "Unable to send endofline");
	if (send(sockfd, password, strlen(password), 0) == -1)
		addLog( "Unable to send password");
	if (send(sockfd, "\n", 1, 0) == -1)
		addLog( "Unable to send endofline");
	if (send(sockfd, command, strlen(command), 0) == -1)
		addLog( "Unable to send command");
	if (send(sockfd, "\1", 1, 0) == -1)
		addLog( "Unable to send endofline");

    while ((numbytes=recv(sockfd, buf, MAXDATASIZE, 0)) != -1) {
	    buf[numbytes] = '\0';

    	fprintf(cgiOut, "%s", buf);
    }
	if (numbytes==-1)
	{
		addLog( "Unable to recv");
//        exit(1);
	}

    close(sockfd);

	addLog("Connection closed...");

    return ;
}

int cgiMain()
{
	char *command, name[40], password[40];
	cgiHeaderContentType("text/html");
	fprintf(cgiOut, "<HTML><HEAD>\n");
	fprintf(cgiOut, "<TITLE>Mudclient</TITLE></HEAD>\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF><H1>Stuff</H1>\n");
	
	command = (char *) malloc(cgiContentLength);
	cgiFormString("command", command, cgiContentLength - 2);
	cgiFormString("name", name, 40);
	cgiFormString("password", password, 40);

	fprintf(cgiOut, "Name: %s<BR>\n", name);
	fprintf(cgiOut, "Password: %s<BR>\n", password);
	fprintf(cgiOut, "Message: %s<BR>\n", command);

	stuff(name, password, command);

	fprintf(cgiOut, "</BODY></HTML>\n");
}


