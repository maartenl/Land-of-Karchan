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
<HTML>
<HEAD>
<TITLE>
Mmud - Admin
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">Problems</H1>
<H2><A HREF="/karchan/admin/help/problems.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

printf("<H2>Problems with Users</H2>\r\n");
$result = mysql_query("select name from mm_usertable ".
	" where sex not in (\"male\", \"female\")"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A>
(wrong gender)<BR>",
$myrow["name"], $myrow["name"]);
}
$result = mysql_query("select name from mm_usertable left join mm_rooms ".
	" on mm_usertable.room = mm_rooms.id where mm_rooms.id is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A>
(room does not exist)<BR>",
$myrow["name"], $myrow["name"]);
}
$result = mysql_query("select name from mm_usertable where race is null or race = \"\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A>
(race is either NULL or empty)<BR>",
$myrow["name"], $myrow["name"]);
}

printf("<H2>Problems with Rooms</H2>\r\n");
$result = mysql_query("select id from mm_rooms "
	." where contents = null or trim(contents)=\"\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A>
(contents is null or empty)<BR>",
$myrow["id"], $myrow["id"]);
}
$result = mysql_query("select id from mm_rooms "
	." where north is null and south is null and east is null and west is null and up is null and down is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A>
(room has no exits)<BR>",
$myrow["id"], $myrow["id"]);
}
$result = mysql_query("select id from mm_rooms "
	." where west=0 or east=0 or north=0 or south=0 or up=0 or down=0"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A>
(one of the directions equals 0)<BR>",
$myrow["id"], $myrow["id"]);
}
$result = mysql_query("select room1.id from mm_rooms as room1 left join"
	." mm_rooms as room2 on room1.south = room2.id where room2.id is null"
	." and room1.south is not null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A>
(south exit does not exist)<BR>",
$myrow["id"], $myrow["id"]);
}
$result = mysql_query("select room1.id from mm_rooms as room1 left join"
	." mm_rooms as room2 on room1.north = room2.id where room2.id is null"
	." and room1.north is not null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A>
(north exit does not exist)<BR>",
$myrow["id"], $myrow["id"]);
}
$result = mysql_query("select room1.id from mm_rooms as room1 left join"
	." mm_rooms as room2 on room1.east = room2.id where room2.id is null"
	." and room1.east is not null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A>
(east exit does not exist)<BR>",
$myrow["id"], $myrow["id"]);
}
$result = mysql_query("select room1.id from mm_rooms as room1 left join"
	." mm_rooms as room2 on room1.west = room2.id where room2.id is null"
	." and room1.west is not null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A>
(west exit does not exist)<BR>",
$myrow["id"], $myrow["id"]);
}
$result = mysql_query("select room1.id from mm_rooms as room1 left join"
	." mm_rooms as room2 on room1.up = room2.id where room2.id is null"
	." and room1.up is not null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A>
(up exit does not exist)<BR>",
$myrow["id"], $myrow["id"]);
}
$result = mysql_query("select room1.id from mm_rooms as room1 left join"
	." mm_rooms as room2 on room1.down = room2.id where room2.id is null"
	." and room1.down is not null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A>
(down exit does not exist)<BR>",
$myrow["id"], $myrow["id"]);
}

printf("<H2>Problems with Items</H2>\r\n");
$result = mysql_query("select * from mm_items "
	." where description is null or description = \"\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>item:</b> <A HREF=\"/scripts/admin_itemdefs.php?item=%s\">%s</A> (empty description)<BR>",
$myrow["id"], $myrow["id"]);
}

$result = mysql_query("select mm_itemtable.id from mm_items, mm_itemtable, mm_charitemtable "
	." where mm_items.id < 0 "
	." and mm_items.id = mm_itemtable.itemid "
	." and mm_itemtable.id = mm_charitemtable.id"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>item instance :</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s</A> (person has item with negative itemdef)<BR>",
$myrow["id"], $myrow["id"]);
}

$result = mysql_query("select room1.id from mm_roomitemtable as room1 left join"
	." mm_itemtable as room2 on room1.id = room2.id where room2.id is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>item:</b> %s (item instance does not exist)<BR>",
$myrow["id"]);
}

$result = mysql_query("select room1.id from mm_charitemtable as room1 left join"
	." mm_itemtable as room2 on room1.id = room2.id where room2.id is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>item:</b> %s (item instance does not exist)<BR>",
$myrow["id"]);
}

$result = mysql_query("select room1.id from mm_itemitemtable as room1 left join"
	." mm_itemtable as room2 on room1.id = room2.id where room2.id is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>item:</b> %s (item instance does not exist)<BR>",
$myrow["id"]);
}

$result = mysql_query("select room1.id from mm_itemtable as room1 left join"
	." mm_items as room2 on room1.itemid = room2.id where room2.id is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>item:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s</A> (item definition does not exist)<BR>",
$myrow["id"], $myrow["id"]);
}

$result = mysql_query("select itemtable.id from mm_roomitemtable as itemtable left join"
	." mm_rooms on itemtable.room = mm_rooms.id where mm_rooms.id is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>item:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s</A> (item in non-existing room)<BR>",
$myrow["id"], $myrow["id"]);
}


printf("<H2>Problems with Attributes</H2>\r\n");
$result = mysql_query("select room1.name, room1.id from mm_roomattributes as room1 left join"
	." mm_rooms as room2 on room1.id = room2.id where room2.id is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>attribute:</b> %s (room %s does not exist)<BR>",
$myrow["name"], $myrow["id"]);
}

$result = mysql_query("select room1.name, room1.charname from mm_charattributes as room1 left join"
	." mm_usertable as room2 on room1.charname = room2.name where room2.name is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>attribute:</b> %s (character %s does not exist)<BR>",
$myrow["name"], $myrow["charname"]);
}

$result = mysql_query("select room1.name, room1.id from mm_itemattributes as room1 left join"
	." mm_itemtable as room2 on room1.id = room2.id where room2.id is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>attribute:</b> %s (item %s does not exist)<BR>",
$myrow["name"], $myrow["id"]);
}

printf("<H2>Problems with Scripts</H2>\r\n");
$result = mysql_query("select name from mm_methods where src is null or src = \"\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>method name:</b> %s (method has no source)<BR>",
$myrow["name"]);
}

printf("<H2>Problems with Mail</H2>\r\n");

$result = mysql_query("select mm_mailtable.name from mm_mailtable left join"
	." mm_usertable on mm_usertable.name = mm_mailtable.name where mm_usertable.name is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>mail:</b> %s (mailsender %s does not exist)<BR>",
$myrow["name"], $myrow["name"]);
}

$result = mysql_query("select mm_mailtable.toname from mm_mailtable left join"
	." mm_usertable on mm_usertable.name = mm_mailtable.toname where mm_usertable.name is null"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>mail:</b> %s (mailreceiver %s does not exist)<BR>",
$myrow["toname"], $myrow["toname"]);
}

mysql_close($dbhandle);
?>

</BODY>
</HTML>
