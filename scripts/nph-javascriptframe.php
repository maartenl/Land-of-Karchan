<?php
/*-------------------------------------------------------------------------
svninfo: $Id: nph-javascriptframe.php 986 2005-10-20 19:26:20Z maartenl $
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
<TITLE>Karchan</TITLE>
</HEAD>

<BODY BGCOLOR=#FFFFFF OnLoad="top.frames[5].location='/scripts/nph-addendum.php?name=<?php echo $_COOKIE["karchanname"] ?>&password=<?php echo$_REQUEST{"password"} ?>'">
<SCRIPT LANGUAGE="JavaScript1.2">
<!-- Hide script from older browsers

var string = "Spul!<BR>"
var nieuw = 0
var d = top.frames[1].document


function stuffString(fstring)
{
	string+=fstring
	nieuw = 1
}

function givemessage()
{
	alert ("Hoihoihoi!")
}

function test()
{
	if (nieuw == 1) {
	d.writeln(string)
	nieuw = 0
	string = ""
	}
	setTimeout("test()", 1000)
}


d.open()
string = ""
nieuw = 0
test()
// End the hiding here. -->
</SCRIPT>
<P>That's all, folks.

</HTML>
