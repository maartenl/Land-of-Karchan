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
#include "mudmain.h"
#include "cgic.h"

int commandlineinterface = 1;

int 
cgiMain()
{
	char *command;
	char name[22];
	char password[40];
	char frames[10];

	if (commandlineinterface) {
		command = (char *) malloc(1024);
	} else {
		command = (char *) malloc(cgiContentLength);
	}

	if (commandlineinterface) {
		printf("Command:");
		fgets(command, 1023, stdin);
		printf("Name:");
		fgets(name, 21, stdin);
		printf("Password:");
		fgets(password, 39, stdin);
		setFrames(2);
	}
	else 
	{
		char cookiepassword[40];
		cgiFormString("command", command, cgiContentLength - 2);
		if (command[0]==0) {strcpy(command,"l");}
		cgiFormString("name", name, 20);
		cgiFormString("password", password, 40);
		getCookie("Karchan", cookiepassword);
		if (strcmp(cookiepassword, password))
		{
			cgiHeaderContentType("text/html");
			NotActive(name, password,3);
		}
		if (cgiFormString("frames", frames, 10)!=cgiFormSuccess)
		{
			strcpy(frames, "none");
			setFrames(0);
		}
		if (!strcmp(frames,"1")) {setFrames(0);}
		if (!strcmp(frames,"2")) {setFrames(1);}
		if (!strcmp(frames,"3")) {setFrames(2);}
		WriteSentenceIntoOwnLogFile(BigFile,
				     "%s (%s): |%s|\n", name, password, command);

	}
	if (strcasecmp("quit", command))
	{
		cgiHeaderContentType("text/html");
	}
	else
	{
		fprintf(cgiOut, "Content-type: text/html\r\n");
		fprintf(cgiOut, "Set-cookie: Karchan=; expires= Monday, 01-January-01 00:05:00 GMT\r\n\r\n");
	}
	
	/* below starts basically the entire call to the mudEngine */
	
	initGameFunctionIndex(); // initialise command index 
	setMMudOut(cgiOut); // sets the standard output stream of the mud to the filedescriptor as provided by cgic.c
	
	gameMain(command, name, password, cgiRemoteAddr); // the main function THIS IS IT!!!
	
	clearGameFunctionIndex(); // clear command index
	
	free(command); // clear the entered command
}
