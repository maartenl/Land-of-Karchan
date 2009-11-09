<?
/*-------------------------------------------------------------------------
svninfo: $Id: mud.php 1202 2009-10-21 05:37:54Z maartenl $
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

/**
 * verify existence of form information
 */  
if (!isset($_COOKIE["karchanname"]) &&
	!isset($_COOKIE["karchanpassword"]) )
{   
	error_message("Form information missing.");
}
/**
 * verify that name and password are correct in the database
 */
$query ="select \"yes\" from mm_usertable where name = \"".
quote_smart($_COOKIE["karchanname"])."\" and lok = \"".
quote_smart($_COOKIE["karchanpassword"])."\" and active = 1 and god < 2";
$result = mysql_query($query
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

if ($good == "no")
{
	error_message("Your user account for user ". quote_smart($_COOKIE["karchanname"]) ." does not exist!");
}

/**
 * Read logfile and dump it to output.
 */
 	$filename = $mudfilepath . "/" . quote_smart($_COOKIE["karchanname"]) . ".log";
 	$fp = fopen($filename, 'r');
 	if (!$fp)
 	{
 	        error_idmessage($errno, "Error opening file " . $filename);
        }
        if (isset($_REQUEST{"position"}))
        {
                fseek($fp, $_REQUEST{"position"});
        }
	while (!feof($fp))
	{
         	$readline = fread ($fp,1024);
		if (get_cfg_var("magic_quotes_gpc") == "1")
		{
			$readline = stripslashes($readline);
		}
		echo $readline;
	}
	fclose ($fp);
?>
