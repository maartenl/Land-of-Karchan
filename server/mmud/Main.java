/*-------------------------------------------------------------------------
cvsinfo: $Header$
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
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;
import java.util.logging.*;

import mmud.database.*;
import mmud.characters.*;

public class Main
{
	public Main()
	{
		
	}

	public static void main(String[] args)
	{
		try
		{
			Constants.init();
			// Log a simple INFO message.
			Constants.logger.info("Starting server");
			Database.connect();
			ServerSocket myServerSocket = null;
			Constants.logger.info("Retrieving characters...");
			Persons.init();
			Constants.logger.info("Creating Server Socket...");
			myServerSocket = new ServerSocket(3339);
			for (int i = 1; i < 100; i++)
			{
				Socket mySocket = myServerSocket.accept();
				MudSocket myMudSocket = new MudSocket(mySocket);
				myMudSocket.start();
			}
	//		DocumentBuilder myDocumentBuilder = new DocumentBuilder();
			myServerSocket.close();
			Database.disconnect();
			// Log a simple INFO message.
			Constants.logger.info("Stopping server");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Constants.logger.log(Level.WARNING, "exception {0}", e);
			return;
		}
	}
}
