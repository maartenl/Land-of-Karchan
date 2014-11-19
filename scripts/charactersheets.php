<?
/*-------------------------------------------------------------------------
svninfo: $Id: charactersheets.php 1078 2006-01-15 09:25:36Z maartenl $
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
	or error_message("Query failed : " . mysql_error());
$numrows = ceil(mysql_num_rows($result) / 5);
$beginstuff = 'x';
$counter = 1;
while ($myrow = mysql_fetch_array($result)) 
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


<?php
if (($_COOKIE{"karchanpassword"} != "") &&
	($_COOKIE["karchanname"] != ""))
{
?>
</UL>
<FORM METHOD="GET" ACTION="/scripts/editcharactersheet.php">
<INPUT TYPE="submit" VALUE="Edit Your Character Sheet">
</FORM>
<hr>

<?php
}
?>

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
<P>
<A HREF="/karchan/chronicles/chronicles2.html" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
<IMG ALT="Backitup!" SRC="/images/gif/webpic/buttono.gif" BORDER="0" name="back"><br></A> </TD>
<DIV ALIGN=right>Last Updated $Date: 2006-01-15 10:25:36 +0100 (Sun, 15 Jan 2006) $
</DIV>
</BODY>
</HTML>
