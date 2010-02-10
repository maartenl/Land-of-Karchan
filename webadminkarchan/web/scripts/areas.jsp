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
<BODY BGCOLOR=#FFFFFF>

<H1><IMG SRC="/images/gif/dragon.gif" alt="dragon">Areas</H1>
<%

if (itsPlayerName == null)
{
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("deputies"))
{
    throw new RuntimeException("User does not have role 'deputies'.");
}
/*
showing the different areas and what rooms belong to which area.

the following constraints need to be checked before any kind of update
is to take place:

changing area:
- the area must exist
- is the administrator the owner of the area
*/

Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;

try
{
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();


// ===============================================================================
// begin authorization check
stmt=con.prepareStatement("select * from mm_admin where name =	'" +
        itsPlayerName + "' and validuntil >= now()"); //  and mm_usertable.lok = '" + itsPlayerSessionId + "'
rst=stmt.executeQuery();
if (!rst.next())
{
    // error getting the info, user not found?
    throw new RuntimeException("Cannot find " + itsPlayerName + " in the database!");
}
// end authorization check
// ===============================================================================
rst.close();
stmt.close();

String area = request.getParameter("area");
String description = request.getParameter("description");
String shortdesc = request.getParameter("shortdesc");

if (area == null || description==null || shortdesc == null)
{
    // show list of areas
    if (area != null)
    {
        stmt=con.prepareStatement("select area, description, shortdesc, owner, 	date_format(creation, \"%Y-%m-%d %T\") as creation2	from mm_area where area = ?");
        stmt.setString(1, area);
    }
    else
    {
        stmt=con.prepareStatement("select area, description, shortdesc, owner, 	date_format(creation, \"%Y-%m-%d %T\") as creation2	from mm_area order by area");
    }
    rst=stmt.executeQuery();
    while (rst.next())
    {
            boolean accessGranted = itsPlayerName.equals(rst.getString("owner")) ||
                    rst.getString("owner") == null ||
                    rst.getString("owner").trim().equals("");
%>
<B>Area:</b> <%=rst.getString("area")%><BR>
<B>Short Description:</b> <%=rst.getString("shortdesc")%><BR>
<B>Long Description:</b> <%=rst.getString("description")%><BR>
<B>owner:</b> <%=rst.getString("owner")%><BR>
<B>creation:</B> <%=rst.getString("creation2")%><BR>
                <%
            if (rst.getString("area").equals(area) && accessGranted)
            {
%>
<FORM METHOD="GET" ACTION="areas.jsp">
<table>
<TR><TD><b>Area:</b></TD><TD> <%=rst.getString("area")%>?></TD></TR>
<INPUT TYPE="hidden" NAME="area" VALUE="<%=rst.getString("area")%>">
<TR><TD><b>Short Description:</b></TD><TD><INPUT TYPE="text" NAME="shortdesc" VALUE="<%=rst.getString("shortdesc")%>" SIZE="40" MAXLENGTH="255"></TD></TR>
<TR><TD><b>Long Description:</b></TD><TD><TEXTAREA NAME="description" ROWS="14" COLS="85"><%=rst.getString("description")%></TEXTAREA></TD></TR>
<TR><TD><b>owner:</b></TD><TD><%=rst.getString("owner")%></TD></TR>
<TR><TD><b>creation:</b></TD><TD><%=rst.getString("creation2")%></TD></TR>
</table>
<INPUT TYPE="submit" VALUE="Change Area">
</b>
</FORM>
<FORM METHOD="GET" ACTION="remove_ownership.jsp">
<INPUT TYPE="hidden" NAME="table" VALUE="area">
<INPUT TYPE="hidden" NAME="id" VALUE="<%=rst.getString("area")%>">
<INPUT TYPE="submit" VALUE="Remove Ownership">
</FORM>
<BR>

<%
            }
    }
    rst.close();
    stmt.close();
}
else
{
// change the area
stmt=con.prepareStatement("update mm_area set description = ?, shortdesc = ?, owner = ? where (owner is null or owner = \"\" or owner = ?) and area = ?");
stmt.setString(1, description);
stmt.setString(2, shortdesc);
stmt.setString(3, itsPlayerName);
stmt.setString(4, itsPlayerName);
stmt.setString(5, area);
stmt.executeQuery();
stmt.close();
con.close();
}
}
catch(Exception e)
{
out.println(e.getMessage());
e.printStackTrace(new PrintWriter(out));
%><%=e.getMessage()%>
<%
}
%>

</body>
</html>
