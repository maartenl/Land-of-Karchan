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
#include "typedefs.h"

/*roomindex, west, east, north, south, up, down, light_source*/
roomstruct room;

int hellroom=164;
int frames;
char secretpassword[40];

MYSQL dbconnection;

int events[50];
int knightlist[50];
int miflist[50];
int rangerlist[50];
banstruct banlist[50];

void setFrames(int i)
{
	frames=i;
}

int getFrames()
{
	return frames;
}

MYSQL getdbconnection()
{
	return dbconnection;
}

void 
FatalError(FILE *output, int i, char *troep, char *busywith)
{         
  FILE           *fp;

	fprintf(output, "<HTML>\n");
	fprintf(output, "<HEAD>\n");
	fprintf(output, "<TITLE>\n");
	fprintf(output, "Land of Karchan - Fatal Error\n");
	fprintf(output, "</TITLE>\n");
	fprintf(output, "</HEAD>\n");
	fprintf(output, "<BODY BGCOLOR=#FFFFFF>\n");
	fprintf(output, "<H1>Fatal Error %i : %s</H1>\r\n", i, troep);
	fprintf(output, "An fatal error was received while %s. Please mail the "
	"error you received to <A HREF=\"mailto:karn@karchan.org\">karn@karchan.org</A>.<P>\r\n", busywith);
	fprintf(output, "</BODY></HTML>\r\n");

  fp = fopen(ErrorFile, "a");
  fprintf(fp, "fatal error %i: %s, %s\n", i, troep, busywith);
  fclose(fp);
  exit(0);
}         

void InitializeRooms(int roomint)
{
MYSQL_RES *res;
MYSQL_ROW row;
char temp[1024];

sprintf(temp, "select west, east, north, south, up, down from rooms where id=%i", roomint);
res=SendSQL2(temp, NULL);

row = mysql_fetch_row(res);

room.west=atoi(row[0]);
room.east=atoi(row[1]);
room.north=atoi(row[2]);
room.south=atoi(row[3]);
room.up=atoi(row[4]);
room.down=atoi(row[5]);

mysql_free_result(res);
}

void exiterr(int exitcode, char *sqlstring, MYSQL *mysql)
{
FILE *fp;
fp = fopen(ErrorFile, "a");
fprintf( fp, "Error %i: %s\n {%s}\n", exitcode, mysql_error(mysql), sqlstring );
fclose(fp);
}

void exiterr2(int exitcode, char *sqlstring, MYSQL *mysql, char *file)
{
FILE *fp;

fp = fopen(file, "a");
fprintf( fp, "<FONT COLOR=red><I>Error</I> %i: %s\n {%s}</FONT><BR>\r\n", exitcode, mysql_error(mysql), sqlstring );
fclose(fp);
}

int transfertime(time_t datetime, char *stringrepresentation)
{
struct tm datetime2;
datetime2 = *(gmtime(&datetime));
        
sprintf(stringrepresentation, "%i-%.2i-%.2i %.2i:%.2i:%.2i",
datetime2.tm_year+1900, datetime2.tm_mon+1, datetime2.tm_mday,
datetime2.tm_hour, datetime2.tm_min, datetime2.tm_sec);
// 0000-01-01 00:00:00' - '9999-12-31 23:59:59' 
}

int transfertimeback(time_t *datetime, char *stringrepresentation)
{
struct tm datetime2;
char field[5];
strncpy(field, stringrepresentation, 4);field[4]='\0';
datetime2.tm_year=atoi(field)-1900;
strncpy(field, stringrepresentation+4, 2);field[2]='\0';
datetime2.tm_mon=atoi(field)-1;
strncpy(field, stringrepresentation+6, 2);field[2]='\0';
datetime2.tm_mday=atoi(field);
strncpy(field, stringrepresentation+8, 2);field[2]='\0';
datetime2.tm_hour=atoi(field);
strncpy(field, stringrepresentation+10, 2);field[2]='\0';
datetime2.tm_min=atoi(field);
strncpy(field, stringrepresentation+12, 2);field[2]='\0';
datetime2.tm_sec=atoi(field);

*datetime = mktime(&datetime2);
// 00000101000000 - 99991231235959
// 0000-01-01 00:00:00' - '9999-12-31 23:59:59' 
}

int SendSQL(char *file, char *name, char *password, char *sqlstring)
{
	MYSQL mysql;
	MYSQL_RES *res;
	MYSQL_ROW row;
	FILE *fp;
	uint i = 0;
 
	if (!(mysql_connect(&mysql,"localhost",name,password))) 
	{
		exiterr(1, sqlstring, &mysql);
		exiterr2(1, sqlstring, &mysql, file);
	}
 
	if (mysql_select_db(&mysql,DatabaseName))
	{
		exiterr(2, sqlstring, &mysql);
		exiterr2(2, sqlstring, &mysql, file);
	}
 
	if (mysql_query(&mysql,sqlstring))
	{
		exiterr(3, sqlstring, &mysql);
		exiterr2(3, sqlstring, &mysql, file);
	}
 
	if (!(res = mysql_store_result(&mysql)))
		{
		exiterr(4, sqlstring, &mysql);
		exiterr2(4, "Unable to retrieve rows, probably insert of update"
		" query...", &mysql, file);
		exiterr2(4, sqlstring, &mysql, file);
		} else {
 
		fp=fopen(file, "a");
		fprintf(fp, "<HR><CENTER><TABLE BORDER=1>\r\n");
		while((row = mysql_fetch_row(res))) {
			fprintf(fp, "<TR>");
			 for (i=0 ; i < mysql_num_fields(res); i++) 
				fprintf(fp, "<TD>%s</TD>\r\n",row[i]);
			fprintf(fp, "</TR>\r\n");
		}
		fprintf(fp, "</TABLE></CENTER><HR><BR>\r\n");
		fclose(fp);
	 
	if (!mysql_eof(res))
	{
		exiterr(5, sqlstring, &mysql);
		exiterr2(5, sqlstring, &mysql, file);
	}
 
	mysql_free_result(res);}
	mysql_close(&mysql);
}

void
opendbconnection()
{
	if (!(mysql_connect(&dbconnection,"localhost",DatabaseLogin,DatabasePassword))) 
		exiterr(1, "error establishing connection with mysql", &dbconnection);
 
	if (mysql_select_db(&dbconnection,DatabaseName))
		exiterr(2, "error opening database", &dbconnection);
}

void
closedbconnection()
{
	mysql_close(&dbconnection);
}

MYSQL_RES *SendSQL2(char *sqlstring, int *affected_rows)
{
	MYSQL_RES *res;
	MYSQL_ROW row;
	uint i = 0;
 
//	if (!(mysql_connect(&dbconnection,"localhost",DatabaseLogin,DatabasePassword))) 
//		exiterr(1, "error establishing connection with mysql", &dbconnection);
 
//	if (mysql_select_db(&dbconnection,DatabaseName))
//		exiterr(2, "error opening database", &dbconnection);

	if (mysql_query(&dbconnection,sqlstring))
		exiterr(3, sqlstring, &dbconnection);
 
	if (!(res = mysql_store_result(&dbconnection)) && mysql_num_fields(&dbconnection))
		exiterr(4, sqlstring, &dbconnection);
                    
//	if (!mysql_eof(res))
//		exiterr(5, sqlstring, &dbconnection);
 
	if (affected_rows!=NULL)
	{
		*affected_rows = mysql_affected_rows(&dbconnection);
	}

	//mysql_free_result(res);
//	mysql_close(&dbconnection);
	return res;
}

roomstruct *GetRoomInfo(int room)
{
MYSQL_RES *res;
MYSQL_ROW row;
roomstruct *roomstr;
int i;
char temp[1024];

sprintf(temp, "select west, east, north, south, up, down from rooms where id=%i", room);
res=SendSQL2(temp, NULL);

row = mysql_fetch_row(res);

roomstr = (roomstruct *)malloc(sizeof(*roomstr));

roomstr->west=atoi(row[0]);
roomstr->east=atoi(row[1]);
roomstr->north=atoi(row[2]);
roomstr->south=atoi(row[3]);
roomstr->up=atoi(row[4]);
roomstr->down=atoi(row[5]);

mysql_free_result(res);

return roomstr;
}

char *generate_password(char *fpassword)
{
int i;
srand(time(NULL));
for (i=0;i<25;i++) 
{
	switch (rand()*3/RAND_MAX)
	{
		case 0 :
			/* numbers 48-57*/
			fpassword[i]=48+(int) (10.0*rand()/(RAND_MAX+1.0));
			break;
		case 1 :
			/* capital letters 65-90 */
			fpassword[i]=65+(int) (26.0*rand()/(RAND_MAX+1.0));
			break;
		case 2 :
			/* small caps 97-122 */
			fpassword[i]=97+(int) (26.0*rand()/(RAND_MAX+1.0));
			break;
	}
}
fpassword[25]=0;
return fpassword;
}

int getCookie(char *name, char *value)
{
	char *environmentvar;
	char *found;
	char *ending;
	environmentvar = getenv("HTTP_COOKIE");
	if (environmentvar == NULL)
	{
		/* Problems: Environment var containing cookies not found */
		return 0;
	}
//	fprintf(fp, "[%s]", environmentvar);
	if ((found = strstr(environmentvar, name)) == NULL)
	{
		/* Problems: Cookie not found! */
		return 0;
	}
	if ((ending = strstr(found, ";")) == NULL)
	{
		/* Hmmm, probably last cookie in the string of cookies */
		found += strlen(name)+1;
		strcpy(value, found);
	}
	else
	{
		/* Hmmm, everything seems to be in order, copying until ; */
		found += strlen(name)+1;
		strncpy(value, found+strlen(name)+1, ending-found);
		value[ending-found]='\0';
	}
	return 1;
}
