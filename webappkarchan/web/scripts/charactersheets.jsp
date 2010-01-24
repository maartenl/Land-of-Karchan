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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page language="java" import="javax.naming.InitialContext"%>
<%@ page language="java" import="javax.naming.Context"%>
<%@ page language="java" import="javax.sql.DataSource"%>
<%@ page language="java" import="java.sql.*"%>
<%@ page language="java" import="java.util.Enumeration"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - Character Sheets</title>
    </head>
    <body bgcolor=#FFFFFF background="/images/gif/webpic/back4.gif">
    <h1><img src="/images/gif/dragon.gif">Character Sheets</h1>


        <p>I feel the following needs a little explanation. Below you see a list of
available Character Sheets. They contain personal information like name,
title, place of birth, and the story line of characters, and references to
other characters. In each case these are put together by the people that
originally created the character on the game.</p>
        <p> It provides valuable
insights into the story behind this Game.</p>
        <p>Now you can add your piece of
information as well. Just fill in your name and password of the character
you created on the mud, and you will be presented with a form that you can
fill out, and change later in the same way.</p>

<table align=top border=0><tr>
<%

Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;

try
{
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();

stmt=con.prepareStatement("select mm_usertable.name from characterinfo, mm_usertable where mm_usertable.name=characterinfo.name order by mm_usertable.name");
rst=stmt.executeQuery();
char first = 'w';
int counter = 0;
while(rst.next())
{
    String aName = rst.getString("name");
    String aDisplayName = (aName.charAt(0)+"").toUpperCase() + aName.substring(1);
    if (aDisplayName.charAt(0) != first)
    {
        counter = 0;
        first = aDisplayName.charAt(0);
        %></tr><tr><td colspan="5"><h1><%=first%></h1></td></tr><tr><%
    }
    %>
    <td><a href="/karchan/scripts/charactersheet.jsp?name=<%=aName%>"><%=aDisplayName%></a></td>
    <%
    counter++;
    if (counter == 5)
    {
        counter = 0;
        %></tr><tr><%
    }
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

</tr></table>
<p>
<A HREF="/karchan/chronicles/chronicles2.html" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
<IMG ALT="Backitup!" SRC="/images/gif/webpic/buttono.gif" BORDER="0" name="back"><br/></A> </TD>
<DIV ALIGN=right>Last Updated $Date: 2006-01-15 10:25:36 +0100 (Sun, 15 Jan 2006) $
</DIV>
</p>
    </body>
</html>
