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
Driek van Erpstraat 9
5341 AK Oss
Nederland
Europe
maartenl@il.fontys.nl
-------------------------------------------------------------------------*/
#include <time.h>
#include "mud-lib3.h"

int 
cgiMain()
{
	struct tm datumtijd;
	time_t   datetime;
	MYSQL_RES *res;
	MYSQL_ROW row;
	int room;
	char method_name[32];

	umask(0000);

	opendbconnection();
	setMMudOut(stdout);

//	openDatabase();
	res=SendSQL2("select src, room "
	"from events, methods "
	"where callable = 1 "
	"and methods.name = events.method_name "
	"and ( month = -1 or month = MONTH(NOW()) ) "
	"and ( dayofmonth = -1 or dayofmonth = DAYOFMONTH(NOW()) ) "
	"and ( hour = -1 or hour = HOUR(NOW()) ) "
	"and ( minute = -1 or minute = MINUTE(NOW()) ) "
	"and ( dayofweek = -1 or dayofweek = DAYOFWEEK(NOW()) )"
	, NULL);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		while (row != NULL)
		{
			room =  atoi(row[1]);
			Parse("anonymous", &room, row[0]);
			row = mysql_fetch_row(res);
		}
		mysql_free_result(res);
	}

	closedbconnection();

}
