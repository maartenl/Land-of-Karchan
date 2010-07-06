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
<%@ page language="java" import="java.text.SimpleDateFormat"%>
<%@ page language="java" import="javax.naming.Context"%>
<%@ page language="java" import="javax.sql.DataSource"%>
<%@ page language="java" import="java.sql.*"%>
<%@ page language="java" import="java.util.Enumeration"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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


/**
 *
 * Character Entity Code
 * <    &lt;
 * >    &gt;
 * &    &amp;
 * ’    &#039;
 * "    &#034;
 */
public static String transform(String s)
{
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\'", "&#039;").replace("\"", "&#034;");
}

%>
<%
  itsPlayerName = request.getRemoteUser();
  itsPlayerSessionId = request.getSession(true).getId();
%>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - <%=itsPlayerName%></title>
        <link rel="stylesheet" type="text/css" href="/css/karchangame.css" />
        <!-- ** CSS ** -->
        <!-- base library -->
        <link rel="stylesheet" type="text/css" href="/ext-3.2.1/resources/css/ext-all.css" />
                <!-- overrides to base library -->


        <!-- ** Javascript ** -->
        <!-- ExtJS library: base/adapter -->
         <script type="text/javascript" src="/ext-3.2.1/adapter/ext/ext-base.js"></script>
        <!-- ExtJS library: all widgets -->
         <script type="text/javascript" src="/ext-3.2.1/ext-all-debug.js"></script>

        <!-- overrides to base library -->

        <!-- extensions -->

        <!-- page specific -->

        <script type="text/javascript">
         // Path to the blank image should point to a valid location on your server
        Ext.BLANK_IMAGE_URL = '/ext-3.2.1/resources/images/default/s.gif';

        Ext.onReady(function(){

        var myData = [
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

stmt=con.prepareStatement("select * from mm_mailtable where toname = '" + itsPlayerName + "'");
rst=stmt.executeQuery();
boolean first = true;
SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
while (rst.next())
{
    if (!first)
    {
        %>,<%
    }
    String name = rst.getString("name");
     // write mail
    %>['<%= (rst.getInt("haveread") != 0 ? "X" : "") %>',
    '<%= rst.getInt("newmail") != 0 ? "X" : "" %>','<%= transform(rst.getString("name")) %>','<%= transform(rst.getString("header")) %>','<%= newFormat.format(rst.getTimestamp("whensent")) %>']
<%  first = false;

}
rst.close();
stmt.close();


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
   ];

    // create the data store
    var ds = new Ext.data.ArrayStore({
        fields: [
           {name: 'haveread'},
           {name: 'newmail'},
           {name: 'from'},
           {name: 'subject'},
           {name: 'whensent', type: 'date', dateFormat: 'Y-m-d H:i:s'}
        ]
    });
          // manually load local data
    ds.loadData(myData);
    // create the colum Manager
    var colModel = new Ext.grid.ColumnModel([
            {header: 'Read', width: 35, sortable: true, dataIndex: 'haveread'},
            {header: 'New', width: 35, sortable: true, dataIndex: 'newmail'},
            {header: 'From', sortable: true, dataIndex: 'from'},
            {header: 'Subject', width: 470,
            sortable: true, dataIndex: 'subject'},
            {header: 'Sent', width: 95,
             sortable: true,
                renderer: Ext.util.Format.dateRenderer('Y-m-d H:i:s'), dataIndex: 'whensent'}
    ]);

    // create the Grid
    var grid = new Ext.grid.GridPanel({
        store: ds,
        colModel: colModel,
        height: 300,
        width: 800,
        title: 'My MudMail - <%=itsPlayerName%>'
    });

    // render the grid to the specified div in the page
    grid.render('mailgrid');

    Ext.QuickTips.init();

    // turn on validation errors beside the field globally
    Ext.form.Field.prototype.msgTarget = 'side';

    var bd = Ext.getBody();

        }); //end onReady
        </script>

    </head>
<BODY>
<h1>
<IMG SRC="/images/gif/dragon.gif" alt="dragon">
MudMail - <%=itsPlayerName%></h1>
<div id="mailgrid"></div>
<a href="mudnewmail.jsp">Send new mail</a>.
<p>
<a HREF="/karchan/player/">
<img SRC="/images/gif/webpic/buttono.gif"
BORDER="0" alt="Back"></a><p>
    </body>
</html>
