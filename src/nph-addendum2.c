/*-------------------------------------------------------------------------
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
#include "cgic.h"

int cgiMain()
{
	char name[20];
	char password[40];
	int i;
	
	FILE *fp;
	char logstring[80];
	char logname[90];

//	cgiHeaderContentType("text/html");
	
	if (0) 
	{
		printf("Name:");gets(name);
		printf("Password:");gets(password);
	}
	else 
	{
		cgiFormString("name", name, 20);
		cgiFormString("password", password, 40);
	}

	i = 0;
	fprintf(cgiOut, "HTTP/1.0 200\n");
	cgiHeaderContentType("multipart/mixed;boundary=---blaat---");
//	fprintf(cgiOut, "Content-type: multipart/mixed;boundary=---blaat---\n\n");
	fprintf(cgiOut, "---blaat---\n");
	fprintf(cgiOut, "Content-type: text/html\n\n");

	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<SCRIPT LANGUAGE=\"JavaScript1.2\">\n");
	fprintf(cgiOut, "<!-- Hide script from older browsers\n\n");

	fprintf(cgiOut, "top.frames[1].stuffString(\"<HTML><BODY BGCOLOR=#FFFFFF>\")\n");
	
	fprintf(cgiOut, "// End the hiding here. -->\n");
	fprintf(cgiOut, "</SCRIPT>\n");
	fprintf(cgiOut, "<P>That's all, folks %i.\n");
	fprintf(cgiOut, "---blaat---\n");
	fflush(stdout);
	sleep(1);
	sprintf(logname, "%s%s.log","/home/karchan/mud/tmp/",name);
	fp = fopen(logname, "r");
//	fseek(fp, 0, SEEK_END);
	while (1)
	{
		char temp[160];
		int i;
		if (fgets(logstring, 79, fp) != 0) {
		logstring[strlen(logstring)-1]=0;
		*temp=0;
		for (i=0l;i<strlen(logstring);i++)
		{
			if (logstring[i]=='\r')
			{
				strncat(temp, "\\r", 5);
			}
			else
			{
				if (logstring[i]=='"')
				{
					strncat(temp, "\\\"", 5);
				}
				else
				{
					strncat(temp, logstring+i, 1);
				}
			}
		}
			fprintf(cgiOut, "Content-type: text/html\n\n");

			fprintf(cgiOut, "<HTML>\n");
			fprintf(cgiOut, "<SCRIPT LANGUAGE=\"JavaScript1.2\">\n");
			fprintf(cgiOut, "<!-- Hide script from older browsers\n\n");

			fprintf(cgiOut, "top.frames[1].stuffString(\"%s\")\n",temp);
			fprintf(cgiOut, "// End the hiding here. -->\n");
			fprintf(cgiOut, "</SCRIPT>\n");
			fprintf(cgiOut, "<P>That's all, folks.\n");
			fprintf(cgiOut, "---blaat---\n");
			fflush(stdout);
		}
		sleep(1);
	}

	fclose(fp);
	return 0;
}
