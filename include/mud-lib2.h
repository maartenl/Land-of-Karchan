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

void ReadMail(char *name, char *password, int room, int messnr, int erasehem);

int ReadBill(char *botname, char *vraag, char *name, int room);

int Who_Command(mudpersonstruct *fmudstruct);

void LookString(char *description, char *name, char *password);

void LookAtProc(int id, char *name, char *password);

void LookItem_Command(char *name, char *password, int room);

void NotActive(char *fname, char *fpassword, int errornr);

int Quit_Command(mudpersonstruct *fmudstruct);

int ItemCheck(char *tok1, char *tok2, char *tok3, char *tok4, int aantal);

int Stats_Command(mudpersonstruct *fmudstruct);

void GetMoney_Command(char *name, char *password, int room);

void DropMoney_Command(char *name, char *password, int room);

void GiveMoney_Command(char *name, char *password, int room);

void GetItem_Command(char *name, char *password, int room);

void DropItem_Command(char *name, char *password, int room);

int Put_Command(mudpersonstruct *fmudstruct);

int Retrieve_Command(mudpersonstruct *fmudstruct);

int BuyItem_Command(char *name, char *password, int room, char *fromname);

void SellItem_Command(char *name, char *password, int room, char *toname);

int Eat_Command(mudpersonstruct *fmudstruct);

int Drink_Command(mudpersonstruct *fmudstruct);

int Wear_Command(mudpersonstruct *fmudstruct);

int Unwear_Command(mudpersonstruct *fmudstruct);

int Wield_Command(mudpersonstruct *fmudstruct);

int Unwield_Command(mudpersonstruct *fmudstruct);

int GiveItem_Command(char *name, char *password, int room);

int Search_Command(mudpersonstruct *fmudstruct);

int Read_Command(mudpersonstruct *fmudstruct);

void Dead(char *name, char *password, int room);

int ChangeTitle_Command(mudpersonstruct *fmudstruct);
