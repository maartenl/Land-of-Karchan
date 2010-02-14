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
<%@ page language="java" import="mmud.web.MultiColumnFormatter"%>
<%@ page language="java" import="mmud.web.BigFormatter"%>
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
<body>
<H1><IMG SRC="/images/gif/dragon.gif" alt="dragon">Bug Report
</H1>
<A HREF="chars.jsp?idstartswith=A">A</A>
        <A HREF="chars.jsp?idstartswith=B">B</A>
        <A HREF="chars.jsp?idstartswith=C">C</A>
        <A HREF="chars.jsp?idstartswith=D">D</A>
        <A HREF="chars.jsp?idstartswith=E">E</A>
        <A HREF="chars.jsp?idstartswith=F">F</A>
        <A HREF="chars.jsp?idstartswith=G">G</A>
        <A HREF="chars.jsp?idstartswith=H">H</A>
        <A HREF="chars.jsp?idstartswith=I">I</A>
        <A HREF="chars.jsp?idstartswith=J">J</A>
        <A HREF="chars.jsp?idstartswith=K">K</A>
        <A HREF="chars.jsp?idstartswith=L">L</A>
        <A HREF="chars.jsp?idstartswith=M">M</A>
        <A HREF="chars.jsp?idstartswith=N">N</A>
        <A HREF="chars.jsp?idstartswith=O">O</A>
        <A HREF="chars.jsp?idstartswith=P">P</A>
        <A HREF="chars.jsp?idstartswith=Q">Q</A>
        <A HREF="chars.jsp?idstartswith=R">R</A>
        <A HREF="chars.jsp?idstartswith=S">S</A>
        <A HREF="chars.jsp?idstartswith=T">T</A>
        <A HREF="chars.jsp?idstartswith=U">U</A>
        <A HREF="chars.jsp?idstartswith=V">V</A>
        <A HREF="chars.jsp?idstartswith=W">W</A>
        <A HREF="chars.jsp?idstartswith=X">X</A>
        <A HREF="chars.jsp?idstartswith=Y">Y</A>
        <A HREF="chars.jsp?idstartswith=Z">Z</A>
        <P>

<%

if (itsPlayerName == null)
{
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("deputies"))
{
    throw new RuntimeException("User does not have role 'deputies'.");
}
/* the following constraints need to be checked before any kind of update is
to take place:

changing character:
- first check that the change is approved (i.e. owner or null)
- check that sex is male or female
- does room exist
- check if sex is correct
- is copper>=0
- is god 1,2,3,4
deleting character:
- checking attributes
- checking items (mm_charitemtable)
- checking mail
- checking family/characterinfo
adding character:
*/

if (request.getParameter("id") != null)
{
    FormProcessor processor = null;
    try {
       String[] columns = {"name", "name", "address", "password", "title", "realname",
        "email", "race", "sex", "age", "length", "width", "complexion", "eyes", "face",
        "hair", "beard", "arm", "leg", "notes", "lok", "guild", "whimpy", "sleep",
       "copper", "god", "active", "lastlogin", "birth", "creation", "owner", "room"};
       String[] displays = {"Name", "Name", "Address", "Password", "Title", "Real name",
       "Email", "Race", "Gender", "Age", "Length", "Width", "Complexion", "Eyes", "Face",
       "Hair", "Beard", "Arms", "Legs", "Notes", "lok", "Guild", "Whimpy", "sleep",
       "Copper", "God", "Active", "Last time logged in", "Birthdate", "Created", "Owner", "Room id"};
       processor = FormProcessorFactory.create("mm_usertable",
               itsPlayerName, displays, columns, new BigFormatter());
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
else
if (request.getParameter("idstartswith") != null)
{
    FormProcessor processor = null;
    try {
       String[] columns = {"name", "name"};
       String[] displays = {"Name", "Name"};
       processor = FormProcessorFactory.create("mm_usertable",
               itsPlayerName, displays, columns, new MultiColumnFormatter());
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
else
{
    out.println("Expecting parameters.");
}


%>

</body>
</html>
