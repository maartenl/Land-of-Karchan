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

int commandlineinterface = 0;

/* three strings destined for parsing the commands */
extern char    *command;
extern char    *printstr;
extern char    *tokens[100];
extern int      aantal;
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
// parameters: name, password, room, tokens, command
typedef int (*gameFunction)(char *, char *, int, char **, char *);


MYSQL_RES *res;
MYSQL_ROW row;
char sqlstring[1024];

void 
InitVar(char *fcommand)
{
	printstr = (char *) malloc(strlen(fcommand)+500);
	time(&datetime);
	datumtijd = *(gmtime(&datetime));
	srandom(datetime);
}				/* endproc */

int
GoDown_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
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
GoUp_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
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
	fprintf(cgiOut, "<HTML><HEAD><TITLE>You have been banned</TITLE></HEAD>\n\n");
	fprintf(cgiOut, "<BODY>\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Banned</H1><HR>\n");
	fprintf(cgiOut, "You, or someone in your domain,  has angered the gods by behaving badly on this mud. ");
	fprintf(cgiOut, "Your ip domain is therefore banned from the game.<P>\n");
	fprintf(cgiOut, "If you have not misbehaved or even have never before played the game before, and wish"
	" to play with your current IP address, email to "
	"<A HREF=\"mailto:deputy@"ServerName"\">deputy@"ServerName"</A> and ask them to make "
	"an exception in your case. Do <I>not</I> forget to provide your "
	"Character name.<P>You'll be okay as long as you follow the rules.<P>\n");
	fprintf(cgiOut, "</body>\n");
	fprintf(cgiOut, "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-%i Banned from mud by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
	exit(0);
}

void CookieNotFound(char *name, char *address)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	fprintf(cgiOut, "<HTML><HEAD><TITLE>Unable to logon</TITLE></HEAD>\n\n");
	fprintf(cgiOut, "<BODY>\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Unable to logon</H1><HR>\n");
	fprintf(cgiOut, "When you logon, a cookie is automatically generated. ");
	fprintf(cgiOut, "However, I have been unable to find my cookie.<P>\n");
	fprintf(cgiOut, "Please attempt to relogon.<P>\n");
	fprintf(cgiOut, "</body>\n");
	fprintf(cgiOut, "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-%i Cookie not found for mud by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
	closedbconnection();
	exit(0);
}

int
Go_Command(char *name, char *password, int room, char **ftokens, char *command)
{
	if (!strcasecmp(tokens[1], "west")) 
	{
		GoWest_Command(name, password, room, tokens, command);	
		return 1;
	}
	if (!strcasecmp(tokens[1], "east")) 
	{
		GoEast_Command(name, password, room, tokens, command);
		return 1;
	}
	if (!strcasecmp(tokens[1], "north"))
	{
		GoNorth_Command(name, password, room, tokens, command);
		return 1;
	}
	if (!strcasecmp(tokens[1], "south"))
	{
		GoSouth_Command(name, password, room, tokens, command);
		return 1;
	}
	if (!strcasecmp(tokens[1], "down"))
	{
		GoDown_Command(name, password, room, tokens, command);
		return 1;
	}
	if (!strcasecmp(tokens[1], "up"))
	{
		GoUp_Command(name, password, room, tokens, command);
		return 1;
	}
	return 0;
}

int
Clear_Command(char *name, char *password, int room, char **ftokens, char *command)
{
	char logname[100];
	sprintf(logname, "%s%s.log", USERHeader, name);
	WriteRoom(name, password, room, 0);
	ClearLogFile(logname);
	WriteSentenceIntoOwnLogFile(logname, "You cleared your mind.<BR>\r\n");
	return 1;
}

int
Help_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (!strcasecmp(fcommand, "help hint")) 
	{
		HelpHint_Command(name, password, room);
		return 1;
	}
	if (!strcasecmp(fcommand, "help")) 
	{
		MYSQL_RES *res;
		MYSQL_ROW row;
		int i;
		char temp[1024];
		
		fprintf(cgiOut, "<HTML>\r\n");
		fprintf(cgiOut, "<HEAD>\r\n");
		fprintf(cgiOut, "<TITLE>\r\n");
		fprintf(cgiOut, "Land of Karchan - General Help\r\n");
		fprintf(cgiOut, "</TITLE>\r\n");
		fprintf(cgiOut, "</HEAD>\r\n");
		
		fprintf(cgiOut, "<BODY>\r\n");
		fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\r\n");
	
		sprintf(temp, "select contents from help where command='general help'");
		res=SendSQL2(temp, NULL);
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			mysql_free_result(res);
			res=SendSQL2("select contents from help where command='sorry'", NULL);
			row = mysql_fetch_row(res);
			fprintf(cgiOut, "%s",row[0]);
		}
		else
		{
			fprintf(cgiOut, "%s",row[0]);
		}
		mysql_free_result(res);
		PrintForm(name, password);
		if (getFrames()!=2) {ReadFile(logname);}
		return 1;
	}
	if ((aantal >= 2) && (!strcasecmp(tokens[0],"help"))) 
	{
		MYSQL_RES *res;
		MYSQL_ROW row;
		int i;
		char temp[1024];
		
		fprintf(cgiOut, "<HTML>\r\n");
		fprintf(cgiOut, "<HEAD>\r\n");
		fprintf(cgiOut, "<TITLE>\r\n");
		fprintf(cgiOut, "Land of Karchan - Command %s\r\n", tokens[1]);
		fprintf(cgiOut, "</TITLE>\r\n");
		fprintf(cgiOut, "</HEAD>\r\n");
		
		fprintf(cgiOut, "<BODY>\r\n");
		fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\r\n");
	
		sprintf(temp, "select contents from help where command='%s'", tokens[1]);
		res=SendSQL2(temp, NULL);
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			mysql_free_result(res);
			res=SendSQL2("select contents from help where command='sorry'", NULL);
			row = mysql_fetch_row(res);
			fprintf(cgiOut, "%s",row[0]);
		}
		else
		{
			fprintf(cgiOut, "%s",row[0]);
		}
		mysql_free_result(res);
		PrintForm(name, password);
		if (getFrames()!=2) {ReadFile(logname);}
		return 1;
	}
}

int
ReadMail_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	ReadMail(name, password, room, atoi(tokens[1]), 0);
	return 1;
		ReadMail(name, password, room, atoi(tokens[1]), 2);
}

int
DeleteMail_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	ReadMail(name, password, room, atoi(tokens[1]), 2);
	return 1;
}

int
SendMail_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char mailto[100], *mailbody, mailheader[100];
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
      		
	mailbody = (char *) malloc(cgiContentLength);
	cgiFormString("mailto", mailto, 99);
	cgiFormString("mailheader", mailheader, 99);
	cgiFormString("mailbody", mailbody, cgiContentLength - 2);
		
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
		
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Whimpy_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char number[10];
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (aantal == 1) {
		strcpy(number,"0");
	} else {
		if (!strcasecmp("help", command + (tokens[1] - tokens[0]))) {
			WriteSentenceIntoOwnLogFile(logname, 
			"Syntax: <B>whimpy &lt;string&gt;</B><UL><LI>feeling well"
			"<LI>feeling fine<LI>feeling quite nice<LI>slightly hurt"
			"<LI>hurt<LI>quite hurt<LI>extremely hurt<LI>terribly hurt"
			"<LI>feeling bad<LI>feeling very bad<LI>at death's door</UL>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		if (!strcasecmp("feeling well", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"10");
			//x.whimpy = 10;
		}
		if (!strcasecmp("feeling fine", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"20");
			//x.whimpy = 20;
		}
		if (!strcasecmp("feeling quite nice", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"30");
			//x.whimpy = 30;
		}
		if (!strcasecmp("slightly hurt", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"40");
			//x.whimpy = 40;
		}
		if (!strcasecmp("hurt", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"50");
			//x.whimpy = 50;
		}
		if (!strcasecmp("quite hurt", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"60");
			//x.whimpy = 60;
		}
		if (!strcasecmp("extremely hurt", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"70");
			//x.whimpy = 70;
		}
		if (!strcasecmp("terribly hurt", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"80");
			//x.whimpy = 80;
		}
		if (!strcasecmp("feeling bad", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"90");
			//x.whimpy = 90;
		}
		if (!strcasecmp("feeling very bad", command + (tokens[1] - tokens[0]))) {
			strcpy(number,"100");
			//x.whimpy = 100;
		}
		if (!strcasecmp("at death's door", command + (tokens[1] - tokens[0]))) {
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
PKill_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
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
		if (!strcasecmp("on", tokens[1])) 
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
Me_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteMessage(name, room, "%s %s<BR>\r\n", name, command + 3);
	WriteSentenceIntoOwnLogFile(logname, "%s %s<BR>\r\n", name, command + 3);
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Stop_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
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
Fight_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
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
	myFightingName = 
	ExistUserByDescription(tokens, 1, aantal - 1, room, &myDescription);
	if (myFightingName == NULL)
	{
		myFightingName = tokens[1];
		myDescription = tokens[1];
	}
	else
	{
		printf("<%s><%s>", myFightingName, myDescription);
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
		if (myFightingName != tokens[1]) {free(myFightingName);}
		if (myDescription != tokens[1]) {free(myDescription);}
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
	if (myFightingName != tokens[1]) {free(myFightingName);}
	if (myDescription != tokens[1]) {free(myDescription);}
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Bow_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (!strcasecmp(command, "bow")) {
		WriteSentenceIntoOwnLogFile(logname, "You bow gracefully.<BR>\r\n");
		WriteMessage(name, room, "%s bows gracefully.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		return 1;
	}
	if (aantal == 3 && (!strcasecmp(tokens[0], "bow")) && (!strcasecmp(tokens[1], "to"))) {
		if (WriteMessageTo(tokens[2], name, room, "%s bows gracefully to %s.<BR>\r\n", name, tokens[2]) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, "%s bows gracefully to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You bow gracefully to %s.<BR>\r\n", tokens[2]);
		}
		WriteRoom(name, password, room, 0);
		return 1;
	}
	return 0;
}

int
Eyebrow_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteSentenceIntoOwnLogFile(logname, "You raise an eyebrow.<BR>\r\n");
	WriteMessage(name, room, "%s raises an eyebrow.<BR>\r\n", name);
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Curtsey_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (!strcasecmp(command, "curtsey")) {
		WriteSentenceIntoOwnLogFile(logname, "You drop a curtsey.<BR>\r\n");
		WriteMessage(name, room, "%s drops a curtsey.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		return 1;
	}
	if (aantal == 3 && (!strcasecmp(tokens[0], "curtsey")) && (!strcasecmp(tokens[1], "to"))) {
		if (WriteMessageTo(tokens[2], name, room, "%s drops a curtsey to %s.<BR>\r\n", name, tokens[2]) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, "%s drops a curtsey to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You drop a curtsey to %s.</BR>\r\n", tokens[2]);
		}
		WriteRoom(name, password, room, 0);
		return 1;
	}
	return 0;
}

int
Flinch_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (!strcasecmp(command, "flinch")) {
		WriteSentenceIntoOwnLogFile(logname, "You flinch.<BR>\r\n");
		WriteMessage(name, room, "%s flinches.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		return 1;
	}
	if (aantal == 3 && (!strcasecmp(tokens[0], "flinch")) && (!strcasecmp(tokens[1], "to"))) {
		if (WriteMessageTo(tokens[2], name, room, "%s flinches to %s.<BR>\r\n", name, tokens[2]) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, "%s flinches to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You flinch to %s.</BR>\r\n", tokens[2]);
		}
		WriteRoom(name, password, room, 0);
		return 1;
	}
	return 0;
}

int
Tell_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if ((aantal > 3) && (!strcasecmp("to", tokens[1]))) {
		if (!WriteLinkTo(tokens[2], name, "<B>%s tells you </B>: %s<BR>\r\n",
					    name, command + (tokens[3] - tokens[0]))) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSentenceIntoOwnLogFile(logname, "<B>You tell %s</B> : %s<BR>\r\n",
			      tokens[2], command + (tokens[3] - tokens[0]));
		}
		WriteRoom(name, password, room, 0);
		return 1;
	}
	return 0;
}

int
Say_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if ((!strcasecmp("to", tokens[1])) && (aantal > 3)) 
	{
		if (!WriteMessageTo(tokens[2], name, room, "%s says [to %s] : %s<BR>\r\n",
				    name, tokens[2], command + (tokens[3] - tokens[0]))) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		}
		else 
		{
			/* person not found */ 
			WriteSayTo(tokens[2], name, room, 
				   "<B>%s says [to you]</B> : %s<BR>\r\n", name, command + (tokens[3] - tokens[0]));
			WriteSentenceIntoOwnLogFile(logname, "<B>You say [to %s]</B> : %s<BR>\r\n", tokens[2], command + (tokens[3] - tokens[0]));
			ReadBill(tokens[2], command + (tokens[3] - tokens[0]), name, room);
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
Shout_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if ((aantal > 3) && (!strcasecmp("to", tokens[1])))
	{
		char           *temp1, *temp2;
		temp1 = (char *) malloc(cgiContentLength + 80);
		temp2 = (char *) malloc(cgiContentLength + 80);
		sprintf(temp1, "<B>%s shouts [to you] </B>: %s<BR>\r\n",
			name, command + (tokens[3] - tokens[0]));
		sprintf(temp2, "%s shouts [to %s] : %s<BR>\r\n",
			name, tokens[2], command + (tokens[3] - tokens[0]));
		if (!WriteMessageTo(tokens[2], name, room, temp2)) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, temp1);
			WriteSentenceIntoOwnLogFile(logname, "<B>You shout [to %s] </B>: %s<BR>\r\n",
						     tokens[2], command + (tokens[3] - tokens[0]));
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
Ask_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if ((!strcasecmp("to", tokens[1])) && (aantal > 3)) 
	{
		char           *temp1, *temp2;
		temp1 = (char *) malloc(cgiContentLength + 80);
		temp2 = (char *) malloc(cgiContentLength + 80);
		sprintf(temp1, "<B>%s asks you </B>: %s<BR>\r\n",
			name, command + (tokens[3] - tokens[0]));
		sprintf(temp2, "%s asks %s : %s<BR>\r\n",
			name, tokens[2], command + (tokens[3] - tokens[0]));
		if (!WriteMessageTo(tokens[2], name, room, temp2)) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, temp1);
			WriteSentenceIntoOwnLogFile(logname, "<B>You ask %s</B> : %s<BR>\r\n",
						     tokens[2], command + (tokens[3] - tokens[0]));
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
Whisper_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	if ((!strcasecmp("to", tokens[1])) && (aantal > 3)) {
		char           *temp1, *temp2;
		temp1 = (char *) malloc(cgiContentLength + 80);
		temp2 = (char *) malloc(cgiContentLength + 80);
		sprintf(temp1, "<B>%s whispers [to you]</B> : %s<BR>\r\n",
			name, command + (tokens[3] - tokens[0]));
		sprintf(temp2, "%s is whispering something to %s, but you cannot hear what.<BR>\r\n",
			name, tokens[2]);
		if (!WriteMessageTo(tokens[2], name, room, temp2)) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, temp1);
			WriteSentenceIntoOwnLogFile(logname, "<B>You whisper [to %s]</B> : %s<BR>\r\n",
						     tokens[2], command + (tokens[3] - tokens[0]));
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
Awaken_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader,name);
	WriteSentenceIntoOwnLogFile(logname, "You aren't asleep, silly.<BR>\r\n");
	WriteRoom(name, password, room, 0);
	return 1;
}

int
Look_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
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
	
	if (aantal > 1) {
		LookItem_Command(name, password, room);
		return 1;
	}
	return 0;
}

int
Get_Command(char *name, char *fpassword, int room, char **ftokens, char *fcommand)
{
	if ( ((aantal==3) || (aantal==4)) && (!strcasecmp("get", tokens[0])) )
	{
	/* get copper coin(s)
	   get 10 copper coin(s)
	*/
		if ((!strcasecmp("coin",tokens[aantal-1])) ||
			(!strcasecmp("coins", tokens[aantal-1])) )
		{
			if ((!strcasecmp("copper",tokens[aantal-2])) ||
				(!strcasecmp("silver",tokens[aantal-2])) ||
				(!strcasecmp("gold", tokens[aantal-2])) )
			{
				GetMoney_Command(name, password, room);
				return 1;
			}
		}
	}
	if (aantal >= 2) 
	{
		GetItem_Command(name, password, room);
		return 1;
	}
}

int
Drop_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	if ( ((aantal==3) || (aantal==4)) && (!strcasecmp("drop", tokens[0])) )
	{
	/* drop copper coin(s)
	   drop 10 copper coin(s)
	*/
		if ((!strcasecmp("coin",tokens[aantal-1])) ||
			(!strcasecmp("coins", tokens[aantal-1])) )
		{
			if ((!strcasecmp("copper",tokens[aantal-2])) ||
				(!strcasecmp("silver",tokens[aantal-2])) ||
				(!strcasecmp("gold", tokens[aantal-2])) )
			{
				DropMoney_Command(name, password, room);
				return 1;
			}
		}
	}
	if ((aantal >= 2) && (!strcasecmp("drop", tokens[0]))) 
	{
		DropItem_Command(name, password, room);
		return 1;
	}
}

int
Buy_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==9)) 
	{
		BuyItem_Command(name, password, room, "Bill");
	}
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==16)) 
	{
		BuyItem_Command(name, password, room, "Karcas");
	}
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==64)) 
	{
		BuyItem_Command(name, password, room, "Karina");
	}
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==73))
	{
		BuyItem_Command(name, password, room, "Hagen");
	}
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==85)) 
	{
		BuyItem_Command(name, password, room, "Karsten");
	}
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==87))
	{
		BuyItem_Command(name, password, room, "Kurst");
	}
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==161))
	{
		BuyItem_Command(name, password, room, "Karstare");
	}
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==228))
	{
		BuyItem_Command(name, password, room, "Vimrilad");
	}
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==2550))
	{
		BuyItem_Command(name, password, room, "Telios");
	}
	if ((aantal >= 2) && (!strcasecmp("buy", tokens[0])) && (room==2551))
	{
		BuyItem_Command(name, password, room, "Nolli");
	}
	return 0;
}

int
Sell_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	if ((aantal >= 2) && (!strcasecmp("sell", tokens[0])) && (room==16)) 
	{
		SellItem_Command(name, password, room, "Karcas");
		return 1;
	}
	return 0;
}

int
Give_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
{
	if ( ((aantal==5) || (aantal==6)) && (!strcasecmp("give", tokens[0])) )
	{
	/* give copper coin(s)
	   give 10 copper coin(s)
	*/
		if ((!strcasecmp("coin",tokens[aantal-3])) ||
			(!strcasecmp("coins", tokens[aantal-3])) )
		{
			if ((!strcasecmp("copper",tokens[aantal-4])) ||
				(!strcasecmp("silver",tokens[aantal-4])) ||
				(!strcasecmp("gold", tokens[aantal-4])) )
			{
				GiveMoney_Command(name, password, room);
				return 1;
			}
		}
	}
	if ((aantal >= 4) && (!strcasecmp("give", tokens[0]))) 
	{
		GiveItem_Command(name, password, room);
		return 1;
	}
	return 0;
}

int
RangerGuild_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
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
		if ( (!strcasecmp("nature", tokens[0])) && (!strcasecmp("talk", tokens[1])) )
		{
			RangerTalk(name, password, room);
			return 1;
		}
	}
	return 0;
}

int
MifGuild_Command(char *name, char *password, int room, char **ftokens, char *fcommand)
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
		if ( (!strcasecmp("magic", tokens[0])) && (!strcasecmp("talk", tokens[1])) )
		{
			MIFTalk(name, password, room);
			return 1;
		}
	}
	return 0;
}

int
gameMain(char *fcommand, char *fname, char *fpassword)
{
	int             oldroom;
	int             i;
	char						frames[10];
	char           *temp;
	char            logname[100];
	char 				*junk;
	gameFunction	*gameFunctionArray; /* array of function types */
	char				**gameCommands; /* string array */
	int				myNumberOfFunctions = 0;

	command = fcommand;
	name = fname;
	password = fpassword;

	umask(0000);

	InitVar(command);

	opendbconnection();

	if (SearchBanList(cgiRemoteAddr, name)) {BannedFromGame(name, cgiRemoteAddr);}

	if (!ExistUser(name)) {
		NotActive(name,password,1);
	}

//	openDatabase();
	sprintf(sqlstring, "select name, lok, sleep, room, lastlogin, god, sex, vitals, maxvital, guild, punishment "
		"from tmp_usertable "
		"where name='%s' and lok<>''", name);
	res=SendSQL2(sqlstring, NULL);
	if (res==NULL)
	{
		NotActive(name, password,2);
	}
	row = mysql_fetch_row(res);
	if (row==NULL)
	{
		NotActive(name, password,3);
	}
	
	strcpy(name, row[0]); /* copy name to name from database */
	room=atoi(row[3]); /* copy roomnumber to room from database */
	sleepstatus=atoi(row[2]); /* copy sleep to sleepstatus from database */
	godstatus=atoi(row[5]); /* copy godstatus of player from database */
	strcpy(sexstatus,row[6]); /* sex status (male, female) */
	if (atoi(row[7])>atoi(row[8])) { /* check vitals along with maxvital */
		Dead(name, password, room);
	}
	strcpy(guildstatus	, row[9]);
	punishment = atoi(row[10]);
	if (strcmp(row[1], password)) {
		if (!commandlineinterface) 
		{
			NotActive(name, password,4);
		}
	}
	mysql_free_result(res);
	if (DebugOptionsOn) {
		if (!strcmp(name, "Karn")) {
			fprintf(cgiOut, "Command: %s<BR>ActivePersonPos: %i<BR>Password: %s<BR>", command, password);
		}
	}
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
			"address='%s' where name='%s'",	cgiRemoteAddr, name);
	res=SendSQL2(sqlstring, NULL);
	
	mysql_free_result(res);

	junk = (char *) malloc(strlen(command));
	strcpy(junk, command);
	tokens[0] = junk;
	tokens[0] = strtok(junk, " ");
	if (tokens[0] != NULL) {
		i = 1;
		while (i != -1) {
			tokens[i] = strtok(NULL, " ");
			i++;
			if (tokens[i - 1] == NULL) {
				aantal = i - 1;
				i = -1;
			}	/* endif */
			if (i>95) {
				aantal = i - 1;
				i = -1;
			}	/* endif */
		}		/* endwhile */
	}			/* endif */

	if ((strstr(command,"<applet")!=NULL) || (strstr(command,"<script")!=NULL)
		|| (strstr(command,"java-script")!=NULL) || (strstr(command,"CommandForm")!=NULL)) { 
		WriteSentenceIntoOwnLogFile(logname, "I am afraid, I do not understand that.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
		}

	if (sleepstatus==1) 
	{
		if (!strcasecmp(command, "awaken"))
		{
			Awaken2_Command(name, password, room);
		}
		WriteSentenceIntoOwnLogFile(logname, "You can't do that. You are asleep, silly.<BR>\r\n");
		WriteRoom(name, password, room, 1);
		KillGame();
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
		KillGame();
		}
		if ((*command == '\0') ||
		(!strcasecmp(command, "look around")) ||
		(!strcasecmp(command, "look")) ||
		(!strcasecmp(command, "l"))) {
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		if ((!strcasecmp(command, "go west")) ||
		(!strcasecmp(command, "west")) ||
		(!strcasecmp(command, "w"))) {
				GoWest_Command(name, password, room, tokens, command);
		}
		if ((!strcasecmp(command, "go east")) ||
		(!strcasecmp(command, "east")) ||
		(!strcasecmp(command, "e"))) {
			GoEast_Command(name, password, room, tokens, command);
		}
		if ((!strcasecmp(command, "go north")) ||
		(!strcasecmp(command, "north")) ||
		(!strcasecmp(command, "n"))) {
			GoNorth_Command(name, password, room, tokens, command);
		}
		if ((!strcasecmp(command, "go south")) ||
		(!strcasecmp(command, "south")) ||
		(!strcasecmp(command, "s"))) {
			GoSouth_Command(name, password, room, tokens, command);
		}
	
		if ((!strcasecmp(command, "go down")) ||
		(!strcasecmp(command, "down"))) {
			GoDown_Command(name, password, room, tokens, command);
		}
		if ((!strcasecmp(command, "go up")) ||
		(!strcasecmp(command, "up"))) {
			GoUp_Command(name, password, room, tokens, command);
		}
		if (!strcasecmp(command, "quit")) 
		{
			Quit_Command(name, password, room, tokens, command);
		}

	WriteSentenceIntoOwnLogFile(logname, "You cannot do that, you are a frog, remember?<BR>\r\n");
	WriteRoom(name, password, room, 0);
	KillGame();
	}

	SearchForSpecialCommand(name, password, room);
	
	/* initialise and fill the Function array */
	gameFunctionArray = (gameFunction *) malloc(sizeof(gameFunction)*100);
	gameCommands = (char **) malloc(sizeof(char *)*100);
	gameFunctionArray[myNumberOfFunctions++] = &Ask_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Awaken_Command;
	gameFunctionArray[myNumberOfFunctions++] = &BigTalk_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Bow_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Buy_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Clear_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Curtsey_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Date_Command;
	gameFunctionArray[myNumberOfFunctions++] = &DeleteMail_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoDown_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Drink_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoEast_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoEast_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Eat_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Eyebrow_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Fight_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Flinch_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Get_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Give_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Go_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Help_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Inventory_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Inventory_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Look_Command;
	gameFunctionArray[myNumberOfFunctions++] = &ListMail_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Look_Command;
	gameFunctionArray[myNumberOfFunctions++] = &MifGuild_Command;
	gameFunctionArray[myNumberOfFunctions++] = &MailFormDumpOnScreen;
	gameFunctionArray[myNumberOfFunctions++] = &Me_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoNorth_Command;
	gameFunctionArray[myNumberOfFunctions++] = &RangerGuild_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoNorth_Command;
	gameFunctionArray[myNumberOfFunctions++] = &PKill_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Put_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Quit_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Read_Command;
	gameFunctionArray[myNumberOfFunctions++] = &ReadMail_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Unwear_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Retrieve_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoSouth_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Say_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Search_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Sell_Command;
	gameFunctionArray[myNumberOfFunctions++] = &SendMail_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Shout_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Sleep_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoSouth_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Stats_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Stop_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Tell_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Time_Command;
	gameFunctionArray[myNumberOfFunctions++] = &ChangeTitle_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Unwield_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoUp_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoWest_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Wear_Command;
	gameFunctionArray[myNumberOfFunctions++] = &GoWest_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Whimpy_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Whisper_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Who_Command;
	gameFunctionArray[myNumberOfFunctions++] = &Wield_Command;
	myNumberOfFunctions=0;
	/* abcdefghijklmnopqrstuvwxyz */
	gameCommands[myNumberOfFunctions++] = "ask";
	gameCommands[myNumberOfFunctions++] = "awaken";
	gameCommands[myNumberOfFunctions++] = "bigtalk";
	gameCommands[myNumberOfFunctions++] = "bow";
	gameCommands[myNumberOfFunctions++] = "buy";
	gameCommands[myNumberOfFunctions++] = "clear";
	gameCommands[myNumberOfFunctions++] = "curtsey";
	gameCommands[myNumberOfFunctions++] = "date";
	gameCommands[myNumberOfFunctions++] = "deletemail";
	gameCommands[myNumberOfFunctions++] = "down";
	gameCommands[myNumberOfFunctions++] = "drink";
	gameCommands[myNumberOfFunctions++] = "e";
	gameCommands[myNumberOfFunctions++] = "east";
	gameCommands[myNumberOfFunctions++] = "eat";
	gameCommands[myNumberOfFunctions++] = "eyebrow";
	gameCommands[myNumberOfFunctions++] = "fight";
	gameCommands[myNumberOfFunctions++] = "flinch";
	gameCommands[myNumberOfFunctions++] = "get";
	gameCommands[myNumberOfFunctions++] = "give";
	gameCommands[myNumberOfFunctions++] = "go";
	gameCommands[myNumberOfFunctions++] = "help";
	gameCommands[myNumberOfFunctions++] = "i";
	gameCommands[myNumberOfFunctions++] = "inventory";
	gameCommands[myNumberOfFunctions++] = "l";
	gameCommands[myNumberOfFunctions++] = "listmail";
	gameCommands[myNumberOfFunctions++] = "look";
	gameCommands[myNumberOfFunctions++] = "magic";
	gameCommands[myNumberOfFunctions++] = "mail";
	gameCommands[myNumberOfFunctions++] = "me";
	gameCommands[myNumberOfFunctions++] = "n";
	gameCommands[myNumberOfFunctions++] = "nature";
	gameCommands[myNumberOfFunctions++] = "north";
	gameCommands[myNumberOfFunctions++] = "pkill";
	gameCommands[myNumberOfFunctions++] = "put";
	gameCommands[myNumberOfFunctions++] = "quit";
	gameCommands[myNumberOfFunctions++] = "read";
	gameCommands[myNumberOfFunctions++] = "readmail";
	gameCommands[myNumberOfFunctions++] = "remove";
	gameCommands[myNumberOfFunctions++] = "retrieve";
	gameCommands[myNumberOfFunctions++] = "s";
	gameCommands[myNumberOfFunctions++] = "say";
	gameCommands[myNumberOfFunctions++] = "search";
	gameCommands[myNumberOfFunctions++] = "sell";
	gameCommands[myNumberOfFunctions++] = "sendmail";
	gameCommands[myNumberOfFunctions++] = "shout";
	gameCommands[myNumberOfFunctions++] = "sleep";
	gameCommands[myNumberOfFunctions++] = "south";
	gameCommands[myNumberOfFunctions++] = "stats";
	gameCommands[myNumberOfFunctions++] = "stop";
	gameCommands[myNumberOfFunctions++] = "tell";
	gameCommands[myNumberOfFunctions++] = "time";
	gameCommands[myNumberOfFunctions++] = "title";
	gameCommands[myNumberOfFunctions++] = "unwield";
	gameCommands[myNumberOfFunctions++] = "up";
	gameCommands[myNumberOfFunctions++] = "w";
	gameCommands[myNumberOfFunctions++] = "wear";
	gameCommands[myNumberOfFunctions++] = "west";
	gameCommands[myNumberOfFunctions++] = "whimpy";
	gameCommands[myNumberOfFunctions++] = "whisper";
	gameCommands[myNumberOfFunctions++] = "who";
	gameCommands[myNumberOfFunctions++] = "wield";
	{
		/* binary search in index, if found call function */
		int i = (myNumberOfFunctions / 2) + (myNumberOfFunctions % 2);
		int pos = myNumberOfFunctions / 2;
		int equals = strcasecmp(gameCommands[pos], tokens[0]);
		if (commandlineinterface)
		{
			fprintf(cgiOut, "%i\n", myNumberOfFunctions);
		 	fprintf(cgiOut, "%i, %i, %s, %s\n", i, pos, tokens[0], gameCommands[pos]);
		}
		while ((i!=0) && (equals))
		{
			if (i==1) 
			{
				i = 0;
			}
			i = (i / 2) + (i % 2);
			if (equals > 0) {pos -= i;}
			if (equals < 0) {pos += i;}
			if ((pos >= 0) && (pos < myNumberOfFunctions))
			{
				if (commandlineinterface)
				{
					fprintf(cgiOut, "%i, %i, %s, %s\n", i, pos, tokens[0], gameCommands[pos]);
				}
				equals = strcasecmp(gameCommands[pos], tokens[0]);
			}
		}
		if (!equals)
		{
			/* string has been found */
			if (gameFunctionArray[pos](name, password, room, tokens, command))
			{
				/* command executed successfully, kill this session */
				KillGame();
			}
			{
				/* do nothing, the game will in time find out that there is no appropriate response to the bogus command and will produce an error message. Darnit people, why do these damn comment lines sometimes have to be so long! And who turned off my word wrap anyway!!! */
			}
		}
	}
	
	if (godstatus==1) {Root_Command(name, password, room);}
	if (godstatus==2) {Evil_Command(name, password, room);}


/*	if (!strcasecmp(command, "introduce me")) {
		IntroduceMe_Command(logname);
	}*/
	/* smile */
	if ((temp = get_pluralis(command)) != NULL) {
		WriteSentenceIntoOwnLogFile(logname, "You %s.<BR>\r\n", command);
		WriteMessage(name, room, "%s %s.<BR>\r\n", name, temp);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	/* smile engagingly */
	if ( (aantal==2) && ((temp = get_pluralis(tokens[0])) != NULL) &&
		 (exist_adverb(tokens[1])) ) {
		WriteSentenceIntoOwnLogFile(logname, "You %s %s.<BR>\r\n", tokens[0], tokens[1]);
		WriteMessage(name, room, "%s %s %s.<BR>\r\n", name, temp, tokens[1]);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	/* smile to bill */
	if ((aantal == 3) && ((temp = get_pluralis(tokens[0])) != NULL) && 
		(!strcasecmp(tokens[1], "to")) ) {
		if (WriteMessageTo(tokens[2], name, room, "%s %s to %s.<BR>\r\n", name, temp, tokens[2]) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, "%s %s to you.<BR>\r\n", name, temp);
			WriteSentenceIntoOwnLogFile(logname, "You %s to %s.</BR>\r\n", tokens[0], tokens[2]);
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	/* smile(0) engagingly(1) to(2) Bill(3) */
	if ((aantal == 4) && ((temp = get_pluralis(tokens[0])) != NULL) && 
		(!strcasecmp(tokens[2], "to")) && (exist_adverb(tokens[1])) ) {
		if (WriteMessageTo(tokens[3], name, room, "%s %s %s to %s.<BR>\r\n", name, temp, tokens[1], tokens[3]) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[3], name, room, "%s %s %s to you.<BR>\r\n", name, temp, tokens[1]);
			WriteSentenceIntoOwnLogFile(logname, "You %s %s to %s.</BR>\r\n", tokens[0], tokens[1], tokens[3]);
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	/* multiple person emotions */
	/* caress bill */
	if ((aantal == 2) && ((temp = get_pluralis2(tokens[0])) != NULL)) {
		char            temp1[50], temp2[50];
		sprintf(temp1, "%s %s you.<BR>\r\n", name, temp);
		sprintf(temp2, "%s %s %s.<BR>\r\n", name, temp, tokens[1]);
		if (!WriteMessageTo(tokens[1], name, room, "%s %s %s.<BR>\r\n",
					    name, temp, tokens[1])) {
			WriteSentenceIntoOwnLogFile(logname, "That user doesn't exist.<BR>\r\n");
		} else {
			WriteSayTo(tokens[1], name, room,
				"%s %s you.<BR>\r\n", name, temp);
			WriteSentenceIntoOwnLogFile(logname, "You %s %s.<BR>\r\n", tokens[0], tokens[1]);
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}			/* end of multiple persons emotions */
	/* caress bill absentmindedly */
	if ((aantal == 3) && ((temp = get_pluralis2(tokens[0])) != NULL) &&
		(exist_adverb(tokens[2])) ) {
		char            temp1[50], temp2[50];
		sprintf(temp1, "%s %s you, %s.<BR>\r\n", name, temp, tokens[2]);
		sprintf(temp2, "%s %s %s, %s.<BR>\r\n", name, temp, tokens[1], tokens[2]);
		if (!WriteMessageTo(tokens[1], name, room, "%s %s %s, %s.<BR>\r\n",
					    name, temp, tokens[1], tokens[2])) {
			WriteSentenceIntoOwnLogFile(logname, "That user doesn't exist.<BR>\r\n");
		} else {
			WriteSayTo(tokens[1], name, room,
				"%s %s you, %s.<BR>\r\n", name, temp, tokens[2]);
			WriteSentenceIntoOwnLogFile(logname, "You %s %s, %s.<BR>\r\n", tokens[0], tokens[1], tokens[2]);
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}			/* end of multiple persons emotions */

/* add SWTalk */		
	if (!strcasecmp("SW", guildstatus))
	{
		if ( (!strcasecmp("pow", tokens[0])) && (!strcasecmp("wow", tokens[1])) )
		{
			SWTalk(name, password, room);
			KillGame();
		}

	}		

/* add BKTalk */		
	if (!strcasecmp("BKIC", guildstatus))
	{
		if ( (!strcasecmp("chaos", tokens[0])) && (!strcasecmp("murmur", tokens[1])) )
		{
			BKTalk(name, password, room);
			KillGame();
		}

	}

/* add VampTalk */		
	if (!strcasecmp("Kindred", guildstatus))
	{
		if ( (!strcasecmp("misty", tokens[0])) && (!strcasecmp("whisper", tokens[1])) )
		{
			VampTalk(name, password, room);
			KillGame();
		}

	}

/* add KnightTalk */		
	if (!strcasecmp("Knights", guildstatus))
	{
		if ( (!strcasecmp("knight", tokens[0])) && (!strcasecmp("talk", tokens[1])) )
		{
			KnightTalk(name, password, room);
			KillGame();
		}

	}

/* add CoDTalk */		
	if (!strcasecmp("CoD", guildstatus))
	{
		if ( (!strcasecmp("mogob", tokens[0])) && (!strcasecmp("burz", tokens[1])) )
		{
			CoDTalk(name, password, room);
			KillGame();
		}

	}

	/* End Guilds */

	WriteSentenceIntoOwnLogFile(logname, "I am afraid, I do not understand that.<BR>\r\n");
	WriteRoom(name, password, room, 0);
}

int 
cgiMain()
{
	char *command;
	char name[22];
	char password[40];
	char frames[10];

	if (commandlineinterface) {
		command = (char *) malloc(1000);
	} else {
		command = (char *) malloc(cgiContentLength);
	}

	if (commandlineinterface) {
		printf("Command:");
		gets(command);
		printf("Name:");
		gets(name);
		printf("Password:");
		gets(password);
		setFrames(0);
	}
	else 
	{
		char cookiepassword[40];
		cgiFormString("command", command, cgiContentLength - 2);
		if (command[0]==0) {strcpy(command,"l");}
		cgiFormString("name", name, 20);
		cgiFormString("password", password, 40);
		getCookie("Karchan", cookiepassword);
		if (strcmp(cookiepassword, password))
		{
			cgiHeaderContentType("text/html");
			NotActive(name, password,3);
		}
		if (cgiFormString("frames", frames, 10)!=cgiFormSuccess)
		{
			strcpy(frames, "none");
			setFrames(0);
		}
		if (!strcmp(frames,"1")) {setFrames(0);}
		if (!strcmp(frames,"2")) {setFrames(1);}
		if (!strcmp(frames,"3")) {setFrames(2);}
		WriteSentenceIntoOwnLogFile(BigFile,
				     "%s (%s): |%s|\n", name, password, command);

	}
	if (strcasecmp("quit", command))
	{
		cgiHeaderContentType("text/html");
	}
	else
	{
		fprintf(cgiOut, "Content-type: text/html\r\n");
		fprintf(cgiOut, "Set-cookie: Karchan=; expires= Monday, 01-January-01 00:05:00 GMT\r\n\r\n");
	}
	gameMain(command, name, password);
	free(command);
}
