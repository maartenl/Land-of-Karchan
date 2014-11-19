<?php
/*-------------------------------------------------------------------------
svninfo: $Id: admin_boards.php 1140 2006-08-07 20:27:55Z maartenl $
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
<IMG SRC="/images/gif/dragon.gif">Public/Private Boards</H1>
<A HREF="/karchan/admin/help/boards.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A><P>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
$selection="";
$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as
        creation2 from mm_boards"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<b>id:</b> %s ", $myrow["id"]);
	printf("<b>name:</b> %s ", $myrow["name"]);
	printf("<b>owner:</b> %s ", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	printf("<b>description:</b> %s<BR>", $myrow["description"]);
	$selection .= "<option value=\"".$myrow["id"]."\">".$myrow["name"];
}
if ((isset($_REQUEST{"boardid"})) &&
	(isset($_REQUEST{"name"})))
{
	if (isset($_REQUEST{"posttime"}))
	{
		$query = "update mm_boardmessages set removed = if(removed=1,0,1)
			where boardid=\"".
				quote_smart($_REQUEST{"boardid"})."\" and name=\"".
				quote_smart($_REQUEST{"name"})."\" and posttime=\"".
				quote_smart($_REQUEST{"posttime"})."\"";
		mysql_query($query, $dbhandle)
			or error_message("Query failed : " . mysql_error());
		writeLogLong($dbhandle, "Removed/Unremoved message (".$_REQUEST{"boardid"}.", ".$_REQUEST{"name"}.", ".$_REQUEST{"posttime"}.").", $query);
	}
	$query = "select *, date_format(posttime, \"%Y-%m-%d %T\") as
    	    creation2 from mm_boardmessages where boardid=\"".
			quote_smart($_REQUEST{"boardid"})."\" and name=\"".
			quote_smart($_REQUEST{"name"})."\" order by posttime";
	$result = mysql_query($query, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result)) 
	{
		printf("[<A HREF=\"/scripts/admin_boards.php?boardid=%s&name=%s&posttime=%s\">%s</A>]",
			$myrow["boardid"], $myrow["name"], $myrow["posttime"],
			($myrow["removed"] == "1"? "ündo remove":"remove"));
		printf("<b>name:</b> %s ", $myrow["name"]);
		printf("<b>posttime:</b> %s <BR>", $myrow["creation2"]);
		printf("<b>contents:</b> %s <BR>", ($removed==1?"-removed-":$myrow["message"]));
	}
}

mysql_close($dbhandle);
?>

<FORM METHOD="GET" ACTION="/scripts/admin_boards.php">
<TABLE>
<TR><TD><B>Board:</b></TD><TD><SELECT NAME="boardid">
<?php	echo $selection;
?>
</SELECT></TD></TR>
<TR><TD><B>Character name:</b></TD><TD><INPUT TYPE="text" NAME="name" VALUE="" SIZE="40" MAXLENGTH="40"></TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Search for Userposts">
</b>
</FORM>
</BODY>
</HTML>
