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
Item Definition <?php echo $_REQUEST{"item"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
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
	  mysql_escape_string($_REQUEST{"item"}).
	  "\" and (owner is null or owner = \"".
	  mysql_escape_string($_COOKIE["karchanadminname"]).
	  "\")"
	  , $dbhandle)
	  or die("Query(1) failed : " . mysql_error());
   if (mysql_num_rows($result) != 1)
   {
	  die("You are not the owner of this itemdefinition.");
   }
   $result = mysql_query("select id from mm_items where id = \"".
	  mysql_escape_string($_REQUEST{"item"})."\""
	  , $dbhandle)
	  or die("Query(2) failed : " . mysql_error());
   if (mysql_num_rows($result) != 1)
   {
	  die("Item Definition does not exist.");
   }
   $result = mysql_query("select 1 
	from mm_itemtable, mm_charitemtable 
	where mm_itemtable.id = mm_charitemtable.id and
	mm_charitemtable.wearing is not null and
	mm_itemtable.itemid = ".
	  mysql_escape_string($_REQUEST{"item"})
	  , $dbhandle)
	  or die("Query(2) failed : " . mysql_error());
   if (mysql_num_rows($result) != 0)
   {
	  die("Characters are wearing the item, cannot change wearable.");
   }

   $wearable2 = 0;
   for ($i = 0; $i < count($_REQUEST{"wearable"}); $i++)
   {
      $wearable2 += $_REQUEST{"wearable"}[$i];
   }
   $eatable = $_REQUEST["eatable"];
   $drinkable = $_REQUEST["drinkable"];
   $readdescr = $_REQUEST["readdescr"];
   // make that change.
   $query = "update mm_items set name=\"".
	  mysql_escape_string($_REQUEST{"name"}).
	  "\", adject1=\"".
	  mysql_escape_string($_REQUEST{"adject1"}).
	  "\", adject2=\"".
	  mysql_escape_string($_REQUEST{"adject2"}).
	  "\", adject3=\"".
	  mysql_escape_string($_REQUEST{"adject3"}).
	  "\", wearable=\"".
	  mysql_escape_string($wearable2).
	  "\", description=\"".
	  mysql_escape_string($_REQUEST["description"]).
	  "\", eatable=". ($eatable==""?"null": 
			"\"".mysql_escape_string($eatable)."\"").
	  ", drinkable=". ($drinkable==""?"null": 
			"\"".mysql_escape_string($drinkable)."\"").
	  ", readdescr=". ($readdescr==""?"null": 
			"\"".mysql_escape_string($readdescr)."\"").
	  ", gold=".
	  mysql_escape_string($_REQUEST["gold"]).
	  ", silver=".
	  mysql_escape_string($_REQUEST["silver"]).
	  ", copper=".
	  mysql_escape_string($_REQUEST["copper"]).
	  ", weight=".
	  mysql_escape_string($_REQUEST["weight"]).
	  ", container=".
	  mysql_escape_string($_REQUEST["container"]).
	  ", owner=\"".
	  mysql_escape_string($_COOKIE["karchanadminname"]).
	  "\" where id = \"".
	  mysql_escape_string($_REQUEST{"item"}).
	  "\"";
   mysql_query($query
	  , $dbhandle)
	  or die("Query(8) failed : " . mysql_error());
   writeLogLong($dbhandle, "Changed item definition ".$_REQUEST{"item"}.".", $query);
}
																																							
$result = mysql_query("select * from mm_items where id = ".
		mysql_escape_string($_REQUEST{"item"})
	, $dbhandle)
	or die("Query failed : " . mysql_error());
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
	printf("<b>Owner:</b> %s<BR>", $myrow["owner"]);
	printf("<b>Creation:</b> %s<BR>", $myrow["creation"]);
	if ($myrow["owner"] == null || $myrow["owner"] == "" ||
	$myrow["owner"] == $_COOKIE["karchanadminname"])
	{
?>
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
<?php
	}
}
$result = mysql_query("select id from mm_itemtable where itemid = ".
		mysql_escape_string($_REQUEST{"item"})
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s<A><BR> ", $myrow[0], $myrow[0]);
}

mysql_close($dbhandle);
?>

</BODY>
</HTML>
