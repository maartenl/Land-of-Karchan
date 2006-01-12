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
Mmud - Ownership
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Areas</H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
/* 
showing the different areas and what rooms belong to which area.

the following constraints need to be checked before any kind of update
is to take place:

changing area:
- the area must exist
- is the administrator the owner of the area
*/

 
printf("<H2>Areas</H2>");

if (isset($_REQUEST{"area"}))
{
	// check that area exists
	$result = mysql_query("select area from mm_area where area=\"".
		quote_smart($_REQUEST{"area"})."\""
		, $dbhandle)
	or error_message("Query(2) failed : " . mysql_error());
	if (mysql_num_rows($result) == 0)
	{
		error_message("Area does not exist.");
	}
	$query = "update mm_area ".
	"set description = '".
	quote_smart($_REQUEST{"description"}).
	"', shortdesc = '".
	quote_smart($_REQUEST{"shortdesc"}).
	"', owner = '".
	quote_smart($_COOKIE["karchanadminname"]).
	"' where (owner is null or owner = \"\" or owner = '".
	quote_smart($_COOKIE["karchanadminname"]).
	"') and area = '".
	quote_smart($_REQUEST{"area"}).
	"'";
	mysql_query($query, $dbhandle)
	or error_message("Query (".$query.") failed : " . mysql_error());
	writeLogLong($dbhandle, "Changed area ".$_REQUEST{"area"}.".", $query);
}

$result = mysql_query("select area, description, shortdesc, owner, 
	date_format(creation, \"%Y-%m-%d %T\") as creation2
	from mm_area order by area"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result))
{
	if ($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"])
	{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_areas.php">
<TABLE>
<TR><TD><B>Area:</b></TD><TD> <?php echo $myrow["area"]?></TD></TR>
<INPUT TYPE="hidden" NAME="area" VALUE="<?php echo $myrow["area"]?>">
<TR><TD><B>Short Description:</b></TD><TD><INPUT TYPE="text" NAME="shortdesc" VALUE="<?php echo $myrow["shortdesc"]?>" SIZE="40" MAXLENGTH="255"></TD></TR>
<TR><TD><B>Long Description:</b></TD><TD><TEXTAREA NAME="description" ROWS="14" COLS="85"><?php echo $myrow["description"]?></TEXTAREA></TD></TR>
<TR><TD><B>owner:</b></TD><TD> <?php echo $myrow["owner"]?></TD></TR>
<TR><TD><B>creation:</B></TD><TD> <?php echo $myrow["creation2"]?></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Area">
</b>
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_ownership.php">
<INPUT TYPE="hidden" NAME="id" VALUE="<?php echo $myrow["area"] ?>">
<INPUT TYPE="hidden" NAME="removeownership" VALUE="9">
<INPUT TYPE="submit" VALUE="Remove Ownership">
</FORM>
<BR>
<?php
	}
	else
	{
		printf("<B>Area:</b> %s<BR>", $myrow["area"]);
		printf("<B>Short Description:</b> %s<BR>", $myrow["shortdesc"]);
		printf("<B>Long Description:</b> %s<BR>", $myrow["description"]);
		printf("<B>owner:</b> %s<BR>", $myrow["owner"]);
		printf("<B>creation:</B> %s<BR>", $myrow["creation2"]);
	}

	printf("<B>rooms:</B> ");
	$result2 = mysql_query("select id from mm_rooms where area = '"
	.quote_smart($myrow["area"])
	."' order by id"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
	while ($myrow2 = mysql_fetch_array($result2))
	{
		printf("<A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A> ", $myrow2["id"], $myrow2["id"]);
	}
	printf("<P><B>boundaries:</B> ");
	$result2 = mysql_query("select distinct mm_rooms1.id 
	from mm_rooms as mm_rooms1, mm_rooms as mm_rooms2 
	where mm_rooms1.area <> '"
	.quote_smart($myrow["area"])
	."' and mm_rooms2.area = '"
	.quote_smart($myrow["area"])
	."' and 
	mm_rooms1.id in (mm_rooms2.north, mm_rooms2.south, mm_rooms2.east, mm_rooms2.west, mm_rooms2.up, mm_rooms2.down) order by mm_rooms1.id"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
	while ($myrow2 = mysql_fetch_array($result2))
	{
		printf("<A HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A> ", $myrow2["id"], $myrow2["id"]);
	}
	printf("<P>");
}

mysql_close($dbhandle);
?>


</BODY>
</HTML>
