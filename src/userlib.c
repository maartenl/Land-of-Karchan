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
#include "userlib.h"

extern int      events[50];
extern int      knightlist[50];
extern int      miflist[50];
extern int      rangerlist[50];
extern banstruct banlist[50];
char            ItemDescr[100];

void 
Error(int i, char *troep)
{
	FILE           *fp;
	fp = fopen(ErrorFile, "a");
	fprintf(fp, "error %i: %s\n", i, troep);
	fclose(fp);
}

char           *
lowercase(char dest[512], char *buf)
{
	int             i;
	for (i = 0; i < strlen(buf) + 1; i++) {
		dest[i] = tolower(buf[i]);
	}
	return (dest);
}

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

int 
ActivateUser(char *name)
{
	FILE *fp, *fp2;
	int i = 0;
	char filenaam[100], troepstr[100];
	
	/*move all resident mudmail from user to activemail*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	
	/* ------------------------------- Mail ------------------------------ */
	
	sprintf(temp, "insert into tmp_mailtable select * from mailtable where toname='%s'", name);
	res=SendSQL2(temp, NULL);
	
	mysql_free_result(res);
	
	/* ------------------------------ User -------------------------------- */
	
	sprintf(temp, "update usertable set active=1, "
	"lastlogin=date_sub(now(), interval 2 hour) where name='%s'", name);
	res=SendSQL2(temp, NULL);
	
	mysql_free_result(res);
	
	sprintf(temp, "insert into tmp_usertable select * from usertable where name='%s'", name);
	res=SendSQL2(temp, NULL);
	
	mysql_free_result(res);
	
		umask(0000);
		sprintf(filenaam, USERHeader "%s.log", name);
		fp = fopen(filenaam, "w");
		fclose(fp);
		
	/* -------------------------------------- Items --------------------------- */
	
	/* Copy all items of the user to tmp_itemstable */
	sprintf(temp, "insert into tmp_itemtable select * from itemtable where belongsto='%s'", name);
	res=SendSQL2(temp, NULL);
	
	mysql_free_result(res);

	return 1;
}

int 
RemoveUser(char *name)
{
	FILE *fp, *fp2;
	char filenaam[100], troepstr[100];
	int i = 0;

/*remove all active mudmail from user*/
MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];

/* ------------------------------- Mail ------------------------------ */

/* Remove mail of user in temp_mailtable */
sprintf(temp, "delete from tmp_mailtable where toname='%s'", name);
res=SendSQL2(temp, NULL);

mysql_free_result(res);
sprintf(temp, "update mailtable SET newmail=0 WHERE toname='%s'", name);
res=SendSQL2(temp, NULL);

mysql_free_result(res);

/* ------------------------------ User -------------------------------- */

/* make certain nobody is fighting against this removed person */
sprintf(temp, "update tmp_usertable set fightingwho='' where fightingwho='%s'", name);
res=SendSQL2(temp, NULL);
mysql_free_result(res);

/* set active to 0 on name in temp_usertable */
sprintf(temp, "update tmp_usertable set active=0, fightingwho='' where name='%s'", name);
res=SendSQL2(temp, NULL);

mysql_free_result(res);

/* replace name in temp_usertable in usertable */
sprintf(temp, "replace into usertable select * from tmp_usertable where name='%s'", name);
res=SendSQL2(temp, NULL);

mysql_free_result(res);

/* delete name from temp_usertable */
sprintf(temp, "delete from tmp_usertable where name='%s'", name);
res=SendSQL2(temp, NULL);

mysql_free_result(res);

	sprintf(filenaam, USERHeader "%s.log", name);
	remove(filenaam);

/* -------------------------------------- Items --------------------------- */

/* Remove items of user in itemtable */
sprintf(temp, "delete from itemtable where belongsto='%s'", name);
res=SendSQL2(temp, NULL);

mysql_free_result(res);
/* Copy items of user in tmp_itemtable to itemtable */
sprintf(temp, "replace into itemtable select * from tmp_itemtable where belongsto='%s'", name);
res=SendSQL2(temp, NULL);

mysql_free_result(res);

/* Remove items of user in tmp_itemtable */
sprintf(temp, "delete from tmp_itemtable where belongsto='%s'", name);
res=SendSQL2(temp, NULL);

mysql_free_result(res);

	return 1;
}

int 
ExistUser(char *name)
{
MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];

/* Check if user exists in temp_usertable */
sprintf(temp, "select count(*) from tmp_usertable where name='%s'", name);
res=SendSQL2(temp, NULL);

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

int 
ExistUserRoom(int roomnr, char *name)
{
MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];

/* Check if user exists in temp_usertable in room roomnr */
sprintf(temp, "select count(*) from tmp_usertable where name='%s' and room=%i", name, roomnr);
res=SendSQL2(temp, NULL);
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

char * 
ExistUserByDescription(char **ftokens, int beginning, int amount, int room, char **returndesc)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;
	char *returnuser = NULL;
	int i ;
	
	/* Check if user exists in tmp_usertable, search by description */
	temp = (char *) malloc (200 + amount * 200);
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
		room, ftokens[beginning]);
	for (i=beginning+1;i<beginning+amount;i++)
	{
		strcat(temp, " and '");
		strcat(temp, ftokens[i]);
		strcat(temp, "' in (race, sex, age, length, width, complexion, eyes, face, hair, beard, arm, leg)");
	}
	res=SendSQL2(temp, NULL);
	free(temp);
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
	returnuser = (char *) malloc (strlen(row[0])+2);
	strcpy(returnuser, row[0]);
	*returndesc = (char *) malloc (strlen(row[1])+2);
	strcpy(*returndesc, row[1]);
	mysql_free_result(res);
	
	return returnuser;
}

int 
SearchUser(char *name)
{
MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];
int returnvalue;

/* Check if user exists in usertable */
sprintf(temp, "select count(*) from usertable where name='%s'", name);
res=SendSQL2(temp, NULL);
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

void 
ReadEvents()
{
	FILE *fp;
	fp = fopen(EventFile, "rb");
	fread(events, sizeof(events), 1, fp);
	fclose(fp);
}

void 
CleanEvents()
{
	FILE *fp;
	int i;
	fp = fopen(EventFile, "wb");
	for (i = 0; i != 50; i++) {
		events[i] = 0;
	}
	fwrite(events, sizeof(events), 1, fp);
	fclose(fp);
}

void 
WriteEvents()
{
	FILE *fp;
	fp = fopen(EventFile, "wb");
	fwrite(events, sizeof(events), 1, fp);
	fclose(fp);
}

void 
ReadGuildLists()
{
	FILE *fp;
	fp = fopen(GuildListFile, "rb");
	fread(knightlist, sizeof(knightlist), 1, fp);
	fread(miflist, sizeof(miflist), 1, fp);
	fread(rangerlist, sizeof(rangerlist), 1, fp);
	fclose(fp);
}

int 
SearchGuildLists(int item)
{
	int i,j;	
	i=0;
	for ( j=0; knightlist[j]!=0 && knightlist[j]!=item; j++) {}
	if ( knightlist[j]==item ) {i+=1;}
	for ( j=0; miflist[j]!=0 && miflist[j]!=item; j++) {}
	if ( miflist[j]==item ) {i+=2;}
	for ( j=0; rangerlist[j]!=0 && rangerlist[j]!=item; j++) {}
	if ( rangerlist[j]==item ) {i+=4;}
	return i;
}

void 
WriteGuildLists()
{
	FILE *fp;
	fp = fopen(GuildListFile, "wb");
	fwrite(knightlist, sizeof(knightlist), 1, fp);
	fwrite(miflist, sizeof(miflist), 1, fp);
	fwrite(rangerlist, sizeof(rangerlist), 1, fp);
	fclose(fp);
}

void 
ReadBanList()
{
	FILE *fp;
	fp = fopen(BanListFile, "rb");
	fread(banlist, sizeof(banlist), 1, fp);
	fclose(fp);
}

int 
SearchBanList(char *item, char *username)
{
MYSQL_RES *res;
MYSQL_ROW row;
int i;
char temp[1024];

sprintf(temp, "select count(name) from sillynamestable where '%s' like name", username);
res=SendSQL2(temp, NULL);

row = mysql_fetch_row(res);

if (strcmp(row[0],"0")) 
{
	mysql_free_result(res);
	return 1;
}
mysql_free_result(res);

sprintf(temp, "select count(name) from unbantable where name='%s'", username);
res=SendSQL2(temp, NULL);

row = mysql_fetch_row(res);

if (strcmp(row[0],"0")) 
{
	mysql_free_result(res);
	return 0;
}
mysql_free_result(res);

sprintf(temp, "select count(address) from bantable where '%s' like address", item);
res=SendSQL2(temp, NULL);

row = mysql_fetch_row(res);

if (strcmp(row[0],"0")) 
{
	mysql_free_result(res);
	return 1;
}
mysql_free_result(res);

return 0;

/*	int i,j;	
	i=0;
	for ( j=0; j<45 ; j++) {
		if ( (strstr(item, banlist[j].ip)!=NULL) &&
			(banlist[j].days>0) ) {return 1;}
		}
	return 0;*/
}

void 
WriteBanList()
{
	FILE *fp;
	fp = fopen(BanListFile, "wb");
	fwrite(banlist, sizeof(banlist), 1, fp);
	fclose(fp);
}

char *
ItemDescription(char *name)
{
MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];
char *ItemDescr;

/* Get description of item from items */
sprintf(temp, "select adject1, adject2, adject3, name from items where name='%s'", name);
res=SendSQL2(temp, NULL);

row = mysql_fetch_row(res);

	ItemDescr = (char *)malloc(200);
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

void 
ClearLogFile(char *filenaam)
{
	FILE *fp;
	fp = fopen(filenaam, "w");
	fclose(fp);
}

void 
WriteSentenceIntoOwnLogFile(char *string, char *filenaam)
{
	FILE *filep;
	filep = fopen(filenaam, "a");
	fprintf(filep, "%s", string);
	fclose(filep);
}

void 
WriteSentenceIntoOwnLogFile2(char *filenaam, char *fmt,...)
{
	FILE *filep;
	va_list ap;
	char *s;
	int i;
	char c;

	filep = fopen(filenaam, "a");
	va_start(ap, fmt);
	(void) vfprintf(filep, fmt, ap);
	va_end(ap);
	fclose(filep);
}

void 
WriteMessage(char *to, char *name, int roomnr)
{
}

void 
WriteMessage2(char *name, int roomnr, char *fmt,...)
{
FILE *fp;
char troep[100];
FILE *filep;
va_list ap;
char *s, *save;
int i;
char c;

MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];

sprintf(temp, "select name from tmp_usertable where name<>'%s' and room=%i", name, roomnr);
res=SendSQL2(temp, NULL);

save = fmt;
while((row = mysql_fetch_row(res))) {
			strcpy(troep, USERHeader);
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

int 
WriteMessageTo(char *to, char *second, char *toname)
{
}

int 
WriteMessageTo2(char *toname, char *name, int roomnr, char *fmt,...)
{
	char *save;
	char troep[100], naamxx1[22], naamxx2[22];

MYSQL_RES *res;
MYSQL_ROW row;
int i;
char temp[1024];

sprintf(temp, "select name, room from tmp_usertable where name='%s' and room=%i", toname, roomnr);
res=SendSQL2(temp, NULL);

if (!mysql_fetch_row(res)) 
{
	return 0;
}

mysql_free_result(res);

sprintf(temp, "select name from tmp_usertable where name<>'%s' and name<>'%s' and room=%i", name, toname, roomnr);
res=SendSQL2(temp, NULL);

save = fmt;
while((row = mysql_fetch_row(res))) {
				FILE *filep;
				va_list ap;
				char *s;
				int i;
				char c;
			strcpy(troep, USERHeader);
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

int 
WriteSayTo(char *toname, char *name, int roomnr, char *fmt,...)
{
	char troep[100];
	FILE *filep;
	va_list ap;
	char *s;
	int i;
	char c;

MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];

sprintf(temp, "select name from tmp_usertable where name='%s' and room=%i", toname, roomnr);
res=SendSQL2(temp, NULL);

if (!(row = mysql_fetch_row(res))) 
{
	return 0;
}

	strcpy(troep, USERHeader);
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

int 
WriteLinkTo(char *toname, char *name, char *fmt,...)
{
	char troep[100];
	FILE *filep;
	va_list ap;
	char *s;
	int i;
	char c;

MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];

sprintf(temp, "select name from tmp_usertable where name='%s'", toname);
res=SendSQL2(temp, NULL);

if (!(row = mysql_fetch_row(res))) 
{
	return 0;
}

	strcpy(troep, USERHeader);
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

void 
SayToAll(char *to)
{
	char troep[100];

MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];

sprintf(temp, "select name from tmp_usertable");
res=SendSQL2(temp, NULL);

while ((row = mysql_fetch_row(res))) {
	strcpy(troep, USERHeader);
	strcat(troep, row[0]);
	strcat(troep, ".log");
	WriteSentenceIntoOwnLogFile(to, troep);
	WriteSentenceIntoOwnLogFile("<P>\r\n", troep);
	}

mysql_free_result(res);
}

char *
HeShe(char *kill)
{
	if (kill[0] == 'm') {
		return "He";
	} else {
		return "She";
	}
}

char *
HeSheSmall(char *kill)
{
	if (kill[0] == 'm') {
		return "he";
	} else {
		return "she";
	}
}

char *
HeShe2(char *kill)
{
	if (kill[0] == 'm') {
		return "him";
	} else {
		return "her";
	}
}

char *
HeShe3(char *kill)
{
	if (kill[0] == 'm') {
		return "his";
	} else {
		return "her";
	}
}

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
}

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

char *
HitMe(int i)
{	/* [0..4] : normal
	 [5..9]: sword,dagger
	 [10..14]:stick
	 [15..19]:pick
	 */

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

