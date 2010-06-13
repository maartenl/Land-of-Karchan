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

--%><%@ page language="java" import="javax.naming.InitialContext"%>
<%@ page language="java" import="java.io.PrintWriter"%>
<%@ page language="java" import="java.io.IOException"%>
<%@ page language="java" import="javax.naming.Context"%>
<%@ page language="java" import="javax.sql.DataSource"%>
<%@ page language="java" import="java.sql.*"%>
<%@ page language="java" import="java.util.Enumeration"%>
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
<%@ page language="java" import="java.util.logging.*"%>
<%!
        // authentication && authorization

        /* name of the current user logged in */
        private String itsPlayerName;

        private Logger itsLog = Logger.getLogger("mmud");

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
    itsLog.entering(this.getClass().getCanonicalName(), "begin");
    String command = request.getParameter("command");
    if (command == null)
    {
        command = "l";
    }
    String itsAction = request.getParameter("action");
    if (!"logon".equals(itsAction))
    {
        itsAction = "mud";
    }
    String itsFrames = request.getParameter("frames");
    if (itsFrames == null)
    {
        itsFrames = "1";
    }
    itsLog.fine("begin itsAction=" + itsAction +
            ",name=" + request.getParameter("name") + "/" + itsPlayerName +
            ",password=" + request.getParameter("password") +
            ",cookie=" + session.getAttribute("cookie") +
            ",command=" + request.getParameter("command")+ "/" + command);
    if (session.getAttribute("cookie") == null)
    {
        itsLog.fine("cookie not in cookiejar.");
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
            if (rst.next())
            {
                 // full charactersheet
                session.setAttribute("cookie", rst.getString("lok"));
                itsLog.fine("cookie set to " + session.getAttribute("cookie"));
            }
            else
            {
                // error getting the info, user not found?
                throw new RuntimeException("Cannot find " + itsPlayerName + " in the database!");
            }
            rst.close();
            stmt.close();
            // end authorization check
            // ===============================================================================

            con.close();
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getCanonicalName(), "error getting cookie", e);
        }
    }
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
            myOutputStream.println(itsAction);
            String myCrap = readLine(myInputStream, request, response); // Name:
            if (itsAction.equals("logon"))
            {
                itsLog.fine("branch: logon");
                myOutputStream.println(request.getParameter("name"));
                myCrap = readLine(myInputStream, request, response); // Password:
                myOutputStream.println(request.getParameter("password")); // no password required
                myCrap = readLine(myInputStream, request, response); // Address:
                myOutputStream.println(request.getRemoteAddr());
                // contents.append(request.getRemoteAddr());
                myCrap = readLine(myInputStream, request, response); // Cookie:
                myOutputStream.println(""); // cookies are no longer an issue, let glassfish take care of it
                // myOutputStream.println(session.getAttribute("cookie")); // cookies are no longer an issue, let glassfish take care of it
                myCrap = readLine(myInputStream, request, response); // Frames:
                // contents.append("<br/>Received:" + myCrap);
                myOutputStream.println(itsFrames);
                // contents.append("debug2");
            }
            else
            {
                itsLog.fine("branch: mmud");
                myOutputStream.println(itsPlayerName);
                myCrap = readLine(myInputStream, request, response); // Cookie:
                //myOutputStream.println("s4e.~79vba4w5owv45b9a27ba2v7nav297t;2SE%;2~&FGO* YBIJK"); // cookies are no longer an issue, let glassfish take care of it
                myOutputStream.println(session.getAttribute("cookie")); // cookies are no longer an issue, let glassfish take care of it
                myCrap = readLine(myInputStream, request, response); // Frames:
                myOutputStream.println(itsFrames); 
                myCrap = readLine(myInputStream, request, response); // Command:
                myOutputStream.println(request.getParameter("command"));
                myOutputStream.println(".\n");
            }

            contents = new StringBuffer();
            String readStuff = readLine(myInputStream, request, response);
            while ((readStuff != null) && !(".".equals(readStuff)))
            {
                    if (readStuff.startsWith("sessionpassword="))
                    {
                        session.setAttribute("cookie", readStuff.substring("sessionpassword=".length()));
                    }
                    else
                    {
                        contents.append(readStuff);
                        contents.append("\r\n");
                    }
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
    if ("quit".equalsIgnoreCase(request.getParameter("command")))
    {
        session.setAttribute("cookie", null);
        session.invalidate();
    }
    itsLog.exiting(this.getClass().getCanonicalName(), "end");

    String parsed_contents = contents.toString().replace("/scripts/mud.php", "game.jsp").replace("index.html", "index.jsp");
    parsed_contents = parsed_contents.replace("/scripts/mudleftframe.php", "leftframe.jsp");
    parsed_contents = parsed_contents.replace("/scripts/mudlogonframe.php", "logonframe.jsp");
%>
<%= parsed_contents %>
