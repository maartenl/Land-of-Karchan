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
extern char    *command;
extern char    *printstr;
extern struct tm datumtijd;
extern time_t   datetime;

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

	sprintf(logname, "%s%s.log",USERHeader,name);

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

	sprintf(logname, "%s%s.log",USERHeader,name);

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

	sprintf(logname, "%s%s.log",USERHeader,name);

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

	sprintf(logname, "%s%s.log",USERHeader,name);

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

int
Sleep_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
                        
	sprintf(logname, "%s%s.log",USERHeader,name);

	temp = composeSqlStatement("update tmp_usertable set sleep=1, jumpmana=jumpmana+1, jumpmove=jumpmove+2, jumpvital=jumpvital+3 where name='%x'", name);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, "You go to sleep.<BR>\n");
	WriteMessage(name, room, "%s goes to sleep.<BR>\n", name);
	WriteRoom(name, password, room, 1);
	return 1;
}

void
Awaken2_Command(char *name, char *password, int room)
{
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
                        
	sprintf(logname, "%s%s.log",USERHeader,name);

	temp = composeSqlStatement("update tmp_usertable set sleep=0, jumpmana=jumpmana-1, jumpmove=jumpmove-2, jumpvital=jumpvital-3 where name='%x'", name);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, "You wake up.<BR>\n");
	WriteMessage(name, room, "%s wakes up. %s is now wide awake.<BR>\n", name, name);
	WriteRoom(name, password, room, 0);
}

int
BigTalk_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	fprintf(getMMudOut(), "<HTML>\n");
	fprintf(getMMudOut(), "<HEAD>\n");
	fprintf(getMMudOut(), "<TITLE>\n");
	fprintf(getMMudOut(), "Land of Karchan - Big Talk\n");
	fprintf(getMMudOut(), "</TITLE>\n");
	fprintf(getMMudOut(), "</HEAD>\n");

	fprintf(getMMudOut(), "<BODY>\n");
	if (!getFrames())
	{
		fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
	fprintf(getMMudOut(), "<H1>Big Talk</H1>This is an alternative for typing. "
	"Due to the size of the area, this is where the really big messages are "
	"typed. This is ideally suited for exampe mudmail.<P>\r\n");

	fprintf(getMMudOut(), "<SCRIPT language=\"JavaScript\">\r\n"
			"<!-- In hiding!\r\n"
			"function setfocus() {\r\n"
			"       document.CommandForm.command.focus();\r\n"
			"	return;\r\n"
			"	}\r\n"
			"//-->\r\n"
			"</SCRIPT>\r\n");
	fprintf(getMMudOut(), "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", MudExe);
	fprintf(getMMudOut(), "<TEXTAREA NAME=\"command\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n");
	fprintf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	fprintf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	fprintf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"%i\">\n", getFrames()+1);
	fprintf(getMMudOut(), "<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n");
	fprintf(getMMudOut(), "</FORM><P>\n");
	if (getFrames()!=2) {ReadFile(logname);}
	fprintf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(getMMudOut(), "<DIV ALIGN=left><P></BODY></HTML>");
	return 1;
}				/* endproc */

int
MailFormDumpOnScreen(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	fprintf(getMMudOut(), "<HTML>\n");
	fprintf(getMMudOut(), "<HEAD>\n");
	fprintf(getMMudOut(), "<TITLE>\n");
	fprintf(getMMudOut(), "Land of Karchan - Mail to:\rn");
	fprintf(getMMudOut(), "</TITLE>\n");
	fprintf(getMMudOut(), "</HEAD>\n");

	fprintf(getMMudOut(), "<BODY>\n");
	if (!getFrames())
	{
		fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
	fprintf(getMMudOut(), "<H1>Mail To</H1><HR noshade>\r\n");

	fprintf(getMMudOut(), "<SCRIPT language=\"JavaScript\">\r\n"
		"<!-- In hiding!\r\n" 
		"function setfocus() {\r\n"
		" document.CommandForm.mailto.focus();\r\n"
		" return;\r\n"
		" }\r\n"
		"//-->\r\n"
		"</SCRIPT>\r\n");

	fprintf(getMMudOut(), "<TABLE BORDER=0>");
	fprintf(getMMudOut(), "<TR><TD>From:</TD><TD><B>%s</B></TD></TR>\r\n",name);
	fprintf(getMMudOut(), "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", MudExe);
	fprintf(getMMudOut(), "<TR><TD>To: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailto\" VALUE=\"\"></TD></TR>\r\n");
	fprintf(getMMudOut(), "<TR><TD>Header: </TD><TD><INPUT TYPE=\"text\" NAME=\"mailheader\" SIZE=80 VALUE=\"\"></TD></TR>\r\n");
	fprintf(getMMudOut(), "<TR><TD>Body:</TD><TD>\r\n");
	fprintf(getMMudOut(), "<TEXTAREA NAME=\"mailbody\" VALUE=\"\" ROWS=\"10\" COLS=\"85\"></TEXTAREA><P>\n");
	fprintf(getMMudOut(), "</TD></TR></TABLE><INPUT TYPE=\"hidden\" NAME=\"command\" VALUE=\"sendmail\">\n");
	fprintf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	fprintf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	fprintf(getMMudOut(), "<HR noshade><INPUT TYPE=\"submit\" VALUE=\"Sendmail\">\n");
	fprintf(getMMudOut(), "<INPUT TYPE=\"reset\" VALUE=\"Resetform\">\n");
	fprintf(getMMudOut(), "</FORM><P>\r\n");
	if (getFrames()!=2) {ReadFile(logname);}
	fprintf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(getMMudOut(), "<DIV ALIGN=left><P></BODY></HTML>");
	return 1;
}				/* endproc */

int
Time_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteSentenceIntoOwnLogFile(logname, "Current time is %i:%i:%i<BR>\r\n",
		 datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec);
	WriteRoom(name, password, room, 0);
	return 1;
}				/* endproc */

int
Date_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteSentenceIntoOwnLogFile(logname, "Current date is %i-%i-%i<BR>\r\n",
		datumtijd.tm_mon + 1, datumtijd.tm_mday, datumtijd.tm_year+1900);
	WriteRoom(name, password, room, 0);
	return 1;
}				/* endproc */

void
LookSky_Command(char *name, char *password)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	LookAtProc(-10, name, password);
}				/* endproc */

