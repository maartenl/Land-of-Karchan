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
Attribute <?php echo $_REQUEST{"name"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
$result = mysql_query("select * from mm_charattributes ".
	"where name = \"".$_REQUEST{"name"}."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_row($result)) 
{
	printf("<b>value:</b> %s ", $myrow[1]);
	printf("<b>value_type:</b> %s ", $myrow[2]);
	printf("<b>character:</b> <A HREF=\"/scripts/admin_chars.php?char=%s\">%s<A><BR>", $myrow[3], $myrow[3]);
}
printf("<P>");

$result = mysql_query("select * from mm_roomattributes ".
	"where name = \"".$_REQUEST{"name"}."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_row($result)) 
{
	printf("<b>value:</b> %s ", $myrow[1]);
	printf("<b>value_type:</b> %s ", $myrow[2]);
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s<A><BR>", $myrow[3], $myrow[3]);
}
printf("<P>");

$result = mysql_query("select * from mm_itemattributes ".
	"where name = \"".$_REQUEST{"name"}."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_row($result)) 
{
	printf("<b>value:</b> %s ", $myrow[1]);
	printf("<b>value_type:</b> %s ", $myrow[2]);
	printf("<b>item:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s<A><BR>", $myrow[3], $myrow[3]);
}
printf("<P>");

mysql_close($dbhandle);
?>

<a HREF="/karchan/admin/index.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>