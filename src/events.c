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
#include <stdlib.h>
#include <string.h>
#include "typedefs.h"
#include "parser.h"

/*! \file events.c
	\brief  simple executable that starts up events depending on information 
in the database table "events". Will start up "methods" in the "methods" 
table. Usually this executable is put into your crontab. */

//! tokenizes the main command into seperate words
/*! tokenises the main command into separate words, space (" ") is used
    as the separator. Multiple spaces are interpreted as a single space.
    The way this works is that elements of the tokenarray will point to
    the start of the new words in the 'commandstring'. This means that
    the commandstring contains a string with multiple \0 breaks.
    The first token (tokenarray[0]) will always point to the first word
    in the commandstring, which is always the same address as commandstring.
    <P>
    Why not strtok? Strtok is dangerous and is not thread safe.
\param commandstring char* containing the command. Attention! This variable
will be changed!!!
\param tokenarray char** containing the different words, will never exceed 50 words.
\return int containing the amount of tokens entered into the array
*/
int tokenizer(char *commandstring, char **tokenarray)
{
	char *index;
	int number;
	tokenarray[0] = commandstring;
	number = 1;
	index = commandstring;
	while (*index != 0)
	{
		if (*index == ' ')
		{
			*index=0;
			if ((*(index+1) != ' ') && (*(index+1) != 0))
			{
				tokenarray[number++] = index+1;
				if (number == 50)
				{
					return number;
				}
			}
		}
		index++;
	}
	return number;
}

int 
main(int argc, char *argv[])
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	int room;

//	umask(0000);
	
	initParam();
	readConfigFiles("/karchan/config.xml");

	opendbconnection();

//	openDatabase();
	res=sendQuery("select src, room "
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
	      mudpersonstruct mypersonstruct;
			mypersonstruct.readbuf=NULL;
			strcpy(mypersonstruct.name,"anonymous");
			strcpy(mypersonstruct.password,"bogus");
			strcpy(mypersonstruct.address,"asd");
			strcpy(mypersonstruct.cookie, "bogus");
			mypersonstruct.frames=1;
			mypersonstruct.action = "mud";
			mypersonstruct.command = row[0];
	      mypersonstruct.socketfd = 0;
			mypersonstruct.newchar=NULL;
			mypersonstruct.memblock=strdup(mypersonstruct.command);
			mypersonstruct.tokenamount = tokenizer(mypersonstruct.memblock, mypersonstruct.tokens);
			mypersonstruct.next=NULL;
			room =  atoi(row[1]);
			mypersonstruct.room=room;
			Parse(&mypersonstruct, "anonymous", &room, "this is totally bogus stuff", row[0], 1);
			free(mypersonstruct.memblock);
			row = mysql_fetch_row(res);
		}
		mysql_free_result(res);
	}

	closedbconnection();
	freeParam();
	return 0;
}
