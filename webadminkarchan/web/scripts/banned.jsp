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
Introduction</H1>

The banning of people and characters from the mud may seem to be
complicated at first. Therefore this short introduction. The following rules
govern wether or not the character is banned. The rules are processed in the
order that they appear here. If a rule indicates that a person is
<I>banned</I> or
is <I>not banned</I>, then all te next rules are skipped.
<OL><LI>if the name exists in the Silly names table, the character is
<I>banned</I>
<LI>if the name exists in the Unbanned names table, the character is <I>not
banned</I>
<LI>if the name exists in the Banned names table, the character is
<I>banned</I>
<LI>if the ip number exists or is part of a range of ipnumbers specified in
the Banned people table, the character is <I>banned</I>
<LI>in <I>all</I> other cases the character is allowed to log on.
</OL>

<A NAME="bannedpeople"><H1>Banned People</H1>
<TABLE><TR><TD></TD><TD><B>Address</B></TD><TD><B>Days</B></TD><TD><B>Ip
</B></TD><TD><B>Name</B></TD><TD><B>Deputy</B></TD><TD><B>Date
</B></TD><TD><B>Reason</B></TD></TR>
<%

if (itsPlayerName == null)
{
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("deputies"))
{
    throw new RuntimeException("User does not have role 'deputies'.");
}
/*
showing the different areas and what rooms belong to which area.

the following constraints need to be checked before any kind of update
is to take place:

changing area:
- the area must exist
- is the administrator the owner of the area
*/

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

// show list of banned people
stmt=con.prepareStatement("select * from mm_bantable");
rst=stmt.executeQuery();
while (rst.next())
{
%>
<TR><TD><a HREF="remove_banned.jsp?remove_banned=<%=rst.getString("address")%>">X</a></TD><TD> <%=rst.getString("address")%></TD>
<TD><%=rst.getString("days")%>
<TD><%=rst.getString("IP")%>
<TD><%=rst.getString("name")%>
<TD><%=rst.getString("deputy")%>
<TD><%=rst.getString("date")%>
<TD><%=rst.getString("reason")%></TD></TR>
<%
    }
rst.close();
stmt.close();
%>
</TABLE>
Add Ban:<FORM METHOD="GET" ACTION="add_banned.jsp">
Address:<INPUT TYPE="text" NAME="ban_address" VALUE="" SIZE="40" MAXLENGTH="40"><P>
Days:<INPUT TYPE="text" NAME="ban_days" VALUE="" SIZE="3" MAXLENGTH="3"><P>
Ip:<INPUT TYPE="text" NAME="ban_ip" VALUE="" SIZE="40" MAXLENGTH="40"><P>
Name:<INPUT TYPE="text" NAME="ban_name" VALUE="" SIZE="20" MAXLENGTH="20"><P>
Reason:<INPUT TYPE="text" NAME="ban_reason" VALUE="" SIZE="40" MAXLENGTH="255"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<A NAME="unbannedchars"><H1>Unbanned Characters</H1>
<TABLE><TR><TD>
<%
// show list of banned people
stmt=con.prepareStatement("select * from mm_unbantable order by name");
rst=stmt.executeQuery();
int count = 1;
while (rst.next())
{
    out.println("<a HREF=\"remove_banned.jsp?remove_unban=" +
            rst.getString(0) + "\">X</a> " +
            rst.getString(0) + "<BR>");
    count ++;
    if (count > 40)
    {
            count = 1;
            out.println("</TD><TD>");
    }
}
rst.close();
stmt.close();
%>

Add Unbanname:<FORM METHOD="GET" ACTION="add_banned.jsp">
<INPUT TYPE="text" NAME="unbanname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<A NAME="bannedchars"><H1>Banned Characters</H1>
<TABLE><TR><TD></TD><TD><B>Name</B></TD><TD><B>Deputy</B></TD><TD><B>Creation
</B></TD><TD><B>Days</B></TD><TD><B>Reason</B></TD></TR>
<%
// show list of banned people
stmt=con.prepareStatement("select * from mm_bannednamestable order by name");
rst=stmt.executeQuery();
while (rst.next())
{
    %>
<tr>
    <td><a HREF="remove_banned.jsp?remove_bannedname=<%=rst.getString("name")%>">"X</a></td>
    <td><%=rst.getString("name")%></A></TD>
    <TD><%=rst.getString("deputy")%></TD>
    <TD><%=rst.getString("creation")%></TD>
    <TD><%=rst.getString("days")%></TD>
    <TD><%=rst.getString("reason")%></TD>
</tr>
            <%
}
rst.close();
stmt.close();
%>

            Add Banned name:<FORM METHOD="GET" ACTION="add_banned.jsp">
<INPUT TYPE="text" NAME="add_bannedname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
Days:<INPUT TYPE="text" NAME="add_bandays" VALUE="" SIZE="3" MAXLENGTH="3"><P>
Reason:<INPUT TYPE="text" NAME="add_banreason" VALUE="" SIZE="40" MAXLENGTH="255"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<A NAME="sillynames"><H1>Banned Silly Names</H1>
<table><tr>
            <%
// show list of banned people
stmt=con.prepareStatement("select * from mm_sillynamestable order by name");
rst=stmt.executeQuery();
count = 1;
while (rst.next())
{
    out.println("<A HREF=\"remove_banned.jsp?remove_sillyname=" +
            rst.getString(0) + "\">X</a> " +
            rst.getString(0) + "<BR>");
    count ++;
    if (count > 40)
    {
            count = 1;
            out.println("</TD><TD>");
    }
}
rst.close();
stmt.close();
%>
    </tr></table>

<%
con.close();
}
}
catch(Exception e)
{
out.println(e.getMessage());
e.printStackTrace(new PrintWriter(out));
%><%=e.getMessage()%>
<%
}
%>
Add Sillyname:<FORM METHOD="GET" ACTION="add_banned.jsp">
<INPUT TYPE="text" NAME="sillyname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

</body>
</html>
