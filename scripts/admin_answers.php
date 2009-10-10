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

if (!isset($_REQUEST{"bot"}))
{
printf("The following bots have answers:<P>\r\n");
$result = mysql_query("select distinct name from mm_answers "
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<A HREF=\"/scripts/admin_answers.php?bot=%s\">%s</A><BR>", $myrow["name"], $myrow["name"]);
}
}
else
/**
 * verify form information
 */
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
		printf("<TR><TD>%s</TD><TD>%s</TD></TR>\r\n", $myrow["question"], $myrow["answer"]);
	}
	printf("</TABLE>");

	$result = mysql_query("select owner from mm_usertable where name = \"".
		quote_smart($_REQUEST{"bot"}).
		"\" and (owner is null or owner = \"".   
		quote_smart($_COOKIE["karchanadminname"]).
		"\")"
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	if ($myrow = mysql_fetch_array($result)) 
	{
		if (isset($_REQUEST{"bot_answer"}))
		{
			// make that change.
			$query = "replace into mm_answers (name, question, answer) ".
			"values(\"".
			quote_smart($_REQUEST{"bot"}).
			"\", \"".
			quote_smart($_REQUEST{"bot_question"}).
			"\", \"".
			quote_smart($_REQUEST{"bot_answer"}).
			"\")";
			mysql_query($query
			, $dbhandle)
			or error_message("Query(as) failed : " . mysql_error());
			writeLogLong($dbhandle, "Changed answer for ".$_REQUEST{"bot"}.".", $query);
		}
		if (isset($_REQUEST{"remove_question"}))
		{
			// make that change.
			$query = "delete from mm_answers where name=\"".
			quote_smart($_REQUEST{"bot"}).
			"\" and question = \"".
			quote_smart($_REQUEST{"remove_question"}).
			"\"";
			mysql_query($query
			, $dbhandle)
			or error_message("Query(afg) failed : " . mysql_error());
			writeLogLong($dbhandle, "Removed answer for ".$_REQUEST{"bot"}.".", $query);
		}
		?>
<FORM METHOD="POST" ACTION="/scripts/admin_answers.php">
<b>
<INPUT TYPE="hidden" NAME="bot" VALUE="<?php echo $_REQUEST{"bot"} ?>">
Question: <INPUT TYPE="text" SIZE="100" NAME="bot_question" VALUE="<?php echo $_REQUEST{"bot_question"} ?>"><BR>
Answer: <INPUT TYPE="text" SIZE="100" NAME="bot_answer" VALUE="<?php echo $_REQUEST{"bot_answer"} ?>"><BR>
<INPUT TYPE="submit" VALUE="Submit Answer">
</b>
</FORM>
<P>
<FORM METHOD="POST" ACTION="/scripts/admin_answers.php">
<b>
<INPUT TYPE="hidden" NAME="bot" VALUE="<?php echo $_REQUEST{"bot"} ?>">
Question: <INPUT TYPE="text" SIZE="100" NAME="remove_question" VALUE="<?php echo $_REQUEST{"bot_question"} ?>"><BR>
<INPUT TYPE="submit" VALUE="Delete Question">
</b>
</FORM>
<P>
		
		<?php
	}
	

}
mysql_close($dbhandle);
?>

<a HREF="/scripts/admin_answers.php">
<img SRC="/images/gif/webpic/buttono.gif"
BORDER="0"></a><p>

</BODY>
</HTML>
