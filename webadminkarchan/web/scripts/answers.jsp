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
<%@ page language="java" import="mmud.web.FormProcessorFactory"%>
<%@ page language="java" import="mmud.web.TableFormatter"%>
<%@ page language="java" import="mmud.web.FormProcessor"%>
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

<H1><IMG SRC="/images/gif/dragon.gif" alt="dragon">Answers</H1>
This script is used for manipulating the answers that can be provided by bots.<P>
The following bots have answers:<p/>
<%

if (itsPlayerName == null)
{
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("deputies"))
{
    throw new RuntimeException("User does not have role 'deputies'.");
}

String bot = request.getParameter("id");

if (bot == null)
{
    // show all bots that have answers
    FormProcessor processor = null;
    try {
        String[] columns = {"name"};
        String[] displays = {"Name"};
        processor = FormProcessorFactory.create("mm_answers", itsPlayerName, displays, columns, new TableFormatter());
        out.println(processor.getList(request, "select distinct name from mm_answers"));
    } catch (SQLException e) {
        out.println(e.getMessage());
        e.printStackTrace(new PrintWriter(out));
    %><%=e.getMessage()%>
    <%
    } finally {
        if (processor != null) {processor.closeConnection();}
    }
}
else
{
    // show all answers of a certain bot
    %> <%= bot%> has the following answers available:<p/><%
    FormProcessor processor = null;
    try {
        String[] columns = {"name", "question", "answer"};
        String[] displays = {"Name", "Question", "Answer"};
        processor = FormProcessorFactory.create("mm_answers", itsPlayerName, displays, columns, new TableFormatter());
        out.println(processor.getList(request));
    } catch (SQLException e) {
        out.println(e.getMessage());
        e.printStackTrace(new PrintWriter(out));
    %><%=e.getMessage()%>
    <%
    } finally {
        if (processor != null) {processor.closeConnection();}
    }
}

%>
<FORM METHOD="POST" ACTION="answers.jsp">
<b>
<INPUT TYPE="hidden" NAME="id" VALUE="<%=bot%>">
Question: <INPUT TYPE="text" SIZE="100" NAME="question" VALUE=""><BR>
Answer: <INPUT TYPE="text" SIZE="100" NAME="answer" VALUE=""><BR>
<INPUT TYPE="submit" VALUE="Submit Answer">
</b>
</FORM>

</body>
</html>
