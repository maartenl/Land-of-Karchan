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
Land of Karchan - <?php echo $_COOKIE["karchanname"] ?>
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Character Sheets</H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 

        
/**
 * verify form information
 */      
if (!isset($_COOKIE["karchanname"]) &&    
    !isset($_COOKIE["karchanpassword"]) )
{   
    die("Form information missing.");
}

$result = mysql_query("select * from mm_usertable where mm_usertable.name =
	'".mysql_escape_string($_COOKIE["karchanname"])."' 
	and mm_usertable.lok =
	'".mysql_escape_string($_COOKIE["karchanpassword"])."'
	and mm_usertable.god < 2"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
if (mysql_num_rows($result) == 0)
{
	die("Password not correct, or character does not exist.");
}

if ($_REQUEST{"family"} != "0")
{
	mysql_query("replace into family
	values(\"".mysql_escape_string($_COOKIE["karchanname"])."\",
	\"".mysql_escape_string($_REQUEST{"familyname"}). 
	"\",".mysql_escape_string($_REQUEST{"family"}).")",
		 $dbhandle)
		or die("Query failed : " . mysql_error());
}
mysql_query("replace into characterinfo 
	values(\"".mysql_escape_string($_COOKIE["karchanname"])."\",
	\"".mysql_escape_string($_REQUEST{"imageurl"})."\",
	\"".mysql_escape_string($_REQUEST{"homepageurl"})."\",
	\"".mysql_escape_string($_REQUEST{"dateofbirth"})."\",
	\"".mysql_escape_string($_REQUEST{"cityofbirth"})."\",
	\"".mysql_escape_string($_REQUEST{"storyline"})."\")",
	 $dbhandle)
	or die("Query failed : " . mysql_error());
mysql_close($dbhandle);
?>
Form information has been submitted.<P>
Please click <A HREF="/scripts/charactersheet.php?name=<?php echo
$_COOKIE["karchanname"] ?>">
Character Sheet Info</A> to view the submitted information.<P>

<p>
<a HREF="/scripts/charactersheets.php">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
