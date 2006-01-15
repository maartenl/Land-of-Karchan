<?
/*-------------------------------------------------------------------------
svninfo: $Id: wholist.php 992 2005-10-23 08:03:45Z maartenl $
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
Land of Karchan - Fortune 100
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H2>
<IMG SRC="/images/gif/dragon.gif">
List of Rich People</H2>
<TABLE>
<TR><TD><B>Position</B></TD><TD><B>Name</B></TD><TD><B>Money</B></TD></TR>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
$result = mysql_query("select name, floor(copper/100) as gold, floor((copper % 100)/10) as silver, copper % 10 as copper
	from mm_usertable 
	where god<=1
	order by gold desc, silver desc, copper desc, name asc
	limit 100"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
//	error_message("Query failed : " . mysql_error());
$i = 1;
while ($myrow = mysql_fetch_array($result)) 
{
		if ($i < 4) 
		{
			printf("<TR><TD>%s</TD><TD><B>%s</B></TD><TD><B>%s gold, %s silver, %s copper</B></TD></TR>\r\n",
			$i++, $myrow["name"], $myrow["gold"], $myrow["silver"], $myrow["copper"]);
		}
		else
		{
			printf("<TR><TD>%s</TD><TD>%s</TD><TD>%s gold, %s silver, %s copper</TD></TR>\r\n",
			$i++, $myrow["name"], $myrow["gold"], $myrow["silver"], $myrow["copper"]);
		}
}
mysql_close($dbhandle);
?>
</TABLE>
<hr>

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
