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
Char <?php echo $_REQUEST{"char"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
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
- all information filled out correctly?
- does area exist?
todo:
- update room
- update owner
- insert into log

*/
if (isset($_REQUEST{"race"}))
{
	// check it.
	$result = mysql_query("select name from mm_usertable where name = \"".
		mysql_escape_string($_REQUEST{"char"}).
		"\" and (owner is null or owner = \"".   
		mysql_escape_string($_COOKIE["karchanadminname"]).
		"\")"
		, $dbhandle)
		or die("Query(1) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		die("You are not the owner of this character.");
	}
	$result = mysql_query("select id from mm_rooms where id = \"".
		mysql_escape_string($_REQUEST{"room"})."\""
		, $dbhandle)
		or die("Query(2) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		die("Room does not exist.");
	}
	// make that change.
	$query = "update mm_usertable set race=\"".
		mysql_escape_string($_REQUEST{"race"}).
		"\", sex=\"".
		mysql_escape_string($_REQUEST{"sex"}).
		"\", age=\"".
		mysql_escape_string($_REQUEST{"age"}).
		"\", length=\"".
		mysql_escape_string($_REQUEST{"length"}).
		"\", width=\"".
		mysql_escape_string($_REQUEST{"width"}).
		"\", complexion=\"".
		mysql_escape_string($_REQUEST{"complexion"}).
		"\", eyes=\"".
		mysql_escape_string($_REQUEST{"eyes"}).
		"\", face=\"".
		mysql_escape_string($_REQUEST{"face"}).
		"\", hair=\"".
		mysql_escape_string($_REQUEST{"hair"}).
		"\", beard=\"".
		mysql_escape_string($_REQUEST{"beard"}).
		"\", arm=\"".
		mysql_escape_string($_REQUEST{"arm"}).
		"\", leg=\"".
		mysql_escape_string($_REQUEST{"leg"}).
		"\", room=\"".
		mysql_escape_string($_REQUEST{"room"}).
		"\", experience=\"".
		mysql_escape_string($_REQUEST{"experience"}).
		"\", god=\"".
		mysql_escape_string($_REQUEST{"god"}).
		"\", active=\"".
		mysql_escape_string($_REQUEST{"active"}).
		"\", owner=\"".
		mysql_escape_string($_COOKIE["karchanadminname"]).
		"\" where name = \"".
		mysql_escape_string($_REQUEST{"char"}).
		"\"";
	mysql_query($query
		, $dbhandle)  
		or die("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Changed character ".$_REQUEST{"char"}.".", $query);
}

$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as
	creation2 from mm_usertable where name =
	\"".mysql_escape_string($_REQUEST{"char"})."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> %s<BR>", $myrow["name"]);
	printf("<b>address:</b> %s<BR>", $myrow["address"]);
	printf("<b>password:</b> %s<BR>", $myrow["password"]);
	printf("<b>title:</b> %s<BR>", $myrow["title"]);
	printf("<b>realname:</b> %s<BR>", $myrow["realname"]);
	printf("<b>email:</b> %s<BR>", $myrow["email"]);
	printf("<b>race:</b> %s<BR>", $myrow["race"]);
	printf("<b>sex:</b> %s<BR>", $myrow["sex"]);
	printf("<b>age:</b> %s<BR>", $myrow["age"]);
	printf("<b>length:</b> %s<BR>", $myrow["length"]);
	printf("<b>width:</b> %s<BR>", $myrow["width"]);
	printf("<b>complexion:</b> %s<BR>", $myrow["complexion"]);
	printf("<b>eyes:</b> %s<BR>", $myrow["eyes"]);
	printf("<b>face:</b> %s<BR>", $myrow["face"]);
	printf("<b>hair:</b> %s<BR>", $myrow["hair"]);
	printf("<b>beard:</b> %s<BR>", $myrow["beard"]);
	printf("<b>arm:</b> %s<BR>", $myrow["arm"]);
	printf("<b>leg:</b> %s<BR>", $myrow["leg"]);
	printf("<b>lok:</b> %s<BR>", $myrow["lok"]);
	printf("<b>whimpy:</b> %s<BR>", $myrow["whimpy"]);
	printf("<b>sleep:</b> %s<BR>", $myrow["sleep"]);
	printf("<b>god:</b> %s<BR>", $myrow["god"]);
	printf("<b>active:</b> %s<BR>", $myrow["active"]);
	printf("<b>lastlogin:</b> %s<BR>", $myrow["lastlogin"]);
	printf("<b>birth:</b> %s<BR>", $myrow["birth"]);
	printf("<b>Creation:</b> %s<BR>", $myrow["creation2"]);
	printf("<b>Owner:</b> %s<BR>", $myrow["owner"]);
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow[21], $myrow[21]);
	if ($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"])
	{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_chars.php">
<b>
<INPUT TYPE="hidden" NAME="char" VALUE="<?php echo $myrow["name"] ?>">
<TABLE>
<TR><TD>race</TD><TD><INPUT TYPE="text" NAME="race" VALUE="<?php echo $myrow["race"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>   
<TR><TD>sex</TD><TD><INPUT TYPE="text" NAME="sex" VALUE="<?php echo $myrow["sex"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>   
<TR><TD>age</TD><TD><INPUT TYPE="text" NAME="age" VALUE="<?php echo $myrow["age"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>length</TD><TD><INPUT TYPE="text" NAME="length" VALUE="<?php echo $myrow["length"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>width</TD><TD><INPUT TYPE="text" NAME="width" VALUE="<?php echo $myrow["width"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>complexion</TD><TD><INPUT TYPE="text" NAME="complexion" VALUE="<?php echo $myrow["complexion"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>eyes</TD><TD><INPUT TYPE="text" NAME="eyes" VALUE="<?php echo $myrow["eyes"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>face</TD><TD><INPUT TYPE="text" NAME="face" VALUE="<?php echo $myrow["face"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>hair</TD><TD><INPUT TYPE="text" NAME="hair" VALUE="<?php echo $myrow["hair"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>beard</TD><TD><INPUT TYPE="text" NAME="beard" VALUE="<?php echo $myrow["beard"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>arm</TD><TD><INPUT TYPE="text" NAME="arm" VALUE="<?php echo $myrow["arm"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>leg</TD><TD><INPUT TYPE="text" NAME="leg" VALUE="<?php echo $myrow["leg"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>room</TD><TD><INPUT TYPE="text" NAME="room" VALUE="<?php echo $myrow["room"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>experience</TD><TD><INPUT TYPE="text" NAME="experience" VALUE="<?php echo $myrow["experience"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>god</TD><TD><INPUT TYPE="text" NAME="god" VALUE="<?php echo $myrow["god"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>active</TD><TD><INPUT TYPE="text" NAME="active" VALUE="<?php echo $myrow["active"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Character">
</b>   
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_resetcharpasswd.php">
<INPUT TYPE="hidden" NAME="char" VALUE="<?php echo $myrow["name"] ?>">
<INPUT TYPE="submit" VALUE="Reset Character Password">
</FORM>
<?php
	}
}

printf("<P>");

$result = mysql_query("select * ".
	" from mm_charattributes".
	" where charname = \"".mysql_escape_string($_REQUEST{"char"})."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_attributelist.php?name=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>value:</b> %s ", $myrow[1]);
	printf("<b>value_type:</b> %s<BR>", $myrow[2]);
}

$result = mysql_query("select * ".
	" from characterinfo".
	" where name = \"".mysql_escape_string($_REQUEST{"char"})."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>imageurl:</b> %s<BR>", $myrow["imageurl"]);
	printf("<b>homepageurl:</b> %s<BR>", $myrow["homepageurl"]);
	printf("<b>dateofbirth:</b> %s<BR>", $myrow["dateofbirth"]);
	printf("<b>cityofbirth:</b> %s<BR>", $myrow["cityofbirth"]);
	printf("<b>storyline:</b> %s<BR>", $myrow["storyline"]);
}

printf("<P>");
$result = mysql_query("select mm_charitemtable.id, mm_items.id, ".
	" mm_items.adject1, mm_items.adject2, mm_items.adject3, mm_items.name from mm_items, mm_itemtable, mm_charitemtable".
	" where mm_itemtable.id = mm_charitemtable.id and ".
	" mm_items.id = mm_itemtable.itemid and ".
	" mm_charitemtable.belongsto =
	\"".mysql_escape_string($_REQUEST{"char"})."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>itemid:</b> <A HREF=\"/scripts/admin_itemdefs.php?item=%s\">%s</A> ", $myrow[1], $myrow[1]);
	printf("<b>description:</b> %s %s %s %s<BR>", $myrow[2], $myrow[3], $myrow[4], $myrow[5]);
}
mysql_close($dbhandle);
?>

<a HREF="/karchan/admin/chars.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
