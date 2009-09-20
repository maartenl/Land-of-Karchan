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
Methods</H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

/* the following constraints need to be checked before any kind of update is
to take place:

changing method:
- first check that the change is approved (i.e. owner or null)
adding method:
- no check
deleting method:
- check that no event is using this method
- check that no command is using this method
- check that correct owner or owner==null

/* 
------------------------------------------------------------------
METHODS
------------------------------------------------------------------
*/
?>

<H2><A HREF="/karchan/admin/help/scripting3.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A>
Methods</H2>

<?php

if (isset($_REQUEST{"methodname"}) && isset($_REQUEST{"src"}))
{
    // check that the method is the proper owner.
    // check it.
    $result = mysql_query("select name from mm_methods where name = \"".
        quote_smart($_REQUEST{"methodname"}).  
        "\" and (owner is null or owner = \"".   
        quote_smart($_COOKIE["karchanadminname"]).
        "\")"
        , $dbhandle)
        or error_message("Query(1) failed : " . mysql_error());
    if (mysql_num_rows($result) != 1)
    {
        error_message("You are not the owner of this method.");
    }
	// make that change
	$query = "update mm_methods set src=\"".
		quote_smart($_REQUEST{"src"}).
		"\", owner=\"".
		quote_smart($_COOKIE["karchanadminname"]).
                "\" where name = \"".
		quote_smart($_REQUEST{"methodname"}).
		"\"";
    mysql_query($query
        , $dbhandle)
        or error_message("Query(8) failed : " . mysql_error());
    writeLogLong($dbhandle, "Changed method ".$_REQUEST{"methodname"}.".", $query);
}
if (isset($_REQUEST{"addmethodname"}))
{
	// make that change
	$query = "insert into mm_methods (name, owner) values(\"".
		quote_smart($_REQUEST{"addmethodname"}).
		"\", \"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\")";
    mysql_query($query
        , $dbhandle)
        or error_message("Query(8) failed : " . mysql_error());
    writeLogLong($dbhandle, "Added method ".$_REQUEST{"addmethodname"}.".", $query);
}
if (isset($_REQUEST{"deletemethodname"}))
{
  // check if no event is using this method
  $result = mysql_query("select 1 from mm_events where method_name = \"".
    quote_smart($_REQUEST{"deletemethodname"}).
	"\"", $dbhandle);
        if (mysql_num_rows($result) > 0)
        {
            error_message("There are still events using this method.");
        }
        // check if no command is using this method
  $result = mysql_query("select 1 from mm_commands where method_name = \"".
    quote_smart($_REQUEST{"deletemethodname"}).
	"\"", $dbhandle);
        if (mysql_num_rows($result) > 0)
        {
            error_message("There are still commands using this method.");
        }

        // make it so
        $query = "delete from mm_methods where name = \"".
		quote_smart($_REQUEST{"deletemethodname"}).
		"\" and (owner is null or owner = \"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\")";
	mysql_query($query, $dbhandle)
		or error_message("Query (".$query.") failed : " . mysql_error());
	if (mysql_affected_rows() != 1)
	{
		error_message("Method does not exist or not proper owner.");
	}
    writeLogLong($dbhandle, "Removed method ".$_REQUEST{"deletemethodname"}.".", $query);
}

$result = mysql_query("select *, 
replace(replace(replace(src, \"&\", \"&amp;\"), \">\",\"&gt;\"), \"<\", \"&lt;\") 
as src2, date_format(creation, \"%Y-%m-%d %T\") as creation2 
	from mm_methods"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
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
<FORM METHOD="POST" ACTION="/scripts/admin_commandseventsmethods.php">
<b>
<INPUT TYPE="hidden" NAME="deletemethodname" VALUE="<?php echo $myrow["name"] ?>">
<INPUT TYPE="submit" VALUE="Delete Method">
</b>
</FORM>
<FORM METHOD="POST" ACTION="/scripts/admin_commandseventsmethods.php">
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

<FORM METHOD="GET" ACTION="/scripts/admin_ownership.php">
<b>
<INPUT TYPE="hidden" NAME="id" VALUE="<?php echo $myrow["name"] ?>">
<INPUT TYPE="hidden" NAME="removeownership" VALUE="1">
<INPUT TYPE="submit" VALUE="Remove Ownership">
</b>
</FORM>
<?php
	}
}

mysql_close($dbhandle);
?>

<FORM METHOD="POST" ACTION="/scripts/admin_commandseventsmethods.php">
<b>
Method name: <INPUT TYPE="text" NAME="addmethodname" VALUE="">
<INPUT TYPE="submit" VALUE="Add Method">
</b>
</FORM>
<P>

<a HREF="/karchan/admin/rooms.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
