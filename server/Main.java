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

/* imports */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;

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
			Database.connect();
			ServerSocket myServerSocket = null;
			System.out.println("Retrieving characters...");
			Characters.init();
			System.out.println("Creating Server Socket...");
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		try
		{
			Database.runMethod();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
