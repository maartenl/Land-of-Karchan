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
#include "cgic.h"

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
		fprintf(cgiOut,"<A HREF=\"http://"ServerName"/karchan/enter.html\">Click here to retry</A></body>\n");
		fprintf(cgiOut, "</body>\n");
		fprintf(cgiOut, "</HTML>\n");
		time(&tijd);
		datum=*(gmtime(&tijd));
		WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-%i Invalid name by %s (%s) <BR>\n",datum.tm_hour,
		datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
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
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-%i Cookie not found for newchar by %s (%s) <BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
	closedbconnection();
	exit(0);
}

void 
MakeStart2(char *name, char *password, char *address)
{
	char            printstr[512];
	time_t          tijd;
	struct tm       datum;

	time(&tijd);
	datum = *(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile, "%i:%i:%i %i-%i-%i %s (%s) has entered the game and is new<BR>\n", datum.tm_hour,
				     datum.tm_min, datum.tm_sec, datum.tm_mday, datum.tm_mon + 1, datum.tm_year+1900, name, address);
	sprintf(printstr, "%s has entered the game and is new here...<BR>\r\n", name, address);
	sprintf(printstr, USERHeader "%s.log", name);
	WriteSentenceIntoOwnLogFile(printstr, "You appear from nowhere.<BR>\r\n");
//	cgiHeaderContentType("text/html");
	fprintf(cgiOut, "Content-type: text/html\r\n");  
	fprintf(cgiOut, "Set-cookie: Karchan=%s;\r\n\r\n", password);        
//getCookie(cgiOut, "Karchan","");
	WriteRoom(name, password, 1, 0);
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
	char *temp;
	time(&tijd);
	datum=*(gmtime(&tijd));
//	printf("Dude3! %s, %s, %s, %i\n", name, password, address, room);
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-%i  %s (%s) entered the game<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name, address);
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
	
	temp = composeSqlStatement("UPDATE tmp_usertable SET "
	"cgiServerSoftware='%x', "
	"cgiServerName='%x', "
	"cgiGatewayInterface='%x', "
	"cgiServerProtocol='%x', "
	"cgiServerPort='%x', "
	"cgiRequestMethod='%x', "
	"cgiPathInfo='%x', "
	"cgiPathTranslated='%x', "
	"cgiScriptName='%x', "
	"cgiRemoteHost='%x', "
	"cgiRemoteAddr='%x', "
	"cgiAuthType='%x', "
	"cgiRemoteUser='%x', "
	"cgiRemoteIdent='%x', "
	"cgiContentType='%x', "
	"cgiAccept='%x', "
	"cgiUserAgent='%x', address='%x' "
	"WHERE name='%x'", cgiServerSoftware, cgiServerName, cgiGatewayInterface, 
	cgiServerProtocol, cgiServerPort, cgiRequestMethod, cgiPathInfo, 
	cgiPathTranslated, cgiScriptName, cgiRemoteHost, cgiRemoteAddr,
	cgiAuthType, cgiRemoteUser, cgiRemoteIdent, cgiContentType, cgiAccept,
	cgiUserAgent, cgiRemoteAddr, name); 
	res=SendSQL2(temp, NULL);
	free(temp);
	mysql_free_result(res);
	WriteSentenceIntoOwnLogFile(printstr, "You appear from nowhere.<BR>\r\n");

//	printf("Dude3! %s, %s, %s, %i\n", name, password, address, room);
	temp = composeSqlStatement("SELECT count(*) FROM tmp_mailtable"
		" WHERE toname='%x' and newmail=1", name);
//	printf("%s\n",temp);
	res=SendSQL2(temp, NULL);
	free(temp);
	                        
	row = mysql_fetch_row(res);
	if (*row[0]=='0') {WriteSentenceIntoOwnLogFile(printstr, "You have no new MudMail...<P>\r\n");}
		else {WriteSentenceIntoOwnLogFile(printstr, "You have new MudMail!<P>\r\n");}
	mysql_free_result(res);

	if (!getFrames())
	{
//		cgiHeaderContentType("text/html");
		fprintf(cgiOut, "Content-type: text/html\r\n");
		fprintf(cgiOut, "Set-cookie: Karchan=%s;\r\n\r\n", password);        
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
fprintf(cgiOut,"<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Wrong Password</H1><HR>\n");
fprintf(cgiOut,"You filled out the wrong password for that particular name! \n");
fprintf(cgiOut,"Please retry by clicking at the link below:<P>\n");
fprintf(cgiOut,"<A HREF=\"http://"ServerName"/karchan/enter.html\">Click here to retry</A></body>\n");
time(&tijd);
datum=*(gmtime(&tijd));
WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-%i Password Fault by %s (%s) (newchar)<BR>\n",datum.tm_hour, 
datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,name,address);
exit(0);
}

void InsertPersonalInfo()
{
	int textlength, sqlsize, i;
	char *sqlstring, *formstring, *sqlformstring;
	char *formitems[] = 
		{"RealName", 
		"EMAIL", 
		"myrealage", 
		"mysex", 
		"mycountry",
		"myoccupation",
		"myinternal",
		"myexternal",
		"mycomments",
		NULL};

	sqlstring = (char *) malloc(48);
	sqlsize=48;
	strcpy(sqlstring, "insert into private_info values (null, now(), '");

	for (i = 0; formitems[i] != NULL; i++)
	{
		/*	- put length of cgientry in 'textlength'
				- allocate 'textlength' memory to formstring
				- put cgientry in 'formstring'
				- allocate double memory to sqlformstring
				- put safesql formstring in 'sqlformstring'
				- textlength = length of sqlformstring
				- reallocate memory for sqlstring
				- add sqlformstring to sqlstring
				- free everything and add to sqlsize
			*/
		cgiFormStringSpaceNeeded(formitems[i], &textlength);
		formstring = (char *) malloc(textlength);
		cgiFormString(formitems[i], formstring, textlength);
		sqlformstring = (char *) malloc(2*textlength+1+2);
		mysql_escape_string(sqlformstring,formstring,strlen(formstring));
		if (formitems[i+1] == NULL)
		{
			strcat(sqlformstring,"')");
		}
		else
		{
			strcat(sqlformstring,"','");
		}
		textlength = strlen(sqlformstring);
		sqlstring = (char *) realloc(sqlstring, sqlsize + textlength);
		strcat(sqlstring, sqlformstring);
		sqlsize += textlength;
//		fprintf(cgiOut, "[%s][%s]<BR>\n", formstring, sqlformstring);
		free(formstring);free(sqlformstring);
	}
	SendSQL2(sqlstring, NULL);
//	fprintf(cgiOut, "[%s]", sqlstring);
	free(sqlstring);
}

int 
cgiMain()
{
	char           *frace[] = {
		"human",
		"dwarf",
		"elf"
	};
	char           *fsex[] = {
		"male",
		"female"
	};
	char           *fage[] = {
		"young",
		"middle-aged",
		"old",
		"very old"
	};
	char           *flength[] = {
		"very small",
		"small",
		"medium",
		"tall",
		"very tall"
	};
	char           *fwidth[] = {
		"very thin",
		"thin",
		"transparent",
		"skinny",
		"slender",
		"medium",
		"corpulescent",
		"fat",
		"very fat",
		"ascetic",
		"athlethic",
		"none",
	};
	char           *fcomplexion[] = {
		"black",
		"brown-skinned",
		"dark-skinned",
		"ebony-skinned",
		"green-skinned",
		"ivory-skinned",
		"light-skinned",
		"pale",
		"pallid",
		"swarthy",
		"white",
		"none",
	};
	char           *feyes[] = {
		"black-eyed",
		"blue-eyed",
		"brown-eyed",
		"dark-eyed",
		"gray-eyed",
		"green-eyed",
		"one-eyed",
		"red-eyed",
		"round-eyed",
		"slant-eyed",
		"squinty-eyed",
		"yellow-eyed",
		"none"
	};
	char           *fface[] = {
		"big-nosed",
		"chinless",
		"dimpled",
		"double-chinned",
		"furry-eared",
		"hook-nosed",
		"jug-eared",
		"knobnosed",
		"long-faced",
		"pointy-eared",
		"poppy-eyed",
		"potato-nosed",
		"pug-nosed",
		"red-nosed",
		"roman-nosed",
		"round-faced",
		"sad-faced",
		"square-faced",
		"square-jawed",
		"stone-faced",
		"thin-faced",
		"upnosed",
		"wide-mouthed",
		"none"
	};
	char           *fhair[] = {
		"bald",
		"balding",
		"black-haired",
		"blond-haired",
		"blue-haired",
		"brown-haired",
		"chestnut-haired",
		"dark-haired",
		"gray-haired",
		"green-haired",
		"light-haired",
		"long haired",
		"orange-haired",
		"purple-haired",
		"red-haired",
		"white-haired",
		"none"
	};

	char           *fbeard[] = {
		"black-bearded",
		"blond-bearded",
		"blue-bearded",
		"brown-bearded",
		"clean",
		"shaven",
		"fork-bearded",
		"gray-bearded",
		"green-bearded",
		"long bearded",
		"mustachioed",
		"orange-bearded",
		"purple-bearded",
		"red-bearded",
		"thinly-bearded",
		"white-bearded",
		"with a ponytale",
		"none"
	};

	char           *farm[] = {
		"long-armed",
		"short-armeda",
		"thick-armed",
		"thin-armed",
		"none"
	};

	char           *fleg[] = {
		"bandy-legged",
		"long-legged",
		"short-legged",
		"skinny-legged",
		"thin-legged",
		"thick-legged",
		"none"
	};

	int             choice;
	time_t          currenttime;
	char 		troep[100];
	char		tempfield[255], safetempfield[511];
	char 		sqlstring[2048];
	char		name[40], password[40], frames[10];
	cgiFormResultType error;

/*  fprintf(cgiOut, "[%s]", getenv("HTTP_COOKIE"));*/
	generate_password(secretpassword);
  	opendbconnection();
	setMMudOut(cgiOut);

	umask(0000);
	strcpy(sqlstring,"insert into usertable "
	"(name, password, title, realname, email, race, sex, age, length, width, complexion, eyes, face, hair, beard, arm, leg, lok, lastlogin, birth)"
	"values('");

	error = cgiFormString("name", tempfield, 20);
	strcpy(name, tempfield);
	strcpy(troep, name);
	if (SearchBanList(cgiRemoteAddr, name)) {BannedFromGame(name, cgiRemoteAddr);}

	strcat(sqlstring, tempfield);strcat(sqlstring, "','"); /*name*/
	if (error != cgiFormSuccess) {
		exit(0);
	}
	if (cgiFormString("frames", frames, 10)!=cgiFormSuccess)
	{
		strcpy(frames, "none");
		setFrames(0);
	}
	if (!strcmp(frames,"1")) {setFrames(0);}
	if (!strcmp(frames,"2")) {setFrames(1);}
	if (!strcmp(frames,"3")) {setFrames(2);}
	cgiFormString("password", tempfield, 40);
	strcpy(password, tempfield);
	strcat(sqlstring, tempfield);strcat(sqlstring, "','");
	StrangeName(troep, password, cgiRemoteAddr);
	if (SearchUser(troep)==1) {WrongPasswd(troep, cgiRemoteAddr);}
	cgiFormString("title", tempfield, 80);
	mysql_escape_string(safetempfield, tempfield, strlen(tempfield));
	strcat(sqlstring, safetempfield);
	strcat(sqlstring, "','");
	cgiFormString("RealName", tempfield, 80);
	mysql_escape_string(safetempfield, tempfield, strlen(tempfield));
	strcat(sqlstring, safetempfield);
	strcat(sqlstring, "','");
	cgiFormString("EMAIL", tempfield, 40);
	mysql_escape_string(safetempfield, tempfield, strlen(tempfield));
	strcat(sqlstring, safetempfield);
	strcat(sqlstring, "','");
	cgiFormSelectSingle("race", frace, 3, &choice, 0);
	strcat(sqlstring, frace[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("sex", fsex, 2, &choice, 0);
	strcat(sqlstring, fsex[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("age", fage, 4, &choice, 0);
	strcat(sqlstring, fage[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("length", flength, 5, &choice, 0);
	strcat(sqlstring, flength[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("width", fwidth, 12, &choice, 0);
	strcat(sqlstring, fwidth[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("complexion", fcomplexion, 12, &choice, 0);
	strcat(sqlstring, fcomplexion[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("eyes", feyes, 12, &choice, 0);
	strcat(sqlstring, feyes[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("face", fface, 24, &choice, 0);
	strcat(sqlstring, fface[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("hair", fhair, 18, &choice, 0);
	strcat(sqlstring, fhair[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("beard", fbeard, 18, &choice, 0);
	strcat(sqlstring, fbeard[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("arms", farm, 5, &choice, 0);
	strcat(sqlstring, farm[choice]);strcat(sqlstring, "','");
	cgiFormSelectSingle("legs", fleg, 7, &choice, 0);
	strcat(sqlstring, fleg[choice]);strcat(sqlstring, "','");

	time(&currenttime);

	strcat(sqlstring, secretpassword); /* == lok */
	strcat(sqlstring, "'");
	strcat(sqlstring, ",DATE_SUB(NOW(), INTERVAL 2 HOUR), "
						 "DATE_SUB(NOW(), INTERVAL 2 HOUR))");
	SendSQL2(sqlstring, NULL);
	free(sqlstring);
	InsertPersonalInfo();
 	
	MakeStart(name, secretpassword, cgiRemoteAddr, 1);
	ActivateUser(name);
	closedbconnection();
	return (0);
}
