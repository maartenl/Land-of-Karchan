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
#include "guild.h"

extern roomstruct room;
extern int      hellroom;
extern int      events[50];
extern int      knightlist[50];
extern int      miflist[50];
extern int      rangerlist[50];
extern banstruct banlist[50];
char           *troep;
char           *command;
char           *junk;
char           *printstr;
char           *tokens[100];
int             aantal;
struct tm       datumtijd;
time_t          datetime;

void 
MIFList(char *name, char *password, int room)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - MIF List\n");
	fprintf(cgiOut, "</TITLE>\n");
	fprintf(cgiOut, "</HEAD>\n");

	fprintf(cgiOut, "<BODY>\n");
	if (!getFrames())
	{
		fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}

	fprintf(cgiOut, "<H1><IMG SRC=\"http://"ServerName"/images/gif/dragon.gif\">MIF List of Members</H1><HR><UL>\r\n");
	sprintf(temp, "select name, title from usertable where guild='mif'");
	res=SendSQL2(temp, NULL);
	while (row = mysql_fetch_row(res))
	{
		fprintf(cgiOut, "<LI>%s, %s\r\n", row[0], row[1]);
	}
	mysql_free_result(res);
	fprintf(cgiOut, "</UL>\r\n");
	PrintForm(name, password);
	if (getFrames()!=2) {ReadFile(logname);}
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
}

void 
MIFEntryIn(char *name, char *password, int room)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024], mysex[10];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	sprintf(temp, "select sex from tmp_usertable where name='%s'", name);
	res=SendSQL2(temp, NULL);
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

	WriteMessage2(name, room, "You notice %s waving %s hand in front of the south-wall. "
	"At first you wonder what %s is waving at, then suddenly the entire wall disappears! "
	" %s leaves to the south and behind %s the wall immediately appears again as before.<BR>\r\n",
	name, HeShe3(mysex), name, name, HeShe2(mysex));
	room = 143;
	WriteMessage2(name, room, "You notice that the wall in the north disappears, %s appears, and the wall replaces itself.<BR>\r\n",
	name);

	sprintf(temp, "update tmp_usertable set room=143 where name='%s'", name);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);

	res=SendSQL2("select contents from action where id=9", NULL);
	row = mysql_fetch_row(res);
	LookString(row[0], name, password);
	mysql_free_result(res);

	KillGame();
}

void 
MIFEntryOut(char *name, char *password, int room)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024], mysex[10];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	sprintf(temp, "select sex from tmp_usertable where name='%s'", name);
	res=SendSQL2(temp, NULL);
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

	WriteMessage2(name, room, "You notice %s waving %s hand in front of the north-wall. "
	"At first you wonder what %s is waving at, then suddenly the entire wall disappears! "
	" %s leaves to the north and behind %s the wall immediately appears again as before.<BR>\r\n", 
	name, HeShe3(mysex), name, name, HeShe2(mysex));
	room = 142;
	WriteMessage2(name, room, "You notice that the wall in the south of this chamber suddenly disappears. "
	" %s appears from behind the wall, where you can see another room, and the wall "
	"represents itself. You are pretty amazed.<BR>\r\n", name);

	sprintf(temp, "update tmp_usertable set room=142 where name='%s'", name);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);

	res=SendSQL2("select contents from action where id=10", NULL);
	row = mysql_fetch_row(res);
	LookString(row[0], name, password);
	mysql_free_result(res);

	KillGame();
}

void 
MIFTalk(char *name, char *password, int room)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024], *temp2;
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	temp2 = (char *) malloc(strlen(troep) + 80);
	sprintf(temp2, "<B><Font color=red>Magitalk</font> </B>[%s] : %s<BR>\r\n",
	name, command + (tokens[2] - tokens[0]));
	
	sprintf(temp, "select name from tmp_usertable where guild='mif'");
	res=SendSQL2(temp, NULL);
	while (row = mysql_fetch_row(res))
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	free(temp2);
	WriteRoom(name, password, room, 0);
	KillGame();
}

void 
RangerList(char *name, char *password, int room)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - Ranger List\n");
	fprintf(cgiOut, "</TITLE>\n");
	fprintf(cgiOut, "</HEAD>\n");

	fprintf(cgiOut, "<BODY>\n");
	if (!getFrames())
	{
		fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}

	fprintf(cgiOut, "<H1><IMG SRC=\"http://"ServerName"/images/gif/dragon.gif\">Ranger List of Members</H1><HR><UL>\r\n");
	sprintf(temp, "select name, title from usertable where guild='rangers'");
	res=SendSQL2(temp, NULL);
	while (row = mysql_fetch_row(res))
	{
		fprintf(cgiOut, "<LI>%s, %s\r\n", row[0], row[1]);
	}
	mysql_free_result(res);
	fprintf(cgiOut, "</UL>\r\n");
	PrintForm(name, password);
	if (getFrames()!=2) {ReadFile(logname);}
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
}

void 
RangerEntryIn(char *name, char *password, int room)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024], mysex[10];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	sprintf(temp, "select sex from tmp_usertable where name='%s'", name);
	res=SendSQL2(temp, NULL);
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

	WriteMessage2(name, room, "%s walks into the calm, crystal-clear, water, and "
	 "steps up to the waterfall. %s then makes the sound of a bird and the"
	 " water slowy parts and %s passes through the solid stone wall"
	 " without a trace, leaving you baffled.<BR>\r\n",
	name, HeSheSmall(mysex), name);
	room = 216;
	WriteMessage2(name, room, "You notice that the waterfall parts in two streams, "
	 "%s appears, and the waterfall flows back together.<BR>\r\n",
	name);

	sprintf(temp, "update tmp_usertable set room=216 where name='%s'", name);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);

	res=SendSQL2("select contents from action where id=13", NULL);
	row = mysql_fetch_row(res);
	LookString(row[0], name, password);
	mysql_free_result(res);

	KillGame();
}

void 
RangerEntryOut(char *name, char *password, int room)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024], mysex[10];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	sprintf(temp, "select sex from tmp_usertable where name='%s'", name);
	res=SendSQL2(temp, NULL);
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

	WriteMessage2(name, room, "%s steps up to the waterfall. %s then makes the sound of a bird and the"
	" water slowy parts and %s passes through the solid stone wall"
	" without a trace.<BR>\r\n",
	name, HeSheSmall(mysex));
	room = 43;
	WriteMessage2(name, room, "You hear the rustling water change in sound, Before your eye's"
	" %s slowly appears through the seemingly solid waterfall without a trace.<BR>\r\n", name);

	sprintf(temp, "update tmp_usertable set room=43 where name='%s'", name);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);

	res=SendSQL2("select contents from action where id=14", NULL);
	row = mysql_fetch_row(res);
	LookString(row[0], name, password);
	mysql_free_result(res);

	KillGame();
}

void 
RangerTalk(char *name, char *password, int room)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024], *temp2;
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	temp2 = (char *) malloc(strlen(troep) + 80);
	sprintf(temp2, "<B><Font color=green>Naturetalk</font> </B>[%s] : %s<BR>\r\n",
	name, command + (tokens[2] - tokens[0]));
	
	sprintf(temp, "select name from tmp_usertable where guild='rangers'");
	res=SendSQL2(temp, NULL);
	while (row = mysql_fetch_row(res))
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	free(temp2);
	WriteRoom(name, password, room, 0);
	KillGame();
}
