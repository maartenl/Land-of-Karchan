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
package mmud.database; 

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;

/**
 * Used for queries towards the database regarding Mail.
 * @see Database
 */
public class MailDb
{

	public static String sqlYouHaveNewMailString = "select count(*) as count from mm_mailtable where toname = ? and newmail = 1";
	public static String sqlListMailString = "select name, haveread, newmail, header from mm_mailtable where toname = ? order by whensent asc";
	public static String sqlReadMailString = "select * from mm_mailtable where toname = ? order by whensent asc";
	public static String sqlDeleteMailString = "delete from mm_mailtable where name = ? and toname = ? and whensent = ? ";
	public static String sqlUpdateMailString = "update mm_mailtable set haveread=1 where name = ? and toname = ? and whensent = ? ";
	public static String sqlSendMailString = "insert into mm_mailtable " +
		"(name, toname, header, whensent, haveread, newmail, message) " +
		"values (?, ?, ?, now(), 0, 1, ?)";

	/**
	 * returns a list of mudmails
	 * @param aUser the user to list the mudmails for
	 * @return String containing a bulleted list of mudmails.
	 */
	public static String getListOfMail(User aUser)
	{
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		int j = 1;
		String result = "<TABLE BORDER=0 VALIGN=top>\r\n";
		try
		{

		PreparedStatement sqlListMailMsg = Database.prepareStatement(sqlListMailString);
		sqlListMailMsg.setString(1, aUser.getName());
		res = sqlListMailMsg.executeQuery();
		if (res != null)
		{
			while (res.next())
			{
				result += "<TR VALIGN=TOP><TD>" + j + ".</TD><TD>";
				if (res.getInt("newmail") > 0) 
				{
					result += "N";
				}
				if (res.getInt("haveread") > 0) 
				{
					result += "U";
				}
				result += "</TD><TD><B>From: </B>" + res.getString("name") + "</TD>";
				result += "<TD><B>Header: </B><A HREF=\"" + aUser.getUrl("readmail+" + j) + "\">";
				result += res.getString("header") + "</A></TD><TD><A HREF=\"" + aUser.getUrl("deletemail+" + j) + "\">Delete</A></TD></TR>\r\n";
				j++;
			}
			res.close();
			result += "</TABLE><BR>\r\n";
		}
		sqlListMailMsg.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * reads a mail from a user.
	 * @param aUser the user whos mudmail should be read
	 * @param messagenr the identification of the message, usually a number
	 * where "1" represents the first message, "2" the second, etc.
	 * @return String properly HTML formatted containing the mudmail.
	 */
	public static String readMail(User aUser, int messagenr)
		throws MailException
	{
		Logger.getLogger("mmud").finer("");
		return doStuffWithMail(aUser, messagenr, false);
	}

	/**
	 * deletes a mail. Returns the mudmail that has been erased.
	 * @param aUser the user whos mudmail must be erased.
	 * @param messagenr the message number identifying the mudmail
	 * where "1" represents the first message, "2" the second, etc.
	 * @return String properly HTML formatted containing the mudmail.
	 * @throws MailException if the message with messagenumber could
	 * not be found. (for example the message number was illegal)
	 */
	public static String deleteMail(User aUser, int messagenr)
		throws MailException
	{
		Logger.getLogger("mmud").finer("");
		return doStuffWithMail(aUser, messagenr, true);
	}

	/**
	 * does either of two things with mail, dependant of the boolean
	 * parameter
	 * @param aUser the user whos mudmail we wish to mutate
	 * @param messagenr the identification number of the message
	 * where "1" represents the first message, "2" the second, etc.
	 * @param deleteIt boolean, <UL><LI>true = delete mail<LI>false = do not
	 * delete mail, but update <I>haveread</I></UL>
	 * @return String containing the mail in question.
	 * @throws MailException if the messagenumber is invalid.
	 */
	private static String doStuffWithMail(User aUser, int messagenr, boolean deleteIt)
		throws MailException
	{
		Logger.getLogger("mmud").finer("");
		if (messagenr <= 0)
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDMAILERROR);
			throw new InvalidMailException();
		}
		ResultSet res;
		int j = 1;
		String result = "<TABLE BORDER=0 VALIGN=top>\r\n";
		try
		{
		PreparedStatement sqlReadMailMsg =
			Database.prepareStatement(sqlReadMailString,
			ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		sqlReadMailMsg.setString(1, aUser.getName());
		res = sqlReadMailMsg.executeQuery();
		if (res != null)
		{
			if (!res.absolute(messagenr))
			{
				Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDMAILERROR);
				throw new InvalidMailException();
			}
			result += "<H1>Read Mail - " + res.getString("header") + "</H1>";
			result += "<HR noshade><TABLE BORDER=0>\r\n";
			result += "<TR><TD>Mes. Nr:</TD><TD> <B>" + messagenr + "</B></TD></TR>\r\n";
			result += "<TR><TD>From:</TD><TD><B>" + res.getString("name") + "</B></TD></TR>\r\n";
			result += "<TR><TD>On:</TD><TD><B>" + res.getDate("whensent") + " " + res.getTime("whensent") + "</B></TD></TR>\r\n";
			result += "<TR><TD>New?:</TD><TD><B>";
			if (res.getInt("newmail")>0) 
			{				
				result += "Yes</B></TD></TR>\r\n";
			}
			else 
			{
				result += "No</B></TD></TR>\r\n";
			}
			result += "<TR><TD>Read?:</TD><TD><B>";
			if (res.getInt("haveread")>0) 
			{
				result += "Yes</B></TD></TR>\r\n";
			}
			else 
			{				
				result += "No</B></TD></TR>\r\n";
			}
			result += "<TR><TD>Header:</TD><TD><B>" + res.getString("header") + "</B></TABLE>\r\n";
			result += "<HR noshade>" + res.getString("message");
			result += "<HR noshade><A HREF=\"" + aUser.getUrl("listmail") + "\">ListMail</A><P>";

			if (deleteIt)
			{
				PreparedStatement sqlDeleteMail = Database.prepareStatement(sqlDeleteMailString);
				sqlDeleteMail.setString(1, res.getString("name"));
				sqlDeleteMail.setString(2, aUser.getName());
				sqlDeleteMail.setTimestamp(3, res.getTimestamp("whensent"));
				sqlDeleteMail.executeUpdate();
				sqlDeleteMail.close();
			}
			else
			{
				PreparedStatement sqlUpdateMail = Database.prepareStatement(sqlUpdateMailString);
				sqlUpdateMail.setString(1, res.getString("name"));
				sqlUpdateMail.setString(2, aUser.getName());
				sqlUpdateMail.setTimestamp(3, res.getTimestamp("whensent"));
				sqlUpdateMail.executeUpdate();
				sqlUpdateMail.close();
			}

			res.close();
			result += "</TABLE><BR>\r\n";
		}
		else
		{
			sqlReadMailMsg.close();
			Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDMAILERROR);
			throw new InvalidMailException();
		}
		sqlReadMailMsg.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * send mud mail.
	 * @param aUser the user who wishes to send mudmail.
	 * @param toUser the user who has to receive the send mudmail.
	 * @param header the header of the mudmail
	 * @param message the body of the mudmail
	 */
	public static void sendMail(User aUser, User toUser, String header, String message)
	{
		Logger.getLogger("mmud").finer("");
		try
		{

		PreparedStatement sqlSendMailUser = Database.prepareStatement(sqlSendMailString);
		sqlSendMailUser.setString(1, aUser.getName());
		sqlSendMailUser.setString(2, toUser.getName());
		sqlSendMailUser.setString(3, header);
		sqlSendMailUser.setString(4, message);
		int res = sqlSendMailUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSendMailUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * see if the person has new mail
	 * @return boolean, true if found, false if not found
	 * @param aUser User whos mail must be checked.
	 */
	public static boolean hasUserNewMail(User aUser)
	{
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		try
		{

		PreparedStatement sqlGetMailStatus = Database.prepareStatement(sqlYouHaveNewMailString);
		sqlGetMailStatus.setString(1, aUser.getName());
		res = sqlGetMailStatus.executeQuery();
		if (res != null)
		{
			if (res.next())
			{
				if (res.getInt("count") > 0) 
				{
					res.close();
					sqlGetMailStatus.close();
					return true;
				}
			}
			res.close();
		}
		sqlGetMailStatus.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}


}
