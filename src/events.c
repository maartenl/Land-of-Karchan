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
#include "typedefs.h"
#include "parser.h"

/*! \file events.c
	\brief  simple executable that starts up events depending on information 
in the database table "events". Will start up "methods" in the "methods" 
table. Usually this executable is put into your crontab. */

int 
main(int argc, char *argv[])
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int room;
	char method_name[32];

	umask(0000);
	
	initParam();
	readConfigFiles("/karchan/config.xml");

	opendbconnection();
	setMMudOut(0);

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
			Parse("anonymous", &room, "this is totally bogus stuff", row[0]);
			row = mysql_fetch_row(res);
		}
		mysql_free_result(res);
	}

	closedbconnection();
	freeParam();
	
}
