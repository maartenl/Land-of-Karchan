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
        <title>Land of Karchan - <%=itsPlayerName%></title>
        <link rel="stylesheet" type="text/css" href="/css/karchangame.css" />
    </head>
<BODY>
<H1>
<IMG SRC="/images/gif/dragon.gif" alt="dragon">
Edit Character Sheet of <%=itsPlayerName%></H1>
<%!


/**
 * returns the different possibilities for a family member
 * be it friend, brother, sister, wife, etc in the apporpriate form.
 */
public String familyValues(Connection aCon)
throws SQLException, IOException
{
    ResultSet rst=null;
    PreparedStatement stmt=null;
    String result = "";
    stmt=aCon.prepareStatement("select * from familyvalues");
    rst=stmt.executeQuery();
    while (rst.next())
    {
        result += "<option selected value=\"" + rst.getString("id") + "\">" +
        rst.getString("description");
    }
rst.close();
stmt.close();
return result;
}
%>
        <%

if (itsPlayerName == null)
{
    throw new RuntimeException("User undefined.");
}
if (!request.isUserInRole("players"))
{
    throw new RuntimeException("User does not have role 'players'.");
}

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
stmt=con.prepareStatement("select * from mm_usertable where mm_usertable.name =	'" +
        itsPlayerName + "'"); //  and mm_usertable.lok = '" + itsPlayerSessionId + "'
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

stmt=con.prepareStatement("select *, replace(replace(replace(storyline,	'&','&amp;'),'<', '&lt;'), '>', '&gt;') as secondstoryline from characterinfo where name = '" + itsPlayerName + "'");
rst=stmt.executeQuery();
String homepageurl = "";
String imageurl = "";
String dateofbirth = "";
String cityofbirth = "";
String storyline = "";
String secondstoryline = "";
if (rst.next())
{
     // full charactersheet
    homepageurl = rst.getString("homepageurl");
    imageurl = rst.getString("imageurl");
    dateofbirth = rst.getString("dateofbirth");
    cityofbirth = rst.getString("cityofbirth");
    storyline= rst.getString("storyline");
    secondstoryline= rst.getString("secondstoryline");
}
rst.close();
stmt.close();

boolean isChanged = false;
if ((request.getParameter("homepageurl") != null) &&
        (!request.getParameter("homepageurl").equals(homepageurl)))
{
    isChanged = true;
    homepageurl = request.getParameter("homepageurl");
}
if ((request.getParameter("imageurl") != null) &&
        (!request.getParameter("imageurl").equals(homepageurl)))
{
    isChanged = true;
    imageurl = request.getParameter("imageurl");
}
if ((request.getParameter("dateofbirth") != null) &&
        (!request.getParameter("dateofbirth").equals(homepageurl)))
{
    isChanged = true;
    dateofbirth = request.getParameter("dateofbirth");
}
if ((request.getParameter("cityofbirth") != null) &&
        (!request.getParameter("cityofbirth").equals(homepageurl)))
{
    isChanged = true;
    cityofbirth = request.getParameter("cityofbirth");
}
if ((request.getParameter("storyline") != null) &&
        (!request.getParameter("storyline").equals(homepageurl)))
{
    isChanged = true;
    storyline = request.getParameter("storyline");
}

if (isChanged)
{
stmt=con.prepareStatement("replace into characterinfo values(?, ?, ?, ?, ?, ?)");
stmt.setString(1, itsPlayerName);
stmt.setString(2, imageurl);
stmt.setString(3, homepageurl);
stmt.setString(4, dateofbirth);
stmt.setString(5, cityofbirth);
stmt.setString(6, storyline);
stmt.executeQuery();
stmt.close();
}
if (request.getParameter("familyname") != null && !request.getParameter("familyname").trim().equals(""))
{
    stmt=con.prepareStatement("replace into family values(?, ?, ?)");
    stmt.setString(1, itsPlayerName);
    stmt.setString(2, request.getParameter("familyname"));
    stmt.setString(3, request.getParameter("family"));
    stmt.executeQuery();
    stmt.close();
}
%>
<FORM METHOD="POST" ACTION="editcharsheet.jsp">
<HR>
Homepage Url:<BR>
<INPUT TYPE="text" NAME="homepageurl" VALUE="<%=homepageurl%>" SIZE="50" MAXLENGTH="253"><P>
Image Url:<BR>
<INPUT TYPE="text" NAME="imageurl" VALUE="<%=imageurl%>" SIZE="50" MAXLENGTH="253"><P>
Date of Birth:
<INPUT TYPE="text" NAME="dateofbirth" VALUE="<%=dateofbirth%>" SIZE="20" MAXLENGTH="253"><P>
City of Birth:
<INPUT TYPE="text" NAME="cityofbirth" VALUE="<%=cityofbirth%>" SIZE="20" MAXLENGTH="253"><P>
Original storyline was...<BR><TABLE><TR><TD><TT><%=secondstoryline%></TT></TD></TR></TABLE>
Storyline:<BR>
<TEXTAREA NAME="storyline" ROWS="30" COLS="85"><%=storyline%></TEXTAREA><P>
(<I>Make sure you spell the name of the familymember correctly, otherwise
the corresponding charactersheet can not be obtained.</I>)<BR>
Add family relation:
<select id="family" name="family">
<option selected value="0">None
    <%=familyValues(con)%>
</select>of <INPUT TYPE="text" NAME="familyname" VALUE="" SIZE="50" MAXLENGTH="40"><P>
<INPUT TYPE="submit" VALUE="Submit">
<INPUT TYPE="reset" VALUE="Clear">
</FORM>
<%

con.close();
}
catch(Exception e)
{
out.println(e.getMessage());
e.printStackTrace(new PrintWriter(out));
%><%=e.getMessage()%>
<%
}
%>


<p>
<a HREF="/karchan/scripts/charactersheet.jsp?name=<%=itsPlayerName%>">
<img SRC="/images/gif/webpic/buttono.gif"
BORDER="0" alt="Back"></a><p>
    </body>
</html>
