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
Land of Karchan - Admin
</TITLE>
</HEAD>
																													  
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Room <?php echo $_REQUEST{"room"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
$result = mysql_query("select * from mm_rooms where id = ".$_REQUEST{"room"}
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_row($result)) 
{
	printf("<b>id:</b> %s<BR>", $myrow[0]);
	if ($myrow[1]<>0)
	{
		printf("<b>west:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow[1], $myrow[1]);
	}
	if ($myrow[2]<>0)
	{
		printf("<b>east:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow[2], $myrow[2]);
	}
	if ($myrow[3]<>0)
	{
		printf("<b>north:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow[3], $myrow[3]);
	}
	if ($myrow[4]<>0)
	{
		printf("<b>south:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow[4], $myrow[4]);
	}
	if ($myrow[5]<>0)
	{
		printf("<b>up:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow[5], $myrow[5]);
	}
	if ($myrow[6]<>0)
	{
		printf("<b>down:</b> %s<BR>", $myrow[6]);
	}
	printf("<b>contents:</b> %s<BR>", $myrow[7]);
	printf("<b>owner:</b> %s<BR>", $myrow[8]);
	printf("<b>creation:</b> %s<BR>", $myrow[9]);
	printf("<b>area:</b> %s<BR>", $myrow[10]);
}

printf("</P>");
$result = mysql_query("select * ".
	" from mm_roomattributes".
	" where id = ".$_REQUEST{"room"}
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_row($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_attributelist.php?name=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>value:</b> %s ", $myrow[1]);
	printf("<b>value_type:</b> %s<BR>", $myrow[2]);
}

printf("</P>");
$result = mysql_query("select mm_roomitemtable.id, mm_items.id, ".
	" mm_items.adject1, mm_items.adject2, mm_items.adject3, mm_items.name from mm_items, mm_itemtable, mm_roomitemtable".
	" where mm_itemtable.id = mm_roomitemtable.id and ".
	" mm_items.id = mm_itemtable.itemid and ".
	" mm_roomitemtable.room = ".$_REQUEST{"room"}
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_row($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>itemid:</b> %s ", $myrow[1]);
	printf("<b>description:</b> %s %s %s %s<BR>", $myrow[2], $myrow[3], $myrow[4], $myrow[5]);
}
printf("</P>");

$result = mysql_query("select mm_usertable.name ".
	" from mm_usertable".
	" where active = 1 and room  = ".$_REQUEST{"room"}
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_row($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A><BR> ", $myrow[0], $myrow[0]);
}
printf("</P>");
$result = mysql_query("select mm_usertable.name ".
	" from mm_usertable".
	" where active = 0 and room  = ".$_REQUEST{"room"}
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_row($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A><BR> ", $myrow[0], $myrow[0]);
}

mysql_close($dbhandle);
?>

<a HREF="/karchan/admin/index.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
