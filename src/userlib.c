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
#include <stdarg.h>
#include <string.h>
#include <stdlib.h>
#include "typedefs.h"
#include "userlib.h"

/*! \file userlib.c
	\brief  definition file with basic user/item/etc operations */

#ifndef MEMMAN
#define mud_malloc(A,B,C)	malloc(A)
#define mud_free(A)		free(A)
#define mud_strdup(A,B,C)	strdup(A)
#define mud_realloc(A,B)	realloc(A,B)
#endif

char            ItemDescr[100];

//! show error in MM_ERRORFILE
/*! appends an error to the file
	\param i contains valid error values
	*/
void 
Error(int i, char *description)
{
	FILE           *fp;
	fp = fopen(getParam(MM_ERRORFILE), "a");
	fprintf(fp, "error %i: %s\n", i, description);
	fclose(fp);
}

//! wait a number of sec/usec
void
Wait(int sec, int usec)
{
	struct timeval tv;

	/* Wait up to sec.usec seconds. */
	tv.tv_sec = sec;
	tv.tv_usec = usec;

	select(0, NULL, NULL, NULL, &tv); 
	/* Don't rely on the value of tv now! */ 
	return ;
}

//! activate user in the game
/*! logs the user into the game, the current way of doing this is copying userinfomation, items he/she has in posession and 
mail he/she reads into tmp_tables.
	\param name char* containing a valid name and of a user that is not active in the game
*/
int 
ActivateUser(char *name)
{
	FILE *fp;
	char filenaam[100];
	
	/*move all resident mudmail from user to activemail*/
	MYSQL_RES *res;
	char *temp;
	
	/* ------------------------------- Mail ------------------------------ */
	
	temp = composeSqlStatement("insert into tmp_mailtable select * from mailtable where toname='%x'", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	
	mysql_free_result(res);
	
	/* ------------------------------ User -------------------------------- */
	
	temp = composeSqlStatement("update usertable set active=1, "
	"lastlogin=date_sub(now(), interval 2 hour) where name='%x'", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	
	mysql_free_result(res);
	
	temp = composeSqlStatement("insert into tmp_usertable select * from usertable where name='%x'", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	
	mysql_free_result(res);
	
	/* -------------------------------------- Attributes --------------------------- */
	
	temp = composeSqlStatement("insert into tmp_attributes select * from attributes where objectid='%x' and objecttype=1", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	
	mysql_free_result(res);
	
//		umask(0000);
		sprintf(filenaam, "%s%s.log", getParam(MM_USERHEADER), name);
		fp = fopen(filenaam, "w");
		fclose(fp);
		
	/* -------------------------------------- Items --------------------------- */
	
	/* Copy all items of the user to tmp_itemstable */
	temp = composeSqlStatement("insert into tmp_itemtable select * from itemtable where belongsto='%x'", name);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	
	mysql_free_result(res);

	return 1;
}

//! removed the player from active gameplaying
/*! \see ActivateUser
	\param name char*, name of playercharacter and must be active in the game
*/
int 
RemoveUser(char *name)
{
	char filenaam[100];
	MYSQL_RES *res;
	char *temp;

	/*remove all active mudmail from user*/
/* ------------------------------- Mail ------------------------------ */

/* Remove mail of user in temp_mailtable */
temp = composeSqlStatement("delete from tmp_mailtable where toname='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);
temp = composeSqlStatement("update mailtable SET newmail=0 WHERE toname='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);

/* ------------------------------ User -------------------------------- */

/* make certain nobody is fighting against this removed person */
temp = composeSqlStatement("update tmp_usertable set fightingwho='' where fightingwho='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;
mysql_free_result(res);

/* set active to 0 on name in temp_usertable */
temp = composeSqlStatement("update tmp_usertable set lok='', active=0, fightingwho='' where name='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);

/* replace name in temp_usertable in usertable */
temp = composeSqlStatement("replace into usertable select * from tmp_usertable where name='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);

/* delete name from temp_usertable */
temp = composeSqlStatement("delete from tmp_usertable where name='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);

/* -------------------------------------- Attributes ----------------------- */

/* Remove attributes of user in attributes table */
temp = composeSqlStatement("delete from attributes where objectid='%x' and objecttype=1", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);
/* replace tmp_attributes in attributes */
temp = composeSqlStatement("replace into attributes select * from tmp_attributes where objectid='%x' and objecttype=1", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);

/* delete name from temp_usertable */
temp = composeSqlStatement("delete from tmp_attributes where objectid='%x' and objecttype=1", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);

	sprintf(filenaam, "%s%s.log", getParam(MM_USERHEADER), name);
	remove(filenaam);

/* -------------------------------------- Items --------------------------- */

/* Remove items of user in itemtable */
temp = composeSqlStatement("delete from itemtable where belongsto='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);
/* Copy items of user in tmp_itemtable to itemtable */
temp = composeSqlStatement("replace into itemtable select * from tmp_itemtable where belongsto='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);

/* Remove items of user in tmp_itemtable */
temp = composeSqlStatement("delete from tmp_itemtable where belongsto='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

mysql_free_result(res);

	return 1;
}

//! is user online/playing?
/* \param name char*, user exists and is active in the game
*/
int 
ExistUser(char *name)
{
MYSQL_RES *res;
MYSQL_ROW row;
char *temp;

/* Check if user exists in temp_usertable */
temp = composeSqlStatement("select count(*) from tmp_usertable where name='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

if (res==NULL)
{
	mysql_free_result(res);
	return 0;
}
row = mysql_fetch_row(res);
if (row==NULL)
{
	mysql_free_result(res);
	return 0;
}
mysql_free_result(res);

return 1;
}

//! is user playing/online and currently occupying that specific room?
int 
ExistUserRoom(int roomnr, char *name)
{
MYSQL_RES *res;
MYSQL_ROW row;
char *temp;

/* Check if user exists in temp_usertable in room roomnr */
temp = composeSqlStatement("select count(*) from tmp_usertable where name='%x' and room=%i", name, roomnr);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;
if (res==NULL)
{
	return 0;
}

row = mysql_fetch_row(res);
if (row==NULL)
{
	mysql_free_result(res);
	return 0;
}
mysql_free_result(res);

return 1;
}

//! see if user is online in the game, based in his/her description instead of character name
/* \param beginning int, >=0
	\param amount int, >1
	\param room int, must be a valid room
	\param returndesc array of strings, contains the description tags to search for
	\return char* describing the requested player, if a player in that room fitting that
	description is not found, the method will return NULL
*/
char * 
ExistUserByDescription(mudpersonstruct *fmudstruct, int beginning, int amount, int room, char **returndesc)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	char *returnuser = NULL;
	int i ;
	
	/* Check if user exists in tmp_usertable, search by description */
	temp = (char *) mud_malloc (200 + amount * 200, __LINE__, __FILE__);
	sprintf(temp, "select name, "
	"concat(age,"
	"if(length = 'none', '', concat(', ',length)),"
	"if(width = 'none', '', concat(', ',width)),"
	"if(complexion = 'none', '', concat(', ',complexion)),"
	"if(eyes = 'none', '', concat(', ',eyes)),"
	"if(face = 'none', '', concat(', ',face)),"
	"if(hair = 'none', '', concat(', ',hair)),"
	"if(beard = 'none', '', concat(', ',beard)),"
	"if(arm = 'none', '', concat(', ',arm)),"
	"if(leg = 'none', '', concat(', ',leg)),"
	"' ', sex, ' ', race)"
	" from tmp_usertable where room = %i and '%s' in "
	"(race, sex, age, length, width, complexion, eyes, face, hair, beard, arm, leg) ",
		room, getToken(fmudstruct, beginning));
	for (i=beginning+1;i<beginning+amount;i++)
	{
		strcat(temp, " and '");
		strcat(temp, getToken(fmudstruct, i));
		strcat(temp, "' in (race, sex, age, length, width, complexion, eyes, face, hair, beard, arm, leg)");
	}
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;
	if (res==NULL)
	{
		return NULL;
	}
	row = mysql_fetch_row(res);
	if (row==NULL)
	{
		mysql_free_result(res);
		return NULL;
	}
	returnuser = (char *) mud_malloc (strlen(row[0])+2, __LINE__, __FILE__);
	strcpy(returnuser, row[0]);
	*returndesc = (char *) mud_malloc (strlen(row[1])+2, __LINE__, __FILE__);
	strcpy(*returndesc, row[1]);
	mysql_free_result(res);
	
	return returnuser;
}

//! search for user wether online or otherwise
/*! \return int, 0 if user does not exist with that name, 1 if found
	\param name char* that contains the name of the player to search for
*/
int 
SearchUser(char *name)
{
MYSQL_RES *res;
MYSQL_ROW row;
char *temp;
int returnvalue;

/* Check if user exists in usertable */
temp = composeSqlStatement("select count(*) from usertable where name='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;
if (res==NULL)
{
	return 0;
}
row = mysql_fetch_row(res);
if (row==NULL)
{
	mysql_free_result(res);
	return 0;
}
returnvalue=(row[0][0]!='0');
mysql_free_result(res);

return returnvalue;
}

//! search for the username amongst the banned users list in the database
/*! first checks the sillynamestable in the database, then checks the unbantable and
	as last check checks the bantable.
	\return int 1 if found, 0 if not found
	\param item char* that contains the address of the player
	\param username char* name of the playercharacter
*/
int 
SearchBanList(char *item, char *username)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;

	temp = composeSqlStatement("select count(name) from sillynamestable where '%x' like name", username);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;

	row = mysql_fetch_row(res);

	if (strcmp(row[0],"0")) 
	{
		mysql_free_result(res);
		return 1;
	}
	mysql_free_result(res);

	temp = composeSqlStatement("select count(name) from unbantable where name='%x'", username);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;

	row = mysql_fetch_row(res);

	if (strcmp(row[0],"0")) 
	{
		mysql_free_result(res);
		return 0;
	}
	mysql_free_result(res);

	temp = composeSqlStatement("select count(address) from bantable where '%x' like address", item);
	res=sendQuery(temp, NULL);
	mud_free(temp);temp=NULL;

	row = mysql_fetch_row(res);

	if (strcmp(row[0],"0")) 
	{
		mysql_free_result(res);
		return 1;
	}
	mysql_free_result(res);

	return 0;
}

//! get description of items available based on the name of the item
char *
ItemDescription(char *name)
{
MYSQL_RES *res;
MYSQL_ROW row;
char *temp;
char *ItemDescr;

/* Get description of item from items */
temp = composeSqlStatement("select adject1, adject2, adject3, name from items where name='%x'", name);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

row = mysql_fetch_row(res);

	ItemDescr = (char *)mud_malloc(200, __LINE__, __FILE__);
	*ItemDescr = '\0';
	if (row[0][0] != '\0') {
		strcat(ItemDescr, row[0]);
		if (row[1][0] == '\0') {
			strcat(ItemDescr, " ");
		} else {
			strcat(ItemDescr, ", ");
			strcat(ItemDescr, row[1]);
			if (row[2][0] != '\0') {
				strcat(ItemDescr, ", ");
				strcat(ItemDescr, row[2]);
				strcat(ItemDescr, " ");
			} else {strcat(ItemDescr, " ");}
		}
	}
	strcat(ItemDescr, row[3]);

	mysql_free_result(res);

	return ItemDescr;
}

//! clear filename, used by command Clear
void 
ClearLogFile(char *filenaam)
{
	FILE *fp;
	fp = fopen(filenaam, "w");
	fclose(fp);
}

//! append message to file
/*! can contain a formatted string and extra parameters
\see printf
*/
void 
WriteSentenceIntoOwnLogFile(const char *filenaam, char *fmt,...)
{
	FILE *filep;
	va_list ap;

	filep = fopen(filenaam, "a");
	va_start(ap, fmt);
	(void) vfprintf(filep, fmt, ap);
	va_end(ap);
	fclose(filep);
}

//! write message to users in room
/*! can contain a formatted string and extra parameters
\see printf
*/
void 
WriteMessage(char *name, int roomnr, char *fmt,...)
{
char troep[100];
FILE *filep;
va_list ap;
char *save;

MYSQL_RES *res;
MYSQL_ROW row;
char *temp;

temp = composeSqlStatement("select name from tmp_usertable where name<>'%x' and room=%i", name, roomnr);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

save = fmt;
while((row = mysql_fetch_row(res))) {
			strcpy(troep, getParam(MM_USERHEADER));
			strcat(troep, row[0]);
			strcat(troep, ".log");
			filep = fopen(troep, "a");
			fmt = save;
			va_start(ap, fmt);
			(void) vfprintf(filep, fmt, ap);
			va_end(ap);
			fclose(filep);
	}			/* endwhile */
	mysql_free_result(res);
}

//! write message to specific user in room
/*! can contain a formatted string and extra parameters
\see printf
*/
int 
WriteMessageTo(char *toname, char *name, int roomnr, char *fmt,...)
{
	char *save;
	char troep[100];
	MYSQL_RES *res;
MYSQL_ROW row;
char *temp;

temp = composeSqlStatement("select name, room from tmp_usertable where name='%x' and room=%i", toname, roomnr);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

if (!mysql_fetch_row(res)) 
{
	return 0;
}

mysql_free_result(res);

temp = composeSqlStatement("select name from tmp_usertable where name<>'%x' and name<>'%x' and room=%i", name, toname, roomnr);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

save = fmt;
while((row = mysql_fetch_row(res))) {
				FILE *filep;
				va_list ap;
			strcpy(troep, getParam(MM_USERHEADER));
			strcat(troep, row[0]);
			strcat(troep, ".log");
				fmt = save;
				filep = fopen(troep, "a");

				va_start(ap, fmt);
				(void) vfprintf(filep, fmt, ap);
				va_end(ap);

				fclose(filep);
	}			/* endwhile */

	mysql_free_result(res);

	return 1;
}

//! say something to specific user in room
/*! can contain a formatted string and extra parameters
\see printf
*/
int 
WriteSayTo(char *toname, char *name, int roomnr, char *fmt,...)
{
	char troep[100];
	FILE *filep;
	va_list ap;

MYSQL_RES *res;
MYSQL_ROW row;
char *temp;

temp = composeSqlStatement("select name from tmp_usertable where name='%x' and room=%i", toname, roomnr);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

if (!(row = mysql_fetch_row(res))) 
{
	return 0;
}

	strcpy(troep, getParam(MM_USERHEADER));
	strcat(troep, row[0]);
	strcat(troep, ".log");
	filep = fopen(troep, "a");
	va_start(ap, fmt);
	(void) vfprintf(filep, fmt, ap);
	va_end(ap);
	fclose(filep);

	mysql_free_result(res);

	return 1;
}

//! tell something to specific user regardless of in which room the player resides
/*! can contain a formatted string and extra parameters
\see printf
*/
int 
WriteLinkTo(char *toname, char *name, char *fmt,...)
{
	char troep[100];
	FILE *filep;
	va_list ap;

MYSQL_RES *res;
MYSQL_ROW row;
char *temp;

temp = composeSqlStatement("select name from tmp_usertable where name='%x'", toname);
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

if (!(row = mysql_fetch_row(res))) 
{
	return 0;
}

	strcpy(troep, getParam(MM_USERHEADER));
	strcat(troep, row[0]);
	strcat(troep, ".log");
	filep = fopen(troep, "a");
	va_start(ap, fmt);
	(void) vfprintf(filep, fmt, ap);
	va_end(ap);
	fclose(filep);
	mysql_free_result(res);

	return 1;
}

//! tell a message to everyone in the game
void 
SayToAll(char *to)
{
	char troep[100];

MYSQL_RES *res;
MYSQL_ROW row;
char *temp;

temp = composeSqlStatement("select name from tmp_usertable");
res=sendQuery(temp, NULL);
mud_free(temp);temp=NULL;

while ((row = mysql_fetch_row(res))) {
	strcpy(troep, getParam(MM_USERHEADER));
	strcat(troep, row[0]);
	strcat(troep, ".log");
	WriteSentenceIntoOwnLogFile(troep, to);
	WriteSentenceIntoOwnLogFile(troep, "<P>\r\n");
	}

mysql_free_result(res);
}

//! returns 'He' or 'She' string depending on sex
char *
HeShe(char *kill)
{
	if (kill[0] == 'm') {
		return "He";
	} else {
		return "She";
	}
}

//! returns 'he' or 'she' string depending on sex
char *
HeSheSmall(char *kill)
{
	if (kill[0] == 'm') {
		return "he";
	} else {
		return "she";
	}
}

//! returns 'him' or 'her' string depending on sex
char *
HeShe2(char *kill)
{
	if (kill[0] == 'm') {
		return "him";
	} else {
		return "her";
	}
}

//! returns 'his' or 'her' string depending on sex
char *
HeShe3(char *kill)
{
	if (kill[0] == 'm') {
		return "his";
	} else {
		return "her";
	}
}

//! provides constant string describing current damage level
/*! damage level is portrayed in 12 steps from good (0) to worse/dead (maxi) */
char *
ShowString(int i, int maxi)
{
	if (i < (9*maxi)/118) {
		return " to be feeling very well";
	}
	if (i < (19*maxi)/118) {
		return " to be feeling well";
	}
	if (i < (29*maxi)/118) {
		return " to be feeling fine";
	}
	if (i < (39*maxi)/118) {
		return " to be feeling quite nice";
	}
	if (i < (49*maxi)/118) {
		return " to be slightly hurt";
	}
	if (i < (59*maxi)/118) {
		return " to be hurt";
	}
	if (i < (69*maxi)/118) {
		return " to be quite hurt";
	}
	if (i < (79*maxi)/118) {
		return " to be extermely hurt";
	}
	if (i < (89*maxi)/118) {
		return " to be terribly hurt";
	}
	if (i < (99*maxi)/118) {
		return " to be feeling bad";
	}
	if (i < (109*maxi)/118) {
		return " to be feeling very bad";
	}
	return " to be at death's door.";
}

//! show how tired you are, directly related to how much you can move.
/*! in steps of 7
*/
char *
ShowMovement(int i, int maxi)
{
	if (i < (9*maxi)/68) {
		return " to be not tired at all";
	}
	if (i < (19*maxi)/68) {
		return " to be slightly fatigued";
	}
	if (i < (29*maxi)/68) {
		return " to be slightly tired";
	}
	if (i < (39*maxi)/68) {
		return " to be tired";
	}
	if (i < (49*maxi)/68) {
		return " to be very tired";
	}
	if (i < (59*maxi)/68) {
		return " to be almost exhausted";
	}
	return " to be fully exhausted.";
}

//!  hit stuff with a part of you,
char *
ShowHittingStuff(int i, char *race)
{
	switch (i)
	{
		case 0 : return "left fist";
		case 1 : return "right fist";
		case 2 : return "left elbow";
		case 3 : return "right elbow";
		case 4 : return "head";
		case 5 : return "left knee";
		case 6 : return "right knee";
	}
	return "right fist";
}

//! show how much you drank
/* in steps of 6, however, it is also possible to become inebriated with alcohol, which there is another
set of 6 steps for the negative values of i.
\param i int, drinkstats, -59 is very drunk, 0 is sober, >=49 flush with fluids.
\see ShowEat
*/
char *
ShowDrink(int i)
{
	if (i < -59) {
		return "You are out of your skull on alcohol.<BR>";
	}
	if (i < -49) {
		return "You are very drunk.<BR>";
	}
	if (i < -39) {
		return "You are drunk.<BR>";
	}
	if (i < -29) {
		return "You are pissed.<BR>";
	}
	if (i < -19) {
		return "You are a little drunk.<BR>";
	}
	if (i < -9) {
		return "You have a headache.<BR>";
	}
	if (i < 9) {
		return "You are thirsty.<BR>";
	}
	if (i < 19) {
		return "You can drink a whole lot more.<BR>";
	}
	if (i < 29) {
		return "You can drink a lot more.<BR>";
	}
	if (i < 39) {
		return "You can drink some.<BR>";
	}
	if (i < 49) {
		return "You can drink a little more.<BR>";
	}
	return "You cannot drink anymore.<BR>";
}

//! show how much you have eaten
/*! in steps of 6
\param i int, eatstats 0 is hungry, >=49 full
\see ShowDrink
*/
char *
ShowEat(int i)
{
	if (i < 9) {
		return "You are hungry.<BR>";
	}
	if (i < 19) {
		return "You can eat a whole lot more.<BR>";
	}
	if (i < 29) {
		return "You can eat a lot more.<BR>";
	}
	if (i < 39) {
		return "You can eat some.<BR>";
	}
	if (i < 49) {
		return "You can only eat a little more.<BR>";
	}
	return "You are full.<BR>";
}

//! show how much you are weighed down by burden
/*! in steps of 7
\param i int, less than 500 is travellibg lightly, >=3000 carrying too much and unable to move
\see ShowDrink
\see ShowEat
*/
char *
ShowBurden(int i)
{
/* 3000:
 0..500
500..1000
 1000..1500
 1500..2000
 2500..3000
*/
	if (i < 500) {
		return "You are travelling lightly.<BR>";
	}
	if (i < 1000) {
		return "You are carrying only a few items.<BR>";
	}
	if (i < 1500) {
		return "You are carrying quite a few items, yet you can manage.<BR>";
	}
	if (i < 2000) {
		return "You are carrying many items.<BR>";
	}
	if (i < 2500) {
		return "You are burdened.<BR>";
	}
	if (i < 3000) {
		return "You are heavily burdened.<BR>";
	}
	return "You are carrying way too much and you feel the entire weight pressing down on you.<BR>";
}

//! compute the amount of encumberance based on strength of character and weight of items carried.
/*! i.e. this is the amount of a movement penalty that you get when attempting to move. It is subtracted
from your total physical stats.
	\return int containing -1..50, where 0 is travelling lightly, 50 is carrying really much, -1 if carrying TOO MUCH
*/
int
computeEncumberance(int fweight, int fstrength)
{
	if (fweight < 500 + 50 * fstrength) return 0;
	if (fweight < 1000 + 50 * fstrength) return 10;
	if (fweight < 1500 + 50 * fstrength) return 20;
	if (fweight < 2000 + 50 * fstrength) return 30;
	if (fweight < 2500 + 50 * fstrength) return 40;
	if (fweight < 3000 + 50 * fstrength) return 50;
	return -1;
}

//! show the alignnment of your character, wether you are good or evil
/* 
\param i int, 0 is neutral, smaller then -90 is utter evil, bigger then 90 is angelic
*/
char *
ShowAlignment(int i)
{
/*-90..0..90
200/10=20
-90..-70 evil
-70..-50 bad
-50..-30 mean
-30..-10 untr
-10..10neutral
10..30 trust
30..50 kind
50..70 awf
70..90 good*/

	if (i < -70) {
		return "You are <I>pure evil</I>.<BR>";
	}
	if (i < -50) {
		return "You are bad.<BR>";
	}
	if (i < -30) {
		return "You are mean.<BR>";
	}
	if (i < -10) {
		return "You are untrustworthy.<BR>";
	}
	if (i < 10) {
		return "You are inclined to neither the good nor the bad side.<BR>";
	}
	if (i < 30) {
		return "You are trustworthy.<BR>";
	}
	if (i < 50) {
		return "You are kind.<BR>";
	}
	if (i < 70) {
		return "You are awfully kind.<BR>";
	}
		return "You are good.<BR>";
}

//! returns a verb depending on with what you are hitting a person
/*! 
\param i int, 
<UL><LI>0..4 : normal
<LI>5..9 : sword,dagger
<LI>10..14 : stick
<LI>15..19 : pick
</UL>
*/
char *
HitMe(int i)
{
	switch (i) {
/*---------------- normal -----------*/
/*with jaws, left paw, right paw, */
	case 0:{
			return "hit";
		}
	case 1:{
			return "crush";
		}
	case 2:{
			return "brush";
		}
	case 3:{
			return "scrape";
		}
	case 4:{
			return "hit";
		}
/*------------ sword/dagger --------------*/
	case 5:{
			return "slice";
		}
	case 6:{
			return "hit";
		}
	case 7:{
			return "wound";
		}
	case 8:{
			return "pierce";
		}
	case 9:{
			return "scrape";
		}
/*------------ stick -------------*/
	case 10:{
			return "beat";
		}
	case 11:{
			return "prod";
		}
	case 12:{
			return "smack";
		}
	case 13:{
			return "hit";
		}
	case 14:{
			return "knock";
		}
/*------------ pick -------------*/
	case 15:{
			return "swing";
		}
	case 16:{
			return "pierce";
		}
	case 17:{
			return "smack";
		}
	case 18:{
			return "hit";
		}
	case 19:{
			return "knock";
		}
	default:{
			return "";
		}
	}
}

//! returns the power (in words) with which you hit the person you are fighting
/*! 
\param i int, 0..10 where 0  is slightly and 10 is very hard.
*/
char *
ErgHard(int i)
{
	switch (i) {
		case 1:{
			return ", slightly";
		}
	case 2:{
			return "";
		}
	case 3:{
			return "";
		}
	case 4:{
			return "";
		}
	case 5:{
			return "";
		}
	case 6:{
			return ", heavily";
		}
	case 7:{
			return ", hard";
		}
	case 8:{
			return ", brutally";
		}
	case 9:{
			return ", rather hard";
		}
	case 10:{
			return ", very hard";
		}
	default:{
			return "";
		}
	}
}

