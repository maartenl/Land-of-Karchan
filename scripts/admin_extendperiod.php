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
Mmud - Admin
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Admin User <?php echo $_REQUEST{"char"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
if ($_COOKIE["karchanadminname"] != "Karn")
{
	error_message("This administration option is only available to Karn.");
}
$result = mysql_query("update mm_admin set validuntil = date_add(validuntil,
	interval 1 month) where name = \"".
	quote_smart($_REQUEST{"char"})."\" and 
	validuntil >= now()"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());

$result = mysql_query("update mm_admin set validuntil = date_add(now(),
	interval 1 month) where name = \"".
	quote_smart($_REQUEST{"char"})."\" and 
	validuntil < now()"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());

$result = mysql_query("select validuntil from mm_admin 
	where name = \"".
	quote_smart($_REQUEST{"char"})."\""
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result))
{
	writeLog($dbhandle, "Expiration date for ".$_REQUEST{"char"}." changed to ".
		$myrow["validuntil"].".");
	printf("Expiration date for ".$_REQUEST{"char"}." changed to ".
		$myrow["validuntil"].".<P>\r\n");
}
mysql_close($dbhandle);
?>

<a HREF="/karchan/admin/extend_period.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
