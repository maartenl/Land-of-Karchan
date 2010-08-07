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

<%

Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;

try
{
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();

stmt=con.prepareStatement("select mm_usertable.name, title, sex, concat(age, if(length = 'none', '', concat(', ',length)),if(width = 'none', '', concat(', ',width)),        if(complexion = 'none', '', concat(', ',complexion)),        if(eyes = 'none', '', concat(', ',eyes)),        if(face = 'none', '', concat(', ',face)),        if(hair = 'none', '', concat(', ',hair)),        if(beard = 'none', '', concat(', ',beard)),        if(arm = 'none', '', concat(', ',arm)),        if(leg = 'none', '', concat(', ',leg)),        ' ', sex, ' ', race) as description,         concat('<IMG SRC=\"',imageurl,'\">') as imageurl,         guild,   homepageurl,         dateofbirth, cityofbirth, mm_usertable.lastlogin, storyline         from mm_usertable, characterinfo         where mm_usertable.name = '" + request.getParameter("name") + "' and mm_usertable.name = characterinfo.name");
rst=stmt.executeQuery();
if (!rst.next())
{
    throw new Exception("Character does not exist.");
}
%>
        <title>Land of Karchan - <%=rst.getString("name")%></title>
    </head>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif" alt=""dragon">
Character Sheet of <%=rst.getString("name")%></H1>
<HR>
<a href="/karchan/player/scripts/editcharsheet.jsp">Edit your Charactersheet</a><p/>
<B>Name:</B><%=rst.getString("name")%><BR>
<B>Title:</B><%=rst.getString("title")%><BR>
<B>Sex:</B><%=rst.getString("sex")%><BR>
<B>Description:</B><%=rst.getString("description")%><BR>
<%
String imageurl = rst.getString("imageurl");
if ((imageurl != null) &&
        (imageurl.trim().equals("")))
{
    out.println("<B>Image:</B> " + imageurl + "<BR>");
}
%><br/>
<B>Member of:</B><%=rst.getString("guild")%><BR>
<B>Homepage:</B><%
String homepageurl = rst.getString("homepageurl");
	if ((homepageurl != null) &&
	   (!homepageurl.trim().equals("")))
	{
		out.println("<A HREF=\" " + homepageurl + "\">" +
                        homepageurl + "</a>");
	}
        %>
        <br/>
<B>Born: </B>Yes<BR>
<B>Born When:</B> <%=rst.getString("dateofbirth")%><BR>
<B>Born Where:</B> <%=rst.getString("cityofbirth")%><BR>


<B>Family Relations:</B><BR><UL>

<%
String lastlogin = rst.getString("lastlogin");
String storyline = rst.getString("storyline");
rst.close();
stmt.close();

// family values
stmt=con.prepareStatement("select familyvalues.description, toname,		characterinfo.name 		from family, familyvalues, characterinfo 		where family.name =		'" + request.getParameter("name") + "' and 		family.description = familyvalues.id and		characterinfo.name = family.toname");
rst=stmt.executeQuery();
while (rst.next())
{
    if (rst.getString("name") == null)
    {
	out.println("<LI>" + rst.getString("description") + " of " + rst.getString("toname") + "<BR>");
    }
    else
    {
	out.println("<LI>" + rst.getString("description") + " of <a href=\"/karchan/scripts/charactersheet.jsp?name=" + rst.getString("toname") + "\">" + rst.getString("toname") + "</A><BR>");
    }
    out.println("</ul>");
}

// finished with the family values
%>
<b>Last logged on:</b> <%=lastlogin%><br/>
<b>Storyline:</b> <%=storyline%><br/>

<%
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

<p/>
<a HREF="/karchan/scripts/charactersheets.jsp">
<img SRC="/images/gif/webpic/buttono.gif"
BORDER="0" alt="Back"></a><p/>
    </body>
</html>
