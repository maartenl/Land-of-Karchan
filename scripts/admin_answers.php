<?
/*-------------------------------------------------------------------------
svninfo: $Id: admin_problems.php 1049 2005-11-15 10:54:04Z maartenl $
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
<IMG SRC="/images/gif/dragon.gif">Answers</H1>
This script is used for manipulating the answers that can be provided by bots.<P>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

printf("The following bots have answers:\r\n");
$result = mysql_query("select distinct name from mm_answers "
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<A HREF=\"/scripts/admin_answers.php?bot=%s\">%s</A><BR>", $myrow["name"], $myrow["name"]);
}

/**
 * verify form information
 */
if (isset($_REQUEST{"bot"}))
{
	printf($_REQUEST{"bot"}." has the following answers available:<BR><TABLE><TR><TD><B>Question</B></TD><TD><B>Answer</B></TD></TR>\r\n");
	$result = mysql_query("select question, answer ".
		"from mm_answers ".
		"where name = \"".
		quote_smart($_REQUEST{"bot"})."\""
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result)) 
	{
		printf("<TR><TD>%s</TD><TD>%s</TD></TR>", $myrow["question"], $myrow["answer"]);
	}
	printf("</TABLE>");
}
mysql_close($dbhandle);
?>

</BODY>
</HTML>
