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
#include "typedefs.h"

#define debuggin 0

int clearInactiveCharacters()
{
	MYSQL_RES *res, *res2;
	MYSQL_ROW row;
	FILE *fp;
	uint i = 0;
	
	opendbconnection();
	
	res = executeQuery(NULL, " select name, lastlogin, "
	"time_to_sec(date_sub(NOW(), INTERVAL 2 HOUR))-time_to_sec(lastlogin) "
	"from tmp_usertable where lastlogin < date_sub(NOW(), INTERVAL 3 HOUR) "
	"and god=0");

	if (res != NULL)
	{
		time_t tijd;
		struct tm datum;
		time(&tijd);
		datum=*(gmtime(&tijd));
		while((row = mysql_fetch_row(res))) 
		{
			printf("%i:%i:%i %i-%i-%i %s deactivated by system (last active %s, %i min. idle)<BR>\r\n",
			datum.tm_hour,datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,
			row[0], row[1], atoi(row[2])/60);
			// Fighting who generates Seg Faults if someone is fighting non-active person
			mysql_free_result(executeQuery(NULL, "update tmp_usertable set fightingwho='' where fightingwho='%x'", row[0]));
			mysql_free_result(executeQuery(NULL, "delete from tmp_mailtable where toname='%x'", row[0]));
			mysql_free_result(executeQuery(NULL, "delete from itemtable where belongsto='%x'", row[0]));
			mysql_free_result(executeQuery(NULL, "replace into itemtable select * from tmp_itemtable where belongsto='%x'", row[0]));
			mysql_free_result(executeQuery(NULL, "replace into usertable select * from tmp_usertable where name='%x'", row[0]));
			mysql_free_result(executeQuery(NULL, "delete from tmp_itemtable where belongsto='%x'", row[0]));
		}

		mysql_free_result(res);

		mysql_free_result(executeQuery(NULL, "delete from tmp_usertable where "
		"god = 0 and lastlogin < date_sub(NOW(), INTERVAL 3 HOUR)"));

	}
	
	closedbconnection();
}

int
main()
{
	initParam();
	readConfigFiles("/karchan/config.xml");
	clearInactiveCharacters();
	freeParam();
	return 0;
}
