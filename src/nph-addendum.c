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
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include "cgi-util.h"
#include "typedefs.h"

/*! \file nph-addendum.c
	\brief  simple cgi-binary used for server push in mudinterface 3. */

int checkPassword(char *name, char *password)
{
MYSQL_RES *res;
MYSQL_ROW row;
char *tempsql, temp[1024];

opendbconnection();

tempsql = composeSqlStatement("select lok from tmp_usertable where name='%x' and lok<>''", name);
res=sendQuery(tempsql, NULL);
free(tempsql);
if (res==NULL)
{
	return 0;
} else
{
	row = mysql_fetch_row(res);
	if (row==NULL)
	{
		mysql_free_result(res);
		return 0;
	}
}
strcpy(temp, row[0]);
mysql_free_result(res);
closedbconnection();
return !strcmp(temp, password);
}

void WrongPasswd()
{
	printf("<html><head><Title>Error</Title></head>\n");
	printf("<body>\n");
	printf("<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Wrong Password</H1><HR>\n");
	printf("You filled out the wrong password for that particular name! \n");
	printf("Please retry by clicking at the link below:<P>\n");
	printf("<A HREF=\"http://%s/karchan/enter.html\">Click here to retry</A></body>\n", getParam(MM_SERVERNAME));
}

int main(int argc, char * argv[])
{
	char name[20];
	char password[40];
	int i, res;
	
	FILE *fp;
	char logstring[80];
	char logname[90];

	initParam();
	readConfigFiles("/karchan/config.xml");
      
//	cgiHeaderContentType("text/html");
	res = cgi_init();
	if (res != CGIERR_NONE)
	{
		printf("Content-type: text/html\n\n");
		printf("Error # %d: %s<P>\n", res, cgi_strerror(res));
		cgi_quit();
		return 1;
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

	if (!checkPassword(name, password)) 
	{
		WrongPasswd();
		cgi_quit();
		return 1;
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

	printf("top.frames[4].stuffString(\"<HTML><BODY BGCOLOR=#FFFFFF>\")\n");
	
	printf("// End the hiding here. -->\n");
	printf("</SCRIPT>\n");
	printf("<P>That's all, folks.\n");
	printf("---blaat---\n");
	fflush(stdout);
	sleep(1);
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	fp = fopen(logname, "r");
//	fseek(fp, 0, SEEK_END);
	while (1)
	{
		char temp[160];
		int i;
		if (fgets(logstring, 79, fp) != 0) 
		{
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
			printf("Content-type: text/html\n\n");

			printf("<HTML>\n");
			printf("<SCRIPT LANGUAGE=\"JavaScript1.2\">\n");
			printf("<!-- Hide script from older browsers\n\n");
	
			printf("top.frames[4].stuffString(\"%s\")\n",temp);
			printf("// End the hiding here. -->\n");
			printf("</SCRIPT>\n");
			printf("<P>That's all, folks.\n");
			printf("---blaat---\n");
		}
		else
		{
			fflush(stdout);
			sleep(1);
		}
	}
	cgi_quit();
	
	fclose(fp);
	freeParam();
	return 0;
}
