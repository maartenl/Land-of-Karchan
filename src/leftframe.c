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
#include "cgi-util.h"

int main(int argc, char * argv[])
{
	char name[20];
	char password[40];
	int i, res;

	printf("Content-type: text/html\n\n");
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
	printf("<BODY BGCOLOR=#FFFFFF>\r\n");
	printf("\r\n");
	printf("<IMG SRC=\"http://"ServerName"/images/gif/roos.gif\"\r\n");
	printf("USEMAP=\"#roosmap\" BORDER=\"0\" ISMAP ALT=\"compass\"><P>\r\n");
	printf("<P>\r\n");
	printf("<script language=\"JavaScript\">\r\n");
	printf("<!-- In hiding!\r\n");
	printf("\r\n");
	printf("browserName = navigator.appName;\r\n");
	printf(" toc1on = new Image;\r\n");
	printf(" toc1on.src =\"../images/gif/webpic/new/buttonk.gif\";\r\n");
	printf(" toc2on = new Image;\r\n");
	printf(" toc2on.src =\"../images/gif/webpic/new/buttonj.gif\";\r\n");
	printf(" toc3on = new Image;\r\n");
	printf(" toc3on.src =\"../images/gif/webpic/new/buttonr.gif\";\r\n");
	printf("\r\n");
	printf(" toc1off = new Image;\r\n");
	printf(" toc1off.src =\"../images/gif/webpic/buttonk.gif\";\r\n");
	printf(" toc2off = new Image;\r\n");
	printf(" toc2off.src =\"../images/gif/webpic/buttonj.gif\";\r\n");
	printf(" toc3off = new Image;\r\n");
	printf(" toc3off.src =\"../images/gif/webpic/buttonr.gif\";\r\n");
	printf("\r\n");
	printf("function img_act(imgName) {\r\n");
	printf("imgOn = eval(imgName + \"on.src\");\r\n");
	printf("document [imgName].src = imgOn;\r\n");
	printf("}\r\n");
	printf("\r\n");
	printf("function img_inact(imgName) {\r\n");
	printf("imgOff = eval(imgName + \"off.src\");\r\n");
	printf("document [imgName].src = imgOff;\r\n");
	printf("}\r\n");
	printf("\r\n");
	printf("//-->\r\n");
	printf("\r\n");
	printf("</SCRIPT>\r\n");
	printf("\r\n");
	printf("\r\n");
	printf("<A HREF=\"%s?command=quit&name=%s&password=%s&frames=2\" ", MudExe, name, password);
	printf("TARGET=_top ");
	printf("onMouseOver=\"img_act('toc2')\" ");
	printf("onMouseOut=\"img_inact('toc2')\">\r\n");
	printf("<IMG SRC=\"http://"ServerName"/images/gif/webpic/buttonj.gif\" ");
	printf("BORDER=0 ALT=\"quit\" NAME=\"toc2\">\r\n");
	printf("</A><P>\r\n");
	printf("\r\n");
	printf("<A HREF=\"%s?command=sleep&name=%s&password=%s&frames=2\" onMouseOver=\"img_act('toc1')\" ", MudExe, name, password);
	printf("TARGET=\"main\"");
	printf(" onMouseOut=\"img_inact('toc1')\">\r\n");
	printf("<IMG SRC=\"http://"ServerName"/images/gif/webpic/buttonk.gif\" ");
	printf("BORDER=0 ALT=\"sleep\" NAME=\"toc1\">\r\n");
	printf("</A><P>\r\n\r\n");
	printf("<A HREF=\"%s?command=clear&name=%s&password=%s&frames=2\" onMouseOver=\"img_act('toc3')\" ", MudExe, name, password);
	printf("TARGET=\"main\"");
	printf(" onMouseOut=\"img_inact('toc3')\">\r\n");
	printf("<IMG SRC=\"http://"ServerName"/images/gif/webpic/buttonr.gif\" ");
	printf("BORDER=0 ALT=\"clear\" NAME=\"toc3\">\r\n");
	printf("</A>\r\n\r\n");
	printf("<MAP NAME=\"roosmap\">\r\n");
	printf("<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,63,0,0,0\" HREF=\"%s?command=n&name=%s&password=%s&frames=2\" TARGET=\"main\">\r\n", MudExe, name, password);
	printf("<AREA SHAPE=\"POLY\" COORDS=\"0,63,33,31,63,63,0,63\" HREF=\"%s?command=s&name=%s&password=%s&frames=2\" TARGET=\"main\">\r\n", MudExe, name, password);
	printf("<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,0,63,0,0\" HREF=\"%s?command=w&name=%s&password=%s&frames=2\" TARGET=\"main\">\r\n", MudExe, name, password);
	printf("<AREA SHAPE=\"POLY\" COORDS=\"63,0,33,31,63,63,63,0\" HREF=\"%s?command=e&name=%s&password=%s&frames=2\" TARGET=\"main\">\r\n", MudExe, name, password);
	printf("</MAP>\r\n");
	printf("\r\n");
	printf("<P>\r\n");
	printf("<HR><FONT Size=1>&copy; Copyright Maarten van Leunen 1996-1999\r\n");
	printf("<P>\r\n");
	printf("\r\n");
	printf("</BODY>\r\n");
	printf("</HTML>\r\n");

	cgi_quit();
	return 0;
}
