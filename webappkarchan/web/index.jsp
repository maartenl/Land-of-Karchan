<%-- 
    Document   : index
    Created on : Dec 23, 2009, 6:50:03 AM
    Author     : maartenl
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBliC "-//W3C//Dtd XHTML 1.0 Strict//EN" "http://www.w3.org/tr/xhtml1/Dtd/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Land of Karchan</title>
        <%@include file="includes/head.jsp" %>
    </head>

    <body>
        <div class="containerMain">
            <div class="header"></div>
            <div class="columns">
                <div class="column1">
                    <ul class="menu1">
                        <li><a href="/karchan/intro.html">Introduction</a></li>
                        <li><a href="/karchan/chronicles/chronicles2.html">Chronicles</a></li>
                        <li><a href="/karchan/player/">Logon</a></li>
                        <li><a href="/karchan/scripts/wholist.jsp">Active Users</a></li>
                        <li><a href="/karchan/help/thelaw.html">The Law</a></li>
                        <li><a href="/karchan/help/helpindex.html">Help</a></li>
                        <li><a href="/karchan/source.html">Source</a></li>
                        <li><a href="/karchan/scripts/links.jsp">Links</a>
                        </li>
                    </ul>
                </div>
                <div class="column2" id="Column2">
                    <h1 class="heading1">Welcome</h1>
                    <p>This is the MUD (multi-user dungeon) called Land of Karchan, and it is one of the first real MUD's on the World Wide Web. If you are new here, the game is very simple. Just type below, a fictive name (and make it a little original) and a password (to prevent people from using your character with devastating problems). If you already have a character here, you will automatically get where you were when you left this great game. Look at the following links:</p>

                    <ul>
                        <li>Loggging into Karchan</li>
                        <li>Homepage of Karchan</li>
                        <li>Manual and Explanation</li>
                        <li>Or Mail Me (Better response) </li>
                    </ul>
                        <p>If you have entered your name and password, please hit the Submit button. (Remember that you are not supposed to type multiple names, just one name will suffice and more will give grave problems.) Or hit Clear for a new try...</p>

                        <p>If you wish to create a new character, click here.</p>

                        <p>Karn (Ruler of Karchan, Keeper of the Key to the Room of Lost Souls)</p>

                </div>
            </div>
            <div class="footer"></div>
        </div>

        <%@include file="includes/footer.jsp" %>

    </body>
</html>
