<?
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
?>
<META http-equiv="refresh" content="120">
<META HTTP-EQUIV="pragma" CONTENT="no-cache">
<HTML>
<HEAD>
<TITLE>
Land of Karchan - Who
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H2>
<IMG SRC="/images/gif/dragon.gif">
List of All Active Users</H2>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
$result = mysql_query("select name, title, sleep, 
	floor((unix_timestamp(NOW())-unix_timestamp(lastlogin)) / 60) as min,
	((unix_timestamp(NOW())-unix_timestamp(lastlogin)) % 60) as sec,
	if (mm_area.area <> \"Main\", concat(\" in \" , mm_area.shortdesc), \"\") as area
	from mm_usertable, mm_rooms, mm_area 
	where god<=1 and active=1 and mm_rooms.id = mm_usertable.room and
	mm_rooms.area = mm_area.area"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
printf("<I>There are %s persons active in the game.</I><P><UL>\r\n", 
	mysql_num_rows($result));
while ($myrow = mysql_fetch_array($result)) 
{
	if ($myrow[2] == "1")
	{
		printf("<li>%s, %s, sleeping %s (logged on %s min, %s sec ago)\r\n",
		$myrow["name"], $myrow["title"], $myrow["area"], $myrow["min"], $myrow["sec"]);
	}
	else
	{
		printf("<li>%s, %s %s (logged on %s min, %s sec ago)\r\n",
		$myrow["name"], $myrow["title"], $myrow["area"], $myrow["min"], $myrow["sec"]);
	}

}
mysql_close($dbhandle);
?>
</UL>
<hr>

<a HREF="/karchan/index.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
