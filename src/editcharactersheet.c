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

#define debuggin 0

/*! \file editcharactersheet.c
	\brief  simple cgi-binary used for editing your charactersheet. */

void showFamilyValues(const char *name, const char *password)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	
	res = executeQuery(NULL, "select * from familyvalues");
	if (res)// there are rows
	{
		int num_fields = mysql_num_fields(res);
		// retrieve rows, then call mysql_free_result(result)
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			// full character sheet
			printf("(<I>Make sure you spell the name of the ");
			printf("familymember correctly, otherwise the corresponding ");
			printf("charactersheet can not be obtained.</I>)<BR>");
			printf("Add family relation:\r\n");
			printf("<SELECT NAME=\"family\">\r\n");
			printf("<option selected value=\"0\">None");
			while (row != NULL)
			{
				printf("<option value=\"%s\">%s", row[0], row[1]);
				row = mysql_fetch_row(res);
			}
			printf("</SELECT>of ");
			printf("<INPUT TYPE=\"text\" NAME=\"familyname\" VALUE=\"\" SIZE=\"50\" MAXLENGTH=\"40\"><P>");
		}
		else
		{
			// empty family values, impossible
			fprintf(stderr, "Error: No family values present in table...\n");
		}
		mysql_free_result(res);
	}
}


void showCharacterSheet(const char *name, const char *password)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int textlength, sqlsize, i;
	char *sqlstring;
	
	res = executeQuery(NULL, "select *, replace(replace(replace(storyline, '&','&amp;'),'<', '&lt;'), '>', '&gt;') "
	"from characterinfo "
	"where name = '%x'", name);

	if (res)// there are rows
	{
		int num_fields = mysql_num_fields(res);
		// retrieve rows, then call mysql_free_result(result)
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			// full character sheet
			printf("<FORM METHOD=\"GET\" ACTION=\"submitcharactersheet.cgi\">");
			printf("<HR>");
			printf("Homepage Url:<BR>");
			printf("<INPUT TYPE=\"text\" NAME=\"homepageurl\" VALUE=\"%s\" SIZE=\"50\" MAXLENGTH=\"253\"><P>", row[2]);
			printf("Image Url:<BR>");
			printf("<INPUT TYPE=\"text\" NAME=\"imageurl\" VALUE=\"%s\" SIZE=\"50\" MAXLENGTH=\"253\"><P>", row[1]);
			printf("Date of Birth: ");
			printf("<INPUT TYPE=\"text\" NAME=\"dateofbirth\" VALUE=\"%s\" SIZE=\"20\" MAXLENGTH=\"253\"><P>", row[3]);
			printf("City of Birth:");
			printf("<INPUT TYPE=\"text\" NAME=\"cityofbirth\" VALUE=\"%s\" SIZE=\"20\" MAXLENGTH=\"253\"><P>", row[4]);
			printf("Original storyline was...<BR><TABLE><TR><TD><TT>%s</TT></TD></TR></TABLE>\r\n", row[6]);
			printf("Storyline:<BR>");
			printf("<TEXTAREA NAME=\"storyline\" VALUE=\"\" ROWS=\"30\" COLS=\"85\"></TEXTAREA><P>");
			showFamilyValues(name, password);
 				printf("<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
			printf("<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
 				printf("<INPUT TYPE=\"submit\" VALUE=\"Submit\">\r\n");
			printf("<INPUT TYPE=\"reset\" VALUE=\"Clear\">\r\n");
			printf("</FORM>\r\n");
		}
		else
		{
			// empty character sheet
			printf("<FORM METHOD=\"GET\" ACTION=\"submitcharactersheet.cgi\">");
			printf("<HR>");
			printf("Homepage Url:<BR>");
			printf("<INPUT TYPE=\"text\" NAME=\"homepageurl\" VALUE=\"http://\" SIZE=\"50\" MAXLENGTH=\"253\"><P>");
			printf("Image Url:<BR>");
			printf("<INPUT TYPE=\"text\" NAME=\"imageurl\" VALUE=\"http://\" SIZE=\"50\" MAXLENGTH=\"253\"><P>");
			printf("Date of Birth: ");
			printf("<INPUT TYPE=\"text\" NAME=\"dateofbirth\" VALUE=\"\" SIZE=\"20\" MAXLENGTH=\"253\"><P>");
			printf("City of Birth:");
			printf("<INPUT TYPE=\"text\" NAME=\"cityofbirth\" VALUE=\"\" SIZE=\"20\" MAXLENGTH=\"253\"><P>");
			printf("Storyline:<BR>");
			printf("<TEXTAREA NAME=\"storyline\" VALUE=\"\" ROWS=\"30\" COLS=\"85\"></TEXTAREA><P>");
			showFamilyValues(name, password);
 				printf("<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
			printf("<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
 				printf("<INPUT TYPE=\"submit\" VALUE=\"Submit\">\r\n");
			printf("<INPUT TYPE=\"reset\" VALUE=\"Clear\">\r\n");
			printf("</FORM>\r\n");
		}
		mysql_free_result(res);
	}
}

void validateUser()
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int textlength, sqlsize, i;
	int success = 0;
	
	res = executeQuery(NULL, "select * from usertable where usertable.name = '%x' and usertable.password = '%x'", 
		cgi_getentrystr("name"), cgi_getentrystr("password"));
	printf("Content-type: text/html\n\n");
	printf("<HTML>\n");
	printf("<HEAD>\n");
	printf("<TITLE>\n");
	printf("Land of Karchan - %s\n", cgi_getentrystr("name"));
	printf("</TITLE>\n");
	printf("</HEAD>\n");

	printf("<BODY BGCOLOR=#FFFFFF  BACKGROUND=\"/images/gif/webpic/back4.gif\">\n");
	printf("<H1>\n");
	printf("<IMG SRC=\"/images/gif/dragon.gif\">Edit Character Sheet of %s</H1>\n", cgi_getentrystr("name"));
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
		showCharacterSheet(cgi_getentrystr("name"), cgi_getentrystr("password"));
	}
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
