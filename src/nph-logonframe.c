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
#include "typedefs.h"
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
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF>\r\n");
	fprintf(cgiOut, "<DIV ALIGN=CENTER>\r\n");
	fprintf(cgiOut, "<FORM METHOD=\"GET\" ACTION=\"%s\" NAME=\"myForm\" TARGET=\"statusFrame\">\r\n", MudExe);
	fprintf(cgiOut, "<INPUT TYPE=\"text\" NAME=\"command\" SIZE=\"60\" VALUE=\"\">\r\n");
	fprintf(cgiOut, "<INPUT TYPE=\"submit\" VALUE=\"Submit\" onClick='document.myForm.command.command=\"\"'>\r\n");
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\r\n", name);
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\r\n", password);
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"3\">\r\n");
	fprintf(cgiOut, "<P>\r\n");
	fprintf(cgiOut, "</FORM>\r\n");
	fprintf(cgiOut, "</DIV>\r\n");
	fprintf(cgiOut, "</BODY>\r\n");
	fprintf(cgiOut, "</HTML>\r\n");

	return 0;
}
