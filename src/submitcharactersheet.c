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
#include "cgi-util.h"
#include "typedefs.h"

/*! \file cgi-binary for submitting a character sheet */

#define debuggin 0

void updateCharacterSheet(const char *name, const char *password)
{
	MYSQL_RES *res;
	MYSQL_ROW row;

	if (atoi(cgi_getentrystr("family")) != 0) 
	{
		mysql_free_result(executeQuery(NULL, "replace into family values(\"%x\",\"%x\",%i)", 
			name, cgi_getentrystr("familyname"), atoi(cgi_getentrystr("family"))));
	}
	mysql_free_result(executeQuery(NULL, "replace into characterinfo "
		"values(\"%x\", \"%x\", \"%x\", \"%x\", \"%x\", \"%x\")", 
		name, cgi_getentrystr("imageurl"), cgi_getentrystr("homepageurl"), cgi_getentrystr("dateofbirth"),
		cgi_getentrystr("cityofbirth"), cgi_getentrystr("storyline")));
 	
}

void validateUser()
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int success = 0;
	
	res = executeQuery(NULL, "select * "
	"from usertable "
	"where usertable.name = '%x' and "
	"usertable.password = '%x' and "
	"usertable.god<2", cgi_getentrystr("name"), cgi_getentrystr("password"));
	printf("Content-type: text/html\n\n");
	printf("<HTML>\n");
	printf("<HEAD>\n");
	printf("<TITLE>\n");
	printf("Land of Karchan - %s\n", cgi_getentrystr("name"));
	printf("</TITLE>\n");
	printf("</HEAD>\n");

	printf("<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\n");
	printf("<H1>\n");
	printf("<IMG SRC=\"/images/gif/dragon.gif\">Character Sheets</H1>\n");
	printf("\n");
	//printf("[%s]\n", sqlstring);
 	
	if (res)// there are rows
	{
		int num_fields = mysql_num_fields(res);
		// retrieve rows, then call mysql_free_result(result)
		row = mysql_fetch_row(res);
		if (row == NULL)
		{
			success = 0;
			printf("Error retrieving character sheet. Either Character does not exist or password is incorrect...<P>\r\n");
		}
		else
		{
			success=1;
		}
		mysql_free_result(res);
	}
	if (success) 
	{
		updateCharacterSheet(cgi_getentrystr("name"), cgi_getentrystr("password"));
	}
	printf("Form information has been submitted.<P>\n");
	printf("Please click <A HREF=\"charactersheet.cgi?name=%s\">", cgi_getentrystr("name"));
	printf("Character Sheet Info</A> to view the submitted information.<P>");
	printf("<A HREF=\"charactersheets.cgi\"><IMG SRC=\"/images/gif/webpic/new/buttono.gif\" BORDER=\"0\" ALT=\"Backitup!\"></A>\n");   
	printf("</BODY>\n");
	printf("</HTML>\n");
}

int
main(int argc, char *argv[])
{
	int res;
	initParam();
	readConfigFiles("/karchan/config.xml");
      
	res = cgi_init();
	if (res != CGIERR_NONE)
	{
		printf("Content-type: text/html\n\n");
		printf("Error # %d: %s<P>\n", res, cgi_strerror(res));
		cgi_quit();
		exit(1);
	}
	
	opendbconnection();
	validateUser();
	closedbconnection();
	cgi_quit();
	freeParam();
	return 0;
}
