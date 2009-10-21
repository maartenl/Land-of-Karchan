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
include("/var/www/html/scripts/keys_test.php");

// Quote variable to make safe
function quote_smart($value)
{
    // Stripslashes
    if (get_magic_quotes_gpc()) 
    {
        $value = stripslashes($value);
    }
    // Quote if not integer
    if (!is_numeric($value)) 
    {
//         this is nicce, but for a future implementation.
//        $value = "'" . mysql_real_escape_string($value) . "'";
        $value = mysql_real_escape_string($value);
    }
    return $value;
}

// error method for displaying a proper error message
function error_idmessage($value, $message)
{
    if (get_magic_quotes_gpc()) 
    {
        $value = stripslashes($value);
        $message = stripslashes($message);
    }
?>
<I>An error has occurred. 
Please email <A HREF="mailto:karn@karchan.org">karn@karchan.org</A>.
Include the error message, the webpage where the error
occurred and possibly, if relevant, your karchan charactername in the email 
so that I can start debugging.<P>
The error message was:<P><B>
id:<?php echo $value . " message:" . $message ?></B>
<P>Thank you.<P>Karn</I>
<?php
    die();
}

// error method for displaying a proper error message
function error_message($message)
{
    error_idmessage("unknown", $message);
}

$dbhandle = mysql_connect($host, $user, $passwd)
	or error_message("Could not connect : " . mysql_error());
mysql_select_db($db,$dbhandle) or error_message("Could not select database: " . mysql_error());
?>
