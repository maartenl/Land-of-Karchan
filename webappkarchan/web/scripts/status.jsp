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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - Status</title>
    </head>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1><IMG SRC="/images/gif/dragon.gif">
Status of Land of Karchan</H1>

There is a hierarchie in the game and some have more powers and some less.
The reason for this that some people have to be accountable to others, and
everybody is accountable to the all mighty Implementor in the sky. The
hierachy is as follows:<P>

<H3>Implementors</H3>
<UL>
<LI>Karn, Ruler of Karchan, Keeper of the Key to the Room of Lost Souls
</UL>
<H3>Administrators</H3>
<ul>
<%

Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;

try
{
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();

stmt=con.prepareStatement("select mm_admin.name, title from mm_admin, mm_usertable where mm_admin.name = mm_usertable.name and mm_admin.validuntil > now()");
rst=stmt.executeQuery();
while(rst.next())
{
    %>
    <LI><%=rst.getString("name")%>, <%=rst.getString("title")%>
    <%
}
rst.close();
stmt.close();
con.close();
}
catch(Exception e)
{
System.out.println(e.getMessage());
%><%=e.getMessage()%>
<%
}
%>
</ul>
<p/>
<P><BR><BR>
<A HREF="/karchan/help/helpindex.html" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
<IMG ALT="Back" SRC="/images/gif/webpic/buttono.gif" BORDER="0" id="back" name="back"><br></A> </TD>
<DIV ALIGN=right>Last Updated $Date: 2006-01-15 10:25:36 +0100 (Sun, 15 Jan 2006) $
</DIV>

</body>
</html>
