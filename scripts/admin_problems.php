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
<IMG SRC="/images/gif/dragon.gif">Problems</H1>

<H1>Unconnected items</H1>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
$result = mysql_query("select mm_itemtable.id from mm_itemtable ".
	" LEFT JOIN mm_roomitemtable ON mm_roomitemtable.id = mm_itemtable.id where mm_roomitemtable.id is null ".
	" LEFT JOIN mm_charitemtable ON mm_charitemtable.id = mm_itemtable.id where mm_charitemtable.id is null ".
	" LEFT JOIN mm_itemitemtable ON mm_itemitemtable.id = mm_itemtable.id where mm_itemitemtable.id is null "
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s</A><BR>", $myrow[0], $myrow[0]);
}
mysql_close($dbhandle);
?>

<a HREF="/karchan/admin/index.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
