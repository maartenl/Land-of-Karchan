<?
/*-------------------------------------------------------------------------
cvsinfo: $Header$
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
Land of Karchan - Character Sheets
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Character Sheets</H1>

I feel the following needs a little explanation. Below you see a list of
available Character Sheets. They contain personal information like name,
title, place of birth, and the story line of characters, and references to
other characters. In each case these are put together by the people that
originally created the character on the game.<P> It provides valuable
insights into the story behind this Game.<P> Now you can add your piece of
information as well. Just fill in your name and password of the character
you created on the mud, and you will be presented with a form that you can
fill out, and change later in the same way.<P>
<TABLE ALIGN=top BORDER=1><TR><TD><UL>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
$result = mysql_query("select concat(\"<A
	HREF=\\\"/scripts/charactersheet.php?name=\",mm_usertable.name,\"\\\">\",mm_usertable.name,\"</A>\") 
	, mm_usertable.name from characterinfo, mm_usertable 
	where mm_usertable.name=characterinfo.name"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
$numrows = ceil(mysql_num_rows($result) / 5);
$beginstuff = 'x';
$counter = 1;
while ($myrow = mysql_fetch_row($result)) 
{
	$myrow[1] = ucfirst($myrow[1]);
	if ($myrow[1]{0} != $beginstuff)
	{
		$beginstuff = $myrow[1]{0};
		printf("</UL><H1>%s</H1><UL>\r\n", $beginstuff);
	}
	printf("%s<BR>", $myrow[0]);
	if ($counter++ > $numrows)
   {
		printf("</TD>\r\n<TD>");
		$counter=1;
   }
}
mysql_close($dbhandle);
?>
</TR></TABLE>

</UL>
<FORM METHOD="GET" ACTION="/scripts/editcharactersheet.php">
<HR>
(Fictional) Name:<BR>
<INPUT TYPE="text" NAME="name" VALUE="" SIZE="19" MAXLENGTH="19"><P>
Password:<BR>
<INPUT TYPE="password" NAME="password" VALUE="" SIZE="10" MAXLENGTH="39">
<P>
<INPUT TYPE="submit" VALUE="Edit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>
<hr>

<a HREF="/karchan/chronicles/chronicles2.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
