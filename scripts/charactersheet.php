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
Character Sheet of <?php echo $_REQUEST{"name"} ?></H1>
<HR>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 

$result = mysql_query("select mm_usertable.name, title, sex,
	concat(age,
	if(length = 'none', '', concat(', ',length)),
        if(width = 'none', '', concat(', ',width)),
        if(complexion = 'none', '', concat(', ',complexion)),
        if(eyes = 'none', '', concat(', ',eyes)),
        if(face = 'none', '', concat(', ',face)),
        if(hair = 'none', '', concat(', ',hair)),
        if(beard = 'none', '', concat(', ',beard)),
        if(arm = 'none', '', concat(', ',arm)),
        if(leg = 'none', '', concat(', ',leg)),
        ' ', sex, ' ', race), 
        concat('<IMG SRC=\"',imageurl,'\">'), 
        guild, 
        concat('<A HREF=\"',homepageurl,'\">',homepageurl,'</A>'), 
        \"Yes\", dateofbirth, cityofbirth, mm_usertable.lastlogin, storyline 
        from mm_usertable, characterinfo 
        where mm_usertable.name = '".$_REQUEST{"name"}."' and 
        mm_usertable.name = characterinfo.name"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
if (mysql_num_rows($result) == 0)
{
	die("Character does not exist.");
}
if ($myrow = mysql_fetch_row($result)) 
{
	// full character sheet 
	printf("<B>Name:</B> %s<BR>", $myrow[0]);
	printf("<B>Title:</B> %s<BR>", $myrow[1]);
	printf("<B>Sex:</B> %s<BR>", $myrow[2]);
	printf("<B>Description:</B> %s<BR>", $myrow[3]);
	if (($myrow[4] != "<IMG SRC=\"\">") &&
	   ($myrow[4] != "<IMG SRC=\"http://\">") )
	{
		printf("<B>Image:</B> %s<BR>", $myrow[4]);
	}
	printf("<B>Member of:</B> %s<BR>", $myrow[5]);
	if (($myrow[6] != "<A HREF=\"\"></A>") &&
	   ($myrow[6] != "<A HREF=\"http://\">http://</A>") )
	{
		printf("<B>Homepage:</B> %s<BR>", $myrow[6]);
	}
	printf("<B>Born:</B> %s<BR>", $myrow[7]);
	printf("<B>Born When:</B> %s<BR>", $myrow[8]);
	printf("<B>Born Where:</B> %s<BR>", $myrow[9]);
	familyValues($dbhandle);
	printf("<B>Last logged on:</B> %s<BR>", $myrow[10]);
	printf("<B>Storyline:</B> %s<BR>", $myrow[11]);
}
mysql_close($dbhandle);

function familyValues($arg)
{
	$result = mysql_query("select familyvalues.description, toname,
		characterinfo.name 
		from family, familyvalues 
		left join characterinfo 
		on characterinfo.name = family.toname 
		where family.name = '".$_REQUEST{"name"}."' and 
		family.description = familyvalues.id", $arg)
		or die("Query failed : " . mysql_error());
	printf("<B>Family Relations:</B><BR><UL>");
	while ($myrow = mysql_fetch_row($result))
	{
		if ($myrow[2] == null)
		{
			printf("<LI>%s of %s<BR>", $myrow[0], $myrow[1]);
		}
		else
		{
			printf("<LI>%s of <A
HREF=\"/scripts/charactersheet.php?name=%s\">%s</A><BR>",
				$myrow[0],$myrow[1],$myrow[1]);
		}
	}
	printf("</UL>");
}
?>
<p>
<a HREF="/scripts/charactersheets.php">
<img SRC="/images/gif/webpic/buttono.gif"  
BORDER="0"></a><p>

</BODY>
</HTML>
