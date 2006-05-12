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
<IMG SRC="/images/gif/dragon.gif">Public/Private Boards</H1>
<A HREF="/karchan/admin/help/boards.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A><P>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

if ( (isset($_REQUEST{"id"})) &&
	(isset($_REQUEST{"name"})) &&
	(isset($_REQUEST{"posttime"})) )
{
	// checkit
	$query = "update mm_boardmessages set removed = 1 where boardid = ".
		quote_smart($_REQUEST{"id"}).
		" and name = \"".
		quote_smart($_REQUEST{"name"}).
		"\" and posttime+0 = ".
		quote_smart($_REQUEST{"posttime"});
	mysql_query($query
		, $dbhandle)  
		or error_message("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Removed board message due to offensive content.", $query);
}

$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as
        creation2 from mm_boards"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> %s ", $myrow["id"]);
	printf("<b>name:</b> %s ", $myrow["name"]);
	printf("<b>owner:</b> %s ", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	printf("<b>description:</b> %s<BR>", $myrow["description"]);
	$result2 = mysql_query("select *, posttime + 0 as stuff from mm_boardmessages where boardid = ".
		$myrow["id"].
		" and week(posttime)=week(now()) and year(posttime)=year(now())"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
	while ($myrow2 = mysql_fetch_array($result2)) 
	{
		if ($myrow2["removed"] == 0)
		{
			printf("<A HREF=\"/scripts/admin_boards.php?id=%s&name=%s&posttime=%s\">Remove</A> ",
				 $myrow2["boardid"], $myrow2["name"], $myrow2["stuff"]);
		}
		printf("<b>name:</b> %s ", $myrow2["name"]);
		printf("<b>posttime:</b> %s ", $myrow2["posttime"]);
		printf("<b>removed:</b> %s<BR>", ($myrow2["removed"] == 1?"yes":"no"));
	}
}

mysql_close($dbhandle);
?>

</BODY>
</HTML>
