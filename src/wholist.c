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
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <stdarg.h>
#include <sys/file.h>
#include "typedefs.h"

/*! \file wholist.c
	\brief  simple cgi-binary, shows all users active in the game. */

int showWhoList()
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	
	printf("Content-type: text/html\n\n");
	printf("<META http-equiv=\"refresh\" content=\"120\">");
	printf("<META HTTP-EQUIV=\"pragma\" CONTENT=\"no-cache\">\r\n\r\n");
	printf("<HTML>\n");
	printf("<HEAD>\n");
	printf("<TITLE>\n");
	printf("Land of Karchan - Who\n");
	printf("</TITLE>\n");
	printf("</HEAD>\n");
	
	printf("<BODY>\n");
	printf("<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" >\n");
	printf("<H2>");
	printf("<IMG SRC=\"/images/gif/dragon.gif\">");
	printf("List of All Active Users</H2>\r\n");
	opendbconnection();
	res = executeQuery(NULL, "select name, title, "
	"time_to_sec(NOW())-time_to_sec(lastlogin)"
	", sleep from tmp_usertable where god<=1");

	if (res != NULL)
	{
		printf("<I>There are %i persons active in the game.</I><P>\r\n", (int) mysql_num_rows(res));
		printf("<UL>");
		while((row = mysql_fetch_row(res))) 
		{
			if (atoi(row[3])==1)
			{
				printf("<LI>%s, %s, sleeping (%i min, %i sec idle)\r\n", 
				row[0], row[1], atoi(row[2]) / 60, atoi(row[2]) % 60);
			}
			else
			{
				printf("<LI>%s, %s (%i min, %i sec idle)\r\n", 
				row[0], row[1], atoi(row[2]) / 60, atoi(row[2]) & 60);
			}
		}
		mysql_free_result(res);
	}
	else
	{
		printf("Error connecting to the database.");
	}
	printf("</UL>\r\n");

	printf("</BODY>\r\n");
	printf("</HTML>\r\n");
	closedbconnection();
}

int
main()
{
	initParam();
	readConfigFiles("/karchan/config.xml");
	showWhoList();
	freeParam();
	return 0;
}
