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
#include "mud-lib2.h"

extern roomstruct room;
extern int      hellroom;
extern int      events[50];
extern int      knightlist[50];
extern int      miflist[50];
extern int      rangerlist[50];
extern banstruct banlist[50];
char           *troep;
char           *command;
char           *junk;
char           *printstr;
char           *tokens[100];
int             aantal;
struct tm       datumtijd;
time_t          datetime;

/*
  events: 
  	0 : chain? 
  	1 : hek? 
  	2 : cupboard deur open? 
  	3 : datum van de laatste keer dat er gereset is 
  	4 : rocks are there? 
  	5 : is chest in herberg open? 
  	6 : Zit er een rope aan de well? 
  	7 : Is die verdomde dwarf on the move? 
  	8 : Is de hatch open? 
  	9 : Is Karn aanwezig in de game?
  	10 : Weathertype 
  	11: day (1) or night (0)?
  	12: avalanche happened at room 291 or so?
 */

void 
KillGame()
{
//	closeDatabase();
	closedbconnection();
	free(troep);
	free(command);
	free(junk);
	free(printstr);
	exit(0);
}

void 
WriteMail(char *name, char *toname, char *header, char *message)
{
	int             i = 1,j;
	FILE           *fp;
MYSQL_RES *res;
MYSQL_ROW row;
char *temp;
char *safeheader, *safemessage;

//'9999-12-31 23:59:59'
temp = (char *) malloc(strlen(header)+strlen(message)+400);
safeheader = (char *) malloc(strlen(header)+100);
safemessage = (char *) malloc(strlen(message)+100);

mysql_escape_string(safeheader, header, strlen(header));
mysql_escape_string(safemessage, message, strlen(message));
                
sprintf(temp, "INSERT INTO %s VALUES ('%s', '%s', '%s', "
	"'%i-%i-%i %i:%i:%i',0,1, '%s')", 
	"tmp_mailtable", name, toname, safeheader,
	datumtijd.tm_year+1900, datumtijd.tm_mon+1, datumtijd.tm_mday,
	datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec,
	 safemessage);
res=SendSQL2(temp, NULL);
	mysql_free_result(res);
sprintf(temp, "INSERT INTO %s VALUES ('%s', '%s', '%s', "
	"'%i-%i-%i %i:%i:%i',0,1, '%s')", 
	"mailtable", name, toname, safeheader,
	datumtijd.tm_year+1900, datumtijd.tm_mon+1, datumtijd.tm_mday,
	datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec,
	 safemessage);
res=SendSQL2(temp, NULL);
	mysql_free_result(res);
free(temp);

/*	mailstruct      mailrec;
	FILE           *fp;
	fp = fopen(MailMessageFile, "ab");
	mailrec.positie.fileposition = ftell(fp);
	mailrec.positie.size = strlen(message) + 2;
	fwrite(message, strlen(message) + 2, 1, fp);
	fclose(fp);
	fp = fopen(MailStructFile, "ab");
	strcpy(mailrec.name, name);
	strcpy(mailrec.toname, toname);
	strcpy(mailrec.header, header);
	time(&(mailrec.datetime));
	fwrite(&mailrec, sizeof(mailrec), 1, fp);
	fclose(fp);*/
}

void 
ListMail(char *name, char *password, char *logname)
{
	int             i = 1,j;
	FILE           *fp;
MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];

  	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan\n");
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
	fprintf(cgiOut, "<H2>List of Mail</H2>");

sprintf(temp, "SELECT name, haveread, newmail, header FROM tmp_mailtable"
	" WHERE toname='%s' ORDER BY whensent ASC", name);
res=SendSQL2(temp, NULL);

fprintf(cgiOut, "<TABLE BORDER=0 VALIGN=top>\r\n");j=1;
while(row = mysql_fetch_row(res)) {
  fprintf(cgiOut, "<TR VALIGN=top><TD>%i.</TD><TD>", j++);
  if (atoi(row[2])>0) {fprintf(cgiOut,"N");}
  if (atoi(row[1])==0) {fprintf(cgiOut,"U");}
  fprintf(cgiOut,"</TD><TD><B>From: <BR>Header: </B></TD><TD>%s<BR>%s<P></TD></TR>\r\n",row[0],row[3]);
}
 fprintf(cgiOut, "</TABLE><BR>\r\n");
	mysql_free_result(res);

	PrintForm(name, password);
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
}

void 
ReadMail(char *name, char *password, int room, int messnr, int erasehem)
{
	int             i = 1,j;
	FILE           *fp;
	char logname[100];
MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];
char mailname[40], mailtoname[40], maildatetime[40];

sprintf(logname, "%s%s.log", USERHeader, name);

sprintf(temp, "SELECT * FROM tmp_mailtable"
	" WHERE toname='%s' ORDER BY whensent ASC", name);
res=SendSQL2(temp, NULL);

j=1;
while((row = mysql_fetch_row(res)) && (messnr!=j)) {j++;}
if (messnr!=j) 
{
	WriteSentenceIntoOwnLogFile(logname, "No mail with that number!<BR>\r\n");
	WriteRoom(name, password, room, 0);
	KillGame();
}

strcpy(mailname, row[0]);
strcpy(mailtoname, row[1]);
strcpy(maildatetime, row[3]);
	
fprintf(cgiOut, "<HTML>\n");
fprintf(cgiOut, "<HEAD>\n");
fprintf(cgiOut, "<TITLE>\n");
fprintf(cgiOut, "Land of Karchan\n");
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
fprintf(cgiOut, "<H1>Read Mail - %s</H1>", row[2]);
fprintf(cgiOut, "<HR noshade><TABLE BORDER=0>\r\n");
fprintf(cgiOut, "<TR><TD>Mes. Nr:</TD><TD> <B>%i</B></TD></TR>\r\n", messnr);
fprintf(cgiOut, "<TR><TD>From:</TD><TD><B>%s</B></TD></TR>\r\n", row[0]);
fprintf(cgiOut, "<TR><TD>Time:</TD><TD><B>%s</B></TD></TR>\r\n", row[3]+11);
*(row[3]+10)='\0';
fprintf(cgiOut, "<TR><TD>Date:</TD><TD><B>%s</B></TD></TR>\r\n", row[3]);
fprintf(cgiOut, "<TR><TD>New?:</TD><TD><B>");
if (atoi(row[5])>0) {fprintf(cgiOut, "Yes</B></TD></TR>\r\n");}
		else {fprintf(cgiOut, "No</B></TD></TR>\r\n");}
fprintf(cgiOut, "<TR><TD>Read?:</TD><TD><B>");
if (atoi(row[4])>0) {fprintf(cgiOut, "Yes</B></TD></TR>\r\n");}
		else {fprintf(cgiOut, "No</B></TD></TR>\r\n");}
fprintf(cgiOut, "<TR><TD>Header:</TD><TD><B>%s</B></TABLE>\r\n", row[2]);
fprintf(cgiOut, "<HR noshade>%s", row[6]);
fprintf(cgiOut, "<HR noshade><P>");
PrintForm(name, password);
fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
fprintf(cgiOut, "<DIV ALIGN=left><P>");

mysql_free_result(res);

if (erasehem) {
	sprintf(temp, "DELETE FROM %s WHERE name='%s' and toname='%s' and whensent='%s' ",
	"tmp_mailtable", mailname, mailtoname, maildatetime);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);
	sprintf(temp, "DELETE FROM %s WHERE name='%s' and toname='%s' and whensent='%s' ",
	"mailtable", mailname, mailtoname, maildatetime);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);
 } else {
	sprintf(temp, "UPDATE %s SET haveread=1 WHERE name='%s' and toname='%s' and whensent='%s' ",
	"tmp_mailtable", mailname, mailtoname, maildatetime);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);
	sprintf(temp, "UPDATE %s SET haveread=1 WHERE name='%s' and toname='%s' and whensent='%s' ",
	"mailtable", mailname, mailtoname, maildatetime);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);}
}

int 
ReadBill(char *botname, char *vraag, char *name, int room)
{
	FILE *fp;
	char logname[100];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	sprintf(temp, "select god from tmp_usertable where name = \"%s\" ", botname);
	res=SendSQL2(temp, NULL);
	if (res==NULL)
	{
		return 0;
	}
	if (row = mysql_fetch_row(res)) 
	{
		if (atoi(row[0])!=2)
		{
			return 0;
		}
	}
	else
	{
		mysql_free_result(res);
		return 0;
	}

	sprintf(temp, "select answer from answers where \"%s\" like question and "
			"name = \"%s\" ", vraag, botname);
	res=SendSQL2(temp, NULL);

	if (row = mysql_fetch_row(res)) {
	WriteSentenceIntoOwnLogFile(logname, "%s says [to you]: %s<BR>\r\n", botname, row[0]);
	WriteMessage(name, room, "%s says [to %s]: %s<BR>\r\n", botname, name, row[0]);
	} else {
	WriteSentenceIntoOwnLogFile(logname, "%s ignores you.<BR>\r\n", botname);
	WriteMessage(name, room, "%s ignores %s.<BR>\r\n", botname, name);
	fp = fopen(ErrorFile, "a");
	fprintf(fp, "No response: %s says [to %s]: %s\n", name, botname, vraag);
	fclose(fp);
	}
	mysql_free_result(res);
	return 0;
}
	

void 
ListActivePlayers(char *name, char *password)
{
	int				i = 0;
	FILE			*fp;
	MYSQL_RES 		*res;
	MYSQL_ROW		row;
	char			tempsql[1024];
	
	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - Who\n");
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
	fprintf(cgiOut, "<H2>List of All Users</H2>");
	sprintf(tempsql, "select count(*) from tmp_usertable where god<=1");
	res=SendSQL2(tempsql, NULL);
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
		fprintf(cgiOut, "<I>There are %s persons active in the game.</I><P>", 
			row[0]); 
	}
	mysql_free_result(res);
		
	fprintf(cgiOut, "<UL>");
	sprintf(tempsql, "select name, title, "
	"time_to_sec(date_sub(NOW(), INTERVAL 2 HOUR))-time_to_sec(lastlogin)"
	", sleep from tmp_usertable "
	"where god<=1");
	res=SendSQL2(tempsql, NULL);
	if (res!=NULL)
	{
		while ((row = mysql_fetch_row(res)))
		{
			if (atoi(row[3])==1)
			{
				fprintf(cgiOut, "<LI>%s, %s, sleeping (%i min, %i sec idle)\r\n", 
				row[0], row[1], atoi(row[2]) / 60, atoi(row[2]) % 60);
			}
			else
			{
				fprintf(cgiOut, "<LI>%s, %s (%i min, %i sec idle)\r\n", 
				row[0], row[1], atoi(row[2]) / 60, atoi(row[2]) % 60);
			}
		}
	}
	mysql_free_result(res);
	
	fprintf(cgiOut, "</UL>");

	PrintForm(name, password);
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
	KillGame();
}

void 
LookString(char *description, char *name, char *password)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan\n");
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

	fprintf(cgiOut, "%s", description);
	PrintForm(name, password);
	if (getFrames()!=2) {ReadFile(logname);}
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
}

void 
LookAtProc(int id, char *name, char *password)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan\n");
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

	sprintf(temp, "select description from items where id=%i",
			id);
	res=SendSQL2(temp, NULL);
	row = mysql_fetch_row(res);
	fprintf(cgiOut, "%s", row[0]);
	mysql_free_result(res);

	PrintForm(name, password);
	if (getFrames()!=2) {ReadFile(logname);}
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
}

void 
Look_Command(char *name, char *password, int room)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[2048];
	int i;
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	if (aantal==3)
	{
		sprintf(temp, "select items.description, items.name, items.adject1, items.adject2 from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '') and "
			"(items.name = '%s')",room, tokens[2]);
	}
	if (aantal==4)
	{
		sprintf(temp, "select items.description, items.name, items.adject1, items.adject2 from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '') and "
			"(items.name = '%s') and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') )",
			room, tokens[3], tokens[2], tokens[2], tokens[2]);
	}
	if (aantal==5)
	{
		sprintf(temp, "select items.description, items.name, items.adject1, items.adject2 from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '') and "
			"(items.name = '%s') and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') ) and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') )",
			room, tokens[4], tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3]);
	}
	if (aantal==6)
	{
		sprintf(temp, "select items.description, items.name, items.adject1, items.adject2 from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '') and "
			"(items.name = '%s') and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') ) and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') ) and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') )",
			room, tokens[5], tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3],
			tokens[4], tokens[4], tokens[4]);
	}
	res=SendSQL2(temp, NULL);
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);

		if (row!=NULL)
		{
			WriteSentenceIntoOwnLogFile(logname, "You look carefully at the %s %s %s.<BR>\r\n",
				row[2], row[3], row[1]);
	
			fprintf(cgiOut, "<HTML>\n");
			fprintf(cgiOut, "<HEAD>\n");
			fprintf(cgiOut, "<TITLE>\n");
			fprintf(cgiOut, "Land of Karchan\n");
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
		
			fprintf(cgiOut, "%s", row[0]);
			mysql_free_result(res);

			PrintForm(name, password);
			if (getFrames()!=2) {ReadFile(logname);}
			fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
			fprintf(cgiOut, "<DIV ALIGN=left><P>");
			KillGame();
			return ;
		}
		mysql_free_result(res);
	}
	else
	{
		mysql_free_result(res);

	}

	if (aantal==3)
	{
		sprintf(temp, "select items.description, items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '%s') and "
			"(items.name = '%s')",name, tokens[2]);
	}
	if (aantal==4)
	{
		sprintf(temp, "select items.description, items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '%s') and "
			"(items.name = '%s') and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') )",
			name, tokens[3], tokens[2], tokens[2], tokens[2]);
	}
	if (aantal==5)
	{
		sprintf(temp, "select items.description, items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '%s') and "
			"(items.name = '%s') and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') ) and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') )",
			name, tokens[4], tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3]);
	}
	if (aantal==6)
	{
		sprintf(temp, "select items.description, items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '%s') and "
			"(items.name = '%s') and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') ) and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') ) and "
			"( (items.adject1='%s') or "
			"  (items.adject2='%s') or "
			"  (items.adject3='%s') )",
			name, tokens[5], tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3],
			tokens[4], tokens[4], tokens[4]);
	}
	res=SendSQL2(temp, NULL);
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);

		if (row!=NULL)
		{
			if (row[4][0]!='\0')
			{
				WriteSentenceIntoOwnLogFile(logname, "You look carefully at the %s, %s %s, you are wearing on your %s.<BR>\r\n",
					row[2], row[3], row[1], row[4]);
			} else
			{
				if (row[5][0]!='\0')
				{
					WriteSentenceIntoOwnLogFile(logname, "You look carefully at the %s, %s %s, you are wielding in your %s.<BR>\r\n",
						row[2], row[3], row[1], row[5]);
				}
				else
				{
					WriteSentenceIntoOwnLogFile(logname, "You look carefully at the %s, %s %s, you are carrying.<BR>\r\n",
						row[2], row[3], row[1]);
				}
			}
	
			fprintf(cgiOut, "<HTML>\n");
			fprintf(cgiOut, "<HEAD>\n");
			fprintf(cgiOut, "<TITLE>\n");
			fprintf(cgiOut, "Land of Karchan\n");
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
		
			fprintf(cgiOut, "%s", row[0]);
			PrintForm(name, password);
			if (getFrames()!=2) {ReadFile(logname);}
			fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
			fprintf(cgiOut, "<DIV ALIGN=left><P>");
			mysql_free_result(res);

			KillGame();
			return ;
		}
		mysql_free_result(res);

	}
	else
	{
		mysql_free_result(res);
	}

	if (aantal==3)
	{
		sprintf(temp, "select * from tmp_usertable "
			"where (room = %i) and "
			"(name<>'%s') and "
			"(name='%s')",
			room, name, tokens[2]);
		res=SendSQL2(temp, NULL);
		if (res!=NULL)
		{
			row = mysql_fetch_row(res);
	
			if (row!=NULL)
			{
				int i;
				WriteSentenceIntoOwnLogFile(logname, "You look carefully at the %s, ", row[8]);
				if (strcmp(row[9], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[9]);}
				if (strcmp(row[10], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[10]);}
				if (strcmp(row[11], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[11]);}
				if (strcmp(row[12], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[12]);}
				if (strcmp(row[13], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[13]);}
				if (strcmp(row[14], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[14]);}
				if (strcmp(row[15], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[15]);}
				if (strcmp(row[16], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[16]);}
				if (strcmp(row[17], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[17]);}
				WriteSentenceIntoOwnLogFile(logname, "%s %s", row[7], row[6]);
				WriteSentenceIntoOwnLogFile(logname, " who calls %sself \r\n"
					"%s (%s).<BR>\r\n", /*sex*/ HeShe2(row[7]), row[0], row[3]);
				sprintf(temp, "select '%s', '%s', items.adject1, items.adject2, items.name,"
				" tmpitems.wearing, tmpitems.wielding from tmp_itemtable tmpitems, items where "
				"(belongsto='%s') and "
				"((wearing <> '') or "
				" (wielding <> '')) and "
				" (items.id = tmpitems.id)",row[0], row[7], row[0]);
				WriteSentenceIntoOwnLogFile(logname, "%s seems %s.<BR>\r\n", HeShe(row[7]), ShowString(atoi(row[29]), atoi(row[52])));
				if (!strcmp(row[26],"1")) 
				{
					WriteSentenceIntoOwnLogFile(logname, "%s is fast asleep.<BR>\r\n", row[0]);
				}
				WriteMessageTo(row[0], name, room, "%s is looking at %s.<BR>\r\n",name, row[0]);
				WriteSayTo(row[0], name, room, "You notice %s looking at you.<BR>", name);
				res=SendSQL2(temp, &i);
				while ((row = mysql_fetch_row(res))!=NULL)
				{
					if (row[5][0]!='\0')
					{
						WriteSentenceIntoOwnLogFile(logname, "%s is wearing a %s, %s %s on %s %s.<BR>\r\n",
						row[0], row[2], row[3], row[4], HeShe3(row[1]), row[5]);
					}
					else
					{
						char position[40];
						if (row[6][0]=='1') {strcpy(position, "right hand");}
							else {strcpy(position, "left hand");}
						WriteSentenceIntoOwnLogFile(logname, "%s is wielding a %s, %s %s in %s %s.<BR>\r\n",
						row[0], row[2], row[3], row[4], HeShe3(row[1]), position);
					}
					
				}
				mysql_free_result(res);

				WriteRoom(name, password, room, 0);
				KillGame();
				return ;
			}
			mysql_free_result(res);
	
		}
		else
		{
			mysql_free_result(res);
		}
	}
	
	WriteSentenceIntoOwnLogFile(logname, "You see nothing special.<BR>\r\n");
	WriteRoom(name, password, room, 0);
	KillGame();
}

void 
NotActive(char *fname, char *fpassword, int errornr)
{
	time_t tijd;
	struct tm datum;
	        
	fprintf(cgiOut, "<HTML><HEAD><TITLE>Error</TITLE></HEAD>\n\n");
	fprintf(cgiOut, "<BODY>\n");
	fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>You are no longer active</H1><HR>\n");
	fprintf(cgiOut, "You cannot MUD because you are not logged in (no more)."
	    " This might have happened to the following circumstances:<P>");
	fprintf(cgiOut, "<UL><LI>you were kicked out of the game for bad conduct");
	fprintf(cgiOut, "<LI>the game went down for dayly cleanup, killing of all"
		"active users");
	fprintf(cgiOut, "<LI>you were deactivated for not responding for over 1 hour");
	fprintf(cgiOut, "<LI>an error occurred</UL>");
	fprintf(cgiOut, "You should be able to relogin by using the usual link below:<P>");
	fprintf(cgiOut, "<A HREF=\"http://"ServerName"/karchan/enter.html\">Click here to\n");
	fprintf(cgiOut, "relogin</A><P>\n");
	fprintf(cgiOut, "</body>\n");
	fprintf(cgiOut, "</HTML>\n");
	
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(AuditTrailFile,"%i:%i:%i %i-%i-19%i NotActive by %s (%s) (error %i)<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,fname, fpassword, errornr);

	KillGame();
}

void 
DeputyAlias(char *name, char *password, int room)
{
	char logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[2048], temp2[2048];
	int i;
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	sprintf(temp, "select * from depalias where \"%s\" like id and \"%s\" like names",
			troep, name);
	res=SendSQL2(temp, NULL);
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);

		if (row!=NULL)
		{
			strcpy(temp, row[2]);
			mysql_free_result(res);
			*temp2=0;
			for (i=0;i<=strlen(temp);i++)
			{
				if (temp[i]!='$') 
				{
					temp2[strlen(temp2)+1]=0;
					temp2[strlen(temp2)]=temp[i];
				} else
				{
					i+=1;
					if (temp[i]=='*')
					{
					strcat(temp2, name);
					}
					else
					{
					strcat(temp2, tokens[temp[i]-'0']);
					}
				}
			}
			SendSQL(logname, name, password, temp2);
		}
		else
		{
			mysql_free_result(res);
		}
	}
	WriteRoom(name, password, room, 0);
	KillGame();
}

void 
Root_Command(char *name, char *password, int room)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader, name);
	if (tokens[0][0]=='x') {DeputyAlias(name, password, room);}
	if (!strcmp("s endsql",tokens[0])) {
		SendSQL(logname, name, password, command+(tokens[1]-tokens[0]));
		WriteRoom(name, password, room, 0);
		KillGame();
		}
	if ((aantal == 2) && (!strcmp(tokens[0], "deactivate")))
	{
		RemoveUser(tokens[1]);
		WriteSentenceIntoOwnLogFile(logname, "%s deactivated.<BR>\r\n", tokens[1]);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((*troep == 'a') && (*(troep + 1) == 'l') && (*(troep + 2) == 'l')) {
		SayToAll(command + 4);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
}

int CheckRoom(int i)
{
int j;
j=0;
switch(i) {
case 143 : {j=1;break;}
case 205 : {j=1;break;}
case 206 : {j=1;break;}
case 207 : {j=1;break;}
case 208 : {j=1;break;}
case 209 : {j=1;break;}
case 210 : {j=1;break;}
case 211 : {j=1;break;}
case 212 : {j=1;break;}
case 213 : {j=1;break;}
case 214 : {j=1;break;}
case 215 : {j=1;break;}
}  
return j;
}

void 
Evil_Command(char *name, char *password, int room)
{
	char logname[100];
	sprintf(logname, "%s%s.log",USERHeader, name);
	if ((*troep == 'a') && (*(troep + 1) == 'l') && (*(troep + 2) == 'l')) {
		SayToAll(command + 4);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((aantal == 3) &&
	    (!strcmp("beam", tokens[0])) && (!CheckRoom(atoi(tokens[2])))) {
		WriteMessage(name, room, "%s's eyes light up red for a few seconds, then black smoke begins to "
		"surround him. When the black smoke clears he appears to be gone.<BR>\r\n", name);
		WriteSentenceIntoOwnLogFile(logname, "You disappear and reappear in a cloud of smoke.<BR>\r\n");
		// x.room = atoi(tokens[2]);
		WriteMessage(name, room, "A black smokey cloud appears, when the smoke clears %s is standing"
		" there, grinning evilly at your surprised face.<BR>\r\n", name);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	if ((aantal == 4) &&
	    (!strcmp("beam", tokens[0]))  && (!CheckRoom(atoi(tokens[2])))) {
//		y.room = atoi(tokens[3]);
		WriteRoom(name, password, room, 0);
		KillGame();
	}
} /*endproc*/

void 
Quit_Command(char *name)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	char logname[100];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	sprintf(temp, "select punishment, address, room  from tmp_usertable where name='%s'",
			name);
	res=SendSQL2(temp, NULL);

	row = mysql_fetch_row(res);

	fprintf(cgiOut, "<HTML>\n");
	if (atoi(row[0]) > 0) {
		WriteMessage(name, atoi(row[2]), "A disgusting toad called ");
	}
	WriteMessage(name, atoi(row[2]), "%s left the game...<BR>\r\n", name);
	WriteSentenceIntoOwnLogFile(AuditTrailFile,
	"%i:%i:%i %i-%i-%i  %s (%s) has left the game<BR>\n", datumtijd.tm_hour,
				     datumtijd.tm_min, datumtijd.tm_sec, datumtijd.tm_mday, datumtijd.tm_mon + 1, datumtijd.tm_year+1900, name, row[1]);
	mysql_free_result(res);
	ReadFile(HTMLHeader "goodbye.html");
	RemoveUser(name);
	KillGame();
}

void 
HelpHint_Command(char *name, char *password, int room)
{
	char logname[100];
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	switch (room) {
		case 1:
		{
			WriteSentenceIntoOwnLogFile(logname, "Hint: Look very carefully at everything.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	case 9:
		{
			WriteSentenceIntoOwnLogFile(logname, "Hint: Look very carefully at everything.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	case 239:
		{
			WriteSentenceIntoOwnLogFile(logname, "Hint: Only your pride seems to be hurt as you stand "
			"in soggy clothing at the foot of a small slope. You notice a small object floating upstream to the north. It"
			" looks strangely familiar... .<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	case 16:
		{
			WriteSentenceIntoOwnLogFile(logname, "Hint: Look very carefully at everything.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	case 3:
		{
			WriteSentenceIntoOwnLogFile(logname, "Hint: Read sign to see how to mail messages.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	case 8:
		{
			WriteSentenceIntoOwnLogFile(logname, "Hint: Talk to Karaoke, give him what he needs most.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	case 5:
		{
			WriteSentenceIntoOwnLogFile(logname, "Hint: Try to climb down the well.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	case 20:
		{
			WriteSentenceIntoOwnLogFile(logname, "Hint: Open the chest with the key.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	}
	WriteSentenceIntoOwnLogFile(logname, "This room is exceptionally boring.<BR>\r\n");
	WriteRoom(name, password, room, 0);
	KillGame();
}

/*
 * tokens[1..aantal]
 * 
 * aantal=1 tokens[1]=noun
 * 
 * aantal=2 tokens[1..2]=adject1 noun tokens[1..2]=adject2 noun
 * tokens[1..2]=adject3 noun
 * 
 * aantal=3 tokens[1..3]=adject1 adject2 noun tokens[1..3]=adject1 adject3 noun
 * tokens[1..3]=adject2 adject1 noun tokens[1..3]=adject2 adject3 noun
 * tokens[1..3]=adject3 adject1 noun tokens[1..3]=adject3 adject2 noun
 * 
 * aantal=4 tokens[1..4]=adject1 adject2 adject3 noun tokens[1..4]=adject1 adject3
 * adject2 noun tokens[1..4]=adject2 adject1 adject3 noun tokens[1..4]=adject2
 * adject3 adject1 noun tokens[1..4]=adject3 adject1 adject2 noun
 * tokens[1..4]=adject3 adject2 adject1 noun
 */

int 
ItemCheck(char *tok1, char *tok2, char *tok3, char *tok4, int aantal)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int id;
	char temp[1024], dude[1024];

	sprintf(dude, "select id from items where");

	switch (aantal) {
		/* noun */
	case 1:{
		sprintf(temp, "%s items.name='%s'", dude, tok1);
		break;
		}
		/* adjective */
	case 2:{
		sprintf(temp, "%s '%s' in (items.adject1, items.adject2, items.adject3) "
			"and items.name='%s'", dude, tok1, tok2);
		break;
		}
		/* adjective adjective noun */
	case 3:{
		sprintf(temp, "%s '%s' in (items.adject1, items.adject2, items.adject3) "
			"and '%s' in (items.adject1, items.adject2, items.adject3) "
			"and items.name='%s'", dude, tok1, tok2, tok3);
		break;
		}
		/* adjective adjective adjective noun */
	case 4:{
		sprintf(temp, "%s '%s' in (items.adject1, items.adject2, items.adject3) "
			"and '%s' in (items.adject1, items.adject2, items.adject3) "
			"and '%s' in (items.adject1, items.adject2, items.adject3) "
			"and items.name='%s'", dude, tok1, tok2, tok3, tok4);
		break;
		}		/* end4 */
	}			/* endswitch */
	res=SendSQL2(temp, NULL);

	row = mysql_fetch_row(res);
	
	if (row) {id=atoi(row[0]);} else {id=0;}

	mysql_free_result(res);
	return id;
}				/* endproc */

void 
Stats_Command(char *name, char *password)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	char logname[100];
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	sprintf(temp, "select * from tmp_usertable where name='%s'",name);
	res=SendSQL2(temp, NULL);

	row = mysql_fetch_row(res);
	
	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - Character Statistics\n");
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
	fprintf(cgiOut, "<Center><H1>Character Statistics</H1></Center>");
	fprintf(cgiOut, "<H2>Appearance:</H2>");
	fprintf(cgiOut, "A %s", row[8]); /*age*/
	if (strcmp(row[9], "none")) {fprintf(cgiOut, ", %s", row[9]);}
	if (strcmp(row[10], "none")) {fprintf(cgiOut, ", %s", row[10]);}
	if (strcmp(row[11], "none")) {fprintf(cgiOut, ", %s", row[11]);}
	if (strcmp(row[12], "none")) {fprintf(cgiOut, ", %s", row[12]);}
	if (strcmp(row[13], "none")) {fprintf(cgiOut, ", %s", row[13]);}
	if (strcmp(row[14], "none")) {fprintf(cgiOut, ", %s", row[14]);}
	if (strcmp(row[15], "none")) {fprintf(cgiOut, ", %s", row[15]);}
	if (strcmp(row[16], "none")) {fprintf(cgiOut, ", %s", row[16]);}
	if (strcmp(row[17], "none")) {fprintf(cgiOut, ", %s", row[17]);}/*leg*/

	fprintf(cgiOut, ", %s %s introduces %sself as %s, %s.<BR>\r\n(%s)<BR>", 
	row[7], row[6], HeShe2(row[7]), row[0], row[3], row[4]);/*sex, race*/

	fprintf(cgiOut, "<P><H2>Statistics:</H2>");
	fprintf(cgiOut, "%s, %s<BR>\r\nYou seem %s<BR>\r\nYou seem %s<BR>\r\n%s%s%s%s"
	,row[0], row[3], ShowString(atoi(row[29])/*vitals*/, atoi(row[52])/*maxvital*/),
	ShowMovement(atoi(row[49]), atoi(row[51])),
	 ShowDrink(atoi(row[32])), ShowEat(atoi(row[33])), ShowBurden(CheckWeight(name)), ShowAlignment(atoi(row[47])));
	fprintf(cgiOut, "You are level <B>%i</B>, <B>%i</B>, <B>%i</B> points away from levelling.<BR>\r\n",
	 atoi(row[24]) /*x.experience*/ / 1000, atoi(row[24]) % 1000, 1000 - (atoi(row[24]) % 1000));
	fprintf(cgiOut, "Strength <B>%s</B> Intelligence <B>%s</B> Dexterity <B>%s</B> Constitution <B>%s</B> Wisdom <B>%s</B>"
		" Mana <B>%i (<font color=blue>%i</Font>)</B> Movement <B>%i (<font color=blue>%i</font>)</B><BR>\r\n",
		row[39] /*strength*/, row[40], row[41], row[42], row[43], atoi(row[50])-atoi(row[48]), atoi(row[50]), 
		atoi(row[51])-atoi(row[49]), atoi(row[51]));
	fprintf(cgiOut, "You have <B>%s</B> training sessions and <B>%s</B> practice sessions left.<BR>\r\n",
		row[45], row[44]);
	if (atoi(row[23]) == 0) {
		fprintf(cgiOut, "You are not whimpy at all.<BR>\r\n");
	} else {
		fprintf(cgiOut, "You are whimpy if you seem %s.<BR>\r\n", ShowString(atoi(row[23]), atoi(row[52])));
	}
	if (atoi(row[26]) == 1) { /*sleep*/
		fprintf(cgiOut, "You are fast asleep.<BR>\r\n");
	}
	if (atoi(row[26]) >= 100) {
		fprintf(cgiOut, "You are sitting at a table.<BR>\r\n");
	}
	mysql_free_result(res);

	sprintf(temp, "select items.adject1, items.adject2, items.name,"
	" tmpitems.wearing, tmpitems.wielding from tmp_itemtable tmpitems, items where "
	"(belongsto='%s') and "
	"((wearing <> '') or "
	" (wielding <> '')) and "
	" (items.id = tmpitems.id)",name);
	res=SendSQL2(temp, NULL);
	while ((row = mysql_fetch_row(res))!=NULL)
	{
		if (row[3][0]!='\0')
		{
			fprintf(cgiOut, "You are wearing a %s, %s %s on your %s.<BR>\r\n",
			row[0], row[1], row[2], row[3]);
		}
		else
		{
			char position[20];
			if (row[4][0]=='1') {strcpy(position, "right hand");}
				else {strcpy(position, "left hand");}
			fprintf(cgiOut, "You are wielding a %s, %s %s in your %s.<BR>\r\n",
			row[0], row[1], row[2], position);
		}
		
	}
	mysql_free_result(res);
	fprintf(cgiOut, "<P>");

	sprintf(temp, "select concat('Your skill in ', name, ' is level ', "
	"skilllevel, '.<BR>\r\n') from skills, skilltable where skilltable.number="
	"skills.number and forwhom='%s'",name);
	res=SendSQL2(temp, NULL);

	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
		if (row==NULL)
		{
			fprintf(cgiOut, "You currently have no special skills.<BR>\r\n");
		}
		while (row!=NULL)
		{
			fprintf(cgiOut, "%s", row[0]);
			row = mysql_fetch_row(res);
		}
		mysql_free_result(res);
	}
	
	fprintf(cgiOut, "<P>");
	PrintForm(name, password);

	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
	fprintf(cgiOut, "</BODY></HTML>");
	KillGame();
}

void
GetMoney_Command(char *name, char *password, int room)
{
	/*
	* get [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	amount = strtol(tokens[1], &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
		/*get iron pick*/
		sprintf(sqlstring, 
		"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
		"where (items.name='coin') and "
		"(items.adject1='valuable') and "
		"(items.adject2='%s') and "
		"(items.adject3='shiny') and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '')"
		, tokens[numberfilledout+1],
		amount, room);
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Coins not found.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Coins not found.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

		/*get pick*/
		sprintf(sqlstring, 
			"update tmp_usertable set "
			"%s=%s+%i "
			"where (name='%s')"
			, itemadject2, itemadject2, amount, name);
		res=SendSQL2(sqlstring, &changedrows);
		mysql_free_result(res);

	if (amountitems>amount)
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-%i "
		"where (id = %i) and "
		"(room = %i)"
		, amount, itemid, room);
	}
	else
	{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id = %i) and "
		"(room = %i)"
		, itemid, room);
	}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You pick up a %s coin.<BR>\r\n", 
		itemadject2);
	WriteMessage(name, room, "%s picks up a %s coin.<BR>\r\n",
		name, itemadject2);
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You pick up %i %s coins.<BR>\r\n", 
		amount, itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s picks up %i %s coins.<BR>\r\n",
		name, amount, itemadject2);
	}
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
DropMoney_Command(char *name, char *password, int room)
{
	/*
	* drop [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* to be more specific:
	* drop [amount] copper coins
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, numberfilledout;
	int mycopper, mysilver, mygold, itemid;
	char *checkerror;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	/* look for specific person */
	sprintf(sqlstring, 
	"select copper, silver, gold from tmp_usertable where 
	(name = '%s')"
	, name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	mycopper = atoi(row[0]);
	mysilver = atoi(row[1]);
	mygold = atoi(row[2]);
	mysql_free_result(res);

	amount = strtol(tokens[1], &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}

	if (!strcmp(tokens[aantal-2],"copper"))
	{
		if (mycopper<amount)
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough copper coins.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		/* look for specific person */
		sprintf(sqlstring, 
			"update tmp_usertable set copper=copper-%i "
			" where (name = '%s')"
			,amount, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
		itemid=36;
	}
	if (!strcmp(tokens[aantal-2],"silver"))
	{
		if (mysilver<amount)
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough silver coins.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		/* look for specific person */
		sprintf(sqlstring, 
			"update tmp_usertable set silver=silver-%i "
			" where (name = '%s')"
			,amount, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
		itemid=37;
	}
	if (!strcmp(tokens[aantal-2],"gold"))
	{
		if (mygold<amount)
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough gold coins.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		/* look for specific person */
		sprintf(sqlstring, 
			"update tmp_usertable set gold=gold-%i "
			" where (name = '%s')"
			,amount, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
		itemid=38;
	}
	
		/*put pick*/
		sprintf(sqlstring, 
			"update tmp_itemtable set amount=amount+%i "
			"where (id=%i) and "
			"(room=%i)"
			, amount, itemid, room);
		res=SendSQL2(sqlstring, &changedrows);
		mysql_free_result(res);

		if (!changedrows) 
		{
			int changedrows2;
			sprintf(sqlstring, 
			"insert into tmp_itemtable values(%i,'','',%i,%i,'','')"
			, itemid, amount, room);
			res=SendSQL2(sqlstring, &changedrows2);

			if (!changedrows2)
			{
				WriteSentenceIntoOwnLogFile(logname, "Copper coins not found.<BR>\r\n");
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			mysql_free_result(res);
	}

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You drop a %s coin.<BR>\r\n", 
		tokens[aantal-2]);
	WriteMessage(name, room, "%s drops a %s coin.<BR>\r\n",
		name, tokens[aantal-2]);
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You drop %i %s coins.<BR>\r\n", 
		amount, tokens[aantal-2]);
	WriteMessage(name, room, "%s drops %i %s coins.<BR>\r\n",
		name, amount, tokens[aantal-2]);
	}
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
GiveMoney_Command(char *name, char *password, int room)
{
	/*
	* give [amount] <silver/gold/copper> coins to <person>
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100], toname[40];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout;
	int mygold, mysilver, mycopper;
	char *checkerror;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	/* look for specific person */
	sprintf(sqlstring, 
		"select name from tmp_usertable where (name = '%s') and "
		"(name <> '%s') and "
		"(room = %i)",tokens[aantal-1], name, room);
	res=SendSQL2(sqlstring, NULL);
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	strcpy(toname, row[0]);
	mysql_free_result(res);

	sprintf(sqlstring, 
		"select gold, silver, copper from tmp_usertable where (name = '%s')"
		,name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	mygold=atoi(row[0]);
	mysilver=atoi(row[1]);
	mycopper=atoi(row[2]);
	mysql_free_result(res);

	amount = strtol(tokens[1], &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}

	if (!strcmp(tokens[numberfilledout+1],"copper"))
	{
		if (amount>mycopper) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough copper coins.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	}
	if (!strcmp(tokens[numberfilledout+1],"silver"))
	{
		if (amount>mysilver) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough silver coins.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	}
	if (!strcmp(tokens[numberfilledout+1],"gold"))
	{
		if (amount>mygold) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough gold coins.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
	}

	/*give money to Karn*/
	sprintf(sqlstring, 
		"update tmp_usertable set %s=%s-%i "
		"where (name='%s')"
		, tokens[numberfilledout+1], tokens[numberfilledout+1], amount, name);
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);
	sprintf(sqlstring, 
		"update tmp_usertable set %s=%s+%i "
		"where (name='%s')"
		, tokens[numberfilledout+1], tokens[numberfilledout+1], amount, toname);
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You give a %s coin to %s.<BR>\r\n", 
		tokens[numberfilledout+1], toname);
	WriteMessageTo(toname, name, room, "%s gives a %s coin to %s.<BR>\r\n",
		name, tokens[numberfilledout+1], toname);
	WriteSayTo(toname, name, room, "%s gives a %s coin to you.<BR>\r\n",
		name, tokens[numberfilledout+1]);
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You give %i %s coins to %s.<BR>\r\n", 
		amount, tokens[numberfilledout+1] ,toname);
	WriteMessageTo(toname, name, room, "%s gives %i %s coins to %s.<BR>\r\n",
		name, amount, tokens[numberfilledout+1], toname);
	WriteSayTo(toname, name, room, "%s gives %i %s coins to you.<BR>\r\n",
		name, amount, tokens[numberfilledout+1]);
	}
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Get_Command(char *name, char *password, int room)
{
	/*
	* get [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	amount = strtol(tokens[1], &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
		if (aantal==2+numberfilledout) 
		{
			/*get pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='') and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(items.getable<>0)"
			, tokens[1+numberfilledout], amount, room);
		}
		if (aantal==3+numberfilledout) 
		{
			/*get iron pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='') and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(items.getable<>0)"
			,tokens[numberfilledout+2], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			amount, room);
		}
		if (aantal==4+numberfilledout) 
		{
			/*get iron strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='') and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(items.getable<>0)"
			,tokens[numberfilledout+3], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
			amount, room);
		}
		if (aantal==5+numberfilledout) 
		{
			/*get iron strong strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='') and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(items.getable<>0)"
			,tokens[numberfilledout+4], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
			tokens[numberfilledout+3], tokens[numberfilledout+3], tokens[numberfilledout+3],
			amount, room);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

		/*get pick*/
		sprintf(sqlstring, 
			"update tmp_itemtable set amount=amount+%i "
			"where (id=%i) and "
			"(belongsto='%s') and "
			"(wearing = '') and "
			"(wielding = '')"
			, amount, itemid, name);
		res=SendSQL2(sqlstring, &changedrows);
		mysql_free_result(res);

		if (!changedrows) 
		{
			int changedrows2;
			sprintf(sqlstring, 
			"insert into tmp_itemtable values(%i,'','%s',%i,0,'','')"
			, itemid, name, amount);
			res=SendSQL2(sqlstring, &changedrows2);

			if (!changedrows2)
			{
				WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			mysql_free_result(res);
	}

	if (amountitems>amount)
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-%i "
		"where (id = %i) and "
		"(room = %i)"
		, amount, itemid, room);
	}
	else
	{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id = %i) and "
		"(room = %i)"
		, itemid, room);
	}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You pick up a %s, %s %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s picks up a %s, %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname);
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You pick up %i %s, %s %ss.<BR>\r\n", 
		amount, itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s picks up %i %ss, %s %s.<BR>\r\n",
		name, amount, itemadject1, itemadject2, itemname);
	}
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Drop_Command(char *name, char *password, int room)
{
	/*
	* get [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	amount = strtol(tokens[1], &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
		if (aantal==2+numberfilledout) 
		{
			/*put pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			, tokens[1+numberfilledout], amount, name);
		}
		if (aantal==3+numberfilledout) 
		{
			/*get iron pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(items.dropable<>0)"
			,tokens[numberfilledout+2], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			amount, name);
		}
		if (aantal==4+numberfilledout) 
		{
			/*get iron strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(items.dropable<>0)"
			,tokens[numberfilledout+3], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
			amount, name);
		}
		if (aantal==5+numberfilledout) 
		{
			/*get iron strong strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(items.dropable<>0)"
			,tokens[numberfilledout+4], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
			tokens[numberfilledout+3], tokens[numberfilledout+3], tokens[numberfilledout+3],
			amount, name);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

		/*put pick*/
		sprintf(sqlstring, 
			"update tmp_itemtable set amount=amount+%i "
			"where (id=%i) and "
			"(room=%i)"
			, amount, itemid, room);
		res=SendSQL2(sqlstring, &changedrows);
		mysql_free_result(res);

		if (!changedrows) 
		{
			int changedrows2;
			sprintf(sqlstring, 
			"insert into tmp_itemtable values(%i,'','',%i,%i,'','')"
			, itemid, amount, room);
			res=SendSQL2(sqlstring, &changedrows2);

			if (!changedrows2)
			{
				WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			mysql_free_result(res);
	}

	if (amountitems>amount)
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-%i "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, amount, itemid, name);
	}
	else
	{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, itemid, name);
	}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You drop a %s, %s %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s drops a %s, %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname);
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You drop %i %s, %s %ss.<BR>\r\n", 
		amount, itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s drops %i %ss, %s %s.<BR>\r\n",
		name, amount, itemadject1, itemadject2, itemname);
	}
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Wear_Command(char *name, char *password, int room)
{
	/*
	* wear <item> on <position>; 
	* <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* <position> = {head, neck, body, lefthand, righthand, legs, feet}
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024], sqlcomposite[80];
	char logname[100], mysex[20];
	char itemname[40], itemadject1[40], itemadject2[40];
	int changedrows, itemid, amountitems, itemwearable;
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	itemwearable=-1;
	if (!strcmp(tokens[aantal-1],"lefthand")) {itemwearable=1;}
	if (!strcmp(tokens[aantal-1],"righthand")) {itemwearable=2;}
	if (!strcmp(tokens[aantal-1],"head")) {itemwearable=4;}
	if (!strcmp(tokens[aantal-1],"neck")) {itemwearable=7;}
	if (!strcmp(tokens[aantal-1],"head")) {itemwearable=8;}
	if (!strcmp(tokens[aantal-1],"body")) {itemwearable=9;}
	if (!strcmp(tokens[aantal-1],"legs")) {itemwearable=10;}
	if (!strcmp(tokens[aantal-1],"feet")) {itemwearable=11;}
	
	if ((itemwearable==1) || (itemwearable==2)) 
	{
		sprintf(sqlcomposite,"((items.wearable = %i) or (items.wearable = 3)) and ", itemwearable);
	}
	else
	{
		sprintf(sqlcomposite,"(items.wearable = %i) and ", itemwearable);
	}
	
	/* look for specific person */
	sprintf(sqlstring, 
		"select sex from tmp_usertable where (name = '%s')"
		, name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

		if (aantal==4) 
		{
			/*wear pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"%s"
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			, tokens[1], name, sqlcomposite);
		}
		if (aantal==5) 
		{
			/*get iron pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"%s"
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			,tokens[2], 
			tokens[1], tokens[1], tokens[1],
			name, sqlcomposite);
		}
		if (aantal==6) 
		{
			/*get iron strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"%s"
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			,tokens[3], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			name, sqlcomposite);
		}
		if (aantal==7) 
		{
			/*get iron strong strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"%s"
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			,tokens[4], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3],
			name, sqlcomposite);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You try to wear it, and fail.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You try to wear it, and fail.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

	if (amountitems>1)
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-1 "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, itemid, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
		sprintf(sqlstring, 
		"insert into tmp_itemtable values(%i, '', '%s', 1, 0, '%s', '')"
		, itemid, name, tokens[aantal-1]);
	}
	else
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set wearing='%s' "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wielding='') and "
		"(wearing='')"
		, tokens[aantal-1], itemid, name);
	}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You wear a %s, %s %s on your %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, tokens[aantal-1]);
	WriteMessage(name, room, "%s wears a %s, %s %s on %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, HeShe3(mysex), tokens[aantal-1]);
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Unwear_Command(char *name, char *password, int room)
{
	/*
	* wear <item> on <position>; 
	* <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* <position> = {head, neck, body, lefthand, righthand, legs, feet}
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024], sqlcomposite[80];
	char logname[100], mysex[20];
	char itemname[40], itemadject1[40], itemadject2[40], itemwearing[20];
	int changedrows, itemid, amountitems, itemwearable;
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	/* look for specific person */
	sprintf(sqlstring, 
		"select sex from tmp_usertable where (name = '%s')"
		, name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

		if (aantal==2) 
		{
			/*wear pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wearing from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing <> '') and "
			"(tmpitems.wielding = '')"
			, tokens[1], name );
		}
		if (aantal==3) 
		{
			/*get iron pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wearing from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing <> '') and "
			"(tmpitems.wielding = '')"
			,tokens[2], 
			tokens[1], tokens[1], tokens[1],
			name);
		}
		if (aantal==4) 
		{
			/*get iron strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wearing from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing <> '') and "
			"(tmpitems.wielding = '')"
			,tokens[3], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			name);
		}
		if (aantal==5) 
		{
			/*get iron strong strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wearing from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing <> '') and "
			"(tmpitems.wielding = '')"
			,tokens[4], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3],
			name);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You try to remove it, and fail.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You try to remove it, and fail.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		strcpy(itemwearing, row[5]);
		
		mysql_free_result(res);

		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount+1 "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, itemid, name);
		res=SendSQL2(sqlstring, &changedrows);
		mysql_free_result(res);
		if (!changedrows) 
		{
			sprintf(sqlstring, 
			"update tmp_itemtable set wearing='' "
			"where (id=%i) and "
			"(belongsto='%s') and "
			"(wearing='%s') and "
			"(wielding='')"
			, itemid, name, itemwearing);
		}
		else
		{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wielding='') and "
		"(wearing='%s')"
		, itemid, name, itemwearing);
		}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You remove a %s, %s %s from your %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, itemwearing);
	WriteMessage(name, room, "%s removes a %s, %s %s from %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, HeShe3(mysex), itemwearing);
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Wield_Command(char *name, char *password, int room)
{
	/*
	* wield <item>; 
	* <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* <position> = {left hand, right hand}
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100], mysex[20];
	char itemname[40], itemadject1[40], itemadject2[40], position2[20];
	int changedrows, itemid, amountitems, itemwearable, position;
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	position=1;strcpy(position2, "right hand");
	sprintf(sqlstring, 
	"select wielding from tmp_itemtable tmpitems "
	"where (tmpitems.belongsto='%s') and "
	"(tmpitems.room = 0) and "
	"(tmpitems.search = '') and "
	"(tmpitems.wearing = '') and "
	"(tmpitems.wielding <> '')"
	, name);
	res=SendSQL2(sqlstring, NULL);
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
		if (row!=NULL)
		{
			if (row[0][0]=='1') {position=2;strcpy(position2, "left hand");}
			if (row[0][1]=='2') {position=1;strcpy(position2, "right hand");}
			row = mysql_fetch_row(res);
			if (row!=NULL)
			{
				WriteSentenceIntoOwnLogFile(logname, "You are already wielding two items.<BR>\r\n");
				WriteRoom(name, password, room, 0);
				KillGame();
			}
		}
	}
	mysql_free_result(res);

	/* look for specific person */
	sprintf(sqlstring, 
		"select sex from tmp_usertable where (name = '%s')"
		, name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

		if (aantal==2) 
		{
			/*wear pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(items.wieldable = 3) and "
			"(tmpitems.wielding = '')"
			, tokens[1], name);
		}
		if (aantal==3) 
		{
			/*get iron pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(items.wieldable = 3) and "
			"(tmpitems.wielding = '')"
			,tokens[2], 
			tokens[1], tokens[1], tokens[1],
			name);
		}
		if (aantal==4) 
		{
			/*get iron strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(items.wieldable = 3) and "
			"(tmpitems.wielding = '')"
			,tokens[3], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			name);
		}
		if (aantal==5) 
		{
			/*get iron strong strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(items.wieldable = 3) and "
			"(tmpitems.wielding = '')"
			,tokens[4], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3],
			name);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have that item.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have that item.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

	if (amountitems>1)
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-1 "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, itemid, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
		sprintf(sqlstring, 
		"insert into tmp_itemtable values(%i, '', '%s', 1, 0, '', '%i')"
		, itemid, name, position);
	}
	else
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set wielding='%i' "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wielding='') and "
		"(wearing='')"
		, position, itemid, name);
	}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You wield a %s, %s %s in your %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, position2);
	WriteMessage(name, room, "%s wears a %s, %s %s in %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, HeShe3(mysex), position2);
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Unwield_Command(char *name, char *password, int room)
{
	/*
	* unwield <item>; 
	* <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* <position> = {left hand=2, right hand=1}
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024], sqlcomposite[80];
	char logname[100], mysex[20];
	char itemname[40], itemadject1[40], itemadject2[40], itemwielding[20], position[20];
	int changedrows, itemid, amountitems, itemwieldable;
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	/* look for specific person */
	sprintf(sqlstring, 
		"select sex from tmp_usertable where (name = '%s')"
		, name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

		if (aantal==2) 
		{
			/*wear pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wielding from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding <> '')"
			, tokens[1], name );
		}
		if (aantal==3) 
		{
			/*get iron pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wielding from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding <> '')"
			,tokens[2], 
			tokens[1], tokens[1], tokens[1],
			name);
		}
		if (aantal==4) 
		{
			/*get iron strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wielding from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding <> '')"
			,tokens[3], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			name);
		}
		if (aantal==5) 
		{
			/*get iron strong strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wielding from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding <> '')"
			,tokens[4], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3],
			name);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You fail to stop wielding it.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You fail to stop wielding it.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		strcpy(itemwielding, row[5]);
		
		mysql_free_result(res);

		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount+1 "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, itemid, name);
		res=SendSQL2(sqlstring, &changedrows);
		mysql_free_result(res);
		if (!changedrows) 
		{
			sprintf(sqlstring, 
			"update tmp_itemtable set wielding='' "
			"where (id=%i) and "
			"(belongsto='%s') and "
			"(wearing='') and "
			"(wielding='%s')"
			, itemid, name, itemwielding);
		}
		else
		{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wielding='%s') and "
		"(wearing='')"
		, itemid, name, itemwielding);
		}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	if (itemwielding[0]=='1') {strcpy(position, "right hand");}
		else {strcpy(position, "left hand");}
	WriteSentenceIntoOwnLogFile(logname, 
		"You stop wielding the %s, %s %s in your %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, position);
	WriteMessage(name, room, "%s stops wielding the %s, %s %s in %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, HeShe3(mysex), position);
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Eat_Command(char *name, char *password, int room)
{
	/*
	* eat <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;			
	char sqlstring[1024];
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int changedrows, itemid, amountitems, myeatstats;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	/* check drinkstats of specific person */
	sprintf(sqlstring, 
		"select eatstats from tmp_usertable where (name = '%s')"
		, name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	myeatstats=atoi(row[0]);
	mysql_free_result(res);
	if (myeatstats > 50) 
	{
			WriteSentenceIntoOwnLogFile(logname, "You are full, and cannot eat any more.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
	} /* too much to eat */
		if (aantal==2) 
		{
			/*eat pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.eatable from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(items.eatable <> '')"
			, tokens[1], name);
		}
		if (aantal==3) 
		{
			/*get iron pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.eatable from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.eatable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			,tokens[2], 
			tokens[1], tokens[1], tokens[1],
			name);
		}
		if (aantal==4) 
		{
			/*get iron strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.eatable from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.eatable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			,tokens[3], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			name);
		}
		if (aantal==5) 
		{
			/*get iron strong strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.eatable from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.eatable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			,tokens[4], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3],
			name);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You cannot eat that.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You cannot eat that.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		LookString(row[5], name, password);
		
		mysql_free_result(res);

	/* check eatstats of specific person */
	sprintf(sqlstring, 
		"update tmp_usertable set eatstats=eatstats+10 "
		"where (name = '%s')"
		, name);
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);
	if (amountitems>1)
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-1 "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, itemid, name);
	}
	else
	{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing = '') and "
		"(wielding = '')"
		, itemid, name);
	}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You eat a %s, %s %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s eats a %s, %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname);
	KillGame();
}

void
Drink_Command(char *name, char *password, int room)
{
	/*
	* get [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int changedrows, itemid, amountitems;
	int mydrinkstats;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	/* check drinkstats of specific person */
	sprintf(sqlstring, 
		"select drinkstats from tmp_usertable where (name = '%s')"
		, name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	mydrinkstats=atoi(row[0]);
	mysql_free_result(res);
	if (mydrinkstats >= 49) 
	{
			WriteSentenceIntoOwnLogFile(logname, "You have drunk your fill.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
	} /* too much to drink */
	if (mydrinkstats < -59) 
	{
			WriteSentenceIntoOwnLogFile(logname, "You are already dangerously intoxicated, "
			"and another drop might just possibly kill you.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
	} /* too much to drink spiritual like */
		if (aantal==2) 
		{
			/*put pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.drinkable from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.drinkable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			, tokens[1], name);
		}
		if (aantal==3) 
		{
			/*get iron pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.drinkable from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.drinkable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			,tokens[2], 
			tokens[1], tokens[1], tokens[1],
			name);
		}
		if (aantal==4) 
		{
			/*get iron strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.drinkable from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.drinkable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			,tokens[3], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			name);
		}
		if (aantal==5) 
		{
			/*get iron strong strong pick*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.drinkable from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.drinkable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			,tokens[4], 
			tokens[1], tokens[1], tokens[1],
			tokens[2], tokens[2], tokens[2],
			tokens[3], tokens[3], tokens[3],
			name);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You cannot drink that.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You cannot drink that.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
	LookString(row[5], name, password);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		mysql_free_result(res);

	if ((!strcmp(itemname,"beer")) ||
		(!strcmp(itemname,"ale")) ||
		(!strcmp(itemname,"whisky")) ||
		(!strcmp(itemname,"vodka"))) 
	{
		/* check drinkstats of specific person */
		sprintf(sqlstring, 
			"update tmp_usertable set drinkstats=drinkstats-10 where (name = '%s')"
			, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
	}
	else
	{
		/* check drinkstats of specific person */
		sprintf(sqlstring, 
			"update tmp_usertable set drinkstats=drinkstats+10 "
			"where (name = '%s')"
			, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
	}

	if (amountitems>1)
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-1 "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, itemid, name);
	}
	else
	{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, itemid, name);
	}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You drink a %s, %s %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s drinks a %s, %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname);
	KillGame();
}

void
RemapShoppingList_Command(char *name)
{
	/*
	* remaps the shoppinglist of a certain bot
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	int number=0;
	
	if (!strcmp(name, "Karcas")) {number=-49;}
	if (number==0) {return;}

	sprintf(sqlstring,"update items set readdescr='<H1>"
	"<IMG SRC=\\\"http://"ServerName"/images/gif/scroll.gif\\\">"
	"The List</H1><HR>Items:<UL>' where id=%i", number);
	SendSQL2(sqlstring, NULL);

	sprintf(sqlstring, 
	"select items.name, items.adject1, items.adject2, items.copper, items.silver, items.gold"
	" from items, tmp_itemtable tmpitems where "
	"(tmpitems.id=items.id) and "
	"(tmpitems.belongsto='%s')"
	, name);
	res=SendSQL2(sqlstring, NULL);
	while (row = mysql_fetch_row(res))
	{
		sprintf(sqlstring,"update items set readdescr=CONCAT(readdescr, "
		"'<LI>%s, %s %s (%s gold, %s silver, %s copper a piece)') "
		"where id=%i",row[1],row[2], row[0], row[5], row[4], row[3], number);
		SendSQL2(sqlstring, NULL);
	}
	mysql_free_result(res);

	sprintf(sqlstring, "update items set readdescr=CONCAT(readdescr, '</UL>"
	"<P>"
	"') where id=%i",number);
	SendSQL2(sqlstring, NULL);

}

void
Buy_Command(char *name, char *password, int room, char *fromname)
{
	/*
	* buy [amount] <item> to <person> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100], toname[40];
	char itemname[40], itemadject1[40], itemadject2[40];
	int mygold, mysilver, mycopper;
	int itemgold, itemsilver, itemcopper;
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	/* look for specific person */
	sprintf(sqlstring, 
		"select copper, silver, gold from tmp_usertable where (name = '%s')"
		, name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	mycopper = atoi(row[0]);
	mysilver = atoi(row[1]);
	mygold = atoi(row[2]);
	mysql_free_result(res);

	amount = strtol(tokens[1], &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
		if (aantal==2+numberfilledout) 
		{
			/*give pick to Karn*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
			"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s')"
			, tokens[1+numberfilledout], amount, fromname);
		}
		if (aantal==3+numberfilledout) 
		{
			/*give iron pick to Karn*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
			"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s')"
			,tokens[numberfilledout+2], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			amount, fromname);
		}
		if (aantal==4+numberfilledout) 
		{
			/*give iron strong pick to Karn*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
			"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s')"
			,tokens[numberfilledout+3], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
			amount, fromname);
		}
		if (aantal==5+numberfilledout) 
		{
			/*give iron strong pick to Karn*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
			"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s')"
			,tokens[numberfilledout+4], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
			tokens[numberfilledout+3], tokens[numberfilledout+3], tokens[numberfilledout+3],
			amount, fromname);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You fail to buy the item.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You fail to buy the item.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		itemcopper = atoi(row[5]);
		itemsilver = atoi(row[6]);
		itemgold = atoi(row[7]);
		
		if (!PayUp(amount*itemgold, amount*itemsilver, amount*itemcopper,
			&mygold, &mysilver, &mycopper))
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough money.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}

		/* look for specific person */
		sprintf(sqlstring, 
			"update tmp_usertable set copper=%i, silver=%i, gold=%i "
			" where (name = '%s')"
			,mycopper, mysilver, mygold, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
		
		if (!strcmp(fromname, "Karcas"))
		{
			sprintf(sqlstring, 
				"update tmp_itemtable set amount=amount-%i "
				"where (id=%i) and "
				"(belongsto='%s')"
				, amount, itemid, fromname);
			res=SendSQL2(sqlstring, NULL);
			mysql_free_result(res);
			sprintf(sqlstring, 
				"delete from tmp_itemtable where (amount=0) "
				"and (id=%i) and "
				"(belongsto='%s')"
				, itemid, fromname);
			res=SendSQL2(sqlstring, NULL);
			mysql_free_result(res);
		}

		/*give pick to Karn*/
		sprintf(sqlstring, 
			"update tmp_itemtable set amount=amount+%i "
			"where (id=%i) and "
			"(belongsto='%s')"
			, amount, itemid, name);
		res=SendSQL2(sqlstring, &changedrows);
		mysql_free_result(res);

		if (!changedrows) 
		{
			int changedrows2;
			sprintf(sqlstring, 
			"insert into tmp_itemtable values(%i,'','%s',%i,0,'','')"
			, itemid, name, amount);
			res=SendSQL2(sqlstring, &changedrows2);

			if (!changedrows2)
			{
				WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			mysql_free_result(res);
	}

	RemapShoppingList_Command(fromname);

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You buy a %s, %s %s from %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, fromname);
	WriteMessage(name, room, "%s buys a %s, %s %s from %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, fromname);
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You buy %i %s, %s %ss from %s.<BR>\r\n", 
		amount, itemadject1, itemadject2, itemname, fromname);
	WriteMessage(name, room, "%s buys %i %ss, %s %s from %s.<BR>\r\n",
		name, amount, itemadject1, itemadject2, itemname, fromname);
	}
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Sell_Command(char *name, char *password, int room, char *toname)
{
	/*
	* sell [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int mygold, mysilver, mycopper;
	int itemgold, itemsilver, itemcopper;
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	amount = strtol(tokens[1], &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
	if (aantal==2+numberfilledout) 
	{
		/*sell pick to Karn*/
		sprintf(sqlstring, 
		"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
		"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
		" where (items.name='%s') and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='%s') and "
		"(tmpitems.wearing='') and "
		"(tmpitems.wielding='')"
		, tokens[1+numberfilledout], amount, name);
	}
	if (aantal==3+numberfilledout) 
	{
		/*give iron pick to Karn*/
		sprintf(sqlstring, 
		"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
		"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
		"where (name='%s') and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='%s') and "
		"(tmpitems.wearing='') and "
		"(tmpitems.wielding='')"
		,tokens[numberfilledout+2], 
		tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
		amount, name);
	}
	if (aantal==4+numberfilledout) 
	{
		/*give iron strong pick to Karn*/
		sprintf(sqlstring, 
		"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
		"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
		"where (name='%s') and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='%s') and "
		"(tmpitems.wearing='') and "
		"(tmpitems.wielding='')"
		,tokens[numberfilledout+3], 
		tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
		tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
		amount, name);
	}
	if (aantal==5+numberfilledout) 
	{
		/*give iron strong pick to Karn*/
		sprintf(sqlstring, 
		"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
		"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
		" where (name='%s') and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='%s') and "
		"(tmpitems.wearing='') and "
		"(tmpitems.wielding='')"
		,tokens[numberfilledout+4], 
		tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
		tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
		tokens[numberfilledout+3], tokens[numberfilledout+3], tokens[numberfilledout+3],
		amount, name);
	}
	res=SendSQL2(sqlstring, &changedrows);
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "You fail to sell the item.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "You fail to sell the item.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	itemid = atoi(row[0]);
	amountitems = atoi(row[1]);
	strcpy(itemname, row[2]);
	strcpy(itemadject1, row[3]);
	strcpy(itemadject2, row[4]);
	itemcopper = atoi(row[5]);
	itemsilver = atoi(row[6]);
	itemgold = atoi(row[7]);
	
	sprintf(sqlstring, 
		"update tmp_usertable set copper=copper+%i, silver=silver+%i, gold=gold+%i "
		" where (name = '%s')"
		, amount*itemcopper, amount*itemsilver, amount*itemgold, name);
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);
	
	if (amount == amountitems)
	{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, itemid, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
	}
	else
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-%i "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, amount, itemid, name);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
	}

	sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount+%i "
		"where (id=%i) and "
		"(belongsto='%s') and "
		"(wearing='') and "
		"(wielding='')"
		, amount, itemid, toname);
	res=SendSQL2(sqlstring, &changedrows);
	mysql_free_result(res);

	if (!changedrows)
	{
		sprintf(sqlstring, 
		"insert into tmp_itemtable values(%i,'','%s',%i,0,'','')"
		, itemid, toname, amount);
		res=SendSQL2(sqlstring, NULL);
		mysql_free_result(res);
	}

	RemapShoppingList_Command(toname);

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You sell a %s, %s %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s sells a %s, %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname);
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You sell %i %s, %s %ss.<BR>\r\n", 
		amount, itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s sells %i %ss, %s %s.<BR>\r\n",
		name, amount, itemadject1, itemadject2, itemname);
	}
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Search_Command(char *name, char *password, int room)
{
	/*
	* search <object>
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	/*search pick*/
	sprintf(sqlstring, 
	"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
	" where "
	"(items.id=tmpitems.id) and "
	"(tmpitems.belongsto='') and "
	"(tmpitems.room = %i) and "
	"(tmpitems.search = '%s')"
	, room, troep+(tokens[1]-tokens[0]));
	res=SendSQL2(sqlstring, NULL);
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Object not found.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You search %s dilligently, yet find nothing at all.<BR>\r\n", troep+(tokens[1]-tokens[0]));
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

		/*search pick*/
		sprintf(sqlstring, 
			"update tmp_itemtable set amount=amount+1 "
			"where (id=%i) and "
			"(belongsto='%s') and "
			"(wearing = '') and "
			"(wielding = '')"
			, itemid, name);
		res=SendSQL2(sqlstring, &changedrows);
		mysql_free_result(res);

		if (!changedrows) 
		{
			int changedrows2;
			sprintf(sqlstring, 
			"insert into tmp_itemtable values(%i,'','%s',1,0,'','')"
			, itemid, name);
			res=SendSQL2(sqlstring, &changedrows2);

			if (!changedrows2)
			{
				WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			mysql_free_result(res);
	}

	if (amountitems>1)
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-1 "
		"where (id=%i) and "
		"(search='%s') and "
		"(room=%i)"
		, itemid, troep+(tokens[1]-tokens[0]), room);
	}
	else
	{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id=%i) and "
		"(search='%s') and "
		"(room=%i)"
		, itemid, troep+(tokens[1]-tokens[0]), room);
	}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You search %s and you find a %s, %s %s.<BR>\r\n", 
		troep+(tokens[1]-tokens[0]), itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s searches %s and finds a %s, %s %s.<BR>\r\n",
		name, troep+(tokens[1]-tokens[0]), itemadject1, itemadject2, itemname);
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Give_Command(char *name, char *password, int room)
{
	/*
	* give [amount] <item> to <person> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100], toname[40];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	/* look for specific person */
	sprintf(sqlstring, 
		"select name from tmp_usertable where (name = '%s') and "
		"(name <> '%s') and "
		"(room = %i)",tokens[aantal-1], name, room);
	res=SendSQL2(sqlstring, NULL);
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	strcpy(toname, row[0]);
	mysql_free_result(res);

	amount = strtol(tokens[1], &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
		if (aantal==4+numberfilledout) 
		{
			/*give pick to Karn*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (items.name='%s') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.wearing='') and "
			"(tmpitems.wielding='')"
			, tokens[1+numberfilledout], amount, name);
		}
		if (aantal==5+numberfilledout) 
		{
			/*give iron pick to Karn*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.wearing='') and "
			"(tmpitems.wielding='')"
			,tokens[numberfilledout+2], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			amount, name);
		}
		if (aantal==6+numberfilledout) 
		{
			/*give iron strong pick to Karn*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.wearing='') and "
			"(tmpitems.wielding='')"
			,tokens[numberfilledout+3], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
			amount, name);
		}
		if (aantal==7+numberfilledout) 
		{
			/*give iron strong pick to Karn*/
			sprintf(sqlstring, 
			"select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (name='%s') and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"( (adject1='%s') or "
			"  (adject2='%s') or "
			"  (adject3='%s') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%s') and "
			"(tmpitems.wearing='') and "
			"(tmpitems.wielding='')"
			,tokens[numberfilledout+4], 
			tokens[numberfilledout+1], tokens[numberfilledout+1], tokens[numberfilledout+1],
			tokens[numberfilledout+2], tokens[numberfilledout+2], tokens[numberfilledout+2],
			tokens[numberfilledout+3], tokens[numberfilledout+3], tokens[numberfilledout+3],
			amount, name);
		}
		res=SendSQL2(sqlstring, &changedrows);
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

		/*give pick to Karn*/
		sprintf(sqlstring, 
			"update tmp_itemtable set amount=amount+%i "
			"where (id=%i) and "
			"(belongsto='%s') and "
			"(wearing='') and "
			"(wielding='')"
			, amount, itemid, toname);
		res=SendSQL2(sqlstring, &changedrows);
		mysql_free_result(res);

		if (!changedrows) 
		{
			int changedrows2;
			sprintf(sqlstring, 
			"insert into tmp_itemtable values(%i,'','%s',%i,0,'','')"
			, itemid, toname, amount);
			res=SendSQL2(sqlstring, &changedrows2);

			if (!changedrows2)
			{
				WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
				WriteRoom(name, password, room, 0);
				KillGame();
			}
			mysql_free_result(res);
	}

	if (amountitems>amount)
	{
		sprintf(sqlstring, 
		"update tmp_itemtable set amount=amount-%i "
		"where (id=%i) and "
		"(belongsto='%s') and "
			"(wearing='') and "
			"(wielding='')"
		, amount, itemid, name);
	}
	else
	{
		sprintf(sqlstring, 
		"delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%s') and "
			"(wearing='') and "
			"(wielding='')"
		, itemid, name);
	}
	res=SendSQL2(sqlstring, NULL);
	mysql_free_result(res);

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You give a %s, %s %s to %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, toname);
	WriteMessageTo(toname, name, room, "%s gives a %s, %s %s to %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, toname);
	WriteSayTo(toname, name, room, "%s gives a %s, %s %s to you.<BR>\r\n",
		name, itemadject1, itemadject2, itemname);
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You give %i %s, %s %ss to %s.<BR>\r\n", 
		amount, itemadject1, itemadject2, itemname, toname);
	WriteMessageTo(toname, name, room, "%s gives %i %ss, %s %s to %s.<BR>\r\n",
		name, amount, itemadject1, itemadject2, itemname, toname);
	WriteSayTo(toname, name, room, "%s gives %i %s, %s %ss to you.<BR>\r\n",
		name, amount, itemadject1, itemadject2, itemname);
	}
	WriteRoom(name, password, room, 0);
	KillGame();
}

void
Read_Command(char *name, char *password, int room)
{
	/*
	* read <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];
	char logname[100], mysex[10];
	
	sprintf(logname, "%s%s.log", USERHeader, name);
	
	/* look for specific person */
	sprintf(sqlstring, 
	"select sex from tmp_usertable where (name = '%s')"
	, name);
	res=SendSQL2(sqlstring, NULL);
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);
	
	if (aantal==2) 
	{
		/*put pick*/
		sprintf(sqlstring, 
		"select items.name, items.adject1, items.adject2, items.readdescr from items, tmp_itemtable tmpitems "
		" where (items.name='%s') and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=0) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		, tokens[1], room);
	}
	if (aantal==3) 
	{
		/*get iron pick*/
		sprintf(sqlstring, 
		"select items.name, items.adject1, items.adject2, items.readdescr from items, tmp_itemtable tmpitems "
		"where (name='%s') and "
		"( (adject1='%s') or "
		"(adject2='%s') or "
		"(adject3='%s') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,tokens[2], 
		tokens[1], tokens[1], tokens[1],
		room);
	}
	if (aantal==4) 
	{
		/*get iron strong pick*/
		sprintf(sqlstring, 
		"select items.name, items.adject1, items.adject2, items.readdescr from items, tmp_itemtable tmpitems "
		"where (name='%s') and "
		"( (adject1='%s') or "
		"(adject2='%s') or "
		"(adject3='%s') ) and "
		"( (adject1='%s') or "
		"(adject2='%s') or "
		"(adject3='%s') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,tokens[3], 
		tokens[1], tokens[1], tokens[1],
		tokens[2], tokens[2], tokens[2],
		room);
	}
	if (aantal==5) 
	{
		/*get iron strong strong pick*/
		sprintf(sqlstring, 
		"select items.name, items.adject1, items.adject2, items.readdescr from items, tmp_itemtable tmpitems "
		" where (name='%s') and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,tokens[4], 
		tokens[1], tokens[1], tokens[1],
		tokens[2], tokens[2], tokens[2],
		tokens[3], tokens[3], tokens[3],
		room);
	}
	res=SendSQL2(sqlstring, NULL);
	if (res!=NULL) 
	{
		row = mysql_fetch_row(res);
		if (row!=NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, 
				"You read the %s, %s %s.<BR>\r\n", 
				row[1], row[2], row[0]);
			WriteMessage(name, room, "%s reads the %s, %s %s.<BR>\r\n",
				name, row[1], row[2], row[0]);
			LookString(row[3], name, password);
			KillGame();
		}
	}
	
	mysql_free_result(res);

	if (aantal==2) 
	{
		/*put pick*/
		sprintf(sqlstring, 
		"select items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding, items.readdescr from items, tmp_itemtable tmpitems "
		" where (items.name='%s') and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=0) and "
		"(tmpitems.belongsto='%s') and "
		"(tmpitems.room = 0) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		, tokens[1], name);
	}
	if (aantal==3) 
	{
		/*get iron pick*/
		sprintf(sqlstring, 
		"select items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding, items.readdescr from items, tmp_itemtable tmpitems "
		"where (name='%s') and "
		"( (adject1='%s') or "
		"(adject2='%s') or "
		"(adject3='%s') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='%s') and "
		"(tmpitems.room = 0) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,tokens[2], 
		tokens[1], tokens[1], tokens[1],
		name);
	}
	if (aantal==4) 
	{
		/*get iron strong pick*/
		sprintf(sqlstring, 
		"select items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding, items.readdescr from items, tmp_itemtable tmpitems "
		"where (name='%s') and "
		"( (adject1='%s') or "
		"(adject2='%s') or "
		"(adject3='%s') ) and "
		"( (adject1='%s') or "
		"(adject2='%s') or "
		"(adject3='%s') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='%s') and "
		"(tmpitems.room = 0) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,tokens[3], 
		tokens[1], tokens[1], tokens[1],
		tokens[2], tokens[2], tokens[2],
		name);
	}
	if (aantal==5) 
	{
		/*get iron strong strong pick*/
		sprintf(sqlstring, 
		"select items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding, items.readdescr from items, tmp_itemtable tmpitems "
		" where (name='%s') and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"( (adject1='%s') or "
		"  (adject2='%s') or "
		"  (adject3='%s') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='%s') and "
		"(tmpitems.room = 0) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,tokens[4], 
		tokens[1], tokens[1], tokens[1],
		tokens[2], tokens[2], tokens[2],
		tokens[3], tokens[3], tokens[3],
		name);
	}
	res=SendSQL2(sqlstring, NULL);
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
		WriteRoom(name, password, room, 0);
		KillGame();
	}
	
	mysql_free_result(res);

	if (row[3][0]!='\0') 
	{
		WriteSentenceIntoOwnLogFile(logname, 
			"You read the %s, %s %s, you are wearing.<BR>\r\n", 
			row[1], row[2], row[0]);
		WriteMessage(name, room, "%s reads the %s, %s %s, %s is wearing.<BR>\r\n",
			name, row[1], row[2], row[0], HeSheSmall(mysex));
	}
	else
	{
		if (row[4][0]!='\0')
		{
			WriteSentenceIntoOwnLogFile(logname, 
				"You read the %s, %s %s, you are wielding.<BR>\r\n", 
				row[1], row[2], row[0]);
			WriteMessage(name, room, "%s reads the %s, %s %s, %s is wielding.<BR>\r\n",
				name, row[1], row[2], row[0], HeSheSmall(mysex));
		}
		else
		{
			WriteSentenceIntoOwnLogFile(logname, 
				"You read the %s, %s %s, you are carrying.<BR>\r\n", 
				row[1], row[2], row[0]);
			WriteMessage(name, room, "%s reads the %s, %s %s, %s is carrying.<BR>\r\n",
				name, row[1], row[2], row[0], HeSheSmall(mysex));
		}
	}
	LookString(row[5], name, password);
	KillGame();
}

void 
Dead(char *name, char *password, int room)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	char logname[100];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	fprintf(cgiOut, "<HTML><HEAD><TITLE>Death</TITLE></HEAD>\n\n");
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
	fprintf(cgiOut, "You died.<Center><H1>The End</H1></Center><P>");
	fprintf(cgiOut, "You are dead. You are at the moment in a dark room. You can't see anything.");
	fprintf(cgiOut, "You hear sighs.<P>");
	fprintf(cgiOut, "A voice says (behind you): Oh no, not another one...<P>");
	fprintf(cgiOut, "Another voice sighs : They just keep on comin'.<P>");
	fprintf(cgiOut, "Suddenly, in the distance, you see a light which slowly comes closer. As it");
	fprintf(cgiOut, "comes closer, you see that it is a lantern which is being held by somebody");
	fprintf(cgiOut, "(or something you can't really make it out). That someone is getting closer.");
	fprintf(cgiOut, "When he is standing right before you, you have a good view of who he is. You");
	fprintf(cgiOut, "can't see his face, because that is hidden by a cap. He is all in black.<P>");
	fprintf(cgiOut, "You say: Hey, did somebody just die?<P>");
	fprintf(cgiOut, "He is carrying a heavy axe with him. This, you gather, must be Mr. Death");
	fprintf(cgiOut, "himself. He swings back his axe, and you hide your hands behind your face.");
	fprintf(cgiOut, "(That shouldn't however help much) Than the axe comes crushing down.<P>(Type");
	fprintf(cgiOut, "<B>look around</B>)<P>");
	
	sprintf(temp, "update tmp_usertable set vitals=0, sleep=0 where name='%s'",name);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);
	WriteMessage(name, room, "%s appears from nowhere.<BR>", name);
	PrintForm(name, password);
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
	KillGame();
}

void
ChangeTitle_Command(char *name, char *password, int room)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	char logname[100];
	
	sprintf(logname, "%s%s.log", USERHeader, name);

	WriteSentenceIntoOwnLogFile(logname, "Title changed to : %s<BR>\n", command+(tokens[1]-tokens[0]));
	sprintf(temp, "update tmp_usertable set title='%s' where name='%s'",
		command+(tokens[1]-tokens[0]),name);
	res=SendSQL2(temp, NULL);
	mysql_free_result(res);
	WriteRoom(name, password, room, 0);
	KillGame();
}				/* endproc */

