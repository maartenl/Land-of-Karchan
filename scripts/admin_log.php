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
Land of Karchan - Admin
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Logs <?php echo $_REQUEST{"status"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
if ($_REQUEST{"status"} = "1")
{
	$query = "select date_format(creation), name, message, addendum from mm_log order by creation";
}
if ($_REQUEST{"status"} = "2")
{
	$query = "select date_format(creation, \"%Y-%c-%e %T\"), name, message, addendum from mm_log where creation > now() -  INTERVAL 7 day order by creation";
}
if ($_REQUEST{"status"} = "3")
{
	$query = "select date_format(creation, \"%Y-%c-%e %T\"), name, message, addendum from mm_log where creation > now() - INTERVAL 1 day order by creation";
}
$result = mysql_query($query
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	if ($myrow["addendum"] == "")
	{
		printf("<b>creation:</b> %s <b>name:</b> %s <b>message:</b> %s<BR> ", $myrow[0],
			$myrow[1], $myrow[2]);
	}
	else
	{
		printf("<b>creation:</b> %s <b>name:</b> %s <b>message:</b>
%s<PRE>%s</PRE><BR> ", $myrow[0],
			$myrow[1], $myrow[2], $myrow["addendum"]);
	}
}

mysql_close($dbhandle);
?>

<a HREF="/scripts/admin.php">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
