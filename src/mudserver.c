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

char name[20];		/* contains the name derived from the forms */
char password[40];	/* contains the password derived from the forms */
int room;		/* contains the room where the character is located */
int godstatus;		/* contains the godstatus of the character */
char sexstatus[8];
int sleepstatus;	/* contains the sleepstatus of the character */
char guildstatus[10];		/* contains the guild a person belongsto */

MYSQL_RES *res;
MYSQL_ROW row;
char sqlstring[1024];

void 
InitVar()
{
	if (DebugOptionsOn) {
		printstr = (char *) malloc(500);
		troep = (char *) malloc(100);
		command = (char *) malloc(100);
		junk = (char *) malloc(100);
	} else {
		printstr = (char *) malloc(cgiContentLength + 500);
		troep = (char *) malloc(cgiContentLength);
		command = (char *) malloc(cgiContentLength);
		junk = (char *) malloc(cgiContentLength);
	}
	time(&datetime);
	datumtijd = *(gmtime(&datetime));
	srandom(datetime);
}				/* endproc */

void
GoDown_Command(char *name, char *password, int room)
{
	roomstruct*temproom;
	int i=0;
	char logname[100];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];

//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",USERHeader,name);

	temproom=GetRoomInfo(room);

	if (!temproom->down)  {
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>");
	} else {
		WriteMessage(name, room, "%s leaves down.<BR>\r\n", name);
		room = temproom->down;
		sprintf(temp, "update tmp_usertable set room=%i where name='%s'"
						, room, name);
		res=SendSQL2(temp, NULL);
		mysql_free_result(res);
		WriteMessage(name, room, "%s appears.<BR>\r\n", name);
	}
	free(temproom);
	WriteRoom(name, password, room, 0);
	KillGame();
} 				/* endproc */

void
GoUp_Command(char *name, char *password, int room)
{
	roomstruct*temproom;
	int i=0;
	char logname[100];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];

//	RoomTextProc(room);

	sprintf(logname, "%s%s.log",USERHeader,name);

	temproom=GetRoomInfo(room);

	if (!temproom->up)  {
		WriteSentenceIntoOwnLogFile(logname, "You can't go that way.<BR>");
	} else {
		WriteMessage(name, room, "%s leaves up.<BR>\r\n", name);
		room = temproom->up;
		sprintf(temp, "update tmp_usertable set room=%i where name='%s'"
						, room, name);
		res=SendSQL2(temp, NULL);
		mysql_free_result(res);
		WriteMessage(name, room, "%s appears.<BR>\r\n", name);
	}
	free(temproom);
	WriteRoom(name, password, room, 0);
	KillGame();
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

int 
cgiMain()
{
	int             oldroom;
	int             i;
	char           *temp;
	char            logname[100];

	umask(0000);
	InitVar();
	if (0) {
		printf("Command:");
		gets(command);
		printf("Name:");
		gets(name);
		printf("Password:");
		gets(password);
	}
	else 
	{
		cgiFormString("command", command, cgiContentLength - 2);
		if (command[0]==0) {strcpy(command,"l");}
		cgiFormString("name", name, 0);
		cgiFormString("password", password, 40);
	}
	cgiHeaderContentType("text/html");

	if (SearchBanList(cgiRemoteHost, name)) {BannedFromGame(name, cgiRemoteHost);}

	if (!ExistUser(name)) {
		NotActive();
	}

//	openDatabase();
	sprintf(sqlstring, "select name, password, sleep, room, lastlogin, god, sex, vitals, maxvital, guild from tmp_usertable where "
		"name='%s'", name);
	res=SendSQL2(sqlstring, NULL);
	if (res==NULL)
	{
		NotActive();
	}
	row = mysql_fetch_row(res);
	if (row==NULL)
	{
		NotActive();
	}
	
	strcpy(name, row[0]); /* copy name to name from database */
	room=atoi(row[3]); /* copy roomnumber to room from database */
	sleepstatus=atoi(row[2]); /* copy sleep to sleepstatus from database */
	godstatus=atoi(row[5]); /* copy godstatus of player from database */
	strcpy(sexstatus,row[6]); /* sex status (male, female) */
	if (atoi(row[7])>atoi(row[8])) { /* check vitals along with maxvital */
		Dead(name, password, room);
	}
	strcpy(guildstatus, row[9]);
	if (strcmp(row[1], password)) {
		NotActive();
	}
	mysql_free_result(res);
	if (DebugOptionsOn) {
		if (!strcmp(name, "Karn")) {
			fprintf(cgiOut, "Command: %s<BR>ActivePersonPos: %i<BR>Password: %s<BR>", command, password);
		}
	}
	if (*name == '\0') { NotActive();
	}
	if (godstatus==2) 
	{
		NotActive();
	}
	sprintf(logname, "%s%s.log", USERHeader, name);

	lowercase(troep, command);
	
//	'0000-01-01 00:00:00' - '9999-12-31 23:59:59'
	sprintf(sqlstring, "update tmp_usertable set lastlogin=date_sub(NOW(), INTERVAL 2 HOUR), "
			"address='%s' where name='%s'",	cgiRemoteHost, name);
	res=SendSQL2(sqlstring, NULL);
	
	mysql_free_result(res);

	strcpy(junk, troep);
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
	WriteSentenceIntoOwnLogFile("/home/karchan/mud/data/bigfile",
				     "%s: |%s|\n", name, command);


	if ((strstr(troep,"<applet")!=NULL) || (strstr(troep,"<script")!=NULL)) {
		WriteSentenceIntoOwnLogFile(logname, "I am afraid, I do not understand that.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
		}

	if (sleepstatus==1) 
	{
		if (!strcmp(troep, "awaken"))
		{
			Awaken_Command(name, password, room);
		}
		WriteSentenceIntoOwnLogFile(logname, "You can't do that. You are asleep, silly.<BR>\r\n");
		WriteRoom(name, password, room, 1);
		KillGame();
	}

	if (!strcmp(troep, "sleep"))
	{
		Sleep_Command(name, password, room);
	}
	
	if (!strcmp(troep, "awaken"))
	{
		WriteSentenceIntoOwnLogFile(logname, "You aren't asleep, silly.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	
	if (!strcmp(troep, "quit")) 
	{
		Quit_Command(name);
	}

	if (godstatus==1) {Root_Command(name, password, room);}
	if (godstatus==2) {Evil_Command(name, password, room);}

	if (!strcmp(troep, "help hint")) 
	{
		HelpHint_Command(name, password, room);
	}
	if (!strcmp(troep, "help")) 
	{
		ReadFile("/home/karchan/mud/help/help.txt");
		PrintForm(name, password);
		ReadFile(logname);
		KillGame();
	}
	if ((aantal >= 2) && (!strcmp(tokens[0],"help"))) 
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
		ReadFile(logname);
		KillGame();
	}

    if ((!strcmp(troep, "inventory")) || (!strcmp(troep, "i"))) 
    {
		WriteInventoryList(name, password);
		KillGame();
	}           /* Inventory_Command */
	if (!strcmp(troep, "clear")) {
	        char logname[100];
	        sprintf(logname, "%s%s.log", USERHeader, name);
		WriteRoom(name, password, room, 0);
		ClearLogFile(logname);
		WriteSentenceIntoOwnLogFile(logname, "You cleared your mind.<BR>\r\n");
		WriteMessage(name, room, "%s clears %s mind.<BR>\r\n", name, HeShe3(sexstatus));
		KillGame();
	}			/* Clear_Command */
	if (!strcmp(troep, "who")) {
		ListActivePlayers(name, password);
		WriteRoom(name, password, room, 0);
		KillGame();
	}			/* Who_Command */
	if (!strcmp("big talk", troep)) 
	{
		BigTalk_Command(name, password);
	}
	if (!strcmp(troep, "time")) 
	{
		Time_Command(name, password, room);
	}
	if (!strcmp(troep, "date")) 
	{
		Date_Command(name, password, room);
	}
	if ((!strcmp("look at sky", troep)) ||
	   (!strcmp("look at clouds", troep))) 
	{
		LookSky_Command(name, password);
	}
/*	if (!strcmp(troep, "introduce me")) {
		IntroduceMe_Command(logname);
	}*/
	if (!strcmp(troep, "bow")) {
		WriteSentenceIntoOwnLogFile(logname, "You bow gracefully.<BR>\r\n");
		WriteMessage(name, room, "%s bows gracefully.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if (aantal == 3 && (!strcmp(tokens[0], "bow")) && (!strcmp(tokens[1], "to"))) {
		if (WriteMessageTo(tokens[2], name, room, "%s bows gracefully to %s.<BR>\r\n", name, tokens[2]) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, "%s bows gracefully to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You bow gracefully to %s.<BR>\r\n", tokens[2]);
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if (!strcmp(troep, "eyebrow")) {
		WriteSentenceIntoOwnLogFile(logname, "You raise an eyebrow.<BR>\r\n");
		WriteMessage(name, room, "%s raises an eyebrow.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	/* smile */
	if ((temp = get_pluralis(troep)) != NULL) {
		WriteSentenceIntoOwnLogFile(logname, "You %s.<BR>\r\n", troep);
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
		(!strcmp(tokens[1], "to")) ) {
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
		(!strcmp(tokens[2], "to")) && (exist_adverb(tokens[1])) ) {
		if (WriteMessageTo(tokens[3], name, room, "%s %s %s to %s.<BR>\r\n", name, temp, tokens[1], tokens[3]) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[3], name, room, "%s %s %s to you.<BR>\r\n", name, temp, tokens[1]);
			WriteSentenceIntoOwnLogFile(logname, "You %s %s to %s.</BR>\r\n", tokens[0], tokens[1], tokens[3]);
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if (!strcmp(troep, "curtsey")) {
		WriteSentenceIntoOwnLogFile(logname, "You drop a curtsey.<BR>\r\n");
		WriteMessage(name, room, "%s drops a curtsey.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if (aantal == 3 && (!strcmp(tokens[0], "curtsey")) && (!strcmp(tokens[1], "to"))) {
		if (WriteMessageTo(tokens[2], name, room, "%s drops a curtsey to %s.<BR>\r\n", name, tokens[2]) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, "%s drops a curtsey to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You drop a curtsey to %s.</BR>\r\n", tokens[2]);
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if (!strcmp(troep, "flinch")) {
		WriteSentenceIntoOwnLogFile(logname, "You flinch.<BR>\r\n");
		WriteMessage(name, room, "%s flinches.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if (aantal == 3 && (!strcmp(tokens[0], "flinch")) && (!strcmp(tokens[1], "to"))) {
		if (WriteMessageTo(tokens[2], name, room, "%s flinches to %s.<BR>\r\n", name, tokens[2]) == 0) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSayTo(tokens[2], name, room, "%s flinches to you.<BR>\r\n", name);
			WriteSentenceIntoOwnLogFile(logname, "You flinch to %s.</BR>\r\n", tokens[2]);
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((*command == '\0') ||
	    (!strcmp(troep, "look around")) ||
	    (!strcmp(troep, "look")) ||
	    (!strcmp(troep, "l"))) {
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((!strcmp(troep, "go west")) ||
	    (!strcmp(troep, "west")) ||
	    (!strcmp(troep, "w"))) {
		GoWest_Command(name, password, room);
	}
	if ((!strcmp(troep, "go east")) ||
	    (!strcmp(troep, "east")) ||
	    (!strcmp(troep, "e"))) {
		GoEast_Command(name, password, room);
	}
	if ((!strcmp(troep, "go north")) ||
	    (!strcmp(troep, "north")) ||
	    (!strcmp(troep, "n"))) {
		GoNorth_Command(name, password, room);
	}
	if ((!strcmp(troep, "go south")) ||
	    (!strcmp(troep, "south")) ||
	    (!strcmp(troep, "s"))) {
		GoSouth_Command(name, password, room);
	}

	if ((!strcmp(troep, "go down")) ||
	    (!strcmp(troep, "down"))) {
		GoDown_Command(name, password, room);
	}
	if ((!strcmp(troep, "go up")) ||
	    (!strcmp(troep, "up"))) {
		GoUp_Command(name, password, room);
	}
	if ((aantal >1) && (!strcmp(tokens[0], "title"))) {
		ChangeTitle_Command(name, password, room);
	}
	if ((aantal > 1) && (!strcmp(tokens[0], "look"))) {
		Look_Command(name, password, room);
	}
	if ((aantal > 1) && (!strcmp(tokens[0], "me"))) {
		WriteMessage(name, room, "%s %s<BR>\r\n", name, command + 3);
		WriteSentenceIntoOwnLogFile(logname, "%s %s<BR>\r\n", name, command + 3);
		WriteRoom(name, password, room, 0);
		KillGame();
	} /* endme */
	if ((aantal > 1) && (!strcmp(tokens[0], "public")) && (room == 3)) {
		FILE           *fp;
		if (strstr(command, "<") == NULL) {
			fp = fopen("/home/karchan/mud/data/board.txt", "a");
			fprintf(fp, "<HR noshade>From: <B>%s</B><BR>"
				"Time: <B>%i:%i:%i</B><BR>"
				"Date: <B>%i-%i-%i</B><BR>"
				"<HR>%s<HR noshade><BR>\r\n",
				name, datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec,
				datumtijd.tm_mday, datumtijd.tm_mon + 1, datumtijd.tm_year+1900, command + 7);
			fclose(fp);
			WriteSentenceIntoOwnLogFile(logname, "Public Mail Message Sent.<BR>\r\n");
		} else {
			WriteSentenceIntoOwnLogFile(logname, "Error: Forbidden HTML codes used.<BR>\r\n");
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((room == 3) && (!strcmp(troep, "read public"))) {
		ReadFile("/home/karchan/mud/data/board.txt");
		PrintForm(name, password);
		ReadFile(logname);
		KillGame();
	}
	if ((aantal > 3) && (!strcmp("tell", tokens[0])) && (!strcmp("to", tokens[1]))) {
		if (!WriteLinkTo(tokens[2], name, "<B>%s tells you </B>: %s<BR>\r\n",
					    name, command + (tokens[3] - tokens[0]))) {
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		} else {
			WriteSentenceIntoOwnLogFile(logname, "<B>You tell %s</B> : %s<BR>\r\n",
			      tokens[2], command + (tokens[3] - tokens[0]));
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}
//	if ((aantal > 1) && (!strcmp("me", tokens[0]))) {
//		WriteSentenceIntoOwnLogFile(logname, "%s %s<BR>\r\n", name, command + 3);
//		WriteRoom(name, password, room, 0);
//		KillGame();
//	}			/* endme */
	if ((!strcmp("say", tokens[0])) && (aantal > 1)) {
		if ((!strcmp("to", tokens[1])) && (aantal > 3)) {
			if (!WriteMessageTo(tokens[2], name, room, "%s says [to %s] : %s<BR>\r\n",
					    name, tokens[2], command + (tokens[3] - tokens[0]))) {
				WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
			}
			 /* person not found */ 
			else {
				WriteSayTo(tokens[2], name, room, 
					   "<B>%s says [to you]</B> : %s<BR>\r\n", name, command + (tokens[3] - tokens[0]));
				WriteSentenceIntoOwnLogFile(logname, "<B>You say [to %s]</B> : %s<BR>\r\n", tokens[2], command + (tokens[3] - tokens[0]));
				ReadBill(tokens[2], troep + (tokens[3] - tokens[0]), name, room);
			}
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		WriteSentenceIntoOwnLogFile(logname, "<B>You say </B>: %s<BR>\r\n", command + 4);
		WriteMessage(name, room, "%s says : %s<BR>\r\n", name, command + 4);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((aantal > 1) && (!strcmp("shout", tokens[0]))) {
		if ((!strcmp("to", tokens[1])) && (aantal > 3)) {
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
			KillGame();
		}
		WriteSentenceIntoOwnLogFile(logname, "<B>You shout</B> : %s<BR>\r\n", command + 6);
		WriteMessage(name, room, "%s shouts : %s<BR>\r\n", name, command + 6);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((aantal > 1) && (!strcmp("ask", tokens[0]))) {
		if ((!strcmp("to", tokens[1])) && (aantal > 3)) {
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
			KillGame();
		}
		WriteSentenceIntoOwnLogFile(logname, "<B>You ask</B> : %s<BR>\r\n", command + 4);
		WriteMessage(name, room, "%s asks : %s<BR>\r\n", name, command + 4);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((aantal > 1) && (!strcmp("whisper", tokens[0]))) {
		if ((!strcmp("to", tokens[1])) && (aantal > 3)) {
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
			KillGame();
		}
		WriteSentenceIntoOwnLogFile(logname, "<B>You whisper </B>: %s<BR>\r\n", command + 8);
		WriteMessage(name, room, "%s whispers : %s<BR>\r\n", name, command + 8);
		WriteRoom(name, password, room, 0);
		KillGame();
	}

	if ((aantal == 2) && (!strcmp("fight", tokens[0])))
	{
		sprintf(sqlstring, "select fightable from tmp_usertable where "
			"name='%s'", name);
		res=SendSQL2(sqlstring, NULL);
		row = mysql_fetch_row(res);
		if (atoi(row[0])!=1)
		{
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "Pkill is off, so you cannot fight.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		mysql_free_result(res);
		sprintf(sqlstring, "select name from tmp_usertable where "
			"name<>'%s' and "
			"name='%s' and "
			"fightable=1 and "
			"god<>2 and "
			"room=%i"
			, name, tokens[1], room);
		res=SendSQL2(sqlstring, NULL);
		row = mysql_fetch_row(res);
		if (row!=NULL)
		{
			WriteSentenceIntoOwnLogFile(logname, "You start to fight against %s.<BR>\r\n", row[0]);
			WriteMessageTo(tokens[1], name, room, "%s starts fighting against %s.<BR>\r\n",
				    name, row[0]);
			WriteSayTo(row[0], name, room, 
				   "%s starts fighting against you.<BR>\r\n", name);
			mysql_free_result(res);
			sprintf(sqlstring, "update tmp_usertable set fightingwho='%s' where name='%s'",
			tokens[1], name);
			res=SendSQL2(sqlstring, NULL);
			mysql_free_result(res);
			sprintf(sqlstring, "update tmp_usertable set fightingwho='%s' where name='%s'",
			name, tokens[1]);
			res=SendSQL2(sqlstring, NULL);
		}
		else
		{
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		}
		mysql_free_result(res);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if (!strcmp("stop fighting", troep))
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
			KillGame();
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
		KillGame();
	}
	if ((aantal == 2) && (!strcmp("pkill", tokens[0]))) 
	{
		if (!strcmp("on", tokens[1])) 
		{
			sprintf(sqlstring, "update tmp_usertable set fightable=1 where "
				"name='%s'", name);
			res=SendSQL2(sqlstring, NULL);
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "Pkill is now on.<BR>\r\n");
		}
		else
		{
			sprintf(sqlstring, "update tmp_usertable set fightable=0 where "
				"name='%s'", name);
			res=SendSQL2(sqlstring, NULL);
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "Pkill is now off.<BR>\r\n");
		}
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((aantal >= 1) && (!strcmp("whimpy", tokens[0]))) {
		char number[10];
		if (aantal == 1) {
			strcpy(number,"0");
		} else {
			if (!strcmp("help", troep + (tokens[1] - tokens[0]))) {
				WriteSentenceIntoOwnLogFile(logname, 
				"Syntax: <B>whimpy &lt;string&gt;</B><UL><LI>feeling well"
				"<LI>feeling fine<LI>feeling quite nice<LI>slightly hurt"
				"<LI>hurt<LI>quite hurt<LI>extremely hurt<LI>terribly hurt"
				"<LI>feeling bad<LI>feeling very bad<LI>at death's door</UL>\r\n");
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			if (!strcmp("feeling well", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"10");
				//x.whimpy = 10;
			}
			if (!strcmp("feeling fine", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"20");
				//x.whimpy = 20;
			}
			if (!strcmp("feeling quite nice", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"30");
				//x.whimpy = 30;
			}
			if (!strcmp("slightly hurt", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"40");
				//x.whimpy = 40;
			}
			if (!strcmp("hurt", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"50");
				//x.whimpy = 50;
			}
			if (!strcmp("quite hurt", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"60");
				//x.whimpy = 60;
			}
			if (!strcmp("extremely hurt", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"70");
				//x.whimpy = 70;
			}
			if (!strcmp("terribly hurt", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"80");
				//x.whimpy = 80;
			}
			if (!strcmp("feeling bad", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"90");
				//x.whimpy = 90;
			}
			if (!strcmp("feeling very bad", troep + (tokens[1] - tokens[0]))) {
				strcpy(number,"100");
				//x.whimpy = 100;
			}
			if (!strcmp("at death's door", troep + (tokens[1] - tokens[0]))) {
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
		KillGame();
	}			/* endwhimpy */
	if ((aantal == 1) && (!strcmp("stats", tokens[0]))) {
		Stats_Command(name, password);
	}
	if ( ((aantal==3) || (aantal==4)) && (!strcmp("get", tokens[0])) )
	{
	/* get copper coin(s)
	   get 10 copper coin(s)
	*/
		if ((!strcmp("coin",tokens[aantal-1])) ||
			(!strcmp("coins", tokens[aantal-1])) )
		{
			if ((!strcmp("copper",tokens[aantal-2])) ||
				(!strcmp("silver",tokens[aantal-2])) ||
				(!strcmp("gold", tokens[aantal-2])) )
			{
				GetMoney_Command(name, password, room);
			}
		}
	}
	if ((aantal >= 2) && (!strcmp("get", tokens[0]))) 
	{
		Get_Command(name, password, room);
	}
	if ( ((aantal==3) || (aantal==4)) && (!strcmp("drop", tokens[0])) )
	{
	/* drop copper coin(s)
	   drop 10 copper coin(s)
	*/
		if ((!strcmp("coin",tokens[aantal-1])) ||
			(!strcmp("coins", tokens[aantal-1])) )
		{
			if ((!strcmp("copper",tokens[aantal-2])) ||
				(!strcmp("silver",tokens[aantal-2])) ||
				(!strcmp("gold", tokens[aantal-2])) )
			{
				DropMoney_Command(name, password, room);
			}
		}
	}
	if ((aantal >= 2) && (!strcmp("drop", tokens[0]))) 
	{
		Drop_Command(name, password, room);
	}
	if ((aantal >= 2) && (!strcmp("eat", tokens[0]))) 
	{
		Eat_Command(name, password, room);
	}
	if ((aantal >= 2) && (!strcmp("drink", tokens[0]))) 
	{
		Drink_Command(name, password, room);
	}
	if ((aantal >= 4) && (!strcmp("wear", tokens[0]))) 
	{
		Wear_Command(name, password, room);
	}
	if ((aantal >= 2) && (!strcmp("remove", tokens[0]))) 
	{
		Unwear_Command(name, password, room);
	}
	if ((aantal >= 2) && (!strcmp("wield", tokens[0]))) 
	{
		Wield_Command(name, password, room);
	}
	if ((aantal >= 2) && (!strcmp("unwield", tokens[0]))) 
	{
		Unwield_Command(name, password, room);
	}
	if ((aantal >= 2) && (!strcmp("buy", tokens[0])) && (room==9)) 
	{
		Buy_Command(name, password, room, "Bill");
	}
	if ((aantal >= 2) && (!strcmp("buy", tokens[0])) && (room==16)) 
	{
		Buy_Command(name, password, room, "Karcas");
	}
	if ((aantal >= 2) && (!strcmp("buy", tokens[0])) && (room==64)) 
	{
		Buy_Command(name, password, room, "Karina");
	}
	if ((aantal >= 2) && (!strcmp("buy", tokens[0])) && (room==85)) 
	{
		Buy_Command(name, password, room, "Karsten");
	}
	if ((aantal >= 2) && (!strcmp("sell", tokens[0])) && (room==16)) 
	{
		Sell_Command(name, password, room, "Karcas");
	}
	if ((aantal >= 2) && (!strcmp("search", tokens[0]))) 
	{
		Search_Command(name, password, room);
	}
	if ( ((aantal==5) || (aantal==6)) && (!strcmp("give", tokens[0])) )
	{
	/* give copper coin(s)
	   give 10 copper coin(s)
	*/
		if ((!strcmp("coin",tokens[aantal-3])) ||
			(!strcmp("coins", tokens[aantal-3])) )
		{
			if ((!strcmp("copper",tokens[aantal-4])) ||
				(!strcmp("silver",tokens[aantal-4])) ||
				(!strcmp("gold", tokens[aantal-4])) )
			{
				GiveMoney_Command(name, password, room);
			}
		}
	}
	if ((aantal >= 4) && (!strcmp("give", tokens[0]))) 
	{
		Give_Command(name, password, room);
	}
	if ((aantal >= 2) && (!strcmp("read", tokens[0]))) 
	{
		Read_Command(name, password, room);
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
	/* Guilds */
	if (!strcmp("mif", guildstatus))
	{
		if (!strcmp("magic list", troep))
		{
			MIFList(name, password, room);
			KillGame();
		}
		if ( (!strcmp("magic wave", troep)) && (room==142) )
		{
			MIFEntryIn(name, password, room);
			KillGame();
		}
		if ( (!strcmp("magic wave", troep)) && (room==143) )
		{
			MIFEntryOut(name, password, room);
			KillGame();
		}
		if ( (!strcmp("magic", tokens[0])) && (!strcmp("talk", tokens[1])) )
		{
			MIFTalk(name, password, room);
			KillGame();
		}
	}
	/* End Guilds */
	if (!strcmp("mail", troep))
	{
		MailFormDumpOnScreen(name, password);
	}
	if (!strcmp("sendmail", tokens[0])) {
		char mailto[100], *mailbody, mailheader[100];
		
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
		KillGame();
	}			/* endofmailmessage */
	if ((!strcmp("readmail", tokens[0])) && (aantal == 2)) {
		ReadMail(name, password, room, atoi(tokens[1]), 0);
		KillGame();
	}			/* endofreadmailmessage */
	if ((!strcmp("deletemail", tokens[0])) && (aantal == 2)) {
		ReadMail(name, password, room, atoi(tokens[1]), 2);
		KillGame();
	}			/* endofreadmailmessage */
	if (!strcmp("listmail", tokens[0])) {
		ListMail(name, password, logname);
		KillGame();
	}			/* endoflistmail */
	WriteSentenceIntoOwnLogFile(logname, "I am afraid, I do not understand that.<BR>\r\n");
	WriteRoom(name, password, room, 0);
}
