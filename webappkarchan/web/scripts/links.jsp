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
        <title>Land of Karchan - Links</title>
    </head>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1><IMG SRC="/images/gif/dragon.gif" alt="dragon">Links</H1>

Hello, and welcome to the <I>Links</I> page. Here I put links to other
homepages concerning both Karchan (indirect and direct) and anything
relating to fantasy.
<p/>
<a href="/karchan/player/scripts/links.jsp">Add a link?</a><p/>
<H1>Homepages of Other Karchanians</H1>
<ul>
<%!
Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;
%>
            <%

 
            try
            {
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();

stmt=con.prepareStatement("select * from links where type = 1 order by creation");
rst=stmt.executeQuery();
while(rst.next())
{
    out.println("<li><A HREF=\"" + rst.getString("url") + "\">" +
		rst.getString("linkname") + "</A> (<i>" +
                rst.getString("name") + "</i>)<P>");
}
           rst.close();
        stmt.close();
        }
        catch(Exception e)
        {
        System.out.println(e.getMessage());
            %><%=e.getMessage()%>
            <%
            }
            %>

</ul>

<h1>General Links</h1>

<ul>
    <%
                try
            {

    stmt=con.prepareStatement("select * from links where type = 2 order by creation");
rst=stmt.executeQuery();
while(rst.next())
{
    out.println("<li><A HREF=\"" + rst.getString("url") + "\">" +
		rst.getString("linkname") + "</A> (<i>" +
                rst.getString("name") + "</i>)<P>");
}
           rst.close();
        stmt.close();
        }
        catch (Exception e)
        {
        System.out.println(e.getMessage());
            %><%=e.getMessage()%>
            <%
            }
            %>
</ul>

<H1>Guild Homepages</H1>
<ul>
        <%
                    try
            {

    stmt=con.prepareStatement("select * from links where type = 3 order by creation");
rst=stmt.executeQuery();
while(rst.next())
{
    out.println("<li><A HREF=\"" + rst.getString("url") + "\">" +
		rst.getString("linkname") + "</A> (<i>" +
                rst.getString("name") + "</i>)<P>");
}
           rst.close();
        stmt.close();
        }
        catch(Exception e)
        {
        System.out.println(e.getMessage());
            %><%=e.getMessage()%>
            <%
            }
            %>

</ul>
<%
        con.close();
%>
<H1>Programming Karchan Homepages</H1>

<ul>
<li>The Homepage of MMud - <A HREF="http://mmud.sourceforge.net">http://mmud.sourceforge.net</A><P>
<li>The Administation Page of MMud - <A
HREF="http://www.sourceforge.net/projects/mmud">http://sourceforge.net/projects/mmud</A><P>
</ul>

<H1>Glossary</H1>

<DL>
<DT>MMud
<DD>the source code or gaming engine on which Land of Karchan is built.
<DT>Sourceforge
<DD>A free Software Project Development Webpage
</DL>
<DIV ALIGN=right>Last Updated $Date: 2005-10-23 10:03:45 +0200 (Sun, 23 Oct 2005) $
</DIV>

<A HREF="/karchan/player/scripts/links.jsp">Links</A><P>

<A HREF="/karchan/index.jsp"><IMG SRC="/images/gif/webpic/buttono.gif" BORDER="0" alt="Back"></A><P>
</body>
</html>
