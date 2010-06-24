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
<!DOCtype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <script language="JavaScript" src="/karchan/js/karchan.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan</title>
        <link rel="stylesheet" type="text/css" href="/css/karchangame.css" />
    </head>
    <body>
<div align=CENTER>
<form method="GET" ACTION="game.jsp" name="myForm" target="main">
<img src="/images/icons/bigtalk.ico" alt="big2talk" name="big2talk" id="big2talk" border="0" align="top" onclick="bigtalk()">
<input type="text" name="command" size="60" value="">
<input type="submit" value="Submit" onClick='document.myForm.command.command=""'>
<input type="hidden" name="name" value="<%= request.getParameter("name") %>">
<input type="hidden" name="frames" value="2">
<P>
</form>
</div>
    </body>
</html>
