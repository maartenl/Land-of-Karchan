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
Char <?php echo $_REQUEST{"char"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
$result = mysql_query("select * from mm_usertable where name =
	\"".mysql_escape_string($_REQUEST{"char"})."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> %s<BR>", $myrow["name"]);
	printf("<b>address:</b> %s<BR>", $myrow["address"]);
	printf("<b>password:</b> %s<BR>", $myrow["password"]);
	printf("<b>title:</b> %s<BR>", $myrow["title"]);
	printf("<b>realname:</b> %s<BR>", $myrow["realname"]);
	printf("<b>email:</b> %s<BR>", $myrow["email"]);
	printf("<b>race:</b> %s<BR>", $myrow["race"]);
	printf("<b>sex:</b> %s<BR>", $myrow["sex"]);
	printf("<b>age:</b> %s<BR>", $myrow["age"]);
	printf("<b>length:</b> %s<BR>", $myrow["length"]);
	printf("<b>width:</b> %s<BR>", $myrow["width"]);
	printf("<b>complexion:</b> %s<BR>", $myrow["complexion"]);
	printf("<b>eyes:</b> %s<BR>", $myrow["eyes"]);
	printf("<b>face:</b> %s<BR>", $myrow["face"]);
	printf("<b>hair:</b> %s<BR>", $myrow["hair"]);
	printf("<b>beard:</b> %s<BR>", $myrow["beard"]);
	printf("<b>arm:</b> %s<BR>", $myrow["arm"]);
	printf("<b>leg:</b> %s<BR>", $myrow["leg"]);
	printf("<b>lok:</b> %s<BR>", $myrow["lok"]);
	printf("<b>whimpy:</b> %s<BR>", $myrow["whimpy"]);
	printf("<b>sleep:</b> %s<BR>", $myrow["sleep"]);
	printf("<b>god:</b> %s<BR>", $myrow["god"]);
	printf("<b>active:</b> %s<BR>", $myrow["active"]);
	printf("<b>lastlogin:</b> %s<BR>", $myrow["lastlogin"]);
	printf("<b>birth:</b> %s<BR>", $myrow["birth"]);
	printf("<b>Creation:</b> %s<BR>", $myrow["creation"]);
	printf("<b>Owner:</b> %s<BR>", $myrow["owner"]);
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow[21], $myrow[21]);
}

printf("<P>");

$result = mysql_query("select * ".
    " from mm_charattributes".
    " where charname = \"".mysql_escape_string($_REQUEST{"char"})."\""
    , $dbhandle)
    or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_attributelist.php?name=%s\">%s</A> ", $myrow[0], $myrow[0]);
    printf("<b>value:</b> %s ", $myrow[1]);
    printf("<b>value_type:</b> %s<BR>", $myrow[2]);
}

$result = mysql_query("select * ".
    " from characterinfo".
    " where name = \"".mysql_escape_string($_REQUEST{"char"})."\""
    , $dbhandle)
    or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
    printf("<b>imageurl:</b> %s<BR>", $myrow["imageurl"]);
    printf("<b>homepageurl:</b> %s<BR>", $myrow["homepageurl"]);
    printf("<b>dateofbirth:</b> %s<BR>", $myrow["dateofbirth"]);
    printf("<b>cityofbirth:</b> %s<BR>", $myrow["cityofbirth"]);
    printf("<b>storyline:</b> %s<BR>", $myrow["storyline"]);
}

printf("<P>");
$result = mysql_query("select mm_charitemtable.id, mm_items.id, ".
	" mm_items.adject1, mm_items.adject2, mm_items.adject3, mm_items.name from mm_items, mm_itemtable, mm_charitemtable".
	" where mm_itemtable.id = mm_charitemtable.id and ".
	" mm_items.id = mm_itemtable.itemid and ".
	" mm_charitemtable.belongsto =
	\"".mysql_escape_string($_REQUEST{"char"})."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>itemid:</b> %s ", $myrow[1]);
	printf("<b>description:</b> %s %s %s %s<BR>", $myrow[2], $myrow[3], $myrow[4], $myrow[5]);
}
mysql_close($dbhandle);
?>

<a HREF="/scripts/admin.php">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
