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
#include <time.h>
#include <stdlib.h>
#include <string.h>
#include "typedefs.h"
#include "userlib.h"
#include "mud-lib.h"
#include "mud-lib2.h"
#include "guild.h"

/*! \file guild.c
	\brief  part of the server that does the whole guild thing as well as
some "talk lines". */

#ifndef MEMMAN
#define mud_malloc(A,B,C)	malloc(A)
#define mud_free(A)		free(A)
#define mud_strdup(A,B,C)	strdup(A)
#define mud_realloc(A,B)	realloc(A,B)
#endif

//! list of guildmembers of the MIF
void 
MIFList(char *name, char *password, int room, int frames, int socketfd)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	send_printf(socketfd, "<HTML>\n");
	send_printf(socketfd, "<HEAD>\n");
	send_printf(socketfd, "<TITLE>\n");
	send_printf(socketfd, "Land of Karchan - MIF List\n");
	send_printf(socketfd, "</TITLE>\n");
	send_printf(socketfd, "</HEAD>\n");

	send_printf(socketfd, "<BODY>\n");
	if (!frames)
	{
		send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (frames==1)
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}

	send_printf(socketfd, "<H1><IMG SRC=\"http://%s/images/gif/dragon.gif\">MIF List of Members</H1><HR><UL>\r\n", getParam(MM_SERVERNAME));
	temp = composeSqlStatement("select name, title from usertable where guild='mif'");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL; // temp was allocated by composeSqlStatement 
	while ( (row = mysql_fetch_row(res)) )
	{
		send_printf(socketfd, "<LI>%s, %s\r\n", row[0], row[1]);
	}
	mysql_free_result(res);
	send_printf(socketfd, "</UL>\r\n");
	PrintForm(name, password, frames, socketfd);
	if (frames!=2) {ReadFile(logname, socketfd);}
	send_printf(socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(socketfd, "<DIV ALIGN=left><P>");
}

//! make a member of the mif enter the mif domicile
void 
MIFEntryIn(char *name, char *password, int room, int frames, int socketfd)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, mysex[10];
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp = composeSqlStatement("select sex from tmp_usertable where name='%x'", name);
	res = sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

	WriteMessage(name, room, "You notice %s waving %s hand in front of the south-wall. "
	"At first you wonder what %s is waving at, then suddenly the entire wall disappears! "
	" %s leaves to the south and behind %s the wall immediately appears again as before.<BR>\r\n",
	name, HeShe3(mysex), name, name, HeShe2(mysex));
	room = 143;
	WriteMessage(name, room, "You notice that the wall in the north disappears, %s appears, and the wall replaces itself.<BR>\r\n",
	name);

	temp = composeSqlStatement("update tmp_usertable set room=143 where name='%x'", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	mysql_free_result(res);

	res=sendQuery("select contents from action where id=9", NULL);
	row = mysql_fetch_row(res);
	LookString(row[0], name, password, frames, socketfd);
	mysql_free_result(res);

}

//! make a member leave the mif domicile
void 
MIFEntryOut(char *name, char *password, int room, int frames, int socketfd)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, mysex[10];
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp = composeSqlStatement("select sex from tmp_usertable where name='%x'", name);
	res = sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

	WriteMessage(name, room, "You notice %s waving %s hand in front of the north-wall. "
	"At first you wonder what %s is waving at, then suddenly the entire wall disappears! "
	" %s leaves to the north and behind %s the wall immediately appears again as before.<BR>\r\n", 
	name, HeShe3(mysex), name, name, HeShe2(mysex));
	room = 142;
	WriteMessage(name, room, "You notice that the wall in the south of this chamber suddenly disappears. "
	" %s appears from behind the wall, where you can see another room, and the wall "
	"represents itself. You are pretty amazed.<BR>\r\n", name);

	temp = composeSqlStatement("update tmp_usertable set room=142 where name='%x'", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	mysql_free_result(res);

	res=sendQuery("select contents from action where id=10", NULL);
	row = mysql_fetch_row(res);
	LookString(row[0], name, password, frames, socketfd);
	mysql_free_result(res);

}

//! talk to other mif members on the game
void 
MIFTalk(mudpersonstruct *fmudstruct)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, *temp2;
	char *name;
	char *password;
	char *command;
	int room;

	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp2 = (char *) mud_malloc(strlen(command) + 80, __LINE__, __FILE__);
	sprintf(temp2, "<B><Font color=red>Magitalk</font></B> [%s] : %s<BR>\r\n",
	name, command + (getToken(fmudstruct, 2) - getToken(fmudstruct, 0)));
	
	temp = composeSqlStatement("select name from tmp_usertable where guild='mif'");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	while ( (row = mysql_fetch_row(res)) )
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	mud_free(temp2);
	WriteRoom(fmudstruct);
}

//! list ranger members
void 
RangerList(char *name, char *password, int room, int frames, int socketfd)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	send_printf(socketfd, "<HTML>\n");
	send_printf(socketfd, "<HEAD>\n");
	send_printf(socketfd, "<TITLE>\n");
	send_printf(socketfd, "Land of Karchan - Ranger List\n");
	send_printf(socketfd, "</TITLE>\n");
	send_printf(socketfd, "</HEAD>\n");

	send_printf(socketfd, "<BODY>\n");
	if (!frames)
	{
		send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (frames==1)
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}

	send_printf(socketfd, "<H1><IMG SRC=\"http://%s/images/gif/dragon.gif\">Ranger List of Members</H1><HR><UL>\r\n", getParam(MM_SERVERNAME));
	temp = composeSqlStatement("select name, title from usertable where guild='rangers'");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	while ( (row = mysql_fetch_row(res)) )
	{
		send_printf(socketfd, "<LI>%s, %s\r\n", row[0], row[1]);
	}
	mysql_free_result(res);
	send_printf(socketfd, "</UL>\r\n");
	PrintForm(name, password, frames, socketfd);
	if (frames!=2) {ReadFile(logname, socketfd);}
	send_printf(socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(socketfd, "<DIV ALIGN=left><P>");
}

//! make a member enter the ranger guild room
void 
RangerEntryIn(char *name, char *password, int room, int frames, int socketfd)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, mysex[10];
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp = composeSqlStatement("select sex from tmp_usertable where name='%x'", name);
	res = sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

	WriteMessage(name, room, "%s walks into the calm, crystal-clear, water, and "
	 "steps up to the waterfall. %s then makes the sound of a bird and the"
	 " water slowy parts and %s passes through the solid stone wall"
	 " without a trace, leaving you baffled.<BR>\r\n",
	name, HeSheSmall(mysex), name);
	room = 216;
	WriteMessage(name, room, "You notice that the waterfall parts in two streams, "
	 "%s appears, and the waterfall flows back together.<BR>\r\n",
	name);

	temp = composeSqlStatement("update tmp_usertable set room=216 where name='%x'", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	mysql_free_result(res);

	res=sendQuery("select contents from action where id=13", NULL);
	row = mysql_fetch_row(res);
	LookString(row[0], name, password, frames, socketfd);
	mysql_free_result(res);

}

//! make a ranger leave the guild room
void 
RangerEntryOut(char *name, char *password, int room, int frames, int socketfd)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, mysex[10];
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp = composeSqlStatement("select sex from tmp_usertable where name='%x'", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

	WriteMessage(name, room, "%s steps up to the waterfall. %s then makes the sound of a bird and the"
	" water slowy parts and %s passes through the solid stone wall"
	" without a trace.<BR>\r\n",
	name, HeSheSmall(mysex));
	room = 43;
	WriteMessage(name, room, "You hear the rustling water change in sound, Before your eye's"
	" %s slowly appears through the seemingly solid waterfall without a trace.<BR>\r\n", name);

	temp = composeSqlStatement("update tmp_usertable set room=43 where name='%x'", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	mysql_free_result(res);

	res=sendQuery("select contents from action where id=14", NULL);
	row = mysql_fetch_row(res);
	LookString(row[0], name, password, frames, socketfd);
	mysql_free_result(res);

}

//! talk to other rangers on the game
void 
RangerTalk(mudpersonstruct *fmudstruct)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, *temp2;
	char *name;
	char *password;
	char *command;
	int room;

	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp2 = (char *) mud_malloc(strlen(command) + 80, __LINE__, __FILE__);
	sprintf(temp2, "<B><Font color=green>Naturetalk</font></B> [%s] : %s<BR>\r\n",
	name, command + (getToken(fmudstruct, 2) - getToken(fmudstruct, 0)));
	
	temp = composeSqlStatement("select name from tmp_usertable where guild='rangers'");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	while ( (row = mysql_fetch_row(res)) )
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	mud_free(temp2);
	WriteRoom(fmudstruct);
}

/*! add SWTalk */
void 
SWTalk(mudpersonstruct *fmudstruct)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, *temp2;
	char *name;
	char *password;
	char *command;
	int room;

	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp2 = (char *) mud_malloc(strlen(command) + 80, __LINE__, __FILE__);
	sprintf(temp2, "<B><Font color=brown>Pow Wow</font></B> [%s] : %s<BR>\r\n",
	name, command + (getToken(fmudstruct, 2) - getToken(fmudstruct, 0)));
	
	temp = composeSqlStatement("select name from tmp_usertable where guild='SW'");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	while ( (row = mysql_fetch_row(res)) )
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	mud_free(temp2);
	WriteRoom(fmudstruct);
}

/*! add DepTalk */
void 
DepTalk(mudpersonstruct *fmudstruct)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, *temp2;
	char *name;
	char *password;
	char *command;
	int room;

	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp2 = (char *) mud_malloc(strlen(command) + 80, __LINE__, __FILE__);
	sprintf(temp2, "<B><Font color=purple>Deputy Line</font></B> [%s] : %s<BR>\r\n",
	name, command + (getToken(fmudstruct, 2) - getToken(fmudstruct, 0)));
	
	temp = composeSqlStatement("select name from tmp_usertable where god=1");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	while ( (row = mysql_fetch_row(res)) )
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	mud_free(temp2);
	WriteRoom(fmudstruct);
}	
/*! add BKTalk */
void 
BKTalk(mudpersonstruct *fmudstruct)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, *temp2;
	char *name;
	char *password;
	char *command;
	int room;

	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp2 = (char *) mud_malloc(strlen(command) + 80, __LINE__, __FILE__);
	sprintf(temp2, "<B><Font color=#CC0000>Chaos Murmur</font></B> [%s] : %s<BR>\r\n",
	name, command + (getToken(fmudstruct, 2) - getToken(fmudstruct, 0)));
	
	temp = composeSqlStatement("select name from tmp_usertable where guild='BKIC'");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	while ( (row = mysql_fetch_row(res)) )
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	mud_free(temp2);
	WriteRoom(fmudstruct);
}
/*! add VampTalk */
void 
VampTalk(mudpersonstruct *fmudstruct)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, *temp2;
	char *name;
	char *password;
	char *command;
	int room;

	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp2 = (char *) mud_malloc(strlen(command) + 80, __LINE__, __FILE__);
	sprintf(temp2, "<B><Font color=#666666>Misty Whisper</font></B> [%s] : %s<BR>\r\n",
	name, command + (getToken(fmudstruct, 2) - getToken(fmudstruct, 0)));
	
	temp = composeSqlStatement("select name from tmp_usertable where guild='Kindred'");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	while ( (row = mysql_fetch_row(res)) )
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	mud_free(temp2);
	WriteRoom(fmudstruct);
}
/*! add KnightTalk */
void 
KnightTalk(mudpersonstruct *fmudstruct)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, *temp2;
	char *name;
	char *password;
	char *command;
	int room;

	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp2 = (char *) mud_malloc(strlen(command) + 80, __LINE__, __FILE__);
	sprintf(temp2, "<B><Font color=#0000CC>Knight Talk</font></B> [%s] : %s<BR>\r\n",
	name, command + (getToken(fmudstruct, 2) - getToken(fmudstruct, 0)));

	temp = composeSqlStatement("select name from tmp_usertable where guild='Knights'");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	while ( (row = mysql_fetch_row(res)) )
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	mud_free(temp2);
	WriteRoom(fmudstruct);
}
/*! add CoDTalk */
void 
CoDTalk(mudpersonstruct *fmudstruct)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, *temp2;
	char *name;
	char *password;
	char *command;
	int room;

	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp2 = (char *) mud_malloc(strlen(command) + 80, __LINE__, __FILE__);
	sprintf(temp2, "<B><Font color=#660000>Mogob Burz</font></B> [%s] : %s<BR>\r\n",
	name, command + (getToken(fmudstruct, 2) - getToken(fmudstruct, 0)));
	
	temp = composeSqlStatement("select name from tmp_usertable where guild='CoD'");
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	while ( (row = mysql_fetch_row(res)) )
	{
		WriteLinkTo(row[0], name, temp2);
	}
	mysql_free_result(res);
	
	mud_free(temp2);
	WriteRoom(fmudstruct);
}

