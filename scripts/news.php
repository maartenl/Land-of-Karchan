<?
/*-------------------------------------------------------------------------
svninfo: $Id: news.php 992 2005-10-23 08:03:45Z maartenl $
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
Land of Karchan - News
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
News of Land of Karchan</H2>

<BR>
<IMG SRC="/images/gif/letters/w.gif"
ALT="W" ALIGN=left>
elcome to the Land of Karchan MUD, a land filled with mystery
and enchantment, where weapons, magic, intelligence, and common
sense play key roles in the realm. Where love and war can be one
and the same. Where elves coexist peacefully with the humans, and
make war with the dwarves. Where the sun rises, and the moon falls.
Where one can change into a hero with a single swipe of his
sword.<P>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
$result = mysql_query("select mm_boardmessages.name,  
	date_format(posttime, \"%W, %M %e, %H:%i\") as posttime, message
	from mm_boardmessages, mm_boards 
	where boardid=id and 
	mm_boards.name = \"logonmessage\"
	order by mm_boardmessages.posttime desc"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<hr>".$myrow["posttime"]."<p>".
		$myrow["message"]."<p><i>".$myrow["name"]."</i>");
}

mysql_close($dbhandle);
?>
<HR>

<A HREF="/karchan/help/helpindex.html">
<IMG SRC="/images/gif/webpic/buttono.gif"
BORDER="0"></A><P>

</BODY>
</HTML>   

