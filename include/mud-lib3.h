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

int GoWest_Command(mudpersonstruct *fmudstruct);
int GoEast_Command(mudpersonstruct *fmudstruct);
int GoNorth_Command(mudpersonstruct *fmudstruct);
int GoSouth_Command(mudpersonstruct *fmudstruct);
int Sleep_Command(mudpersonstruct *fmudstruct);
void Awaken2_Command(char *name, char *password, int room);
int BigTalk_Command(mudpersonstruct *fmudstruct);
int MailFormDumpOnScreen(mudpersonstruct *fmudstruct);
int Time_Command(mudpersonstruct *fmudstruct);
int Date_Command(mudpersonstruct *fmudstruct);
void LookSky_Command(char *name, char *password);

void SwitchRoomCheck(char *name, char *password, int room);
