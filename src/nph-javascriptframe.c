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

/*! \file nph-javascriptframe.c
	\brief  cgi-binary for creating the neccessary server push for mud interface 3 */

int main(int argc, char * argv[])
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
	printf("<HEAD>\r\n");
	printf("<TITLE>Karchan</TITLE>\r\n");
	printf("</HEAD>\r\n");
	printf("\r\n");
	printf("<BODY BGCOLOR=#FFFFFF OnLoad=\"top.frames[5].location='/cgi-bin/nph-addendum.cgi?name=%s&password=%s'\">\r\n", name, password);
	printf("<SCRIPT LANGUAGE=\"JavaScript1.2\">\r\n");
	printf("<!-- Hide script from older browsers\r\n");
	printf("\r\n");
	printf("var string = \"Spul!<BR>\"\r\n");
	printf("var nieuw = 0\r\n");
	printf("var d = top.frames[1].document\r\n");
	printf("\r\n");
	printf("\r\n");
	printf("function stuffString(fstring)\r\n");
	printf("{\r\n");
	printf("	string+=fstring\r\n");
	printf("	nieuw = 1\r\n");
	printf("}\r\n");
	printf("\r\n");
	printf("function givemessage()\r\n");
	printf("{\r\n");
	printf("	alert (\"Hoihoihoi!\")\r\n");
	printf("}\r\n");
	printf("\r\n");
	printf("function test()\r\n");
	printf("{\r\n");
	printf("	if (nieuw == 1) {\r\n");
	printf("	d.writeln(string)\r\n");
	printf("	nieuw = 0\r\n");
	printf("	string = \"\"\r\n");
	printf("	}\r\n");
	printf("	setTimeout(\"test()\", 1000)\r\n");
	printf("}\r\n");
	printf("\r\n");
	printf("\r\n");
	printf("d.open()\r\n");
	printf("string = \"\"\r\n");
	printf("nieuw = 0\r\n");
	printf("test()\r\n");
	printf("// End the hiding here. -->\r\n");
	printf("</SCRIPT>\r\n");
	printf("<P>That's all, folks.\r\n");
	printf("\r\n");
	printf("</HTML>\r\n");
	fflush(stdout);
	cgi_quit();
	return 0;
}
