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
#include <stdlib.h>
#include <string.h>
#include "typedefs.h"
#include "userlib.h"
#include "mudmain.h"
#include "mudlogon.h"
#include "mudnewchar.h"

/*! \file mudnewchar.c
	\brief  part of the server that takes care of creating a new character.
*/

#ifndef MEMMAN
#define mud_malloc(A,B,C)	malloc(A)
#define mud_free(A)		free(A)
#define mud_strdup(A,B,C)	strdup(A)
#define mud_realloc(A,B)	realloc(A,B)
#endif

//extern char secretpassword[40];

//! create a new structure for containing the information for creating a new character
mudnewcharstruct *create_mudnewcharstruct()
{
	mudnewcharstruct *mynewstruct;
	
	mynewstruct = (mudnewcharstruct *) mud_malloc(sizeof(mudnewcharstruct), __LINE__, __FILE__);
	if (mynewstruct == NULL)
	{
		printf("Fatal - Unable to allocate memory for mudnewcharstruct...\n");
		abort();
	}
	memset(mynewstruct, 0, sizeof(mudnewcharstruct));
	mynewstruct->ftitle = mynewstruct->frealname = mynewstruct->femail = NULL;
	return mynewstruct;
}

//! main function for creating a new character
/*! function will create a new character from data provided in the parameter list
\param socketfd int, contains the socket descriptior, can be used to retrieve information about the current player
by means of get_from_list
\see get_from_list
\returns int containing success (!=0) or failure (0).
*/
int 
gameNewchar(int socketfd)
{
	int i;
	char *name;
	char *address = NULL;
	char *password;
	char secretpassword[26];
	mudpersonstruct *mymudstruct;
	mudnewcharstruct *infostruct;
	
	mymudstruct = find_in_list(socketfd);
	name = mymudstruct->name;
	password = mymudstruct->password;
	infostruct = (mudnewcharstruct *) mymudstruct->newchar;
#ifdef DEBUG
	printf("gameNewchar started(%s, %s, %s)!!!\n", name, password, address);
#endif

	/* send_printf(fmudstruct->socketfd, "[%s]", getenv("HTTP_COOKIE"));*/
	generate_password(secretpassword);

	if (SearchBanList(address, name)) 
	{
		BannedFromGame(name, address, socketfd);
		return 0;
	}

	if (StrangeName(name, password, address, socketfd) == 0)
	{
		return 0;
	}

	/* check if user already exists, if so -> exit */
	if (SearchUser(name)==1) 
	{
		WrongPasswd(name, address, "Wrong password detected during creation of new character", socketfd);
		return 0;
	}

	executeQuery(&i, "insert into usertable "
	"(name, password, title, realname, email, race, sex, age, length, width, complexion, eyes, face, hair, beard, arm, leg, lok, lastlogin, birth) "
		"values('%x','%x','%x','%x','%x','%x','%x','%x','%x','%x','%x','%x','%x','%x','%x','%x','%x','%x', DATE_SUB(NOW(), INTERVAL 2 HOUR), "
		"DATE_SUB(NOW(), INTERVAL 2 HOUR))",
		name, password, infostruct->ftitle, infostruct->frealname,
		infostruct->femail, infostruct->frace, 
		infostruct->fsex,
		infostruct->fage, infostruct->flength, infostruct->fwidth,
		infostruct->fcomplexion, infostruct->feyes, infostruct->fface,
		infostruct->fhair, infostruct->fbeard,  infostruct->farm,
		infostruct->fleg, secretpassword);

	ActivateUser(name);
	MakeStart(name, secretpassword, secretpassword, address, mymudstruct->frames, socketfd);
	return 1;
}
