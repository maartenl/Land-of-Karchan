/*-------------------------------------------------------------------------
cvsinfo: $Header$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA02111-1307, USA.

Maarten van Leunen
Driek van Erpstraat 9
5341 AK Oss
Nederland
Europe
maartenl@il.fontys.nl
-------------------------------------------------------------------------*/
#include "mud-lib.h"
#include "cgic.h"

/* name, west, east, north, south, up, down */
extern char secretpassword[40];

void 
NoNewActivate()
{
	cgiHeaderContentType("text/html");
   fprintf(cgiOut, "<HTML><HEAD><TITLE>Goodbye</TITLE></HEAD>\n\n");
	fprintf(cgiOut, "<BODY>\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>"
		"<IMG SRC=\"http://"ServerName"/images/gif/dragon.gif\">Goodbye</H1><HR>\n");
	fprintf(cgiOut, "Thank you for using Land of Karchan. Have a nice day.<P>");
	fprintf(cgiOut, "</body>\n");
	fprintf(cgiOut, "</HTML>\n");
	closedbconnection();
	exit(0);
}


char *ages[] = {
	"yes",
"no"};

void 
MakeStart(char *name, char *password, int room, char *address)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	time(&tijd);
	datum = *(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile, "%i:%i:%i %i-%i-%i%s (%s) entered the game, again.<BR>\n", datum.tm_hour,
				 datum.tm_min, datum.tm_sec, datum.tm_mday, datum.tm_mon + 1, datum.tm_year+1900, name, address);
	sprintf(printstr, "%s has entered the game, again...<BR>\r\n", name);
	sprintf(printstr, USERHeader "%s.log", name);
	WriteSentenceIntoOwnLogFile(printstr, "You appear from nowhere.<BR>\r\n");

	if (!getFrames())
	{
			fprintf(cgiOut, "Content-type: text/html\r\n");  
			fprintf(cgiOut, "Set-cookie: Karchan=%s;\r\n\r\n", password);        
//			cgiHeaderContentType("text/html");
//getCookie(cgiOut, "Karchan","");
   		WriteRoom(name, password, room, 0);
	}
	else
	{
		if (getFrames()==1)
		{
//			cgiHeaderContentType("text/html");
			fprintf(cgiOut, "Content-type: text/html\r\n");  
			fprintf(cgiOut, "Set-cookie: Karchan=%s;\r\n\r\n", password);        
//getCookie(cgiOut, "Karchan","");
			fprintf(cgiOut, "<HTML><HEAD><TITLE>Land of Karchan - %s</TITLE></HEAD>\r\n", name);
			fprintf(cgiOut, "<FRAMESET ROWS=\"*,50\">\r\n");
			fprintf(cgiOut, "	<FRAMESET COLS=\"*,180\">\r\n");
			fprintf(cgiOut, "		<FRAME SRC=%s?command=l&name=%s&password=%s&frames=2 NAME=\"main\" border=0>\r\n", MudExe, name, password);
			fprintf(cgiOut, "		<FRAME SRC=%s?name=%s&password=%s NAME=\"leftframe\" scrolling=\"no\" border=0>\r\n", LeftframeExe, name, password);
			fprintf(cgiOut, "	</FRAMESET>\r\n");
			fprintf(cgiOut, "	<FRAME SRC=%s?name=%s&password=%s NAME=\"logon\" scrolling=\"no\" border=0>\r\n", LogonframeExe, name, password);
			fprintf(cgiOut, "</FRAMESET>\r\n");
			fprintf(cgiOut, "</HTML>\r\n");
		} else
		{
//			cgiHeaderContentType("text/html");
			fprintf(cgiOut, "Content-type: text/html\r\n");  
			fprintf(cgiOut, "Set-cookie: Karchan=%s;\r\n\r\n", password);        
//getCookie(cgiOut, "Karchan","");
			fprintf(cgiOut, "<HTML><HEAD><TITLE>Land of Karchan - %s</TITLE></HEAD>\r\n", name);
			fprintf(cgiOut, "<FRAMESET ROWS=\"*,50,0,0\">\r\n");
			fprintf(cgiOut, "	<FRAMESET COLS=\"*,180\">\r\n");
			fprintf(cgiOut, "		<FRAMESET ROWS=\"60%,40%\">\r\n");
			fprintf(cgiOut, "		<FRAME SRC=%s?command=l&name=%s&password=%s&frames=3 NAME=\"statusFrame\" border=0>\r\n", MudExe, name, password);
			fprintf(cgiOut, "		<FRAME SRC=http://%s/karchan/empty.html NAME=\"logFrame\">\r\n", ServerName);
			fprintf(cgiOut, "		</FRAMESET>\r\n");
			fprintf(cgiOut, "	<FRAME SRC=http://%s%snph-leftframe.cgi?name=%s&password=%s NAME=\"leftFrame\" scrolling=\"no\" border=0>\r\n", ServerName, CGIName, name, password);
			fprintf(cgiOut, "	</FRAMESET>\r\n\r\n");
			fprintf(cgiOut, "	<FRAME SRC=http://%s%snph-logonframe.cgi?name=%s&password=%s NAME=\"commandFrame\" scrolling=\"no\" border=0>\r\n", ServerName, CGIName, name, password);
			fprintf(cgiOut, "	<FRAME SRC=http://%s%snph-javascriptframe.cgi?name=%s&password=%s NAME=\"javascriptFrame\">\r\n", ServerName, CGIName, name, password);
			fprintf(cgiOut, "	<FRAME SRC=http://%s/karchan/empty.html NAME=\"duhFrame\">\r\n", ServerName);
			fprintf(cgiOut, "</FRAMESET>\r\n");
			fprintf(cgiOut, "</HTML>\r\n");
		}
	}
}

void WrongPasswd(char *name, char *address)
{
char printstr[512];
time_t tijd;
struct tm datum;
fprintf(cgiOut,"<html><head><Title>Error</Title></head>\n");
fprintf(cgiOut,"<body>\n");
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
fprintf(cgiOut,"<H1>Wrong Password</H1><HR>\n");
fprintf(cgiOut,"You filled out the wrong password for that particular name! \n");
fprintf(cgiOut,"Please retry by clicking at the link below:<P>\n");
fprintf(cgiOut,"<A HREF=\"http://"ServerName"/karchan/enter.html\">Click here to retry</A></body>\n");
time(&tijd);
datum=*(gmtime(&tijd));
WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-%i Password Fault by %s (%s) (mudrelogin)<BR>\n",datum.tm_hour, 
datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name,address);
	closedbconnection();
exit(0);
}

void BannedFromGame(char *name, char *address)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	cgiHeaderContentType("text/html");
//getCookie(cgiOut, "Karchan","");
	fprintf(cgiOut, "<HTML><HEAD><TITLE>You have been banned</TITLE></HEAD>\n\n");
	fprintf(cgiOut, "<BODY>\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Banned</H1><HR>\n");
	fprintf(cgiOut, "You, or someone in your domain,has angered the gods by behaving badly on this mud. ");
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
	closedbconnection();
	exit(0);
}

void CookieNotFound(char *name, char *address)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	cgiHeaderContentType("text/html");
//getCookie(cgiOut, "Karchan","");
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
cgiMain()
{
	char name[20];
	char password[40];
	char frames[10];
	int room;
	int i;
	int ageChoice;
	int	sleepstatus;
	char ageText[10];
	MYSQL_RES		*res;
	MYSQL_ROW		row;
	char 			temp[1024];
	umask(0000);

/*  fprintf(cgiOut, "[%s]", getenv("HTTP_COOKIE"));*/
  	opendbconnection();
	setMMudOut(cgiOut);
	if (0)
	{
		fprintf(cgiOut, "Name:");
		fgets(name, 20, stdin);
		fprintf(cgiOut, "Password:");
		fgets(password, 40, stdin);
		strcpy(ageText, "yes");
		setFrames(0);
	}
	else
	{
		cgiFormString("choice", ageText, 10);
		cgiFormString("name", name, 20);
		cgiFormString("password", password, 40);
		if (cgiFormString("frames", frames, 10)!=cgiFormSuccess)
		{
			strcpy(frames, "none");
			setFrames(0);
		}
		if (!strcmp(frames,"1")) {setFrames(0);}
		if (!strcmp(frames,"2")) {setFrames(1);}
		if (!strcmp(frames,"3")) {setFrames(2);}
	}
	if (!strcasecmp(ageText, "no")) {
		NoNewActivate();
	}
//	printf("Dude! %s", ageText);

	if (SearchBanList(cgiRemoteAddr, name)) {BannedFromGame(name, cgiRemoteAddr);}

	if (ExistUser(name)==0) 
	{
		WrongPasswd(name, cgiRemoteAddr);
	}

	/* GetUser */
	sprintf(temp, "select name, lok, room, sleep "
		"from tmp_usertable "
		"where name='%s' and lok<>''", name);
	res=SendSQL2(temp, NULL);
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);

		if (row!=NULL)
		{
			strcpy(name, row[0]);
			room=atoi(row[2]);
			sleepstatus=atoi(row[3]);
			if (strcmp(row[1], password)!=0) 
			{
				mysql_free_result(res);
				WrongPasswd(name, cgiRemoteAddr);
			}
			strcpy(password, row[1]);
			mysql_free_result(res);

		}
		else
		{
			mysql_free_result(res);
			WrongPasswd(name, cgiRemoteAddr);
		}
	}
	else
	{
		mysql_free_result(res);
		WrongPasswd(name, cgiRemoteAddr);	
	}

	/* Create New lok-temporary-secretpassword */
	generate_password(secretpassword);
	sprintf(temp, "update tmp_usertable "
		"set lok='%s' "
		"where name='%s'", secretpassword, name);
	res=SendSQL2(temp, NULL);
	if (res!=NULL) 
	{
		mysql_free_result(res);
	}

	MakeStart(name, secretpassword, room, cgiRemoteAddr);
	closedbconnection();
	return 0;
}
