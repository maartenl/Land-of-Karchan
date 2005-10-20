<?
/*-------------------------------------------------------------------------
svninfo: $Id$
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
Char <?php echo $_REQUEST{"char"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";

if ( (!isset($_REQUEST{"char"})) ||
     (!isset($_REQUEST{"newpassword"})) )
{
	die("Form information missing.");
}
$password = $_REQUEST{"newpassword"};
if (strlen($password) < 5)
{
	die("Password should be at least 5 characters long.");
}
// make that change.
$query = "update mm_usertable set password = password(\"".
	$password.
	"\"), owner=\"".
	quote_smart($_COOKIE["karchanadminname"]).
	"\" where name = \"".
	quote_smart($_REQUEST{"char"}).
	"\" and (owner is null or owner = \"".
	quote_smart($_COOKIE["karchanadminname"])."\")";
mysql_query($query, $dbhandle)  
or die("Query(8) failed : " . mysql_error(). "[" . $query . "]");
writeLog($dbhandle, "Changed password of character ".$_REQUEST{"char"}.".");
mysql_close($dbhandle);
?>

Changed password of character <I><?php echo $_REQUEST{"char"} ?></I>
to <I><?php echo $password ?></I>.<P>

<a HREF="/karchan/admin/chars.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
