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
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php";

function writeLog ($dbhandle, $arg)
{
	mysql_query("insert into mm_log (name, message) values(".
		"\"".mysql_escape_string($_COOKIE["karchanadminname"])."\",\"".
		mysql_escape_string($arg)."\")"
    , $dbhandle)
    or die("Query failed : " . mysql_error());
}

function writeLogLong ($dbhandle, $arg, $addendum)
{
	mysql_query("insert into mm_log (name, message, addendum) values(".
		"\"".mysql_escape_string($_COOKIE["karchanadminname"])."\",\"".
		mysql_escape_string($arg)."\",\"".
		mysql_escape_string($addendum)
		."\")"
    , $dbhandle)
    or die("Query failed : " . mysql_error());
}

$result = mysql_query("select \"yes\" from mm_admin where name = \"".
        $_COOKIE["karchanadminname"]."\" and passwd = password(\"".
		$_COOKIE["karchanadminpassword"]."\") and validuntil >= now()"
    , $dbhandle)
    or die("Query failed : " . mysql_error());
$good = "no";
while ($myrow = mysql_fetch_array($result))
{
	if ($myrow[0] == "yes")
	{
		$good = "yes";
	}
}

$result = mysql_query("select \"invalid\" from mm_admin where name = \"".
        $_COOKIE["karchanadminname"]."\" and passwd = password(\"".
		$_COOKIE["karchanadminpassword"]."\") and validuntil < now()"
    , $dbhandle)
    or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result))
{
	if ($myrow[0] == "invalid")
	{
		$good = "invalid";
	}
}

if ($good == "no")
{
	die("Your user account does not exist!");
}

if ($good == "invalid")
{
	die("Your account has expired. Contact karn@karchan.org.");
}

?>
