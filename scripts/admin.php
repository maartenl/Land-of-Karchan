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
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php";
if (isset($_REQUEST{"username"}))
{
	setcookie("karchanadminname", $_REQUEST{"username"}, time() + 7200);
	setcookie("karchanadminpassword", $_REQUEST{"userpassword"}, time() + 7200);
	mysql_query("insert into mm_log (name, message) values(".
	  "\"".quote_smart($_REQUEST{"username"}).
	  "\",\"Attempted logon to admin account from host ".
	  quote_smart(gethostbyaddr($_SERVER['REMOTE_ADDR']))." (".
	  quote_smart($_SERVER['REMOTE_ADDR']).
	  ")\")"
	  , $dbhandle)
	or error_message("Query failed : " . mysql_error());
}
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
<IMG SRC="/images/gif/dragon.gif">Karchan Admin Page</H1>

<P>
Click <A HREF="/karchan/admin/admin.html" TARGET="_top">here</A> to return to the menu.

<?php
if (!isset($_COOKIE["karchanadminname"]))
{
?>
<FORM METHOD="GET" ACTION="/scripts/admin.php">
Administrator name:<BR>
<INPUT TYPE="text" NAME="username" VALUE="" SIZE="39" MAXLENGTH="39"><P>
Administrator password:<BR>
<INPUT TYPE="password" NAME="userpassword" VALUE="" SIZE="38" MAXLENGTH="38"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>
<?php
}
else
{
?>

You are logged on.<P>
Your logon information will expire in 2 hours.
<P>
<?php

if ($_COOKIE["karchanadminname"] == "Karn")
{
?>
<FORM METHOD="GET" ACTION="/scripts/admin_extendperiod.php">
Extend Admin with name:
<INPUT TYPE="text" NAME="char" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>
<FORM METHOD="GET" ACTION="/scripts/admin_importitems.php">
Import items for user with name:
<INPUT TYPE="text" NAME="char" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<?php
}
}
?>

</BODY>
</HTML>



