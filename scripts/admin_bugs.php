<?
/*-------------------------------------------------------------------------
svninfo: $Id: admin_bugs.php 1129 2006-05-10 07:37:13Z maartenl $
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
Mmud - Bug Reports
</TITLE>
</HEAD>
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1><IMG SRC="/images/gif/dragon.gif">Bug Report
</H1>
<TABLE>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

if (isset($_REQUEST{"title"}))
{
	$query = "update bugs set title=\"".
		quote_smart($_REQUEST{"title"})."\", description=\"".
		quote_smart($_REQUEST{"description"})."\",  answer=\"".
		quote_smart($_REQUEST{"answer"})."\", closed= ".
		quote_smart($_REQUEST{"closed"})." where  creation+0=\"".
		quote_smart($_REQUEST{"bug"})."\"";
	mysql_query($query
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	writeLogLong($dbhandle, "Changed bugreport  ".$_REQUEST{"bug"}.".", $query);
}

// show results
$result = mysql_query("select *, creation+0 as creation3, date_format(creation, \"%Y-%m-%d %T\") as
	creation2 from bugs order by creation desc"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	if ( (!isset($_REQUEST{"open"})) or
		($_REQUEST{"open"}==$myrow["closed"]) )
	{
	printf("<TR><TD><A HREF=\"/scripts/admin_bugs.php?bug=".$myrow["creation3"]."
		\">".$myrow["creation2"].
		"</A></TD><TD>".$myrow["title"].
		"</TD><TD>".$myrow["name"].
		"</TD><TD>".($myrow["closed"]==1?"Closed":"Open")."</TD></TR>");
	}
	if (isset($_REQUEST{"bug"}) && 
		($myrow["creation3"]==$_REQUEST{"bug"}))
	{
?>
Change Bug Report:<P>
<FORM METHOD="GET" ACTION="/scripts/admin_bugs.php">
Bug: <INPUT TYPE="text" NAME="bug" VALUE="<?php echo $_REQUEST{"bug"} ?>" SIZE="60"><BR>
Title: <INPUT TYPE="text" NAME="title" VALUE="<?php echo $myrow["title"] ?>" SIZE="60"><BR>
Description:<BR>
<TEXTAREA NAME="description" VALUE="" ROWS="15" COLS="79">
<?php echo $myrow["description"] ?></TEXTAREA><P>
Answer:<BR>
<TEXTAREA NAME="answer" VALUE="" ROWS="15" COLS="79">
<?php echo $myrow["answer"] ?></TEXTAREA><P>
<input type="radio" name="closed" value="0" 
<?php echo ($myrow["closed"] == 0 ? "checked" : "") ?>>Open<BR>
<input type="radio" name="closed" value="1"
<?php echo ($myrow["closed"] == 1 ? "checked" : "") ?>>Closed
<BR>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>
<?php

	}
}
printf("</TABLE>");
mysql_close($dbhandle);
?>

</BODY>
</HTML>

