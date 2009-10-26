<?php
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
<script language="JavaScript" src="/karchan/js/karchan.js"></script>
</HEAD>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<DIV ALIGN=CENTER>
<FORM METHOD="GET" ACTION="/scripts/mud.php" id="CommandForm" NAME="myForm" TARGET="main">
<IMG SRC="/images/icons/bigtalk.ico" alt="big2talk" name="big2talk" id="big2talk" border="0" align="top" onclick="bigtalk()">
<INPUT TYPE="text" ID="command" NAME="command" SIZE="60" VALUE="">
<INPUT TYPE="submit" VALUE="Submit" onClick='document.myForm.command.command=""'>
<INPUT TYPE="hidden" NAME="name" VALUE="<?php echo $_COOKIE["karchanname"] ?>">
<INPUT TYPE="hidden" NAME="frames" VALUE="2">
<P>
</FORM>
</DIV>
</BODY>
</HTML>
