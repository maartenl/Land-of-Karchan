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

	cgiHeaderContentType("text/html");
	
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
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF>\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "<IMG SRC=\"http://"ServerName"/images/gif/roos.gif\"\r\n");
	fprintf(cgiOut, "USEMAP=\"#roosmap\" BORDER=\"0\" ISMAP ALT=\"compass\"><P>\r\n");
	fprintf(cgiOut, "<P>\r\n");
	fprintf(cgiOut, "<script language=\"JavaScript\">\r\n");
	fprintf(cgiOut, "<!-- In hiding!\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "browserName = navigator.appName;\r\n");
	fprintf(cgiOut, "               toc1on = new Image;          \r\n");
	fprintf(cgiOut, "               toc1on.src =\"../images/gif/webpic/new/buttonk.gif\";\r\n");
	fprintf(cgiOut, "               toc2on = new Image;          \r\n");
	fprintf(cgiOut, "               toc2on.src =\"../images/gif/webpic/new/buttonj.gif\";\r\n");
	fprintf(cgiOut, "               toc3on = new Image;          \r\n");
	fprintf(cgiOut, "               toc3on.src =\"../images/gif/webpic/new/buttonr.gif\";\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "               toc1off = new Image;\r\n");
	fprintf(cgiOut, "               toc1off.src =\"../images/gif/webpic/buttonk.gif\";\r\n");
	fprintf(cgiOut, "               toc2off = new Image;\r\n");
	fprintf(cgiOut, "               toc2off.src =\"../images/gif/webpic/buttonj.gif\";\r\n");
	fprintf(cgiOut, "               toc3off = new Image;\r\n");
	fprintf(cgiOut, "               toc3off.src =\"../images/gif/webpic/buttonr.gif\";\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "function img_act(imgName) {\r\n");
	fprintf(cgiOut, "        imgOn = eval(imgName + \"on.src\");\r\n");
	fprintf(cgiOut, "        document [imgName].src = imgOn;\r\n");
	fprintf(cgiOut, "}\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "function img_inact(imgName) {\r\n");
	fprintf(cgiOut, "        imgOff = eval(imgName + \"off.src\");\r\n");
	fprintf(cgiOut, "        document [imgName].src = imgOff;\r\n");
	fprintf(cgiOut, "}\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "//-->\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "</SCRIPT>\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "<A HREF=\"%s?command=quit&name=%s&password=%s&frames=2\" ", MudExe, name, password);
	fprintf(cgiOut, "TARGET=\"main\" ");
	fprintf(cgiOut, "onMouseOver=\"img_act('toc2')\" ");
	fprintf(cgiOut, "onMouseOut=\"img_inact('toc2')\">\r\n");
	fprintf(cgiOut, "<IMG SRC=\"http://"ServerName"/images/gif/webpic/buttonj.gif\" ");
	fprintf(cgiOut, "BORDER=0 ALT=\"quit\" NAME=\"toc2\">\r\n");
	fprintf(cgiOut, "</A><P>\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "<A HREF=\"%s?command=sleep&name=%s&password=%s&frames=2\" onMouseOver=\"img_act('toc1')\" ", MudExe, name, password);
	fprintf(cgiOut, "TARGET=\"main\"");
	fprintf(cgiOut, " onMouseOut=\"img_inact('toc1')\">\r\n");
	fprintf(cgiOut, "<IMG SRC=\"http://"ServerName"/images/gif/webpic/buttonk.gif\" ");
	fprintf(cgiOut, "BORDER=0 ALT=\"sleep\" NAME=\"toc1\">\r\n");
	fprintf(cgiOut, "</A><P>\r\n\r\n");
	fprintf(cgiOut, "<A HREF=\"%s?command=clear&name=%s&password=%s&frames=2\" onMouseOver=\"img_act('toc3')\" ", MudExe, name, password);
	fprintf(cgiOut, "TARGET=\"main\"");
	fprintf(cgiOut, " onMouseOut=\"img_inact('toc3')\">\r\n");
	fprintf(cgiOut, "<IMG SRC=\"http://"ServerName"/images/gif/webpic/buttonr.gif\" ");
	fprintf(cgiOut, "BORDER=0 ALT=\"clear\" NAME=\"toc3\">\r\n");
	fprintf(cgiOut, "</A>\r\n\r\n");
	fprintf(cgiOut, "<MAP NAME=\"roosmap\">\r\n");
	fprintf(cgiOut, "<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,63,0,0,0\" HREF=\"%s?command=n&name=%s&password=%s&frames=2\" TARGET=\"main\">\r\n", MudExe, name, password);
	fprintf(cgiOut, "<AREA SHAPE=\"POLY\" COORDS=\"0,63,33,31,63,63,0,63\" HREF=\"%s?command=s&name=%s&password=%s&frames=2\" TARGET=\"main\">\r\n", MudExe, name, password);
	fprintf(cgiOut, "<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,0,63,0,0\" HREF=\"%s?command=w&name=%s&password=%s&frames=2\" TARGET=\"main\">\r\n", MudExe, name, password);
	fprintf(cgiOut, "<AREA SHAPE=\"POLY\" COORDS=\"63,0,33,31,63,63,63,0\" HREF=\"%s?command=e&name=%s&password=%s&frames=2\" TARGET=\"main\">\r\n", MudExe, name, password);
	fprintf(cgiOut, "</MAP>\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "<P>\r\n");
	fprintf(cgiOut, "<HR><FONT Size=1>&copy; Copyright Maarten van Leunen 1996-1999\r\n");
	fprintf(cgiOut, "<P>\r\n");
	fprintf(cgiOut, "\r\n");
	fprintf(cgiOut, "</BODY>\r\n");
	fprintf(cgiOut, "</HTML>\r\n");

	return 0;
}
