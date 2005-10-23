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

function writeLog ($dbhandle, $arg)
{
	mysql_query("insert into mm_log (name, message) values(".
		"\"".quote_smart($_COOKIE["karchanadminname"])."\",\"".
		quote_smart($arg)."\")"
    , $dbhandle)
    or error_message("Query failed : " . mysql_error());
}

function writeLogLong ($dbhandle, $arg, $addendum)
{
	mysql_query("insert into mm_log (name, message, addendum) values(".
		"\"".quote_smart($_COOKIE["karchanadminname"])."\",\"".
		quote_smart($arg)."\",\"".
		quote_smart($addendum)
		."\")"
    , $dbhandle)
    or error_message("Query failed : " . mysql_error());
}

/**
 * verify form information
 */
if (!isset($_COOKIE["karchanadminname"]) ||
	!isset($_COOKIE["karchanadminpassword"]))
{   
    error_message("Admin Name and/or password information missing.");
}

$result = mysql_query("select \"yes\" from mm_admin where name = \"".
        quote_smart($_COOKIE["karchanadminname"])."\" and passwd = password(\"".
	quote_smart($_COOKIE["karchanadminpassword"])."\") and validuntil >= now()"
    , $dbhandle)
    or error_message("Query failed : " . mysql_error());
$good = "no";
while ($myrow = mysql_fetch_array($result))
{
	if ($myrow[0] == "yes")
	{
		$good = "yes";
	}
}

$result = mysql_query("select \"invalid\" from mm_admin where name = \"".
        quote_smart($_COOKIE["karchanadminname"])."\" and passwd = password(\"".
	quote_smart($_COOKIE["karchanadminpassword"])."\") and validuntil < now()"
    , $dbhandle)
    or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result))
{
	if ($myrow[0] == "invalid")
	{
		$good = "invalid";
	}
}

if ($good == "no")
{
	error_message("Your user account does not exist!");
}

if ($good == "invalid")
{
	error_message("Your account has expired. Contact karn@karchan.org.");
}

?>
