<?php
/*-------------------------------------------------------------------------
svninfo: $Id: mudlogon.php 1202 2009-10-21 05:37:54Z maartenl $
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

function mudlogin($name, $password, $frames = 1) {
_karchan_log("mudlogin " . $name);

// Hack prevention.
//	$headers = apache_request_headers();
//	header()
//	setcookie();

//	foreach ($headers as $header => $value) 
//	{
//		echo "$header: $value <br />\n";
//	}
	//$fp = fsockopen ($server_host, $server_port, $errno, $errstr, 30);
	$fp = fsockopen ("localhost", 3340, $errno, $errstr, 30);
	if (!$fp) 
	{
	        $_SESSION["karchan_errormsg"] = "Could not open socket.";
        	ob_end_flush();
	        return;
        }
        $readme = "";
	$readme = fgets ($fp,128); // Mmud id
	_karchan_log("mudlogin " . $readme);
	$readme = fgets ($fp,128); // action
	_karchan_log("mudlogin " . $readme);
	fputs ($fp, "logon\n");
	$readme = fgets ($fp,128); // name
	_karchan_log("mudlogin " . $readme);
	fputs ($fp, $name."\n");
	$readme = fgets ($fp,128); // password
	_karchan_log("mudlogin " . $readme);
	fputs ($fp, $password."\n");
	$readme = fgets ($fp,128); // ip address
	_karchan_log("mudlogin " . $readme);
	fputs ($fp, gethostbyaddr($_SERVER['REMOTE_ADDR'])."\n");
	$readme = fgets ($fp,128); // cookie
	_karchan_log("mudlogin " . $readme);
	if (isset($_COOKIE["karchanpassword"]))
	{
        	fputs ($fp, $_COOKIE["karchanpassword"]."\n");
	}
	else
	{
        	fputs ($fp, "\n");
	}
	$readme = fgets ($fp,128); // frames
	_karchan_log("mudlogin " . $readme);
	fputs ($fp, $frames."\n");
	// retrieve cookie that is always sent when attempting a login.
	$cookie = $readme = fgets ($fp,128);
	_karchan_log("mudlogin " . $readme);
	setcookie("karchanname", $name, 0, "/");
	if (strstr($cookie, "sessionpassword=") != FALSE)
	{
		$cookie = substr($cookie, 16);
		$cookie = substr_replace($cookie, "", -1, 1);
		setcookie("karchanpassword", $cookie, 0, "/");
        	$readline = fgets ($fp,128);
                $_SESSION["karchan_errormsg"] = $readline;
	}
	else
	{
	        $_SESSION["karchan_errormsg"] = $cookie;
	}
	$readline = fgets ($fp,128);
	$contents = $readline;
	while ((!feof($fp)) && ($readline != ".\n"))
	{
		// echo $readline;
        	_karchan_log("mudlogin " . $readline);
		$readline = fgets ($fp,128);
		$contents .= $readline;
	}
       	_karchan_log("mudlogin " . $readline);
 	fputs ($fp, "\nOk\nOk\n");
	fclose ($fp);
	$_SESSION["frames"] = $frames;
        $_SESSION["karchan_contents"] = $contents;
}
