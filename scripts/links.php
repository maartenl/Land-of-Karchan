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
Land of Karchan - Links
</TITLE>
</HEAD>
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1><IMG SRC="/images/gif/dragon.gif">Links</H1>

Hello, and welcome to the <I>Links</I> page. Here I put links to other
homepages concerning both Karchan (indirect and direct) and anything
relating to fantasy.

<H1>Homepages of Other Karchanians</H1>
</UL>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
if (($_REQUEST{"linkname"} != "") &&
	($_COOKIE["karchanname"] != ""))
{
	if (($_REQUEST{"type"} != "1") &&
		($_REQUEST{"type"} != "2") &&
		($_REQUEST{"type"} != "3"))
	{
		die("Wrong link type!");
	}

        $_REQUEST{"url"} = stripslashes($_REQUEST{"url"});
        $_REQUEST{"linkname"} = stripslashes($_REQUEST{"linkname"});
	$query = "replace into links 
		(linkname, url, type, name)
		select \"".
		mysql_escape_string($_REQUEST{"linkname"})."\", \"".
		mysql_escape_string($_REQUEST{"url"})."\", ".
		mysql_escape_string($_REQUEST{"type"}).", name 
		from mm_usertable
		where name = \"".
		mysql_escape_string($_COOKIE["karchanname"]).
		"\" and lok = \"".
		mysql_escape_string($_COOKIE["karchanpassword"]).
		"\"";
	$result = mysql_query($query
		, $dbhandle)
		or die("Query failed : " . mysql_error());
}

$result = mysql_query("select * from links where type = 1 order by creation"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<LI><A HREF=\"".$myrow["url"]."\">".
		$myrow["linkname"]."</A> (<i>".$myrow["name"]."</i>)<P>");
}
?>
</UL>

<H1>General Links</H1>

<UL>
<?php
$result = mysql_query("select * from links where type = 2 order by creation"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<LI><A HREF=\"".$myrow["url"]."\">".
		$myrow["linkname"]."</A> (<i>".$myrow["name"]."</i>)<P>");
}
?>
</UL>

<H1>Guild Homepages</H1>
<UL>
<?php

$result = mysql_query("select * from links where type = 3 order by creation"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<LI><A HREF=\"".$myrow["url"]."\">".
		$myrow["linkname"]."</A> (<i>".$myrow["name"]."</i>)<P>");
}

mysql_close($dbhandle);
?>
</UL>

<H1>Programming Karchan Homepages</H1>

<UL>
<LI>The Homepage of MMud - <A HREF="http://mmud.sourceforge.net">http://mmud.sourceforge.net</A><P>
<LI>The Administation Page of MMud - <A
HREF="http://www.sourceforge.net/projects/mmud">http://sourceforge.net/projects/mmud</A><P>
</UL>

<H1>Glossary</H1>

<DL>
<DT>MMud
<DD>the source code or gaming engine on which Land of Karchan is built.
<DT>Sourceforge
<DD>A free Software Project Development Webpage
</DL>
<DIV ALIGN=right>Last Updated $Date$
</DIV>

<?php
if (($_COOKIE{"karchanpassword"} != "") &&
	($_COOKIE["karchanname"] != ""))
{
?>
Add link:<FORM METHOD="GET" ACTION="/scripts/links.php">
Name of Website: <INPUT TYPE="text" NAME="linkname" VALUE="" SIZE="50" MAXLENGTH="255"><P>
Url of Website: <INPUT TYPE="text" NAME="url" VALUE="" SIZE="50" MAXLENGTH="255"><P>
Type: <BR><input type="radio" name="type" value="1" checked>Karchanian Homepage<BR>
<input type="radio" name="type" value="2">General Link<BR>
<input type="radio" name="type" value="3">Guild Homepage<BR>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<?php 
}
?>

<A HREF="/karchan/index.html"><IMG SRC="/images/gif/webpic/buttono.gif" BORDER="0"></A><P>

</BODY>
</HTML>

