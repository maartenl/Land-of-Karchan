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
#include "mudnewchar.h"

extern char secretpassword[40];

mudnewcharstruct *create_mudnewcharstruct()
{
	mudnewcharstruct *mynewstruct;
	
	mynewstruct = (mudnewcharstruct *) malloc(sizeof(mudnewcharstruct));
	if (mynewstruct == NULL)
	{
		printf("Fatal - Unable to allocate memory for mudnewcharstruct...\n");
		abort();
	}
	memset(mynewstruct, 0, sizeof(mudnewcharstruct));
	mynewstruct->ftitle = mynewstruct->frealname = mynewstruct->femail = NULL;
	return mynewstruct;
}

int 
gameNewchar(char *name, char *password, char *cookie, char *address, mudnewcharstruct *infostruct)
{
	time_t          currenttime;
	int i;


#ifdef DEBUG
	printf("gameNewchar started(%s, %s, %s, %s)!!!\n", name, password, cookie, address);
#endif

	/* fprintf(getMMudOut(), "[%s]", getenv("HTTP_COOKIE"));*/
	generate_password(secretpassword);

	if (SearchBanList(address, name)) 
	{
		BannedFromGame(name, address);
		return 0;
	}

	if (StrangeName(name, password, address) == 0)
	{
		return 0;
	}

	/* check if user already exists, if so -> exit */
	if (SearchUser(name)==1) 
	{
		WrongPasswd(name, address);
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
	MakeStart(name, secretpassword, address);
	return 1;
}
