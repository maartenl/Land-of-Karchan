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
#include <time.h>
#include "guild.h"

/*
events:
 0 : chain?
 1 : hek?
 2 : cupboard deur open?
 3 : datum van de laatste keer dat er gereset is
 4 : rocks are there?
 5 : is chest in herberg open?
 6 : Zit er een rope aan de well?
 7 : Is die verdomde dwarf on the move?
 8 : Is de hatch open?
 9 : Wuthering Heights int
*/

void KillGame();
/* frees all reserved memory space of commandlines and the like and exits 
	the program
*/

void WriteMail(char *name, char *toname, char *header, char *message);
/* inserts a new mudmail
		Pre: name = name of sender ^
				toname = name of receiver ^
				header = title ^
				message = contents
		Post:added mudmail containing name, toname, header, date/time and
					message, set flags as New and Unread
*/
   			
void ListMail(char *name, char *password, char *logname);
/* lists available mudmail
		Pre: name = name of receiver, commonly yourself ^
		Post:show list of available mail
*/

void ReadMail(char *name, char *password, int room, int messnr, int erasehem);
/* read or delete mudmail with number <messnr>
		Pre: messnr = number of message to be read/erased
				erasehem = delete message if true else only read
		Post: display message ^ delete message if erasehem=true
*/

int ReadBill(char *botname, char *vraag, char *name, int room);
/* select answer if something asked to bot
		Pre: true
		post:if answer found, return answer else "Ignore" bot and add to log
*/

int Who_Command(char *name, char *password, int room, char **ftokens, char *fcommand);
/* dump list of people onto screen who are logged in at this time
		Pre: true
		Post: list of active people with idletime added
*/

void LookString(char *description, char *name, char *password);
/* write "description" onto screen, handy for looking at things or otherwise
		Pre: description contains text
		Post: ouput description nicely formatted onto screen
*/

void LookAtProc(int id, char *name, char *password);
/* look at item, selects the appropriate description and dump it to stdout
		Pre: id = identification number of item
		Post: output description of specified item to stdout
*/

void Look_Command(char *name, char *password, int room);
/* incredibly comprehesive look command, will check on the following first:
		- items
		- persons
		Pre: Format can be the following
				look at [<adjective> [<adjective> [<adjective>]]] <name>
				look at <person>
		Post: in case of item : description put onto the screen 
					in case of person: description + health + wearing put onto screen
*/

void NotActive(char *fname, char *fpassword, int errornr);
/* print error message due to inactivity */

void Root_Command(char *name, char *password, int room);
/* access root command, following rootcommands currently present:
		- sendsql
		- deactivate
		- deputyboard <message>
		- read deputyboard
		- read audit trail
		- read map
		- all <message>
*/

int CheckRoom(int i);
/* unable to beam to the following chambers */

void Evil_Command(char *name, char *password, int room);
/* access evil commands, following evil commands currently present:
		- all <message>
		- beam <number>
*/

int Quit_Command(char *name, char *password, int room, char **ftokens, char *fcommand);
/* deactivate and save player, then exit player, and show message */

void HelpHint_Command(char *name, char *password, int room);
/* show hint according to room you are in */

int ItemCheck(char *tok1, char *tok2, char *tok3, char *tok4, int aantal);
/* check if there is an item available corresponding to said parameters
	Pre: 1<= aantal <=4
	Post: aantal=1 : <tok1> = name
				aantal=2 : <tok1> <tok2> = [adject1, adject2, adject3] <name>
				aantal=3 : <tok1> <tok2> <tok3> = [adject1, adject2, adject3]x2 <name>
				aantal=4 : <tok1> <tok2> <tok3> <tok4> = [adject1, adject2, adject3]x3 <name>
				return 1 if available/checked else 0
*/

void Stats_Command(char *name, char *password);
/* show character statistics */

void GetMoney_Command(char *name, char *password, int room);
/* get money from floor
	Pre: get [amount] [gold, silver, copper] coin(s)
	Post: money is available added to inventory
*/

void DropMoney_Command(char *name, char *password, int room);
/* drop money on floor
	Pre: drop [amount] [gold, silver, copper] coin(s)
	Post: money is available for others on the floor
*/

void GiveMoney_Command(char *name, char *password, int room);
/* give money to person
	Pre: give [amount] [gold, silver, copper] coin(s) to <person> &
			enough available money on person
	Post: true
*/

void Get_Command(char *name, char *password, int room);
/* get item from floor
	Pre: get [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	Post: item(s) added to inventory
*/

void Drop_Command(char *name, char *password, int room);
/* drop item on floor
	Pre: drop [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	Post: item(s) dropped from inventory onto floor
*/

void Put_Command(char *name, char *password, int room);
/* put item into container
	Pre: put [amount] <item> in <item>; 
	     <item> = [bijv vmw] [bijv vnm] [bijv vnm] name ^
			second item must be container and (in room) V (in inventory) ^
			first item must be in inventory.
	Post: item(s) added to container if possible
*/

void Retrieve_Command(char *name, char *password, int room);
/* retrieve item from container
	Pre: retrieve [amount] <item> from <item>; 
			<item> = [bijv vmw] [bijv vnm] [bijv vnm] name ^
			second item must be container and (in room) V (in inventory)
	Post: item(s) retrieved from container and put into inventory if possible
*/

void Buy_Command(char *name, char *password, int room, char *fromname);
/* buy item from shop
	Pre: buy [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	Post: item(s) added to inventory
*/

void Sell_Command(char *name, char *password, int room, char *toname);
/* sell item to shop
	Pre: sell [amount] <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	Post: item(s) dropped from inventory, money added
*/

void Eat_Command(char *name, char *password, int room);
/* eat item in inventory
	Pre: eat <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	Post: item(s) dropped from inventory, eaten if possible
*/

void Drink_Command(char *name, char *password, int room);
/* drink item in inventory
	Pre: drink <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	Post: item(s) dropped from inventory, drunk if possible
*/

void Wear_Command(char *name, char *password, int room);
/* wear an item
	Pre: wear <item> on <position> ^ 
				<item> = [bijv vmw] [bijv vnm] [bijv vnm] name ^
				<position> = {head, neck, body, lefthand, righthand, legs, feet}
	Post: item dropped from inventory, worn if possible
*/

void Unwear_Command(char *name, char *password, int room);
/* remove an item being worn
	Pre: remove <item> ^ 
				<item> = [bijv vmw] [bijv vnm] [bijv vnm] name ^
	Post: item removed from body and added to inventory again
*/

void Wield_Command(char *name, char *password, int room);
/* wield an item in left or right hand
	Pre: wield <item> ^ 
				<item> = [bijv vmw] [bijv vnm] [bijv vnm] name ^
	Post: item removed from inventory and wielded
*/

void Unwield_Command(char *name, char *password, int room);
/* stop wielding an item in left or right hand
	Pre: unwield <item> ^ 
				<item> = [bijv vmw] [bijv vnm] [bijv vnm] name ^
	Post: item removed from right or left hand and added to inventory
*/

void Give_Command(char *name, char *password, int room);
/* give item to person
	Pre: give [amount] <item> to <person> ^
					<item> = [bijv vmw] [bijv vnm] [bijv vnm] name 
	Post: true
*/

void Search_Command(char *name, char *password, int room);
/* search something in the area to see if there's hidden items obtainable
	Pre: search <object>
	Post: found item added to inventory
*/

void Read_Command(char *name, char *password, int room);
/* read item
	Pre: read <item> ; <item> = [bijv vmw] [bijv vnm] [bijv vnm] name
	Post:dump read description onto stdout
*/

void Dead(char *name, char *password, int room);
/* Show dead screen */

void ChangeTitle_Command(char *name, char *password, int room);
/* change title of person */
