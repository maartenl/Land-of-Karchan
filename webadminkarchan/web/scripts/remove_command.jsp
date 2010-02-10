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
%>
<%
  itsPlayerName = request.getRemoteUser();
  itsPlayerSessionId = request.getSession(true).getId();
%>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Mmud - Admin</title>
        <%@include file="/includes/head.jsp" %>
    </head>
<BODY BGCOLOR=#FFFFFF>

<H1>
<IMG SRC="/images/gif/dragon.gif">
Table of Contents</H1>
<A HREF="#introduction">Introduction</A><P>
<A HREF="#bannedpeople">Banned People</A><P>
<A HREF="#unbannedchars">Unbanned Characters</A><P>
<A HREF="#bannedchars">Banned Characters</A><P>
<A HREF="#sillynames">Silly Names</A><P>

<A NAME="introduction"><H1><A HREF="/karchan/admin/help/banning.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0"></A>
Removing a Ban</H1>

<%

if (itsPlayerName == null)
{
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("deputies"))
{
    throw new RuntimeException("User does not have role 'deputies'.");
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
stmt=con.prepareStatement("select * from mm_admin where name =	'" +
        itsPlayerName + "' and validuntil >= now()"); //  and mm_usertable.lok = '" + itsPlayerSessionId + "'
rst=stmt.executeQuery();
if (!rst.next())
{
    // error getting the info, user not found?
    throw new RuntimeException("Cannot find " + itsPlayerName + " in the database!");
}
// end authorization check
// ===============================================================================
rst.close();

// remove banned people
String query = "select ?";
String which_one = "1";
if (request.getParameter("remove_banned") != null)
{
    query = "delete from mm_bantable where trim(address) = ?";
    which_one = request.getParameter("remove_banned");
}
if (request.getParameter("remove_unban") != null)
{
    query = "delete from mm_unbantable where name = ?";
    which_one = request.getParameter("remove_unban");
}
if (request.getParameter("remove_sillyname") != null)
{
    query = "delete from mm_sillynamestable where name = ?";
    which_one = request.getParameter("remove_sillyname");
}
if (request.getParameter("remove_bannedname") != null)
{
    query = "delete from mm_bannednamestable where name = ?";
    which_one = request.getParameter("remove_bannedname");
}
if (request.getParameter("remove_banned") != null)
{
    query = "delete from mm_bantable where trim(address) = ?";
    which_one = request.getParameter("remove_banned");
}

stmt=con.prepareStatement(query);
stmt.setString(1, which_one);
stmt.executeQuery();
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

</body>
</html>
