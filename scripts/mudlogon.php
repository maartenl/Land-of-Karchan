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
?>
<HTML>
<TITLE>An Error Occured Attempting to Logon To The Mud
</TITLE>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1><IMG SRC="/images/gif/dragon.gif">An Error Occured Attempting To Logon To The Mud</H1><HR>
The following error occured:<P><TT>
<?php	echo "$errstr ($errno)<br>\n"; ?></TT><P>
<?php
if ($errno = "111")
{
?>Error code <B>111</B> usually indicates that the mud server has crashed or has
been deactivated. It needs to be restarted.<P><?php
}
?>
Please contact Karn (at <A HREF-"mailto:karn@karchan.org">karn@karchan.org</A>) 
as soon as possible to mention this problem and 
please mention the error message and error code.<P>
Thank you.
</BODY>
</HTML>
<?php
	} 
	else 
	{
		fgets ($fp,128); // Mmud id
		fgets ($fp,128); // action
		fputs ($fp, "logon\n");
		fgets ($fp,128); // name
		fputs ($fp, $_REQUEST{"name"}."\n");
		fgets ($fp,128); // password
		fputs ($fp, $_REQUEST{"password"}."\n");
		fgets ($fp,128); // ip address
		fputs ($fp, gethostbyaddr($_SERVER['REMOTE_ADDR'])."\n");
		fgets ($fp,128); // cookie
		fputs ($fp, $_COOKIE["karchanpassword"]."\n");
		fgets ($fp,128); // frames
		fputs ($fp, $_REQUEST{"frames"}."\n");
		// retrieve cookie that is always sent when attempting a login.
		$cookie = fgets ($fp,128);
		setcookie("karchanname", $_REQUEST{"name"});
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
