/*-------------------------------------------------------------------------
svninfo: $Id$
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
-------------------------------------------------------------------------*/

package mmud;

/* imports */
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.logging.Level;

import mmud.characters.Persons;
import mmud.database.Database;
import mmud.database.MudDatabaseException;

/**
 * Main class, used for starting the main method.
 */
public class Main
{


	/**
	 * Connects to the database and does error handling.
	 */
	public static void dbConnect()
	{
		try
		{
			Database.connect();
		}
		catch (SQLException e)
		{
			System.out.println("An sql error occurred while attempting to contact the database.");
			System.out.println("The message was : " + e.getMessage());
			Constants.logger.throwing("mmud.database.Database", "connect()", e);
			System.out.println("Exiting mud...");
			System.exit(1);
		}
		catch (InstantiationException e2)
		{
			System.out.println("An instantiation error occurred while attempting to contact the database.");
			System.out.println("The message was : " + e2.getMessage());
			Constants.logger.throwing("mmud.database.Database", "connect()", e2);
			System.out.println("Exiting mud...");
			System.exit(2);
		}
		catch (ClassNotFoundException e3)
		{
			System.out.println("Java driver for the mysql database is not found.");
			System.out.println("The message was : " + e3.getMessage());
			Constants.logger.throwing("mmud.database.Database", "connect()", e3);
			System.out.println("Exiting mud...");
			System.exit(3);
		}
		catch (IllegalAccessException e4)
		{
			System.out.println("An illegal access to a class was attempted while attempting to contact the database.");
			System.out.println("The message was : " + e4.getMessage());
			Constants.logger.throwing("mmud.database.Database", "connect()", e4);
			System.out.println("Exiting mud...");
			System.exit(4);
		}
	}
	
	/**
	 * the default method that is called when the server is started.
	 * @param args The arguments provided when attempting to start the
	 * program.
	 */
	public static void main(String[] args)
	{
		// Log a simple INFO message.
		Constants.init();
		dbConnect();
		Constants.logger.info("Starting server");
		Database.writeLog("root","Starting server");
		ServerSocket myServerSocket = null;
		Constants.logger.info("Retrieving characters...");
		try
		{
			Persons.init();
		}
		catch (MudException e)
		{
			System.out.println("An mud database error occurred while attempting to initialise users.");
			System.out.println("The message was : " + e.getMessage());
			Constants.logger.throwing("mmud.characters.Persons", "init()", e);
			System.out.println("Exiting mud...");
			System.exit(1);
		}
		Constants.logger.info("Starting tickerthread...");
		TickerThread myTicker = new TickerThread();
		Constants.getThreadPool().execute(myTicker);
		Constants.logger.info("Loading user commands...");
		try
		{
			Constants.setUserCommands(Database.getUserCommands());
		}
		catch (MudDatabaseException e)
		{
			System.out.println("An mud database error occurred while attempting to retrieve user commands.");
			System.out.println("The message was : " + e.getMessage());
			Constants.logger.throwing("mmud.database.Database", "getUserCommand()", e);
			System.out.println("Exiting mud...");
			System.exit(1);
		}
		Constants.logger.info("Creating Server Socket...");
		InetAddress myAddress = null;
		try
		{
			myAddress = 
				("all".equals(Constants.mudhost) ? null :
				InetAddress.getByName(Constants.mudhost));
		}
		catch (UnknownHostException e)
		{
			System.out.println("The host " + Constants.mudhost + " is unknown.");
			System.out.println("The message was : " + e.getMessage());
			Constants.logger.throwing("mmud.database.Database", "connect()", e);
			System.out.println("Exiting mud...");
			System.exit(1);
		}
		try
		{
			myServerSocket = new ServerSocket(
				Constants.mudportnumber,
				0, // default
				myAddress
				);
		}
		catch (IOException e)
		{
			System.out.println("An Input/Output error occurred while attempting to create a server socket.");
			System.out.println("The message was : " + e.getMessage());
			Constants.logger.throwing("mmud.database.Database", "connect()", e);
			System.out.println("Exiting mud...");
			System.exit(1);
		}
		try
		{
			while (!Constants.shutdown)
			{
				Socket mySocket = myServerSocket.accept();
				MudSocket myMudSocket = new MudSocket(mySocket);
				Constants.getThreadPool().execute(myMudSocket);
			}
			Constants.logger.log(Level.INFO, "Shutting down Threadpool");
			Constants.getThreadPool().shutdown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Constants.logger.log(Level.WARNING, "exception {0}", e);
			return;
		}
		try
		{
			myServerSocket.close();
		}
		catch (IOException e)
		{
			System.out.println("An Input/output error occurred while attempting to close the server socket.");
			System.out.println("The message was : " + e.getMessage());
			Constants.logger.throwing("mmud.database.Database", "connect()", e);
			System.out.println("Exiting mud...");
			System.exit(1);
		}
		Database.writeLog("root","Stopping server");
		try
		{
			Database.disconnect();
		}
		catch (SQLException e)
		{
			System.out.println("An sql error occurred while attempting to contact the database.");
			System.out.println("The message was : " + e.getMessage());
			Constants.logger.throwing("mmud.database.Database", "connect()", e);
			System.out.println("Exiting mud...");
			System.exit(1);
		}
		// Log a simple INFO message.
		Constants.logger.info("Stopping server");
	}
}
