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
Table of Contents</H1>
<A HREF="#introduction">Introduction</A><P>
<A HREF="#bannedpeople">Banned People</A><P>
<A HREF="#unbannedchars">Unbanned Characters</A><P>
<A HREF="#bannedchars">Banned Characters</A><P>
<A HREF="#sillynames">Silly Names</A><P>

<A NAME="introduction"><H1><A HREF="/karchan/admin/help/banning.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A>
Introduction</H1>

The banning of people and characters from the mud may seem to be 
complicated at first. Therefore this short introduction. The following rules
govern wether or not the character is banned. The rules are processed in the
order that they appear here. If a rule indicates that a person is
<I>banned</I> or
is <I>not banned</I>, then all te next rules are skipped.
<OL><LI>if the name exists in the Silly names table, the character is
<I>banned</I>
<LI>if the name exists in the Unbanned names table, the character is <I>not
banned</I>
<LI>if the name exists in the Banned names table, the character is
<I>banned</I>
<LI>if the ip number exists or is part of a range of ipnumbers specified in
the Banned people table, the character is <I>banned</I>
<LI>in <I>all</I> other cases the character is allowed to log on.
</OL>

<A NAME="bannedpeople"><H1>Banned People</H1>
<TABLE><TR><TD><B>Address</B></TD><TD><B>Days</B></TD><TD><B>Ip
</B></TD><TD><B>Name</B></TD><TD><B>Deputy</B></TD><TD><B>Date
</B></TD><TD><B>Reason</B></TD></TR>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

if ($_REQUEST{"ban_address"} != "")
{
	mysql_query("replace into mm_bantable values(".
	"\"".quote_smart($_REQUEST{"ban_address"})."\",".
	quote_smart($_REQUEST{"ban_days"}).",".
	"\"".quote_smart($_REQUEST{"ban_ip"})."\",".
	"\"".quote_smart($_REQUEST{"ban_name"})."\",".
	"\"".quote_smart($_COOKIE{"karchanadminname"})."\",".
	"now(),".
	"\"".quote_smart($_REQUEST{"ban_reason"})."\"".
	")"
	, $dbhandle)
	or error_message("Query(8) failed : " . mysql_error());
	writeLog($dbhandle, "Added ban on address ".$_REQUEST{"ban_address"}.".");
}
if ($_REQUEST{"add_bannedname"} != "")
{
	$query = "replace into mm_bannednamestable values(\"".
	quote_smart($_REQUEST{"add_bannedname"})."\",\"".
	quote_smart($_COOKIE{"karchanadminname"})."\",".
	"now(),".
	quote_smart($_REQUEST{"add_bandays"}).",\"".
	quote_smart($_REQUEST{"add_banreason"}).
	"\")";
	mysql_query($query, $dbhandle)
	or error_message("Query(8) failed : " . $query . mysql_error());
	writeLog($dbhandle, "Added ban on character ".$_REQUEST{"add_bannedname"}.".");
}
if ($_REQUEST{"unbanname"} <> "")
{
	mysql_query("replace into mm_unbantable values(\"".
	quote_smart($_REQUEST{"unbanname"})."\")"
	, $dbhandle)
	or error_message("Query(2) failed : " . mysql_error());
	writeLog($dbhandle, "Added name to be unbanned. (".$_REQUEST{"unbanname"}.")");
}
if ($_REQUEST{"sillyname"} != "")
{
	mysql_query("replace into mm_sillynamestable values(\"".
	quote_smart($_REQUEST{"sillyname"})."\")"
	, $dbhandle)
	or error_message("Query(3) failed : " . mysql_error());
	writeLog($dbhandle, "Added sillyname. (".$_REQUEST{"sillyname"}.")");
}
if ($_REQUEST{"remove_unban"} != NULL)
{
	mysql_query("delete from mm_unbantable where name = \"".
		quote_smart($_REQUEST{"remove_unban"})."\""
		, $dbhandle)
		or error_message("Query(4) failed : " . mysql_error());
	writeLog($dbhandle, "Removed unbanname. (".$_REQUEST{"remove_unban"}.")");
}
if ($_REQUEST{"remove_sillyname"} != NULL)
{
	mysql_query("delete from mm_sillynamestable where name = \"".
		quote_smart($_REQUEST{"remove_sillyname"})."\""
		, $dbhandle)
		or error_message("Query(6) failed : " . mysql_error());
	writeLog($dbhandle, "Removed sillyname. (".$_REQUEST{"remove_sillyname"}.")");
}
if ($_REQUEST{"remove_bannedname"} != NULL)
{
	mysql_query("delete from mm_bannednamestable where name = \"".
		quote_smart($_REQUEST{"remove_bannedname"})."\""
		, $dbhandle)
		or error_message("Query(6) failed : " . mysql_error());
	writeLog($dbhandle, "Removed banned name. (".$_REQUEST{"remove_bannedname"}.")");
}
if ($_REQUEST{"remove_banned"} != NULL)
{
	$query = "delete from mm_bantable where trim(address) = \"".
		quote_smart($_REQUEST{"remove_banned"})."\"";
	mysql_query($query, $dbhandle)
		or error_message("Query(6) failed : " . mysql_error());
	writeLog($dbhandle, "Removed ".$query."banned address. (".$_REQUEST{"remove_banned"}.")");
}

$result = mysql_query("select * from mm_bantable"
	, $dbhandle)
	or error_message("Query(1) failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<TR><TD><A HREF=\"/scripts/admin_banned.php?remove_banned=%s\">%s</A></TD>",
	$myrow["address"], $myrow["address"]);
	printf("<TD>%s ",$myrow["days"]);
	printf("<TD>%s ", $myrow["IP"]);
	printf("<TD>%s ", $myrow["name"]);
	printf("<TD>%s ", $myrow["deputy"]);
	printf("<TD>%s ", $myrow["date"]);
	printf("<TD>%s</TD></TR>", $myrow["reason"]);
}
?>

</TABLE>
Add Ban:<FORM METHOD="GET" ACTION="/scripts/admin_banned.php">
Address:<INPUT TYPE="text" NAME="ban_address" VALUE="" SIZE="40" MAXLENGTH="40"><P>
Days:<INPUT TYPE="text" NAME="ban_days" VALUE="" SIZE="3" MAXLENGTH="3"><P>
Ip:<INPUT TYPE="text" NAME="ban_ip" VALUE="" SIZE="40" MAXLENGTH="40"><P>
Name:<INPUT TYPE="text" NAME="ban_name" VALUE="" SIZE="20" MAXLENGTH="20"><P>
Reason:<INPUT TYPE="text" NAME="ban_reason" VALUE="" SIZE="40" MAXLENGTH="255"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<A NAME="unbannedchars"><H1>Unbanned Characters</H1>
<?php
$result = mysql_query("select * from mm_unbantable order by name"
	, $dbhandle)
	or error_message("Query(5) failed : " . mysql_error());
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

?>
Add Unbanname:<FORM METHOD="GET" ACTION="/scripts/admin_banned.php">
<INPUT TYPE="text" NAME="unbanname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<A NAME="bannedchars"><H1>Banned Characters</H1>
<TABLE><TR><TD><B>Name</B></TD><TD><B>Deputy</B></TD><TD><B>Creation
</B></TD><TD><B>Days</B></TD><TD><B>Reason</B></TD></TR><TR><TD>
<?php
$result = mysql_query("select * from mm_bannednamestable order by name"
	, $dbhandle)
	or error_message("Query(7) failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<A HREF=\"/scripts/admin_banned.php?remove_bannedname=%s\">%s</A><BR>",
		$myrow["name"], $myrow["name"]);
	printf("</TD><TD>%s</TD><TD>%s</TD><TD>%s</TD><TD>%s",
		$myrow["deputy"], $myrow["creation"], $myrow["days"],
		$myrow["reason"]);
	printf("</TD></TR><TR><TD>");
}
?>
</TD></TR></TABLE>

Add Banned name:<FORM METHOD="GET" ACTION="/scripts/admin_banned.php">
<INPUT TYPE="text" NAME="add_bannedname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
Days:<INPUT TYPE="text" NAME="add_bandays" VALUE="" SIZE="3" MAXLENGTH="3"><P>
Reason:<INPUT TYPE="text" NAME="add_banreason" VALUE="" SIZE="40" MAXLENGTH="255"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<A NAME="sillynames"><H1>Banned Silly Names</H1>

<?php
$result = mysql_query("select * from mm_sillynamestable order by name"
	, $dbhandle)
	or error_message("Query(7) failed : " . mysql_error());
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


Add Sillyname:<FORM METHOD="GET" ACTION="/scripts/admin_banned.php">
<INPUT TYPE="text" NAME="sillyname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>


</BODY>
</HTML>

