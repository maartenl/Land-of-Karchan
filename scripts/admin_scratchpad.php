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
Mmud - Admin
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Scratchpad</H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

/*
There are two possibilities:
1. just the contents
2. just the contents but with a changeable form
*/

if (isset($_REQUEST{"scratchpad"}))
{
$result = mysql_query("update scratchpad set scratch=
	\"".mysql_escape_string($_REQUEST{"scratchpad"})."\",
	owner=\"".mysql_escape_string($_COOKIE["karchanadminname"])."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
}
$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as
last_updated2 from scratchpad"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>Last updated:</b> %s, by %s<BR>", $myrow["last_updated2"]
		, $myrow["owner"]);  
    printf("%s", $myrow["scratch"]);
	if (isset($_REQUEST{"bogus"}))
	{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_scratchpad.php">
<TEXTAREA NAME="scratchpad" ROWS="50" COLS="85">
<?php echo $myrow["scratch"] ?>
</TEXTAREA>
<P><INPUT TYPE="submit" VALUE="Submit Info"><P>
</FORM>
<?php
	}
	else
	{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_scratchpad.php">
<INPUT TYPE="hidden" NAME="bogus" VALUE="bogus">
<P><INPUT TYPE="submit" VALUE="Change Scratchpad"><P>
</FORM>
<?php
	}
}
mysql_close($dbhandle);
?>


</BODY>
</HTML>
