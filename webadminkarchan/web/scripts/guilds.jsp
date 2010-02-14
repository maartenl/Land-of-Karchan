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
<%@ page language="java" import="mmud.web.Formatter"%>
<%@ page language="java" import="mmud.web.BigFormatter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <%!// authentication && authorization

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
    <body>

        <H1><A HREF="/karchan/admin/help/scripting2.html" target="_blank">
                <IMG SRC="/images/icons/9pt4a.gif" BORDER="0" alt=""info"></A>
            Events</H1>


            <%

/* the following constraints need to be checked before any kind of update is
to take place:

adding guild:
- the guildname should be at least 5 characters
- the guildname is not allowed to contain spaces
- does the guild not already exist?
- does bossname exist?
- is the bossman already a guildmaster?
- does the bossman have a guildwish?
- is the guildname at least 3 characters?
changing guild:
- does the guild exist?
- does bossname exist?
- is the bossman already a guildmaster of another guild?
- are numeric fields numeric?
- is the administrator the owner of the guild.
deleting guild:
- any guild members left?
- is the administrator the owner of the guild.
*/
if (itsPlayerName == null) {
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("deputies")) {
    throw new RuntimeException("User does not have role 'deputies'.");
}
FormProcessor processor = null;
try {
    String[] columns = {"name", "name", "title", "daysguilddeath", "maxguilddeath",
    "minguildmembers", "minguildlevel", "guilddescription", "guildurl", "logonmessage",
    "bossname", "active", "owner", "creation"};
    String[] displays = {"Name", "Name", "Title", "Days until guild death", "Maximum days until guild death",
    "Minimum required members", "Minimum level requirement", "Description", "URL",
    "Logon message", "Guildmaster", "Active", "Owner", "Creation"};
    processor = FormProcessorFactory.create("mm_guilds", itsPlayerName, displays, columns, new BigFormatter());
    out.println(processor.getList(request));
    // print the members of each guild
    // print the hopefulls of each guild
} catch (SQLException e) {
    out.println(e.getMessage());
    e.printStackTrace(new PrintWriter(out));
    %><%=e.getMessage()%>
    <%
} finally {
    if (processor != null) {processor.closeConnection();}
}
%>

<FORM METHOD="GET" ACTION="guilds.jsp">
<b>
<TABLE>
<TR><TD>guildname</TD><TD><INPUT TYPE="text" NAME="addguildname" VALUE=""  SIZE="40" MAXLENGTH="40"></TD></TR>
<TR><TD>bossname</TD><TD><INPUT TYPE="text" NAME="bossname" VALUE="" SIZE="40" MAXLENGTH="40"></TD></TR>
</TD></TR>
</TABLE>
<INPUT TYPE="submit" VALUE="Add Guild">
</b>
</FORM>
    </body>
</html>
