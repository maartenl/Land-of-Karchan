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
#include "mud-lib.h"

/*name, west, east, north, south, up, down*/
extern int hellroom;
extern char secretpassword[40];

void StrangeName(char *name, char *password, char *address)
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
		char printstr[512];
		char *error1,*error2;
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
		cgiHeaderContentType("text/html");
//getCookie(cgiOut, "Karchan","");
		fprintf(cgiOut, "<HTML><HEAD><TITLE>Error - %s</TITLE></HEAD>\n\n", error1);
		fprintf(cgiOut, "<BODY>\n");
		fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>%s</H1><HR>\n", error1);
		fprintf(cgiOut,"%s<P>\r\n", error2);

		fprintf(cgiOut,"The following rules need to be followed when filling out a name and password:<P>\r\n");
		fprintf(cgiOut,"<UL><LI>the following characters are valid in a name: {A..Z, a..z, _, ~}");
		fprintf(cgiOut,"<LI>all characters are valid in a password except {\"} and {'}");
		fprintf(cgiOut,"<LI>at least 3 characters are required for a name");
		fprintf(cgiOut,"<LI>at least 5 characters are required for a password");
		fprintf(cgiOut,"</UL><P>These are the rules.<P>\r\n");
		fprintf(cgiOut,"<A HREF=\"http://%s/karchan/enter.html\">Click here to retry</A></body>\n", ServerName);
		fprintf(cgiOut, "</body>\n");
		fprintf(cgiOut, "</HTML>\n");
		time(&tijd);
		datum=*(gmtime(&tijd));
		WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i Invalid name by %s (%s) <BR>\n",datum.tm_hour,
		datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,name, address);
		closedbconnection();
				exit(0);
	}
} /*endproc*/

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
	fprintf(cgiOut, "You, or someone in your domain,  has angered the gods by behaving badly on this mud. ");
	fprintf(cgiOut, "Your ip domain is therefore banned from the game.<P>\n");
	fprintf(cgiOut, "If you have not misbehaved or even have never before played the game before, and wish"
	" to play with your current IP address, email to "
	"<A HREF=\"mailto:deputy@%s\">deputy@%s</A> and ask them to make "
	"an exception in your case. Do <I>not</I> forget to provide your "
	"Character name.<P>You'll be okay as long as you follow the rules.<P>\n", ServerName, ServerName);
	fprintf(cgiOut, "</body>\n");
	fprintf(cgiOut, "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i Banned from mud by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,name, address);
	closedbconnection();
	exit(0);
}

void MultiPlayerDetected(char *name, char *address)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	cgiHeaderContentType("text/html");
//getCookie(cgiOut, "Karchan","");
	fprintf(cgiOut, "<HTML><HEAD><TITLE>Multiple Player Detected</TITLE></HEAD>\n\n");
	fprintf(cgiOut, "<BODY>\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Multiple Player Detected</H1><HR>\n");
	fprintf(cgiOut, "You are already playing this mud under another character's");
	fprintf(cgiOut, " name. You are only allowed to log on once.<P>\n");
	fprintf(cgiOut, "</body>\n");
	fprintf(cgiOut, "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i Multiplayer detected by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,name, address);
	closedbconnection();
	exit(0);
}

void CheckForOfflineMud()
{
FILE *fp;

fp = fopen(MudOffLineFile, "r");
if (fp==NULL)
	{
	// Everything fine...
	} else
	{
	fclose(fp);
	cgiHeaderContentType("text/html");
//getCookie(cgiOut, "Karchan","");
	ReadFile(MudOffLineFile);
	closedbconnection();
	exit(0);
	}
}

void AlreadyActive(char *name, char *password, char *address)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	cgiHeaderContentType("text/html");
//getCookie(cgiOut, "Karchan","");
	fprintf(cgiOut, "<HTML><HEAD><TITLE>Error</TITLE></HEAD>\n\n");
	fprintf(cgiOut, "<BODY>\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Already Active</H1><HR>\n");
	fprintf(cgiOut, "You tried to start a session which is already in progress. You can't play \n");
	fprintf(cgiOut, "two sessions at the same time! Please check below to try again. In case you \n");
	fprintf(cgiOut, "accidently turned of your computerterminal or Netscape or Lynx without first \n");
	fprintf(cgiOut, "having typed <B>QUIT</B> while you were in the MUD, you can reenter the game \n");
	fprintf(cgiOut, "by using the second link. You have to sit at the same computer as you did \n");
	fprintf(cgiOut, "when you logged in.<P>\n");
	fprintf(cgiOut, "<A HREF=\"http://%s/karchan/enter.html\">Click here to\n", ServerName);
	fprintf(cgiOut, "retry</A><P>\n");
	
	fprintf(cgiOut,"<FORM METHOD=\"POST\" ACTION=\"%s\">", MudreloginExe);
	fprintf(cgiOut,"Do you wish to enter into the active character?<BR>");
	fprintf(cgiOut,"<input type=\"radio\" name=\"choice\" value=\"yes\">yes<BR>");
	fprintf(cgiOut,"<input type=\"radio\" name=\"choice\" value=\"no\" checked>no<P>");
	fprintf(cgiOut,"<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n",name);
	fprintf(cgiOut,"<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n",password);
	fprintf(cgiOut,"<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"%i\">\n",getFrames()+1);
	fprintf(cgiOut,"<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n");
	fprintf(cgiOut,"<INPUT TYPE=\"reset\" VALUE=\"Clear\">\n");
	fprintf(cgiOut,"</FORM>\n");
	
	fprintf(cgiOut,"<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut,"<DIV ALIGN=left><P>");
	fprintf(cgiOut, "</body>\n");
	fprintf(cgiOut, "</HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
//	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i Already Active Fault by %s (%s)<BR>\n",datum.tm_hour,
//	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,name,address);
	closedbconnection();
	exit(0);
}

void WrongPasswd(char *name, char *address, char *error)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	cgiHeaderContentType("text/html");
	fprintf(cgiOut,"<html><head><Title>Error</Title></head>\n");
	fprintf(cgiOut,"<body>\n");
	fprintf(cgiOut,"<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Wrong Password</H1><HR>\n");
	fprintf(cgiOut,"You filled out the wrong password for that particular name! \n");
	fprintf(cgiOut,"Please retry by clicking at the link below:<P>\n");
	fprintf(cgiOut,"<A HREF=\"http://%s/karchan/enter.html\">Click here to retry</A></body>\n", ServerName);
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i %s by %s (%s)<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,error,name,address);
	closedbconnection();
	exit(0);
}

void WriteError(char *name, char *address, char *error)
{
	time_t tijd;
	struct tm datum;
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i %s by %s (%s)<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,error,name,address);
}

void ToManyUsers() 
{
	fprintf(cgiOut,"<head><Title>Error</Title></head>");
	fprintf(cgiOut,"<body>");
	fprintf(cgiOut,"<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>To many users</H1><HR>");
	fprintf(cgiOut,"I am sorry, but this game is currently out of space to provide for another");
	fprintf(cgiOut," new character. Please try again tomorrow or so when I make some more room.");
	fprintf(cgiOut," Thank you and sorry for the inconvenience. Please check below to try again,");
	fprintf(cgiOut," although that will probably not function immediately.<P> ");
	fprintf(cgiOut,"<A HREF=\"http:///karchan/enter.html\">Click here to retry</A></body>\n"), ServerName;
	closedbconnection();
	exit(0);
}
	
void ToManyNames(char *name, char *address) 
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	fprintf(cgiOut,"<head><Title>Error</Title></head>\n");
	fprintf(cgiOut,"<body>\n");
	fprintf(cgiOut,"<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>To many names</H1><HR>\n");
	fprintf(cgiOut,"I am sorry, but this game will not accept a multitude of names which you \n");
	fprintf(cgiOut,"have filled out in the Name-box of the fillout-form. Please do not type more \n");
	fprintf(cgiOut,"than one name. In the next fillout form you will be asked to type a title, \n");
	fprintf(cgiOut,"there you can type as much as you want.<P>"
	               "It is also illegal to use less than 3 characters for a name and "
	               "less then 5 characters in your password.<P>\n");
	fprintf(cgiOut,"<A HREF=\"http://%s/karchan/enter.html\">Click here to retry</A></body>\n", ServerName);
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i Too many names Fault by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,name,address);
	closedbconnection();
	exit(0);
}

void NewPlayer(char *fname, char *address, char *fpassword)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	cgiHeaderContentType("text/html");
	ReadFile(HTMLHeader"newchar.html");
	fprintf(cgiOut,"<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n",fname);
	fprintf(cgiOut,"<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n",fpassword);
	fprintf(cgiOut,"<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"%i\">\n",getFrames()+1);
	fprintf(cgiOut,"<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n");
	fprintf(cgiOut,"<INPUT TYPE=\"reset\" VALUE=\"Clear\">\n");
	fprintf(cgiOut,"</FORM></BODY></HTML>\n");
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i New User Signup : %s (%s)\n<BR>",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,fname, address);
	closedbconnection();
	exit(0);
}

void MakeStart(char *name, char *password, char *address, int room)
{
	char printstr[512];
	time_t tijd;
	struct tm datum;
	int             i = 1,j;
	FILE           *fp; 
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	time(&tijd);
	datum=*(gmtime(&tijd));
//	printf("Dude3! %s, %s, %s, %i\n", name, password, address, room);
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i  %s (%s) entered the game<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,name, address);
//	printf("Dude4!\n");
	sprintf(printstr,"%s has entered the game...<BR>\r\n",name);
	sprintf(printstr,USERHeader"%s.log",name);
//	printf("Dude3! %s, %s, %s, %i\n", name, password, address, room);
	res=SendSQL2("SELECT message FROM logonmessage WHERE id=0", NULL);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			WriteSentenceIntoOwnLogFile(printstr, row[0]);
		}
		mysql_free_result(res);
	}
	
	sprintf(temp, "UPDATE tmp_usertable SET "
	"cgiServerSoftware='%s', "
	"cgiServerName='%s', "
	"cgiGatewayInterface='%s', "
	"cgiServerProtocol='%s', "
	"cgiServerPort='%s', "
	"cgiRequestMethod='%s', "
	"cgiPathInfo='%s', "
	"cgiPathTranslated='%s', "
	"cgiScriptName='%s', "
	"cgiRemoteHost='%s', "
	"cgiRemoteAddr='%s', "
	"cgiAuthType='%s', "
	"cgiRemoteUser='%s', "
	"cgiRemoteIdent='%s', "
	"cgiContentType='%s', "
	"cgiAccept='%s', "
	"cgiUserAgent='%s', address='%s' "
	"WHERE name='%s'", cgiServerSoftware, cgiServerName, cgiGatewayInterface, 
	cgiServerProtocol, cgiServerPort, cgiRequestMethod, cgiPathInfo, 
	cgiPathTranslated, cgiScriptName, cgiRemoteHost, cgiRemoteAddr,
	cgiAuthType, cgiRemoteUser, cgiRemoteIdent, cgiContentType, cgiAccept,
	cgiUserAgent, cgiRemoteAddr, name); 
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);
	WriteSentenceIntoOwnLogFile(printstr, "You appear from nowhere.<BR>\r\n");

//	printf("Dude3! %s, %s, %s, %i\n", name, password, address, room);
	sprintf(temp, "SELECT count(*) FROM tmp_mailtable"
		" WHERE toname='%s' and newmail=1", name);
//	printf("%s\n",temp);
	res=SendSQL2(temp, NULL);
	                        
	row = mysql_fetch_row(res);
	if (*row[0]=='0') {WriteSentenceIntoOwnLogFile(printstr, "You have no new MudMail...<P>\r\n");}
		else {WriteSentenceIntoOwnLogFile(printstr, "You have new MudMail!<P>\r\n");}
	mysql_free_result(res);

	if (!getFrames())
	{
//			cgiHeaderContentType("text/html");
			fprintf(cgiOut, "Content-type: text/html\r\n");
			fprintf(cgiOut, "Set-cookie: Karchan=%s;\r\n\r\n", password);
//getCookie(cgiOut, "Karchan","");
   		WriteRoom(name, password, room, 0);
	}
	else
	{
		if (getFrames()==1)
		{
			fprintf(cgiOut, "Content-type: text/html\r\n");
			fprintf(cgiOut, "Set-cookie: Karchan=%s;\r\n\r\n", password);
//			cgiHeaderContentType("text/html");
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
			fprintf(cgiOut, "Content-type: text/html\r\n");
			fprintf(cgiOut, "Set-cookie: Karchan=%s;\r\n\r\n", password);
//			cgiHeaderContentType("text/html");
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

void checkcookie(char *name, char *address)
{
	char cookie[100];
	if (getCookie("Karchan", cookie))
	{
		MultiPlayerDetected(name, address);
	}
}

int cgiMain()
{
	char name[20];
	char password[40];
	char frames[10];
	int room;
	int i;
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	
	umask(0000);
	
	opendbconnection();
	
	if (0)
	{
		printf("Name:");gets(name);
		printf("Password:");gets(password);
		setFrames(1);		
	}
	else 
	{
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

/*	fprintf(cgiOut, "[%s]", getenv("HTTP_COOKIE"));*/
	
	if (strcmp("Karn", name)) {CheckForOfflineMud();}

	if (SearchBanList(cgiRemoteAddr, name)) {BannedFromGame(name, cgiRemoteAddr);}

	StrangeName(name, password, cgiRemoteAddr);
	
	if (strstr(name," ")!=NULL) {ToManyNames(name, cgiRemoteAddr);}
	if (strstr(password," ")!=NULL) {ToManyNames(name, cgiRemoteAddr);}
	if (strlen(password)<5) {ToManyNames(name, cgiRemoteAddr);}
	if (strlen(name)<3) {ToManyNames(name, cgiRemoteAddr);}
	
	/* set the secret password */
	generate_password(secretpassword);
	sprintf(temp, "update usertable "
	"set lok = '%s' "
	"where name = '%s'", secretpassword, name);
	res=SendSQL2(temp, NULL);
	if (res!=NULL) 
	{
		mysql_free_result(res);
	} 
	
	/* ExistUser */
	sprintf(temp, "select name, password, god, lok from tmp_usertable where name='%s' and lok<>''", name);
	
	res=SendSQL2(temp, NULL);

	if (res!=NULL) 
	{
		row = mysql_fetch_row(res);
		if (row!=NULL) 
		{
			char fpassword[40], fname[40], fsecretpassword[40], fcookie[40];
			strcpy(fname, row[0]);
			strcpy(fpassword, row[1]);
			strcpy(fsecretpassword, row[3]);
			if (!strcmp(row[2],"2")) 
			{
				mysql_free_result(res);
				WrongPasswd(name, cgiRemoteAddr, "Bot attempted logon detected");
			}
			mysql_free_result(res);
			if (strcmp(fpassword, password)!=0) 
			{
				WrongPasswd(name, cgiRemoteAddr, "Wrong Password detected during relogin");
			}
			if (getCookie("Karchan", fcookie))
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
					MultiPlayerDetected(name, cgiRemoteAddr);
				}
			}
			AlreadyActive(fname, fsecretpassword, cgiRemoteAddr);
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
	sprintf(temp, "select name, password, room from usertable where name='%s'", name);
	res=SendSQL2(temp, NULL); 
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
	
		if (row!=NULL)
		{
			if (strcmp(row[1], password)!=0) 
			{
				mysql_free_result(res);
				WrongPasswd(name, cgiRemoteAddr, "Wrong Password detected during logon");
			}
		}
		else
		{
			mysql_free_result(res);
			NewPlayer(name,cgiRemoteAddr, password);
		}
	}
	else
	{
		NewPlayer(name,cgiRemoteAddr, password);
	}
	
	/* the user is attempting to logon to the game, yet he already has a cookie
		in this case this is not allowed, and a multiplayer log entry will appear.
		In some cases this is erroneous, namely in the fact that the user might be
		deactivated after an hour, but his cookie will remain.
	*/
	checkcookie(name, cgiRemoteAddr);
	strcpy(name, row[0]);
	ActivateUser(name);
	MakeStart(name, secretpassword, cgiRemoteAddr, atoi(row[2]));
	mysql_free_result(res);
	closedbconnection();
	return 0;
}
