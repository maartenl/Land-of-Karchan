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

char *retrieveCgiValue(const char *fieldname)
{
	char *result, *formstring;
	int textlength;
	cgiFormStringSpaceNeeded(fieldname, &textlength);
	formstring = (char *) malloc(textlength);
	cgiFormString(fieldname, formstring, textlength);
	result = (char *) malloc(2*textlength+1+2);
	mysql_escape_string(result,formstring,strlen(formstring));
	textlength = strlen(result);
	return result;
}

void updateCharacterSheet(char *name, char *password)
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
	int textlength, sqlsize, i;
	char *sqlstring;
	char *image, *homepage, *dateofbirth, *cityofbirth, *storyline;
	char *family, *familyname;
	char *sqlstring2;
	int familyid;
	image = retrieveCgiValue("imageurl");
	homepage = retrieveCgiValue("homepageurl");
	dateofbirth = retrieveCgiValue("dateofbirth");
	cityofbirth = retrieveCgiValue("cityofbirth");
	storyline = retrieveCgiValue("storyline");
	family = retrieveCgiValue("family");
	familyid = atoi(family);
	free(family);
	if (familyid != 0) 
	{
		family = retrieveCgiValue("familyname");
		sqlstring2 = (char *) malloc(200+strlen(name)+strlen(family)+3);
		sprintf(sqlstring2, "replace into family values(\"%s\",\"%s\",%i)", name, family, familyid);
	}
	sqlstring = (char *) malloc(200+strlen(name)+strlen(image)+strlen(homepage)+strlen(dateofbirth)+strlen(cityofbirth)+strlen(storyline));
	sprintf(sqlstring, "replace into characterinfo "
		"values(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")", name, image, homepage, dateofbirth, cityofbirth, storyline);
	// fprintf(cgiOut, "[%s]\n", sqlstring);
	free(image);free(homepage);free(dateofbirth);free(cityofbirth);free(storyline);
 	
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
		fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
	}
	if (familyid != 0)
	{
		if (mysql_query(&mysql,sqlstring2))
		{
			// error
			fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
		}
		free(sqlstring2);
	}
 	mysql_close(&mysql);
	free(sqlstring);
}

void validateUser()
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
	int textlength, sqlsize, i;
	int success = 0;
	char *sqlstring, *formstring, *name, *password;
	
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
	name = (char *) malloc(2*textlength+1+2);
	mysql_escape_string(name,formstring,strlen(formstring));
	textlength = strlen(name);

	cgiFormStringSpaceNeeded("password", &textlength);
	formstring = (char *) malloc(textlength);
	cgiFormString("password", formstring, textlength);
	password = (char *) malloc(2*textlength+1+2);
	mysql_escape_string(password,formstring,strlen(formstring));
	textlength = strlen(password);

	sqlstring = (char *) malloc(100+strlen(name)+strlen(password));
	sprintf(sqlstring, "select * "
	"from usertable "
	"where usertable.name = '%s' and "
	"usertable.password = '%s' and "
	"usertable.god<2", name, password);
	fprintf(cgiOut, "Content-type: text/html\n\n");
	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - %s\n", name);
	fprintf(cgiOut, "</TITLE>\n");
	fprintf(cgiOut, "</HEAD>\n");

	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\n");
	fprintf(cgiOut, "<H1>\n");
	fprintf(cgiOut, "<IMG SRC=\"/images/gif/dragon.gif\">Character Sheets</H1>\n");
	fprintf(cgiOut, "\n");
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
			if (row == NULL)
			{
				success = 0;
				fprintf(cgiOut, "Error retrieving character sheet. Either Character does not exist or password is incorrect...<P>\r\n");
			}
			else
			{
				success=1;
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
	free(sqlstring);
	if (success) 
	{
		updateCharacterSheet(name, password);
	}
	fprintf(cgiOut, "Form information has been submitted.<P>\n");
	fprintf(cgiOut, "Please click <A HREF=\"charactersheet.cgi?name=%s\">", name);
	fprintf(cgiOut, "Character Sheet Info</A> to view the submitted information.<P>");
	free(name);free(password);
	fprintf(cgiOut, "<A HREF=\"charactersheets.cgi\"><IMG SRC=\"/images/gif/webpic/new/buttono.gif\" BORDER=\"0\" ALT=\"Backitup!\"></A>\n");   
	fprintf(cgiOut, "</BODY>\n");
	fprintf(cgiOut, "</HTML>\n");
}

int
cgiMain()
{
	validateUser();
	return 0;
}
