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

/*! \file charactersheet.c
	\brief  cgi-binary to show the character sheet of a player */

#define debuggin 0

void showFamilyValues(const char *name)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	
	res = executeQuery(NULL, "select familyvalues.description, toname, characterinfo.name "
	"from family, familyvalues "
		"left join characterinfo "
		"on characterinfo.name = family.toname "
	"where family.name = '%x' and "
	"family.description = familyvalues.id", name);
	printf("<B>Family Relations:</B><BR><UL>\n");
	if (res)// there are rows
	{
		// retrieve rows, then call mysql_free_result(result)
		while ((row = mysql_fetch_row(res))!=NULL) 
		{
			if (row[2] == NULL)
			{
				printf("<LI>%s of %s<BR>",row[0], row[1]);
			}
			else
			{
				printf("<LI>%s of <A HREF=\"charactersheet.cgi?name=%s\">%s</A><BR>",row[0], row[1], row[1]);
			}
		}
		mysql_free_result(res);
	}
	printf("</UL>");
}
                 
void showCharacterSheet()
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *databaseitems[] = 
		{"Name:",
		"Title",
		"Sex:",
		"Description:",
		"Image:",
		"Member of:",
		"Homepage:",
		"Born:",
		"Born When:",
		"Born Where:",
		"Last logged on:",
		"Storyline:",
		NULL};
	
//	char *sqlstring, *formstring, *sqlformstring;
	
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
	
	opendbconnection();
	res = executeQuery(NULL,
	"select usertable.name, title, sex, concat(age,"
	"if(length = 'none', '', concat(', ',length)),"
	"if(width = 'none', '', concat(', ',width)),"
	"if(complexion = 'none', '', concat(', ',complexion)),"
	"if(eyes = 'none', '', concat(', ',eyes)),"
	"if(face = 'none', '', concat(', ',face)),"
	"if(hair = 'none', '', concat(', ',hair)),"
	"if(beard = 'none', '', concat(', ',beard)),"
	"if(arm = 'none', '', concat(', ',arm)),"
	"if(leg = 'none', '', concat(', ',leg)),"
	"' ', sex, ' ', race), "
	"concat('<IMG SRC=\"',imageurl,'\">'), "
	"guild, "
	"concat('<A HREF=\"',homepageurl,'\">',homepageurl,'</A>'), "
	"\"Yes\", dateofbirth, cityofbirth, usertable.lastlogin, storyline "
	"from usertable, characterinfo "
	"where usertable.name = '%x' and "
	"usertable.name = characterinfo.name", cgi_getentrystr("name"));
	printf("Content-type: text/html\n\n");
	printf("<HTML>\n");
	printf("<HEAD>\n");
	printf("<TITLE>\n");
	printf("Land of Karchan - %s\n", cgi_getentrystr("name"));
	printf("</TITLE>\n");
	printf("</HEAD>\n");

	printf("<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\n");
	printf("<H1>\n");
	printf("<IMG SRC=\"/images/gif/dragon.gif\">Character Sheet of %s</H1>\n", cgi_getentrystr("name"));
	printf("<HR>\n");
	
	//printf("[%s]\n", sqlstring);
	if (res != NULL) // query succeeded, process any data returned by it
	{
		int num_fields = mysql_num_fields(res);
		// retrieve rows, then call mysql_free_result(result)
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			int i;
			for (i=0;i<num_fields;i++)
			{
				if ((strcmp("<IMG SRC=\"http://\">", row[i])) &&
				   (strcmp("<IMG SRC=\"\">", row[i]))) 
				{
					printf("<B>%s</B> %s<BR>",databaseitems[i],row[i]);
				}
				if (i==9) 
				{
					showFamilyValues(cgi_getentrystr("name"));
				}
			}
		}
		mysql_free_result(res);
	}
	closedbconnection();

	printf("<HR><P><A HREF=\"charactersheets.cgi\"><IMG SRC=\"/images/gif/webpic/new/buttono.gif\" BORDER=\"0\" ALT=\"Backitup!\"></A>\n");
	printf("</BODY>\n");
	printf("</HTML>\n");
}

int
main(int argc, char * argv[])
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

	showCharacterSheet();
	
	cgi_quit();
	freeParam();
	return 0;
}
