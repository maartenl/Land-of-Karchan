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

%>
<%
    // authentication && authorization

    /* name of the current user logged in */
    String itsPlayerName;

    /* password of the current user logged in, unsure if used */
    String itsPlayerPassword = "";

    /* sessionid/cookiepassword of current user */
    String itsPlayerSessionId;

  itsPlayerName = request.getRemoteUser();
  itsPlayerSessionId = request.getSession(true).getId();
%>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - Links</title>
        <link rel="stylesheet" type="text/css" href="/css/karchangame.css" />
    </head>
<body>
<h1>
<IMG SRC="/images/gif/dragon.gif" alt="dragon">Bug Report</h1>
    
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

String bug = request.getParameter("bug");
String title = "";
String description = "";
String answer = "";
String closed = "";
String date = "";
String name = itsPlayerName;

if (bug != null) // (!request.isUserInRole("deputies"))
{
    stmt=con.prepareStatement("select *, creation+0 as creation3, date_format(creation, \"%Y-%m-%d %T\") as creation2 from bugs where creation+0 = '" + bug + "'");
    rst=stmt.executeQuery();
    if (rst.next())
    {
         // full charactersheet
        bug = rst.getString("creation3");
        date = rst.getString("creation2");
        title = rst.getString("title");
        description = rst.getString("description");
        answer= rst.getString("answer");
        closed= rst.getString("closed");
        name= rst.getString("name");
    }
    rst.close();
    stmt.close();
}
boolean isChanged = false;
if ((request.getParameter("title") != null) &&
        (!request.getParameter("title").equals(title)))
{
    isChanged = true;
    title = request.getParameter("title");
}
if ((request.getParameter("description") != null) &&
        (!request.getParameter("description").equals(description)))
{
    isChanged = true;
    description = request.getParameter("description");
}
if ((request.getParameter("answer") != null) &&
        (!request.getParameter("answer").equals(answer)))
{
    isChanged = true;
    answer = request.getParameter("answer");
}
if ((request.getParameter("closed") != null) &&
        (!request.getParameter("closed").equals(closed)))
{
    isChanged = true;
    closed = request.getParameter("closed");
}
if ((request.getParameter("name") != null) &&
        (!request.getParameter("name").equals(name)))
{
    isChanged = true;
    name = request.getParameter("name");
}
if (isChanged)
{
    if (bug == null)
    {
        // new bug
        stmt=con.prepareStatement("replace into bugs (title, description, answer, closed, name, creation) values(?, ?, ?, ?, ?, now())");
        stmt.setString(1, title);
        stmt.setString(2, description);
        stmt.setString(3, answer);
        stmt.setString(4, closed);
        stmt.setString(5, itsPlayerName);
        stmt.executeQuery();
        stmt.close();
    }
    else
    {
        // replace existing bug
        stmt=con.prepareStatement("replace into bugs (title, description, answer, closed, name, creation) values(?, ?, ?, ?, ?, ?)");
        stmt.setString(1, title);
        stmt.setString(2, description);
        stmt.setString(3, answer);
        stmt.setString(4, closed);
        stmt.setString(5, itsPlayerName);
        stmt.setString(6, bug);
        stmt.executeQuery();
        stmt.close();
    }
}
%>
<FORM METHOD="POST" ACTION="bugs.jsp">
<% if (bug != null)
    {
    %>Bug: <INPUT TYPE="text" NAME="bug" VALUE="<%=bug%>" SIZE="60"><BR>
<%}%>
Title: <INPUT TYPE="text" NAME="title" VALUE="<%=title%>" SIZE="60"><BR>

Description:<BR>
<TEXTAREA NAME="description" ROWS="15" COLS="79"><%=description%></TEXTAREA><P>
<% if (bug != null)
    {
    %>
Answer:<BR>
<TEXTAREA NAME="answer" ROWS="15" COLS="79"><%=answer%></TEXTAREA><P>

<input type="radio" name="closed" value="0"
<%= ("0".equals(closed) ? "checked" : "") %>>Open<BR>
<input type="radio" name="closed" value="1"
<%= ("1".equals(closed) ? "checked" : "") %>>Closed<BR>

<%}%>

<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>
<%

con.close();
}
catch(Exception e)
{
out.println(e.getMessage());
e.printStackTrace(new PrintWriter(out));
%><%=e.getMessage()%>
<%
}
finally
{
    if (rst != null) {try {rst.close();} catch (Exception e){}}
    if (stmt != null) {try {stmt.close();} catch (Exception e){}}
    if (con != null) {try {con.close();} catch (Exception e){}}
}


%>

<p>
<a HREF="/karchan/scripts/bugs.jsp">
<img SRC="/images/gif/webpic/buttono.gif" BORDER="0" alt="Back"></a><p>
</body>
</html>
