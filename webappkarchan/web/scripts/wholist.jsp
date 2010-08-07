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
<%@ page language="java" import="java.sql.*"%>
<%@ page language="java" import="javax.naming.InitialContext"%>
<%@ page language="java" import="javax.naming.Context"%>
<%@ page language="java" import="javax.sql.DataSource"%>
<%@ page language="java" import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.logging.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - Who</title>
    </head>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H2>
<IMG SRC="/images/gif/dragon.gif" alt="dragon">
List of All Active Users</H2>
<%!
        private Logger itsLog = Logger.getLogger("mmud");
%>
<%

Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;

try
{
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();
itsLog.finest(this.getClass().getName() + ": connection with database opened.");
stmt=con.prepareStatement("select name, mm_usertable.title, sleep, 	floor((unix_timestamp(NOW())-unix_timestamp(lastlogin)) / 60) as min,	((unix_timestamp(NOW())-unix_timestamp(lastlogin)) % 60) as sec,	if (mm_area.area <> \"Main\", concat(\" in \" , mm_area.shortdesc), \"\") as area	from mm_usertable, mm_rooms, mm_area 	where god<=1 and active=1 and mm_rooms.id = mm_usertable.room and	mm_rooms.area = mm_area.area");
rst=stmt.executeQuery();
while(rst.next())
{
    %>
<li><%=rst.getString("name")%>, <%=rst.getString("title")%>, <%=(rst.getInt("sleep") != 1 ? "" : "sleeping")%> <%=rst.getString("area")%> (logged on <%=rst.getString("min")%> min, <%=rst.getString("sec")%> sec ago)
    <%
}
}
catch(Exception e)
{
System.out.println(e.getMessage());
%><%=e.getMessage()%>
<%
}
finally
{
    if (rst != null) {try {rst.close();} catch (Exception e){}}
    if (stmt != null) {try {stmt.close();} catch (Exception e){}}
    if (con != null) {try {con.close();} catch (Exception e){}}
    itsLog.finest(this.getClass().getName() + ": connection with database closed.");
}
%>
<P><BR><BR>
<A HREF="/karchan/index.jsp" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
<IMG ALT="Back" SRC="/images/gif/webpic/buttono.gif" BORDER="0" id="back" name="back"><br></A>
<DIV ALIGN=right>Last Updated $Date: 2006-01-15 10:25:36 +0100 (Sun, 15 Jan 2006) $
</DIV>

    </body>
</html>
