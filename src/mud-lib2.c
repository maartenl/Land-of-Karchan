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
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/
#include <time.h>
#include "typedefs.h"
#include "mud-lib2.h"

/*! \file mud-lib2.c
	\brief  server file providing extentions and commands to the mud */

//! write mail to another person
/*! \param name char*, name of sender
	\param toname char*, name of receiver
	\param header char*, title
	\param message char*, contents of email
*/
void 
WriteMail(char *name, char *toname, char *header, char *message)
{
	int             i = 1,j;
	FILE           *fp;
	struct tm       datumtijd;
	time_t   datetime;
MYSQL_RES *res;
MYSQL_ROW row;
char *temp;

//'9999-12-31 23:59:59'

	time(&datetime);
	datumtijd = *(gmtime(&datetime));

temp = composeSqlStatement("INSERT INTO %s VALUES ('%x', '%x', '%x', "
	"'%i-%i-%i %i:%i:%i',0,1, '%x')", 
	"tmp_mailtable", name, toname, header,
	datumtijd.tm_year+1900, datumtijd.tm_mon+1, datumtijd.tm_mday,
	datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec,
	 message);
res=sendQuery(temp, NULL);
free(temp);temp=NULL;
mysql_free_result(res);
temp = composeSqlStatement("INSERT INTO %s VALUES ('%x', '%x', '%x', "
	"'%i-%i-%i %i:%i:%i',0,1, '%x')", 
	"mailtable", name, toname, header,
	datumtijd.tm_year+1900, datumtijd.tm_mon+1, datumtijd.tm_mday,
	datumtijd.tm_hour, datumtijd.tm_min, datumtijd.tm_sec,
	message);
res=sendQuery(temp, NULL);
free(temp);temp=NULL;
	mysql_free_result(res);

}

//! list of mails received by the current player
int 
ListMail_Command(mudpersonstruct *fmudstruct)
{
	int             i = 1,j;
	FILE           *fp;
	char				logname[100];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

  	send_printf(fmudstruct->socketfd, "<HTML>\n");
	send_printf(fmudstruct->socketfd, "<HEAD>\n");
	send_printf(fmudstruct->socketfd, "<TITLE>\n");
	send_printf(fmudstruct->socketfd, "Land of Karchan\n");
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
	send_printf(fmudstruct->socketfd, "<H2>List of Mail</H2>");

temp = composeSqlStatement("SELECT name, haveread, newmail, header FROM tmp_mailtable"
	" WHERE toname='%x' ORDER BY whensent ASC", name);
res=sendQuery(temp, NULL);
free(temp);temp=NULL;

send_printf(fmudstruct->socketfd, "<TABLE BORDER=0 VALIGN=top>\r\n");j=1;
while(row = mysql_fetch_row(res)) 
{
	send_printf(fmudstruct->socketfd, "<TR VALIGN=top><TD>%i.</TD><TD>", j);
	if (atoi(row[2])>0) {send_printf(fmudstruct->socketfd,"N");}
	if (atoi(row[1])==0) {send_printf(fmudstruct->socketfd,"U");}
	send_printf(fmudstruct->socketfd,"</TD><TD><B>From: </B>%s</TD><TD><B>Header: </B>"
	"<A HREF=\"%s?command=readmail+%i&name=%s&password=%s&frames=%i\">"
	"%s</A></TD><TD><A HREF=\"%s?command=deletemail+%i&name=%s&password=%s&frames=%i\">Delete</A></TD>"
	"</TR>\r\n",row[0], getParam(MM_MUDCGI), j, name, password, fmudstruct->frames+1, row[3], getParam(MM_MUDCGI), j, name, password, fmudstruct->frames+1);
	j++;
}
 send_printf(fmudstruct->socketfd, "</TABLE><BR>\r\n");
	mysql_free_result(res);

	PrintForm(name, password, fmudstruct->frames, fmudstruct->socketfd);
	send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P>");
}

//! read or delete mail (yeah, I know, confusing)
/*! \param messnr integer containing the number of the message to read/delete
	\param erasehem integer, 1 if needs to be deleted, 0 otherwise, message is always displayed
	\param int 1 on success, 0 on failure. If 0, WriteRoom needs to be called in calling method.
*/
int 
ReadMail(char *name, char *password, int room, int frames, int messnr, int erasehem, int socketfd)
{
	int             i = 1,j;
	FILE           *fp;
	char logname[100];
MYSQL_RES *res;
MYSQL_ROW row;
char *temp;
char mailname[40], mailtoname[40], maildatetime[40];

sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

temp = composeSqlStatement("SELECT count(*) FROM tmp_mailtable"
	" WHERE toname='%x' ", name);
res=sendQuery(temp, NULL);
free(temp);temp=NULL;
if (res != NULL)
{
	row = mysql_fetch_row(res);
	if (row != NULL)
	{
		if ( (atoi(row[0])<messnr) || (messnr < 1))
		{
			mysql_free_result(res);
			WriteSentenceIntoOwnLogFile(logname, "No mail with that number!<BR>\r\n");
			return 0;
		}
	}
	else
	{
		mysql_free_result(res);
		WriteSentenceIntoOwnLogFile(logname, "No mail with that number!<BR>\r\n");
		return 0;
	}
	mysql_free_result(res);
}
else
{
	WriteSentenceIntoOwnLogFile(logname, "No mail with that number!<BR>\r\n");
	return 0;
}

temp = composeSqlStatement("SELECT * FROM tmp_mailtable"
	" WHERE toname='%x' ORDER BY whensent ASC", name);
res=sendQuery(temp, NULL);
free(temp);temp=NULL;
if (res==NULL)
{
	WriteSentenceIntoOwnLogFile(logname, "No mail with that number!<BR>\r\n");
	return 0;
}
j=1;
while((row = mysql_fetch_row(res)) && (messnr!=j)) {j++;}
if (messnr!=j) 
{
	mysql_free_result(res);
	WriteSentenceIntoOwnLogFile(logname, "No mail with that number!<BR>\r\n");
	return 0;
}


strcpy(mailname, row[0]);
strcpy(mailtoname, row[1]);
strcpy(maildatetime, row[3]);
	
send_printf(socketfd, "<HTML>\n");
send_printf(socketfd, "<HEAD>\n");
send_printf(socketfd, "<TITLE>\n");
send_printf(socketfd, "Land of Karchan\n");
send_printf(socketfd, "</TITLE>\n");
send_printf(socketfd, "</HEAD>\n");

send_printf(socketfd, "<BODY>\n");
	if (!frames)
	{
		send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (frames==1)
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
send_printf(socketfd, "<H1>Read Mail - %s</H1>", row[2]);
send_printf(socketfd, "<HR noshade><TABLE BORDER=0>\r\n");
send_printf(socketfd, "<TR><TD>Mes. Nr:</TD><TD> <B>%i</B></TD></TR>\r\n", messnr);
send_printf(socketfd, "<TR><TD>From:</TD><TD><B>%s</B></TD></TR>\r\n", row[0]);
send_printf(socketfd, "<TR><TD>Time:</TD><TD><B>%s</B></TD></TR>\r\n", row[3]+11);
*(row[3]+10)='\0';
send_printf(socketfd, "<TR><TD>Date:</TD><TD><B>%s</B></TD></TR>\r\n", row[3]);
send_printf(socketfd, "<TR><TD>New?:</TD><TD><B>");
if (atoi(row[5])>0) {send_printf(socketfd, "Yes</B></TD></TR>\r\n");}
		else {send_printf(socketfd, "No</B></TD></TR>\r\n");}
send_printf(socketfd, "<TR><TD>Read?:</TD><TD><B>");
if (atoi(row[4])>0) {send_printf(socketfd, "Yes</B></TD></TR>\r\n");}
		else {send_printf(socketfd, "No</B></TD></TR>\r\n");}
send_printf(socketfd, "<TR><TD>Header:</TD><TD><B>%s</B></TABLE>\r\n", row[2]);
send_printf(socketfd, "<HR noshade>%s", row[6]);
send_printf(socketfd, "<HR noshade>");
send_printf(socketfd,"<A HREF=\"%s?command=listmail&name=%s&password=%s&frames=%i\">ListMail</A><P>",
	getParam(MM_MUDCGI), name, password, frames+1);
PrintForm(name, password, frames, socketfd);
send_printf(socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
send_printf(socketfd, "<DIV ALIGN=left><P>");

mysql_free_result(res);

if (erasehem) 
{
	temp = composeSqlStatement("DELETE FROM %s WHERE name='%x' and toname='%x' and whensent='%x' ",
	"tmp_mailtable", mailname, mailtoname, maildatetime);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);
	temp = composeSqlStatement("DELETE FROM %s WHERE name='%x' and toname='%x' and whensent='%x' ",
	"mailtable", mailname, mailtoname, maildatetime);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);
} 
else 
{
	temp = composeSqlStatement("UPDATE %s SET haveread=1 WHERE name='%x' and toname='%x' and whensent='%x' ",
	"tmp_mailtable", mailname, mailtoname, maildatetime);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);
	temp = composeSqlStatement("UPDATE %s SET haveread=1 WHERE name='%x' and toname='%x' and whensent='%x' ",
	"mailtable", mailname, mailtoname, maildatetime);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);
	}
return 1;
}

//! get some answers from bots, for example Bill but is not only Bill
/*! answer if found, otherwise bot ignores you and message asked is added to the log
*/
int 
ReadBill(char *botname, char *vraag, char *name, int room)
{
	FILE *fp;
	char logname[100];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	temp = composeSqlStatement("select god from tmp_usertable where name = \"%s\" ", botname);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
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

	temp = composeSqlStatement("select answer from answers where \"%x\" like question and "
			"name = \"%x\" ", vraag, botname);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;

	if (row = mysql_fetch_row(res)) {
	WriteSentenceIntoOwnLogFile(logname, "%s says [to you]: %s<BR>\r\n", botname, row[0]);
	WriteMessage(name, room, "%s says [to %s]: %s<BR>\r\n", botname, name, row[0]);
	} else {
	WriteSentenceIntoOwnLogFile(logname, "%s ignores you.<BR>\r\n", botname);
	WriteMessage(name, room, "%s ignores %s.<BR>\r\n", botname, name);
	fp = fopen(getParam(MM_ERRORFILE), "a");
	fprintf(fp, "No response: %s says [to %s]: %s\n", name, botname, vraag);
	fclose(fp);
	}
	mysql_free_result(res);
	return 0;
}
	

//! display who is online excluding deps
/*! shows a bulleted list of people currently online, with their idle times and wether or not they are asleep.
	Deputies are excluded because they usually have an idle time that would curdle milk. */
int 
Who_Command(mudpersonstruct *fmudstruct)
{
	int				i = 0;
	FILE			*fp;
	MYSQL_RES 		*res;
	MYSQL_ROW		row;
	char			*tempsql;
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
		
	send_printf(fmudstruct->socketfd, "<HTML>\n");
	send_printf(fmudstruct->socketfd, "<HEAD>\n");
	send_printf(fmudstruct->socketfd, "<TITLE>\n");
	send_printf(fmudstruct->socketfd, "Land of Karchan - Who\n");
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
	send_printf(fmudstruct->socketfd, "<H2>List of All Users</H2>");
	tempsql = composeSqlStatement("select count(*) from tmp_usertable where god<1");
	res=sendQuery(tempsql, NULL);
	free(tempsql);tempsql=NULL;
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
		send_printf(fmudstruct->socketfd, "<I>There are %s persons active in the game.</I><P>", 
			row[0]); 
	}
	mysql_free_result(res);
		
	send_printf(fmudstruct->socketfd, "<UL>");
	tempsql = composeSqlStatement("select name, title, "
	"time_to_sec(date_sub(NOW(), INTERVAL 2 HOUR))-time_to_sec(lastlogin)"
	", sleep from tmp_usertable "
	"where god<1");
	res=sendQuery(tempsql, NULL);
	free(tempsql);tempsql=NULL;
	if (res!=NULL)
	{
		while ((row = mysql_fetch_row(res)))
		{
			if (atoi(row[3])==1)
			{
				send_printf(fmudstruct->socketfd, "<LI>%s, %s, sleeping (%i min, %i sec idle)\r\n", 
				row[0], row[1], atoi(row[2]) / 60, atoi(row[2]) % 60);
			}
			else
			{
				send_printf(fmudstruct->socketfd, "<LI>%s, %s (%i min, %i sec idle)\r\n", 
				row[0], row[1], atoi(row[2]) / 60, atoi(row[2]) % 60);
			}
		}
	}
	mysql_free_result(res);
	
	send_printf(fmudstruct->socketfd, "</UL>");

	PrintForm(name, password, fmudstruct->frames, fmudstruct->socketfd);
	send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P>");
	return 1;
}

//! dispay a description
/*! handy for displaying random bits of text.
	\param description char* that contains the (large) amount of text to be displayed
*/
void 
LookString(char *description, char *name, char *password, int frames, int socketfd)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	send_printf(socketfd, "<HTML>\n");
	send_printf(socketfd, "<HEAD>\n");
	send_printf(socketfd, "<TITLE>\n");
	send_printf(socketfd, "Land of Karchan\n");
	send_printf(socketfd, "</TITLE>\n");
	send_printf(socketfd, "</HEAD>\n");

	send_printf(socketfd, "<BODY>\n");
	if (!frames)
	{
		send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (frames==1)
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}

	send_printf(socketfd, "%s", description);
	PrintForm(name, password, frames, socketfd);
	if (frames!=2) {ReadFile(logname, socketfd);}
	send_printf(socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(socketfd, "<DIV ALIGN=left><P>");
}

//! displays the description of an item properly
/*! \param id int, contains the id number of the item of which the description needs to be displayed
*/
void 
LookAtProc(int id, mudpersonstruct *fmudstruct)
{
	char 		logname[100];

	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	char *name;
	char *password;
	int frames;
	int socketfd;
	name = fmudstruct->name;
	password = fmudstruct->password;
	frames = fmudstruct->frames;
	socketfd = fmudstruct->socketfd;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	send_printf(socketfd, "<HTML>\n");
	send_printf(socketfd, "<HEAD>\n");
	send_printf(socketfd, "<TITLE>\n");
	send_printf(socketfd, "Land of Karchan\n");
	send_printf(socketfd, "</TITLE>\n");
	send_printf(socketfd, "</HEAD>\n");

	send_printf(socketfd, "<BODY>\n");
	if (!frames)
	{
		send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (frames==1)
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}

	temp = composeSqlStatement("select description from items where id=%i",	id);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			send_printf(socketfd, "%s", row[0]);
		}
		else
		{
			send_printf(socketfd, "[item description not found]");
		}
	}
	mysql_free_result(res);

	PrintForm(name, password, frames, socketfd);
	if (frames!=2) {ReadFile(logname, socketfd);}
	send_printf(socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(socketfd, "<DIV ALIGN=left><P>");
}

//! look at item, either in inventory or on floor or hidden or at a person in the room
/*! incredibly comprehensive look command, will check on the following first:
<UL><LI>items
<LI>persons</UL>
Format can be the following
<UL><LI>look at [&lt;adjective&gt; [&lt;adjective&gt; [&lt;adjective&gt;]]] &lt;name&gt;
<LI>look at &lt;person&gt;
</UL>In case of an item, the description is put onto the screen.
In case of a person, description + health + wearing is put onto the screen.
*/
void 
LookItem_Command(mudpersonstruct *fmudstruct)
{
	char 		logname[100];
	char *name;
	char *password;
	int room;
	int frames;
	
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	int i, containerid = 0;
	
	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	frames = fmudstruct->frames;

	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	if ((getTokenAmount(fmudstruct)<3) || (getTokenAmount(fmudstruct)>6))
	{
		WriteSentenceIntoOwnLogFile(logname, "You see nothing special.<BR>\r\n");
		WriteRoom(fmudstruct);
		return;
	}
	if (getTokenAmount(fmudstruct)==3)
	{
		temp = composeSqlStatement("select items.description, items.name, items.adject1, items.adject2, "
			"items.container, tmpitems.containerid from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '') and "
			"(items.name = '%x')",room, getToken(fmudstruct, 2));
	}
	if (getTokenAmount(fmudstruct)==4)
	{
		temp = composeSqlStatement("select items.description, items.name, items.adject1, items.adject2, "
			"items.container, tmpitems.containerid from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '') and "
			"(items.name = '%x') and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') )",
			room, getToken(fmudstruct, 3), getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2));
	}
	if (getTokenAmount(fmudstruct)==5)
	{
		temp = composeSqlStatement("select items.description, items.name, items.adject1, items.adject2, "
			"items.container, tmpitems.containerid from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '') and "
			"(items.name = '%x') and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') ) and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') )",
			room, getToken(fmudstruct, 4), getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3));
	}
	if (getTokenAmount(fmudstruct)==6)
	{
		temp = composeSqlStatement("select items.description, items.name, items.adject1, items.adject2, "
			"items.container, tmpitems.containerid from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '') and "
			"(items.name = '%x') and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') ) and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') ) and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') )",
			room, getToken(fmudstruct, 5), getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
			getToken(fmudstruct, 4), getToken(fmudstruct, 4), getToken(fmudstruct, 4));
	}
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);

		if (row!=NULL)
		{
			WriteSentenceIntoOwnLogFile(logname, "You look carefully at the %s %s %s.<BR>\r\n",
				row[2], row[3], row[1]);
	
			send_printf(fmudstruct->socketfd, "<HTML>\n");
			send_printf(fmudstruct->socketfd, "<HEAD>\n");
			send_printf(fmudstruct->socketfd, "<TITLE>\n");
			send_printf(fmudstruct->socketfd, "Land of Karchan\n");
			send_printf(fmudstruct->socketfd, "</TITLE>\n");
			send_printf(fmudstruct->socketfd, "</HEAD>\n");
		
			send_printf(fmudstruct->socketfd, "<BODY>\n");
	if (!frames)
	{
		send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (frames==1)
		{
			send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
		
			send_printf(fmudstruct->socketfd, "%s", row[0]);
			if (atoi(row[4])!=0)
			{
				containerid = atoi(row[5]);
				if (containerid!=0) 
				{
					send_printf(fmudstruct->socketfd, "The %s %s %s seems to contain ", row[2], row[3], row[1]);
				}
				else
				{
					send_printf(fmudstruct->socketfd, "The %s %s %s is empty.<P>\r\n", row[2], row[3], row[1]);
				}
				
			}
			mysql_free_result(res);

			if (containerid)
			{
				temp = composeSqlStatement("select tmpitems.amount, items.name, items.adject1, items.adject2 "
					" from items, "
					"containeditems tmpitems where "
					"items.id = tmpitems.id and "
					"tmpitems.containedin = %i",
					containerid);
				res=sendQuery(temp, NULL);
				free(temp);temp=NULL;
				if (res!=NULL)
				{
					int firsttime=1;
					while ((row = mysql_fetch_row(res))!=NULL) 
					{
						int amount = atoi(row[0]);
						if (firsttime)
						{
							firsttime=0;
						}
						else
						{
							send_printf(fmudstruct->socketfd, ", ");
						}
						if (amount>1)
						{
							send_printf(fmudstruct->socketfd, "%i %s %s %ss", amount, row[2], row[3], row[1]);
						}
						else
						{
							send_printf(fmudstruct->socketfd, "a %s %s %s", row[2], row[3], row[1]);
						}
					}
					send_printf(fmudstruct->socketfd, ".<P>\r\n");
					mysql_free_result(res);
				}
			}

			PrintForm(name, password, frames, fmudstruct->socketfd);
			if (frames!=2) {ReadFile(logname, fmudstruct->socketfd);}
			send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
			send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P>");
			return ;
		}
		mysql_free_result(res);
	}
	else
	{
		mysql_free_result(res);

	}

	if (getTokenAmount(fmudstruct)==3)
	{
		temp = composeSqlStatement("select items.description, items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '%x') and "
			"(items.name = '%x')",name, getToken(fmudstruct, 2));
	}
	if (getTokenAmount(fmudstruct)==4)
	{
		temp = composeSqlStatement("select items.description, items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '%x') and "
			"(items.name = '%x') and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') )",
			name, getToken(fmudstruct, 3), getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2));
	}
	if (getTokenAmount(fmudstruct)==5)
	{
		temp = composeSqlStatement("select items.description, items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '%x') and "
			"(items.name = '%x') and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') ) and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') )",
			name, getToken(fmudstruct, 4), getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3));
	}
	if (getTokenAmount(fmudstruct)==6)
	{
		temp = composeSqlStatement("select items.description, items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding from items, "
			"tmp_itemtable tmpitems where "
			"(items.id = tmpitems.id) and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.belongsto = '%x') and "
			"(items.name = '%x') and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') ) and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') ) and "
			"( (items.adject1='%x') or "
			"  (items.adject2='%x') or "
			"  (items.adject3='%x') )",
			name, getToken(fmudstruct, 5), getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
			getToken(fmudstruct, 4), getToken(fmudstruct, 4), getToken(fmudstruct, 4));
	}
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
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
	
			send_printf(fmudstruct->socketfd, "<HTML>\n");
			send_printf(fmudstruct->socketfd, "<HEAD>\n");
			send_printf(fmudstruct->socketfd, "<TITLE>\n");
			send_printf(fmudstruct->socketfd, "Land of Karchan\n");
			send_printf(fmudstruct->socketfd, "</TITLE>\n");
			send_printf(fmudstruct->socketfd, "</HEAD>\n");
		
			send_printf(fmudstruct->socketfd, "<BODY>\n");
	if (!frames)
	{
		send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (frames==1)
		{
			send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(fmudstruct->socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
		
			send_printf(fmudstruct->socketfd, "%s", row[0]);
			PrintForm(name, password, frames, fmudstruct->socketfd);
			if (frames!=2) {ReadFile(logname, fmudstruct->socketfd);}
			send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
			send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P>");
			mysql_free_result(res);
			return ;
		}
		mysql_free_result(res);

	}
	else
	{
		mysql_free_result(res);
	}

	if (getTokenAmount(fmudstruct)==3)
	{
		char *extralook;
		extralook = NULL;
		temp = composeSqlStatement("select * from tmp_attributes "
			"where name='look' and "
			"objectid='%x' and "
			"objecttype=1",
			getToken(fmudstruct, 2));
		res=sendQuery(temp, NULL);
		free(temp);temp=NULL;
		if (res!=NULL)
		{
			row = mysql_fetch_row(res);
			if (row != NULL)
			{
				extralook = (char *)malloc(strlen(row[1])+2);
				strcpy(extralook, row[1]);
			}
			mysql_free_result(res);
		}
		
		temp = composeSqlStatement("select * from tmp_usertable "
			"where (room = %i) and "
			"(tmp_usertable.name<>'%x') and "
			"(tmp_usertable.name='%x')",
			room, name, getToken(fmudstruct, 2));
		res=sendQuery(temp, NULL);
		free(temp);temp=NULL;
		if (res!=NULL)
		{
			row = mysql_fetch_row(res);
	
			if (row!=NULL)
			{
				int i;
				WriteSentenceIntoOwnLogFile(logname, "You look carefully at the %s, ", row[8]);
				if (strcasecmp(row[9], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[9]);}
				if (strcasecmp(row[10], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[10]);}
				if (strcasecmp(row[11], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[11]);}
				if (strcasecmp(row[12], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[12]);}
				if (strcasecmp(row[13], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[13]);}
				if (strcasecmp(row[14], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[14]);}
				if (strcasecmp(row[15], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[15]);}
				if (strcasecmp(row[16], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[16]);}
				if (strcasecmp(row[17], "none")) {WriteSentenceIntoOwnLogFile(logname, "%s, ", row[17]);}
				WriteSentenceIntoOwnLogFile(logname, "%s %s", row[7], row[6]);
				WriteSentenceIntoOwnLogFile(logname, " who calls %sself \r\n"
					"%s (%s).<BR>\r\n", /*sex*/ HeShe2(row[7]), row[0], row[3]);
				temp = composeSqlStatement("select '%x', '%x', items.adject1, items.adject2, items.name,"
				" tmpitems.wearing, tmpitems.wielding from tmp_itemtable tmpitems, items where "
				"(belongsto='%x') and "
				"((wearing <> '') or "
				" (wielding <> '')) and "
				" (containerid = 0) and "
				" (items.id = tmpitems.id)",row[0], row[7], row[0]);
				WriteSentenceIntoOwnLogFile(logname, "%s seems %s.<BR>\r\n", HeShe(row[7]), ShowString(atoi(row[29]), atoi(row[52])));
				if (extralook != NULL)
				{
					WriteSentenceIntoOwnLogFile(logname, "%s is %s<BR>\r\n", HeShe(row[7]), extralook);
					free(extralook);
				}
				if (!strcasecmp(row[26],"1")) 
				{
					WriteSentenceIntoOwnLogFile(logname, "%s is fast asleep.<BR>\r\n", row[0]);
				}
				WriteMessageTo(row[0], name, room, "%s is looking at %s.<BR>\r\n",name, row[0]);
				WriteSayTo(row[0], name, room, "You notice %s looking at you.<BR>", name);
				res=sendQuery(temp, &i);
				free(temp);temp=NULL;
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

				WriteRoom(fmudstruct);
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
	WriteRoom(fmudstruct);
}

//! throw standard error page to user regarding not being active.
void 
NotActive(char *fname, char *fpassword, int errornr, int socketfd)
{
	time_t tijd;
	struct tm datum;
	        
	send_printf(socketfd, "<HTML><HEAD><TITLE>Error</TITLE></HEAD>\n\n");
	send_printf(socketfd, "<BODY>\n");
	send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>You are no longer active</H1><HR>\n");
	send_printf(socketfd, "You cannot MUD because you are not logged in (no more)."
	    " This might have happened to the following circumstances:<P>");
	send_printf(socketfd, "<UL><LI>you were kicked out of the game for bad conduct");
	send_printf(socketfd, "<LI>the game went down for dayly cleanup, killing of all"
		"active users");
	send_printf(socketfd, "<LI>you were deactivated for not responding for over 1 hour");
	send_printf(socketfd, "<LI>an error occurred</UL>");
	send_printf(socketfd, "You should be able to relogin by using the usual link below:<P>");
	send_printf(socketfd, "<A HREF=\"http://%s/karchan/enter.html\">Click here to\n", getParam(MM_SERVERNAME));
	send_printf(socketfd, "relogin</A><P>\n");
	send_printf(socketfd, "</body>\n");
	send_printf(socketfd, "</HTML>\n");
	
	time(&tijd);
	datum=*(gmtime(&tijd));
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),"%i:%i:%i %i-%i-%i NotActive by %s (%s) (error %i)<BR>\n",datum.tm_hour,
	datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,fname, fpassword, errornr);
}

//! quit the game
/*! deactivate and save player, then exit player, and show goodbye message found in goodbye.html
*/
int 
Quit_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	struct tm       datumtijd;
	time_t   datetime;
	MYSQL_ROW row;
	char *temp;
	char logname[100];
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	time(&datetime);
	datumtijd = *(gmtime(&datetime));
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
		
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	temp = composeSqlStatement("select punishment, address, room  from tmp_usertable where name='%x'",
			name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;

	row = mysql_fetch_row(res);

	send_printf(fmudstruct->socketfd, "<HTML>\n");
	if (atoi(row[0]) > 0) {
		WriteMessage(name, atoi(row[2]), "A disgusting toad called ");
	}
	WriteMessage(name, atoi(row[2]), "%s left the game...<BR>\r\n", name);
	WriteSentenceIntoOwnLogFile(getParam(MM_AUDITTRAILFILE),
	"%i:%i:%i %i-%i-%i  %s (%s) has left the game<BR>\n", datumtijd.tm_hour,
				     datumtijd.tm_min, datumtijd.tm_sec, datumtijd.tm_mday, datumtijd.tm_mon + 1, datumtijd.tm_year+1900, name, row[1]);
	mysql_free_result(res);
	sprintf(logname, "%sgoodbye.html", getParam(MM_HTMLHEADER));
	ReadFile(logname, fmudstruct->socketfd);
	RemoveUser(name);
	return 1;
}

/*! check if item exists regarding a name and appropriate adjectives.
<TT>
  tokens[1..getTokenAmount(fmudstruct)]
  
  getTokenAmount(fmudstruct)=1 getToken(fmudstruct, 1)=noun
  
  getTokenAmount(fmudstruct)=2 tokens[1..2]=adject1 noun tokens[1..2]=adject2 noun
  tokens[1..2]=adject3 noun
  
  getTokenAmount(fmudstruct)=3 tokens[1..3]=adject1 adject2 noun tokens[1..3]=adject1 adject3 noun
  tokens[1..3]=adject2 adject1 noun tokens[1..3]=adject2 adject3 noun
  tokens[1..3]=adject3 adject1 noun tokens[1..3]=adject3 adject2 noun
  
  getTokenAmount(fmudstruct)=4 tokens[1..4]=adject1 adject2 adject3 noun tokens[1..4]=adject1 adject3
  adject2 noun tokens[1..4]=adject2 adject1 adject3 noun tokens[1..4]=adject2
  adject3 adject1 noun tokens[1..4]=adject3 adject1 adject2 noun
  tokens[1..4]=adject3 adject2 adject1 noun
  </TT>
  \param aantal integer, 1..4
  \return int 0 upon not found, id of the item if found
*/
int 
ItemCheck(char *tok1, char *tok2, char *tok3, char *tok4, int aantal)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int id;
	char *temp, *dude;

	dude = "select id from items where";

	switch (aantal) {
		/* noun */
	case 1:{
		temp = composeSqlStatement("%s items.name='%x'", dude, tok1);
		break;
		}
		/* adjective */
	case 2:{
		temp = composeSqlStatement("%s '%x' in (items.adject1, items.adject2, items.adject3) "
			"and items.name='%x'", dude, tok1, tok2);
		break;
		}
		/* adjective adjective noun */
	case 3:{
		temp = composeSqlStatement("%s '%x' in (items.adject1, items.adject2, items.adject3) "
			"and '%x' in (items.adject1, items.adject2, items.adject3) "
			"and items.name='%x'", dude, tok1, tok2, tok3);
		break;
		}
		/* adjective adjective adjective noun */
	case 4:{
		temp = composeSqlStatement("%s '%x' in (items.adject1, items.adject2, items.adject3) "
			"and '%x' in (items.adject1, items.adject2, items.adject3) "
			"and '%x' in (items.adject1, items.adject2, items.adject3) "
			"and items.name='%x'", dude, tok1, tok2, tok3, tok4);
		break;
		}		/* end4 */
	}			/* endswitch */
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
	
		if (row) 
		{
			id=atoi(row[0]);
		} 
		else 
		{
			id=0;
		}
	}
	else
	{
		id = 0;
	}

	mysql_free_result(res);
	return id;
}				/* endproc */

//! show statistics of current player
int 
Stats_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	char logname[100];
	char *extralook = NULL; 
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
		
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	extralook = NULL;
	temp = composeSqlStatement("select concat('You are ',value,'<BR>') from tmp_attributes "
		"where name='look' and "
		"objectid='%x' and "
		"objecttype=1",
		name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			extralook = (char *)malloc(strlen(row[0])+2);
			strcpy(extralook, row[0]);
		}
		mysql_free_result(res);
	}

	temp = composeSqlStatement("select tmp_usertable.* "
	"from tmp_usertable "
	" where tmp_usertable.name='%x'",name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;

	row = mysql_fetch_row(res);
	
	send_printf(fmudstruct->socketfd, "<HTML>\n");
	send_printf(fmudstruct->socketfd, "<HEAD>\n");
	send_printf(fmudstruct->socketfd, "<TITLE>\n");
	send_printf(fmudstruct->socketfd, "Land of Karchan - Character Statistics\n");
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
	send_printf(fmudstruct->socketfd, "<Center><H1>Character Statistics</H1></Center>");
	send_printf(fmudstruct->socketfd, "<H2>Appearance:</H2>");
	send_printf(fmudstruct->socketfd, "A %s", row[8]); /*age*/
	if (strcasecmp(row[9], "none")) {send_printf(fmudstruct->socketfd, ", %s", row[9]);}
	if (strcasecmp(row[10], "none")) {send_printf(fmudstruct->socketfd, ", %s", row[10]);}
	if (strcasecmp(row[11], "none")) {send_printf(fmudstruct->socketfd, ", %s", row[11]);}
	if (strcasecmp(row[12], "none")) {send_printf(fmudstruct->socketfd, ", %s", row[12]);}
	if (strcasecmp(row[13], "none")) {send_printf(fmudstruct->socketfd, ", %s", row[13]);}
	if (strcasecmp(row[14], "none")) {send_printf(fmudstruct->socketfd, ", %s", row[14]);}
	if (strcasecmp(row[15], "none")) {send_printf(fmudstruct->socketfd, ", %s", row[15]);}
	if (strcasecmp(row[16], "none")) {send_printf(fmudstruct->socketfd, ", %s", row[16]);}
	if (strcasecmp(row[17], "none")) {send_printf(fmudstruct->socketfd, ", %s", row[17]);}/*leg*/

	send_printf(fmudstruct->socketfd, ", %s %s introduces %sself as %s, %s.<BR>\r\n(%s)<BR>", 
	row[7], row[6], HeShe2(row[7]), row[0], row[3], row[4]);/*sex, race*/

	send_printf(fmudstruct->socketfd, "<P><H2>Statistics:</H2>");
	send_printf(fmudstruct->socketfd, "%s, %s<BR>\r\nYou seem %s.<BR>\r\n%s\r\nYou seem %s.<BR>\r\n%s%s%s"
	,row[0], row[3], ShowString(atoi(row[29])/*vitals*/, atoi(row[52])/*maxvital*/), (extralook == NULL? "" : extralook),
	ShowMovement(atoi(row[49]), atoi(row[51])),
	 ShowDrink(atoi(row[32])), ShowEat(atoi(row[33])), ShowBurden(CheckWeight(name)), ShowAlignment(atoi(row[47])));
	send_printf(fmudstruct->socketfd, "You are level <B>%i</B>, <B>%i</B> experience points, <B>%i</B> points away from levelling.<BR>\r\n",
	 atoi(row[24]) /*x.experience*/ / 1000, atoi(row[24]) % 1000, 1000 - (atoi(row[24]) % 1000));
	send_printf(fmudstruct->socketfd, "Strength <B>%s</B> Intelligence <B>%s</B> Dexterity <B>%s</B> Constitution <B>%s</B> Wisdom <B>%s</B>"
		" Mana <B>%i (<font color=blue>%i</Font>)</B> Movement <B>%i (<font color=blue>%i</font>)</B><BR>\r\n",
		row[39] /*strength*/, row[40], row[41], row[42], row[43], atoi(row[50])-atoi(row[48]), atoi(row[50]), 
		atoi(row[51])-atoi(row[49]), atoi(row[51]));
	send_printf(fmudstruct->socketfd, "You have <B>%s</B> training sessions and <B>%s</B> practice sessions left.<BR>\r\n",
		row[45], row[44]);
	if (atoi(row[23]) == 0) {
		send_printf(fmudstruct->socketfd, "You are not whimpy at all.<BR>\r\n");
	} else {
		send_printf(fmudstruct->socketfd, "You are whimpy if you seem %s.<BR>\r\n", ShowString(atoi(row[23]), atoi(row[52])));
	}
	if (atoi(row[26]) == 1) { /*sleep*/
		send_printf(fmudstruct->socketfd, "You are fast asleep.<BR>\r\n");
	}
	if (atoi(row[26]) >= 100) {
		send_printf(fmudstruct->socketfd, "You are sitting at a table.<BR>\r\n");
	}
	if (extralook != NULL) 
	{
		free(extralook);
	}
	mysql_free_result(res);

	temp = composeSqlStatement("select items.adject1, items.adject2, items.name,"
	" tmpitems.wearing, tmpitems.wielding from tmp_itemtable tmpitems, items where "
	"(belongsto='%x') and "
	"((wearing <> '') or "
	" (wielding <> '')) and "
	" (containerid = 0) and "
	" (items.id = tmpitems.id)",name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	while ((row = mysql_fetch_row(res))!=NULL)
	{
		if (row[3][0]!='\0')
		{
			send_printf(fmudstruct->socketfd, "You are wearing a %s, %s %s on your %s.<BR>\r\n",
			row[0], row[1], row[2], row[3]);
		}
		else
		{
			char position[20];
			if (row[4][0]=='1') {strcpy(position, "right hand");}
				else {strcpy(position, "left hand");}
			send_printf(fmudstruct->socketfd, "You are wielding a %s, %s %s in your %s.<BR>\r\n",
			row[0], row[1], row[2], position);
		}
		
	}
	mysql_free_result(res);
	send_printf(fmudstruct->socketfd, "<P>");

	temp = composeSqlStatement("select concat('Your skill in ', name, ' is level ', "
	"skilllevel, '.<BR>\r\n') from skills, skilltable where skilltable.number="
	"skills.number and forwhom='%x'",name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;

	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
		if (row==NULL)
		{
			send_printf(fmudstruct->socketfd, "You currently have no special skills.<BR>\r\n");
		}
		while (row!=NULL)
		{
			send_printf(fmudstruct->socketfd, "%s", row[0]);
			row = mysql_fetch_row(res);
		}
		mysql_free_result(res);
	}
	
	send_printf(fmudstruct->socketfd, "<P>");
	PrintForm(name, password, fmudstruct->frames, fmudstruct->socketfd);

	send_printf(fmudstruct->socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(fmudstruct->socketfd, "<DIV ALIGN=left><P>");
	send_printf(fmudstruct->socketfd, "</BODY></HTML>");
	return 1;
}

//! get money from floor. this is in place of items, because money is a special case.
/*! proper appelation in the game would be <I> get [&lt;amount&gt;] [gold, silver, copper] coin[s]</I>
*/
void
GetMoney_Command(mudpersonstruct *fmudstruct)
{
	/*
	* get [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	
	char *name;
	char *password;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return;
	}

		/*get iron pick*/
		sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
		"where (items.name='coin') and "
		"(items.adject1=' valuable') and "
		"(items.adject2='%x') and "
		"(items.adject3='shiny') and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '') and "
		"(tmpitems.containerid = 0)"
		, getToken(fmudstruct, numberfilledout+1),
		amount, room);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Coins not found.<BR>\r\n");
			WriteRoom(fmudstruct);
			return ;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Coins not found.<BR>\r\n");
			WriteRoom(fmudstruct);
			return;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

		/*get pick*/
		sqlstring = composeSqlStatement( 
			"update tmp_usertable set "
			"%s=%s+%i "
			"where (name='%x')"
			, itemadject2, itemadject2, amount, name);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);

	if (amountitems>amount)
	{
		sqlstring = composeSqlStatement( 
		"update tmp_itemtable set amount=amount-%i "
		"where (id = %i) and "
		"(room = %i) and "
		"(containerid = 0)"
		, amount, itemid, room);
	}
	else
	{
		sqlstring = composeSqlStatement( 
		"delete from tmp_itemtable "
		"where (id = %i) and "
		"(room = %i) and "
		"(containerid = 0)"
		, itemid, room);
	}
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
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
	WriteRoom(fmudstruct);
}

//! drop money onto the floor
/*! proper appelation in the game would be <I> drop [&lt;amount&gt;] [gold, silver, copper] coin[s]</I>
*/
void
DropMoney_Command(mudpersonstruct *fmudstruct)
{
	/*
	* drop [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* to be more specific:
	* drop [amount] copper coins
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, numberfilledout;
	int mycopper, mysilver, mygold, itemid;
	char *checkerror;
	
	char *name;
	char *password;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	if (getTokenAmount(fmudstruct)==1)
	{
		WriteRoom(fmudstruct);
		return;
	}
	
	/* look for specific person */
	sqlstring = composeSqlStatement("select copper, silver, gold from tmp_usertable where (name = '%x')", name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	mycopper = atoi(row[0]);
	mysilver = atoi(row[1]);
	mygold = atoi(row[2]);
	mysql_free_result(res);

	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return;
	}

	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-2),"copper"))
	{
		if (mycopper<amount)
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough copper coins.<BR>\r\n");
			WriteRoom(fmudstruct);
			return;
		}
		/* look for specific person */
			sqlstring = composeSqlStatement("update tmp_usertable set copper=copper-%i "
			" where (name = '%x')"
			,amount, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		itemid=36;
	}
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-2),"silver"))
	{
		if (mysilver<amount)
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough silver coins.<BR>\r\n");
			WriteRoom(fmudstruct);
			return;
		}
		/* look for specific person */
			sqlstring = composeSqlStatement("update tmp_usertable set silver=silver-%i "
			" where (name = '%x')"
			,amount, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		itemid=37;
	}
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-2),"gold"))
	{
		if (mygold<amount)
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough gold coins.<BR>\r\n");
			WriteRoom(fmudstruct);
			return ;
		}
		/* look for specific person */
		sqlstring = composeSqlStatement("update tmp_usertable set gold=gold-%i "
			" where (name = '%x')"
			,amount, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		itemid=38;
	}
	
		/*put pick*/
		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount+%i "
			"where (id=%i) and "
			"(room=%i) and "
			"(containerid = 0)"
			, amount, itemid, room);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);

		if (!changedrows) 
		{
			int changedrows2;
			sqlstring = composeSqlStatement("insert into tmp_itemtable (id, search, belongsto, amount, room, wearing, wielding)"
			" values(%i,'','',%i,%i,'','')"
			, itemid, amount, room);
			res=sendQuery(sqlstring, &changedrows2);
			free(sqlstring);sqlstring=NULL;

			if (!changedrows2)
			{
				WriteSentenceIntoOwnLogFile(logname, "Copper coins not found.<BR>\r\n");
				WriteRoom(fmudstruct);
				return;
			}
			mysql_free_result(res);
	}

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You drop a %s coin.<BR>\r\n", 
		getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
	WriteMessage(name, room, "%s drops a %s coin.<BR>\r\n",
		name, getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You drop %i %s coins.<BR>\r\n", 
		amount, getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
	WriteMessage(name, room, "%s drops %i %s coins.<BR>\r\n",
		name, amount, getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
	}
	WriteRoom(fmudstruct);
}

//! give an amount of money to someone else.
/*! Proper appelation in game is <I>give [&lt;amount&gt;] [gold, silver, copper] coin[s] to &lt;person&gt;</I>.
	Provided a nice warning if money not sufficiently available.
*/
void
GiveMoney_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100], toname[40];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout;
	int mygold, mysilver, mycopper;
	char *checkerror;
	char *name;
	char *password;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->password;
	room = fmudstruct->room;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	/* look for specific person */
	sqlstring = composeSqlStatement("select name from tmp_usertable where (name = '%x') and "
		"(name <> '%x') and "
		"(room = %i)",getToken(fmudstruct, getTokenAmount(fmudstruct)-1), name, room);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return ;
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return ;
	}
	strcpy(toname, row[0]);
	mysql_free_result(res);

	sqlstring = composeSqlStatement("select gold, silver, copper from tmp_usertable where (name = '%x')"
		,name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	mygold=atoi(row[0]);
	mysilver=atoi(row[1]);
	mycopper=atoi(row[2]);
	mysql_free_result(res);

	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return ;
	}

	if (!strcasecmp(getToken(fmudstruct, numberfilledout+1),"copper"))
	{
		if (amount>mycopper) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough copper coins.<BR>\r\n");
			WriteRoom(fmudstruct);
			return ;
		}
	}
	if (!strcasecmp(getToken(fmudstruct, numberfilledout+1),"silver"))
	{
		if (amount>mysilver) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough silver coins.<BR>\r\n");
			WriteRoom(fmudstruct);
			return ;
		}
	}
	if (!strcasecmp(getToken(fmudstruct, numberfilledout+1),"gold"))
	{
		if (amount>mygold) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have enough gold coins.<BR>\r\n");
			WriteRoom(fmudstruct);
			return ;
		}
	}

	/*give money to Karn*/
	sqlstring = composeSqlStatement("update tmp_usertable set %s=%s-%i "
		"where (name='%x')"
		, getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), amount, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);
	sqlstring = composeSqlStatement("update tmp_usertable set %s=%s+%i "
		"where (name='%x')"
		, getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), amount, toname);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);

	if (amount == 1)
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You give a %s coin to %s.<BR>\r\n", 
		getToken(fmudstruct, numberfilledout+1), toname);
	WriteMessageTo(toname, name, room, "%s gives a %s coin to %s.<BR>\r\n",
		name, getToken(fmudstruct, numberfilledout+1), toname);
	WriteSayTo(toname, name, room, "%s gives a %s coin to you.<BR>\r\n",
		name, getToken(fmudstruct, numberfilledout+1));
	}
	else
	{
	WriteSentenceIntoOwnLogFile(logname, 
		"You give %i %s coins to %s.<BR>\r\n", 
		amount, getToken(fmudstruct, numberfilledout+1) ,toname);
	WriteMessageTo(toname, name, room, "%s gives %i %s coins to %s.<BR>\r\n",
		name, amount, getToken(fmudstruct, numberfilledout+1), toname);
	WriteSayTo(toname, name, room, "%s gives %i %s coins to you.<BR>\r\n",
		name, amount, getToken(fmudstruct, numberfilledout+1));
	}
	WriteRoom(fmudstruct);
}

//! retrieve an item from the floor.
/*! Proper appelation: <I>get [&lt;amount&gt;] &lt;item&gt; ; item = &lt;adjective1..3&gt; &lt;name&gt; </I>
*/
void
GetItem_Command(mudpersonstruct *fmudstruct)
{
	/*
	* get [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout, containerid;
	char *checkerror;
	char *name;
	char *password;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->password;
	room  = fmudstruct->room;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return ;
	}
		if (getTokenAmount(fmudstruct)==2+numberfilledout) 
		{
			/*get pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='') and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(items.getable<>0)"
			, getToken(fmudstruct, 1+numberfilledout), amount, room);
		}
		if (getTokenAmount(fmudstruct)==3+numberfilledout) 
		{
			/*get iron pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='') and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(items.getable<>0)"
			,getToken(fmudstruct, numberfilledout+2), 
			getToken(fmudstruct, numberfilledout+1), 
			getToken(fmudstruct, numberfilledout+1), 
			getToken(fmudstruct, numberfilledout+1),
			amount, room);
		}
		if (getTokenAmount(fmudstruct)==4+numberfilledout) 
		{
			/*get iron strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='') and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(items.getable<>0)"
			,getToken(fmudstruct, numberfilledout+3), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
			amount, room);
		}
		if (getTokenAmount(fmudstruct)>=5+numberfilledout) 
		{
			/*get iron strong strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='') and "
			"(tmpitems.room = %i) and "
			"(tmpitems.search = '') and "
			"(items.getable<>0)"
			,getToken(fmudstruct, numberfilledout+4), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
			getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3),
			amount, room);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(fmudstruct);
			return ;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(fmudstruct);
			return ;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		containerid = atoi(row[5]);
		
		mysql_free_result(res);

		if (containerid != 0)
		{
			/* it is a container, and it is containing something! */
			sqlstring = composeSqlStatement("update tmp_itemtable set belongsto='%x', room=0 "
				"where (id=%i) and "
				"room = %i and "
				"(belongsto='') and "
				"(search = '') and "
				"(wearing = '') and "
				"(wielding = '') and "
				"containerid = %i"
				, name, itemid, room, containerid);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
		}
		else
		{
			/*get pick*/
			sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount+%i "
				"where (id=%i) and "
				"(belongsto='%x') and "
				"(wearing = '') and "
				"(wielding = '') and "
				"(containerid = 0)"
				, amount, itemid, name);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
	
			if (!changedrows) 
			{
				int changedrows2;
				sqlstring = composeSqlStatement("insert into tmp_itemtable (id, search, belongsto, amount, room, wearing, wielding)"
				" values(%i,'','%x',%i,0,'','')"
				, itemid, name, amount);
				res=sendQuery(sqlstring, &changedrows2);
				free(sqlstring);sqlstring=NULL;
	
				if (!changedrows2)
				{
					WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
					WriteRoom(fmudstruct);
					return ;
				}
				mysql_free_result(res);
		}
	
		if (amountitems>amount)
		{
			sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-%i "
			"where (id = %i) and "
			"(room = %i) and "
			"(containerid = 0)"
			, amount, itemid, room);
		}
		else
		{
			sqlstring = composeSqlStatement("delete from tmp_itemtable "
			"where (id = %i) and "
			"(room = %i) and "
			"(containerid = 0)"
			, itemid, room);
		}
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
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
	WriteRoom(fmudstruct);
}

//! drop an item on the floor
/*! Proper appelation: <I>drop [&lt;amount&gt;] &lt;item&gt; ; item = &lt;adjective1..3&gt; &lt;name&gt; </I>
*/
void
DropItem_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout, containerid;
	char *checkerror;
	char *name;
	char *password;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->password;
	room  = fmudstruct->room;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return ;
	}
		if (getTokenAmount(fmudstruct)==2+numberfilledout) 
		{
			/*put pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '')"
			, getToken(fmudstruct, 1+numberfilledout), amount, name);
		}
		if (getTokenAmount(fmudstruct)==3+numberfilledout) 
		{
			/*get iron pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(items.dropable<>0)"
			,getToken(fmudstruct, numberfilledout+2), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			amount, name);
		}
		if (getTokenAmount(fmudstruct)==4+numberfilledout) 
		{
			/*get iron strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(items.dropable<>0)"
			,getToken(fmudstruct, numberfilledout+3), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
			amount, name);
		}
		if (getTokenAmount(fmudstruct)>=5+numberfilledout) 
		{
			/*get iron strong strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(items.dropable<>0)"
			,getToken(fmudstruct, numberfilledout+4), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
			getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3),
			amount, name);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(fmudstruct);
			return ;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(fmudstruct);
			return ;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		containerid = atoi(row[5]);

		mysql_free_result(res);

		if (containerid!=0)
		{
			/* it is a container, and it is actually containing something */
			sqlstring = composeSqlStatement("update tmp_itemtable set belongsto='', room=%i "
				"where (id=%i) and "
				"(room=0) and "
				"(belongsto='%x') and "
				"search = '' and "
				"wearing = '' and "
				"wielding = '' and "
				"containerid = %i"
				, room, itemid, name, containerid);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
	
		}
		else
		{
			/*put pick*/
			sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount+%i "
				"where (id=%i) and "
				"(room=%i) and "
				"search = '' and "
				"wearing = '' and "
				"wielding = '' and "
				"(containerid = 0)"
				, amount, itemid, room);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
	
			if (!changedrows) 
			{
				int changedrows2;
				sqlstring = composeSqlStatement("insert into tmp_itemtable (id, search, belongsto, amount, room, wearing, wielding)"
				" values(%i,'','',%i,%i,'','')"
				, itemid, amount, room);
				res=sendQuery(sqlstring, &changedrows2);
				free(sqlstring);sqlstring=NULL;
	
				if (!changedrows2)
				{
					WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
					WriteRoom(fmudstruct);
					return ;
				}
				mysql_free_result(res);
		}
	
		if (amountitems>amount)
		{
			sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-%i "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(wearing='') and "
			"(wielding='') and "
			"(containerid = 0)"
			, amount, itemid, name);
		}
		else
		{
			sqlstring = composeSqlStatement("delete from tmp_itemtable "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(wearing='') and "
			"(wielding='') and "
			"(containerid = 0)"
			, itemid, name);
		}
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	
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
	WriteRoom(fmudstruct);
}

//! put an item into a container
/*! Proper appelation: <I>put [&lt;amount&gt;] &lt;item&gt; in &lt;container&gt; ; container = &lt;item&gt; ; item = &lt;adjective1..3&gt; &lt;name&gt; </I>
	Second item must be a container, and be in room or in inventory. First item must be in inventory.
*/
int
Put_Command(mudpersonstruct *fmudstruct)
{
	/*
	* put [amount] <item> in <item>;<item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	int already_in_there = 0;
	int maxcontainerid;
	int itemid_a, amountitems_a;
	char itemname_a[40], itemadject1_a[40], itemadject2_a[40];
	int itemid_b, amountitems_b;
	char itemname_b[40], itemadject1_b[40], itemadject2_b[40];
	char itembelongsto_b[40];
	int containerid_b, room_b;
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	char *name;
	char *password;
	char *fcommand;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	fcommand = fmudstruct->command;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	if (getTokenAmount(fmudstruct)<4)
	{
		return 0;
	}
	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	/* item_a = item to be put into container
	   item_b = container */
	if (*checkerror!='\0')
	{
		amount=1;
		numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	if ((getTokenAmount(fmudstruct) > 2+numberfilledout) && (!strcasecmp(getToken(fmudstruct, 2+numberfilledout), "in")))
	{
		/* put name in [bijv vmw] [bijv vnm] [bijv vnm] name */
		strcpy(itemname_a, getToken(fmudstruct, 1+numberfilledout));
		*itemadject1_a = 0;
		*itemadject2_a = 0;
		*itemadject1_b = 0;
		*itemadject2_b = 0;
		strcpy(itemname_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
		if (getTokenAmount(fmudstruct)>4+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
		}
		if (getTokenAmount(fmudstruct)>5+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-3));
		}
	}
	else
	if ((getTokenAmount(fmudstruct) > 3+numberfilledout) && (!strcasecmp(getToken(fmudstruct, 3+numberfilledout), "in")))
	{
		/* put <bijv vmw> name in [bijv vmw] [bijv vnm] [bijv vnm] name */
		strcpy(itemname_a, getToken(fmudstruct, 2+numberfilledout));
		strcpy(itemadject1_a, getToken(fmudstruct, 1+numberfilledout));
		*itemadject2_a = 0;
		*itemadject1_b = 0;
		*itemadject2_b = 0;
		strcpy(itemname_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
		if (getTokenAmount(fmudstruct)>5+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
		}
		if (getTokenAmount(fmudstruct)>6+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-3));
		}
	}
	else
	if ((getTokenAmount(fmudstruct) > 4+numberfilledout) && (!strcasecmp(getToken(fmudstruct, 4+numberfilledout), "in")))
	{
		/* put <bijv vmw> <bijv vmw> name in [bijv vmw] [bijv vnm] [bijv vnm] name */
		strcpy(itemname_a, getToken(fmudstruct, 3+numberfilledout));
		strcpy(itemadject1_a, getToken(fmudstruct, 1+numberfilledout));
		strcpy(itemadject2_a, getToken(fmudstruct, 2+numberfilledout));
		*itemadject1_b = 0;
		*itemadject2_b = 0;
		strcpy(itemname_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
		if (getTokenAmount(fmudstruct)>6+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
		}
		if (getTokenAmount(fmudstruct)>7+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-3));
		}
	}
	else
	{
		return 0;
	}
	/* retrieve info to find out if item to be put into container exists */
	sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 "
	"from items, tmp_itemtable tmpitems "
	"where (items.name='%x') and "
	"(('%x' in (items.adject1, items.adject2, items.adject3)) or '%x'='') and "
	"(('%x' in (items.adject1, items.adject2, items.adject3)) or '%x'='') and "
	"(items.id=tmpitems.id) and "
	"(tmpitems.amount>=%i) and " /* amount is bigger/equal to amount requested */
	"(tmpitems.belongsto='%x') and " /* belongsto user */
	"(tmpitems.room = 0) and " /* belongsto/ not in room */
	"(tmpitems.search = '') and " /* not hidden */
	"(tmpitems.wearing = '') and " /* not worn */
	"(tmpitems.wielding = '') and " /* not wielding */
	"(tmpitems.containerid=0)" /* is not a container or empty container */
	, itemname_a, itemadject1_a, itemadject1_a,
	itemadject2_a, itemadject2_a,
	amount, name);
	res=sendQuery(sqlstring, &changedrows);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	itemid_a = atoi(row[0]);
	amountitems_a = atoi(row[1]);
	strcpy(itemname_a, row[2]);
	strcpy(itemadject1_a, row[3]);
	strcpy(itemadject2_a, row[4]);
	
	mysql_free_result(res);

	/* retrieve info to find out if container exists */
	sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
	"tmpitems.containerid, tmpitems.belongsto, tmpitems.room "
	"from items, tmp_itemtable tmpitems "
	"where (items.name='%x') and "
	"(('%x' in (items.adject1, items.adject2, items.adject3)) or '%x'='') and "
	"(('%x' in (items.adject1, items.adject2, items.adject3)) or '%x'='') and "
	"(items.id=tmpitems.id) and "
	"((tmpitems.belongsto='%x') or "
	"(tmpitems.room = %i)) and "
	"(tmpitems.search = '') and "
	"(tmpitems.wearing = '') and "
	"(tmpitems.wielding = '') and "
	"(items.container<>0) "
	"order by room asc, containerid desc"
	, itemname_b, itemadject1_b, itemadject1_b, 
	itemadject2_b, itemadject2_b, name, room);
	res=sendQuery(sqlstring, &changedrows);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Container not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Container not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	itemid_b = atoi(row[0]);
	amountitems_b = atoi(row[1]);
	strcpy(itemname_b, row[2]);
	strcpy(itemadject1_b, row[3]);
	strcpy(itemadject2_b, row[4]);
	containerid_b = atoi(row[5]);
	strcpy(itembelongsto_b, row[6]);
	room_b = atoi(row[7]);
	
	mysql_free_result(res);

	/* retrieve maxcontainerid (sometimes not used, but gathered anyway
		just in case */
	sqlstring = composeSqlStatement("select ifnull(max(containedin)+1,1) "
	"from containeditems");
	res=sendQuery(sqlstring, &changedrows);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Container not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Container not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	maxcontainerid = atoi(row[0]);
	mysql_free_result(res);

	/* step 1: if amount of item requested is smaller then item found */
	if (amount < amountitems_a)
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-%i "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(wearing = '') and "
			"(wielding = '') and "
			"(containerid = 0)"
			, amount, itemid_a, name);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	else
	{
		sqlstring = composeSqlStatement("delete from tmp_itemtable "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(wearing = '') and "
			"(wielding = '') and "
			"(containerid = 0)"
			, itemid_a, name);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}

	/* step 2: if we have more then one 'potential' container */
	if (amountitems_b>1)
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-1 "
			"where (id=%i) and "
			"belongsto='%x' and "
			"room=%i and "
			"wearing = '' and "
			"wielding = '' and "
			"containerid = 0"
			, itemid_b, itembelongsto_b, room_b);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		/* retrieve maxcontainerid */
		sqlstring = composeSqlStatement("insert into tmp_itemtable "
			"(id, search, belongsto, amount, room, wearing, wielding, containerid) "
			"values(%i, '', '%x', 1, %i, '', '', %i)"
			, itemid_b, itembelongsto_b, room_b, maxcontainerid);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	else
	{
		/* exactly one container available */
		/* step 3 : is the container empty? */
		if (containerid_b == 0)
		{
			/* retrieve maxcontainerid */
			sqlstring = composeSqlStatement("update tmp_itemtable set containerid = %i "
				"where (id=%i) and "
				"belongsto='%x' and "
				"room=%i and "
				"wearing = '' and "
				"wielding = '' and "
				"containerid = 0"
				, maxcontainerid, itemid_b, itembelongsto_b, room_b);
//			send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
		}
		else
		{
			/* single container found, but container was not empty */
			maxcontainerid = containerid_b;
			/* step 4: does the item already exist in this container? */
			sqlstring = composeSqlStatement("select 1 "
			"from containeditems "
			"where id = %i and "
			"amount > 0 and "
			"containedin = %i"
			, itemid_a, maxcontainerid);
//			send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			if (res!=NULL) 
			{
				row = mysql_fetch_row(res);
				if (row!=NULL) 
				{
					/* darnit! there's already a bunch of items of this type in there! */
					already_in_there = 1;
				}
				mysql_free_result(res);
			}
		}
	}
	
	if (already_in_there)
	{
		sqlstring = composeSqlStatement("update containeditems set amount = amount + %i "
		"where id = %i and "
		"containedin = %i"
		, amount, itemid_a, maxcontainerid);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	else
	{
		sqlstring = composeSqlStatement("insert into containeditems "
		"(id, amount, containedin) "
		"values(%i, %i, %i)"
		, itemid_a, amount, maxcontainerid);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	if (amount == 1)
	{
		WriteSentenceIntoOwnLogFile(logname, 
			"You put a %s, %s %s in the %s, %s %s.<BR>\r\n", 
			itemadject1_a, itemadject2_a, itemname_a,
			itemadject1_b, itemadject2_b, itemname_b);
		WriteMessage(name, room, "%s puts a %s, %s %s in the %s, %s %s.<BR>\r\n",
			name, itemadject1_a, itemadject2_a, itemname_a,
			itemadject1_b, itemadject2_b, itemname_b);
	}
	else
	{
		WriteSentenceIntoOwnLogFile(logname, 
			"You put %i %s, %s %ss in the %s, %s %s.<BR>\r\n", 
			amount, itemadject1_a, itemadject2_a, itemname_a,
			itemadject1_b, itemadject2_b, itemname_b);
		WriteMessage(name, room, "%s puts %i %s, %s %ss in the %s, %s %s.<BR>\r\n",
			name, amount, itemadject1_a, itemadject2_a, itemname_a,
			itemadject1_b, itemadject2_b, itemname_b);
	}
	WriteRoom(fmudstruct);
	return 1;
}

//! retrieve an item from a container
/*! Proper appelation: <I>retrieve [&lt;amount&gt;] &lt;item&gt; from &lt;container&gt; ; container = &lt;item&gt; ; item = &lt;adjective1..3&gt; &lt;name&gt; </I>
	Second item must be a container, and be in room or in inventory. First item shall be added to your inventory.
*/
int
Retrieve_Command(mudpersonstruct *fmudstruct)
{
	/*
	* retrieve [amount] <item> from <item>;<item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	int already_in_inventory = 0;
	int maxcontainerid;
	int itemid_a, amountitems_a;
	char itemname_a[40], itemadject1_a[40], itemadject2_a[40];
	int itemid_b, amountitems_b;
	char itemname_b[40], itemadject1_b[40], itemadject2_b[40];
	char itembelongsto_b[40];
	int containerid_b, room_b;
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	char *name;
	char *password;
	char *command;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	if (getTokenAmount(fmudstruct) < 4)
	{
		return 0;
	}
	if (getTokenIndex(fmudstruct, "from") == -1)
	{
		/* command does not contain a 'from' word */
		return 0;
	}
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	/* item_a = item to retrieve from container
	   item_b = container */
	if (*checkerror!='\0')
	{
		amount=1;
		numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	if ((numberfilledout) && (!strcasecmp(getToken(fmudstruct, 2), "from")))
	{
		return 0;
	}
	if (!strcasecmp(getToken(fmudstruct, 2+numberfilledout), "from"))
	{
		/* put name in [bijv vmw] [bijv vnm] [bijv vnm] name */
		strcpy(itemname_a, getToken(fmudstruct, 1+numberfilledout));
		*itemadject1_a = 0;
		*itemadject2_a = 0;
		*itemadject1_b = 0;
		*itemadject2_b = 0;
		strcpy(itemname_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
		if (getTokenAmount(fmudstruct)>4+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
		}
		if (getTokenAmount(fmudstruct)>5+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-3));
		}
	}
	else
	if (!strcasecmp(getToken(fmudstruct, 3+numberfilledout), "from"))
	{
		/* put <bijv vmw> name in [bijv vmw] [bijv vnm] [bijv vnm] name */
		strcpy(itemname_a, getToken(fmudstruct, 2+numberfilledout));
		strcpy(itemadject1_a, getToken(fmudstruct, 1+numberfilledout));
		*itemadject2_a = 0;
		*itemadject1_b = 0;
		*itemadject2_b = 0;
		strcpy(itemname_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
		if (getTokenAmount(fmudstruct)>5+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
		}
		if (getTokenAmount(fmudstruct)>6+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-3));
		}
	}
	else
	if (!strcasecmp(getToken(fmudstruct, 4+numberfilledout), "from"))
	{
		/* put <bijv vmw> <bijv vmw> name in [bijv vmw] [bijv vnm] [bijv vnm] name */
		strcpy(itemname_a, getToken(fmudstruct, 3+numberfilledout));
		strcpy(itemadject1_a, getToken(fmudstruct, 1+numberfilledout));
		strcpy(itemadject2_a, getToken(fmudstruct, 2+numberfilledout));
		*itemadject1_b = 0;
		*itemadject2_b = 0;
		strcpy(itemname_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
		if (getTokenAmount(fmudstruct)>6+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-2));
		}
		if (getTokenAmount(fmudstruct)>7+numberfilledout)
		{
			strcpy(itemadject1_b, getToken(fmudstruct, getTokenAmount(fmudstruct)-3));
		}
	}
	/* retrieve info to find out if item to be retrieved from container exist */
	sqlstring = composeSqlStatement("select items1.id, tmpitems1.amount, items1.name, items1.adject1, items1.adject2, "
	"items2.id, items2.name, items2.adject1, items2.adject2, tmpitems2.containerid, tmpitems2.belongsto, tmpitems2.room "
	"from items items1, containeditems tmpitems1, items items2, tmp_itemtable tmpitems2 "
	"where (items1.name='%x') and "
	"(('%x' in (items1.adject1, items1.adject2, items1.adject3)) or '%x'='') and "
	"(('%x' in (items1.adject1, items1.adject2, items1.adject3)) or '%x'='') and "
	"(items1.id=tmpitems1.id) and "
	"(tmpitems1.amount>=%i) and " /* amount is bigger/equal to amount requested */
	"(tmpitems1.containedin<>0) and " /* is present in a container */
	"(tmpitems1.containedin = tmpitems2.containerid) and " /* item is in container */
	"(items2.name='%x') and "
	"(('%x' in (items2.adject1, items2.adject2, items2.adject3)) or '%x'='') and "
	"(('%x' in (items2.adject1, items2.adject2, items2.adject3)) or '%x'='') and "
	"(items2.id=tmpitems2.id) and "
	"(tmpitems2.amount=1) and " /* amount has to be one, because it is ONE container */
	"((tmpitems2.belongsto='%x') or " /* either belongsto user */
	"(tmpitems2.room = %i)) and " /* or in room */
	"(tmpitems2.search = '') and " /* not hidden */
	"(tmpitems2.wearing = '') and " /* not worn */
	"(tmpitems2.wielding = '') and " /* not wielding */
	"(tmpitems2.containerid<>0)", 
	itemname_a, itemadject1_a, itemadject1_a,	itemadject2_a, itemadject2_a,
	amount,
	itemname_b, itemadject1_b, itemadject1_b,	itemadject2_b, itemadject2_b,
	name, room);
//	send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
	res=sendQuery(sqlstring, &changedrows);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Item or container not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Item or container not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	itemid_a = atoi(row[0]);
	amountitems_a = atoi(row[1]);
	strcpy(itemname_a, row[2]);
	strcpy(itemadject1_a, row[3]);
	strcpy(itemadject2_a, row[4]);
	
	itemid_b = atoi(row[5]);
	strcpy(itemname_b, row[6]);
	strcpy(itemadject1_b, row[7]);
	strcpy(itemadject2_b, row[8]);
	containerid_b = atoi(row[9]);
	strcpy(itembelongsto_b, row[10]);
	room_b = atoi(row[11]);
	
	mysql_free_result(res);

	/* step 1: if amount of item requested is smaller then item found */
	if (amount < amountitems_a)
	{
		sqlstring = composeSqlStatement("update containeditems set amount=amount-%i "
			"where (id=%i) and "
			"(containedin = %i)"
			, amount, itemid_a, containerid_b);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	else
	{
		int containerempty = 0;
		sqlstring = composeSqlStatement("delete from containeditems "
			"where (id=%i) and "
			"(containedin = %i)"
			, itemid_a, containerid_b);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);

		/* step 2: if container is empty */
		sqlstring = composeSqlStatement("select 1 "
			"from containeditems "
			"where containedin = %i"
			, containerid_b);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res!=NULL) 
		{
			row = mysql_fetch_row(res);
			if (row==NULL) 
			{
				containerempty = 1;
			}
			else
			{
				containerempty = 0;
			}
			mysql_free_result(res);
		}
		else
		{
			containerempty = 1;
		}
		if (containerempty)
		{
			sqlstring = composeSqlStatement("update tmp_itemtable "
				"set containerid = 0 "
				"where id = %i and "
				"search = '' and "
				"belongsto = '%x' and "
				"amount = 1 and "
				"room = %i  and "
				"wearing = '' and "
				"wielding = '' and "
				"containerid = %i"
				, itemid_b, itembelongsto_b, room_b, containerid_b);
//			send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
		}
	}

	/* step 3: does the item already exist in the inventory? */
	sqlstring = composeSqlStatement("select 1 "
	"from tmp_itemtable "
	"where id = %i and "
	"search = '' and "
	"belongsto = '%x' and "
	"amount > 0 and "
	"room = 0 and "
	"wearing = '' and "
	"wielding = '' and "
	"containerid = 0"
	, itemid_a, name);
//	send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
	res=sendQuery(sqlstring, &changedrows);
	free(sqlstring);sqlstring=NULL;
	if (res!=NULL) 
	{
		row = mysql_fetch_row(res);
		if (row!=NULL) 
		{
			/* darnit! there's already a bunch of items of this type in the inventory! */
			already_in_inventory = 1;
		}
		mysql_free_result(res);
	}
	
	if (already_in_inventory)
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set amount = amount + %i "
		"where id = %i and "
		"search = '' and "
		"belongsto = '%x' and "
		"amount > 0 and "
		"room = 0 and "
		"wearing = '' and "
		"wielding = '' and "
		"containerid = 0"
		, amount, itemid_a, name);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	else
	{
		sqlstring = composeSqlStatement("insert into tmp_itemtable "
		"(id, search, belongsto, amount, room, wearing, wielding, containerid) "
		"values(%i, '', '%x', %i, 0, '', '', 0)"
		, itemid_a, name, amount);
//		send_printf(fmudstruct->socketfd, "[%s]\n", sqlstring);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}

	if (amount == 1)
	{
		WriteSentenceIntoOwnLogFile(logname, 
			"You pull a %s, %s %s out of the %s, %s %s.<BR>\r\n", 
			itemadject1_a, itemadject2_a, itemname_a,
			itemadject1_b, itemadject2_b, itemname_b);
		WriteMessage(name, room, "%s pulls a %s, %s %s out of the %s, %s %s.<BR>\r\n",
			name, itemadject1_a, itemadject2_a, itemname_a,
			itemadject1_b, itemadject2_b, itemname_b);
	}
	else
	{
		WriteSentenceIntoOwnLogFile(logname, 
			"You pull %i %s, %s %ss out of the %s, %s %s.<BR>\r\n", 
			amount, itemadject1_a, itemadject2_a, itemname_a,
			itemadject1_b, itemadject2_b, itemname_b);
		WriteMessage(name, room, "%s pulls %i %s, %s %ss out of %s, %s %s.<BR>\r\n",
			name, amount, itemadject1_a, itemadject2_a, itemname_a,
			itemadject1_b, itemadject2_b, itemname_b);
	}
	WriteRoom(fmudstruct);
	return 1;
}

//! wear an item on your body someplace
/*! correct appelation: <I>wear &lt;item&gt; on &lt;position&gt; ; position = {head, neck, body, lefthand, righthand, legs, feet}</I>
*/
int
Wear_Command(mudpersonstruct *fmudstruct)
{
	/*
	* wear <item> on <position>; 
	* <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* <position> = {head, neck, body, lefthand, righthand, legs, feet}
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring, sqlcomposite[80];
	char logname[100], mysex[20];
	char itemname[40], itemadject1[40], itemadject2[40];
	int changedrows, itemid, amountitems, itemwearable;
	char *name;
	char *password;
	char *command;
	int room;
	
	name = fmudstruct->name;
	password = fmudstruct->cookie;
	room = fmudstruct->room;
	command = fmudstruct->command;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	if (getTokenAmount(fmudstruct)<4)
	{
		return 0;
	}
	if (strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-2), "on"))
	{
		return 0;
	}
	itemwearable=-1;
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-1),"lefthand")) {itemwearable=1;}
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-1),"righthand")) {itemwearable=2;}
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-1),"head")) {itemwearable=4;}
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-1),"neck")) {itemwearable=7;}
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-1),"head")) {itemwearable=8;}
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-1),"body")) {itemwearable=9;}
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-1),"legs")) {itemwearable=10;}
	if (!strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-1),"feet")) {itemwearable=11;}
	
	if ((itemwearable==1) || (itemwearable==2)) 
	{
		sprintf(sqlcomposite,"((items.wearable = %i) or (items.wearable = 3)) and ", itemwearable);
	}
	else
	{
		sprintf(sqlcomposite,"(items.wearable = %i) and ", itemwearable);
	}
	
	/* check to see that person does not already have something on said bodypart */
	sqlstring = composeSqlStatement("select 1 "
		"from tmp_itemtable "
		"where belongsto = '%x' and "
		"wearing = '%x'"
		, name, getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			WriteSentenceIntoOwnLogFile(logname, "You are already wearing something there!<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		mysql_free_result(res);
	}

	/* look for specific person */
	sqlstring = composeSqlStatement("select sex from tmp_usertable where (name = '%x')"
		, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

		if (getTokenAmount(fmudstruct)==4) 
		{
			/*wear pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"%s"
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			, getToken(fmudstruct, 1), name, sqlcomposite);
		}
		if (getTokenAmount(fmudstruct)==5) 
		{
			/*get iron pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"%s"
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 2), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			name, sqlcomposite);
		}
		if (getTokenAmount(fmudstruct)==6) 
		{
			/*get iron strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"%s"
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems_containerid = 0)"
			,getToken(fmudstruct, 3), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			name, sqlcomposite);
		}
		if (getTokenAmount(fmudstruct)>=7) 
		{
			/*get iron strong strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"%s"
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 4), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
			name, sqlcomposite);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You try to wear it, and fail.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You try to wear it, and fail.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

	if (amountitems>1)
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-1 "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, itemid, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		sqlstring = composeSqlStatement("insert into tmp_itemtable (id, search, belongsto, amount, room, wearing, wielding)"
		" values(%i, '', '%x', 1, 0, '%x', '')"
		, itemid, name, getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
	}
	else
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set wearing='%x' "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wielding='') and "
		"(wearing='') and "
		"(containerid = 0)"
		, getToken(fmudstruct, getTokenAmount(fmudstruct)-1), itemid, name);
	}
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You wear a %s, %s %s on your %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
	WriteMessage(name, room, "%s wears a %s, %s %s on %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, HeShe3(mysex), getToken(fmudstruct, getTokenAmount(fmudstruct)-1));
	WriteRoom(fmudstruct);
	return 1;
}

//! remove item from body
/*! correct appelation: <I>remove &lt;item&gt;</I>
*/
int
Unwear_Command(mudpersonstruct *fmudstruct)
{
	/*
	* wear <item> on <position>; 
	* <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* <position> = {head, neck, body, lefthand, righthand, legs, feet}
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring, sqlcomposite[80];
	char logname[100], mysex[20];
	char itemname[40], itemadject1[40], itemadject2[40], itemwearing[20];
	int changedrows, itemid, amountitems, itemwearable;
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
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	/* look for specific person */
	sqlstring = composeSqlStatement("select sex from tmp_usertable where (name = '%x')"
		, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

		if (getTokenAmount(fmudstruct)==2) 
		{
			/*wear pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wearing from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing <> '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			, getToken(fmudstruct, 1), name );
		}
		if (getTokenAmount(fmudstruct)==3) 
		{
			/*get iron pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wearing from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing <> '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 2), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			name);
		}
		if (getTokenAmount(fmudstruct)==4) 
		{
			/*get iron strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wearing from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing <> '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 3), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			name);
		}
		if (getTokenAmount(fmudstruct)>=5) 
		{
			/*get iron strong strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wearing from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing <> '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 4), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
			name);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You try to remove it, and fail.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You try to remove it, and fail.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		strcpy(itemwearing, row[5]);
		
		mysql_free_result(res);

		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount+1 "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, itemid, name);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		if (!changedrows) 
		{
			sqlstring = composeSqlStatement("update tmp_itemtable set wearing='' "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(wearing='%x') and "
			"(wielding='') and "
			"(containerid = 0)"
			, itemid, name, itemwearing);
		}
		else
		{
			sqlstring = composeSqlStatement("delete from tmp_itemtable "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(wielding='') and "
			"(wearing='%x') and "
			"(containerid = 0)"
			, itemid, name, itemwearing);
		}
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You remove a %s, %s %s from your %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, itemwearing);
	WriteMessage(name, room, "%s removes a %s, %s %s from %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, HeShe3(mysex), itemwearing);
	WriteRoom(fmudstruct);
	return 1;
}

//! wield an item (in left or right hand)
/*! correct appelation: <I>wield &lt;item&gt;</I>
*/
int
Wield_Command(mudpersonstruct *fmudstruct)
{
	/*
	* wield <item>; 
	* <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* <position> = {left hand, right hand}
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100], mysex[20];
	char itemname[40], itemadject1[40], itemadject2[40], position2[20];
	int changedrows, itemid, amountitems, itemwearable, position;
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
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	position=1;strcpy(position2, "right hand");
	sqlstring = composeSqlStatement("select wielding from tmp_itemtable tmpitems "
	"where (tmpitems.belongsto='%x') and "
	"(tmpitems.room = 0) and "
	"(tmpitems.search = '') and "
	"(tmpitems.wearing = '') and "
	"(tmpitems.wielding <> '') and "
	"(tmpitems.containerid = 0)"
	, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
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
				WriteRoom(fmudstruct);
				return 1;
			}
		}
	}
	mysql_free_result(res);

	/* look for specific person */
	sqlstring = composeSqlStatement("select sex from tmp_usertable where (name = '%x')"
		, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

		if (getTokenAmount(fmudstruct)==2) 
		{
			/*wear pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(items.wieldable = 3) and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"			
			, getToken(fmudstruct, 1), name);
		}
		if (getTokenAmount(fmudstruct)==3) 
		{
			/*get iron pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(items.wieldable = 3) and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 2), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			name);
		}
		if (getTokenAmount(fmudstruct)==4) 
		{
			/*get iron strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(items.wieldable = 3) and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 3), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			name);
		}
		if (getTokenAmount(fmudstruct)>=5) 
		{
			/*get iron strong strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(items.wieldable = 3) and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 4), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
			name);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have that item.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You do not have that item.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		
		mysql_free_result(res);

	if (amountitems>1)
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-1 "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, itemid, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		sqlstring = composeSqlStatement("insert into tmp_itemtable (id, search, belongsto, amount, room, wearing, wielding)"
		" values(%i, '', '%x', 1, 0, '', '%i')"
		, itemid, name, position);
	}
	else
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set wielding='%i' "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wielding='') and "
		"(wearing='') and "
		"(containerid = 0)"
		, position, itemid, name);
	}
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You wield a %s, %s %s in your %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, position2);
	WriteMessage(name, room, "%s wields a %s, %s %s in %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, HeShe3(mysex), position2);
	WriteRoom(fmudstruct);
	return 1;
}

//! stop wielding an item in right or left hand
/*! correct appelation: <I>unwield &lt;item&gt;</I>
*/
int
Unwield_Command(mudpersonstruct *fmudstruct)
{
	/*
	* unwield <item>; 
	* <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	* <position> = {left hand=2, right hand=1}
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring, sqlcomposite[80];
	char logname[100], mysex[20];
	char itemname[40], itemadject1[40], itemadject2[40], itemwielding[20], position[20];
	int changedrows, itemid, amountitems, itemwieldable;
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
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	/* look for specific person */
	sqlstring = composeSqlStatement("select sex from tmp_usertable where (name = '%x')"
		, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);

		if (getTokenAmount(fmudstruct)==2) 
		{
			/*wear pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wielding from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding <> '') and "
			"(tmpitems.containerid = 0)"
			, getToken(fmudstruct, 1), name );
		}
		if (getTokenAmount(fmudstruct)==3) 
		{
			/*get iron pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wielding from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding <> '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 2), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			name);
		}
		if (getTokenAmount(fmudstruct)==4) 
		{
			/*get iron strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wielding from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding <> '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 3), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			name);
		}
		if (getTokenAmount(fmudstruct)>=5) 
		{
			/*get iron strong strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.wielding from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding <> '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 4), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
			name);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You fail to stop wielding it.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You fail to stop wielding it.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		strcpy(itemwielding, row[5]);
		
		mysql_free_result(res);

		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount+1 "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, itemid, name);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		if (!changedrows) 
		{
			sqlstring = composeSqlStatement("update tmp_itemtable set wielding='' "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(wearing='') and "
			"(wielding='%x') and "
			"(containerid = 0)"
			, itemid, name, itemwielding);
		}
		else
		{
		sqlstring = composeSqlStatement("delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wielding='%x') and "
		"(wearing='') and "
		"(containerid = 0)"
		, itemid, name, itemwielding);
		}
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);

	if (itemwielding[0]=='1') {strcpy(position, "right hand");}
		else {strcpy(position, "left hand");}
	WriteSentenceIntoOwnLogFile(logname, 
		"You stop wielding the %s, %s %s in your %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname, position);
	WriteMessage(name, room, "%s stops wielding the %s, %s %s in %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname, HeShe3(mysex), position);
	WriteRoom(fmudstruct);
	return 1;
}

//! eat an item
/*! Proper appelation: <I>eat &lt;item&gt; ; item = &lt;adjective1..3&gt; &lt;name&gt; </I>
*/
int
Eat_Command(mudpersonstruct *fmudstruct)
{
	/*
	* eat <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;			
	char *sqlstring;
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int changedrows, itemid, amountitems, myeatstats;
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

	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	/* check drinkstats of specific person */
	sqlstring = composeSqlStatement("select eatstats from tmp_usertable where (name = '%x')"
		, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	myeatstats=atoi(row[0]);
	mysql_free_result(res);
	if (myeatstats > 50) 
	{
			WriteSentenceIntoOwnLogFile(logname, "You are full, and cannot eat any more.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
	} /* too much to eat */
		if (getTokenAmount(fmudstruct)==2) 
		{
			/*eat pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.eatable from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(items.eatable <> '') and "
			"(tmpitems.containerid = 0)"
			, getToken(fmudstruct, 1), name);
		}
		if (getTokenAmount(fmudstruct)==3) 
		{
			/*get iron pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.eatable from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.eatable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 2), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			name);
		}
		if (getTokenAmount(fmudstruct)==4) 
		{
			/*get iron strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.eatable from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.eatable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 3), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			name);
		}
		if (getTokenAmount(fmudstruct)>=5) 
		{
			/*get iron strong strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.eatable from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.eatable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 4), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
			name);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You cannot eat that.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You cannot eat that.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		LookString(row[5], name, password, fmudstruct->frames, fmudstruct->socketfd);
		
		mysql_free_result(res);

	/* check eatstats of specific person */
	sqlstring = composeSqlStatement("update tmp_usertable set eatstats=eatstats+10 "
		"where (name = '%x')"
		, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);
	if (amountitems>1)
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-1 "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, itemid, name);
	}
	else
	{
		sqlstring = composeSqlStatement("delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing = '') and "
		"(wielding = '') and "
		"(containerid = 0)"
		, itemid, name);
	}
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You eat a %s, %s %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s eats a %s, %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname);
	return 1;
}

//! drink an item
/*! Proper appelation: <I>drink &lt;item&gt; ; item = &lt;adjective1..3&gt; &lt;name&gt; </I>
*/
int
Drink_Command(mudpersonstruct *fmudstruct)
{
	/*
	* get [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int changedrows, itemid, amountitems;
	int mydrinkstats;
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

	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	/* check drinkstats of specific person */
	sqlstring = composeSqlStatement("select drinkstats from tmp_usertable where (name = '%x')"
		, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	mydrinkstats=atoi(row[0]);
	mysql_free_result(res);
	if (mydrinkstats >= 49) 
	{
			WriteSentenceIntoOwnLogFile(logname, "You have drunk your fill.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
	} /* too much to drink */
	if (mydrinkstats < -59) 
	{
			WriteSentenceIntoOwnLogFile(logname, "You are already dangerously intoxicated, "
			"and another drop might just possibly kill you.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
	} /* too much to drink spiritual like */
		if (getTokenAmount(fmudstruct)==2) 
		{
			/*put pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.drinkable from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.drinkable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			, getToken(fmudstruct, 1), name);
		}
		if (getTokenAmount(fmudstruct)==3) 
		{
			/*get iron pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.drinkable from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.drinkable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 2), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			name);
		}
		if (getTokenAmount(fmudstruct)==4) 
		{
			/*get iron strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.drinkable from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.drinkable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 3), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			name);
		}
		if (getTokenAmount(fmudstruct)>=5) 
		{
			/*get iron strong strong pick*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, items.drinkable from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=1) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.room = 0) and "
			"(tmpitems.search = '') and "
			"(items.drinkable <> '') and "
			"(tmpitems.wearing = '') and "
			"(tmpitems.wielding = '') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, 4), 
			getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
			getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
			getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
			name);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You cannot drink that.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You cannot drink that.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		itemid = atoi(row[0]);
	LookString(row[5], name, password, fmudstruct->frames, fmudstruct->socketfd);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		mysql_free_result(res);

	if ((!strcasecmp(itemname,"beer")) ||
		(!strcasecmp(itemname,"ale")) ||
		(!strcasecmp(itemname,"whisky")) ||
		(!strcasecmp(itemname,"vodka"))) 
	{
		/* check drinkstats of specific person */
		sqlstring = composeSqlStatement("update tmp_usertable set drinkstats=drinkstats-10 where (name = '%x')"
			, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	else
	{
		/* check drinkstats of specific person */
		sqlstring = composeSqlStatement("update tmp_usertable set drinkstats=drinkstats+10 "
			"where (name = '%x')"
			, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}

	if (amountitems>1)
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-1 "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, itemid, name);
	}
	else
	{
		sqlstring = composeSqlStatement("delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, itemid, name);
	}
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);

	WriteSentenceIntoOwnLogFile(logname, 
		"You drink a %s, %s %s.<BR>\r\n", 
		itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s drinks a %s, %s %s.<BR>\r\n",
		name, itemadject1, itemadject2, itemname);
	return 1;
}

//! remaps the shopping list of a certain bot
void
RemapShoppingList_Command(char *name)
{
	/*
	* remaps the shoppinglist of a certain bot
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	int number=0;
	
	if (!strcasecmp(name, "Karcas")) {number=-49;}
	if (number==0) {return;}

	sqlstring = composeSqlStatement("update items set readdescr='<H1>"
	"<IMG SRC=\\\"http://%s/images/gif/scroll.gif\\\">"
	"The List</H1><HR>Items:<UL>' where id=%i", getParam(MM_SERVERNAME), number);
	sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;

	sqlstring = composeSqlStatement("select items.name, items.adject1, items.adject2, items.copper, items.silver, items.gold"
	" from items, tmp_itemtable tmpitems where "
	"(tmpitems.id=items.id) and "
	"(tmpitems.belongsto='%x') and "
	"(tmpitems.containerid = 0)"
	, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	while (row = mysql_fetch_row(res))
	{
		sqlstring = composeSqlStatement("update items set readdescr=CONCAT(readdescr, "
		"'<LI>%s, %s %s (%s gold, %s silver, %s copper a piece)') "
		"where id=%i",row[1],row[2], row[0], row[5], row[4], row[3], number);
		sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
	}
	mysql_free_result(res);

	sqlstring = composeSqlStatement("update items set readdescr=CONCAT(readdescr, '</UL>"
	"<P>"
	"') where id=%i",number);
	sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;

}

//! buy an item from a bot
/*! Proper appelation: <I>buy [&lt;amount&gt;] &lt;item&gt; ; item = &lt;adjective1..3&gt; &lt;name&gt; </I>
	Item is added to inventory if enough money and the money is subtracted.
*/
int
BuyItem_Command(mudpersonstruct *fmudstruct, char *fromname)
{
	/*
	* buy [amount] <item> to <person> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100], toname[40];
	char itemname[40], itemadject1[40], itemadject2[40];
	int mygold, mysilver, mycopper;
	int itemgold, itemsilver, itemcopper;
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	char *name;
	char *password;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->password;
	room  = fmudstruct->room;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	/* look for specific person */
	sqlstring = composeSqlStatement("select copper, silver, gold from tmp_usertable where (name = '%x')"
		, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	mycopper = atoi(row[0]);
	mysilver = atoi(row[1]);
	mygold = atoi(row[2]);
	mysql_free_result(res);

	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
		if (getTokenAmount(fmudstruct)==2+numberfilledout) 
		{
			/*give pick to Karn*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
			"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.containerid = 0)"
			, getToken(fmudstruct, 1+numberfilledout), amount, fromname);
		}
		if (getTokenAmount(fmudstruct)==3+numberfilledout) 
		{
			/*give iron pick to Karn*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
			"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, numberfilledout+2), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			amount, fromname);
		}
		if (getTokenAmount(fmudstruct)==4+numberfilledout) 
		{
			/*give iron strong pick to Karn*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
			"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, numberfilledout+3), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
			amount, fromname);
		}
		if (getTokenAmount(fmudstruct)>=5+numberfilledout) 
		{
			/*give iron strong pick to Karn*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
			"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.containerid = 0)"
			,getToken(fmudstruct, numberfilledout+4), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
			getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3),
			amount, fromname);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You fail to buy the item.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You fail to buy the item.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
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
			WriteRoom(fmudstruct);
			return 1;
		}

		/* look for specific person */
		sqlstring = composeSqlStatement("update tmp_usertable set copper=%i, silver=%i, gold=%i "
			" where (name = '%x')"
			,mycopper, mysilver, mygold, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
		
		if (!strcasecmp(fromname, "Karcas"))
		{
			sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-%i "
				"where (id=%i) and "
				"(belongsto='%x') and "
				"(containerid = 0)"
				, amount, itemid, fromname);
			res=sendQuery(sqlstring, NULL);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
			sqlstring = composeSqlStatement("delete from tmp_itemtable where (amount=0) "
				"and (id=%i) and "
				"(belongsto='%x') and "
				"(containerid = 0)"
				, itemid, fromname);
			res=sendQuery(sqlstring, NULL);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
		}

		/*give pick to Karn*/
		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount+%i "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(containerid = 0)"
			, amount, itemid, name);
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);

		if (!changedrows) 
		{
			int changedrows2;
			sqlstring = composeSqlStatement("insert into tmp_itemtable (id, search, belongsto, amount, room, wearing, wielding)"
			" values(%i,'','%x',%i,0,'','')"
			, itemid, name, amount);
			res=sendQuery(sqlstring, &changedrows2);
			free(sqlstring);sqlstring=NULL;

			if (!changedrows2)
			{
				WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
				WriteRoom(fmudstruct);
				return 1;
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
	WriteRoom(fmudstruct);
	return 1;
}

//! sell an item to a bot
/*! Proper appelation: <I>sell [&lt;amount&gt;] &lt;item&gt; ; item = &lt;adjective1..3&gt; &lt;name&gt; </I>
	Item is removed from inventory and the money is added.
*/
void
SellItem_Command(mudpersonstruct *fmudstruct, char *toname)
{
	/*
	* sell [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int mygold, mysilver, mycopper;
	int itemgold, itemsilver, itemcopper;
	int amount, changedrows, itemid, amountitems, numberfilledout;
	char *checkerror;
	char *name;
	char *password;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->password;
	room  = fmudstruct->room;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return ;
	}
	if (getTokenAmount(fmudstruct)==2+numberfilledout) 
	{
		/*sell pick to Karn*/
		sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
		"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
		" where (items.name='%x') and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='%x') and "
		"(tmpitems.wearing='') and "
		"(tmpitems.wielding='') and "
		"(tmpitems.containerid = 0)"
		, getToken(fmudstruct, 1+numberfilledout), amount, name);
	}
	if (getTokenAmount(fmudstruct)==3+numberfilledout) 
	{
		/*give iron pick to Karn*/
		sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
		"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
		"where (name='%x') and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='%x') and "
		"(tmpitems.wearing='') and "
		"(tmpitems.wielding='') and "
		"(tmpitems.containerid = 0)"
		,getToken(fmudstruct, numberfilledout+2), 
		getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
		amount, name);
	}
	if (getTokenAmount(fmudstruct)==4+numberfilledout) 
	{
		/*give iron strong pick to Karn*/
		sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
		"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
		"where (name='%x') and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='%x') and "
		"(tmpitems.wearing='') and "
		"(tmpitems.wielding='') and "
		"(tmpitems.containerid = 0)"
		,getToken(fmudstruct, numberfilledout+3), 
		getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
		getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
		amount, name);
	}
	if (getTokenAmount(fmudstruct)>=5+numberfilledout) 
	{
		/*give iron strong pick to Karn*/
		sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, "
		"items.copper, items.silver, items.gold from items, tmp_itemtable tmpitems "
		" where (name='%x') and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=%i) and "
		"(tmpitems.belongsto='%x') and "
		"(tmpitems.wearing='') and "
		"(tmpitems.wielding='') and "
		"(tmpitems.containerid = 0)"
		,getToken(fmudstruct, numberfilledout+4), 
		getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
		getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
		getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3),
		amount, name);
	}
	res=sendQuery(sqlstring, &changedrows);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "You fail to sell the item.<BR>\r\n");
		WriteRoom(fmudstruct);
		return ;
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "You fail to sell the item.<BR>\r\n");
		WriteRoom(fmudstruct);
		return;
	}
	itemid = atoi(row[0]);
	amountitems = atoi(row[1]);
	strcpy(itemname, row[2]);
	strcpy(itemadject1, row[3]);
	strcpy(itemadject2, row[4]);
	itemcopper = atoi(row[5]);
	itemsilver = atoi(row[6]);
	itemgold = atoi(row[7]);
	
	sqlstring = composeSqlStatement("update tmp_usertable set copper=copper+%i, silver=silver+%i, gold=gold+%i "
		" where (name = '%x')"
		, amount*itemcopper, amount*itemsilver, amount*itemgold, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);
	
	if (amount == amountitems)
	{
		sqlstring = composeSqlStatement("delete from tmp_itemtable "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, itemid, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	else
	{
		sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-%i "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, amount, itemid, name);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}

	sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount+%i "
		"where (id=%i) and "
		"(belongsto='%x') and "
		"(wearing='') and "
		"(wielding='') and "
		"(containerid = 0)"
		, amount, itemid, toname);
	res=sendQuery(sqlstring, &changedrows);
	free(sqlstring);sqlstring=NULL;
	mysql_free_result(res);

	if (!changedrows)
	{
		sqlstring = composeSqlStatement("insert into tmp_itemtable (id, search, belongsto, amount, room, wearing, wielding)"
		" values(%i,'','%x',%i,0,'','')"
		, itemid, toname, amount);
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
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
	WriteRoom(fmudstruct);
}

//! search for an item among the stuff in a room
/*! correct appelation: <I>search &lt;item&gt;</I>
*/
int
Search_Command(mudpersonstruct *fmudstruct)
{
	/*
	* search <object>
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout, containerid;
	char *checkerror;
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
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	/*search pick*/
	sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
	"from items, tmp_itemtable tmpitems "
	" where "
	"(items.id=tmpitems.id) and "
	"(tmpitems.belongsto='') and "
	"(tmpitems.room = %i) and "
	"(tmpitems.search = '%x')"
	, room, fcommand+(getToken(fmudstruct, 1)-getToken(fmudstruct, 0)));
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Object not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "You search %s dilligently, yet find nothing at all.<BR>\r\n", fcommand+(getToken(fmudstruct, 1)-getToken(fmudstruct, 0)));
			WriteRoom(fmudstruct);
			return 1;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		containerid = atoi(row[5]);
		
		mysql_free_result(res);
		
		if (containerid !=0)
		{
			/* you are searching and you actually find a container that contains something */
			sqlstring = composeSqlStatement("update tmp_itemtable set belongsto='%x', room=0, search='' "
				"where (id=%i) and "
				"room = %i and "
				"(belongsto='') and "
				"(wearing = '') and "
				"(wielding = '') and "
				"(containerid = %i) and "
				"(tmpitems.search = '%x')"
				, name, itemid, containerid, fcommand+(getToken(fmudstruct, 1)-getToken(fmudstruct, 0)));
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
	
		}
		else
		{
			/*search pick*/
			sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount+1 "
				"where (id=%i) and "
				"(belongsto='%x') and "
				"(wearing = '') and "
				"(wielding = '') and "
				"(containerid = 0)"
				, itemid, name);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
	
			if (!changedrows) 
			{
				int changedrows2;
				sqlstring = composeSqlStatement("insert into tmp_itemtable (id, search, belongsto, amount, room, wearing, wielding)"
				" values(%i,'','%x',1,0,'','')"
				, itemid, name);
				res=sendQuery(sqlstring, &changedrows2);
				free(sqlstring);sqlstring=NULL;
				if (!changedrows2)
				{
					WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
					WriteRoom(fmudstruct);
					return 1;
				}
				mysql_free_result(res);
		}
	
		if (amountitems>1)
		{
			sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-1 "
			"where (id=%i) and "
			"(search='%x') and "
			"(room=%i) and "
			"(containerid = 0)"
			, itemid, fcommand+(getToken(fmudstruct, 1)-getToken(fmudstruct, 0)), room);
		}
		else
		{
			sqlstring = composeSqlStatement("delete from tmp_itemtable "
			"where (id=%i) and "
			"(search='%x') and "
			"(room=%i) and "
			"(containerid = 0)"
			, itemid, fcommand+(getToken(fmudstruct, 1)-getToken(fmudstruct, 0)), room);
		}
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	WriteSentenceIntoOwnLogFile(logname, 
		"You search %s and you find a %s, %s %s.<BR>\r\n", 
		fcommand+(getToken(fmudstruct, 1)-getToken(fmudstruct, 0)), itemadject1, itemadject2, itemname);
	WriteMessage(name, room, "%s searches %s and finds a %s, %s %s.<BR>\r\n",
		name, fcommand+(getToken(fmudstruct, 1)-getToken(fmudstruct, 0)), itemadject1, itemadject2, itemname);
	WriteRoom(fmudstruct);
	return 1;
}

//! give an item to a person
/*! correct appelation: <I>give [amount] &lt;item&gt; to &lt;person&gt;</I>
*/
int
GiveItem_Command(mudpersonstruct *fmudstruct)
{
	/*
	* give [amount] <item> to <person> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100], toname[40];
	char itemname[40], itemadject1[40], itemadject2[40];
	int amount, changedrows, itemid, amountitems, numberfilledout, containerid;
	char *checkerror;
	char *name;
	char *password;
	int room;
	name = fmudstruct->name;
	password = fmudstruct->password;
	room  = fmudstruct->room;
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	if (getTokenAmount(fmudstruct) < 4)
	{
		return 0;
	}
	if (strcasecmp(getToken(fmudstruct, getTokenAmount(fmudstruct)-2), "to"))
	{
		return 0;
	}

	/* look for specific person */
	sqlstring = composeSqlStatement("select name from tmp_usertable where (name = '%x') and "
		"(name <> '%x') and "
		"(room = %i)",getToken(fmudstruct, getTokenAmount(fmudstruct)-1), name, room);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	strcpy(toname, row[0]);
	mysql_free_result(res);

	amount = strtol(getToken(fmudstruct, 1), &checkerror, 10);
	numberfilledout=1;
	if (*checkerror!='\0')
	{
		amount=1;numberfilledout=0;
	}
	if (amount<1) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Negative amounts are not allowed.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
		if (getTokenAmount(fmudstruct)==4+numberfilledout) 
		{
			/*give pick to Karn*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			" where (items.name='%x') and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.wearing='') and "
			"(tmpitems.wielding='')"
			, getToken(fmudstruct, 1+numberfilledout), amount, name);
		}
		if (getTokenAmount(fmudstruct)==5+numberfilledout) 
		{
			/*give iron pick to Karn*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.wearing='') and "
			"(tmpitems.wielding='')"
			,getToken(fmudstruct, numberfilledout+2), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			amount, name);
		}
		if (getTokenAmount(fmudstruct)==6+numberfilledout) 
		{
			/*give iron strong pick to Karn*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			"where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.wearing='') and "
			"(tmpitems.wielding='')"
			,getToken(fmudstruct, numberfilledout+3), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
			amount, name);
		}
		if (getTokenAmount(fmudstruct)>=7+numberfilledout) 
		{
			/*give iron strong pick to Karn*/
			sqlstring = composeSqlStatement("select items.id, tmpitems.amount, items.name, items.adject1, items.adject2, tmpitems.containerid "
			"from items, tmp_itemtable tmpitems "
			" where (name='%x') and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"( (adject1='%x') or "
			"  (adject2='%x') or "
			"  (adject3='%x') ) and "
			"(items.id=tmpitems.id) and "
			"(tmpitems.amount>=%i) and "
			"(tmpitems.belongsto='%x') and "
			"(tmpitems.wearing='') and "
			"(tmpitems.wielding='')"
			,getToken(fmudstruct, numberfilledout+4), 
			getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1), getToken(fmudstruct, numberfilledout+1),
			getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2), getToken(fmudstruct, numberfilledout+2),
			getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3), getToken(fmudstruct, numberfilledout+3),
			amount, name);
		}
		res=sendQuery(sqlstring, &changedrows);
		free(sqlstring);sqlstring=NULL;
		if (res==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		row = mysql_fetch_row(res);
		if (row==NULL) 
		{
			WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
			WriteRoom(fmudstruct);
			return 1;
		}
		itemid = atoi(row[0]);
		amountitems = atoi(row[1]);
		strcpy(itemname, row[2]);
		strcpy(itemadject1, row[3]);
		strcpy(itemadject2, row[4]);
		containerid = atoi(row[5]);
		
		mysql_free_result(res);

		if (containerid != 0)
		{
			/* cool! the thing is a container, and is containing something! */
			sqlstring = composeSqlStatement("update tmp_itemtable set belongsto='%x' "
				"where (id=%i) and "
				"(belongsto='%x') and "
				"(wearing='') and "
				"(wielding='') and "
				"(containerid = %i)"
				, toname, itemid, name, containerid);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
		}
		else
		{
			/*give pick to Karn*/
			sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount+%i "
				"where (id=%i) and "
				"(belongsto='%x') and "
				"(wearing='') and "
				"(wielding='') and "
				"(containerid = 0)"
				, amount, itemid, toname);
			res=sendQuery(sqlstring, &changedrows);
			free(sqlstring);sqlstring=NULL;
			mysql_free_result(res);
	
			if (!changedrows) 
			{
				int changedrows2;
				sqlstring = composeSqlStatement("insert into tmp_itemtable (id, search, belongsto, amount, room, wearing, wielding)"
				" values(%i,'','%x',%i,0,'','')"
				, itemid, toname, amount);
				res=sendQuery(sqlstring, &changedrows2);
				free(sqlstring);sqlstring=NULL;
				if (!changedrows2)
				{
					WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
					WriteRoom(fmudstruct);
					return 1;
				}
				mysql_free_result(res);
		}
	
		if (amountitems>amount)
		{
			sqlstring = composeSqlStatement("update tmp_itemtable set amount=amount-%i "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(wearing='') and "
			"(wielding='') and "
			"(containerid = 0)"
			, amount, itemid, name);
		}
		else
		{
			sqlstring = composeSqlStatement("delete from tmp_itemtable "
			"where (id=%i) and "
			"(belongsto='%x') and "
			"(wearing='') and "
			"(wielding='') and "
			"(containerid = 0)"
			, itemid, name);
		}
		res=sendQuery(sqlstring, NULL);
		free(sqlstring);sqlstring=NULL;
		mysql_free_result(res);
	}
	
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
	WriteRoom(fmudstruct);
}

//! read an item, hidden or otherwise
/*! correct appelation: <I>read &lt;item&gt;</I>
*/
int
Read_Command(mudpersonstruct *fmudstruct)
{
	/*
	* read <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;
	char logname[100], mysex[10];
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
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	
	/* look for specific person */
	sqlstring = composeSqlStatement("select sex from tmp_usertable where (name = '%x')"
	, name);
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	row = mysql_fetch_row(res);
	strcpy(mysex, row[0]);
	mysql_free_result(res);
	
	if (getTokenAmount(fmudstruct)==2) 
	{
		/*put pick*/
		sqlstring = composeSqlStatement("select items.name, items.adject1, items.adject2, items.readdescr from items, tmp_itemtable tmpitems "
		" where (items.name='%x') and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=0) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		, getToken(fmudstruct, 1), room);
	}
	if (getTokenAmount(fmudstruct)==3) 
	{
		/*get iron pick*/
		sqlstring = composeSqlStatement("select items.name, items.adject1, items.adject2, items.readdescr from items, tmp_itemtable tmpitems "
		"where (name='%x') and "
		"( (adject1='%x') or "
		"(adject2='%x') or "
		"(adject3='%x') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,getToken(fmudstruct, 2), 
		getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
		room);
	}
	if (getTokenAmount(fmudstruct)==4) 
	{
		/*get iron strong pick*/
		sqlstring = composeSqlStatement("select items.name, items.adject1, items.adject2, items.readdescr from items, tmp_itemtable tmpitems "
		"where (name='%x') and "
		"( (adject1='%x') or "
		"(adject2='%x') or "
		"(adject3='%x') ) and "
		"( (adject1='%x') or "
		"(adject2='%x') or "
		"(adject3='%x') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,getToken(fmudstruct, 3), 
		getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
		getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
		room);
	}
	if (getTokenAmount(fmudstruct)>=5) 
	{
		/*get iron strong strong pick*/
		sqlstring = composeSqlStatement("select items.name, items.adject1, items.adject2, items.readdescr from items, tmp_itemtable tmpitems "
		" where (name='%x') and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='') and "
		"(tmpitems.room = %i) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,getToken(fmudstruct, 4), 
		getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
		getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
		getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
		room);
	}
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
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
			LookString(row[3], name, password, fmudstruct->frames, fmudstruct->socketfd);
			return 1;
		}
	}
	
	mysql_free_result(res);

	if (getTokenAmount(fmudstruct)==2) 
	{
		/*put pick*/
		sqlstring = composeSqlStatement("select items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding, items.readdescr from items, tmp_itemtable tmpitems "
		" where (items.name='%x') and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=0) and "
		"(tmpitems.belongsto='%x') and "
		"(tmpitems.room = 0) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		, getToken(fmudstruct, 1), name);
	}
	if (getTokenAmount(fmudstruct)==3) 
	{
		/*get iron pick*/
		sqlstring = composeSqlStatement("select items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding, items.readdescr from items, tmp_itemtable tmpitems "
		"where (name='%x') and "
		"( (adject1='%x') or "
		"(adject2='%x') or "
		"(adject3='%x') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='%x') and "
		"(tmpitems.room = 0) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,getToken(fmudstruct, 2), 
		getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
		name);
	}
	if (getTokenAmount(fmudstruct)==4) 
	{
		/*get iron strong pick*/
		sqlstring = composeSqlStatement("select items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding, items.readdescr from items, tmp_itemtable tmpitems "
		"where (name='%x') and "
		"( (adject1='%x') or "
		"(adject2='%x') or "
		"(adject3='%x') ) and "
		"( (adject1='%x') or "
		"(adject2='%x') or "
		"(adject3='%x') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='%x') and "
		"(tmpitems.room = 0) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,getToken(fmudstruct, 3), 
		getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
		getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
		name);
	}
	if (getTokenAmount(fmudstruct)>=5) 
	{
		/*get iron strong strong pick*/
		sqlstring = composeSqlStatement("select items.name, items.adject1, items.adject2, tmpitems.wearing, tmpitems.wielding, items.readdescr from items, tmp_itemtable tmpitems "
		" where (name='%x') and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"( (adject1='%x') or "
		"  (adject2='%x') or "
		"  (adject3='%x') ) and "
		"(items.id=tmpitems.id) and "
		"(tmpitems.amount>=1) and "
		"(tmpitems.belongsto='%x') and "
		"(tmpitems.room = 0) and "
		"(tmpitems.search = '') and "
		"(items.readdescr <> '')"
		,getToken(fmudstruct, 4), 
		getToken(fmudstruct, 1), getToken(fmudstruct, 1), getToken(fmudstruct, 1),
		getToken(fmudstruct, 2), getToken(fmudstruct, 2), getToken(fmudstruct, 2),
		getToken(fmudstruct, 3), getToken(fmudstruct, 3), getToken(fmudstruct, 3),
		name);
	}
	res=sendQuery(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	row = mysql_fetch_row(res);
	if (row==NULL) 
	{
		WriteSentenceIntoOwnLogFile(logname, "Item not found.<BR>\r\n");
		WriteRoom(fmudstruct);
		return 1;
	}
	
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
	LookString(row[5], name, password, fmudstruct->frames, fmudstruct->socketfd);
	mysql_free_result(res);
	return 1;
}

//! execute the dead
/*! show dead screen */
void 
Dead(char *name, char *password, int room, int frames, int socketfd)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	char logname[100];
	
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);

	send_printf(socketfd, "<HTML><HEAD><TITLE>Death</TITLE></HEAD>\n\n");
	if (!frames)
	{
		send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (frames==1)
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(socketfd, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
	send_printf(socketfd, "You died.<Center><H1>The End</H1></Center><P>");
	send_printf(socketfd, "You are dead. You are at the moment in a dark room. You can't see anything.");
	send_printf(socketfd, "You hear sighs.<P>");
	send_printf(socketfd, "A voice says (behind you): Oh no, not another one...<P>");
	send_printf(socketfd, "Another voice sighs : They just keep on comin'.<P>");
	send_printf(socketfd, "Suddenly, in the distance, you see a light which slowly comes closer. As it");
	send_printf(socketfd, "comes closer, you see that it is a lantern which is being held by somebody");
	send_printf(socketfd, "(or something you can't really make it out). That someone is getting closer.");
	send_printf(socketfd, "When he is standing right before you, you have a good view of who he is. You");
	send_printf(socketfd, "can't see his face, because that is hidden by a cap. He is all in black.<P>");
	send_printf(socketfd, "You say: Hey, did somebody just die?<P>");
	send_printf(socketfd, "He is carrying a heavy axe with him. This, you gather, must be Mr. Death");
	send_printf(socketfd, "himself. He swings back his axe, and you hide your hands behind your face.");
	send_printf(socketfd, "(That shouldn't however help much) Than the axe comes crushing down.<P>(Type");
	send_printf(socketfd, "<B>look around</B>)<P>");

	temp = composeSqlStatement("update tmp_usertable set vitals=0, sleep=0 where name='%x'",name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);
	WriteMessage(name, room, "%s appears from nowhere.<BR>", name);
	PrintForm(name, password, frames, socketfd);
	send_printf(socketfd, "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(socketfd, "<DIV ALIGN=left><P>");
}

//! change the title of the player
int
ChangeTitle_Command(mudpersonstruct *fmudstruct)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp, *title;
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
	sprintf(logname, "%s%s.log", getParam(MM_USERHEADER), name);
	title = fcommand+(getToken(fmudstruct, 1)-getToken(fmudstruct, 0));
	WriteSentenceIntoOwnLogFile(logname, "Title changed to : %s<BR>\n", title);
	temp = composeSqlStatement("update tmp_usertable set title='%x' where name='%x'",
		title, name);
	res=sendQuery(temp, NULL);
	free(temp);temp=NULL;
	mysql_free_result(res);
	WriteRoom(fmudstruct);
	return 1;
}				/* endproc */

