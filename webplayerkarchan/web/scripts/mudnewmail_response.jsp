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

<%@ page language="java"%>
<%@ page language="java" import="javax.naming.InitialContext"%>
<%@ page language="java" import="java.io.PrintWriter"%>
<%@ page language="java" import="java.io.IOException"%>
<%@ page language="java" import="javax.naming.Context"%>
<%@ page language="java" import="javax.sql.DataSource"%>
<%@ page language="java" import="java.sql.*"%>
<%@ page language="java" import="java.util.Enumeration"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%!
// authentication && authorization

/* name of the current user logged in */
private String itsPlayerName;

/* password of the current user logged in, unsure if used */
private String itsPlayerPassword = "";

/* sessionid/cookiepassword of current user */
private String itsPlayerSessionId;


/**
 *
 * Character Entity Code
 * <    &lt;
 * >    &gt;
 * &    &amp;
 * ’    &#039;
 * "    &#034;
 */
public static String transform(String s)
{
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\'", "&#039;").replace("\"", "&#034;");
}

%>
<%
  itsPlayerName = request.getRemoteUser();
  itsPlayerSessionId = request.getSession(true).getId();
%>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - <%=itsPlayerName%></title>
        <link rel="stylesheet" type="text/css" href="/css/karchangame.css" />
        <!-- ** CSS ** -->
        <!-- base library -->
        <link rel="stylesheet" type="text/css" href="/ext-3.2.1/resources/css/ext-all.css" />
                <!-- overrides to base library -->


        <!-- ** Javascript ** -->
        <!-- ExtJS library: base/adapter -->
         <script type="text/javascript" src="/ext-3.2.1/adapter/ext/ext-base.js"></script>
        <!-- ExtJS library: all widgets -->
         <script type="text/javascript" src="/ext-3.2.1/ext-all-debug.js"></script>

        <!-- overrides to base library -->

        <!-- extensions -->

        <!-- page specific -->

        <%

if (itsPlayerName == null)
{
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("players"))
{
    throw new RuntimeException("User does not have role 'players'.");
}

Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;

try
{
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();


// ===============================================================================
// begin authorization check
stmt=con.prepareStatement("select * from mm_usertable where mm_usertable.name =	'" +
        itsPlayerName + "'"); //  and mm_usertable.lok = '" + itsPlayerSessionId + "'
rst=stmt.executeQuery();
if (!rst.next())
{
    // error getting the info, user not found?
    throw new RuntimeException("Cannot find " + itsPlayerName + " in the database!");
}
// end authorization check
// ===============================================================================
rst.close();
stmt.close();

stmt=con.prepareStatement("insert into mm_mailtable (name, toname, header, whensent, haveread, newmail, message) values(?,?,?,now(), 0, 1, ?)");
stmt.setString(1, itsPlayerName);
stmt.setString(2, request.getParameter("first"));
stmt.setString(3, request.getParameter("header"));
stmt.setString(4, request.getParameter("body"));
if (stmt.executeUpdate() != 1)
{
    throw new Exception("Mudmail has not been sent.");
}
stmt.close();


con.close();
}
catch(Exception e)
{
out.println(e.getMessage());
e.printStackTrace(new PrintWriter(out));
%><%=e.getMessage()%>
<%
}
%>

    </head>
<BODY>
<h1>
<IMG SRC="/images/gif/dragon.gif" alt="dragon">
MudMail - <%=itsPlayerName%></h1>
The following mail has been sent:
<p><b>From:</b> <%=itsPlayerName%></p>
<p><b>To:</b> <%= request.getParameter("first") %></p>
<p><b>Header:</b> <%= request.getParameter("header") %></p>
<p><b>Body</b> <%= request.getParameter("body") %></p>

<p>
<a HREF="mudmail.jsp">
<img SRC="/images/gif/webpic/buttono.gif"
BORDER="0" alt="Back"></a><p>
    </body>
</html>
