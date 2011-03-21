<?php
/*-------------------------------------------------------------------------
svninfo: $Id: connect.php 1202 2009-10-21 05:37:54Z maartenl $
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

/**
 * the host that runs the mysql daemon
 */
$mmud_host="localhost";

/**
 * the name of the mysql database
 */
$mmud_db="mmud";

/**
 * the user to connect with to the database server
 */
$mmud_user="root";

/**
 * the password of the user of the database server
 */
$mmud_passwd="vary070colt515";

/**
 * the host that runs the mmud daemon
 */
$mmud_server_host="localhost";

/**
 * the port on which the mmud daemon is listening
 */
$mmud_server_port=3340;
//clude("/var/www/keys.php");

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

function mmud_connect()
{
    $dbhandle = mysql_connect("localhost", "root", "vary070colt515");
    mysql_select_db("mmud",$dbhandle);
    return $dbhandle;
}
