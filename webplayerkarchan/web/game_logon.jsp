<%--
-----------------------------------------------------------------------
svninfo: $Id: charactersheets.php 1078 2006-01-15 09:25:36Z maartenl $
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
-----------------------------------------------------------------------

--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - Logon Screen</title>
        <link rel="stylesheet" type="text/css" href="/css/karchangame.css" />
    </head>
    <body>
<DIV ALIGN=center>
<script type="text/javascript"><!--
google_ad_client = "pub-1476585918448696";
/* MyFIrstAd */
google_ad_slot = "2319287585";
google_ad_width = 728;
google_ad_height = 90;
//-->
</script>
<script type="text/javascript"
src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>
<H1>Game Logon screen</H1>

<form method="POST" action="game.jsp">
<table border="1" cellspacing="0"><tr><td>
<table border="0" frame="box" bgcolor="#AACCFF" width="400"
background="/images/gif/webpic/back4.gif">
<tr>
	<td width="25%">
	Name: </td><td>

<input type="text" maxlength="19" size="36" name="name"></td>
</tr>
<tr>
	<td>
	Password:</td><td>
<input type="password" name="password" size="36" maxlength="39"></td>
<input type="hidden" name="action" value="logon"></td>
</tr>
<tr colspan="2" border="above">
	<td></td>
	<td align="right">
<input type="submit" value="Submit">
<input type="reset" value="Reset">
</td>
</tr>
<tr>
	<td>Frames:</td><td>
	<input type="radio" name="frames" value="1" checked>No frames<BR>
	<input type="radio" name="frames" value="2">Yes frames</td>
</tr>
</table>
</td></tr></table>
</form>

<I>If you wish to create a new character, click <A
HREF="/karchan/newchar.html">here</A>.</I>
<P>
<script type="text/javascript"><!--
google_ad_client = "pub-1476585918448696";
/* MyFIrstAd */
google_ad_slot = "2319287585";
google_ad_width = 728;
google_ad_height = 90;
//-->
</script>
<script type="text/javascript"
src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>

</div>
    </body>
</html>
