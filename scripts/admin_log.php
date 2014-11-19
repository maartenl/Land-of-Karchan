<?
/*-------------------------------------------------------------------------
svninfo: $Id: admin_log.php 1190 2009-09-25 16:54:08Z maartenl $
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
<IMG SRC="/images/gif/dragon.gif">
Logs</H1>

<TABLE>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
#	$query = "select date_format(creation, \"%Y-%m-%d %T\"), name, message, 
#		replace(replace(addendum,'>','&gt;'),'<','&lt;') as addendum from mm_log order by creation";

if (isset($_REQUEST{"specifics"}))
{
?>
<TABLE>
<TR><TH>creation</TH><TH>name</TH><TH>message</TH></TR>
<?php
	$query = "select date_format(creation, \"%Y-%m-%d %T\") as thetime, name, message, addendum
		from mm_log where 1=0 and creation = \"".
		$_REQUEST{"specifics"}.
		"\"";
        $result = mysql_query($query
            	, $dbhandle)
          	or error_message("Query failed : " . mysql_error());
        while ($myrow = mysql_fetch_array($result)) 
        {
	      printf("<TR><TD><A HREF=\"/scripts/admin_log.php?specifics=%s\">%s</A></TD><TD>%s</TD><TD>%s</TD></TR>".
	      "<TR><TD colspan=\"3\">%s</TD></TR>\r\n", $myrow["thetime"],
                    $myrow["thetime"], $myrow["name"], $myrow["message"], $myrow["addendum"]);
        }
	$query = "select date_format(creation, \"%Y-%m-%d %T\") as thetime, name, message, addendum
		from mm_oldlog where 1=0 and creation = \"".
		$_REQUEST{"specifics"}.
		"\"";
        $result = mysql_query($query
            	, $dbhandle)
          	or error_message("Query failed : " . mysql_error());
        while ($myrow = mysql_fetch_array($result)) 
        {
	      printf("<TR><TD><A HREF=\"/scripts/admin_log.php?specifics=%s\">%s</A></TD><TD>%s</TD><TD>%s</TD></TR>".
	      "<TR><TD colspan=\"3\">%s</TD></TR>\r\n", $myrow["thetime"],
                    $myrow["thetime"], $myrow["name"], $myrow["message"], $myrow["addendum"]);
        }
}
else
{
?>
<TABLE>
<TR><TH>creation</TH><TH>name</TH><TH>message</TH></TR>
<?php
if (isset($_REQUEST{"day"}))
{
	$query = "select date_format(creation, \"%Y-%m-%d %T\") as thetime, name, message
		from mm_oldlog where 1=0 and date(creation) = date(\"".
		$_REQUEST{"day"}."-".
		$_REQUEST{"month"}."-".
		$_REQUEST{"year"}.
		"\") order by creation";
} else
{
	$query = "select date_format(creation, \"%Y-%m-%d %T\") as thetime, name, message
		from mm_log where 1 = 0 order by creation";
}
        $result = mysql_query($query
            	, $dbhandle)
          	or error_message("Query failed : " . mysql_error());
        while ($myrow = mysql_fetch_array($result)) 
        {
	      printf("<TR><TD><A HREF=\"/scripts/admin_log.php?specifics=%s\">%s</A></TD><TD>%s</TD><TD>%s</TD></TR>\r\n ", $myrow["thetime"],
                    $myrow["thetime"], $myrow["name"], $myrow["message"]);
        }
}
mysql_close($dbhandle);
?>
</TABLE>

<a HREF="/karchan/admin/showlog.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
