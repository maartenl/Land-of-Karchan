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
#include <time.h>
#include "mud-lib3.h"

extern roomstruct room;
extern int      hellroom;
extern int      events[50];
extern int      knightlist[50];
extern int      miflist[50];
extern int      rangerlist[50];
extern char    *troep;
extern char    *command;
extern char    *junk;
extern char    *printstr;
extern char    *tokens[100];
extern int      aantal;
extern struct tm datumtijd;
extern time_t   datetime;

void
GoWest_Command(char *name, char *password, int room)
{
	roomstruct      *temproom;
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	int strength, movementstats, maxmove;
	
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",USERHeader,name);

	sprintf(temp, "select strength, movementstats, maxmove from tmp_usertable "
		"where name='%s'"
		, name);
	res=SendSQL2(temp, NULL);
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
				sprintf(temp, "update tmp_usertable set room=%i where name='%s'"
								, room, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	free(temproom);
	WriteRoom(name, password, room, 0);
	KillGame();
}				/* endproc */

void
GoEast_Command(char *name, char *password, int room)
{
	roomstruct      *temproom;
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	int strength, movementstats, maxmove;
	                        
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",USERHeader,name);

	sprintf(temp, "select strength, movementstats, maxmove from tmp_usertable "
	"where name='%s'"
	, name);
	res=SendSQL2(temp, NULL);  
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
				sprintf(temp, "update tmp_usertable set room=%i, "
				"movementstats=%i where name='%s'"
				, room, movementstats, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	} 
	free(temproom);
	WriteRoom(name, password, room, 0);
	KillGame();
}				/* endproc */

void
GoNorth_Command(char *name, char *password, int room)
{
	roomstruct      *temproom;
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	int strength, movementstats, maxmove;
                        
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",USERHeader,name);

	sprintf(temp, "select strength, movementstats, maxmove from tmp_usertable "
	"where name='%s'"
	, name);
	res=SendSQL2(temp, NULL);
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
				sprintf(temp, "update tmp_usertable set room=%i, "
					"movementstats=%i where name='%s'"
								, room, movementstats, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	free(temproom);
	WriteRoom(name, password, room, 0);
	KillGame();
}				/* endproc */

void
GoSouth_Command(char *name, char *password, int room)
{
	roomstruct      *temproom;
	int i=0;
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	int strength, movementstats, maxmove;
	
//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",USERHeader,name);

	sprintf(temp, "select strength, movementstats, maxmove from tmp_usertable "
		"where name='%s'"
		, name);
	res=SendSQL2(temp, NULL);
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
				sprintf(temp, "update tmp_usertable set room=%i where name='%s'"
								, room, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteMessage(name, room, "%s appears.<BR>\r\n", name);
			} /* if burden NOT too heavy to move */
		} /* if NOT exhausted */
	}
	free(temproom);
	WriteRoom(name, password, room, 0);
	KillGame();
}				/* endproc */

void
Sleep_Command(char *name, char *password, int room)
{
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
                        
	sprintf(logname, "%s%s.log",USERHeader,name);

	sprintf(temp, "update tmp_usertable set sleep=1, jumpmana=jumpmana+1, jumpmove=jumpmove+2, jumpvital=jumpvital+3 where name='%s'", name);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, "You go to sleep.<BR>\n");
	WriteMessage(name, room, "%s goes to sleep.<BR>\n", name);
	WriteRoom(name, password, room, 1);
	KillGame();
}

void
Awaken_Command(char *name, char *password, int room)
{
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
                        
	sprintf(logname, "%s%s.log",USERHeader,name);

	sprintf(temp, "update tmp_usertable set sleep=0, jumpmana=jumpmana-1, jumpmove=jumpmove-2, jumpvital=jumpvital-3 where name='%s'", name);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, "You wake up.<BR>\n");
	WriteMessage(name, room, "%s wakes up. %s is now wide awake.<BR>\n", name, name);
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
BigTalk_Command(char *name, char *password)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - Big Talk\n");
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
	fprintf(cgiOut, "<H1>Big Talk</H1>This is an alternative for typing. "
	"Due to the size of the area, this is where the really big messages are "
	"typed. This is ideally suited for exampe mudmail.<P>\r\n");

	fprintf(cgiOut, "<SCRIPT language=\"JavaScript\">\r\n"
			"<!-- In hiding!\r\n"
			"function setfocus() {\r\n"
			"       document.CommandForm.command.focus();\r\n"
			"	return;\r\n"
			"	}\r\n"
			"//-->\r\n"
			"</SCRIPT>\r\n");
	fprintf(cgiOut, "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", MudExe);
	fprintf(cgiOut, "<TEXTAREA NAME=\"command\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n");
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"%i\">\n", getFrames()+1);
	fprintf(cgiOut, "<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n");
	fprintf(cgiOut, "</FORM><P>\n");
	if (getFrames()!=2) {ReadFile(logname);}
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P></BODY></HTML>");
	KillGame();
}				/* endproc */

void
MailFormDumpOnScreen(char *name, char *password)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - Mail to:\rn");
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
	fprintf(cgiOut, "<H1>Mail To</H1><HR noshade>\r\n");

	fprintf(cgiOut, "<SCRIPT language=\"JavaScript\">\r\n"
		"<!-- In hiding!\r\n" 
		"function setfocus() {\r\n"
		" document.CommandForm.mailto.focus();\r\n"
		" return;\r\n"
		" }\r\n"
		"//-->\r\n"
		"</SCRIPT>\r\n");

	fprintf(cgiOut, "<TABLE BORDER=0>");
	fprintf(cgiOut, "<TR><TD>From:</TD><TD><B>%s</B></TD></TR>\r\n",name);
	fprintf(cgiOut, "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", MudExe);
	fprintf(cgiOut, "<TR><TD>To: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailto\" VALUE=\"\"></TD></TR>\r\n");
	fprintf(cgiOut, "<TR><TD>Header: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailheader\" SIZE=80 VALUE=\"\"></TD></TR>\r\n");
	fprintf(cgiOut, "<TR><TD>Body:</TD><TD>\r\n");
	fprintf(cgiOut, "<TEXTAREA NAME=\"mailbody\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n");
	fprintf(cgiOut, "</TD></TR></TABLE><INPUT TYPE=\"hidden\" NAME=\"command\" VALUE=\"sendmail\">\n");
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	fprintf(cgiOut, "<HR noshade><INPUT TYPE=\"submit\" VALUE=\"Sendmail\">\n");
	fprintf(cgiOut, "<INPUT TYPE=\"reset\" VALUE=\"Resetform\">\n");
	fprintf(cgiOut, "</FORM><P>\r\n");
	if (getFrames()!=2) {ReadFile(logname);}
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P></BODY></HTML>");
 	KillGame();
}				/* endproc */

void
Time_Command(char *name, char *password, int room)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteSentenceIntoOwnLogFile(logname, "Current time is %i:%i:%i<BR>\r\n",
		 datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec);
	WriteRoom(name, password, room, 0);
	KillGame();
}				/* endproc */

void
Date_Command(char *name, char *password, int room)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteSentenceIntoOwnLogFile(logname, "Current date is %i-%i-%i<BR>\r\n",
		datumtijd.tm_mon + 1, datumtijd.tm_mday, datumtijd.tm_year+1900);
	WriteRoom(name, password, room, 0);
	KillGame();
}				/* endproc */

void
LookSky_Command(char *name, char *password)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	LookAtProc(-10, name, password);
	KillGame();
}				/* endproc */

