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
Item <?php echo $_REQUEST{"item"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
/* the following constraints need to be checked before any kind of update is
to take place:

adding item to room:
- check itemid is integer/numeric
- check if roomid is integer/numeric
- check if owner is null or correct
- check that room exists
deleting item from room:
- check itemid is integer/numeric
- check if owner is null or correct

adding item to char:
- check itemid is integer/numeric
- check if owner is null or correct
- check that character exists
deleting item from char:
- check itemid is integer/numeric
- check if owner is null or correct

adding item to container:
- check itemid is integer/numeric
- check containerid is integer/numeric
- check if owner is null or correct
- check that container exists AND make sure that the item is a container!!
- check that the container referenced is not the item itself.
deleting item from container:
- check itemid is integer/numeric
- check if owner is null or correct
*/
$owner = false;
$claimed = false; // is the item allocated to a character/room or container?

if (!isset($_REQUEST{"item"}))
{
	error_message("Form information missing.");
}
$result = mysql_query("select mm_itemtable.id, mm_itemtable.owner, 
mm_itemtable.itemid,
date_format(mm_itemtable.creation, \"%Y-%m-%d %T\") as creation2, 
concat(adject1, \" \", adject2, \" \", adject3, \" \", name) as description
from mm_itemtable left join mm_items ".
	" on mm_items.id = mm_itemtable.itemid where mm_itemtable.id =
	".quote_smart($_REQUEST{"item"})
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> %s<BR>", $myrow["id"]);
	printf("<b>itemid:</b> <A HREF=\"/scripts/admin_itemdefs.php?item=%s\">%s</A><BR>", $myrow["itemid"], $myrow["itemid"]);
	printf("<b>description:</b> %s<BR>", $myrow["description"]);
	printf("<b>owner:</b> %s<BR>", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	$owner = false;
	if ($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"])   
	{
		$owner = true;
	}

}

if (isset($_REQUEST{"removeitemfromroom"}))
{
	// check numeric stuff
	if (!is_numeric($_REQUEST{"removeitemfromroom"}))
	{
		error_message("Expected field to be an integer, and it wasn't.");
	}
	// check if owner is correct
	if (!$owner)
	{
		error_message("You are not the owner of this item instance.");
	}
	$query = "delete from mm_roomitemtable where id = ".
	quote_smart($_REQUEST{"removeitemfromroom"});
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	if (mysql_affected_rows() < 1)
	{
		error_message("Item does not exist in a room.");
	}
	writeLogLong($dbhandle, "Removed item ".$_REQUEST{"removeitemfromroom"}." from room(s).", $query);
}
if (isset($_REQUEST{"removeitemfromchar"}))
{
	// check numeric stuff
	if (!is_numeric($_REQUEST{"removeitemfromchar"}))
	{
		error_message("Expected field to be an integer, and it wasn't.");
	}
	// check if owner is correct
	if (!$owner)
	{
		error_message("You are not the owner of this item instance.");
	}
	$query = "delete from mm_charitemtable where id = ".
	quote_smart($_REQUEST{"removeitemfromchar"});
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	if (mysql_affected_rows() < 1)
	{
		error_message("Item does not belong to a character.");
	}
	writeLogLong($dbhandle, "Removed item ".$_REQUEST{"removeitemfromchar"}." from character(s).", $query);
}
if (isset($_REQUEST{"removeitemfromitem"}))
{
	// check numeric stuff
	if (!is_numeric($_REQUEST{"removeitemfromitem"}))
	{
		error_message("Expected field to be an integer, and it wasn't.");
	}
	// check if owner is correct
	if (!$owner)
	{
		error_message("You are not the owner of this item instance.");
	}
	$query = "delete from mm_itemitemtable where id = ".
	quote_smart($_REQUEST{"removeitemfromitem"});
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	if (mysql_affected_rows() < 1)
	{
		error_message("Item is not stored in a container.");
	}
	writeLogLong($dbhandle, "Removed item ".$_REQUEST{"removeitemfromitem"}." from container(s).", $query);
}
if (isset($_REQUEST{"removeitemtotally"}))
{
	// check numeric stuff
	if (!is_numeric($_REQUEST{"removeitemtotally"}))
	{
		error_message("Expected field to be an integer, and it wasn't.");
	}
	// check if owner is correct
	if (!$owner)
	{
		error_message("You are not the owner of this item instance.");
	}
	
	$query = "delete from mm_itemtable where id = ".
	quote_smart($_REQUEST{"removeitemtotally"});
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	writeLogLong($dbhandle, "Removed item instance ".$_REQUEST{"removeitemtotally"}.".", $query);
}
if (isset($_REQUEST{"additemtoroom"}))
{
	// check numeric stuff
	if (!is_numeric($_REQUEST{"additemtoroom"}))
	{
		error_message("Expected field to be an integer, and it wasn't.");
	}
	if (!is_numeric($_REQUEST{"additemtoroom_roomid"}))
	{
		error_message("Expected field to be an integer, and it wasn't.");
	}
	// check if owner is correct
	if (!$owner)
	{
		error_message("You are not the owner of this item instance.");
	}
	// check that room exists
	$result = mysql_query("select id from mm_rooms where id = ".
	quote_smart($_REQUEST{"additemtoroom_roomid"})
	, $dbhandle)
	or error_message("Query(2) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		error_message("Room does not exist.");
	}
	
	$query = "insert into mm_roomitemtable (id, room) values(".
	quote_smart($_REQUEST{"additemtoroom"}).
	", ".
	quote_smart($_REQUEST{"additemtoroom_roomid"}).
	")";
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	writeLogLong($dbhandle, "Added item ".$_REQUEST{"additemtoroom"}." to room ".$_REQUEST{"additemtoroom_roomid"}.".", $query);
}
if (isset($_REQUEST{"additemtochar"}))
{
	// check numeric stuff
	if (!is_numeric($_REQUEST{"additemtochar"}))
	{
		error_message("Expected field to be an integer, and it wasn't.");
	}
	// check if owner is correct
	if (!$owner)
	{
		error_message("You are not the owner of this item instance.");
	}
	// check that character exists
	$result = mysql_query("select name from mm_usertable where name = \"".
	quote_smart($_REQUEST{"additemtochar_charname"}).
	"\""
	, $dbhandle)
	or error_message("Query(2) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		error_message("Character does not exist.");
	}

	$query = "insert into mm_charitemtable (id, belongsto) values(".
	quote_smart($_REQUEST{"additemtochar"}).
	", \"".
	quote_smart($_REQUEST{"additemtochar_charname"}).
	"\")";
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	writeLogLong($dbhandle, "Added item ".$_REQUEST{"additemtochar"}." to character ".$_REQUEST{"additemtochar_charname"}.".", $query);
}
if (isset($_REQUEST{"additemtocontainer"}))
{
	// check numeric stuff
	if (!is_numeric($_REQUEST{"additemtocontainer"}))
	{
		error_message("Expected field to be an integer, and it wasn't.");
	}
	if (!is_numeric($_REQUEST{"additemtocontainer_containerid"}))
	{
		error_message("Expected field to be an integer, and it wasn't.");
	}
	// check if owner is correct
	if (!$owner)
	{
		error_message("You are not the owner of this item instance.");
	}
	// check that not item is same as container
	if ($_REQUEST{"additemtocontainer_containerid"} == $_REQUEST{"additemtocontainer"})
	{
		error_message("You cannot put an item into itself.");
	}
	// check that container exists and is in fact a container
	$result = mysql_query("select mm_itemtable.itemid from mm_itemtable, mm_items ".
	"where mm_itemtable.itemid = mm_items.id and mm_items.container = 1 and mm_itemtable.id = ".
	quote_smart($_REQUEST{"additemtocontainer_containerid"})
	, $dbhandle)
	or error_message("Query(2) failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		error_message("Container does not exist or is not a container.");
	}

	$query = "insert into mm_itemitemtable (id, containerid) values(".
	quote_smart($_REQUEST{"additemtocontainer"}).
	", ".
	quote_smart($_REQUEST{"additemtocontainer_containerid"}).
	")";
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	writeLogLong($dbhandle, "Added item ".$_REQUEST{"additemtocontainer"}." to container ".$_REQUEST{"additemtocontainer_containerid"}.".", $query);
}

$result = mysql_query("select containerid ".
	" from mm_itemitemtable".
	" where mm_itemitemtable.id =
	".quote_smart($_REQUEST{"item"})
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_items.php">
<INPUT TYPE="hidden" NAME="removeitemfromitem" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="submit" VALUE="Remove From Container">
<b>Item contained in item:</b> <A HREF="/scripts/admin_items.php?item=<?php echo $myrow[0] ?>"><?php echo $myrow[0] ?></A><BR>
</FORM>
<?php
	$claimed = true;
}
$result = mysql_query("select room ".
	" from mm_roomitemtable".
	" where mm_roomitemtable.id =
	".quote_smart($_REQUEST{"item"})
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_items.php">
<INPUT TYPE="hidden" NAME="removeitemfromroom" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="submit" VALUE="Remove From Room">
<b>Item contained in room:</b> <A HREF="/scripts/admin_rooms.php?room=<?php echo $myrow[0] ?>"><?php echo $myrow[0] ?></A><BR>
</FORM>
<?php
	$claimed = true;
}
$result = mysql_query("select belongsto ".
	" from mm_charitemtable".
	" where mm_charitemtable.id =
	".quote_smart($_REQUEST{"item"})
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_items.php">
<INPUT TYPE="hidden" NAME="removeitemfromchar" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="submit" VALUE="Remove From Character">
<b>Item belongsto character:</b> <A HREF="/scripts/admin_chars.php?char=<?php echo $myrow[0] ?>"><?php echo $myrow[0] ?></A><BR>
</FORM>
<?php
	$claimed = true;
}

$result = mysql_query("select mm_itemitemtable.id, mm_items.id, ".
	" mm_items.adject1, mm_items.adject2, mm_items.adject3, mm_items.name from mm_items, mm_itemtable, mm_itemitemtable".
	" where mm_itemtable.id = mm_itemitemtable.id and ".
	" mm_items.id = mm_itemtable.itemid and ".
	" mm_itemitemtable.containerid =
	".quote_smart($_REQUEST{"item"})
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> <A HREF=\"/scripts/admin_items.php?item=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>itemid:</b> %s ", $myrow[1]);
	printf("<b>description:</b> %s %s %s %s<BR>", $myrow[2], $myrow[3], $myrow[4], $myrow[5]);
}

if (($owner) && (!$claimed))
{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_items.php">
<INPUT TYPE="hidden" NAME="additemtoroom" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="submit" VALUE="Add To Room">
Roomid: <INPUT TYPE="text" NAME="additemtoroom_roomid" VALUE="" SIZE="40" MAXLENGTH="40">
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_items.php">
<INPUT TYPE="hidden" NAME="additemtochar" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="submit" VALUE="Add To Character">
Charactername: <INPUT TYPE="text" NAME="additemtochar_charname" VALUE="" SIZE="40" MAXLENGTH="40">
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_items.php">
<INPUT TYPE="hidden" NAME="additemtocontainer" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="submit" VALUE="Add To Container">
Containerid: <INPUT TYPE="text" NAME="additemtocontainer_containerid" VALUE="" SIZE="40" MAXLENGTH="40">
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_items.php">
<INPUT TYPE="hidden" NAME="removeitemtotally" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $_REQUEST{"item"} ?>">
<INPUT TYPE="submit" VALUE="Remove Item Instance">
</FORM>
<?php
}

printf("<H2><A HREF=\"/karchan/admin/help/attributes.html\" target=\"_blank\">
<IMG SRC=\"/images/icons/9pt4a.gif\" BORDER=\"0\"></A>Attributes</H2>");

if (isset($_REQUEST{"item"}) &&
	isset($_REQUEST{"mm_itemattributes_name"}) &&
	isset($_REQUEST{"mm_itemattributes_value"}) &&
	isset($_REQUEST{"mm_itemattributes_value_type"}) &&
	$owner)
{
	$query = "replace into mm_itemattributes
		(name, value, value_type, id) values(\""
		.quote_smart($_REQUEST{"mm_itemattributes_name"}).
		"\", \""
		.quote_smart($_REQUEST{"mm_itemattributes_value"}).
		"\", \""
		.quote_smart($_REQUEST{"mm_itemattributes_value_type"}).
		"\", \""
		.quote_smart($_REQUEST{"item"}).
		"\")";
	mysql_query($query
		, $dbhandle)
		or error_message("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Added attribute to ".$_REQUEST{"char"}.".", $query);
}
$result = mysql_query("select * ".
	" from mm_itemattributes".
	" where id = ".quote_smart($_REQUEST{"item"})
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A HREF=\"/scripts/admin_attributelist.php?name=%s\">%s</A> ", $myrow[0], $myrow[0]);
	printf("<b>value:</b> %s ", $myrow[1]);
	printf("<b>value_type:</b> %s<BR>", $myrow[2]);
}
mysql_close($dbhandle);
if ($owner)
	{
?>

<FORM METHOD="GET" ACTION="/scripts/admin_items.php">
<b>
<INPUT TYPE="hidden" NAME="item" VALUE="<?php echo $_REQUEST{"item"} ?>">
<TABLE>
<TR><TD>name</TD><TD><INPUT TYPE="text" NAME="mm_itemattributes_name" VALUE="" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>value</TD><TD><INPUT TYPE="text" NAME="mm_itemattributes_value" VALUE="" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>value_type</TD><TD><SELECT NAME="mm_itemattributes_value_type" SIZE="2"> 
<OPTION VALUE="string" selected >string
<OPTION VALUE="integer">integer
<OPTION VALUE="boolean">boolean
</SELECT></TD></TR> 
</TABLE>
<INPUT TYPE="submit" VALUE="Add Attribute">
</b> 
</FORM>
<?php
	}
?>

</BODY>
</HTML>
