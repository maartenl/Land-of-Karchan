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
#include "typedefs.h"
#include "mud-lib3.h"

/*! \file mud-lib3.c
	\brief  part of the server that takes care of extended commands */

//! have the character proceed downwards
int
GoDown_Command(mudpersonstruct *fmudstruct)
{
	int i=0;
	char logname[100];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
	char *name;
	char *password;
	char *fcommand;
	int room;
	int direction;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;

//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
		"where name='%x'"
		, name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;

	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);

	direction = 0;
	res = executeQuery(NULL, "select down from rooms where id=%i", room);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		direction = atoi(row[0]);
	}
	mysql_free_result(res);

	if (!direction)  
	{
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>");
	} 
	else 
	{
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage(name, room, "%s attempts to leave down, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile(logname, "You are exhausted.<BR>\r\n");
		}
		else   
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage(name, room, "%s attempts to leave down, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage(name, room, "%s leaves down.<BR>\r\n", name);
				room = direction;
				fmudstruct->room = room;
				temp = composeSqlStatement("update tmp_usertable set room=%i where name='%x'"
					,room, name);
				res=sendQuery(temp, NULL);
				free(temp);temp=NULL;

				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	WriteRoom(fmudstruct);
	return 1;
} 				/* endproc */

//! have the character proceed upwards
int
GoUp_Command(mudpersonstruct *fmudstruct)
{
	int i=0;
	char logname[100];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
	char *name;
	char *password;
	char *fcommand;
	int room;
	int direction;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
		"where name='%x'"
		, name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;

	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);

	direction = 0;
	res = executeQuery(NULL, "select up from rooms where id=%i", room);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		direction = atoi(row[0]);
	}
	mysql_free_result(res);


	if (!direction)  {
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage(name, room, "%s attempts to leave up, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile(logname, "You are exhausted.<BR>\r\n");
		}
		else   
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage(name, room, "%s attempts to leave up, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage(name, room, "%s leaves up.<BR>\r\n", name);
				room = direction;
				fmudstruct->room = room;
				temp = composeSqlStatement("update tmp_usertable set room=%i where name='%x'"
								, room, name);
				res=sendQuery(temp, NULL);
				free(temp);temp=NULL;

				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	WriteRoom(fmudstruct);
	return 1;
} 				/* endproc */

//! make character go west
int
GoWest_Command(mudpersonstruct *fmudstruct)
{
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
	char *name, *password, *command;
	int room;
	int direction;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	command = fmudstruct->command;
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
		"where name='%x'"
		, name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);
	
	direction = 0;
	res = executeQuery(NULL, "select west from rooms where id=%i", room);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		direction = atoi(row[0]);
	}
	mysql_free_result(res);

                                
	if (!direction)  {
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>\r\n");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage(name, room, "%s attempts to leave west, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile(logname, "You are exhausted, and can't move.<BR>\r\n");
		}
		else   
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage(name, room, "%s attempts to leave west, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage(name, room, "%s leaves west.<BR>\r\n", name);
				room = direction;
				fmudstruct->room = room;
				temp = composeSqlStatement("update tmp_usertable set room=%i where name='%x'"
								, room, name);
				res=sendQuery(temp, NULL);
				free(temp);temp=NULL;
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	WriteRoom(fmudstruct);
	return 1;
}				/* endproc */

//! make character go east
int
GoEast_Command(mudpersonstruct *fmudstruct)
{
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
	char *name, *password, *command;
	int room;
	int direction;

	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	command = fmudstruct->command;

//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
	"where name='%x'"
	, name);
	res=sendQuery(temp, NULL);  
	free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);
	
	direction = 0;
	res = executeQuery(NULL, "select east from rooms where id=%i", room);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		direction = atoi(row[0]);
	}
	mysql_free_result(res);

                                
	if (!direction)  {
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>\r\n");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage(name, room, "%s attempts to leave east, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile(logname, "You are exhausted, and can't move.<BR>\r\n");
		}
		else
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage(name, room, "%s attempts to leave east, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage(name, room, "%s leaves east.<BR>\r\n", name);
				room = direction;
				fmudstruct->room = room;
				temp = composeSqlStatement("update tmp_usertable set room=%i, "
				"movementstats=%i where name='%x'"
				, room, movementstats, name);
				res=sendQuery(temp, NULL);
				free(temp);temp=NULL;
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	} 
	WriteRoom(fmudstruct);
	return 1;
}				/* endproc */

//! make character go north
int
GoNorth_Command(mudpersonstruct *fmudstruct)
{
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
	char *name, *password, *command;
	int room;
	int direction;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	command = fmudstruct->command;
#ifdef DEBUG
        printf("gameMain %i, %i!!!\n", room, fmudstruct->room);
#endif
                                
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
	"where name='%x'"
	, name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);

	direction = 0;
	res = executeQuery(NULL, "select north from rooms where id=%i", room);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		direction = atoi(row[0]);
	}
	mysql_free_result(res);


	if (!direction)  {
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>\r\n");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage(name, room, "%s attempts to leave north, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile(logname, "You are exhausted, and can't move.<BR>\r\n");
		}
		else
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage(name, room, "%s attempts to leave north, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage(name, room, "%s leaves north.<BR>\r\n", name);
				room = direction;
				fmudstruct->room = room;
				temp = composeSqlStatement("update tmp_usertable set room=%i, "
					"movementstats=%i where name='%x'"
								, room, movementstats, name);
				res=sendQuery(temp, NULL);
				free(temp);temp=NULL;
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	WriteRoom(fmudstruct);
	return 1;
}				/* endproc */

//! make character go south
int
GoSouth_Command(mudpersonstruct *fmudstruct)
{
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
	char *name, *password, *command;
	int room;
	int direction;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	command = fmudstruct->command;
		
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
		"where name='%x'"
		, name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);
	
	direction = 0;
	res = executeQuery(NULL, "select south from rooms where id=%i", room);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		direction = atoi(row[0]);
	}
	mysql_free_result(res);

                                
	if (!direction)  {
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>\r\n");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage(name, room, "%s attempts to leave south, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile(logname, "You are exhausted, and can't move.<BR>\r\n");
		}
		else   
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage(name, room, "%s attempts to leave south, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage(name, room, "%s leaves south.<BR>\r\n", name);
				room = direction;
				fmudstruct->room = room;
				temp = composeSqlStatement("update tmp_usertable set room=%i where name='%x'"
								, room, name);
				res=sendQuery(temp, NULL);
				free(temp);temp=NULL;
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	WriteRoom(fmudstruct);
	return 1;
}				/* endproc */

//! make character go to sleep
int
Sleep_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	char *name, *password, *fcommand;
	int room;

	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	                        
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("update tmp_usertable set sleep=1, jumpmana=jumpmana+1, jumpmove=jumpmove+2, jumpvital=jumpvital+3 where name='%x'", name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, "You go to sleep.<BR>\n");
	WriteMessage(name, room, "%s goes to sleep.<BR>\n", name);
	WriteRoom(fmudstruct);
	return 1;
}

//! make character wake up (method called from Awaken_Command
void
Awaken2_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	char *name;
	char *password;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("update tmp_usertable set sleep=0, jumpmana=jumpmana-1, jumpmove=jumpmove-2, jumpvital=jumpvital-3 where name='%x'", name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, "You wake up.<BR>\n");
	WriteMessage(name, room, "%s wakes up. %s is now wide awake.<BR>\n", name, name);
	WriteRoom(fmudstruct);
}

//! dump a rather large input field on the screen of the user, for easy putting large masses of text.
int
BigTalk_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];  
	char *name, *password, *fcommand;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	send_printf(fmudstruct->socketfd, "<HTML>\n");
	send_printf(fmudstruct->socketfd, "<HEAD>\n");
	send_printf(fmudstruct->socketfd, "<TITLE>\n");
	send_printf(fmudstruct->socketfd, "Land of Karchan - Big Talk\n");
	send_printf(fmudstruct->socketfd, "</TITLE>\n");
	send_printf(fmudstruct->socketfd, "</HEAD>\n");

	send_printf(fmudstruct->socketfd, "<BODY>\n");
	if (!fmudstruct->frames)
	{
		send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (fmudstruct->frames==1)
		{
			send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
	send_printf(fmudstruct->socketfd, "<H1>Big Talk</H1>This is an alternative for typing. "
	"Due to the size of the area, this is where the really big messages are "
	"typed. This is ideally suited for exampe mudmail.<P>\r\n");

	send_printf(fmudstruct->socketfd, "<SCRIPT language=\"JavaScript\">\r\n"
			"<!-- In hiding!\r\n"
			"function setfocus() {\r\n"
			"       document.CommandForm.command.focus();\r\n"
			"	return;\r\n"
			"	}\r\n"
			"//-->\r\n"
			"</SCRIPT>\r\n");
	send_printf(fmudstruct->socketfd, "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", getParam(MM_MUDCGI));
	send_printf(fmudstruct->socketfd, "<TEXTAREA NAME=\"command\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n");
	send_printf(fmudstruct->socketfd, "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	send_printf(fmudstruct->socketfd, "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	send_printf(fmudstruct->socketfd, "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"%i\">\n", fmudstruct->frames+1);
	send_printf(fmudstruct->socketfd, "<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n");
	send_printf(fmudstruct->socketfd, "</FORM><P>\n");
	if (fmudstruct->frames!=2) {ReadFile(logname, fmudstruct->socketfd);}
	send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P></BODY></HTML>");
	return 1;
}				/* endproc */

//! create a html page containing a nice form for sending mudmail
int
MailFormDumpOnScreen(mudpersonstruct *fmudstruct)
{
	char logname[100];  
	char *name, *password, *fcommand;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	send_printf(fmudstruct->socketfd, "<HTML>\n");
	send_printf(fmudstruct->socketfd, "<HEAD>\n");
	send_printf(fmudstruct->socketfd, "<TITLE>\n");
	send_printf(fmudstruct->socketfd, "Land of Karchan - Mail to:\rn");
	send_printf(fmudstruct->socketfd, "</TITLE>\n");
	send_printf(fmudstruct->socketfd, "</HEAD>\n");

	send_printf(fmudstruct->socketfd, "<BODY>\n");
	if (!fmudstruct->frames)
	{
		send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (fmudstruct->frames==1)
		{
			send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
	send_printf(fmudstruct->socketfd, "<H1>Mail To</H1><HR noshade>\r\n");

	send_printf(fmudstruct->socketfd, "<SCRIPT language=\"JavaScript\">\r\n"
		"<!-- In hiding!\r\n" 
		"function setfocus() {\r\n"
		" document.CommandForm.mailto.focus();\r\n"
		" return;\r\n"
		" }\r\n"
		"//-->\r\n"
		"</SCRIPT>\r\n");

	send_printf(fmudstruct->socketfd, "<TABLE BORDER=0>");
	send_printf(fmudstruct->socketfd, "<TR><TD>From:</TD><TD><B>%s</B></TD></TR>\r\n",name);
	send_printf(fmudstruct->socketfd, "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", getParam(MM_MUDCGI));
	send_printf(fmudstruct->socketfd, "<TR><TD>To: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailto\" VALUE=\"\"></TD></TR>\r\n");
	send_printf(fmudstruct->socketfd, "<TR><TD>Header: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailheader\" SIZE=80 VALUE=\"\"></TD></TR>\r\n");
	send_printf(fmudstruct->socketfd, "<TR><TD>Body:</TD><TD>\r\n");
	send_printf(fmudstruct->socketfd, "<TEXTAREA NAME=\"mailbody\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n");
	send_printf(fmudstruct->socketfd, "</TD></TR></TABLE><INPUT TYPE=\"hidden\" NAME=\"command\" VALUE=\"sendmail\">\n");
	send_printf(fmudstruct->socketfd, "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	send_printf(fmudstruct->socketfd, "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	send_printf(fmudstruct->socketfd, "<HR noshade><INPUT TYPE=\"submit\" VALUE=\"Sendmail\">\n");
	send_printf(fmudstruct->socketfd, "<INPUT TYPE=\"reset\" VALUE=\"Resetform\">\n");
	send_printf(fmudstruct->socketfd, "</FORM><P>\r\n");
	if (fmudstruct->frames!=2) {ReadFile(logname, fmudstruct->socketfd);}
	send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P></BODY></HTML>");
	return 1;
}				/* endproc */

//! show time on the mud
int
Time_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];  
	char *name, *password;
	struct tm datumtijd;
	time_t   datetime;
time(&datetime);
	int room;
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	
	time(&datetime);
	datumtijd = *(gmtime(&datetime));
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	WriteSentenceIntoOwnLogFile(logname, "Current time is %i:%i:%i<BR>\r\n",
		 datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec);
	WriteRoom(fmudstruct);
	return 1;
}				/* endproc */

//! show date on the mud
int
Date_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];  
	char *name, *password;
	struct tm datumtijd;
	time_t   datetime;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;

	time(&datetime);
	datumtijd = *(gmtime(&datetime));
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	WriteSentenceIntoOwnLogFile(logname, "Current date is %i-%i-%i<BR>\r\n",
		datumtijd.tm_mon + 1, datumtijd.tm_mday, datumtijd.tm_year+1900);
	WriteRoom(fmudstruct);
	return 1;
}				/* endproc */

