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

/*! \file charactersheets.c
	\brief  simple cgi-binary used for displaying a html page with all characters that have filled out a charactersheet.
*/

void listSheets()
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
	
	printf("Content-type: text/html\n\n");
	printf("<HTML>\n");
	printf("<HEAD>\n");
	printf("<TITLE>\n");
	printf("Land of Karchan - Character Sheets\n");
	printf("</TITLE>\n");
	printf("</HEAD>\n");

	printf("<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\n");
	printf("<H1>\n");
	printf("<IMG SRC=\"/images/gif/dragon.gif\">Character Sheets</H1>\n");
	printf("	I feel the following needs a little explanation. Below you see a list of");
	printf(" available Character Sheets. They contain personal information like name,");
	printf(" title, place of birth, and the story line of characters, and references to");
	printf(" other characters. In each case these");
	printf(" are put together by the people that originally created the character on the");
	printf(" game.<P>");
	printf("It provides valuable insights into the story behind this Game.<P>");
	printf("Now you can add your piece of information as well. Just fill in your name");
	printf(" and password of the character you created on the mud, and you will be");
	printf(" presented with a form that you can fill out, and change later in the same");
	printf(" way.<P>");
	printf("\n");
 	
	if (!(mysql_connect(&mysql,getParam(MM_DATABASEHOST),getParam(MM_DATABASELOGIN), getParam(MM_DATABASEPASSWORD)))) 
	{
		// error
		fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
	}
 
	if (mysql_select_db(&mysql, getParam(MM_DATABASENAME)))
	{
		// error
		fprintf(stderr, "Error: %s\n", mysql_error(&mysql));
	}
 
	if (mysql_query(&mysql,"select concat(\"<A HREF=\\\"charactersheet.cgi?name=\",usertable.name,\"\\\">\",usertable.name,\"</A>\") "
	", usertable.name from characterinfo, usertable "
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
			int num_rows = mysql_num_rows(res);
			char beginstuff = 'x';
			int counter = 1;
			num_rows = num_rows / 5;
			// retrieve rows, then call mysql_free_result(result);
			printf("<TABLE ALIGN=top BORDER=1><TR><TD>");
			while ((row = mysql_fetch_row(res))!=NULL)
			{
				if (toupper(row[1][0]) != beginstuff)
				{
					beginstuff = toupper(row[1][0]);
					printf("</UL><H1>%c</H1><UL>\r\n", beginstuff);
				}
				printf("%s<BR>",row[0]);
				if (counter++>num_rows)
				{
					printf("</TD>\r\n<TD>");
					counter=1;
				}
				
			}
			printf("</TR></TABLE>\r\n");
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
	printf("</UL>");
	printf("<FORM METHOD=\"GET\" ACTION=\"editcharactersheet.cgi\">\n");
	printf("<HR>\r\n");
	printf("(Fictional) Name:<BR>\r\n");
	printf("<INPUT TYPE=\"text\" NAME=\"name\" VALUE=\"\" SIZE=\"19\"\r\n");
	printf("MAXLENGTH=\"19\"><P>\r\n");
	printf("Password:<BR>\r\n");
	printf("<INPUT TYPE=\"password\" NAME=\"password\" VALUE=\"\" SIZE=\"10\"\r\n");
	printf("MAXLENGTH=\"39\"><P>\r\n");
	printf("<P>\r\n");
	printf("<INPUT TYPE=\"submit\" VALUE=\"Edit\">\r\n");
	printf("<INPUT TYPE=\"reset\" VALUE=\"Clear\"><P>\r\n");
	printf("</FORM>\r\n");

	printf("<A HREF=\"/karchan/chronicles/chronicles2.html\"><IMG SRC=\"/images/gif/webpic/new/buttono.gif\" BORDER=\"0\" ALT=\"Backitup!\"></A>\n");

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

	listSheets();
	cgi_quit();
	freeParam();
	return 0;
}
