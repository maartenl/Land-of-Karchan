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

<A HREF="/scripts/admin_methods.php?methodstartswith=A">A</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=B">B</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=C">C</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=D">D</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=E">E</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=F">F</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=G">G</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=H">H</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=I">I</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=J">J</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=K">K</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=L">L</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=M">M</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=N">N</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=O">O</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=P">P</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=Q">Q</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=R">R</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=S">S</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=T">T</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=U">U</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=V">V</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=W">W</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=X">X</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=Y">Y</A>
<A HREF="/scripts/admin_methods.php?methodstartswith=Z">Z</A>
<P>

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

if (isset($_REQUEST{"methodname"}))
{
        $result = mysql_query("select *, 
        replace(replace(replace(src, \"&\", \"&amp;\"), \">\",\"&gt;\"), \"<\", \"&lt;\") 
        as src2, date_format(creation, \"%Y-%m-%d %T\") as creation2 
	from mm_methods where name like \"".
	quote_smart($_REQUEST{"methodname"}).
	"%\""
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
}
else
{
        if (isset($_REQUEST{"methodstartswith"}))
        {
        $result = mysql_query("select *, 
        replace(replace(replace(src, \"&\", \"&amp;\"), \">\",\"&gt;\"), \"<\", \"&lt;\") 
        as src2, date_format(creation, \"%Y-%m-%d %T\") as creation2 
	from mm_methods where name like \"".
	quote_smart($_REQUEST{"methodstartswith"}).
	"%\""
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
	}
	else
	{
	$result = mysql_query("select *, 
	replace(replace(replace(src, \"&\", \"&amp;\"), \">\",\"&gt;\"), \"<\", \"&lt;\") 
	as src2, date_format(creation, \"%Y-%m-%d %T\") as creation2 
  	from mm_methods where name = \"completebogyd\""
	, $dbhandle)
  	or error_message("Query failed : " . mysql_error());
  	}
}	
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>name:</b> <A
HREF=\"/scripts/admin_methods.php?methodname=%s\">%s</A> ",
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
<FORM METHOD="POST" ACTION="/scripts/admin_methods.php">
<b>
<INPUT TYPE="hidden" NAME="deletemethodname" VALUE="<?php echo $myrow["name"] ?>">
<INPUT TYPE="submit" VALUE="Delete Method">
</b>
</FORM>
<FORM METHOD="POST" ACTION="/scripts/admin_methods.php">
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
<H3>Related Commands</H3><UL>
<?php
	$getcommands = mysql_query("select id, command 
  	from mm_commands where method_name = \"".
  	$_REQUEST{"methodname"}.
  	"\""
	, $dbhandle)
  	or error_message("Query failed : " . mysql_error());
  	while ($mycommand = mysql_fetch_array($getcommands)) 
  	{
  	  ?>
  	  <LI><A HREF="/scripts/admin_commands.php?commandid=<?php echo $mycommand["id"] ?>">
  	  <?php echo $mycommand["command"] ?></A><BR>
  	  <?php
  	}
  	echo "</UL><H3>Related Events</H3><UL>";
	$getevents = mysql_query("select eventid 
  	from mm_events where method_name = \"".
  	$_REQUEST{"methodname"}.
  	"\""
	, $dbhandle)
  	or error_message("Query failed : " . mysql_error());
  	while ($myevent = mysql_fetch_array($getevents)) 
  	{
  	  ?>
  	  <LI><A HREF="/scripts/admin_events.php?eventid=<?php echo $myevent["eventid"] ?>">
  	  <?php echo $myevent["eventid"] ?></A><BR>
  	  <?php
  	}
	}
}

mysql_close($dbhandle);
?>
</UL>
<FORM METHOD="POST" ACTION="/scripts/admin_methods.php">
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
