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
Banned People</H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

if ($_REQUEST{"ban_address"} != "")
{
	mysql_query("replace into mm_bantable values(".
	"\"".mysql_escape_string($_REQUEST{"ban_address"})."\",".
	mysql_escape_string($_REQUEST{"ban_days"}).",".
	"\"".mysql_escape_string($_REQUEST{"ban_ip"})."\",".
	"\"".mysql_escape_string($_REQUEST{"ban_name"})."\",".
	"\"".mysql_escape_string($_COOKIE{"karchanadminname"})."\",".
	"now(),".
	"\"".mysql_escape_string($_REQUEST{"ban_reason"})."\"".
	")"
	, $dbhandle)
	or die("Query(8) failed : " . mysql_error());
	writeLog($dbhandle, "Added ban on address ".$_REQUEST{"ban_address"}.".");
}
if ($_REQUEST{"unbanname"} <> "")
{
	mysql_query("replace into mm_unbantable values(\"".
	mysql_escape_string($_REQUEST{"unbanname"})."\")"
	, $dbhandle)
	or die("Query(2) failed : " . mysql_error());
	writeLog($dbhandle, "Added name to be unbanned. (".$_REQUEST{"unbanname"}.")");
}
if ($_REQUEST{"sillyname"} != "")
{
	mysql_query("replace into mm_sillynamestable values(\"".
	mysql_escape_string($_REQUEST{"sillyname"})."\")"
	, $dbhandle)
	or die("Query(3) failed : " . mysql_error());
	writeLog($dbhandle, "Added sillyname. (".$_REQUEST{"sillyname"}.")");
}
if ($_REQUEST{"remove_unban"} != NULL)
{
	mysql_query("delete from mm_unbantable where name = \"".
		mysql_escape_string($_REQUEST{"remove_unban"})."\""
		, $dbhandle)
		or die("Query(4) failed : " . mysql_error());
	writeLog($dbhandle, "Removed unbanname. (".$_REQUEST{"remove_unban"}.")");
}
if ($_REQUEST{"remove_sillyname"} != NULL)
{
	mysql_query("delete from mm_sillynamestable where name = \"".
		mysql_escape_string($_REQUEST{"remove_sillyname"})."\""
		, $dbhandle)
		or die("Query(6) failed : " . mysql_error());
	writeLog($dbhandle, "Removed sillyname. (".$_REQUEST{"remove_sillyname"}.")");
}


printf("Bantable<P>");
$result = mysql_query("select * from mm_bantable"
	, $dbhandle)
	or die("Query(1) failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>address:</b> %s ", $myrow[0]);
	printf("<b>days:</b> %s ",$myrow[1]);
	printf("<b>ip:</b> %s ", $myrow[2]);
	printf("<b>name:</b> %s ", $myrow[3]);
	printf("<b>deputy:</b> %s ", $myrow[4]);
	printf("<b>date:</b> %s ", $myrow[5]);
	printf("<b>reason:</b> %s<BR>", $myrow[6]);
}


printf("Unbantable<P>");
$result = mysql_query("select * from mm_unbantable order by name"
	, $dbhandle)
	or die("Query(5) failed : " . mysql_error());
$count = 1;
printf("<TABLE><TR><TD>");
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<A HREF=\"/scripts/admin_banned.php?remove_unban=%s\">%s</A><BR>", 
		$myrow[0], $myrow[0]);
	$count ++;
	if ($count > 40)
	{
		$count = 1;
		printf("</TD><TD>");
	}
}
printf("</TD></TR></TABLE>");

printf("Sillynamestable<P>");
$result = mysql_query("select * from mm_sillynamestable order by name"
	, $dbhandle)
	or die("Query(7) failed : " . mysql_error());
$count = 1;
printf("<TABLE><TR><TD>");
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<A HREF=\"/scripts/admin_banned.php?remove_sillyname=%s\">%s</A><BR>",
		$myrow[0], $myrow[0]);
	$count ++;
	if ($count > 40)
	{
		$count = 1;
		printf("</TD><TD>");
	}
}
printf("</TD></TR></TABLE>");


mysql_close($dbhandle);
?>

Add Unbanname:<FORM METHOD="GET" ACTION="/scripts/admin_banned.php">
<INPUT TYPE="text" NAME="unbanname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

Add Sillyname:<FORM METHOD="GET" ACTION="/scripts/admin_banned.php">
<INPUT TYPE="text" NAME="sillyname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

Add Ban:<FORM METHOD="GET" ACTION="/scripts/admin_banned.php">
Address:<INPUT TYPE="text" NAME="ban_address" VALUE="" SIZE="40" MAXLENGTH="40"><P>
Days:<INPUT TYPE="text" NAME="ban_days" VALUE="" SIZE="3" MAXLENGTH="3"><P>
Ip:<INPUT TYPE="text" NAME="ban_ip" VALUE="" SIZE="40" MAXLENGTH="40"><P>
Name:<INPUT TYPE="text" NAME="ban_name" VALUE="" SIZE="20" MAXLENGTH="20"><P>
Reason:<INPUT TYPE="text" NAME="ban_reason" VALUE="" SIZE="40" MAXLENGTH="255"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

</BODY>
</HTML>

