/*-------------------------------------------------------------------------
cvsinfo: $Header$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/
#include <stdio.h>
#include <string.h>
#include "cgi-util.h"

/*! \file cgi-binary showing the logon frame/html page on which it is possible to submit commands */

int main(int argc, char *argv[])
{
	char name[20];
	char password[40];
	int i, res;

//	cgiHeaderContentType("text/html");
	res = cgi_init();
	if (res != CGIERR_NONE)
	{
		printf("Content-type: text/html\n\n");
		printf("Error # %d: %s<P>\n", res, cgi_strerror(res));
		cgi_quit();
		exit(1);
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

	printf("<HTML>\r\n");
	printf("<BODY BGCOLOR=#FFFFFF>\r\n");
	printf("<DIV ALIGN=CENTER>\r\n");
	printf("<FORM METHOD=\"GET\" ACTION=\"/cgi-bin/mud.cgi\" NAME=\"myForm\" TARGET=\"statusFrame\">\r\n");
	printf("<INPUT TYPE=\"text\" NAME=\"command\" SIZE=\"60\" VALUE=\"\">\r\n");
	printf("<INPUT TYPE=\"submit\" VALUE=\"Submit\" onClick='document.myForm.command.command=\"\"'>\r\n");
	printf("<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\r\n", name);
	printf("<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\r\n", password);
	printf("<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"3\">\r\n");
	printf("<P>\r\n");
	printf("</FORM>\r\n");
	printf("</DIV>\r\n");
	printf("</BODY>\r\n");
	printf("</HTML>\r\n");
	fflush(stdout);
	cgi_quit();
	return 0;
}
