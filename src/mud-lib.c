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
#include "mud-lib.h"

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

int 
ReadFile(char *filenaam)
{
	FILE           *fp;
	char            string[81];
	fp = fopen(filenaam, "r");
	if (fp==NULL)
	{
		int i = errno;
		fprintf(cgiOut, "%i: %s (%s)\n", i, strerror(i), filenaam);
		return 1;
	}
	
	while (fgets(string, 80, fp) != 0) {
		fprintf(cgiOut, "%s", string);
	}
	fclose(fp);
	return 1;
}

int
PrintForm(char * name, char * password)
{
if (!getFrames())
{
	fprintf(cgiOut, "<SCRIPT language=\"JavaScript\">\r\n"
			"<!-- In hiding!\r\n"
			"function setfocus() {\r\n"
			"       document.CommandForm.command.focus();\r\n"
			"	return;\r\n"
			"	}\r\n"
			"//-->\r\n"
			"</SCRIPT>\r\n");
	fprintf(cgiOut, "<FORM METHOD=\"POST\" ACTION=\"%s\" NAME=\"CommandForm\">\n", MudExe);
	fprintf(cgiOut, "<INPUT TYPE=\"text\" NAME=\"command\" VALUE=\"\" SIZE=\"50\"><P>\n");
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"%s\">\n", name);
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"%s\">\n", password);
	fprintf(cgiOut, "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"1\">\n");
	fprintf(cgiOut, "<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n");
	fprintf(cgiOut, "</FORM><P>\n");
}
}

void 
WriteInventoryList(char * name, char * password)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char sqlstring[1024];

	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan - Inventory\n");
	fprintf(cgiOut, "</TITLE>\n");
	fprintf(cgiOut, "</HEAD>\n");
	fprintf(cgiOut, "<BODY>\n");
	if (!getFrames())
	{
		fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}
	fprintf(cgiOut, "<H1><IMG SRC=\"http://"ServerName"/images/gif/money.gif\">Inventory</H1>You have");
	fprintf(cgiOut, "<UL>");
	sprintf(sqlstring, "select tmpitems.amount, items.name, items.adject1, items.adject2 from items, tmp_itemtable tmpitems"
		" where (items.id = tmpitems.id) and "
		" (items.visible <> 0) and "
		" (tmpitems.search = '') and "
		" (tmpitems.belongsto = '%s') and "
		" (tmpitems.amount >= 1) and "
		" (tmpitems.room = 0) and "
		" (tmpitems.wearing = '') and "
		" (tmpitems.wielding = '') and "
		" (tmpitems.containerid = 0)", name);
	res=SendSQL2(sqlstring, NULL);
	if (res!=NULL)
	{
		while ((row = mysql_fetch_row(res))!=NULL)
		{
			if (atoi(row[0])!=1) 
			{
				fprintf(cgiOut, "<LI>%s %s, %s %ss.<BR>\r\n",
					row[0], row[2], row[3], row[1]);
			}
			else
			{
				fprintf(cgiOut, "<LI>a %s, %s %s.<BR>\r\n",
					row[2], row[3], row[1]);
			}
		}
	}
	mysql_free_result(res);

	sprintf(sqlstring, 
		"select items.id, items.name, items.adject1, items.adject2, "
		"containeditems.amount, items2.name, items2.adject1, items2.adject2 "
		"from items, tmp_itemtable tmpitems, containeditems, items items2 "
		" where (items.id = tmpitems.id) and "
		" (items.visible <> 0) and "
		" (tmpitems.search = '') and "
		" (tmpitems.belongsto = '%s') and "
		" (tmpitems.amount = 1) and "
		" (tmpitems.room = 0) and "
		" (tmpitems.wearing = '') and "
		" (tmpitems.wielding = '') and "
		" (tmpitems.containerid <> 0) and "
		" (tmpitems.containerid = containeditems.containedin) and "
		" (containeditems.id = items2.id) "
		" order by items.name, items.adject1, items.adject2", name);
	res=SendSQL2(sqlstring, NULL);
	if (res!=NULL)
	{
		int itemid=0;
		while ((row = mysql_fetch_row(res))!=NULL)
		{
			if (itemid != atoi(row[0]))
			{
				fprintf(cgiOut, "\r\n<LI>a %s %s, %s containing ",
					row[2], row[3], row[1]);
				itemid = atoi(row[0]);
			}
			else
			{
				fprintf(cgiOut, ", ");
			}
			if (atoi(row[4])!=1) 
			{
				fprintf(cgiOut, "%s %s, %s %ss",
					row[4], row[6], row[7], row[5]);
			}
			else
			{
				fprintf(cgiOut, "a %s, %s %s",
					row[6], row[7], row[5]);
			}
		}
	}
	mysql_free_result(res);

	sprintf(sqlstring, "select gold, silver, copper from tmp_usertable"
		" where name = '%s'", name);
	res=SendSQL2(sqlstring, NULL);
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);
		if (row!=NULL) 
		{
			fprintf(cgiOut, "<LI>%s gold coins</A>\r\n", row[0]);
			fprintf(cgiOut, "<LI>%s silver coins</A>\r\n", row[1]);
			fprintf(cgiOut, "<LI>%s copper coins</A>\r\n", row[2]);
		}
	}
	mysql_free_result(res);

	fprintf(cgiOut, "</UL><BR>");
	PrintForm(name, password);
	
	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
	fprintf(cgiOut, "</BODY></HTML>");
}

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

char           *
get_pluralis(char *s)
{
	int             i;

	for (i = 0; emotions[i][SINGULARIS] != NULL &&
	     strcmp(emotions[i][SINGULARIS], s) != 0; i++);

	return emotions[i][PLURALIS];
}

char           *
get_pluralis2(char *s)
{
	int             i;

	for (i = 0; emotions2[i][SINGULARIS] != NULL &&
	     strcmp(emotions2[i][SINGULARIS], s) != 0; i++);

	return emotions2[i][PLURALIS];
}

int
exist_adverb(char *s)
{
	int             i;

	for (i = 0; adverb[i] != NULL &&
	     strcmp(adverb[i], s) != 0; i++);

	return adverb[i]!=NULL;
}

void 
RoomTextProc(int z)
{
	FILE           *fp;
	indexrec        y;
	char           *pakem;
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];

	fprintf(cgiOut, "<HTML>\n");
	fprintf(cgiOut, "<HEAD>\n");
	fprintf(cgiOut, "<TITLE>\n");
	fprintf(cgiOut, "Land of Karchan\n");
	fprintf(cgiOut, "</TITLE>\n");
	fprintf(cgiOut, "</HEAD>\n");

	if (!getFrames())
	{
		fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
	}
	else
	{
		if (getFrames()==1)
		{
			fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
		} else
		{
			fprintf(cgiOut, "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
		}
	}

	sprintf(temp, "select contents from rooms where id=%i", z);
	res=SendSQL2(temp, NULL);
	if (res!=NULL)
	{
		row = mysql_fetch_row(res);

		if (row!=NULL)
		{
			fprintf(cgiOut, "%s", row[0]);
		}
		else
		{
			fprintf(cgiOut, "<H1>Cardboard</H1>"
			"Everywhere around you you notice cardboard. It seems as if this part"
			" has either not been finished yet or you encountered an error"
			" in retrieving the room description from the server.<P>");
		}
		mysql_free_result(res);
	}
	else
	{
		mysql_free_result(res);
		fprintf(cgiOut, "<H1>Cardboard</H1>"
		"Everywhere around you you notice cardboard. It seems as if this part"
		" has either not been finished yet or you encountered an error"
		" in retrieving the room description from the server.<P>");
	}
}

void 
WriteRoom(char * name, char * password, int room, int sleepstatus)
{
	roomstruct	*temproom;
	int i=0;
	char logname[100];
	MYSQL_RES *res;
	MYSQL_ROW row;
	char tempsql[1024];
	RoomTextProc(room);

	fprintf(cgiOut, "[");
	temproom=GetRoomInfo(room);
	if (temproom->west) {
		fprintf(cgiOut, "<A HREF=\"%s?command=w&name=%s&password=%s&frames=%i\">west</A>",
		                      MudExe, name, password, getFrames()+1);
		i++;
	}
	if (temproom->east) {
		if (i) {
			fprintf(cgiOut, ", ");
		}
		fprintf(cgiOut, "<A HREF=\"%s?command=e&name=%s&password=%s&frames=%i\">east</A>",
		                      MudExe, name, password, getFrames()+1);
		i++;
	}
	if (temproom->north) {
		if (i) {
			fprintf(cgiOut, ", ");
		}
		fprintf(cgiOut, "<A HREF=\"%s?command=n&name=%s&password=%s&frames=%i\">north</A>",
		                      MudExe, name, password, getFrames()+1);
		i++;
	}
	if (temproom->south) {
		if (i) {
			fprintf(cgiOut, ", ");
		}
		fprintf(cgiOut, "<A HREF=\"%s?command=s&name=%s&password=%s&frames=%i\">south</A>",
		                      MudExe, name, password, getFrames()+1);
		i++;
	}
	if (temproom->up) {
		if (i) {
			fprintf(cgiOut, ", ");
		}
		fprintf(cgiOut, "<A HREF=\"%s?command=up&name=%s&password=%s&frames=%i\">up</A>",
		                      MudExe, name, password, getFrames()+1);
		i++;
	}
	if (temproom->down) {
		if (i) {
			fprintf(cgiOut, ", ");
		}
		fprintf(cgiOut, "<A HREF=\"%s?command=down&name=%s&password=%s&frames=%i\">down</A>",
		                      MudExe, name, password, getFrames()+1);
	}
	free(temproom);
	i = 0;
	
	fprintf(cgiOut, "]<P>\r\n");

if (!getFrames()) 
{
        fprintf(cgiOut,"<TABLE ALIGN=right>\n");
	fprintf(cgiOut,"<TR><TD><IMG ALIGN=right SRC=\"http://"ServerName"/images/gif/roos.gif\" "
	"USEMAP=\"#roosmap\" BORDER=\"0\" ISMAP ALT=\"N-S-E-W\"><P>");
	if (sleepstatus==1) {
	
	fprintf(cgiOut, "<script language=\"JavaScript\">\r\n");

	fprintf(cgiOut, "<!-- In hiding!\r\n");
	fprintf(cgiOut, " browserName = navigator.appName;          \r\n");
	fprintf(cgiOut, "           browserVer = parseInt(navigator.appVersion);\r\n");
	fprintf(cgiOut, "               if (browserName == \"Netscape\" && browserVer >= 3) version =\r\n");
	fprintf(cgiOut, "\"n3\";\r\n");
	fprintf(cgiOut, "               else version = \"n2\";\r\n");
	               
	fprintf(cgiOut, "               if (version == \"n3\") {                \r\n");
	fprintf(cgiOut, "               toc1on = new Image;          \r\n");
	fprintf(cgiOut, "               toc1on.src = \"../images/gif/webpic/new/buttonl.gif\";\r\n");
	               
	               
	fprintf(cgiOut, "               toc1off = new Image;\r\n");
	fprintf(cgiOut, "               toc1off.src = \"../images/gif/webpic/buttonl.gif\";\r\n");
	               
	fprintf(cgiOut, "        }\r\n");
	
	fprintf(cgiOut, "function img_act(imgName) {\r\n");
	fprintf(cgiOut, "        if (version == \"n3\") {\r\n");
	fprintf(cgiOut, "        imgOn = eval(imgName + \"on.src\");\r\n");
	fprintf(cgiOut, "        document [imgName].src = imgOn;\r\n");
	fprintf(cgiOut, "        }\r\n");
	fprintf(cgiOut, "}\r\n");
	
	fprintf(cgiOut, "function img_inact(imgName) {\r\n");
	fprintf(cgiOut, "        if (version == \"n3\") {\r\n");
	fprintf(cgiOut, "        imgOff = eval(imgName + \"off.src\");\r\n");
	fprintf(cgiOut, "        document [imgName].src = imgOff;\r\n");
	fprintf(cgiOut, "        }\r\n");
	fprintf(cgiOut, "}\r\n");
	
	fprintf(cgiOut, "//-->\r\n");
	   
	
	fprintf(cgiOut, "</SCRIPT>\r\n");
	
	fprintf(cgiOut, "<TR><TD><A HREF=\"%s?command=awaken&name=%s&password=%s\" "
		" onMouseOver=\"img_act('toc1')\" onMouseOut=\"img_inact('toc1')\">\n",
	                MudExe, name, password);
	fprintf(cgiOut, "<IMG ALIGN=left SRC=\"http://"ServerName"/images/gif/webpic/buttonl.gif\" BORDER=0 ALT=\"AWAKEN\" NAME=\"toc1\"></A><P>\n");
	} else {
	fprintf(cgiOut, "<script language=\"JavaScript\">\r\n");
	
	fprintf(cgiOut, "<!-- In hiding!\r\n");
	fprintf(cgiOut, " browserName = navigator.appName;          \r\n");
	fprintf(cgiOut, "           browserVer = parseInt(navigator.appVersion);\r\n");
	fprintf(cgiOut, "               if (browserName == \"Netscape\" && browserVer >= 3) version =\r\n");
	fprintf(cgiOut, "\"n3\";\r\n");
	fprintf(cgiOut, "               else version = \"n2\";\r\n");
	               
	fprintf(cgiOut, "               if (version == \"n3\") {                \r\n");
	fprintf(cgiOut, "               toc1on = new Image;          \r\n");
	fprintf(cgiOut, "               toc1on.src = \"../images/gif/webpic/new/buttonk.gif\";\r\n");
	fprintf(cgiOut, "               toc2on = new Image;          \r\n");
	fprintf(cgiOut, "               toc2on.src = \"../images/gif/webpic/new/buttonj.gif\";\r\n");
	fprintf(cgiOut, "               toc3on = new Image;          \r\n");
	fprintf(cgiOut, "               toc3on.src = \"../images/gif/webpic/new/buttonr.gif\";\r\n");
	               
	               
	fprintf(cgiOut, "               toc1off = new Image;\r\n");
	fprintf(cgiOut, "               toc1off.src = \"../images/gif/webpic/buttonk.gif\";\r\n");
	fprintf(cgiOut, "               toc2off = new Image;\r\n");
	fprintf(cgiOut, "               toc2off.src = \"../images/gif/webpic/buttonj.gif\";\r\n");
	fprintf(cgiOut, "               toc3off = new Image;\r\n");
	fprintf(cgiOut, "               toc3off.src = \"../images/gif/webpic/buttonr.gif\";\r\n");
	               
	fprintf(cgiOut, "        }\r\n");
	
	fprintf(cgiOut, "function img_act(imgName) {\r\n");
	fprintf(cgiOut, "        if (version == \"n3\") {\r\n");
	fprintf(cgiOut, "        imgOn = eval(imgName + \"on.src\");\r\n");
	fprintf(cgiOut, "        document [imgName].src = imgOn;\r\n");
	fprintf(cgiOut, "        }\r\n");
	fprintf(cgiOut, "}\r\n");
	
	fprintf(cgiOut, "function img_inact(imgName) {\r\n");
	fprintf(cgiOut, "        if (version == \"n3\") {\r\n");
	fprintf(cgiOut, "        imgOff = eval(imgName + \"off.src\");\r\n");
	fprintf(cgiOut, "        document [imgName].src = imgOff;\r\n");
	fprintf(cgiOut, "        }\r\n");
	fprintf(cgiOut, "}\r\n");
	
	fprintf(cgiOut, "//-->\r\n");
	   
	
	fprintf(cgiOut, "</SCRIPT>\r\n");
	
	fprintf(cgiOut, "<TR><TD><A HREF=\"%s?command=quit&name=%s&password=%s\" "
		" onMouseOver=\"img_act('toc2')\" onMouseOut=\"img_inact('toc2')\">\n",
	                MudExe, name, password);
	fprintf(cgiOut, "<IMG ALIGN=left SRC=\"http://"ServerName"/images/gif/webpic/buttonj.gif\" BORDER=0 ALT=\"QUIT\" NAME=\"toc2\"></A><P>\n");
	
	fprintf(cgiOut, "<TR><TD><A HREF=\"%s?command=sleep&name=%s&password=%s\" "
		" onMouseOver=\"img_act('toc1')\" onMouseOut=\"img_inact('toc1')\">\n",
	                MudExe, name, password);
	fprintf(cgiOut, "<IMG ALIGN=left SRC=\"http://"ServerName"/images/gif/webpic/buttonk.gif\" BORDER=0 ALT=\"SLEEP\" NAME=\"toc1\"></A><P>\n");

	fprintf(cgiOut, "<TR><TD><A HREF=\"%s?command=clear&name=%s&password=%s\" "
		" onMouseOver=\"img_act('toc3')\" onMouseOut=\"img_inact('toc3')\">\n",
	                MudExe, name, password);
	fprintf(cgiOut, "<IMG ALIGN=left SRC=\"http://"ServerName"/images/gif/webpic/buttonr.gif\" BORDER=0 ALT=\"CLEAR\" NAME=\"toc3\"></A><P>\n");
	}
	        fprintf(cgiOut,"</TABLE>\n");

	                    fprintf(cgiOut, "<MAP NAME=\"roosmap\">\n");
	                    fprintf(cgiOut, "<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,63,0,0,0\" "
	                    "HREF=\"%s?command=n&name=%s&password=%s\">\n",
	                    MudExe, name, password);
	                    fprintf(cgiOut, "<AREA SHAPE=\"POLY\" COORDS=\"0,63,33,31,63,63,0,63\" "
	                    "HREF=\"%s?command=s&name=%s&password=%s\">\n",
	                    MudExe, name, password);
	                    fprintf(cgiOut, "<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,0,63,0,0\" "
	                    "HREF=\"%s?command=w&name=%s&password=%s\">\n",
	                    MudExe, name, password);
	                    fprintf(cgiOut, "<AREA SHAPE=\"POLY\" COORDS=\"63,0,33,31,63,63,63,0\" "
	                    "HREF=\"%s?command=e&name=%s&password=%s\">\n",
	                    MudExe, name, password);
	                    fprintf(cgiOut, "</MAP>\n");
} /*end if getFrames dude*/
	/* Print characters in room */
	sprintf(tempsql, "select "
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
	", sleep, god, punishment, name from tmp_usertable where (room=%i) and (name<>'%s')",room,name);
	res=SendSQL2(tempsql, NULL);
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
		fprintf(cgiOut, "<FONT COLOR=%s>A frog called <A HREF=\"%s?command=look+at+%s&name=%s&password=%s&frames=%i\">%s</A> is here.<BR>\r\n", 
			colorme, MudExe, row[4], name, password, getFrames()+1, row[0]);
	}
	else
	{
			if (atoi(row[1])==0) 
			{
				fprintf(cgiOut, "<A HREF=\"%s?command=look+at+%s&name=%s&password=%s&frames=%i\"><FONT COLOR=%s>%s</FONT></A> is here.<BR>\r\n", 
					MudExe, row[4], name, password, getFrames()+1, colorme, row[0]);
			}
			else
			{
				fprintf(cgiOut, "<A HREF=\"%s?command=look+at+%s&name=%s&password=%s&frames=%i\"><FONT COLOR=%s>%s</FONT></A> is here, asleep.<BR>\r\n", 
					MudExe, row[4], name, password, getFrames()+1, colorme, row[0]);
			}
		}
	}
	} 
	mysql_free_result(res);
	fprintf(cgiOut, "<BR>\r\n");

	/* Print items in room */
	sprintf(tempsql, "select tmpitems.amount, items.adject1, items.adject2, items.name from items, tmp_itemtable tmpitems "
			"where (items.id = tmpitems.id) and "
			"      (tmpitems.search = '') and "
			"      (tmpitems.belongsto = '') and "
			"      (tmpitems.amount >=1) and "
			"      (tmpitems.room = %i) and "
			"      (items.visible <> 0) and "
			"      (tmpitems.containerid=0)"
			"      ",room);
	res=SendSQL2(tempsql, NULL);
	if (res!=NULL)
	{
		while ((row = mysql_fetch_row(res))!=NULL)
		{
			if (atoi(row[0])>1) 
			{
				fprintf(cgiOut, "%s %s, %s %ss are here.<BR>\r\n", 
					row[0], row[1], row[2], row[3]);
			}
			else
			{
				fprintf(cgiOut, "A %s, %s %s is here.<BR>\r\n", 
					row[1], row[2], row[3]);
			}
		}
	} 
	mysql_free_result(res);
	/* print special items (containers and such) */
		sprintf(tempsql, "select items.id, items.name, items.adject1, items.adject2, "
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
			" order by items.name, items.adject1, items.adject2", room);
	res=SendSQL2(tempsql, NULL);
	if (res!=NULL)
	{
		int itemid = 0;
		while ((row = mysql_fetch_row(res))!=NULL)
		{
			if (itemid != atoi(row[0]))
			{
				if (itemid != 0)
				{
					fprintf(cgiOut, " is here.<BR>");
				}
				fprintf(cgiOut, "\r\nA %s %s, %s containing ",
					row[2], row[3], row[1]);
				itemid = atoi(row[0]);
			}
			else
			{	
				fprintf(cgiOut, ", ");
			}
			if (atoi(row[4])!=1)
			{
				fprintf(cgiOut, "%s %s, %s %ss",
					row[4], row[6], row[7], row[5]);
			}
			else
			{ 
				fprintf(cgiOut, "a %s, %s %s",
					row[6], row[7], row[5]); 
			}
		}
		if (itemid != 0)
		{
			fprintf(cgiOut, " is here.");
		}
	} 
	mysql_free_result(res);
	fprintf(cgiOut, "<BR>\r\n");

	PrintForm(name, password);
	sprintf(logname, "%s%s.log",USERHeader,name);
	if (getFrames()!=2) {ReadFile(logname);}

	fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
	fprintf(cgiOut, "<DIV ALIGN=left><P>");
}

int
CheckWeight(char * name)
{
/* weight = copper*1 + silver*2 + gold*3 + 
	(item1.weight*item1.amount + item2.weight*item2.amount + ...) 
*/
	MYSQL_RES *res;
	MYSQL_ROW row;
	char tempsql[1024];
	int totalgold, totalitems;
	
	/* Check total weight of items */
	sprintf(tempsql, "select tmp_usertable.gold*3 + tmp_usertable.silver*2 "
	"+ tmp_usertable.copper from tmp_usertable where "
	"tmp_usertable.name='%s'", name);
	res=SendSQL2(tempsql, NULL);
	row = mysql_fetch_row(res);
	totalgold = atoi(row[0]); 
	mysql_free_result(res);

	/* Check total weight of items */
	sprintf(tempsql, "select sum(items.weight*tmp_itemtable.amount) from "
	"tmp_itemtable, items where tmp_itemtable.belongsto='%s' and "
	"tmp_itemtable.id=items.id and tmp_itemtable.containerid=0", name);
	res=SendSQL2(tempsql, NULL);
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
