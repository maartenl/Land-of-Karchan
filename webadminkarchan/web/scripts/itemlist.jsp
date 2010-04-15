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
<%@ page language="java" import="mmud.web.BigFormatter"%>
<%@ page language="java" import="mmud.web.MultiColumnFormatter"%>
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

<H1>
<IMG SRC="/images/gif/dragon.gif" alt="dragon">
     Item Definition <%= request.getParameter("id") %></H1>

<A HREF="/karchan/admin/help/items.html" target="_blank">
<IMG SRC="/images/icons/9pt4a.gif" BORDER="0" alt="info"></A><P>
            <%

if (itsPlayerName == null) {
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("deputies")) {
    throw new RuntimeException("User does not have role 'deputies'.");
}

/* the following constraints need to be checked before any kind of update is
to take place:

changing itemdef:
- first check that the change is approved (i.e. owner or null)
- does wearable not conflict with anybody who is actively wearing it?
deleting itemdef:
- first check that the change is approved (i.e. owner or null)
- check item instances created using item
adding itemdef:
- all information filled out correctly?

*/
FormProcessor processor = null;
if (request.getParameter("id") != null)
{
    try {
        String[] columns = {"id", "name", "adject1", "adject2", "adject3", "manaincrease",
        "hitincrease", "vitalincrease", "movementincrease", "eatable", "drinkable",
        "lightable", "getable", "dropable", "visible", "description", "readdescr", "notes",
        "wearable", "copper", "weight", "pasdefense", "damageresistance", "container",
        "capacity", "isopenable", "keyid", "owner", "creation"};
        String[] displays = {"Id", "Name", "First Adjective", "Second Adjective", "Third Adjective",
        "Mana increase", "Hit increase", "Vital increase", "Movement increase", "Eatable",
        "Drinkable", "Lightable", "Getable", "Dropable", "Visible", "Description",
        "Read description", "Notes", "Wearable", "Copper", "Weight", "Passive defense",
        "Damage resistance", "Is Container", "Capacity", "Is openable", "Key Item Id",
        "Owner", "Creation"};
        processor = FormProcessorFactory.create("mm_items", itsPlayerName, displays, columns, new BigFormatter());
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
}
else
{
    try {
        String[] columns = {"id", "name", "adject1", "adject2", "adject3"};
        String[] displays = {"Id", "Name", "First Adjective", "Second Adjective", "Third Adjective"};
        processor = FormProcessorFactory.create("mm_items", itsPlayerName, displays, columns, new TableFormatter());
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
}
    %>

</body>
</html>
