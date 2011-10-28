package mmud.commands;

import java.util.logging.Logger;

import mmud.Constants;
import mmud.MudException;
import mmud.characters.CommunicationListener;
import mmud.characters.Person;
import mmud.characters.Persons;
import mmud.characters.User;

public abstract class CommunicationCommand extends NormalCommand
{

	public abstract CommunicationListener.CommType getCommType();

	private String theMessage;

	public CommunicationCommand(String regExpr)
	{
		super(regExpr);
	}

	@Override
	public boolean run(User aUser) throws MudException
	{
		Logger.getLogger("mmud").finer("");
		String command = getCommand();
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length <= 1)
		{
			return false;
		}
		if (myParsed.length > 3 && myParsed[1].equalsIgnoreCase("to"))
		{
			Person toChar = Persons.retrievePerson(myParsed[2]);
			if ((toChar == null) || (toChar.getRoom() != aUser.getRoom()))
			{
				aUser.writeMessage("Cannot find that person.<BR>\r\n");
			} else
			{
				if (aUser.isIgnored(toChar))
				{
					aUser.writeMessage(toChar.getName()
							+ " is ignoring you fully.<BR>\r\n");
					return true;
				}
				setMessage(command.substring(
						command.indexOf(myParsed[3], getCommType().toString()
								.length()
								+ 1 + 2 + 1 + myParsed[2].length())).trim());
				Persons.sendMessageExcl(aUser, toChar, "%SNAME "
						+ getCommType().getPlural() + " [to %TNAME] : "
						+ getMessage() + "<BR>\r\n");
				aUser.writeMessage(aUser, toChar, "<B>%SNAME "
						+ getCommType().toString() + " [to %TNAME]</B> : "
						+ getMessage() + "<BR>\r\n");
				toChar.writeMessage(aUser, toChar, "<B>%SNAME "
						+ getCommType().getPlural() + " [to %TNAME]</B> : "
						+ getMessage() + "<BR>\r\n");
				if (toChar instanceof CommunicationListener)
				{
					((CommunicationListener) toChar).commEvent(aUser,
							getCommType(), getMessage());
				}
			}
		} else
		{
			setMessage(command.substring(getCommType().toString().length() + 1)
					.trim());
			Persons.sendMessageExcl(aUser, "%SNAME "
					+ getCommType().getPlural() + " : " + getMessage()
					+ "<BR>\r\n");
			aUser.writeMessage(aUser, "<B>%SNAME " + getCommType().toString()
					+ "</B> : " + getMessage() + "<BR>\r\n");
		}
		return true;
	}

	public abstract Command createCommand();

	/**
	 * Sets the message that needs to be said.
	 * 
	 * @param aMessage
	 *            string containing the message.
	 */
	protected void setMessage(String aMessage)
	{
		theMessage = aMessage;
	}

	/**
	 * Gets the message that needs to be said.
	 * 
	 * @return String containing the message.
	 */
	protected String getMessage()
	{
		return theMessage;
	}

}