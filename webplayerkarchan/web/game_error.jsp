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
<%@ page language="java" import="java.io.IOException"%>
<%
    Exception e = (Exception) request.getAttribute("exception");
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>An Error Occured Attempting to Logon To The Mud</title>
    </head>
    <body>

        <h1><IMG SRC="/images/gif/dragon.gif" alt="dragon" />An Error Occured Attempting To Logon To The Mud</h1><hr/>
        The following error occured:<p><TT>
        <%= e.getClass().getName() %> <%= e.getMessage() %></TT></p>
        <%
        if (e instanceof IOException)
        {
        %>Error code <B>111</B> usually indicates that the mud server has crashed or has
        been deactivated. It needs to be restarted.<P><%
        }
        %>
        Please contact Karn (at <A HREF="mailto:karn@karchan.org">karn@karchan.org</A>)
        as soon as possible to mention this problem and
        please mention the error message and error code.<P>
        Thank you.
    </body>
</html>
