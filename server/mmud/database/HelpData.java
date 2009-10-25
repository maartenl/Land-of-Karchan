/*-------------------------------------------------------------------------
svninfo: $Id: BoardsDb.java 1165 2009-08-05 05:58:29Z karn $
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

/**
 * Used for storing Help info.
 * 
 * @see Database.getHelp
 */
public class HelpData
{
	private final String theCommand;
	private final String theSynopsis;
	private final String theDescription;
	private String theExample1[];
	private String theExample2[];
	private final String theSeealso;

	public HelpData(String aCommand, String aDescription, String aSynopsis,
			String aSeealso)
	{
		theCommand = aCommand;
		theDescription = aDescription;
		theSynopsis = aSynopsis;
		theSeealso = aSeealso;
	}

	public void setFirstExample(String anExample, String aMe,
			String aEveryoneElse)
	{
		String[] temp =
		{ anExample, aMe, aEveryoneElse };
		if ((temp[0] != null) && (temp[0].trim().equals("")))
		{
			temp[0] = null;
		}
		if ((temp[1] != null) && (temp[1].trim().equals("")))
		{
			temp[1] = null;
		}
		if ((temp[2] != null) && (temp[2].trim().equals("")))
		{
			temp[2] = null;
		}
		theExample1 = temp;
	}

	public String getDescription()
	{
		return theDescription;
	}

	public String getSynopsis()
	{
		return theSynopsis;
	}

	private String getCCommand()
	{
		return theCommand.substring(0, 1).toUpperCase()
				+ theCommand.substring(1);
	}

	public String getCommand()
	{
		return theCommand;
	}

	private String getCDescription()
	{
		return getDescription().replaceAll(getCommand(),
				"<B>" + getCommand() + "</B>").replaceAll(getCCommand(),
				"<B>" + getCCommand() + "</B>");
	}

	private String getCSynopsis()
	{
		if (getSynopsis() == null)
		{
			return "";
		}
		return getSynopsis().replaceAll("<", "&lt;").replaceAll(">", "&gt;")
				.replaceAll("\"", "&quot;").replaceAll(getCommand(),
						"<B>" + getCommand() + "</B>");
	}

	@Override
	public String toString()
	{
		String result = "<H1>" + getCCommand() + "</H1><DL><DT><B>NAME</B>"
				+ "<DD><B>" + getCCommand() + "</B> - formatted output<P>"
				+ "<DT><B>SYNOPSIS</B>" + "<DD>" + getCSynopsis() + "<P>"
				+ "<DT><B>DESCRIPTION</B>" + "<DD>" + getCDescription() + "<P>"
				+ "<DT><B>EXAMPLES</B><DD>";
		result += getExample1Description();
		result += getExample2Description();
		result += "<DT><B>SEE ALSO</B><DD>" + getSeealso() + "<P></DL>";
		return result;
	}

	private String getExample2Description()
	{
		if ((theExample2 == null) || (theExample2.length != 4))
		{
			return "";
		}
		StringBuffer result = new StringBuffer();
		if ((theExample2[0] != null) && !theExample2[0].trim().equals(""))
		{
			result.append("\"" + theExample2[0] + "\"<P>");
		}
		if ((theExample2[1] != null) && !theExample2[1].trim().equals(""))
		{
			result.append("You: <TT>" + theExample2[1] + "</TT><BR>");
		}
		if ((theExample2[2] != null) && !theExample2[2].trim().equals(""))
		{
			result.append("Marvin: <TT>" + theExample2[2] + "</TT><BR>");
		}
		if ((theExample2[3] != null) && !theExample2[3].trim().equals(""))
		{
			result.append("Anybody: <TT>" + theExample2[3] + "</TT><P>");
		}
		return result.toString();
	}

	private String getExample1Description()
	{
		if ((theExample1 == null) || (theExample1.length != 3))
		{
			return "";
		}
		StringBuffer result = new StringBuffer();
		if ((theExample1[0] != null) && !theExample1[0].trim().equals(""))
		{
			result.append("\"" + theExample1[0] + "\"<P>");
		}
		if ((theExample1[1] != null) && !theExample1[1].trim().equals(""))
		{
			result.append("You: <TT>" + theExample1[1] + "</TT><BR>");
		}
		if ((theExample1[2] != null) && !theExample1[2].trim().equals(""))
		{
			result.append("Anybody: <TT>" + theExample1[2] + "</TT><P>");
		}
		return result.toString();
	}

	public String getSeealso()
	{
		return (theSeealso==null ? null : theSeealso);
	}

	public void setSecondExample(String anExample, String aMe, String aTarget,
			String aEveryoneElse)
	{
		String[] temp =
		{ anExample, aMe, aTarget, aEveryoneElse };
		if ((temp[0] != null) && (temp[0].trim().equals("")))
		{
			temp[0] = null;
		}
		if ((temp[1] != null) && (temp[1].trim().equals("")))
		{
			temp[1] = null;
		}
		if ((temp[2] != null) && (temp[2].trim().equals("")))
		{
			temp[2] = null;
		}
		if ((temp[3] != null) && (temp[3].trim().equals("")))
		{
			temp[3] = null;
		}
		theExample2 = temp;
	}
}
