<?php
/*-------------------------------------------------------------------------
svninfo: $Id: mudnewchar.php 1088 2006-02-23 08:14:58Z maartenl $
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
-------------------------------------------------------------------------

karchan specific functions

*/


require("mudlib.php");

require("connect.php");

function mudnewchar($name, $password, $password2, $title,
	$realname, $email, $race, $sex, $age, $length, $width,
	$complexion, $eyes, $face, $hair, $beard, $arms, $legs) {
_karchan_log("mudnewchar " . $name);
 
$dbhandle = mmud_connect();

 // check for offline mud
if (file_exists ("/home/karchan/offline.txt")) 
{
	$_SESSION["karchan_errormsg"] = "Karchan offline.";
	return;
}
		
// aName must match [A-Z|_|a-z]{3,}
if (preg_match("/([A-Z]|_|[a-z]){3,}/", $name) == 0)
{
	$_SESSION["karchan_errormsg"] = "Karchan new username wrong.";
	return;

}

// aPassword must length > 5
if (strlen($password) < 5)
{
	$_SESSION["karchan_errormsg"] = "Karchan new password must be at least 5 characters long.";
	return;
}

// apassword must be the same as the second entered password
if ($password != $password2)
{
	$_SESSION["karchan_errormsg"] = "Karchan passwords are not identical.";
	return;
}
//  is sqlGetBan1String > 0 => user banned
$banned = false;
$result = mysql_query("select count(name) as count from mm_sillynamestable 
		where '".quote_smart($name)."' like name"
	, $dbhandle);
$myrow =  mysql_fetch_array($result);
if ($myrow["count"] != "0")
{
	$banned = true;
}
//	sqlGetBan2String > 0 => user not banned
$result = mysql_query("select count(name) as count from mm_unbantable 
	where name = '".quote_smart($name)."'"
	, $dbhandle);
$myrow =  mysql_fetch_array($result);
if ($myrow["count"] != "0")
{
	$banned = false;
}
else
{
	//	sqlGetBan4String > 0 => user banned
	$result = mysql_query("select count(address) as count from mm_bantable 
		where '".quote_smart(gethostbyaddr($_SERVER['REMOTE_ADDR']))."' like address or 
		'".quote_smart($_SERVER['REMOTE_ADDR'])."' like address"
		, $dbhandle);
	$myrow =  mysql_fetch_array($result);
	if ($myrow["count"] != "0")
	{
		$banned = true;
	}
		//	sqlGetBan3String > 0 => user banned
	$result = mysql_query("select count(*) as count from mm_bannednamestable 
		where name = '".quote_smart($name)."'"
		, $dbhandle);
	$myrow =  mysql_fetch_array($result);
	if ($myrow["count"] != "0")
	{
		$banned = true;
	}
}

if ($banned)
{
	$_SESSION["karchan_errormsg"] = "You have been banned.";
	return;
}

// user must NOT exist in mm_usertable
$result = mysql_query("select mm_usertable.name from mm_usertable
		where mm_usertable.name = '".quote_smart($name)."'"
	, $dbhandle);
if (mysql_num_rows($result) != 0)
{
	$_SESSION["karchan_errormsg"] = "Character already exists.";
	return;
}

	// make that change.
	$query = "insert into mm_usertable ".
		"(name, address, password, title, realname, email, race, sex, age, 
		length, width, complexion, eyes, face, hair, beard, arm, leg, lok, 
		active, lastlogin, birth) ".
		"values(\"".
		quote_smart($name).
		"\", \"".
		quote_smart($_SERVER['REMOTE_ADDR']).
		"\", sha1(\"".
		quote_smart($password).
		"\"), \"".
		quote_smart($title).
		"\", \"".
		quote_smart($realname).
		"\", \"".
		quote_smart($email).
		"\", \"".
		quote_smart($race).
		"\", \"".
		quote_smart($sex).
		"\", \"".
		quote_smart($age).
		"\", \"".
		quote_smart($length).
		"\", \"".
		quote_smart($width).
		"\", \"".
		quote_smart($complexion).
		"\", \"".
		quote_smart($eyes).
		"\", \"".
		quote_smart($face).
		"\", \"".
		quote_smart($hair).
		"\", \"".
		quote_smart($beard).
		"\", \"".
		quote_smart($arms).
		"\", \"".
		quote_smart($legs).
		"\", null, 0, now(), now())";
	if (!mysql_query($query
		, $dbhandle))
	{
		writeLogLong($dbhandle, "Error creating new user ".$name." from ".
		$_SERVER['REMOTE_ADDR'].".", $query . mysql_error());
		$_SESSION["karchan_errormsg"] = "An error occurred creating the character.";
	}
	else
	{
		writeLogLong($dbhandle, "Created new user ".$name." from ".
		$_SERVER['REMOTE_ADDR'].".", $query);
		$_SESSION["karchan_errormsg"] = "Ok.";
	}	
mysql_close($dbhandle);


}
