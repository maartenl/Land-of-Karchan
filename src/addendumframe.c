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
#include <string.h>
#include "cgi-util.h"

/*! \file simple cgi-binary used for server push in mudinterface 3. */

int main(int argc, char * argv[])
{
	char name[20];
	char password[40];
	int i, res;

//	cgiHeaderContentType("text/html");
	res = cgi_init();
	if (res != CGIERR_NONE)
	{
		printf("Content-type: text/html\n\n");
		printf("Error # %d: %s<P>\n", res, cgi_strerror(res));
		cgi_quit();
		exit(1);
	}
	
	if (0) 
	{
		printf("Name:");gets(name);
		printf("Password:");gets(password);
	}
	else 
	{
		strncpy(name, cgi_getentrystr("name"), 19);name[19]=0;
		strncpy(password, cgi_getentrystr("password"), 39);password[39]=0;
	}

	i = 0;
	printf("HTTP/1.0 200\n");
//	cgiHeaderContentType("multipart/mixed;boundary=---blaat---");
	printf("Content-type: multipart/mixed;boundary=---blaat---\n\n");
	printf("---blaat---\n");
	printf("Content-type: text/html\n\n");

	printf("<HTML>\n");
	printf("<SCRIPT LANGUAGE=\"JavaScript1.2\">\n");
	printf("<!-- Hide script from older browsers\n\n");

	printf("top.frames[1].stuffString(\"<HTML><BODY BGCOLOR=#FFFFFF>\")\n");
	printf("// End the hiding here. -->\n");
	printf("</SCRIPT>\n");
	printf("<P>That's all, folks %i.\n");
	printf("---blaat---\n");
	fflush(stdout);
	sleep(1);
	while (1)
	{
		printf("Content-type: text/html\n\n");

		printf("<HTML>\n");
		printf("<SCRIPT LANGUAGE=\"JavaScript1.2\">\n");
		printf("<!-- Hide script from older browsers\n\n");

		printf("top.frames[1].stuffString(\"%s, %s<BR>\")\n",name, password);
		printf("// End the hiding here. -->\n");
		printf("</SCRIPT>\n");
		printf("<P>That's all, folks.\n");
		printf("---blaat---\n");
		fflush(stdout);
		sleep(5);
	}

	cgi_quit();
	return 0;
}
