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
<%@ page language="java" import="javax.naming.Context"%>
<%@ page language="java" import="javax.sql.DataSource"%>
<%@ page language="java" import="java.sql.*"%>
<%@ page language="java" import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - Bug Reports</title>
    </head>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1><IMG SRC="/images/gif/dragon.gif" alt="dragon">Bug Reports
</H1>
<a href="/karchan/player/scripts/bugs.jsp">Create a bug report</a><p/>
<table>

<%

Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;

try
{
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();

stmt=con.prepareStatement("select *, creation+0 as creation3, date_format(creation, \"%Y-%m-%d %T\") as	creation2 from bugs order by creation desc");
rst=stmt.executeQuery();
while(rst.next())
{	
        String font = (rst.getInt("closed") == 1 ? "<font color=grey>" : "");
        String isclosed = (rst.getInt("closed") == 1 ? "Closed" : "Open");
    %>
<tr>
    <td><a href="/karchan/scripts/bugs.jsp?bug=<%=rst.getString("creation3")%>"><%=rst.getString("creation2")%></a></td>
    <td><%=font%><%=rst.getString("title")%></td>
    <td><%=font%><%=rst.getString("name")%></td>
    <td><%=font%><%=isclosed%></td>
</tr>
<%
if (rst.getString("creation3").equals(request.getParameter("bug")))
{
%>
<tr>
    <td colspan=4><B>Description</B><P><%=rst.getString("description")%><BR>
        	<B>Answer</B><P><%=(rst.getString("answer") == null ? "&lt;no answer yet&gt;" : rst.getString("answer"))%></td>

</tr>
<%    }
    
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
</table>
<P>
<A HREF="/karchan/help/helpindex.html" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
<IMG ALT="Back" SRC="/images/gif/webpic/buttono.gif" BORDER="0" name="back"><br></A>
<DIV ALIGN=right>Last Updated $Date: 2006-05-12 16:40:26 +0200 (Fri, 12 May 2006) $
</DIV>
    </body>
</html>
