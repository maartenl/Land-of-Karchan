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
<%@ page language="java" import="java.io.BufferedReader"%>
<%@ page language="java" import="java.io.IOException"%>
<%@ page language="java" import="java.io.InputStreamReader"%>
<%@ page language="java" import="java.io.PrintWriter"%>
<%@ page language="java" import="java.io.StringWriter"%>
<%@ page language="java" import="java.net.Socket"%>
<%@ page language="java" import="java.net.UnknownHostException"%>
<%@ page language="java" import="java.io.InputStream"%>
<%@ page language="java" import="java.io.OutputStream"%>
<%@ page language="java" import="java.io.IOException"%>
<%!
        // authentication && authorization

        /* name of the current user logged in */
        private String itsPlayerName;

        /* password of the current user logged in, unsure if used */
        private String itsPlayerPassword = "";

        /* sessionid/cookiepassword of current user */
        private String itsPlayerSessionId;

        private StringBuffer contents = new StringBuffer("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">;");

        /**
         * A little wrapper to properly deal with end-of-stream and io exceptions.
         *
         * @param aReader
         *            the reader stream, should be opened already.
         * @return String read.
         * @throws MudException
         *             incase of problems of end-of-stream reached.
         */
        private String readLine(BufferedReader aReader,
                HttpServletRequest request,
                HttpServletResponse response) throws Exception
        {
                String read = aReader.readLine();
                if (read == null)
                {
                    request.setAttribute("exception", new Exception("unexpected end of connection detected."));
                    String redirectURL = "/game_error.jsp";
                    RequestDispatcher rd = getServletContext().getRequestDispatcher(redirectURL);
                    rd.forward(request, response);
                    return null;
                }
                return read;
                // this point is never reached. There is a return in the try statement.
        }
%>

<%
    itsPlayerName = request.getRemoteUser();
    itsPlayerSessionId = request.getSession(true).getId();
    Socket mySocket = null;
    try
    {
        mySocket = new Socket("localhost", 3340);
    }
    catch (Exception e)
    {
        request.setAttribute("exception", e);
        String redirectURL = "/game_error.jsp";
        RequestDispatcher rd = getServletContext().getRequestDispatcher(redirectURL);
        rd.forward(request, response);
        return;
    }
    PrintWriter myOutputStream = null;
    BufferedReader myInputStream = null;
    try
    {
            myOutputStream = new PrintWriter(mySocket.getOutputStream(), true);
            myInputStream = new BufferedReader(new InputStreamReader(mySocket
                            .getInputStream()));

    } catch (IOException e)
    {
        try
        {
            mySocket.close();
        }
        catch (IOException ioexception)
        {
            out.println("Error closing socket.");
        }
        request.setAttribute("exception", e);
        String redirectURL = "/game_error.jsp";
        RequestDispatcher rd = getServletContext().getRequestDispatcher(redirectURL);
        rd.forward(request, response);
        return;
    }
    try
    {
            String myMudVersion = readLine(myInputStream, request, response);
            String myMudAction = readLine(myInputStream, request, response);
            myOutputStream.println("mud");
            String myCrap = readLine(myInputStream, request, response); // Name:
            myOutputStream.println(itsPlayerName);
            myCrap = readLine(myInputStream, request, response); // Cookie:
            //myOutputStream.println("s4e.~79vba4w5owv45b9a27ba2v7nav297t;2SE%;2~&FGO* YBIJK"); // cookies are no longer an issue, let glassfish take care of it
            myOutputStream.println("crap"); // cookies are no longer an issue, let glassfish take care of it
            myCrap = readLine(myInputStream, request, response); // Frames:
            myOutputStream.println("1"); // we're going with full frame
            myCrap = readLine(myInputStream, request, response); // Command:
            myOutputStream.println(request.getParameter("command"));
            myOutputStream.println("\n.\n");

            contents = new StringBuffer();
            String readStuff = readLine(myInputStream, request, response);
            while ((readStuff != null) && !(".".equals(readStuff)))
            {
                    contents.append(readStuff);
                    readStuff = readLine(myInputStream, request, response);
            }
            myOutputStream.println("\nOk\nOk\n");
            myOutputStream.flush();
            myOutputStream.close();

            try
            {
                myInputStream.close();
            } catch (IOException e)
            {
                // oooh, should print something here or something
            }

            try
            {
                mySocket.close();
            } catch (IOException e2)
            {
                // ooh, another unable to close!
            }
    } catch (Exception e3)
    {
        request.setAttribute("exception", e3);
        String redirectURL = "/game_error.jsp";
        RequestDispatcher rd = getServletContext().getRequestDispatcher(redirectURL);
        rd.forward(request, response);
        return;
    }
    finally
    {
        try
        {
            mySocket.close();
        }
        catch (IOException e4)
        {
            out.println("Error closing socket.");
        }

    }

%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <head><title>Try me</title></head>
    <body><%= contents %>
    </body>
</html>
