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
Commands, Events and Methods</H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

$result = mysql_query("select date_format(now(), \"%Y-%m-%d %T\") as now"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
$myrow = mysql_fetch_array($result);
printf("<H2>Current date/time</H2>".$myrow["now"]."<P>");
?>
<H2><A HREF="/karchan/admin/help/scripting1.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A>
Events</H2>
<?php
$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2 
	from mm_events"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>eventid:</b> %s ", $myrow["eventid"]);
	if ($myrow["room"] != null)
	{
		printf("<b>room:</b> <A
        HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A> ",
        $myrow["room"], $myrow["room"]);
	}
	if ($myrow["name"] != null)
	{
		printf("<b>name:</b> <A
    HREF=\"/scripts/admin_chars.php?char=%s\">%s</A> ", $myrow["name"],
    $myrow["name"]);
	}
	if ($myrow["month"] != "-1")
	{
		printf("<b>month:</b> %s ", $myrow["month"]);
	}
	if ($myrow["dayofmonth"] != "-1")
	{
		printf("<b>dayofmonth:</b> %s ", $myrow["dayofmonth"]);
	}
	if ($myrow["hour"] != "-1")
	{
		printf("<b>hour:</b> %s ", $myrow["hour"]);
	}
	if ($myrow["minute"] != "-1")
	{
		printf("<b>minute:</b> %s ", $myrow["minute"]);
	}
	if ($myrow["dayofweek"] != "-1")
	{
		printf("<b>dayofweek:</b> %s ", $myrow["dayofweek"]);
	}
	printf("<b>callable:</b> %s ", ($myrow["callable"]=="1"?"yes":"no"));
	printf("<b>method_name:</b> <A
HREF=\"/scripts/admin_commandseventsmethods.php?methodname=%s\">%s</A> ", $myrow["method_name"], $myrow["method_name"]);
	printf("<b>owner:</b> %s ", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	if ( ($_REQUEST{"eventid"} == $myrow["eventid"]) &&
		($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"]) )
	{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_rooms.php">
<b>
<INPUT TYPE="hidden" NAME="room" VALUE="<?php echo $myrow["eventid"] ?>">
<TABLE>
<TR><TD>west</TD><TD><INPUT TYPE="text" NAME="west" VALUE="<?php echo $myrow["west"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>east</TD><TD><INPUT TYPE="text" NAME="east" VALUE="<?php echo $myrow["east"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>north</TD><TD><INPUT TYPE="text" NAME="north" VALUE="<?php echo $myrow["north"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>south</TD><TD><INPUT TYPE="text" NAME="south" VALUE="<?php echo $myrow["south"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>up</TD><TD><INPUT TYPE="text" NAME="up" VALUE="<?php echo $myrow["up"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>down</TD><TD><INPUT TYPE="text" NAME="down" VALUE="<?php echo $myrow["down"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>contents</TD><TD><TEXTAREA NAME="contents" ROWS="14" COLS="85"><?php echo $myrow["contents"] ?></TEXTAREA></TD></TR>
<TR><TD>area</TD><TD><SELECT NAME="method_name">
<?php
$arearesult = mysql_query("select name from mm_methods"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($namemyrow = mysql_fetch_array($arearesult)) 
{
	printf("<option %s value=\"%s\">%s",
		($namemyrow["name"] == $myrow["method_name"] ? "selected" : ""),
		$namemyrow["name"], $namemyrow["name"]);
}
?>
</SELECT></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Event">
</b>
</FORM>

<?php
	}
}
?>

<H2><A HREF="/karchan/admin/help/scripting2.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A>
Commands</H2>

<?php
$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2 
	from mm_commands"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> %s ", $myrow["id"]);
	printf("<b>callable:</b> %s ", ($myrow["callable"]=="1"?"yes":"no"));
	printf("<b>command:</b> %s ", $myrow["command"]);
	printf("<b>method_name:</b> <A
HREF=\"/scripts/admin_commandseventsmethods.php?methodname=%s\">%s</A> ", $myrow["method_name"], $myrow["method_name"]);
	if ($myrow["room"] != null)
	{
		printf("<b>room:</b> <A
        HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A> ",
        $myrow["room"], $myrow["room"]);
	}
	printf("<b>owner:</b> %s ", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	if ( ($_REQUEST{"commandid"} == $myrow["id"]) &&
		($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"]) )
	{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_rooms.php">
<b>
<INPUT TYPE="hidden" NAME="room" VALUE="<?php echo $myrow["commandid"] ?>">
<TABLE>
<TR><TD>west</TD><TD><INPUT TYPE="text" NAME="west" VALUE="<?php echo $myrow["west"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>east</TD><TD><INPUT TYPE="text" NAME="east" VALUE="<?php echo $myrow["east"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>north</TD><TD><INPUT TYPE="text" NAME="north" VALUE="<?php echo $myrow["north"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>south</TD><TD><INPUT TYPE="text" NAME="south" VALUE="<?php echo $myrow["south"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>up</TD><TD><INPUT TYPE="text" NAME="up" VALUE="<?php echo $myrow["up"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>down</TD><TD><INPUT TYPE="text" NAME="down" VALUE="<?php echo $myrow["down"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>contents</TD><TD><TEXTAREA NAME="contents" ROWS="14" COLS="85"><?php echo $myrow["contents"] ?></TEXTAREA></TD></TR>
<TR><TD>area</TD><TD><SELECT NAME="method_name">
<?php
$arearesult = mysql_query("select name from mm_methods"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($namemyrow = mysql_fetch_array($arearesult)) 
{
	printf("<option %s value=\"%s\">%s",
		($namemyrow["name"] == $myrow["method_name"] ? "selected" : ""),
		$namemyrow["name"], $namemyrow["name"]);
}
?>
</SELECT></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Command">
</b>
</FORM>
<?php
	}
}
?>

<H2><A HREF="/karchan/admin/help/scripting3.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A>
Methods</H2>

<?php

if (isset($_REQUEST{"methodname"}) && isset($_REQUEST{"src"}))
{
	// make that change
	$query = "update mm_methods set src=\"".
		mysql_escape_string($_REQUEST{"src"}).
		"\" where name = \"".
		mysql_escape_string($_REQUEST{"methodname"}).
		"\"";
    mysql_query($query
        , $dbhandle)
        or die("Query(8) failed : " . mysql_error());
    writeLogLong($dbhandle, "Changed method ".$_REQUEST{"methodname"}.".", $query);
}

$result = mysql_query("select *, 
replace(replace(replace(src, \"&\", \"&amp;\"), \">\",\"&gt;\"), \"<\", \"&lt;\") 
as src2, date_format(creation, \"%Y-%m-%d %T\") as creation2 
	from mm_methods"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A
HREF=\"/scripts/admin_commandseventsmethods.php?methodname=%s\">%s</A> ",
	$myrow["name"], $myrow["name"]);
	printf("<b>owner:</b> %s ", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	if ($_REQUEST{"methodname"} == $myrow["name"])
	{
		printf("<b>src:</b> <PRE>%s</PRE>", $myrow["src2"]);
	}
	if ( ($_REQUEST{"methodname"} == $myrow["name"]) &&
		($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"]) )
	{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_commandseventsmethods.php">
<I>(Refresh the command list in the game, after changing a method.)</I><BR>
<b>
<INPUT TYPE="hidden" NAME="methodname" VALUE="<?php echo $myrow["name"] ?>">
<TABLE>
<TR><TD>src</TD><TD><TEXTAREA NAME="src" ROWS="14" COLS="85"><?php echo
$myrow["src2"] ?></TEXTAREA></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Method">
</b>
</FORM>
<?php
	}
}

mysql_close($dbhandle);
?>
<P>
<a HREF="/karchan/admin/rooms.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
