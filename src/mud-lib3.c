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
		WriteSentenceIntoOwnLogFile2(logname, "You can't go that way.<BR>\r\n");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage2(name, room, "%s attempts to leave west, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile2(logname, "You are exhausted, and can't move.<BR>\r\n");
		}
		else   
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage2(name, room, "%s attempts to leave west, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile2(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage2(name, room, "%s leaves west.<BR>\r\n", name);
				room = temproom->west;
				sprintf(temp, "update tmp_usertable set room=%i where name='%s'"
								, room, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteMessage2(name, room, "%s appears.<BR>\r\n", name);
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
		WriteSentenceIntoOwnLogFile2(logname, "You can't go that way.<BR>\r\n");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage2(name, room, "%s attempts to leave east, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile2(logname, "You are exhausted, and can't move.<BR>\r\n");
		}
		else
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage2(name, room, "%s attempts to leave east, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile2(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage2(name, room, "%s leaves east.<BR>\r\n", name);
				room = temproom->east;
				sprintf(temp, "update tmp_usertable set room=%i, "
				"movementstats=%i where name='%s'"
				, room, movementstats, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteMessage2(name, room, "%s appears.<BR>\r\n", name);
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
		WriteSentenceIntoOwnLogFile2(logname, "You can't go that way.<BR>\r\n");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage2(name, room, "%s attempts to leave north, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile2(logname, "You are exhausted, and can't move.<BR>\r\n");
		}
		else
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage2(name, room, "%s attempts to leave north, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile2(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage2(name, room, "%s leaves north.<BR>\r\n", name);
				room = temproom->north;
				sprintf(temp, "update tmp_usertable set room=%i, "
					"movementstats=%i where name='%s'"
								, room, movementstats, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteMessage2(name, room, "%s appears.<BR>\r\n", name);
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
		WriteSentenceIntoOwnLogFile2(logname, "You can't go that way.<BR>\r\n");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage2(name, room, "%s attempts to leave south, but is exhausted.<BR>\r\n", name, name);
			WriteSentenceIntoOwnLogFile2(logname, "You are exhausted, and can't move.<BR>\r\n");
		}
		else   
		{
			/* if NOT exhausted */
			int burden;
			burden = CheckWeight(name);
			/* if burden too heavy to move */
			if (computeEncumberance(burden, strength) == -1)
			{
				WriteMessage2(name, room, "%s attempts to leave south, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile2(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage2(name, room, "%s leaves south.<BR>\r\n", name);
				room = temproom->south;
				sprintf(temp, "update tmp_usertable set room=%i where name='%s'"
								, room, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteMessage2(name, room, "%s appears.<BR>\r\n", name);
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

	WriteSentenceIntoOwnLogFile2(logname, "You go to sleep.<BR>\n");
	WriteMessage2(name, room, "%s goes to sleep.<BR>\n", name);
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

	WriteSentenceIntoOwnLogFile2(logname, "You wake up.<BR>\n");
	WriteMessage2(name, room, "%s wakes up. %s is now wide awake.<BR>\n", name, name);
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
	WriteSentenceIntoOwnLogFile2(logname, "Current time is %i:%i:%i<BR>\r\n",
		 datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec);
	WriteRoom(name, password, room, 0);
	KillGame();
}				/* endproc */

void
Date_Command(char *name, char *password, int room)
{
	char logname[100];  
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteSentenceIntoOwnLogFile2(logname, "Current date is %i-%i-%i<BR>\r\n",
		datumtijd.tm_mon + 1, datumtijd.tm_mday, datumtijd.tm_year);
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

void
SwitchRoomCheck(char *name, char *password, int room)
{
	char logname[100];  
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
                        
	sprintf(logname, "%s%s.log",USERHeader,name);

	switch (room)
	{
		case 1:
		{
			if (!strcmp(troep, "pull chain3"))
			{
				sprintf(temp, "select id from items "
				"where (id=-3) and (adject3='loose')");
				res=SendSQL2(temp, NULL);
				if (res!=NULL)
				{
					row = mysql_fetch_row(res);
					if (row!=NULL)
					{
						mysql_free_result(res);
						WriteSentenceIntoOwnLogFile2(logname, "You pull vehemently, but nothing exciting happens.<BR>\r\n");
						WriteRoom(name, password, room, 0);
						KillGame();
					}
				}
				mysql_free_result(res);
				sprintf(temp, "update items set adject3='loose' where id=-3");
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteSentenceIntoOwnLogFile2(logname, "You let go of the chain, wondering what that was all about.<BR>\r\n");
				WriteMessage2(name, room, "%s pulls at the chain in the wall. "
				"You hear the ominous sounds of wheels churning and rope snapping, yet the "
				"sound suddenly stops again.<BR>\r\n", name);

				res=SendSQL2("select contents from action where id=1", NULL);
				row = mysql_fetch_row(res);
				LookString(row[0], name, password);
				mysql_free_result(res);

				KillGame();
			}
			if (!strcmp(troep, "jump idn lake"))
			{
				sprintf(temp, "select id from items "
				"where (id=-3) and (adject3='loose')");
				res=SendSQL2(temp, NULL);
				if (res==NULL)
				{
					WriteMessage2(name, room, "%s appears from nowhere.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "You appear from nowhere.<BR>\r\n");
					res=SendSQL2("select contents from action where id=2", NULL);
					row = mysql_fetch_row(res);
					LookString(row[0], name, password);
					mysql_free_result(res);
					KillGame();
				}
				row = mysql_fetch_row(res);
				if (row==NULL)
				{
					WriteMessage2(name, room, "%s appears from nowhere.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "You appear from nowhere.<BR>\r\n");
					res=SendSQL2("select contents from action where id=2", NULL);
					row = mysql_fetch_row(res);
					LookString(row[0], name, password);
					mysql_free_result(res);
					KillGame();
				}
				mysql_free_result(res);

				WriteMessage2(name, room, "%s jumps into the lake.<BR>\r\n", name);
				room=17;
				WriteMessage2(name, room, "%s crawls out of the lake.<BR>\r\n", name);

				WriteSentenceIntoOwnLogFile2(logname, "You crawl out of the lake, dripping wet.<BR>\r\n");
				
				res=SendSQL2("select contents from action where id=3", NULL);
				row = mysql_fetch_row(res);
				LookString(row[0], name, password);
				mysql_free_result(res);

				sprintf(temp, "update tmp_usertable set room=17 where name='%s'", name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				KillGame();
			}
			break;
		}
		case 4:
		{
			if (!strcmp(troep, "use pick with stones")) 
			{
				sprintf(temp, "select wielding from tmp_itemtable tmpitems "
				"where (tmpitems.id=2) and "
				"(tmpitems.room = 0) and "
				"(tmpitems.search = '') and "
				"(tmpitems.belongsto = '%s') and "
				"(tmpitems.wearing = '') and "
				"(tmpitems.wielding <> '')", name);
				res=SendSQL2(temp, NULL);
				if (res==NULL)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You are not wielding a pick.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				row = mysql_fetch_row(res);
				if (row==NULL)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You are not wielding a pick.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				sprintf(temp, "select south from rooms where id=4");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])!=0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "There are only small pebbles present.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);
				WriteSentenceIntoOwnLogFile2(logname, "You are hacking away at the rocks,"
				" using your pickaxe with a skill of which most people can only dream.<BR>\r\n");
				WriteMessage2(name, room, "%s is hacking away at the rocks, using a pickaxe.<BR>\r\n", name);
				res=SendSQL2("select contents from action where id=0", NULL);
				row = mysql_fetch_row(res);
				LookString(row[0], name, password);
				mysql_free_result(res);

				res=SendSQL2("update items set description='<H1>The Pebbles</H1><HR> "
				"You look at the pebbles. They are solid and very small and they are not "
				"blocking your entrance to the south. Somebody apparently has been hacking "
				"away at them, or they wouldn\\'t have this small a size.<P>"
				"', adject1='small', adject2='light', name='pebbles' where id=-16", NULL);
				mysql_free_result(res);

				res=SendSQL2("update items set description='<H1>The Pebbles</H1><HR> "
				"You look at the pebbles. They are solid and very small and they are not "
				"blocking your entrance to the north. Somebody apparently has been hacking "
				"away at them, or they wouldn\\'t have this small a size.<P>"
				"', adject1='small', adject2='light', name='pebbles' where id=-40", NULL);
				mysql_free_result(res);

				res=SendSQL2("update rooms set contents='<H1>The Other Side</H1> <Center>"
				"<IMG ALT=\"Mountain\" "
				"SRC=\"http://"ServerName"/images/jpeg/berg.jpg\"></Center><BR> "
				"You appear to be on the other side of a big range of mountains, which make "
				"out a lonely valley. This appears to be a whole range of mountains, shutting "
				"the people out from the outside world. At least, that would be the case "
				"if the passage you see to the north was blocked. Now, however, is seems to provide "
				"a clear exit to the north. Only some insignificant little pebbles bar "
				"your way.<P>"
				"', north=4 where id=23", NULL);
				mysql_free_result(res);

				res=SendSQL2("update rooms set contents='<H1>The Road</H1> <Center><IMG "
				"ALT=\"Mountain\" "
				"SRC=\"http://"ServerName"/images/jpeg/berg.jpg\"></Center><BR>"
				"<IMG SRC=\"http://"ServerName"/images/gif/letters/y.gif\" "
				"ALIGN=left>ou "
				"are standing on a road with leads North and South. Towards the south "
				"the road used to be blocked by heavy stones, but now there are only "
				"little light pebbles visible. You have a clear way towards the south now. "
				"Towards the north the road is tolerably well and would make travelling an "
				"easy matter. To the west a forest stretches out. It is impossible to go to "
				"the east due to the large mountains that are that way. <P>"
				"', south=23 where id=4", NULL);
				mysql_free_result(res);

				KillGame();
			}
			break;
		}
		case 8:
		{
			if (!strcmp(troep, "give stick to karaoke")) 
			{
				int i=0,j=0;
				sprintf(temp, "select amount from tmp_itemtable where id=56 and belongsto='%s' and wielding=''", name);
				res=SendSQL2(temp, NULL);
				if (res==NULL)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You do not have a stick.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				row = mysql_fetch_row(res);
				if (row==NULL)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You do not have a stick.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				i=atoi(row[0]);
				mysql_free_result(res);

				sprintf(temp, "select amount from tmp_itemtable where id=39 and belongsto='%s' and wielding=''", name);
				res=SendSQL2(temp, NULL);
				if (res!=NULL)
				{
					row = mysql_fetch_row(res);
					if (row!=NULL)
					{
						j=atoi(row[0]);
					}
				}
				mysql_free_result(res);

				WriteSentenceIntoOwnLogFile2(logname, "You give a stick to Karaoke.<BR>\r\n");
				WriteMessage2(name, room, "%s gives a stick to Karaoke.<BR>\r\n", name);
				WriteSentenceIntoOwnLogFile2(logname, "You receive a key from Karaoke.<BR>"
				"You now have an old rusty key.<BR>\r\n");
				WriteMessage2(name, room, "Karaoke gives a key to %s.<BR>\r\n", name);

				if (i==1)
				{
					sprintf(temp, "delete from tmp_itemtable where id=56 and belongsto='%s' and wielding=''", name);
					res=SendSQL2(temp, NULL);
				}
				else
				{
					sprintf(temp, "update tmp_itemtable set amount=amount-1 where id=56 and belongsto='%s' and wielding=''", name);
					res=SendSQL2(temp, NULL);
				}

				if (j==0)
				{
					sprintf(temp, "insert into tmp_itemtable values(39, '', '%s', 1, 0, '', '')", name);
					res=SendSQL2(temp, NULL);
				}
				else
				{
					sprintf(temp, "update tmp_itemtable set amount=amount+1 where id=39 and belongsto='%s' and wielding=''", name);
					res=SendSQL2(temp, NULL);
				}

				res=SendSQL2("select contents from action where id=11", NULL);
				row = mysql_fetch_row(res);
				LookString(row[0], name, password);
				mysql_free_result(res);
				KillGame();
			}
			break;
		}
		case 16:
		{
			if ((!strcmp(troep, "go down")) ||
				(!strcmp(troep, "down"))) 
			{
				sprintf(temp, "select room from tmp_usertable where name='Karcas'");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])==16)
				{
					mysql_free_result(res);
					WriteSentenceIntoOwnLogFile2(logname, "You try to move behind the counter, but Karcas immediately intervenes.<BR>\r\n");
					WriteMessage2(name, room, "%s tries to move behind the counter, but Karcas immediately intervenes.<BR>\r\n", name);
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				sprintf(temp, "select adject2 from items where name='hatch'");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (!strcmp(row[0],"wooden"))
				{
					mysql_free_result(res);
					WriteSentenceIntoOwnLogFile2(logname, "You can't go that way.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				WriteSentenceIntoOwnLogFile2(logname, "You go down the hatch.<BR>\r\n");
				WriteMessage2(name, room, "%s goes down the hatch.<BR>\r\n", name);
				room=26;

				sprintf(temp, "update tmp_usertable set room=26 where name='%s'", name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteMessage2(name, room, "%s appears from the hatch.<BR>\r\n", name);
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			if (!strcmp(troep, "open hatch"))
			{
				sprintf(temp, "select room from tmp_usertable where name='Karcas'");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])==16)
				{
					mysql_free_result(res);
					WriteMessage2(name, room, "%s tries to open the hatch, but Karcas prevents him from doing so.<BR>\r\n", name);
					res=SendSQL2("select contents from action where id=6", NULL);
					row = mysql_fetch_row(res);
					LookString(row[0], name, password);
					mysql_free_result(res);

					KillGame();
				}
				mysql_free_result(res);
				sprintf(temp, "select adject2 from items where name='hatch'");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (!strcmp(row[0],"open"))
				{
					mysql_free_result(res);
					WriteSentenceIntoOwnLogFile2(logname, "The hatch is already open, silly.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				WriteSentenceIntoOwnLogFile2(logname, "You open the hatch.<BR>\r\n");
				WriteMessage2(name, room, "%s tries to open the hatch, and is successfull!<BR>\r\n", name);

				res=SendSQL2("update items set adject2='open' where name='hatch'", NULL);
				mysql_free_result(res);
				res=SendSQL2("select contents from action where id=7", NULL);
				row = mysql_fetch_row(res);
				LookString(row[0], name, password);
				mysql_free_result(res);

				KillGame();
			}
			if (!strcmp(troep, "close hatch"))
			{
				sprintf(temp, "select room from tmp_usertable where name='Karcas'");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])==16)
				{
					mysql_free_result(res);
					WriteMessage2(name, room, "%s tries to close the hatch, but Karcas prevents him from doing so.<BR>\r\n", name);
					res=SendSQL2("select contents from action where id=6", NULL);
					row = mysql_fetch_row(res);
					LookString(row[0], name, password);
					mysql_free_result(res);

					KillGame();
				}
				mysql_free_result(res);
				sprintf(temp, "select adject2 from items where name='hatch'");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (!strcmp(row[0],"wooden"))
				{
					mysql_free_result(res);
					WriteSentenceIntoOwnLogFile2(logname, "The hatch is already closed, silly.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				WriteSentenceIntoOwnLogFile2(logname, "You close the hatch.<BR>\r\n");
				WriteMessage2(name, room, "%s closes the secret hatch<BR>\r\n", name);

				res=SendSQL2("update items set adject2='wooden' where name='hatch'", NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			break;
		} /* end 16 */
		case 20:
		{
			if (!strcmp(troep, "open chest")) 
			{
				sprintf(temp, "select adject1 from items where id=-91");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (!strcmp(row[0], "open"))
				{
					WriteSentenceIntoOwnLogFile2(logname, "The chest is already open.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				sprintf(temp, "select count(*) from tmp_itemtable where id=39"
				" and belongsto='%s'", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])==0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You do not have the key.<BR>\r\n");
					WriteMessage2(name, room, "%s tries to open the chest, but fails for lack of a key.<BR>\r\n", name);
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				WriteSentenceIntoOwnLogFile2(logname, "You open the chest.<BR>\r\n");
				WriteMessage2(name, room, "%s opens the chest.<BR>\r\n", name);

				res=SendSQL2("update items set description='<H1><IMG SRC=\"http://"ServerName"/images/gif/hidden04.gif\">"
				"The Chest</H1><HR> "
				"On the floor on the far side of the hidden room above the main room "
				"of the Inn you see a solid iron chest. It happens to be very old and very "
				"sturdy. It\\\'s lock is rusted and the lid is standing wide open. Everybody can "
				"look in the chest if they wanted to.<P>"
				"', adject1='open'  where id=-91", NULL);
				mysql_free_result(res);

				res=SendSQL2("update rooms set contents='<H1><IMG SRC=\"http://"ServerName"/images/gif/hidden03.gif\">"
				"The Hidden Room</H1>"
				"<font size=+3>Y</font>ou are in the middle of a hidden room on the upper "
				"floor of the Inn. It is "
				"very dusty and all around sticky old cobwebbs have been made by the usual "
				"little "
				"insects. On the far side of the room a strong iron chest can be seen. It "
				"seems to be open. Above "
				"you you can see a lot of wooden stuff to hold up the roof. To the south "
				"there is a way out, down the staircase that is visible there.<P>"
				"' where id=20", NULL);
				mysql_free_result(res);
				res=SendSQL2("select contents from action where id=12", NULL);
				row = mysql_fetch_row(res);
				LookString(row[0], name, password);
				mysql_free_result(res);
				KillGame();
			}
			if (!strcmp(troep, "close chest")) 
			{
				sprintf(temp, "select adject1 from items where id=-91");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (strcmp(row[0], "open"))
				{
					WriteSentenceIntoOwnLogFile2(logname, "The chest is already closed.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				sprintf(temp, "select count(*) from tmp_itemtable where id=39"
				" and belongsto='%s'", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])==0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You do not have the key.<BR>\r\n");
					WriteMessage2(name, room, "%s tries to close the chest, but fails for lack of a key.<BR>\r\n", name);
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				WriteSentenceIntoOwnLogFile2(logname, "You close the chest. [<A HREF=\"http://"ServerName"/images/mpeg/hid2.mpg\">MPEG</A>]<BR>\r\n");
				WriteMessage2(name, room, "%s closes the chest.<BR>\r\n", name);

				res=SendSQL2("update items set description='<H1>The Lock</H1><HR> "
				"You look carefully at the lock of the chest. It is very well made. "
				"Somebody at least knows his business. You can\\\'t open it, that is for sure. "
				"Why don\\\'t you try finding the key? There is probably no other way to open this "
				"chest but with the key.<P>"
				"', adject1='rusty'  where id=-91", NULL);
				mysql_free_result(res);

				res=SendSQL2("update rooms set contents='<H1><IMG SRC=\\\"http://"ServerName"/images/gif/hidden01.gif\\\">"
				"The Hidden Room</H1>"
				"<font size=+3>"
				"Y</font>ou are in the middle of a hidden room on the upper floor of the Inn. "
				"It is very dusty and all around sticky old cobwebbs have been made by the usual "
				"little "
				"insects. On the far side of the room a strong iron chest can be seen. Above "
				"you you can see a lot of wooden stuff to hold up the roof. To the south "
				"there is a way out, down the staircase that is visible there.<P>"
				"' where id=20", NULL);
				mysql_free_result(res);
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			break;
		}
		case 26:
		{
			if ((!strcmp(troep, "go up")) ||
				(!strcmp(troep, "up"))) 
			{
				sprintf(temp, "select room from tmp_usertable where name='Karcas'");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])==16)
				{
					mysql_free_result(res);
					WriteMessage2(name, room, "%s leaves up, through the hatch.<BR>\r\n", name);
					room=16;
					WriteMessage2(name, room, "You see %s slowly coming out of a hatch behind the counter.<BR>\r\n"
					"Karcas says : What were you doing down there! I'll teach"
					" you to mess with me!<BR>\r\n"
					"He approaches %s, and throws %s out of his shop.<BR>\r\n", name, name, name);
					room=5;
					WriteMessage2(name, room, "You look east and see %s flying through the air.<BR>"
					"%s comes down on the ground, slides west and hits the head on the well.<BR>\r\n", name, name);
					res=SendSQL2("select contents from action where id=8", NULL);
					row = mysql_fetch_row(res);
					LookString(row[0], name, password);
					mysql_free_result(res);

					sprintf(temp, "update tmp_usertable set room=5 where name='%s'", name);
					res=SendSQL2(temp, NULL);
					mysql_free_result(res);

					KillGame();
				}
				mysql_free_result(res);

				WriteSentenceIntoOwnLogFile2(logname, "You disappear through the hatch, only to reappear safely in Karcass shop.<BR>\r\n");
				WriteMessage2(name, room, "%s disappears through the hatch.<BR>\r\n", name);
				room=16;
				WriteMessage2(name, room, "A hidden hatch behind the counter opens, and you notice %s climbing out.<BR>\r\n", name);

				sprintf(temp, "update tmp_usertable set room=16 where name='%s'", name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			break;
		} /* end 26 */
		case 17:
		{
			if (!strcmp(troep, "jump in lake"))
			{
				sprintf(temp, "select id from items "
				"where (id=-3) and (adject3='loose')");
				res=SendSQL2(temp, NULL);
				if (res==NULL)
				{
					sprintf(temp, "update tmp_usertable set room=1 where name='%s'", name);
					res=SendSQL2(temp, NULL);
					mysql_free_result(res);
					room=1;
					WriteMessage2(name, room, "%s appears from nowhere.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "You appear from nowhere.<BR>\r\n");
					res=SendSQL2("select contents from action where id=4", NULL);
					row = mysql_fetch_row(res);
					LookString(row[0], name, password);
					mysql_free_result(res);
					KillGame();
				}
				row = mysql_fetch_row(res);
				if (row==NULL)
				{
					sprintf(temp, "update tmp_usertable set room=1 where name='%s'", name);
					res=SendSQL2(temp, NULL);
					mysql_free_result(res);
					room=1;
					WriteMessage2(name, room, "%s appears from nowhere.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "You appear from nowhere.<BR>\r\n");
					res=SendSQL2("select contents from action where id=4", NULL);
					row = mysql_fetch_row(res);
					LookString(row[0], name, password);
					mysql_free_result(res);
					KillGame();
				}
				mysql_free_result(res);

				WriteMessage2(name, room, "%s jumps into the lake.<BR>\r\n", name);
				room=1;
				WriteMessage2(name, room, "%s crawls out of the lake.<BR>\r\n", name);

				WriteSentenceIntoOwnLogFile2(logname, "You crawl out of the lake, dripping wet.<BR>\r\n");
				
				res=SendSQL2("select contents from action where id=5", NULL);
				row = mysql_fetch_row(res);
				LookString(row[0], name, password);
				mysql_free_result(res);

				sprintf(temp, "update tmp_usertable set room=1 where name='%s'", name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				KillGame();
			}
			break;
		}
		case 23 :
		{
			if (!strcmp(troep, "use pick with stones")) 
			{
				sprintf(temp, "select wielding from tmp_itemtable tmpitems "
				"where (tmpitems.id=2) and "
				"(tmpitems.room = 0) and "
				"(tmpitems.search = '') and "
				"(tmpitems.belongsto = '%s') and "
				"(tmpitems.wearing = '') and "
				"(tmpitems.wielding <> '')", name);
				res=SendSQL2(temp, NULL);
				if (res==NULL)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You are not wielding a pick.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				row = mysql_fetch_row(res);
				if (row==NULL)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You are not wielding a pick.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				sprintf(temp, "select south from rooms where id=4");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])!=0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "There are only small pebbles present.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);
				WriteSentenceIntoOwnLogFile2(logname, "You are hacking away at the rocks,"
				" using your pickaxe with a skill of which most people can only dream.<BR>\r\n");
				WriteMessage2(name, room, "%s is hacking away at the rocks, using a pickaxe.<BR>\r\n", name);
				res=SendSQL2("select contents from action where id=0", NULL);
				row = mysql_fetch_row(res);
				LookString(row[0], name, password);
				mysql_free_result(res);

				res=SendSQL2("update items set description='<H1>The Pebbles</H1><HR> "
				"You look at the pebbles. They are solid and very small and they are not "
				"blocking your entrance to the south. Somebody apparently has been hacking "
				"away at them, or they wouldn\\'t have this small a size.<P>"
				"', adject1='small', adject2='light', name='pebbles' where id=-16", NULL);
				mysql_free_result(res);

				res=SendSQL2("update items set description='<H1>The Pebbles</H1><HR> "
				"You look at the pebbles. They are solid and very small and they are not "
				"blocking your entrance to the north. Somebody apparently has been hacking "
				"away at them, or they wouldn\\'t have this small a size.<P>"
				"', adject1='small', adject2='light', name='pebbles' where id=-40", NULL);
				mysql_free_result(res);

				res=SendSQL2("update rooms set contents='<H1>The Other Side</H1> <Center>"
				"<IMG ALT=\"Mountain\" "
				"SRC=\"http://"ServerName"/images/jpeg/berg.jpg\"></Center><BR> "
				"You appear to be on the other side of a big range of mountains, which make "
				"out a lonely valley. This appears to be a whole range of mountains, shutting "
				"the people out from the outside world. At least, that would be the case "
				"if the passage you see to the north was blocked. Now, however, is seems to provide "
				"a clear exit to the north. Only some insignificant little pebbles bar "
				"your way.<P>"
				"', north=4 where id=23", NULL);
				mysql_free_result(res);

				res=SendSQL2("update rooms set contents='<H1>The Road</H1> <Center><IMG "
				"ALT=\"Mountain\" "
				"SRC=\"http://"ServerName"/images/jpeg/berg.jpg\"></Center><BR>"
				"<IMG SRC=\"http://"ServerName"/images/gif/letters/y.gif\" "
				"ALIGN=left>ou "
				"are standing on a road with leads North and South. Towards the south "
				"the road used to be blocked by heavy stones, but now there are only "
				"little light pebbles visible. You have a clear way towards the south now. "
				"Towards the north the road is tolerably well and would make travelling an "
				"easy matter. To the west a forest stretches out. It is impossible to go to "
				"the east due to the large mountains that are that way. <P>"
				"', south=23 where id=4", NULL);
				mysql_free_result(res);

				KillGame();
			}
			break;
		} /*end 23*/
		case 55 :
		{
			if ( (!strcmp(troep, "lock door")) &&
				 ( (!strcmp(name, "Karn")) ||
				 (!strcmp(name, "Westril")) ) )
			{
				sprintf(temp, "select south from rooms where id=61");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])==0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "The door is already locked.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);
				WriteSentenceIntoOwnLogFile2(logname, "You lock the door.<BR>\r\n");
				WriteMessage2(name, room, "%s locks the door.<BR>\r\n", name);

				res=SendSQL2("update rooms set south=0 where id=61", NULL);
				mysql_free_result(res);
				res=SendSQL2("update rooms set north=0 where id=55", NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			if ( (!strcmp(troep, "unlock door")) &&
				 ( (!strcmp(name, "Karn")) ||
				 (!strcmp(name, "Westril")) ) )
			{
				sprintf(temp, "select south from rooms where id=61");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])!=0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "The door is already unlocked.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);
				WriteSentenceIntoOwnLogFile2(logname, "You unlock the door.<BR>\r\n");
				WriteMessage2(name, room, "%s unlocks the door.<BR>\r\n", name);

				res=SendSQL2("update rooms set south=55 where id=61", NULL);
				mysql_free_result(res);
				res=SendSQL2("update rooms set north=61 where id=55", NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			break;
		} /*end 55*/
		case 61 :
		{
			if (!strcmp(troep, "lock door")) 
			{
				sprintf(temp, "select south from rooms where id=61");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])==0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "The door is already locked.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);
				WriteSentenceIntoOwnLogFile2(logname, "You lock the door.<BR>\r\n");
				WriteMessage2(name, room, "%s locks the door.<BR>\r\n", name);

				res=SendSQL2("update rooms set south=0 where id=61", NULL);
				mysql_free_result(res);
				res=SendSQL2("update rooms set north=0 where id=55", NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			if (!strcmp(troep, "unlock door")) 
			{
				sprintf(temp, "select south from rooms where id=61");
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (atoi(row[0])!=0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "The door is already unlocked.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);
				WriteSentenceIntoOwnLogFile2(logname, "You unlock the door.<BR>\r\n");
				WriteMessage2(name, room, "%s unlocks the door.<BR>\r\n", name);

				res=SendSQL2("update rooms set south=55 where id=61", NULL);
				mysql_free_result(res);
				res=SendSQL2("update rooms set north=61 where id=55", NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			break;
		} /*end 61*/
		case 65:
		{
			if (!strcmp(troep, "minimize coins")) 
			{
				int itemcopper, itemsilver, itemgold;
				sprintf(temp, "select gold, silver, copper from tmp_usertable where name='%s'", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				itemgold=atoi(row[0]);
				itemsilver=atoi(row[1]);
				itemcopper=atoi(row[2]);
				mysql_free_result(res);

				WriteMessage2(name, room, "%s says [to Kroonz] :  Could you minimize my coins, please?<BR>\r\n", name);
				if (!PayUp(0,1,0,&itemgold,&itemsilver,&itemcopper)) 
				{
					WriteMessage2(name, room, "Kroonz says [to %s] :  You do not have enough money to pay for my services.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you] : You do not have enough money to pay for my services.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				WriteMessage2(name, room, "Kroonz says [to %s] : Your coins are minimized.<BR>\r\n", name);
				WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: Your coins are minimized.<BR>\r\n");
				
				while (itemcopper>9)
				{
					itemcopper-=10;
					itemsilver++;
				}
				while (itemsilver>9)
				{
					itemsilver-=10;
					itemgold++;
				}
				sprintf(temp, "update tmp_usertable set "
				"gold=%i, silver=%i, copper=%i where name='%s'",
				itemgold, itemsilver, itemcopper, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			} /*endofminimizecoins*/
			if ((aantal==4) && (!strcmp("change", tokens[0])) &&
				(!strcmp("to", tokens[2])) )
			{
				int itemcopper, itemsilver, itemgold;
				sprintf(temp, "select gold, silver, copper from tmp_usertable where name='%s'", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				itemgold=atoi(row[0]);
				itemsilver=atoi(row[1]);
				itemcopper=atoi(row[2]);
				mysql_free_result(res);

				WriteMessage2(name, room, "%s says [to Kroonz] :  Could you change my %s coins to %s?<BR>\r\n", name, tokens[1], tokens[3]);
				if (!PayUp(0,1,0,&itemgold,&itemsilver,&itemcopper)) 
				{
					WriteMessage2(name, room, "Kroonz says [to %s] :  You do not have enough money to pay for my services.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you] : You do not have enough money to pay for my services.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				WriteMessage2(name, room, "Kroonz hands %s money.<BR>\r\n", name);
				WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: Done.<BR>Kroonz gives you your money.<BR>\r\n");
				
				if ((!strcmp(tokens[1], "copper")) && (!strcmp(tokens[3], "silver")) )
				{
					while (itemcopper>9)
					{
						itemcopper-=10;
						itemsilver++;
					}
				}
				if ((!strcmp(tokens[1], "copper")) && (!strcmp(tokens[3], "gold")) )
				{
					while (itemcopper>99)
					{
						itemcopper-=100;
						itemgold++;
					}
				}
				if ((!strcmp(tokens[1], "silver")) && (!strcmp(tokens[3], "gold")) )
				{
					while (itemsilver>9)
					{
						itemsilver-=10;
						itemgold++;
					}
				}
				if ((!strcmp(tokens[1], "silver")) && (!strcmp(tokens[3], "copper")) )
				{
					while (itemsilver>0)
					{
						itemcopper+=10;
						itemsilver--;
					}
				}
				if ((!strcmp(tokens[1], "gold")) && (!strcmp(tokens[3], "copper")) )
				{
					while (itemgold>0)
					{
						itemcopper+=100;
						itemgold--;
					}
				}
				if ((!strcmp(tokens[1], "gold")) && (!strcmp(tokens[3], "silver")) )
				{
					while (itemgold>0)
					{
						itemsilver+=10;
						itemgold--;
					}
				}
				
				sprintf(temp, "update tmp_usertable set "
				"gold=%i, silver=%i, copper=%i where name='%s'",
				itemgold, itemsilver, itemcopper, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			} /*endofminimizesomecoins*/
			if ((aantal==5) && (!strcmp("change", tokens[0])) &&
				(!strcmp("to", tokens[3])) )
			{
				int itemcopper, itemsilver, itemgold, itemchange, success;
				sprintf(temp, "select gold, silver, copper from tmp_usertable where name='%s'", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				itemgold=atoi(row[0]);
				itemsilver=atoi(row[1]);
				itemcopper=atoi(row[2]);
				itemchange=atoi(tokens[1]);
				mysql_free_result(res);

				WriteMessage2(name, room, "%s says [to Kroonz] :  Could you change some coins for me?<BR>\r\n", name);
				if (!PayUp(0,1,0,&itemgold,&itemsilver,&itemcopper)) 
				{
					WriteMessage2(name, room, "Kroonz says [to %s] :  You do not have enough money to pay for my services.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you] : You do not have enough money to pay for my services.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				
				success=0;
				if (itemchange>0)
				{
					if ((!strcmp(tokens[2], "copper")) && (!strcmp(tokens[4], "silver")) )
					{
						if (itemchange<=itemcopper) {
						itemsilver+=itemchange/10;success=1;
						itemcopper-=(itemchange - (itemchange % 10));
						}
					}
					if ((!strcmp(tokens[2], "copper")) && (!strcmp(tokens[4], "gold")) )
					{
						if (itemchange<=itemcopper) {
						itemgold+=itemchange/100;success=1;
						itemcopper-=(itemchange - (itemchange % 100));
						}
					}
					if ((!strcmp(tokens[2], "silver")) && (!strcmp(tokens[4], "gold")) )
					{
						if (itemchange<=itemsilver) {
						itemgold+=itemchange/10;success=1;
						itemsilver-=(itemchange - (itemchange % 10));
						}
					}
					if ((!strcmp(tokens[2], "silver")) && (!strcmp(tokens[4], "copper")) )
					{
						if (itemchange<=itemsilver) {
						itemcopper+=itemchange*10;success=1;
						itemsilver-=itemchange;
						}
					}
					if ((!strcmp(tokens[2], "gold")) && (!strcmp(tokens[4], "copper")) )
					{
						if (itemchange<=itemgold) {
						itemcopper+=itemchange*100;success=1;
						itemgold-=itemchange;
						}
					}
					if ((!strcmp(tokens[2], "gold")) && (!strcmp(tokens[4], "silver")) )
					{
						if (itemchange<=itemgold) {
						itemsilver+=itemchange*10;success=1;
						itemgold-=itemchange;
						}
					}
				}
				
				if (success==0)
				{
					WriteMessage2(name, room, "Kroonz says [to %s]: You do not have that sort of cash.<BR>Kroonz disparagingly shakes his head.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: You do not have that sort of cash.<BR>Kroonz disparagingly shakes his head.<BR>\r\n");
				}
				else
				{
					WriteMessage2(name, room, "Kroonz hands %s money.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: Done.<BR>Kroonz gives you your money.<BR>\r\n");
					sprintf(temp, "update tmp_usertable set "
					"gold=%i, silver=%i, copper=%i where name='%s'",
					itemgold, itemsilver, itemcopper, name);
					res=SendSQL2(temp, NULL);
					mysql_free_result(res);
				}
				WriteRoom(name, password, room, 0);
				KillGame();
			} /*endofminimizespecificnumberofcoins*/
			if (!strcmp(troep, "open account")) 
			{
				sprintf(temp, "select count(*) from tmp_itemtable where "
				"id=36 and search='%s' and belongsto='' and room=-1 and wearing='' and wielding=''", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				WriteMessage2(name, room, "%s says [to Kroonz] :  Can you open an accont for me?<BR>\r\n", name);
				if (strcmp(row[0], "0"))
				{
					WriteMessage2(name, room, "Kroonz says [to %s] : You already have an account here.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: You already have an account here.<BR>\r\n");
					mysql_free_result(res);
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				WriteMessage2(name, room, "Kroonz says [to %s] : You now have an account.<BR>\r\n", name);
				WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: You now have an account.<BR>\r\n");
				
				sprintf(temp, "insert into tmp_itemtable values(36, '%s', '', 0, -1, '','')",
				name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				sprintf(temp, "insert into tmp_itemtable values(37, '%s', '', 0, -1, '','')",
				name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				sprintf(temp, "insert into tmp_itemtable values(38, '%s', '', 0, -1, '','')",
				name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			} /*endofopenaccount*/
			if (!strcmp(troep, "close account")) 
			{
				int copper, silver, gold;
				sprintf(temp, "select count(*) from tmp_itemtable where "
				"id=36 and search='%s' and belongsto='' and room=-1 and wearing='' and wielding=''", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				WriteMessage2(name, room, "%s says [to Kroonz] :  Can you close my account?<BR>\r\n", name);
				if (!strcmp(row[0], "0"))
				{
					WriteMessage2(name, room, "Kroonz says [to %s] : You do not have an account here.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: You do not have an account here.<BR>\r\n");
					mysql_free_result(res);
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				sprintf(temp, "select a.amount, b.amount, c.amount "
				"from tmp_itemtable a, tmp_itemtable b, tmp_itemtable c where "
				"a.id=36 and b.id=37 and c.id=38 and "
				"a.search='%s' and b.search='%s' and c.search='%s' and "
				"a.belongsto='' and b.belongsto='' and c.belongsto='' and "
				"a.room=-1 and b.room=-1 and c.room=-1 and "
				"a.wearing='' and b.wearing='' and c.wearing='' and "
				"a.wielding='' and b.wielding='' and c.wielding=''", name, name, name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				copper = atoi(row[0]);
				silver = atoi(row[1]);
				gold = atoi(row[2]);
				mysql_free_result(res);

				WriteMessage2(name, room, "Kroonz gives %s %i copper coins, %i silver coins and %i gold coins.<BR>\r\n", name, copper, silver, gold);
				WriteSentenceIntoOwnLogFile2(logname, "Kroonz gives you %i copper coins, %i silver coins and %i gold coins.<BR>\r\n", copper, silver, gold);
				
				sprintf(temp, "update tmp_usertable set "
				"copper=copper+%i, silver=silver+%i, gold=gold+%i "
				"where name='%s'", copper, silver, gold, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				sprintf(temp, "delete from tmp_itemtable "
				"where room=-1 and search='%s'", name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			} /*endofcloseaccount*/
			if (!strcmp(troep, "show balance")) 
			{
				int copper, silver, gold;
				sprintf(temp, "select count(*) from tmp_itemtable where "
				"id=36 and search='%s' and belongsto='' and room=-1 and wearing='' and wielding=''", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				WriteMessage2(name, room, "%s says [to Kroonz] :  Show me the balance of my account.<BR>\r\n", name);
				if (!strcmp(row[0], "0"))
				{
					WriteMessage2(name, room, "Kroonz says [to %s] : You do not have an account here.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: You do not have an account here.<BR>\r\n");
					mysql_free_result(res);
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				sprintf(temp, "select a.amount, b.amount, c.amount "
				"from tmp_itemtable a, tmp_itemtable b, tmp_itemtable c where "
				"a.id=36 and b.id=37 and c.id=38 and "
				"a.search='%s' and b.search='%s' and c.search='%s' and "
				"a.belongsto='' and b.belongsto='' and c.belongsto='' and "
				"a.room=-1 and b.room=-1 and c.room=-1 and "
				"a.wearing='' and b.wearing='' and c.wearing='' and "
				"a.wielding='' and b.wielding='' and c.wielding=''", name, name, name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				copper = atoi(row[0]);
				silver = atoi(row[1]);
				gold = atoi(row[2]);
				mysql_free_result(res);

				WriteMessage2(name, room, "Kroonz whispers something to %s.<BR>\r\n", name);
				WriteSentenceIntoOwnLogFile2(logname, "Kroonz whispers [to you]: You currently have %i copper coins, %i silver coins and %i gold coins in your account.<BR>\r\n", copper, silver, gold);
				
				WriteRoom(name, password, room, 0);
				KillGame();
			} /*endofshowbalance*/
			if ((aantal==4) && (!strcmp("deposit", tokens[0])) &&
				(!strcmp("coins", tokens[3])) && 
				( (!strcmp("gold", tokens[2])) ||
				  (!strcmp("silver", tokens[2])) ||
				  (!strcmp("copper", tokens[2])) ) )
			{
				/* check if accounts present */
				/* check if money entered is positive */
				/* check if money in posession is greater or equal then the amount of money to be
				deposited*/
				int money, moneyowned;
				int coinid;
				money = atoi(tokens[1]);
				if (!strcmp("copper", tokens[2])) {coinid=36;}
				if (!strcmp("silver", tokens[2])) {coinid=37;}
				if (!strcmp("gold", tokens[2])) {coinid=38;}
				sprintf(temp, "select count(*) from tmp_itemtable where "
				"id=36 and search='%s' and belongsto='' and room=-1 and wearing='' and wielding=''", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				WriteMessage2(name, room, "%s says [to Kroonz] :  Can you deposit %i %s coins in my account?<BR>\r\n", name, money, tokens[2]);
				if (!strcmp(row[0], "0"))
				{
					WriteMessage2(name, room, "Kroonz says [to %s] : You do not have an account here.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: You do not have an account here.<BR>\r\n");
					mysql_free_result(res);
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				if (money < 0)
				{
					WriteMessage2(name, room, "Kroonz says [to %s] : Very funny...<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: Very funny...<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}

				sprintf(temp, "select %s from tmp_usertable where "
				"name='%s'", tokens[2], name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				moneyowned = atoi(row[0]);
				mysql_free_result(res);
				if (moneyowned < money)
				{
					WriteMessage2(name, room, "Kroonz says [to %s] : You do not have that much money...<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: You do not have that much money...<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				
				sprintf(temp, "update tmp_usertable set %s=%s-%i "
				"where name='%s'",
				tokens[2], tokens[2], money, name);
				res = SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteMessage2(name, room, "Kroonz receives %i %s coins from %s and writes something in a ledger.<BR>\r\n", money, tokens[2], name);
				WriteSentenceIntoOwnLogFile2(logname, "You deposit %i %s coins in your account.<BR>\r\n", money, tokens[2]);
				
				sprintf(temp, "update tmp_itemtable set "
				"amount = amount + %i "
				"where id=%i and "
				"search='%s' and "
				"belongsto='' and "
				"room=-1 and "
				"wearing='' and "
				"wielding=''", money, coinid, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			} /*endofdepositmoneyintoaccount*/
			if ((aantal==4) && (!strcmp("withdraw", tokens[0])) &&
				(!strcmp("coins", tokens[3])) && 
				( (!strcmp("gold", tokens[2])) ||
				  (!strcmp("silver", tokens[2])) ||
				  (!strcmp("copper", tokens[2])) ) )
			{
				/* check if account is present */
				/* check if money entered is positive */
				/* check if money in account is greater or equal then the amount of money to be
				withdrawn*/
				int money, moneyaccount;
				int coinid;
				money = atoi(tokens[1]);
				if (!strcmp("copper", tokens[2])) {coinid=36;}
				if (!strcmp("silver", tokens[2])) {coinid=37;}
				if (!strcmp("gold", tokens[2])) {coinid=38;}
				sprintf(temp, "select count(*) from tmp_itemtable where "
				"id=36 and search='%s' and belongsto='' and room=-1 and wearing='' and wielding=''", name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				WriteMessage2(name, room, "%s says [to Kroonz] :  Please withdraw %i %s coins from my account.<BR>\r\n", name, money, tokens[2]);
				if (!strcmp(row[0], "0"))
				{
					WriteMessage2(name, room, "Kroonz says [to %s] : You do not have an account here.<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: You do not have an account here.<BR>\r\n");
					mysql_free_result(res);
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				mysql_free_result(res);

				if (money < 0)
				{
					WriteMessage2(name, room, "Kroonz says [to %s] : Very funny...<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: Very funny...<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}

				sprintf(temp, "select amount from tmp_itemtable "
				"where id=%i and "
				"search='%s' and "
				"belongsto='' and "
				"room=-1 and "
				"wearing='' and "
				"wielding=''", coinid, name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				moneyaccount = atoi(row[0]);
				mysql_free_result(res);
				if (moneyaccount < money)
				{
					WriteMessage2(name, room, "Kroonz says [to %s] : You do not have that much money in your account...<BR>\r\n", name);
					WriteSentenceIntoOwnLogFile2(logname, "Kroonz says [to you]: You do not have that much money in your account...<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				
				sprintf(temp, "update tmp_usertable set %s=%s+%i "
				"where name='%s'",
				tokens[2], tokens[2], money, name);
				res = SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteMessage2(name, room, "Kroonz gives %i %s coins to %s and writes something in a ledger.<BR>\r\n", money, tokens[2], name);
				WriteSentenceIntoOwnLogFile2(logname, "You withdraw %i %s coins from your account.<BR>\r\n", money, tokens[2]);
				
				sprintf(temp, "update tmp_itemtable set "
				"amount = amount - %i "
				"where id=%i and "
				"search='%s' and "
				"belongsto='' and "
				"room=-1 and "
				"wearing='' and "
				"wielding=''", money, coinid, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
			} /*endofwithdrawmoneyfromaccount*/
			break;	
		} /*end of 65*/
		case 88 :
		{
			if (!strcmp(troep, "pay west")) 
			{
				int itemcopper, itemsilver, itemgold;
				sprintf(temp, "select gold, silver, copper from tmp_usertable where name='%s'",
				name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				itemgold=atoi(row[0]);
				itemsilver=atoi(row[1]);
				itemcopper=atoi(row[2]);
				mysql_free_result(res);

				if (!PayUp(0,1,0, &itemgold, &itemsilver, &itemcopper))
				{
					WriteSentenceIntoOwnLogFile2(logname, "You do not have enough money.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}

				WriteSentenceIntoOwnLogFile2(logname, "You pay Keanur and leave west.<BR>\r\n");
				WriteMessage2(name, room, "You notice %s paying Keanur and leaving west.<BR>\r\n", name);

				sprintf(temp, "update tmp_usertable set room=89, "
				"gold=%i, silver=%i, copper=%i where name='%s'",
				itemgold, itemsilver, itemcopper, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				room=89;
				WriteMessage2(name, room, "%s enters from the bridge.<BR>\r\n", name);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			if (!strcmp(troep, "pay east")) 
			{
				int itemcopper, itemsilver, itemgold;
				sprintf(temp, "select gold, silver, copper from tmp_usertable where name='%s'",
				name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				itemgold=atoi(row[0]);
				itemsilver=atoi(row[1]);
				itemcopper=atoi(row[2]);
				mysql_free_result(res);

				if (!PayUp(0,1,0, &itemgold, &itemsilver, &itemcopper))
				{
					WriteSentenceIntoOwnLogFile2(logname, "You do not have enough money.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}

				WriteSentenceIntoOwnLogFile2(logname, "You pay Keanur and leave east.<BR>\r\n");
				WriteMessage2(name, room, "You notice %s paying Keanur and leaving east.<BR>\r\n", name);

				sprintf(temp, "update tmp_usertable set room=45, "
				"gold=%i, silver=%i, copper=%i where name='%s'",
				itemgold, itemsilver, itemcopper, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				room=45;
				
				WriteMessage2(name, room, "%s enters from the bridge.<BR>\r\n", name);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			if ((!strcmp(troep, "go west")) ||
				(!strcmp(troep, "west")) ||
				(!strcmp(troep, "w"))) 
			{
				WriteSentenceIntoOwnLogFile2(logname, "You try to go west, yet Keanur hinders you.<BR>"
				"'I am sorry, this isn't a toll-free bridge, and while passing onto the bridge"
				" is free, leaving it costs 1 silver coin. Use <B>pay west</B>.', chuckles Keanur.<BR>\r\n");
				WriteMessage2(name, room, "%s tries to go west, yet Keanur hinders.<BR>"
				"'I am sorry, this isn't a toll-free bridge, and while passing onto the bridge"
				" is free, leaving it costs 1 silver coin. Use <B>pay west</B>.', chuckles Keanur.<BR>\r\n", name);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			if ((!strcmp(troep, "go east")) ||
				(!strcmp(troep, "east")) ||
				(!strcmp(troep, "e"))) 
			{
				WriteSentenceIntoOwnLogFile2(logname, "You try to go east, yet Keanur hinders you.<BR>"
				"'I am sorry, this isn't a toll-free bridge, and while passing onto the bridge"
				" is free, leaving it costs 1 silver coin. Use <B>pay east</B>.', chuckles Keanur.<BR>\r\n");
				WriteMessage2(name, room, "%s tries to go east, yet Keanur hinders.<BR>"
				"'I am sorry, this isn't a toll-free bridge, and while passing onto the bridge"
				" is free, leaving it costs 1 silver coin. Use <B>pay east</B>.', chuckles Keanur.<BR>\r\n", name);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			break;
		} /*end of 88*/
		case 182 :
		{
			if (!strcmp(troep, "pull stone")) 
			{
				WriteSentenceIntoOwnLogFile2(logname, "Nothing happens. I'm afraid you'll have to try something else.<BR>\r\n");
				WriteMessage2(name, room, "%s tries to pull the stone and ... ooh, how exciting... Nothing happens!<BR>\r\n", name);

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			if (!strcmp(troep, "push stone")) 
			{
				WriteMessage2(name, room, "%s tries to push the stone, the stone slides back"
				" into the wall, and suddenly %s begins to shimmer, and "
				"fade away from existence...<BR>\r\n", name, name);
				
				sprintf(temp, "update tmp_usertable set room=364 where name='%s'", name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				room=364;

				WriteSentenceIntoOwnLogFile2(logname, "You look around yourself and are surprised to see that your entire surroundings have changed.<BR>\r\n");

				WriteRoom(name, password, room, 0);
				KillGame();
			}
			break;
		} /*end of 182*/
		case 300 :
		{
			if ((aantal>1) && (!strcmp("learn", tokens[0])))
			{
				int i, skillid;
				skillid = -1;
				sprintf(temp, "select number from skills"
				" where name='%s'",
				troep + (tokens[1] - tokens[0]));
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (row != NULL)
				{
					skillid = atoi(row[0]);
				}
				mysql_free_result(res);
				if (skillid==-1)
				{
					WriteSentenceIntoOwnLogFile2(logname, "That particular skill is unknown.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				sprintf(temp, "select count(*) from skilltable"
				" where number=%i and forwhom='%s'",
				skillid, name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				i = atoi(row[0]);
				mysql_free_result(res);
				if (i!=0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You have already learned that skill.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				sprintf(temp, "select practises from tmp_usertable where name='%s'",
				name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				i = atoi(row[0]);
				mysql_free_result(res);
				if (i==0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You do not have enough practise sessions to learn that skill.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				sprintf(temp, "insert into skilltable values(%i, '%s', 0)", skillid, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				sprintf(temp, "update tmp_usertable set practises=practises-1 where name='%s'",
				name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteSentenceIntoOwnLogFile2(logname, "You %s.<BR>\r\n", troep);
				WriteMessage2(name, room, "%s learns %s.<BR>\r\n", name, troep + (tokens[1] - tokens[0]));
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			if ((aantal>1) && (!strcmp("practise", tokens[0])))
			{
				int i, skillid;
				skillid = -1;
				sprintf(temp, "select number from skills"
				" where name='%s'",
				troep + (tokens[1] - tokens[0]));
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				if (row != NULL)
				{
					skillid = atoi(row[0]);
				}
				mysql_free_result(res);
				if (skillid==-1)
				{
					WriteSentenceIntoOwnLogFile2(logname, "That particular skill is unknown.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				sprintf(temp, "select count(*) from skilltable"
				" where number=%i and forwhom='%s'",
				skillid, name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				i = atoi(row[0]);
				mysql_free_result(res);
				if (i==0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You do not have that skill.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				sprintf(temp, "select practises from tmp_usertable where name='%s'",
				name);
				res=SendSQL2(temp, NULL);
				row = mysql_fetch_row(res);
				i = atoi(row[0]);
				mysql_free_result(res);
				if (i==0)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You do not have enough practise sessions to practise that skill.<BR>\r\n");
					WriteRoom(name, password, room, 0);
					KillGame();
				}
				sprintf(temp, "update skilltable set skilllevel = skilllevel + 1 where number = %i and forwhom = '%s'", skillid, name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				sprintf(temp, "update tmp_usertable set practises=practises-1 where name='%s'",
				name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				WriteSentenceIntoOwnLogFile2(logname, "You %s.<BR>\r\n", troep);
				WriteMessage2(name, room, "%s practises %s.<BR>\r\n", name, troep + (tokens[1] - tokens[0]));
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			break;
		} /*end of 300*/
		case 364 :
		{
			if (!strcmp(troep, "get key")) 
			{
				WriteSentenceIntoOwnLogFile2(logname, "You get the key from the pillar, and immediately"
				" you appear to be back in the sewers. How odd, it just"
				" happened, and you didn't notice.<BR>\r\n");
				WriteMessage2(name, room, "%s takes the key from the pillar, yet another immediately appears.<BR>"
				"When you look around you, %s seems to have suddenly disappeared.<BR>\r\n", name, name);

				sprintf(temp, "update tmp_usertable set room=182 where name='%s'", name);
				res=SendSQL2(temp, NULL);
				mysql_free_result(res);
				room=182;

				WriteSentenceIntoOwnLogFile2(logname, "You look around yourself and are surprised to see that your entire surroundings have changed.<BR>\r\n");

				WriteRoom(name, password, room, 0);
				KillGame();
			}
		} /*end 364*/
	}
}
