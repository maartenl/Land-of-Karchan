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
Item Definition <?php echo $_REQUEST{"item"} ?></H1>

<A HREF="/karchan/admin/help/items.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A><P>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

/* the following constraints need to be checked before any kind of update is
to take place:
																																							
changing itemdef:
- first check that the change is approved (i.e. owner or null)
- does wearable not conflict with anybody who is actively wearing it?
deleting itemdef:
- first check that the change is approved (i.e. owner or null)
- check item instances created using item
adding itemdef:
- all information filled out correctly?
																																							
*/
if (isset($_REQUEST{"name"}))
{
	// check it.
	$result = mysql_query("select id from mm_items where id = \"".
	  quote_smart($_REQUEST{"item"}).
	  "\" and (owner is null or owner = \"".
	  quote_smart($_COOKIE["karchanadminname"]).
	  "\")"
	  , $dbhandle)
	  or error_message("Query(1) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
	  error_message("You are not the owner of this itemdefinition.");
	}
	$result = mysql_query("select id from mm_items where id = \"".
	  quote_smart($_REQUEST{"item"})."\""
	  , $dbhandle)
	  or error_message("Query(2) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
	  error_message("Item Definition does not exist.");
	}
	if (($_REQUEST["container"] != "1") &&
	   ($_REQUEST["container"] != "0"))
	{
		error_message("Container value must be either 0 (no) or 1 (yes).");
	}
	if (($_REQUEST["getable"] != "1") &&
	   ($_REQUEST["getable"] != "0"))
	{
		error_message("Getable value must be either 0 (no) or 1 (yes).");
	}
	if (($_REQUEST["dropable"] != "1") &&
	   ($_REQUEST["dropable"] != "0"))
	{
		error_message("Dropable value must be either 0 (no) or 1 (yes).");
	}
	if (($_REQUEST["isopenable"] != "1") &&
	   ($_REQUEST["isopenable"] != "0"))
	{
		error_message("isOpenable value must be either 0 (no) or 1 (yes).");
	}
	if (($_REQUEST["visible"] != "1") &&
	   ($_REQUEST["visible"] != "0"))
	{
		error_message("Visible value must be either 0 (no) or 1 (yes).");
	}
	if ($_REQUEST["keyid"] != "")
	{
		$result = mysql_query("select 1 
		from mm_items 
		where id = \"".
		  quote_smart($_REQUEST["keyid"]) . "\""
		  , $dbhandle)
		  or error_message("Query(3) failed : " . mysql_error());
		if (mysql_num_rows($result) == 0)
		{
		  error_message("Key Item Definition Id does not exist.");
		}
	}

	$capacity = $_REQUEST["capacity"];
	$keyid = $_REQUEST["keyid"];
	$eatable = $_REQUEST["eatable"];
	$drinkable = $_REQUEST["drinkable"];
	$readdescr = $_REQUEST["readdescr"];
	// make that change.
	$query = "update mm_items set name=\"".
	  quote_smart($_REQUEST{"name"}).
	  "\", adject1=\"".
	  quote_smart($_REQUEST{"adject1"}).
	  "\", adject2=\"".
	  quote_smart($_REQUEST{"adject2"}).
	  "\", adject3=\"".
	  quote_smart($_REQUEST{"adject3"}).
	  "\", description=\"".
	  quote_smart($_REQUEST["description"]).
	  "\", eatable=". ($eatable==""?"null": 
			"\"".quote_smart($eatable)."\"").
	  ", drinkable=". ($drinkable==""?"null": 
			"\"".quote_smart($drinkable)."\"").
	  ", readdescr=". ($readdescr==""?"null": 
			"\"".quote_smart($readdescr)."\"").
	  ", capacity=". ($capacity==""?"null": 
			"\"".quote_smart($capacity)."\"").
	  ", keyid=". ($keyid==""?"null": 
			"\"".quote_smart($keyid)."\"").
	  ", gold=".
	  quote_smart($_REQUEST["gold"]).
	  ", silver=".
	  quote_smart($_REQUEST["silver"]).
	  ", copper=".
	  quote_smart($_REQUEST["copper"]).
	  ", weight=".
	  quote_smart($_REQUEST["weight"]).
	  ", container=".
	  quote_smart($_REQUEST["container"]).
	  ", isopenable=".
	  quote_smart($_REQUEST["isopenable"]).
	  ", getable=".
	  quote_smart($_REQUEST["getable"]).
	  ", dropable=".
	  quote_smart($_REQUEST["dropable"]).
	  ", visible=".
	  quote_smart($_REQUEST["visible"]).
	  ", owner=\"".
	  quote_smart($_COOKIE["karchanadminname"]).
	  "\" where id = \"".
	  quote_smart($_REQUEST{"item"}).
	  "\"";
	mysql_query($query
	  , $dbhandle)
	  or error_message("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Changed item definition ".$_REQUEST{"item"}.".", $query);

	$wearable2 = 0;
	for ($i = 0; $i < count($_REQUEST{"wearable"}); $i++)
	{
		$wearable2 += $_REQUEST{"wearable"}[$i];
	}
	$result = mysql_query("select wearable
	from mm_items
	where wearable <> ".
	quote_smart($wearable2).
	" and id = ".
	  quote_smart($_REQUEST["item"])
	  , $dbhandle)
	  or error_message("Query(4) failed : " . mysql_error());
	if (mysql_num_rows($result) != 0)
	{
		// check the wearing...
		$result = mysql_query("select 1 
		from mm_itemtable, mm_charitemtable 
		where mm_itemtable.id = mm_charitemtable.id and
		mm_charitemtable.wearing is not null and
		mm_itemtable.itemid = ".
		  quote_smart($_REQUEST["item"])
		  , $dbhandle)
		  or error_message("Query(4) failed : " . mysql_error());
		if (mysql_num_rows($result) != 0)
		{
		  error_message("Characters are wearing the item, cannot change wearable.");
		}

		// change the wearing...
		$query = "update mm_items set wearable=\"".
		  quote_smart($wearable2).
		  "\", owner=\"".
		  quote_smart($_COOKIE["karchanadminname"]).
		  "\" where id = \"".
		  quote_smart($_REQUEST{"item"}).
		  "\"";
		mysql_query($query
		  , $dbhandle)
		  or error_message("Query(9) failed : " . mysql_error());
		writeLogLong($dbhandle, "Changed wearing field of item definition ".$_REQUEST{"item"}.".", $query);
	}

}
																																							

if (isset($_REQUEST{"deleteitemdef"}))
{
	// check if everything is in proper format
	if ( !is_numeric($_REQUEST{"deleteitemdef"}))
	{
		error_message("Expected item definition id to be an integer, and it wasn't.");
	}
	// check if no item instances are derived from this item definition
	$result = mysql_query("select 1 from mm_itemtable where itemid = ".
	quote_smart($_REQUEST{"deleteitemdef"}), $dbhandle);
	if (mysql_num_rows($result) > 0)
	{
		error_message("There are still item instances using this item definition.");
	}
	
	// make it so
	$query = "delete from mm_items where id = ".
	quote_smart($_REQUEST{"deleteitemdef"}).
	" and (owner is null or owner = \"".
	quote_smart($_COOKIE["karchanadminname"]).
	"\")";
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	if (mysql_affected_rows() != 1)
	{
		error_message("Item definition does not exist or not proper owner.");
	}
	writeLogLong($dbhandle, "Removed item definition ".$_REQUEST{"deleteitemdef"}.".", $query);
}

$result = mysql_query("select *,date_format(creation, \"%Y-%m-%d %T\") as
	creation2 from mm_items where id = ".
		quote_smart($_REQUEST{"item"})
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> %s<BR>", $myrow["id"]);
	printf("<b>name:</b> %s<BR>", $myrow["name"]);
	printf("<b>adject1:</b> %s<BR>", $myrow["adject1"]);
	printf("<b>adject2:</b> %s<BR>", $myrow["adject2"]);
	printf("<b>adject3:</b> %s<BR>", $myrow["adject3"]);
	printf("<b>manaincrease:</b> %s<BR>", $myrow["manaincrease"]);
	printf("<b>hitincrease:</b> %s<BR>", $myrow["hitincrease"]);
	printf("<b>vitalincrease:</b> %s<BR>", $myrow["vitalincrease"]);
	printf("<b>movementincrease:</b> %s<BR>", $myrow["movementincrease"]);
	printf("<b>eatable:</b> %s<BR>", $myrow["eatable"]);
	printf("<b>drinkable:</b> %s<BR>", $myrow["drinkable"]);
	printf("<b>lightable:</b> %s<BR>", ($myrow["lightable"]==1? "yes":"no"));
	printf("<b>getable:</b> %s<BR>", ($myrow["getable"]==1? "yes":"no"));
	printf("<b>dropable:</b> %s<BR>", ($myrow["dropable"]==1? "yes":"no"));
	printf("<b>visible:</b> %s<BR>", ($myrow["visible"]==1? "yes":"no"));
	printf("<b>description:</b> %s<BR>", $myrow["description"]);
	printf("<b>readdescr:</b> %s<BR>", $myrow["readdescr"]);
	printf("<b>wearable:</b> <UL>");
	if ($myrow["wearable"] & 1)
	{
		printf("<li>on head");
	}
	if ($myrow["wearable"] & 2)
	{
		printf("<li>on neck");
	}
	if ($myrow["wearable"] & 4)
	{
		printf("<li>on torso");
	}
	if ($myrow["wearable"] & 8)
	{
		printf("<li>on arms");
	}
	if ($myrow["wearable"] & 16)
	{
		printf("<li>on left wrist");
	}
	if ($myrow["wearable"] & 32)
	{
		printf("<li>on right wrist");
	}
	if ($myrow["wearable"] & 64)
	{
		printf("<li>on left finger");
	}
	if ($myrow["wearable"] & 128)
	{
		printf("<li>on right finger");
	}
	if ($myrow["wearable"] & 256)
	{
		printf("<li>on feet");
	}
	if ($myrow["wearable"] & 512)
	{
		printf("<li>on hands");
	}
	if ($myrow["wearable"] & 1024)
	{
		printf("<li>floating nearby");
	}
	if ($myrow["wearable"] & 2048)
	{
		printf("<li>on waist");
	}
	if ($myrow["wearable"] & 4096)
	{
		printf("<li>on legs");
	}
	if ($myrow["wearable"] & 8192)
	{
		printf("<li>on eyes");
	}
	if ($myrow["wearable"] & 16384)
	{
		printf("<li>on ears");
	}
	if ($myrow["wearable"] & 32768)
	{
		printf("<li>on body");
	}
	if ($myrow["wearable"] & 65536)
	{
		printf("<li>wielding with left hand");
	}
	if ($myrow["wearable"] & 131072)
	{
		printf("<li>wielding with right hand");
	}
	if ($myrow["wearable"] & 262144)
	{
		printf("<li>wielding with both hands");
	}
	printf("</ul><b>gold:</b> %s<BR>", $myrow["gold"]);
	printf("<b>silver:</b> %s<BR>", $myrow["silver"]);
	printf("<b>copper:</b> %s<BR>", $myrow["copper"]);
	printf("<b>weight:</b> %s<BR>", $myrow["weight"]);
	printf("<b>pasdefense:</b> %s<BR>", $myrow["pasdefense"]);
	printf("<b>damageresistance:</b> %s<BR>", $myrow["damageresistance"]);
	printf("<b>container:</b> %s<BR>", ($myrow["container"]==1? "yes":"no"));
	printf("<b>capacity:</b> %s<BR>", $myrow["capacity"]);
	printf("<b>isopenable:</b> %s<BR>", ($myrow["isopenable"]==1? "yes":"no"));
	printf("<b>keyid:</b> <A HREF=\"/scripts/admin_itemdefs.php?item=%S\">%s</A><BR>", $myrow["keyid"], $myrow["keyid"]);
	printf("<b>owner:</b> %s<BR>", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	if ($myrow["owner"] == null || $myrow["owner"] == "" ||
	$myrow["owner"] == $_COOKIE["karchanadminname"])
	{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_itemdefs.php">
<b>
<INPUT TYPE="hidden" NAME="deleteitemdef" VALUE="<?php echo $myrow["id"] ?>">
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $myrow["id"] ?>">
<INPUT TYPE="submit" VALUE="Delete Item Definition">
</FORM>

<FORM METHOD="GET" ACTION="/scripts/admin_itemdefs.php">
<b>
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $myrow["id"] ?>">
<TABLE>
<TR><TD>name</TD><TD><INPUT TYPE="text" NAME="name" VALUE="<?php echo $myrow["name"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>adject1</TD><TD><INPUT TYPE="text" NAME="adject1" VALUE="<?php echo $myrow["adject1"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>adject2</TD><TD><INPUT TYPE="text" NAME="adject2" VALUE="<?php echo $myrow["adject2"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>adject3</TD><TD><INPUT TYPE="text" NAME="adject3" VALUE="<?php echo $myrow["adject3"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>description</TD><TD><TEXTAREA NAME="description" ROWS="10" COLS="85">
<?php echo htmlspecialchars($myrow["description"]) ?></TEXTAREA><P></TD></TR>
<TR><TD>eatable</TD><TD><TEXTAREA NAME="eatable" 
ROWS="10" COLS="85">
<?php echo htmlspecialchars($myrow["eatable"]) ?></TEXTAREA><P></TD></TR>
<TR><TD>drinkable</TD><TD><TEXTAREA NAME="drinkable" ROWS="10" COLS="85">
<?php echo htmlspecialchars($myrow["drinkable"]) ?></TEXTAREA><P></TD></TR>
<TR><TD>readdescr</TD><TD><TEXTAREA NAME="readdescr" ROWS="10" COLS="85">
<?php echo htmlspecialchars($myrow["readdescr"]) ?></TEXTAREA><P></TD></TR>
<TR><TD>gold</TD><TD><INPUT TYPE="text" NAME="gold" VALUE="<?php echo $myrow["gold"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>silver</TD><TD><INPUT TYPE="text" NAME="silver" VALUE="<?php echo $myrow["silver"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>copper</TD><TD><INPUT TYPE="text" NAME="copper" VALUE="<?php echo $myrow["copper"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>weight</TD><TD><INPUT TYPE="text" NAME="weight" VALUE="<?php echo $myrow["weight"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>container</TD><TD><INPUT TYPE="radio" NAME="container" VALUE="1" <?php echo ($myrow["container"] == 1? "checked" : "") ?>>yes
<BR><INPUT TYPE="radio" NAME="container" VALUE="0" <?php echo ($myrow["container"] == 0? "checked" : "") ?>>no</TD></TR>
<TR><TD>isopenable</TD><TD><INPUT TYPE="radio" NAME="isopenable" VALUE="1" <?php echo ($myrow["isopenable"] == 1? "checked" : "") ?>>yes
<BR><INPUT TYPE="radio" NAME="isopenable" VALUE="0" <?php echo ($myrow["isopenable"] == 0? "checked" : "") ?>>no</TD></TR>
<TR><TD>capacity</TD><TD><INPUT TYPE="text" NAME="capacity" VALUE="<?php echo $myrow["capacity"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>keyid</TD><TD><INPUT TYPE="text" NAME="keyid" VALUE="<?php echo $myrow["keyid"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>getable</TD><TD><INPUT TYPE="radio" NAME="getable" VALUE="1" <?php echo ($myrow["getable"] == 1? "checked" : "") ?>>yes
<BR><INPUT TYPE="radio" NAME="getable" VALUE="0" <?php echo ($myrow["getable"] == 0? "checked" : "") ?>>no</TD></TR>
<TR><TD>dropable</TD><TD><INPUT TYPE="radio" NAME="dropable" VALUE="1" <?php echo ($myrow["dropable"] == 1? "checked" : "") ?>>yes
<BR><INPUT TYPE="radio" NAME="dropable" VALUE="0" <?php echo ($myrow["dropable"] == 0? "checked" : "") ?>>no</TD></TR>
<TR><TD>visible</TD><TD><INPUT TYPE="radio" NAME="visible" VALUE="1" <?php echo ($myrow["visible"] == 1? "checked" : "") ?>>yes
<BR><INPUT TYPE="radio" NAME="visible" VALUE="0" <?php echo ($myrow["visible"] == 0? "checked" : "") ?>>no</TD></TR>
<TR><TD>wearable/wieldable</TD><TD><SELECT MULTIPLE NAME="wearable[] " SIZE="20">
<OPTION VALUE="1" <?php if ($myrow["wearable"] & 1) {printf("selected");} ?>>head
<OPTION VALUE="2" <?php if ($myrow["wearable"] & 2) {printf("selected");} ?>>neck
<OPTION VALUE="4" <?php if ($myrow["wearable"] & 4) {printf("selected");} ?>>torso
<OPTION VALUE="8" <?php if ($myrow["wearable"] & 8) {printf("selected");} ?>>arms
<OPTION VALUE="16" <?php if ($myrow["wearable"] & 16) {printf("selected");} ?>>leftwrist
<OPTION VALUE="32" <?php if ($myrow["wearable"] & 32) {printf("selected");} ?>>rightwrist
<OPTION VALUE="64" <?php if ($myrow["wearable"] & 64) {printf("selected");} ?>>leftfinger
<OPTION VALUE="128" <?php if ($myrow["wearable"] & 128) {printf("selected");} ?>>rightfinger
<OPTION VALUE="256" <?php if ($myrow["wearable"] & 256) {printf("selected");} ?>>feet
<OPTION VALUE="512" <?php if ($myrow["wearable"] & 512) {printf("selected");} ?>>hands
<OPTION VALUE="1024" <?php if ($myrow["wearable"] & 1024) {printf("selected");} ?>>floating
<OPTION VALUE="2048" <?php if ($myrow["wearable"] & 2048) {printf("selected");} ?>>waist
<OPTION VALUE="4096" <?php if ($myrow["wearable"] & 4096) {printf("selected");} ?>>legs
<OPTION VALUE="8192" <?php if ($myrow["wearable"] & 8192) {printf("selected");} ?>>eyes
<OPTION VALUE="16384" <?php if ($myrow["wearable"] & 16384) {printf("selected");} ?>>ears
<OPTION VALUE="32768" <?php if ($myrow["wearable"] & 32768) {printf("selected");} ?>>body
<OPTION VALUE="65536" <?php if ($myrow["wearable"] & 65536) {printf("selected");} ?>>wield lefthand
<OPTION VALUE="131072" <?php if ($myrow["wearable"] & 131072) {printf("selected");} ?>>wield righthand
<OPTION VALUE="262144" <?php if ($myrow["wearable"] & 262144) {printf("selected");} ?>>wielding both hands
</SELECT>
</TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Item Definition">
</b>
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_ownership.php">
<b>
<INPUT TYPE="hidden" NAME="id" VALUE="<?php echo $myrow["id"] ?>">
<INPUT TYPE="hidden" NAME="removeownership" VALUE="4">
<INPUT TYPE="submit" VALUE="Remove Ownership">
</b>
</FORM>
<?php
	}
}
$result = mysql_query("select id from mm_itemtable where itemid = ".
		quote_smart($_REQUEST{"item"})
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s<A><BR> ", $myrow[0], $myrow[0]);
}

mysql_close($dbhandle);
?>

</BODY>
</HTML>
