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
stmt.close();

String bot = request.getParameter("bot");

if (bot == null)
{
    stmt=con.prepareStatement("select distinct name from mm_answers");
    rst=stmt.executeQuery();
    while (rst.next())
    {
            out.println("<A HREF=\"scripts/answers.jsp?bot=" + rst.getString("name") + "\">" + rst.getString("name") + "</A><BR>");
    }
    rst.close();
    stmt.close();
}
else
{
%> <%= bot%> has the following answers available:
<BR><table><TR><TD><B>Question</B></TD><TD><B>Answer</B></TD></TR>
<%

stmt=con.prepareStatement("select question, answer from mm_answers where name = \"" + bot + "\"");
rst=stmt.executeQuery();
while (rst.next())
{%>
<TR><TD><%=rst.getString("question")%></TD><TD><%=rst.getString("answer")%></TD></TR>
<%}
rst.close();
stmt.close();
%></table><%

String question = request.getParameter("question");
String answer = request.getParameter("answer");

boolean isChanged = (question != null && answer != null);

if (isChanged)
{
stmt=con.prepareStatement("replace into mm_answers (name, question, answer) " +
			"values( ?, ?, ?)");
stmt.setString(1, bot);
stmt.setString(2, question);
stmt.setString(3, answer);
stmt.executeQuery();
stmt.close();
}
%>
<FORM METHOD="POST" ACTION="answers.jsp">
<b>
<INPUT TYPE="hidden" NAME="bot" VALUE="<%=bot%>">
Question: <INPUT TYPE="text" SIZE="100" NAME="question" VALUE="<%=(question == null ? "":question)%>"><BR>
Answer: <INPUT TYPE="text" SIZE="100" NAME="answer" VALUE="<%=(answer==null ? "" : answer)%>"><BR>
<INPUT TYPE="submit" VALUE="Submit Answer">
</b>
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
%>

</body>
</html>
