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

extern roomstruct room;
extern char    *command;
extern char    *printstr;
extern struct tm datumtijd;
extern time_t   datetime;

//! make character go west
int
GoWest_Command(char *name, char *password, int room, char *command)
{
	roomstruct      *temproom;
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
	
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
		"where name='%x'"
		, name);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);
	
	temproom=GetRoomInfo(room);
                                
	if (!temproom->west)  {
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
				room = temproom->west;
				temp = composeSqlStatement("update tmp_usertable set room=%i where name='%x'"
								, room, name);
				res=SendSQL2(temp, NULL);
				free(temp);temp=NULL;
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	free(temproom);
	WriteRoom(name, password, room, 0);
	return 1;
}				/* endproc */

//! make character go east
int
GoEast_Command(char *name, char *password, int room, char *fcommand)
{
	roomstruct      *temproom;
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
	                        
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
	"where name='%x'"
	, name);
	res=SendSQL2(temp, NULL);  
	free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);
	
	temproom=GetRoomInfo(room);
                                
	if (!temproom->east)  {
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
				room = temproom->east;
				temp = composeSqlStatement("update tmp_usertable set room=%i, "
				"movementstats=%i where name='%x'"
				, room, movementstats, name);
				res=SendSQL2(temp, NULL);
				free(temp);temp=NULL;
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	} 
	free(temproom);
	WriteRoom(name, password, room, 0);
	return 1;
}				/* endproc */

//! make character go north
int
GoNorth_Command(char *name, char *password, int room, char *fcommmand)
{
	roomstruct      *temproom;
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
                        
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
	"where name='%x'"
	, name);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);

	temproom=GetRoomInfo(room);

	if (!temproom->north)  {
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
				room = temproom->north;
				temp = composeSqlStatement("update tmp_usertable set room=%i, "
					"movementstats=%i where name='%x'"
								, room, movementstats, name);
				res=SendSQL2(temp, NULL);
				free(temp);temp=NULL;
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	free(temproom);
	WriteRoom(name, password, room, 0);
	return 1;
}				/* endproc */

//! make character go south
int
GoSouth_Command(char *name, char *password, int room, char *fcommand)
{
	roomstruct      *temproom;
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int strength, movementstats, maxmove;
	
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("select strength, movementstats, maxmove from tmp_usertable "
		"where name='%x'"
		, name);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;
	row = mysql_fetch_row(res);
	strength = atoi(row[0]);
	movementstats = atoi(row[1]);
	maxmove = atoi(row[2]);
	mysql_free_result(res);
	
	temproom=GetRoomInfo(room);
                                
	if (!temproom->south)  {
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
				room = temproom->south;
				temp = composeSqlStatement("update tmp_usertable set room=%i where name='%x'"
								, room, name);
				res=SendSQL2(temp, NULL);
				free(temp);temp=NULL;
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	free(temproom);
	WriteRoom(name, password, room, 0);
	return 1;
}				/* endproc */

//! make character go to sleep
int
Sleep_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
                        
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("update tmp_usertable set sleep=1, jumpmana=jumpmana+1, jumpmove=jumpmove+2, jumpvital=jumpvital+3 where name='%x'", name);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, "You go to sleep.<BR>\n");
	WriteMessage(name, room, "%s goes to sleep.<BR>\n", name);
	WriteRoom(name, password, room, 1);
	return 1;
}

//! make character wake up (method called from Awaken_Command
void
Awaken2_Command(char *name, char *password, int room)
{
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
                        
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);

	temp = composeSqlStatement("update tmp_usertable set sleep=0, jumpmana=jumpmana-1, jumpmove=jumpmove-2, jumpvital=jumpvital-3 where name='%x'", name);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, "You wake up.<BR>\n");
	WriteMessage(name, room, "%s wakes up. %s is now wide awake.<BR>\n", name, name);
	WriteRoom(name, password, room, 0);
}

//! dump a rather large input field on the screen of the user, for easy putting large masses of text.
int
BigTalk_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	send_printf(getMMudOut(), "<HTML>\n");
	send_printf(getMMudOut(), "<HEAD>\n");
	send_printf(getMMudOut(), "<TITLE>\n");
	send_printf(getMMudOut(), "Land of Karchan - Big Talk\n");
	send_printf(getMMudOut(), "</TITLE>\n");
	send_printf(getMMudOut(), "</HEAD>\n");

	send_printf(getMMudOut(), "<BODY>\n");
	if (!getFrames())
	{
		send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
	send_printf(getMMudOut(), "<H1>Big Talk</H1>This is an alternative for typing. "
	"Due to the size of the area, this is where the really big messages are "
	"typed. This is ideally suited for exampe mudmail.<P>\r\n");

	send_printf(getMMudOut(), "<SCRIPT language=\"JavaScript\">\r\n"
			"<!-- In hiding!\r\n"
			"function setfocus() {\r\n"
			"       document.CommandForm.command.focus();\r\n"
			"	return;\r\n"
			"	}\r\n"
			"//-->\r\n"
			"</SCRIPT>\r\n");
	send_printf(getMMudOut(), "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", getParam(MM_MUDCGI));
	send_printf(getMMudOut(), "<TEXTAREA NAME=\"command\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n");
	send_printf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	send_printf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	send_printf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"%i\">\n", getFrames()+1);
	send_printf(getMMudOut(), "<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n");
	send_printf(getMMudOut(), "</FORM><P>\n");
	if (getFrames()!=2) {ReadFile(logname);}
	send_printf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(getMMudOut(), "<DIV ALIGN=left><P></BODY></HTML>");
	return 1;
}				/* endproc */

//! create a html page containing a nice form for sending mudmail
int
MailFormDumpOnScreen(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	send_printf(getMMudOut(), "<HTML>\n");
	send_printf(getMMudOut(), "<HEAD>\n");
	send_printf(getMMudOut(), "<TITLE>\n");
	send_printf(getMMudOut(), "Land of Karchan - Mail to:\rn");
	send_printf(getMMudOut(), "</TITLE>\n");
	send_printf(getMMudOut(), "</HEAD>\n");

	send_printf(getMMudOut(), "<BODY>\n");
	if (!getFrames())
	{
		send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
	send_printf(getMMudOut(), "<H1>Mail To</H1><HR noshade>\r\n");

	send_printf(getMMudOut(), "<SCRIPT language=\"JavaScript\">\r\n"
		"<!-- In hiding!\r\n" 
		"function setfocus() {\r\n"
		" document.CommandForm.mailto.focus();\r\n"
		" return;\r\n"
		" }\r\n"
		"//-->\r\n"
		"</SCRIPT>\r\n");

	send_printf(getMMudOut(), "<TABLE BORDER=0>");
	send_printf(getMMudOut(), "<TR><TD>From:</TD><TD><B>%s</B></TD></TR>\r\n",name);
	send_printf(getMMudOut(), "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", getParam(MM_MUDCGI));
	send_printf(getMMudOut(), "<TR><TD>To: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailto\" VALUE=\"\"></TD></TR>\r\n");
	send_printf(getMMudOut(), "<TR><TD>Header: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailheader\" SIZE=80 VALUE=\"\"></TD></TR>\r\n");
	send_printf(getMMudOut(), "<TR><TD>Body:</TD><TD>\r\n");
	send_printf(getMMudOut(), "<TEXTAREA NAME=\"mailbody\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n");
	send_printf(getMMudOut(), "</TD></TR></TABLE><INPUT TYPE=\"hidden\" NAME=\"command\" VALUE=\"sendmail\">\n");
	send_printf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	send_printf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	send_printf(getMMudOut(), "<HR noshade><INPUT TYPE=\"submit\" VALUE=\"Sendmail\">\n");
	send_printf(getMMudOut(), "<INPUT TYPE=\"reset\" VALUE=\"Resetform\">\n");
	send_printf(getMMudOut(), "</FORM><P>\r\n");
	if (getFrames()!=2) {ReadFile(logname);}
	send_printf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(getMMudOut(), "<DIV ALIGN=left><P></BODY></HTML>");
	return 1;
}				/* endproc */

//! show time on the mud
int
Time_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	WriteSentenceIntoOwnLogFile(logname, "Current time is %i:%i:%i<BR>\r\n",
		 datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec);
	WriteRoom(name, password, room, 0);
	return 1;
}				/* endproc */

//! show date on the mud
int
Date_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	WriteSentenceIntoOwnLogFile(logname, "Current date is %i-%i-%i<BR>\r\n",
		datumtijd.tm_mon + 1, datumtijd.tm_mday, datumtijd.tm_year+1900);
	WriteRoom(name, password, room, 0);
	return 1;
}				/* endproc */

//! look at the sky
void
LookSky_Command(char *name, char *password)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	LookAtProc(-10, name, password);
}				/* endproc */

