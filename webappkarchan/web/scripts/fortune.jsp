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
        <title>Land of Karchan - Fortune 100</title>
    </head>
    <body BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
        <H2>
<IMG SRC="/images/gif/dragon.gif" alt="dragon">
List of Very Rich People</H2>
<TABLE>
<TR><TD><B>Position</B></TD><TD><B>Name</B></TD><TD><B>Money</B></TD></TR>

<%

Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;

try
{
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();

stmt=con.prepareStatement("select name, floor(copper/100) as gold, floor((copper % 100)/10) as silver, copper % 10 as copper	from mm_usertable	where god<=1	order by gold desc, silver desc, copper desc, name asc	limit 100");
rst=stmt.executeQuery();
int counter = 0;
while(rst.next())
{
    if (counter < 5)
    {
	out.println("<TR><TD>" + counter + "</TD><TD><B>" + rst.getString("name") +
                "</B></TD><TD><B>" + rst.getString("gold") + " gold, " + rst.getString("silver") +
                " silver, " + rst.getString("copper") + " copper</B></TD></TR>\r\n");
    }
    else
    {
	out.println("<TR><TD> " + counter + "</TD><TD>" + rst.getString("name") +
                "</TD><TD>" + rst.getString("gold") + " gold, " + rst.getString("silver") +
                " silver, " + rst.getString("copper") + " copper</TD></TR>\r\n");
    }

    counter++;
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
}

%>

</TABLE>
<hr>
<P><BR><BR>
<A HREF="/karchan/chronicles/chronicles2.html" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
<IMG ALT="Back" SRC="/images/gif/webpic/buttono.gif" BORDER="0" name="back"><br></A>
<DIV ALIGN=right>Last Updated $Date: 2003-11-27 23:34:25 +0100 (Thu, 27 Nov 2003) $
</DIV>

    </body>
</html>
