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
#include <string.h>
#include "../include/typedefs.h"
#include "cgic.h"

int cgiMain()
{
	char name[20];
	char password[40];
	int i;

//	cgiHeaderContentType("text/html");
	
	if (0) 
	{
		printf("Name:");gets(name);
		printf("Password:");gets(password);
	}
	else 
	{
		cgiFormString("name", name, 20);
		cgiFormString("password", password, 40);
	}

	fprintf(cgiOut, "<HTML>\r\n");
	fprintf(cgiOut, "<HEAD>\r\n");
	fprintf(cgiOut, "<TITLE>Karchan</TITLE>\r\n");
	fprintf(cgiOut, "</HEAD>\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF OnLoad=\"top.frames[5].location='http://www.karchan.org/cgi-bin/nph-addendum.cgi?name=%s&password=%s'\">\r\n", name, password);
	fprintf(cgiOut, "<SCRIPT LANGUAGE=\"JavaScript1.2\">\r\n");
	fprintf(cgiOut, "<!-- Hide script from older browsers\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "var string = \"Spul!<BR>\"\r\n");
	fprintf(cgiOut, "var nieuw = 0\r\n");
	fprintf(cgiOut, "var d = top.frames[1].document\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "function stuffString(fstring)\r\n");
	fprintf(cgiOut, "{\r\n");
	fprintf(cgiOut, "	string+=fstring\r\n");
	fprintf(cgiOut, "	nieuw = 1\r\n");
	fprintf(cgiOut, "}\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "function givemessage()\r\n");
	fprintf(cgiOut, "{\r\n");
	fprintf(cgiOut, "	alert (\"Hoihoihoi!\")\r\n");
	fprintf(cgiOut, "}\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "function test()\r\n");
	fprintf(cgiOut, "{\r\n");
	fprintf(cgiOut, "	if (nieuw == 1) {\r\n");
	fprintf(cgiOut, "	d.writeln(string)\r\n");
	fprintf(cgiOut, "	nieuw = 0\r\n");
	fprintf(cgiOut, "	string = \"\"\r\n");
	fprintf(cgiOut, "	}\r\n");
	fprintf(cgiOut, "	setTimeout(\"test()\", 1000)\r\n");
	fprintf(cgiOut, "}\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "d.open()\r\n");
	fprintf(cgiOut, "string = \"\"\r\n");
	fprintf(cgiOut, "nieuw = 0\r\n");
	fprintf(cgiOut, "test()\r\n");
	fprintf(cgiOut, "// End  the hiding here. -->\r\n");
	fprintf(cgiOut, "</SCRIPT>\r\n");
	fprintf(cgiOut, "<P>That's all, folks.\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "</HTML>\r\n");
	fflush(cgiOut);
	
	return 0;
}
