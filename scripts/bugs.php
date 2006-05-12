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
<HTML>
<HEAD>
<TITLE>
Land of Karchan - Bug Reports
</TITLE>
</HEAD>
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1><IMG SRC="/images/gif/dragon.gif">Bug Report
</H1>
<TABLE>
<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/connect.php"; 
if (isset($_REQUEST{"title"}))
{
	$query = "insert into bugs
		(title, description, name, creation)
		select \"".
		quote_smart($_REQUEST{"title"})."\", \"".
		quote_smart($_REQUEST{"description"})."\", \"".
		quote_smart($_COOKIE{"karchanname"})."\", now()
		from mm_usertable
		where name = \"".
		quote_smart($_COOKIE["karchanname"]).
		"\" and lok = \"".
		quote_smart($_COOKIE["karchanpassword"]).
		"\"";
	mysql_query($query
		, $dbhandle)
		or error_message("Query failed : " . mysql_error());
}


// show results
$result = mysql_query("select *, creation+0 as creation3, date_format(creation, \"%Y-%m-%d %T\") as
	creation2 from bugs order by creation desc"
	, $dbhandle)
	or error_message("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	printf("<TR><TD><A HREF=\"/scripts/bugs.php?bug=".$myrow["creation3"]."
		\">".$myrow["creation2"].
		"</A></TD><TD>".($myrow["closed"]==1?"<FONT COLOR=grey>":"").$myrow["title"].
		"</TD><TD>".($myrow["closed"]==1?"<FONT COLOR=grey>":"").$myrow["name"].
		"</TD><TD>".($myrow["closed"]==1?"<FONT COLOR=grey>":"").($myrow["closed"]==1?"Closed":"Open")."</TD></TR>");
	if (isset($_REQUEST{"bug"}) && 
		($myrow["creation3"]==$_REQUEST{"bug"}))
	{
		printf("<TR><TD COLSPAN=4><B>Description</B><P>".$myrow["description"]."<BR>");
		printf("<B>Answer</B><P>".$myrow["answer"]."</TD></TR>");
	}
}
printf("</TABLE>");
mysql_close($dbhandle);
?>

<?php
if ((isset($_COOKIE{"karchanpassword"})) &&
    (isset($_COOKIE["karchanname"])))
{   
?>
Add Bug Report:<P>
<FORM METHOD="GET" ACTION="/scripts/bugs.php">
Title: <INPUT TYPE="text" NAME="title" VALUE="" SIZE="60"><BR>
Description:<BR>
<TEXTAREA NAME="description" VALUE="" ROWS="30" COLS="85"></TEXTAREA><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>
<?php
} 
?>

<script language="JavaScript">

<!-- In hiding!
 browserName = navigator.appName;          
           browserVer = parseInt(navigator.appVersion);
               backon = new Image;          
               backon.src = "/images/gif/webpic/new/buttono.gif";
               
               
               backoff = new Image;
               backoff.src = "/images/gif/webpic/buttono.gif";
               

function img_act(imgName) {
        imgOn = eval(imgName + "on.src");
        document [imgName].src = imgOn;
}

function img_inact(imgName) {
        imgOff = eval(imgName + "off.src");
        document [imgName].src = imgOff;
}

//-->
</SCRIPT>
<P>
<A HREF="/karchan/help/helpindex.html" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
<IMG ALT="Backitup!" SRC="/images/gif/webpic/buttono.gif" BORDER="0" name="back"><br></A> </TD>
<DIV ALIGN=right>Last Updated $Date$
</DIV>
</BODY>
</HTML>

