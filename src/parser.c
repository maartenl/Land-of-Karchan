/*-------------------------------------------------------------------------
cvsinfo: $Header$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA02111-1307, USA.

Maarten van Leunen
Driek van Erpstraat 9
5341 AK Oss
ederland
Europe 
maartenl@il.fontys.nl
-------------------------------------------------------------------------*/
#include "parser.h"

extern char*troep;

//char *replace = 
//{
//{"	",""},
//{"%me","Karn"},
//{"%whom","Bill"},
//{"%this","54"},
//{NULL,NULL}
//}

int debug = 0;

int ParseSentence(char *name, int room, char *parserstring)
/* Pre: name = the name of the person executing the command
		parserstring = single string containing a command or the beginning of an if statement
	Post:returns int, possible values
			0 -> continue operation as normal
			1 -> end found
			2 -> else found
			3 -> return found
			4 -> if found, if true
			5 -> if found, if false (so skip first sequence of actions)
			6 -> showstandard found, skip everything and dump output
			7 -> show found, dump description and skip everything else
*/
{
	if (!strcmp(parserstring, "end"))
	{
		if (debug) {fprintf(cgiOut, "end found...<BR>\n");}
		return 1;
	}
	if (!strcmp(parserstring, "else"))
	{
		if (debug) {fprintf(cgiOut, "else found...<BR>\n");}
		return 2;
	}
	if (!strcmp(parserstring, "return"))
	{
		if (debug) {fprintf(cgiOut, "return found...<BR>\n");}
		return 3;
	}
	if (strstr(parserstring, "if sql(")==parserstring)
	{
		/* compute sql return value */
		char *temp;
		MYSQL_RES *res;
		MYSQL_ROW row;
		temp = (char *) malloc(strlen(parserstring)+1);
		strcpy(temp, parserstring+8);
		temp[strlen(temp)-2]=0;

		if (debug) {fprintf(cgiOut, "if sql found...<BR>\n");}
		res=SendSQL2(temp, NULL);
		free(temp);
		if (res != NULL)
		{
			row = mysql_fetch_row(res);
			if (row != NULL)
			{
				/*	row[0] should contain a 1 */
				int success = !strcmp("1", row[0]);
				mysql_free_result(res);
				if (success)
				{
					return 4; /* true, resultset okay, row okay, 1 found */
				}
				else
				{
					return 5; /* false, resultset okay, row okay, 1 not found */
				}
			}
			else
			{
				mysql_free_result(res);
				return 5; /* false, resultset okay, but no rows */
			}
		}
		return 5; /* false, due to no resultset */
	}
	if (!strcmp(parserstring, "debug"))
	{
		debug=1;
		if (debug) {fprintf(cgiOut, "<HR><FONT COLOR=red>debug found, writing messages...<BR>\n");}
	}
	if (!strcmp(parserstring, "showstandard"))
	{
		if (debug) {fprintf(cgiOut, "showstandard found, exiting...<BR>\n");}
		return 6;
	}
	if (strstr(parserstring, "show(")==parserstring)
	{
		/* execute sql statement */
		char *temp;
		MYSQL_RES *res;
		MYSQL_ROW row;
		if (debug) {fprintf(cgiOut, "show found...<BR>\n");}
		temp = (char *) malloc(strlen(parserstring)+1);
		strcpy(temp, parserstring+6);
		temp[strlen(temp)-2]=0;

		res=SendSQL2(temp, NULL);
		free(temp);
		if (res != NULL)
		{
			row = mysql_fetch_row(res);
			if (row != NULL)
			{
				/*	row[0] should contain a description to be displayed */
				fprintf(cgiOut, "<HTML>\n");
				fprintf(cgiOut, "<HEAD>\n");
				fprintf(cgiOut, "<TITLE>\n");
				fprintf(cgiOut, "Land of Karchan\n");
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

				fprintf(cgiOut, "%s", row[0]);
				mysql_free_result(res);
				return 7;
			}
			mysql_free_result(res);
		}
	}
	if (strstr(parserstring, "sql(")==parserstring)
	{
		/* execute sql statement */
		char *temp;
		MYSQL_RES *res;
		MYSQL_ROW row;
		if (debug) {fprintf(cgiOut, "sql found...<BR>\n");}
		temp = (char *) malloc(strlen(parserstring)+1);
		strcpy(temp, parserstring+5);
		temp[strlen(temp)-2]=0;

		res=SendSQL2(temp, NULL);
		free(temp);
		if (res != NULL)
		{
			mysql_free_result(res);
		}
	}
	if (strstr(parserstring, "say(")==parserstring)
	{
		/* says something to yourself */
		char logname[100];
		char *temp;
		if (debug) {fprintf(cgiOut, "say found...<BR>\n");}
		sprintf(logname, "%s%s.log",USERHeader,name);
		temp = (char *) malloc(strlen(parserstring)+1);
		strcpy(temp, parserstring+5);
		temp[strlen(temp)-2]=0;
		WriteSentenceIntoOwnLogFile2(logname, temp);
		free(temp);
	}
	if (strstr(parserstring, "sayeveryone(")==parserstring)
	{
		/* says something to everyone except to you */
		char *temp;
		temp = (char *) malloc(strlen(parserstring)+1);
		if (debug) {fprintf(cgiOut, "sayeveryone found...<BR>\n");}
		strcpy(temp, parserstring+13);
		temp[strlen(temp)-2]=0;
		WriteMessage2(name, room, temp);
		free(temp);
	}
	if (strstr(parserstring, "sayto(")==parserstring)
	{
		/* says something to somebody only */
		/* syntax: sayto("Bill","Howdie!<BR>") */
		char *temp, *temp2, *temp3;
		char logname[100];
		temp = (char *) malloc(strlen(parserstring)+1);
		temp2 = (char *) malloc(strlen(parserstring)+1);
		sprintf(logname, "%s%s.log",USERHeader,name);
		if (debug) {fprintf(cgiOut, "sayto found...<BR>\n");}
		temp3 = strstr(parserstring,",");
		strcpy(temp, parserstring+7);
		strcpy(temp2, temp3+2);
		temp[temp3-parserstring-8]=0;
		temp2[strlen(temp2)-2]=0;
		WriteSayTo(temp, name, room, temp2);
		free(temp);
		free(temp2);
	}
	if (strstr(parserstring, "sayeveryoneto(")==parserstring)
	{
		/* says something to everyone except someone */
		/* syntax: sayto("Bill","Howdie!<BR>") */
		char *temp, *temp2, *temp3;
		char logname[100];
		sprintf(logname, "%s%s.log",USERHeader,name);
		temp = (char *) malloc(strlen(parserstring)+1);
		temp2 = (char *) malloc(strlen(parserstring)+1);
		if (debug) {fprintf(cgiOut, "sayeveryoneto found...<BR>\n");}
		temp3 = strstr(parserstring,",");
		strcpy(temp, parserstring+15);
		strcpy(temp2, temp3+2);
		temp[temp3-parserstring-16]=0;
		temp2[strlen(temp2)-2]=0;
		if (!WriteMessageTo2(temp, name, room, temp2))
		{
			WriteSentenceIntoOwnLogFile2(logname, "Person not found.<BR>\r\n");
		}
		free(temp);
		free(temp2);
	}
	return 0;
}

int Parse(char *name, int room, char *parserstring)
{
	char *string;
	int pos, memory, level, state[20];
	
	/*provides us with what has to be read	
	Perhaps needs a little more explanations:
	0 : state normal, normal operation, => 1/2
	1 : skip everything until encounter "else" => 0 
	2 : run everything until encounter "else" => 3
	3 : skip everything until encounter "end" => 0
	*/
	for (pos=0;pos<20;pos++) {state[pos]=0;}
	pos=0;memory=255;
	string = (char *) malloc(memory);
	string[0]=0;
	level=0; //provides us with the level of the if statements (nesting level)
	while (pos < strlen(parserstring))
	{
		if (parserstring[pos] == '\n')
		{
			if ((pos>0) && (parserstring[pos-1]=='\\'))
			{
//				fprintf(cgiOut, "<found double slash>\n");
				string[strlen(string)-1]=0;
				memory+=255;
				string = (char *) realloc(string, memory);
				if (string == NULL)
				{
					fprintf(cgiOut, "Error trying realloc...\n");
				}
			}
			else
			{
				char *i;
//				fprintf(cgiOut, "{%i}\n", strstr(string, "%me")-string);
				while ((i=strstr(string, "%me"))!=NULL)
				{
					char *temp;
					temp = (char *) malloc(memory+255);
					temp[i-string]=0;
					strncpy(temp, string, i-string);
					strcat(temp, name);
					strcat(temp, i+strlen(name)-1);
					strcpy(string, temp);
					free(temp);
				}
				if (debug) {fprintf(cgiOut, "[%s]<BR>\n", string);}
				/* this is the parsing part, the previous part was just splitting up */
				if ((state[level]!=1) && (state[level]!=3))
				{
					switch (ParseSentence(name, room, string))
					{
						case 1 : // end found
						{
							if (level > 0)	level--;
							break;
						}
						case 2 : // else found
						{
							if (level > 0)
							{
								state[level]=3;
							}
							break;
						}
						case 3 : // return found
						{
							free(string);
							return 0;
						}
						case 4 : // if found, if true
						{
							level++;
							state[level]=2;
							break;
						}
						case 5 : // if found, if false (so skip first sequence of actions)
						{
							level++;
							state[level]=1;
							break;
						}
						case 6 : // showstandard found, exiting
						{
							free(string);
							return 1;
							break;
						}
						case 7 : // show found, exiting
						{
							free(string);
							return 2;
							break;
						}
					} // end switch
				} // end if
				else
				{
					if ((state[level]==1) && (!strcmp(string, "else")))
					{
						state[level]=0;
					}
					if ((state[level]==1) && (!strcmp(string, "end")))
					{
						level--;
					}
					if ((state[level]==3) && (!strcmp(string, "end")))
					{
						level--;
					}
				} // end elseif
				if (debug) {fprintf(cgiOut, "[state=%i,level=%i]<BR>\n", state[level], level);}
				string[0]=0;
				memory=255;
				string = (char *) realloc(string, memory);
				if (string == NULL)
				{
					fprintf(cgiOut, "Error trying realloc...\n");
				}
			}
		}
		else if (parserstring[pos] != '\t')
		{
			string[strlen(string)+1]=0;
			string[strlen(string)]=parserstring[pos];
		}
		pos++;
	}
	free(string);
	return 0;
}

int SearchForSpecialCommand(char *name, char *password, int room)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	char temp[1024];
	int returnvalue = 0;
	
	sprintf(temp, "select commands.id, commands.name, commands.method_name, commands.args, methods.src"
	" from commands, tmp_usertable user, methods"
	" where commands.callable != 0 "
	" and \"%s\" like commands.command "
	" and (commands.room = 0 or commands.room = user.room)"
	" and user.name = \"%s\" "
	" and methods.name = commands.method_name "
	,troep, name);
	res=SendSQL2(temp, NULL);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		if (row != NULL)
		{
			/* this is where it all begins 
				row[0] = command.id
				row[1] = command.name
				row[2] = command.method
				row[3] = command.args
				row[4] = method.src
			*/
			returnvalue = Parse(name, room, row[4]);
		}
		if (debug) {fprintf(cgiOut, "</FONT><HR>\r\n");}
		mysql_free_result(res);
		if (returnvalue==1)
		{
			WriteRoom(name, password, room, 0);
			KillGame();
		}
		if (returnvalue==2)
		{
			char logname[100];
			sprintf(logname, "%s%s.log",USERHeader,name);
 			PrintForm(name, password);
			if (getFrames()!=2) {ReadFile(logname);}
			fprintf(cgiOut, "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
			fprintf(cgiOut, "<DIV ALIGN=left><P>");
			KillGame();
		}
	}
}