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
Items from <?php echo $_REQUEST{"char"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
$result = mysql_query("select * from oldmud.itemtable 
	where belongsto = \"".mysql_escape_string($_REQUEST{"char"})."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
$numfields = mysql_num_fields($result);
while ($myrow = mysql_fetch_array($result)) 
{
	for ($i = 0; $i < $numfields; $i++) 
	{
		printf($myrow[$i] . " ");
	}
	printf("<BR>");
	for ($i=0;$i < $myrow["amount"];$i++)
	{
		$select = "insert into mm_itemtable (itemid, creation, owner) 
			values(".$myrow["id"].", now(), null)";
		printf("<TT>".$select."</TT><BR>");
		mysql_query($select, $dbhandle)
			or die("Query failed : " . mysql_error());
		$select = "insert into mm_charitemtable (id, belongsto) 
			select id, \"".$myrow["belongsto"]."\"
			from mm_itemtable
			where id IS NULL";
		printf("<TT>".$select."</TT><BR>");
		mysql_query($select, $dbhandle)
			or die("Query failed : " . mysql_error());
	}
}


mysql_close($dbhandle);
?>

<a HREF="/scripts/admin.php">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
