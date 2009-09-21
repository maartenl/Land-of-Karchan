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

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

/* the following constraints need to be checked before any kind of update is
to take place:

changing command:
adding command:
- check that method exists
<?php
/* 
------------------------------------------------------------------
COMMANDS
------------------------------------------------------------------
*/
?>

<H1><A HREF="/karchan/admin/help/scripting2.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A>
Commands</H1>

<A HREF="/scripts/admin_commands.php?commandstartswith=A">A</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=B">B</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=C">C</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=D">D</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=E">E</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=F">F</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=G">G</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=H">H</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=I">I</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=J">J</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=K">K</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=L">L</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=M">M</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=N">N</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=O">O</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=P">P</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=Q">Q</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=R">R</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=S">S</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=T">T</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=U">U</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=V">V</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=W">W</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=X">X</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=Y">Y</A>
<A HREF="/scripts/admin_commands.php?commandstartswith=Z">Z</A>
<P>

<?php
if ( isset($_REQUEST{"commandid"})
	&& isset($_REQUEST{"commandroom"})
    && isset($_REQUEST{"commandmethodname"})
    && isset($_REQUEST{"commandcallable"}) )
{
	// check proper information formats
	if ( !is_numeric($_REQUEST{"commandid"}) )
	{
		error_message("Expected commandid (".$_REQUEST{"commandid"}.") to be an integer, and it wasn't.");
	}
	// check that the command is the proper owner.
    $result = mysql_query("select id from mm_commands where id = \"".
        quote_smart($_REQUEST{"commandid"}).  
        "\" and (owner is null or owner = \"".   
        quote_smart($_COOKIE["karchanadminname"]).
        "\")"
        , $dbhandle)
        or error_message("Query(1) failed : " . mysql_error());
    if (mysql_num_rows($result) != 1)
    {
        error_message("You are not the owner of this command.");
    }
	// check to see that method exists
	$result = mysql_query("select 1 from mm_methods where name = \"".
		quote_smart($_REQUEST{"commandmethodname"}).
		"\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		error_message("Method does not exist.");
	}
    if ( ($_REQUEST{"commandcallable"} != "1") &&
        ($_REQUEST{"commandcallable"} != "0") )
    {
        error_message("Callable should be either 0 or 1.");
    }
	// check to see that room exists
	if (trim($_REQUEST{"commandroom"}) != "")
	{
		$result = mysql_query("select 1 from mm_rooms where id = \"".
			quote_smart($_REQUEST{"commandroom"}).
			"\""
			, $dbhandle)
			or error_message("Query failed : " . mysql_error());
		if (mysql_num_rows($result) != 1)
		{
			error_message("Room ".$_REQUEST{"commandroom"}." does not exist.");
		}
	}
	// make that change
	$query = "update mm_commands set command=\"".
		quote_smart($_REQUEST{"commandcommand"}).
		"\", callable=\"".
		quote_smart($_REQUEST{"commandcallable"}).
		"\", method_name=\"".
		quote_smart($_REQUEST{"commandmethodname"}).
		"\", room=".
		(trim($_REQUEST{"commandroom"}) != "" ?
			"\"".quote_smart($_REQUEST{"commandroom"})."\"":
			"null").
		", owner=\"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\" where id = \"".
		quote_smart($_REQUEST{"commandid"}).
		"\"";
    mysql_query($query
        , $dbhandle)
        or error_message("Query(8) failed : " . mysql_error());
    writeLogLong($dbhandle, "Changed command ".$_REQUEST{"commandid"}.
		".", $query);
}
if (isset($_REQUEST{"addcommandname"}) &&
   isset($_REQUEST{"addcommandmethodname"}))
{
	// check to see that method exists
	$result = mysql_query("select 1 from mm_methods where name = \"".
		quote_smart($_REQUEST{"addcommandmethodname"}).
		"\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	if (mysql_num_rows($result) != 1)
	{
		error_message("Method does not exist.");
	}
	// create new id
	$result = mysql_query("select max(id) as id from mm_commands"
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	$maxid = 0;
	while ($myrow = mysql_fetch_array($result)) 
	{
		$maxid = $myrow["id"];
	}
	if ($maxid == 0)
	{
		error_message("Could not compute id for command.");
	}
	
	// make that change
	$query = "insert into mm_commands (id, command, method_name, owner) values(".
		($maxid + 1).
		", \"".
		quote_smart($_REQUEST{"addcommandname"}).
		"\", \"".
		quote_smart($_REQUEST{"addcommandmethodname"}).
		"\", \"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\")";
    mysql_query($query
        , $dbhandle)
        or error_message("Query(8) failed : " . mysql_error());
    writeLogLong($dbhandle, "Added command ".$_REQUEST{"addcommandname"}.".", $query);
}
if (isset($_REQUEST{"deletecommandid"}))
{
	if ( !is_numeric($_REQUEST{"deletecommandid"}))
	{
		error_message("Expected commandid to be an integer, and it wasn't.");
	}
        $query = "delete from mm_commands where id = ".
		quote_smart($_REQUEST{"deletecommandid"}).
		" and (owner is null or owner = \"".
		quote_smart($_COOKIE["karchanadminname"]).
		"\")";
	mysql_query($query, $dbhandle)
		or error_message("Query (".$query.") failed : " . mysql_error());
	if (mysql_affected_rows() != 1)
	{
		error_message("Command does not exist or not proper owner.");
	}
    writeLogLong($dbhandle, "Removed command ".$_REQUEST{"deletecommandid"}.".", $query);
}
if (isset($_REQUEST{"commandid"}))
{
	$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2 
		from mm_commands where id like ".
			quote_smart($_REQUEST{"commandid"}).
			""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
}
else
{
	if (isset($_REQUEST{"commandstartswith"}))
	{
	$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2 
		from mm_commands where command like \"".
			quote_smart($_REQUEST{"commandstartswith"}).
			"%\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	}
	else
	{
	$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2 
		from mm_commands where command = \"completelybogyd\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	}
}
while ($myrow = mysql_fetch_array($result)) 
{
	if ( ($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"]) )
	{
		printf("<b>id:</b> <A
    	   HREF=\"/scripts/admin_commands.php?commandid=%s\">%s</A> ",
	       $myrow["id"], $myrow["id"]);
	}
	else
	{
		printf("<b>id:</b> %s ",
	       $myrow["id"]);
	}
	printf("<b>callable:</b> %s ", ($myrow["callable"]=="1"?"yes":"no"));
	printf("<b>command:</b> %s ", $myrow["command"]);
	printf("<b>method_name:</b> <A
HREF=\"/scripts/admin_methods.php?methodname=%s\">%s</A> ", $myrow["method_name"], $myrow["method_name"]);
	if ($myrow["room"] != null)
	{
		printf("<b>room:</b> <A
        HREF=\"/scripts/admin_rooms.php?room=%s\">%s</A> ",
        $myrow["room"], $myrow["room"]);
	}
	printf("<b>owner:</b> %s ", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	if ( ($_REQUEST{"commandid"} == $myrow["id"]) &&
		($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"]) )
	{
?>
<FORM METHOD="POST" ACTION="/scripts/admin_commands.php">
<b>
<INPUT TYPE="hidden" NAME="deletecommandid" VALUE="<?php echo $myrow["id"] ?>">
<INPUT TYPE="submit" VALUE="Delete Command">
</b>
</FORM>
<FORM METHOD="POST" ACTION="/scripts/admin_commands.php">
<b>
<INPUT TYPE="hidden" NAME="commandid" VALUE="<?php echo $myrow["id"] ?>">
<TABLE>
<TR><TD>command:</TD><TD><INPUT TYPE="text" NAME="commandcommand" VALUE="<?php echo $myrow["command"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>room:</TD><TD><INPUT TYPE="text" NAME="commandroom" VALUE="<?php echo $myrow["room"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>method_name:</TD><TD><INPUT TYPE="text" NAME="commandmethodname" VALUE="<?php echo $myrow["method_name"] ?>" SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>callable</TD><TD><SELECT NAME="commandcallable" SIZE="2">
<option value="1" <?php if ($myrow["callable"] == "1") {printf("selected");} ?>>yes
<option value="0" <?php if ($myrow["callable"] == "0") {printf("selected");} ?>>no
</SELECT></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Change Command">
</b>
</FORM>

<FORM METHOD="GET" ACTION="/scripts/admin_ownership.php">
<b>
<INPUT TYPE="hidden" NAME="id" VALUE="<?php echo $myrow["id"] ?>">
<INPUT TYPE="hidden" NAME="removeownership" VALUE="2">
<INPUT TYPE="submit" VALUE="Remove Ownership">
</b>
</FORM>
<?php
	}
}
?>
<FORM METHOD="POST" ACTION="/scripts/admin_commands.php">
<b>
Command name: <INPUT TYPE="text" NAME="addcommandname" VALUE="">
<BR>
Method name: <INPUT TYPE="text" NAME="addcommandmethodname" VALUE="">
<INPUT TYPE="submit" VALUE="Add Command">
</b>
</FORM>
<P>

<a HREF="/karchan/admin/scripts.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
