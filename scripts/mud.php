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

/**
 * verify form information
 */      
if (!isset($_REQUEST{"command"}) &&
	!isset($_COOKIE["karchanname"]) &&
	!isset($_COOKIE["karchanpassword"]) )
{   
	die("Form information missing.");
}
// Hack prevention.
//	$headers = apache_request_headers();
//	header()
//	setcookie();

//	foreach ($headers as $header => $value) 
//	{
//		echo "$header: $value <br />\n";
//	}
	$fp = fsockopen ("localhost", 3339, $errno, $errstr, 30);
	if (!$fp) 
	{
		echo "$errstr ($errno)<br>\n";
	} 
	else 
	{
		mysql_query("insert into mm_commandlog (name, command) values(\"".
			mysql_escape_string($_COOKIE["karchanname"])."\", \"".
			mysql_escape_string($_REQUEST{"command"})."\")"
			, $dbhandle)
			or die("Query failed : " . mysql_error());

		fgets ($fp,128); // mud id
		fgets ($fp,128); // action
		fputs ($fp, "mud\n");
		fgets ($fp,128); // name
		fputs ($fp, $_COOKIE["karchanname"]."\n");
		fgets ($fp,128); // cookie
		fputs ($fp, $_COOKIE["karchanpassword"]."\n");
		fgets ($fp,128); //  frames
		fputs ($fp, $_REQUEST{"frames"}."\n");
		fgets ($fp,128); // command
		if ($_REQUEST{"command"} == "sendmail")
		{
			$foo = strlen($_REQUEST{"mailheader"});
			fputs ($fp, "sendmail "
				.$_REQUEST{"mailto"}." "
				.$foo." "
				.$_REQUEST{"mailheader"}." "
				.$_REQUEST{"mailbody"}
				."\n");
		}
		else
		{
			fputs ($fp, $_REQUEST{"command"}."\n");
		}
		fputs ($fp, ".\n");
		$cookie = fgets ($fp,128);
		if (strstr($cookie, "sessionpassword=") != FALSE)
		{
			$cookie = substr($cookie, 16);
			$cookie = substr_replace($cookie, "", -1, 1);
			setcookie("karchanpassword", $cookie);
			if ($cookie == "")
			{
				setcookie("karchanname", $cookie);
			}
		}
		else
		{
			if (get_cfg_var("magic_quotes_gpc") == "1")
			{
				$cookie = stripslashes($cookie);
			}
			echo $cookie;
		}
		$readline = fgets ($fp,128);
		while ((!feof($fp)) && ($readline != ".\n"))
		{
			if (get_cfg_var("magic_quotes_gpc") == "1")
			{
				$readline = stripslashes($readline);
			}
			echo $readline;
			$readline = fgets ($fp, 128);
		}

		fputs ($fp, "\nOk\n");
		fputs ($fp, "\nOk\n");
		fclose ($fp);
	}
	exit;
?>
