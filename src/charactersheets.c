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

void listSheets()
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
	int i;
	
	fprintf(cgiOut, "Content-type: text/html\n\n");
	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - Character Sheets\n");
	fprintf(cgiOut, "</TITLE>\n");
	fprintf(cgiOut, "</HEAD>\n");

	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\n");
	fprintf(cgiOut, "<H1>\n");
	fprintf(cgiOut, "<IMG SRC=\"/images/gif/dragon.gif\">Character Sheets</H1>\n");
	fprintf(cgiOut, "	I feel the following needs a little explanation. Below you see a list of");
	fprintf(cgiOut, " available Character Sheets. They contain personal information like name,");
	fprintf(cgiOut, " title, place of birth, and the story line of characters, and references to");
	fprintf(cgiOut, " other characters. In each case these");
	fprintf(cgiOut, " are put together by the people that originally created the character on the");
	fprintf(cgiOut, " game.<P>");
	fprintf(cgiOut, "It provides valuable insights into the story behind this Game.<P>");
	fprintf(cgiOut, "Now you can add your piece of information as well. Just fill in your name");
	fprintf(cgiOut, " and password of the character you created on the mud, and you will be");
	fprintf(cgiOut, " presented with a form that you can fill out, and change later in the same");
	fprintf(cgiOut, " way.<P>");
	fprintf(cgiOut, "<UL>\n");
 	
	if (!(mysql_connect(&mysql,"localhost",DatabaseLogin, DatabasePassword))) 
	{
		// error
		fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
	}
 
	if (mysql_select_db(&mysql,DatabaseName))
	{
		// error
		fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
	}
 
	if (mysql_query(&mysql,"select concat(\"<LI><A HREF=\\\"/cgi-bin/charactersheet.cgi?name=\",usertable.name,\"\\\">\",usertable.name,\"</A>\") "
	"from characterinfo, usertable "
	"where usertable.name=characterinfo.name"))
	{
		// error
		fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
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
				fprintf(cgiOut, "%s",row[0]);
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
	fprintf(cgiOut, "</UL>");
	fprintf(cgiOut, "<FORM METHOD=\"GET\" ACTION=\"/cgi-bin/editcharactersheet.cgi\">\n");
	fprintf(cgiOut, "<HR>\r\n");
	fprintf(cgiOut, "(Fictional) Name:<BR>\r\n");
	fprintf(cgiOut, "<INPUT TYPE=\"text\" NAME=\"name\" VALUE=\"\" SIZE=\"19\"\r\n");
	fprintf(cgiOut, "MAXLENGTH=\"19\"><P>\r\n");
	fprintf(cgiOut, "Password:<BR>\r\n");
	fprintf(cgiOut, "<INPUT TYPE=\"password\" NAME=\"password\" VALUE=\"\" SIZE=\"10\"\r\n");
	fprintf(cgiOut, "MAXLENGTH=\"39\"><P>\r\n");
	fprintf(cgiOut, "<P>\r\n");
	fprintf(cgiOut, "<INPUT TYPE=\"submit\" VALUE=\"Edit\">\r\n");
	fprintf(cgiOut, "<INPUT TYPE=\"reset\" VALUE=\"Clear\">\r\n");
	fprintf(cgiOut, "</FORM>\r\n");

	fprintf(cgiOut, "<A HREF=\"/karchan/chronicles/chronicles2.html\"><IMG SRC=\"/images/gif/webpic/new/buttono.gif\" BORDER=\"0\" ALT=\"Backitup!\"></A>\n");

	fprintf(cgiOut, "</BODY>\n");
	fprintf(cgiOut, "</HTML>\n");
}

int
cgiMain()
{
	listSheets();
	return 0;
}
