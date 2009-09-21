<?
/*-------------------------------------------------------------------------
svninfo: $Id: admin_commandseventsmethods.php 1150 2007-02-13 07:37:53Z maartenl $
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
Events</H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

/* the following constraints need to be checked before any kind of update is
to take place:

changing event:
- check that event is owned by owner or is null
- check callable is either 1 or 0
- check that methodname exists
- check that name of character (if filled in) exists
- check that room number (if filled in) exists
- check that numbers are filled in for times and room
adding event:
- check that method exists
deleting event:
- check that correct owner or owner==null
- check that eventid is numeric in format

/* 
------------------------------------------------------------------
EVENTS
------------------------------------------------------------------
*/
if ( (isset($_REQUEST{"eventid"}))
	&& (isset($_REQUEST{"eventcharname"}))
    && isset($_REQUEST{"eventroom"})
    && isset($_REQUEST{"eventmonth"})
    && isset($_REQUEST{"eventdayofmonth"})
    && isset($_REQUEST{"eventhour"})
    && isset($_REQUEST{"eventminute"})
    && isset($_REQUEST{"eventdayofweek"})
    && isset($_REQUEST{"eventmethodname"})
    && isset($_REQUEST{"eventcallable"}) )
{
	// check proper information formats
	if ( !is_numeric($_REQUEST{"eventid"}) ||
         !is_numeric($_REQUEST{"eventmonth"}) ||
         !is_numeric($_REQUEST{"eventdayofmonth"}) ||
         !is_numeric($_REQUEST{"eventhour"}) ||
         !is_numeric($_REQUEST{"eventminute"}) ||
         !is_numeric($_REQUEST{"eventdayofweek"}) )
	{
		error_message("Expected one of the fields to be an integer, and it wasn't.");
	}
	// check that the event is the proper owner.
    // check it.
    $result = mysql_query("select eventid from mm_events where eventid = \"".
        quote_smart($_REQUEST{"eventid"}).  
        "\" and (owner is null or owner = \"".   
        quote_smart($_COOKIE["karchanadminname"]).
        "\")"
        , $dbhandle)
        or error_message("Query(1) failed : " . mysql_error());
    if (mysql_num_rows($result) != 1)
    {
        error_message("You are not the owner of this event.");
    }
	// check to see that method exists
	$result = mysql_query("select 1 from mm_methods where name = \"".
		quote_smart($_REQUEST{"eventmethodname"}).
		"\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		error_message("Method does not exist.");
	}
    if ( ($_REQUEST{"eventcallable"} != "1") &&
        ($_REQUEST{"eventcallable"} != "0") )
    {
        error_message("Callable should be either 0 or 1.");
    }
	// check to see that character exists
	if (trim($_REQUEST{"eventcharname"}) != "")
	{
		$result = mysql_query("select 1 from mm_usertable where name = \"".
			quote_smart($_REQUEST{"eventcharname"}).
			"\""
			, $dbhandle)
			or error_message("Query failed : " . mysql_error());
		if (mysql_num_rows($result) != 1)
		{
			error_message("Character ".$_REQUEST{"eventcharname"}." does not exist.");
		}
	}
	// check to see that room exists
	if (trim($_REQUEST{"eventroom"}) != "")
	{
		$result = mysql_query("select 1 from mm_rooms where id = \"".
			quote_smart($_REQUEST{"eventroom"}).
			"\""
			, $dbhandle)
			or error_message("Query failed : " . mysql_error());
		if (mysql_num_rows($result) != 1)
		{
			error_message("Room ".$_REQUEST{"eventroom"}." does not exist.");
		}
	}
	// make that change
	$query = "update mm_events set name=".
		(trim($_REQUEST{"eventcharname"}) != "" ?
			"\"".quote_smart($_REQUEST{"eventcharname"})."\"":
			"null").
		", month=\"".
		quote_smart($_REQUEST{"eventmonth"}).
		"\", dayofmonth=\"".
		quote_smart($_REQUEST{"eventdayofmonth"}).
		"\", hour=\"".
		quote_smart($_REQUEST{"eventhour"}).
		"\", minute=\"".
		quote_smart($_REQUEST{"eventminute"}).
		"\", dayofweek=\"".
		quote_smart($_REQUEST{"eventdayofweek"}).
		"\", callable=\"".
		quote_smart($_REQUEST{"eventcallable"}).
		"\", method_name=\"".
		quote_smart($_REQUEST{"eventmethodname"}).
		"\", room=".
		(trim($_REQUEST{"eventroom"}) != "" ?
			"\"".quote_smart($_REQUEST{"eventroom"})."\"":
			"null").
		", owner=\"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\" where eventid = \"".
		quote_smart($_REQUEST{"eventid"}).
		"\"";
    mysql_query($query
        , $dbhandle)
        or error_message("Query(8) failed : " . mysql_error());
    writeLogLong($dbhandle, "Changed event ".$_REQUEST{"eventid"}.
		".", $query);
}
if (isset($_REQUEST{"addeventmethodname"}))
{
	// check to see that method exists
	$result = mysql_query("select 1 from mm_methods where name = \"".
		quote_smart($_REQUEST{"addeventmethodname"}).
		"\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		error_message("Method does not exist.");
	}
	// create new id
	$result = mysql_query("select max(eventid) as id from mm_events"
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	$maxid = 0;
	while ($myrow = mysql_fetch_array($result)) 
	{
		$maxid = $myrow["id"];
	}
	if ($maxid == 0)
	{
		error_message("Could not compute id for event.");
	}
	
	// make that change
	$query = "insert into mm_events (eventid, method_name, owner) values(".
		($maxid + 1).
		", \"".
		quote_smart($_REQUEST{"addeventmethodname"}).
		"\", \"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\")";
    mysql_query($query
        , $dbhandle)
        or error_message("Query(8) failed : " . mysql_error());
    writeLogLong($dbhandle, "Added event ".($maxid + 1).
		" which uses method ".$_REQUEST{"addeventmethodname"}.".", $query);
}
if (isset($_REQUEST{"deleteeventid"}))
{
	if ( !is_numeric($_REQUEST{"deleteeventid"}))
	{
		error_message("Expected eventid to be an integer, and it wasn't.");
	}
        $query = "delete from mm_events where eventid = ".
		quote_smart($_REQUEST{"deleteeventid"}).
		" and (owner is null or owner = \"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\")";
	mysql_query($query, $dbhandle)
		or error_message("Query (".$query.") failed : " . mysql_error());
	if (mysql_affected_rows() != 1)
	{
		error_message("Event does not exist or not proper owner.");
	}
    writeLogLong($dbhandle, "Removed event ".$_REQUEST{"deleteeventid"}.".", $query);
}

$result = mysql_query("select date_format(now(), \"%Y-%m-%d %T\") as now"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
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
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	if ( ($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"]) )
	{
		printf("<b>eventid:</b> <A
    	   HREF=\"/scripts/admin_events.php?eventid=%s\">%s</A> ",
	       $myrow["eventid"], $myrow["eventid"]);
	}
	else
	{
		printf("<b>eventid:</b> %s ",
	       $myrow["eventid"]);
	}
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
HREF=\"/scripts/admin_methods.php?methodname=%s\">%s</A> ", $myrow["method_name"], $myrow["method_name"]);
	printf("<b>owner:</b> %s ", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	if ( ($_REQUEST{"eventid"} == $myrow["eventid"]) &&
		($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"]) )
	{
?>
<FORM METHOD="POST" ACTION="/scripts/admin_events.php">
<b>
<INPUT TYPE="hidden" NAME="deleteeventid" VALUE="<?php echo $myrow["eventid"] ?>">
<INPUT TYPE="submit" VALUE="Delete Event">
</b>
</FORM>
<FORM METHOD="POST" ACTION="/scripts/admin_events.php">
<b>
<INPUT TYPE="hidden" NAME="eventid" VALUE="<?php echo $myrow["eventid"] ?>">
<TABLE>
<TR><TD>character name:</TD><TD><INPUT TYPE="text" NAME="eventcharname" VALUE="<?php echo $myrow["name"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>room:</TD><TD><INPUT TYPE="text" NAME="eventroom" VALUE="<?php echo $myrow["room"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>month:</TD><TD><INPUT TYPE="text" NAME="eventmonth" VALUE="<?php echo $myrow["month"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>dayofmonth:</TD><TD><INPUT TYPE="text" NAME="eventdayofmonth" VALUE="<?php echo $myrow["dayofmonth"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>hour:</TD><TD><INPUT TYPE="text" NAME="eventhour" VALUE="<?php echo $myrow["hour"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>minute:</TD><TD><INPUT TYPE="text" NAME="eventminute" VALUE="<?php echo $myrow["minute"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>dayofweek:</TD><TD><INPUT TYPE="text" NAME="eventdayofweek" VALUE="<?php echo $myrow["dayofweek"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>method_name:</TD><TD><INPUT TYPE="text" NAME="eventmethodname" VALUE="<?php echo $myrow["method_name"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>callable</TD><TD><SELECT NAME="eventcallable" SIZE="2">
<option value="1" <?php if ($myrow["callable"] == "1") {printf("selected");} ?>>yes
<option value="0" <?php if ($myrow["callable"] == "0") {printf("selected");} ?>>no
</SELECT></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Event">
</b>
</FORM>

<FORM METHOD="GET" ACTION="/scripts/admin_ownership.php">
<b>
<INPUT TYPE="hidden" NAME="id" VALUE="<?php echo $myrow["eventid"] ?>">
<INPUT TYPE="hidden" NAME="removeownership" VALUE="3">
<INPUT TYPE="submit" VALUE="Remove Ownership">
</b>
</FORM>

<?php
	}
}
?>
<FORM METHOD="POST" ACTION="/scripts/admin_events.php">
<b>
Method name: <INPUT TYPE="text" NAME="addeventmethodname" VALUE="">
<INPUT TYPE="submit" VALUE="Add Event">
</b>
</FORM>
<P>

<?php
mysql_close($dbhandle);
?>

<a HREF="/karchan/admin/scripts.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
