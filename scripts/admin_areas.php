<?
/*-------------------------------------------------------------------------
svninfo: $Id$
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
Mmud - Ownership
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Areas</H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
/* 
showing the different areas and what rooms belong to which area.
*/

 
printf("<H2>Areas</H2>");
$result = mysql_query("select area, description, shortdesc, owner, 
	date_format(creation, \"%Y-%m-%d %T\") as creation2
	from mm_area order by area"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result))
{
	printf("<B>Area:</b> %s<BR>", $myrow["area"]);
	printf("<B>Short Description:</b> %s<BR>", $myrow["shortdesc"]);
	printf("<B>Long Description:</b> %s<BR>", $myrow["description"]);
	printf("<B>owner:</b> %s<BR>", $myrow["owner"]);
	printf("<B>creation:</B> %s<BR>", $myrow["creation2"]);
	printf("<B>rooms:</B> ");
	$result2 = mysql_query("select id from mm_rooms where area = '"
	.quote_smart($myrow["area"])
	."' order by id"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
	while ($myrow2 = mysql_fetch_array($result2))
	{
		printf("<A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A> ", $myrow2["id"], $myrow2["id"]);
	}
	printf("<P><B>boundaries:</B> ");
	$result2 = mysql_query("select distinct mm_rooms1.id 
	from mm_rooms as mm_rooms1, mm_rooms as mm_rooms2 
	where mm_rooms1.area <> '"
	.quote_smart($myrow["area"])
	."' and mm_rooms2.area = '"
	.quote_smart($myrow["area"])
	."' and 
	mm_rooms1.id in (mm_rooms2.north, mm_rooms2.south, mm_rooms2.east, mm_rooms2.west, mm_rooms2.up, mm_rooms2.down) order by mm_rooms1.id"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
	while ($myrow2 = mysql_fetch_array($result2))
	{
		printf("<A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A> ", $myrow2["id"], $myrow2["id"]);
	}
	printf("<P>");
}

mysql_close($dbhandle);
?>


</BODY>
</HTML>
