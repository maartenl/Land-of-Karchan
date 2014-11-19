<?php
/*-------------------------------------------------------------------------
svninfo: $Id: admin.php 1151 2008-01-08 07:30:24Z maartenl $
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
printf("<TABLE>");
printf("<tr><td><b>name</b></TD><td><b>valid until</b></td><td><b>email</b></td></tr>", $myrow["name"], $myrow["validuntil"], $myrow["email"]);
$result = mysql_query("select * from mm_admin"
  , $dbhandle)
or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result))
{
  printf("<tr><td>%s</TD><td>%s</td><td>%s</td></tr>", $myrow["name"], $myrow["validuntil"], $myrow["email"]);
}
printf("</TABLE>");
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
<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_s-xclick">
<input type="image" src="https://www.paypal.com/en_US/i/btn/x-click-but20.gif" border="0" name="submit" alt="Make payments with PayPal - it's fast, free and secure!">
<img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
<input type="hidden" name="encrypted" value="-----BEGIN PKCS7-----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-----END PKCS7-----
">
</form>

<A HREF="https://www.paypal.com/cgi-bin/webscr?cmd=_subscr-find&alias=maarten_l%40yahoo%2ecom">
<IMG SRC="https://www.paypal.com/en_US/i/btn/cancel_subscribe_gen.gif" BORDER="0">
</A>

<BR>
<A HREF="https://www.paypal.com/cgi-bin/webscr?cmd=_subscr-find&alias=maarten_l%40yahoo%2ecom">View all active subscriptions</A><P>

<HR><A HREF="/scripts/admin_logout.php" TARGET="MainFrame">Logout</A><BR>

</BODY>
</HTML>

