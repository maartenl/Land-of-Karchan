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
Char <?php echo $_REQUEST{"char"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

/**
 * verify form information
 */
if (!isset($_REQUEST{"char"}))
{
    die("Form information missing.");
}

/* the following constraints need to be checked before any kind of update is
to take place:

changing character:
- first check that the change is approved (i.e. owner or null)
- check that sex is male or female
- does room exist
- check if sex is correct
deleting character:   
- checking attributes
- checking items (mm_charitemtable)
- checking mail
- checking family/characterinfo
adding character:
*/
if (isset($_REQUEST{"race"}))
{
	// check it.
	$result = mysql_query("select name from mm_usertable where name = \"".
		quote_smart($_REQUEST{"char"}).
		"\" and (owner is null or owner = \"".   
		quote_smart($_COOKIE["karchanadminname"]).
		"\")"
		, $dbhandle)
		or die("Query(1) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		die("You are not the owner of this character.");
	}
	if ( ($_REQUEST{"sex"} != "female") &&
		($_REQUEST{"sex"} != "male") )
	{
		die("Gender should be either male or female.");
	}
	$result = mysql_query("select id from mm_rooms where id = \"".
		quote_smart($_REQUEST{"room"})."\""
		, $dbhandle)
		or die("Query(2) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		die("Room does not exist.");
	}
	// make that change.
	$query = "update mm_usertable set race=\"".
		quote_smart($_REQUEST{"race"}).
		"\", sex=\"".
		quote_smart($_REQUEST{"sex"}).
		"\", age=\"".
		quote_smart($_REQUEST{"age"}).
		"\", length=\"".
		quote_smart($_REQUEST{"length"}).
		"\", width=\"".
		quote_smart($_REQUEST{"width"}).
		"\", complexion=\"".
		quote_smart($_REQUEST{"complexion"}).
		"\", eyes=\"".
		quote_smart($_REQUEST{"eyes"}).
		"\", face=\"".
		quote_smart($_REQUEST{"face"}).
		"\", hair=\"".
		quote_smart($_REQUEST{"hair"}).
		"\", beard=\"".
		quote_smart($_REQUEST{"beard"}).
		"\", arm=\"".
		quote_smart($_REQUEST{"arm"}).
		"\", leg=\"".
		quote_smart($_REQUEST{"leg"}).
		"\", room=\"".
		quote_smart($_REQUEST{"room"}).
		"\", experience=\"".
		quote_smart($_REQUEST{"experience"}).
		"\", god=\"".
		quote_smart($_REQUEST{"god"}).
		"\", active=\"".
		quote_smart($_REQUEST{"active"}).
		"\", owner=\"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\" where name = \"".
		quote_smart($_REQUEST{"char"}).
		"\"";
	mysql_query($query
		, $dbhandle)  
		or die("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Changed character ".$_REQUEST{"char"}.".", $query);
}

$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as
	creation2 from mm_usertable where name =
	\"".quote_smart($_REQUEST{"char"})."\""
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
	$god = "unknown";
	if ($myrow["god"] == "0") {$god = "normal";}
	if ($myrow["god"] == "1") {$god = "deputy";}
	if ($myrow["god"] == "2") {$god = "bot";}
	if ($myrow["god"] == "3") {$god = "mob";}
	printf("<b>god:</b> %s<BR>", $god);
	printf("<b>active:</b> %s<BR>", $myrow["active"]);
	printf("<b>lastlogin:</b> %s<BR>", $myrow["lastlogin"]);
	printf("<b>birth:</b> %s<BR>", $myrow["birth"]);
	printf("<b>Creation:</b> %s<BR>", $myrow["creation2"]);
	printf("<b>Owner:</b> %s<BR>", $myrow["owner"]);
	printf("<b>room:</b> <A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A><BR>", $myrow[21], $myrow[21]);
	$owner = false;
	if ($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"])
	{
		$owner = true;
?>
<FORM METHOD="GET" ACTION="/scripts/admin_chars.php">
<b>
<INPUT TYPE="hidden" NAME="char" VALUE="<?php echo $myrow["name"] ?>">
<TABLE>
<TR><TD>race</TD><TD><SELECT NAME="race" SIZE="20"> 
<OPTION VALUE="fox" <?php if ($myrow["race"] == "fox") {printf("selected");} ?>>fox
<OPTION VALUE="zombie" <?php if ($myrow["race"] == "zombie") {printf("selected");} ?>>zombie
<OPTION VALUE="wyvern" <?php if ($myrow["race"] == "wyvern") {printf("selected");} ?>>wyvern
<OPTION VALUE="wolf" <?php if ($myrow["race"] == "wolf") {printf("selected");} ?>>wolf
<OPTION VALUE="turtle" <?php if ($myrow["race"] == "turtle") {printf("selected");} ?>>turtle
<OPTION VALUE="troll" <?php if ($myrow["race"] == "troll") {printf("selected");} ?>>troll
<OPTION VALUE="spider" <?php if ($myrow["race"] == "spider") {printf("selected");} ?>>spider
<OPTION VALUE="slug" <?php if ($myrow["race"] == "slug") {printf("selected");} ?>>slug
<OPTION VALUE="ropegnaw" <?php if ($myrow["race"] == "ropegnaw") {printf("selected");} ?>>ropegnaw
<OPTION VALUE="rabbit" <?php if ($myrow["race"] == "rabbit") {printf("selected");} ?>>rabbit
<OPTION VALUE="orc" <?php if ($myrow["race"] == "orc") {printf("selected");} ?>>orc
<OPTION VALUE="ooze" <?php if ($myrow["race"] == "ooze") {printf("selected");} ?>>ooze
<OPTION VALUE="human" <?php if ($myrow["race"] == "human") {printf("selected");} ?>>human
<OPTION VALUE="elf" <?php if ($myrow["race"] == "elf") {printf("selected");} ?>>elf
<OPTION VALUE="dwarf" <?php if ($myrow["race"] == "dwarf") {printf("selected");} ?>>dwarf
<OPTION VALUE="duck" <?php if ($myrow["race"] == "duck") {printf("selected");} ?>>duck
<OPTION VALUE="deity" <?php if ($myrow["race"] == "deity") {printf("selected");} ?>>deity
<OPTION VALUE="chipmunk" <?php if ($myrow["race"] == "chipmunk") {printf("selected");} ?>>chipmunk
<OPTION VALUE="buggie" <?php if ($myrow["race"] == "buggie") {printf("selected");} ?>>buggie
<OPTION VALUE="dragon" <?php if ($myrow["race"] == "dragon") {printf("selected");} ?>>dragon
</SELECT></TD></TR>   
<TR><TD>sex</TD><TD><SELECT NAME="sex" SIZE="2"> 
<OPTION VALUE="male" <?php if ($myrow["sex"] == "male") {printf("selected");} ?>>male
<OPTION VALUE="female" <?php if ($myrow["sex"] == "female") {printf("selected");} ?>>female
</SELECT></TD></TR>   
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
<TR><TD>god</TD><TD><SELECT NAME="god" SIZE="4"> 
<OPTION VALUE="0" <?php if ($myrow["god"] == "0") {printf("selected");} ?>>normal
<OPTION VALUE="1" <?php if ($myrow["god"] == "1") {printf("selected");} ?>>deputy
<OPTION VALUE="2" <?php if ($myrow["god"] == "2") {printf("selected");} ?>>bot
<OPTION VALUE="3" <?php if ($myrow["god"] == "3") {printf("selected");} ?>>mob
</SELECT></TD></TR>
<TR><TD>active</TD><TD><INPUT TYPE="text" NAME="active" VALUE="<?php echo $myrow["active"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Character">
</b>   
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_resetcharpasswd.php">
<INPUT TYPE="hidden" NAME="char" VALUE="<?php echo $myrow["name"] ?>">
New password:
<INPUT TYPE="text" NAME="newpassword" SIZE="40" MAXLENGTH="40">
<INPUT TYPE="submit" VALUE="Reset Password">
</FORM>

<FORM METHOD="GET" ACTION="/scripts/admin_ownership.php">
<b>
<INPUT TYPE="hidden" NAME="id" VALUE="<?php echo $myrow["name"] ?>">
<INPUT TYPE="hidden" NAME="removeownership" VALUE="7">
<INPUT TYPE="submit" VALUE="Remove Ownership">
</b>
</FORM>
<?php
	}
}

printf("<H2><A HREF=\"/karchan/admin/help/attributes.html\" target=\"_blank\">
<IMG SRC=\"/images/icons/9pt4a.gif\" BORDER=\"0\"></A>Attributes</H2>");

if (isset($_REQUEST{"char"}) &&
	isset($_REQUEST{"mm_charattributes_name"}) &&
	isset($_REQUEST{"mm_charattributes_value"}) &&
	isset($_REQUEST{"mm_charattributes_value_type"}) &&
	$owner)
{
	$query = "replace into mm_charattributes 
		(name, value, value_type, charname) values(\""
		.quote_smart($_REQUEST{"mm_charattributes_name"}).
		"\", \""
		.quote_smart($_REQUEST{"mm_charattributes_value"}).
		"\", \""
		.quote_smart($_REQUEST{"mm_charattributes_value_type"}).
		"\", \""
		.quote_smart($_REQUEST{"char"}).
		"\")";
	mysql_query($query
		, $dbhandle)  
		or die("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Added attribute to ".$_REQUEST{"char"}.".", $query);
}

$result = mysql_query("select * ".
	" from mm_charattributes".
	" where charname = \"".quote_smart($_REQUEST{"char"})."\""
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_attributelist.php?name=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>value:</b> %s ", $myrow[1]);
	printf("<b>value_type:</b> %s<BR>", $myrow[2]);
}
	if ($owner)
	{
?>

<FORM METHOD="GET" ACTION="/scripts/admin_chars.php">
<b>
<INPUT TYPE="hidden" NAME="mm_charattributes_charname" VALUE="<?php echo $_REQUEST{"char"} ?>">
<INPUT TYPE="hidden" NAME="char" VALUE="<?php echo $_REQUEST{"char"} ?>">
<TABLE>
<TR><TD>name</TD><TD><INPUT TYPE="text" NAME="mm_charattributes_name" VALUE="" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>value</TD><TD><INPUT TYPE="text" NAME="mm_charattributes_value" VALUE="" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>value_type</TD><TD><SELECT NAME="mm_charattributes_value_type" SIZE="2"> 
<OPTION VALUE="string" selected >string
<OPTION VALUE="integer">integer
<OPTION VALUE="boolean">boolean
</SELECT></TD></TR>   
</TABLE>
<INPUT TYPE="submit" VALUE="Add/Replace Attribute">
</b>   
</FORM>
<?php
	}
$result = mysql_query("select * ".
	" from characterinfo".
	" where name = \"".quote_smart($_REQUEST{"char"})."\""
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
	\"".quote_smart($_REQUEST{"char"})."\""
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
