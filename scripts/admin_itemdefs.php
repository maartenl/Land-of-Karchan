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
Item Definition <?php echo $_REQUEST{"item"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
$result = mysql_query("select * from mm_items where id = ".
		mysql_escape_string($_REQUEST{"item"})
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> %s<BR>", $myrow["id"]);
	printf("<b>name:</b> %s<BR>", $myrow["name"]);
	printf("<b>adject1:</b> %s<BR>", $myrow["adject1"]);
	printf("<b>adject2:</b> %s<BR>", $myrow["adject2"]);
	printf("<b>adject3:</b> %s<BR>", $myrow["adject3"]);
	printf("<b>manaincrease:</b> %s<BR>", $myrow["manaincrease"]);
	printf("<b>hitincrease:</b> %s<BR>", $myrow["hitincrease"]);
	printf("<b>vitalincrease:</b> %s<BR>", $myrow["vitalincrease"]);
	printf("<b>movementincrease:</b> %s<BR>", $myrow["movementincrease"]);
	printf("<b>eatable:</b> %s<BR>", $myrow["eatable"]);
	printf("<b>drinkable:</b> %s<BR>", $myrow["drinkable"]);
	printf("<b>lightable:</b> %s<BR>", $myrow["lightable"]);
	printf("<b>getable:</b> %s<BR>", $myrow["getable"]);
	printf("<b>dropable:</b> %s<BR>", $myrow["dropable"]);
	printf("<b>visible:</b> %s<BR>", $myrow["visible"]);
	printf("<b>wieldable:</b> %s<BR>", $myrow["wieldable"]);
	printf("<b>description:</b> %s<BR>", $myrow["description"]);
	printf("<b>readdescr:</b> %s<BR>", $myrow["readdescr"]);
	printf("<b>wearable:</b> <UL>");
	if ($myrow["wearable"] & 1)
	{
		printf("<li>on head");
	}
	if ($myrow["wearable"] & 2)
	{
		printf("<li>on neck");
	}
	if ($myrow["wearable"] & 4)
	{
		printf("<li>on torso");
	}
	if ($myrow["wearable"] & 8)
	{
		printf("<li>on arms");
	}
	if ($myrow["wearable"] & 16)
	{
		printf("<li>on left wrist");
	}
	if ($myrow["wearable"] & 32)
	{
		printf("<li>on right wrist");
	}
	if ($myrow["wearable"] & 64)
	{
		printf("<li>on left finger");
	}
	if ($myrow["wearable"] & 128)
	{
		printf("<li>on right finger");
	}
	if ($myrow["wearable"] & 256)
	{
		printf("<li>on feet");
	}
	if ($myrow["wearable"] & 512)
	{
		printf("<li>on hands");
	}
	if ($myrow["wearable"] & 1024)
	{
		printf("<li>floating nearby");
	}
	if ($myrow["wearable"] & 2048)
	{
		printf("<li>on waist");
	}
	if ($myrow["wearable"] & 4096)
	{
		printf("<li>on legs");
	}
	if ($myrow["wearable"] & 8192)
	{
		printf("<li>on eyes");
	}
	if ($myrow["wearable"] & 16384)
	{
		printf("<li>on ears");
	}
	if ($myrow["wearable"] & 32768)
	{
		printf("<li>on body");
	}
	if ($myrow["wearable"] & 65536)
	{
		printf("<li>wielding with left hand");
	}
	if ($myrow["wearable"] & 131072)
	{
		printf("<li>wielding with right hand");
	}
	if ($myrow["wearable"] & 262144)
	{
		printf("<li>wielding with both hands");
	}
	printf("</ul><b>gold:</b> %s<BR>", $myrow["gold"]);
	printf("<b>silver:</b> %s<BR>", $myrow["silver"]);
	printf("<b>copper:</b> %s<BR>", $myrow["copper"]);
	printf("<b>weight:</b> %s<BR>", $myrow["weight"]);
	printf("<b>pasdefense:</b> %s<BR>", $myrow["pasdefense"]);
	printf("<b>damageresistance:</b> %s<BR>", $myrow["damageresistance"]);
	printf("<b>container:</b> %s<BR>", $myrow["container"]);
	printf("<b>Owner:</b> %s<BR>", $myrow["owner"]);
	printf("<b>Creation:</b> %s<BR>", $myrow["creation"]);
}

$result = mysql_query("select id from mm_itemtable where itemid = ".
		mysql_escape_string($_REQUEST{"item"})
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s<A><BR> ", $myrow[0], $myrow[0]);
}

mysql_close($dbhandle);
?>

<a HREF="/scripts/admin.php">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
