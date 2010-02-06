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
<%@ page language="java" import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <%!
// authentication && authorization

/* name of the current user logged in */
private String itsPlayerName = "";

/* password of the current user logged in, unsure if used */
private String itsPlayerPassword = "";

/* sessionid/cookiepassword of current user */
private String itsPlayerSessionId = "";
%>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - Links</title>
    </head>
<BODY BGCOLOR=#FFFFFF>
            <%
            if (request.getParameter("linkname") != null)
                {
            Connection con=null;
            ResultSet rst=null;
            PreparedStatement stmt=null;

            try
            {
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();

stmt=con.prepareStatement("replace into links (linkname, url, type, name) select ?, ?, ?, name from mm_usertable where name = ? and lok = ?");
stmt.setString(0, request.getParameter("linkname"));
stmt.setString(1, request.getParameter("url"));
stmt.setString(2, request.getParameter("type"));
stmt.setString(3, itsPlayerName);
stmt.setString(4, itsPlayerSessionId);
stmt.executeQuery();
        stmt.close();
        con.close();
        }
        catch(Exception e)
        {
        System.out.println(e.getMessage());
            %><%=e.getMessage()%>
            <%
            }
            }
            else
                {%>
Add link:<FORM METHOD="GET" ACTION="/scripts/links.php">
Name of Website: <INPUT TYPE="text" NAME="linkname" VALUE="" SIZE="50" MAXLENGTH="255"><P>
Url of Website: <INPUT TYPE="text" NAME="url" VALUE="" SIZE="50" MAXLENGTH="255"><P>
Type: <BR><input type="radio" name="type" value="1" checked>Karchanian Homepage<BR>
<input type="radio" name="type" value="2">General Link<BR>
<input type="radio" name="type" value="3">Guild Homepage<BR>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear"><P>
</FORM>

                         <%
                }
            %>

</body>
</html>
