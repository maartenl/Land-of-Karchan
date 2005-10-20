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
Room <?php echo $_REQUEST{"room"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

/* the following constraints need to be checked before any kind of update is
to take place:

changing room:
- first check that the change is approved (i.e. owner or null)
- does room south exist
- does room north exist
- does room east exist
- does room west exist
- does room up exist
- does room down exist
- does area exist
deleting room:
- first check that the change is approved (i.e. owner or null)
- check persons in room
- check items in room
- check references to this room in other rooms
adding room:
- does area exist?
- is area owned by the current admin guy?
todo:
- update room
- update owner
- insert into log

*/
if (isset($_REQUEST{"addroom_area"}))
{
	// check the area for admin rights.
	$result = mysql_query("select area from mm_area where area = \"".
		quote_smart($_REQUEST{"addroom_area"}).
		"\" and owner = \"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\""
		, $dbhandle)
		or die("Query(7) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		die("Area does not exist or you are not the owner.");
	}
	$result = mysql_query("select max(id)+1 as maxid from mm_rooms"
		, $dbhandle)
		or die("Query(8) failed : " . mysql_error());
	// get the new room number.
	$roomid = 0;
	while ($myrow = mysql_fetch_array($result)) 
	{
		$roomid = $myrow["maxid"];
	}
	// make that change.
	$query = "insert into mm_rooms (id, area, owner, creation) values(".
		$roomid.
		", \"".
		quote_smart($_REQUEST{"addroom_area"}).
		"\", \"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\", now())";
	mysql_query($query
		, $dbhandle)
		or die("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Added room ".$roomid.".", $query);
	$_REQUEST{"room"} = $roomid;
}
if (isset($_REQUEST{"west"}))
{
	// check it.
	$result = mysql_query("select id from mm_rooms where id = ".
		quote_smart($_REQUEST{"room"}).
		" and (owner is null or owner = \"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\")"
		, $dbhandle)
		or die("Query(1) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		die("You are not the owner of this room.");
	}
	$south = trim($_REQUEST{"south"});
	$north = trim($_REQUEST{"north"});
	$west = trim($_REQUEST{"west"});
	$east = trim($_REQUEST{"east"});
	$up = trim($_REQUEST{"up"});
	$down = trim($_REQUEST{"down"});
	if ($south != "")
	{
		$result = mysql_query("select id from mm_rooms where id = ".
			quote_smart($south)
			, $dbhandle)
			or die("Query failed : " . mysql_error());
		if (mysql_num_rows($result) != 1)
		{
			die("South exit does not exist.");
		}
	}
	else
	{
		$south = "null";
	}
	if ($north != "")
	{
		$result = mysql_query("select id from mm_rooms where id = ".
			quote_smart($north)
			, $dbhandle)
			or die("Query(2) failed : " . mysql_error());
		if (mysql_num_rows($result) != 1)
		{
			die("North exit does not exist.");
		}
	}
	else
	{
		$north = "null";
	}
	if ($west != "")
	{
		$result = mysql_query("select id from mm_rooms where id = ".
			quote_smart($west)
			, $dbhandle)
			or die("Query(3) failed : " . mysql_error());
		if (mysql_num_rows($result) != 1)
		{
			die("West exit does not exist.");
		}
	}
	else
	{
		$west = "null";
	}
	if ($east != "")
	{
		$result = mysql_query("select id from mm_rooms where id = ".
			quote_smart($east)
			, $dbhandle)
			or die("Query(4) failed : " . mysql_error());
		if (mysql_num_rows($result) != 1)
		{
			die("East exit does not exist.");
		}
	}
	else
	{
		$east = "null";
	}
	if ($up != "")
	{
		$result = mysql_query("select id from mm_rooms where id = ".
			quote_smart($up)
			, $dbhandle)
			or die("Query(5) failed : " . mysql_error());
		if (mysql_num_rows($result) != 1)
		{
			die("Up exit does not exist.");
		}
	}
	else
	{
		$up = "null";
	}
	if ($down != "")
	{
		$result = mysql_query("select id from mm_rooms where id = ".
			quote_smart($down)
			, $dbhandle)
			or die("Query(6) failed : " . mysql_error());
		if (mysql_num_rows($result) != 1)
		{
			die("Down exit does not exist.");
		}
	}
	else
	{
		$down = "null";
	}
	$result = mysql_query("select area from mm_area where area = \"".
		quote_smart($_REQUEST{"area"})."\""
		, $dbhandle)
		or die("Query(7) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		die("Area does not exist.");
	}
	// make that change.
	$query = "update mm_rooms set north=".
		quote_smart($north).
		", south=".
		quote_smart($south).
		", east=".
		quote_smart($east).
		", west=".
		quote_smart($west).
		", up=".
		quote_smart($up).
		", down=".
		quote_smart($down).
		", contents=\"".
		quote_smart($_REQUEST{"contents"}).
		"\", area=\"".
		quote_smart($_REQUEST{"area"}).
		"\", owner=\"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\" where id = ".
		quote_smart($_REQUEST{"room"});
	mysql_query($query
		, $dbhandle)
		or die("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Changed room ".$_REQUEST{"room"}.".", $query);
}

$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2 from mm_rooms where id =
	".quote_smart($_REQUEST{"room"})
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> %s<BR>", $myrow[0]);
	if ($myrow["west"]<>0)
	{
		printf("<b>west:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow["west"], $myrow["west"]);
	}
	if ($myrow["east"]<>0)
	{
		printf("<b>east:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow["east"], $myrow["east"]);
	}
	if ($myrow["north"]<>0)
	{
		printf("<b>north:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow["north"], $myrow["north"]);
	}
	if ($myrow["south"]<>0)
	{
		printf("<b>south:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow["south"], $myrow["south"]);
	}
	if ($myrow["up"]<>0)
	{
		printf("<b>up:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow["up"], $myrow["up"]);
	}
	if ($myrow["down"]<>0)
	{
		printf("<b>down:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow["down"], $myrow["down"]);
	}
	printf("<b>contents:</b> %s<BR>", $myrow["contents"]);
	printf("<b>owner:</b> %s<BR>", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	printf("<b>area:</b> %s<BR>", $myrow["area"]);
	if ($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"])
	{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_rooms.php">
<b>
<INPUT TYPE="hidden" NAME="room" VALUE="<?php echo $myrow["id"] ?>">
<TABLE>
<TR><TD>west</TD><TD><INPUT TYPE="text" NAME="west" VALUE="<?php echo $myrow["west"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>east</TD><TD><INPUT TYPE="text" NAME="east" VALUE="<?php echo $myrow["east"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>north</TD><TD><INPUT TYPE="text" NAME="north" VALUE="<?php echo $myrow["north"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>south</TD><TD><INPUT TYPE="text" NAME="south" VALUE="<?php echo $myrow["south"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>up</TD><TD><INPUT TYPE="text" NAME="up" VALUE="<?php echo $myrow["up"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>down</TD><TD><INPUT TYPE="text" NAME="down" VALUE="<?php echo $myrow["down"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>contents</TD><TD><TEXTAREA NAME="contents" ROWS="14" COLS="85"><?php echo $myrow["contents"] ?></TEXTAREA></TD></TR>
<TR><TD>area</TD><TD><SELECT NAME="area">
<?php
$arearesult = mysql_query("select area from mm_area"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($areamyrow = mysql_fetch_array($arearesult)) 
{
	printf("<option %s value=\"%s\">%s",
		($areamyrow["area"] == $myrow["area"] ? "selected" : ""),
		$areamyrow["area"], $areamyrow["area"]);
}
?>
</SELECT></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Room">
</b>
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_rooms.php">
<b>
area <SELECT NAME="addroom_area">
<?php
$arearesult = mysql_query("select area from mm_area where owner='".
	quote_smart($_COOKIE["karchanadminname"]).
	"'"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($areamyrow = mysql_fetch_array($arearesult)) 
{
	printf("<option %s value=\"%s\">%s",
		($areamyrow["area"] == $myrow["area"] ? "selected" : ""),
		$areamyrow["area"], $areamyrow["area"]);
}
?>
</SELECT>
<INPUT TYPE="submit" VALUE="Add Room">
</b>
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_ownership.php">
<b>
<INPUT TYPE="hidden" NAME="id" VALUE="<?php echo $myrow["id"] ?>">
<INPUT TYPE="hidden" NAME="removeownership" VALUE="5">
<INPUT TYPE="submit" VALUE="Remove Ownership">
</b>
</FORM>
<?php
	}
}

printf("</P>");
$result = mysql_query("select * ".
	" from mm_roomattributes".
	" where id = ".quote_smart($_REQUEST{"room"})
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_attributelist.php?name=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>value:</b> %s ", $myrow[1]);
	printf("<b>value_type:</b> %s<BR>", $myrow[2]);
}

printf("</P>");
$result = mysql_query("select mm_roomitemtable.id, mm_items.id, ".
	" mm_items.adject1, mm_items.adject2, mm_items.adject3, mm_items.name from mm_items, mm_itemtable, mm_roomitemtable".
	" where mm_itemtable.id = mm_roomitemtable.id and ".
	" mm_items.id = mm_itemtable.itemid and ".
	" mm_roomitemtable.room = ".quote_smart($_REQUEST{"room"})
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>itemid:</b> <A HREF=\"/scripts/admin_itemdefs.php?item=%s\">%s</A> ", $myrow[1], $myrow[1]);
	printf("<b>description:</b> %s %s %s %s<BR>", $myrow[2], $myrow[3], $myrow[4], $myrow[5]);
}
printf("</P>");

$result = mysql_query("select mm_usertable.name ".
	" from mm_usertable".
	" where active = 1 and room  =
".quote_smart($_REQUEST{"room"})
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A><BR> ", $myrow[0], $myrow[0]);
}
printf("</P>");
$result = mysql_query("select mm_usertable.name ".
	" from mm_usertable".
	" where active = 0 and room  =
	".quote_smart($_REQUEST{"room"})
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A><BR> ", $myrow[0], $myrow[0]);
}

printf("<b>Referenced in the following rooms:</b><BR>");
$result = mysql_query("select id from mm_rooms where
	".quote_smart($_REQUEST{"room"}).
	" in (west, east, north, south, up, down) "
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow[0], $myrow[0]);
}


mysql_close($dbhandle);
?>

<a HREF="/karchan/admin/rooms.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
