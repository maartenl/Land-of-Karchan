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
#include <stdlib.h>
#include <string.h>
#include "typedefs.h"
#include "userlib.h"
#include "mud-lib.h"
#include "mudlogon.h"
#include "mudmain.h"

/*! \file mudlogon.c
	\brief  part of the server that takes care of entering characters in the game */

//extern char secretpassword[40];

//! dump page to user explaining that his/her name or password are illegal according to validation rules
/*! validation rules are explained on the page */
int
StrangeName(char *name, char *password, char *address, int socketfd)
{
	int i=0, j=0;
	while (i<strlen(password)) 
	{
		/*' and " forbidden*/
		if ( (password[i]=='\'') || (password[i]=='"') ) {j=3;}
		i++;
	} /*endwhile*/
	i=0;
	while (i<strlen(name)) 
	{
		/*65..90 = A..Z, 97..122 = a..z, 95 = _, 126 = ~*/
		if (name[i]<'A') {j=2;}
		if (name[i]>'z' && name[i]!='~') {j=2;}
		if ((name[i]>'Z') && (name[i]<'a') && (name[i]!='_')) {j=2;}
		if (name[i]==32) {j=1;}
		i++;
	} /*endwhile*/
	if (strlen(name)<3) {j=4;}
	if (strlen(password)<5) {j=5;}
	if (j)
	{
		char *error1 = NULL, *error2 = NULL;
		time_t tijd;
		struct tm datum;
		switch (j)
		{
			case (1) :
			{
				error1="Multiple Names";
				error2="This game does not accept a multitude of names. Please"
				" fill out just one (<I>1</I>) name in the Name-field.";
				break;
			}
			case (2) :
			{
				error1="Funny Characters in Name";
				error2="This game does not accept strange characters in a name."
				" See for characters that are allowed, below.";
				break;
			}
			case (3) :
			{
				error1="Funny Characters in Password";
				error2="Do not fill out a <B>\"</B> or a <B>'</B> in a password."
				" Strange characters are accepted in a password, except these two."
				" See for characters that are allowed, below.";
				break;
			}
			case (4) :
			{
				error1="Name too short";
				error2="The name you filled out has to be a minimum of three"
				" characters.";
				break;
			}
			case (5) :
			{
				error1="Password too short";
				error2="The password you filled out has to be a minimum of five characters.";
				break;
			}
		}
		send_printf(socketfd, "<HTML><HEAD><TITLE>Error - %s</TITLE></HEAD>\n\n", error1);
		send_printf(socketfd, "<BODY>\n");
		send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>%s</H1><HR>\n", error1);
		send_printf(socketfd,"%s<P>\r\n", error2);

		send_printf(socketfd,"The following rules need to be followed when filling out a name and password:<P>\r\n");
		send_printf(socketfd,"<UL><LI>the following characters are valid in a name: {A..Z, a..z, _, ~}");
		send_printf(socketfd,"<LI>all characters are valid in a password except {\"} and {'}");
		send_printf(socketfd,"<LI>at least 3 characters are required for a name");
		send_printf(socketfd,"<LI>at least 5 characters are required for a password");
		send_printf(socketfd,"</UL><P>These are the rules.<P>\r\n");
		send_printf(socketfd,"<A HREF=\"http://%s/karchan/enter.html\">Click here to retry</A></body>\n", getParam(MM_SERVERNAME));
		send_printf(socketfd, "</body>\n");
		send_printf(socketfd, "</HTML>\n");
		time(&tijd);
		datum=*(gmtime(&tijd));
		WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i Invalid name by %s (%s) <BR>\n",datum.tm_hour,
		datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
		return 0;
	}
	return 1;
} /*endproc*/

 
//! dump page showing that a player is attempting to log on, while already playing as a different character
/*! this is not allowed, a person may only be logged onto the game as one character as all times. */
void MultiPlayerDetected(char *name, char *address, int socketfd)
{
	time_t tijd;
	struct tm datum;
	send_printf(socketfd, "<HTML><HEAD><TITLE>Multiple Player Detected</TITLE></HEAD>\n\n");
	send_printf(socketfd, "<BODY>\n");
	send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Multiple Player Detected</H1><HR>\n");
	send_printf(socketfd, "You are already playing this mud under another character's");
	send_printf(socketfd, " name. You are only allowed to log on once.<P>\n");
	send_printf(socketfd, "</body>\n");
	send_printf(socketfd, "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i Multiplayer detected by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
}

//! check to see if mud is online or not
/*! checking is done by verifying that a file exists, to be precise the file MM_MUDOFFLINEFILE. If it does exist,
the file is dumped to the user (the file should containing a valid html page containing the reason why the game is temporarily offline 
and no more processing takes place. 
\returns int containing 1 if everything is okay, 0 in the case that the file exists. */
int
CheckForOfflineMud(int socketfd)
{
	FILE *fp;

	fp = fopen(getParam(MM_MUDOFFLINEFILE), "r");
	if (fp==NULL)
	{
		// Everything fine...
	} 
	else
	{
		fclose(fp);
		ReadFile(getParam(MM_MUDOFFLINEFILE), socketfd);
		return 0;
	}
	return 1;
}

//! dump a alread-active page towards the player.
void AlreadyActive(char *name, char *password, char *cookie, char *address, int frames, int socketfd)
{
	time_t tijd;
	struct tm datum;
	send_printf(socketfd, "Content-type: text/html\r\n");
	send_printf(socketfd, "Set-cookie: Karchan=%s;\r\n\r\n", cookie);


	if (!frames)
	{
		send_printf(socketfd, "<HTML><HEAD><TITLE>Error</TITLE></HEAD>\n\n");
		send_printf(socketfd, "<BODY>\n");
		send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Already Active</H1><HR>\n");
		send_printf(socketfd, "You tried to start a session which is already in progress. You can't play \n");
		send_printf(socketfd, "two sessions at the same time! Please check below to try again. In case you \n");
		send_printf(socketfd, "accidently turned of your computerterminal or Netscape or Lynx without first \n");
		send_printf(socketfd, "having typed <B>QUIT</B> while you were in the MUD, you can reenter the game \n");
		send_printf(socketfd, "by using the second link. You have to sit at the same computer as you did \n");
		send_printf(socketfd, "when you logged in.<P>\n");
		send_printf(socketfd, "<A HREF=\"http://%s/karchan/enter.html\">Click here to\n", getParam(MM_SERVERNAME));
		send_printf(socketfd, "retry</A><P>\n");
		
		send_printf(socketfd,"Do you wish to enter into the active character?<BR><UL><LI>");
		send_printf(socketfd, "<A HREF=\"%s?command=me+entered+the+game+again...&name=%s&password=%s&frames=%i\">Yes</A>",
			 getParam(MM_MUDCGI), name, password, frames+1);
		send_printf(socketfd,"<LI><A HREF=\"/karchan/index.html\">No</A></UL>");
		send_printf(socketfd,"<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
		send_printf(socketfd,"<DIV ALIGN=left><P>");
		send_printf(socketfd, "</body>\n");
		send_printf(socketfd, "</HTML>\n");
	}
	else
	{
		if (frames==1)
		{
			send_printf(socketfd, "<HTML><HEAD><TITLE>Land of Karchan - %s</TITLE></HEAD>\r\n", name);
			send_printf(socketfd, "<FRAMESET ROWS=\"*,50\">\r\n");
			send_printf(socketfd, "	<FRAMESET COLS=\"*,180\">\r\n");
			send_printf(socketfd, "		<FRAME SRC=\"/karchan/already_active.html\" NAME=\"main\" border=0>\r\n");
			send_printf(socketfd, "		<FRAME SRC=%s?name=%s&password=%s NAME=\"leftframe\" scrolling=\"no\" border=0>\r\n", getParam(MM_LEFTFRAMECGI), name, password);
			send_printf(socketfd, "	</FRAMESET>\r\n");
			send_printf(socketfd, "	<FRAME SRC=%s?name=%s&password=%s NAME=\"logon\" scrolling=\"no\" border=0>\r\n", getParam(MM_LOGONFRAMECGI), name, password);
			send_printf(socketfd, "</FRAMESET>\r\n");
			send_printf(socketfd, "</HTML>\r\n");
		} else
		{
			send_printf(socketfd, "<HTML><HEAD><TITLE>Land of Karchan - %s</TITLE></HEAD>\r\n", name);
			send_printf(socketfd, "<FRAMESET ROWS=\"*,50,0,0\">\r\n");
			send_printf(socketfd, "	<FRAMESET COLS=\"*,180\">\r\n");
			send_printf(socketfd, "		<FRAMESET ROWS=\"60%,40%\">\r\n");
			send_printf(socketfd, "		<FRAME SRC=\"/karchan/already_active.html\" NAME=\"statusFrame\" border=0>\r\n");
			send_printf(socketfd, "		<FRAME SRC=http://%s/karchan/empty.html NAME=\"logFrame\">\r\n", getParam(MM_SERVERNAME));
			send_printf(socketfd, "		</FRAMESET>\r\n");
			send_printf(socketfd, "	<FRAME SRC=http://%s%snph-leftframe.cgi?name=%s&password=%s NAME=\"leftFrame\" scrolling=\"no\" border=0>\r\n", getParam(MM_SERVERNAME), getParam(MM_CGINAME), name, password);
			send_printf(socketfd, "	</FRAMESET>\r\n\r\n");
			send_printf(socketfd, "	<FRAME SRC=http://%s%snph-logonframe.cgi?name=%s&password=%s NAME=\"commandFrame\" scrolling=\"no\" border=0>\r\n", getParam(MM_SERVERNAME), getParam(MM_CGINAME), name, password);
			send_printf(socketfd, "	<FRAME SRC=http://%s%snph-javascriptframe.cgi?name=%s&password=%s NAME=\"javascriptFrame\">\r\n", getParam(MM_SERVERNAME), getParam(MM_CGINAME), name, password);
			send_printf(socketfd, "	<FRAME SRC=http://%s/karchan/empty.html NAME=\"duhFrame\">\r\n", getParam(MM_SERVERNAME));
			send_printf(socketfd, "</FRAMESET>\r\n");
			send_printf(socketfd, "</HTML>\r\n");
		}
	}


	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i Already Active Fault by %s (%s)<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name,address);
}


//! dump a wrong-password page to the user.
void WrongPasswd(char *name, char *address, char *error, int socketfd)
{
	time_t tijd;
	struct tm datum;
	send_printf(socketfd,"<html><head><Title>Error</Title></head>\n");
	send_printf(socketfd,"<body>\n");
	send_printf(socketfd,"<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Wrong Password</H1><HR>\n");
	send_printf(socketfd,"You filled out the wrong password for that particular name! \n");
	send_printf(socketfd,"Please retry by clicking at the link below:<P>\n");
	send_printf(socketfd,"<A HREF=\"http://%s/karchan/enter.html\">Click here to retry</A></body>\n", getParam(MM_SERVERNAME));
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i %s by %s (%s)<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,error,name,address);
}

//! write error into the audit trail containing what went wrong, used by most methods that dump a error page to the user.
void WriteError(char *name, char *address, char *error)
{
	time_t tijd;
	struct tm datum;
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i %s by %s (%s)<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,error,name,address);
}

//! too many users have attempted to log onto the game, dump error page to user this is for future use right now,
void ToManyUsers(int socketfd) 
{
	send_printf(socketfd,"<head><Title>Error</Title></head>");
	send_printf(socketfd,"<body>");
	send_printf(socketfd,"<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>To many users</H1><HR>");
	send_printf(socketfd,"I am sorry, but this game is currently out of space to provide for another");
	send_printf(socketfd," new character. Please try again tomorrow or so when I make some more room.");
	send_printf(socketfd," Thank you and sorry for the inconvenience. Please check below to try again,");
	send_printf(socketfd," although that will probably not function immediately.<P> ");
	send_printf(socketfd,"<A HREF=\"/karchan/enter.html\">Click here to retry</A></body>\n", getParam(MM_SERVERNAME));
}
	
//! error page for the user, user attempted to enter a name for a player containing spaces.
void ToManyNames(char *name, char *address, int socketfd) 
{
	time_t tijd;
	struct tm datum;
	send_printf(socketfd,"<head><Title>Error</Title></head>\n");
	send_printf(socketfd,"<body>\n");
	send_printf(socketfd,"<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>To many names</H1><HR>\n");
	send_printf(socketfd,"I am sorry, but this game will not accept a multitude of names which you \n");
	send_printf(socketfd,"have filled out in the Name-box of the fillout-form. Please do not type more \n");
	send_printf(socketfd,"than one name. In the next fillout form you will be asked to type a title, \n");
	send_printf(socketfd,"there you can type as much as you want.<P>"
	               "It is also illegal to use less than 3 characters for a name and "
	               "less then 5 characters in your password.<P>\n");
	send_printf(socketfd,"<A HREF=\"http://%s/karchan/enter.html\">Click here to retry</A></body>\n", getParam(MM_SERVERNAME));
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i Too many names Fault by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name,address);
}

//! show the user the registration page for creating a new player
/*! usually called when the name entered on the logon form does not exist yet. */
void NewPlayer(char *fname, char *address, char *fpassword, int frames, int socketfd)
{
	time_t tijd;
	struct tm datum;
	char dood[100];
	sprintf(dood, "%snewchar.html", getParam(MM_HTMLHEADER));
	ReadFile(dood, socketfd);
	send_printf(socketfd,"<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n",fname);
	send_printf(socketfd,"<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n",fpassword);
	send_printf(socketfd,"<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"%i\">\n",frames+1);
	send_printf(socketfd,"<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n");
	send_printf(socketfd,"<INPUT TYPE=\"reset\" VALUE=\"Clear\">\n");
	send_printf(socketfd,"</FORM></BODY></HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i New User Signup : %s (%s)\n<BR>",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,fname, address);
}

//! start the game appropriately
void MakeStart(char *name, char *password, char *cookie, char *address, int frames, int socketfd)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i  %s (%s) entered the game<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);

	sprintf(printstr,"%s%s.log", getParam(MM_USERHEADER), name);
	res=sendQuery("SELECT message FROM logonmessage WHERE id=0", NULL);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			WriteSentenceIntoOwnLogFile(printstr, row[0]);
		}
		mysql_free_result(res);
	}
	
	temp = composeSqlStatement("SELECT count(*) FROM tmp_mailtable"
		" WHERE toname='%x' and newmail=1", name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	                        
	row = mysql_fetch_row(res);
	if (*row[0]=='0') {WriteSentenceIntoOwnLogFile(printstr, "You have no new MudMail...<P>\r\n");}
		else {WriteSentenceIntoOwnLogFile(printstr, "You have new MudMail!<P>\r\n");}
	mysql_free_result(res);

	if (!frames)
	{
			mudpersonstruct *mystruct = find_in_list(socketfd);
			send_printf(socketfd, "Content-type: text/html\r\n");
			send_printf(socketfd, "Set-cookie: Karchan=%s;\r\n\r\n", password);
			strcpy(mystruct->name, name);
			strcpy(mystruct->password, password);
			strcpy(mystruct->cookie, cookie);
			strcpy(mystruct->address, address);
			mystruct->frames = frames;
			if (mystruct->command != NULL) 
			{
				free(mystruct->command);
			}
			mystruct->command = strdup("me has entered the game...<BR>\r\n");
			gameMain(socketfd);
	}
	else
	{
		if (frames==1)
		{
			send_printf(socketfd, "Content-type: text/html\r\n");
			send_printf(socketfd, "Set-cookie: Karchan=%s;\r\n\r\n", password);
			send_printf(socketfd, "<HTML><HEAD><TITLE>Land of Karchan - %s</TITLE></HEAD>\r\n", name);
			send_printf(socketfd, "<FRAMESET ROWS=\"*,50\">\r\n");
			send_printf(socketfd, "	<FRAMESET COLS=\"*,180\">\r\n");
			send_printf(socketfd, "		<FRAME SRC=%s?command=me+has+entered+the+game...&name=%s&password=%s&frames=2 NAME=\"main\" border=0>\r\n", getParam(MM_MUDCGI), name, password);
			send_printf(socketfd, "		<FRAME SRC=%s?name=%s&password=%s NAME=\"leftframe\" scrolling=\"no\" border=0>\r\n", getParam(MM_LEFTFRAMECGI), name, password);
			send_printf(socketfd, "	</FRAMESET>\r\n");
			send_printf(socketfd, "	<FRAME SRC=%s?name=%s&password=%s NAME=\"logon\" scrolling=\"no\" border=0>\r\n", getParam(MM_LOGONFRAMECGI), name, password);
			send_printf(socketfd, "</FRAMESET>\r\n");
			send_printf(socketfd, "</HTML>\r\n");
		} else
		{
			send_printf(socketfd, "Content-type: text/html\r\n");
			send_printf(socketfd, "Set-cookie: Karchan=%s;\r\n\r\n", password);
			send_printf(socketfd, "<HTML><HEAD><TITLE>Land of Karchan - %s</TITLE></HEAD>\r\n", name);
			send_printf(socketfd, "<FRAMESET ROWS=\"*,50,0,0\">\r\n");
			send_printf(socketfd, "	<FRAMESET COLS=\"*,180\">\r\n");
			send_printf(socketfd, "		<FRAMESET ROWS=\"60%,40%\">\r\n");
			send_printf(socketfd, "		<FRAME SRC=%s?command=me+has+entered+the+game...&name=%s&password=%s&frames=3 NAME=\"statusFrame\" border=0>\r\n", getParam(MM_MUDCGI), name, password);
			send_printf(socketfd, "		<FRAME SRC=http://%s/karchan/empty.html NAME=\"logFrame\">\r\n", getParam(MM_SERVERNAME));
			send_printf(socketfd, "		</FRAMESET>\r\n");
			send_printf(socketfd, "	<FRAME SRC=http://%s%snph-leftframe.cgi?name=%s&password=%s NAME=\"leftFrame\" scrolling=\"no\" border=0>\r\n", getParam(MM_SERVERNAME), getParam(MM_CGINAME), name, password);
			send_printf(socketfd, "	</FRAMESET>\r\n\r\n");
			send_printf(socketfd, "	<FRAME SRC=http://%s%snph-logonframe.cgi?name=%s&password=%s NAME=\"commandFrame\" scrolling=\"no\" border=0>\r\n", getParam(MM_SERVERNAME), getParam(MM_CGINAME), name, password);
			send_printf(socketfd, "	<FRAME SRC=http://%s%snph-javascriptframe.cgi?name=%s&password=%s NAME=\"javascriptFrame\">\r\n", getParam(MM_SERVERNAME), getParam(MM_CGINAME), name, password);
			send_printf(socketfd, "	<FRAME SRC=http://%s/karchan/empty.html NAME=\"duhFrame\">\r\n", getParam(MM_SERVERNAME));
			send_printf(socketfd, "</FRAMESET>\r\n");
			send_printf(socketfd, "</HTML>\r\n");
		}
	}
}

//! main function for logging into the game 
/*! 
\param socketfd int, is the socketdescriptor of the communication channel, can be used to look up information regarding the connection
using find_in_list for example.
\see find_in_list
\see remove_from_list
\returns integer with the success code, 0 is bad, 1 is good
*/
int
gameLogon(int socketfd)
{
	int frames;
	char *name;
	char *password;
	char *cookie;
	char *address;
	char secretpassword[26];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	mudpersonstruct *mymudstruct;
	
//	umask(0000);
	mymudstruct = find_in_list(socketfd);
	cookie = mymudstruct->cookie;
	name = mymudstruct->name;
	password = mymudstruct->password;
	frames = mymudstruct->frames;
	address = mymudstruct->address;
	/*	send_printf(fmudstruct->socketfd, "[%s]", getenv("HTTP_COOKIE"));*/
#ifdef DEBUG
	printf("gameLogon started (%s,%s,%s,%s)!!!\n", name, password, cookie, address);
#endif
	
	
	if (strcmp("Karn", name)) 
	{
		if (CheckForOfflineMud(socketfd) == 0)
		{
			return 0;
		}
	}

	if (SearchBanList(address, name)) 
	{
		BannedFromGame(name, address, socketfd);
		return 0;
	}

	if (StrangeName(name, password, address, socketfd) == 0)
	{
		return 0;
	}
	
	if ( (strstr(name," ")!=NULL) ||
		(strstr(password," ")!=NULL) ||
		(strlen(password)<5) ||
		(strlen(name)<3) )
	{
		ToManyNames(name, address, socketfd);
		return 0;
	}
	
	/* set the secret password */
	generate_password(secretpassword);
	temp = composeSqlStatement("update usertable set lok='%x' where name='%x'", secretpassword, name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	if (res!=NULL) 
	{
		mysql_free_result(res);
	} 
	
	/* ExistUser */
	temp = composeSqlStatement("select name, password, god, lok from tmp_usertable where name='%x' and lok<>''", name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;

	if (res!=NULL) 
	{
		row = mysql_fetch_row(res);
		if (row!=NULL) 
		{
			char fpassword[40], fname[40], fsecretpassword[40], *fcookie;
			fcookie=cookie;
			strcpy(fname, row[0]);
			strcpy(fpassword, row[1]);
			strcpy(fsecretpassword, row[3]);
			if (!strcmp(row[2],"2")) 
			{
				mysql_free_result(res);
				WrongPasswd(name, address, "Bot attempted logon detected", socketfd);
				return 0;
			}
			mysql_free_result(res);
			if (strcmp(fpassword, password)!=0) 
			{
				WrongPasswd(name, address, "Wrong Password detected during relogin", socketfd);
				return 0;
			}
			if ((cookie != NULL) && (strcmp(cookie, "")) && (strcmp(cookie, " ")))
			{
				/* cookie exists, check if cookie corresponds with current user 
				 in that case, the user is attempting to relogon after a browser
				 crash
				*/
				if (strcmp(fcookie, fsecretpassword))
				{
					/* cookie does not correspond with the lok-value in the usertable
					 in this case, the user is attempting to relogon a character
					 for which the cookie does not match. In the case of an error,
					 he will be required to close and restart his webbrowser 
				 */
					MultiPlayerDetected(name, address, socketfd);
					return 0;
				}
			}
			AlreadyActive(fname, fsecretpassword, fsecretpassword, address, frames, socketfd);
			return 0;
		}
		else
		{
			mysql_free_result(res);
		}
	} 
	else
	{
		mysql_free_result(res);
	}
	
	/* SearchUser */
	temp = composeSqlStatement("select name, password from usertable where name='%x'", name);
	res=sendQuery(temp, NULL); 
	free(temp);temp=NULL;
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
	
		if (row!=NULL)
		{
			if (strcmp(row[1], password)!=0) 
			{
				mysql_free_result(res);
				WrongPasswd(name, address, "Wrong Password detected during logon", socketfd);
				return 0;
			}
		}
		else
		{
			mysql_free_result(res);
			NewPlayer(name,address, password, frames, socketfd);
			return 1;
		}
	}
	else
	{
		NewPlayer(name,address, password, frames, socketfd);
		return 1;
	}
	
	/* the user is attempting to logon to the game, yet he already has a cookie
		in this case this is not allowed, and a multiplayer log entry will appear.
		In some cases this is erroneous, namely in the fact that the user might be
		deactivated after an hour, but his cookie will remain.
	*/
	if ((cookie != NULL) && (strcmp(cookie, "")) && (strcmp(cookie, " ")))
	{
		MultiPlayerDetected(name, address, socketfd);
		return 0;
	}

	strcpy(name, row[0]);
	mysql_free_result(res);
	ActivateUser(name);
	MakeStart(name, secretpassword, secretpassword, address, frames, socketfd);
	return 1;
}
