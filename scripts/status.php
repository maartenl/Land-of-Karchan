<?
/*-------------------------------------------------------------------------
svninfo: $Id: status.php 1078 2006-01-15 09:25:36Z maartenl $
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
Land of Karchan - Status
</TITLE>
</HEAD>
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1><IMG SRC="/images/gif/dragon.gif">
Status of Land of Karchan</H1>

There is a hierarchie in the game and some have more powers and some less.
The reason for this that some people have to be accountable to others, and
everybody is accountable to the all mighty Implementor in the sky. The
hierachy is as follows:<P>

<H3>Implementors</H3>
<UL>
<LI><A HREF="mailto:karn@karchan.org">Karn</A>, Ruler of Karchan, Keeper of the Key to the Room of Lost Souls
</UL>
<H3><A HREF="mailto:deputy@karchan.org">Administrators</A></H3>
<UL>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 

// show results
$result = mysql_query("select mm_admin.name, title from mm_admin, mm_usertable 
where mm_admin.name = mm_usertable.name and mm_admin.validuntil > now()"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	echo "<LI><A HREF=\"mailto:".
		$myrow["name"].
		"@karchan.org\">".
		$myrow["name"].
		"</A>, ".
		$myrow["title"] . "\r\n";
}
mysql_close($dbhandle);
?>

</UL>
<P>

<script language="JavaScript">

<!-- In hiding!
 browserName = navigator.appName;          
           browserVer = parseInt(navigator.appVersion);
               backon = new Image;          
               backon.src = "/images/gif/webpic/new/buttono.gif";
               
               
               backoff = new Image;
               backoff.src = "/images/gif/webpic/buttono.gif";
               

function img_act(imgName) {
        imgOn = eval(imgName + "on.src");
        document [imgName].src = imgOn;
}

function img_inact(imgName) {
        imgOff = eval(imgName + "off.src");
        document [imgName].src = imgOff;
}

//-->
</SCRIPT>
<P><BR><BR>
<A HREF="/karchan/help/helpindex.html" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
<IMG ALT="Backitup!" SRC="/images/gif/webpic/buttono.gif" BORDER="0" name="back"><br></A> </TD>
<DIV ALIGN=right>Last Updated $Date: 2006-01-15 10:25:36 +0100 (Sun, 15 Jan 2006) $
</DIV>
</BODY>
</HTML>

