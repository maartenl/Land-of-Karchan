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
<IMG SRC="/images/gif/dragon.gif">Public/Private Help</H1>
<A HREF="/karchan/admin/help/help.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A><P>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

if ( isset($_REQUEST{"newcommand"}) )
{
	// make that change.
	$query = "insert into mm_help (command, contents) values(\"".
		quote_smart($_REQUEST{"newcommand"}).
		"\",\"<H1></H1>
<DL>
<DT><B>NAME</B>
<DD><B></B>- formatted output<P>
<DT><B>SYNOPSIS</B>
<DD><B></B>[<B>to</B> <person>]<P>
<DT><B>DESCRIPTION</B>
<DD><B></B><P>
<DT><B>EXAMPLES</B>
<DD>
\\\"\\\"<P>
You: <TT></TT><BR>
Anybody: <TT></tt><P>
\\\"\\\"<P>
You: <TT></TT><BR>
Marvin: <TT></TT><BR>
Anybody: <TT></TT><P>
<DT><B>SEE ALSO</B>
<DD><P>
</DL>\")";
	mysql_query($query
		, $dbhandle)
		or die("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Created new help on command ".$_REQUEST{"newcommand"}.".", $query);
}
if ( isset($_REQUEST{"command"}) && 
	 isset($_REQUEST{"contents"}) )
{
	// make that change.
	$query = "update mm_help set contents=\"".
		quote_smart($_REQUEST{"contents"}).
		"\" where command = \"".
		quote_smart($_REQUEST{"command"})."\"";
	mysql_query($query
		, $dbhandle)
		or die("Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Changed help on command ".$_REQUEST{"command"}.".", $query);
}
$result = mysql_query("select command, contents from mm_help order by
command"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<BR><b>command:</b> <A
HREF=\"/scripts/admin_helptext.php?command=%s\">%s</A> ", 
		$myrow["command"], $myrow["command"]);
	if ( isset($_REQUEST{"command"}) && 
		($_REQUEST{"command"}== $myrow["command"]) )
	{
?>
<b>contents:</b> <?php echo $myrow["contents"]; ?>
<FORM METHOD="GET" ACTION="/scripts/admin_helptext.php">
<b>
<INPUT TYPE="hidden" NAME="command" VALUE="<?php echo $myrow["command"] ?>">
contents<BR><TEXTAREA NAME="contents" ROWS="14" COLS="85"><?php
echo $myrow["contents"] ?></TEXTAREA>
<BR>
<INPUT TYPE="submit" VALUE="Change Help">
</b>   
</FORM>

<?php
	}
}

mysql_close($dbhandle);
?>

<FORM METHOD="GET" ACTION="/scripts/admin_helptext.php">
<b>
command: <INPUT TYPE="text" NAME="newcommand" VALUE="">
<INPUT TYPE="submit" VALUE="Create Help">
</b>   

</FORM>
</BODY>
</HTML>
