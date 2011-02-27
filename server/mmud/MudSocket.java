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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mmud.characters.InvalidFrameException;
import mmud.characters.Person;
import mmud.characters.PersonException;
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.characters.UserAlreadyActiveException;
import mmud.characters.UserNotFoundException;
import mmud.database.Database;
import mmud.database.MailDb;

/**
 * the class that takes care of all the socket communication. Is basically a
 * thread that terminates when the socket communication is terminated.<p/>
 * Now there are a couple of use cases that are worth mentioning:
 * <ol><li>A person logs onto the mud
 * <li>A person plays the mud
 * <li>A person logs off of the mud
 * <li>A person logs onto the mud, but is already playing
 * </ol>
 * 1. A Person logs onto the mud<p/>
 * 2. A person plays the mud<p/>
 * 3. A person logs off of the mud<p/>
 * 4. A person logs onto the mud, but is already playing<p/>
 * 
 */
public class MudSocket implements Runnable
{
	private Socket theSocket = null;
	private boolean theSuccess = true;

	private boolean theThreadCountDone = false;

	/**
	 * Checks to see if the mud is temporarily offline or not.
	 * 
	 * @return String containing a description to print if the mud is offline.
	 *         Returns a null pointer if the mud is not offline at all.
	 */
	public String isOffline() throws IOException, MudException
	{
		return Constants.getOfflineDescription();
	}

	/**
	 * Indicates if the mud call was successfull.
	 * 
	 * @return boolean, true if successfull, false otherwise
	 */
	public boolean isSuccessfull()
	{
		Logger.getLogger("mmud").finer("");
		return theSuccess;
	}

	/**
	 * Constructor
	 * 
	 * @param aSocket
	 *            the (already open) socket through which communication has to
	 *            take place.
	 */
	public MudSocket(Socket aSocket)
	{
		Logger.getLogger("mmud").finer("");
		theSocket = aSocket;
	}

	/**
	 * A little wrapper to properly deal with end-of-stream and io exceptions.
	 * 
	 * @param aReader
	 *            the reader stream, should be opened already.
	 * @return String read.
	 * @throws MudException
	 *             incase of problems of end-of-stream reached.
	 */
	private String readLine(BufferedReader aReader) throws MudException
	{
		try
		{
			String read = aReader.readLine();
			if (read == null)
			{
				throw new MudException("unexpected end of connection detected.");
			}
			return read;
		} catch (IOException e)
		{
			throw new MudException("io error during reading from socket.", e);
		}
		// this point is never reached. There is a return in the try statement.
	}

	/**
	 * Used primarily for receiving information for logging a user onto the mud
	 * and executing the enterMud method with the retrieved information.
	 * 
	 * @param aWriter
	 *            the writer to write output to.
	 * @param aReader
	 *            the reader to read input from.
	 * @see #enterMud
	 * @throws MudException
	 *             when either reading of the input goes wrong, or executing mud
	 *             functionality goes wrong.
	 */
	private void readLogonInfoFromSocket(PrintWriter aWriter,
			BufferedReader aReader) throws MudException
	{
		aWriter.println("Name:");
		String name = readLine(aReader);
		Logger.getLogger("mmud").finest(
				"received from socket: name=[" + name + "]");
		aWriter.println("Password:");
		String password = readLine(aReader);
		aWriter.println("Address:");
		String address = readLine(aReader);
		Logger.getLogger("mmud").finest(
				"received from socket: password=[" + password + "]");
		aWriter.println("Cookie:");
		String cookie = readLine(aReader);
		Logger.getLogger("mmud").finest(
				"received from socket: cookie=[" + cookie + "]");
		aWriter.println("Frames:");
		String frames = readLine(aReader);
		Logger.getLogger("mmud").finest(
				"received from socket: frames=[" + frames + "]");
		int frame = 1;
		try
		{
			frame = Integer.parseInt(frames);
		} catch (NumberFormatException e)
		{
			Logger.getLogger("mmud").warning(
					"unable to interpret frame information, defaulting to 0.");
		}
		aWriter.println(enterMud(name, password, address, cookie, frame - 1)
				+ "\n.\n");
	}

	/**
	 * Used primarily for receiving information and executing a specific command
	 * for a playing user in the mud.
	 * 
	 * @param aWriter
	 *            the writer to write output to.
	 * @param aReader
	 *            the reader to read input from.
	 * @see #executeMud
	 * @throws MudException
	 *             when either reading of the input goes wrong, or executing mud
	 *             functionality goes wrong.
	 */
	private void readCommandInfoFromSocket(PrintWriter aWriter,
			BufferedReader aReader) throws MudException
	{
		aWriter.println("Name:");
		String name = readLine(aReader);
		Logger.getLogger("mmud").finest(
				"received from socket: name=[" + name + "]");
		aWriter.println("Cookie:");
		String cookie = readLine(aReader);
		Logger.getLogger("mmud").finest(
				"received from socket: cookie=[" + cookie + "]");
		aWriter.println("Frames:");
		String frames = readLine(aReader);
		Logger.getLogger("mmud").finest(
				"received from socket: frames=[" + frames + "]");
		int frame = 1;
		try
		{
			frame = Integer.parseInt(frames);
		} catch (NumberFormatException e)
		{
			Logger.getLogger("mmud").throwing("mmud.MudSocket",
					"readCommandInfoFromSocket", e);
		}
		aWriter.println("Command:");
		StringBuffer command = new StringBuffer();
		String readem = readLine(aReader);
		if (!readem.equals("."))
		{
			command.append(readem);
			readem = readLine(aReader);
		}
		while (!readem.equals("."))
		{
			command.append("\n" + readem);
			readem = readLine(aReader);
		}
		Logger.getLogger("mmud").finest(
				"received from socket: command=[" + command + "]");
		if (command.toString().trim().equals(""))
		{
			command = new StringBuffer("l");
		}
		String result = executeMud(name, theSocket.getInetAddress()
				.getCanonicalHostName(), cookie, frame - 1, command.toString())
				+ "\n.\n";
		aWriter.println(result);
		// }
	}

	/**
	 * Run method of the thread. This is started when the start method is
	 * called. Reads from and writes to the socket provided upon creation of
	 * this thread.
	 * <P>
	 * The protocol description is as follows. (red is output sent to the user,
	 * green is input received from the user) The following are examples:
	 * <P>
	 * <FONT COLOR="red">Action (logon, mud, newchar):</FONT><BR>
	 * <FONT COLOR="green">logon</FONT><BR>
	 * <FONT COLOR="red">Name:</FONT><BR>
	 * <FONT COLOR="green">Karn</FONT><BR>
	 * <FONT COLOR="red">Password:</FONT><BR>
	 * <FONT COLOR="green">some damnable password thingy</FONT><BR>
	 * <FONT COLOR="red">Address:</FONT><BR>
	 * <FONT COLOR="green">someaddress.athome.nl</FONT><BR>
	 * <FONT COLOR="red">Cookie:</FONT><BR>
	 * <FONT COLOR="green">bdf87d7dfbb8fdbf87dfb7dfb</FONT><BR>
	 * <FONT COLOR="red">Frames:</FONT><BR>
	 * <FONT COLOR="green">1</FONT><BR>
	 * 
	 * <P>
	 * <FONT COLOR="red">Action (logon, mud, newchar):</FONT><BR>
	 * <FONT COLOR="green">mud</FONT><BR>
	 * <FONT COLOR="red">Name:</FONT><BR>
	 * <FONT COLOR="green">Karn</FONT><BR>
	 * <FONT COLOR="red">Cookie:</FONT><BR>
	 * <FONT COLOR="green">bdf87d7dfbb8fdbf87dfb7dfb</FONT><BR>
	 * <FONT COLOR="red">Frames:</FONT><BR>
	 * <FONT COLOR="green">1</FONT><BR>
	 * <FONT COLOR="red">Command:</FONT><BR>
	 * <FONT COLOR="green">look around<BR>
	 * .</FONT>
	 * 
	 * <P>
	 * Bear in mind that the <I>Command</I> field must end in a "." on a
	 * separate line.
	 * <P>
	 * 
	 * @see mmud.character.User#getFrames
	 */
	public void run()
	{
		Constants.incrementThreadsRunning();
		Logger.getLogger("mmud").finer("");
		PrintWriter myOutputStream = null;
		BufferedReader myInputStream = null;
		try
		{
			myOutputStream = new PrintWriter(theSocket.getOutputStream(), true);
			myInputStream = new BufferedReader(new InputStreamReader(theSocket
					.getInputStream()));

		} catch (UnknownHostException e)
		{
			theSuccess = false;
			Logger.getLogger("mmud").throwing("mmud.MudSocket", "run", e);
			Database.writeLog("root", e);
			setThreadCounters();
			return;
		} catch (IOException e)
		{
			Logger.getLogger("mmud").throwing("mmud.MudSocket", "run", e);
			Database.writeLog("root", e);
			theSuccess = false;
			setThreadCounters();
			return;
		}

		try
		{
			myOutputStream.println(Constants.IDENTITY);
			myOutputStream.println("Action (logon, mud, newchar):");
			String myAction = readLine(myInputStream);
			Logger.getLogger("mmud").finest(
					"received from socket: [" + myAction + "]");
			if (myAction.equals("logon"))
			{
				readLogonInfoFromSocket(myOutputStream, myInputStream);
			} else if (myAction.equals("mud"))
			{
				readCommandInfoFromSocket(myOutputStream, myInputStream);
			} else
			{
				throw new MudException(
						"unable to determine the action to take.");
			}
			String expectingOk;
			expectingOk = readLine(myInputStream);
			while (expectingOk != null && !expectingOk.equals("Ok"))
			{
				expectingOk = readLine(myInputStream);
			}
			myOutputStream.close();

			try
			{
				myInputStream.close();
			} catch (IOException e)
			{
				throw new MudException("unable to close input stream.", e);
			}

			try
			{
				theSocket.close();
			} catch (IOException e)
			{
				throw new MudException("unable to close socket.", e);
			}
			Logger.getLogger("mmud").finer("closing connection...");
		} catch (MudException e)
		{
			theSuccess = false;
			Logger.getLogger("mmud").throwing("mmud.MudSocket", "run", e);
			Database.writeLog("root", e);
			setThreadCounters();
			return;
		} catch (OutOfMemoryError mem_error)
		{
                        mem_error.printStackTrace();
                        Constants.logger.log(Level.WARNING, "exception {0}", mem_error);
                        System.out.println("Exiting abnormally.");
                        System.exit(1);
			return;
		} catch (RuntimeException e2)
		{
			Logger.getLogger("mmud").throwing("mmud.MudSocket", "run", e2);
			Database.writeLog("root", e2);
			theSuccess = false;
			setThreadCounters();
			return;
		}
		setThreadCounters();
		theSuccess = true;
		return;
	}

	private void setThreadCounters()
	{
		if (theThreadCountDone)
		{
			throw new RuntimeException(
					"Error! Already stepped thread counters.");
		}
		theThreadCountDone = true;
		Constants.decrementThreadsRunning();
		Constants.incrementThreadsProcessed();
	}

	/**
	 * main function for logging into the game.
	 * <P>
	 * The following checks and tasks are performed:
	 * <OL>
	 * <LI>check if mud is enabled/disabled
	 * <LI>validate input using perl reg exp.
	 * <LI>check if user is banned
	 * <LI>create new sessionpassword
	 * </OL>
	 * 
	 * @param aName
	 *            the name of the character to logon.
	 * @param aPassword
	 *            password of the character to logon.
	 * @param aAddress
	 *            the address of the person connecting.
	 * @param aCookie
	 *            the sessionpassword to verify the current session.
	 * @param aFrames
	 *            the user interface to use
	 *            <ul>
	 *            <li>0 = single window </li><li>1 = multiple windows (frames) </li><li>2
	 *            = drupal</li>
	 *            </ul>
	 * @return String containing the text to be sent to the user
	 * @throws MudException
	 *             in case something goes wrong.
	 */
	private String enterMud(String aName, String aPassword, String aAddress,
			String aCookie, int aFrames) throws MudException
	{
		Logger.getLogger("mmud").finer(
				"aName=" + aName + ",aPassword=" + aPassword + ",aAddress="
						+ aAddress + ",aCookie=" + aCookie + ",aFrames="
						+ aFrames);
		if (Constants.debugOn(aName))
		{
			Logger.getLogger("mmud_debug").finest(
				"enterMud aName=" + aName + ",aPassword=" + aPassword + ",aAddress="
						+ aAddress + ",aCookie=" + aCookie + ",aFrames="
						+ aFrames);
		}
		if ((aAddress == null) || ("".equals(aAddress.trim())))
		{
			throw new MudException("address unknown");
		}
		try
		{
			try
			{
				String myOfflineString = isOffline();
				if (myOfflineString != null)
				{
					if (aFrames == 2) {return "Karchan is offline.";}
					return myOfflineString;
				}
			} catch (IOException e)
			{
				throw new MudException(e.getMessage(), e);
			}
			if (!aName.matches("[A-Z|_|a-z]{3,}"))
			{
				Logger.getLogger("mmud").info("invalid name " + aName);
				if (aFrames == 2) {return "Name contains illegal characters.";}
				return Constants.logoninputerrormessage;
			}
			if (aPassword.length() < 5)
			{
				Logger.getLogger("mmud")
						.info("password too short " + aPassword);
				if (aFrames == 2) {return "Password needs at least 5 characters.";}
				return Constants.logoninputerrormessage;
			}
			if (Database.isUserBanned(aName, aAddress))
			{
				Logger.getLogger("mmud").info(
						"thrown " + Constants.USERBANNEDERROR);
				if (aFrames == 2) {return "Player is banned from playing.";}
				throw new MudException(Constants.USERBANNEDERROR);
			}
			if (!Database.existsUser(aName))
			{
				if (aFrames == 2) {return "Player not found.";}
				throw new UserNotFoundException();
			}
			User myUser = Persons.activateUser(aName, aPassword, aAddress,
					aCookie);
			myUser.generateSessionPassword();
			myUser.setFrames(aFrames);
			myUser.writeMessage(Database.getLogonMessage());
			if (MailDb.hasUserNewMail(myUser))
			{
				myUser.writeMessage("You have new Mudmail!<P>\r\n");
			} else
			{
				myUser.writeMessage("You have no new Mudmail...<P>\r\n");
			}
			if (myUser.getGuild() != null)
			{
				if (myUser.getGuild().getLogonMessage() != null)
				{
					myUser.writeMessage(myUser.getGuild().getLogonMessage()
							+ "<HR>");
				}
				myUser.writeMessage(myUser.getGuild().getAlarmDescription()
						+ "<HR>");
			}
			StringBuffer returnStuff = new StringBuffer();
			Database.writeLog(myUser.getName(), "entered game.");
			switch (myUser.getFrames())
			{
			case 0:
			{
				returnStuff.append("sessionpassword="
						+ myUser.getSessionPassword() + "\n");
				returnStuff.append(gameMain(myUser,
						"me has entered the game..."));
				break;
			}
			case 1:
			{
				returnStuff.append("sessionpassword="
						+ myUser.getSessionPassword() + "\n");
				returnStuff
						.append("<FRAMESET ROWS=\"*,80\">\r\n"
						+ " <FRAMESET COLS=\"*,180\">\r\n" + "	 <FRAME SRC=");
				returnStuff.append(myUser.getUrl("me+has+entered+the+game..."));
				returnStuff.append(" NAME=\"main\" border=0>\r\n"
						+ "	 <FRAME SRC=" + Constants.leftframecgi + "?name="
						+ myUser.getName() + "&password="
						+ myUser.getPassword()
						+ " NAME=\"leftframe\" scrolling=\"no\" border=0>\r\n");
				returnStuff.append(" </FRAMESET>\r\n");
				returnStuff.append(" <FRAME SRC=" + Constants.logonframecgi
						+ "?name=" + myUser.getName() + "&password="
						+ myUser.getPassword()
						+ " NAME=\"logon\" scrolling=\"no\" border=0>\r\n"
						+ "</FRAMESET>\r\n");
				break;
			}
			case 2:
			{
				returnStuff.append("sessionpassword="
						+ myUser.getSessionPassword() + "\n");
				returnStuff.append("Ok");
				break;
			}
			default:
			{
				Logger.getLogger("mmud").info(
						"thrown " + Constants.INVALIDFRAMEERROR);
				throw new InvalidFrameException();
			}
			}
			if (Constants.debugOn(aName))
			{
				Logger.getLogger("mmud_debug").finest("returns: [" + returnStuff + "]");
			}
			Logger.getLogger("mmud").finest("returns: [" + returnStuff + "]");
			return returnStuff.toString();
		} catch (UserAlreadyActiveException e)
		{
			if (Constants.debugOn(aName))
			{
				Logger.getLogger("mmud_debug").throwing(this.getClass().getName(), "enterMud", e);
			}
			// already active user wishes to relogin
			User user = (User) Persons.retrievePerson(aName);
			String returnStuff = reloginMud(user, aName, aPassword, aFrames);
			Database.writeLog(aName, "logonattempt, already playing.");
			Logger.getLogger("mmud").finest("returns: [" + returnStuff + "]");
			return returnStuff;
		} catch (UserNotFoundException e)
		{
			if (Constants.debugOn(aName))
			{
				Logger.getLogger("mmud_debug").throwing(this.getClass().getName(), "enterMud", e);
			}
			// new user
			String myString = "";
			try
			{
				myString = Constants.readFile("newchar.html");
			} catch (IOException f)
			{
				throw new MudException(f.getMessage());
			}
			Database.writeLog(aName, "unknown character.");
			if (aFrames == 2) {return "Player not found.";}
			return myString;
		} catch (PersonException e)
		{
			if (Constants.debugOn(aName))
			{
				Logger.getLogger("mmud_debug").throwing(this.getClass().getName(), "enterMud", e);
			}
			Logger.getLogger("mmud").log(Level.WARNING, "Exception detected!",
					e);
			return Database.getErrorMessage(e);
		} catch (MudException e)
		{
			if (Constants.debugOn(aName))
			{
				Logger.getLogger("mmud_debug").throwing(this.getClass().getName(), "enterMud", e);
			}
			Logger.getLogger("mmud").log(Level.WARNING, "Exception detected!",
					e);
			return Database.getErrorMessage(e);
		}
	}

	/**
	 * main function for executing a command in the game.
	 * <P>
	 * The following checks and tasks are performed:
	 * <OL>
	 * <LI>check if mud is enabled/disabled
	 * <LI>validate input using perl reg exp.
	 * <LI>check if user is banned
	 * </OL>
	 * 
	 * @param aName
	 *            the name of the character
	 * @param aAddress
	 *            the address of the computer connecting
	 * @param aCookie
	 *            the session password
	 * @param aFrames
	 *            the user interface to use
	 *            <ul>
	 *            <li>0 = single window <li>1 = multiple windows (frames) <li>2
	 *            = multiple windows with serverpush and javascript and all
	 *            sorts of other stuff.
	 *            </ul>
	 * @param aCommand
	 *            the command to be executed
	 * @return the text to be sent back to the user connecting
	 */
	private String executeMud(String aName, String aAddress, String aCookie,
			int aFrames, String aCommand)
	{
		if (Constants.debugOn(aName))
		{
			Logger.getLogger("mmud_debug").finest("executeMud [" + aCommand + "]");
		}
		Logger.getLogger("mmud").finer(
				"aName=" + aName + ",aAddress=" + aAddress + ",aCookie="
						+ aCookie + ",aFrames=" + aFrames + ",aCommand="
						+ aCommand);
		try
		{
			try
			{
				String myOfflineString = isOffline();
				if (myOfflineString != null)
				{
					return myOfflineString;
				}
			} catch (IOException e)
			{
				throw new MudException(e.getMessage());
			}
			if (!aName.matches("[A-Z|_|a-z]{3,}"))
			{
				Logger.getLogger("mmud").info("invalid name " + aName);
				return Constants.logoninputerrormessage;
			}
			if ((aCommand.toUpperCase().matches("(.)*<APPLET(.)*"))
					|| (aCommand.toUpperCase().matches("(.)*ONLOAD(.)*"))
					|| (aCommand.toUpperCase().matches("(.)*<SCRIPT(.)*"))
					|| (aCommand.toUpperCase().matches("(.)*JAVA-SCRIPT(.)*"))
					|| (aCommand.toUpperCase().matches("(.)*JAVASCRIPT(.)*"))
					|| (aCommand.toUpperCase().matches("(.)*COMMANDFORM(.)*")))
			{
				Logger.getLogger("mmud").info(
						"thrown: " + Constants.INVALIDCOMMANDERROR);
				throw new MudException(Constants.INVALIDCOMMANDERROR);
			}

			if (Database.isUserBanned(aName, aAddress))
			{
				Logger.getLogger("mmud").info(
						"thrown: " + Constants.USERBANNEDERROR);
				throw new MudException(Constants.USERBANNEDERROR);
			}
			Person myChar = Persons.retrievePerson(aName);
			if (myChar == null)
			{
				Logger.getLogger("mmud").info(
						"thrown: " + Constants.USERNOTFOUNDERROR);
				throw new MudException(Constants.USERNOTFOUNDERROR);
			}
			if (!(myChar instanceof User))
			{
				Logger.getLogger("mmud").info(
						"thrown: " + Constants.NOTAUSERERROR);
				throw new MudException(Constants.NOTAUSERERROR);
			}
			User myUser = (User) myChar;
			if (!myUser.verifySessionPassword(aCookie))
			{
				Logger.getLogger("mmud").info(
						"thrown: " + Constants.PWDINCORRECTERROR);
				throw new MudException(Constants.PWDINCORRECTERROR);
			}
			myUser.setFrames(aFrames);
			StringBuffer returnStuff;
			returnStuff = new StringBuffer(gameMain(myUser, aCommand));
			if (myUser.getFrames() != 2)
			{
				returnStuff.append("<HR><FONT Size=1><DIV ALIGN=right>");
				returnStuff.append(Constants.mudcopyright);
				returnStuff.append("<DIV ALIGN=left></FONT>");
			}
			Logger.getLogger("mmud").finest("returns: " + returnStuff);
			return returnStuff.toString();
		} catch (PersonException e)
		{
			e.printStackTrace();
			return Database.getErrorMessage(e);
		} catch (MudException e)
		{
			e.printStackTrace();
			return Database.getErrorMessage(e);
		}
	}

	/**
	 * dump a page to the user displaying a <I>relogging in</I> page.
	 */
	private String reloginMud(User aUser, String aName, String aPassword,
			int aFrames) throws MudException
	{
		Logger.getLogger("mmud").finer("");
		StringBuffer returnStuff;
		aUser.generateSessionPassword();
		aUser.setFrames(aFrames);
		returnStuff = new StringBuffer("sessionpassword="
				+ aUser.getSessionPassword() + "\n");
		switch (aFrames)
		{
		case 0:
		{
			returnStuff
					.append("<H1>Already Active</H1><HR>\n");
			returnStuff
					.append("You tried to start a session which is already in progress. You can't play \n");
			returnStuff
					.append("two sessions at the same time! Please check below to try again. In case you \n");
			returnStuff
					.append("accidently turned of your computerterminal or Netscape or Lynx without first \n");
			returnStuff
					.append("having typed <B>QUIT</B> while you were in the MUD, you can reenter the game \n");
			returnStuff
					.append("by using the second link. You have to sit at the same computer as you did \n");
			returnStuff.append("when you logged in.<P>\n");
			returnStuff
					.append("<A HREF=\"/karchan/enter.html\">Click here to\n");
			returnStuff.append("retry</A><P>\n");

			returnStuff
					.append("Do you wish to enter into the active character?<BR><UL><LI>");
			returnStuff.append("<A HREF=\"" + Constants.mudcgi
					+ "?command=me+entered+the+game+again...&name=" + aName
					+ "&frames=" + (aFrames + 1) + "\">Yes</A>");
			returnStuff
					.append("<LI><A HREF=\"/karchan/index.html\">No</A></UL>");
			returnStuff.append("<HR><FONT Size=1><DIV ALIGN=right>"
					+ Constants.mudcopyright);
			returnStuff.append("<DIV ALIGN=left><P>");
			break;
		}
		case 1:
		{
			returnStuff.append("<FRAMESET ROWS=\"*,50\">\r\n");
			returnStuff.append(" <FRAMESET COLS=\"*,180\">\r\n");
			returnStuff
					.append("	 <FRAME SRC=\"/karchan/already_active.html\" NAME=\"main\" border=0>\r\n");
			returnStuff.append("	 <FRAME SRC=\"" + Constants.leftframecgi
					+ "?name=" + aName + "&password=" + aPassword
					+ "\" NAME=\"leftframe\" scrolling=\"no\" border=0>\r\n");
			returnStuff.append(" </FRAMESET>\r\n");
			returnStuff.append(" <FRAME SRC=\"" + Constants.logonframecgi
					+ "?name=" + aName + "&password=" + aPassword
					+ "\" NAME=\"logon\" scrolling=\"no\" border=0>\r\n");
			returnStuff.append("</FRAMESET>\r\n");
			break;
		}
		case 2:
		{
			returnStuff.append("Player is already active.");
			break;
		}
		default:
		{
			if (Constants.debugOn(aName))
			{
				Logger.getLogger("mmud_debug").logp(Level.FINEST, this.getClass().getName(), "reloginMud", "Invalid frame " + aFrames);
			}
			throw new MudException(Constants.INVALIDFRAMEERROR);
		}
		}
		Logger.getLogger("mmud").finer("returns: [" + returnStuff + "]");
		if (Constants.debugOn(aName))
		{
			Logger.getLogger("mmud_debug").logp(Level.FINEST, this.getClass().getName(), "reloginMud", "returns: [" + returnStuff + "]");
		}
		return returnStuff.toString();
	}

	/**
	 * the main batch of the server. It parses and executes the command of the
	 * user.
	 * 
	 * @param aUser
	 *            User who wishes to execute a command.
	 * @param aCommand
	 *            String containing the command entered
	 * @return returns a string containing the answer.
	 * @throws MudException
	 *             when something goes wrong.
	 */
	public String gameMain(User aUser, String aCommand) throws MudException
	{
		Logger.getLogger("mmud").finer(
				"aUser=" + aUser.getName() + ",aCommand=" + aCommand);
		StringBuffer returnStuff = new StringBuffer("");
		String result = null;
		Database.writeCommandLog(aUser.getName(), aCommand);
		try
		{
			result = aUser.runCommand(aCommand);
		} catch (MudException e)
		{
			Database.writeLog(aUser.getName(), e);
			throw e;
		}
		if (result == null)
		{
			returnStuff.append(aUser.getRoom().getDescription(aUser));
			if (aUser.getFrames() != 2) {returnStuff.append(aUser.printForm());}
			returnStuff.append(aUser.readLog());
		} else
		{
			returnStuff.append(result);
		}
		return returnStuff.toString();
	}

}
