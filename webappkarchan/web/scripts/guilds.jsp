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
        <title>Land of Karchan - Guilds</title>
    </head>
    <body BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
        <H1>
            <IMG SRC="/images/gif/dragon.gif" alt="dragon">
            Guilds</H1>

        <DL>
<%

            Connection con=null;
            ResultSet rst=null;
            PreparedStatement stmt=null;
            ResultSet rst2=null;
            PreparedStatement stmt2=null;

            try
            {
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();

stmt=con.prepareStatement("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2 from mm_guilds order by title");
rst=stmt.executeQuery();
while(rst.next())
{
    if ("".equals(rst.getString("guildurl")))
    {
        out.println("<DT><FONT SIZE=+2><B>" + rst.getString("title") + "</B> (" + rst.getString("bossname") + ")</FONT>");
    }
    else
    {
        out.println("<DT><FONT SIZE=+2><A HREF=\"" + rst.getString("guildurl") +
            "\"><B>" + rst.getString("title") +
            "</B></A> ("  + rst.getString("bossname") + ")</FONT>");
    }
    out.println("<DD>" + rst.getString("guilddescription"));
    out.println("Created on " + rst.getString("creation2") + ".<BR>");
stmt2=con.prepareStatement("select count(*) as count from mm_usertable where guild=\"" + rst.getString("name") + "\"");
rst2=stmt2.executeQuery();
if(rst2.next())
{
    out.println("Currently has " + rst2.getString("count") + " members.<p/>");
}
        rst2.close();
        stmt2.close();
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
            if (rst2 != null) {try {rst2.close();} catch (Exception e){}}
            if (stmt2 != null) {try {stmt2.close();} catch (Exception e){}}
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
        }

            %>

</DL>
    <P><BR><BR>
        <A HREF="/karchan/chronicles/chronicles2.html" onMouseOver="img_act('back')" onMouseOut="img_inact('back')">
            <IMG ALT="Back" SRC="/images/gif/webpic/buttono.gif" BORDER="0" id="back" name="back"><br></A> </TD>
<DIV ALIGN=right>Last Updated $Date: 2003-11-27 23:34:25 +0100 (Thu, 27 Nov 2003) $
</DIV>
</body>
</html>
