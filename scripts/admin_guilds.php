<?
/*-------------------------------------------------------------------------
svninfo: $Id: admin_items.php 992 2005-10-23 08:03:45Z maartenl $
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
Guilds</H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
/* the following constraints need to be checked before any kind of update is
to take place:

adding guild:
- the guildname should be at least 5 characters
- the guildname is not allowed to contain spaces
- does the guild not already exist?
- does bossname exist?
- is the bossman already a guildmaster?
- does the bossman have a guildwish?
- is the guildname at least 3 characters?
changing guild:
- does the guild exist?
- does bossname exist?
- is the bossman already a guildmaster of another guild?
- are numeric fields numeric?
- is the administrator the owner of the guild.
deleting guild:
- any guild members left?
- is the administrator the owner of the guild.
*/
$owner = false;

if (isset($_REQUEST{"editguildname"}))
{
	// check that guild exists
	$result = mysql_query("select name from mm_guilds where name=\"".
	quote_smart($_REQUEST{"editguildname"})."\""
	, $dbhandle)
	or error_message("Query(2) failed : " . mysql_error());
	if (mysql_num_rows($result) == 0)
	{
		error_message("Guild does not exist.");
	}
	// check that bossname exists
	$result = mysql_query("select name from mm_usertable where name=\"".
	quote_smart($_REQUEST{"bossname"})."\""
	, $dbhandle)
	or error_message("Query(3) failed : " . mysql_error());
	if (mysql_num_rows($result) == 0)
	{
		error_message("Guild master does not exist.");
	}
	// check that bossname is not already a guildmaster
	$result = mysql_query("select bossname from mm_guilds where bossname=\"".
	quote_smart($_REQUEST{"bossname"})."\" and name <> \"".
	quote_smart($_REQUEST{"editguildname"})."\""
	, $dbhandle)
	or error_message("Query(4) failed : " . mysql_error());
	if (mysql_num_rows($result) != 0)
	{
		error_message("This person is already a Guild master of another guild.");
	}
	// check numeric stuff
	if (!is_numeric($_REQUEST{"maxguilddeath"})) 
	{
		error_message("maxguilddeath field does not contain a number.");
	} 
	if (!is_numeric($_REQUEST{"minguildmembers"}))
	{
		error_message("minguildmembers  field does not contain a number.");
	}
	if (!is_numeric($_REQUEST{"minguildlevel"}))
	{
		error_message("minguildlevel field does not contain a number.");
	}
	
	$query = "update mm_guilds ".
	"set title = '".
	quote_smart($_REQUEST{"guildtitle"}).
	"', maxguilddeath = ".
	quote_smart($_REQUEST{"maxguilddeath"}).
	", minguildmembers = ".
	quote_smart($_REQUEST{"minguildmembers"}).
	", minguildlevel = ".
	quote_smart($_REQUEST{"minguildlevel"}).
	", guilddescription = '".
	quote_smart($_REQUEST{"guilddescription"}).
	"', guildurl = '".
	quote_smart($_REQUEST{"guildurl"}).
	"', bossname = '".
	quote_smart($_REQUEST{"bossname"}).
	"', owner = '".
	quote_smart($_COOKIE["karchanadminname"]).
	"' where (owner is null or owner = \"\" or owner = '".
	quote_smart($_COOKIE["karchanadminname"]).
	"') and name = '".
	quote_smart($_REQUEST{"editguildname"}).
	"'";
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	writeLogLong($dbhandle, "Changed guild ".$_REQUEST{"editguildname"}.".", $query);
}
if (!isset($_REQUEST{"guildname"}))
{
	$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2
		from mm_guilds"
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result)) 
	{
		printf("<b>name:</b> <A HREF=\"/scripts/admin_guilds.php?guildname=%s\">%s</A><BR>", $myrow["name"], $myrow["name"]);
	}
}
else
{
	$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2
		from mm_guilds where name = \"".
		quote_smart($_REQUEST{"guildname"})."\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result)) 
	{
		if ($myrow["owner"] == null || $myrow["owner"] == "" ||
			$myrow["owner"] == $_COOKIE["karchanadminname"])
		{
			printf("<FORM METHOD=\"GET\" ACTION=\"/scripts/admin_guilds.php\">");
			printf("<b>");
			printf("<TABLE>");
			printf("<TR><TD><b>name:</b></TD><TD><INPUT TYPE=\"hidden\" NAME=\"editguildname\" VALUE=\"%s\">%s</TD></TR>", $myrow["name"], $myrow["name"]);
			printf("<TR><TD><b>title:</b></TD><TD><INPUT TYPE=\"text\" NAME=\"guildtitle\" VALUE=\"%s\"  SIZE=\"40\" MAXLENGTH=\"100\"></TD></TR> ", $myrow["title"]);
			printf("<TR><TD><b>daysguilddeath:</b></TD><TD>%s</TD></TR>", $myrow["daysguilddeath"]);
			printf("<TR><TD><b>maxguilddeath:</b></TD><TD><INPUT TYPE=\"text\" NAME=\"maxguilddeath\" VALUE=\"%s\"  SIZE=\"10\" MAXLENGTH=\"10\"></TD></TR>", $myrow["maxguilddeath"]);
			printf("<TR><TD><b>minguildmembers:</b></TD><TD><INPUT TYPE=\"text\" NAME=\"minguildmembers\" VALUE=\"%s\"  SIZE=\"10\" MAXLENGTH=\"10\"></TD></TR>", $myrow["minguildmembers"]);
			printf("<TR><TD><b>minguildlevel:</b></TD><TD><INPUT TYPE=\"text\" NAME=\"minguildlevel\" VALUE=\"%s\"  SIZE=\"10\" MAXLENGTH=\"10\"></TD></TR>", $myrow["minguildlevel"]);
			printf("<TR><TD><b>guilddescription:</b></TD><TD><TEXTAREA NAME=\"guilddescription\"  ROWS=\"14\" COLS=\"85\">%s</TEXTAREA></TD></TR>", $myrow["guilddescription"]);
			printf("<TR><TD><b>guildurl:</b></TD><TD><INPUT TYPE=\"text\" NAME=\"guildurl\" VALUE=\"%s\"  SIZE=\"100\" MAXLENGTH=\"100\"></TD></TR>", $myrow["guildurl"]);
			printf("<TR><TD><b>logonmessage:</b></TD><TD>%s</TD></TR>", $myrow["logonmessage"]);
			printf("<TR><TD><b>bossname:</b></TD><TD><INPUT TYPE=\"text\" NAME=\"bossname\" VALUE=\"%s\"  SIZE=\"20\" MAXLENGTH=\"20\"></TD></TR>", $myrow["bossname"]);
			printf("<TR><TD><b>active:</b></TD><TD>%s</TD></TR>", ($myrow["active"] == 1 ? "yes":"no"));
			printf("<TR><TD><b>owner:</b></TD><TD>%s</TD></TR>", $myrow["owner"]);
			printf("<TR><TD><b>creation:</b></TD><TD>%s</TD></TR>", $myrow["creation2"]);
			printf("</TABLE>");
			printf("<INPUT TYPE=\"submit\" VALUE=\"Change Guild\">");
			printf("</b>   ");
			printf("</FORM>");
?>
<FORM METHOD="GET" ACTION="/scripts/admin_ownership.php">
<b>
<INPUT TYPE="hidden" NAME="id" VALUE="<?php echo $myrow["name"] ?>">
<INPUT TYPE="hidden" NAME="removeownership" VALUE="8">
<INPUT TYPE="submit" VALUE="Remove Ownership">
</FORM>
</b><P>
<FORM METHOD="GET" ACTION="/scripts/admin_guilds.php">
<INPUT TYPE="hidden" NAME="removeguildname" VALUE="<?php echo $myrow["name"] ?>"> 
<INPUT TYPE="submit" VALUE="Delete Guild">
</FORM>
<?php
		}
		else
		{
			printf("<b>name:</b> %s<BR>", $myrow["name"]);
			printf("<b>title:</b> %s<BR>", $myrow["title"]);
			printf("<b>daysguilddeath:</b> %s<BR>", $myrow["daysguilddeath"]);
			printf("<b>maxguilddeath:</b> %s<BR>", $myrow["maxguilddeath"]);
			printf("<b>minguildmembers:</b> %s<BR>", $myrow["minguildmembers"]);
			printf("<b>minguildlevel:</b> %s<BR>", $myrow["minguildlevel"]);
			printf("<b>guilddescription:</b> %s<BR>", $myrow["guilddescription"]);
			printf("<b>guildurl:</b> %s<BR>", $myrow["guildurl"]);
			printf("<b>logonmessage:</b> %s<BR>", $myrow["logonmessage"]);
			printf("<b>bossname:</b> <A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A><BR>", $myrow["bossname"], $myrow["bossname"]);
			printf("<b>active:</b> %s<BR>", ($myrow["active"] == 1 ? "yes":"no"));
			printf("<b>owner:</b> %s<BR>", $myrow["owner"]);
			printf("<b>creation:</b> %s<BR><P>", $myrow["creation2"]);
		}
	}
	printf("<b>Members:</b><BR>");
	$result = mysql_query("select name from mm_usertable where guild = \"".
		quote_smart($_REQUEST{"guildname"})."\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result)) 
	{
		printf("<A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A> ", $myrow["name"], $myrow["name"]);
	}
	printf("<P><b>Hopefuls:</b><BR>");
	$result = mysql_query("select charname from mm_charattributes 
		where name = \"guildwish\" and value=\"".
		quote_smart($_REQUEST{"guildname"})."\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result)) 
	{
		printf("<A HREF=\"/scripts/admin_chars.php?char=%s\">%s</A> ", $myrow["charname"], $myrow["charname"]);
	}
}
if (isset($_REQUEST{"removeguildname"}))
{
	// check that guild exists
	$result = mysql_query("select name from mm_guilds where name=\"".
	quote_smart($_REQUEST{"removeguildname"})."\""
	, $dbhandle)
	or error_message("Query(2) failed : " . mysql_error());
	if (mysql_num_rows($result) == 0)
	{
		error_message("Guild does not exist.");
	}
	
	$query = "delete from mm_guilds ".
	"where name = '".
	quote_smart($_REQUEST{"removeguildname"}).
	"' and (owner is null or owner = \"\" or owner = '".
	quote_smart($_COOKIE["karchanadminname"]).
	"')";
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	writeLogLong($dbhandle, "Removed guild ".$_REQUEST{"removeguildname"}.".", $query);
}
if (isset($_REQUEST{"addguildname"}))
{
	// guildname must be at least 5 characters.
	if (strlen($_REQUEST{"addguildname"})<3)
	{
		error_message("Guild name must be at least 3 characters.");
	}
	// check that guild not already exists
	$result = mysql_query("select name from mm_guilds where name=\"".
	quote_smart($_REQUEST{"addguildname"})."\""
	, $dbhandle)
	or error_message("Query(5) failed : " . mysql_error());
	if (mysql_num_rows($result) != 0)
	{
		error_message("Guild already exists.");
	}
	// check that bossname exists
	$result = mysql_query("select name from mm_usertable where name=\"".
	quote_smart($_REQUEST{"bossname"})."\""
	, $dbhandle)
	or error_message("Query(6) failed : " . mysql_error());
	if (mysql_num_rows($result) == 0)
	{
		error_message("Guild master does not exist.");
	}
	// check that bossname is not already a guidmaster
	$result = mysql_query("select bossname from mm_guilds where bossname=\"".
	quote_smart($_REQUEST{"bossname"})."\""
	, $dbhandle)
	or error_message("Query(7) failed : " . mysql_error());
	if (mysql_num_rows($result) != 0)
	{
		error_message("This person is already a Guild master of another guild.");
	}

	$query = "insert into mm_guilds (name, bossname, creation, owner) values('".
	quote_smart($_REQUEST{"addguildname"}).
	"', '".
	quote_smart($_REQUEST{"bossname"}).
	"', now(), '".
	quote_smart($_COOKIE["karchanadminname"]).
	"')";
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	writeLogLong($dbhandle, "Added guild ".$_REQUEST{"addguildname"}.".", $query);
	$query = "update mm_usertable set guild='".
	quote_smart($_REQUEST{"addguildname"}).
	"' where name='".
	quote_smart($_REQUEST{"bossname"}).
	"'";
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	writeLogLong($dbhandle, "Added guildmaster ".$_REQUEST{"bossname"}." to guild ".$_REQUEST{"addguildname"}.".", $query);
}
mysql_close($dbhandle);
?>

<FORM METHOD="GET" ACTION="/scripts/admin_guilds.php">
<b>
<TABLE>
<TR><TD>guildname</TD><TD><INPUT TYPE="text" NAME="addguildname" VALUE=""  SIZE="40" MAXLENGTH="40"></TD></TR> 
<TR><TD>bossname</TD><TD><INPUT TYPE="text" NAME="bossname" VALUE="" SIZE="40" MAXLENGTH="40"></TD></TR> 
</TD></TR> 
</TABLE>
<INPUT TYPE="submit" VALUE="Add Guild">
</b>   
</FORM>


</BODY>
</HTML>
