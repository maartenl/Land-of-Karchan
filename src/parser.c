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

extern int      aantal;
extern char    *tokens[100];
extern char *command;
char *stringbuffer;

//char *replace = 
//{
//{"	",""},
//{"%me","Karn"},
//{"%whom","Bill"},
//{"%this","54"},
//{NULL,NULL}
//}

int debug = 0;

int ParseSentence(char *name, int *room, char *parserstring)
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
			8 -> showstring found, dump string and skip everything else
*/
{
	if (!strcasecmp(parserstring, "end"))
	{
		if (debug) {fprintf(getMMudOut(), "end found...<BR>\n");}
		return 1;
	}
	if (!strcasecmp(parserstring, "else"))
	{
		if (debug) {fprintf(getMMudOut(), "else found...<BR>\n");}
		return 2;
	}
	if (!strcasecmp(parserstring, "return"))
	{
		if (debug) {fprintf(getMMudOut(), "return found...<BR>\n");}
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

		if (debug) {fprintf(getMMudOut(), "if sql found...<BR>\n");}
		res=SendSQL2(temp, NULL);
		free(temp);
		if (res != NULL)
		{
			row = mysql_fetch_row(res);
			if (row != NULL)
			{
				/*	row[0] should contain a 1 */
				int success = !strcasecmp("1", row[0]);
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
	if (!strcasecmp(parserstring, "debug"))
	{
		debug=1;
		if (debug) {fprintf(getMMudOut(), "<HR><FONT COLOR=red>debug found, writing messages...<BR>\n");}
	}
	if (!strcasecmp(parserstring, "showstandard"))
	{
		if (debug) {fprintf(getMMudOut(), "showstandard found, exiting...<BR>\n");}
		return 6;
	}
	if (!strcasecmp(parserstring, "showstring"))
	{
		if (debug) {fprintf(getMMudOut(), "showstring found, exiting...<BR>\n");}
		if (stringbuffer == NULL)
		{
			if (debug) {fprintf(getMMudOut(), "showstring skipped, stringbuffer empty...<BR>\n");}
		}
		else
		{
			fprintf(getMMudOut(), "<HTML>\n");
			fprintf(getMMudOut(), "<HEAD>\n");
			fprintf(getMMudOut(), "<TITLE>\n");
			fprintf(getMMudOut(), "Land of Karchan\n");
			fprintf(getMMudOut(), "</TITLE>\n");
			fprintf(getMMudOut(), "</HEAD>\n");

			fprintf(getMMudOut(), "<BODY>\n");
			if (!getFrames())
			{
				fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
			}
			else
			{
				if (getFrames()==1)
				{
					fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
				} else
				{
					fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
				}
			}
			fprintf(getMMudOut(), "%s", stringbuffer);
			return 8;
		}
	}
	if (strstr(parserstring, "show(")==parserstring)
	{
		/* execute sql statement */
		char *temp;
		MYSQL_RES *res;
		MYSQL_ROW row;
		if (debug) {fprintf(getMMudOut(), "show found...<BR>\n");}
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
				fprintf(getMMudOut(), "<HTML>\n");
				fprintf(getMMudOut(), "<HEAD>\n");
				fprintf(getMMudOut(), "<TITLE>\n");
				fprintf(getMMudOut(), "Land of Karchan\n");
				fprintf(getMMudOut(), "</TITLE>\n");
				fprintf(getMMudOut(), "</HEAD>\n");
				
				fprintf(getMMudOut(), "<BODY>\n");
				if (!getFrames())
				{
					fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"setfocus()\">\n");
				}
				else
				{
					if (getFrames()==1)
					{
						fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n");
					} else
					{
						fprintf(getMMudOut(), "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\" onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n");
					}
				}

				fprintf(getMMudOut(), "%s", row[0]);
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
		if (debug) {fprintf(getMMudOut(), "sql found...<BR>\n");}
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
		if (debug) {fprintf(getMMudOut(), "say found...<BR>\n");}
		sprintf(logname, "%s%s.log",USERHeader,name);
		temp = (char *) malloc(strlen(parserstring)+1);
		strcpy(temp, parserstring+5);
		temp[strlen(temp)-2]=0;
		WriteSentenceIntoOwnLogFile(logname, temp);
		free(temp);
	}
	if (strstr(parserstring, "sayeveryone(")==parserstring)
	{
		/* says something to everyone except to you */
		char *temp;
		temp = (char *) malloc(strlen(parserstring)+1);
		if (debug) {fprintf(getMMudOut(), "sayeveryone found...<BR>\n");}
		strcpy(temp, parserstring+13);
		temp[strlen(temp)-2]=0;
		WriteMessage(name, *room, temp);
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
		if (debug) {fprintf(getMMudOut(), "sayto found...<BR>\n");}
		temp3 = strstr(parserstring,",");
		strcpy(temp, parserstring+7);
		strcpy(temp2, temp3+2);
		temp[temp3-parserstring-8]=0;
		temp2[strlen(temp2)-2]=0;
		WriteSayTo(temp, name, *room, temp2);
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
		if (debug) {fprintf(getMMudOut(), "sayeveryoneto found...<BR>\n");}
		temp3 = strstr(parserstring,",");
		strcpy(temp, parserstring+15);
		strcpy(temp2, temp3+2);
		temp[temp3-parserstring-16]=0;
		temp2[strlen(temp2)-2]=0;
		if (!WriteMessageTo(temp, name, *room, temp2))
		{
			WriteSentenceIntoOwnLogFile(logname, "Person not found.<BR>\r\n");
		}
		free(temp);
		free(temp2);
	}
	if (strstr(parserstring, "set room=")==parserstring)
	{
		/* set the room inside the parser
		  this is necesary because the room number is not reread after
		  an update statement on the database. So both commands need to be
		  in tandem if necessary. */
		/* syntax: set room=30 */
		if (debug) {fprintf(getMMudOut(), "set room=%s found...<BR>\n", parserstring+9);}
		*room = atoi(parserstring+9);
	}
	if (strstr(parserstring, "getstring(")==parserstring)
	{
		/* compute string return value */
		char *temp;
		MYSQL_RES *res;
		MYSQL_ROW row;
		temp = (char *) malloc(strlen(parserstring)+1);
		strcpy(temp, parserstring+11);
		temp[strlen(temp)-2]=0;

		if (debug) {fprintf(getMMudOut(), "getstring found...<BR>\n");}
		res=SendSQL2(temp, NULL);
		free(temp);
		if (res != NULL)
		{
			row = mysql_fetch_row(res);
			if ( (row != NULL) && (row[0] != NULL) )
			{
				/*	row[0] should contain a string */
				if (stringbuffer != NULL)
				{
					free(stringbuffer);
				}
				stringbuffer = (char *) malloc(strlen(row[0])+1);
				strcpy(stringbuffer, row[0]);
			}
			mysql_free_result(res);
		}
	}
	if (strstr(parserstring, "addstring(")==parserstring)
	{
		/* compute string return value */
		char *temp;
		MYSQL_RES *res;
		MYSQL_ROW row;
		temp = (char *) malloc(strlen(parserstring)+1);
		strcpy(temp, parserstring+11);
		temp[strlen(temp)-2]=0;

		if (debug) {fprintf(getMMudOut(), "addstring [%s]found...<BR>\n", parserstring);}
		res=SendSQL2(temp, NULL);
		free(temp);
		if (res != NULL)
		{
			int stringbuffersize;
			if (stringbuffer == NULL)
			{
				stringbuffer = (char *) malloc(2);
				stringbuffer[0]=0;
				stringbuffersize=2;
			}
			else
			{
				stringbuffersize=strlen(stringbuffer)+2;
			}
			while ((row = mysql_fetch_row(res))!=NULL)
			{
				/*	row[0] should contain a string */
				if (row[0]!=NULL)
				{
					char *temp;
					stringbuffersize += strlen(row[0])+2;
					temp = (char *) realloc(stringbuffer, stringbuffersize);
					if (temp != NULL)
					{
						stringbuffer=temp;
						strcat(stringbuffer, row[0]);
					}
					else
					{
						if (debug) {fprintf(getMMudOut(), "addstring: realloc failed...<BR>\n");}
					}
				}
			}
			mysql_free_result(res);
		}
	}
	if (strstr(parserstring, "log(")==parserstring)
	{
		/* logs something onto the audit trail */
		time_t tijd;
		struct tm datum;
		char *temp;
		if (debug) {fprintf(getMMudOut(), "log found...<BR>\n");}
		temp = (char *) malloc(strlen(parserstring)+1);
		strcpy(temp, parserstring+5);
		temp[strlen(temp)-2]=0;
		time(&tijd);
		datum=*(gmtime(&tijd));
		WriteSentenceIntoOwnLogFile(AuditTrailFile, "%i:%i:%i %i-%i-%i %s\n",
		datum.tm_hour, datum.tm_min,datum.tm_sec,datum.tm_mday,datum.tm_mon+1,datum.tm_year+1900,
		temp);
		free(temp);
	}
	return 0;
}

int Parse(char *name, int *room, char *parserstring)
{
	char *string;
	int pos, memory, level, state[20];
	
	if (stringbuffer!=NULL)
	{
		/* clear stringbuffer for new parser method */
		free(stringbuffer);
		stringbuffer = NULL;
	}
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
//				fprintf(getMMudOut(), "<found double slash>\n");
				string[strlen(string)-1]=0;
				memory+=255;
				string = (char *) realloc(string, memory);
				if (string == NULL)
				{
					fprintf(getMMudOut(), "Error trying realloc...\n");
				}
			}
			else
			{
				char *i;
//				fprintf(getMMudOut(), "{%i}\n", strstr(string, "%me")-string);
				while ((i = strstr(string, "%me")) != NULL)
				{
					char *temp;
					temp = (char *) malloc(memory+255);
					temp[i-string]=0;
					strncpy(temp, string, i-string);
					strcat(temp, name);
					strcat(temp, i+strlen("%me"));
					free(string);
					string=temp;
					memory+=255;
				}
				while ((i = strstr(string, "%*")) != NULL)
				{
					char *temp, *temp2;
					temp2 = (char *) malloc(strlen(command)*2+4);
					mysql_escape_string(temp2, command, strlen(command));
					temp = (char *) malloc(memory+strlen(temp2)+2);
					temp[i-string]=0;
					strncpy(temp, string, i-string);
					strcat(temp, temp2);
					strcat(temp, i+strlen("%*"));
					free(string);
					string=temp;
					memory+=strlen(temp2)+2;
					free(temp2);
				}
				while ((i = strstr(string, "%string")) != NULL)
				{
					char *temp;
					char *mybuffer;
					if (stringbuffer==NULL)
					{
						mybuffer = "NULL";
					}
					else
					{
						mybuffer = stringbuffer;
					}
					temp = (char *) malloc(memory+255+strlen(mybuffer));
					temp[i-string]=0;
					strncpy(temp, string, i-string);
					strcat(temp, mybuffer);
					strcat(temp, i+strlen("%string"));
					free(string);
					string=temp;
					memory+=255+strlen(mybuffer);
				}
				while ((i = strstr(string, "%amount")) != NULL)
				{
					char *temp, change[20];
					temp = (char *) malloc(memory+255);
					temp[i-string]=0;
					strncpy(temp, string, i-string);
					sprintf(change, "%i", aantal);
					strcat(temp, change);
					strcat(temp, i+strlen("%amount"));
					free(string);
					string=temp;
					memory+=255;
				}
				while ( ((i = strstr(string, "%0")) != NULL) ||
					((i = strstr(string, "%1")) != NULL) )
				{
					char *temp, number[3];
					int integer;
					number[0]=i[1];
					number[1]=i[2];
					number[2]=0;
					integer = atoi(number);
					if ((integer > 0) && (integer <= aantal))
					{
						temp = (char *) malloc(memory+255);
						temp[i-string]=0;
						strncpy(temp, string, i-string);
						strcat(temp, tokens[integer-1]);
						strcat(temp, i+3);
						free(string);
						string=temp;
						memory+=255;
					}
					else
					{
						temp = (char *) malloc(memory+255);
						temp[i-string]=0;
						strncpy(temp, string, i-string);
						strcat(temp, i+1);
						free(string);
						string=temp;
						memory+=255;
					}
				}
				/* this is the parsing part, the previous part was just splitting up */
				if ((state[level]!=1) && (state[level]!=3))
				{
					if (debug) {fprintf(getMMudOut(), "[%s]<BR>\n", string);}
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
						case 8 : // showstring found, exiting
						{
							free(string);
							return 3;
							break;
						}
					} // end switch
				} // end if
				else
				{
					if ((state[level]==1) && (!strcasecmp(string, "else")))
					{
						state[level]=0;
					}
					if ((state[level]==1) && (!strcasecmp(string, "end")))
					{
						level--;
					}
					if ((state[level]==3) && (!strcasecmp(string, "end")))
					{
						level--;
					}
					if ( (state[level]==3) && (strstr(string, "if sql(")==string) )
					{
						level++;state[level]=3;
					}
					if ( (state[level]==1) && (strstr(string, "if sql(")==string) )
					{
						level++;state[level]=3;
					}
				} // end elseif
				if (debug) {fprintf(getMMudOut(), "[state=%i,level=%i]<BR>\n", state[level], level);}
				string[0]=0;
				memory=255;
				string = (char *) realloc(string, memory);
				if (string == NULL)
				{
					fprintf(getMMudOut(), "Error trying realloc...\n");
				}
			}
		}
		else if ((parserstring[pos] != '\t') && (parserstring[pos] != '\r'))
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
	MYSQL stuff;
	char *tempstr, *troep2;
	int returnvalue = 0;
	
	int myroom = room;
	
	stuff = getdbconnection();
	troep2 = (char *) malloc(strlen(command)*2+3);
	// unsigned int mysql_real_escape_string(MYSQL *mysql, char *to, const char *from, unsigned int length) 
	if (troep2 == NULL)
	{
		perror("SearchForSpecialCommand - attempting to allocate space for char (1)");
		return 0;
	}
	mysql_real_escape_string(&stuff, troep2, command, strlen(command));
	tempstr = (char *) malloc(strlen(troep2)+7*100+strlen(name));
	if (tempstr == NULL)
	{
		perror("SearchForSpecialCommand - attempting to allocate space for char (2)");
		return 0;
	}
	sprintf(tempstr, "select commands.id, commands.name, commands.method_name, commands.args, methods.src"
	" from commands, tmp_usertable user, methods"
	" where commands.callable != 0 "
	" and \"%s\" like commands.command "
	" and (commands.room = 0 or commands.room = user.room)"
	" and user.name = \"%s\" "
	" and methods.name = commands.method_name "
	,troep2, name);
	res=SendSQL2(tempstr, NULL);
	free(tempstr);
	free(troep2);
	if (res != NULL)
	{
		row = mysql_fetch_row(res);
		while ((row != NULL) && (returnvalue==0))
		{
			/* this is where it all begins 
				row[0] = command.id
				row[1] = command.name
				row[2] = command.method
				row[3] = command.args
				row[4] = method.src
			*/
			returnvalue = Parse(name, &myroom, row[4]);
			row = mysql_fetch_row(res);
		}
		if (debug) {fprintf(getMMudOut(), "</FONT><HR>\r\n");}
		
		mysql_free_result(res);
		if (returnvalue==1)
		{
			/* showstandard command found */
			WriteRoom(name, password, myroom, 0);
			return 1;
		}
		if (returnvalue==2)
		{
			/* show() command found */
			char logname[100];
			sprintf(logname, "%s%s.log",USERHeader,name);
 			PrintForm(name, password);
			if (getFrames()!=2) {ReadFile(logname);}
			fprintf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
			fprintf(getMMudOut(), "<DIV ALIGN=left><P>");
			return 1;
		}
		if (returnvalue==3)
		{
			/* showstring command found */
			char logname[100];
			sprintf(logname, "%s%s.log",USERHeader,name);
 			PrintForm(name, password);
			if (getFrames()!=2) {ReadFile(logname);}
			fprintf(getMMudOut(), "<HR><FONT Size=1><DIV ALIGN=right>%s", CopyrightHeader);
			fprintf(getMMudOut(), "<DIV ALIGN=left><P>");
			return 1;
		}
	}
	return 0;
}

