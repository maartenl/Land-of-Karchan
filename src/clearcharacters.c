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
/*#include "/karchan2/mysql/include/mysql/mysql.h"*/
#include "/usr/local/include/mysql/mysql.h"

#define debuggin 0

void exiterr(int exitcode, char *sqlstring, MYSQL *mysql)
{
printf("Error %i: %s\n {%s}\n", exitcode, mysql_error(mysql), sqlstring );
}

int SendSQL()
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
	FILE *fp;
	uint i = 0;
	
	char sqlstring[1024];
//	strcpy(sqlstring, "select name "
//	" from tmp_usertable where "
//	" god=0 and "
//	"abs(hour(lastlogin)*60+minute(lastlogin)-(hour(now())*60+minute(now())) ) > 180"
//	"");

	strcpy(sqlstring, " select name, lastlogin, "
	"time_to_sec(date_sub(NOW(), INTERVAL 2 HOUR))-time_to_sec(lastlogin) "
	"from tmp_usertable where lastlogin < date_sub(NOW(), INTERVAL 3 HOUR) "
	"and god=0");
	if (!(mysql_connect(&mysql,"localhost","root", ""))) 
		exiterr(1, sqlstring, &mysql);
 
	if (mysql_select_db(&mysql,"mud"))
		exiterr(2, sqlstring, &mysql);
 
	if (mysql_query(&mysql,sqlstring))
		exiterr(3, sqlstring, &mysql);
 
	if (!(res = mysql_store_result(&mysql)))
		{
			exiterr(4, sqlstring, &mysql);
		} 
		else 
		{
		time_t tijd;
		struct tm datum;
		time(&tijd);
		datum=*(gmtime(&tijd));
		while((row = mysql_fetch_row(res))) {
			printf("%i:%i:%i %i-%i-19%i %s deactivated by system (last active %s, %i min. idle)<BR>\r\n",
			datum.tm_hour,datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year,
			row[0], row[1], atoi(row[2])/60);
			// Fighting who generates Seg Faults if someone is fighting non-active person
			sprintf(sqlstring, "update tmp_usertable set fightingwho='' where fightingwho='%s'", row[0]);
			if (debuggin) {printf("[%s]\n", sqlstring);}
			mysql_query(&mysql,sqlstring);
			sprintf(sqlstring, "delete from tmp_mailtable where toname='%s'", row[0]);
			if (debuggin) {printf("[%s]\n", sqlstring);}
			mysql_query(&mysql,sqlstring);
			sprintf(sqlstring, "delete from itemtable where belongsto='%s'", row[0]);
			if (debuggin) {printf("[%s]\n\n", sqlstring);}
			mysql_query(&mysql,sqlstring);
			sprintf(sqlstring, "replace into itemtable select * from tmp_itemtable where belongsto='%s'", row[0]);
			if (debuggin) {printf("[%s]\n\n", sqlstring);}
			mysql_query(&mysql,sqlstring);
			sprintf(sqlstring, "replace into usertable select * from tmp_usertable where name='%s'", row[0]);
			if (debuggin) {printf("[%s]\n\n", sqlstring);}
			mysql_query(&mysql,sqlstring);
			sprintf(sqlstring, "delete from tmp_itemtable where belongsto='%s'", row[0]);
			if (debuggin) {printf("[%s]\n\n", sqlstring);}
			mysql_query(&mysql,sqlstring);
		}
	 
	if (!mysql_eof(res))
		exiterr(5, sqlstring, &mysql);
 
	sprintf(sqlstring, "delete from tmp_usertable where "
	"god = 0 and "
	"lastlogin < date_sub(NOW(), INTERVAL 3 HOUR)");
	if (debuggin) {printf("[%s]\n", sqlstring);}
	mysql_query(&mysql,sqlstring);

	mysql_free_result(res);}
	mysql_close(&mysql);
}

void
main()
{
	SendSQL();
}
