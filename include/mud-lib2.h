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

void WriteMail(char *name, char *toname, char *header, char *message);
   			
int ListMail_Command(mudpersonstruct *fmudstruct);

int ReadMail(char *name, char *password, int room, int frames, int messnr, int erasehem, int socketfd);

int ReadBill(char *botname, char *vraag, char *name, int room);

int Who_Command(mudpersonstruct *fmudstruct);

void LookString(char *description, char *name, char *password, int frames, int socketfd);

void LookAtProc(int id, mudpersonstruct *fmudstruct);

void LookItem_Command(mudpersonstruct *fmudstruct);

void NotActive(char *fname, char *fpassword, int errornr, int socketfd);

int Quit_Command(mudpersonstruct *fmudstruct);

int ItemCheck(char *tok1, char *tok2, char *tok3, char *tok4, int aantal);

int Stats_Command(mudpersonstruct *fmudstruct);

void GetMoney_Command(mudpersonstruct *fmudstruct);

void DropMoney_Command(mudpersonstruct *fmudstruct);

void GiveMoney_Command(mudpersonstruct *fmudstruct);

void GetItem_Command(mudpersonstruct *fmudstruct);

void DropItem_Command(mudpersonstruct *fmudstruct);

int Put_Command(mudpersonstruct *fmudstruct);

int Retrieve_Command(mudpersonstruct *fmudstruct);

int BuyItem_Command(mudpersonstruct *fmudstruct, char *fromname);

void SellItem_Command(mudpersonstruct *fmudstruct, char *toname);

int Eat_Command(mudpersonstruct *fmudstruct);

int Drink_Command(mudpersonstruct *fmudstruct);

int Wear_Command(mudpersonstruct *fmudstruct);

int Unwear_Command(mudpersonstruct *fmudstruct);

int Wield_Command(mudpersonstruct *fmudstruct);

int Unwield_Command(mudpersonstruct *fmudstruct);

int GiveItem_Command(mudpersonstruct *fmudstruct);

int Search_Command(mudpersonstruct *fmudstruct);

int Read_Command(mudpersonstruct *fmudstruct);

void Dead(char *name, char *password, int room, int frames, int socketfd);

int ChangeTitle_Command(mudpersonstruct *fmudstruct);
