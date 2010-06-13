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
        <title>Land of Karchan</title>
        <link rel="stylesheet" type="text/css" href="/css/karchangame.css" />
    </head>
    <body style="">
<IMG SRC="/images/gif/roos.gif"
USEMAP="#roosmap" BORDER="0" ISMAP ALT="compass"><P>
<P>
<script language="JavaScript">
<!-- In hiding!

browserName = navigator.appName;
 toc1on = new Image;
 toc1on.src ="../images/gif/webpic/new/buttonk.gif";
 toc2on = new Image;
 toc2on.src ="../images/gif/webpic/new/buttonj.gif";
 toc3on = new Image;
 toc3on.src ="../images/gif/webpic/new/buttonr.gif";

 toc1off = new Image;
 toc1off.src ="../images/gif/webpic/buttonk.gif";
 toc2off = new Image;
 toc2off.src ="../images/gif/webpic/buttonj.gif";
 toc3off = new Image;
 toc3off.src ="../images/gif/webpic/buttonr.gif";

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


<A HREF="game.jsp?command=quit&name=<%= request.getParameter("name") %>&frames=2"
TARGET=_top onMouseOver="img_act('toc2')" onMouseOut="img_inact('toc2')">
<IMG SRC="/images/gif/webpic/buttonj.gif" BORDER=0 ALT="quit" NAME="toc2">
</A><P>

<A HREF="game.jsp?command=sleep&name=<%= request.getParameter("name") %>&frames=2" onMouseOver="img_act('toc1')"
TARGET="main" onMouseOut="img_inact('toc1')">
<IMG SRC="/images/gif/webpic/buttonk.gif" BORDER=0 ALT="sleep" NAME="toc1">
</A><P>

<A HREF="game.jsp?command=clear&name=<%= request.getParameter("name") %>&frames=2" onMouseOver="img_act('toc3')"
TARGET="main" onMouseOut="img_inact('toc3')">
<IMG SRC="/images/gif/webpic/buttonr.gif" BORDER=0 ALT="clear" NAME="toc3">
</A>

<MAP NAME="roosmap">
<AREA SHAPE="POLY" COORDS="0,0,33,31,63,0,0,0" HREF="game.jsp?command=n&name=<%= request.getParameter("name") %>&frames=2" TARGET="main">
<AREA SHAPE="POLY" COORDS="0,63,33,31,63,63,0,63" HREF="game.jsp?command=s&name=<%= request.getParameter("name") %>&frames=2" TARGET="main">
<AREA SHAPE="POLY" COORDS="0,0,33,31,0,63,0,0" HREF="game.jsp?command=w&name=<%= request.getParameter("name") %>&frames=2" TARGET="main">
<AREA SHAPE="POLY" COORDS="63,0,33,31,63,63,63,0" HREF="game.jsp?command=e&name=<%= request.getParameter("name") %>&frames=2" TARGET="main">
</MAP>

<P>
<HR><FONT Size=1>&copy; Copyright Maarten van Leunen
<P>
    </body>
</html>
