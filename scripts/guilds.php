<?
/*-------------------------------------------------------------------------
svninfo: $Id: admin_items.php 992 2005-10-23 08:03:45Z maartenl $
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
Mmud - Guilds
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Guilds</H1>
<DL>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php";

	$result = mysql_query("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2
		from mm_guilds order by title"
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result)) 
	{
		if ($myrow["guildurl"] == "")
		{
			printf("<DT><FONT SIZE=+2><B>%s</B> (%s)</FONT>", $myrow["title"], $myrow["bossname"]);
		}
		else
		{
			printf("<DT><FONT SIZE=+2><A HREF=\"%s\"><B>%s</B></A> (%s)</FONT>", $myrow["guildurl"], $myrow["title"], $myrow["bossname"]);
		}
		printf("<DD>%s", $myrow["guilddescription"]);
		printf("Created on %s.<BR>", $myrow["creation2"]);
		$result2 = mysql_query("select count(*) as count from mm_usertable where guild=\"".
		$myrow["name"].
		"\""
			, $dbhandle)
			or error_message("Query failed : " . mysql_error());
		while ($myrow2 = mysql_fetch_array($result2)) 
		{
			printf("Currently has %s members.", $myrow2["count"]);
			printf("<P>");
		}
		printf("<P>");
	}
mysql_close($dbhandle);
?>
</DL>

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
<A HREF="/karchan/chronicles/chronicles2.html" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
<IMG ALT="Backitup!" SRC="/images/gif/webpic/buttono.gif" BORDER="0" name="back"><br></A> </TD>
<DIV ALIGN=right>Last Updated $Date: 2003-11-27 23:34:25 +0100 (Thu, 27 Nov 2003) $
</DIV>
</BODY>
</HTML>
