/*-------------------------------------------------------------------------
cvsinfo: $Header$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This nifty program is distributed in the hope that it will be useful,
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

/* three strings destined for parsing the commands */
extern char    *command;
extern char    *printstr;
char    *myTokens[100];
extern struct tm datumtijd;
extern time_t   datetime;
extern char secretpassword[40];

char *name;		/* contains the name derived from the forms */
char *password;	/* contains the password derived from the forms */
int room;		/* contains the room where the character is located */
int godstatus;		/* contains the godstatus of the character */
char sexstatus[8];
int sleepstatus;	/* contains the sleepstatus of the character */
char guildstatus[10];		/* contains the guild a person belongsto */
int punishment;	/* contains wether or not you are normal, are a frog, or are a rat. */

// type-definition: 'gameFunction' now can be used as type
// parameters: name, password, room, command
typedef int (*gameFunction)(char *, char *, int, char *);

// the two indexes to be used for searching for commands rather quickly.
gameFunction	*gameFunctionArray; /* array of function types */
char				*gameCommands[] =
{"admin",
	"ask",
	"awaken",
	"bigtalk",
	"bow",
	"buy",
	"clear",
	"curtsey",
	"date",
	"deletemail",
	"down",
	"drink",
	"drop",
	"e",
	"east",
	"eat",
	"eyebrow",
//	"fight",
	"flinch",
	"get",
	"give",
	"go",
	"help",
	"i",
	"inventory",
	"l",
	"listmail",
	"look", 
	"magic",
	"mail",
	"me",
	"n",
	"nature",
	"north",
	"pkill",
	"put",
	"quit",
	"read",
	"readmail",
	"remove",
	"retrieve",
	"s",
	"say",
	"search",
	"sell",
	"sendmail",
	"shout",
	"sleep",
	"south",
	"stats",
	"stop",
	"tell",
	"time",
	"title",
	"unwield",
	"up",
	"w",
	"wear",
	"west",
	"whimpy",
	"whisper",
	"who",
	"wield", NULL}; /* string array */
int				theNumberOfFunctions = 0;
	
MYSQL_RES *res;
MYSQL_ROW row;
char sqlstring[1024];

int
clearGameFunctionIndex()
{
	free(gameFunctionArray);
}

int
GoDown_Command(char *name, char *password, int room, char *fcommand)
{
	roomstruct*temproom;
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

	if (!temproom->down)  {
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage(name, room, "%s attempts to leave north, but is exhausted.<BR>\r\n", name, name);
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
				WriteMessage(name, room, "%s attempts to leave north, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage(name, room, "%s leaves down.<BR>\r\n", name);
				room = temproom->down;
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
	return 1;
} 				/* endproc */

int
GoUp_Command(char *name, char *password, int room, char *fcommand)
{
	roomstruct*temproom;
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

	if (!temproom->up)  {
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>");
	} else {
		if (movementstats >= maxmove)
		{
			/* if exhausted */
			WriteMessage(name, room, "%s attempts to leave north, but is exhausted.<BR>\r\n", name, name);
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
				WriteMessage(name, room, "%s attempts to leave north, but is too heavily burdened.<BR>\r\n", name, name);
				WriteSentenceIntoOwnLogFile(logname, "You are carrying <I>way</I> too many items to move.<BR>\r\n");
			}
			else /* if burden NOT too heavy to move */
			{
				movementstats = movementstats + computeEncumberance(burden, strength);
				if (movementstats > maxmove) {movementstats = maxmove;}
				WriteMessage(name, room, "%s leaves up.<BR>\r\n", name);
				room = temproom->up;
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
	return 1;
} 				/* endproc */


void BannedFromGame(char *name, char *address)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	fprintf(getMMudOut(), "<HTML><HEAD><TITLE>You have been banned</TITLE></HEAD>\n\n");
	fprintf(getMMudOut(), "<BODY>\n");
	fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Banned</H1><HR>\n");
	fprintf(getMMudOut(), "You, or someone in your domain,  has angered the gods by behaving badly on this mud. ");
	fprintf(getMMudOut(), "Your ip domain is therefore banned from the game.<P>\n");
	fprintf(getMMudOut(), "If you have not misbehaved or even have never before played the game before, and wish"
	" to play with your current IP address, email to "
	"<A HREF=\"mailto:deputy@"ServerName"\">deputy@"ServerName"</A> and ask them to make "
	"an exception in your case. Do <I>not</I> forget to provide your "
	"Character name.<P>You'll be okay as long as you follow the rules.<P>\n");
	fprintf(getMMudOut(), "</body>\n");
	fprintf(getMMudOut(), "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-%i Banned from mud by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
}

void CookieNotFound(char *name, char *address)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	fprintf(getMMudOut(), "<HTML><HEAD><TITLE>Unable to logon</TITLE></HEAD>\n\n");
	fprintf(getMMudOut(), "<BODY>\n");
	fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Unable to logon</H1><HR>\n");
	fprintf(getMMudOut(), "When you logon, a cookie is automatically generated. ");
	fprintf(getMMudOut(), "However, I have been unable to find my cookie.<P>\n");
	fprintf(getMMudOut(), "Please attempt to relogon.<P>\n");
	fprintf(getMMudOut(), "</body>\n");
	fprintf(getMMudOut(), "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-%i Cookie not found for mud by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
}

int
Go_Command(char *name, char *password, int room, char *command)
{
	if (getTokenAmount() < 2)
	{
		return 0;
	}
	if (!strcasecmp(getToken(1), "west")) 
	{
		GoWest_Command(name, password, room, command);	
		return 1;
	}
	if (!strcasecmp(getToken(1), "east")) 
	{
		GoEast_Command(name, password, room, command);
		return 1;
	}
	if (!strcasecmp(getToken(1), "north"))
	{
		GoNorth_Command(name, password, room, command);
		return 1;
	}
	if (!strcasecmp(getToken(1), "south"))
	{
		GoSouth_Command(name, password, room, command);
		return 1;
	}
	if (!strcasecmp(getToken(1), "down"))
	{
		GoDown_Command(name, password, room, command);
		return 1;
	}
	if (!strcasecmp(getToken(1), "up"))
	{
		GoUp_Command(name, password, room, command);
		return 1;
	}
	return 0;
}

int
Clear_Command(char *name, char *password, int room, char *command)
{
	char logname[100];
	sprintf(logname, "%s%s.log", USERHeader, name);
	WriteRoom(name, password, room, 0);
	ClearLogFile(logname);
	WriteSentenceIntoOwnLogFile(logname, "You cleared your mind.<BR>\r\n");
	return 1;
}

int
Help_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (!strcasecmp(fcommand, "help")) 
	{
		MYSQL_RES *res;
		MYSQL_ROW row;
		int i;
		char temp[1024];
		
		fprintf(getMMudOut(), "<HTML>\r\n");
		fprintf(getMMudOut(), "<HEAD>\r\n");
		fprintf(getMMudOut(), "<TITLE>\r\n");
		fprintf(getMMudOut(), "Land of Karchan - General Help\r\n");
		fprintf(getMMudOut(), "</TITLE>\r\n");
		fprintf(getMMudOut(), "</HEAD>\r\n");
		
		fprintf(getMMudOut(), "<BODY>\r\n");
		fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\r\n");
	
		sprintf(temp, "select contents from help where command='general help'");
		res=SendSQL2(temp, NULL);
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			mysql_free_result(res);
			res=SendSQL2("select contents from help where command='sorry'", NULL);
			row = mysql_fetch_row(res);
			fprintf(getMMudOut(), "%s",row[0]);
		}
		else
		{
			fprintf(getMMudOut(), "%s",row[0]);
		}
		mysql_free_result(res);
		PrintForm(name, password);
		if (getFrames()!=2) {ReadFile(logname);}
		return 1;
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp(getToken(0),"help"))) 
	{
		MYSQL_RES *res;
		MYSQL_ROW row;
		int i;
		char temp[1024];
		
		fprintf(getMMudOut(), "<HTML>\r\n");
		fprintf(getMMudOut(), "<HEAD>\r\n");
		fprintf(getMMudOut(), "<TITLE>\r\n");
		fprintf(getMMudOut(), "Land of Karchan - Command %s\r\n", getToken(1));
		fprintf(getMMudOut(), "</TITLE>\r\n");
		fprintf(getMMudOut(), "</HEAD>\r\n");
		
		fprintf(getMMudOut(), "<BODY>\r\n");
		fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\r\n");
	
		sprintf(temp, "select contents from help where command='%s'", getToken(1));
		res=SendSQL2(temp, NULL);
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			mysql_free_result(res);
			res=SendSQL2("select contents from help where command='sorry'", NULL);
			row = mysql_fetch_row(res);
			fprintf(getMMudOut(), "%s",row[0]);
		}
		else
		{
			fprintf(getMMudOut(), "%s",row[0]);
		}
		mysql_free_result(res);
		PrintForm(name, password);
		if (getFrames()!=2) {ReadFile(logname);}
		return 1;
	}
}

int
ReadMail_Command(char *name, char *password, int room, char *fcommand)
{
	if (getTokenAmount()<2)
	{
		return 0;
	}
	ReadMail(name, password, room, atoi(getToken(1)), 0);
	return 1;
}

int
DeleteMail_Command(char *name, char *password, int room, char *fcommand)
{
	ReadMail(name, password, room, atoi(getToken(1)), 2);
	return 1;
}

/* sendmail <to> <headerlength> <header> <body>
*/
int
SendMail_Command(char *name, char *password, int room, char *fcommand)
{
	char *mailto, *mailbody, *mailheader;
	char logname[100];
	int thelength;
	sprintf(logname, "%s%s.log",USERHeader,name);
      		
   if (getTokenAmount() < 5)
   {
   	return 0;
   }

   mailto = getToken(1);
	thelength = atoi(getToken(2));
	if (thelength<1)
	{
		return 0;
	}
	mailheader = (char *) malloc(thelength+2);
	strncpy(mailheader, command + (getToken(3)-getToken(0)), thelength);
	mailheader[thelength+1]=0;
	mailbody = command + (getToken(3)-getToken(0)) + thelength + 1;
	//cgiFormString("mailto", mailto, 99);
	//cgiFormString("mailheader", mailheader, 99);
	//cgiFormString("mailbody", mailbody, strlen(fcommand) - 2);
		
	sprintf(sqlstring, "select name from usertable where "
		"name='%s' and god<2", mailto);
	res=SendSQL2(sqlstring, NULL);
	
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
	
		if (row!=NULL) 
		{
			WriteMail(name, row[0], mailheader, mailbody);
			WriteSentenceIntoOwnLogFile(logname, "Mail sent.<BR>\r\n");
		}  /* endif real user */ 
		else 
		{
			WriteSentenceIntoOwnLogFile(logname, "Mail not sent! User not found.<BR>\r\n");
		}
	}
	else
	{
		WriteSentenceIntoOwnLogFile(logname, "Mail not sent! User not found.<BR>\r\n");
	}
	mysql_free_result(res);
	free(mailheader);
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Whimpy_Command(char *name, char *password, int room, char *fcommand)
{
	char number[10];
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (getTokenAmount() == 1) {
		strcpy(number,"0");
	} else {
		if (!strcasecmp("help", command + (getToken(1) - getToken(0)))) {
			WriteSentenceIntoOwnLogFile(logname, 
			"Syntax: <B>whimpy &lt;string&gt;</B><UL><LI>feeling well"
			"<LI>feeling fine<LI>feeling quite nice<LI>slightly hurt"
			"<LI>hurt<LI>quite hurt<LI>extremely hurt<LI>terribly hurt"
			"<LI>feeling bad<LI>feeling very bad<LI>at death's door</UL>\r\n");
			WriteRoom(name, password, room, 0);
			return 1;
		}
		if (!strcasecmp("feeling well", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"10");
			//x.whimpy = 10;
		}
		if (!strcasecmp("feeling fine", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"20");
			//x.whimpy = 20;
		}
		if (!strcasecmp("feeling quite nice", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"30");
			//x.whimpy = 30;
		}
		if (!strcasecmp("slightly hurt", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"40");
			//x.whimpy = 40;
		}
		if (!strcasecmp("hurt", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"50");
			//x.whimpy = 50;
		}
		if (!strcasecmp("quite hurt", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"60");
			//x.whimpy = 60;
		}
		if (!strcasecmp("extremely hurt", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"70");
			//x.whimpy = 70;
		}
		if (!strcasecmp("terribly hurt", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"80");
			//x.whimpy = 80;
		}
		if (!strcasecmp("feeling bad", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"90");
			//x.whimpy = 90;
		}
		if (!strcasecmp("feeling very bad", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"100");
			//x.whimpy = 100;
		}
		if (!strcasecmp("at death's door", command + (getToken(1) - getToken(0)))) {
			strcpy(number,"110");
			//x.whimpy = 110;
		}
	}
	sprintf(sqlstring, "update tmp_usertable set whimpy=%s "
		" where name='%s'", number, name);
	res=SendSQL2(sqlstring, NULL);
	if (res!=NULL)
	{
		mysql_free_result(res);
	}
	
	WriteSentenceIntoOwnLogFile(logname, "<I>Whimpy set.</I><BR>\r\n");
	WriteRoom(name, password, room, 0);
	return 1;
}

int
PKill_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	if (getTokenAmount() < 2)
	{
		return 0;
	}
	sprintf(logname, "%s%s.log",USERHeader,name);
	sprintf(sqlstring, "select fightingwho from tmp_usertable where "
		"name='%s'", name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	if (row[0][0]!=0)
	{
		mysql_free_result(res);
		WriteSentenceIntoOwnLogFile(logname, "You cannot change your pkill status during combat.<BR>\r\n");
	}
	else
	{
		mysql_free_result(res);
		if (!strcasecmp("on", getToken(1))) 
		{
			sprintf(sqlstring, "update tmp_usertable set fightable=1 where "
				"name='%s'", name);
			res=SendSQL2(sqlstring, NULL);
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "Pkill is now on.<BR>\r\n");
		} /* pkill is turned on */
		else
		{
			sprintf(sqlstring, "update tmp_usertable set fightable=0 where "
				"name='%s'", name);
			res=SendSQL2(sqlstring, NULL);
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "Pkill is now off.<BR>\r\n");
		} /* pkill is turned of */
	} /* if indeed the person is not already fighting */
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Me_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteMessage(name, room, "%s %s<BR>\r\n", name, command + 3);
	WriteSentenceIntoOwnLogFile(logname, "%s %s<BR>\r\n", name, command + 3);
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Stop_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (!strcasecmp("stop fighting", fcommand))
	{
		sprintf(sqlstring, "select fightingwho from tmp_usertable where "
			"name='%s'", name);
		res=SendSQL2(sqlstring, NULL);
		row = mysql_fetch_row(res);
		if (row[0][0]==0)
		{
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "You are not fighting anyone.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			return 1;
		}
		mysql_free_result(res);
		sprintf(sqlstring, "update tmp_usertable set fightingwho='' where "
			"name='%s'"
			, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
		WriteMessage(name, room, "%s stops fighting.<BR>\r\n", name);
		WriteSentenceIntoOwnLogFile(logname, "You stop fighting.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		return 1;
	}
	return 0;
}

int 
Fight_Command(char *name, char *password, int room, char *fcommand)
{
	int myFightable;
	char *myFightingName;
	char *myDescription;
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	sprintf(sqlstring, "select fightable from tmp_usertable where "
		"name='%s'", name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	myFightable = atoi(row[0]);
	mysql_free_result(res);

	/* this section takes care of the looking up of the description */
	myFightingName = ExistUserByDescription(1, getTokenAmount() - 1, room, &myDescription);
	if (myFightingName == NULL)
	{
		myFightingName = getToken(1);
		myDescription = getToken(1);
	}
	
	sprintf(sqlstring, "select name,god from tmp_usertable where "
	"name<>'%s' and "
	"name='%s' and "
	"fightable=1 and "
	"god<>2 and "
	"room=%i"
	, name, myFightingName, room);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	if ((myFightable!=1) && (atoi(row[1])!=3))
	{
		mysql_free_result(res);
		if (myFightingName != getToken(1)) {free(myFightingName);}
		if (myDescription != getToken(1)) {free(myDescription);}
		WriteSentenceIntoOwnLogFile(logname, "Pkill is off, so you cannot fight.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		return 1;
	}
	if (row!=NULL)
	{
		WriteSentenceIntoOwnLogFile(logname, "You start to fight against %s.<BR>\r\n", myDescription);
		WriteMessageTo(myFightingName, name, room, "%s starts fighting against %s.<BR>\r\n",
			    name, myDescription);
		WriteSayTo(myDescription, name, room, 
			   "%s starts fighting against you.<BR>\r\n", name);
		mysql_free_result(res);
		sprintf(sqlstring, "update tmp_usertable set fightingwho='%s' where name='%s'",
		myFightingName, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
		sprintf(sqlstring, "update tmp_usertable set fightingwho='%s' where name='%s'",
		name, myFightingName);
		res=SendSQL2(sqlstring, NULL);
	}
	else
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
	}
	mysql_free_result(res);
	if (myFightingName != getToken(1)) {free(myFightingName);}
	if (myDescription != getToken(1)) {free(myDescription);}
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Bow_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (!strcasecmp(command, "bow")) {
		WriteSentenceIntoOwnLogFile(logname, "You bow gracefully.<BR>\r\n");
		WriteMessage(name, room, "%s bows gracefully.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		return 1;
	}
	if (getTokenAmount() == 3 && (!strcasecmp(getToken(0), "bow")) && (!strcasecmp(getToken(1), "to"))) {
		if (WriteMessageTo(getToken(2), name, room, "%s bows gracefully to %s.<BR>\r\n", name, getToken(2)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(2), name, room, "%s bows gracefully to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You bow gracefully to %s.<BR>\r\n", getToken(2));
		}
		WriteRoom(name, password, room, 0);
		return 1;
	}
	return 0;
}

int
Eyebrow_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteSentenceIntoOwnLogFile(logname, "You raise an eyebrow.<BR>\r\n");
	WriteMessage(name, room, "%s raises an eyebrow.<BR>\r\n", name);
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Curtsey_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (!strcasecmp(command, "curtsey")) {
		WriteSentenceIntoOwnLogFile(logname, "You drop a curtsey.<BR>\r\n");
		WriteMessage(name, room, "%s drops a curtsey.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		return 1;
	}
	if (getTokenAmount() == 3 && (!strcasecmp(getToken(0), "curtsey")) && (!strcasecmp(getToken(1), "to"))) {
		if (WriteMessageTo(getToken(2), name, room, "%s drops a curtsey to %s.<BR>\r\n", name, getToken(2)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(2), name, room, "%s drops a curtsey to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You drop a curtsey to %s.</BR>\r\n", getToken(2));
		}
		WriteRoom(name, password, room, 0);
		return 1;
	}
	return 0;
}

int
Flinch_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (!strcasecmp(command, "flinch")) {
		WriteSentenceIntoOwnLogFile(logname, "You flinch.<BR>\r\n");
		WriteMessage(name, room, "%s flinches.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		return 1;
	}
	if (getTokenAmount() == 3 && (!strcasecmp(getToken(0), "flinch")) && (!strcasecmp(getToken(1), "to"))) {
		if (WriteMessageTo(getToken(2), name, room, "%s flinches to %s.<BR>\r\n", name, getToken(2)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(2), name, room, "%s flinches to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You flinch to %s.</BR>\r\n", getToken(2));
		}
		WriteRoom(name, password, room, 0);
		return 1;
	}
	return 0;
}

int
Tell_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if ((getTokenAmount() > 3) && (!strcasecmp("to", getToken(1)))) {
		if (!WriteLinkTo(getToken(2), name, "<B>%s tells you </B>: %s<BR>\r\n",
					    name, command + (getToken(3) - getToken(0)))) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSentenceIntoOwnLogFile(logname, "<B>You tell %s</B> : %s<BR>\r\n",
			      getToken(2), command + (getToken(3) - getToken(0)));
		}
		WriteRoom(name, password, room, 0);
		return 1;
	}
	return 0;
}

int
Say_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	if (getTokenAmount() == 1)
	{
		return 0;
	}
	sprintf(logname, "%s%s.log",USERHeader,name);
	if ((!strcasecmp("to", getToken(1))) && (getTokenAmount() > 3)) 
	{
		if (!WriteMessageTo(getToken(2), name, room, "%s says [to %s] : %s<BR>\r\n",
				    name, getToken(2), command + (getToken(3) - getToken(0)))) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		}
		else 
		{
			/* person not found */ 
			WriteSayTo(getToken(2), name, room, 
				   "<B>%s says [to you]</B> : %s<BR>\r\n", name, command + (getToken(3) - getToken(0)));
			WriteSentenceIntoOwnLogFile(logname, "<B>You say [to %s]</B> : %s<BR>\r\n", getToken(2), command + (getToken(3) - getToken(0)));
			ReadBill(getToken(2), command + (getToken(3) - getToken(0)), name, room);
		}
		WriteRoom(name, password, room, 0);
		return 1;
	}
	WriteSentenceIntoOwnLogFile(logname, "<B>You say </B>: %s<BR>\r\n", command + 4);
	WriteMessage(name, room, "%s says : %s<BR>\r\n", name, command + 4);
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Shout_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (getTokenAmount() == 1)
	{
		return 0;
	}
	if ((getTokenAmount() > 3) && (!strcasecmp("to", getToken(1))))
	{
		char           *temp1, *temp2;
		temp1 = (char *) malloc(strlen(fcommand) + 80);
		temp2 = (char *) malloc(strlen(fcommand) + 80);
		sprintf(temp1, "<B>%s shouts [to you] </B>: %s<BR>\r\n",
			name, command + (getToken(3) - getToken(0)));
		sprintf(temp2, "%s shouts [to %s] : %s<BR>\r\n",
			name, getToken(2), command + (getToken(3) - getToken(0)));
		if (!WriteMessageTo(getToken(2), name, room, temp2)) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(2), name, room, temp1);
			WriteSentenceIntoOwnLogFile(logname, "<B>You shout [to %s] </B>: %s<BR>\r\n",
						     getToken(2), command + (getToken(3) - getToken(0)));
		}
		free(temp2);
		free(temp1);
		WriteRoom(name, password, room, 0);
		return 1;
	}
	WriteSentenceIntoOwnLogFile(logname, "<B>You shout</B> : %s<BR>\r\n", command + 6);
	WriteMessage(name, room, "%s shouts : %s<BR>\r\n", name, command + 6);
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Ask_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (getTokenAmount() == 1)
	{
		return 0;
	}
	if ((!strcasecmp("to", getToken(1))) && (getTokenAmount() > 3)) 
	{
		char           *temp1, *temp2;
		temp1 = (char *) malloc(strlen(fcommand) + 80);
		temp2 = (char *) malloc(strlen(fcommand) + 80);
		sprintf(temp1, "<B>%s asks you </B>: %s<BR>\r\n",
			name, command + (getToken(3) - getToken(0)));
		sprintf(temp2, "%s asks %s : %s<BR>\r\n",
			name, getToken(2), command + (getToken(3) - getToken(0)));
		if (!WriteMessageTo(getToken(2), name, room, temp2)) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(2), name, room, temp1);
			WriteSentenceIntoOwnLogFile(logname, "<B>You ask %s</B> : %s<BR>\r\n",
						     getToken(2), command + (getToken(3) - getToken(0)));
		}
		free(temp2);
		free(temp1);
		WriteRoom(name, password, room, 0);
		return 1;
	}
	WriteSentenceIntoOwnLogFile(logname, "<B>You ask</B> : %s<BR>\r\n", command + 4);
	WriteMessage(name, room, "%s asks : %s<BR>\r\n", name, command + 4);
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Whisper_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	if (getTokenAmount() == 1)
	{
		return 0;
	}
	sprintf(logname, "%s%s.log",USERHeader,name);
	if ((!strcasecmp("to", getToken(1))) && (getTokenAmount() > 3)) {
		char           *temp1, *temp2;
		temp1 = (char *) malloc(strlen(fcommand) + 80);
		temp2 = (char *) malloc(strlen(fcommand) + 80);
		sprintf(temp1, "<B>%s whispers [to you]</B> : %s<BR>\r\n",
			name, command + (getToken(3) - getToken(0)));
		sprintf(temp2, "%s is whispering something to %s, but you cannot hear what.<BR>\r\n",
			name, getToken(2));
		if (!WriteMessageTo(getToken(2), name, room, temp2)) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(2), name, room, temp1);
			WriteSentenceIntoOwnLogFile(logname, "<B>You whisper [to %s]</B> : %s<BR>\r\n",
						     getToken(2), command + (getToken(3) - getToken(0)));
		}
		free(temp2);
		free(temp1);
		WriteRoom(name, password, room, 0);
		return 1;
	}
	WriteSentenceIntoOwnLogFile(logname, "<B>You whisper </B>: %s<BR>\r\n", command + 8);
	WriteMessage(name, room, "%s whispers : %s<BR>\r\n", name, command + 8);
	WriteRoom(name, password, room, 0);
	return 1;
}

int 
Awaken_Command(char *name, char *password, int room, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteSentenceIntoOwnLogFile(logname, "You aren't asleep, silly.<BR>\r\n");
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Look_Command(char *name, char *password, int room, char *fcommand)
{
	if ((!strcasecmp("look at sky", command)) ||
	   (!strcasecmp("look at clouds", command))) 
	{
		LookSky_Command(name, password);
		return 1;
	}
	if ((*command == '\0') ||
	    (!strcasecmp(command, "look around")) ||
	    (!strcasecmp(command, "look")) ||
	    (!strcasecmp(command, "l"))) 
	{
		WriteRoom(name, password, room, 0);
		return 1;
	}
	
	if (getTokenAmount() > 2) 
	{
		LookItem_Command(name, password, room);
		return 1;
	}
	return 0;
}

int
Get_Command(char *name, char *fpassword, int room, char *fcommand)
{
	if ( ((getTokenAmount()==3) || (getTokenAmount()==4)) && (!strcasecmp("get", getToken(0))) )
	{
	/* get copper coin(s)
	   get 10 copper coin(s)
	*/
		if ((!strcasecmp("coin",getToken(getTokenAmount()-1))) ||
			(!strcasecmp("coins", getToken(getTokenAmount()-1))) )
		{
			if ((!strcasecmp("copper",getToken(getTokenAmount()-2))) ||
				(!strcasecmp("silver",getToken(getTokenAmount()-2))) ||
				(!strcasecmp("gold", getToken(getTokenAmount()-2))) )
			{
				GetMoney_Command(name, password, room);
				return 1;
			}
		}
	}
	if (getTokenAmount() >= 2) 
	{
		GetItem_Command(name, password, room);
		return 1;
	}
}

int
Drop_Command(char *name, char *password, int room, char *fcommand)
{
	if ( ((getTokenAmount()==3) || (getTokenAmount()==4)) && (!strcasecmp("drop", getToken(0))) )
	{
	/* drop copper coin(s)
	   drop 10 copper coin(s)
	*/
		if ((!strcasecmp("coin",getToken(getTokenAmount()-1))) ||
			(!strcasecmp("coins", getToken(getTokenAmount()-1))) )
		{
			if ((!strcasecmp("copper",getToken(getTokenAmount()-2))) ||
				(!strcasecmp("silver",getToken(getTokenAmount()-2))) ||
				(!strcasecmp("gold", getToken(getTokenAmount()-2))) )
			{
				DropMoney_Command(name, password, room);
				return 1;
			}
		}
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("drop", getToken(0)))) 
	{
		DropItem_Command(name, password, room);
		return 1;
	}
}

int
Buy_Command(char *name, char *password, int room, char *fcommand)
{
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==9)) 
	{
		BuyItem_Command(name, password, room, "Bill");
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==16)) 
	{
		BuyItem_Command(name, password, room, "Karcas");
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==64)) 
	{
		BuyItem_Command(name, password, room, "Karina");
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==73))
	{
		BuyItem_Command(name, password, room, "Hagen");
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==85)) 
	{
		BuyItem_Command(name, password, room, "Karsten");
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==87))
	{
		BuyItem_Command(name, password, room, "Kurst");
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==161))
	{
		BuyItem_Command(name, password, room, "Karstare");
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==228))
	{
		BuyItem_Command(name, password, room, "Vimrilad");
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==2550))
	{
		BuyItem_Command(name, password, room, "Telios");
	}
	if ((getTokenAmount() >= 2) && (!strcasecmp("buy", getToken(0))) && (room==2551))
	{
		BuyItem_Command(name, password, room, "Nolli");
	}
	return 0;
}

int
Sell_Command(char *name, char *password, int room, char *fcommand)
{
	if ((getTokenAmount() >= 2) && (!strcasecmp("sell", getToken(0))) && (room==16)) 
	{
		SellItem_Command(name, password, room, "Karcas");
		return 1;
	}
	return 0;
}

int
Admin_Command(char *name, char *password, int room, char *fcommand)
{
	if (godstatus != 1)
	{
		return 0;
	}
	if (!strcasecmp(fcommand, "admin shutdown"))
	{
		fprintf(getMMudOut(), "<HTML>\n");
		fprintf(getMMudOut(), "<HEAD>\n");
		fprintf(getMMudOut(), "<TITLE>\n");
		fprintf(getMMudOut(), "Land of Karchan - Admin Shutdown\n");
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

		fprintf(getMMudOut(), "<H1>Admin Shutdown - Shutting down Game</H1>\n");
		fprintf(getMMudOut(), "Shutting down of game initiated. Please stand by...<P>");
		
		PrintForm(name, password);
		fprintf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
		fprintf(getMMudOut(), "<DIV ALIGN=left><P>");
		setShutdown(1);
		return 1;
	}
	if (!strcasecmp(fcommand, "admin stats"))
	{
		mudinfostruct mymudinfo;
		mymudinfo = getMudInfo();

		fprintf(getMMudOut(), "<HTML>\n");
		fprintf(getMMudOut(), "<HEAD>\n");
		fprintf(getMMudOut(), "<TITLE>\n");
		fprintf(getMMudOut(), "Land of Karchan - Admin Stats\n");
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

		fprintf(getMMudOut(), "<H1>Admin Stats - Displaying current usage statistics of game</H1>\n");
		fprintf(getMMudOut(), "Host: %s<BR>\nIp address: %s<BR>\nDomainname: %s<BR>\nProtocol version: %s<BR>\nMmud version: %s %s %s<P>\n", 
			mymudinfo.hostname, mymudinfo.hostip, mymudinfo.domainname, mymudinfo.protversion, mymudinfo.mmudversion,
			mymudinfo.mmudtime, mymudinfo.mmuddate);

		fprintf(getMMudOut(), "Mmud started on : %s<P>\n", asctime(gmtime(&mymudinfo.mmudstartuptime)));
		fprintf(getMMudOut(), "Total connections: %i<BR>\nTimeouts: %i<BR>\nCurrent connections: %i<BR>\nMax. current connections: %i<BR>\n",
			mymudinfo.number_of_connections, mymudinfo.number_of_timeouts, mymudinfo.number_of_current_connections,
			mymudinfo.maxnumber_of_current_connections);
	
		PrintForm(name, password);
		fprintf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
		fprintf(getMMudOut(), "<DIV ALIGN=left><P>");
		return 1;
	}
	return 0;
}

int
Give_Command(char *name, char *password, int room, char *fcommand)
{
	if ( ((getTokenAmount()==5) || (getTokenAmount()==6)) && (!strcasecmp("give", getToken(0))) )
	{
	/* give copper coin(s)
	   give 10 copper coin(s)
	*/
		if ((!strcasecmp("coin",getToken(getTokenAmount()-3))) ||
			(!strcasecmp("coins", getToken(getTokenAmount()-3))) )
		{
			if ((!strcasecmp("copper",getToken(getTokenAmount()-4))) ||
				(!strcasecmp("silver",getToken(getTokenAmount()-4))) ||
				(!strcasecmp("gold", getToken(getTokenAmount()-4))) )
			{
				GiveMoney_Command(name, password, room);
				return 1;
			}
		}
	}
	if ((getTokenAmount() >= 4) && (!strcasecmp("give", getToken(0)))) 
	{
		int i;
		i = GiveItem_Command(name, password, room);
		return i;
	}
	return 0;
}

int
RangerGuild_Command(char *name, char *password, int room, char *fcommand)
{
	/* Guilds */
	if (!strcasecmp("rangers", guildstatus))
	{
		if (!strcasecmp("nature list", command))
		{
			RangerList(name, password, room);
			return 1;
		}
		if ( (!strcasecmp("nature call", command)) && (room==43) )
		{
			RangerEntryIn(name, password, room);	
			return 1;
		}
		if ( (!strcasecmp("nature call", command)) && (room==216) )
		{
			RangerEntryOut(name, password, room);
			return 1;
		}
		if ( (!strcasecmp("nature", getToken(0))) && (!strcasecmp("talk", getToken(1))) )
		{
			RangerTalk(name, password, room);
			return 1;
		}
	}
	return 0;
}

int
MifGuild_Command(char *name, char *password, int room, char *fcommand)
{
	if (!strcasecmp("mif", guildstatus))
	{
		if (!strcasecmp("magic list", command))
		{
			MIFList(name, password, room);
			return 1;
		}
		if ( (!strcasecmp("magic wave", command)) && (room==142) )
		{
			MIFEntryIn(name, password, room);
			return 1;
		}
		if ( (!strcasecmp("magic wave", command)) && (room==143) )
		{
			MIFEntryOut(name, password, room);
			return 1;
		}
		if ( (!strcasecmp("magic", getToken(0))) && (!strcasecmp("talk", getToken(1))) )
		{
			MIFTalk(name, password, room);
			return 1;
		}
	}
	return 0;
}

int
initGameFunctionIndex()
{
	/* initialise and fill the Function array */
	gameFunctionArray = (gameFunction *) malloc(sizeof(gameFunction)*100);
	gameFunctionArray[theNumberOfFunctions++] = &Admin_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Ask_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Awaken_Command;
	gameFunctionArray[theNumberOfFunctions++] = &BigTalk_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Bow_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Buy_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Clear_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Curtsey_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Date_Command;
	gameFunctionArray[theNumberOfFunctions++] = &DeleteMail_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoDown_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Drink_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Drop_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoEast_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoEast_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Eat_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Eyebrow_Command;
//	gameFunctionArray[theNumberOfFunctions++] = &Fight_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Flinch_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Get_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Give_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Go_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Help_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Inventory_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Inventory_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Look_Command;
	gameFunctionArray[theNumberOfFunctions++] = &ListMail_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Look_Command;
	gameFunctionArray[theNumberOfFunctions++] = &MifGuild_Command;
	gameFunctionArray[theNumberOfFunctions++] = &MailFormDumpOnScreen;
	gameFunctionArray[theNumberOfFunctions++] = &Me_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoNorth_Command;
	gameFunctionArray[theNumberOfFunctions++] = &RangerGuild_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoNorth_Command;
	gameFunctionArray[theNumberOfFunctions++] = &PKill_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Put_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Quit_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Read_Command;
	gameFunctionArray[theNumberOfFunctions++] = &ReadMail_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Unwear_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Retrieve_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoSouth_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Say_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Search_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Sell_Command;
	gameFunctionArray[theNumberOfFunctions++] = &SendMail_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Shout_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Sleep_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoSouth_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Stats_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Stop_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Tell_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Time_Command;
	gameFunctionArray[theNumberOfFunctions++] = &ChangeTitle_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Unwield_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoUp_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoWest_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Wear_Command;
	gameFunctionArray[theNumberOfFunctions++] = &GoWest_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Whimpy_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Whisper_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Who_Command;
	gameFunctionArray[theNumberOfFunctions++] = &Wield_Command;
}

int
gameMain(char *fcommand, char *fname, char *fpassword, char *faddress)
{
	int		oldroom;
	int		i, amount;
	char	frames[10];
	char	*temp;
	char	logname[100];
	char	*junk;

#ifdef DEBUG	
	printf("gameMain started!!!\n");
#endif
	command = fcommand;
	name = fname;
	password = fpassword;
	umask(0000);

	printstr = (char *) malloc(strlen(fcommand)+500);
	time(&datetime);
	datumtijd = *(gmtime(&datetime));
	srandom(datetime);
	
	if (SearchBanList(faddress, name)) 
	{
		BannedFromGame(name, faddress);
		free(printstr);
		return 0;
	}

	if (!ExistUser(name)) {
		NotActive(name,password,1);
		free(printstr);
		return 0;
	}

//	openDatabase();
	sprintf(sqlstring, "select name, lok, sleep, room, lastlogin, god, sex, vitals, maxvital, guild, punishment "
		"from tmp_usertable "
		"where name='%s' and lok<>''", name);
	res=SendSQL2(sqlstring, NULL);
	if (res==NULL)
	{
		NotActive(name, password,2);
		free(printstr);
		return 0;
	}
	row = mysql_fetch_row(res);
	if (row==NULL)
	{
		NotActive(name, password,3);
		free(printstr);
		return 0;
	}
	
	strcpy(name, row[0]); /* copy name to name from database */
	room=atoi(row[3]); /* copy roomnumber to room from database */
	sleepstatus=atoi(row[2]); /* copy sleep to sleepstatus from database */
	godstatus=atoi(row[5]); /* copy godstatus of player from database */
	strcpy(sexstatus,row[6]); /* sex status (male, female) */
	if (atoi(row[7])>atoi(row[8])) { /* check vitals along with maxvital */
		mysql_free_result(res);
		Dead(name, password, room);
		free(printstr);
		return 0;
	}
	strcpy(guildstatus	, row[9]);
	punishment = atoi(row[10]);
	if (strcmp(row[1], password)) 
	{
			NotActive(name, password,4);
	}
	mysql_free_result(res);
#ifdef DEBUG
	if (!strcmp(name, "Karn")) 
	{
		fprintf(getMMudOut(), "Command: %s<BR>Password: %s<BR>", command, password);
	}
#endif
	if (*name == '\0') 
	{ 
		NotActive(name, password,5);
	}
	if (godstatus==2) 
	{
		NotActive(name, password,6);
	}
	sprintf(logname, "%s%s.log", USERHeader, name);

//	'0000-01-01 00:00:00' - '9999-12-31 23:59:59'
	sprintf(sqlstring, "update tmp_usertable set lastlogin=date_sub(NOW(), INTERVAL 2 HOUR), "
			"address='%s' where name='%s'",	faddress, name);
	res=SendSQL2(sqlstring, NULL);
	
	mysql_free_result(res);

	junk = (char *) malloc(strlen(command)+1);
	strcpy(junk, command);
	while ((strlen(junk)>0) && (junk[strlen(junk)-1]==' ')) 
	{
		junk[strlen(junk)-1]=0;
	}
	
	if (*junk == '\0') 
	{
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
	}
	
		if ((strstr(command,"<applet")!=NULL) || (strstr(command,"<script")!=NULL)
		|| (strstr(command,"java-script")!=NULL) || (strstr(command,"CommandForm")!=NULL)) 
	{ 
		WriteSentenceIntoOwnLogFile(logname, "I am afraid, I do not understand that.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
	}

	if (sleepstatus==1) 
	{
		if (!strcasecmp(command, "awaken"))
		{
			Awaken2_Command(name, password, room);
			free(junk);
			free(printstr);
			return 1;
		}
		WriteSentenceIntoOwnLogFile(logname, "You can't do that. You are asleep, silly.<BR>\r\n");
		WriteRoom(name, password, room, 1);
		free(junk);
		free(printstr);
		return 1;
	}
	
	if (punishment > 0)
	{
		if (!strcasecmp(command, "say rrribbit"))
		{
		WriteMessage(name, room, "The toad called %s says : <I>rrrrrribbit.</I><BR>\r\n", name);
		WriteSentenceIntoOwnLogFile(logname, "You say : <I>rrrrrribbit.</I><BR>\r\n"
			"You still have %i ribbits to go...<BR>\r\n", punishment-1);
		if (punishment == 1)
		{
		WriteMessage(name, room, "The toad called %s changes back into it's old self.<BR>\r\n", name);
		WriteSentenceIntoOwnLogFile(logname, "You change back into your old self.<BR>\r\n"
			 "Do not do again what you did to become this.<BR>\r\n");
		
		}
		sprintf(sqlstring, "update tmp_usertable set punishment=punishment-1 where name='%s'",
			name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
		}
		if ((*command == '\0') ||
		(!strcasecmp(command, "look around")) ||
		(!strcasecmp(command, "look")) ||
		(!strcasecmp(command, "l"))) {
			WriteRoom(name, password, room, 0);
			free(junk);
			free(printstr);
			return 1;
		}
		if ((!strcasecmp(command, "go west")) ||
		(!strcasecmp(command, "west")) ||
		(!strcasecmp(command, "w"))) {
				GoWest_Command(name, password, room, command);
		}
		if ((!strcasecmp(command, "go east")) ||
		(!strcasecmp(command, "east")) ||
		(!strcasecmp(command, "e"))) {
			GoEast_Command(name, password, room, command);
		}
		if ((!strcasecmp(command, "go north")) ||
		(!strcasecmp(command, "north")) ||
		(!strcasecmp(command, "n"))) {
			GoNorth_Command(name, password, room, command);
		}
		if ((!strcasecmp(command, "go south")) ||
		(!strcasecmp(command, "south")) ||
		(!strcasecmp(command, "s"))) {
			GoSouth_Command(name, password, room, command);
		}
	
		if ((!strcasecmp(command, "go down")) ||
		(!strcasecmp(command, "down"))) {
			GoDown_Command(name, password, room, command);
		}
		if ((!strcasecmp(command, "go up")) ||
		(!strcasecmp(command, "up"))) {
			GoUp_Command(name, password, room, command);
		}
		if (!strcasecmp(command, "quit")) 
		{
			Quit_Command(name, password, room, command);
		}

		WriteSentenceIntoOwnLogFile(logname, "You cannot do that, you are a frog, remember?<BR>\r\n");
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
	}

	myTokens[0] = junk;
	myTokens[0] = strtok(junk, " ");
	if (myTokens[0] != NULL) {
		i = 1;
		while (i != -1) {
			myTokens[i] = strtok(NULL, " ");
			i++;
			if (myTokens[i - 1] == NULL) {
				amount = i - 1;
				i = -1;
			}	/* endif */
			if (i>95) {
				amount = i - 1;
				i = -1;
			}	/* endif */
		}		/* endwhile */
	}			/* endif */
	setTokens(myTokens);
	setTokenAmount(amount);

	if (SearchForSpecialCommand(name, password, room)==1)
	{
		free(junk);
		free(printstr);
		return 1;
	}
#ifdef DEBUG
	printf("binary search started!!!\n");
#endif
	
	{
		/* binary search in index, if found call function */
		int i = (theNumberOfFunctions / 2) + (theNumberOfFunctions % 2);
		int pos = theNumberOfFunctions / 2;
		int equals = strcasecmp(gameCommands[pos], getToken(0));
		#ifdef DEBUG
		fprintf(getMMudOut(), "%i\n", theNumberOfFunctions);
		fprintf(getMMudOut(), "%i, %i, %s, %s\n", i, pos, getToken(0), gameCommands[pos]);
		#endif
		while ((i>0) && (equals))
		{
			if (i==1) 
			{
				i = 0;
			}
			i = (i / 2) + (i % 2);
			if (equals > 0) {pos -= i;}
			if (equals < 0) {pos += i;}
			if ((pos >= 0) && (pos < theNumberOfFunctions))
			{
				#ifdef DEBUG
				fprintf(getMMudOut(), "%i, %i, %s, %s\n", i, pos, getToken(0), gameCommands[pos]);
				#endif
				
				equals = strcasecmp(gameCommands[pos], getToken(0));
			}
		}
		if (!equals)
		{
			/* string has been found */
#ifdef DEBUG
			printf("command found (%s)\n", gameCommands[pos]);
#endif
			if (gameFunctionArray[pos](name, password, room, command))
			{
				/* command executed successfully, kill this session */
				free(junk);
				free(printstr);
				return 1;
			}
			{
				/* do nothing, the game will in time find out that there is no appropriate response to the bogus command and will produce an error message. Darnit people, why do these damn comment lines sometimes have to be so long! And who turned off my word wrap anyway!!! */
			}
		}
	}

#ifdef DEBUG
	printf("binary search ended\n");
#endif
/*	if (!strcasecmp(command, "introduce me")) {
		IntroduceMe_Command(logname);
	}*/
	/* smile */
	if ((temp = get_pluralis(command)) != NULL) {
		WriteSentenceIntoOwnLogFile(logname, "You %s.<BR>\r\n", command);
		WriteMessage(name, room, "%s %s.<BR>\r\n", name, temp);
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
	}
	/* smile engagingly */
	if ( (getTokenAmount()==2) && ((temp = get_pluralis(getToken(0))) != NULL) &&
		 (exist_adverb(getToken(1))) ) {
		WriteSentenceIntoOwnLogFile(logname, "You %s %s.<BR>\r\n", getToken(0), getToken(1));
		WriteMessage(name, room, "%s %s %s.<BR>\r\n", name, temp, getToken(1));
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
	}
	/* smile to bill */
	if ((getTokenAmount() == 3) && ((temp = get_pluralis(getToken(0))) != NULL) && 
		(!strcasecmp(getToken(1), "to")) ) {
		if (WriteMessageTo(getToken(2), name, room, "%s %s to %s.<BR>\r\n", name, temp, getToken(2)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(2), name, room, "%s %s to you.<BR>\r\n", name, temp);
			WriteSentenceIntoOwnLogFile(logname, "You %s to %s.</BR>\r\n", getToken(0), getToken(2));
		}
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
	}
	/* smile(0) engagingly(1) to(2) Bill(3) */
	if ((getTokenAmount() == 4) && ((temp = get_pluralis(getToken(0))) != NULL) && 
		(!strcasecmp(getToken(2), "to")) && (exist_adverb(getToken(1))) ) {
		if (WriteMessageTo(getToken(3), name, room, "%s %s %s to %s.<BR>\r\n", name, temp, getToken(1), getToken(3)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(3), name, room, "%s %s %s to you.<BR>\r\n", name, temp, getToken(1));
			WriteSentenceIntoOwnLogFile(logname, "You %s %s to %s.</BR>\r\n", getToken(0), getToken(1), getToken(3));
		}
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
	}
	/* multiple person emotions */
	/* caress bill */
	if ((getTokenAmount() == 2) && ((temp = get_pluralis2(getToken(0))) != NULL)) {
		char            temp1[50], temp2[50];
		sprintf(temp1, "%s %s you.<BR>\r\n", name, temp);
		sprintf(temp2, "%s %s %s.<BR>\r\n", name, temp, getToken(1));
		if (!WriteMessageTo(getToken(1), name, room, "%s %s %s.<BR>\r\n",
					    name, temp, getToken(1))) {
			WriteSentenceIntoOwnLogFile(logname, "That user doesn't exist.<BR>\r\n");
		} else {
			WriteSayTo(getToken(1), name, room,
				"%s %s you.<BR>\r\n", name, temp);
			WriteSentenceIntoOwnLogFile(logname, "You %s %s.<BR>\r\n", getToken(0), getToken(1));
		}
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
	}			/* end of multiple persons emotions */
	/* caress bill absentmindedly */
	if ((getTokenAmount() == 3) && ((temp = get_pluralis2(getToken(0))) != NULL) &&
		(exist_adverb(getToken(2))) ) {
		char            temp1[50], temp2[50];
		sprintf(temp1, "%s %s you, %s.<BR>\r\n", name, temp, getToken(2));
		sprintf(temp2, "%s %s %s, %s.<BR>\r\n", name, temp, getToken(1), getToken(2));
		if (!WriteMessageTo(getToken(1), name, room, "%s %s %s, %s.<BR>\r\n",
					    name, temp, getToken(1), getToken(2))) {
			WriteSentenceIntoOwnLogFile(logname, "That user doesn't exist.<BR>\r\n");
		} else {
			WriteSayTo(getToken(1), name, room,
				"%s %s you, %s.<BR>\r\n", name, temp, getToken(2));
			WriteSentenceIntoOwnLogFile(logname, "You %s %s, %s.<BR>\r\n", getToken(0), getToken(1), getToken(2));
		}
		WriteRoom(name, password, room, 0);
		free(junk);
		free(printstr);
		return 1;
	}			/* end of multiple persons emotions */

/* add SWTalk */		
	if (!strcasecmp("SW", guildstatus))
	{
		if ( (!strcasecmp("pow", getToken(0))) && (!strcasecmp("wow", getToken(1))) )
		{
			SWTalk(name, password, room);
			free(junk);
			free(printstr);
			return 1;
		}

	}		

/* add BKTalk */		
	if (!strcasecmp("BKIC", guildstatus))
	{
		if ( (!strcasecmp("chaos", getToken(0))) && (!strcasecmp("murmur", getToken(1))) )
		{
			BKTalk(name, password, room);
			free(junk);
			free(printstr);
			return 1;
		}

	}

/* add VampTalk */		
	if (!strcasecmp("Kindred", guildstatus))
	{
		if ( (!strcasecmp("misty", getToken(0))) && (!strcasecmp("whisper", getToken(1))) )
		{
			VampTalk(name, password, room);
			free(junk);
			free(printstr);
			return 1;
		}

	}

/* add KnightTalk */		
	if (!strcasecmp("Knights", guildstatus))
	{
		if ( (!strcasecmp("knight", getToken(0))) && (!strcasecmp("talk", getToken(1))) )
		{
			KnightTalk(name, password, room);
			free(junk);
			free(printstr);
			return 1;
		}

	}

/* add CoDTalk */		
	if (!strcasecmp("CoD", guildstatus))
	{
		if ( (!strcasecmp("mogob", getToken(0))) && (!strcasecmp("burz", getToken(1))) )
		{
			CoDTalk(name, password, room);
			free(junk);
			free(printstr);
			return 1;
		}

	}

	/* End Guilds */

	WriteSentenceIntoOwnLogFile(logname, "I am afraid, I do not understand that.<BR>\r\n");
	WriteRoom(name, password, room, 0);
	return 1;
}
