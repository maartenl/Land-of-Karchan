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
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/
#include <time.h>
#include <string.h>
#include <stdlib.h>
#include "typedefs.h"
#include "userlib.h"
#include "parser.h"
#include "guild.h"
#include "mud-lib.h"
#include "mud-lib2.h"
#include "mud-lib3.h"

/*! \file mudmain.c
	\brief  server file containing the gameMain function that is used to parse and execute issued commands */

// type-definition: 'gameFunction' now can be used as type
// parameters: mudpersonstruct *
typedef int (*gameFunction)(mudpersonstruct *);

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
	
//! remove gamefunctionarray from memory, to be called at the end of mmserver
int
clearGameFunctionIndex()
{
	free(gameFunctionArray);
	return 1;
}

//! throw standard banned-from-game page to user
void BannedFromGame(char *name, char *address, int socketfd)
{
	time_t tijd;
	struct tm datum;
	send_printf(socketfd, "<HTML><HEAD><TITLE>You have been banned</TITLE></HEAD>\n\n");
	send_printf(socketfd, "<BODY>\n");
	send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Banned</H1><HR>\n");
	send_printf(socketfd, "You, or someone in your domain,  has angered the gods by behaving badly on this mud. ");
	send_printf(socketfd, "Your ip domain is therefore banned from the game.<P>\n");
	send_printf(socketfd, "If you have not misbehaved or even have never before played the game before, and wish"
	" to play with your current IP address, email to "
	"<A HREF=\"mailto:deputy@%s\">deputy@%s</A> and ask them to make "
	"an exception in your case. Do <I>not</I> forget to provide your "
	"Character name.<P>You'll be okay as long as you follow the rules.<P>\n", getParam(MM_SERVERNAME), getParam(MM_SERVERNAME));
	send_printf(socketfd, "</body>\n");
	send_printf(socketfd, "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i Banned from mud by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
}

//! throw standard cookie-not-found page to user
/*! cookie is not found if the webbrowser has disabled cookie support, or the person in question is trying something
nasty. The cookie in question should be the same as the session password the person is logging in
*/
void CookieNotFound(char *name, char *address, int socketfd)
{
	time_t tijd;
	struct tm datum;
	send_printf(socketfd, "<HTML><HEAD><TITLE>Unable to logon</TITLE></HEAD>\n\n");
	send_printf(socketfd, "<BODY>\n");
	send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Unable to logon</H1><HR>\n");
	send_printf(socketfd, "When you logon, a cookie is automatically generated. ");
	send_printf(socketfd, "However, I have been unable to find my cookie.<P>\n");
	send_printf(socketfd, "Please attempt to relogon.<P>\n");
	send_printf(socketfd, "</body>\n");
	send_printf(socketfd, "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i Cookie not found for mud by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
}

//! go somewhere command
/*! this is the command that is executed first, because it is triggered by the 'go' word in the beginning of a command
entry. Afterwards, it starts to decide in which direction and triggers the appropriate method for that
\see GoUp_Command
\see GoDown_Command
\see GoWest_Command
\see GoEast_Command
\see GoNorth_Command
\see GoSouth_Command
*/
int
Go_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	char *command;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	command = fmudstruct->command;
	if (getTokenAmount(fmudstruct) < 2)
	{
		return 0;
	}
	if (!strcasecmp(getToken(fmudstruct, 1), "west")) 
	{
		GoWest_Command(fmudstruct);	
		return 1;
	}
	if (!strcasecmp(getToken(fmudstruct, 1), "east")) 
	{
		GoEast_Command(fmudstruct);
		return 1;
	}
	if (!strcasecmp(getToken(fmudstruct, 1), "north"))
	{
		GoNorth_Command(fmudstruct);
		return 1;
	}
	if (!strcasecmp(getToken(fmudstruct, 1), "south"))
	{
		GoSouth_Command(fmudstruct);
		return 1;
	}
	if (!strcasecmp(getToken(fmudstruct, 1), "down"))
	{
		GoDown_Command(fmudstruct);
		return 1;
	}
	if (!strcasecmp(getToken(fmudstruct, 1), "up"))
	{
		GoUp_Command(fmudstruct);
		return 1;
	}
	return 0;
}

//! clear the log file of the player playing
/*! clearing of the log file of the player happens <I>after</I> displaying that entire log to the user.
This helps it for the user to determine what the last messages were that he/she received before the clear command
was executed. 
*/
int
Clear_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;

	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	WriteRoom(fmudstruct);
	ClearLogFile(logname);
	WriteSentenceIntoOwnLogFile(logname, "You cleared your mind.<BR>\r\n");
	return 1;
}

//! show general help or specific help on a command
int
Help_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if (!strcasecmp(fcommand, "help")) 
	{
		MYSQL_RES *res;
		MYSQL_ROW row;
		char *temp;
		
		send_printf(fmudstruct->socketfd, "<HTML>\r\n");
		send_printf(fmudstruct->socketfd, "<HEAD>\r\n");
		send_printf(fmudstruct->socketfd, "<TITLE>\r\n");
		send_printf(fmudstruct->socketfd, "Land of Karchan - General Help\r\n");
		send_printf(fmudstruct->socketfd, "</TITLE>\r\n");
		send_printf(fmudstruct->socketfd, "</HEAD>\r\n");
		
		send_printf(fmudstruct->socketfd, "<BODY>\r\n");
		send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\r\n");
	
		temp = composeSqlStatement("select contents from help where command='general help'");
		res=sendQuery(temp, NULL);
		free(temp);temp=NULL;

		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			mysql_free_result(res);
			res=sendQuery("select contents from help where command='sorry'", NULL);
			row = mysql_fetch_row(res);
			send_printf(fmudstruct->socketfd, "%s",row[0]);
		}
		else
		{
			send_printf(fmudstruct->socketfd, "%s",row[0]);
		}
		mysql_free_result(res);
		PrintForm(name, password, fmudstruct->frames, fmudstruct->socketfd);
		if (fmudstruct->frames!=2) {ReadFile(logname, fmudstruct->socketfd);}
		return 1;
	}
	if ((getTokenAmount(fmudstruct) >= 2) && (!strcasecmp(getToken(fmudstruct, 0),"help"))) 
	{
		MYSQL_RES *res;
		MYSQL_ROW row;
		char *temp;
		
		send_printf(fmudstruct->socketfd, "<HTML>\r\n");
		send_printf(fmudstruct->socketfd, "<HEAD>\r\n");
		send_printf(fmudstruct->socketfd, "<TITLE>\r\n");
		send_printf(fmudstruct->socketfd, "Land of Karchan - Command %s\r\n", getToken(fmudstruct, 1));
		send_printf(fmudstruct->socketfd, "</TITLE>\r\n");
		send_printf(fmudstruct->socketfd, "</HEAD>\r\n");
		
		send_printf(fmudstruct->socketfd, "<BODY>\r\n");
		send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\">\r\n");
	
		temp = composeSqlStatement("select contents from help where command='%x'", getToken(fmudstruct, 1));
		res=sendQuery(temp, NULL);
		free(temp);temp=NULL;

		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			mysql_free_result(res);
			res=sendQuery("select contents from help where command='sorry'", NULL);
			row = mysql_fetch_row(res);
			send_printf(fmudstruct->socketfd, "%s",row[0]);
		}
		else
		{
			send_printf(fmudstruct->socketfd, "%s",row[0]);
		}
		mysql_free_result(res);
		PrintForm(name, password, fmudstruct->frames, fmudstruct->socketfd);
		if (fmudstruct->frames!=2) {ReadFile(logname, fmudstruct->socketfd);}
		return 1;
	}
	return 0;
}

//! read the mail, actually is passed onto the next method
/*!
\see ReadMail
*/
int
ReadMail_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if (getTokenAmount(fmudstruct)!=2)
	{
		return 0;
	}
	if (!ReadMail(name, password, room, fmudstruct->frames, atoi(getToken(fmudstruct, 1)), 0, fmudstruct->socketfd))
	{
		WriteRoom(fmudstruct);
	};
	return 1;
}

//! delete the mail, actually is passed onto the next method
/*!
\see ReadMail
*/
int
DeleteMail_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	if (!ReadMail(name, password, room, fmudstruct->frames, atoi(getToken(fmudstruct, 1)), 2, fmudstruct->socketfd))
	{
		WriteRoom(fmudstruct);
	}
	return 1;
}

//! sendmail &lt;to&gt; &lt;headerlength&gt; &lt;header&gt; &lt;body&gt;
int
SendMail_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *mailto, *mailbody, *mailheader, *sqlstring;
	char logname[100];
	int thelength;
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
      		
	if (getTokenAmount(fmudstruct) < 5)
	{
		return 0;
	}

	mailto = getToken(fmudstruct, 1);
	thelength = atoi(getToken(fmudstruct, 2));
	if (thelength<1)
	{
		return 0;
	}
	mailheader = (char *) malloc(thelength+2);
	strncpy(mailheader, fcommand + (getToken(fmudstruct, 3)-getToken(fmudstruct, 0)), thelength);
	mailheader[thelength+1]=0;
	mailbody = fcommand + (getToken(fmudstruct, 3)-getToken(fmudstruct, 0)) + thelength + 1;
	//cgiFormString("mailto", mailto, 99);
	//cgiFormString("mailheader", mailheader, 99);
	//cgiFormString("mailbody", mailbody, strlen(fcommand) - 2);
		
	sqlstring = composeSqlStatement("select name from usertable where "
		"name='%x' and god<2", mailto);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	
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
	WriteRoom(fmudstruct);
	return 1;
}

//! set/show your whimpy
/*! whimpy determines at what damage level you attempt to 'flee the scene'
*/
int
Whimpy_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	char number[10];
	char *sqlstring;
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if (getTokenAmount(fmudstruct) == 1) {
		strcpy(number,"0");
	} else {
		if (!strcasecmp("help", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			WriteSentenceIntoOwnLogFile(logname, 
			"Syntax: <B>whimpy &lt;string&gt;</B><UL><LI>feeling well"
			"<LI>feeling fine<LI>feeling quite nice<LI>slightly hurt"
			"<LI>hurt<LI>quite hurt<LI>extremely hurt<LI>terribly hurt"
			"<LI>feeling bad<LI>feeling very bad<LI>at death's door</UL>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		if (!strcasecmp("feeling well", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"10");
			//x.whimpy = 10;
		}
		if (!strcasecmp("feeling fine", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"20");
			//x.whimpy = 20;
		}
		if (!strcasecmp("feeling quite nice", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"30");
			//x.whimpy = 30;
		}
		if (!strcasecmp("slightly hurt", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"40");
			//x.whimpy = 40;
		}
		if (!strcasecmp("hurt", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"50");
			//x.whimpy = 50;
		}
		if (!strcasecmp("quite hurt", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"60");
			//x.whimpy = 60;
		}
		if (!strcasecmp("extremely hurt", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"70");
			//x.whimpy = 70;
		}
		if (!strcasecmp("terribly hurt", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"80");
			//x.whimpy = 80;
		}
		if (!strcasecmp("feeling bad", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"90");
			//x.whimpy = 90;
		}
		if (!strcasecmp("feeling very bad", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"100");
			//x.whimpy = 100;
		}
		if (!strcasecmp("at death's door", fcommand + (getToken(fmudstruct, 1) - getToken(fmudstruct, 0)))) {
			strcpy(number,"110");
			//x.whimpy = 110;
		}
	}
	sqlstring = composeSqlStatement("update tmp_usertable set whimpy=%s "
		" where name='%x'", number, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res!=NULL)
	{
		mysql_free_result(res);
	}
	
	WriteSentenceIntoOwnLogFile(logname, "<I>Whimpy set.</I><BR>\r\n");
	WriteRoom(fmudstruct);
	return 1;
}

//! turn on/off player killing possibility for your player character
int
PKill_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if (getTokenAmount(fmudstruct) < 2)
	{
		return 0;
	}
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	sqlstring = composeSqlStatement("select fightingwho from tmp_usertable where "
		"name='%x'", name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	if (row[0][0]!=0)
	{
		mysql_free_result(res);
		WriteSentenceIntoOwnLogFile(logname, "You cannot change your pkill status during combat.<BR>\r\n");
	}
	else
	{
		mysql_free_result(res);
		if (!strcasecmp("on", getToken(fmudstruct, 1))) 
		{
			sqlstring = composeSqlStatement("update tmp_usertable set fightable=1 where "
				"name='%x'", name);
			res=sendQuery(sqlstring, NULL);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "Pkill is now on.<BR>\r\n");
		} /* pkill is turned on */
		else
		{
			sqlstring = composeSqlStatement("update tmp_usertable set fightable=0 where "
				"name='%x'", name);
			res=sendQuery(sqlstring, NULL);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "Pkill is now off.<BR>\r\n");
		} /* pkill is turned of */
	} /* if indeed the person is not already fighting */
	WriteRoom(fmudstruct);
	return 1;
}

//! do an action/emote
/*! for example: <i>me opens the fridge</i>
*/
int
Me_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *command;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	command = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	WriteMessage(name, room, "%s %s<BR>\r\n", name, command + 3);
	WriteSentenceIntoOwnLogFile(logname, "%s %s<BR>\r\n", name, command + 3);
	WriteRoom(fmudstruct);
	return 1;
}

//! stop fighting
int
Stop_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char logname[100];
	char *sqlstring;
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if (!strcasecmp("stop fighting", fcommand))
	{
		sqlstring = composeSqlStatement("select fightingwho from tmp_usertable where "
			"name='%x'", name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		row = mysql_fetch_row(res);
		if (row[0][0]==0)
		{
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "You are not fighting anyone.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		mysql_free_result(res);
		sqlstring = composeSqlStatement("update tmp_usertable set fightingwho='' where "
			"name='%x'"
			, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		WriteMessage(name, room, "%s stops fighting.<BR>\r\n", name);
		WriteSentenceIntoOwnLogFile(logname, "You stop fighting.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	return 0;
}

//! start fighting
int 
Fight_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int myFightable;
	char *myFightingName;
	char *sqlstring;
	char *myDescription;
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	sqlstring = composeSqlStatement("select fightable from tmp_usertable where "
		"name='%x'", name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	myFightable = atoi(row[0]);
	mysql_free_result(res);

	/* this section takes care of the looking up of the description */
	myFightingName = ExistUserByDescription(fmudstruct, 1, getTokenAmount(fmudstruct) - 1, room, &myDescription);
	if (myFightingName == NULL)
	{
		myFightingName = getToken(fmudstruct, 1);
		myDescription = getToken(fmudstruct, 1);
	}
	
	sqlstring = composeSqlStatement("select name,god from tmp_usertable where "
	"name<>'%x' and "
	"name='%x' and "
	"fightable=1 and "
	"god<>2 and "
	"room=%i"
	, name, myFightingName, room);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	if ((myFightable!=1) && (atoi(row[1])!=3))
	{
		mysql_free_result(res);
		if (myFightingName != getToken(fmudstruct, 1)) {free(myFightingName);}
		if (myDescription != getToken(fmudstruct, 1)) {free(myDescription);}
		WriteSentenceIntoOwnLogFile(logname, "Pkill is off, so you cannot fight.<BR>\r\n");
		WriteRoom(fmudstruct);
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
		sqlstring = composeSqlStatement("update tmp_usertable set fightingwho='%x' where name='%x'",
		myFightingName, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		sqlstring = composeSqlStatement("update tmp_usertable set fightingwho='%x' where name='%x'",
		name, myFightingName);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
	}
	else
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
	}
	mysql_free_result(res);
	if (myFightingName != getToken(fmudstruct, 1)) {free(myFightingName);}
	if (myDescription != getToken(fmudstruct, 1)) {free(myDescription);}
	WriteRoom(fmudstruct);
	return 1;
}

//! bow (to somebody possibly)
int
Bow_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if (!strcasecmp(fcommand, "bow")) {
		WriteSentenceIntoOwnLogFile(logname, "You bow gracefully.<BR>\r\n");
		WriteMessage(name, room, "%s bows gracefully.<BR>\r\n", name);
		WriteRoom(fmudstruct);
		return 1;
	}
	if (getTokenAmount(fmudstruct) == 3 && (!strcasecmp(getToken(fmudstruct, 0), "bow")) && (!strcasecmp(getToken(fmudstruct, 1), "to"))) {
		if (WriteMessageTo(getToken(fmudstruct, 2), name, room, "%s bows gracefully to %s.<BR>\r\n", name, getToken(fmudstruct, 2)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(fmudstruct, 2), name, room, "%s bows gracefully to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You bow gracefully to %s.<BR>\r\n", getToken(fmudstruct, 2));
		}
		WriteRoom(fmudstruct);
		return 1;
	}
	return 0;
}

//! raise an eyebrow
int
Eyebrow_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	WriteSentenceIntoOwnLogFile(logname, "You raise an eyebrow.<BR>\r\n");
	WriteMessage(name, room, "%s raises an eyebrow.<BR>\r\n", name);
	WriteRoom(fmudstruct);
	return 1;
}

//! curtsey to someone
int
Curtsey_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if (!strcasecmp(fcommand, "curtsey")) {
		WriteSentenceIntoOwnLogFile(logname, "You drop a curtsey.<BR>\r\n");
		WriteMessage(name, room, "%s drops a curtsey.<BR>\r\n", name);
		WriteRoom(fmudstruct);
		return 1;
	}
	if (getTokenAmount(fmudstruct) == 3 && (!strcasecmp(getToken(fmudstruct, 0), "curtsey")) && (!strcasecmp(getToken(fmudstruct, 1), "to"))) {
		if (WriteMessageTo(getToken(fmudstruct, 2), name, room, "%s drops a curtsey to %s.<BR>\r\n", name, getToken(fmudstruct, 2)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(fmudstruct, 2), name, room, "%s drops a curtsey to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You drop a curtsey to %s.</BR>\r\n", getToken(fmudstruct, 2));
		}
		WriteRoom(fmudstruct);
		return 1;
	}
	return 0;
}

//! flinch
int
Flinch_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if (!strcasecmp(fcommand, "flinch")) {
		WriteSentenceIntoOwnLogFile(logname, "You flinch.<BR>\r\n");
		WriteMessage(name, room, "%s flinches.<BR>\r\n", name);
		WriteRoom(fmudstruct);
		return 1;
	}
	if (getTokenAmount(fmudstruct) == 3 && (!strcasecmp(getToken(fmudstruct, 0), "flinch")) && (!strcasecmp(getToken(fmudstruct, 1), "to"))) {
		if (WriteMessageTo(getToken(fmudstruct, 2), name, room, "%s flinches to %s.<BR>\r\n", name, getToken(fmudstruct, 2)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(fmudstruct, 2), name, room, "%s flinches to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You flinch to %s.</BR>\r\n", getToken(fmudstruct, 2));
		}
		WriteRoom(fmudstruct);
		return 1;
	}
	return 0;
}

//! tell to person message
int
Tell_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if ((getTokenAmount(fmudstruct) > 3) && (!strcasecmp("to", getToken(fmudstruct, 1)))) {
		if (!WriteLinkTo(getToken(fmudstruct, 2), name, "<B>%s tells you </B>: %s<BR>\r\n",
					    name, fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)))) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSentenceIntoOwnLogFile(logname, "<B>You tell %s</B> : %s<BR>\r\n",
			      getToken(fmudstruct, 2), fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
		}
		WriteRoom(fmudstruct);
		return 1;
	}
	return 0;
}

//! say to person message, where person is optional
int
Say_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if (getTokenAmount(fmudstruct) == 1)
	{
		return 0;
	}
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if ((!strcasecmp("to", getToken(fmudstruct, 1))) && (getTokenAmount(fmudstruct) > 3)) 
	{
		if (!WriteMessageTo(getToken(fmudstruct, 2), name, room, "%s says [to %s] : %s<BR>\r\n",
				    name, getToken(fmudstruct, 2), fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)))) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		}
		else 
		{
			/* person not found */ 
			WriteSayTo(getToken(fmudstruct, 2), name, room, 
				   "<B>%s says [to you]</B> : %s<BR>\r\n", name, fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
			WriteSentenceIntoOwnLogFile(logname, "<B>You say [to %s]</B> : %s<BR>\r\n", getToken(fmudstruct, 2), fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
			ReadBill(getToken(fmudstruct, 2), fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)), name, room);
		}
		WriteRoom(fmudstruct);
		return 1;
	}
	WriteSentenceIntoOwnLogFile(logname, "<B>You say </B>: %s<BR>\r\n", fcommand + 4);
	WriteMessage(name, room, "%s says : %s<BR>\r\n", name, fcommand + 4);
	WriteRoom(fmudstruct);
	return 1;
}

//! shout to person message, where person is optional
int
Shout_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if (getTokenAmount(fmudstruct) == 1)
	{
		return 0;
	}
	if ((getTokenAmount(fmudstruct) > 3) && (!strcasecmp("to", getToken(fmudstruct, 1))))
	{
		char           *temp1, *temp2;
		temp1 = (char *) malloc(strlen(fcommand) + 80);
		temp2 = (char *) malloc(strlen(fcommand) + 80);
		sprintf(temp1, "<B>%s shouts [to you] </B>: %s<BR>\r\n",
			name, fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
		sprintf(temp2, "%s shouts [to %s] : %s<BR>\r\n",
			name, getToken(fmudstruct, 2), fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
		if (!WriteMessageTo(getToken(fmudstruct, 2), name, room, temp2)) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(fmudstruct, 2), name, room, temp1);
			WriteSentenceIntoOwnLogFile(logname, "<B>You shout [to %s] </B>: %s<BR>\r\n",
						     getToken(fmudstruct, 2), fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
		}
		free(temp2);
		free(temp1);
		WriteRoom(fmudstruct);
		return 1;
	}
	WriteSentenceIntoOwnLogFile(logname, "<B>You shout</B> : %s<BR>\r\n", fcommand + 6);
	WriteMessage(name, room, "%s shouts : %s<BR>\r\n", name, fcommand + 6);
	WriteRoom(fmudstruct);
	return 1;
}

//! ask to person message, where person is optional
/*! bots will only react to THIS command
*/
int
Ask_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if (getTokenAmount(fmudstruct) == 1)
	{
		return 0;
	}
	if ((!strcasecmp("to", getToken(fmudstruct, 1))) && (getTokenAmount(fmudstruct) > 3)) 
	{
		char           *temp1, *temp2;
		temp1 = (char *) malloc(strlen(fcommand) + 80);
		temp2 = (char *) malloc(strlen(fcommand) + 80);
		sprintf(temp1, "<B>%s asks you </B>: %s<BR>\r\n",
			name, fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
		sprintf(temp2, "%s asks %s : %s<BR>\r\n",
			name, getToken(fmudstruct, 2), fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
		if (!WriteMessageTo(getToken(fmudstruct, 2), name, room, temp2)) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(fmudstruct, 2), name, room, temp1);
			WriteSentenceIntoOwnLogFile(logname, "<B>You ask %s</B> : %s<BR>\r\n",
						     getToken(fmudstruct, 2), fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
		}
		free(temp2);
		free(temp1);
		WriteRoom(fmudstruct);
		return 1;
	}
	WriteSentenceIntoOwnLogFile(logname, "<B>You ask</B> : %s<BR>\r\n", fcommand + 4);
	WriteMessage(name, room, "%s asks : %s<BR>\r\n", name, fcommand + 4);
	WriteRoom(fmudstruct);
	return 1;
}

//! whisper to person message, where person is optional
/*! however, if you do not enter a person, the whispered message can be heard by everyone in the room! 
*/
int
Whisper_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if (getTokenAmount(fmudstruct) == 1)
	{
		return 0;
	}
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if ((!strcasecmp("to", getToken(fmudstruct, 1))) && (getTokenAmount(fmudstruct) > 3)) {
		char           *temp1, *temp2;
		temp1 = (char *) malloc(strlen(fcommand) + 80);
		temp2 = (char *) malloc(strlen(fcommand) + 80);
		sprintf(temp1, "<B>%s whispers [to you]</B> : %s<BR>\r\n",
			name, fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
		sprintf(temp2, "%s is whispering something to %s, but you cannot hear what.<BR>\r\n",
			name, getToken(fmudstruct, 2));
		if (!WriteMessageTo(getToken(fmudstruct, 2), name, room, temp2)) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(fmudstruct, 2), name, room, temp1);
			WriteSentenceIntoOwnLogFile(logname, "<B>You whisper [to %s]</B> : %s<BR>\r\n",
						     getToken(fmudstruct, 2), fcommand + (getToken(fmudstruct, 3) - getToken(fmudstruct, 0)));
		}
		free(temp2);
		free(temp1);
		WriteRoom(fmudstruct);
		return 1;
	}
	WriteSentenceIntoOwnLogFile(logname, "<B>You whisper </B>: %s<BR>\r\n", fcommand + 8);
	WriteMessage(name, room, "%s whispers : %s<BR>\r\n", name, fcommand + 8);
	WriteRoom(fmudstruct);
	return 1;
}

//! wake up
int 
Awaken_Command(mudpersonstruct *fmudstruct)
{
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	WriteSentenceIntoOwnLogFile(logname, "You aren't asleep, silly.<BR>\r\n");
	WriteRoom(fmudstruct);
	return 1;
}

//! look
/*! rather extensive command, following is possible:
<UL><LI>look (around)
<LI>look at item
<LI>look at person
</UL>
*/
int
Look_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if ((*fcommand == '\0') ||
	    (!strcasecmp(fcommand, "look around")) ||
	    (!strcasecmp(fcommand, "look")) ||
	    (!strcasecmp(fcommand, "l"))) 
	{
		WriteRoom(fmudstruct);
		return 1;
	}
	
	if (getTokenAmount(fmudstruct) > 2) 
	{
		LookItem_Command(fmudstruct);
		return 1;
	}
	return 0;
}

//! get an item
/*! item must be on the floor
*/
int
Get_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if ( ((getTokenAmount(fmudstruct)==3) || (getTokenAmount(fmudstruct)==4)) && (!strcasecmp("get", getToken(fmudstruct, 0))) )
	{
	/* get copper coin(s)
	   get 10 copper coin(s)
	*/
		if ((!strcasecmp("coin",getToken(fmudstruct, getTokenAmount(fmudstruct)-1))) ||
			(!strcasecmp("coins", getToken(fmudstruct, getTokenAmount(fmudstruct)-1))) )
		{
			if ((!strcasecmp("copper",getToken(fmudstruct, getTokenAmount(fmudstruct)-2))) ||
				(!strcasecmp("silver",getToken(fmudstruct, getTokenAmount(fmudstruct)-2))) ||
				(!strcasecmp("gold", getToken(fmudstruct, getTokenAmount(fmudstruct)-2))) )
			{
				GetMoney_Command(fmudstruct);
				return 1;
			}
		}
	}
	if (getTokenAmount(fmudstruct) >= 2) 
	{
		GetItem_Command(fmudstruct);
		return 1;
	}
	return 0;
}

//! drops item from inventory onto the floor
int
Drop_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if ( ((getTokenAmount(fmudstruct)==3) || (getTokenAmount(fmudstruct)==4)) && (!strcasecmp("drop", getToken(fmudstruct, 0))) )
	{
	/* drop copper coin(s)
	   drop 10 copper coin(s)
	*/
		if ((!strcasecmp("coin",getToken(fmudstruct, getTokenAmount(fmudstruct)-1))) ||
			(!strcasecmp("coins", getToken(fmudstruct, getTokenAmount(fmudstruct)-1))) )
		{
			if ((!strcasecmp("copper",getToken(fmudstruct, getTokenAmount(fmudstruct)-2))) ||
				(!strcasecmp("silver",getToken(fmudstruct, getTokenAmount(fmudstruct)-2))) ||
				(!strcasecmp("gold", getToken(fmudstruct, getTokenAmount(fmudstruct)-2))) )
			{
				DropMoney_Command(fmudstruct);
				return 1;
			}
		}
	}
	if ((getTokenAmount(fmudstruct) >= 2) && (!strcasecmp("drop", getToken(fmudstruct, 0)))) 
	{
		DropItem_Command(fmudstruct);
		return 1;
	}
	return 0;
}

//! buy an item from a bot (at a store)
int
Buy_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if (getTokenAmount(fmudstruct) < 2)
	{
		return 0;
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==9)) 
	{
		return BuyItem_Command(fmudstruct, "Bill");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==16)) 
	{
		return BuyItem_Command(fmudstruct, "Karcas");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==64)) 
	{
		return BuyItem_Command(fmudstruct, "Karina");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==73))
	{
		return BuyItem_Command(fmudstruct, "Hagen");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==85)) 
	{
		return BuyItem_Command(fmudstruct, "Karsten");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==87))
	{
		return BuyItem_Command(fmudstruct, "Kurst");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==161))
	{
		return BuyItem_Command(fmudstruct, "Karstare");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==228))
	{
		return BuyItem_Command(fmudstruct, "Vimrilad");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==2550))
	{
		return BuyItem_Command(fmudstruct, "Telios");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==2551))
	{
		return BuyItem_Command(fmudstruct, "Nolli");
	}
	if ((!strcasecmp("buy", getToken(fmudstruct, 0))) && (room==2551))
	{
		return BuyItem_Command(fmudstruct, "Nolli");
	}
return 0;
}

//! sell an item at a store
int
Sell_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if ((getTokenAmount(fmudstruct) >= 2) && (!strcasecmp("sell", getToken(fmudstruct, 0))) && (room==16)) 
	{
		SellItem_Command(fmudstruct, "Karcas");
		return 1;
	}
	return 0;
}

//! administration command
/*! different administration commands are possible, and are only possible to those people who have a godstatus of 1
in the database.
Possible commands are:
<DL>
<DT>admin shutdown<DD>shuts the game server down! dangerous!
<DT>admin readconfig<DD>rereads the config files. convenient if they have been changed and need to be reloaded
<DT>admin config<DD>shows current config settings (exclusing the database password, for security reasons)
<DT>admin stats<DD>shows the current statistics of the server
</DL>
*/
int
Admin_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	char *fcommand;
	int room;
	MYSQL_RES *res;
	MYSQL_ROW row;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	res = executeQuery(NULL, "select god from tmp_usertable where name='%x'", name);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			if (strcmp("1", row[0]))
			{
				mysql_free_result(res);
				return 0;
			}
			mysql_free_result(res);
		}
		else
		{
			mysql_free_result(res);
			return 0;
		}
	}
	else
	{
		return 0;
	}
	if (!strcasecmp(fcommand, "admin shutdown"))
	{
		send_printf(fmudstruct->socketfd, "<HTML>\n");
		send_printf(fmudstruct->socketfd, "<HEAD>\n");
		send_printf(fmudstruct->socketfd, "<TITLE>\n");
		send_printf(fmudstruct->socketfd, "Land of Karchan - Admin Shutdown\n");
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

		send_printf(fmudstruct->socketfd, "<H1>Admin Shutdown - Shutting down Game</H1>\n");
		send_printf(fmudstruct->socketfd, "Shutting down of game initiated. Please stand by...<P>");
		
		PrintForm(name, password, fmudstruct->frames, fmudstruct->socketfd);
		send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
		send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P>");
		setShutdown(1);
		return 1;
	}
	if (!strcasecmp(fcommand, "admin readconfig"))
	{
		send_printf(fmudstruct->socketfd, "<HTML>\n");
		send_printf(fmudstruct->socketfd, "<HEAD>\n");
		send_printf(fmudstruct->socketfd, "<TITLE>\n");
		send_printf(fmudstruct->socketfd, "Land of Karchan - Admin Readconfig\n");
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

		send_printf(fmudstruct->socketfd, "<H1>Admin Readconfig - Reading Config file</H1>\n");
		
		send_printf(fmudstruct->socketfd, "Rereading config files. Please use 'admin config' to view any new settings. "
		"Bear in mind that a change in database info or socket info requires a restart of the server. Please stand by...<P>");
		readConfigFiles("config.xml");
		PrintForm(name, password, fmudstruct->frames, fmudstruct->socketfd);
		send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
		send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P>");
		return 1;
	}
	if (!strcasecmp(fcommand, "admin config"))
	{
		send_printf(fmudstruct->socketfd, "<HTML>\n");
		send_printf(fmudstruct->socketfd, "<HEAD>\n");
		send_printf(fmudstruct->socketfd, "<TITLE>\n");
		send_printf(fmudstruct->socketfd, "Land of Karchan - Admin Config\n");
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

		send_printf(fmudstruct->socketfd, "<H1>Admin Config - Config file</H1>\n");
		
		send_printf(fmudstruct->socketfd, "Reading config files. Please stand by...<P>");
		writeConfig(fmudstruct->socketfd);
		PrintForm(name, password, fmudstruct->frames, fmudstruct->socketfd);
		send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
		send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P>");
		return 1;
	}
	if (!strcasecmp(fcommand, "admin stats"))
	{
		mudinfostruct mymudinfo;
		mymudinfo = getMudInfo();

		send_printf(fmudstruct->socketfd, "<HTML>\n");
		send_printf(fmudstruct->socketfd, "<HEAD>\n");
		send_printf(fmudstruct->socketfd, "<TITLE>\n");
		send_printf(fmudstruct->socketfd, "Land of Karchan - Admin Stats\n");
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

		send_printf(fmudstruct->socketfd, "<H1>Admin Stats - Displaying current usage statistics of game</H1>\n");
		send_printf(fmudstruct->socketfd, "Host: %s<BR>\nIp address: %s<BR>\nDomainname: %s<BR>\nProtocol version: %s<BR>\nMmud version: %s %s %s<P>\n", 
			mymudinfo.hostname, mymudinfo.hostip, mymudinfo.domainname, mymudinfo.protversion, mymudinfo.mmudversion,
			mymudinfo.mmudtime, mymudinfo.mmuddate);

		send_printf(fmudstruct->socketfd, "Mmud started on : %s<P>\n", asctime(gmtime(&mymudinfo.mmudstartuptime)));
		send_printf(fmudstruct->socketfd, "Total connections: %i<BR>\nTimeouts: %i<BR>\nCurrent connections: %i<BR>\nMax. current connections: %i<BR>\n",
			mymudinfo.number_of_connections, mymudinfo.number_of_timeouts, mymudinfo.number_of_current_connections,
			mymudinfo.maxnumber_of_current_connections);
	
		PrintForm(name, password, fmudstruct->frames, fmudstruct->socketfd);
		send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
		send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P>");
		return 1;
	}
	return 0;
}

//! give item to person
int
Give_Command(mudpersonstruct *fmudstruct)
{
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	if ( ((getTokenAmount(fmudstruct)==5) || (getTokenAmount(fmudstruct)==6)) && (!strcasecmp("give", getToken(fmudstruct, 0))) )
	{
	/* give copper coin(s)
	   give 10 copper coin(s)
	*/
		if ((!strcasecmp("coin",getToken(fmudstruct, getTokenAmount(fmudstruct)-3))) ||
			(!strcasecmp("coins", getToken(fmudstruct, getTokenAmount(fmudstruct)-3))) )
		{
			if ((!strcasecmp("copper",getToken(fmudstruct, getTokenAmount(fmudstruct)-4))) ||
				(!strcasecmp("silver",getToken(fmudstruct, getTokenAmount(fmudstruct)-4))) ||
				(!strcasecmp("gold", getToken(fmudstruct, getTokenAmount(fmudstruct)-4))) )
			{
				GiveMoney_Command(fmudstruct);
				return 1;
			}
		}
	}
	if ((getTokenAmount(fmudstruct) >= 4) && (!strcasecmp("give", getToken(fmudstruct, 0)))) 
	{
		int i;
		i = GiveItem_Command(fmudstruct);
		return i;
	}
	return 0;
}

//! ranger guild command
int
RangerGuild_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *name;
	char *password;
	char *fcommand;
	char guildstatus[10];
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	res = executeQuery(NULL, "select guildstatus from tmp_usertable where name='%x'", name);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		if (row == NULL)
		{
			mysql_free_result(res);
			return 0;
		}
		strcpy(guildstatus, row[0]);
	}
	else
	{
		return 0;
	}
	mysql_free_result(res);
	/* Guilds */
	if (!strcasecmp("rangers", guildstatus))
	{
		if (!strcasecmp("nature list", fcommand))
		{
			RangerList(name, password, room, fmudstruct->frames, fmudstruct->socketfd);
			return 1;
		}
		if ( (!strcasecmp("nature call", fcommand)) && (room==43) )
		{
			RangerEntryIn(name, password, room, fmudstruct->frames, fmudstruct->socketfd);
			return 1;
		}
		if ( (!strcasecmp("nature call", fcommand)) && (room==216) )
		{
			RangerEntryOut(name, password, room, fmudstruct->frames, fmudstruct->socketfd);
			return 1;
		}
		if ( (getTokenAmount(fmudstruct) > 2) && (!strcasecmp("nature", getToken(fmudstruct, 0))) && (!strcasecmp("talk", getToken(fmudstruct, 1))) )
		{
			RangerTalk(fmudstruct);
			return 1;
		}
	}
	return 0;
}

//! mif guild command
int
MifGuild_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *name;
	char *password;
	char *fcommand;
	char guildstatus[10];
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	res = executeQuery(NULL, "select guildstatus from tmp_usertable where name='%x'", name);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		if (row == NULL)
		{
			mysql_free_result(res);
			return 0;
		}
		strcpy(guildstatus, row[0]);
	}
	else
	{
		return 0;
	}
	mysql_free_result(res);
	if (!strcasecmp("mif", guildstatus))
	{
		if (!strcasecmp("magic list", fcommand))
		{
			MIFList(name, password, room, fmudstruct->frames, fmudstruct->socketfd);
			return 1;
		}
		if ( (!strcasecmp("magic wave", fcommand)) && (room==142) )
		{
			MIFEntryIn(name, password, room, fmudstruct->frames, fmudstruct->socketfd);
			return 1;
		}
		if ( (!strcasecmp("magic wave", fcommand)) && (room==143) )
		{
			MIFEntryOut(name, password, room, fmudstruct->frames, fmudstruct->socketfd);
			return 1;
		}
		if ( (getTokenAmount(fmudstruct) > 2) && (!strcasecmp("magic", getToken(fmudstruct, 0))) && (!strcasecmp("talk", getToken(fmudstruct, 1))) )
		{
			MIFTalk(fmudstruct);
			return 1;
		}
	}
	return 0;
}

//! initialise the gamefunctionindex, called at the beginning of mmserver.c
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
	return 1;
}

//! main command parsing and executing body
/*! executes commands issued in the mud as a regular player
\param socketfd integer containing the socket descriptor, used to retrieve the mudpersonstruct
containing the essential information regarding the current player. */
int
gameMain(int socketfd)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int room;
	int i, amount = 0;
	char *name;
	char *password;
	char	*temp;
	char *sqlstring;
	char	logname[100];
	char	*junk;
	char *command;
	char    **myTokens;
	struct tm datumtijd;
	time_t   datetime;
	mudpersonstruct *mymudstruct;
	int godstatus;		/* contains the godstatus of the character */
	char sexstatus[8];
	int sleepstatus;	/* contains the sleepstatus of the character */
	char guildstatus[10];		/* contains the guild a person belongsto */
	int punishment;	/* contains wether or not you are normal, are a frog, or are a rat. */

#ifdef DEBUG	
	printf("gameMain started!!!\n");
#endif
	mymudstruct = find_in_list(socketfd);
	command = mymudstruct->command;
	name = mymudstruct->name;
	password = mymudstruct->password;
	myTokens = mymudstruct->tokens;
//	umask(0000);

	time(&datetime);
	datumtijd = *(gmtime(&datetime));
	srandom(datetime);
	
	if (SearchBanList(mymudstruct->address, name)) 
	{
		BannedFromGame(name, mymudstruct->address, mymudstruct->socketfd);
		return 0;
	}

	if (!ExistUser(name)) {
		NotActive(name,password,1,mymudstruct->socketfd);
		return 0;
	}

//	openDatabase();
	sqlstring = composeSqlStatement("select name, lok, sleep, room, lastlogin, god, sex, vitals, maxvital, guild, punishment "
		"from tmp_usertable "
		"where name='%x' and lok<>''", name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL)
	{
		NotActive(name, password,2,mymudstruct->socketfd);
		return 0;
	}
	row = mysql_fetch_row(res);
	if (row==NULL)
	{
		NotActive(name, password,3, mymudstruct->socketfd);
		return 0;
	}
	
	strcpy(name, row[0]); /* copy name to name from database */
	mymudstruct->room = room = atoi(row[3]); /* copy roomnumber to room from database */
	
	sleepstatus=atoi(row[2]); /* copy sleep to sleepstatus from database */
	godstatus=atoi(row[5]); /* copy godstatus of player from database */
	strcpy(sexstatus,row[6]); /* sex status (male, female) */
	if (atoi(row[7])>atoi(row[8])) 
	{ 
		/* check vitals along with maxvital */
		mysql_free_result(res);
		Dead(name, password, room, mymudstruct->frames, mymudstruct->socketfd);
		return 0;
	}
	strcpy(guildstatus	, row[9]);
	punishment = atoi(row[10]);
	if (strcmp(row[1], password)) 
	{
			NotActive(name, password,4, mymudstruct->socketfd);
	}
	mysql_free_result(res);
	if (*name == '\0') 
	{ 
		NotActive(name, password,5, mymudstruct->socketfd);
	}
	if (godstatus==2) 
	{
		NotActive(name, password,6, mymudstruct->socketfd);
	}
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

//	'0000-01-01 00:00:00' - '9999-12-31 23:59:59'
	sqlstring = composeSqlStatement("update tmp_usertable set lastlogin=date_sub(NOW(), INTERVAL 2 HOUR), "
			"address='%x' where name='%x'",	mymudstruct->address, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);

#ifdef DEBUG
	printf("create memblock!!!\n");
#endif
	junk = strdup(command);
	if (mymudstruct->memblock != NULL)
	{
		free(mymudstruct->memblock);
	}
	mymudstruct->memblock = junk;
	while ((strlen(junk)>0) && (junk[strlen(junk)-1]==' ')) 
	{
		junk[strlen(junk)-1]=0;
	}
	
	if (*junk == '\0') 
	{
		WriteRoom(mymudstruct);
		return 1;
	}
	
#ifdef DEBUG
	printf("end of create memblock!!!\n");
#endif
	if ((strstr(command,"<applet")!=NULL) || (strstr(command,"<script")!=NULL)
	|| (strstr(command,"java-script")!=NULL) || (strstr(command,"CommandForm")!=NULL)) 
	{ 
		WriteSentenceIntoOwnLogFile(logname, "I am afraid, I do not understand that.<BR>\r\n");
		WriteRoom(mymudstruct);
		return 1;
	}

	if (sleepstatus==1) 
	{
		if (!strcasecmp(command, "awaken"))
		{
			Awaken2_Command(mymudstruct);
			return 1;
		}
		WriteSentenceIntoOwnLogFile(logname, "You can't do that. You are asleep, silly.<BR>\r\n");
		WriteRoom(mymudstruct);
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
		sqlstring = composeSqlStatement("update tmp_usertable set punishment=punishment-1 where name='%x'",
			name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		WriteRoom(mymudstruct);
		return 1;
		}
		if ((*command == '\0') ||
		(!strcasecmp(command, "look around")) ||
		(!strcasecmp(command, "look")) ||
		(!strcasecmp(command, "l"))) {
			WriteRoom(mymudstruct);
			return 1;
		}
		if ((!strcasecmp(command, "go west")) ||
		(!strcasecmp(command, "west")) ||
		(!strcasecmp(command, "w"))) {
				GoWest_Command(mymudstruct);
		}
		if ((!strcasecmp(command, "go east")) ||
		(!strcasecmp(command, "east")) ||
		(!strcasecmp(command, "e"))) {
			GoEast_Command(mymudstruct);
		}
		if ((!strcasecmp(command, "go north")) ||
		(!strcasecmp(command, "north")) ||
		(!strcasecmp(command, "n"))) {
			GoNorth_Command(mymudstruct);
		}
		if ((!strcasecmp(command, "go south")) ||
		(!strcasecmp(command, "south")) ||
		(!strcasecmp(command, "s"))) {
			GoSouth_Command(mymudstruct);
		}
	
		if ((!strcasecmp(command, "go down")) ||
		(!strcasecmp(command, "down"))) {
			GoDown_Command(mymudstruct);
		}
		if ((!strcasecmp(command, "go up")) ||
		(!strcasecmp(command, "up"))) {
			GoUp_Command(mymudstruct);
		}
		if (!strcasecmp(command, "quit")) 
		{
			Quit_Command(mymudstruct);
		}

		WriteSentenceIntoOwnLogFile(logname, "You cannot do that, you are a frog, remember?<BR>\r\n");
		WriteRoom(mymudstruct);
		return 1;
	}

#ifdef DEBUG
	printf("tokenizer started!!!\n");
#endif
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
			if (i>45) {
				amount = i - 1;
				i = -1;
			}	/* endif */
		}		/* endwhile */
	}			/* endif */
	mymudstruct->tokenamount = amount;

#ifdef DEBUG
	printf("parser started!!!\n");
#endif
	if (SearchForSpecialCommand(mymudstruct, name, password, command, room, mymudstruct->frames)==1)
	{
		return 1;
	}
#ifdef DEBUG
	printf("binary search started!!!\n");
#endif
	
	{
		/* binary search in index, if found call function */
		int i = (theNumberOfFunctions / 2) + (theNumberOfFunctions % 2);
		int pos = theNumberOfFunctions / 2;
		int equals = strcasecmp(gameCommands[pos], getToken(mymudstruct, 0));
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
				equals = strcasecmp(gameCommands[pos], getToken(mymudstruct, 0));
			}
		}
		if (!equals)
		{
			/* string has been found */
#ifdef DEBUG
			printf("command found (%s)\n", gameCommands[pos]);
#endif
			if (gameFunctionArray[pos](mymudstruct))
			{
				/* command executed successfully, kill this session */
				return 1;
			}
			else
			{
				/* do nothing, the game will in time find out that there is no appropriate response to the bogus command and will produce an error message. Darnit people, why do these damn comment lines sometimes have to be so long! And who turned off my word wrap anyway!!! */
			}
#ifdef DEBUG
			printf("command finished (%s)\n", gameCommands[pos]);
#endif
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
		WriteRoom(mymudstruct);
		return 1;
	}
	/* smile engagingly */
	if ( (getTokenAmount(mymudstruct)==2) && ((temp = get_pluralis(getToken(mymudstruct, 0))) != NULL) &&
		 (exist_adverb(getToken(mymudstruct, 1))) ) {
		WriteSentenceIntoOwnLogFile(logname, "You %s %s.<BR>\r\n", getToken(mymudstruct, 0), getToken(mymudstruct, 1));
		WriteMessage(name, room, "%s %s %s.<BR>\r\n", name, temp, getToken(mymudstruct, 1));
		WriteRoom(mymudstruct);
		return 1;
	}
	/* smile to bill */
	if ((getTokenAmount(mymudstruct) == 3) && ((temp = get_pluralis(getToken(mymudstruct, 0))) != NULL) && 
		(!strcasecmp(getToken(mymudstruct, 1), "to")) ) {
		if (WriteMessageTo(getToken(mymudstruct, 2), name, room, "%s %s to %s.<BR>\r\n", name, temp, getToken(mymudstruct, 2)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(mymudstruct, 2), name, room, "%s %s to you.<BR>\r\n", name, temp);
			WriteSentenceIntoOwnLogFile(logname, "You %s to %s.</BR>\r\n", getToken(mymudstruct, 0), getToken(mymudstruct, 2));
		}
		WriteRoom(mymudstruct);
		return 1;
	}
	/* smile(0) engagingly(1) to(2) Bill(3) */
	if ((getTokenAmount(mymudstruct) == 4) && ((temp = get_pluralis(getToken(mymudstruct, 0))) != NULL) && 
		(!strcasecmp(getToken(mymudstruct, 2), "to")) && (exist_adverb(getToken(mymudstruct, 1))) ) {
		if (WriteMessageTo(getToken(mymudstruct, 3), name, room, "%s %s %s to %s.<BR>\r\n", name, temp, getToken(mymudstruct, 1), getToken(mymudstruct, 3)) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(getToken(mymudstruct, 3), name, room, "%s %s %s to you.<BR>\r\n", name, temp, getToken(mymudstruct, 1));
			WriteSentenceIntoOwnLogFile(logname, "You %s %s to %s.</BR>\r\n", getToken(mymudstruct, 0), getToken(mymudstruct, 1), getToken(mymudstruct, 3));
		}
		WriteRoom(mymudstruct);
		return 1;
	}
	/* multiple person emotions */
	/* caress bill */
	if ((getTokenAmount(mymudstruct) == 2) && ((temp = get_pluralis2(getToken(mymudstruct, 0))) != NULL)) {
		char            temp1[50], temp2[50];
		sprintf(temp1, "%s %s you.<BR>\r\n", name, temp);
		sprintf(temp2, "%s %s %s.<BR>\r\n", name, temp, getToken(mymudstruct, 1));
		if (!WriteMessageTo(getToken(mymudstruct, 1), name, room, "%s %s %s.<BR>\r\n",
					    name, temp, getToken(mymudstruct, 1))) {
			WriteSentenceIntoOwnLogFile(logname, "That user doesn't exist.<BR>\r\n");
		} else {
			WriteSayTo(getToken(mymudstruct, 1), name, room,
				"%s %s you.<BR>\r\n", name, temp);
			WriteSentenceIntoOwnLogFile(logname, "You %s %s.<BR>\r\n", getToken(mymudstruct, 0), getToken(mymudstruct, 1));
		}
		WriteRoom(mymudstruct);
		return 1;
	}			/* end of multiple persons emotions */
	/* caress bill absentmindedly */
	if ((getTokenAmount(mymudstruct) == 3) && ((temp = get_pluralis2(getToken(mymudstruct, 0))) != NULL) &&
		(exist_adverb(getToken(mymudstruct, 2))) ) {
		char            temp1[50], temp2[50];
		sprintf(temp1, "%s %s you, %s.<BR>\r\n", name, temp, getToken(mymudstruct, 2));
		sprintf(temp2, "%s %s %s, %s.<BR>\r\n", name, temp, getToken(mymudstruct, 1), getToken(mymudstruct, 2));
		if (!WriteMessageTo(getToken(mymudstruct, 1), name, room, "%s %s %s, %s.<BR>\r\n",
					    name, temp, getToken(mymudstruct, 1), getToken(mymudstruct, 2))) {
			WriteSentenceIntoOwnLogFile(logname, "That user doesn't exist.<BR>\r\n");
		} else {
			WriteSayTo(getToken(mymudstruct, 1), name, room,
				"%s %s you, %s.<BR>\r\n", name, temp, getToken(mymudstruct, 2));
			WriteSentenceIntoOwnLogFile(logname, "You %s %s, %s.<BR>\r\n", getToken(mymudstruct, 0), getToken(mymudstruct, 1), getToken(mymudstruct, 2));
		}
		WriteRoom(mymudstruct);
		return 1;
	}			/* end of multiple persons emotions */

/* add SWTalk */		
	if (!strcasecmp("SW", guildstatus))
	{
		if ( (!strcasecmp("pow", getToken(mymudstruct, 0))) && (!strcasecmp("wow", getToken(mymudstruct, 1))) )
		{
			SWTalk(mymudstruct);
			return 1;
		}

	}		

/* add BKTalk */		
	if (!strcasecmp("BKIC", guildstatus))
	{
		if ( (!strcasecmp("chaos", getToken(mymudstruct, 0))) && (!strcasecmp("murmur", getToken(mymudstruct, 1))) )
		{
			BKTalk(mymudstruct);
			return 1;
		}

	}

/* add VampTalk */		
	if (!strcasecmp("Kindred", guildstatus))
	{
		if ( (!strcasecmp("misty", getToken(mymudstruct, 0))) && (!strcasecmp("whisper", getToken(mymudstruct, 1))) )
		{
			VampTalk(mymudstruct);
			return 1;
		}

	}

/* add KnightTalk */		
	if (!strcasecmp("Knights", guildstatus))
	{
		if ( (getTokenAmount(mymudstruct) > 2) && (!strcasecmp("knight", getToken(mymudstruct, 0))) && (!strcasecmp("talk", getToken(mymudstruct, 1))) )
		{
			KnightTalk(mymudstruct);
			return 1;
		}

	}

/* add CoDTalk */		
	if (!strcasecmp("CoD", guildstatus))
	{
		if ( (!strcasecmp("mogob", getToken(mymudstruct, 0))) && (!strcasecmp("burz", getToken(mymudstruct, 1))) )
		{
			CoDTalk(mymudstruct);
			return 1;
		}

	}

	/* End Guilds */

	WriteSentenceIntoOwnLogFile(logname, "I am afraid, I do not understand that.<BR>\r\n");
	WriteRoom(mymudstruct);
	return 1;
}
