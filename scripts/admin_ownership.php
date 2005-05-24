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
Mmud - Ownership
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Owner <?php echo $_COOKIE["karchanadminname"] ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
/* 
changing ownership/resetting ownership is only possible
if 
either
1.: you are the current owner , or
2: there is no owner defined yet.

The following tables are effected:
- mm_methods
- mm_commands
- mm_events
- mm_items
- mm_rooms
- mm_boards
- mm_usertable
*/

 
if (isset($_REQUEST{"removeownership"}))
{
	// check numeric stuff
	if (!is_numeric($_REQUEST{"removeownership"}))
	{
		die("Expected field to be an integer, and it wasn't.");
	}
	if ($_REQUEST{"removeownership"} == "1")
	{
		$table = "mm_methods";$row = "name";
	}
	else if ($_REQUEST{"removeownership"} == "2")
	{
		$table = "mm_commands";$row = "id";
	}
	else if ($_REQUEST{"removeownership"} == "3")
	{
		$table = "mm_events";$row = "eventid";
	}
	else if ($_REQUEST{"removeownership"} == "4")
	{
		$table = "mm_items";$row = "id";
	}
	else if ($_REQUEST{"removeownership"} == "5")
	{
		$table = "mm_rooms";$row = "id";
	}
	else if ($_REQUEST{"removeownership"} == "6")
	{
		$table = "mm_boards";$row = "id";
	}
	else if ($_REQUEST{"removeownership"} == "7")
	{
		$table = "mm_usertable";$row = "name";
	}
	else
	{
		die("Unknown table row to claim ownership on...");
	}
	$query = "update ".$table." set owner = null where owner = '"
	   .mysql_escape_string($_COOKIE["karchanadminname"])
	   ."' and "
	   .$row
	   ." = '".mysql_escape_string($_REQUEST{"id"})."'";
	mysql_query($query, $dbhandle)
	or die("Query (".$query.") failed : " . mysql_error());
	if (mysql_affected_rows() < 1)
	{
		die("You are not the owner.");
	}
	writeLogLong($dbhandle, "Relinquished ownership.", $query);
	printf("Relinquished ownership.<P>");
}
else
{
	printf("<TABLE BORDER=1 FRAME=void><TR VALIGN=top>");
	printf("<TD><H2>Methods</H2>");
	$result = mysql_query("select name as name from mm_methods
		where owner = '"
		.mysql_escape_string($_COOKIE["karchanadminname"])
		."' order by name"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result))
	{
		printf("%s<BR>", $myrow["name"]);
	}
	printf("</TD><TD><H2>Commands</H2>");
	$result = mysql_query("select id as id from mm_commands
		where owner = '"
		.mysql_escape_string($_COOKIE["karchanadminname"])
		."' order by id"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result))
	{
		printf("%s<BR>", $myrow["id"]);
	}
	printf("</TD><TD><H2>Events</H2>");
	$result = mysql_query("select eventid as id from mm_events
		where owner = '"
		.mysql_escape_string($_COOKIE["karchanadminname"])
		."' order by eventid"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result))
	{
		printf("%s<BR>", $myrow["id"]);
	}
	printf("</TD><TD><H2>Items</H2>");
	$result = mysql_query("select id as id from mm_items
		where owner = '"
		.mysql_escape_string($_COOKIE["karchanadminname"])
		."' order by id"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result))
	{
		printf("%s<BR>", $myrow["id"]);
	}
	printf("</TD><TD><H2>Rooms</H2>");
	$result = mysql_query("select id as id from mm_rooms
		where owner = '"
		.mysql_escape_string($_COOKIE["karchanadminname"])
		."' order by id"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result))
	{
		printf("%s<BR>", $myrow["id"]);
	}
	printf("</TD><TD><H2>Boards</H2>");
	$result = mysql_query("select id as id from mm_boards
		where owner = '"
		.mysql_escape_string($_COOKIE["karchanadminname"])
		."' order by id"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result))
	{
		printf("%s<BR>", $myrow["id"]);
	}
	printf("</TD><TD><H2>Persons</H2>");
	$result = mysql_query("select name as name from mm_usertable
		where owner = '"
		.mysql_escape_string($_COOKIE["karchanadminname"])
		."' order by name"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result))
	{
		printf("%s<BR>", $myrow["name"]);
	}
	printf("</TD></TR></TABLE>");
}

mysql_close($dbhandle);
?>


</BODY>
</HTML>
