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
#include "cgic.h"
#include "typedefs.h"

#define debuggin 0

void showFamilyValues(MYSQL mysql, char *name)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int textlength, sqlsize, i;
	char *sqlstring;
	
	sqlstring = (char *) malloc(1400+textlength);
	sprintf(sqlstring, "select familyvalues.description, toname, characterinfo.name "
	"from family, familyvalues "
		"left join characterinfo "
		"on characterinfo.name = family.toname "
	"where family.name = '%s' and "
	"family.description = familyvalues.id", name);
	fprintf(cgiOut, "<B>Family Relations:</B><BR><UL>\n");
	if (mysql_query(&mysql,sqlstring))
	{
		// error
	}
	else // query succeeded, process any data returned by it
	{
		res = mysql_store_result(&mysql);
		if (res)// there are rows
		{
			int num_fields = mysql_num_fields(res);
			// retrieve rows, then call mysql_free_result(result)
			while ((row = mysql_fetch_row(res))!=NULL) 
			{
				if (row[2] == NULL)
				{
					fprintf(cgiOut, "<LI>%s of %s<BR>",row[0], row[1]);
				}
				else
				{
					fprintf(cgiOut, "<LI>%s of <A HREF=\"/cgi-bin/charactersheet.cgi?name=%s\">%s</A><BR>",row[0], row[1], row[1]);
				}
			}
			mysql_free_result(res);
		}
		else// mysql_store_result() returned nothing; should it have?
		{
			if(mysql_field_count(&mysql) == 0)
			{
				// query does not return data
				// (it was not a SELECT)
				// num_rows = mysql_affected_rows(&mysql);
			}
			else // mysql_store_result() should have returned data
			{
				fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
			}
		}	
	}
	free(sqlstring);
	fprintf(cgiOut, "</UL>");
}
                 
void showCharacterSheet()
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
	int textlength, sqlsize, i;
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
		"Storyline:",
		NULL};
	
	char *sqlstring, *formstring, *sqlformstring;
	
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
	cgiFormStringSpaceNeeded("name", &textlength);
	formstring = (char *) malloc(textlength);
	cgiFormString("name", formstring, textlength);
	sqlformstring = (char *) malloc(2*textlength+1+2);
	mysql_escape_string(sqlformstring,formstring,strlen(formstring));
	textlength = strlen(sqlformstring);

	sqlstring = (char *) malloc(1400+textlength);
	sprintf(sqlstring, "select usertable.name, title, sex, concat(age,"
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
	"\"Yes\", dateofbirth, cityofbirth, storyline "
	"from usertable, characterinfo "
	"where usertable.name = '%s' and "
	"usertable.name = characterinfo.name", sqlformstring);
	fprintf(cgiOut, "Content-type: text/html\n\n");
	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - %s\n", formstring);
	fprintf(cgiOut, "</TITLE>\n");
	fprintf(cgiOut, "</HEAD>\n");

	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\n");
	fprintf(cgiOut, "<H1>\n");
	fprintf(cgiOut, "<IMG SRC=\"/images/gif/dragon.gif\">Character Sheet of %s</H1>\n", sqlformstring);
	fprintf(cgiOut, "<HR>\n");
	
	//fprintf(cgiOut, "[%s]\n", sqlstring);
 	
	if (!(mysql_connect(&mysql,"localhost",DatabaseLogin, DatabasePassword))) 
	{
		fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
	}
 
	if (mysql_select_db(&mysql,DatabaseName))
	{
		fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
	}
 
	if (mysql_query(&mysql,sqlstring))
	{
		// error
	}
	else // query succeeded, process any data returned by it
	{
		res = mysql_store_result(&mysql);
		if (res)// there are rows
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
						fprintf(cgiOut, "<B>%s</B> %s<BR>",databaseitems[i],row[i]);
					}
					if (i==9) 
					{
						showFamilyValues(mysql, sqlformstring);
					}
				}
			}
			mysql_free_result(res);
		}
		else// mysql_store_result() returned nothing; should it have?
		{
			if(mysql_field_count(&mysql) == 0)
			{
				// query does not return data
				// (it was not a SELECT)
				// num_rows = mysql_affected_rows(&mysql);
			}
			else // mysql_store_result() should have returned data
			{
				fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
			}
		}	
	}
 	mysql_close(&mysql);
	free(formstring);free(sqlformstring);
	free(sqlstring);
	fprintf(cgiOut, "<HR><P><A HREF=\"/cgi-bin/charactersheets.cgi\"><IMG SRC=\"/images/gif/webpic/new/buttono.gif\" BORDER=\"0\" ALT=\"Backitup!\"></A>\n");
	fprintf(cgiOut, "</BODY>\n");
	fprintf(cgiOut, "</HTML>\n");
}

int
cgiMain()
{
	showCharacterSheet();
	return 0;
}
