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

import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.File;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.MalformedPatternException;
import java.util.logging.Logger;

import mmud.characters.*;
import mmud.database.*;
import mmud.commands.*;

public class MudSocket extends Thread
{
	private Socket theSocket = null;
	private boolean theSuccess = true;

	public String isOffline()
		throws IOException, MudException
	{
		if (Constants.offline)
		{
			File myFile = new File(Constants.mudofflinefile);
			if (!myFile.exists())
			{
				throw new MudException("mudofflinefile '" + Constants.mudofflinefile +"' not found.");
			}
			if (!myFile.canRead())
			{
				throw new MudException("mudofflinefile '" + Constants.mudofflinefile + "' unreadable.");
			}
			return Constants.readFile(Constants.mudofflinefile);
		}
		return null;
	}

	public boolean isSuccessfull()
	{
		Logger.getLogger("mmud").finer("");
		return theSuccess;
	}

	public MudSocket(Socket aSocket)
	{
		Logger.getLogger("mmud").finer("");
		theSocket = aSocket;
	}

	public void run()
	{
		Logger.getLogger("mmud").finer("");
		PrintWriter myOutputStream = null;  
		BufferedReader myInputStream = null;
		try
		{
			myOutputStream = new PrintWriter(theSocket.getOutputStream(), true);
			myInputStream = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
			
		} catch (UnknownHostException e) 
		{
			Logger.getLogger("mmud").warning("Don't know about host.");
			theSuccess = false;
			return;
		} catch (IOException e) {
			Logger.getLogger("mmud").warning("Couldn't get I/O for "
			+ "the connection.");
			theSuccess = false;
			return;
		}

		try
		{
			StringBuffer userInput = new StringBuffer();

			myOutputStream.println(Constants.IDENTITY);
			try
			{
				myOutputStream.println("Action (logon, mud, newchar):");
				String myAction = myInputStream.readLine();
				Logger.getLogger("mmud").finest("received from socket: [" + myAction + "]");
				if (myAction.equals("logon"))
				{
					myOutputStream.println("Name:");
					String name = myInputStream.readLine();
					Logger.getLogger("mmud").finest("received from socket: name=[" + name + "]");
					myOutputStream.println("Password:");
					String password = myInputStream.readLine();
					Logger.getLogger("mmud").finest("received from socket: password=[" + password + "]");
					myOutputStream.println("Cookie:");
					String cookie = myInputStream.readLine();
					Logger.getLogger("mmud").finest("received from socket: cookie=[" + cookie + "]");
					myOutputStream.println("Frames:");
					String frames = myInputStream.readLine();
					Logger.getLogger("mmud").finest("received from socket: frames=[" + frames + "]");
					int frame = 1;
					try
					{
						frame = Integer.parseInt(frames);
					}
					catch (NumberFormatException e)
					{
						Logger.getLogger("mmud").warning(
							"unable to interpret frame information, defaulting to 0.");
					}
					try
					{
		 				myOutputStream.println(
							enterMud(name, password, 
								theSocket.getInetAddress().getCanonicalHostName(),
								cookie, frame-1) + "\n.\n");
					}
					catch (Exception e)
					{
						Logger.getLogger("mmud").warning(
							e.getMessage());
						myOutputStream.println("\n" + e.toString());
					}
				}
				if (myAction.equals("mud"))
				{
					myOutputStream.println("Name:");
					String name = myInputStream.readLine();
					Logger.getLogger("mmud").finest("received from socket: name=[" + name + "]");
					myOutputStream.println("Cookie:");
					String cookie = myInputStream.readLine();
					Logger.getLogger("mmud").finest("received from socket: cookie=[" + cookie + "]");
					myOutputStream.println("Frames:");
					String frames = myInputStream.readLine();
					Logger.getLogger("mmud").finest("received from socket: frames=[" + frames + "]");
					int frame = 1;
					try
					{
						frame = Integer.parseInt(frames);
					}
					catch (NumberFormatException e)
					{
						Logger.getLogger("mmud").warning(
							"unable to interpret frame information, defaulting to 0.");
					}
//					while (true)
//					{
					myOutputStream.println("Command:");
					StringBuffer command = new StringBuffer();
					String readem = myInputStream.readLine();
					if (!readem.equals("."))
					{
						command.append(readem);
						readem = myInputStream.readLine();
					}
					while (!readem.equals("."))
					{
						command.append("\n" + readem);
						readem = myInputStream.readLine();
					}
					Logger.getLogger("mmud").finest("received from socket: command=[" + command + "]");
					myOutputStream.println(
						executeMud(name, 
						theSocket.getInetAddress().getCanonicalHostName(),
						cookie,
						frame-1, command.toString()) + "\n.\n");
//					}
				}
				if (myAction.equals("newchar"))
				{
					myOutputStream.println("Name:");
					String name = myInputStream.readLine();
					myOutputStream.println("Password:");
					String password = myInputStream.readLine();
					myOutputStream.println("Cookie:");
					String cookie = myInputStream.readLine();
					myOutputStream.println("Frames:");
					String frames = myInputStream.readLine();
					int frame = 1;
					try
					{
						frame = Integer.parseInt(frames);
					}
					catch (NumberFormatException e)
					{
						Logger.getLogger("mmud").warning(
							"unable to interpret frame information, defaulting to 0.");
					}
					myOutputStream.println("Realname:");
					String realname = myInputStream.readLine();
					myOutputStream.println("Email:");
					String email = myInputStream.readLine();
					myOutputStream.println("Title:");
					String title = myInputStream.readLine();
					myOutputStream.println("Race:");
					String race = myInputStream.readLine();
					myOutputStream.println("Sex:");
					String sex = myInputStream.readLine();
					myOutputStream.println("Age:");
					String age = myInputStream.readLine();
					myOutputStream.println("Length:");
					String length = myInputStream.readLine();
					myOutputStream.println("width:");
					String width = myInputStream.readLine();
					myOutputStream.println("Complexion:");
					String complexion = myInputStream.readLine();
					myOutputStream.println("Eyes:");
					String eyes = myInputStream.readLine();
					myOutputStream.println("Face:");
					String face = myInputStream.readLine();
					myOutputStream.println("Hair:");
					String hair = myInputStream.readLine();
					myOutputStream.println("Beard:");
					String beard = myInputStream.readLine();
					myOutputStream.println("Arms:");
					String arms = myInputStream.readLine();
					myOutputStream.println("Legs:");
					String legs = myInputStream.readLine();
					myOutputStream.println(
						newUserMud(name, password,
						theSocket.getInetAddress().getCanonicalHostName(),
						realname, email, title, race, Sex.createFromString(sex), 
						age, length,
						width, complexion, eyes, face, hair, beard, arms,
						legs, cookie, frame-1) + "\n.\n");
				}
			}
			catch (Exception e)
			{
				Logger.getLogger("mmud").warning(
					e.getMessage());
				myOutputStream.println(e.toString());
			}
			String expectingOk;
			expectingOk = myInputStream.readLine();
			while (expectingOk != null && !expectingOk.equals("Ok"))
			{
				expectingOk = myInputStream.readLine();
			}
			myOutputStream.close();
			myInputStream.close();
			theSocket.close();
			Logger.getLogger("mmud").info("closing connection...");
		} catch (IOException e)
		{
			theSuccess = false;
			return;
		}
		theSuccess = true;
		return;
	}

	/**
	 * main function for logging into the game<P>
	 * The following checks and tasks are performed:
	 * <OL><LI>check if mud is enabled/disabled
	 * <LI>validate input using perl reg exp.
	 * <LI>check if user is banned
	 * <LI>create new sessionpassword
	 * </OL>
	 * @param socketfd int, is the socketdescriptor of the communication channel,
	 * can using find_in_list for example. 
	 * @see find_in_list
	 * @see remove_from_list
	 * @returns integer with the success code, 0 is bad, 1 is good
	 */
	private String enterMud(String aName, String aPassword, String aAddress, String aCookie, int aFrames)
		throws MudException
	{
		Logger.getLogger("mmud").finer("aName=" + aName + 
			",aPassword=" + aPassword + 
			",aAddress=" + aAddress + 
			",aCookie=" + aCookie + 
			",aFrames=" + aFrames);
		try
		{
		try
		{
		String myOfflineString = isOffline();
		if (myOfflineString != null) 
		{
			return myOfflineString;
		}
		}
		catch (IOException e)
		{
			throw new MudException(e.getMessage());
		}
		Perl5Compiler myCompiler = new Perl5Compiler();
		Perl5Matcher myMatcher = new Perl5Matcher();
		try
		{
			if (!myMatcher.matches(aName, myCompiler.compile("[A-Z|_|a-z]{3,}")))
			{
				Logger.getLogger("mmud").info("invalid name " + aName);
				return Constants.logoninputerrormessage;
			}
			if (aPassword.length() < 5)
			{
				Logger.getLogger("mmud").info("password too short " + aPassword);
				return Constants.logoninputerrormessage;
			}
		}
		catch (MalformedPatternException e)
		{
			// this should not happen
			e.printStackTrace();
		}
		if (Database.isUserBanned(aName, aAddress))
		{
			Logger.getLogger("mmud").info("thrown " + Constants.USERBANNEDERROR);
			throw new MudException(Constants.USERBANNEDERROR);
		}
		User myUser = Persons.activateUser(aName, aPassword, aCookie);
		myUser.generateSessionPassword();
		myUser.setFrames(aFrames);
		myUser.writeMessage(Database.getLogonMessage());
		if (Database.hasUserNewMail(myUser)) 
		{
			myUser.writeMessage("You have no new Mudmail...<P>\r\n");
		}
		else
		{
			myUser.writeMessage("You have new Mudmail!<P>\r\n");
		}
		String returnStuff;
		switch (myUser.getFrames())
		{
			case 0 :
			{
				returnStuff = "sessionpassword=" + myUser.getSessionPassword() + "\n";
				returnStuff += gameMain(myUser, "me has entered the game...<BR>\r\n");
				break;
			}
			case 1 :
			{
				returnStuff = "sessionpassword=" + myUser.getSessionPassword() + "\n";
				returnStuff += "<HTML><HEAD><TITLE>Land of Karchan - " + myUser.getName() + "</TITLE></HEAD>\r\n";
				returnStuff += "<FRAMESET ROWS=\"*,50\">\r\n";
				returnStuff += " <FRAMESET COLS=\"*,180\">\r\n";
				returnStuff += "	 <FRAME SRC=" + myUser.getUrl("me+has+entered+the+game...") + " NAME=\"main\" border=0>\r\n";
				returnStuff += "	 <FRAME SRC=" + Constants.leftframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"leftframe\" scrolling=\"no\" border=0>\r\n";
				returnStuff += " </FRAMESET>\r\n";
				returnStuff += " <FRAME SRC=" + Constants.logonframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"logon\" scrolling=\"no\" border=0>\r\n";
				returnStuff += "</FRAMESET>\r\n";
				returnStuff += "</HTML>\r\n";
				break;
			}
			case 2 :
			{
				returnStuff = "sessionpassword=" + myUser.getSessionPassword() + "\n";
				returnStuff += "<HTML><HEAD><TITLE>Land of Karchan - " + myUser.getName() + "</TITLE></HEAD>\r\n";
				returnStuff += "<FRAMESET ROWS=\"*,50,0,0\">\r\n";
				returnStuff += " <FRAMESET COLS=\"*,180\">\r\n";
				returnStuff += "	 <FRAMESET ROWS=\"60%,40%\">\r\n";
				returnStuff += "	 <FRAME SRC=" + myUser.getUrl("me+has+entered+the+game...") + " NAME=\"statusFrame\" border=0>\r\n";
				returnStuff += "	 <FRAME SRC=/karchan/empty.html NAME=\"logFrame\">\r\n";
				returnStuff += "	 </FRAMESET>\r\n";
				returnStuff += " <FRAME SRC=" + Constants.nph_leftframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"leftFrame\" scrolling=\"no\" border=0>\r\n";
				returnStuff += " </FRAMESET>\r\n\r\n";
				returnStuff += " <FRAME SRC=" + Constants.nph_logonframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"commandFrame\" scrolling=\"no\" border=0>\r\n";
				returnStuff += " <FRAME SRC=" + Constants.nph_javascriptframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"javascriptFrame\">\r\n";
				returnStuff += " <FRAME SRC=/karchan/empty.html NAME=\"duhFrame\">\r\n";
				returnStuff += "</FRAMESET>\r\n";
				returnStuff += "</HTML>\r\n";
				break;
			}
			default :
			{
				Logger.getLogger("mmud").info("thrown " + Constants.INVALIDFRAMEERROR);
				throw new PersonException(Constants.INVALIDFRAMEERROR);
			}
		}
		Logger.getLogger("mmud").finest("returns: [" + returnStuff + "]");
		return returnStuff;
		}
		catch (PersonException e)
		{
			if (e.getMessage().equals(Constants.USERALREADYACTIVEERROR))
			{
				// already active user wishes to relogin
				User user = (User) Persons.retrievePerson(aName);
				String returnStuff = reloginMud(user, aName, aPassword, aFrames);
				Logger.getLogger("mmud").finest("returns: [" + returnStuff + "]");
				return returnStuff;
			}
			if (e.getMessage().equals(Constants.USERNOTFOUNDERROR))
			{
				// new user
				String myString = "";
				try
				{
				myString = Constants.readFile(Constants.mudnewcharfile);
				}
				catch (IOException f)
				{
					throw new MudException(f.getMessage());
				}
				myString += "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"" + aName + "\">\n";
				myString += "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"" + aPassword + "\">\n";
				myString += "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"" + (aFrames + 1) + "\">\n";
				myString += "<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n";
				myString += "<INPUT TYPE=\"reset\" VALUE=\"Clear\">\n";
				myString += "</FORM></BODY></HTML>\n";
				return myString;
			}
			return Database.getErrorMessage(e.getMessage());
		}
		catch (MudException e)
		{
			return Database.getErrorMessage(e.getMessage());
		}
	}


	/**
	 * main function for executing a command in the game<P>
	 * The following checks and tasks are performed:
	 * <OL><LI>check if mud is enabled/disabled
	 * <LI>validate input using perl reg exp.
	 * <LI>check if user is banned
	 * </OL>
	 */
	private String executeMud(String aName, String aAddress, String aCookie, int aFrames, String aCommand)
	{
		Logger.getLogger("mmud").finer("aName=" + aName + 
			",aAddress=" + aAddress + 
			",aCookie=" + aCookie + 
			",aFrames=" + aFrames + 
			",aCommand=" + aCommand);
		try
		{
		try
		{
		String myOfflineString = isOffline();
		if (myOfflineString != null) 
		{
			return myOfflineString;
		}
		}
		catch (IOException e)
		{
			throw new MudException(e.getMessage());
		}
		Perl5Compiler myCompiler = new Perl5Compiler();
		Perl5Matcher myMatcher = new Perl5Matcher();
		try
		{
			if (!myMatcher.matches(aName, myCompiler.compile("[A-Z|_|a-z]{3,}")))
			{
				Logger.getLogger("mmud").info("invalid name " + aName);
				return Constants.logoninputerrormessage;
			}
/*			if (myMatcher.matches(aCommand, myCompiler.compile("(.)*([<applet|<script|java-script|CommandForm])+(.)*")))
			{
				Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDCOMMANDERROR);
				throw new MudException(Constants.INVALIDCOMMANDERROR);
			}
*/
		}
		catch (MalformedPatternException e)
		{
			// this should not happen
			e.printStackTrace();
		}
		if (Database.isUserBanned(aName, aAddress))
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.USERBANNEDERROR);
			throw new MudException(Constants.USERBANNEDERROR);
		}
		Person myChar = Persons.retrievePerson(aName);
		if (myChar == null)
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.USERNOTFOUNDERROR);
			throw new MudException(Constants.USERNOTFOUNDERROR);
		}
		if (!(myChar instanceof User))
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.NOTAUSERERROR);
			throw new MudException(Constants.NOTAUSERERROR);
		}
		User myUser = (User) myChar;
		if (!myUser.verifySessionPassword(aCookie))
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.PWDINCORRECTERROR);
			throw new MudException(Constants.PWDINCORRECTERROR);
		}
		myUser.setFrames(aFrames);
		String returnStuff;
		returnStuff = gameMain(myUser, aCommand);
		returnStuff += "<HR><FONT Size=1><DIV ALIGN=right>" + Constants.mudcopyright + "<DIV ALIGN=left></FONT>";
		returnStuff += "</BODY></HTML>";
		Logger.getLogger("mmud").finest("returns: " + returnStuff);
		return returnStuff;
		}
		catch (PersonException e)
		{
			return Database.getErrorMessage(e.getMessage());
		}
		catch (MudException e)
		{
			return Database.getErrorMessage(e.getMessage());
		}
	}

	/**
	 * dump a page to the user displaying a <I>relogging in</I> page.
	 */
	private String reloginMud(User aUser, String aName, String aPassword, int aFrames)
		throws MudException
	{
		Logger.getLogger("mmud").finer("");
		String returnStuff;
		aUser.generateSessionPassword();
		aUser.setFrames(aFrames);
		returnStuff = "sessionpassword=" + aUser.getSessionPassword() + "\n";
		switch (aFrames)
		{
			case 0 :
			{
				returnStuff += "<HTML><HEAD><TITLE>Error</TITLE></HEAD>\n\n";
				returnStuff += "<BODY>\n";
				returnStuff += "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"/images/gif/webpic/back4.gif\"><H1>Already Active</H1><HR>\n";
				returnStuff += "You tried to start a session which is already in progress. You can't play \n";
				returnStuff += "two sessions at the same time! Please check below to try again. In case you \n";
				returnStuff += "accidently turned of your computerterminal or Netscape or Lynx without first \n";
				returnStuff += "having typed <B>QUIT</B> while you were in the MUD, you can reenter the game \n";
				returnStuff += "by using the second link. You have to sit at the same computer as you did \n";
				returnStuff += "when you logged in.<P>\n";
				returnStuff += "<A HREF=\"/karchan/enter.html\">Click here to\n";
				returnStuff += "retry</A><P>\n";
		
				returnStuff += "Do you wish to enter into the active character?<BR><UL><LI>";
				returnStuff += "<A HREF=\"" + Constants.mudcgi + "?command=me+entered+the+game+again...&name=" + aName + "&frames=" + (aFrames+1) + "\">Yes</A>";
				returnStuff += "<LI><A HREF=\"/karchan/index.html\">No</A></UL>";
				returnStuff += "<HR><FONT Size=1><DIV ALIGN=right>" + Constants.mudcopyright;
				returnStuff += "<DIV ALIGN=left><P>";
				returnStuff += "</body>\n";
				returnStuff += "</HTML>\n";
				break;
			}
			case 1 :
			{
				returnStuff += "<HTML><HEAD><TITLE>Land of Karchan - " + aName + "</TITLE></HEAD>\r\n";
				returnStuff += "<FRAMESET ROWS=\"*,50\">\r\n";
				returnStuff += " <FRAMESET COLS=\"*,180\">\r\n";
				returnStuff += "	 <FRAME SRC=\"/karchan/already_active.html\" NAME=\"main\" border=0>\r\n";
				returnStuff += "	 <FRAME SRC=\"" + Constants.leftframecgi + "?name=" + aName + "&password=" + aPassword + "\" NAME=\"leftframe\" scrolling=\"no\" border=0>\r\n";
				returnStuff += " </FRAMESET>\r\n";
				returnStuff += " <FRAME SRC=\"" + Constants.logonframecgi + "?name=" + aName + "&password=" + aPassword + "\" NAME=\"logon\" scrolling=\"no\" border=0>\r\n";
				returnStuff += "</FRAMESET>\r\n";
				returnStuff += "</HTML>\r\n";
				break;
			}
			case 2 :
			{
				returnStuff += "<HTML><HEAD><TITLE>Land of Karchan - " + aName + "</TITLE></HEAD>\r\n";
				returnStuff += "<FRAMESET ROWS=\"*,50,0,0\">\r\n";
				returnStuff += " <FRAMESET COLS=\"*,180\">\r\n";
				returnStuff += "	 <FRAMESET ROWS=\"60%,40%\">\r\n";
				returnStuff += "	 <FRAME SRC=\"/karchan/already_active.html\" NAME=\"statusFrame\" border=0>\r\n";
				returnStuff += "	 <FRAME SRC=\"/empty.html\" NAME=\"logFrame\">\r\n";
				returnStuff += "	 </FRAMESET>\r\n";
				returnStuff += " <FRAME SRC=\"" + Constants.nph_leftframecgi + "?name=" + aName + "&password=" + aPassword + "\" NAME=\"leftFrame\" scrolling=\"no\" border=0>\r\n";
				returnStuff += " </FRAMESET>\r\n\r\n";
				returnStuff += " <FRAME SRC=\"" + Constants.nph_logonframecgi + "?name=" + aName + "&password=" + aPassword + "\" NAME=\"commandFrame\" scrolling=\"no\" border=0>\r\n";
				returnStuff += " <FRAME SRC=\"" + Constants.nph_javascriptframecgi + "?name=" + aName + "&password=" + aPassword + "\" NAME=\"javascriptFrame\">\r\n";
				returnStuff += " <FRAME SRC=\"/karchan/empty.html\" NAME=\"duhFrame\">\r\n";
				returnStuff += "</FRAMESET>\r\n";
				returnStuff += "</HTML>\r\n";
				break;
			}
			default :
			{
				throw new MudException(Constants.INVALIDFRAMEERROR);
			}
		} 
		Logger.getLogger("mmud").finer("returns: [" + returnStuff + "]");
		return returnStuff;
	}

	/**
	 * create a new user and log him in 
	 */
	private String newUserMud(String aName, String aPassword, String anAddress,
		String aRealName,
		String aEmail,
		String aTitle,
		String aRace,
		Sex aSex,
		String aAge,
		String aLength,
		String aWidth,
		String aComplexion,
		String aEyes,
		String aFace,
		String aHair,
		String aBeard,
		String aArms,
		String aLegs,
		String aCookie, int aFrames)
	{
		Logger.getLogger("mmud").finer("");
		try
		{
		try
		{
		String myOfflineString = isOffline();
		if (myOfflineString != null) 
		{
			return myOfflineString;
		}
		}
		catch (IOException e)
		{
			throw new MudException(e.getMessage());
		}
		Perl5Compiler myCompiler = new Perl5Compiler();
		Perl5Matcher myMatcher = new Perl5Matcher();
		try
		{
			if (!myMatcher.matches(aName, myCompiler.compile("[A-Z|_|a-z]{3,}")))
			{
				Logger.getLogger("mmud").info("invalid name " + aName);
				return Constants.logoninputerrormessage;
			}
			if (aPassword.length() < 5)
			{
				Logger.getLogger("mmud").info("password too short " + aPassword);
				return Constants.logoninputerrormessage;
			}
		}
		catch (MalformedPatternException e)
		{
			// this should not happen
			e.printStackTrace();
		}
		if (Database.isUserBanned(aName, anAddress))
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.USERBANNEDERROR);
			throw new MudException(Constants.USERBANNEDERROR);
		}
		User myUser = Persons.createUser(aName,
			aPassword, anAddress, aRealName,
			aEmail,
			aTitle,
			aRace,
			aSex,
			aAge,
			aLength,
			aWidth,
			aComplexion,
			aEyes,
			aFace,
			aHair,
			aBeard,
			aArms,
			aLegs,
			aCookie);
		myUser.generateSessionPassword();
		myUser.setFrames(aFrames);
		myUser.writeMessage(Database.getLogonMessage());
		if (Database.hasUserNewMail(myUser)) 
		{
			myUser.writeMessage("You have no new Mudmail...<P>\r\n");
		}
		else
		{
			myUser.writeMessage("You have new Mudmail!<P>\r\n");
		}
		String returnStuff = "sessionpassword=" + myUser.getSessionPassword() + "\n";
		switch (myUser.getFrames())
		{
			case 0 :
			{
				returnStuff += gameMain(myUser, "me has entered the game...<BR>\r\n");
				break;
			}
			case 1 :
			{
				returnStuff += "<HTML><HEAD><TITLE>Land of Karchan - " + myUser.getName() + "</TITLE></HEAD>\r\n";
				returnStuff += "<FRAMESET ROWS=\"*,50\">\r\n";
				returnStuff += " <FRAMESET COLS=\"*,180\">\r\n";
				returnStuff += "	 <FRAME SRC=" + myUser.getUrl("me+has+entered+the+game...") + " NAME=\"main\" border=0>\r\n";
				returnStuff += "	 <FRAME SRC=" + Constants.leftframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"leftframe\" scrolling=\"no\" border=0>\r\n";
				returnStuff += " </FRAMESET>\r\n";
				returnStuff += " <FRAME SRC=" + Constants.logonframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"logon\" scrolling=\"no\" border=0>\r\n";
				returnStuff += "</FRAMESET>\r\n";
				returnStuff += "</HTML>\r\n";
				break;
			}
			case 2 :
			{
				returnStuff += "<HTML><HEAD><TITLE>Land of Karchan - " + myUser.getName() + "</TITLE></HEAD>\r\n";
				returnStuff += "<FRAMESET ROWS=\"*,50,0,0\">\r\n";
				returnStuff += " <FRAMESET COLS=\"*,180\">\r\n";
				returnStuff += "	 <FRAMESET ROWS=\"60%,40%\">\r\n";
				returnStuff += "	 <FRAME SRC=" + myUser.getUrl("me+has+entered+the+game...") + " NAME=\"statusFrame\" border=0>\r\n";
				returnStuff += "	 <FRAME SRC=/karchan/empty.html NAME=\"logFrame\">\r\n";
				returnStuff += "	 </FRAMESET>\r\n";
				returnStuff += " <FRAME SRC=" + Constants.nph_leftframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"leftFrame\" scrolling=\"no\" border=0>\r\n";
				returnStuff += " </FRAMESET>\r\n\r\n";
				returnStuff += " <FRAME SRC=" + Constants.nph_logonframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"commandFrame\" scrolling=\"no\" border=0>\r\n";
				returnStuff += " <FRAME SRC=" + Constants.nph_javascriptframecgi + "?name=" + myUser.getName() + "&password=" + myUser.getPassword() + " NAME=\"javascriptFrame\">\r\n";
				returnStuff += " <FRAME SRC=/karchan/empty.html NAME=\"duhFrame\">\r\n";
				returnStuff += "</FRAMESET>\r\n";
				returnStuff += "</HTML>\r\n";
				break;
			}
			default :
			{
				Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDFRAMEERROR);
				throw new PersonException(Constants.INVALIDFRAMEERROR);
			}
		}
		return returnStuff;
		}
		catch (PersonException e)
		{
			return Database.getErrorMessage(e.getMessage());
		}
		catch (MudException e)
		{
			return Database.getErrorMessage(e.getMessage());
		}
	}

	/**
	 * the main batch of the server. It parses and executes
	 * the command of the user.
	 * @param aUser User who wishes to execute a command.
	 * @param aCommand String containing the command entered
	 * @return returns a string containing the answer.
	 */
	public String gameMain(User aUser, String aCommand)
		throws MudException
	{
		Logger.getLogger("mmud").finer("aUser=" + aUser.getName() + 
			",aCommand=" + aCommand);
		String returnStuff = "<HTML>\n";
		if (aCommand.equalsIgnoreCase("quit"))
		{
			returnStuff = "sessionpassword=\n<HTML>\n";
		}
		returnStuff += "<HEAD>\n";
		returnStuff += "<TITLE>\n";
		returnStuff += Constants.mudtitle;
		returnStuff += "\n</TITLE>\n";
		returnStuff += "</HEAD>\n";
		switch (aUser.getFrames())
		{
			case 0 :
			{
				returnStuff += "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"" + Constants.mudbackground + "\" onLoad=\"setfocus()\">\n";
				break;
			}
			case 1 :
			{
				returnStuff += "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"" + Constants.mudbackground + "\" OnLoad=\"top.frames[2].document.myForm.command.value='';top.frames[2].document.myForm.command.focus()\">\n";
				break;
			}
			case 2 :
			{
				returnStuff += "<BODY BGCOLOR=#FFFFFF BACKGROUND=\"" + Constants.mudbackground + "\"onLoad=\"top.frames[3].document.myForm.command.value='';top.frames[3].document.myForm.command.focus()\">\n";
				break;
			}
			default :
			{
				Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDFRAMEERROR);
				throw new PersonException(Constants.INVALIDFRAMEERROR);
			}
		} // end switch 
		int i = aCommand.indexOf(' ');
		Command myCommand = 
			Constants.getCommand( (i != -1 ? aCommand.substring(0, i) : aCommand) );
		myCommand.run(aUser, aCommand);
		if (myCommand.getResult() == null)
		{
			returnStuff += aUser.getRoom().getDescription(aUser);
			returnStuff += aUser.printForm();
			returnStuff += aUser.readLog();
		}
		else
		{
			returnStuff += myCommand.getResult();
		}
		return returnStuff;
	}

}
