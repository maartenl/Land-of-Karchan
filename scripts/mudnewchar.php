<?
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
-------------------------------------------------------------------------*/
?>
<HTML>
<HEAD>
<TITLE>
A New Character - <?php echo $_REQUEST{"name"} ?>
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 

 
function writeLogLong ($dbhandle, $arg, $addendum)
{
	mysql_query("insert into mm_log (name, message, addendum) values(".
		"\"".quote_smart($_REQUEST{"name"})."\",\"".
		quote_smart($arg)."\",\"".
		quote_smart($addendum)
		."\")"
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
}
 
 // check for offline mud
if (file_exists ("/home/karchan/offline.txt")) 
{
	$file = fopen ("/home/karchan/offline.txt", "r");
	while (!feof ($file)) 
	{
		$line = fgets ($file, 1024);
		echo $line;
	}
	fclose($file);
	exit;
}
		
// aName must match [A-Z|_|a-z]{3,}
if (preg_match("/([A-Z]|_|[a-z]){3,}/", $_REQUEST{"name"}) == 0)
{
?>
<H1>
<IMG SRC="/images/gif/dragon.gif">
Error Creating Character - Name invalid</H1>
Unable to create character, because the name is invalid.
The following rules govern if your name is accepted or not:
<UL><LI>at least three letters in length
<LI>may contain only letters and an underscore (_)
</UL>
Regards, Karn.
<P><a HREF="/karchan/newchar.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>
</BODY></HTML>
<?php
die();
}

// aPassword must length > 5
if (strlen($_REQUEST{"password"}) < 5)
{
?>
<H1>
<IMG SRC="/images/gif/dragon.gif">
Error Creating Character - Password length less than 5</H1>
Unable to create character, because the length of the password
was less than the mandatory 5 characters.
<P>Regards, Karn.
<P><a HREF="/karchan/newchar.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>
</BODY></HTML>
<?php
die();
}

// apassword must be the same as the second entered password
if ($_REQUEST{"password"} != $_REQUEST{"password2"})
{
?>
<H1>
<IMG SRC="/images/gif/dragon.gif">
Error Creating Character - Password does not match</H1>
Unable to create character, because the password does not match.
On the form it is required that the password is typed twice for
verification. Apparently the first password was different
from the second password.<P>
Regards, Karn.
<P><a HREF="/karchan/newchar.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>
</BODY></HTML>
<?php
die();
}
//  is sqlGetBan1String > 0 => user banned
$banned = false;
$result = mysql_query("select count(name) as count from mm_sillynamestable 
		where '".quote_smart($_REQUEST{"name"})."' like name"
	, $dbhandle)
	or error_idmessage(1, "Query failed : " . mysql_error());
$myrow =  mysql_fetch_array($result);
if ($myrow["count"] != "0")
{
	$banned = true;
}
//	sqlGetBan2String > 0 => user not banned
$result = mysql_query("select count(name) as count from mm_unbantable 
	where name = '".quote_smart($_REQUEST{"name"})."'"
	, $dbhandle)
	or error_idmessage(2, "Query failed : " . mysql_error());
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
		, $dbhandle)
		or error_idmessage(3, "Query failed : " . mysql_error());
	$myrow =  mysql_fetch_array($result);
	if ($myrow["count"] != "0")
	{
		$banned = true;
	}
		//	sqlGetBan3String > 0 => user banned
	$result = mysql_query("select count(*) as count from mm_bannednamestable 
		where name = '".quote_smart($_REQUEST{"name"})."'"
		, $dbhandle)
		or error_idmessage(4, "Query failed : " . mysql_error());
	$myrow =  mysql_fetch_array($result);
	if ($myrow["count"] != "0")
	{
		$banned = true;
	}
}

if ($banned)
{
?>
<H1>
<IMG SRC="/images/gif/dragon.gif">
Error Creating Character - You are banned</h1>
You, or someone in your domain,  has angered the gods by behaving badly on
this mud. Your ip domain/charactername is therefore banned from the game.<P>

If you have not misbehaved or even have never before played the 
game, and wish
to play with your current IP address, email to 
<A HREF="mailto:deputy@karchan.org">deputy@karchan.org</A> and ask 
them to make an exception in your case. Do <I>not</I> forget to provide your 
Character name.<P>
You'll be okay as long as you follow the rules.
<P>Regards, Karn.
<P><a HREF="/karchan/newchar.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>
</BODY></HTML>
<?php
die();
}

// user must NOT exist in mm_usertable
$result = mysql_query("select mm_usertable.name from mm_usertable
		where mm_usertable.name = '".quote_smart($_REQUEST{"name"})."'"
	, $dbhandle)
	or error_idmessage(5, "Query failed : " . mysql_error());
if (mysql_num_rows($result) != 0)
{
?>
<H1>
<IMG SRC="/images/gif/dragon.gif">
Error Creating Character - Character Exists</H1>
Unable to create character, because someone already created a character
with that particular name. Please choose a different name and try again.
<P>Regards, Karn.
<P><a HREF="/karchan/newchar.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>
</BODY></HTML>
<?php
die();
}

?><H1>
<IMG SRC="/images/gif/dragon.gif">
<?php echo $_REQUEST{"name"} ?> - A New Character</H1>
<?php
if (isset($_REQUEST{"name"}))
{
	// make that change.
	$query = "insert into mm_usertable ".
		"(name, address, password, title, realname, email, race, sex, age, 
		length, width, complexion, eyes, face, hair, beard, arm, leg, lok, 
		active, lastlogin, birth) ".
		"values(\"".
		quote_smart($_REQUEST{"name"}).
		"\", \"".
		quote_smart($_SERVER['REMOTE_ADDR']).
		"\", sha1(\"".
		quote_smart($_REQUEST{"password"}).
		"\"), \"".
		quote_smart($_REQUEST{"title"}).
		"\", \"".
		quote_smart($_REQUEST{"realname"}).
		"\", \"".
		quote_smart($_REQUEST{"email"}).
		"\", \"".
		quote_smart($_REQUEST{"race"}).
		"\", \"".
		quote_smart($_REQUEST{"sex"}).
		"\", \"".
		quote_smart($_REQUEST{"age"}).
		"\", \"".
		quote_smart($_REQUEST{"length"}).
		"\", \"".
		quote_smart($_REQUEST{"width"}).
		"\", \"".
		quote_smart($_REQUEST{"complexion"}).
		"\", \"".
		quote_smart($_REQUEST{"eyes"}).
		"\", \"".
		quote_smart($_REQUEST{"face"}).
		"\", \"".
		quote_smart($_REQUEST{"hair"}).
		"\", \"".
		quote_smart($_REQUEST{"beard"}).
		"\", \"".
		quote_smart($_REQUEST{"arms"}).
		"\", \"".
		quote_smart($_REQUEST{"legs"}).
		"\", null, 0, now(), now())";
	mysql_query($query
		, $dbhandle)
		or error_idmessage(6, "Query(8) failed : " . mysql_error());
	writeLogLong($dbhandle, "Created new user ".$_REQUEST{"name"}." from ".
	$_SERVER['REMOTE_ADDR'].".", $query);
}

mysql_close($dbhandle);

?>

The new character has been created. Click on the link <I>Back</I> below 
to return to the logon screen. In the logon screen fill out the name
and the password of the newly created character.
<p>
<a HREF="/karchan/index.html">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
