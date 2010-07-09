<%--
    Document   : index
    Created on : Dec 23, 2009, 6:50:03 AM
    Author     : maartenl
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBliC "-//W3C//Dtd XHTML 1.0 Strict//EN" "http://www.w3.org/tr/xhtml1/Dtd/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%!

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
    // authentication && authorization

    /* name of the current user logged in */
    String itsPlayerName;

    /* password of the current user logged in, unsure if used */
    String itsPlayerPassword = "";

    /* sessionid/cookiepassword of current user */
    String itsPlayerSessionId;

  itsPlayerName = request.getRemoteUser();
  itsPlayerSessionId = request.getSession(true).getId();
%>
    <head>
        <title>Welcome, <%=itsPlayerName%>!</title>
        <%@include file="includes/head.jsp" %>
    </head>

    <body>
        <div class="containerMain">
            <div class="header"></div>
            <div class="columns">
                <div class="column1">
                    <ul class="menu1">
                        <li><a href="enter.jsp">Logon</a></li>
                        <li><a href="game_logon.jsp">Enter</a></li>
                        <li><a href="game.jsp">Continue</a></li>
                        <li><a href="scripts/editcharsheet.jsp">Edit</a></li>
                        <li><a href="scripts/mudmail.jsp">Mail</a></li>
                        <li><a href="/karchan/index.jsp?logout=true">Logout</a></li>
                        <li><a href="/karchan/index.jsp">Back</a></li>
                        </li>
                    </ul>
                </div>
                <div class="column2" id="Column2">
                    <h1 class="heading1">Welcome, <%=itsPlayerName%>!</h1>
                    <p>The menu on your right will take you to the following destinations:</p>

                    <dl>
                        <dt><b>Logon</b></dt>
                    <dd>Use this if you wish to switch users.</dd>
                    <dt><b>Enter</b></dt>
                    <dd>Enter and start playing the game!</dd>
                    <dt><b>Continue</b></dt>
                    <dd>Continue playing the game where you left off.</dd>
                    <dt><b>Edit</b></dt>
                    <dd>Edit your personal Character Sheet, change your title, description or add family members.</dd>
                    <dt><b>Mail</b></dt>
                    <dd>Check your mud mail, read your mud mail, and send new mails to other Karchanians.</dd>
                    </dl>
                        <p>Karn (Ruler of Karchan, Keeper of the Key to the Room of Lost Souls)</p>

                </div>
            </div>
            <div class="footer"></div>
        </div>

        <%@include file="includes/footer.jsp" %>

    </body>
</html>