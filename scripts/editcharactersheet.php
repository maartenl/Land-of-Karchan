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
<HTML>
<HEAD>
<TITLE>
Land of Karchan - <?php echo $_REQUEST{"name"} ?>
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Edit Character Sheet of <?php echo $_REQUEST{"name"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 


$result = mysql_query("select * from usertable where usertable.name =
	'".$_REQUEST{"name"}."' 
	and usertable.password = '".$_REQUEST{"password"}."'"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
if (mysql_num_rows($result) == 0)
{
	die("Password not correct, or character does not exist.");
}
$result = mysql_query("select *, replace(replace(replace(storyline,
	'&','&amp;'),'<', '&lt;'), '>', '&gt;') 
	from characterinfo 
        where name = '".$_REQUEST{"name"}."'", $dbhandle)
	or die("Query failed : " . mysql_error());
if ($myrow = mysql_fetch_row($result)) 
{
	// full character sheet 
?>
<FORM METHOD="GET" ACTION="/scripts/submitcharactersheet.php">
<HR>
Homepage Url:<BR>
<INPUT TYPE="text" NAME="homepageurl" VALUE="<?php echo $myrow[2] ?>" SIZE="50" MAXLENGTH="253"><P>
Image Url:<BR>
<INPUT TYPE="text" NAME="imageurl" VALUE="<?php echo $myrow[1] ?>" SIZE="50" MAXLENGTH="253"><P>
Date of Birth: 
<INPUT TYPE="text" NAME="dateofbirth" VALUE="<?php echo $myrow[3] ?>" SIZE="20" MAXLENGTH="253"><P>
City of Birth:
<INPUT TYPE="text" NAME="cityofbirth" VALUE="<?php echo $myrow[4] ?>" SIZE="20" MAXLENGTH="253"><P>
Original storyline was...<BR><TABLE><TR><TD><TT><?php echo $myrow[6] ?></TT></TD></TR></TABLE>
Storyline:<BR>
<TEXTAREA NAME="storyline" VALUE="" ROWS="30" COLS="85"></TEXTAREA><P>
<?php familyValues($dbhandle); ?>
<INPUT TYPE="hidden" NAME="name" VALUE="<?php echo $_REQUEST{"name"} ?>">
<INPUT TYPE="hidden" NAME="password" VALUE="<?php echo $_REQUEST{"password"} ?>">
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear">
</FORM>
<?php
}
else
{
	// empty character sheet
?>
<FORM METHOD="GET" ACTION="/cgi-bin/submitcharactersheet.cgi">
<HR>
Homepage Url:<BR>
<INPUT TYPE="text" NAME="homepageurl" VALUE="http://" SIZE="50" MAXLENGTH="253"><P>
Image Url:<BR>
<INPUT TYPE="text" NAME="imageurl" VALUE="http://" SIZE="50" MAXLENGTH="253"><P>
Date of Birth: 
<INPUT TYPE="text" NAME="dateofbirth" VALUE="" SIZE="20" MAXLENGTH="253"><P>
City of Birth:
<INPUT TYPE="text" NAME="cityofbirth" VALUE="" SIZE="20" MAXLENGTH="253"><P>
Storyline:<BR>
<TEXTAREA NAME="storyline" VALUE="" ROWS="30" COLS="85"></TEXTAREA><P>
<?php familyValues($dbhandle); ?>
<INPUT TYPE="hidden" NAME="name" VALUE="<?php echo $_REQUEST{"name"} ?>">
<INPUT TYPE="hidden" NAME="password" VALUE="<?php echo $_REQUEST{"password"} ?>">
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear">
</FORM>
<?php
}
mysql_close($dbhandle);

function familyValues($arg)
{
	$result = mysql_query("select * from familyvalues", $arg)
		or die("Query failed : " . mysql_error());
	printf("(<I>Make sure you spell the name of the ");
	printf("familymember correctly, otherwise the corresponding ");
	printf("charactersheet can not be obtained.</I>)<BR>");
	printf("Add family relation:\r\n");
	printf("<SELECT NAME=\"family\">\r\n");
	printf("<option selected value=\"0\">None");
	while ($myrow = mysql_fetch_row($result))
	{
		printf("<option selected value=\"%s\">%s", 
			$myrow[0],$myrow[1]);
	}
	printf("</SELECT>of ");
	printf("<INPUT TYPE=\"text\" NAME=\"familyname\" VALUE=\"\" SIZE=\"50\" MAXLENGTH=\"40\"><P>");
}
?>
<p>
<a HREF="/scripts/charactersheets.php">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
