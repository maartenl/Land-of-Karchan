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
#include <errno.h>
#include "typedefs.h"
#include "userlib.h"
#include "mud-lib.h"

/*! \file server file containing the import game extentions like ReadFile and WriteRoom and standard communication */

extern roomstruct room;
extern int      events[50];

char           *emotions[][2] = {
	{"agree", "agrees"},
	{"apologize", "apologizes"},
	{"blink", "blinks"},
	{"cheer", "cheers"},
	{"chuckle", "chuckles"},
	{"cough", "coughs"},
	{"dance", "dances"},
	{"disagree", "disagrees"},
	{"flinch", "flinches"},
	{"flirt", "flirts"},
	{"frown", "frowns"},
	{"giggle", "giggles"},
	{"glare", "glares"},
	{"grin", "grins"},
	{"groan", "groans"},
	{"growl", "growls"},
	{"grumble", "grumbles"},
	{"grunt", "grunts"},
	{"hmm", "hmms"},
	{"howl", "howls"},
	{"hum", "hums"},
	{"kneel", "kneels"},
	{"listen", "listens"},
	{"melt", "melts"},
	{"mumble", "mumbles"},
	{"mutter", "mutters"},
	{"nod", "nods"},
	{"purr", "purrs"},
	{"shrug", "shrugs"},
	{"sigh", "sighs"},
	{"smile", "smiles"},
	{"smirk", "smirks"},
	{"snarl", "snarls"},
	{"sneeze", "sneezes"},
	{"stare", "stares"},
	{"think", "thinks"},
	{"wave", "waves"},
	{"whistle", "whistles"},
	{"wink", "winks"},
	{"laugh", "laughs out loud"},
	{"wonder", "wonders"},
{NULL, NULL}};
char           *emotions2[][2] = {
	{"caress", "caresses"},
	{"comfort", "comforts"},
	{"confuse", "confuses"},
	{"congratulate", "congratulates"},
	{"cuddle", "cuddles"},
	{"fondle", "fondles"},
	{"greet", "greets"},
	{"hug", "hugs"},
	{"ignore", "ignores"},
	{"kick", "kicks"},
	{"kiss", "kisses"},
	{"knee", "knees"},
	{"lick", "licks"},
	{"like", "likes"},
	{"love", "loves"},
	{"nudge", "nudges"},
	{"pat", "pats"},
	{"pinch", "pinches"},
	{"poke", "pokes"},
	{"slap", "slaps"},
	{"smooch", "smooches"},
	{"sniff", "sniffes"},
	{"squeeze", "squeezes"},
	{"tackle", "tackles"},
	{"thank", "thanks"},
	{"tickle", "tickles"},
	{"worship", "worships"},
{NULL, NULL}};
char           *adverb[] = {
"absentmindedly",
"aimlessly",
"amazedly",
"amusedly",
"angrily",
"anxiously",
"appreciatively",
"appropriately",
"archly",
"astonishingly",
"attentively",
"badly",
"barely",
"belatedly",
"bitterly",
"boringly",
"breathlessly",
"briefly",
"brightly",
"brotherly",
"busily",
"carefully",
"cautiously",
"charmingly",
"cheerfully",
"childishly",
"clumsily",
"coaxingly",
"coldly",
"completely",
"confidently",
"confusedly",
"contentedly",
"coquetishly",
"courageously",
"coyly",
"crazily",
"cunningly",
"curiously",
"cutely",
"cynically",
"dangerously",
"deeply",
"defiantly",
"dejectedly",
"delightedly",
"delightfully",
"deliriously",
"demonically",
"depressively",
"derisively",
"desperately",
"devilishly",
"dirtily",
"disappointedly",
"discretely",
"disgustedly",
"doubtfully",
"dreamily",
"dubiously",
"earnestly",
"egocentrically",
"egoistically",
"encouragingly",
"endearingly",
"enthusiastically",
"enviously",
"erotically",
"evilly",
"exhaustedly",
"exuberantly",
"faintly",
"fanatically",
"fatherly",
"fiercefully",
"firmly",
"foolishly",
"formally",
"frantically",
"friendly",
"frostily",
"funnily",
"furiously",
"generously",
"gleefully",
"gracefully",
"graciously",
"gratefully",
"greedily",
"grimly",
"happily",
"harmonically",
"headlessly",
"heartbrokenly",
"heavily",
"helpfully",
"helplessly",
"honestly",
"hopefully",
"humbly",
"hungrily",
"hysterically",
"ignorantly",
"impatiently",
"inanely",
"indecently",
"indifferently",
"innocently",
"inquiringly",
"inquisitively",
"insanely",
"instantly",
"intensely",
"interestedly",
"ironically",
"jauntily",
"jealously",
"joyfully",
"joyously",
"kindly",
"knowingly",
"lazily",
"loudly",
"lovingly",
"lustfully",
"madly",
"maniacally",
"melancholically",
"menacingly",
"mercilessly",
"merrily",
"mischieviously",
"motherly",
"musically",
"mysteriously",
"nastily",
"naughtily",
"nervously",
"nicely",
"noisily",
"nonchalantly",
"outrageously",
"overwhelmingly",
"painfully",
"passionately",
"patiently",
"patronizingly",
"perfectly",
"personally",
"physically",
"pitifully",
"playfully",
"politely",
"professionally",
"profoundly",
"profusely",
"proudly",
"questioningly",
"quickly",
"quietly",
"quizzically",
"randomly",
"rapidly",
"really",
"rebelliously",
"relieved",
"reluctantly",
"remorsefully",
"repeatedly",
"resignedly",
"respectfully",
"romantically",
"rudely",
"sadistically",
"sadly",
"sarcastically",
"sardonically",
"satanically",
"scornfully",
"searchingly",
"secretively",
"seductively",
"sensually",
"seriously",
"sexily",
"shamelessly",
"sheepishly",
"shyly",
"sickly",
"significantly",
"silently",
"sisterly",
"skilfully",
"sleepily",
"slightly",
"slowly",
"slyly",
"smilingly",
"smugly",
"socially",
"softly",
"solemnly",
"strangely",
"stupidly",
"sweetly",
"tearfully",
"tenderly",
"terribly",
"thankfully",
"theoretically",
"thoughtfully",
"tightly",
"tiredly",
"totally",
"tragically",
"truly",
"trustfully",
"uncontrollably",
"understandingly",
"unexpectedly",
"unhappily",
"unintentionally",
"unknowingly",
"vaguely",
"viciously",
"vigorously",
"violently",
"virtually",
"warmly",
"wearily",
"wholeheartedly",
"wickedly",
"wildly",
"wisely",
"wistfully",
NULL};

//! reads a file and dimps it straight onto the output
int 
ReadFile(const char *filenaam)
{
	FILE           *fp;
	char            string[81];
	fp = fopen(filenaam, "r");
	if (fp==NULL)
	{
		int i = errno;
		send_printf(getMMudOut(), "%i: %s (%s)\n", i, strerror(i), filenaam);
		return 1;
	}
	
	while (fgets(string, 80, fp) != 0) {
		send_printf(getMMudOut(), "%s", string);
	}
	fclose(fp);
	return 1;
}

//! print the standard command form for filling in commands
int
PrintForm(char * name, char * password)
{
if (!getFrames())
{
	send_printf(getMMudOut(), "<SCRIPT language=\"JavaScript\">\r\n"
			"<!-- In hiding!\r\n"
			"function setfocus() {\r\n"
			"       document.CommandForm.command.focus();\r\n"
			"	return;\r\n"
			"	}\r\n"
			"//-->\r\n"
			"</SCRIPT>\r\n");
	send_printf(getMMudOut(), "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", getParam(MM_MUDCGI));
	send_printf(getMMudOut(), "<INPUT TYPE=\"text\" NAME=\"command\" VALUE=\"\" SIZE=\"50\"><P>\n");
	send_printf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	send_printf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	send_printf(getMMudOut(), "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"1\">\n");
	send_printf(getMMudOut(), "<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n");
	send_printf(getMMudOut(), "</FORM><P>\n");
}
}

//! show inventory, items person is carrying
int
Inventory_Command(char * name, char * password, int room, char *fcommand)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *sqlstring;

	send_printf(getMMudOut(), "<HTML>\n");
	send_printf(getMMudOut(), "<HEAD>\n");
	send_printf(getMMudOut(), "<TITLE>\n");
	send_printf(getMMudOut(), "Land of Karchan - Inventory\n");
	send_printf(getMMudOut(), "</TITLE>\n");
	send_printf(getMMudOut(), "</HEAD>\n");
	send_printf(getMMudOut(), "<BODY>\n");
	if (!getFrames())
	{
		send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
	send_printf(getMMudOut(), "<H1><IMG SRC=\"http://%s/images/gif/money.gif\">Inventory</H1>You have", getParam(MM_SERVERNAME));
	send_printf(getMMudOut(), "<UL>");
	sqlstring = composeSqlStatement("select tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems"
		" where (items.id = tmpitems.id) and "
		" (items.visible <> 0) and "
		" (tmpitems.search = '') and "
		" (tmpitems.belongsto = '%x') and "
		" (tmpitems.amount >= 1) and "
		" (tmpitems.room = 0) and "
		" (tmpitems.wearing = '') and "
		" (tmpitems.wielding = '') and "
		" (tmpitems.containerid = 0)", name);
	res=SendSQL2(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res!=NULL)
	{
		while ((row = mysql_fetch_row(res))!=NULL)
		{
			if (atoi(row[0])!=1) 
			{
				send_printf(getMMudOut(), "<LI>%s %s, %s %ss.<BR>\r\n",
					row[0], row[2], row[3], row[1]);
			}
			else
			{
				send_printf(getMMudOut(), "<LI>a %s, %s %s.<BR>\r\n",
					row[2], row[3], row[1]);
			}
		}
	}
	mysql_free_result(res);

	sqlstring = composeSqlStatement("select tmpitems.containerid, items.id, items.name, items.adject1, items.adject2, "
		"containeditems.amount, items2.name, items2.adject1, items2.adject2 "
		"from items, tmp_itemtable tmpitems, containeditems, items items2 "
		" where (items.id = tmpitems.id) and "
		" (items.visible <> 0) and "
		" (tmpitems.search = '') and "
		" (tmpitems.belongsto = '%x') and "
		" (tmpitems.amount = 1) and "
		" (tmpitems.room = 0) and "
		" (tmpitems.wearing = '') and "
		" (tmpitems.wielding = '') and "
		" (tmpitems.containerid <> 0) and "
		" (tmpitems.containerid = containeditems.containedin) and "
		" (containeditems.id = items2.id) "
		" order by tmpitems.containerid, items.name, items.adject1, items.adject2", name);
	res=SendSQL2(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res!=NULL)
	{
		int containerid=0;
		while ((row = mysql_fetch_row(res))!=NULL)
		{
			if (containerid != atoi(row[0]))
			{
				send_printf(getMMudOut(), "\r\n<LI>a %s %s, %s containing ",
					row[3], row[4], row[2]);
				containerid = atoi(row[0]);
			}
			else
			{
				send_printf(getMMudOut(), ", ");
			}
			if (atoi(row[5])!=1) 
			{
				send_printf(getMMudOut(), "%s %s, %s %ss",
					row[5], row[7], row[8], row[6]);
			}
			else
			{
				send_printf(getMMudOut(), "a %s, %s %s",
					row[7], row[8], row[6]);
			}
		}
	}
	mysql_free_result(res);

	sqlstring = composeSqlStatement("select gold, silver, copper from tmp_usertable"
		" where name = '%x'", name);
	res=SendSQL2(sqlstring, NULL);
	free(sqlstring);sqlstring=NULL;
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
		if (row!=NULL) 
		{
			send_printf(getMMudOut(), "<LI>%s gold coins</A>\r\n", row[0]);
			send_printf(getMMudOut(), "<LI>%s silver coins</A>\r\n", row[1]);
			send_printf(getMMudOut(), "<LI>%s copper coins</A>\r\n", row[2]);
		}
	}
	mysql_free_result(res);

	send_printf(getMMudOut(), "</UL><BR>");
	PrintForm(name, password);
	
	send_printf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(getMMudOut(), "<DIV ALIGN=left><P>");
	send_printf(getMMudOut(), "</BODY></HTML>");
}

//! small help function for computing what you pay and what you get returned
/*! before any computation is done, it is computed wether or not enough money is available to pay the amount requested. This is done using the following
calculation: z0*100+z1*10+z2<=a*100+b*10+c
\param z0 int, gold coins to be paid
\param z1 int, silver coins to be paid
\param z2 int, copper coins to be paid
\param a pointer to int, because needs to change, gold coins available
\param b pointer to int, because needs to change, silver coins available
\param c pointer to int, because needs to change, copper coins available
\returns integer, 1 upon success, 1 upon failure
*/
int 
PayUp(int z0, int z1, int z2, int *a, int *b, int *c)
/*
 * Pre : true Post: return z0*100+z1*10+z2>*a*100+*b*10+*c
 */
{
	if (z0 * 100 + z1 * 10 + z2 > *a * 100 + *b * 10 + *c) 
	{
		return 0;
	}
	*c = *c - z2;
	*b = *b - z1;
	*a = *a - z0;
	while ((*c < 0) || (*b < 0) || (*a < 0)) 
	{	/* doorgaan als een van beide negatief is */
		if (*c < 0) 
		{
			*b = *b - 1;
			*c = *c + 10;
		}
		if (*b < 0) 
		{
			*a = *a - 1;
			*b = *b + 10;
		}
		if (*a < 0)
		{
			if (*b >= 10)
			{
				*b = *b - 10;
				*a = *a + 1;
			}
			else
			{
				if (*c >=10)
				{
					*c = *c - 10;
					*b = *b + 1;
				}
			}
		}
	}			/* endwhile */
	return 1;
}

//! returns the plural of an emotion
/*!
\see get_pluralis2
*/
char           *
get_pluralis(char *s)
{
	int             i;

	for (i = 0; emotions[i][SINGULARIS] != NULL &&
	     strcasecmp(emotions[i][SINGULARIS], s) != 0; i++);

	return emotions[i][PLURALIS];
}

//! returns the plural of an emotion
/* this method has the ability to be combined with persons, hence the distinction from get_pluralis
\see get_pluralis
*/
char           *
get_pluralis2(char *s)
{
	int             i;

	for (i = 0; emotions2[i][SINGULARIS] != NULL &&
	     strcasecmp(emotions2[i][SINGULARIS], s) != 0; i++);

	return emotions2[i][PLURALIS];
}


//! check adverb is available
/*! for instance: absentmindedly
*/
int
exist_adverb(char *s)
{
	int             i;

	for (i = 0; adverb[i] != NULL &&
	     strcasecmp(adverb[i], s) != 0; i++);

	return adverb[i]!=NULL;
}

//! writes the text of a room to output
/*!
\param z int, roomnumber
*/
void 
RoomTextProc(int z)
{
	FILE           *fp;
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *temp;

	send_printf(getMMudOut(), "<HTML>\n");
	send_printf(getMMudOut(), "<HEAD>\n");
	send_printf(getMMudOut(), "<TITLE>\n");
	send_printf(getMMudOut(), "Land of Karchan\n");
	send_printf(getMMudOut(), "</TITLE>\n");
	send_printf(getMMudOut(), "</HEAD>\n");

	if (!getFrames())
	{
		send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			send_printf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}

	temp = composeSqlStatement("select contents from rooms where id=%i", z);
	res=SendSQL2(temp, NULL);
	free(temp);temp=NULL;
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);

		if (row!=NULL)
		{
			send_printf(getMMudOut(), "%s", row[0]);
		}
		else
		{
			send_printf(getMMudOut(), "<H1>Cardboard</H1>"
			"Everywhere around you you notice cardboard. It seems as if this part"
			" has either not been finished yet or you encountered an error"
			" in retrieving the room description from the server.<P>");
		}
		mysql_free_result(res);
	}
	else
	{
		mysql_free_result(res);
		send_printf(getMMudOut(), "<H1>Cardboard</H1>"
		"Everywhere around you you notice cardboard. It seems as if this part"
		" has either not been finished yet or you encountered an error"
		" in retrieving the room description from the server.<P>");
	}
}

//! write entire room to output, ending function
/*! this function is used quite often. It is used as the last thing to execute. Once a command has been executed
it is usually this method that is called last, in order to print the appropriate text
*/
void 
WriteRoom(char * name, char * password, int room, int sleepstatus)
{
	roomstruct	*temproom;
	int i=0;
	char logname[100];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *tempsql;
	RoomTextProc(room);

	send_printf(getMMudOut(), "[");
	temproom=GetRoomInfo(room);
	if (temproom->west) {
		send_printf(getMMudOut(), "<A HREF=\"%s?command=w&name=%s&password=%s&frames=%i\">west</A>",
		                      getParam(MM_MUDCGI), name, password, getFrames()+1);
		i++;
	}
	if (temproom->east) {
		if (i) {
			send_printf(getMMudOut(), ", ");
		}
		send_printf(getMMudOut(), "<A HREF=\"%s?command=e&name=%s&password=%s&frames=%i\">east</A>",
		                      getParam(MM_MUDCGI), name, password, getFrames()+1);
		i++;
	}
	if (temproom->north) {
		if (i) {
			send_printf(getMMudOut(), ", ");
		}
		send_printf(getMMudOut(), "<A HREF=\"%s?command=n&name=%s&password=%s&frames=%i\">north</A>",
		                      getParam(MM_MUDCGI), name, password, getFrames()+1);
		i++;
	}
	if (temproom->south) {
		if (i) {
			send_printf(getMMudOut(), ", ");
		}
		send_printf(getMMudOut(), "<A HREF=\"%s?command=s&name=%s&password=%s&frames=%i\">south</A>",
		                      getParam(MM_MUDCGI), name, password, getFrames()+1);
		i++;
	}
	if (temproom->up) {
		if (i) {
			send_printf(getMMudOut(), ", ");
		}
		send_printf(getMMudOut(), "<A HREF=\"%s?command=up&name=%s&password=%s&frames=%i\">up</A>",
		                      getParam(MM_MUDCGI), name, password, getFrames()+1);
		i++;
	}
	if (temproom->down) {
		if (i) {
			send_printf(getMMudOut(), ", ");
		}
		send_printf(getMMudOut(), "<A HREF=\"%s?command=down&name=%s&password=%s&frames=%i\">down</A>",
		                      getParam(MM_MUDCGI), name, password, getFrames()+1);
	}
	free(temproom);
	i = 0;
	
	send_printf(getMMudOut(), "]<P>\r\n");

if (!getFrames()) 
{
        send_printf(getMMudOut(),"<TABLE ALIGN=right>\n");
	send_printf(getMMudOut(),"<TR><TD><IMG ALIGN=right SRC=\"http://%s/images/gif/roos.gif\" "
	"USEMAP=\"#roosmap\" BORDER=\"0\" ISMAP ALT=\"N-S-E-W\"><P>", getParam(MM_SERVERNAME));
	if (sleepstatus==1) {
	
	send_printf(getMMudOut(), "<script language=\"JavaScript\">\r\n");

	send_printf(getMMudOut(), "<!-- In hiding!\r\n");
	send_printf(getMMudOut(), " browserName = navigator.appName;          \r\n");
	send_printf(getMMudOut(), "           browserVer = parseInt(navigator.appVersion);\r\n");
	send_printf(getMMudOut(), "               if (browserName == \"Netscape\" && browserVer >= 3) version =\r\n");
	send_printf(getMMudOut(), "\"n3\";\r\n");
	send_printf(getMMudOut(), "               else version = \"n2\";\r\n");
	               
	send_printf(getMMudOut(), "               if (version == \"n3\") {                \r\n");
	send_printf(getMMudOut(), "               toc1on = new Image;          \r\n");
	send_printf(getMMudOut(), "               toc1on.src = \"../images/gif/webpic/new/buttonl.gif\";\r\n");
	               
	               
	send_printf(getMMudOut(), "               toc1off = new Image;\r\n");
	send_printf(getMMudOut(), "               toc1off.src = \"../images/gif/webpic/buttonl.gif\";\r\n");
	               
	send_printf(getMMudOut(), "        }\r\n");
	
	send_printf(getMMudOut(), "function img_act(imgName) {\r\n");
	send_printf(getMMudOut(), "        if (version == \"n3\") {\r\n");
	send_printf(getMMudOut(), "        imgOn = eval(imgName + \"on.src\");\r\n");
	send_printf(getMMudOut(), "        document [imgName].src = imgOn;\r\n");
	send_printf(getMMudOut(), "        }\r\n");
	send_printf(getMMudOut(), "}\r\n");
	
	send_printf(getMMudOut(), "function img_inact(imgName) {\r\n");
	send_printf(getMMudOut(), "        if (version == \"n3\") {\r\n");
	send_printf(getMMudOut(), "        imgOff = eval(imgName + \"off.src\");\r\n");
	send_printf(getMMudOut(), "        document [imgName].src = imgOff;\r\n");
	send_printf(getMMudOut(), "        }\r\n");
	send_printf(getMMudOut(), "}\r\n");
	
	send_printf(getMMudOut(), "//-->\r\n");
	   
	
	send_printf(getMMudOut(), "</SCRIPT>\r\n");
	
	send_printf(getMMudOut(), "<TR><TD><A HREF=\"%s?command=awaken&name=%s&password=%s\" "
		" onMouseOver=\"img_act('toc1')\" onMouseOut=\"img_inact('toc1')\">\n",
	                getParam(MM_MUDCGI), name, password);
	send_printf(getMMudOut(), "<IMG ALIGN=left SRC=\"http://%s/images/gif/webpic/buttonl.gif\" BORDER=0 ALT=\"AWAKEN\" NAME=\"toc1\"></A><P>\n", getParam(MM_SERVERNAME));
	} else {
	send_printf(getMMudOut(), "<script language=\"JavaScript\">\r\n");
	
	send_printf(getMMudOut(), "<!-- In hiding!\r\n");
	send_printf(getMMudOut(), " browserName = navigator.appName;          \r\n");
	send_printf(getMMudOut(), "           browserVer = parseInt(navigator.appVersion);\r\n");
	send_printf(getMMudOut(), "               if (browserName == \"Netscape\" && browserVer >= 3) version =\r\n");
	send_printf(getMMudOut(), "\"n3\";\r\n");
	send_printf(getMMudOut(), "               else version = \"n2\";\r\n");
	               
	send_printf(getMMudOut(), "               if (version == \"n3\") {                \r\n");
	send_printf(getMMudOut(), "               toc1on = new Image;          \r\n");
	send_printf(getMMudOut(), "               toc1on.src = \"../images/gif/webpic/new/buttonk.gif\";\r\n");
	send_printf(getMMudOut(), "               toc2on = new Image;          \r\n");
	send_printf(getMMudOut(), "               toc2on.src = \"../images/gif/webpic/new/buttonj.gif\";\r\n");
	send_printf(getMMudOut(), "               toc3on = new Image;          \r\n");
	send_printf(getMMudOut(), "               toc3on.src = \"../images/gif/webpic/new/buttonr.gif\";\r\n");
	               
	               
	send_printf(getMMudOut(), "               toc1off = new Image;\r\n");
	send_printf(getMMudOut(), "               toc1off.src = \"../images/gif/webpic/buttonk.gif\";\r\n");
	send_printf(getMMudOut(), "               toc2off = new Image;\r\n");
	send_printf(getMMudOut(), "               toc2off.src = \"../images/gif/webpic/buttonj.gif\";\r\n");
	send_printf(getMMudOut(), "               toc3off = new Image;\r\n");
	send_printf(getMMudOut(), "               toc3off.src = \"../images/gif/webpic/buttonr.gif\";\r\n");
	               
	send_printf(getMMudOut(), "        }\r\n");
	
	send_printf(getMMudOut(), "function img_act(imgName) {\r\n");
	send_printf(getMMudOut(), "        if (version == \"n3\") {\r\n");
	send_printf(getMMudOut(), "        imgOn = eval(imgName + \"on.src\");\r\n");
	send_printf(getMMudOut(), "        document [imgName].src = imgOn;\r\n");
	send_printf(getMMudOut(), "        }\r\n");
	send_printf(getMMudOut(), "}\r\n");
	
	send_printf(getMMudOut(), "function img_inact(imgName) {\r\n");
	send_printf(getMMudOut(), "        if (version == \"n3\") {\r\n");
	send_printf(getMMudOut(), "        imgOff = eval(imgName + \"off.src\");\r\n");
	send_printf(getMMudOut(), "        document [imgName].src = imgOff;\r\n");
	send_printf(getMMudOut(), "        }\r\n");
	send_printf(getMMudOut(), "}\r\n");
	
	send_printf(getMMudOut(), "//-->\r\n");
	   
	
	send_printf(getMMudOut(), "</SCRIPT>\r\n");
	
	send_printf(getMMudOut(), "<TR><TD><A HREF=\"%s?command=quit&name=%s&password=%s\" "
		" onMouseOver=\"img_act('toc2')\" onMouseOut=\"img_inact('toc2')\">\n",
	                getParam(MM_MUDCGI), name, password);
	send_printf(getMMudOut(), "<IMG ALIGN=left SRC=\"http://%s/images/gif/webpic/buttonj.gif\" BORDER=0 ALT=\"QUIT\" NAME=\"toc2\"></A><P>\n", getParam(MM_SERVERNAME));
	
	send_printf(getMMudOut(), "<TR><TD><A HREF=\"%s?command=sleep&name=%s&password=%s\" "
		" onMouseOver=\"img_act('toc1')\" onMouseOut=\"img_inact('toc1')\">\n",
	                getParam(MM_MUDCGI), name, password);
	send_printf(getMMudOut(), "<IMG ALIGN=left SRC=\"http://%s/images/gif/webpic/buttonk.gif\" BORDER=0 ALT=\"SLEEP\" NAME=\"toc1\"></A><P>\n", getParam(MM_SERVERNAME));

	send_printf(getMMudOut(), "<TR><TD><A HREF=\"%s?command=clear&name=%s&password=%s\" "
		" onMouseOver=\"img_act('toc3')\" onMouseOut=\"img_inact('toc3')\">\n",
	                getParam(MM_MUDCGI), name, password);
	send_printf(getMMudOut(), "<IMG ALIGN=left SRC=\"http://%s/images/gif/webpic/buttonr.gif\" BORDER=0 ALT=\"CLEAR\" NAME=\"toc3\"></A><P>\n", getParam(MM_SERVERNAME));
	}
	        send_printf(getMMudOut(),"</TABLE>\n");

	                    send_printf(getMMudOut(), "<MAP NAME=\"roosmap\">\n");
	                    send_printf(getMMudOut(), "<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,63,0,0,0\" "
	                    "HREF=\"%s?command=n&name=%s&password=%s\">\n",
	                    getParam(MM_MUDCGI), name, password);
	                    send_printf(getMMudOut(), "<AREA SHAPE=\"POLY\" COORDS=\"0,63,33,31,63,63,0,63\" "
	                    "HREF=\"%s?command=s&name=%s&password=%s\">\n",
	                    getParam(MM_MUDCGI), name, password);
	                    send_printf(getMMudOut(), "<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,0,63,0,0\" "
	                    "HREF=\"%s?command=w&name=%s&password=%s\">\n",
	                    getParam(MM_MUDCGI), name, password);
	                    send_printf(getMMudOut(), "<AREA SHAPE=\"POLY\" COORDS=\"63,0,33,31,63,63,63,0\" "
	                    "HREF=\"%s?command=e&name=%s&password=%s\">\n",
	                    getParam(MM_MUDCGI), name, password);
	                    send_printf(getMMudOut(), "</MAP>\n");
} /*end if getFrames dude*/
	/* Print characters in room */
	tempsql = composeSqlStatement("select "
	"if(god=3, concat('A ',age,"
	"if(length = 'none', '', concat(', ',length)),"
	"if(width = 'none', '', concat(', ',width)),"
	"if(complexion = 'none', '', concat(', ',complexion)),"
	"if(eyes = 'none', '', concat(', ',eyes)),"
	"if(face = 'none', '', concat(', ',face)),"
	"if(hair = 'none', '', concat(', ',hair)),"
	"if(beard = 'none', '', concat(', ',beard)),"
	"if(arm = 'none', '', concat(', ',arm)),"
	"if(leg = 'none', '', concat(', ',leg)),"
	"' ', sex, ' ', race),name)"
	", sleep, god, punishment, name from tmp_usertable where (room=%i) and (name<>'%x')",room,name);
	res=SendSQL2(tempsql, NULL);
	free(tempsql);tempsql=NULL;
	if (res!=NULL)
	{
		char colorme[10];
		while ((row = mysql_fetch_row(res))!=NULL)
		{
			strcpy(colorme, "black");
			if (row[2][0]=='2') {strcpy(colorme, "blue");}
			if (row[2][0]=='3') {strcpy(colorme, "green");}
	if (atoi(row[3])!=0)
	{
		send_printf(getMMudOut(), "<FONT COLOR=%s>A frog called <A HREF=\"%s?command=look+at+%s&name=%s&password=%s&frames=%i\">%s</A> is here.<BR>\r\n", 
			colorme, getParam(MM_MUDCGI), row[4], name, password, getFrames()+1, row[0]);
	}
	else
	{
			if (atoi(row[1])==0) 
			{
				send_printf(getMMudOut(), "<A HREF=\"%s?command=look+at+%s&name=%s&password=%s&frames=%i\"><FONT COLOR=%s>%s</FONT></A> is here.<BR>\r\n", 
					getParam(MM_MUDCGI), row[4], name, password, getFrames()+1, colorme, row[0]);
			}
			else
			{
				send_printf(getMMudOut(), "<A HREF=\"%s?command=look+at+%s&name=%s&password=%s&frames=%i\"><FONT COLOR=%s>%s</FONT></A> is here, asleep.<BR>\r\n", 
					getParam(MM_MUDCGI), row[4], name, password, getFrames()+1, colorme, row[0]);
			}
		}
	}
	} 
	mysql_free_result(res);
	send_printf(getMMudOut(), "<BR>\r\n");

	/* Print items in room */
	tempsql = composeSqlStatement("select tmpitems.amount, items.adject1, items.adject2, items.name from items, tmp_itemtable tmpitems "
			"where (items.id = tmpitems.id) and "
			"      (tmpitems.search = '') and "
			"      (tmpitems.belongsto = '') and "
			"      (tmpitems.amount >=1) and "
			"      (tmpitems.room = %i) and "
			"      (items.visible <> 0) and "
			"      (tmpitems.containerid=0)"
			"      ",room);
	res=SendSQL2(tempsql, NULL);
	free(tempsql);tempsql=NULL;
	if (res!=NULL)
	{
		while ((row = mysql_fetch_row(res))!=NULL)
		{
			if (atoi(row[0])>1) 
			{
				send_printf(getMMudOut(), "%s %s, %s %ss are here.<BR>\r\n", 
					row[0], row[1], row[2], row[3]);
			}
			else
			{
				send_printf(getMMudOut(), "A %s, %s %s is here.<BR>\r\n", 
					row[1], row[2], row[3]);
			}
		}
	} 
	mysql_free_result(res);
	/* print special items (containers and such) */
	tempsql = composeSqlStatement("select tmpitems.containerid, items.id, items.name, items.adject1, items.adject2, "
		"containeditems.amount, items2.name, items2.adject1, items2.adject2 "
		"from items, tmp_itemtable tmpitems, containeditems, items items2 "
		" where (items.id = tmpitems.id) and "
		" (items.visible <> 0) and "
		" (tmpitems.search = '') and "
		" (tmpitems.belongsto = '') and "
		" (tmpitems.amount = 1) and "
		" (tmpitems.room = %i) and "
		" (tmpitems.wearing = '') and "
		" (tmpitems.wielding = '') and " 
		" (tmpitems.containerid <> 0) and "
		" (tmpitems.containerid = containeditems.containedin) and "
		" (containeditems.id = items2.id) "
		" order by tmpitems.containerid, items.name, items.adject1, items.adject2", room);
	res=SendSQL2(tempsql, NULL);
	free(tempsql);tempsql=NULL;
	if (res!=NULL)
	{
		int containerid = 0;
		while ((row = mysql_fetch_row(res))!=NULL)
		{
			if (containerid != atoi(row[0]))
			{
				if (containerid != 0)
				{
					send_printf(getMMudOut(), " is here.<BR>");
				}
				send_printf(getMMudOut(), "\r\nA %s %s, %s containing ",
					row[3], row[4], row[2]);
				containerid = atoi(row[0]);
			}
			else
			{	
				send_printf(getMMudOut(), ", ");
			}
			if (atoi(row[5])!=1)
			{
				send_printf(getMMudOut(), "%s %s, %s %ss",
					row[5], row[7], row[8], row[6]);
			}
			else
			{ 
				send_printf(getMMudOut(), "a %s, %s %s",
					row[7], row[8], row[6]); 
			}
		}
		if (containerid != 0)
		{
			send_printf(getMMudOut(), " is here.");
		}
	} 
	mysql_free_result(res);
	send_printf(getMMudOut(), "<BR>\r\n");

	PrintForm(name, password);
	sprintf(logname, "%s%s.log",getParam(MM_USERHEADER),name);
	if (getFrames()!=2) {ReadFile(logname);}

	send_printf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", getParam(MM_COPYRIGHTHEADER));
	send_printf(getMMudOut(), "<DIV ALIGN=left><P>");
}

//! checks the weight of a person by combining the weights of everything he/she carries
/*! 
\returns integer, which is weight = copper*1 + silver*2 + gold*3 + 
	(item1.weight*item1.amount + item2.weight*item2.amount + ...) 
*/
int
CheckWeight(char * name)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *tempsql;
	int totalgold, totalitems;
	
	/* Check total weight of items */
	tempsql = composeSqlStatement("select tmp_usertable.gold*3 + tmp_usertable.silver*2 "
	"+ tmp_usertable.copper from tmp_usertable where "
	"tmp_usertable.name='%x'", name);
	res=SendSQL2(tempsql, NULL);
	free(tempsql);tempsql=NULL;
	row = mysql_fetch_row(res);
	totalgold = atoi(row[0]); 
	mysql_free_result(res);

	/* Check total weight of items */
	tempsql = composeSqlStatement("select sum(items.weight*tmp_itemtable.amount) from "
	"tmp_itemtable, items where tmp_itemtable.belongsto='%x' and "
	"tmp_itemtable.id=items.id and tmp_itemtable.containerid=0", name);
	res=SendSQL2(tempsql, NULL);
	free(tempsql);tempsql=NULL;
	totalitems=0;
	if (res!=NULL)
	{
		if ((row = mysql_fetch_row(res))!=NULL)
		{
			if (row[0] == NULL)
			{
				totalitems = 0;
			} else
			{
				totalitems = atoi(row[0]);
			}
		}
	} 
	mysql_free_result(res);
	return totalitems + totalgold;
}
