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
/*
V 1. implement TERRIBLE DEGREE incidents.
V 2. experience gain + levelling + adding training and experience stuff
V 3. experience loss during death
V 4. experience loss during fleeing
V 5. set standard brawl skill (add standard brawl skill when adding a char)
V 6. defense successfull when 3/4 roll
V 7. if god<>0 then is bot, then remove it totally.
V 8. Left hand still missing!!!

4. drop money as well.
5. put items in corpse to search for
6. update corpse degradation
*/

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

/* person who is fighting (tmp_usertable/usertable) */
#define XNAME 0
#define XADDRESS 1
#define XPASSWORD 2
#define XTITLE 3
#define XREALNAME 4
#define XEMAIL 5
#define XRACE 6
#define XSEX 7
#define XAGE 8
#define XLENGTH 9
#define XWIDTH 10
#define XCOMPLEXION 11
#define XEYES 12
#define XFACE 13
#define XHAIR 14
#define XBEARD 15
#define XARM 16
#define XLEG 17
#define XGOLD 18
#define XSILVER 19
#define XCOPPER 20
#define XROOM 21
#define XLOK 22
#define XWHIMPY 23
#define XEXPERIENCE 24
#define XFIGHTINGWHO 25
#define XSLEEP 26
#define XPUNISHMENT 27
#define XFIGHTABLE 28
#define XVITALS 29
#define XFYSICALLY 30
#define XMENTALLY 31
#define XDRINKSTATS 32
#define XEATSTATS 33
#define XACTIVE 34
#define XLASTLOGIN 35
#define XBIRTH 36
#define XGOD 37
#define XGUILD 38
#define XSTRENGTH 39
#define XINTELLIGENCE 40
#define XDEXTERITY 41
#define XCONSTITUTION 42
#define XWISDOM 43
#define XPRACTISES 44
#define XTRAINING 45
#define XBANDAGE 46
#define XALIGNMENT 47
#define XMANASTATS 48
#define XMOVEMENTSTATS 49
#define XMAXMANA 50
#define XMAXMOVE 51
#define XMAXVITAL 52
#define XJUMPMANA 70
#define XJUMPMOVE 71
#define XJUMPVITAL 72

/* person who is being fought (tmp_usertable/usertable) */
#define YNAME 73
#define YADDRESS 74
#define YPASSWORD 75
#define YTITLE 76
#define YREALNAME 77
#define YEMAIL 78
#define YRACE 79
#define YSEX 80
#define YAGE 81
#define YLENGTH 82
#define YWIDTH 83
#define YCOMPLEXION 84
#define YEYES 85
#define YFACE 86
#define YHAIR 87
#define YBEARD 88
#define YARM 89
#define YLEG 90
#define YGOLD 91
#define YSILVER 92
#define YCOPPER 93
#define YROOM 94
#define YLOK 95
#define YWHIMPY 96 
#define YEXPERIENCE 97
#define YFIGHTINGWHO 98
#define YSLEEP 99
#define YPUNISHMENT 100
#define YFIGHTABLE 101
#define YVITALS 102
#define YFYSICALLY 103
#define YMENTALLY 104
#define YDRINKSTATS 105
#define YEATSTATS 106
#define YACTIVE 107
#define YLASTLOGIN 108
#define YBIRTH 109
#define YGOD 110
#define YGUILD 111
#define YSTRENGTH 112
#define YINTELLIGENCE 113
#define YDEXTERITY 114
#define YCONSTITUTION 115
#define YWISDOM 116
#define YPRACTISES 117
#define YTRAINING 118
#define YBANDAGE 119
#define YALIGNMENT 120
#define YMANASTATS 121
#define YMOVEMENTSTATS 122
#define YMAXMANA 123
#define YMAXMOVE 124
#define YMAXVITAL 125
#define YJUMPMANA 143
#define YJUMPMOVE 144
#define YJUMPVITAL 145

/* item in tmp_itemtable/itemtable */
#define IID 0
#define ISEARCH 1
#define IBELONGSTO 2
#define IAMOUNT 3
#define IROOM 4
#define IWEARING 5
#define IWIELDING 6
#define TMPITEMTOTAL 7

/* item definition (items) */
#define INAME 1
#define IADJECT1 2
#define IADJECT2 3
#define IADJECT3 4
#define IPASDEFENSE 24
#define IDAMRESISTANCE 25
#define ITEMTOTAL 26

/* skills for a person (skilltable) */
#define SID 0
#define SFORWHOM 1
#define SSKILLLEVEL 2

/* skill definition (skills) */
#define SNAME 1
#define SINLEVEL 2
#define SOUTLEVEL 3
#define SMANACOST 4
#define SBEGINEFFECT 5
#define SENDEFFECT 6
#define SMODIFIERNAME 7
#define SDIFFICULTY 8
#define STYPE 9
//#define SACTIVEDEFENSE 10
#define SKILLTOTAL 10

/* room definition (rooms) */
#define RID 0
#define RWEST 1
#define REAST 2
#define RNORTH 3
#define RSOUTH 4
#define RUP 5
#define RDOWN 6
#define RCONTENTS 7

typedef struct {
	int verb;
	int defense;
	int damage;
	int onposition;
	char adject1[20], adject2[20], adject3[20], name[30];
	char defensename[20];
	char defadject1[20], defadject2[20], defadject3[20], defname[30];
	char hand[20];
	int success;
} combat_description;

char *strikeweaponarray[][3] = {
	{"fist", "pound", "pounds"},
	{"fist", "trash", "trashes"},
	{"fist", "crush", "crushes"},
	{"fist", "slap", "slaps"},
	{"fist", "punch", "punshes"},
	{"fist", "smash", "smashes"},
	{"fist", "thwack", "thwacks"},
	{"elbow", "pound", "pounds"},
	{"elbow", "trash", "trashes"},
	{"elbow", "crush", "crushes"},
	{"elbow", "slap", "slaps"},
	{"elbow", "punch", "punshes"},
	{"elbow", "smash", "smashes"},
	{"elbow", "thwack", "thwacks"},
	{"paw", "pound", "pounds"},
	{"paw", "claw", "claws"},
	{"paw", "crush", "crushes"},
	{"paw", "slap", "slaps"},
	{"paw", "punch", "punshes"},
	{"paw", "smash", "smashes"},
	{"paw", "thwack", "thwacks"},
	{"teeth", "bite", "bites"},
	{"teeth", "scratch", "scratches"},
	{"teeth", "tear", "tears"},
	{"teeth", "cut", "cuts"},
	{"teeth", "chomp", "chomps"},
	{"teeth", "bite", "bites"},
	{"teeth", "bite", "bites"},
	{"bill", "bite", "bites"},
	{"bill", "snap", "snaps"},
	{"bill", "hit", "hits"},
	{"bill", "strike", "strikes"},
	{"bill", "beat", "beats"},
	{"bill", "pick", "picks"},
	{"bill", "gnaw", "gnaws"},
	{"claw", "slice", "slices"},
	{"claw", "smash", "smashes"},
	{"claw", "tear", "tears"},
	{"claw", "crush", "crushes"},
	{"claw", "scratch", "scratches"},
	{"claw", "cut", "cuts"},
	{"claw", "rip", "rips"},
	{"jaws", "rip", "rips"},
	{"jaws", "tear", "tears"},
	{"jaws", "bite", "bites"},
	{"jaws", "gnaw", "gnaws"},
	{"jaws", "rip", "rips"},
	{"jaws", "munch", "munches"},
	{"jaws", "rip", "rips"},
	{"frontteeth", "slice", "slices"},
	{"frontteeth", "cut", "cuts"},
	{"frontteeth", "impale", "impales"},
	{"frontteeth", "mutilate", "mutilates"},
	{"frontteeth", "scar", "scars"},
	{"frontteeth", "crush", "crushes"},
	{"frontteeth", "strike", "strikes"},
	{"tail", "whap", "whaps"},
	{"tail", "smash", "smashes"},
	{"tail", "strike", "strikes"},
	{"tail", "thwack", "thwacks"},
	{"tail", "pummel", "pummels"},
	{"tail", "stroke", "strokes"},
	{"tail", "whap", "whaps"},
	{"sting", "prick", "pricks"},
	{"sting", "stab", "stabs"},
	{"sting", "impale", "impales"},
	{"sting", "prick", "pricks"},
	{"sting", "stab", "stabs"},
	{"sting", "sting", "stings"},
	{"sting", "cut", "cuts"},
	{"tentacle", "sting", "stings"},
	{"tentacle", "strike", "strikes"},
	{"tentacle", "shock", "shocks"},
	{"tentacle", "crush", "crushes"},
	{"tentacle", "thwack", "thwacks"},
	{"tentacle", "cut", "cuts"},
	{"tentacle", "sting", "stings"},
	{"pick", "strike", "strikes"},
	{"pick", "slice", "slices"},
	{"pick", "pound", "pounds"},
	{"pick", "beat", "beats"},
	{"pick", "cleave", "cleaves"},
	{"pick", "maul", "mauls"},
	{"pick", "crush", "crushes"},
	{NULL, NULL}};
char *strikewitharray[][4] = {
	{"human", "left","","fist"},
	{"human", "right","","fist"},
	{"human", "left","","elbow"},
	{"human", "right","","elbow"},
	{"elf", "left","","fist"},
	{"elf", "right","","fist"},
	{"elf", "left","","elbow"},
	{"elf", "right","","elbow"},
	{"dwarf", "left","","fist"},
	{"dwarf", "right","","fist"},
	{"dwarf", "left","","elbow"},
	{"dwarf", "right","","elbow"},
	{"zombie", "left","","fist"},
	{"zombie", "right","","fist"},
	{"zombie", "left","","elbow"},
	{"zombie", "right","","elbow"},
	{"wyvern", "left","","fist"},
	{"wyvern", "right","","fist"},
	{"wyvern", "left","","elbow"},
	{"wyvern", "right","","elbow"},
	{"turtle", "left","","fist"},
	{"turtle", "right","","fist"},
	{"turtle", "left","","elbow"},
	{"turtle", "right","","elbow"},
	{"spider", "left","","fist"},
	{"spider", "right","","fist"},
	{"spider", "left","","elbow"},
	{"spider", "right","","elbow"},
	{"slug", "left","","fist"},
	{"slug", "right","","fist"},
	{"slug", "left","","elbow"},
	{"slug", "right","","elbow"},
	{"orc", "left","","fist"},
	{"orc", "right","","fist"},
	{"orc", "left","","elbow"},
	{"orc", "right","","elbow"},
	{"deity", "left","","fist"},
	{"deity", "right","","fist"},
	{"deity", "left","","elbow"},
	{"deity", "right","","elbow"},
	{"fox", "left","","paw"},
	{"fox", "right","","paw"},
	{"fox", "gnashing","","teeth"},
	{"fox", "gnashing","","teeth"},
	{"rabbit", "left","","paw"},
	{"rabbit", "right","","paw"},
	{"rabbit", "gnashing","","teeth"},
	{"rabbit", "gnashing","","teeth"},
	{"duck", "snapping", "","bill"},
	{"duck", "snapping", "","bill"},
	{"duck", "snapping", "","bill"},
	{"duck", "snapping", "","bill"},
	{"wolf", "ferocious", "","teeth"},
	{"wolf", "left", "","claw"},
	{"wolf", "right", "","claw"},
	{"wolf", "blickering","", "jaws"},
	{"chipmunk", "snapping","", "frontteeth"},
	{"chipmunk", "sharp","", "frontteeth"},
	{"chipmunk", "sharp","", "frontteeth"},
	{"chipmunk", "long","", "tail"},
	{"ropegnaw", "little","", "sting"},
	{"ropegnaw", "little", "","jaws"},
	{"ropegnaw", "little", "","sting"},
	{"ropegnaw", "little", "","jaws"},
	{"buggie", "little", "","sting"},
	{"buggie", "little", "","jaws"},
	{"buggie", "little", "","sting"},
	{"buggie", "little", "","jaws"},
	{"ooze", "long", "","tentacle"},
	{"ooze", "sharp", "","tentacle"},
	{"ooze", "agile", "","tentacle"},
	{"ooze", "stinging", "","tentacle"},
	{"troll", "left", "","fist"},
	{"troll", "right", "","fist"},
	{"troll", "left", "","elbow"},
	{"troll", "right", "","elbow"},
	{NULL, NULL}};
char *positionarray[][2] = {
	{"human", "head"},
	{"human", "left arm"},
	{"human", "right arm"},
	{"human", "neck"},
	{"human", "left leg"},
	{"human", "right leg"},

	{"elf", "head"},
	{"elf", "left arm"},
	{"elf", "right arm"},
	{"elf", "neck"},
	{"elf", "left leg"},
	{"elf", "right leg"},

	{"dwarf", "head"},
	{"dwarf", "left arm"},
	{"dwarf", "right arm"},
	{"dwarf", "neck"},
	{"dwarf", "left leg"},
	{"dwarf", "right leg"},

	{"zombie", "head"},
	{"zombie", "left arm"},
	{"zombie", "right arm"},
	{"zombie", "neck"},
	{"zombie", "left leg"},
	{"zombie", "right leg"},
	
	{"wyvern", "head"},
	{"wyvern", "left arm"},
	{"wyvern", "right arm"},
	{"wyvern", "neck"},
	{"wyvern", "left leg"},
	{"wyvern", "right leg"},
	
	{"turtle", "head"},
	{"turtle", "left arm"},
	{"turtle", "right arm"},
	{"turtle", "neck"},
	{"turtle", "left leg"},
	{"turtle", "right leg"},
	
	{"spider", "head"},
	{"spider", "left arm"},
	{"spider", "right arm"},
	{"spider", "neck"},
	{"spider", "left leg"},
	{"spider", "right leg"},
	
	{"slug", "head"},
	{"slug", "left arm"},
	{"slug", "right arm"},
	{"slug", "neck"},
	{"slug", "left leg"},
	{"slug", "right leg"},
	
	{"orc", "head"},
	{"orc", "left arm"},
	{"orc", "right arm"},
	{"orc", "neck"},
	{"orc", "left leg"},
	{"orc", "right leg"},
	
	{"diety", "head"},
	{"diety", "left arm"},
	{"diety", "right arm"},
	{"diety", "neck"},
	{"diety", "left leg"},
	{"diety", "right leg"},
	
	{"fox", "head"},
	{"fox", "head"},
	{"fox", "left paw"},
	{"fox", "right paw"},
	{"fox", "body"},
	{"fox", "neck"},
	
	{"rabbit", "head"},
	{"rabbit", "head"},
	{"rabbit", "left paw"},
	{"rabbit", "right paw"},
	{"rabbit", "furry body"},
	{"rabbit", "long ears"},

	{"duck", "head"},
	{"duck", "left wing"},
	{"duck", "right wing"},
	{"duck", "body"},
	{"duck", "head"},
	{"duck", "body"},

	{"wolf", "head"},
	{"wolf", "body"},
	{"wolf", "tail"},
	{"wolf", "chest"},
	{"wolf", "snout"},
	{"wolf", "back"},

	{"chipmunk", "long tail"},
	{"chipmunk", "head"},
	{"chipmunk", "body"},
	{"chipmunk", "hind legs"},
	{"chipmunk", "front legs"},
	{"chipmunk", "body"},

	{"ropegnaw", "shell"},
	{"ropegnaw", "antenna"},
	{"ropegnaw", "spiderly legs"},
	{"ropegnaw", "spiderly legs"},
	{"ropegnaw", "body"},
	{"ropegnaw", "shell"},

	{"buggie", "shell"},
	{"buggie", "antenna"},
	{"buggie", "spiderly legs"},
	{"buggie", "spiderly legs"},
	{"buggie", "body"},
	{"buggie", "shell"},

	{"ooze", "white-blue flesh"},
	{"ooze", "malformed body"},
	{"ooze", "unshapely appendage"},
	{"ooze", "reaching tentacle"},
	{"ooze", "sickly body"},

	{"troll", "left leg"},
	{"troll", "right leg"},
	{"troll", "chest"},
	{"troll", "head"},
	{"troll", "left arm"},
	{"troll", "right arm"},

	{NULL, NULL}};
	     
combat_description combat;
int debuggin=0;
int infinity=0;
char *logfile;
FILE *outputfile;

MYSQL mysql;
MYSQL_RES *res;
MYSQL_ROW row;

int returnWeapon(char *weapon)
{
	int i=0;
	while (strikeweaponarray[i][0]!=NULL) 
	{
		if (!strcmp(strikeweaponarray[i][0], weapon))
		{
			return i+1;
		}
		i++;
	}
	return 0;
}

char *return_time()
{
	time_t tijd;
	time(&tijd);
	return ctime(&tijd);
}

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
	if (filep != NULL)
	{
		time(&tijd);
		datum=*(gmtime(&tijd));
		fprintf(filep, "[%i:%i:%i %i-%i-%i] ",
		datum.tm_hour,datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900);
		va_start(ap, fmt);
		(void) vfprintf(filep, fmt, ap);
		va_end(ap);
		fclose(filep);
	}
	else
	{
		int i = errno;
		printf("error %i: %s on file %s\n", i, strerror(i), filenaam);
		exit(1);
	}
}

void mysqlExitErr(char *sqlstring, MYSQL *mysql)
{
fprintf(outputfile, "[%sbg: MySQL Error : %s\n {%s}\n", return_time(), mysql_error(mysql), sqlstring );
fflush(outputfile);
}

void error(char *string)
{
fprintf(outputfile, "[%sbg: Fatal Error %s\n", return_time(), string );
fflush(outputfile);
infinity=1;
}

int checkWeaponSkill(char *itemname, int *skillid)
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	char sqlstring[1024];
	int modifiercount;

	*skillid = 0;
	if (!strcmp(itemname,"pick"))
	{
		/*axe/mace skill*/
		*skillid = 1;
	}
	if (!strcmp(itemname,"knife"))
	{
		/*knife skill*/
		*skillid = 2;
	}
	sprintf(sqlstring, 
		"select * from skills, skilltable where "
		"skilltable.number = %i and "
		"skilltable.forwhom = '%s' and "
		"skilltable.number = skills.number"
		, *skillid, row[XNAME]);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}
	
	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	
	if (mysql_num_rows(res2)!=0)
	{
		int i;
		row2 = mysql_fetch_row(res2);
		switch (atoi(row2[SMODIFIERNAME]))
		{
			case 1 :
				modifiercount = atoi(row[XSTRENGTH]) / 2;
				break;
			case 2 :
				modifiercount = atoi(row[XDEXTERITY]) / 2;
				break;
			case 3 :
				modifiercount = atoi(row[XINTELLIGENCE]) / 2;
				break;
			case 4 :
				modifiercount = atoi(row[XWISDOM]) / 2;
				break ;
			case 5 :
				modifiercount = atoi(row[XCONSTITUTION]) / 2;
				break;
			default :
				modifiercount = 0;
				break;
		}
		i = atoi(row2[SSKILLLEVEL + SKILLTOTAL]) + modifiercount;
		return i;
	}

	return 0;
}

int checkBestHand(int *skillid)
/* returns 1 if right hand, 0 if left hand is best*/
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	int position,i;
	char sqlstring[1024];
	/* put a position into combat */
	position = random() % 6;
	i=0;
	while (strcmp(row[YRACE], positionarray[i][0])) {i++;}
	position+=i;combat.onposition=position;

	/*check for item in hands*/
	sprintf(sqlstring, 
		"select tmp_itemtable.*, items.* from tmp_itemtable, items where "
		"tmp_itemtable.id = items.id and "
		"tmp_itemtable.belongsto = '%s' and "
		"(tmp_itemtable.wielding = 1 or tmp_itemtable.wielding = 2)"
		, row[XNAME]);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}
	
	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	
	if (mysql_num_rows(res2)==1)
	{
		row2 = mysql_fetch_row(res2);
		checkWeaponSkill(row2[TMPITEMTOTAL + INAME], skillid);
		if (returnWeapon(row2[TMPITEMTOTAL + INAME]))
		{
			fprintf(outputfile, "with %s ", row2[TMPITEMTOTAL + INAME]);
			fflush(outputfile);
			strcpy(combat.adject1, row2[TMPITEMTOTAL + IADJECT1]);
			strcpy(combat.adject2, row2[TMPITEMTOTAL + IADJECT2]);
			strcpy(combat.adject3, row2[TMPITEMTOTAL + IADJECT3]);
			strcpy(combat.name, row2[TMPITEMTOTAL + INAME]);
			i = 0;
			while (strcmp(combat.name, strikeweaponarray[i][0])) {i++;}
			i += random() % 7;
			combat.verb = i;
			return !strcmp(row2[IWIELDING],"1");
		}
	}

	if (mysql_num_rows(res2)==2)
	{
		int i,j, skillid2;
		char temp[100];
		row2 = mysql_fetch_row(res2);
		i = -10;
		if (returnWeapon(row2[TMPITEMTOTAL + INAME]))
		{
			strcpy(combat.adject1, row2[TMPITEMTOTAL + IADJECT1]);
			strcpy(combat.adject2, row2[TMPITEMTOTAL + IADJECT2]);
			strcpy(combat.adject3, row2[TMPITEMTOTAL + IADJECT3]);
			strcpy(combat.name, row2[TMPITEMTOTAL + INAME]);
			i = 0;
			while (strcmp(combat.name, strikeweaponarray[i][0])) {i++;}
			i += random() % 7;
			combat.verb = i;
			strcpy(temp, row2[TMPITEMTOTAL + INAME]);
			i = checkWeaponSkill(row2[TMPITEMTOTAL + INAME], &skillid2);
		}
		row2 = mysql_fetch_row(res2);
		if (returnWeapon(row2[TMPITEMTOTAL + INAME]))
		{
			j = checkWeaponSkill(row2[TMPITEMTOTAL + INAME], skillid);
		}
		else
		{
			j = -10;
		}
		if ((i==-10) && (j==-10)) 
		{
			if ((i==0) && (j==0))
			{
				if (i>j) 
				{
					fprintf(outputfile, "with %s ", temp);
					fflush(outputfile);
				}
				else
				{
					int i;
					fprintf(outputfile, "with %s ", row2[TMPITEMTOTAL + INAME]);
					fflush(outputfile);
					strcpy(combat.adject1, row2[TMPITEMTOTAL + IADJECT1]);
					strcpy(combat.adject2, row2[TMPITEMTOTAL + IADJECT2]);
					strcpy(combat.adject3, row2[TMPITEMTOTAL + IADJECT3]);
					strcpy(combat.name, row2[TMPITEMTOTAL + INAME]);
					i = 0;
					while (strcmp(combat.name, strikeweaponarray[i][0])) {i++;}
					i += random() % 7;
					combat.verb = i;
				}
				if (i>j) 
				{ 
					char temp[100];
					/* first found row is better */
					/* opposite hand of the second row*/
					*skillid = skillid2;
					strcpy(temp, row2[IWIELDING]);
					mysql_free_result(res2);
					return strcmp(temp,"1");
				} 
				else 
				{ 
					char temp[100];
					/*second found row is better */
					/* hand of the second row*/
					strcpy(temp, row2[IWIELDING]);
					mysql_free_result(res2);
					return !strcmp(temp,"1");
				}
			} 
		}
	}
	else
	{
		mysql_free_result(res2);
	}


	/*not carrying items in either hand with which a good beating can be inflicted*/
	fprintf(outputfile, "with nothing special ");
	fflush(outputfile);
	*skillid = 0;
	i = 0;
	while (strcmp(row[XRACE], strikewitharray[i][0])) {i++;}
	i += random() % 4;
	strcpy(combat.adject1, strikewitharray[i][1]);
	strcpy(combat.adject2, strikewitharray[i][2]);
	strcpy(combat.adject3, "");
	strcpy(combat.name, strikewitharray[i][3]);
	i = 0;
	while (strcmp(combat.name, strikeweaponarray[i][0])) {i++;}
	i += random() % 7;
	combat.verb = i;
	return 1;
}

int AttackRoll(int whichskill, int *critical)
/* returns 1 if success, 0 if else
 critical returns 1 or -1 if the success is critical or if the 
 failure is critial*/
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	char sqlstring[1024];
	int modifiercount;

	/*get all skill info*/
	sprintf(sqlstring, 
		"select skills.*, skilltable.* from skills, skilltable where "
		"skills.number = skilltable.number and "
		"skills.number = %i and "
		"skilltable.forwhom = '%s'"
		, whichskill, row[XNAME]);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}
	
	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	
	if (mysql_num_rows(res2)==1)
	{
		int i,j;
		row2 = mysql_fetch_row(res2);
		*critical=0;
		i = ((random() % 6) + 1) + ((random() % 6) + 1) + ((random() % 6) + 1);
		if ((i==3) || (i==4)) {*critical=1;}
		if (i==18) {*critical=-1;}
		switch (atoi(row2[SMODIFIERNAME]))
		{
			case 1 :
				modifiercount = atoi(row[XSTRENGTH]) / 2;
				break;
			case 2 :
				modifiercount = atoi(row[XDEXTERITY]) / 2;
				break;
			case 3 :
				modifiercount = atoi(row[XINTELLIGENCE]) / 2;
				break;
			case 4 :
				modifiercount = atoi(row[XWISDOM]) / 2;
				break ;
			case 5 :
				modifiercount = atoi(row[XCONSTITUTION]) / 2;
				break;
			default :
				modifiercount = 0;
				break;
		}
		j = atoi(row2[SSKILLLEVEL + SKILLTOTAL]) + modifiercount;
		fprintf(outputfile, "(dice %i from %i) ", i, j);
		fflush(outputfile);
		mysql_free_result(res2);
		return i<=j;
	}

	mysql_free_result(res2);
	sprintf(sqlstring,
		"select * from skills where "
		"skills.number = %i"
		, whichskill);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}

	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}

	if (mysql_num_rows(res2)!=0)
	{
		int i;
		i = ((random() % 6) + 1) + ((random() % 6) + 1) + ((random() % 6) + 1);
		if ((i==3) || (i==4)) {*critical=1;}
		if (i==18) {*critical=-1;}
		row2 = mysql_fetch_row(res2);
		switch (atoi(row2[SMODIFIERNAME]))
		{
			case 1 :
				modifiercount = atoi(row[XSTRENGTH]) / 2;
				break;
			case 2 :
				modifiercount = atoi(row[XDEXTERITY]) / 2;
				break;
			case 3 :
				modifiercount = atoi(row[XINTELLIGENCE]) / 2;
				break;
			case 4 :
				modifiercount = atoi(row[XWISDOM]) / 2;
				break ;
			case 5 :
				modifiercount = atoi(row[XCONSTITUTION]) / 2;
				break;
			default :
				modifiercount = 0;
				break;
		}
		fprintf(outputfile, "(dice %i from %i) ", i, modifiercount);
		fflush(outputfile);
		mysql_free_result(res2);
		return i<=modifiercount;
	}
	return 0;
}

int DefenseRoll()
/* returns 1 if defense is successfull, 0 if not*/
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	char sqlstring[1024];
	int pasdefense, dice;

	/*active defense skills*/
	/* either dodging, blocking or parrying */
	/* dodging is part of your move skill */
	/* let's forget about the rest for now */
	/* blocking is possible if person is wielding a shield */
	/* parrying is possible if person is wielding handweapons */
	/* (blades, clubs, axes, spears, polearms) */
	
	/*passive defense skills*/
	/*	armour 1..6 */
	/*	shield 1..4 */
	
	/*get all skill info*/
	sprintf(sqlstring, 
		"select sum(items.pasdefense) from items, tmp_itemtable where "
		"tmp_itemtable.id = items.id and "
		"(tmp_itemtable.wielding <> '' or "
		"tmp_itemtable.wearing <> '') "
		"and tmp_itemtable.belongsto='%s'"
		, row[YNAME]);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}
	
	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	
	if (mysql_num_rows(res2)==1)
	{
		row2 = mysql_fetch_row(res2);
		if (row2[0] == NULL)
		{
			pasdefense = 	(atoi(row[YMOVEMENTSTATS]) / atoi(row[YMAXMOVE])) * 4;
		}
		else
		{
			pasdefense = atoi(row2[0]) + (atoi(row[YMOVEMENTSTATS]) / atoi(row[YMAXMOVE])) * 4;
		}
	}
	else
	{
		pasdefense = (atoi(row[YMOVEMENTSTATS]) / atoi(row[YMAXMOVE])) * 4;
	}
	mysql_free_result(res2);

	dice = ((random() % 6) + 1) + ((random() % 6) + 1) + ((random() % 6) + 1);
	fprintf(outputfile, "(dice %i from %i)", dice, pasdefense);
	fflush(outputfile);
	if ((dice == 3) || (dice == 4))
	{
		/* defense is always successfull when the dice rolls 3 or 4 */
		return 1;
	}
	return dice <= pasdefense;
}

int DamageRoll()
/* returns the amount of hits done */
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	char sqlstring[1024];
	int damage, damageresistance, nrdice, i;

	/* weapon determines how many dice to roll */
	/* low tech weapons determines strength, so basically in this case everything is strength based */

	nrdice = atoi(row[XSTRENGTH])/6+1;
	damage = 0;
	i = 1;
	while (i<=nrdice) {damage+=((random() % 6) + 1);i++;}
	
	/* subtract DR Damage Resistance from dicedamage */

	sprintf(sqlstring, 
		"select sum(items.damageresistance) from items, tmp_itemtable where "
		"tmp_itemtable.id = items.id and "
		"(tmp_itemtable.wielding <> '' or "
		"tmp_itemtable.wearing <> '') "
		"and tmp_itemtable.belongsto='%s'"
		, row[YNAME]);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}

	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	
	if (mysql_num_rows(res2)==1)
	{
		row2 = mysql_fetch_row(res2);
		if (row2[0] == NULL)
		{
			damageresistance = 0;
		}
		else
		{
			damageresistance = atoi(row2[0]);
		}
	}
	else
	{
		damageresistance = 0;
	}
	mysql_free_result(res2);

	if (damage<0) {damage=0;}

	sprintf(sqlstring, 
		"update tmp_usertable set vitals = vitals + %i where name='%s'"
		, damage, row[YNAME]);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}

	return damage;
}

int embraceDeath()
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	char sqlstring[1024], logname[100];
	int level_range, base_exp, align, changealign;
	
	/* write down death message from victim */
	WriteMessage2(row[YNAME], atoi(row[XROOM]), 
	"%s dies a terrible death.<BR>\r\n", row[YNAME]);
	sprintf(logname, "%s%s.log",USERHeader,row[YNAME]);
	WriteSentenceIntoOwnLogFile2(logname, "You die.<BR>\r\n");
	fprintf(outputfile, "%s dies at the hands of %s.\n", row[YNAME], row[XNAME]);
	fflush(outputfile);

	/* drops inventory of the killed party */
	sprintf(sqlstring, 
		"select * "
		"from tmp_itemtable "
		"where search='' and "
		"belongsto='%s' and "
		"amount>0 and "
		"room=0 and "
		"wearing='' and "
		"wielding=''"
		, row[YNAME]);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}

	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	
	if (res2 != NULL)
	{
		row2 = mysql_fetch_row(res2);
		while (row2 != NULL)
		{
			sprintf(sqlstring, 
				"update tmp_itemtable "
				"set amount = amount + %i "
				"where id = %i and "
				"search = '' and "
				"belongsto = '' and "
				"amount > 0 and "
				"room = %i and "
				"wearing = '' and "
				"wielding = ''"
				, atoi(row2[IAMOUNT]), atoi(row2[IID]), atoi(row[YROOM]));
			if (mysql_query(&mysql, sqlstring))
			{
				mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
				exit(1);
			}
			if (mysql_affected_rows(&mysql) != 1)
			{
				sprintf(sqlstring, 
					"insert into tmp_itemtable "
					"values(%i, '', '', %i, %i, '', '')"
					, atoi(row2[IID]), atoi(row2[IAMOUNT]), atoi(row[YROOM]));
				if (mysql_query(&mysql, sqlstring))
				{
					mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
					exit(1);
				}
			} /* if item is not already in room, create the item */
			row2 = mysql_fetch_row(res2);
		}
		mysql_free_result(res2);
	} /* end while items found in inventory */
	/* remove items from inventory */
	sprintf(sqlstring, 
	"delete from tmp_itemtable "
	"where search = '' and "
	"belongsto = '%s' and "
	"amount > 0 and "
	"room = 0 and "
	"wearing = '' and "
	"wielding = ''",
	row[YNAME]);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}

	/* add corpse to the existing corpses or create a new one */
	sprintf(sqlstring, 
		"select tmp_itemtable.* "
		"from tmp_itemtable, items "
		"where tmp_itemtable.id = items.id and "
		"items.adject1 = 'dead' and "
		"items.adject2 = '%s' and "
		"items.adject3 = 'killed' and "
		"items.name = 'corpse' and "
		"search = '' and "
		"belongsto = '' and "
		"amount > 0 and "
		"tmp_itemtable.room = %s and "
		"wearing = '' and "
		"wielding = ''"	, row[YRACE], row[YROOM]);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}

	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	
	if (res2 != NULL)
	{
		row2 = mysql_fetch_row(res2);
		if (row2 != NULL)
		{
			/* another corpse, same origin, already in room */
			sprintf(sqlstring, 
				"update tmp_itemtable "
				"set amount = amount + 1 "
				"where id = %s and "
				"search = '' and "
				"belongsto = '' and "
				"amount > 0 and "
				"room = %i and "
				"wearing = '' and "
				"wielding = ''"
				, row2[IID], atoi(row[YROOM]));
			if (mysql_query(&mysql, sqlstring))
			{
				mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
				exit(1);
			}
		}
		else
		{
			/* create new corpse */
			sprintf(sqlstring, 
				"insert into tmp_itemtable "
				"select id, '', '', 1, %i, '', '' "
				"from items where "
				"adject1 = 'dead' and "
				"adject2 = '%s' and "
				"items.adject3 = 'killed' and "
				"items.name = 'corpse'"
				, atoi(row[YROOM]), row[YRACE]);
			if (mysql_query(&mysql, sqlstring))
			{
				mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
				exit(1);
			}
		} /* if item is not already in room, create the item */
		mysql_free_result(res2);
	} /* end if query no goody */
	/* end of corpse added */	

	/* remove person back to Cave and reset fighting unless /*
	/* person in question is a bot */
	if (strcmp(row[YGOD], "3"))
	{
		/* is not a bot */
		sprintf(sqlstring, 
			"update tmp_usertable set room=1, fightingwho=''"
			", experience=experience-round((experience % 1000)/2)"
			" where name='%s'"
			, row[YNAME]);
		sprintf(logname, "%s%s.log",USERHeader,row[YNAME]);
		WriteSentenceIntoOwnLogFile2(logname, "You lose %i experience points.<BR>\r\n", (atoi(row[YEXPERIENCE]) % 1000)/2);
	}
	else
	{
		/* is a bot, yessirree */
		sprintf(sqlstring, 
			"delete from tmp_usertable "
			"where name='%s'"
			, row[YNAME]);
	}
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}

	if (atoi(row[XGOD])==0)
	{ 
		/* the person who is receiving the experience points is a real person */
		/* compute xp gained from the skill */
	
		level_range = (atoi(row[YEXPERIENCE]) - atoi(row[XEXPERIENCE])) / 1000;
		/* compute the base exp */
		switch (level_range)
		{
			default : base_exp = 0; break;
			case -9 : base_exp = 1; break;
			case -8 : base_exp = 2; break;
			case -7 : base_exp = 5; break;
			case -6 : base_exp = 9; break;
			case -5 : base_exp = 11; break;
			case -4 : base_exp = 12; break;
			case -3 : base_exp = 15; break;
			case -2 : base_exp = 20; break;
			case -1 : base_exp = 30; break;
			case 0 : base_exp = 40; break;
			case 1 : base_exp = 50; break;
			case 2 : base_exp = 60; break;
			case 3 : base_exp = 70; break;
			case 4 : base_exp = 80; break;
		}
		if (level_range > 4)
			base_exp = 160 + 20 * (level_range - 4);
		
		/* add experience to user but first check for levelling information */
		sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
		WriteSentenceIntoOwnLogFile2(logname, "You gain %i experience points.<BR>\r\n", base_exp);
		if (atoi(row[XEXPERIENCE]) / 1000< (atoi(row[XEXPERIENCE])+base_exp)/1000)
		{
			/* person has levelled */
			sprintf(sqlstring, 
				"update tmp_usertable set fightingwho=''. "
				"experience=experience+%i, "
				"practises=practises+3, "
				"training=training+1"
				" where name='%s'"
				, base_exp, row[YNAME]);
			if (mysql_query(&mysql, sqlstring))
			{
				mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
				exit(1);
			}
			/* write down that person has levelled */
			WriteMessage2(row[XNAME], atoi(row[XROOM]), 
			"%s has levelled!<BR>\r\n", row[XNAME]);
			sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
			WriteSentenceIntoOwnLogFile2(logname, "You have levelled. You are now level %i.<BR>\r\n", (atoi(row[XEXPERIENCE])+base_exp)/1000);
			fprintf(outputfile, "%s levels to %i.\n", row[YNAME], row[XNAME],  (atoi(row[XEXPERIENCE])+base_exp)/1000);
			fflush(outputfile);
		} /* person has levelled */
		else
		{
			/* person has not levelled */
			sprintf(sqlstring, 
				"update tmp_usertable set fightingwho=''"
				", experience=experience+%i"
				" where name='%s'"
				, base_exp, row[YNAME]);
			if (mysql_query(&mysql, sqlstring))
			{
				mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
				exit(1);
			}
		} /* person has not levelled */

		/* edit alignment of killer */
		align = atoi(row[YALIGNMENT]) - atoi(row[XALIGNMENT]);
		if (align > 90) /* killed person more good then slayer */
		{
		}
		if (align < -90)	/* killed person more bad then slayer */
		{
		}
	} /* the person who gets experience/alignment is real */
	return 1;
}

int fleeNow(char *fname, int froom)
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	char logname[100];
	char sqlstring[1024];
	int fled;
	
	/* makes a valid attempt to flee somewhere */
	sprintf(logname, "%s%s.log",USERHeader,fname);
	sprintf(sqlstring, 
		"select * "
		"from rooms "
		"where id = %i"
		, froom);
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}

	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	
	if (res2 != NULL)
	{
		row2 = mysql_fetch_row(res2);
		if (row2 != NULL)
		{
			int i,dirint;
			char direction[20];
			i = random() % 4;
			switch (i)
			{
			case 0 : {dirint=RNORTH;strcpy(direction, "north");break;}
			case 1 : {dirint=RSOUTH;strcpy(direction, "south");break;}
			case 2 : {dirint=REAST;strcpy(direction, "east");break;}
			case 3 : {dirint=RWEST;strcpy(direction, "west");break;}
			}
			if (!strcmp(row2[dirint],"0"))
			{
				/* you try to flee but fail, no exit that way. */
				WriteSentenceIntoOwnLogFile2(logname, "You try to flee %s, but fail!<BR>\r\n", direction);
				WriteMessage2(fname, froom, "%s tries to flee %s, but fails!!!<BR>\r\n", fname, direction);
				fprintf(outputfile, "%s fails to flee %s (%s).\n", fname, direction, row2[dirint]);
				fflush(outputfile);
				fled=0;
			} 
			else
			{
				/* you flee successfully */
				WriteSentenceIntoOwnLogFile2(logname, "You flee %s!<BR>\r\n", direction);
				WriteMessage2(fname, froom, "%s flees to the %s!!!<BR>\r\n", fname, direction);
				sprintf(sqlstring, 
					"update tmp_usertable set room=%s, experience=experience - abs((experience % 1000)-20) "
					"where name = '%s'"
					, row2[dirint], fname);
				if (mysql_query(&mysql, sqlstring))
				{
					mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
					exit(1);
				}
				WriteMessage2(fname, atoi(row2[dirint]), "%s appears, running and looking over his shoulder.<BR>\r\n", fname, direction);
				fprintf(outputfile, "%s flees %s (%s).\n", fname, direction, row2[dirint]);
				fflush(outputfile);
				fled=1;
			} /* succeed in fleeing */
		}
		mysql_free_result(res2);
	}
	return fled;
}

int checkforFlee()
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	char sqlstring[1024];
	
	/* check who's to flee and who's to stay */
	sprintf(sqlstring, 
		"select X.*, Y.* from tmp_usertable as X, tmp_usertable as Y where "
		"X.fightingwho = Y.name and "
		"X.sleep = 0 and "
		"Y.sleep = 0 and "
		"X.room = Y.room and "
		"Y.god != 2 and "
		"X.fightable = 1 and "
		"Y.fightable = 1 and "
		"Y.vitals > Y.whimpy and "
		"Y.whimpy != 0");
	if (mysql_query(&mysql, sqlstring))
	{
		mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
		exit(1);
	}

	if (!(res2 = mysql_store_result(&mysql)))
	{
		mysqlExitErr("Unable to retrieve results, exiting...", &mysql);
		exit(1);
	}
	
	if (res2 != NULL)
	{
		opendbconnection();
		while ((row2 = mysql_fetch_row(res2))!=NULL)
		{
			fleeNow(row2[YNAME], atoi(row2[YROOM]));
		}
		mysql_free_result(res2);
		closedbconnection();
	}
	return 1;
}

void terriblyWrong()
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	char sqlstring[1024];
	int wrongtype;
	char logname[100];
	
	sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
	opendbconnection();

	/* create instances where things go very wrong */
	fprintf(outputfile, " to a TERRIBLE DEGREE.\n");
	fflush(outputfile);
	wrongtype = random() % 3;
	switch(wrongtype)
	{
		case 1:
		WriteSentenceIntoOwnLogFile2(logname, "You accidently hit yourself on the foot.<BR>\r\n");
		WriteMessage2(row[XNAME], atoi(row[XROOM]), "%s, in %s clumsiness, hits %sself on the foot.<BR>\r\n", row[XNAME], HeShe3(row[XSEX]), HeShe2(row[XSEX]));
		/* diminish damage */
		sprintf(sqlstring, 
			"update tmp_usertable set vitals = vitals + 2 "
			"where name = '%s'", row[XNAME]);
		if (mysql_query(&mysql, sqlstring))
		{
			mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
			exit(1);
		}
			break;
		case 2:
		WriteSentenceIntoOwnLogFile2(logname, "You accidentally sprain your wrist.<BR>\r\n");
		WriteMessage2(row[XNAME], atoi(row[XROOM]), "%s sprains %s wrist when attempting a particularly nasty move.<BR>\r\n", row[XNAME], HeShe3(row[XSEX]));
		/* diminish damage */
		sprintf(sqlstring, 
			"update tmp_usertable set vitals = vitals + 2 "
			"where name = '%s'", row[XNAME]);
		if (mysql_query(&mysql, sqlstring))
		{
			mysqlExitErr("Unable to send sql statement, exiting...", &mysql);
			exit(1);
		}
			break;
		default:
			WriteSentenceIntoOwnLogFile2(logname, "You accidentally lose your balance and you miss completely.<BR>\r\n");
			WriteMessage2(row[XNAME], atoi(row[XROOM]), "%s accidentally loses %s balance completely, and misses big time.<BR>\r\n", row[XNAME], HeShe3(row[XSEX]));
			break;
	}
	closedbconnection();
}
						
void terriblyGood()
{
	MYSQL_RES *res2;
	MYSQL_ROW row2;
	char sqlstring[1024];
	int wrongtype;
	char logname[100], logname2[100];
	
	sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
	sprintf(logname2, "%s%s.log",USERHeader,row[YNAME]);
	opendbconnection();

	/* create instances where things go very good */
	fprintf(outputfile, " to a FANTASTIC DEGREE.\n");
	fflush(outputfile);
	wrongtype = random() % 3;
	if (combat.adject3[0]==0)
	{
		switch(wrongtype)
		{
			case 1:
				WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
				"%s seems to have hit %s headon.<BR>\r\n", row[XNAME], row[YNAME]);
				WriteSentenceIntoOwnLogFile2(logname, 
				"You seem to have hit %s headon.<BR>\r\n", row[YNAME]);
				WriteSentenceIntoOwnLogFile2(logname2, 
				"%s hits you headon, leaving you groggy and disoriented.<BR>\r\n", row[XNAME]);
				break;
			case 2:
				WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
				"%s seems to have hit %s a lot stronger, knocking %s senseless.<BR>\r\n", row[XNAME], row[YNAME], HeShe2(row[YSEX]));
				WriteSentenceIntoOwnLogFile2(logname, 
				"You hit %s headon, leaving %s groggy and disoriented.<BR>\r\n", row[YNAME], HeShe2(row[YSEX]));
				WriteSentenceIntoOwnLogFile2(logname2, 
				"%s hits you headon, leaving you groggy and disoriented.<BR>\r\n", row[XNAME]);
				break;
			default:
				WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
				"%s hits %s and you hear bones crushing.<BR>\r\n", row[XNAME], row[YNAME]);
				WriteSentenceIntoOwnLogFile2(logname, 
				"You hit %s and you hear bones crushing.<BR>\r\n", row[YNAME]);
				WriteSentenceIntoOwnLogFile2(logname2, 
				"%s hits you and you hear your bones crushing from the impact.<BR>\r\n", row[XNAME]);
				break;
		}
	}
	else
	{
		WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
		"%s makes the %s, %s %s whistle through the air with great speed, heading towards %s.<BR>\r\n", row[XNAME], combat.adject1, combat.adject2, combat.name, row[YNAME]);
		WriteSentenceIntoOwnLogFile2(logname, 
		"You make the %s, %s %s whistle through the air with great speed, heading towards your opponent.<BR>\r\n", combat.adject1, combat.adject2, combat.name);
		WriteSentenceIntoOwnLogFile2(logname2, 
		"%s makes the %s, %s %s whistle through the air heading with great speed towards you.<BR>\r\n", row[XNAME], combat.adject1, combat.adject2, combat.name);
	}
	closedbconnection();
}
						
int StartSQL()
{
	FILE *fp;
	int counter=0;
	int total_delay=5;
	char logname[100], message[100];
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

	/*repeat until infinity, can only be broken by an external signal*/
 	while (!infinity)
 	{
	if (debuggin==2) 
	{
		fprintf(outputfile, "-----------------------------------------------------------\n");
		fflush(outputfile);
	}
	if (mysql_query(&mysql,
		"select X.*, Y.* from tmp_usertable as X, tmp_usertable as Y where "
		"X.fightingwho = Y.name and "
		"X.sleep = 0 and "
		"Y.sleep = 0 and "
		"X.room = Y.room and "
		"Y.god != 2 and "
		"X.fightable = 1 and "
		"Y.fightable = 1"))
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
	{/*nobody is fighting*/
		if (debuggin==2) 
		{
			fprintf(outputfile, "[%sNobody fighting, sleeping %i sec.\n", return_time(), total_delay);
			fflush(outputfile);
		}
		Wait(total_delay++,0);
		if (total_delay>15) {total_delay=15;}
	}
	else
	{
	/* somebody seems to be fighting then*/
	/* say person x is fighting person y */
		if (debuggin==2) 
		{
			fprintf(outputfile, "[%sSomebody fighting, computing hits and sleeping 1 sec.\n", return_time());
			fflush(outputfile);
		}
		total_delay=5;
		while (((row = mysql_fetch_row(res))!=NULL) && (!infinity))
		{
		int skillid;
		combat.damage = 0;
			fprintf(outputfile, "[%s%s fighting against %s ", return_time(), row[XNAME], row[YNAME]);
			fflush(outputfile);
			/*determine most favorible hand to use/one armed combat*/
			/*things to be remembered:
			- whichhand
			- whichskill
			- whom
			*/
			if (checkBestHand(&skillid))
			{
				int critical;
				combat.defense = 0;
				strcpy(combat.hand, "right");
				fprintf(outputfile, "in right hand with skill %i ", skillid);
				fflush(outputfile);
				/*right hand*/
				/*attack roll*/
				if (AttackRoll(skillid, &critical))
				{
					combat.success = 1;
					fprintf(outputfile, "and is successfull");
					fflush(outputfile);
					/*reaction roll - apparently this is only necessary with NPCs and 
					not with combat as that is handled automatically*/
					if (critical) 
					{
						terriblyGood();
					} else 
					{
	 					/*defense roll - if this is successfull there is NO damage rolls
						- if there is a critical result, no defense is possible.*/
						fprintf(outputfile, "\n");
						if (!DefenseRoll())
						{
							int damage;
							combat.defense = 0;
							fprintf(outputfile, "%s's defense was NOT successfull.\n ", row[YNAME]);
							fflush(outputfile);
							/*damage roll*/
							damage = DamageRoll();
							combat.damage = damage;
							fprintf(outputfile, "%s does %i damage.\n", row[XNAME], damage);
							fflush(outputfile);
						}
						else
						{
							combat.defense = 1;
							fprintf(outputfile, "%s's defense was successfull.\n", row[YNAME]);
							fflush(outputfile);
						}
					}
				}
				else
				{
					fprintf(outputfile, "and is catastrophic");
					fflush(outputfile);
					combat.success = 0;
					if (critical) 
					{
						terriblyWrong();
					}
					else {fprintf(outputfile, "\n");
					}
				}
			}
			else
			{
				int critical;
				combat.defense = 0;
				strcpy(combat.hand, "left");
				fprintf(outputfile, "in left hand with skill %i ", skillid);
				fflush(outputfile);
				/*left hand*/
				/*attack roll*/
				if (AttackRoll(skillid, &critical))
				{
					combat.success = 1;
					fprintf(outputfile, "and is successfull");
					fflush(outputfile);
					/*reaction roll - apparently this is only necessary with NPCs and 
					not with combat as that is handled automatically*/
					if (critical) 
					{
						terriblyGood();
					} else 
					{
	 					/*defense roll - if this is successfull there is NO damage rolls
						- if there is a critical result, no defense is possible.*/
						fprintf(outputfile, "\n");
						if (!DefenseRoll())
						{
							int damage;
							combat.defense = 0;
							fprintf(outputfile, "%s's defense was NOT successfull.\n ", row[YNAME]);
							fflush(outputfile);
							/*damage roll*/
							damage = DamageRoll();
							combat.damage = damage;
							fprintf(outputfile, "%s does %i damage.\n", row[XNAME], damage);
							fflush(outputfile);
						}
						else
						{
							combat.defense = 1;
							fprintf(outputfile, "%s's defense was successfull.\n", row[YNAME]);
							fflush(outputfile);
						}
					}
				}
				else
				{
					fprintf(outputfile, "and is catastrophic");
					fflush(outputfile);
					combat.success = 0;
					if (critical) 
					{
						terriblyWrong();
					}
					else {fprintf(outputfile, "\n");
					}
				}
			}
			/* Print messages to the game using the combat structure */
			opendbconnection();
			if (combat.success==1)
			{
				if (combat.defense)
				{
					if (combat.adject3[0]==0)
					{
						/* we are fighting with nothing in our hand */
						strcpy(message, "%s %s to %s %s with %s %s %s but %s %s.<BR>\r\n");
						WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
						message, row[XNAME], "attempts", strikeweaponarray[combat.verb][1], row[YNAME],  HeShe3(row[XSEX]), combat.adject1, combat.name, row[YNAME], "dodges");
						closedbconnection();
						sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
						WriteSentenceIntoOwnLogFile2(logname, 
						message, "You", "attempt", strikeweaponarray[combat.verb][1], row[YNAME], "your", combat.adject1, combat.name, HeSheSmall(row[YSEX]), "dodges");
						sprintf(logname, "%s%s.log",USERHeader,row[YNAME]);
						WriteSentenceIntoOwnLogFile2(logname, 
						message, row[XNAME], "attempts", strikeweaponarray[combat.verb][1], "you", HeShe3(row[XSEX]), combat.adject1, combat.name, "you", "dodge");
					}
					else
					{
						/* we are fighting with something in our hand */
						strcpy(message, "%s %s to %s %s with %s %s, %s %s but %s %s.<BR>\r\n");
						WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
						message, row[XNAME], "attempts", strikeweaponarray[combat.verb][1], row[YNAME], HeShe3(row[XSEX]), combat.adject1, combat.adject2, combat.name, row[YNAME], "dodges");
						closedbconnection();
						sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
						WriteSentenceIntoOwnLogFile2(logname, 
						message, "You", "attempt", strikeweaponarray[combat.verb][1], row[YNAME], "your", combat.adject1, combat.adject2, combat.name, HeSheSmall(row[YSEX]), "dodges");
						sprintf(logname, "%s%s.log",USERHeader,row[YNAME]);
						WriteSentenceIntoOwnLogFile2(logname, 
						message, row[XNAME], "attempts", strikeweaponarray[combat.verb][1], "you", HeShe3(row[XSEX]), combat.adject1, combat.adject2, combat.name, "you", "dodge");
					}
				}
				else
				{
					if (combat.adject3[0]==0)
					{
						/* we are fighting with nothing in our hand */
						strcpy(message, "%s %s %s on %s %s with %s %s %s.<BR>\r\n");
						WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
						message, row[XNAME], strikeweaponarray[combat.verb][2], row[YNAME],  HeShe3(row[YSEX]), positionarray[combat.onposition][1], HeShe3(row[XSEX]), combat.adject1, combat.name);
						sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
						WriteSentenceIntoOwnLogFile2(logname, 
						message, "You", strikeweaponarray[combat.verb][1], row[YNAME], HeShe3(row[YSEX]), positionarray[combat.onposition][1], "your", combat.adject1, combat.name);
						sprintf(logname, "%s%s.log",USERHeader,row[YNAME]);
						WriteSentenceIntoOwnLogFile2(logname, 
						message, row[XNAME], strikeweaponarray[combat.verb][2], "you", "your", positionarray[combat.onposition][1], HeShe3(row[XSEX]), combat.adject1, combat.name);
					}
					else
					{
						/* we are fighting with something in our hand */
						strcpy(message, "%s %s %s on %s %s with %s %s, %s %s.<BR>\r\n");
						WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
						message, row[XNAME], strikeweaponarray[combat.verb][2], row[YNAME], HeShe3(row[YSEX]), positionarray[combat.onposition][1], HeShe3(row[XSEX]), combat.adject1, combat.adject2, combat.name);
						sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
						WriteSentenceIntoOwnLogFile2(logname, 
						message, "You", strikeweaponarray[combat.verb][1], row[YNAME], HeShe3(row[YSEX]), positionarray[combat.onposition][1], "your", combat.adject1, combat.adject2, combat.name);
						sprintf(logname, "%s%s.log",USERHeader,row[YNAME]);
						WriteSentenceIntoOwnLogFile2(logname, 
						message, row[XNAME], strikeweaponarray[combat.verb][2], "you", "your", positionarray[combat.onposition][1], HeShe3(row[XSEX]), combat.adject1, combat.adject2, combat.name);
					}
					if (strcmp(ShowString(atoi(row[YVITALS]), atoi(row[YMAXVITAL])),
						ShowString(atoi(row[YVITALS])+combat.damage, atoi(row[YMAXVITAL]))))
					{
						/* write down change in vitals for person attacked */
						WriteMessage2(row[YNAME], atoi(row[XROOM]), 
						"%s seems to be %s.<BR>\r\n", row[YNAME], ShowString(atoi(row[YVITALS])+combat.damage, atoi(row[YMAXVITAL])));
						sprintf(logname, "%s%s.log",USERHeader,row[YNAME]);
						WriteSentenceIntoOwnLogFile2(logname, 
						"You seem to be %s.<BR>\r\n", ShowString(atoi(row[YVITALS])+combat.damage, atoi(row[YMAXVITAL])));
					}
					if (atoi(row[YVITALS])+combat.damage > atoi(row[YMAXVITAL]))
					{
						/* the person who is being attacked has been fatally wounded and */
						/* will die now */
						embraceDeath();
					}
					closedbconnection();
				}
			} /* if successfull */
			else
			{
				if (combat.adject3[0]==0)
				{
					/* we are fighting with nothing in our hand */
					strcpy(message, "%s %s to %s %s with %s %s %s but %s.<BR>\r\n");
					WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
					message, row[XNAME], "attempts", strikeweaponarray[combat.verb][1], row[YNAME],  HeShe3(row[XSEX]), combat.adject1, combat.name, "misses");
					closedbconnection();
					sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
					WriteSentenceIntoOwnLogFile2(logname, 
					message, "You", "attempt", strikeweaponarray[combat.verb][1], row[YNAME], "your", combat.adject1, combat.name, "miss");
					sprintf(logname, "%s%s.log",USERHeader,row[YNAME]);
					WriteSentenceIntoOwnLogFile2(logname, 
					message, row[XNAME], "attempts", strikeweaponarray[combat.verb][1], "you", HeShe3(row[XSEX]), combat.adject1, combat.name, "misses");
				}
				else
				{
					/* we are fighting with something in our hand */
					strcpy(message, "%s %s to %s %s with %s %s, %s %s but %s.<BR>\r\n");
					WriteMessageTo2(row[XNAME], row[YNAME], atoi(row[XROOM]), 
					message, row[XNAME], "attempts", strikeweaponarray[combat.verb][1], row[YNAME], HeShe3(row[XSEX]), combat.adject1, combat.adject2, combat.name, "misses");
					closedbconnection();
					sprintf(logname, "%s%s.log",USERHeader,row[XNAME]);
					WriteSentenceIntoOwnLogFile2(logname, 
					message, "You", "attempt", strikeweaponarray[combat.verb][1], row[YNAME], "your", combat.adject1, combat.adject2, combat.name, "miss");
					sprintf(logname, "%s%s.log",USERHeader,row[YNAME]);
					WriteSentenceIntoOwnLogFile2(logname, 
					message, row[XNAME], "attempts", strikeweaponarray[combat.verb][1], "you", HeShe3(row[XSEX]), combat.adject1, combat.adject2, combat.name, "misses");
				}
			} /* if not successfull */
		} /*end of while - no more fighting computations*/
		/* compute the people who need to flee to some direction or other */
		checkforFlee();
		Wait(total_delay,0);
	} /*end if somebody fighting*/

	mysql_free_result(res);
	}

	/*closing mysql connection message*/
	fprintf(outputfile, "[%sclosing mysql connection...\n", return_time());
	fflush(outputfile);

	/*after infinity is done, close mysql connection,
		seems excessive yet might be wise just in case*/
	mysql_close(&mysql);
}

int searchParam(char *term, int cnt, char **prm)
/* examine all parameters available upon loading of the program for a
certain one and return the number of the parameter if found otherwise return
0
term is the term looked for, cnt is the number of parameters, and **prm is
an array of characterstrings containing the parameters
*/
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
	exit(1);
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
	fprintf(outputfile, "[%sReceived SIGCHLD... Continueing execution...", return_time());
	fflush(outputfile);
}

int
main(int cntp, char **prm)
{
	FILE *fp;
	int i;
	char *pidfile;

	outputfile = stdout;

	/*trap all events and have them handled appropriately*/
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
	
	/*parameter check*/
	if ( (searchParam("-h",cntp, prm)) || (searchParam("--help",cntp, prm)) )
	{
		printf("Usage: %s [options]\n\n", prm[0]);
		printf("  -p <filename>            put pid (process id) into <filename>\n");
		printf("                           (default /tmp/mmudbackground.pid)\n");
		printf("  -d/--debug               display ugly debugging information\n");
		printf("  -l <filename>            redirect all logging output to <filename>\n");
		printf("                           (default stdout)\n");
		printf("  -h/--help                display this help and exit\n\n");
 		printf("Report bugs to maartenl@il.fontys.nl\n");
 		return 0;
	}
	
	/*debugging mode on?*/
	if ( (searchParam("-d",cntp, prm)) || (searchParam("--debug",cntp, prm)) )
	{
		debuggin=2;
	}
	
	/*retrieve location of the PID file, the Process Identification File and
	put the process id there.*/
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
	if (fp != NULL)
	{
		fprintf(fp, "%i\n", getpid());
		fclose(fp);
		chmod(pidfile, 0600);
	}
	else
	{
		int i = errno;
		printf("error %i: %s on pidfile %s\n", i, strerror(i), pidfile);
		exit(1);
	}

	/*retrieve location of the Log file, and put all (error) messages there.*/
	i=searchParam("-l", cntp, prm);
	if ((i) && (i<cntp-1))
	{
		logfile = (char *) malloc(strlen(prm[i+1])+3);
		strcpy(logfile,prm[i+1]);
		outputfile = fopen(logfile, "w");
		if (outputfile == NULL)
		{
			printf("Unable to open log file... exiting...\n");
			exit(1);
		}
	}
	else
	{
		logfile="/tmp/mmudbackground.log";
	}

	/*initialisation message*/
	fprintf(outputfile, "[%smmudbackground process started...\n", return_time());
	fflush(outputfile);
	
	/*main process branch*/
	StartSQL();
	
	/*exiting message*/
	fprintf(outputfile, "[%smmudbackground process stopped...\n", return_time());
	fflush(outputfile);
	fclose(outputfile);

	/*exit cleanly*/
	return 0;
}
