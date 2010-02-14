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
<%@ page language="java" import="mmud.web.FormProcessor"%>
<%@ page language="java" import="mmud.web.TableFormatter"%>
<%@ page language="java" import="mmud.web.MultiColumnFormatter"%>
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
<%

if (itsPlayerName == null)
{
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("deputies"))
{
    throw new RuntimeException("User does not have role 'deputies'.");
}

    FormProcessor processor = null;
    try {

        String[] columns = {"address", "days", "IP", "name", "deputy", "date", "reason"};
        String[] displays = {"Address", "Days to go", "IPaddress", "Name", "Deputy", "Date of occurrence", "Reason"};
        processor = FormProcessorFactory.create("mm_bantable", itsPlayerName, displays, columns, new TableFormatter());
        out.println(processor.getList(request));
    } catch (SQLException e) {
        out.println(e.getMessage());
        e.printStackTrace(new PrintWriter(out));
    %><%=e.getMessage()%>
    <%
    } finally {
        if (processor != null) {processor.closeConnection();}
    }
    %>

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
<%
// show list of banned people
    try {

        String[] columns = {"name"};
        String[] displays = {"Name"};
        processor = FormProcessorFactory.create("mm_unbantable", itsPlayerName, displays, columns, new MultiColumnFormatter());
        out.println(processor.getList(request));
    } catch (SQLException e) {
        out.println(e.getMessage());
        e.printStackTrace(new PrintWriter(out));
    %><%=e.getMessage()%>
    <%
    } finally {
        if (processor != null) {processor.closeConnection();}
    }
    %>
<%--int count = 1;
    count ++;
    if (count > 40)
    {
            count = 1;
            out.println("</TD><TD>");
    }
}--%>


Add Unbanname:<FORM METHOD="GET" ACTION="add_banned.jsp">
<INPUT TYPE="text" NAME="unbanname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<A NAME="bannedchars"><H1>Banned Characters</H1>
<%
// show list of banned people
    try {

        String[] columns = {"name", "deputy", "creation", "days", "reason"};
        String[] displays = {"Name", "Deputy", "creation", "days", "reason"};
        processor = FormProcessorFactory.create("mm_bannednamestable", itsPlayerName, displays, columns, new TableFormatter());
        out.println(processor.getList(request));
    } catch (SQLException e) {
        out.println(e.getMessage());
        e.printStackTrace(new PrintWriter(out));
    %><%=e.getMessage()%>
    <%
    } finally {
        if (processor != null) {processor.closeConnection();}
    }
%>

            Add Banned name:<FORM METHOD="GET" ACTION="add_banned.jsp">
<INPUT TYPE="text" NAME="add_bannedname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
Days:<INPUT TYPE="text" NAME="add_bandays" VALUE="" SIZE="3" MAXLENGTH="3"><P>
Reason:<INPUT TYPE="text" NAME="add_banreason" VALUE="" SIZE="40" MAXLENGTH="255"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

<A NAME="sillynames"><H1>Banned Silly Names</H1>
            <%
// show list of banned people
    try {

        String[] columns = {"name"};
        String[] displays = {"Name"};
        processor = FormProcessorFactory.create("mm_sillynamestable", itsPlayerName, displays, columns, new MultiColumnFormatter(80));
        out.println(processor.getList(request));
    } catch (SQLException e) {
        out.println(e.getMessage());
        e.printStackTrace(new PrintWriter(out));
    %><%=e.getMessage()%>
    <%
    } finally {
        if (processor != null) {processor.closeConnection();}
    }

%>

Add Sillyname:<FORM METHOD="GET" ACTION="add_banned.jsp">
<INPUT TYPE="text" NAME="sillyname" VALUE="" SIZE="20" MAXLENGTH="20"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

</body>
</html>
