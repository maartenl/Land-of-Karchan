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
<?php
if (isset($_REQUEST{"username"}))
{
	setcookie("karchanadminname", $_REQUEST{"username"}, time() + 7200);
	setcookie("karchanadminpassword", $_REQUEST{"userpassword"}, time() + 7200);
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
if ( (!isset($_COOKIE["karchanadminname"])) &&
	(!isset($_REQUEST{"username"})) )
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

You are logged on.
<P>
<?php
}
?>

</BODY>
</HTML>



