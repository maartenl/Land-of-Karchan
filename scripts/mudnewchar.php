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
	header ("Content-type: text/html");
   
// Hack prevention.
//	$headers = apache_request_headers();
//	header()
//	setcookie();
	ob_start();

//	foreach ($headers as $header => $value) 
//	{
//		echo "$header: $value <br />\n";
//	}
	$fp = fsockopen ("localhost", 3340, $errno, $errstr, 30);
	if (!$fp) 
	{
		echo "$errstr ($errno)<br>\n";
	} 
	else 
	{
		// RealName, EMAIL, mysex, myrealage, mycountry,
	 	// myoccupation, mycomments, myinternal, myexternal
		// title, race, sex, age, length,  width, complexion
		// eyes, face, hair, beard, arms, legs
		fgets ($fp,128); // Mmud id
		fgets ($fp,128); // action
		fputs ($fp, "newchar\n");
		fgets ($fp,128); // name
		fputs ($fp, $_COOKIE["karchanname"]."\n");
		fgets ($fp,128); // password
		fputs ($fp, $_REQUEST{"password"}."\n");
		fgets ($fp,128); // cookie
		fputs ($fp, $_COOKIE["karchanpassword"]."\n");
		fgets ($fp,128); // frames
		fputs ($fp, $_REQUEST{"frames"}."\n");
		fgets ($fp,128); // realname
		fputs ($fp, $_REQUEST{"RealName"}."\n");
		fgets ($fp,128); // email
		fputs ($fp, $_REQUEST{"EMAIL"}."\n");
		fgets ($fp,128); // title
		fputs ($fp, $_REQUEST{"title"}."\n");
		fgets ($fp,128); // race
		fputs ($fp, $_REQUEST{"race"}."\n");
		fgets ($fp,128); // sex
		fputs ($fp, $_REQUEST{"sex"}."\n");
		fgets ($fp,128); // age
		fputs ($fp, $_REQUEST{"age"}."\n");
		fgets ($fp,128); // length
		fputs ($fp, $_REQUEST{"length"}."\n");
		fgets ($fp,128); // width
		fputs ($fp, $_REQUEST{"width"}."\n");
		fgets ($fp,128); // complexion
		fputs ($fp, $_REQUEST{"complexion"}."\n");
		fgets ($fp,128); // eyes
		fputs ($fp, $_REQUEST{"eyes"}."\n");
		fgets ($fp,128); // face
		fputs ($fp, $_REQUEST{"face"}."\n");
		fgets ($fp,128); // hair
		fputs ($fp, $_REQUEST{"hair"}."\n");
		fgets ($fp,128); // beard
		fputs ($fp, $_REQUEST{"beard"}."\n");
		fgets ($fp,128); // arms
		fputs ($fp, $_REQUEST{"arms"}."\n");
		fgets ($fp,128); // legs
		fputs ($fp, $_REQUEST{"legs"}."\n");
		// retrieve cookie that is always sent when attempting a newchar.
		$cookie = fgets ($fp,128);
		if (strstr($cookie, "sessionpassword=") != FALSE)
		{
			$cookie = substr($cookie, 16);
			$cookie = substr_replace($cookie, "", -1, 1);
			setcookie("karchanpassword", $cookie);
		}
		else
		{
			echo $cookie;
		}
		$readline = fgets ($fp,128);
		while ((!feof($fp)) && ($readline != ".\n"))
		{
			echo $readline;
			$readline = fgets ($fp,128);
		}
  		fputs ($fp, "\nOk\nOk\n");
		fclose ($fp);
	}
	ob_end_flush();
	exit;
?>
