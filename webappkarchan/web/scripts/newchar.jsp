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
<!DOCTYPE html PUBLIC "-//W3C//DTD html 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - New Character</title>
    </head>
<body BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<%!
%>
<%

String name = request.getParameter("name");
if (name == null || !name.matches("([A-Za-z]){3,}"))
{
    %><h1><img src="/images/gif/dragon.gif" alt="Dragon">
Error Creating Character - Name invalid</h1>
Unable to create character, because the name is invalid.
The following rules govern if your name is accepted or not:
<UL><LI>at least three letters in length
<LI>may contain only letters
</UL>
Regards, Karn.
<p/><a href="/karchan/newchar.jsp">
<img src="/images/gif/webpic/buttono.gif" border="0" alt="Back"></a><p/>
</body></html>
<%
    return;
}
String password = request.getParameter("password");
if (password == null || password.length() < 5)
{
    %><h1>
<img src="/images/gif/dragon.gif"  alt="Dragon">
Error Creating Character - Password length less than 5</h1>
Unable to create character, because the length of the password
was less than the mandatory 5 characters.
<p/>Regards, Karn.
<p/><a href="/karchan/newchar.jsp">
<img src="/images/gif/webpic/buttono.gif" border="0" alt="Back"></a><p/>
</body></html>
<%
    return;
}
String password2 = request.getParameter("password2");
if (password2 == null || !password2.equals(password))
{
    %><h1>
<img src="/images/gif/dragon.gif" alt="Dragon">
Error Creating Character - Password does not match</h1>
Unable to create character, because the password does not match.
On the form it is required that the password is typed twice for
verification. Apparently the first password was different
from the second password.<p/>
Regards, Karn.
<p/><a href="/karchan/newchar.jsp">
<img src="/images/gif/webpic/buttono.gif" border="0" alt="Back"></a><p/>
</body></html>
<%
    return;
}

Connection con=null;
ResultSet rst=null;
PreparedStatement stmt=null;

boolean isBanned = false;
boolean isNotBanned = false;
boolean alreadyExists = false;
boolean isSuccessfull = false;

/**
 * First check's firsst:
 * - does the user already exists?
 * The following sequence of banning is in effect
 * - name should not be silly ((exists in sillynamestable))
 * - address should not be banned ((exists in mm_bantable))
 * - name should not be banned ((exists in the mm_bannednamestable))
 * Addendum:
 * - if is banned, but name is explicitly unbanned, continue as normal.
 * ((exists in unbantable))
 */
try
{
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
con = ds.getConnection();

stmt=con.prepareStatement("select name from mm_usertable where ? = name");
stmt.setString(1, name);
rst=stmt.executeQuery();
if (rst.next())
{
    alreadyExists = true;
    isBanned = true;
}
rst.close();
stmt.close();

stmt=con.prepareStatement("select count(name) as count from mm_sillynamestable where ? like name");
stmt.setString(1, name);
rst=stmt.executeQuery();
if (rst.next())
{
    isBanned = true;
}
rst.close();
stmt.close();

if (!isBanned)
{
    stmt=con.prepareStatement("select count(address) as count from mm_bantable " +
            "where ? like address or " +
            " ? like address");
    stmt.setString(1, request.getRemoteAddr());
    stmt.setString(2, request.getRemoteHost());
    rst=stmt.executeQuery();
    if (rst.next())
    {
        isBanned = true;
    }
    rst.close();
    stmt.close();
}

if (!isBanned)
{
    stmt=con.prepareStatement("select count(*) as count from mm_bannednamestable where name = ?");
    stmt.setString(1, name);
    rst=stmt.executeQuery();
    if (rst.next())
    {
        isBanned = true;
    }
    rst.close();
    stmt.close();
}

if (isBanned)
{
    stmt=con.prepareStatement("select count(name) as count from mm_unbantable where name = ?");
    stmt.setString(1, name);
    rst=stmt.executeQuery();
    if (rst.next())
    {
        isBanned = false;
    }
    rst.close();
    stmt.close();
}

if (!isBanned && !alreadyExists)
{
    // create the actual new user in the database
    stmt=con.prepareStatement("insert into mm_usertable " +
                "(name, address, password, title, realname, email, race, sex, age," +
                " length, width, complexion, eyes, face, hair, beard, arm, leg, lok," +
                " active, lastlogin, birth) " +
                "values(?, ?, sha1(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, 0, now(), now())");
    stmt.setString(1, name);
    stmt.setString(2, request.getRemoteAddr());
    stmt.setString(3, password);
    stmt.setString(4, request.getParameter("title"));
    stmt.setString(5, request.getParameter("realname"));
    stmt.setString(6, request.getParameter("email"));
    stmt.setString(7, request.getParameter("race"));
    stmt.setString(8, request.getParameter("sex"));
    stmt.setString(9, request.getParameter("age"));
    stmt.setString(10, request.getParameter("length"));
    stmt.setString(11, request.getParameter("width"));
    stmt.setString(12, request.getParameter("complexion"));
    stmt.setString(13, request.getParameter("eyes"));
    stmt.setString(14, request.getParameter("face"));
    stmt.setString(15, request.getParameter("hair"));
    stmt.setString(16, request.getParameter("beard"));
    stmt.setString(17, request.getParameter("arms"));
    stmt.setString(18, request.getParameter("legs"));
    isSuccessfull = (stmt.executeUpdate() == 1);
    stmt.close();
}

con.close();

if (alreadyExists)
{
    %><h1>
<img src="/images/gif/dragon.gif" alt="Dragon">
Error Creating Character - Character Exists</h1>
Unable to create character, because someone already created a character
with that particular name. Please choose a different name and try again.
<p/>Regards, Karn.
<p/><a href="/karchan/newchar.jsp">
<img src="/images/gif/webpic/buttono.gif"
border="0" alt="Back"></a><p/>
</body></html>
<%
    return;
}

if (isBanned)
{
    %><h1>
<img src="/images/gif/dragon.gif" alt="Dragon">
Error Creating Character - You are banned</h1>
You, or someone in your domain,  has angered the gods by behaving badly on
this mud. Your ip domain/charactername is therefore banned from the game.<p/>

If you have not misbehaved or even have never before played the
game, and wish
to play with your current IP address, email to
<A href="mailto:deputy@karchan.org">deputy@karchan.org</A> and ask
them to make an exception in your case. Do <I>not</I> forget to provide your
Character name.<p/>
You'll be okay as long as you follow the rules.
<p/>Regards, Karn.
<p/><a href="/karchan/newchar.jsp">
<img src="/images/gif/webpic/buttono.gif"
border="0" alt="Back"></a><p/>
</body></html>
<%
    return;
}

if (!isSuccessfull)
{
    %><h1>
<img src="/images/gif/dragon.gif" alt="Dragon">
Error Creating Character - Unable to create character</h1>
For some reason, I was unable to add you character to the database.<p/>

You should mention this via email to
<A href="mailto:deputy@karchan.org">deputy@karchan.org</A>.
Do <I>not</I> forget to provide your
Character name.
<p/>Regards, Karn.
<p/><a href="/karchan/newchar.jsp">
<img src="/images/gif/webpic/buttono.gif"
border="0" alt="Back"></a><p/>
</body></html>
<%
    return;
}

}
catch(Exception e)
{
    System.out.println(e.getMessage());
    %><%=e.getMessage()%>
    <%
    return;
}
%>

The new character has been created. Click on the link <I>Back</I> below
to return to the logon screen. In the logon screen fill out the name
and the password of the newly created character.
<p/>
<a href="/karchan/index.jsp">
<img src="/images/gif/webpic/buttono.gif"
border="0" alt="Back"></a><p/>

    </body>
</html>
