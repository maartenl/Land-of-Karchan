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
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>
#include <stdarg.h>
#include <sys/file.h>
#include <signal.h>		// for catching a signal or two
#include "userlib.h"
#include "/karchan2/mysql/include/mysql/mysql.h"

#define infinity 0

#define CNAME 0  
#define CADDRESS 1  
#define CPASSWORD 2   
#define CTITLE 3  
#define CREALNAME 4   
#define CEMAIL 5  
#define CRACE 6 
#define CSEX 7  
#define CAGE 8   
#define CLENGTH 9  
#define CWIDTH 10 
#define CCOMPLEXION 11  
#define CEYES 12
#define CFACE 13 
#define CHAIR 14 
#define CBEARD 15 
#define CARM 16 
#define CLEG 17
#define CGOLD 18 
#define CSILVER 19  
#define CCOPPER 20 
#define CROOM 21 
#define CLOK 22 
#define CWHIMPY 23  
#define CEXPERIENCE 24  
#define CFIGHTINGWHO 25  
#define CSLEEP 26  
#define CPUNISHMENT 27
#define CFIGHTABLE 28 
#define CVITALS 29 
#define CFYSICALLY 30
#define CMENTALLY 31 
#define CDRINKSTATS 32
#define CEATSTATS 33 
#define CACTIVE 34
#define CLASTLOGIN 35
#define CBIRTH 36 
#define CGOD 37
#define CGUILD 38
#define CSTRENGTH 39 
#define CINTELLIGENCE 40 
#define CDEXTERITY 41 
#define CCONSTITUTION 42 
#define CWISDOM 43 
#define CPRACTISES 44 
#define CTRAINING 45 
#define CBANDAGE 46 
#define CALIGNMENT 47 
#define CMANASTATS 48 
#define CMOVEMENTSTATS 49 
#define CMAXMANA 50
#define CMAXMOVE 51 
#define CMAXVITAL 52 
#define CJUMPMANA 70 
#define CJUMPMOVE 71 
#define CJUMPVITAL 72 

int debuggin=1;

struct nameslist {
	char name[40];
	char toname[40];
} pnameslist[20];
int number_of_Names=0;

void myLog(char *filenaam, char *fmt,...)
{
	FILE *filep;
	va_list ap;
	char *s;
	int i;
	char c;
	time_t tijd;
	struct tm datum;

	filep = fopen(filenaam, "a");
	time(&tijd);
	datum=*(gmtime(&tijd));
	fprintf(filep, "[%i:%i:%i %i-%i-%i] ", 
	datum.tm_hour,datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900);
	va_start(ap, fmt);
	(void) vfprintf(filep, fmt, ap);
	va_end(ap);
	fclose(filep);
}

void mysqlExitErr(char *sqlstring, MYSQL *mysql)
{
myLog("/karchan/mud/data/error.log", "bg: MySQL Error : %s\n {%s}\n", mysql_error(mysql), sqlstring );
}

void error(char *string)
{
myLog("/karchan/mud/data/error.log", "bg: Fatal Error %s\n", string );
exit(-1);
}

int StartSQL()
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row, row2;
	FILE *fp;
	int counter=0;
	char logname[100];
	uint i = 0;
	
	char sqlstring[1024];

	strcpy(sqlstring, "");
	if (!(mysql_connect(&mysql,"localhost","root", ""))) 
	{
		mysqlExitErr("Unable to connect to mysqld, exiting...", &mysql);
		exit(1);
	}
 
	if (mysql_select_db(&mysql,"mud"))
	{
		mysqlExitErr("Unable to connect to database, exiting...", &mysql);
		exit(1);
	}
 
 	while (!infinity) 
 	{
	if (mysql_query(&mysql,
		"select name, fightingwho from tmp_usertable where fightingwho<>'' and "
		"sleep=0 and god<>2"))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}
 
	if (!(res = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	if (mysql_num_rows(res)==0)
	{
		mysql_free_result(res);
		if (debuggin==2) {myLog("/karchan/mud/data/error.log", "Nobody fighting, sleeping 5 sec.\n");}
		sleep(5);
	}
	else
	{
		int i,checkbot=0;
		number_of_Names=mysql_num_rows(res);
//		myLog("/karchan/mud/data/error.log", "Number of rows: %i\n", number_of_Names);
		for (i=0;i<number_of_Names;i++)
		{
			row = mysql_fetch_row(res);
			strcpy(pnameslist[i].name, row[0]);
			strcpy(pnameslist[i].toname, row[1]);
			if (debuggin==2) {myLog("/karchan/mud/data/error.log", "Name: %s, Row[0]: %s, Toname: %s, Row[1]: %s\n",pnameslist[i].name, row[0],pnameslist[i].toname, row[1]);}
		}
		mysql_free_result(res);
		i=0;
		while ((i<number_of_Names) && (!checkbot))
		{
			int hitcount, room, experience;
			char race[40];
			checkbot=0;
			sprintf(sqlstring, "select * from tmp_usertable where name='%s' or name='%s'", 
				pnameslist[i].name, pnameslist[i].toname);
			mysql_query(&mysql, sqlstring);
			res = mysql_store_result(&mysql);
			row = mysql_fetch_row(res);
			if (!strcmp(row[CNAME], pnameslist[i].toname))
			{
				row2 = row;
				row = mysql_fetch_row(res);
			}
			else
			{
				row2 = mysql_fetch_row(res);
			}
			hitcount=0;
			room=atoi(row[CROOM]);
			if (!strcmp(row[CROOM], row2[CROOM]))
			{
				int hithow = random() % 7;
				hitcount=random() % 10;
				if (atoi(row[CVITALS])<atoi(row2[CVITALS])) {hitcount--;}
				hitcount=hitcount+atoi(row[CSTRENGTH]);
				if (hitcount<0) {hitcount=0;}
				
				if (hitcount==0)
				{
					sprintf(logname, "%s%s.log", USERHeader, pnameslist[i].name);
					WriteSentenceIntoOwnLogFile2(logname, "You try to hit %s, but you miss.<BR>\r\n", pnameslist[i].toname);
 						sprintf(logname, "%s%s.log",USERHeader,pnameslist[i].toname);
					WriteSentenceIntoOwnLogFile2(logname, "%s tries to hit you, but misses.<BR>\r\n", pnameslist[i].name);
					WriteMessageTo2(pnameslist[i].toname, pnameslist[i].name, atoi(row[CROOM]), "%s tries to hit %s, but misses.<BR>\r\n", 
					pnameslist[i].name,pnameslist[i].toname);
					strcpy(race, row2[CRACE]);
					experience=atoi(row[CEXPERIENCE]);
					if (debuggin) {printf("%s (%s) misses %s (%s).\n",
							row[CNAME], row[CVITALS], row2[CNAME], row2[CVITALS]);}
				}
				else 
				{
					sprintf(logname, "%s%s.log", USERHeader, pnameslist[i].name);
					WriteSentenceIntoOwnLogFile2(logname, "You hit %s, with your %s.<BR>\r\n", pnameslist[i].toname, ShowHittingStuff(hithow, row[CRACE]));
					WriteSentenceIntoOwnLogFile2(logname, "%s seems%s.<BR>\r\n", HeShe(row2[CSEX]), ShowString(atoi(row2[CVITALS]), atoi(row2[CMAXVITAL])));
					sprintf(logname, "%s%s.log",USERHeader,pnameslist[i].toname);
					WriteSentenceIntoOwnLogFile2(logname, "%s hits you, with %s %s. (<FONT COLOR=green>%.2f %%</FONT>)<BR>\r\n", pnameslist[i].name, HeShe3(row[CSEX]), 
					ShowHittingStuff(hithow, row[CRACE]), 100.0 - (atoi(row2[CVITALS])+0.0) / (atoi(row2[CMAXVITAL])+0.0) * 100.0);
					WriteMessageTo2(pnameslist[i].toname, pnameslist[i].name, atoi(row[CROOM]), "%s hits %s, with %s %s.<BR>\r\n", 
					pnameslist[i].name,pnameslist[i].toname, HeShe3(row[CSEX]), ShowHittingStuff(hithow, row[CRACE]));
					if (atoi(row2[CVITALS])>atoi(row2[CMAXVITAL])) {checkbot++;}
					if ((atoi(row2[CGOD])==3) && (checkbot==1)) {checkbot++;}
					strcpy(race, row2[CRACE]);
					experience=atoi(row[CEXPERIENCE]);
					if ((checkbot==0) && (atoi(row2[CWHIMPY])!=0))
					{
						if (atoi(row2[CVITALS])>atoi(row2[CWHIMPY])) {checkbot=4;}
					}
					if (debuggin) {myLog("/karchan/mud/data/error.log", "%s (%s) hits %s (%s). Strength %i.\n",
							row[CNAME], row[CVITALS], row2[CNAME], row2[CVITALS], hitcount);}
				}
			}
			mysql_free_result(res);
			sprintf(sqlstring, "update tmp_usertable set vitals=vitals+%i "
			"where name='%s'", hitcount, pnameslist[i].toname);
			mysql_query(&mysql, sqlstring);
			
			if ((checkbot==2) || (checkbot==1))
			{
				sprintf(logname, "%s%s.log",USERHeader,pnameslist[i].toname);
				WriteSentenceIntoOwnLogFile2(logname, "%s kills you. <BR>\r\n", pnameslist[i].name);
				WriteMessageTo2(pnameslist[i].toname, pnameslist[i].name, room, "%s sends %s to meet death.<BR>\r\n", 
					pnameslist[i].name, pnameslist[i].toname);
				sprintf(logname, "%s%s.log", USERHeader, pnameslist[i].name);
				WriteSentenceIntoOwnLogFile2(logname, "You send %s to meet death.<BR>\r\n", pnameslist[i].toname);
				if (debuggin) {myLog("/karchan/mud/data/error.log", "%s killed by %s. (bot %i)\n",pnameslist[i].toname, pnameslist[i].name, checkbot==2);}
			}
			
			if (checkbot==1)
			{
				int itemid=40;
				sprintf(sqlstring, "update tmp_usertable set room=1, fightingwho='',"
				" experience=experience-round((experience % 1000)/2) "
				"where name='%s'", pnameslist[i].toname);
				mysql_query(&mysql, sqlstring);
				sprintf(sqlstring, "update tmp_usertable set fightingwho='' "
				"where name='%s'", pnameslist[i].name);
				mysql_query(&mysql, sqlstring);
				if (!strcmp(race, "human")) {itemid=40;}
				if (!strcmp(race, "dwarf")) {itemid=41;}
				if (!strcmp(race, "elf")) {itemid=42;}
				
				sprintf(sqlstring, "update tmp_itemtable set amount=amount+1 "
				"where (id=%i) and "
				"(search='') and "
				"(belongsto='') and "
				"(wearing='') and "
				"(wielding='') and "
				"(room=%i)", itemid, room);
				if (mysql_query(&mysql, sqlstring)==0)
				{
					sprintf(sqlstring, "insert into tmp_itemtable "
					"values(%i, '','',1,%i, '','')", itemid, room);
					mysql_query(&mysql, sqlstring);
				}
			} /* endif dead */
			if (checkbot==2)
			{
				int numberrows;
				int itemid=43, experiencegain=40, traininggain=0, practisegain=0;
			sprintf(sqlstring, "select id from items where name='corpse' and adject1='dead' and adject3='killed' and adject2='%s'", 
				race);
			mysql_query(&mysql, sqlstring);
			res = mysql_store_result(&mysql);
			row = mysql_fetch_row(res);itemid=atoi(row[0]);
			mysql_free_result(res);
				sprintf(sqlstring, "delete from tmp_usertable where name='%s'", pnameslist[i].toname);
				mysql_query(&mysql, sqlstring);
				sprintf(sqlstring, "update tmp_usertable set fightingwho='' where fightingwho='%s'", pnameslist[i].toname);
				mysql_query(&mysql, sqlstring);
				if ((experience % 1000)+experiencegain>=1000)
				{
					WriteSentenceIntoOwnLogFile2(logname, "You have levelled!!! You are now level %i.<BR>\r\n", (experience % 1000) + 1);
					traininggain=1;practisegain=3;
				}
				sprintf(sqlstring, "update tmp_usertable set experience=experience+%i, "
				"training=training+%i, practises=practises+%i "
				"where name='%s'", experiencegain, traininggain, practisegain, pnameslist[i].name);
				mysql_query(&mysql, sqlstring);
				WriteSentenceIntoOwnLogFile2(logname, "You gain %i experience.<BR>\r\n", experiencegain);
				sprintf(sqlstring, "update tmp_itemtable set amount=amount+1 "
				"where (id=%i) and "
				"(search='') and "
				"(belongsto='') and "
				"(wearing='') and "
				"(wielding='') and "
				"(room=%i)", itemid, room);
				if (mysql_query(&mysql, sqlstring)==0)
				{
					sprintf(sqlstring, "insert into tmp_itemtable "
					"values(%i, '','',1,%i, '','')", itemid, room);
					mysql_query(&mysql, sqlstring);
				}
			} /* endif dead and bot */
			if (checkbot==4)
			{
				char toroom[10], direction[10];
				int dirint;
				sprintf(logname, "%s%s.log", USERHeader, pnameslist[i].toname);
				sprintf(sqlstring, "select north, south, east, west from rooms "
				"where id=%i", room);
				mysql_query(&mysql, sqlstring);
				res = mysql_store_result(&mysql);
				row = mysql_fetch_row(res);
				dirint = random() % 4;
				strcpy(toroom, row[dirint]);
				switch (dirint)
				{
					case 0 : {strcpy(direction, "north");break;}
					case 1 : {strcpy(direction, "south");break;}
					case 2 : {strcpy(direction, "east");break;}
					case 3 : {strcpy(direction, "west");break;}
				}
				if (!strcmp(toroom, "0"))
				{
					checkbot=0;
					WriteSentenceIntoOwnLogFile2(logname, "You try to flee, but fail!<BR>\r\n");
					WriteMessage2(pnameslist[i].toname, room, "%s tries to flee, but fails!!!<BR>\r\n", pnameslist[i].toname);
					if (debuggin) {myLog("/karchan/mud/data/error.log", "%s tries to flee, fails.\n", pnameslist[i].toname);}
				} 
				else
				{
					WriteSentenceIntoOwnLogFile2(logname, "You flee %s.<BR>\r\n", direction);
					WriteMessage2(pnameslist[i].toname, room, "%s flees %s.<BR>\r\n", pnameslist[i].toname, direction);
					sprintf(sqlstring, "update tmp_usertable set room=%s, experience=abs(experience-20) "
						"where name='%s'", toroom, pnameslist[i].toname);
					mysql_query(&mysql, sqlstring);
					room=atoi(toroom);
					WriteMessage2(pnameslist[i].toname, room, "%s appears, running.<BR>\r\n", pnameslist[i].toname);
					if (debuggin) {myLog("/karchan/mud/data/error.log", "%s flees %s.\n", pnameslist[i].toname, direction);}
				}
			} /* endif flee */
			i++;
		}
		if (debuggin==2) {myLog("/karchan/mud/data/error.log", "Round done, sleeping 2 secs.\n");}
		sleep(2);
	}
	
	} /*end of while(!infinity)*/
 
	mysql_close(&mysql);
}

int searchParam(char *term, int cnt, char **prm)
{
	int i=0;
	while (i<cnt) 
	{
		if (!strcmp(prm[i],term)) {return i;}
		i++;
	}
	return 0;
}

void sigpipe(int c)
{
	error("Sigpipe received.");
}

void sighup(int c)
{
	error("SIGHUP detected...");
}

void sigquit(int c)
{
	error("Quit signal received.");
}

void sigill(int c)
{
	error("Illegal instruction.");
}

void sigfpe(int c)
{
	error("Floating Point Error.");
}

void sigbus(int c)
{
	error("Bus Error.");
}

void sigsegv(int c)
{
	error("Segmentation Violation.");
}

void sigsys(int c)
{
	error("Bad system call.");
}

void sigterm(int c)
{
	error("Terminate signal received.");
}

void sigxfsz(int c)
{
	error("File descriptor limit exceeded.");
}

void sigchld(int c)
{
	myLog("/karchan/mud/data/error.log", "Received SIGCHLD... Continueing execution...");
}

int
main(int cntp, char **prm)
{
	FILE *fp;
	int i;
	char *pidfile;

	signal(SIGPIPE, sigpipe);
	signal(SIGHUP, sighup);
	signal(SIGQUIT, sigquit);
	signal(SIGILL, sigill);
	signal(SIGFPE, sigfpe);
	signal(SIGBUS, sigbus);
	signal(SIGSEGV, sigsegv);
//	signal(SIGSYS, sigsys);
	signal(SIGTERM, sigterm);
	signal(SIGXFSZ, sigxfsz);
	signal(SIGCHLD, sigchld);
	
	myLog("/karchan/mud/data/error.log", "mmudbackground process started...\n");
	
	if ( (searchParam("-h",cntp, prm)) || (searchParam("--help",cntp, prm)) )
	{
		printf("mmudbackground [-h/--help] [-p /var/run/mmudbackground.pid] [-d/--debug]\n\n");
		return 0;
	}
	
	if ( (searchParam("-d",cntp, prm)) || (searchParam("--debug",cntp, prm)) )
	{
		debuggin=1;
	}
	
	i=searchParam("-p", cntp, prm);
	if ((i) && (i<cntp-1))
	{
		pidfile=prm[i+1];
	}
	else
	{
		pidfile="/tmp/mmudbackground.pid";
	}

	fp=fopen(pidfile, "w");
	fprintf(fp, "%i\n", getpid());
	fclose(fp);
	chmod(pidfile, 0600);

	StartSQL();
	
	return 0;
}
