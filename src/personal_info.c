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
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <stdarg.h>
#include <sys/file.h>
#include "cgi-util.h"
#include "mysql.h"

#define debuggin 0

void exiterr(int exitcode, char *sqlstring, MYSQL *mysql)
{
printf("Error %i: %s\n {%s}\n", exitcode, mysql_error(mysql), sqlstring );
}

int SendSQL(char *sqlstring)
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
	FILE *fp;
	uint i = 0;
	
	if (!(mysql_connect(&mysql,"localhost","root", ""))) 
		exiterr(1, sqlstring, &mysql);
 
	if (mysql_select_db(&mysql,"mud"))
		exiterr(2, sqlstring, &mysql);
 
	if (mysql_query(&mysql,sqlstring))
		exiterr(3, sqlstring, &mysql);
 
	mysql_close(&mysql);
}

void InsertPersonalInfo()
{
	int textlength, sqlsize, i;
	char *sqlstring, *formstring, *sqlformstring;
	char *formitems[] =
		{"RealName",
		"EMAIL",
		"myrealage",
		"mysex",
		"mycountry",
		"myoccupation",
		"myinternal",
		"myexternal",
		"mycomments",
		NULL};
	
	sqlstring = (char *) malloc(48);
	sqlsize=48;
	strcpy(sqlstring, "insert into private_info values (null, now(), '");
	
	for (i = 0; formitems[i] != NULL; i++)
	{
		/*- put length of cgientry in 'textlength'
			- allocate 'textlength' memory to formstring
			- put cgientry in 'formstring'
			- allocate double memory to sqlformstring
			- put safesql formstring in 'sqlformstring'
			- textlength = length of sqlformstring
			- reallocate memory for sqlstring
			- add sqlformstring to sqlstring
			- free everything and add to sqlsize
		*/
		textlength = strlen(cgi_getentrystr(formitems[i]))+1;
		formstring = (char *) malloc(textlength);
		strcpy(formstring, cgi_getentrystr(formitems[i]));
		sqlformstring = (char *) malloc(2*textlength+1+2);
		mysql_escape_string(sqlformstring,formstring,strlen(formstring));
		if (formitems[i+1] == NULL)
		{
			strcat(sqlformstring,"')");
		}
		else
		{
			strcat(sqlformstring,"','");
		}
		textlength = strlen(sqlformstring);
		sqlstring = (char *) realloc(sqlstring, sqlsize + textlength);
		strcat(sqlstring, sqlformstring);
		sqlsize += textlength;
		//printf("[%s][%s]<BR>\n", formstring, sqlformstring);
		free(formstring);free(sqlformstring);
	}
//	printf("[%s]", sqlstring);
	SendSQL(sqlstring);
	free(sqlstring);
}

void WriteExitScreen()
{
	printf("Content-type: text/html\n\n");
	printf("<HTML>\n");
	printf("<HEAD>\n");
	printf("<TITLE>\n");
	printf("Land of Karchan - Thank you\n");
	printf("</TITLE>\n");
	printf("</HEAD>\n");
	
	printf("<BODY>\n");
	printf("<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" >\n");
	printf("<H1>");
	printf("<IMG SRC=\"http://www.karchan.org/images/gif/dragon.gif\">");
	printf("Thank you for your info</H1>\r\n");

	printf("</BODY>\r\n");
	printf("</HTML>\r\n");
	fflush(stdout);
}

int
main(int argc, char *argv[])
{
	int res;
	res = cgi_init();
	if (res != CGIERR_NONE)
	{
		printf("Content-type: text/html\n\n");
		printf("Error # %d: %s<P>\n", res, cgi_strerror(res));
		cgi_quit();
		exit(1);
	}
	
	
	InsertPersonalInfo();
	WriteExitScreen();
	cgi_quit();
	return 0;
}
